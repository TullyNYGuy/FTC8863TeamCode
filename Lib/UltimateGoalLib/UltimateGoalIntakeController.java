package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.DeliveryLiftSystem;

public class UltimateGoalIntakeController implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum States {
        IDLE,
        NO_RING,
        ONE_RING,
        TWO_RING,
        THREE_RING;
    }

    private enum Commands {
        ESTOP,
        OFF,
        INTAKE,
        FIRE_1,
        FIRE_2,
        FIRE_3;
    }


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private States currentState = States.IDLE;
    private States previousState = currentState;
    private Commands currentCommand = Commands.OFF;
    private Commands previousCommand = currentCommand;
    public UltimateGoalIntake intake;
    private boolean commandComplete = true;

    private DataLogging logFile;
    private boolean loggingOn = false;
    // this says that the first line in the data log is about to be written
    private boolean firstLogLine = true;
    private boolean enableUpdate = true;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    public void setEnableUpdate () {
        enableUpdate=true;
    }

    public void setDisableUpdate () {
        enableUpdate= false;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public UltimateGoalIntakeController(HardwareMap hardwareMap, Telemetry telemetry, UltimateGoalIntake intake) {
        this.intake = intake;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Log the state and command into the log file. But only if the value of one of them has changed
     * from the last time the state machine was run.
     *
     * @param state   - value of the state (in the state machine)
     * @param command - the current command to the state machine
     */
    private void logState(UltimateGoalIntakeController.States state, UltimateGoalIntakeController.Commands command) {
        // is there a log file and is logging enabled?
        if (logFile != null && loggingOn) {
            // has the state or the command changed since the last run through the state machine? Or
            // is this the first line in the log file?
            if (state != previousState || command != previousCommand || firstLogLine) {
                // yes there was a change so write them into the log file OR
                // put the initial state and command into the log file
                logFile.logData("Intake Controller", state.toString(), command.toString());
                // save the current state and command so they can be used next time
                previousState = state;
                previousCommand = command;
                // if this was the first line in the log file, change the flag so that later on
                // we know that the first line has already been written
                firstLogLine = false;
            }
        }
    }

    /**
     * Write something into the log file
     *
     * @param stringToLog
     */
    private void log(String stringToLog) {
        if (logFile != null && loggingOn) {
            logFile.logData(stringToLog);
        }
    }

    /**
     * Handling an intake command is the same no matter what state the intake is in, with one minor
     * difference in the NO_RING state.
     *
     * @param currentState
     */
    private void handleIntakeCommand(States currentState) {
        switch (intake.getCurrentRingsAt()) {
            case NO_RINGS:
                intake.requestTurnStage123On();
                if (currentState == States.NO_RING) {
                    commandComplete = true;
                } else {
                    commandComplete = false;
                }
                break;
            case THREE:
                commandComplete = true;
                intake.requestTurnStage12On();
                this.currentState = States.ONE_RING;
                break;
            case TWO_THREE:
                commandComplete = true;
                intake.requestTurnStage1On();
                this.currentState = States.TWO_RING;
                break;
            case ONE_TWO_THREE:
                commandComplete = true;
                intake.requestTurnIntakeOFF();
                this.currentState = States.THREE_RING;
                break;
            case TWO:
            case ONE:
                intake.requestTurnStage123On();
                commandComplete = false;
                this.currentState = States.ONE_RING;
                break;

            case ONE_TWO:
                intake.requestTurnStage123On();
                commandComplete = false;
                this.currentState = States.TWO_RING;
                break;
            case ONE_THREE:
                intake.requestTurnStage12On();
                commandComplete = false;
                this.currentState = States.TWO_RING;
                break;
        }
    }
    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * This is the state machine for the Intake controller
     */
    public void update() {
        // update the intake state machine
        intake.update();
        if (enableUpdate) {
            // log the state and command
            logState(currentState, currentCommand);
            switch (currentState) {
                case IDLE:
                    switch (currentCommand) {
                        case ESTOP:
                            //already idle
                            break;
                        case OFF:
                            //already idle
                            break;
                        case INTAKE:
                        case FIRE_1:
                        case FIRE_2:
                        case FIRE_3:
                            commandComplete = false;
                            switch (intake.getCurrentRingsAt()) {
                                case NO_RINGS:
                                    currentState = States.NO_RING;
                                    break;
                                case THREE:
                                    currentState = States.ONE_RING;
                                    break;
                                case TWO_THREE:
                                    currentState = States.TWO_RING;
                                    break;
                                case ONE_TWO_THREE:
                                    currentState = States.THREE_RING;
                                    break;
                                case TWO:
                                    currentState = States.ONE_RING;
                                    break;
                                case ONE:
                                    currentState = States.ONE_RING;
                                    break;
                                case ONE_TWO:
                                    currentState = States.TWO_RING;
                                    break;
                                case ONE_THREE:
                                    currentState = States.TWO_RING;
                                    break;
                            }
                            break;
                    }
                    break;
                case NO_RING:
                    switch (currentCommand) {
                        case ESTOP:
                            intake.requestTurnIntakeOFF();
                            break;
                        case OFF:
                            if (intake.getCurrentRingsAt() == UltimateGoalIntake.RingsAt.NO_RINGS) {
                                intake.requestTurnIntakeOFF();
                                commandComplete = true;
                                currentState = States.IDLE;
                            }
                            break;
                        case INTAKE:
                            handleIntakeCommand(currentState);
                            break;
                        case FIRE_1:
                        case FIRE_2:
                        case FIRE_3:
                            intake.requestTurnIntakeOFF();
                            commandComplete = true;
                            currentState = States.IDLE;
                            break;
                    }
                    break;
                case ONE_RING:
                    switch (currentCommand) {
                        case ESTOP:
                            intake.requestTurnIntakeOFF();
                            break;
                        case OFF:
                            intake.requestTurnIntakeOFF();
                            commandComplete = true;
                            currentState = States.IDLE;
                            break;
                        case INTAKE:
                            handleIntakeCommand(currentState);
                            break;
                        case FIRE_1:
                        case FIRE_2:
                        case FIRE_3:
                            switch (intake.getCurrentRingsAt()) {
                                case THREE:
                                    intake.requestTurnStage3On();
                                    commandComplete = false;
                                    currentState = States.ONE_RING;
                                    break;
                                case NO_RINGS:
                                    intake.requestTurnIntakeOFF();
                                    commandComplete = true;
                                    currentState = States.IDLE;
                                    currentCommand = Commands.OFF;
                                    break;
                                //no other cases matter
                            }
                            break;
                    }
                    break;
                case TWO_RING:
                    switch (currentCommand) {
                        case ESTOP:
                            intake.requestTurnIntakeOFF();
                            break;
                        case OFF:
                            intake.requestTurnIntakeOFF();
                            commandComplete = true;
                            currentState = States.IDLE;
                            break;
                        case INTAKE:
                            handleIntakeCommand(currentState);
                            break;
                        case FIRE_1:
                            switch (intake.getCurrentRingsAt()) {
                                case TWO_THREE:
                                    intake.requestTurnStage23On();
                                    currentState = States.TWO_RING;
                                    commandComplete = false;
                                    break;
                                case TWO:
                                    intake.requestTurnStage23On();
                                    currentState = States.TWO_RING;
                                    commandComplete = false;
                                    break;
                                case NO_RINGS:
                                    intake.requestTurnStage23On();
                                    currentState = States.TWO_RING;
                                    commandComplete = false;
                                    break;
                                case THREE:
                                    intake.requestTurnIntakeOFF();
                                    commandComplete = true;
                                    currentState = States.IDLE;
                                    currentCommand = Commands.OFF;
                                    break;
                            }
                            break;
                        case FIRE_2:
                        case FIRE_3:
                            switch (intake.getCurrentRingsAt()) {
                                case TWO_THREE:
                                    intake.requestTurnStage23On();
                                    commandComplete = false;
                                    currentState = States.TWO_RING;
                                    break;
                                case TWO:
                                    intake.requestTurnStage23On();
                                    commandComplete = false;
                                    currentState = States.TWO_RING;
                                    break;
                                case NO_RINGS:
                                    if (intake.getNumberOfRingsAtStage3() == 1) {
                                        intake.requestTurnStage23On();
                                        commandComplete = false;
                                        currentState = States.TWO_RING;
                                    }
                                    if (intake.getNumberOfRingsAtStage3() == 2) {
                                        intake.requestTurnIntakeOFF();
                                        commandComplete = true;
                                        currentState = States.IDLE;
                                        currentCommand = Commands.OFF;
                                    }
                                    break;
                                case THREE:
                                    intake.requestTurnStage3On();
                                    commandComplete = false;
                                    currentState = States.TWO_RING;
                                    break;
                            }
                            break;
                    }
                    break;
                case THREE_RING:
                    switch (currentCommand) {
                        case ESTOP:
                            intake.requestTurnIntakeOFF();
                            break;
                        case OFF:
                            intake.requestTurnIntakeOFF();
                            commandComplete = true;
                            currentState = States.IDLE;
                            break;
                        case INTAKE:
                            handleIntakeCommand(currentState);
                            break;
                        case FIRE_1:
                            switch (intake.getCurrentRingsAt()) {
                                case ONE_TWO_THREE:
                                    intake.requestTurnStage3On();
                                    commandComplete = false;
                                    currentState = States.THREE_RING;
                                    break;
                                case TWO_THREE:
                                    commandComplete = true;
                                    intake.requestTurnIntakeOFF();
                                    currentState = States.TWO_RING;
                                    currentCommand = Commands.OFF;
                                    break;
                                case THREE:
                                    intake.requestTurnStage12On();
                                    commandComplete = false;
                                    currentState = States.THREE_RING;
                                    break;
                                case NO_RINGS:
                                    intake.requestTurnStage123On();
                                    commandComplete = false;
                                    currentState = States.THREE_RING;
                                    break;
                                case TWO:
                                    intake.requestTurnStage123On();
                                    commandComplete = false;
                                    currentState = States.THREE_RING;
                                    break;
                                case ONE_THREE:
                                    intake.requestTurnStage12On();
                                    commandComplete = false;
                                    currentState = States.THREE_RING;
                                    break;
                                case ONE_TWO:
                                    intake.requestTurnStage123On();
                                    commandComplete = false;
                                    currentState = States.THREE_RING;
                                    break;
                                case ONE:
                                    intake.requestTurnStage123On();
                                    currentState = States.THREE_RING;
                                    commandComplete = false;
                                    break;
                            }
                            break;
                        case FIRE_2:
                            switch (intake.getCurrentRingsAt()) {
                                case ONE_TWO_THREE:
                                    intake.requestTurnStage123On();
                                    commandComplete = false;
                                    currentState = States.THREE_RING;
                                    break;
                                case TWO_THREE:
                                    intake.requestTurnStage23On();
                                    commandComplete = false;
                                    currentState = States.THREE_RING;
                                    break;
                                case THREE:
                                    intake.requestTurnIntakeOFF();
                                    commandComplete = true;
                                    currentState = States.ONE_RING;
                                    currentCommand = Commands.OFF;
                                    break;
                                case NO_RINGS:
                                case TWO:
                                case ONE_THREE:
                                case ONE_TWO:
                                case ONE:
                                    intake.requestTurnStage123On();
                                    currentState = States.THREE_RING;
                                    commandComplete = false;
                                    break;
                            }
                        case FIRE_3:
                            switch (intake.getCurrentRingsAt()) {
                                case ONE_TWO_THREE:
                                    intake.requestTurnStage123On();
                                    commandComplete = false;
                                    currentState = States.THREE_RING;
                                    break;
                                case NO_RINGS:
                                    if (intake.getNumberOfRingsAtStage3() == 1 || intake.getNumberOfRingsAtStage3() == 2) {
                                        intake.requestTurnStage123On();
                                        commandComplete = false;
                                        currentState = States.THREE_RING;
                                    }
                                    if (intake.getNumberOfRingsAtStage3() == 3) {
                                        intake.requestTurnIntakeOFF();
                                        commandComplete = true;
                                        currentState = States.IDLE;
                                        currentCommand = Commands.OFF;
                                    }
                                    break;
                                case THREE:
                                case TWO_THREE:
                                case ONE:
                                case TWO:
                                case ONE_TWO:
                                case ONE_THREE:
                                    intake.requestTurnStage123On();
                                    currentState = States.THREE_RING;
                                    commandComplete = false;
                                    break;

                            }
                            break;
                    }
                    break;
            }
        }
    }

    //*********************************************************************************************
    //          Ask the controller to do something
    //*********************************************************************************************

    public void requestIntake() {
        if (commandComplete) {
            currentCommand = Commands.INTAKE;
        }
    }

    public void requestOff() {
        if (commandComplete) {
            currentCommand = Commands.OFF;
        }
    }

    public void requestFire_1() {
        if (commandComplete) {
            currentCommand = Commands.FIRE_1;
        }
    }

    public void requestFire_2() {
        if (commandComplete) {
            currentCommand = Commands.FIRE_2;
        }
    }

    public void requestFire_3() {
        if (commandComplete) {
            currentCommand = Commands.FIRE_3;
        }
    }

    public void requestEstop() {
        currentCommand = Commands.ESTOP;
    }

    @Override
    public String getName() {
        return "intakeController";
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
        return true;
    }

    public void reset () {
        currentState=States.IDLE;
        currentCommand= Commands.OFF;
        commandComplete= true;
    }
}