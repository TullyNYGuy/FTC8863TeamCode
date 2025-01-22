package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class ITDExtensionArmIntakeController implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum ExtensionArmIntakeBucketControllerState {
        IDLE,

        // setup for init states (run prior to arriving at match),

        EXTENSION_ARM_RESETTING_FOR_INIT_SETUP,
        EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_INIT_SETUP,
        WAIT_FOR_BUCKET_MOVING_TO_INIT_POSITION,
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
        WAITING_FOR_BUCKET_TO_MOVE_TO_TRANSFER_POSITION,
        EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION_FOR_GET_READY_TO_RUN,
        // TRANSFER_COMPLETE, defined later as part of the transfer states

        // setup for bucket clearance states
        EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE,
        AT_BUCKET_CLEARANCE_POSITION,

        // setup for intake states
        EXTENSION_ARM_MOVING_TO_INTAKE_POSITION,
        AT_INTAKE_POSITION,

        // intake states
        WAITING_FOR_A_GOOD_SAMPLE,
        INTAKE_STOPPED,

        //setup for transfer states
        INTAKE_ARM_MOVING_TO_TRANSFER_POSITION,
        EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION,
        AT_TRANSFER_POSITION,

        // transfer states
        TRANSFERRING_SAMPLE,
        MOVING_TO_OUTTAKE_POSITION,
        OUTTAKING_SAMPLE_AFTER_TRANSFER_ATTEMPT,
        TRANSFER_COMPLETE,

        // deliver sample to bin states
        EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_FOR_DELIVERY,
        LIFT_MOVING_TO_DELIVERY_POSITION,
        BUCKET_ARM_MOVING_TO_DELIVERY_POSITION,
        READY_FOR_DELIVERY,

        // state for command from the driver
        OUTTAKING_SAMPLE,

        RESETTING
    }

    private ExtensionArmIntakeBucketControllerState state;

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
    private final String CONTROLLER_NAME = ITDRobot.HardwareName.EXTENSION_ARM_INTAKE_CONTROLLER.hwName;

    private ITDIntakeSweeperVertical intake;

    private ITDExtensionArm extensionArm;
    private ITDIntakeArmServo intakeArmServo;

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

    public ITDExtensionArmIntakeController(HardwareMap hardwareMap, Telemetry telemetry) {
        extensionArm = new ITDExtensionArm(hardwareMap, telemetry);
        // Give the extension arm access to this controller so it can communicate back to this
        // controller.
        extensionArm.setController(this);
        intakeArmServo = new ITDIntakeArmServo(hardwareMap, telemetry);
        intake = new ITDIntakeSweeperVertical(hardwareMap, telemetry);
        // Give the intake access to this controller so it can communicate back to this
        // controller.
        intake.setController(this);

        timer = new ElapsedTime();
        state = ExtensionArmIntakeBucketControllerState.IDLE;
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

    public void setupAllianceColor (AllianceColor color) {
        intake.setAllianceColor(color);
    }

    /**
     * This starts the process of moving the bucket down into the belly of the robot so that it all
     * fits inside the 18" size limit. First it resets the extension arm so that it knows where
     * the starting point of any extension is. Then it extends the extension arm so that the bucket will
     * clear the intake. Then it rotates the bucket down towards the floor. Then it retracts the
     * extension arm back into the robot and finally rotates the intake so that fits inside the
     * 18" limit.
     */
    public void setupForInitBucketClearance() {
        logCommand("Setup for init bucket clearance");
        extensionArm.reset();
        setExtensionArmResetComplete(false);
        controller.setIntakeReadyForInit(false);
        intakeArmServo.bucketClearancePosition();
        state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_RESETTING_FOR_INIT_SETUP;
    }

    /**
     * We have been waiting for the bucket to move into the init position. The intake / bucket
     * controller has determined that the bucket movement has finished. So the next steps are to
     * retract the extension arm and rotate the intake to its init position.
     */
    public void completeSetupForInit() {
        logCommand("Complete setup for init");
        intakeArmServo.initPosition();
        extensionArm.initPosition();
        setExtensionArmResetComplete(false);
        state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_INIT_POSITION;
    }

    /**
     * This is the actual init for the robot. The assumption is that:
     * -  bucket is already at the init position, or at least tucked down into the belly of the robot.
     * So we are not going to move the extension arm to create clearance for it.
     * -  extension arm is already near the init position. It just needs to reset so it knows where its
     * 0 position is
     * gate servo is open, but we don't handle that here. The Lift / bucket arm / gate controller
     *   will take care of that
     * - the intake is at or near the init position
     * So really all we do here is reset the extension arm so it knows where 0 is, and make sure the
     *   intake is in the init position:
     *
     * @param config
     * @return
     */

    public boolean initFromIntakeBucketController(Configuration config) {
        initComplete = false;
        logCommand("Init");
        intakeArmServo.initPosition();
        extensionArm.reset();
        state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_RESETTING_FOR_INIT;
        return true;
    }

    @Override
    public boolean init(Configuration config) {
        return true;
    }



    /**
     * Once the init is run, we are waiting for a driver to press the play button. When that happens
     * we need to get the bucket safely out of the belly of the robot. To do that the intake has to
     * get out of the way first. Then the bucket will move to transfer, and finally the intake will
     * move to the transfer position. Then the robot can be run.
     */
    public void getReadyToRun() {
        logCommand("Get ready to run");
        // tell the intake / bucket controller that the bucket clearance position is NOT
        // reached. It has to wait until the bucket clearance position is reached before moving the
        // bucket.
        controller.setIntakePositionedForBucketClearance(false);
        // start the movement of the extension arm and the intake arm
        extensionArm.bucketClearancePosition();
        intakeArmServo.bucketClearancePosition();
        state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_FOR_GET_READY_TO_RUN;
    }

    /**
     * After the extension arm and intake were moved to the bucket clearance position, we have to
     * wait for the intake / bucket controller to tell us that that the bucket was moved to the
     * transfer position. Then we can complete getting ready to run. The controller does that by
     * calling this method.
     */
    public void completeGetReadyToRun() {
        logCommand("Complete get ready to run");
        // tell the intake / bucket controller that the transfer position is NOT reached.
        controller.setIntakePositionedForTransfer(false);
        // also tell the intake / bucket controller that a transfer has not been completed yet. This
        // is because after get ready to run is finished we will tell the intake / bucket controller
        // that a transfer is complete. Why not? We already have a sample in the bucket!
        setIntakeTransferComplete(false);
        // start the movement of the extension arm and rotation of the intake to the transfer position
        extensionArm.transferPosition();
        intakeArmServo.transferPosition();
        state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION_FOR_GET_READY_TO_RUN;
    }

    /**
     * The intake / controller wants us to make room for the bucket to move around. To do that we
     * need to move the extension arm and intake to the bucket clearance position.
     */
    public void setupForBucketClearance() {
        logCommand("Setup for bucket clearance");
        // tell the intake / bucket controller that the bucket clearance position is NOT
        // reached. It has to wait until the bucket clearance position is reached before moving the
        // bucket.
        controller.setIntakePositionedForBucketClearance(false);
        // start the movement of the extension arm and the intake arm
        extensionArm.bucketClearancePosition();
        intakeArmServo.transferPosition();
        state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE;
    }

    /**
     * The intake / bucket controller wants us to move to the intake position.
     */
    public void setupForIntake() {
        logCommand("Setup for intake");
        // tell the intake bucket controller that the position is not reached yet
        controller.setIntakePositionReached(false);
        extensionArm.intakePosition();
        intakeArmServo.readyToIntakePosition();
        // The intake is not lowered to the floor yet. Just for safety. That will happen when we
        // get the transfer command.
        state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_INTAKE_POSITION;

    }

    /**
     * The intake bucket controller wants us to intake. The intake is smart. It is going to filter
     * through the samples until it has a good one and then let us know. If we get a good sample,
     * then the setupForTranfer() will be automatically called.
     */
    public void intake() {
        logCommand("Intake");
        controller.setIntakeHasValidSample(false);
        intakeArmServo.intakePosition();
        intake.intake();
        state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_A_GOOD_SAMPLE;
    }

    /**
     * Stop the intake. It will remain at its current rotation and the intake will wait for another command.
     */
    public void stop() {
        logCommand("Stop");
        intake.stop();
        state = ExtensionArmIntakeBucketControllerState.INTAKE_STOPPED;
    }

    /**
     * This command is automatically called by the state machine when the intake has a valid sample.
     * The driver could also call it independently if needed.
     */
    public void setupForTransfer() {
        logCommand("Setup for transfer");
        controller.setIntakePositionedForTransfer(false);
        intakeArmServo.transferPosition();
        // tell the intake / bucket controller that the intake is not ready for a transfer yet
        state = ExtensionArmIntakeBucketControllerState.INTAKE_ARM_MOVING_TO_TRANSFER_POSITION;
    }
    public void setupIntakeAfterDeliver() {
        logCommand("Setup Intake After Delivery");
        controller.setIntakePositionedForTransfer(false);
        intakeArmServo.readyToIntakePosition();
        // tell the intake / bucket controller that the intake is not ready for a transfer yet
        state = ExtensionArmIntakeBucketControllerState.INTAKE_ARM_MOVING_TO_TRANSFER_POSITION;
    }

    public void transfer() {
        logCommand("Transfer");
        controller.setIntakeTransferComplete(false);
        intake.transfer();
        state = ExtensionArmIntakeBucketControllerState.TRANSFERRING_SAMPLE;
    }

    public void setupForOuttake() {
        logCommand("Setup for outtake");
        extensionArm.bucketClearancePosition();
        intakeArmServo.bucketClearancePosition();
        state = ExtensionArmIntakeBucketControllerState.MOVING_TO_OUTTAKE_POSITION;
    }

    public void outtake() {
        logCommand("Outtake");
        intake.outtake();
        state = ExtensionArmIntakeBucketControllerState.OUTTAKING_SAMPLE;
    }

    public void reset() {
        logCommand("Reset");
        intake.reset();
        state = ExtensionArmIntakeBucketControllerState.RESETTING;
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
        logComment("Intake requests outtake");
    }

    private boolean outtakeComplete = false;

    public void setOuttakeComplete(boolean outtakeComplete) {
        this.outtakeComplete = outtakeComplete;
    }

    /**
     * The intake says that it has transferred the sample to the bucket
     */
    private boolean intakeTransferComplete = false;

    /**
     * This method gets called by the intake to let this controller know that the transfer of a
     * sample into the bucket has been completed, or to set up that it is about to start.
     * This method can also be called by this controller when the match starts to indicate that
     * there is a pre-loaded sample in the bucket.
     * @param transferComplete
     */
    public void setIntakeTransferComplete(boolean transferComplete) {
        this.intakeTransferComplete = transferComplete;
        controller.setIntakeTransferComplete(transferComplete);
        if (transferComplete) {
            // if the transfer was completed then the intake is no longer ready for a transfer and
            // no longer has a valid sample
            controller.setIntakeHasValidSample(false);
        }

    }

    /**
     * The intake says it has a good sample.
     */
    private boolean intakeHasValidSample = false;

    public void setIntakeHasValidSample(boolean intakeHasValidSample) {
        this.intakeHasValidSample = intakeHasValidSample;
    }

    //*********************************************************************************************
    //          Communication from Extension Arm
    //*********************************************************************************************
    private boolean extensionArmPositionReached = false;

    public void setExtensionArmPositionReached(boolean positionReached) {
        this.extensionArmPositionReached = positionReached;
    }

    private boolean extensionArmResetComplete = false;

    public void setExtensionArmResetComplete(boolean resetComplete) {
        this.extensionArmResetComplete = resetComplete;
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
        extensionArm.setDataLog(logFile);
        intake.setDataLog(logFile);
        intakeArmServo.setDataLog(logFile);
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
        extensionArm.enableDataLogging();
        intake.enableDataLogging();
        intakeArmServo.enableDataLogging();
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
        extensionArm.disableDataLogging();
        intake.disableDataLogging();
        intakeArmServo.disableDataLogging();
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
        telemetry.addData("EAIC State = ", state.toString());
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    //*********************************************************************************************
    //          State machine
    //*********************************************************************************************

    @Override
    public void update() {
        extensionArm.update();
        intake.update();
        logState();

        switch (state) {

            // setup for init - run before arriving for match
            case EXTENSION_ARM_RESETTING_FOR_INIT_SETUP:
                if (extensionArmResetComplete) {
                    extensionArm.bucketClearancePosition();
                    // tell the intake / bucket controller that the extension / intake is NOT at the
                    // clearance position
                    controller.setIntakePositionedForBucketClearance(false);
                    state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_INIT_SETUP;
                }
                break;
            case EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_INIT_SETUP:
                // have the extension arm and intake arm reached the bucket clearance positions?
                if (extensionArmPositionReached && intakeArmServo.isPositionReached()) {
                    // yes! Tell the intake / bucket controller that the bucket clearance position
                    // has been reached so that it can start the bucket rotation
                    controller.setIntakePositionedForBucketClearance(true);
                    state = ExtensionArmIntakeBucketControllerState.WAIT_FOR_BUCKET_MOVING_TO_INIT_POSITION;
                }
                break;
            case WAIT_FOR_BUCKET_MOVING_TO_INIT_POSITION:
                // wait for the bucket to init position. The controller will tell us when to proceed
                // by calling completeInit()
                break;
            // setup for init continues because the intake / bucket controller said to finish
            // it up
            case INTAKE_ARM_MOVING_TO_INIT_POSITION:
                if (intakeArmServo.isPositionReached()) {
                    extensionArm.initPosition();
                    state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_INIT_POSITION;
                }
                break;
            case EXTENSION_ARM_MOVING_TO_INIT_POSITION:
                if (extensionArmPositionReached) {
                    // robot is now setup for init, pre match
                    controller.setIntakeReadyForInit(true);
                    state = ExtensionArmIntakeBucketControllerState.READY_FOR_INIT;
                }
                break;
            case READY_FOR_INIT:
                // driver may leave robot turned on or may turn it off. We are waiting for some to
                // tell us to init the robot. Since we are all setup for it that will be quick.
                break;

            // init states
            case EXTENSION_ARM_RESETTING_FOR_INIT:
                // The intake arm was commanded to go to the init position too. But we are assuming
                // it is already close to that position and so will take very little time to get
                // there. So we are not going to check if it got there. That would just waste time.
                if (extensionArmResetComplete) {
                    initComplete = true;
                    // tell the intake / bucket controller that the extension arm reset is complete
                    controller.setExtensionArmResetComplete(true);
                    state = ExtensionArmIntakeBucketControllerState.INIT_COMPLETE;
                }
                break;
            case INIT_COMPLETE:
                // wait for get ready to run command
                break;

            // get ready to run states
            case EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE_FOR_GET_READY_TO_RUN:
                if (extensionArmPositionReached && intakeArmServo.isPositionReached()) {
                    // tell the intake / bucket controller that the bucket clearance position is
                    // reached. The bucket can be moved now.
                    controller.setIntakePositionedForBucketClearance(true);
                    state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_BUCKET_TO_MOVE_TO_TRANSFER_POSITION;
                }
                break;
            case WAITING_FOR_BUCKET_TO_MOVE_TO_TRANSFER_POSITION:
                // just waiting here until the intake bucket controller tells use we can proceed. It
                // will tell us to go ahead once the bucket is moved into the transfer position.
                break;
                //
            case EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION_FOR_GET_READY_TO_RUN:
                if (extensionArmPositionReached && intakeArmServo.isPositionReached()) {
                    //extension arm and intake arm are at transfer position. We are assuming that
                    // the bucket is too and that it has a sample in it. This is just like after
                    // a sample has been transferred so why not jump to that state?
                    // Tell the intake / bucket controller that the intake has been positioned for
                    // a transfer, and that a transfer was completed (faking it out).
                    controller.setIntakePositionedForTransfer(true);
                    setIntakeTransferComplete(true);
                    state = ExtensionArmIntakeBucketControllerState.TRANSFER_COMPLETE;
                }
                break;

                // bucket clearance states
            case EXTENSION_ARM_MOVING_TO_BUCKET_CLEARANCE:
                if (extensionArmPositionReached && intakeArmServo.isPositionReached()) {
                    // tell the intake / bucket controller that the bucket clearance position is
                    // reached. The bucket can be moved now.
                    controller.setIntakePositionedForBucketClearance(true);
                    state = ExtensionArmIntakeBucketControllerState.AT_BUCKET_CLEARANCE_POSITION;
                }
                break;
            case AT_BUCKET_CLEARANCE_POSITION:
                // just hanging out here waiting for the intake / bucket controller to tell us
                // to do something
                break;

                // setup for intake states
            case EXTENSION_ARM_MOVING_TO_INTAKE_POSITION:
                if (extensionArmPositionReached) {
                    // tell the intake / bucket controller that the intake position is
                    // reached.
                    controller.setIntakePositionReached(true);
                    state = ExtensionArmIntakeBucketControllerState.AT_INTAKE_POSITION;
                }
                break;
            case AT_INTAKE_POSITION:
                // hang out and wait for the intake / bucket controller to tell us to do something.
                // Most likely it will be to intake.
                break;

                // intake states
            case WAITING_FOR_A_GOOD_SAMPLE:
                // the intake is smart. It is going to filter through the samples until it has a
                // good one and then let us know.
                if (intakeHasValidSample) {
                    controller.setIntakeHasValidSample(true);
                    setupForTransfer();
                }
                break;
            case INTAKE_STOPPED:
                // intake is stopped. Wait for a new command.
                break;

                // setting up for transfer states
            case INTAKE_ARM_MOVING_TO_TRANSFER_POSITION:
                if (intakeArmServo.isPositionReached()) {
                    extensionArm.transferPosition();
                    state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION;
                }
                break;
            case EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION:
                if (extensionArmPositionReached) {
                    // all the movements are finished. Tell the intake bucket controller that the
                    // intake is ready for a transfer.
                    controller.setIntakePositionedForTransfer(true);
                    state = ExtensionArmIntakeBucketControllerState.AT_TRANSFER_POSITION;
                }
                break;
            case AT_TRANSFER_POSITION:
                // waiting for a command from the intake / bucket controller. Most likely it will be
                // to transfer.
                // It is possible to get to this state after an outtake. When that happens there is
                // no sample in the intake, we are just waiting for the driver to decide what to do.
                break;

                // transfer states
            case TRANSFERRING_SAMPLE:
                if (intakeTransferComplete) {
                    // transfer was successful. Tell the intake / bucket controller
                    controller.setIntakeTransferComplete(true);
                    state = ExtensionArmIntakeBucketControllerState.TRANSFER_COMPLETE;
                }
                if (intakesRequestsAnOuttake) {
                    // there is a jam that the intake could not clear. Extend the arm and run an
                    // outtake to try to clear the jam. The intake does not drop to the floor to
                    // save time so hopefully that is not a problem.
                    setupForOuttake();
                }
                break;
            case MOVING_TO_OUTTAKE_POSITION:
                if (extensionArmPositionReached && intakeArmServo.isPositionReached()) {
                    intake.outtake();
                    state = ExtensionArmIntakeBucketControllerState.OUTTAKING_SAMPLE_AFTER_TRANSFER_ATTEMPT;
                }
                break;
            case OUTTAKING_SAMPLE_AFTER_TRANSFER_ATTEMPT:
                if (outtakeComplete) {
                    // we don't have a sample anymore
                    controller.setIntakeHasValidSample(false);
                    // move the intake and extension arm back to the transfer position to prepare
                    // for the driver's next move
                    setupForTransfer();
                }
                break;

            case TRANSFER_COMPLETE:
                break;

            case OUTTAKING_SAMPLE:
                // we get to this state when the driver asks for an outtake
                // the only way out is for the driver to stop the intake
                break;

            case IDLE:
                // just hang out and wait for a command
                break;
        }

    }
}


