package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;

public class ITDExtensionArmIntakeController implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum IntakeHeight {
        BUCKET_CLEARANCE,
        HIGH,
        LOW,
        REALLY_LOW
    }

    private IntakeHeight intakeHeight = IntakeHeight.HIGH;

    public void setIntakeHeight(IntakeHeight intakeHeight) {
        this.intakeHeight = intakeHeight;
    }

    private enum ExtensionArmIntakeBucketControllerState {
        IDLE,

        // setup for init states (run prior to arriving at match),

        EXTENSION_ARM_RESETTING_FOR_INIT_SETUP,
        INTAKE_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_INIT_SETUP,
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
        INTAKE_MOVING_TO_BUCKET_CLEARANCE_FOR_GET_READY_TO_RUN,
        WAITING_FOR_BUCKET_TO_MOVE_TO_TRANSFER_POSITION,
        EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION_FOR_GET_READY_TO_RUN,
        // TRANSFER_COMPLETE, defined later as part of the transfer states

        // setup for bucket clearance states
        INTAKE_MOVING_TO_BUCKET_CLEARANCE,
        AT_BUCKET_CLEARANCE_POSITION,

        // setup for intake states
        EXTENSION_ARM_MOVING_TO_INTAKE_POSITION,
        AT_INTAKE_POSITION,

        // intake states
        WAITING_FOR_A_GOOD_SAMPLE,
        WAITING_FOR_ROTATION_TO_EJECTION_POSITION,
        WAITING_FOR_EJECTION_TO_COMPLETE,
        INTAKE_STOPPED,

        //setup for transfer states
        INTAKE_ARM_MOVING_TO_TRANSFER_POSITION,
        EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION,
        AT_TRANSFER_POSITION,
        WAITING_FOR_INTAKE_AT_PASSTHROUGH_POSITION,
        WAITING_FOR_EXTENSION_ARM_AFTER_PASSTHROUGH,
        WAITING_FOR_TRANSFER_POSITION_AFTER_PASSTHROUGH,

        // gliding intake states
        WAITING_FOR_SETUP_GLIDING_INTAKE,
        //WAIT_1_SEC,
        READY_FOR_GLIDING_INTAKE,
        WAITING_FOR_GOOD_GLIDING_SAMPLE,
        WAITING_FOR_READY_TO_CYCLE_GLIDE,
        DELAY_AFTER_GLIDING_INTAKE_COMPLETE,

        // transfer states
        TRANSFERRING_SAMPLE,
        MOVING_TO_OUTTAKE_POSITION,
        OUTTAKING_SAMPLE_AFTER_TRANSFER_ATTEMPT,
        TRANSFER_COMPLETE,

        // deliver sample to bin states
        INTAKE_MOVING_TO_BUCKET_CLEARANCE_FOR_DELIVERY,
        LIFT_MOVING_TO_DELIVERY_POSITION,
        BUCKET_ARM_MOVING_TO_DELIVERY_POSITION,
        READY_FOR_DELIVERY,

        // state for command from the driver
        OUTTAKING_SAMPLE,

        RESETTING
    }

    private ExtensionArmIntakeBucketControllerState state;

    public enum TransferMode {
        LONG_SIDE_TRANSFER,
        SHORT_SIDE_TRANSFER

    }
    private TransferMode transferMode = TransferMode.LONG_SIDE_TRANSFER;

    public TransferMode getTransferMode() {
        return transferMode;
    }

    public void setTransferMode(TransferMode transferMode) {
        this.transferMode = transferMode;
    }

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

    public ITDExtensionArm extensionArm;
    public ITDIntakeArmServo intakeArmServo;

    private ITDIntakeBucketController controller;

    public void setIntakeBucketController(ITDIntakeBucketController controller) {
        this.controller = controller;
    }
    private double glidingIntakeDelay=0;

    public double getCurrentPosition(){
        return extensionArm.getCurrentPosition();
    }
    private boolean intakeTypeIsGliding=false;


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
        intake = new ITDIntakeSweeperVertical(hardwareMap, telemetry, 1);
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
    //          Communication from Intake
    //*********************************************************************************************

    /**
     * The intake as seen a sample but may not have it completely in the intake and the color determined.
     * This may not be a good sample (proper color). Sometimes the intake jams because it is so close
     * to the floor that the sample cannot rotate into the intake. Knowing that a sample is on
     * its way into the intake allows the controller to rotate the intake arm up to clear the floow
     * and elminate the source of the jam.
     */
    private boolean intakeHasSeenSample = false;

    public void setIntakeHasSeenSample(boolean intakeHasSeenSample) {
        this.intakeHasSeenSample = intakeHasSeenSample;
    }

    public boolean hasIntakeSeenSample() {
        return intakeHasSeenSample;
    }

    /**
     * The intake needs to eject the sample while intaking. In order to spit it out further, the
     * intake will have to be rotated up a bit more.
     */
    private boolean intakeNeedsToEject = false;

    public void setIntakeNeedsToEject(boolean intakeNeedsToEject) {
        this.intakeNeedsToEject = intakeNeedsToEject;
    }

    private boolean intakeEjectionComplete = false;

    public void setIntakeEjectionComplete(boolean intakeEjectionComplete) {
        this.intakeEjectionComplete = intakeEjectionComplete;
    }

    /**
     * The intake says it has a good, proper color sample.
     */
    private boolean intakeHasValidSample = false;

    public void setIntakeHasValidSample(boolean intakeHasValidSample) {
        this.intakeHasValidSample = intakeHasValidSample;
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
     * Allow the intake to request the controller to move the extension arm out so an outtake can
     * take place while the intake is not over the robot body. This occurs when we have a bad jam
     * that cannot be cleared any other way.
     */
    private boolean intakesRequestsAnOuttake = false;

    public void setIntakesRequestsAnOuttake(boolean needOuttake) {
        this.intakesRequestsAnOuttake = needOuttake;
        logComment("Intake requests outtake");
    }

    /**
     * The intake says that an outtake is complete.
     */
    private boolean outtakeComplete = false;

    public void setOuttakeComplete(boolean outtakeComplete) {
        this.outtakeComplete = outtakeComplete;
    }

    public ITDIntakeBucketController.DeliveryMode getDeliveryMode() {
        return controller.getDeliveryMode();
    }

    //*********************************************************************************************
    //          Communication from Extension Arm
    //*********************************************************************************************
    /**
     * The extension arm says it has reached position
     */
    private boolean extensionArmPositionReached = false;

    public void setExtensionArmPositionReached(boolean positionReached) {
        this.extensionArmPositionReached = positionReached;
    }

    /**
     * The extension arm says that its reset has completed.
     */
    private boolean extensionArmResetComplete = false;

    public void setExtensionArmResetComplete(boolean resetComplete) {
        this.extensionArmResetComplete = resetComplete;
    }

    //*********************************************************************************************
    //          Commands
    //*********************************************************************************************

    public void setupAllianceColor (Color color) {
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
        state = ExtensionArmIntakeBucketControllerState.INTAKE_MOVING_TO_BUCKET_CLEARANCE_FOR_GET_READY_TO_RUN;
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
        intakeArmServo.readyToIntakePosition();
        state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION_FOR_GET_READY_TO_RUN;
    }

    /**
     * The intake / controller wants us to make room for the bucket to move around. To do that we
     * need to rotate the intake down to the bucket clearance position.
     */
    public void setupForBucketClearance() {
        logCommand("Setup for bucket clearance");
        // tell the intake / bucket controller that the bucket clearance position is NOT
        // reached. It has to wait until the bucket clearance position is reached before moving the
        // bucket.
        controller.setIntakePositionedForBucketClearance(false);
        // start the movement of the extension arm and the intake arm.
        // We really only need the intake to rotate down out of the way in order to get
        // clearance to move the bucket. The extension arm is moving so that the motor is
        // not trying to pull the arm against the metal causing it to overheat.
        extensionArm.bucketClearancePosition();
        intakeArmServo.bucketClearancePosition();
        state = ExtensionArmIntakeBucketControllerState.INTAKE_MOVING_TO_BUCKET_CLEARANCE;
    }

    /**
     * The intake / bucket controller wants us to move to the intake position.
     */
    public void setupForIntake() {
        logCommand("Setup for intake");
        // tell the intake bucket controller that the position is not reached yet
        controller.setIntakePositionReached(false);
        // send the extension arm out to the max extension
        extensionArm.intakePosition();
        // rotate the intake to a position that is ready to intake, but not on the floor
        intakeArmServo.readyToIntakePosition();
        // The intake is not lowered to the floor yet. Just for safety. That will happen when we
        // get the transfer command.
        state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_INTAKE_POSITION;

    }
    public void setupForIntake(double extentionArmPosition) {
        logCommand("Setup for intake");
        // tell the intake bucket controller that the position is not reached yet
        controller.setIntakePositionReached(false);
        // send the extension arm out to the max extension
        extensionArm.goToPosition(extentionArmPosition);
        // rotate the intake to a position that is ready to intake, but not on the floor
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
    public void intakeLowAltitude() {
        intakeTypeIsGliding=false;
        logCommand("Intake");
        controller.setIntakeHasValidSample(false);
        // lower the intake to the floor
        intakeArmServo.intakePositionLowAltitude();
        // start intaking. This probably occurs as the intake is rotating towards the floor.
        intake.intake();
        state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_A_GOOD_SAMPLE;
    }

    /**
     * The intake bucket controller wants us to intake. The intake is smart. It is going to filter
     * through the samples until it has a good one and then let us know. If we get a good sample,
     * then the setupForTranfer() will be automatically called.
     */
    public void intakeHighAltitude() {
        intakeTypeIsGliding=false;
        logCommand("Intake");
        controller.setIntakeHasValidSample(false);
        // lower the intake to the floor
        intakeArmServo.intakePositionHighAltitude();
        // start intaking. This probably occurs as the intake is rotating towards the floor.
        intake.intake();
        state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_A_GOOD_SAMPLE;
    }

    public void intakeToFloor() {
        logCommand("Intake to low altitude");
        intakeArmServo.intakePositionLowAltitude();
    }

    /**
     * This method will extend the extension arm and rotate the intake to the floor at the same time.
     * @param extensionArmPosition
     */
    public void setupForGlidingIntake(double extensionArmPosition) {
        controller.setSetupForGlidingIntakeComplete(false);
        logCommand("Setup For Gliding intake");
        // move the extension arm to the desired extension
        extensionArm.goToPosition(extensionArmPosition);
        // at the same time rotate the intake to the floor
        switch (intakeHeight) {
            case BUCKET_CLEARANCE:
                intakeArmServo.bucketClearancePosition();
                break;
            case HIGH:
                intakeArmServo.intakePositionHighAltitude();
                break;
            case LOW:
                intakeArmServo.intakePositionLowAltitude();
                break;
            case REALLY_LOW:
                intakeArmServo.intakePositionReallyLowAltitude();
                break;
        }
        state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_SETUP_GLIDING_INTAKE;
    }

    /**
     * This method is meant to be run when the intake is on the floor. It:
     * - Turns on the intake
     * - Extends the extension arm out to the max position, attempting to intake as it goes.
     * - If it intakes a sample, it then runs setupForTransfer, putting the intake back at the
     *      transfer position. The following are set in the intake bucket controller:
     *      intakeHasValidSample = true
     *      glidingIntakeFailed = false
     *      intakePositionedForTransfer = true (from setupForTransfer)
     *  - If it does not get a sample the intake ends up back at extension arm = 2" and intake rotated
     *      to the readyForIntake position. The following are set in the intake bucket controller:
     *      intakeHasValidSample = false
     *      glidingIntakeFailed = false
     */
    public void runGlidingIntake(double maxPosition, double power,double glidingIntakeDelay) {
        intakeTypeIsGliding=true;
        logCommand("Run gliding intake to " + maxPosition);
        // tell the intake bucket controller we do not have a sample and the gliding intake has not
        // failed yet
        controller.setIntakeHasValidSample(false);
        controller.setGlidingIntakeFailed(false);
        this.glidingIntakeDelay=glidingIntakeDelay;
        switch (intakeHeight) {
            case BUCKET_CLEARANCE:
                intakeArmServo.bucketClearancePosition();
                break;
            case HIGH:
                intakeArmServo.intakePositionHighAltitude();
                break;
            case LOW:
                intakeArmServo.intakePositionLowAltitude();
                break;
            case REALLY_LOW:
                intakeArmServo.intakePositionReallyLowAltitude();
                break;
        }
        //start the intake
        intake.intake();
        // extend the arm looking for a sample
        extensionArm.goToPosition(maxPosition, power);
        state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_GOOD_GLIDING_SAMPLE;
    }
    public void runGlidingIntake(double maxPosition, double power) {
        runGlidingIntake( maxPosition, power,0);
    }

    /**
     * Same as above except that you can specify how far the extension arm extends before stopping.
     * @param maxPosition
     */
    public void runGlidingIntake(double maxPosition) {
        intakeTypeIsGliding=true;
        logCommand("Run gliding intake to " + maxPosition);
        // tell the intake bucket controller we do not have a sample and the gliding intake has not
        // failed yet
        controller.setIntakeHasValidSample(false);
        controller.setGlidingIntakeFailed(false);
        //start the intake
        intake.intake();
        // extend the arm looking for a sample
        extensionArm.goToPosition(maxPosition);
        state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_GOOD_GLIDING_SAMPLE;
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
        // tell the intake / bucket controller that the intake is not ready for a transfer yet
        controller.setIntakePositionedForTransfer(false);
        switch(transferMode) {
            case LONG_SIDE_TRANSFER:
                intakeArmServo.transferPosition();
                // added this so it occurs in parallel
                extensionArm.transferPosition();
                timer.reset();
                state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION;
                break;
            case SHORT_SIDE_TRANSFER:
                intakeArmServo.shortSidePassthroughPosition();
                timer.reset();
                state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_INTAKE_AT_PASSTHROUGH_POSITION;
                break;
        }

    }
//    public void setupIntakeAfterDeliver() {
//        logCommand("Setup Intake After Delivery");
//        controller.setIntakePositionedForTransfer(false);
//        intakeArmServo.readyToIntakePosition();
//        // tell the intake / bucket controller that the intake is not ready for a transfer yet
//        state = ExtensionArmIntakeBucketControllerState.INTAKE_ARM_MOVING_TO_TRANSFER_POSITION;
//    }

    public void cleanupFromFailedGlidingIntake(double distance) {

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
        controller.setLiftBucketSampleIsDelivered(false);
        controller.setLiftBucketAtTransferPosition(false);
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

    public void resetExtensionArm() {
        extensionArm.reset();
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
            logFile.logData(getName() + comment);
        }
    }

    public void displayState(Telemetry telemetry) {
        telemetry.addData("EAIC State = ", state.toString());
    }

    public void displayIntakeState(Telemetry telemetry) {
        intake.displayState(telemetry);
    }

    public void displayExtensionArmPosition(Telemetry telemetry) {
        extensionArm.displayPosition(telemetry);
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
                    state = ExtensionArmIntakeBucketControllerState.INTAKE_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_INIT_SETUP;
                }
                break;
            case INTAKE_MOVING_TO_BUCKET_CLEARANCE_POSITION_FOR_INIT_SETUP:
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
                // The intake servo arm was commanded to go to the init position too. But we are assuming
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
            case INTAKE_MOVING_TO_BUCKET_CLEARANCE_FOR_GET_READY_TO_RUN:
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
                // for transfer do not move the extension arm
                //if (extensionArmPositionReached && intakeArmServo.isPositionReached()) {
                if (intakeArmServo.isPositionReached()) {
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
            case INTAKE_MOVING_TO_BUCKET_CLEARANCE:
                // even thought the extension arm is moving out we are not checking to see if that is complete.
                // We really only need the intake to rotate down out of the way in order to get
                // clearance to move the bucket. The extension arm is moving so that the motor is
                // not trying to pull the arm against the metal causing it to overheat.
                if (intakeArmServo.isPositionReached()) {
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
                // Once the front sensor has seen a sample, rotate the intake up a bit. This is
                // because the intake has a tendency to jam when it is on the floor.
                if (intakeHasSeenSample) {
                    intakeArmServo.readyToIntakePosition();
                } else {
                    // if the intake saw a sample, then did not see it again, lower the intake
                    // back towards the floor so it can continue to intake
                    intakeArmServo.intakePositionHighAltitude();
                }
                // The intake needs to tilt up more to eject the sample farther away and not intake
                // the ejected sample again.
                if (intakeNeedsToEject) {
                    // reset the flag
                    intakeNeedsToEject = false;
                    intakeArmServo.ejectPosition();
                    state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_ROTATION_TO_EJECTION_POSITION;
                }
                // the intake is smart. It is going to filter through the samples until it has a
                // good one and then let us know.
                if (intakeHasValidSample) {
                    controller.setIntakeHasValidSample(true);
                    setupForTransfer();
                }
                break;
            case WAITING_FOR_ROTATION_TO_EJECTION_POSITION:
                if (intakeArmServo.isPositionReached()) {
                    intake.setRotationToEjectPositionComplete(true);
                    state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_EJECTION_TO_COMPLETE;
                }
                break;
            case WAITING_FOR_EJECTION_TO_COMPLETE:
                if (intakeEjectionComplete) {
                    // reset the flag
                    intakeEjectionComplete = false;
                    // the ejection has complete so back to intaking
                    intakeArmServo.intakePositionHighAltitude();
                    if (intakeTypeIsGliding){
                        state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_GOOD_GLIDING_SAMPLE;
                    }else {
                        state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_A_GOOD_SAMPLE;
                    }
                   
                }
                break;

            case INTAKE_STOPPED:
                // intake is stopped. Wait for a new command.
                break;

                // setup for gliding intake states
            case WAITING_FOR_SETUP_GLIDING_INTAKE:
                if (extensionArm.isPositionReached() && intakeArmServo.isPositionReached()) {
                    controller.setSetupForGlidingIntakeComplete(true);
                    state = ExtensionArmIntakeBucketControllerState.READY_FOR_GLIDING_INTAKE;
                }
                break;
            case READY_FOR_GLIDING_INTAKE:
                // wait for a command
                break;

                // gliding intake states
            case WAITING_FOR_GOOD_GLIDING_SAMPLE:
                // The intake picked up a sample while it was extending out.
                //todo Could we call a gliding instake success if the intake has seen the sample,
                // but not called it a valid sample yet? In auto, this would start the movement
                // back to the delivery location and the sample would continue to intake.
                // It also would avoid putting in a longer delay for the gliding intake after the
                // extension has been reached. Intake of the sample can take longer than 250mSec
                // after the end of the extension has been reached.

                //todo implement rotation of the intake when a sample is seen
                //todo implement rotation of the intake when an eject is requested
                if (intakeHasValidSample) {
                    controller.setIntakeHasValidSample(true);
                    controller.setGlidingIntakeFailed(false);
                    setupForTransfer();
                }
                // The intake needs to tilt up more to eject the sample farther away and not intake
                // the ejected sample again.
                if (intakeNeedsToEject) {
                    // reset the flag
                    intakeNeedsToEject = false;
                    intakeArmServo.ejectPosition();
                    state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_ROTATION_TO_EJECTION_POSITION;
                }
                // The arm extended all the way out and did not intake a sample
                if (extensionArm.isPositionReached()) {
                    if(glidingIntakeDelay==0){
                        logComment("Gliding intake failed");
                        controller.setGlidingIntakeFailed(true);
                        intake.stop();
                        if (MatchPhase.getMatchPhase() == MatchPhase.TELEOP) {
                            setupForBucketClearance();
                        } else {
                            // in autonomous we let the autonomous state machine tell us what to do next
                            state = ExtensionArmIntakeBucketControllerState.IDLE;
                        }

//                    intakeArmServo.readyToIntakePosition();
//                    extensionArm.goToPosition(2);
//                    state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_READY_TO_CYCLE_GLIDE;
                    }
                    else {
                        timer.reset();
                        state=ExtensionArmIntakeBucketControllerState.DELAY_AFTER_GLIDING_INTAKE_COMPLETE;
                    }

                }
                break;
            case DELAY_AFTER_GLIDING_INTAKE_COMPLETE:
                if (intakeHasValidSample) {
                    controller.setIntakeHasValidSample(true);
                    controller.setGlidingIntakeFailed(false);
                    setupForTransfer();
                }
                if (timer.milliseconds() >glidingIntakeDelay){
                    logComment("Gliding intake failed");
                    controller.setGlidingIntakeFailed(true);
                    intake.stop();
                    if (MatchPhase.getMatchPhase() == MatchPhase.TELEOP) {
                        setupForBucketClearance();
                    } else {
                        // in autonomous we let the autonomous state machine tell us what to do next
                        state = ExtensionArmIntakeBucketControllerState.IDLE;
                    }
                }
                break;
//            case WAIT_1_SEC:
//                timer.reset();
//                if (timer.milliseconds() == 1000){
//                    state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_GOOD_GLIDING_SAMPLE;
//                }
//                break;

//            case WAITING_FOR_READY_TO_CYCLE_GLIDE:
//                if (intakeArmServo.isPositionReached() && extensionArm.isPositionReached()) {
//                    state = ExtensionArmIntakeBucketControllerState.IDLE;
//                }
//            break;

                // setting up for transfer states
            // This state is no longer called because the extension arm and intake arm servo are moving in parallel
//            case INTAKE_ARM_MOVING_TO_TRANSFER_POSITION:
//                if (intakeArmServo.isPositionReached()) {
//                    extensionArm.transferPosition();
//                    state = ExtensionArmIntakeBucketControllerState.EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION;
//                }
//                break;
            case EXTENSION_ARM_MOVING_TO_TRANSFER_POSITION:
                //todo sometimes the retracting extension arm gets jammed on the bucket so fix that
                // check if extensionArmPosition is not reached within certain time
                // if so, then command the extension arm out to a position that unjams the arm and then
                // return to the retraction to get to a transfer position
                if (intakeArmServo.isPositionReached() && extensionArmPositionReached) {
                    // all the movements are finished. Tell the intake bucket controller that the
                    // intake is ready for a transfer.
                    controller.setIntakePositionedForTransfer(true);
                    state = ExtensionArmIntakeBucketControllerState.AT_TRANSFER_POSITION;
                }
                break;

            case WAITING_FOR_INTAKE_AT_PASSTHROUGH_POSITION:
                if (intakeArmServo.isPositionReached()) {
                    extensionArm.transferPosition();
                    state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_EXTENSION_ARM_AFTER_PASSTHROUGH;
                }
                break;
            case WAITING_FOR_EXTENSION_ARM_AFTER_PASSTHROUGH:
                if (extensionArm.isPositionReached()) {
                    intakeArmServo.transferPosition();
                    state = ExtensionArmIntakeBucketControllerState.WAITING_FOR_TRANSFER_POSITION_AFTER_PASSTHROUGH;
                }
                break;
            case WAITING_FOR_TRANSFER_POSITION_AFTER_PASSTHROUGH:
                if (intakeArmServo.isPositionReached()) {
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
                    // immediately move the intake out of the way to prepare for a delivery
                    // This will also set the next state so we don't need to set it here
                    //todo maybe skip the setup for bucket clearance in auto and setup for the gliding intake
                    //instead. That will clear the bucket and setup for the next movement at the same time.
                    setupForBucketClearance();
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


