package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;

public class DecodeSorterController implements FTCRobotSubsystem {

    // private static final Logger log = LoggerFactory.getLogger(decodeSorterController.class);

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum Commands {
        SHOOT_ONE,
        SHOOT_TWO,
        SHOOT_THREE,
        NO_COMMAND,
        PREPARE_TO_INTAKE,
        INTAKE,
        INTAKE_OFF;
    }

    private Commands currentCommand = Commands.NO_COMMAND;

    private enum RampDownStates {
        WAITING_FOR_RAMP_DOWN,
        WAITING_FOR_MOTOR_TO_MOVE;
    }

    private RampDownStates rampDownState;
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private DecodeColorSensorController colorSensorController;
    private DecodeSorterMotor sorterMotor;
    private DecodeRampServo rampServo;
    private DecodeIntakeMotor intakeMotor;
    private boolean commandComplete = true;
    private DecodeRGBIndicator indicator;
    private SorterState stateAfterRampIsUp;
    private DataLogOnChange logCommandOnchange;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommentOnChange;
    private double motorPositionAfterRampDown;
    private SorterState sorterStateAfterRampDown;
    private boolean rampDownDone;
    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods For Implementing FTCRobotSubsystem
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    /**
     * Property that holds a log file
     */
    private DataLogging logFile;

    @Override
    public void setDataLog(DataLogging logFile) {
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
        logCommentOnChange = new DataLogOnChange(logFile);
        sorterMotor.setDataLog(logFile);
        intakeMotor.setDataLog(logFile);
        rampServo.setDataLog(logFile);
        colorSensorController.setDataLog(logFile);
        this.logFile = logFile;
    }

