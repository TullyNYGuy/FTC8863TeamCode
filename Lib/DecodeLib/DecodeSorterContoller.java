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
    private enum SorterState {
        WAITING_ARTIFACT_123,
        WAITING_FOR_120_INTAKE,
        ONE_ARTIFACT_IN_SORTER_312_INTAKE_CYCLE,
        WAITING_FOR_240_INTAKE,
        TWO_ARTIFACT_IN_SORTER_231_INTAKE_CYCLE,
        WAITING_FOR_360_INTAKE,
        THREE_ARTIFACT_IN_SORTER_231_INTAKE_CYCLE,
        WAITING_FOR_RAMP_UP,
        WAITING_FOR_RAMP_DOWN,
        THREE_ARTIFACT_IN_SORTER_123_PRESHOOT,
        WAITING_FOR_FIRST_SHOT_TO_COMPLETE,
        TWO_ARTIFACT_LEFT_312_SHOOTING_CYCLE,
        WAITING_FOR_SECOND_SHOT_TO_COMPLETE,
        ONE_ARTIFACT_LEFT_231_SHOOTING_CYCLE,
        WAITING_FOR_THIRD_SHOT_TO_COMPLETE,
        ZERO_ARTIFACT_LEFT_123_SHOOTING_CYCLE;
    }

    private SorterState currentState = SorterState.WAITING_ARTIFACT_123;

    private enum Commands {
        SHOOT_ONE,
        SHOOT_TWO,
        SHOOT_THREE,
        NO_COMMAND,
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
        // initial state is intake
        currentState = SorterState.WAITING_ARTIFACT_123;
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
        telemetry.addData("Sorter controller state = ", currentState.toString()) ;
    }

    public void displayCommand(Telemetry telemetry) {
        telemetry.addData("Sorter controller command = ", currentCommand.toString()) ;
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
                currentState == SorterState.ONE_ARTIFACT_IN_SORTER_312_INTAKE_CYCLE ||
                currentState == SorterState.TWO_ARTIFACT_IN_SORTER_231_INTAKE_CYCLE ||
                currentState == SorterState.THREE_ARTIFACT_IN_SORTER_123_PRESHOOT) {
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
                currentState == SorterState.ONE_ARTIFACT_IN_SORTER_312_INTAKE_CYCLE ||
                currentState == SorterState.TWO_ARTIFACT_IN_SORTER_231_INTAKE_CYCLE ||
                currentState == SorterState.THREE_ARTIFACT_IN_SORTER_123_PRESHOOT) {
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
                currentState == SorterState.ONE_ARTIFACT_IN_SORTER_312_INTAKE_CYCLE ||
                currentState == SorterState.TWO_ARTIFACT_IN_SORTER_231_INTAKE_CYCLE ||
                currentState == SorterState.THREE_ARTIFACT_IN_SORTER_123_PRESHOOT) {
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
            case WAITING_ARTIFACT_123:
                switch (currentCommand) {
                    case INTAKE:
                        intakeMotor.intake();
                        if (colorSensorController.isArtifactPresent()) {
                            logComment("Artifact #1 in sorter");
                            // got 1st artifact in the sorter
                            colorSensorController.colorSensorsOff();
                            sorterMotor.moveToPosition(120);
                            currentState = SorterState.WAITING_FOR_120_INTAKE;
                        }
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

            // waiting for motor to arrive at position
            // 1 artifact in sorter
            case WAITING_FOR_120_INTAKE:
                if (sorterMotor.isMovementComplete()) {
                    colorSensorController.colorSensorsOn();
                    currentState = SorterState.ONE_ARTIFACT_IN_SORTER_312_INTAKE_CYCLE;
                }
                switch (currentCommand) {
                    case INTAKE:
                        // We are intaking
                        break;
                    case NO_COMMAND:
                        // Not a valid command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case SHOOT_ONE:
                        // Not a valid command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case SHOOT_TWO:
                        // Not a valid command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case SHOOT_THREE:
                        // Not a valid command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case INTAKE_OFF:
                        // Do not act on this command yet
                        break;
                }
                break;

            // 1 artifact in the sorter
            // look for an artifact coming into the sorter
            case ONE_ARTIFACT_IN_SORTER_312_INTAKE_CYCLE:
                switch (currentCommand) {
                    case INTAKE:
                        if (colorSensorController.isArtifactPresent()) {
                            logComment("Artifact #2 in sorter");
                            // got 2nd artifact in the sorter
                            colorSensorController.colorSensorsOff();
                            sorterMotor.moveToPosition(240);
                            currentState = SorterState.WAITING_FOR_240_INTAKE;
                        }
                        break;
                    case NO_COMMAND:
                        // Ignore this command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case SHOOT_ONE:
                    case SHOOT_TWO:
                    case SHOOT_THREE:
                        intakeMotor.off();
                        rampServo.upPosition();
                        currentState = SorterState.WAITING_FOR_RAMP_UP;
                        // set the state to go to after the reamp is up
                        stateAfterRampIsUp = SorterState.ONE_ARTIFACT_LEFT_231_SHOOTING_CYCLE;
                        break;
                    case INTAKE_OFF:
                        intakeMotor.off();
                        commandComplete = true;
                        break;
                }
                break;

            // waiting for motor to arrive at position
            // We have 2 artifacts in the sorter
            case WAITING_FOR_240_INTAKE:
                if (sorterMotor.isMovementComplete()) {
                    colorSensorController.colorSensorsOn();
                    currentState = SorterState.TWO_ARTIFACT_IN_SORTER_231_INTAKE_CYCLE;
                }
                switch (currentCommand) {
                    case INTAKE:
                        // We are intaking
                        break;
                    case NO_COMMAND:
                        // Not a valid command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case SHOOT_ONE:
                        // Not a valid command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case SHOOT_TWO:
                        // Not a valid command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case SHOOT_THREE:
                        // Not a valid command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case INTAKE_OFF:
                        // Do not act on this command yet
                        break;
                }
                break;

            // 2 artifacts in the sorter
            // look for an artifact coming into the sorter
            case TWO_ARTIFACT_IN_SORTER_231_INTAKE_CYCLE:
                switch (currentCommand) {
                    case INTAKE:
                        if (colorSensorController.isArtifactPresent()) {
                            logComment("Artifact #3 in sorter");
                            // got 3rd artifact in the sorter
                            // prepare to shoot
                            colorSensorController.colorSensorsOff();
                            //ToDo Do we need to move to 360?
                            sorterMotor.moveToPosition(360);
                            intakeMotor.off();
                            currentState = SorterState.WAITING_FOR_360_INTAKE;
                        }
                        break;
                    case NO_COMMAND:
                        // Ignore this command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case SHOOT_ONE:
                    case SHOOT_TWO:
                    case SHOOT_THREE:
                        intakeMotor.off();
                        rampServo.upPosition();
                        currentState = SorterState.WAITING_FOR_RAMP_UP;
                        // set the state to go to after the reamp is up
                        stateAfterRampIsUp = SorterState.TWO_ARTIFACT_LEFT_312_SHOOTING_CYCLE;
                        break;
                    case INTAKE_OFF:
                        intakeMotor.off();
                        commandComplete = true;
                        break;
                }

                break;

            // Three artifacts in sorter
            // waiting for motor to arrive at position
            case WAITING_FOR_360_INTAKE:
                if (sorterMotor.isMovementComplete()) {
                    // prepare to shoot
                    commandComplete = true;
                    rampServo.upPosition();
                    intakeOff();
                    currentState = SorterState.WAITING_FOR_RAMP_UP;
                    // set the state to go to after the reamp is up
                    stateAfterRampIsUp = SorterState.THREE_ARTIFACT_IN_SORTER_123_PRESHOOT;
                }
                switch (currentCommand) {
                    case INTAKE:
                        // We are intaking
                        break;
                    case NO_COMMAND:
                        // Not a valid command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case SHOOT_ONE:
                        // Not a valid command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case SHOOT_TWO:
                        // Not a valid command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case SHOOT_THREE:
                        // Not a valid command continue intaking
                        currentCommand = Commands.INTAKE;
                        break;
                    case INTAKE_OFF:
                        // Do not act on this command yet
                        break;
                }
                break;

            case WAITING_FOR_RAMP_UP:
                switch (currentCommand) {
                    case INTAKE:
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
                        intakeMotor.off();
                        break;
                }
                if (rampServo.isPositionReached()) {
                    logComment("Ready to shoot");
                    // move to the state that was setup to move to before this state was entered
                    // DON'T FORGET TO SETUP THE NEXT STATE AFTER THIS ONE BEFORE YOU SET THIS STATE AS THE NEXT STATE
                    // IF YOU FORGET YOU WILL CRASH ON A NULL POINTER EXCEPTION
                    currentState = stateAfterRampIsUp;
                    commandComplete = true;
                }
                break;

            // 3 artifacts in the sorter
            // wait for a shoot command
            case THREE_ARTIFACT_IN_SORTER_123_PRESHOOT:
                switch (currentCommand) {
                    case INTAKE:
                        currentCommand=Commands.NO_COMMAND;
                        commandComplete=true;
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                        logComment("Shooting 1 artifact");
                        // shoot 1 artifact
                        sorterMotor.moveToPosition(120);
                        currentState = SorterState.WAITING_FOR_FIRST_SHOT_TO_COMPLETE;
                        currentCommand = Commands.NO_COMMAND;
                        break;
                    case SHOOT_TWO:
                        logComment("Shooting 2 artifacts");
                        // shoot 2 artifacts
                        sorterMotor.moveToPosition(240);
                        currentState = SorterState.WAITING_FOR_SECOND_SHOT_TO_COMPLETE;
                        currentCommand = Commands.NO_COMMAND;
                        break;
                    case SHOOT_THREE:
                        logComment("Shooting 3 artifacts");
                        // shoot 3 artifacts
                        sorterMotor.moveToPosition(360);
                        currentState = SorterState.WAITING_FOR_THIRD_SHOT_TO_COMPLETE;
                        currentCommand = Commands.NO_COMMAND;
                    case INTAKE_OFF:
                        commandComplete=true;
                        intakeMotor.off();
                        break;
                }

                break;

            // waiting for motor to arrive at position
            case WAITING_FOR_FIRST_SHOT_TO_COMPLETE:
                switch (currentCommand) {
                    case INTAKE:
                        currentCommand=Commands.NO_COMMAND;
                        commandComplete=true;
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                        if (sorterMotor.isMovementComplete()) {
                            currentState = SorterState.TWO_ARTIFACT_LEFT_312_SHOOTING_CYCLE;
                        }
                        break;
                    case SHOOT_TWO:
                        currentCommand=Commands.SHOOT_ONE;
                        break;
                    case SHOOT_THREE:
                        currentCommand=Commands.SHOOT_ONE;
                        break;
                    case INTAKE_OFF:
                        intakeMotor.off();
                        break;
                }

                break;

            // 2 artifacts left in the sorter
            // wait for a shoot command
            case TWO_ARTIFACT_LEFT_312_SHOOTING_CYCLE:
                switch (currentCommand) {
                    case INTAKE:
                        rampServo.downPosition();
                        //currentState=
                        break;
                    case NO_COMMAND:
                        break;
                    case SHOOT_ONE:
                        logComment("Shooting 1 artifacts");
                        // shoot 1 artifact
                        sorterMotor.moveToPosition(240);
                        currentState = SorterState.WAITING_FOR_SECOND_SHOT_TO_COMPLETE;
                        currentCommand = Commands.NO_COMMAND;
                        break;
                    case SHOOT_TWO:
                    case SHOOT_THREE:
                        logComment("Shooting 2 artifacts");
                        // shoot 2 artifacts
                        sorterMotor.moveToPosition(360);
                        currentState = SorterState.WAITING_FOR_THIRD_SHOT_TO_COMPLETE;
                        currentCommand = Commands.NO_COMMAND;
                        break;
                    case INTAKE_OFF:
                        intakeMotor.off();
                        break;
                }
                if (currentCommand == Commands.SHOOT_ONE) {

                }
                // there are 2 artifacts left. If driver says shoot 3, we can only really shoot 2
                if (currentCommand == Commands.SHOOT_TWO || currentCommand == Commands.SHOOT_THREE) {

                }
                break;

            // waiting for motor to arrive at position
            case WAITING_FOR_SECOND_SHOT_TO_COMPLETE:
                switch (currentCommand) {
                    case INTAKE:
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
                if (sorterMotor.isMovementComplete()) {
                    currentState = SorterState.ONE_ARTIFACT_LEFT_231_SHOOTING_CYCLE;
                }
                break;

            // waiting for motor to arrive at position
            case WAITING_FOR_THIRD_SHOT_TO_COMPLETE:
                switch (currentCommand) {
                    case INTAKE:
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
                if (sorterMotor.isMovementComplete()) {
                    logComment("Shot last artifact");
                    currentState = SorterState.ZERO_ARTIFACT_LEFT_123_SHOOTING_CYCLE;
                }
                break;

            // 1 artifact left in the sorter
            // wait for a shoot command
            case ONE_ARTIFACT_LEFT_231_SHOOTING_CYCLE:
                switch (currentCommand) {
                    case INTAKE:
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
                if (currentCommand == Commands.SHOOT_ONE || currentCommand == Commands.SHOOT_TWO || currentCommand == Commands.SHOOT_THREE) {
                    logComment("Shooting 1 artifact");
                    // shoot 1 artifact
                    sorterMotor.moveToPosition(360);
                    currentState = SorterState.WAITING_FOR_THIRD_SHOT_TO_COMPLETE; // THREE_SHOT refers to the position of the last artifact being shot, not the number being shot.
                    currentCommand = Commands.NO_COMMAND;
                }
                break;

            // 0 artifacts left in the sorter
            // automatically start intake again
            case ZERO_ARTIFACT_LEFT_123_SHOOTING_CYCLE:
                switch (currentCommand) {
                    case INTAKE:
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
                if (sorterMotor.isMovementComplete()) {
                    logComment("Shot last artifact so auto starting intake");
                    // prepare to intake
                    rampServo.downPosition();
                    intakeMotor.intake();
                    currentCommand = Commands.INTAKE;
                    colorSensorController.colorSensorsOn();
                }
                currentState = SorterState.WAITING_ARTIFACT_123;
                break;
            case WAITING_FOR_RAMP_DOWN:
                if (rampServo.isPositionReached()){
                    //sorterMotor.moveToPosition();
                }
                break;

        }
        switch (rampDownState) {
            case WAITING_FOR_RAMP_DOWN:
                if (rampServo.isPositionReached()) {
                    sorterMotor.moveToPosition(motorPositionAfterRampDown);
                    rampDownState=RampDownStates.WAITING_FOR_MOTOR_TO_MOVE;
                }
                break;
            case WAITING_FOR_MOTOR_TO_MOVE:
                if (sorterMotor.isMovementComplete()) {
                   currentState=sorterStateAfterRampDown;
                   rampDownDone = true;
                }
                break;
        }
    }
   private boolean rampDownMoveMotor(double position, SorterState nextState) {
        rampDownDone=false;
        motorPositionAfterRampDown=position;
        sorterStateAfterRampDown=nextState;
        rampServo.downPosition();
        rampDownState=RampDownStates.WAITING_FOR_RAMP_DOWN;
        return true;
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
