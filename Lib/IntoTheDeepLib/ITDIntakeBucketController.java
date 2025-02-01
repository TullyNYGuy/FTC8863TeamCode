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
        BUCKET_MOVING_TO_INIT_POSITION_FOR_INIT_SETUP,
        INTAKE_ARM_MOVING_TO_INIT_POSITION,
        EXTENSION_ARM_MOVING_TO_INIT_POSITION,
        READY_FOR_INIT,

        // init states
        EXTENSION_ARM_RESETTING_FOR_INIT,
        BUCKET_MOVING_TO_INIT_POSITION,
        //INTAKE_ARM_MOVING_TO_INIT_POSITION, REUSED
        //EXTENSION_ARM_MOVING_TO_INIT_POSITION, REUSED
        INIT_COMPLETE,

        // get ready to run states
        EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_FOR_GET_READY_TO_RUN,
        BUCKET_MOVING_TO_TRANSFER_POSITION_FOR_GET_READY_TO_RUN,
        EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION_FOR_GET_READY_TO_RUN,
        READY_TO_RUN,
        TRANSFER_COMPLETE,

        // setup for driving to delivery states
        INTAKE_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_SAFE_DRIVING_POSITION_BEFORE_DELIVERY,
        BUCKET_ARM_MOVING_TO_SAFE_POSITION_BEFORE_DELIVERY,
        AT_SAFE_POSITION_BEFORE_DELIVERY,

        // setup for delivery states
        BUCKET_ARM_MOVING_TO_DELIVERY_POSITION,
        BUCKET_ARM_AT_DELIVERY_POSITION,

        // deliver sample states
        DELIVERING_SAMPLE_AND_BUCKET_MOVING_TO_TRANSFER_POSITION,
        EXTENSION_ARM_INTAKE_MOVING_TO_TRANSFER_POSITION,
        AT_TRANSFER_POSITION_AFTER_DELIVERY,

        // setup for intake states
        EXTENSION_ARM_INTAKE_MOVING_TO_SETUP_FOR_INTAKE_POSITION,
        EXTENSION_ARM_INTAKE_AT_SETUP_FOR_INTAKE_POSITION,

        //intake states
        INTAKING,
        WAITING_FOR_READY_TO_TRANSFER,
        TRANSFERRING,
        // TRANSFER_COMPLETE (REUSED)

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
    private DataLogOnChange logStateOnChange;
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

    private boolean getReadyToRunComplete = false;

    public boolean isGetReadyToRunComplete() {
        return getReadyToRunComplete;
    }

    private boolean setupForDeliveryComplete = false;

    public boolean isSetupForDeliveryComplete() {
        return setupForDeliveryComplete;
    }

    private boolean deliveryComplete = false;

    public boolean isDeliveryComplete() {
        return deliveryComplete;
    }

    private boolean setupForIntakeComplete = false;

    public boolean isSetupForIntakeComplete() {
        return setupForIntakeComplete;
    }

    private boolean transferComplete = false;

    public boolean isTransferComplete() {
        return transferComplete;
    }

    private boolean showMaxExtension = false;
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ITDIntakeBucketController(HardwareMap hardwareMap, Telemetry telemetry) {

        timer = new ElapsedTime();
        state = IntakeBucketControllerState.IDLE;
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
     * This moves the bucket, lift, extension arm and intake arm into positions so that when the driver
     * hits init not much needs to happen.
     * Steps:
     * reset the extension arm so it knows where 0 is
     * move the extension arm and intake arm to a position where the intake is out of the way of the bucket
     * move the bucket arm to its init position
     * rotate the intake to its init position
     * retract the extension arm
     * open the bucket gate so a sample can be inserted
     */
    public void setupForInit() {
        logCommand("Setup for init");
        extensionArmIntakeController.setupForInitBucketClearance();
        state = IntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_INIT_SETUP;
    }

    /**
     * The extension arm, intake arm, bucket, gate and lift should all be near their init positions. This
     * gets run when the driver presses the init.
     * Steps:
     * reset the extension arm so it knows where 0 is
     * DO NOT move the extension arm and intake arm to a position where the intake is out of the way of the bucket
     * move the bucket arm to its init position
     * rotate the intake to its init position
     * retract the extension arm
     * open the bucket gate so a sample can be inserted
     *
     * @param config
     * @return
     */
    @Override
    public boolean init(Configuration config) {
        logCommand("Init");
        //todo remove this when this class is finished and let the real init set it
        initComplete = true;
        extensionArmIntakeController.initFromIntakeBucketController(config);
        state = IntakeBucketControllerState.EXTENSION_ARM_RESETTING_FOR_INIT;
        return true;
    }

    /**
     * This method is called when the driver hits play in teleop. The extension arm, bucket and
     * intake arm need to get into position for running. The bucket gate needs to close.
     * Steps:
     * move the intake out of the way
     * move the bucket into transfer position
     * move the intake into transfer position
     */
    public void getReadyToRun() {
        logCommand("Get ready to run");
        extensionArmIntakeController.getReadyToRun();
        state = IntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_FOR_GET_READY_TO_RUN;
    }

    /**
     * This method shold be called after a transfer has occurred. It prepares for a delivery and
     * shortcuts the time to get setup for the delivery
     * Steps:
     * move the intake out of the way
     * rotate the bucket arm to vertical
     * raise the lift to height needed for delivery
     */
    public void setupForDrivingBeforeDelivery() {
        logCommand("Setup for driving before delivery");
        setupForDeliveryComplete = false;
        extensionArmIntakeController.setupForBucketClearance();
        state = IntakeBucketControllerState.INTAKE_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_SAFE_DRIVING_POSITION_BEFORE_DELIVERY;
    }

    /**
     * This method puts bucket over the basket and is meant to be called after most of the driving
     * over to the basket has been done.
     * Steps:
     * rotate bucket orm horizontal (the lift is already at delivery height)
     */
    public void setupForDelivery() {
        logCommand("Setup for delivery");
        liftBucketArmBucketGateController.setupForDelivery();
        state = IntakeBucketControllerState.BUCKET_ARM_MOVING_TO_DELIVERY_POSITION;
    }

    /**
     * This method opens the gate and allows the sample to drop into the basket. Then it puts the
     * bucket arm back into the transfer position.
     * Steps:
     * open the bucket gate
     * wait for the sample to fall out
     * rotate the bucket arm to the vertical position so that when the lift moves it does not
     * put as much stress on the bucket arm servo
     * move the lift to the transfer position
     * move the bucket arm to the transfer position
     * move the intake arm and the extension arm to the transfer position
     * close the bucket gate servo
     */
    public void deliverSample() {
        // lock out any attempt to deliver sample unless bucket arm is at the delivery position
        if (state == state.BUCKET_ARM_AT_DELIVERY_POSITION) {
            logCommand("Deliver sample");
            deliveryComplete = false;
            liftBucketArmBucketGateController.deliverSample();
            state = IntakeBucketControllerState.DELIVERING_SAMPLE_AND_BUCKET_MOVING_TO_TRANSFER_POSITION;
        }
    }

    /**
     * This method will extend the extension arm in preparation for an intake.
     * Steps:
     * extend extension arm to intake position (the intake arm is still up in the air though)
     */
    public void setupForIntake() {
        logCommand("Setup for intake");
        setupForIntakeComplete = false;
        extensionArmIntakeController.setupForIntake();
        liftBucketArmBucketGateController.closeGate();
        state = IntakeBucketControllerState.EXTENSION_ARM_INTAKE_MOVING_TO_SETUP_FOR_INTAKE_POSITION;
    }

    /**
     * This method puts the intake on the floor and starts an intake sequence. The intake automatically
     * filters through the samples until it has a good one, then it moves into the transfer position
     * Steps:
     * turn on the intake
     * rotate the intake onto the floor
     * intake runs until it has a good sample
     * rotate the intake back up into the transfer position
     * retract the extension arm until the intake is in the transfer position
     * transfer the sample into the bucket
     */
    public void intake() {
        logCommand("Intake");
        transferComplete = false;
        extensionArmIntakeController.intake();
        state = IntakeBucketControllerState.INTAKING;

    }

    public void runGlidingIntake() {
        logCommand("Glide Intake");
        setGlidingIntakeFailed(false);
        transferComplete = false;
        extensionArmIntakeController.setupForGlidingIntake();
        state = IntakeBucketControllerState.INTAKING;
    }
    public void showMaxExtension() {
        logCommand("Max extension");
        showMaxExtension = true;
        init(null);
    }


    //*********************************************************************************************
    //          Communication from Extension arm / intake
    //*********************************************************************************************
    /**
     * Allow the intake to request the controller to move the extension arm out so an outtake can
     * take place while the intake is not over the robot body. This occurs when we have a bad jam
     * that cannot be cleared any other way.
     */
    private boolean intakesRequestsAnOuttake = false;

    public void setIntakesRequestsAnOuttake(boolean needOuttake) {
        this.intakesRequestsAnOuttake = needOuttake;
        logComment("Intake requests outtake");
    }

    private boolean intakeReadyForInit = false;

    public void setIntakeReadyForInit(boolean intakeReadyForInit) {
        this.intakeReadyForInit = intakeReadyForInit;
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

    private boolean glidingIntakeFailed = false;

    public void setGlidingIntakeFailed(boolean glidingIntakeFailed) {
        this.glidingIntakeFailed = glidingIntakeFailed;
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

    public boolean isIntakeHasValidSample() {
        return intakeHasValidSample;
    }

    public boolean intakeReadyForTransfer = false;

//    public void setIntakeReadyForTransfer(boolean intakeReadyForTransfer) {
//        this.intakeReadyForTransfer = intakeReadyForTransfer;
//    }

    //*********************************************************************************************
    //          Communication from Lift / bucket / bucket gate controller
    //*********************************************************************************************

    private boolean liftBucketResetComplete = false;

    public void setLiftBucketResetComplete(boolean resetComplete) {
        this.liftBucketResetComplete = resetComplete;
    }

    private boolean liftBucketAtInitPosition = false;

    public void setLiftBucketAtInitPosition(boolean liftBucketAtInitPosition) {
        this.liftBucketAtInitPosition = liftBucketAtInitPosition;
    }

    private boolean liftBucketAtTransferPosition = false;

    public void setLiftBucketAtTransferPosition(boolean liftBucketAtTransferPosition) {
        this.liftBucketAtTransferPosition = liftBucketAtTransferPosition;
    }

    private boolean liftBucketAtSafeToDrivePosition = false;

    public void setLiftBucketAtSafeToDrivePosition(boolean liftBucketAtSafeToDrivePosition) {
        this.liftBucketAtSafeToDrivePosition = liftBucketAtSafeToDrivePosition;
    }

    private boolean liftBucketAtReadyToDeliverPosition = false;

    public void setLiftBucketAtReadyToDeliverPosition(boolean liftBucketAtReadyToDeliverPosition) {
        this.liftBucketAtReadyToDeliverPosition = liftBucketAtReadyToDeliverPosition;
    }

    private boolean liftBucketSampleIsDelivered = false;

    public void setLiftBucketSampleIsDelivered(boolean liftBucketSampleIsDelivered) {
        this.liftBucketSampleIsDelivered = liftBucketSampleIsDelivered;
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
        telemetry.addData("IBC State = ", state.toString());
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

        switch (state) {
            case IDLE:
                // just hang out and wait for a command
                break;

            // setup for init - run before arriving for match
//            case EXTENSION_ARM_RESETTING_FOR_INIT_SETUP:
//                if (extensionArmResetComplete) {
//                    extensionArmIntakeController.setupForInitBucketClearance();
//                    state = IntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_INIT_SETUP;
//                }
//                break;
            case EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_INIT_SETUP:
                // have the extension arm and intake arm reached the bucket clearance positions?
                if (intakePositionedForBucketClearance) {
                    liftBucketArmBucketGateController.setupForInit();
                    state = IntakeBucketControllerState.BUCKET_MOVING_TO_INIT_POSITION_FOR_INIT_SETUP;
                }
                break;
            case BUCKET_MOVING_TO_INIT_POSITION_FOR_INIT_SETUP:
                if (liftBucketAtInitPosition) {
                    extensionArmIntakeController.completeSetupForInit();
                    state = IntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_INIT_POSITION;
                }
                break;
            case EXTENSION_ARM_MOVING_TO_INIT_POSITION:
                if (intakeReadyForInit) {
                    state = IntakeBucketControllerState.READY_FOR_INIT;
                }
                break;
            case READY_FOR_INIT:
                // driver may leave robot turned on or may turn it off
                break;

            // init states
            case EXTENSION_ARM_RESETTING_FOR_INIT:
                if (extensionArmResetComplete) {
                    liftBucketArmBucketGateController.initFromIntakeBucketController(null);
                    state = IntakeBucketControllerState.BUCKET_MOVING_TO_INIT_POSITION;
                }
                break;
            case BUCKET_MOVING_TO_INIT_POSITION:
                if (liftBucketAtInitPosition) {
                    state = IntakeBucketControllerState.INIT_COMPLETE;
                }
                break;
            case INIT_COMPLETE:
                // wait for get ready to run or for other commands
                if (showMaxExtension) {
                    getReadyToRun();
                }
                break;

            // get ready to run states
            case EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_FOR_GET_READY_TO_RUN:
                if (intakePositionedForBucketClearance) {
                    liftBucketArmBucketGateController.getReadyToRun();
                    state = IntakeBucketControllerState.BUCKET_MOVING_TO_TRANSFER_POSITION_FOR_GET_READY_TO_RUN;
                }
                break;
            case BUCKET_MOVING_TO_TRANSFER_POSITION_FOR_GET_READY_TO_RUN:
                if (liftBucketAtTransferPosition) {
                    extensionArmIntakeController.completeGetReadyToRun();
                    state = IntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION_FOR_GET_READY_TO_RUN;
                }
                break;
            case EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION_FOR_GET_READY_TO_RUN:
                if (intakePositionedForTransfer) {
                    getReadyToRunComplete = true;
                    state = IntakeBucketControllerState.READY_TO_RUN;
                }
                break;
            case READY_TO_RUN:
                // we are ready to run. Wait for a command
                if(showMaxExtension) {
                    setupForIntake();
                }
                break;

            // setup for safe driving position before delivery states
            case INTAKE_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_SAFE_DRIVING_POSITION_BEFORE_DELIVERY:
                if (intakePositionedForBucketClearance) {
                    liftBucketArmBucketGateController.setupForDrivingBeforeDelivery();
                    state = IntakeBucketControllerState.BUCKET_ARM_MOVING_TO_SAFE_POSITION_BEFORE_DELIVERY;
                }
                break;
            case BUCKET_ARM_MOVING_TO_SAFE_POSITION_BEFORE_DELIVERY:
                if (liftBucketAtSafeToDrivePosition) {
                    state = IntakeBucketControllerState.BUCKET_ARM_MOVING_TO_DELIVERY_POSITION;
                }
                break;
                // skipping over this state because the liftBucketArmBucketGate controller immediately
                // jumped into moving the bucket arm to the delivery position
            case AT_SAFE_POSITION_BEFORE_DELIVERY:
                // hang out waiting for driver to give setup for delivery command
                break;

            // setup for delivery states
            case BUCKET_ARM_MOVING_TO_DELIVERY_POSITION:
                if (liftBucketAtReadyToDeliverPosition) {
                    setupForDeliveryComplete = true;
                    state = IntakeBucketControllerState.BUCKET_ARM_AT_DELIVERY_POSITION;
                }
                break;
            case BUCKET_ARM_AT_DELIVERY_POSITION:
                // hang out waiting for driver to give deliver sample command
                if(showMaxExtension) {
                    showMaxExtension = false;
                }
                break;

            // deliver sample states
            case DELIVERING_SAMPLE_AND_BUCKET_MOVING_TO_TRANSFER_POSITION:
                if (liftBucketAtTransferPosition) {
                    extensionArmIntakeController.setupIntakeAfterDeliver();
                    state = IntakeBucketControllerState.EXTENSION_ARM_INTAKE_MOVING_TO_TRANSFER_POSITION;
                }
                break;
            case EXTENSION_ARM_INTAKE_MOVING_TO_TRANSFER_POSITION:
                if (intakePositionedForTransfer) {
                    deliveryComplete = true;

                    state = IntakeBucketControllerState.AT_TRANSFER_POSITION_AFTER_DELIVERY;
                }
                break;
            case AT_TRANSFER_POSITION_AFTER_DELIVERY:
                // hang out waiting for the next command
                // it should be to setup for intake (after delivering a sample)
                break;

            // setup for intake states
            case EXTENSION_ARM_INTAKE_MOVING_TO_SETUP_FOR_INTAKE_POSITION:
                if (intakePositionReached) {
                    setupForIntakeComplete = true;
                    state = IntakeBucketControllerState.EXTENSION_ARM_INTAKE_AT_SETUP_FOR_INTAKE_POSITION;
                }
                break;
            case EXTENSION_ARM_INTAKE_AT_SETUP_FOR_INTAKE_POSITION:
                // hang out waiting for an intake command
                // or a back to transfer position command
                if (showMaxExtension) {
                    extensionArmIntakeController.intakeToFloor();
                    setupForDrivingBeforeDelivery();
                }
                break;

            // Intake states
            case INTAKING:
                if (intakeHasValidSample && intakePositionedForTransfer) {
                    liftBucketArmBucketGateController.openGate();
                    extensionArmIntakeController.transfer();
                    state = IntakeBucketControllerState.TRANSFERRING;
                }
                if (glidingIntakeFailed) {

                }
                break;
            case WAITING_FOR_READY_TO_TRANSFER:
                break;
            case TRANSFERRING:
                if (intakeTransferComplete) {
                    transferComplete = true;
                    state = IntakeBucketControllerState.TRANSFER_COMPLETE;
                }
                break;
            case TRANSFER_COMPLETE:
                // wait for a command
                break;
        }

    }
}