    /**
     * Property that holds whether data is being logged into the log file.
     */
    private boolean loggingOn = false;

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
        sorterMotor.enableDataLogging();
        intakeMotor.enableDataLogging();
        rampServo.enableDataLogging();
        colorSensorController.enableDataLogging();
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
        sorterMotor.disableDataLogging();
        intakeMotor.disableDataLogging();
        rampServo.disableDataLogging();
        colorSensorController.disableDataLogging();
    }

    private final String SUB_SYSTEM_NAME = DecodeRobot.HardwareName.SORTER_CONTROLLER.hwName;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public DecodeSorterController(HardwareMap hardwareMap, Telemetry telemetry,
                                  DecodeColorSensorController decodeColorSensorController,
                                  DecodeSorterMotor sorterMotor,
                                  DecodeRampServo decodeRampServo,
                                  DecodeIntakeMotor intakeMotor,
                                  DecodeRGBIndicator indicator) {
        this.colorSensorController = decodeColorSensorController;
        this.sorterMotor = sorterMotor;
        this.rampServo = decodeRampServo;
        this.intakeMotor = intakeMotor;
        this.indicator = indicator;

        // allow commands to be honored
        commandComplete = true;
        // initial command is no command
        currentCommand = Commands.NO_COMMAND;
        // initial state is intake
        currentState = SorterState.EMPTY_EMPTY_EMPTY;

        // set the defaults for the indicator light
        indicator.setFrequency(3);
        // black is off
        indicator.setColor(DecodeRGBIndicator.IndicatorColor.BLACK);
        // blinking means that the turntable is not locked onto the april tag. The turntable controller will
        // set this and control it but we default it here
        indicator.setMode(DecodeRGBIndicator.Mode.SOLID);
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private void logState() {
        if (loggingOn && logFile != null) {
            logStateOnChange.log(getName() + " state = " + currentState.toString());
        }
    }

    private void logCommand() {
        if (loggingOn && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + currentCommand);
        }
    }

    private void logComment(String comment) {
        if (loggingOn && logFile != null) {
            logCommentOnChange.log(getName() + " " + comment);
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    //*********************************************************************************************
    //          Public status
    //*********************************************************************************************

    public void displayState(Telemetry telemetry) {
        telemetry.addData("Sorter controller state = ", currentState.toString());
    }

    public void displayCommand(Telemetry telemetry) {
        telemetry.addData("Sorter controller command = ", currentCommand.toString());
    }

    public void displayCommandComplete(Telemetry telemetry) {
        telemetry.addData("Sorter controller command complete = ", Boolean.toString(commandComplete));
    }

    public void displaySensorStatus(Telemetry telemetry) {
        telemetry.addData("Sorter controller sensors on = ", Boolean.toString(colorSensorController.isSensorOn()));
    }

    //*********************************************************************************************
    //          Public commands
    //*********************************************************************************************
    public void shootOne() {
        // do not honor a new command unless the previous one is complete
        if (commandComplete && (
                // allow a shoot command even when the sorter does not have 3 artifacts in it
                currentState == SorterState.ARTIFACT_EMPTY_EMPTY ||
                currentState == SorterState.EMPTY_ARTIFACT_EMPTY ||
                currentState == SorterState.ARTIFACT_ARTIFACT_EMPTY ||
                currentState == SorterState.ARTIFACT_ARTIFACT_ARTIFACT ||
                currentState == SorterState.EMPTY_ARTIFACT_ARTIFACT)) {
            currentCommand = Commands.SHOOT_ONE;
            logCommand();
            commandComplete = false;
        } else {
            logComment("shoot one command rejected");
        }
    }

    public void shootTwo() {
        // do not honor a new command unless the previous one is complete
        if (commandComplete && (
                // allow a shoot command even when the sorter does not have 3 artifacts in it
                currentState == SorterState.ARTIFACT_EMPTY_EMPTY ||
                currentState == SorterState.EMPTY_ARTIFACT_EMPTY ||
                currentState == SorterState.EMPTY_ARTIFACT_ARTIFACT ||
                currentState == SorterState.ARTIFACT_ARTIFACT_EMPTY ||
                currentState == SorterState.ARTIFACT_ARTIFACT_ARTIFACT)) {
            currentCommand = Commands.SHOOT_TWO;
            logCommand();
            commandComplete = false;
        } else {
            logComment("shoot 2 command rejected");
        }
    }

    public void shootThree() {
        // do not honor a new command unless the previous one is complete
        if (commandComplete && (
                // allow a shoot command even when the sorter does not have 3 artifacts in it
                currentState == SorterState.ARTIFACT_EMPTY_EMPTY ||
                currentState == SorterState.EMPTY_ARTIFACT_EMPTY ||
                currentState == SorterState.EMPTY_ARTIFACT_ARTIFACT ||
                currentState == SorterState.ARTIFACT_ARTIFACT_EMPTY ||
                currentState == SorterState.ARTIFACT_ARTIFACT_ARTIFACT)) {
            currentCommand = Commands.SHOOT_THREE;
            logCommand();
            commandComplete = false;
        } else {
            logComment("shoot 3 command rejected");
        }
    }

    public void intake() {
        // do not honor a new command unless the previous one is complete
        if (commandComplete) {
            currentCommand = Commands.PREPARE_TO_INTAKE;
            logCommand();
            commandComplete = false;
        } else {
            logComment("intake command rejected");
        }
    }

    public void intakeOff() {
        // do not honor a new command unless the previous one is complete
        if (commandComplete) {
            currentCommand = Commands.INTAKE_OFF;
            logCommand();
            commandComplete = false;
        } else {
            logComment("intake off command rejected");
        }
    }

    public boolean isPreloadFinished() {
        if (currentState == SorterState.ARTIFACT_ARTIFACT_ARTIFACT) {
            return true;
        } else {
            return false;
        }
    }

    //*********************************************************************************************
    //          METHODS needed to implement FTCRobotSubsystem
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    //*********************************************************************************************
    //          State machine
    //*********************************************************************************************
    public enum SorterState {
        EMPTY_EMPTY_EMPTY,
        ARTIFACT_EMPTY_EMPTY,
        ARTIFACT_ARTIFACT_EMPTY,
        EMPTY_ARTIFACT_EMPTY,
        ARTIFACT_ARTIFACT_ARTIFACT,
        PREPARING_TO_SHOOT,
        PREPARING_TO_INTAKE,
        // unused or intermediate states - here for tracking sorter slot status
        EMPTY_ARTIFACT_ARTIFACT;
    }

    private SorterState currentState = SorterState.EMPTY_EMPTY_EMPTY;

    public void setCurrentState(SorterState currentState) {
        this.currentState = currentState;
    }

    public SorterState getCurrentState() {
        return currentState;
    }

    /**
     * sorterSlotStatus is used to track where the artifacts are. It is used for debug and displayed
     * on the driver station
     */
    private SorterState sorterSlotStatus = currentState;

    public SorterState getSorterSlotStatus() {
        return sorterSlotStatus;
    }

    public void displaySorterSlotStatus(Telemetry telemetry) {
        telemetry.addData("Sorter Slots = ", sorterSlotStatus.toString());
    }

    @Override
    public void update() {
        intakeMotor.update();
        sorterMotor.update();
        colorSensorController.update();
        // The ramp servo is updated when isPositionReached is called.

        logState();
        logCommand();

        switch (currentState) {

            // 0 artifacts in the sorter
            // This state occurs after shooting all available artifacts
            case EMPTY_EMPTY_EMPTY:
                // red for no artifacts in the sorter
                indicator.setColor(DecodeRGBIndicator.IndicatorColor.RED);
                sorterSlotStatus = currentState;
                switch (currentCommand) {
                    case PREPARE_TO_INTAKE:
                        // since this state already has an open slot, we can intake from this state and don't need to rotate the sorter wheel
                        // We will come back to this state after preparing for the intake
                        prepareToIntake(0, SorterState.EMPTY_EMPTY_EMPTY);
                        currentState = SorterState.PREPARING_TO_INTAKE;
                        indicator.setColor(DecodeRGBIndicator.IndicatorColor.RED);
                        break;
                    // since this state already has an open slot, we can intake from this state
                    case INTAKE:
                        if (colorSensorController.isArtifactPresent()) {
                            currentState = SorterState.ARTIFACT_EMPTY_EMPTY;
                            // since the slot now has an artifact, we will have to prepare to intake when we reach the next state
                            currentCommand = Commands.PREPARE_TO_INTAKE;
                            logComment("Artifact #1 in sorter - NOW ARTIFACT_EMPTY_EMPTY");
                        }
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                    case SHOOT_TWO:
                    case SHOOT_THREE:
                        logComment("shoot 1/2/3 command rejected - no artifacts");
                        //These commands are not valid continue intaking
                        currentCommand = Commands.NO_COMMAND;
                        commandComplete = true;
                        break;
                    case INTAKE_OFF:
                        intakeMotor.off();
                        commandComplete = true;
                        currentCommand = Commands.NO_COMMAND;
                        break;
                }

                break;

            case PREPARING_TO_SHOOT:
                // run the state machine needed to prepare to shoot.
                // That state machine will change us to the next state when it is complete.
                updatePrepareToShoot();
                break;

            case PREPARING_TO_INTAKE:
                // run the state machine needed to prepare to intake.
                // That state machine will change us to the next state when it is complete.
                updatePrepareToIntake();
                break;

//            case ZERO_CHECKING_FOR_ARTIFACT_PRIOR_TO_ARTIFACT_EMPTY_EMPTY:
//                sorterSlotStatus = SorterState.EMPTY_EMPTY_EMPTY;
//                switch (currentCommand) {
//                    case INTAKE:
//                        if (colorSensorController.isArtifactPresent()) {
//                            currentState = SorterState.ARTIFACT_EMPTY_EMPTY;
//                            logComment("Artifact #1 in sorter - NOW ARTIFACT_EMPTY_EMPTY");
//                        }
//                        break;
//                    case NO_COMMAND:
//                        break;
//                    case SHOOT_ONE:
//                    case SHOOT_TWO:
//                    case SHOOT_THREE:
//                        logComment("shoot 1/2/3 command rejected - no artifacts");
//                        //These commands are not valid continue intaking
//                        currentCommand = Commands.INTAKE;
//                        break;
//                    case INTAKE_OFF:
//                        intakeMotor.off();
//                        commandComplete = true;
//                        currentCommand = Commands.NO_COMMAND;
//                        break;
//                }
//                break;

            // 1 artifact in sorter
            // This state occurs after intaking the 1st artifact
            // This state occurs after shooting 1 artifact from ARTIFACT_ARTIFACT_EMPTY
            // This state occurs after shooting 1 artifact from EMPTY_ARTIFACT_ARTIFACT
            case ARTIFACT_EMPTY_EMPTY:
                // orange for 1 artifact in the sorter
                indicator.setColor(DecodeRGBIndicator.IndicatorColor.ORANGE);
                sorterSlotStatus = currentState;
                switch (currentCommand) {
                    case PREPARE_TO_INTAKE:
                        prepareToIntake(120, SorterState.EMPTY_ARTIFACT_EMPTY);
                        currentState = SorterState.PREPARING_TO_INTAKE;
                        break;
                    case INTAKE:
                        // we cannot intake in this state, there is no open slot
                        currentCommand = Commands.NO_COMMAND;
                        commandComplete = true;
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                    case SHOOT_TWO:
                    case SHOOT_THREE:
                        // 1 artifact in the sorter, can only shoot 1
                        currentCommand = Commands.SHOOT_ONE;
                        logComment("Shooting 1 artifact");
                        // 360 to reduce the chances of jamming
                        prepareToShoot(360, SorterState.EMPTY_EMPTY_EMPTY);
                        currentState = SorterState.PREPARING_TO_SHOOT;
                        break;
                    case INTAKE_OFF:
                        intakeMotor.off();
                        commandComplete = true;
                        currentCommand = Commands.NO_COMMAND;
                        break;
                }
                break;

            // 1 artifact in sorter
            // This state occurs after preparing to intake a 2nd artifact
            // This state also occurs after shooting 2 artifacts from a full sorter
            case EMPTY_ARTIFACT_EMPTY:
                // orange for 1 artifact in the sorter
                indicator.setColor(DecodeRGBIndicator.IndicatorColor.ORANGE);
                sorterSlotStatus = currentState;
                switch (currentCommand) {
                    case PREPARE_TO_INTAKE:
                        // since this state already has an open slot, we can intake from this state and don't need to rotate the sorter wheel
                        // We will come back to this state after preparing for the intake
                        prepareToIntake(0, SorterState.EMPTY_ARTIFACT_EMPTY);
                        currentState = SorterState.PREPARING_TO_INTAKE;
                        break;
                    // since this state already has an open slot, we can intake from this state
                    case INTAKE:
                        if (colorSensorController.isArtifactPresent()) {
                            currentState = SorterState.ARTIFACT_ARTIFACT_EMPTY;
                            // since the slot now has an artifact, we will have to prepare to intake when we reach the next state
                            currentCommand = Commands.PREPARE_TO_INTAKE;
                            logComment("Artifact #2 in sorter - NOW ARTIFACT_ARTIFACT_EMPTY");
                        }
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                    case SHOOT_TWO:
                    case SHOOT_THREE:
                        // 1 artifact in the sorter, can only shoot 1
                        currentCommand = Commands.SHOOT_ONE;
                        logComment("Shooting 1 artifact");
                        prepareToShoot(240, SorterState.EMPTY_EMPTY_EMPTY);
                        currentState = SorterState.PREPARING_TO_SHOOT;
                        break;
                    case INTAKE_OFF:
                        intakeMotor.off();
                        commandComplete = true;
                        currentCommand = Commands.NO_COMMAND;
                        break;
                }
                break;

//            // 1 artifact in sorter. Checking for #2
//            case ONE_CHECKING_FOR_ARTIFACT_PRIOR_TO_ARTIFACT_ARTIFACT_EMPTY:
//                sorterSlotStatus = SorterState.EMPTY_ARTIFACT_EMPTY;
//                switch (currentCommand) {
//                    case INTAKE:
//                        if (colorSensorController.isArtifactPresent()) {
//                            currentState = SorterState.ARTIFACT_ARTIFACT_EMPTY;
//                            logComment("Artifact #2 in sorter - NOW ARTIFACT_ARTIFACT_EMPTY");
//                        }
//                        break;
//                    case NO_COMMAND:
//                        break;
//                    case SHOOT_ONE:
//                    case SHOOT_TWO:
//                    case SHOOT_THREE:
//                        // 1 artifact in the sorter, can only shoot 1
//                        currentCommand = Commands.SHOOT_ONE;
//                        logComment("Shooting 1 artifact");
//                        prepareToShoot(240, SorterState.EMPTY_EMPTY_EMPTY);
//                        currentState = SorterState.PREPARING_TO_SHOOT;
//                        break;
//                    case INTAKE_OFF:
//                        intakeMotor.off();
//                        commandComplete = true;
//                        currentCommand = Commands.NO_COMMAND;
//                        break;
//                }
//                break;

            // 2 artifacts in the sorter
            // This state occurs after intaking the 2nd artifact
            case ARTIFACT_ARTIFACT_EMPTY:
                // yellow for 2 artifacts in the sorter
                indicator.setColor(DecodeRGBIndicator.IndicatorColor.BLUE);
                sorterSlotStatus = currentState;
                switch (currentCommand) {
                    case PREPARE_TO_INTAKE:
                        prepareToIntake(120, SorterState.EMPTY_ARTIFACT_ARTIFACT);
                        currentState = SorterState.PREPARING_TO_INTAKE;
                        break;
                    case INTAKE:
                        // we cannot intake in this state, there is no open slot
                        currentCommand = Commands.NO_COMMAND;
                        commandComplete = true;
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                        logComment("Shooting 1 artifact");
                        prepareToShoot(120, SorterState.ARTIFACT_EMPTY_EMPTY);
                        currentState = SorterState.PREPARING_TO_SHOOT;
                        break;
                    case SHOOT_TWO:
                    case SHOOT_THREE:
                        // 2 artifacts in the sorter, can only shoot 2
                        currentCommand = Commands.SHOOT_TWO;
                        logComment("Shooting 2 artifacts");
                        prepareToShoot(240, SorterState.EMPTY_EMPTY_EMPTY);
                        currentState = SorterState.PREPARING_TO_SHOOT;
                        break;
                    case INTAKE_OFF:
                        intakeMotor.off();
                        commandComplete = true;
                        currentCommand = Commands.NO_COMMAND;
                        break;
                }
                break;

            // 2 artifacts in sorter.
            // This state occurs after preparing to intake a 3rd artifact
            case EMPTY_ARTIFACT_ARTIFACT:
                // yellow for 2 artifacts in the sorter
                indicator.setColor(DecodeRGBIndicator.IndicatorColor.BLUE);
                sorterSlotStatus = SorterState.EMPTY_ARTIFACT_ARTIFACT;
                switch (currentCommand) {
                    case PREPARE_TO_INTAKE:
                        // since this state already has an open slot, we can intake from this state and don't need to rotate the sorter wheel
                        // We will come back to this state after preparing for the intake
                        prepareToIntake(0, SorterState.EMPTY_ARTIFACT_ARTIFACT);
                        currentState = SorterState.PREPARING_TO_INTAKE;
                        break;
                    // since this state already has an open slot, we can intake from this state
                    case INTAKE:
                        if (colorSensorController.isArtifactPresent()) {
                            // The sorter is full. Turn off the intake motor
                            intakeMotor.off();
                            // Intake cycle is finished
                            commandComplete = true;
                            currentCommand = Commands.NO_COMMAND;
                            currentState = SorterState.ARTIFACT_ARTIFACT_ARTIFACT;
                            //TODO add a few degrees to the sorter position so that the ramp does not jam on the ball when it comes up.
                            // The ball it jams on is position 3 (artifact_artifact_artifact last one)
                            logComment("Artifact #3 in sorter");
                        }
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                        logComment("Shooting 1 artifact");
                        prepareToShoot(120, SorterState.ARTIFACT_EMPTY_EMPTY);
                        currentState = SorterState.PREPARING_TO_SHOOT;
                        break;
                    case SHOOT_TWO:
                    case SHOOT_THREE:
                        // 2 artifacts in the sorter, can only shoot 2
                        currentCommand = Commands.SHOOT_TWO;
                        logComment("Shooting 2 artifacts");
                        prepareToShoot(360, SorterState.EMPTY_EMPTY_EMPTY);
                        currentState = SorterState.PREPARING_TO_SHOOT;
                        break;
                    case INTAKE_OFF:
                        intakeMotor.off();
                        commandComplete = true;
                        currentCommand = Commands.NO_COMMAND;
                        break;
                }
                break;

            // 3 artifacts in sorter
            // This state occurs after intaking a 3rd artifact
            case ARTIFACT_ARTIFACT_ARTIFACT:
                // green for 3 artifacts in the sorter
                indicator.setColor(DecodeRGBIndicator.IndicatorColor.GREEN);
                sorterSlotStatus = currentState;
                switch (currentCommand) {
                    // The sorter is full. We cannot intake.
                    case PREPARE_TO_INTAKE:
                    case INTAKE:
                        intakeMotor.off();
                        currentCommand = Commands.NO_COMMAND;
                        commandComplete = true;
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                        logComment("Shooting 1 artifact");
                        // shoot 1 artifact
                        prepareToShoot(120, SorterState.ARTIFACT_ARTIFACT_EMPTY);
                        currentState = SorterState.PREPARING_TO_SHOOT;
                        break;
                    case SHOOT_TWO:
                        logComment("Shooting 2 artifacts");
                        // shoot 2 artifacts
                        prepareToShoot(240, SorterState.EMPTY_ARTIFACT_EMPTY);
                        currentState = SorterState.PREPARING_TO_SHOOT;
                        break;
                    case SHOOT_THREE:
                        logComment("Shooting 3 artifacts");
                        // shoot 3 artifacts
                        prepareToShoot(360, SorterState.EMPTY_EMPTY_EMPTY);
                        currentState = SorterState.PREPARING_TO_SHOOT;
                        break;
                    case INTAKE_OFF:
                        commandComplete = true;
                        intakeMotor.off();
                        currentCommand = Commands.NO_COMMAND;
                        break;
                }
                break;
        }
    }

//*********************************************************************************************
// State machine for moving ramp down, intake on and move sorter - prepping for an intake
//*********************************************************************************************

    /**
     * List of possible states
     */
    private enum RampDownIntakeOnMoveSorterStates {
        WAITING_FOR_RAMP_DOWN,
        WAITING_FOR_MOTOR_TO_MOVE;

    }

    /**
     * Variable to hold the current state
     */
    private RampDownIntakeOnMoveSorterStates rampDownIntakeOnMoveSorterState;
    /**
     * Variable to indicate if the state machine is complete
     */
    private boolean rampDownIntakeOnMoveSorterComplete;
    /**
     * Amount of angle to add to the current sorter position in order to move it to the
     * next position. Note that the "current" position is the last commanded position,
     * not the actual position of the sorter. For example, the last commanded position of
     * the soter might be 120 degrees, but the actual position might be 123 degrees. The next
     * position will be 120 + 120 = 240, rather than 123 + 120 = 243. The motor object keeps
     * track of this so we don't have to worry about it here.
     */
    private double rampDownIntakeOnMoveSorterAngleAddition;
    /**
     * After this state machine completes, it sets the next state for the big sorter controller
     * so it can go one with intaking or shooting. This variable holds the sorter controller
     * state to jump to after this state machine completes.
     */
    private SorterState stateAfterRampDownIntakeOnMoveSorter;

    /**
     * Setup this little state machine that moves the ramp down, turns the intake on and moves
     * the sorter before an intake starts.
     *
     * @param positionAddition
     * @param nextState
     * @return
     */
    private boolean prepareToIntake(double positionAddition, SorterState nextState) {
        // set the completion flag to false, since we are just starting this state machine
        rampDownIntakeOnMoveSorterComplete = false;
        // save the position to add so we can use it later
        rampDownIntakeOnMoveSorterAngleAddition = positionAddition;
        // save the next sorter controller state so we can jump to it later
        stateAfterRampDownIntakeOnMoveSorter = nextState;
        // turn the color sensors off so that the moving sorter wheel is not mistaken for an artifact
        colorSensorController.colorSensorsOff();
        // start the ramp / elevator moving down. It will take some time to complete the movement.
        rampServo.downPosition();
        // turn on the intake motor. If a ball gets sucked it, it will hang out until an open slot
        // in the sorter wheel rotates to the front of the robot. Then it will enter the slot.
        intakeMotor.intake();
        // set the first state for this state machine, waiting for the ramp to finish moving
        rampDownIntakeOnMoveSorterState = RampDownIntakeOnMoveSorterStates.WAITING_FOR_RAMP_DOWN;
        return true;
    }

    /**
     * Run the state machine for prepare to intake
     * This method will get called repeatedly by the sorter controller state machine and will run
     * over and over until it completes and sets the next state for the sorter controller to jump to.
     */
    private void updatePrepareToIntake() {
        // check the state
        switch (rampDownIntakeOnMoveSorterState) {
            // wait for the ramp / elevator movement to complete
            case WAITING_FOR_RAMP_DOWN:
                if (rampServo.isPositionReached()) {
                    // ramp movement is complete. Start the sorter wheel movement.
                    sorterMotor.moveByPosition(rampDownIntakeOnMoveSorterAngleAddition);
                    // set the next state so that we can wait for the sorter wheel to complete its movement
                    rampDownIntakeOnMoveSorterState = RampDownIntakeOnMoveSorterStates.WAITING_FOR_MOTOR_TO_MOVE;
                }
                break;
            // wait for the sorter wheel to complete its movement
            case WAITING_FOR_MOTOR_TO_MOVE:
                if (sorterMotor.isMovementComplete()) {
                    // The sorter wheel movement has just completed.
                    // change the state for the sorter controller state machine.
                    // This will stop calling this state machine because the sorter controller state
                    // will jump to a new state.
                    currentState = stateAfterRampDownIntakeOnMoveSorter;
                    // turn the color sensors on so that an artifact can be detected in the intake
                    colorSensorController.colorSensorsOn();
                    // set the flag to indicate that this state machine has completed its work
                    rampDownIntakeOnMoveSorterComplete = true;
                    // now that we are prepared to intake, do it
                    currentCommand = Commands.INTAKE;
                }
                break;
        }
    }
//*********************************************************************************************
// State machine for moving ramp up, intake off and move sorter - prepping for shooting
//*********************************************************************************************

    /**
     * List of possible states
     */
    private enum RampUpIntakeOffMoveSorterStates {
        WAITING_FOR_RAMP_UP,
        WAITING_FOR_MOTOR_TO_MOVE;

    }

    /**
     * Variable to hold the current state
     */
    private RampUpIntakeOffMoveSorterStates rampUpIntakeOffMoveSorterState;
    /**
     * Variable to indicate if the state machine is complete
     */
    private boolean rampUpIntakeOffMoveSorterComplete;
    /**
     * Amount of angle to add to the current sorter position in order to move it to the
     * next position. Note that the "current" position is the last commanded position,
     * not the actual position of the sorter. For example, the last commanded position of
     * the sorter might be 120 degrees, but the actual position might be 123 degrees. The next
     * position will be 120 + 120 = 240, rather than 123 + 120 = 243. The motor object keeps
     * track of this so we don't have to worry about it here.
     */
    private double rampUpIntakeOffMoveSorterAngleAddition;
    /**
     * After this state machine completes, it sets the next state for the big sorter controller
     * so it can go one with intaking or shooting. This variable holds the sorter controller
     * state to jump to after this state machine completes.
     */
    private SorterState stateAfterRampUpIntakeOffMoveSorter;

    /**
     * Setup this little state machine that moves the ramp up , turns the intake off and moves
     * the sorter before shooting starts.
     *
     * @param positionAdditionToShoot
     * @param nextState
     * @return
     */
    private boolean prepareToShoot(double positionAdditionToShoot, SorterState nextState) {
        // set the completion flag to false, since we are just starting this state machine
        rampUpIntakeOffMoveSorterComplete = false;
        // save the position to add so we can use it later
        rampUpIntakeOffMoveSorterAngleAddition = positionAdditionToShoot;
        // save the next sorter controller state so we can jump to it later
        stateAfterRampUpIntakeOffMoveSorter = nextState;
        // turn the color sensors off so that the moving sorter wheel is not mistaken for an artifact
        colorSensorController.colorSensorsOff();
        // start the ramp / elevator moving up. It will take some time to complete the movement.
        rampServo.upPosition();
        // turn off the intake motor.
        intakeMotor.off();
        // set the first state for this state machine, waiting for the ramp to finish moving
        rampUpIntakeOffMoveSorterState = RampUpIntakeOffMoveSorterStates.WAITING_FOR_RAMP_UP;
        return true;
    }

    /**
     * Run the state machine for prepare to shoot
     * This method will get called repeatedly by the sorter controller state machine and will run
     * over and over until it completes and sets the next state for the sorter controller to jump to.
     */
    private void updatePrepareToShoot() {
        // check the state
        switch (rampUpIntakeOffMoveSorterState) {
            // wait for the ramp / elevator movement to complete
            case WAITING_FOR_RAMP_UP:
                if (rampServo.isPositionReached()) {
                    // ramp movement is complete. Start the sorter wheel movement.
                    sorterMotor.moveByPosition(rampUpIntakeOffMoveSorterAngleAddition);
                    // set the next state so that we can wait for the sorter wheel to complete its movement
                    rampUpIntakeOffMoveSorterState = RampUpIntakeOffMoveSorterStates.WAITING_FOR_MOTOR_TO_MOVE;
                }
                break;
            // wait for the sorter wheel to complete its movement
            case WAITING_FOR_MOTOR_TO_MOVE:
                if (sorterMotor.isMovementComplete()) {
                    // The sorter wheel movement has just completed.
                    // change the state for the sorter controller state machine.
                    // This will stop calling this state machine because the sorter controller state
                    // will jump to a new state.
                    currentState = stateAfterRampUpIntakeOffMoveSorter;
                    switch (currentCommand) {
                        case SHOOT_ONE:
                            logComment("1 artifact shot - NOW " + currentState.toString());
                            break;
                        case SHOOT_TWO:
                            logComment("2 artifacts shot - NOW " + currentState.toString());
                            break;
                        case SHOOT_THREE:
                            logComment("3 artifacts shot - NOW " + currentState.toString());
                            break;
                    }
                    // The sorter wheel is ready to shoot.
                    if (stateAfterRampUpIntakeOffMoveSorter == SorterState.EMPTY_EMPTY_EMPTY) {
                        logComment("Shot last artifact so auto starting intake");
                        // auto start the intake if there are no artifacts in the sorter
                        currentCommand = Commands.PREPARE_TO_INTAKE;
                    } else {
                        currentCommand = Commands.NO_COMMAND;
                    }
                    commandComplete = true;
                    rampUpIntakeOffMoveSorterComplete = true;
                }
                break;
        }
    }

    @Override
    public String getName() {
        return SUB_SYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
    }

    @Override
    public boolean init(Configuration config) {
        // init all of the objects that make up this subsystem
        sorterMotor.init(config);
        intakeMotor.init(config);
        rampServo.init(config);
        colorSensorController.init(config);

        sorterMotor.resetEncoder();
        colorSensorController.colorSensorsOn();
        return true;
    }
}
