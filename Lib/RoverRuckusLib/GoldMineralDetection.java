package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest.vision.MineralVoting;

import java.util.List;

public class GoldMineralDetection {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

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
        initVuforia();
        mineralVoting = new MineralVoting(logFile);
        // Check if tensor flow object detection (Tfod) can be run on the phone
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            // yes it can. Initialize tensor flow object detection
            initTfod();
        } else {
            // no it can't
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

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

    private MineralVoting.MineralPosition getMineralPosition(Recognition recognition){
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
        return mineralPosition;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void getRecognition(){
        if (tfod != null) {

            // getUpdatedRecognitions() get the latest set of objects detected
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            updatedRecognitions = tfod.getUpdatedRecognitions();
            // make sure there are some objects recognized and ready to process
            if (updatedRecognitions != null) {
                logFile.logData("  ");
                logFile.logData("# of Objects Detected: " + updatedRecognitions.size());
                mineralVoting.updateRecognitionCount();
                for (Recognition recognition : updatedRecognitions) {
                    //determine if mineral is in left right or center area
                    mineralPosition = getMineralPosition(recognition);
                    if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                        mineralVoting.addMineralVote(MineralVoting.MineralType.GOLD, mineralPosition);
                        telemetry.addData("Gold Mineral Position", mineralPosition.toString());
                    } else {
                        mineralVoting.addMineralVote(MineralVoting.MineralType.SILVER, mineralPosition);
                        telemetry.addData("Silver Mineral Position", mineralPosition.toString());
                    }
                }
            }
        }
    }

    public void activate() {
        if (tfod != null) {
            tfod.activate();
        }
    }
}
