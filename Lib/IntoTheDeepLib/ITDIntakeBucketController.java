package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ITDIntakeBucketController implements FTCRobotSubsystem {

    private static final Logger log = LoggerFactory.getLogger(ITDIntakeBucketController.class);

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum IntakeBucketControllerState {
        IDLE,

        WAITING_FOR_BUCKET_CLEARANCE_FOR_TELEOP_SETUP,

        WAITING_FOR_TELEOP_LIFT_RESET,

        WAITING_FOR_BUCKET_ARM_FOR_TELEOP_SETUP,

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

        // bucket clearance states
        WAIT_FOR_BUCKET_CLEARANCE,
        AT_BUCKET_CLEARANCE,

        // setup for driving to delivery states
        INTAKE_MOVING_TO_BUCKET_CLEARANCE_POSITION_BEFORE_DELIVERY,
        BUCKET_ARM_MOVING_TO_VERTICAL_POSITION,
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
        //setup for gliding intake states
        WAIT_FOR_SETUP_FOR_GLIDING_INTAKE,
        SETUP_FOR_GLIDING_INTAKE_COMPLETE

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
        glidingIntakeMaxExtension = extensionArmIntakeController.extensionArm.getMaxPosition();
        glidingIntakePower = extensionArmIntakeController.extensionArm.getExtendPower();
    }

    private ITDAutonomousStateMachineNewold autonomousStateMachine;

    public void setAutonomousStateMachine(ITDAutonomousStateMachineNewold autonomousStateMachine) {
        this.autonomousStateMachine = autonomousStateMachine;
    }

    private double glidingIntakeMaxExtension;
    private double glidingIntakePower;
    //*********************************************************************************************
    //          Status methods
    //*********************************************************************************************

    private boolean getReadyToRunComplete = false;

    public boolean isGetReadyToRunComplete() {
        return getReadyToRunComplete;
    }

    private boolean setupForDeliveryComplete = false;

    public boolean isSetupForDeliveryComplete() {
        return setupForDeliveryComplete;
    }

//    private boolean bucketAtTransferPositionAfterDelivery = false;
//
//    public boolean isBucketAtTransferPositionAfterDelivery() {
//        return bucketAtTransferPositionAfterDelivery;
//    }

    private boolean setupForIntakeComplete = false;

    public boolean isSetupForIntakeComplete() {
        return setupForIntakeComplete;
    }

    private boolean transferComplete = false;

    public boolean isTransferComplete() {
        return transferComplete;
    }

    private boolean showMaxExtension = false;
    private boolean runGlidingIntakeAfterSetup = false;
    private boolean setupBucketForClearanceNotCalledYet = true;
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

    public void setUpForTeleop(){
        // rotate the intake out of the way
        extensionArmIntakeController.setupForBucketClearance();
        // reset the extension arm
        extensionArmIntakeController.extensionArm.reset();
        state = IntakeBucketControllerState.WAITING_FOR_BUCKET_CLEARANCE_FOR_TELEOP_SETUP;
        // reset the lift
        // rotate the bucket
    }

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

    public void setupForBucketClearance() {
        logCommand("Setup For bucket clearance");
        // this is here because if the driver needs a bucket clearance right after init in order to
        // deliver a sample, setupForBucketClearance has to be called.
        setupBucketForClearanceNotCalledYet = false;
        extensionArmIntakeController.setupForBucketClearance();
        state = IntakeBucketControllerState.WAIT_FOR_BUCKET_CLEARANCE;
    }

    /**
     * This method shold be called after a transfer has occurred. It prepares for a delivery and
     * shortcuts the time to get setup for the delivery
     * Steps:
     * move the intake out of the way
     * rotate the bucket arm to vertical
     * raise the lift to height needed for delivery
     */
    public void setupForDelivery() {
        if(isTransferComplete()) {
            logCommand("Setup for delivery");
            setupForDeliveryComplete = false;
            // to save time, setupForBucketClearance() is now called right after the transfer completes by the
            // extensionArmIntakeController. This also moves the extension arm away from the metal that it is
            // trying to pull against. Doing that prevents the motor from overheating.

            // However, if the driver needs to deliver a sample right after init in teleop, then
            // setupForBucketClearance has never been called. So we need to call it.
            if (setupBucketForClearanceNotCalledYet) {
                extensionArmIntakeController.setupForBucketClearance();
                setupBucketForClearanceNotCalledYet = false;
            }
            state = IntakeBucketControllerState.INTAKE_MOVING_TO_BUCKET_CLEARANCE_POSITION_BEFORE_DELIVERY;
        }
        else {
            log("Setup for delivery ignored");
        }
    }

    /**
     * Normally setup for bucket clearance is called automatically right after a transfer. However,
     * when we want to deliver at the start of auto, no transfer has occurred so we do have to run
     * setupForBucketClearance(). From that point we can run the normal setupForDelivery()
     */
    public void setupForDeliveryUponStart() {
        logCommand("Setup for delivery upon start");
        transferComplete=true;
        // since setup for bucket clearance has not been run yet, run it.
        extensionArmIntakeController.setupForBucketClearance();
        // then follow the normal setup for driving before delivery
        setupForDelivery();
    }

    /**
     * This method puts bucket over the basket and is meant to be called after most of the driving
     * over to the basket has been done.
     * Steps:
     * rotate bucket orm horizontal (the lift is already at delivery height)
     */
    public void lineupForDelivery() {
        logCommand("Lineup for delivery");
        liftBucketArmBucketGateController.lineupForDelivery();
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
            liftBucketAtTransferPosition = false;
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
    public void setupForIntake(double extentionArmPosition) {
        logCommand("Setup for intake");
        setupForIntakeComplete = false;
        extensionArmIntakeController.setupForIntake(extentionArmPosition);
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
    public void intakeLowAltitude() {
        logCommand("Intake");
        transferComplete = false;
        extensionArmIntakeController.intakeLowAltitude();
        state = IntakeBucketControllerState.INTAKING;

    }

    public void intakeHighAltitude() {
        logCommand("Intake");
        transferComplete = false;
        extensionArmIntakeController.intakeHighAltitude();
        state = IntakeBucketControllerState.INTAKING;

    }

    /**
     * This method will extend the extension arm and rotate the intake to the floor at the same time.
     * The amount of extension is a parameter. Usually around 2".
     *
     * @param extensionArmPosition
     */
    public void setupForGlidingIntake(double extensionArmPosition) {
        logCommand("Setup for gliding intake");
        extensionArmIntakeController.setupForGlidingIntake(extensionArmPosition);
        liftBucketArmBucketGateController.closeGate();
        state=IntakeBucketControllerState.WAIT_FOR_SETUP_FOR_GLIDING_INTAKE;
    }

    /**
     * This method will run a gliding intake. It assumes that you have already set it up (run
     * setupForGlidingIntake) first.
     *    When complete the following are set if a sample was gotten:
     *     - intakeHasValidSample = true
     *     - glidingIntakeFailed = false
     *     - intakePositionedForTransfer = true
     *     - The intake will be positioned at the transfer position
     *    When complete the following are set if a sample was not gotten:
     *      - intakeHasValidSample = false
     *      - glidingIntakeFailed = false
     *      - The intake will be at extension arm = 2" and rotated to readyForIntake position.
     */
    public void runGlidingIntake() {
        runGlidingIntake(extensionArmIntakeController.extensionArm.getMaxPosition(), extensionArmIntakeController.extensionArm.getExtendPower());
    }

    /**
     * Same as above except that you can specify how far the extension arm extends before stopping.
     * @param maxPosition
     */
    public void runGlidingIntake(double maxPosition) {
        runGlidingIntake(maxPosition, extensionArmIntakeController.extensionArm.getExtendPower());
    }

    public void runGlidingIntake(double maxPosition, double power,double glidingIntakeDelay) {
        logCommand("Run Gliding Intake");
        setGlidingIntakeFailed(false);
        transferComplete = false;
        extensionArmIntakeController.runGlidingIntake(maxPosition, power,glidingIntakeDelay);
        state = IntakeBucketControllerState.INTAKING;
    }
    public void runGlidingIntake(double maxPosition, double power) {
        runGlidingIntake(maxPosition,power,0);
    }

    public void setupAndRunGlidingIntake() {
        glidingIntakeMaxExtension = extensionArmIntakeController.extensionArm.getMaxPosition();
        glidingIntakePower = extensionArmIntakeController.extensionArm.getExtendPower();
        setupAndRunGlidingIntake(glidingIntakeMaxExtension, glidingIntakePower);
    }

    public void setupAndRunGlidingIntake(double power) {
        glidingIntakeMaxExtension = extensionArmIntakeController.extensionArm.getMaxPosition();
        glidingIntakePower = power;
        setupAndRunGlidingIntake(glidingIntakeMaxExtension, power);
    }

    public void setupAndRunGlidingIntake(double maxPosition, double power) {
        glidingIntakeMaxExtension = maxPosition;
        glidingIntakePower = power;
        runGlidingIntakeAfterSetup = true;
        setupForGlidingIntake(2.0);
    }

    public void showMaxExtension() {
        logCommand("Show max extension");
        showMaxExtension = true;
        setupForIntake();
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

    public boolean isIntakePositionedForBucketClearance() {
        return intakePositionedForBucketClearance;
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

    public boolean intakeHasValidSample() {
        return intakeHasValidSample;
    }

    private boolean setupForGlidingIntakeComplete = false;

    public void setSetupForGlidingIntakeComplete(boolean setupForGlidingIntakeComplete) {
        this.setupForGlidingIntakeComplete = setupForGlidingIntakeComplete;
    }

    public boolean isSetupForGlidingIntakeComplete() {
        return setupForGlidingIntakeComplete;
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

    public boolean isLiftBucketAtTransferPosition() {
        return liftBucketAtTransferPosition;
    }

    private boolean liftBucketAtVerticalPosition = false;

    public void setLiftBucketAtVerticalPosition(boolean liftBucketAtVerticalPosition) {
        this.liftBucketAtVerticalPosition = liftBucketAtVerticalPosition;
    }

    private boolean liftBucketAtDeliveryPosition = false;

    public void setLiftBucketAtDeliveryPosition(boolean liftBucketAtDeliveryPosition) {
        this.liftBucketAtDeliveryPosition = liftBucketAtDeliveryPosition;
    }

    private boolean liftBucketSampleIsDelivered = false;

    public void setLiftBucketSampleIsDelivered(boolean liftBucketSampleIsDelivered) {
        this.liftBucketSampleIsDelivered = liftBucketSampleIsDelivered;
    }

    public boolean isLiftBucketSampleIsDelivered() {
        return liftBucketSampleIsDelivered;
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
    protected void log(String stringToLog) {
        if (logFile != null && loggingOn) {
            logFile.logData(CONTROLLER_NAME, stringToLog);
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

            case WAITING_FOR_BUCKET_CLEARANCE_FOR_TELEOP_SETUP:
                if (extensionArmIntakeController.intakeArmServo.isPositionReached()) {
                    liftBucketArmBucketGateController.lift.reset();
                    state = IntakeBucketControllerState.WAITING_FOR_TELEOP_LIFT_RESET;
                }
                break;

            case WAITING_FOR_TELEOP_LIFT_RESET:
                if (liftBucketArmBucketGateController.lift.isResetComplete()) {
                    liftBucketArmBucketGateController.bucketArmServo.transferPosition();
                    state = IntakeBucketControllerState.WAITING_FOR_BUCKET_ARM_FOR_TELEOP_SETUP;
                }
                break;

            case WAITING_FOR_BUCKET_ARM_FOR_TELEOP_SETUP:
                if (liftBucketArmBucketGateController.bucketArmServo.isPositionReached()) {
                    state = IntakeBucketControllerState.IDLE;
                }
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
                break;

            // bucket clearance states
            case WAIT_FOR_BUCKET_CLEARANCE:
                if (intakePositionedForBucketClearance) {
                    state = IntakeBucketControllerState.AT_BUCKET_CLEARANCE;
                }
                break;
            case AT_BUCKET_CLEARANCE:
                // do nothing while waiting for a command
                break;

            // SETUP for delivery states
            case INTAKE_MOVING_TO_BUCKET_CLEARANCE_POSITION_BEFORE_DELIVERY:
                // We should not have to wait long (if at all) for bucket clearance because bucket clearance
                // was automatically called right after the transfer completed, by the extensionArmIntake controller.
                if (intakePositionedForBucketClearance) {
                    liftBucketArmBucketGateController.setupForDelivery();
                    state = IntakeBucketControllerState.BUCKET_ARM_MOVING_TO_VERTICAL_POSITION;
                }
                break;
            case BUCKET_ARM_MOVING_TO_VERTICAL_POSITION:
                if (liftBucketAtVerticalPosition) {
                    // The LiftBucketArmBucketGateController automatically sends the lift bucket to
                    // the lineup/delivery position so we don't have to issue the command for that
                    state = IntakeBucketControllerState.BUCKET_ARM_MOVING_TO_DELIVERY_POSITION;
                }
                break;
            // skipping over this state because the liftBucketArmBucketGate controller immediately
            // jumped into moving the bucket arm to the delivery position
//            case AT_SAFE_POSITION_BEFORE_DELIVERY:
//                // hang out waiting for driver to give setup for delivery command
//                break;

            // LINEUP for delivery states
            case BUCKET_ARM_MOVING_TO_DELIVERY_POSITION:
                if (liftBucketAtDeliveryPosition) {
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
                    // we don't want the intake to go back to the transfer position after a delivery
                    //extensionArmIntakeController.setupIntakeAfterDeliver();
                    liftBucketAtTransferPosition = true;
                    state = IntakeBucketControllerState.AT_TRANSFER_POSITION_AFTER_DELIVERY;
                }
                break;
//            case EXTENSION_ARM_INTAKE_MOVING_TO_TRANSFER_POSITION:
//                if (intakePositionedForTransfer) {
//                    state = IntakeBucketControllerState.AT_TRANSFER_POSITION_AFTER_DELIVERY;
//                }
//                break;
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
                    // skip over the bucket clearance normally required before the bucket rotates up
                    // since the intake is all the way out already
                    liftBucketArmBucketGateController.setupForDelivery();
                    state = IntakeBucketControllerState.BUCKET_ARM_MOVING_TO_VERTICAL_POSITION;
                }
                break;

            //setup for gliding intake states
            case WAIT_FOR_SETUP_FOR_GLIDING_INTAKE:
                if(setupForGlidingIntakeComplete){
                    if (runGlidingIntakeAfterSetup) {
                        // the command was to run the gliding intake after setting it up
                        // reset the flag
                        runGlidingIntakeAfterSetup = false;
                        // now run the gliding intake
                        runGlidingIntake(glidingIntakeMaxExtension, glidingIntakePower);
                    } else {
                        // the command was just to setup the gliding intake
                        state=IntakeBucketControllerState.SETUP_FOR_GLIDING_INTAKE_COMPLETE;
                    }
                }
                break;
            case SETUP_FOR_GLIDING_INTAKE_COMPLETE:
                //waiting for a command
                break;

            // Intake states
            case INTAKING:
                if (intakeHasValidSample && intakePositionedForTransfer) {
                    //todo now that the extension arm is so fast, we may need to open the gate sooner
                    // BUT we can't open it so soon that it gets stuck on the intake arm chain
                    // while the extension arm is retracting
                    liftBucketArmBucketGateController.openGate();
                    //todo alternatively we could delay the transfer for just a bit. This is
                    // probably the better option.
                    extensionArmIntakeController.transfer();
                    state = IntakeBucketControllerState.TRANSFERRING;
                }
                if (glidingIntakeFailed) {
                    // reset the flag so future intakes don't see a gliding intake failed
                    glidingIntakeFailed = false;
                    logCommand("Gliding intake failed");
                    if (MatchPhase.getMatchPhase() == MatchPhase.AUTONOMOUS) {
                        // tell the autonomous that the gliding intake failed
                        autonomousStateMachine.setGlidingIntakeFailed(true);
                        // autonomous will have to tell us what to do if the gliding intake failed
                        state = IntakeBucketControllerState.IDLE;
                    }
                    if (MatchPhase.getMatchPhase() == MatchPhase.TELEOP) {
                        // extension arm intake controller automatically will setup for bucket clearance
                        state = IntakeBucketControllerState.WAIT_FOR_BUCKET_CLEARANCE;
                    }
                }
                break;
            case WAITING_FOR_READY_TO_TRANSFER:
                break;
            case TRANSFERRING:
                if (intakeTransferComplete) {
                    transferComplete = true;
                    setupForDelivery();
                }
                break;
//            case TRANSFER_COMPLETE:
//                break;
        }

    }
}



