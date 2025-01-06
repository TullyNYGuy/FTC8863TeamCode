package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class ITDLiftBucketArmBucketGateController implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum LiftBucketArmGateControllerState {
        IDLE,

        // setup for init states (run prior to arriving at match)
        // and init states (same thing)

        BUCKET_MOVING_TO_INIT_POSITION,
        INIT_COMPLETE,

        // get ready to run states
        BUCKET_ARM_MOVING_TO_TRANSFER_POSITION_FOR_READY_TO_RUN,
        TRANSFER_COMPLETE,

        // get setup to deliver states
        BUCKET_ARM_MOVING_TO_SAFE_POSITION_BEFORE_DELIVERY,
        LIFT_MOVING_TO_DELIVERY_POSITION,
        BUCKET_ARM_MOVING_TO_DELIVERY_POSITION,
        READY_FOR_DELIVERY,

        // delivery states
        DELIVERING_SAMPLE,
        // to reduce stress on servo, move the bucket arm vertical
        BUCKET_ARM_MOVING_TO_SAFE_POSITION_AFTER_DELIVERY,
        LIFT_MOVING_TO_TRANSFER_POSITION,
        BUCKET_ARM_MOVING_TO_TRANSFER_POSITION,
        READY_FOR_TRANSFER

    }

    private LiftBucketArmGateControllerState state;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private ElapsedTime timer;
    private Telemetry telemetry;

    private DataLogging logFile;
    private boolean loggingOn = false;
    private DataLogOnChange logDataOnchange;

    private boolean initComplete = false;
    private final String CONTROLLER_NAME = ITDRobot.HardwareName.LIFT_BUCKET_ARM_BUCKET_GATE_CONTROLLER.hwName;

    private ITDLift lift;

    public void setLift(ITDLift lift) {
        this.lift = lift;
    }

    private ITDBucketArmServo bucketArmServo;
    private ITDBucketGateServo bucketGateServo;

    private ITDIntakeBucketController controller;

    public void setController(ITDIntakeBucketController controller) {
        this.controller = controller;
    }
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ITDLiftBucketArmBucketGateController(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
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
        logCommand("setup for init");
        lift.reset();
        // protect the gate from hitting on something
        bucketGateServo.closePosition();
        bucketArmServo.initPosition();
        //todo do we need to move the intake out of the way? What is going on with the bucket?
        state = LiftBucketArmGateControllerState.BUCKET_MOVING_TO_INIT_POSITION;
    }

    @Override
    public boolean init(Configuration config) {
        logCommand("Init starting");
        setupForInit();
        return true;
    }

    public void getReadyToRun() {
        bucketGateServo.closePosition();
        bucketArmServo.transferPosition();
        state = LiftBucketArmGateControllerState.BUCKET_ARM_MOVING_TO_TRANSFER_POSITION_FOR_READY_TO_RUN;
    }

    public void setupForDelivery() {
        bucketGateServo.closePosition();
        bucketArmServo.safeForVerticalMovementPosition();
        state = LiftBucketArmGateControllerState.BUCKET_ARM_MOVING_TO_SAFE_POSITION_BEFORE_DELIVERY;
    }

    public void deliverSample() {
        bucketGateServo.openPosition();
        timer.reset();
        state = LiftBucketArmGateControllerState.DELIVERING_SAMPLE;
    }

    //*********************************************************************************************
    //          Communication from Bucket
    //*********************************************************************************************

//    private boolean bucketCompletedDelivery = false;
//
//    public void setBucketCompletedDelivery(boolean bucketCompletedDelivery) {
//        this.bucketCompletedDelivery = bucketCompletedDelivery;
//    }
//
//    private boolean bucketCompletedHang = false;
//
//    public void setBucketCompletedHang(boolean bucketCompletedHang) {
//        this.bucketCompletedHang = bucketCompletedHang;
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
        //telemetry.addData("State = ", armIntakeState.toString());
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    //*********************************************************************************************
    //          State machine
    //*********************************************************************************************

    @Override
    public void update() {
        lift.update();

        switch (state) {

            // setup for init - run before arriving for match
            case BUCKET_MOVING_TO_INIT_POSITION:
                if (liftResetComplete && bucketArmServo.isPositionReached()) {
                    bucketGateServo.openPosition();
                    initComplete = true;
                    state = LiftBucketArmGateControllerState.INIT_COMPLETE;
                }
                break;

            case INIT_COMPLETE:
                // wait for get ready to run
                break;

                // get ready to run states
            case BUCKET_ARM_MOVING_TO_TRANSFER_POSITION_FOR_READY_TO_RUN:
                if (bucketArmServo.isPositionReached() && bucketGateServo.isPositionReached()) {
                    state = LiftBucketArmGateControllerState.TRANSFER_COMPLETE;
                }
                break;
            case TRANSFER_COMPLETE:
                // wait for command to deliver
                break;

                // setup to deliver sample to bin states
            case BUCKET_ARM_MOVING_TO_SAFE_POSITION_BEFORE_DELIVERY:
                if (bucketArmServo.isPositionReached()) {
                    lift.deliveryPosition();
                    state = LiftBucketArmGateControllerState.LIFT_MOVING_TO_DELIVERY_POSITION;
                }
                break;
            case LIFT_MOVING_TO_DELIVERY_POSITION:
                if (liftPositionReached) {
                    bucketArmServo.deliveryPosition();
                    state = LiftBucketArmGateControllerState.BUCKET_ARM_MOVING_TO_DELIVERY_POSITION;
                }
                break;
            case BUCKET_ARM_MOVING_TO_DELIVERY_POSITION:
                if (bucketArmServo.isPositionReached()) {
                    state = LiftBucketArmGateControllerState.READY_FOR_DELIVERY;
                }
                break;
            case READY_FOR_DELIVERY:
                // wait for command to deliver
                break;

                // deliver states
            case DELIVERING_SAMPLE:
                if (timer.milliseconds() > 250) {
                    bucketArmServo.safeForVerticalMovementPosition();
                    state = LiftBucketArmGateControllerState.BUCKET_ARM_MOVING_TO_SAFE_POSITION_AFTER_DELIVERY;
                }
                break;
            case BUCKET_ARM_MOVING_TO_SAFE_POSITION_AFTER_DELIVERY:
                if (bucketArmServo.isPositionReached()) {
                    lift.transferPosition();
                    state = LiftBucketArmGateControllerState.LIFT_MOVING_TO_TRANSFER_POSITION;
                }
                break;

            case LIFT_MOVING_TO_TRANSFER_POSITION:
                if (liftPositionReached) {
                    bucketArmServo.transferPosition();
                    state = LiftBucketArmGateControllerState.BUCKET_ARM_MOVING_TO_TRANSFER_POSITION;
                }
                break;
            case BUCKET_ARM_MOVING_TO_TRANSFER_POSITION:
                if (bucketArmServo.isPositionReached()) {
                    bucketGateServo.openPosition();
                    state = LiftBucketArmGateControllerState.READY_FOR_TRANSFER;
                }
                break;
            case READY_FOR_TRANSFER:
                // wait for a command
                break;


        }

    }
}

// intake / intake arm

// intake -> intake arm to intake, intake
// outake -> intake arm to intake, outtake

// transferPosition -> intake arm to transfer
// bucket clearance -> intake arm to bucket clearance
// init -> intake arm to init

// transfer -> transfer
// stop -> stop intake


