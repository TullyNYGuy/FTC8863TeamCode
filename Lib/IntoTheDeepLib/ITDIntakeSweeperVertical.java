package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;
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
        WAITING_FOR_SAMPLE_TO_STOP_MOVING,
        DOUBLE_CHECK_SAMPLE_PRESENT,
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

        WAITING_FOR_ROATATION_TO_EJECT_POSITION,
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
    private ITDIntakeGateServo intakeGateServo;
    private ITDExtensionArmIntakeController controller;

    public void setController(ITDExtensionArmIntakeController controller) {
        this.controller = controller;
    }

    private ElapsedTime timer;
    private ElapsedTime timerForIntakeAfterJam;
    private ITDColorSensorA intakeColorSensorFront;
    private ITDColorSensorB intakeColorSensorRear;
    private double delayTime;

    private DataLogging logFile;
    private boolean loggingOn = false;
    private DataLogOnChange logDataOnchange;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logComment1OnChange;
    private DataLogOnChange logComment2OnChange;

    private boolean initComplete = false;
    private final String INTAKE_NAME = "Intake";

    private Color sampleColorFront = Color.UNKNOWN;
    private Color sampleColorRear = Color.UNKNOWN;

    /**
     * Returns the sample color. The sample color is updated once per update by reading the color sensor
     * HSV values and passing them to the colorDetector.
     *
     * @return
     */
    public Color getSampleColorFront() {
        return sampleColorFront;
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
    private boolean stopQueuedUp = false;

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
        intakeGateServo = new ITDIntakeGateServo(hardwareMap, telemetry);
        intakeColorSensorFront = new ITDColorSensorA(hardwareMap, telemetry, "intakeColorSensorFrontV3");
        intakeColorSensorRear= new ITDColorSensorB(hardwareMap, telemetry, "intakeColorSensorRearV3");

        if (allianceColor == null) {
            // uh oh the alliance color was never set. Rather than it being nothing, which will
            // cause the intake to stop when it gets a sample and cannot tell if it is a valid
            // color, default it to something
            allianceColor = Color.RED;
            log("alliance color was never set, defaulting to red");
        }

        timer = new ElapsedTime();
        timerForIntakeAfterJam = new ElapsedTime();
        intakeState = IntakeState.IDLE;
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    public void setIntakeSweeperSpeed(double speed) {
        log("Sweeper speed set to " + speed);
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
        intakeColorSensorFront.sensor.updateDataColor();
        // using the just updated HSV values, determine the color seen by the sample
        sampleColorFront = intakeColorSensorFront.sensor.getMostLikelyColor();;
        logComment2("Front Sample color = " + sampleColorFront.toString());
    }

    private void getFreshDistanceFromFrontSensor() {
        intakeColorSensorFront.sensor.updateDataDistance();
    }

    private void getFreshDistanceAndColorFromFrontSensor() {
        intakeColorSensorFront.sensor.updateDataDistanceAndColor();
    }

    private void getFreshColorFromRearSensor() {
        intakeColorSensorRear.sensor.updateDataColor();
        // using the just updated HSV values, determine the color seen by the sample
        sampleColorRear = intakeColorSensorFront.sensor.getMostLikelyColor();;
        logComment2("Rear Sample color = " + sampleColorRear.toString());
    }

    private void getFreshDistanceFromRearSensor() {
        intakeColorSensorRear.sensor.updateDataDistance();
    }


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    //*********************************************************************************************
    //          Communication from Extension arm intake controller
    //*********************************************************************************************

    private boolean rotationToEjectPositionComplete = false;

    public void setRotationToEjectPositionComplete(boolean rotationToEjectPositionComplete) {
        this.rotationToEjectPositionComplete = rotationToEjectPositionComplete;
    }

    private boolean roatationAwayFromEjectPositionComplete = false;

    public void setRoatationAwayFromEjectPositionComplete(boolean roatationAwayFromEjectPositionComplete) {
        this.roatationAwayFromEjectPositionComplete = roatationAwayFromEjectPositionComplete;
    }
    //*********************************************************************************************
    //          Commands
    //*********************************************************************************************

    /**
     * Turn on the color sensor. LED will turn on.
     */
    public void colorSensorsOn() {
        intakeColorSensorFront.sensor.turnSensorOn();
        intakeColorSensorRear.sensor.turnSensorOn();
    }

    public void ColorSensorsOff() {
        intakeColorSensorFront.sensor.turnSensorOff();
        intakeColorSensorRear.sensor.turnSensorOff();
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

            case DOUBLE_CHECK_SAMPLE_PRESENT:
            case WAITING_FOR_SAMPLE_TO_STOP_MOVING:
            case CHECKING_FOR_CONSISTENT_SAMPLE_PRESENT:
            case PULLING_SAMPLE_IN_A_LITTLE_MORE:
            case DEJAMMING_INTAKE:
            case ABORTING_INTAKE_CYCLE:
            case HAVE_SAMPLE_DETERMINING_COLOR:
            case EJECTING:
            case DEJAMMING_EJECTION:
                // The states above should not stop immediately, but should stop ofter they are done
                // with their actions. Queue up a stop
                stopQueuedUp = true;

            case WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION:
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
            case DEJAMMING_INTAKE:
            case CHECKING_FOR_CONSISTENT_SAMPLE_PRESENT:
            case HAVE_SAMPLE_DETERMINING_COLOR:
            case EJECTING:
                // allow the command when in the above states
                intakeActions();
                intakeState = IntakeState.INTAKING;
                break;

            default:
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
        colorSensorsOn();
        // force an update to get fresh distance and color data
        getFreshDistanceAndColorFromFrontSensor();
        intakeGateServo.closePosition();
        setIntakeSweeperSpeed(1);
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
                timeLimit = 300;
                dejamActions(-.3);
                break;
            case 1:
                // this will force the termination of dejamming the intake since the limit is 2 times
                // and this will bump the count to 2. It won't actually perform this dejam.
                timeLimit = 300;
                dejamActions(-.3);
                break;
            case 2:
                // this will force the termination of dejamming the intake since the limit is 2 times
                // and this will bump the count to 3. It won't actually perform this dejam.
                dejamActions(-.3);
                break;
        }
        log("Intake dejam attempt " + dejamCount + " for mS = " + timeLimit);
        timer.reset();
        intakeState = IntakeState.DEJAMMING_INTAKE;
    }

    private void abortIntake() {
        timer.reset();
        // clear the intake by running it backwards
        setIntakeSweeperSpeed(-1);
        intakeState = IntakeState.ABORTING_INTAKE_CYCLE;
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
        setIntakeSweeperSpeed(-1);
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
        intakeGateServo.openPosition();
        setIntakeSweeperSpeed(1);
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
        // rotate the intake up more to eject farther away from the intake
        controller.setIntakeNeedsToEject(true);
        intakeState = IntakeState.WAITING_FOR_ROATATION_TO_EJECT_POSITION;
        // actually turning on the sweepers will be done once the intake arm finishes rotating
        //setIntakeSweeperSpeed(-1);
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
        setIntakeSweeperSpeed(1);
        intakeCommand = IntakeCommand.RESET_STOP;
    }

    //*********************************************************************************************
    //          Color sensor related functions
    //*********************************************************************************************

    public double getDistanceToSampleFront(DistanceUnit distanceUnit) {
        return intakeColorSensorFront.sensor.getDistance(distanceUnit);
    }

    public boolean isSamplePresentFront() {
        if (getDistanceToSampleFront(DistanceUnit.CM) < 3.0) {
            return true;
        } else {
            return false;
        }
    }

    public double getDistanceToSampleRear(DistanceUnit distanceUnit) {
        return intakeColorSensorRear.sensor.getDistance(distanceUnit);
    }

    public boolean isSamplePresentRear() {
        if (getDistanceToSampleRear(DistanceUnit.CM) < 3) {
            return true;
        } else {
            return false;
        }
    }

    public void displayDistanceToSample(Telemetry telemetry) {
        getFreshDistanceFromFrontSensor();
        getFreshDistanceFromRearSensor();
        intakeColorSensorFront.sensor.displayColorSensorDistance(telemetry);
        intakeColorSensorRear.sensor.displayColorSensorDistance(telemetry);
    }

    public void logDistanceToSampleFromBothSensors() {
        logDistanceToSampleFromFrontSensor();
        logDistanceToSampleFromRearSensor();
    }

    public void logDistanceToSampleFromFrontSensor() {
        String frontDistance = String.format("%.2f", getDistanceToSampleFront(DistanceUnit.CM));
        log("Front Distance = " + frontDistance);
    }

    public void logDistanceToSampleFromRearSensor() {
        String rearDistance = String.format("%.2f", getDistanceToSampleRear(DistanceUnit.CM));
        log("Rear Distance = " + rearDistance);
    }

    public void writeColorOfSample() {
        log("Front Sample color = " + sampleColorFront.toString());
        log("Rear Sample color = " + sampleColorRear.toString());
    }

    public void displayColorDataFront(Telemetry telemetry) {
        getFreshColorFromFrontSensor();
        intakeColorSensorFront.sensor.displayColorData(telemetry);
    }

    public void displayColorDataRear(Telemetry telemetry) {
        getFreshColorFromRearSensor();
        intakeColorSensorRear.sensor.displayColorData(telemetry);
    }

    public void displaySampleColorFront(Telemetry telemetry) {
        getFreshColorFromFrontSensor();
        telemetry.addData("Sample color = ", sampleColorFront.toString());
    }

    public void displaySampleColorRear(Telemetry telemetry) {
        getFreshColorFromRearSensor();
        telemetry.addData("Sample color = ", sampleColorRear.toString());
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
                    //The detected sample might actually not be a sample. It might be the tubes
                    //clashing and coming near the sensor. So check the presence a second time.
//                    log("Sample detected");
//                    stopActions();
//                    logDistanceToSampleFromFrontSensor();
//                    // tell the extension arm intake controller we have seen a sample. This will
//                    // rotate the intake up from the floor to help make sure the intake does not
//                    // jam
//                    controller.setIntakeHasSeenSample(true);
//                    timer.reset();
                    intakeState = IntakeState.DOUBLE_CHECK_SAMPLE_PRESENT;
                }
                // After a dejam, if the sample is not seen for a certain period of time during
                // the intake then it may be hard jammed on the front of the intake. We will
                // abort the intake to clear it off the front of the intake. NOTE if the sample
                // fell of the intake while running the servos backward to try to clear the jam,
                // then aborting the intake will cause the sweepers to run backwards while the
                // drivers are trying to intake. This is a tradeoff. It will clear a sample
                // hard jammed to the front of the intake, but if it was a drop during dejam
                // the intake will run backwards for a bit while trying to intake.
                if (timerForIntakeAfterJam.milliseconds() > 500 && dejamCount != 0) {
                    log("Intake too long after a dejam. Clear the intake");
                    abortIntake();
                }
                break;

            case DOUBLE_CHECK_SAMPLE_PRESENT:
                getFreshDistanceFromFrontSensor();
                if (isSamplePresentFront()) {
                    // A sample was detected a second time so probably not the tubing. It is likely
                    // a real sample, but not 100% certain.
                    log("Sample detected");
                    stopActions();
                    logDistanceToSampleFromFrontSensor();
                    // tell the extension arm intake controller we have seen a sample. This will
                    // rotate the intake up from the floor to help make sure the intake does not
                    // jam
                    controller.setIntakeHasSeenSample(true);
                    timer.reset();
                    intakeState = IntakeState.WAITING_FOR_SAMPLE_TO_STOP_MOVING;
                }
                else {
                    // The sample that was previously seen was not seen again. This could have been
                    // tubing giving a false reading or it might be a sample that is jammed into the
                    // front of the intake. Either way, ignore the previous detection and go back
                    // to intaking.
                    intakeState = IntakeState.INTAKING;
                }

                // it takes some time for the sample to move between the front of the intake and the
            // rear
            //todo could I move this into the CHECKING_FOR_CONSISTENT_SAMPLE_PRESENT state and short
            // cut the timer if a sample shows up at the rear sensor before the timer expires?
            case WAITING_FOR_SAMPLE_TO_STOP_MOVING:
                if (timer.milliseconds() > 250) {
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
                    logDistanceToSampleFromRearSensor();
                    intakeState = IntakeState.HAVE_SAMPLE_DETERMINING_COLOR;
                } else {
                    // Sample was not seen at the rear sensor. These are the possibilities:
                    //    sample is still seen at the front - pull it in
                    //    sample is not seen at the front - it might be jammed, it might be just barely in the intake
                    //        if this is in the middle of a dejam, don't run the pullin
                    if (dejamCount != 0) {
                        // there is a dejam running. It ran and now it has failed. We don't want to run the
                        // pull in since it takes a lot of time. Just skip to the next dejam cycle.
                        // We might throw the sample on the floor but since dejam has failed once,
                        // it is better to throw it on the floor than to waste time trying to unjam
                        // a hard jam
                        dejamDuringIntake();
                    } else {
                        // do the pullin when there is no dejam running
                        log("Sample not seen at rear, start pull in");
                        logDistanceToSampleFromRearSensor();
                        // start a timer that limits how long a pull in can run
                        timer.reset();
                        // run the sweepers but at a reduced speed
                        setIntakeSweeperSpeed(.2);
                        intakeState = IntakeState.PULLING_SAMPLE_IN_A_LITTLE_MORE;
                    }

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
                    log("Sample present at rear after pullin");
                    logDistanceToSampleFromRearSensor();
                    // reset the dejam counter since the sample is now in the rear of the intake
                    // and not jammed
                    dejamCount = 0;
                    // since the sample is in the intake find out what color it is
                    intakeState = IntakeState.HAVE_SAMPLE_DETERMINING_COLOR;
                } else {
                    // no sample seen at the rear sensor yet
                    // If this attempt to pull in the sample has run too long without seeing a sample
                    // then perhaps the intake has jammed.
                    if (timer.milliseconds() > 500) {
                        log("Sample not seen at rear after pull in - dejam");
                        logDistanceToSampleFromRearSensor();
                        dejamDuringIntake();
                    }
                }
                break;
            case DEJAMMING_INTAKE:
                // if the dejam has not succeeded after a number of tries, it is time to abort this
                // intake cycle
                if (dejamCount > 1) {
                    log("dejam did not succeed - abort intake");
                    abortIntake();
                }
                // the sweepers are running backwards for a short amount of time. Once that time
                // has passed then we try to intake again.
                if (timer.milliseconds() > timeLimit) {
                    // reset the timer. If the sample is not seen for a certain period of time during
                    // the intake then it may be hard jammed on the front of the intake. We will
                    // abort the intake to clear it off the front of the intake. NOTE if the sample
                    // fell of the intake while running the servos backward to try to clear the jam,
                    // then aborting the intake will cause the sweepers to run backwards while the
                    // drivers are trying to intake. This is a tradeoff. It will clear a sample
                    // hard jammed to the front of the intake, but if it was a drop during dejam
                    // the intake will run backwards for a bit while trying to intake.
                    timerForIntakeAfterJam.reset();
                    if (stopQueuedUp) {
                        // clear the stop so it does not trigger a second time
                        stopQueuedUp = false;
                        stopActions();
                        intakeState = IntakeState.IDLE;
                    } else {
                        intake();
                    }
                }
                break;
            case ABORTING_INTAKE_CYCLE:
                // Once the sweepers have run for long enough to clear out any jam, we start a new
                // intake cycle.
                if (timer.milliseconds() > 500) {
                    resetCounters();
                    controller.setIntakeHasSeenSample(false);
                    if (stopQueuedUp) {
                        // clear the stop so it does not trigger a second time
                        stopQueuedUp = false;
                        stopActions();
                        intakeState = IntakeState.IDLE;
                    } else {
                        intake();
                    }
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
                if (sampleColorFront == Color.UNKNOWN) {
                    log("sample color uknown while have sample");
                    numberOfTimesTriedToDetermineColor++;
                    // if the color is still uknown after 4 attempts we need to do something
                    if (numberOfTimesTriedToDetermineColor > 3) {
                        log("could not determine color of sample 4 times");
                        timer.reset();
                        // reset the counter for next time
                        numberOfTimesTriedToDetermineColor = 0;
                        // We tried to get a color from the front sensor but failed 4 times. Maybe
                        // there is no sample in the intake? We could check the distance for the front
                        // sensor and see if one is there. But if it is, what do we do? We can't get a
                        // color from it. So abort. On the other hand maybe somehow the sample is not
                        // in the intake after all. Again, only choice is to abort.
                        // I suppose to save time I could check the front and rear distances to see
                        // if there is a sample. If not, the skip the abort and go right to the intake.
                        getFreshDistanceFromRearSensor();
                        getFreshDistanceFromFrontSensor();
                        logDistanceToSampleFromBothSensors();
                        if (isSamplePresentFront() || isSamplePresentRear()) {
                            // a sample is detected at either front or back or both
                            // clear the sample
                            log("Sample present in intake - aborting");
                            abortIntake();

                        } else {
                            // a sample is detected, clear it
                            log("Sample not present - restarting intake");
                            resetCounters();
                            intake();

                        }
                    }
                }
                if (allianceColor == Color.BLUE && sampleColorFront == Color.RED) {
                    // the sample is not the right color
                    log("have wrong color sample! " + sampleColorFront.toString());
                    resetCounters();;
                    timer.reset();
                    // eject actions will set the next state
                    ejectActions();
                }
                if (allianceColor == Color.BLUE &&
                        ((sampleColorFront == Color.YELLOW) || sampleColorFront == Color.BLUE)) {
                    // the sample is the right color, tell the controller
                    controller.setIntakeHasValidSample(true);
                    resetCounters();
                    log("have a good sample! " + sampleColorFront.toString());
                    timer.reset();
                    intakeState = IntakeState.WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION;
                }
                if (allianceColor == Color.RED && sampleColorFront == Color.BLUE) {
                    // the sample is not the right color
                    log("have wrong color sample! " + sampleColorFront.toString());
                    resetCounters();
                    timer.reset();
                    // eject actions will set the next state
                    ejectActions();
                }
                if (allianceColor == Color.RED &&
                        ((sampleColorFront == Color.YELLOW) || sampleColorFront == Color.RED)) {
                    // the sample is the right color, tell the controller
                    controller.setIntakeHasValidSample(true);
                    resetCounters();
                    log("have a good sample! " + sampleColorFront.toString());
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
//                if (isSamplePresentFront() && sampleColorFront != Color.UNKNOWN) {
//                    stopActions();
//                    intakeState = IntakeState.WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION;
//                } else {
//                    setIntakeSweeperSpeed(.2);
//                }
//                break;
            // wait while the intake rotates to the bucket and the extension arm retracts
            case WAITING_FOR_MOVEMENT_TO_TRANSFER_POSITION:
                // If a stop is queued up, we don't do anything about it since the intake will
                // have stopped by the time it get here. Just clear the queued stop.
                stopQueuedUp = false;
                // it is possible that the sample somehow escapes from the intake. If this happens
                // then the intake thinks it has a sample and is locked up waiting for a transfer
                // that will never happen. So we have to reset it.
                getFreshDistanceFromFrontSensor();
                getFreshDistanceFromRearSensor();
                if (!isSamplePresentFront() && ! isSamplePresentRear()) {
                    // somehow the sample is lost
                    dejamCount = 0;
                    // fake out the controller: tell it there is no sample in the intake
                    controller.setIntakeTransferComplete(true);
                    logCommand("Lost sample while waiting for a transfer :-(");
                    intakeState = IntakeState.IDLE;
                }
                if (intakeCommand == IntakeCommand.TRANSFER) {
                    transferActions();
                    intakeState = IntakeState.TRANSFERRING;
                    intakeCommand = IntakeCommand.NO_COMMAND;
                    timer.reset();
                }
                break;

                // ejecting states
            case WAITING_FOR_ROATATION_TO_EJECT_POSITION:
                if (rotationToEjectPositionComplete) {
                    setIntakeSweeperSpeed(-1);
                    // clear the request to rotate the intake to eject position
                    controller.setIntakeNeedsToEject(false);
                    timer.reset();
                    intakeState = IntakeState.EJECTING;
                }
                break;
            case EJECTING:
                // refresh the distance from the color sample
                getFreshDistanceFromFrontSensor();
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
                    // tell the controller the ejection has finished so the intake will rotate
                    // back down the floor
                    controller.setIntakeEjectionComplete(true);
                    // in case the eject is coming after a dejam attempt that succeeded
                    dejamCount = 0;
                    // intake again
                    if (IntakeCommand.RESET_STOP == intakeCommand) {
                        stopActions();
                        intakeState = IntakeState.IDLE;
                        controller.setupForTransfer();
                    }
                    else {
                        if (stopQueuedUp) {
                            // reset the queued up stop
                            stopQueuedUp = false;
                            // stop the intake
                            stopActions();
                            intakeState = IntakeState.IDLE;
                        } else {
                            intake();
                        }
                    }
                }
                break;

            case DEJAMMING_EJECTION:
                // refresh the distance from the color sample
                getFreshDistanceFromFrontSensor();;
                if (timer.milliseconds() > 125 && isSamplePresentFront()) {
                    // good the sample stayed in the intake while we ran the sweepers outwards
                    // try the eject again
                    timer.reset();
                    // ejectActiions will set the next state
                    ejectActions();
                }
                if (timer.milliseconds() > 125 && !isSamplePresentFront()) {
                    // uh oh the sample must have been pushed out the front of the intake
                    // tell the controller the ejection has finished so the intake will rotate
                    // back down the floor
                    controller.setIntakeEjectionComplete(true);
                    // intake again
                    dejamCount = 0;
                    if (stopQueuedUp) {
                        // reset the queued up stop
                        stopQueuedUp = false;
                        // stop the intake
                        stopActions();
                        intakeState = IntakeState.IDLE;
                    } else {
                        intakeActions();
                        intakeState = IntakeState.INTAKING;
                    }
                }
                break;

                // transfer states
            case TRANSFERRING:
                // refresh the distance from the color sample
                getFreshDistanceFromFrontSensor();;
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
                getFreshDistanceFromFrontSensor();;
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
