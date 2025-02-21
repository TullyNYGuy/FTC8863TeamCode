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
        // intake states
        INTAKING,
        CHECKING_FOR_CONSISTENT_SAMPLE_PRESENT,
        HAVE_SAMPLE_DETERMINING_COLOR,
        WAITING_FOR_GOOD_COLOR,
        PULLING_SAMPLE_IN_A_LITTLE_MORE,
        DEJAMMING_INTAKE,
        ABORTING_INTAKE_CYCLE,
        DOUBLE_CHECK_READY_TO_TRANSFER,
        MOVING_SAMPLE_TO_TRANSFER_POSITION,

        WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION,
        OUTTAKING,
        OUTTAKING_UNTIL_STOP_REQUESTED,

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
        DEJAM_DURING_INTAKE,
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
    private ColorSensorUpdatable intakeColorSensorFront;
    private ColorSensorUpdatable intakeColorSensorRear;
    private double delayTime;

    private DataLogging logFile;
    private boolean loggingOn = false;
    private DataLogOnChange logDataOnchange;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logComment1OnChange;
    private DataLogOnChange logComment2OnChange;

    private boolean initComplete = false;
    private final String INTAKE_NAME = "Intake";

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
    private ColorInHSV red;
    private ColorInHSV yellow;
    private ColorInHSV blue;
    private ColorInHSV[] possibleColorsDetectorFront;
    private ColorInHSV[] possibleColorsDetectorRear;

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
    private int numberOfTimesSampleSeen = 0;
    private int numberOfTimesTriedToDetermineColor = 0;
    private double timeLimit = 0;
    private boolean pullInToGetColorDetection = false;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ITDIntakeSweeperVertical(HardwareMap hardwareMap, Telemetry telemetry, int sensorNumber) {
        intakeSweeperServoLeft = hardwareMap.get(CRServo.class, "intakeSweeperServoLeft");
        intakeSweeperServoLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeSweeperServoRight = hardwareMap.get(CRServo.class, "intakeSweeperServoRight");
        intakeSweeperServoRight.setDirection(DcMotorSimple.Direction.FORWARD);

        intakeColorSensorFront = new ColorSensorUpdatable(hardwareMap, telemetry, "intakeColorSensorFrontV3", getPossibleColorsDetectorFront());
        intakeColorSensorRear= new ColorSensorUpdatable(hardwareMap, telemetry, "intakeColorSensorRearV3", getPossibleColorsDetectorRear());

        if (allianceColor == null) {
            // uh oh the alliance color was never set. Rather than it being nothing, which will
            // cause the intake to stop when it gets a sample and cannot tell if it is a valid
            // color, default it to something
            allianceColor = Color.RED;
            log("alliance color was never set, defaulting to red");
        }

        timer = new ElapsedTime();
        intakeState = IntakeState.IDLE;
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private ColorInHSV[] getPossibleColorsDetectorFront() {
        // define the colors the intake is looking for
        // f here means float instead of double type. HSV are float type.
        red = new ColorInHSV(Color.RED,
                0, 60,
                0.2f, 0.4f,
                0.07f, 0.09f);
        yellow = new ColorInHSV(Color.YELLOW,
                60, 120,
                0.5f, 0.65f,
                .13f, .16f);

        blue = new ColorInHSV(Color.BLUE,
                180, 240,
                0.54f, 0.66f,
                0.08f, 0.2f);
        // Defined a list of the possible colors and their limits
        possibleColorsDetectorFront = new ColorInHSV[]{red, yellow, blue};
        return possibleColorsDetectorFront;
    }

    private ColorInHSV[] getPossibleColorsDetectorRear() {

            // color sensor is the second one, the one in the new intake
            // define the colors the intake is looking for
            // f here means float instead of double type. HSV are float type.
            red = new ColorInHSV(Color.RED,
                    120, 160,
                    0.1f, 0.4f,
                    0.05f, 0.09f);
            yellow = new ColorInHSV(Color.YELLOW,
                    60, 120,
                    0.4f, 0.54f,
                    .11f, .16f);

            blue = new ColorInHSV(Color.BLUE,
                    190, 240,
                    0.54f, 0.66f,
                    0.08f, 0.2f);
        // Defined a list of the possible colors and their limits
        possibleColorsDetectorRear = new ColorInHSV[]{red, yellow, blue};
        return possibleColorsDetectorRear;
    }

    public void setIntakeSweeperSpeed(double speed) {
        intakeSweeperServoLeft.setPower(speed);
        intakeSweeperServoRight.setPower(speed);
    }

    private void resetCounters() {
        numberOfTimesSampleSeen = 0;
        numberOfTimesTriedToDetermineColor = 0;
        dejamCount = 0;
    }

    /**
     * This method gets fresh color data from the color sensor. Communication with the sensor takes
     * time. So only call this once in each update. Don't combine it with getFreshDistanceFromSensor().
     */
    private void getFreshColorFromFrontSensor() {
        intakeColorSensorFront.updateDataColor();
        // using the just updated HSV values, determine the color seen by the sample
        sampleColor = intakeColorSensorFront.getMostLikelyColor();;
        logComment2(" Sample color = " + sampleColor.toString());
    }

    private void getFreshDistanceFromFrontSensor() {
        intakeColorSensorFront.updateDataDistanceAndColor();
    }

    private void getFreshDistanceAndColorFromFrontSensor() {
        intakeColorSensorFront.updateDataDistanceAndColor();
    }

    private void getFreshColorFromRearSensor() {
        intakeColorSensorRear.updateDataColor();
        // using the just updated HSV values, determine the color seen by the sample
        sampleColor = intakeColorSensorFront.getMostLikelyColor();;
        logComment2(" Sample color = " + sampleColor.toString());
    }

    private void getFreshDistanceFromRearSensor() {
        intakeColorSensorRear.updateDataDistanceAndColor();
    }


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
        intakeColorSensorFront.turnSensorOn();
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

            case HAVE_SAMPLE_DETERMINING_COLOR:
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
        setIntakeSweeperSpeed(0);
        intakeCommand = IntakeCommand.STOP;
    }

    public void intake() {
        // only allow this command when the intake is in certain states
        // this prevents button mashing on the gamepad from screwing up the intake operation
        switch (intakeState) {
            case IDLE:
            case OUTTAKING:
            case OUTTAKING_UNTIL_STOP_REQUESTED:
            case ABORTING_INTAKE_CYCLE:
                // allow the command when in the above states
                intakeActions();
                intakeState = IntakeState.INTAKING;
                break;

            case INTAKING:
            case HAVE_SAMPLE_DETERMINING_COLOR:
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
        logCommand("intake");
        // tell the intake / intake arm / extension arm controller we don't have a good sample
        // and we have not seen a sample yet
        controller.setIntakeHasValidSample(false);
        controller.setIntakeHasSeenSample(false);
        dejamCount = 0;
        intakeColorSensorFront.turnSensorOn();
        // force an update to get fresh distance and color data
        getFreshDistanceAndColorFromFrontSensor();
        setIntakeSweeperSpeed(1.0);
    }

    public void runIntakeServos() {
        intakeSweeperServoLeft.setPower(1);
        intakeSweeperServoRight.setPower(1);
    }

    public void dejamDuringIntake() {
        logCommand("Dejam during intake");
        // each time the intake is dejammed, increase the amount of time the sweepers
        // run backward
        switch (dejamCount) {
            case 0:
                timeLimit = 100;
                break;
            case 1:
                timeLimit = 150;
                break;
            case 2:
                timeLimit = 200;
                break;
            case 3:
                timeLimit = 250;
                break;
        }
        dejamActions(-.2);
        timer.reset();
        intakeState = IntakeState.DEJAMMING_INTAKE;
    }

    public void dejam() {
        // only allow this command when the intake is in certain states
        // this prevents button mashing on the gamepad from screwing up the intake operation
        switch (intakeState) {
            case IDLE:
            case HAVE_SAMPLE_DETERMINING_COLOR: // only for testing the dejam actions
                // allow the command when in the above states
                intakeState = IntakeState.DEJAMMING_EJECTION;
                dejamActions(-1);
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

    private void dejamActions(double speed) {
        setIntakeSweeperSpeed(speed);
        timer.reset();
        dejamCount++;
        intakeCommand = IntakeCommand.DEJAM;
    }

    public void outtake() {
        // only allow this command when the intake is in certain states
        // this prevents button mashing on the gamepad from screwing up the intake operation
        switch (intakeState) {
            case IDLE:
            case INTAKING:
            case HAVE_SAMPLE_DETERMINING_COLOR: // only for emergencies
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
            case HAVE_SAMPLE_DETERMINING_COLOR: // only for testing
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
            case HAVE_SAMPLE_DETERMINING_COLOR: // only for testing
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

    public double getDistanceToSampleFront(DistanceUnit distanceUnit) {
        return intakeColorSensorFront.getDistance(distanceUnit);
    }

    public boolean isSamplePresentFront() {
        if (getDistanceToSampleFront(DistanceUnit.CM) < 3) {
            return true;
        } else {
            return false;
        }
    }

    public double getDistanceToSampleRear(DistanceUnit distanceUnit) {
        return intakeColorSensorRear.getDistance(distanceUnit);
    }

    public boolean isSamplePresentRear() {
        if (getDistanceToSampleRear(DistanceUnit.CM) < 3) {
            return true;
        } else {
            return false;
        }
    }

    public void displayDistanceToSample(Telemetry telemetry) {
        intakeColorSensorFront.displayColorSensorDistance(telemetry);
        intakeColorSensorRear.displayColorSensorDistance(telemetry);
    }

    public void displayColorData(Telemetry telemetry) {
        getFreshColorFromFrontSensor();
        intakeColorSensorFront.displayColorData(telemetry);
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
        logComment1OnChange = new DataLogOnChange(logFile);
        logComment2OnChange = new DataLogOnChange(logFile);
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

    private void logComment1(String comment) {
        if (loggingOn && logFile != null) {
            logComment1OnChange.log(getName() + comment);
        }
    }

    private void logComment2(String comment) {
        if (loggingOn && logFile != null) {
            logComment2OnChange.log(getName() + comment);
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
        logState();

        switch (intakeState) {

            case IDLE:
                // wait for a command
                break;

                // intake states
            case INTAKING:
                // refresh the distance from the sample
                getFreshDistanceFromFrontSensor();
                if (isSamplePresentFront()) {
                    log("Sample detected");
                    stopActions();
                    // tell the extension arm intake controller we have seen a sample
                    controller.setIntakeHasSeenSample(true);
                    timer.reset();
                    intakeState = IntakeState.CHECKING_FOR_CONSISTENT_SAMPLE_PRESENT;
                }
                break;
            case CHECKING_FOR_CONSISTENT_SAMPLE_PRESENT:
                // refresh the distance from rear sensor
                getFreshDistanceFromRearSensor();
                // if the sample is seen at the rear sensor then it is completely in the intake
                if (isSamplePresentRear()) {
                    log("Sample present at rear");
                    intakeState = IntakeState.HAVE_SAMPLE_DETERMINING_COLOR;
                } else {
                    log("Sample not seen at rear, start pull in");
                    // start a timer that limits how long a pull in can run
                    timer.reset();
                    // run the sweepers but at a reduced speed
                    setIntakeSweeperSpeed(.5);
                    intakeState = IntakeState.PULLING_SAMPLE_IN_A_LITTLE_MORE;
                }
                break;

                // intake states when things go wrong
            case PULLING_SAMPLE_IN_A_LITTLE_MORE:
                // update the data from the rear sensor
                getFreshDistanceFromRearSensor();
                // If the sample is now preset at the rear then the sample is fully in the intake
                if (isSamplePresentRear()) {
                    // stop the intake
                    stopActions();
                    // since the sample is in the intake find out what color it is
                    intakeState = IntakeState.HAVE_SAMPLE_DETERMINING_COLOR;
                } else {
                    // no sample seen at the rear sensor yet
                    // If this attempt to pull in the sample has run too long without seeing a sample
                    // then perhaps the intake has jammed.
                    if (timer.milliseconds() > 250) {
                        dejamDuringIntake();
                    }
                }
                break;
            case DEJAMMING_INTAKE:
                logComment1("Intake dejam attempt " + dejamCount + " for mS = " + timeLimit);
                // if the dejam has not succeeded after a number of tries, it is time to abort this
                // intake cycle
                if (dejamCount >= 5) {
                        timer.reset();
                        // clear the intake by running it backwards
                        setIntakeSweeperSpeed(-1);
                        intakeState = IntakeState.ABORTING_INTAKE_CYCLE;
                }
                // the sweepers are running backwards for a short amount of time. Once that time
                // has passed then we try to intake again.
                if (timer.milliseconds() > timeLimit) {
                    intake();
                }
                break;
            case ABORTING_INTAKE_CYCLE:
                // Once the sweepers have run for long enough to clear out any jam, we start a new
                // intake cycle.
                if (timer.milliseconds() > 250) {
                    resetCounters();
                    intake();
                }
                break;

                // we only get to this state when a sample is in the intake so that the sample is seen
                // by the front and rear sensor
            case HAVE_SAMPLE_DETERMINING_COLOR:
                // get fresh color info from the sensor
                getFreshColorFromFrontSensor();

                // sometimes the intake pulls a sample in just far enough to trip the distance sensor
                // but does not come in far enough to obtain a good color reading. Or it takes a bit of
                // time for the sensor to give us a good color. We have to handle
                // this situation
                if (sampleColor == Color.UNKNOWN) {
                    log("sample color uknown while have sample");
                    numberOfTimesTriedToDetermineColor++;
                    // if the color is still uknown after 4 attempts we need to do something
                    //todo this should be changed. We know the sample is seen by the rear sensor
                    // and are assuming it is seen by the front sensor, but maybe not
                    if (numberOfTimesTriedToDetermineColor > 3) {
                        log("could not determine color of sample 4 times");
                        timer.reset();
                        // reset the counter since we are about to restart the intake sequence
                        numberOfTimesTriedToDetermineColor = 0;
                        // run the sweepers but at a reduced speed
                        setIntakeSweeperSpeed(.5);
                        // start a timer to limit how long a pull in runs
                        timer.reset();
                        // tell the pull in we are looking for a color, not a sample to be present
                        pullInToGetColorDetection = true;
                        intakeState = IntakeState.PULLING_SAMPLE_IN_A_LITTLE_MORE;
                    }
                }
                if (allianceColor == Color.BLUE && sampleColor == Color.RED) {
                    // the sample is not the right color
                    log("have wrong color sample! " + sampleColor.toString());
                    resetCounters();
                    ejectActions();;
                    timer.reset();
                    intakeState = IntakeState.EJECTING;
                    timer.reset();
                }
                if (allianceColor == Color.BLUE &&
                        ((sampleColor == Color.YELLOW) || sampleColor == Color.BLUE)) {
                    // the sample is the right color, tell the controller
                    controller.setIntakeHasValidSample(true);
                    resetCounters();
                    log("have a good sample! " + sampleColor.toString());
                    timer.reset();
                    intakeState = IntakeState.WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION;
                }
                if (allianceColor == Color.RED && sampleColor == Color.BLUE) {
                    // the sample is not the right color
                    log("have wrong color sample! " + sampleColor.toString());
                    resetCounters();
                    ejectActions();;
                    timer.reset();
                    intakeState = IntakeState.EJECTING;
                    timer.reset();
                }
                if (allianceColor == Color.RED &&
                        ((sampleColor == Color.YELLOW) || sampleColor == Color.RED)) {
                    // the sample is the right color, tell the controller
                    controller.setIntakeHasValidSample(true);
                    resetCounters();
                    log("have a good sample! " + sampleColor.toString());
                    timer.reset();
                    intakeState = IntakeState.WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION;
                }
                break;
//            case DOUBLE_CHECK_READY_TO_TRANSFER:
//                // delay for a little bit to make sure sample has settled
//                if (timer.milliseconds() > 500) {
//                    intakeState = IntakeState.MOVING_SAMPLE_TO_TRANSFER_POSITION;
//                }
//                break;
//            case MOVING_SAMPLE_TO_TRANSFER_POSITION:
//                getFreshColorFromFrontSensor();
//                if (isSamplePresentFront() && sampleColor != Color.UNKNOWN) {
//                    stopActions();
//                    intakeState = IntakeState.WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION;
//                } else {
//                    setIntakeSweeperSpeed(.2);
//                }
//                break;
            // wait while the intake rotates to the bucket and the extension arm retracts
            case WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION:
                if (intakeCommand == IntakeCommand.TRANSFER) {
                    transferActions();
                    intakeState = IntakeState.TRANSFERRING;
                    intakeCommand = IntakeCommand.NO_COMMAND;
                    timer.reset();
                }
                break;

                // ejecting states
            case EJECTING:
                // refresh the distance from the color sample
                intakeColorSensorFront.updateDataDistance();
                if (timer.milliseconds() > 500 && isSamplePresentFront()) {
                    // the eject failed because the sample is still in the intake
                    if (dejamCount < 2) {
                        // allow up to 2 cycles of dejam
                        dejamActions(-1);
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
                if (!isSamplePresentFront()) {
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
                // refresh the distance from the color sample
                intakeColorSensorFront.updateDataDistance();
                if (timer.milliseconds() > 125 && isSamplePresentFront()) {
                    // good the sample stayed in the intake while we ran the sweepers outwards
                    // try the eject again
                    ejectActions();
                    intakeState = IntakeState.EJECTING;
                    timer.reset();
                }
                if (timer.milliseconds() > 125 && !isSamplePresentFront()) {
                    // uh oh the sample must have been pushed out the front of the intake
                    // intake again
                    dejamCount = 0;
                    intakeActions();
                    intakeState = IntakeState.INTAKING;
                }
                break;

                // transfer states
            case TRANSFERRING:
                // refresh the distance from the color sample
                intakeColorSensorFront.updateDataDistance();
                if (timer.milliseconds() > 1000 && isSamplePresentFront()) {
                    // the sample is still in the intake after the transfer attempt
                    // better try to unjam it
                    // limit the number of dejam attempts to 5
                    if (dejamCount < 5) {
                        dejamActions(-1);
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
                if (!isSamplePresentFront()) {
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
                // refresh the distance from the color sample
                intakeColorSensorFront.updateDataDistance();
                if (timer.milliseconds() > 125 && isSamplePresentFront()) {
                    // good! the sample stayed in the intake while we ran the sweepers outwards
                    // try the transfer again
                    transferActions();;
                    intakeState = IntakeState.TRANSFERRING;
                    timer.reset();
                }
                if (timer.milliseconds() > 125 && !isSamplePresentFront()) {
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
