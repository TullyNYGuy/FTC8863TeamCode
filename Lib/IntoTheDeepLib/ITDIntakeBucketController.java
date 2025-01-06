package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class ITDIntakeBucketController implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum IntakeBucketControllerState {
        IDLE,

        // setup for init states (run prior to arriving at match),

        INTAKE_ARM_MOVING_TO_TRANSFER_POSITION,
        EXTENSION_ARM_RESETTING_FOR_INIT_SETUP,
        EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_INIT_SETUP,
        BUCKET_MOVING_TO_INIT_POSITION,
        INTAKE_ARM_MOVING_TO_INIT_POSITION,
        EXTENSION_ARM_MOVING_TO_INIT_POSITION,
        READY_FOR_INIT,

        // init states
        EXTENSION_ARM_RESETTING_FOR_INIT,
        //BUCKET_MOVING_TO_INIT_POSITION, REUSED
        //INTAKE_ARM_MOVING_TO_INIT_POSITION, REUSED
        //EXTENSION_ARM_MOVING_TO_INIT_POSITION, REUSED
        INIT_COMPLETE,

        // get ready to run states
        EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_FOR_GET_READY_TO_RUN,
        BUCKET_MOVING_TO_TRANSFER_POSITION,
        EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION,
        TRANSFER_COMPLETE,

        // deliver sample to bin states
        EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_FOR_DELIVERY,
        LIFT_MOVING_TO_DELIVERY_POSITION,
        BUCKET_ARM_MOVING_TO_DELIVERY_POSITION,
        READY_FOR_DELIVERY,

    }

    private IntakeBucketControllerState state;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private ElapsedTime timer;

    private DataLogging logFile;
    private boolean loggingOn = false;
    private DataLogOnChange logDataOnchange;

    private boolean initComplete = false;
    private final String CONTROLLER_NAME = ITDRobot.HardwareName.INTAKE_BUCKET_CONTROLLER.hwName;

    private ITDLiftBucketArmBucketGateController liftBucketArmBucketGateController;

    public void setLiftBucketArmBucketGateController(ITDLiftBucketArmBucketGateController liftBucketArmBucketGateController) {
        this.liftBucketArmBucketGateController = liftBucketArmBucketGateController;
    }

    private ITDExtensionArmIntakeController extensionArmIntakeController;

    public void setExtensionArmIntakeController(ITDExtensionArmIntakeController extensionArmIntakeController) {
        this.extensionArmIntakeController = extensionArmIntakeController;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ITDIntakeBucketController(HardwareMap hardwareMap, Telemetry telemetry) {

        timer = new ElapsedTime();
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    //*********************************************************************************************
    //          Commands
    //*********************************************************************************************

    public void setupForInit() {
        //todo do we need to move the intake out of the way? What is going on with the bucket?
        state = IntakeBucketControllerState.EXTENSION_ARM_RESETTING_FOR_INIT_SETUP;
    }

    @Override
    public boolean init(Configuration config) {
        logCommand("Init starting");
        //todo remove this when this class is finished and let the real init set it
        initComplete = true;
        state = IntakeBucketControllerState.EXTENSION_ARM_RESETTING_FOR_INIT;
        return true;
    }

    public void getReadyToRun() {
        state = IntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_FOR_GET_READY_TO_RUN;
    }

    //*********************************************************************************************
    //          Communication from Intake
    //*********************************************************************************************


    //*********************************************************************************************
    //          Communication from Bucket
    //*********************************************************************************************

    private boolean bucketCompletedDelivery = false;

    public void setBucketCompletedDelivery(boolean bucketCompletedDelivery) {
        this.bucketCompletedDelivery = bucketCompletedDelivery;
    }

    private boolean bucketCompletedHang = false;

    public void setBucketCompletedHang(boolean bucketCompletedHang) {
        this.bucketCompletedHang = bucketCompletedHang;
    }

    //*********************************************************************************************
    //          Communication from Intake
    //*********************************************************************************************
    /**
     * Allow the intake to request the controller to move the extension arm out so an outtake can
     * take place while the intake is not over the robot body. This occurs when we have a bad jam
     * that cannot be cleared any other way.
     */
    private boolean intakesRequestsAnOuttake = false;

    public void setIntakesRequestsAnOuttake(boolean needOuttake) {
        this.intakesRequestsAnOuttake = needOuttake;
        logCommand("Intake requests outtake");
    }

    /**
     * The intake says that it has transferred the sample to the bucket
     */
    private boolean intakeTransferComplete = false;

    public void setIntakeTransferComplete(boolean transferComplete) {
        this.intakeTransferComplete = transferComplete;
    }

    private boolean intakePositionedForBucketClearance = false;

    public void setIntakePositionedForBucketClearance(boolean OKForBucketClearance) {
        this.intakePositionedForBucketClearance = OKForBucketClearance;
    }

    private boolean extensionArmResetComplete = false;

    public void setExtensionArmResetComplete(boolean resetComplete) {
        this.extensionArmResetComplete = resetComplete;
    }

    private boolean intakePositionReached = false;

    public void setIntakePositionReached(boolean intakePositionReached) {
        this.intakePositionReached = intakePositionReached;
    }

    private boolean intakePositionedForTransfer = false;

    public void setIntakePositionedForTransfer(boolean intakePositionedForTransfer) {
        this.intakePositionedForTransfer = intakePositionedForTransfer;
    }

    /**
     * The intake says it has a good sample.
     */
    private boolean intakeHasValidSample = false;

    public void setIntakeHasValidSample(boolean intakeHasValidSample) {
        this.intakeHasValidSample = intakeHasValidSample;
    }

    public boolean intakeReadyForTransfer = false;

//    public void setIntakeReadyForTransfer(boolean intakeReadyForTransfer) {
//        this.intakeReadyForTransfer = intakeReadyForTransfer;
//    }

    //*********************************************************************************************
    //          Communication from Lift
    //*********************************************************************************************
    private boolean liftPositionReached = false;

    public void setLiftPositionReached(boolean positionReached) {
        this.liftPositionReached = positionReached;
    }

    private boolean liftResetComplete = false;

    public void setLiftResetComplete(boolean resetComplete) {
        this.liftResetComplete = resetComplete;
    }

    //*********************************************************************************************
    //          Housekeeping stuff
    //*********************************************************************************************
    @Override
    public String getName() {
        return CONTROLLER_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logDataOnchange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    private void logState() {
        if (loggingOn && logFile != null) {
            logDataOnchange.log(getName() + " state = " + state.toString());
        }
    }

    private void logCommand(String command) {
        if (loggingOn && logFile != null) {
            logDataOnchange.log(getName() + " command = " + command);
        }
    }

    public void displayState(Telemetry telemetry) {
        telemetry.addData("State = ", state.toString());
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    //*********************************************************************************************
    //          State machine
    //*********************************************************************************************

    @Override
    public void update() {


        switch (state) {

            // setup for init - run before arriving for match
            case EXTENSION_ARM_RESETTING_FOR_INIT_SETUP:
                if (extensionArmResetComplete) {
                    state = IntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_INIT_SETUP;
                }
                break;
            case EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_INIT_SETUP:
                // have the extension arm and intake arm reached the bucket clearance positions?

                break;
            case BUCKET_MOVING_TO_INIT_POSITION:

                break;
            case EXTENSION_ARM_MOVING_TO_INIT_POSITION:

                break;
            case READY_FOR_INIT:
                // driver may leave robot turned on or may turn it off
                break;

                // init states
            case EXTENSION_ARM_RESETTING_FOR_INIT:
                if (extensionArmResetComplete) {

                    state = IntakeBucketControllerState.INIT_COMPLETE;
                }
                break;
            case INIT_COMPLETE:
                // wait for get ready to run
                break;

                // get ready to run states
            case EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_FOR_GET_READY_TO_RUN:

                break;
            case BUCKET_MOVING_TO_TRANSFER_POSITION:

                break;
            case EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION:

                break;

        }

    }
}



