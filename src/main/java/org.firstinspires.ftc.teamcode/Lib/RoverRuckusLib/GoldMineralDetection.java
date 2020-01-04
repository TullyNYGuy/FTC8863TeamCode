package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;

import java.util.List;

// IMPORTANT NOTE:
// There is a bug in the FTC supplied code. The object detection does not work well when the phone is
// in landscape mode and the camera is to the right (when looking at the screen). So put the camera
// to the left (when looking at the screen).

public class GoldMineralDetection {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum TFODState {
        IDLE,
        RUNNING,
        COMPLETE
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private TFODState tfodState;

    MineralVoting mineralVoting;

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

    //private List<Recognition> updatedRecognitionsFiltered;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private List<Recognition> updatedRecognitions = null;
    private double mineralAngle;
    private MineralVoting.MineralPosition mineralPosition;

    private ElapsedTime timer;
    private double timeToLookAtMineralsInMSec = 1000;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public GoldMineralDetection(HardwareMap hardwareMap, Telemetry telemetry, DataLogging logFile) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.logFile = logFile;

        mineralVoting = new MineralVoting(logFile);
        timer = new ElapsedTime();

        initVuforia();
        initTfod();

        tfodState = TFODState.IDLE;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Initialize Vuforia so it can be used in tensor flow.
     */
    private void initVuforia() {
        // Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
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
        // Check if tensor flow object detection (Tfod) can be run on the phone
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            // yes it can. Initialize tensor flow object detection
            int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
            TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
            tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
            tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
        } else {
            // no it can't
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }
    }

    /**
     * Based on the angle to the object that was recognized, determine if the object is in the left,
     * center or right position.
     *
     * @param recognition
     * @return position
     */
    private MineralVoting.MineralPosition getMineralPositionThreeInView(Recognition recognition) {
        mineralAngle = recognition.estimateAngleToObject(AngleUnit.DEGREES);
        if (mineralAngle <= -10) {
            mineralPosition = MineralVoting.MineralPosition.LEFT;
        }
        if (mineralAngle >= 10) {
            mineralPosition = MineralVoting.MineralPosition.RIGHT;
        }
        if (mineralAngle < 10 && mineralAngle > -10) {
            mineralPosition = MineralVoting.MineralPosition.CENTER;
        }
        logFile.logData("mineral located at " + Double.toString(mineralAngle) + " degrees so position = " + mineralPosition.toString());
        return mineralPosition;
    }

    private MineralVoting.MineralPosition getMineralPositionTwoInView(Recognition recognition) {
        mineralAngle = recognition.estimateAngleToObject(AngleUnit.DEGREES);
        if (mineralAngle <= -1) {
            mineralPosition = MineralVoting.MineralPosition.LEFT;
        }
        if (mineralAngle >= -1) {
            mineralPosition = MineralVoting.MineralPosition.CENTER;
        }
        logFile.logData("mineral located at " + Double.toString(mineralAngle) + " degrees so position = " + mineralPosition.toString());
        return mineralPosition;
    }

//    private List<Recognition> filterMasterList(List<Recognition> updatedRecognitionsMaster) {
//        int cutoffPointBottom = 250;
//        for (Recognition recognition : updatedRecognitionsMaster) {
//            if (recognition.getBottom() > cutoffPointBottom) {
//                updatedRecognitionsFiltered.add(recognition);
//            }
//        }
//        return updatedRecognitionsFiltered;
//    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * If activate() has been run, and the time to run has not expired, see if there are
     * recognitions of objects from tensor flow object detection (tfod). If there are recognitions
     * determine where each object is located and then add a vote for each mineral to the mineral
     * voting routine. Note that activate() has to be run in order to call this method. This method
     * needs to be called over and over again in a loop by the calling code. Once the time to run
     * has expired the method will deactivate tfod automatically.
     * Use isRecognitionComplete() to determine if the recognition time has expired.
     */
    public void getRecognition(int mineralsInViewAmount) {
        switch (tfodState) {
            case IDLE:
                break;
            case RUNNING:
                if (tfod != null) {
                    // getUpdatedRecognitions() get the latest set of objects detected
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    updatedRecognitions = tfod.getUpdatedRecognitions();
                    // make sure there are some objects recognized and ready to process
                    if (updatedRecognitions != null) {
                        logFile.logData("  ");
                        logFile.logData("# of Objects Detected: " + updatedRecognitions.size());
                        // update the count of recognitions
                        mineralVoting.updateRecognitionCount();
                        // for each recognition, determine if it is left, center or right position and
                        // then add to the mineral vote counting
                        for (Recognition recognition : updatedRecognitions) {
                            //determine if mineral is in left right or center area
                            if (mineralsInViewAmount == 3) {
                                mineralPosition = getMineralPositionThreeInView(recognition);
                            }
                            if (mineralsInViewAmount == 2) {
                                mineralPosition = getMineralPositionTwoInView(recognition);
                            }
                            if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                mineralVoting.addMineralVote(MineralVoting.MineralType.GOLD, mineralPosition);
                                logFile.logData("mineral is Gold");
                            } else {
                                mineralVoting.addMineralVote(MineralVoting.MineralType.SILVER, mineralPosition);
                                logFile.logData("mineral is silver");
                            }
                        }
                    }
                }
                // has the time allotted to look at the minerals expired?
                if (timer.milliseconds() > timeToLookAtMineralsInMSec) {
                    // yes it has, recognitions are complete, deactivate tfod
                    tfodState = TFODState.COMPLETE;
                    deactivate();
                }
                break;
            case COMPLETE:
                break;
        }
    }

    /**
     * Is the time for recognitions expired and recognition complete?
     *
     * @return true if yes, false if no
     */
    public boolean isRecognitionComplete() {
        if (tfodState == TFODState.COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the most likely gold mineral position.
     *
     * @return
     */
    public MineralVoting.LikelyPosition getMostLikelyGoldPosition() {
        return mineralVoting.getMostLikelyGoldPosition();
    }

    /**
     * Start obtaining mineral recognitions. Do this for the time input (in milliseconds).
     *
     * @param timeToLookAtMineralsInMSec
     */
    public void activate(double timeToLookAtMineralsInMSec) {
        if (tfod != null) {
            tfod.activate();
            this.timeToLookAtMineralsInMSec = timeToLookAtMineralsInMSec;
            timer.reset();
            tfodState = TFODState.RUNNING;
        }
    }

    /**
     * Deactivate tfod
     */
    public void deactivate() {
        if (tfod != null) {
            tfod.deactivate();
        }
    }

    /**
     * Shutdown tfod
     */
    public void shutdown() {
        if (tfod != null) {
            tfod.shutdown();
        }
    }
}
