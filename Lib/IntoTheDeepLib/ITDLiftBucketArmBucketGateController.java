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

        // setup for driving before delivery states
        BUCKET_ARM_MOVING_TO_SAFE_POSITION_BEFORE_DELIVERY,
        LIFT_MOVING_TO_READY_TO_DELIVER_POSITION,
        WAITING_FOR_SETUP_FOR_DELIVERY_COMMAND,

        // setup for delivery states
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
    private DataLogOnChange logStateOnChange;
    private boolean initComplete = false;
    private final String CONTROLLER_NAME = ITDRobot.HardwareName.LIFT_BUCKET_ARM_BUCKET_GATE_CONTROLLER.hwName;

    private ITDLift lift;

    public void setLift(ITDLift lift) {
        this.lift = lift;
    }

    private ITDBucketArmServo bucketArmServo;
    private ITDBucketGateServo bucketGateServo;

    private ITDIntakeBucketController controller;

    public void setIntakeBucketController(ITDIntakeBucketController controller) {
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
        lift = new ITDLift(hardwareMap, telemetry);
        // give the lift a reference to this controller since it needs to communicate with us
        lift.setController(this);
        bucketArmServo = new ITDBucketArmServo(hardwareMap,telemetry);
        bucketGateServo = new ITDBucketGateServo(hardwareMap, telemetry);
        timer = new ElapsedTime();
        state = LiftBucketArmGateControllerState.IDLE;
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

    /**
     * This should only be called after the intake has been moved out of the way. IT
     */
    public void setupForInit() {
        logCommand("setup for init");
        // tell the lift bucket controller that the lift / bucket init is not complete
        controller.setLiftBucketAtInitPosition(false);
        lift.reset();
        // protect the gate from hitting on something
        bucketGateServo.closePosition();
        bucketArmServo.initPosition();
        state = LiftBucketArmGateControllerState.BUCKET_MOVING_TO_INIT_POSITION;
    }

    public boolean initFromIntakeBucketController(Configuration config) {
        logCommand("Init");
        setupForInit();
        return true;
    }

    @Override
    public boolean init(Configuration config) {
        return true;
    }

    /**
     * This closes the bucket gate, locking the preloaded sample into the bucket. It moves the
     * bucket arm to the transfer position. Note that it only gets called by the intake / bucket
     * contoller once the intake has been moved out of the way. When done, it tells the intake /
     * bucket controller that a transfer is complete since we have a sample in the bucket already.
     */
    public void getReadyToRun() {
        logCommand("Get ready to run");
        controller.setLiftBucketAtTransferPosition(false);
        // normally the intake controller does this, but this is startup and there is a sample in
        // the bucket already
        bucketGateServo.closePosition();
        bucketArmServo.transferPosition();
        state = LiftBucketArmGateControllerState.BUCKET_ARM_MOVING_TO_TRANSFER_POSITION_FOR_READY_TO_RUN;
    }

    /**
     * This moves the bucket arm straight up into the air and the moves the lift up to the height
     * needed to deliver a sample. It does not rotate the bucket over to the dump/delivery position.
     * You just drive around like that.This should minimize the chances of hitting another robot with
     * the bucket arm.
     */
    public void setupForDrivingBeforeDelivery() {
        logCommand("Setup for drive before or after delivery");
        controller.setLiftBucketAtSafeToDrivePosition(false);
        bucketGateServo.closePosition();
        bucketArmServo.safeForVerticalMovementPosition();
        state = LiftBucketArmGateControllerState.BUCKET_ARM_MOVING_TO_SAFE_POSITION_BEFORE_DELIVERY;

    }
    public void setupForDelivery() {
        logCommand("Setup for delivery");
        controller.setLiftBucketAtReadyToDeliverPosition(false);
        bucketArmServo.deliveryPosition();
        state = LiftBucketArmGateControllerState.BUCKET_ARM_MOVING_TO_DELIVERY_POSITION;
    }

    public void deliverSample() {
        logCommand("Deliver sample");
        controller.setLiftBucketSampleIsDelivered(false);
        controller.setLiftBucketAtTransferPosition(false);
        bucketGateServo.openPosition();
        // start a timer to make sure the sample has dropped out of the bucket
        timer.reset();
        state = LiftBucketArmGateControllerState.DELIVERING_SAMPLE;
    }

    public void openGate() {
        logCommand("Open gate");
        bucketGateServo.openPosition();
    }
    public void closeGate() {
        logCommand("Close gate");
        bucketGateServo.closePosition();
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
            logComment("Init complete");
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
        logStateOnChange = new DataLogOnChange(logFile);
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
            logStateOnChange.log(getName() + " state = " + state.toString());
        }
    }

    private void logCommand(String command) {
        if (loggingOn && logFile != null) {
            logDataOnchange.log(getName() + " command = " + command);
        }
    }

    private void logComment(String comment) {
        if (loggingOn && logFile != null) {
            logFile.logData(comment);
        }
    }

    public void displayState(Telemetry telemetry) {
        telemetry.addData("LBABGC State = ", state.toString());
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    //*********************************************************************************************
    //          State machine
    //*********************************************************************************************

    @Override
    public void update() {
        logState();
        lift.update();

        switch (state) {

            // setup for init - run before arriving for match
            case BUCKET_MOVING_TO_INIT_POSITION:
                if (liftResetComplete && bucketArmServo.isPositionReached()) {
                    initComplete = true;
                    controller.setLiftBucketAtInitPosition(true);
                    state = LiftBucketArmGateControllerState.INIT_COMPLETE;
                }
                break;

            case INIT_COMPLETE:
                // wait for get ready to run
                break;

                // get ready to run states
            case BUCKET_ARM_MOVING_TO_TRANSFER_POSITION_FOR_READY_TO_RUN:
                if (bucketArmServo.isPositionReached() && bucketGateServo.isPositionReached()) {
                    controller.setLiftBucketAtTransferPosition(true);
                    state = LiftBucketArmGateControllerState.TRANSFER_COMPLETE;
                }
                break;
            case TRANSFER_COMPLETE:
                // wait for command to setup for delivery
                break;

                // setup to deliver sample to bin states
            case BUCKET_ARM_MOVING_TO_SAFE_POSITION_BEFORE_DELIVERY:
                if (bucketArmServo.isPositionReached()) {
                    lift.readyToDeliverPosition();
                    state = LiftBucketArmGateControllerState.LIFT_MOVING_TO_READY_TO_DELIVER_POSITION;
                }
                break;
            case LIFT_MOVING_TO_READY_TO_DELIVER_POSITION:
                if (liftPositionReached) {
                    controller.setLiftBucketAtSafeToDrivePosition(true);
                    state = LiftBucketArmGateControllerState.WAITING_FOR_SETUP_FOR_DELIVERY_COMMAND;
                }
                break;
            case WAITING_FOR_SETUP_FOR_DELIVERY_COMMAND:
                // jump right to the setup for delivery so the bucket arm is over the basket
                setupForDelivery();
                break;

                // setup for delivery states
            case BUCKET_ARM_MOVING_TO_DELIVERY_POSITION:
                if (bucketArmServo.isPositionReached()) {
                    controller.setLiftBucketAtReadyToDeliverPosition(true);
                    state = LiftBucketArmGateControllerState.READY_FOR_DELIVERY;
                }
                break;
            case READY_FOR_DELIVERY:
                // wait for command to deliver
                break;

                // deliver states
            case DELIVERING_SAMPLE:
                if (timer.milliseconds() > 250) {
                    controller.setLiftBucketSampleIsDelivered(true);
                    bucketArmServo.safeForVerticalMovementPositionAfterDelivery();
                    bucketGateServo.closePosition();
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
                    controller.setLiftBucketAtTransferPosition(true);
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


