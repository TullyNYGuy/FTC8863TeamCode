package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ColorDetectorHSV;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ColorInHSV;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ColorSensorUpdatable;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class ITDIntakeSweeperVertical implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum IntakeState {
        IDLE,
        INTAKING,
        OUTTAKING,
        OUTTAKING_UNTIL_STOP_REQUESTED,
        HAVE_SAMPLE,
        PULL_SAMPLE_IN_A_LITTLE_MORE,
        WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION,
        EJECTING,
        DEJAMMING_EJECTION,
        DEJAMMING_TRANSFER,
        WAITING_FOR_MOVE_TO_OUTTAKING,
        TRANSFERRING
    }

    private IntakeState intakeState;

    private enum IntakeCommand {
        INTAKE,
        OUTAKE,
        TRANSFER,
        STOP,
        EJECT,
        DEJAM,
        RESET_STOP,

        NO_COMMAND
    }

    private IntakeCommand intakeCommand;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private CRServo intakeSweeperServoLeft;
    private CRServo intakeSweeperServoRight;
    private ITDExtensionArmIntakeController controller;

    public void setController(ITDExtensionArmIntakeController controller) {
        this.controller = controller;
    }

    private ElapsedTime timer;
    private ColorSensorUpdatable intakeColorSensor;
    private ColorDetectorHSV intakeColorDetector;
    private double delayTime;

    private DataLogging logFile;
    private boolean loggingOn = false;
    private DataLogOnChange logDataOnchange;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommentOnChange;

    private boolean initComplete = false;
    private final String INTAKE_NAME = "Intake";

    // define the colors the intake is looking for
    // f here means float instead of double type. HSV are float type.
    private ColorInHSV red = new ColorInHSV(Color.RED,
            0, 60,
            0.2f, 0.4f,
            0.07f, 0.09f);
    private ColorInHSV yellow = new ColorInHSV(Color.YELLOW,
            60, 120,
            0.5f, 0.65f,
            .13f, .16f);

    private ColorInHSV blue = new ColorInHSV(Color.BLUE,
            180, 240,
            0.54f, 0.66f,
            0.08f, 0.2f);

    private ColorInHSV[] possibleColors = new ColorInHSV[]{red, yellow, blue};

    private Color sampleColor = Color.UNKNOWN;

    /**
     * Returns the sample color. The sample color is updated once per update by reading the color sensor
     * HSV values and passing them to the colorDetector.
     *
     * @return
     */
    public Color getSampleColor() {
        return sampleColor;
    }

    private Color allianceColor;

    public void setAllianceColor(Color allianceColor) {
        this.allianceColor = allianceColor;
    }

    private Color getAllianceColor() {
        return allianceColor;
    }

    /**
     * the number of times the dejam action has been run
     */
    private int dejamCount = 0;

    private int pullSampleInABitMoreCount = 0;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ITDIntakeSweeperVertical(HardwareMap hardwareMap, Telemetry telemetry) {
        intakeSweeperServoLeft = hardwareMap.get(CRServo.class, "intakeSweeperServoLeft");
        intakeSweeperServoLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeSweeperServoRight = hardwareMap.get(CRServo.class, "intakeSweeperServoRight");
        intakeSweeperServoRight.setDirection(DcMotorSimple.Direction.FORWARD);

        intakeColorSensor = new ColorSensorUpdatable(hardwareMap, telemetry, "intakeColorSensorV3");
        // set up the color detector to look for one of the three possible colors
        intakeColorDetector = new ColorDetectorHSV(possibleColors);

        timer = new ElapsedTime();
        intakeState = IntakeState.IDLE;
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
     * Turn on the color sensor. LED will turn on.
     */
    public void colorSensorOn() {
        intakeColorSensor.turnSensorOn();
    }

    public void ColorSensorOff() {
    }

    public void stop() {
        // only allow this command when the intake is in certain states
        // this prevents button mashing on the gamepad from screwing up the intake operation
        switch (intakeState) {
            case IDLE:
            case INTAKING:
            case OUTTAKING:
            case OUTTAKING_UNTIL_STOP_REQUESTED:
                // allow the command when in the above states
                intakeState = IntakeState.IDLE;
                stopActions();
                break;

            case HAVE_SAMPLE:
            case WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION:
            case EJECTING:
            case DEJAMMING_EJECTION:
            case DEJAMMING_TRANSFER:
            case WAITING_FOR_MOVE_TO_OUTTAKING:
            case TRANSFERRING:
                logCommand("Stop intake command ignored");
                break;
        }
    }

    /**
     * Stop the rotation of the sweeper
     */
    private void stopActions() {
        logCommand("stop intake");
        intakeSweeperServoLeft.setPower(0);
        intakeSweeperServoRight.setPower(0);
        intakeCommand = IntakeCommand.STOP;
    }

    public void intake() {
        // only allow this command when the intake is in certain states
        // this prevents button mashing on the gamepad from screwing up the intake operation
        switch (intakeState) {
            case IDLE:
            case OUTTAKING:
            case OUTTAKING_UNTIL_STOP_REQUESTED:
                // allow the command when in the above states
                intakeActions();
                break;

            case INTAKING:
            case HAVE_SAMPLE:
            case WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION:
            case EJECTING:
            case DEJAMMING_EJECTION:
            case DEJAMMING_TRANSFER:
            case WAITING_FOR_MOVE_TO_OUTTAKING:
            case TRANSFERRING:
                logCommand("Intake command ignored");
                break;
        }
    }

    /**
     * Rotate the sweeper so it intakes
     */
    private void intakeActions() {
        if (allianceColor == null) {
            // uh oh the alliance color was never set. Rather than it being nothing, which will
            // cause the intake to stop when it gets a sample and cannot tell if it is a valid
            // color, default it to something
            allianceColor = Color.BLUE;
            log("alliance color was never set, defaulting to red");
        }
        logCommand("intake");
        // tell the intake / intake arm / extension arm controller we don't have a good sample
        controller.setIntakeHasValidSample(false);
        intakeColorSensor.turnSensorOn();
        // force an update to get fresh distance and color data
        intakeColorSensor.update();
        intakeSweeperServoLeft.setPower(1);
        intakeSweeperServoRight.setPower(1);
        intakeCommand = IntakeCommand.INTAKE;
    }

    public void runIntakeServos() {
        intakeSweeperServoLeft.setPower(1);
        intakeSweeperServoRight.setPower(1);
    }

    public void dejam() {
        // only allow this command when the intake is in certain states
        // this prevents button mashing on the gamepad from screwing up the intake operation
        switch (intakeState) {
            case IDLE:
            case HAVE_SAMPLE: // only for testing the dejam actions
                // allow the command when in the above states
                intakeState = IntakeState.DEJAMMING_EJECTION;
                dejamActions();
                break;

            case INTAKING:
            case OUTTAKING:
            case OUTTAKING_UNTIL_STOP_REQUESTED:
            case WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION:
            case EJECTING:
            case DEJAMMING_EJECTION:
            case DEJAMMING_TRANSFER:
            case WAITING_FOR_MOVE_TO_OUTTAKING:
            case TRANSFERRING:
                logCommand("Dejam command ignored");
                break;
        }

    }

    private void dejamActions() {
        logCommand("dejam");
        intakeSweeperServoLeft.setPower(-1);
        intakeSweeperServoRight.setPower(-1);
        dejamCount++;
        intakeCommand = IntakeCommand.DEJAM;
    }

    public void outtake() {
        // only allow this command when the intake is in certain states
        // this prevents button mashing on the gamepad from screwing up the intake operation
        switch (intakeState) {
            case IDLE:
            case INTAKING:
            case HAVE_SAMPLE: // only for emergencies
                // allow the command when in the above states
                intakeState = IntakeState.OUTTAKING_UNTIL_STOP_REQUESTED;
                outtakeActions();
                break;
            case WAITING_FOR_MOVE_TO_OUTTAKING:
                // when in this state, we are waiting for the movement to an outtake position to
                // complete. It must have completed because someone asked for an outtake.
                // do not change the state, since this state is waiting for the outtake command to
                // proceed with the state machine.
                outtakeActions();
                break;

            case OUTTAKING:
            case OUTTAKING_UNTIL_STOP_REQUESTED:
            case WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION:
            case EJECTING:
            case DEJAMMING_EJECTION:
            case DEJAMMING_TRANSFER:

            case TRANSFERRING:
                logCommand("Outtake sample command ignored");
                break;
        }
    }

    /**
     * Rotate the sweeper to it outtakes.
     */
    private void outtakeActions() {
        // reset the request for an outtake since the controller has started the process
        controller.setIntakesRequestsAnOuttake(false);
        controller.setOuttakeComplete(false);
        logCommand("outtake sample");
        intakeCommand = IntakeCommand.OUTAKE;
        intakeSweeperServoLeft.setPower(-1);
        intakeSweeperServoRight.setPower(-1);
        timer.reset();
    }

    public void transfer() {
        // only allow this command when the intake is in certain states
        // this prevents button mashing on the gamepad from screwing up the intake operation
        switch (intakeState) {
            case HAVE_SAMPLE: // only for testing
            case WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION:
                // allow the command when in the above states
                intakeState = IntakeState.TRANSFERRING;
                transferActions();
                break;

            case IDLE:
            case INTAKING:
            case OUTTAKING:
            case OUTTAKING_UNTIL_STOP_REQUESTED:
            case EJECTING:
            case DEJAMMING_EJECTION:
            case DEJAMMING_TRANSFER:
            case WAITING_FOR_MOVE_TO_OUTTAKING:
            case TRANSFERRING:
                logCommand("Transfer sample command ignored");
                break;
        }
    }
    /**
     * Rotate the sweeper so it transfers a sample to the bucket on the lift
     */
    private void transferActions() {
        logCommand("transfer sample");
        // tell the extension arm / intake arm / intake controller that a transfer is not complete
        // yet.
        controller.setIntakeTransferComplete(false);
        intakeSweeperServoLeft.setPower(1);
        intakeSweeperServoRight.setPower(1);
        intakeCommand = IntakeCommand.TRANSFER;
        timer.reset();
    }

    /**
     * Ejects a sample out the back of the intake
     */
    public void eject() {
        // only allow this command when the intake is in certain states
        // this prevents button mashing on the gamepad from screwing up the intake operation
        switch (intakeState) {
            case HAVE_SAMPLE: // only for testing
                // allow the command when in the above states
                intakeState = IntakeState.EJECTING;
                ejectActions();
                break;

            case IDLE:
            case INTAKING:
            case OUTTAKING:
            case OUTTAKING_UNTIL_STOP_REQUESTED:
            case WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION:
            case EJECTING:
            case DEJAMMING_EJECTION:
            case DEJAMMING_TRANSFER:
            case WAITING_FOR_MOVE_TO_OUTTAKING:
            case TRANSFERRING:
                logCommand("Eject sample command ignored");
                break;
        }
    }

    /**
     * Ejects a sample out the back of the intake
     */
    private void ejectActions() {
        logCommand("eject sample");
        intakeSweeperServoLeft.setPower(1);
        intakeSweeperServoRight.setPower(1);
        intakeCommand = IntakeCommand.EJECT;
    }

    /**
     * Ejects sample and sets intake to stopped state
     */

    public void reset() {
        intakeState = IntakeState.EJECTING;
        resetActions();
    }

    /**
     * Ejects a sample out the back of the intake
     */
    private void resetActions() {
        logCommand("reset intake");
        intakeSweeperServoLeft.setPower(1);
        intakeSweeperServoRight.setPower(1);
        intakeCommand = IntakeCommand.RESET_STOP;
    }

    //*********************************************************************************************
    //          Color sensor related functions
    //*********************************************************************************************

    public double getDistanceToSample(DistanceUnit distanceUnit) {
        return intakeColorSensor.getDistance(distanceUnit);
    }

    public boolean isSamplePresent() {
        if (getDistanceToSample(DistanceUnit.CM) < 3) {
            return true;
        } else {
            return false;
        }
    }

    public void displayDistanceToSample(Telemetry telemetry) {
        intakeColorSensor.displayColorSensorDistance(telemetry);
    }

    public void displayColorData(Telemetry telemetry) {
        intakeColorSensor.displayColorData(telemetry);
    }

    public void displaySampleColor(Telemetry telemetry) {
        telemetry.addData("Sample color = ", sampleColor.toString());
    }

    //*********************************************************************************************
    //          Housekeeping stuff
    //*********************************************************************************************
    @Override
    public String getName() {
        return INTAKE_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
    }

    @Override
    public boolean init(Configuration config) {
        logCommand("Init starting");
        return true;
    }

    @Override
    public void shutdown() {
        stopActions();
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logDataOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
        logCommentOnChange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    /**
     * Write a string into the logfile.
     *
     * @param stringToLog
     */
    protected void log(String stringToLog) {
        if (logFile != null && loggingOn) {
            logFile.logData(INTAKE_NAME, stringToLog);
        }
    }

    private void logState() {
        if (loggingOn && logFile != null) {
            logStateOnChange.log(getName() + " state = " + intakeState.toString());
        }
    }

    private void logCommand(String command) {
        if (loggingOn && logFile != null) {
            logDataOnchange.log(getName() + " command = " + command);
        }
    }

    private void logComment(String comment) {
        if (loggingOn && logFile != null) {
            logCommentOnChange.log(getName() + comment);
        }
    }

    private void logCommentEveryTime(String comment) {
        if (loggingOn && logFile != null) {
            logFile.logData(comment);
        }
    }

    public void displayState(Telemetry telemetry) {
        telemetry.addData("State = ", intakeState.toString());
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    //*********************************************************************************************
    //          State machine
    //*********************************************************************************************

    @Override
    public void update() {
        intakeColorSensor.update();
        // using the just updated HSV values, determine the color seen by the sample
        sampleColor = intakeColorDetector.getMostLikelyColor(intakeColorSensor.getHsvValues());
        logComment(" Sample color = " + sampleColor.toString());
        //todo comment this out
        //logCommentEveryTime(" Sample color = " + sampleColor.toString());
        logState();

        switch (intakeState) {

            case IDLE:
                if (intakeCommand == IntakeCommand.INTAKE) {
                    intakeActions();
                    intakeState = IntakeState.INTAKING;
                    // clear the command since it is being acted upon
                    intakeCommand = IntakeCommand.NO_COMMAND;
                }
                break;

            case INTAKING:
                if (isSamplePresent()) {
                    stopActions();
                    intakeState = IntakeState.HAVE_SAMPLE;
                }
                break;

            case HAVE_SAMPLE:
                // sometimes the intake pulls a sample in just far enough to trip the distance sensor
                // but does not come in far enough to obtain a good color reading. We have to handle
                // that situation
                if (sampleColor == Color.UNKNOWN) {
                    log("sample color uknown while have sample");
//                    timer.reset();
//                    runIntakeServos();
//                    intakeState = IntakeState.PULL_SAMPLE_IN_A_LITTLE_MORE;
                }
                if (allianceColor == Color.BLUE && sampleColor == Color.RED) {
                    // the sample is not the right color
                    log("have wrong color sample!");
                    ejectActions();;
                    timer.reset();
                    intakeState = IntakeState.EJECTING;
                    timer.reset();
                }
                if (allianceColor == Color.BLUE &&
                        ((sampleColor == Color.YELLOW) || sampleColor == Color.BLUE)) {
                    // the sample is the right color, tell the controller
                    controller.setIntakeHasValidSample(true);
                    log("have a good sample!");
                    intakeState = IntakeState.WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION;
                }
                if (allianceColor == Color.RED && sampleColor == Color.BLUE) {
                    // the sample is not the right color
                    log("have wrong color sample!");
                    ejectActions();;
                    timer.reset();
                    intakeState = IntakeState.EJECTING;
                    timer.reset();
                }
                if (allianceColor == Color.RED &&
                        ((sampleColor == Color.YELLOW) || sampleColor == Color.RED)) {
                    // the sample is the right color, tell the controller
                    controller.setIntakeHasValidSample(true);
                    log("have a good sample!");
                    intakeState = IntakeState.WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION;
                }
                break;

            case PULL_SAMPLE_IN_A_LITTLE_MORE:
                if (timer.milliseconds() > 50) {
                    intakeState = IntakeState.HAVE_SAMPLE;
                }
                break;

            case EJECTING:
                if (timer.milliseconds() > 500 && isSamplePresent()) {
                    // the eject failed because the sample is still in the intake
                    if (dejamCount < 2) {
                        // allow up to 2 cycles of dejam
                        dejamActions();
                        intakeState = IntakeState.DEJAMMING_EJECTION;
                        timer.reset();
                    } else {
                        // stop the dejamming if it is not working after a couple of cycles
                        // try outtaking
                        dejamCount = 0;
                        outtakeActions();
                        intakeState = IntakeState.OUTTAKING;
                    }
                }
                if (timer.milliseconds() > 500 && !isSamplePresent()) {
                    // the eject succeeded because the sample is gone
                    // in case the eject is coming after a dejam attempt that succeeded
                    dejamCount = 0;
                    // intake again
                    if (IntakeCommand.RESET_STOP == intakeCommand) {
                        stopActions();
                        intakeState = IntakeState.IDLE;
                        controller.setupForTransfer();
                    }
                    else {
                        intakeActions();
                        intakeState = IntakeState.INTAKING;
                    }
                }
                break;

            case DEJAMMING_EJECTION:
                if (timer.milliseconds() > 125 && isSamplePresent()) {
                    // good the sample stayed in the intake while we ran the sweepers outwards
                    // try the eject again
                    ejectActions();;
                    intakeState = IntakeState.EJECTING;
                    timer.reset();
                }
                if (timer.milliseconds() > 125 && !isSamplePresent()) {
                    // uh oh the sample must have been pushed out the front of the intake
                    // intake again
                    dejamCount = 0;
                    intakeActions();
                    intakeState = IntakeState.INTAKING;
                }
                break;

            // wait while the intake rotates to the bucket and the extension arm retracts
            case WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION:
                if (intakeCommand == IntakeCommand.TRANSFER) {
                    transferActions();
                    intakeState = IntakeState.TRANSFERRING;
                    intakeCommand = IntakeCommand.NO_COMMAND;
                    timer.reset();
                }
                break;

            case TRANSFERRING:
                if (timer.milliseconds() > 1000 && isSamplePresent()) {
                    // the sample is still in the intake after the transfer attempt
                    // better try to unjam it
                    // limit the number of dejam attempts to 5
                    if (dejamCount < 5) {
                        dejamActions();
                        intakeState = IntakeState.DEJAMMING_TRANSFER;
                        timer.reset();
                    } else {
                        // failure after several tries to dejam the intake and transfer
                        // need to outtake this sample but we have to extend the extension arm so the
                        // sample does not drop into the guts of the robot
                        controller.setIntakesRequestsAnOuttake(true);
                        logCommand("intake requests outtake due to jam");
                        intakeState = IntakeState.WAITING_FOR_MOVE_TO_OUTTAKING;
                    }
                }
                if (!isSamplePresent()) {
                    // the transfer was successful
                    stopActions();
                    // since the transfer could have come after a dejam attempt
                    dejamCount = 0;
                    controller.setIntakeTransferComplete(true);
                    logCommand("intake transfer complete");
                    intakeState = IntakeState.IDLE;
                }
                break;

            case DEJAMMING_TRANSFER:
                if (timer.milliseconds() > 125 && isSamplePresent()) {
                    // good! the sample stayed in the intake while we ran the sweepers outwards
                    // try the transfer again
                    transferActions();;
                    intakeState = IntakeState.TRANSFERRING;
                    timer.reset();
                }
                if (timer.milliseconds() > 125 && !isSamplePresent()) {
                    // uh oh the sample must have been pushed out the front of the intake
                    // we are very sad since we probably just dumped the sample into the guts of the
                    // robot
                    stopActions();
                    intakeState = IntakeState.IDLE;
                }
                break;

            case WAITING_FOR_MOVE_TO_OUTTAKING:
                if (intakeCommand == IntakeCommand.OUTAKE) {
                    // outtake actions were already started
                    timer.reset();
                    intakeState = IntakeState.OUTTAKING;
                    intakeCommand = IntakeCommand.NO_COMMAND;
                }
                break;

            case OUTTAKING:
                if (timer.milliseconds() > 1000) {
                    controller.setOuttakeComplete(true);
                    intakeState = IntakeState.IDLE;
                }
                break;

            case OUTTAKING_UNTIL_STOP_REQUESTED:
                // just hanging waiting for a stop command
                break;
        }
    }

}
