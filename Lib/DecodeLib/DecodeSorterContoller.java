package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecodeSorterContoller implements FTCRobotSubsystem {

    private static final Logger log = LoggerFactory.getLogger(DecodeSorterContoller.class);

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
        INTAKE,
        INTAKE_OFF;
    }

    private Commands currentCommand = Commands.NO_COMMAND;

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
    private SorterState stateAfterRampIsUp;
    private DataLogOnChange logCommandOnchange;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommentOnChange;

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
    public DecodeSorterContoller(HardwareMap hardwareMap, Telemetry telemetry,
                                 DecodeColorSensorController decodeColorSensorController,
                                 DecodeSorterMotor sorterMotor,
                                 DecodeRampServo decodeRampServo,
                                 DecodeIntakeMotor intakeMotor) {
        this.colorSensorController = decodeColorSensorController;
        this.sorterMotor = sorterMotor;
        this.rampServo = decodeRampServo;
        this.intakeMotor = intakeMotor;

        // allow commands to be honored
        commandComplete = true;
        // initial command is no command
        currentCommand = Commands.NO_COMMAND;
        // initial state is now set as an empty sorter but this will change for auto and teleop
        //TODO fix initial sorter controller state in auto and teleop
        currentState = SorterState.EMPTY_EMPTY_EMPTY;
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
        if (commandComplete ||
                // allow a shoot command even when the sorter does not have 3 artifacts in it
                currentState == SorterState.ARTIFACT_EMPTY_EMPTY ||
                currentState == SorterState.ARTIFACT_ARTIFACT_EMPTY ||
                currentState == SorterState.EMPTY_ARTIFACT_EMPTY ||
                currentState == SorterState.ARTIFACT_ARTIFACT_ARTIFACT) {
            currentCommand = Commands.SHOOT_ONE;
            logCommand();
            commandComplete = false;
        } else {
            logComment("shoot one command rejected");
        }
    }

    public void shootTwo() {
        // do not honor a new command unless the previous one is complete
        if (commandComplete ||
                // allow a shoot command even when the sorter does not have 3 artifacts in it
                currentState == SorterState.ARTIFACT_EMPTY_EMPTY ||
                currentState == SorterState.ARTIFACT_ARTIFACT_EMPTY ||
                currentState == SorterState.EMPTY_ARTIFACT_EMPTY ||
                currentState == SorterState.ARTIFACT_ARTIFACT_ARTIFACT) {
            currentCommand = Commands.SHOOT_TWO;
            logCommand();
            commandComplete = false;
        } else {
            logComment("shoot 2 command rejected");
        }
    }

    public void shootThree() {
        // do not honor a new command unless the previous one is complete
        if (commandComplete ||
                // allow a shoot command even when the sorter does not have 3 artifacts in it
                currentState == SorterState.ARTIFACT_EMPTY_EMPTY ||
                currentState == SorterState.ARTIFACT_ARTIFACT_EMPTY ||
                currentState == SorterState.EMPTY_ARTIFACT_EMPTY ||
                currentState == SorterState.ARTIFACT_ARTIFACT_ARTIFACT) {
            currentCommand = Commands.SHOOT_THREE;
            logCommand();
            commandComplete = false;
        } else {
            logComment("shoot 3 command rejected");
        }
    }

    public void intake() {
        // do not honor a new command unless the previous one is complete
        // do not honor an intake command if the sorter is full
        if (commandComplete || currentState != SorterState.ARTIFACT_ARTIFACT_ARTIFACT) {
            currentCommand = Commands.INTAKE;
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

    //*********************************************************************************************
    //          METHODS needed to implement FTCRobotSubsystem
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    //*********************************************************************************************
    //          State machine
    //*********************************************************************************************
    private enum SorterState {
        WAITING_FOR_EMPTY_EMPTY_EMPTY,
        // no artifacts in sorter
        EMPTY_EMPTY_EMPTY,
        WAITING_FOR_ARTIFACT_EMPTY_EMPTY,
        // 1 artifact in sorter
        ARTIFACT_EMPTY_EMPTY,
        WAITING_FOR_ARTIFACT_ARTIFACT_EMPTY,
        // 2 artifacts in sorter
        ARTIFACT_ARTIFACT_EMPTY,
        WAITING_FOR_EMPTY_ARTIFACT_EMPTY,
        // 1 artifact in sorter
        EMPTY_ARTIFACT_EMPTY,
        WAITING_FOR_ARTIFACT_ARTIFACT_ARTIFACT,
        // 3 artifacts in sorter
        ARTIFACT_ARTIFACT_ARTIFACT,
        WAITING_FOR_RAMP_DOWN_INTAKE_ON_MOVE_SORTER,
        WAITING_FOR_RAMP_UP_INTAKE_OFF_MOVE_SORTER;
    }

    private SorterState currentState = SorterState.EMPTY_EMPTY_EMPTY;

    @Override
    public void update() {
        intakeMotor.update();
        sorterMotor.update();
        colorSensorController.update();

        logState();
        logCommand();

        switch (currentState) {

            // 0 artifacts in the sorter
            // look for an artifact coming into the sorter
            case EMPTY_EMPTY_EMPTY:
                switch (currentCommand) {
                    case INTAKE:
                        rampDownIntakeOnMoveSorter(0, SorterState.WAITING_FOR_ARTIFACT_EMPTY_EMPTY);
                        currentState = SorterState.WAITING_FOR_RAMP_DOWN_INTAKE_ON_MOVE_SORTER;
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                    case SHOOT_TWO:
                    case SHOOT_THREE:
                        //These commands are not valid continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case INTAKE_OFF:
                        intakeMotor.off();
                        commandComplete = true;
                        currentCommand = Commands.NO_COMMAND;
                        break;
                }
                break;

            case WAITING_FOR_RAMP_DOWN_INTAKE_ON_MOVE_SORTER:
                // just hang out here until the little state machine completes and moves us to the next state
                updateRampDownIntakeOnMoveSorter();
                break;

            case WAITING_FOR_ARTIFACT_EMPTY_EMPTY:
                switch (currentCommand) {
                    case INTAKE:
                        if (colorSensorController.isArtifactPresent()) {
                            logComment("Artifact #1 in sorter");
                            // got 1st artifact in the sorter
                            colorSensorController.colorSensorsOff();
                            currentState = SorterState.ARTIFACT_EMPTY_EMPTY;
                        }
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                        break;
                    case SHOOT_TWO:
                        break;
                    case SHOOT_THREE:
                        break;
                    case INTAKE_OFF:
                        break;
                }
                break;

            case ARTIFACT_EMPTY_EMPTY:
                switch (currentCommand) {
                    case INTAKE:
                        rampDownIntakeOnMoveSorter(120, SorterState.WAITING_FOR_ARTIFACT_ARTIFACT_EMPTY);
                        currentState = SorterState.WAITING_FOR_RAMP_DOWN_INTAKE_ON_MOVE_SORTER;
                        break;
                    case NO_COMMAND:
                        break;
                    // treat any shoot command as a shoot 1 since there is only 1 in the sorter
                    case SHOOT_ONE:
                    case SHOOT_TWO:
                    case SHOOT_THREE:
                        rampUpIntakeOffMoveSorter(240, SorterState.EMPTY_EMPTY_EMPTY);
                        currentState = SorterState.WAITING_FOR_RAMP_UP_INTAKE_OFF_MOVE_SORTER;
                        break;
                    case INTAKE_OFF:
                        break;
                }
                break;

            case WAITING_FOR_RAMP_UP_INTAKE_OFF_MOVE_SORTER:
                // just hang out here until the little state machine completes and moves us to the next state
                updateRampUpIntakeOffMoveSorter();
                break;

            case WAITING_FOR_ARTIFACT_ARTIFACT_EMPTY:
                switch (currentCommand) {
                    case INTAKE:
                        if (colorSensorController.isArtifactPresent()) {
                            logComment("Artifact #2 in sorter");
                            // got 2nd artifact in the sorter
                            colorSensorController.colorSensorsOff();
                            currentState = SorterState.ARTIFACT_ARTIFACT_EMPTY;
                        }
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                        break;
                    case SHOOT_TWO:
                        break;
                    case SHOOT_THREE:
                        break;
                    case INTAKE_OFF:
                        break;
                }
                break;

            case ARTIFACT_ARTIFACT_EMPTY:
                switch (currentCommand) {
                    case INTAKE:
                        rampDownIntakeOnMoveSorter(120, SorterState.WAITING_FOR_ARTIFACT_ARTIFACT_ARTIFACT);
                        currentState = SorterState.WAITING_FOR_RAMP_DOWN_INTAKE_ON_MOVE_SORTER;
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                        rampUpIntakeOffMoveSorter(120, SorterState.EMPTY_ARTIFACT_EMPTY);
                        currentState = SorterState.WAITING_FOR_RAMP_UP_INTAKE_OFF_MOVE_SORTER;
                        break;
                    // there are only 2 artifacts in the sorter so treat a shoot 3 command the same as a shoot 2 command
                    case SHOOT_TWO:
                    case SHOOT_THREE:
                        rampUpIntakeOffMoveSorter(240, SorterState.EMPTY_EMPTY_EMPTY);
                        currentState = SorterState.WAITING_FOR_RAMP_UP_INTAKE_OFF_MOVE_SORTER;
                        break;
                    case INTAKE_OFF:
                        break;
                }
                break;

            case WAITING_FOR_ARTIFACT_ARTIFACT_ARTIFACT:
                switch (currentCommand) {
                    case INTAKE:
                        if (colorSensorController.isArtifactPresent()) {
                            logComment("Artifact #3 in sorter");
                            // got 3rd artifact in the sorter
                            colorSensorController.colorSensorsOff();
                            currentState = SorterState.ARTIFACT_ARTIFACT_ARTIFACT;
                        }
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                        break;
                    case SHOOT_TWO:
                        break;
                    case SHOOT_THREE:
                        break;
                    case INTAKE_OFF:
                        break;
                }
                break;

            case ARTIFACT_ARTIFACT_ARTIFACT:
                switch (currentCommand) {
                    case INTAKE:
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                        rampUpIntakeOffMoveSorter(120, SorterState.ARTIFACT_ARTIFACT_EMPTY);
                        currentState = SorterState.WAITING_FOR_RAMP_UP_INTAKE_OFF_MOVE_SORTER;
                        break;
                    case SHOOT_TWO:
                        rampUpIntakeOffMoveSorter(240, SorterState.EMPTY_ARTIFACT_EMPTY);
                        currentState = SorterState.WAITING_FOR_RAMP_UP_INTAKE_OFF_MOVE_SORTER;
                        break;
                    case SHOOT_THREE:
                        rampUpIntakeOffMoveSorter(360, SorterState.EMPTY_EMPTY_EMPTY);
                        currentState = SorterState.WAITING_FOR_RAMP_UP_INTAKE_OFF_MOVE_SORTER;
                        break;
                    case INTAKE_OFF:
                        break;
                }
                break;

            case EMPTY_ARTIFACT_EMPTY:
                switch (currentCommand) {
                    case INTAKE:
                        rampDownIntakeOnMoveSorter(120, SorterState.WAITING_FOR_ARTIFACT_ARTIFACT_EMPTY);
                        currentState = SorterState.WAITING_FOR_RAMP_DOWN_INTAKE_ON_MOVE_SORTER;
                        break;
                    case NO_COMMAND:
                        break;
                    // there is only 1 artifact in the sorter so treat a shoot 3 or shoot 2 command the same as a shoot 1 command
                    case SHOOT_ONE:
                    case SHOOT_TWO:
                    case SHOOT_THREE:
                        rampUpIntakeOffMoveSorter(120, SorterState.EMPTY_EMPTY_EMPTY);
                        currentState = SorterState.WAITING_FOR_RAMP_UP_INTAKE_OFF_MOVE_SORTER;
                        break;
                    case INTAKE_OFF:
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
    private boolean rampDownIntakeOnMoveSorter(double positionAddition, SorterState nextState) {
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
     * Run the state machine for rampDownIntakeOnMoveSorterState
     * This method will get called repeatedly by the sorter controller state machine and will run
     * over and over until it completes and sets the next state for the sorter controller to jump to.
     */
    private void updateRampDownIntakeOnMoveSorter() {
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
                }
                break;
        }
    }

    //*********************************************************************************************
    // State machine for moving ramp up, intake off and move sorter - prepping for a shoot
    //*********************************************************************************************
    private enum RampUpIntakeOffMoveSorterStates {
        WAITING_FOR_RAMP_UP,
        WAITING_FOR_MOTOR_TO_MOVE;
    }

    private RampUpIntakeOffMoveSorterStates rampUpIntakeOffMoveSorterState;
    private boolean rampUpIntakeOffMoveSorterComplete;
    private double rampUpIntakeOffMoveSorterAngleAddition;
    private SorterState stateAfterRampUpIntakeOffMoveSorter;

    /**
     * Setup the little state machine that moves the ramp up, turns the intake off and moves
     * the sorter before an intake starts.
     *
     * @param positionAddition
     * @param nextState
     * @return
     */
    private boolean rampUpIntakeOffMoveSorter(double positionAddition, SorterState nextState) {
        rampUpIntakeOffMoveSorterComplete = false;
        rampUpIntakeOffMoveSorterAngleAddition = positionAddition;
        stateAfterRampUpIntakeOffMoveSorter = nextState;
        // turn the color sensors off so that the moving sorter wheel is not mistaken for an artifact
        colorSensorController.colorSensorsOff();
        rampServo.upPosition();
        intakeMotor.off();
        rampUpIntakeOffMoveSorterState = RampUpIntakeOffMoveSorterStates.WAITING_FOR_RAMP_UP;
        return true;
    }

    /**
     * Run the state machine for rampUpIntakeOffMoveSorterState
     */
    private void updateRampUpIntakeOffMoveSorter() {
        switch (rampUpIntakeOffMoveSorterState) {
            case WAITING_FOR_RAMP_UP:
                if (rampServo.isPositionReached()) {
                    sorterMotor.moveByPosition(rampUpIntakeOffMoveSorterAngleAddition);
                    rampUpIntakeOffMoveSorterState = RampUpIntakeOffMoveSorterStates.WAITING_FOR_MOTOR_TO_MOVE;
                }
                break;
            case WAITING_FOR_MOTOR_TO_MOVE:
                if (sorterMotor.isMovementComplete()) {
                    // change the state for the sorter controller state machine.
                    // This will stop calling this state machine because the sorter controller state
                    // will jump to a new state.
                    currentState = stateAfterRampUpIntakeOffMoveSorter;
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
        sorterMotor.resetEncoder();
        colorSensorController.colorSensorsOn();
        return true;
    }
}
