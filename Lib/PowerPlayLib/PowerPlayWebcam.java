package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

public class PowerPlayWebcam implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum Phase {
        TELEOP,
        AUTONOMOUS
    }

    private Phase phase = Phase.TELEOP;

    private boolean commandComplete = true;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private ElapsedTime timer;
    private PowerPlayAllianceColor allianceColor;

    // initialization is complete
    private boolean initComplete = false;

    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    // public so that I don't have to wrap the camera methods.
    public OpenCvWebcam webcam;
    private int cameraMonitorViewId;
    // public so that I don't have to wrap the pipeline methods
    public OpenCvPipeline pipeline;
    private String cameraName;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public PowerPlayWebcam(HardwareMap hardwareMap, Telemetry telemetry, String cameraName, OpenCvPipeline pipeline) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.cameraName = cameraName;
        this.pipeline = pipeline;
        // init has not been started yet
        initComplete = false;
        // the lift can be commanded to do something, like the init
        commandComplete = true;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    @Override
    public String getName() {
        return cameraName;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        enableLogging = true;
    }

    @Override
    public void disableDataLogging() {
        enableLogging = false;
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

//    private void logState() {
//        if (enableLogging && logFile != null) {
//            logStateOnChange.log(getName() + " state = " + coneGrabberState.toString());
//        }
//    }

    private void logCommand(String command) {
        if (enableLogging && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    @Override
    /**
     * Move the arm to the init position, move the cone grabber to the init position.
     */
    public boolean init(Configuration config) {
        // start the init
        logCommand("Init starting");
        logCommand("Init");
        /*
         * Instantiate an OpenCvCamera object for the camera we'll be using.
         * Note that you will need to
         * make sure you have added the webcam to your configuration file and
         * adjusted the name here to match what you named it in said config file.
         *
         * We pass it the view that we wish to use for camera monitor (on
         * the RC phone). If no camera monitor is desired, use the alternate
         * single-parameter constructor instead (commented out below)
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, cameraName), cameraMonitorViewId);
        /*
         * Specify the image processing pipeline we wish to invoke upon receipt
         * of a frame from the camera. Note that switching pipelines on-the-fly
         * (while a streaming session is in flight) *IS* supported.
         */
        webcam.setPipeline(pipeline);
        webcam.setMillisecondsPermissionTimeout(5000); // Timeout for obtaining permission is configurable. Set before opening.
        return false;
    }

    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
    }

    public boolean isCommandComplete() {
        return commandComplete;
    }

    public void setPhaseAutonomous() {
        phase = Phase.AUTONOMOUS;
    }

    public void setPhaseTeleop() {
        phase = Phase.TELEOP;
    }

    //********************************************************************************
    // Public commands for controlling the Webcam
    //********************************************************************************

    public void openCamera(OpenCvCameraRotation cameraRotation) {
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                /*
                 * Tell the webcam to start streaming images to us! Note that you must make sure
                 * the resolution you specify is supported by the camera. If it is not, an exception
                 * will be thrown.
                 *
                 * Keep in mind that the SDK's UVC driver (what OpenCvWebcam uses under the hood) only
                 * supports streaming from the webcam in the uncompressed YUV image format. This means
                 * that the maximum resolution you can stream at and still get up to 30FPS is 480p (640x480).
                 * Streaming at e.g. 720p will limit you to up to 10FPS and so on and so forth.
                 *
                 * Also, we specify the rotation that the webcam is used in. This is so that the image
                 * from the camera sensor can be rotated such that it is always displayed with the image upright.
                 * For a front facing camera, rotation is defined assuming the user is looking at the screen.
                 * For a rear facing camera or a webcam, rotation is defined assuming the camera is facing
                 * away from the user.
                 */
                // using 720p resolution. This may have an impact on pipeline performance. Not sure.
                webcam.startStreaming(1280, 720, cameraRotation);

                // I don't see any way to determine if the camera opening was successful. So assume it was.
                initComplete = true;
            }

            @Override
            public void onError(int errorCode)
            {
                logCommand("camera init failed");
                initComplete = true;
            }
        });
    }

    public void closeCamera() {
        /*
         * IMPORTANT NOTE: calling stopStreaming() will indeed stop the stream of images
         * from the camera (and, by extension, stop calling your vision pipeline). HOWEVER,
         * if the reason you wish to stop the stream early is to switch use of the camera
         * over to, say, Vuforia or TFOD, you will also need to call closeCameraDevice()
         * (commented out below), because according to the Android Camera API documentation:
         *         "Your application should only have one Camera object active at a time for
         *          a particular hardware camera."
         *
         * NB: calling closeCameraDevice() will internally call stopStreaming() if applicable,
         * but it doesn't hurt to call it anyway, if for no other reason than clarity.
         *
         * NB2: if you are stopping the camera stream to simply save some processing power
         * (or battery power) for a short while when you do not need your vision pipeline,
         * it is recommended to NOT call closeCameraDevice() as you will then need to re-open
         * it the next time you wish to activate your vision pipeline, which can take a bit of
         * time. Of course, this comment is irrelevant in light of the use case described in
         * the above "important note".
         */
        webcam.stopStreaming();
        webcam.closeCameraDevice();
    }

    @Override
    public void update() {
        // there is no state machine for the camera
    }
}
