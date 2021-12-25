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

package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

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
@TeleOp(name = "Concept: TensorFlow Object Detection Dub", group = "Concept")
@Disabled
public class TestTensorFlowObjectDetection extends LinearOpMode {
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

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection. */
            if (tfod != null) {
                tfod.activate();
            }

            while (opModeIsActive()) {
                if (tfod != null) {
                    // Give the left edge location of each object some bogus intial value, like -1.
                    int goldMineralLeftEdgeLocation = -1;
                    int silverMineral1LeftEdgeLocation = -1;
                    int silverMineral2LeftEdgeLocation = -1;

                    // getUpdatedRecognitions() get the latest set of objects detected
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    // make sure there are some objects recognized and ready to process
                    if (updatedRecognitions != null && updatedRecognitions.size() < 4) {
                        // there are between 1 and 3 objects. Let's get to work!

                        // We are going to get the location of the left edge
                        // of each object in units of camera pixels. After we get the left edge 
                        // values for all of the minerals, we
                        // can check to see which one has the edge that is farthest to the left and
                        // which one is farthest to the right. That will tell us where the objects
                        // are in relationship to each other. We will also figure out which of the
                        // objects is the gold mineral. With the locations, and which one is gold,
                        // we can figure out where the gold is located.

                        // for each object in the recognitions, see if it is gold or silver, and get
                        // its left edge value
                        for (Recognition recognition : updatedRecognitions) {
                            // is it the gold mineral?
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                // get its left edge location
                                goldMineralLeftEdgeLocation = (int) recognition.getLeft();
                                // if not then it must be one of the silvers
                            }
                            // is it a silver mineral?
                            if (recognition.getLabel().equals(LABEL_SILVER_MINERAL)) {
                                if (silverMineral1LeftEdgeLocation == -1) {
                                    // get its left edge location
                                    silverMineral1LeftEdgeLocation = (int) recognition.getLeft();
                                } else {
                                    // one silver mineral has already been seen. This is the second
                                    // silver mineral
                                    silverMineral2LeftEdgeLocation = (int) recognition.getLeft();
                                }
                            }
                        }
                        telemetry.addData("# Object Detected", updatedRecognitions.size());

                        // how many objects are there?
                        switch (updatedRecognitions.size()) {
                            case 1:
                                // not sure what to do here
                                break;
                            case 2:
                                // there are 2 objects. Assume the camera is looking at the left 2 mineral positions.
                                // for each object, find out if it is the gold and gets its left edge location
                                // psuedo code:
                                // if neither of the minerals is gold, then the gold is the right mineral
                                // if one of the minerals is gold, then check if the left edge of the gold 
                                // one is less than the left edge of the silver one. If it is then the left mineral 
                                // is gold. 
                                // if not, then the gold is the center mineral
                                if (goldMineralLeftEdgeLocation == -1 && silverMineral2LeftEdgeLocation >= 0) {
                                    telemetry.addData("Gold Mineral Position", "Right");
                                }
                                if (silverMineral2LeftEdgeLocation == -1) {
                                    if (goldMineralLeftEdgeLocation < silverMineral1LeftEdgeLocation) {
                                        telemetry.addData("Gold Mineral Position", "Left");
                                    } else {
                                        telemetry.addData("Gold Mineral Position", "Center");
                                    }
                                }

                                break;
                            case 3:
                                // Now that we know which one is gold, and the left edge location of each
                                // object, compare the locations to figure out where the gold is located
                                // relative to the other objects: left, center, or right.
                                // First make sure that we have a valid edge location for each mineral
                                if (goldMineralLeftEdgeLocation != -1 && silverMineral1LeftEdgeLocation != -1 && silverMineral2LeftEdgeLocation != -1) {
                                    // we do have all the left edges so now is the gold left edge location furthest to the left?
                                    // If so then the gold is in the left position.
                                    if (goldMineralLeftEdgeLocation < silverMineral1LeftEdgeLocation && goldMineralLeftEdgeLocation < silverMineral2LeftEdgeLocation) {
                                        telemetry.addData("Gold Mineral Position", "Left");
                                    } else {
                                        // if not then is the left edge of the gold farthest to the right?
                                        if (goldMineralLeftEdgeLocation > silverMineral1LeftEdgeLocation && goldMineralLeftEdgeLocation > silverMineral2LeftEdgeLocation) {
                                            telemetry.addData("Gold Mineral Position", "Right");
                                        } else {
                                            // since it was not the left or the right, then the gold has to be
                                            // in the center
                                            telemetry.addData("Gold Mineral Position", "Center");
                                        }
                                    }
                                }
                                break;
                            default:
                                // what to do if there are more than 3 objects detected?
                                // Maybe find the area of each object and pick the 3 largest
                                // objects. They are probably the ones we are trying to hit.
                                break;
                        }
                        telemetry.update();
                    }
                }
            }
        }

        if (tfod != null) {
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
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }
}
