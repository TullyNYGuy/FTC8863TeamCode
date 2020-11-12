/* Copyright (c) 2018 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest.vision;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;

import java.util.List;

/**
 * This 2018-2019 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the gold and silver minerals.
 * <p>
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 * <p>
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@TeleOp(name = "Concept: TensorFlow Gold Detection", group = "Concept")
@Disabled
public class TestTensorFlowGoldDetection extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY = "AdFJpV3/////AAAAGfTNpwzHLEWLhTazSUptJDdvoaO1q58UH3Ix0gMjIizeGqeRTy/mHyHpZI3hX3VrQk0S4VJKsiwBIUrTZy57oWoQQGsD/6JXnrC/R2zQ2ruhxmV9JYc6zr5Lhu+aUFdce/WJezBkcUv7fD2y6kmNHAWlYyMx3ZP8YX2bSfTWu4PjiO3N/CFelgIJSz5BCRtYeFb1gKkCYhsqKUNfkWXznEFvX8ppW72yjbfq62QwqGFeuql/3cPce8asiOVo9NLiG9mIuADM+FWLairEHQ4h2euGHa+JNrk36EO0zVAFk9G2RBQJRkwgA7jUOGpCEW0Qqt0XJoM+0T0sOVORrn3Lqp9M4ecaQYsQsR8RPoZRL0Ox";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;
    private DataLogging logFile;
    private float goldMineralConfidenceLevel;
    private float silverMineral1ConfidenceLevel;
    private float silverMineral2ConfidenceLevel;
    private int loopCount = 0;


    /**
     * {@link #tfod} is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();

        // Initialize tensor flow object detection
         initTfod();


        logFile = new DataLogging("VisionGold", telemetry);

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();
        logFile.startTimer();

        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection. */
            if (tfod != null) {
                tfod.activate();
            }

            while (opModeIsActive()) {
                loopCount++;
                if (tfod != null) {
                    // Give the left edge location of each object some bogus intial value, like -1.
                    int goldMineralLeftEdgeLocation = -1;
                    int silverMineral1LeftEdgeLocation = -1;
                    int silverMineral2LeftEdgeLocation = -1;

                    // getUpdatedRecognitions() get the latest set of objects detected
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    List<Recognition> goldRecognitions = null;
                    // make sure there are some objects recognized and ready to process
                    if (updatedRecognitions != null) {
                        logFile.logData("Found objects on pass = " + loopCount + ". Object count = " + updatedRecognitions.size());

                        // create a list of only the gold minerals - remove all recognitions that are not
                        // gold minerals
                        for (Recognition recognition : updatedRecognitions) {
                            // is it a gold mineral?
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                // yes then add it to the list of gold minerals
                                goldRecognitions.add((recognition));
                            }
                        }

                        if (goldRecognitions != null) {
                            logFile.logData("Number of gold minerals in those objects = " + goldRecognitions.size());
                            // how many gold minerals are there?
                            switch (goldRecognitions.size()) {
                                case 1:
                                    // There is only 1 gold mineral recognized. Treat it as our target.
                                    // Find the angle to the gold mineral. See if the angle falls into
                                    // the left, center or right positions.
                                    break;
                                default:
                                    // come up with a method of determining which of the gold objects is the target
                                    break;
                            }
                        } else {
                            logFile.logData("None of the objects were gold minerals.");
                        }
                        telemetry.update();
                    }
                }
            }
        }

        if (tfod != null) {
            sleep(10000);
            tfod.shutdown();
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        // only load the gold mineral model so only look for gold minerals
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL);
    }
}
