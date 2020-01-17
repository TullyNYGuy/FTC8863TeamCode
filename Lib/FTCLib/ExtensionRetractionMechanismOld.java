package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ExtensionRetractionMechanismOld {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    /**
     * These are the possible commands to give to the extension retraction mechanism.
     */
    private enum ExtensionRetractionCommands {
        NO_COMMAND, // there is no current command
        GO_TO_RETRACTED, // retract fully
        GO_TO_EXTENDED, // extend fully
        GO_TO_POSITION, // go to a specified position between fully extended and fully retracted
        RESET, // it is assumed that a reset moves the mechanism to a retracted position
        JOYSTICK // run under joystick control
    }

    /**
     * These are the states for the state machine that runs the mechanism.
     */
    private enum ExtensionRetractionStates {
        RESET, // reset
        RESET_MOVING_TO_RETRACTED, // reset state and in process of moving to fully retracted position
        RETRACTED, // fully retracted
        IN_BETWEEN, // in between fully retracted and fully extended
        EXTENDED // fully extended
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private DcMotor8863 extensionRetractionMotor;

    private Switch retractedLimitSwitch;
    private Switch extendedLimitSwitch;

    private ExtensionRetractionCommands extensionRetractionCommand;
    private ExtensionRetractionStates previousExtensionRetractionState;
    private ExtensionRetractionStates extensionRetractionState;
    private ExtensionRetractionCommands previousExtensionRetractionCommand;

    private Telemetry telemetry;

    /**
     * The name of the retraction limit switch that was set in the configuration on the phone
     */
    private String retractionLimitSwitchName = "";

    public String getRetractionLimitSwitchName() {
        return retractionLimitSwitchName;
    }

    public void setRetractionLimitSwitchName(String retractionLimitSwitchName) {
        this.retractionLimitSwitchName = retractionLimitSwitchName;
    }

    /**
     * The name of the extension limit switch that was set in the configuration on the phone
     */
    private String extensionLimitSwitchName = "";

    public String getExtensionLimitSwitchName() {
        return extensionLimitSwitchName;
    }

    public void setExtensionLimitSwitchName(String extensionLimitSwitchName) {
        this.extensionLimitSwitchName = extensionLimitSwitchName;
    }

    /**
     * The name of the motor for this mechanism that was set in the configuration on the phone.
     */
    private String motorName = "";

    public String getMotorName() {
        return motorName;
    }

    public void setMotorName(String motorName) {
        this.motorName = motorName;
    }

    /**
     * The type of motor that is driving this mechanism.
     */
    private DcMotor8863.MotorType motorType;

    public DcMotor8863.MotorType getMotorType() {
        return motorType;
    }

    public void setMotorType(DcMotor8863.MotorType motorType) {
        this.motorType = motorType;
    }

    /**
     * The movement of the mechanism per revolution of the motor. This can be in CM, Inches or whatever
     * units you like. Just be consistent in your units when you get positions or give position
     * commands.
     */
    private double movementPerRevolution = 0;

    public double getMovementPerRevolution() {
        return movementPerRevolution;
    }

    public void setMovementPerRevolution(double movementPerRevolution) {
        this.movementPerRevolution = movementPerRevolution;
    }

    /**
     * The name of this mechanism. This is used in the log files and in messages to the user.
     */
    private String mechanismName = "";

    public String getMechanismName() {
        return mechanismName;
    }

    public void setMechanismName(String mechanismName) {
        this.mechanismName = mechanismName;
    }

    private double desiredPosition = 0;
    private double extensionRetractionPower = 0;
    private double extensionRetractionSpeed = .5;

    /**
     * There is one predefined position that is available to you other than fully retracted and fully
     * extended. The home position is the position that the mechanism normally rests at. Call
     * goToHome() to send the mechanism to the home position.
     */
    private double homePosition = 0;

    public double getHomePosition() {
        return homePosition;
    }

    public void setHomePosition(double homePosition) {
        this.homePosition = homePosition;
    }

    /**
     * There is a debug mode that allows you to do things to the mechanism that normally would not
     * be allowed.
     */
    private boolean debugMode = false;

    public boolean isDebugMode() {
        return debugMode;
    }

    public void enableDebugMode() {
        this.debugMode = true;
        this.extensionRetractionSpeed = .2;
        // normally the mechanism has to be reset before it will accept any commands.
        // This forces it to locate its 0 position before any other commands will
        // run. But when debugging you may not want the mechanism to have to reset before
        // running any commands. So if the mechanism is in debug mode, force the state machine to think
        // the mechanism is IN_BETWEEN so any command sent to the mechanism will run.
        extensionRetractionState = ExtensionRetractionStates.IN_BETWEEN;
    }

    public void disableDebugMode() {
        this.debugMode = false;
        this.extensionRetractionSpeed = .5;
    }

    /**
     * The commands sent to the mechanism and the states it passes through can be logged so that
     * you can review them later. The log is a file stored on the phone.
     */
    private DataLogging logFile;

    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    public void enableDataLogging() {
        this.loggingOn = true;
    }

    public void disableDataLogging() {
        this.loggingOn = false;
    }

    private boolean loggingOn = false;
    private boolean arrivedAlreadyLogged = false;

    /**
     * The mechanism can be run manually using a joystick as input. The power is stored here.
     */
    private double joystickPower = 0;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public ExtensionRetractionMechanismOld(HardwareMap hardwareMap, Telemetry telemetry, String motorName,
                                           String extensionLimitSwitchName, String retractionLimitSwitchName,
                                           DcMotor8863.MotorType motorType, double movementPerRevolution,
                                           String mechanismName) {
        // set all of the private variables using the parameters passed into the constructor
        this.motorName = motorName;
        this.extensionLimitSwitchName = extensionLimitSwitchName;
        this.retractionLimitSwitchName = retractionLimitSwitchName;
        this.motorType = motorType;
        this.movementPerRevolution = movementPerRevolution;
        this.mechanismName = mechanismName;

        this.telemetry = telemetry;

        // create the motor
        extensionRetractionMotor = new DcMotor8863(motorName, hardwareMap, telemetry);
        extensionRetractionMotor.setMotorType(motorType);
        extensionRetractionMotor.setMovementPerRev(movementPerRevolution);

        // create the limit switches
        retractedLimitSwitch = new Switch(hardwareMap, retractionLimitSwitchName, Switch.SwitchType.NORMALLY_OPEN);
        extendedLimitSwitch = new Switch(hardwareMap, extensionLimitSwitchName, Switch.SwitchType.NORMALLY_OPEN);

        // set the initial state of the state machine
        extensionRetractionState = ExtensionRetractionStates.RESET;
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Write a string into the logfile.
     *
     * @param stringToLog
     */
    private void log(String stringToLog) {
        if (logFile != null && loggingOn) {
            logFile.logData(stringToLog);
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * This method is meant to be called as the robot initializes. It resets the mechanism upon
     * initialization.
     */
    public void init() {
        log(mechanismName + "Extension retraction system initializing");
        if (!isDebugMode()) {
            // if the mechanism is not already at the retracted then there may be something that has
            // to be done while the mechanism is retracting. For example, suppose there is a delivery
            // box attached to the mechanism. Maybe the delivery box has
            // to be raised in order for it to clear an obstacle while the mechanism is retracting.
            // I need to find a way to pass in a method that can be called before the reset is
            // started and after the reset is completed.
            if (!retractedLimitSwitch.isPressed()) {
                //methodToRunBeforeReset();
            }
            reset();
            while (!isMovementComplete()) {
                update();
            }
            // mechanism movement is complete so run the method for after the reset
            //methodToRunAfterReset();
        }
    }

    /**
     * This method is called as part of the shutdown sequence of the robot
     */
    public void shutdown() {
        //methodToRunAtShutdown;
    }

    //*********************************************************************************************]
    //  motor position feedback
    //**********************************************************************************************

    public int getMotorEncoderValue() {
        return extensionRetractionMotor.getCurrentPosition();
    }

    public void displayMotorEncoderValue() {
        telemetry.addData("Encoder = ", getMotorEncoderValue());
    }

    public double getPosition() {
        return extensionRetractionMotor.getPositionInTermsOfAttachment();
    }

    public void displayPosition() {
        telemetry.addData(mechanismName + " position (inches) = ", getPosition());
    }

    public void displayRequestedPosition() {
        telemetry.addData(mechanismName + " position requested (inches) = ", desiredPosition);
    }

    public void displayPower() {
        telemetry.addData(mechanismName + " power (inches) = ", extensionRetractionPower);
    }

    public void displayMotorState() {
        telemetry.addData("Motor state = ", extensionRetractionMotor.getCurrentMotorState().toString());
    }
    //*********************************************************************************************]
    // mechanism commands - these can come at any time. They are not synced to any state. In
    // engineering terms, they are asynchronous. These commands are publicly accessible.
    //**********************************************************************************************

    /**
     * Reset the mechanism
     */
    public void reset() {
        log("DRIVER COMMANDED " + mechanismName.toUpperCase() + "  TO RESET");
        // the next execution of the state machine will pick up this new command and execute it
        extensionRetractionCommand = ExtensionRetractionCommands.RESET;
    }

    /**
     * Command the mechanism to fully retract
     */
    public void goToFullRetract() {
        log("COMMANDED " + mechanismName.toUpperCase() + " TO RETRACTED POSITION");
        // the next execution of the state machine will pick up this new command and execute it
        extensionRetractionCommand = ExtensionRetractionCommands.GO_TO_RETRACTED;
    }

    /**
     * Command the mechanism to fully extend
     */
    public void goToFullExtend() {
        log("COMMANDED " + mechanismName.toUpperCase() + " TO EXTENDED POSITION");
        // the next execution of the state machine will pick up this new command and execute it
        extensionRetractionCommand = ExtensionRetractionCommands.GO_TO_EXTENDED;
    }

    /**
     * Command the mechanism to go to its predefined home position. Use setHomePosition() to set
     * the home position.
     */
    public void goToHome() {
        log("COMMANDED " + mechanismName.toUpperCase() + " TO GO TO HOME");
        moveToPosition(homePosition, 1);
    }

    /**
     * Move to a position based on zero which is set when the mechanism is all the way retracted,
     * You must call the update() method in a loop after this.
     *
     * @param position                 desired position; 0 is the fully retracted position units are
     *                                 whatever you setup the mechanism for initially
     * @param extensionRetractionPower max power for the motor
     */
    public void moveToPosition(double position, double extensionRetractionPower) {
        if (isMovementComplete()) {
            arrivedAlreadyLogged = false;
            log("Moving mechanism to a position = " + position);
            desiredPosition = position;
            this.extensionRetractionPower = extensionRetractionPower;
            // the next execution of the state machine will pick up this new command and execute it
            extensionRetractionCommand = ExtensionRetractionCommands.GO_TO_POSITION;
            extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            extensionRetractionMotor.moveToPosition(extensionRetractionPower, position, DcMotor8863.FinishBehavior.FLOAT);
        } else {
            // previous mechanism movement is not complete, ignore command
            log("Asked mechanism to move to position but it is already moving, ignored command");
            extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
        }

    }

    /**
     * Use the joystick to control the mechanism. This has it's limits though. You can't joystick
     * control the mechanism during a reset. You can't do stupid things like extend the mechanism
     * when it is already fully extended. etc.
     *
     * @param power apply this power to the motor
     */
    public void setLiftPowerUsingJoystick(double power) {
        // if a command has been given to reset the mechanism, then do not allow the joystick to take
        // effect and override the reset
        joystickPower = 0;
        if (extensionRetractionCommand != ExtensionRetractionCommands.RESET) {
            // A joystick command is only a real command if it is not 0. If the joystick value is 0
            // just ignore the stick
            if (power != 0) {
                extensionRetractionCommand = ExtensionRetractionCommands.JOYSTICK;
                joystickPower = power;
            }
        }
    }

    //*********************************************************************************************]
    // mechanism commands - these can come at any time. They are not synced to any state. In
    // engineering terms, they are asynchronous. These commands are NOT publicly accessible.
    //**********************************************************************************************

    /**
     * This method is only called by the state machine. Cause the mechanism to start to move to a
     * fully retracted position.
     */
    private void moveToFullRetract() {
        // when the mechanism retracts you may want to do something with whatever is attached to it.
        if (!retractedLimitSwitch.isPressed()) {
            //methodToRunBeforeFullRetract;
        }
        extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extensionRetractionMotor.setPower(-1.0);
    }

    /**
     * This method is only called by the state machine. Cause the mechanism to start to move to a
     * fully extended position.
     */
    private void moveToFullExtend() {
        // when the mechanism retracts you may want to do something with whatever is attached to it.
        if (!extendedLimitSwitch.isPressed()) {
            //methodToRunBeforeFullExtend;
        }
        extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extensionRetractionMotor.setPower(+1);
    }

    /**
     * This method is only called by the state machine. Cause the mechanism to start to stop moving.
     */
    private void stopMechanism() {
        log(mechanismName.toUpperCase() + " ARRIVED AT DESTINATION");
        extensionRetractionMotor.setPower(0);
    }

    /**
     * Check if the mechanism needs to extend to get to the target position. This method is used
     * by the state machine to determine whether to extend or to retract in order to reach the
     * requested position.
     *
     * @return
     */
    private boolean isMechanismMovementExtension() {
        // if the position that we want to move to is greater than the current position of the mechanism,
        // then the movement of the mechanism will be to extend. For example, desired position is 10. Current
        // position is 5. So the mechanism has to extend to get there.
        if (desiredPosition - getPosition() > 0) {
            return true;
        } else {
            return false;
        }
    }

    //*********************************************************************************************]
    // mechanism state machine
    //**********************************************************************************************

    public ExtensionRetractionStates update() {
        DcMotor8863.MotorState motorState = extensionRetractionMotor.update();
        logState(extensionRetractionState, extensionRetractionCommand);

        switch (extensionRetractionState) {
            case RESET:
                switch (extensionRetractionCommand) {
                    case RESET:
                        log("Resetting mechanism");
                        // send the mechanism to retract
                        moveToFullRetract();
                        extensionRetractionState = ExtensionRetractionStates.RESET_MOVING_TO_RETRACTED;
                        break;
                    // all other commands are ignored when a reset is issued. Basically force
                    // the command back to a reset
                    case GO_TO_RETRACTED:
                        logIgnoreCommand(ExtensionRetractionCommands.GO_TO_RETRACTED);
                        extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        break;
                    case GO_TO_EXTENDED:
                        logIgnoreCommand(ExtensionRetractionCommands.GO_TO_EXTENDED);
                        extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        break;
                    case GO_TO_POSITION:
                        logIgnoreCommand(ExtensionRetractionCommands.GO_TO_POSITION);
                        extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        break;
                    case JOYSTICK:
                        logIgnoreCommand(ExtensionRetractionCommands.JOYSTICK);
                        extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        break;
                    case NO_COMMAND:
                        break;
                }
                break;

            // This state means that a reset was requested and the mechanism has already started
            // moving to the retracted position. It is here so that a moveToFullRetract() is
            // not repeatedly called.
            case RESET_MOVING_TO_RETRACTED:
                switch (extensionRetractionCommand) {
                    case RESET:
                        // the mechanism has been sent to the retracted position from a reset command.
                        // It is just retracting until the limit switch is pressed and the motor
                        // is told to stop.
                        if (retractedLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. Stop the motor. Clear the command.
                            stopMechanism();
                            // reset the encoder
                            extensionRetractionMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                            extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                            extensionRetractionState = ExtensionRetractionStates.RETRACTED;
                        }
                        break;
                    // all other commands are ignored when a reset is issued. Basically force
                    // the command back to a reset
                    case GO_TO_RETRACTED:
                        logIgnoreCommand(ExtensionRetractionCommands.GO_TO_RETRACTED);
                        extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        break;
                    case GO_TO_EXTENDED:
                        logIgnoreCommand(ExtensionRetractionCommands.GO_TO_EXTENDED);
                        extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        break;
                    case GO_TO_POSITION:
                        logIgnoreCommand(ExtensionRetractionCommands.GO_TO_POSITION);
                        extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        break;
                    case JOYSTICK:
                        logIgnoreCommand(ExtensionRetractionCommands.JOYSTICK);
                        extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        break;
                    case NO_COMMAND:
                        break;
                }
                break;
            // this state does NOT mean that the mechanism is at the retracted position
            // it means that the mechanism is moving to the retracted OR at the retracted position
            case RETRACTED:
                switch (extensionRetractionCommand) {
                    case RESET:
                        // a reset can be requested at any time. Start the motor movement and change
                        // state
                        moveToFullRetract();
                        extensionRetractionState = ExtensionRetractionStates.RESET_MOVING_TO_RETRACTED;
                        break;
                    case GO_TO_RETRACTED:
                        // the mechanism has been sent to the retracted without using a position command.
                        // It is just retracting until the motor is told to stop.
                        if (retractedLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. Stop the motor. Clear the command.
                            stopMechanism();
                            extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                        }
                        break;
                    case GO_TO_EXTENDED:
                        // the mechanism has been requested to move to the extended. The motor needs to be
                        // turned on and will run towards the extended with just speed control, no position
                        // control
                        moveToFullExtend();
                        extensionRetractionState = ExtensionRetractionStates.EXTENDED;
                        break;
                    case GO_TO_POSITION:
                        // the mechanism has been requested to move to a position. The motor has already
                        // been started in position control mode so we don't need to do anything
                        // with the motor. We just need to change state.
                        extensionRetractionState = ExtensionRetractionStates.IN_BETWEEN;
                        break;
                    case JOYSTICK:
                        processJoystick();
                        break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                }
                break;

            // this state is for when the mechanism is located somewhere in between the extended and retracted
            // and is not moving to fully extended or moving to fully retracted or being reset
            case IN_BETWEEN:
                switch (extensionRetractionCommand) {
                    case RESET:
                        // a reset can be requested at any time. Start the motor movement and change
                        // state
                        moveToFullRetract();
                        extensionRetractionState = ExtensionRetractionStates.RESET_MOVING_TO_RETRACTED;
                        break;
                    case GO_TO_RETRACTED:
                        // the mechanism has been requested to move to the retracted. The motor needs to be
                        // turned on and will run towards the retracted with just speed control, no position
                        // control
                        moveToFullRetract();
                        extensionRetractionState = ExtensionRetractionStates.RETRACTED;
                        break;
                    case GO_TO_EXTENDED:
                        // the mechanism has been requested to move to the extended. The motor needs to be
                        // turned on and will run towards the extended with just speed control, no position
                        // control
                        moveToFullExtend();
                        extensionRetractionState = ExtensionRetractionStates.EXTENDED;
                        break;
                    case GO_TO_POSITION:
                        // the mechanism has been requested to move to a position. The motor has already
                        // been started in position control mode so we need to watch to determine
                        // the motor actually reaches the position
                        if (extensionRetractionMotor.isMotorStateComplete()) {
                            // the movement is finished and the motor stopped in the position, but
                            // it still has power applied to it. Stop the motor.
                            stopMechanism();
                            extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                        }

                        // check to make sure the extended limit switch has not been tripped. If it has
                        // then something went wrong or someone gave a bad motor command.
                        if (extendedLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. If the movement is supposed to be
                            // extending, then Stop the motor. Clear the command.
                            if (isMechanismMovementExtension()) {
                                stopMechanism();
                                extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                                extensionRetractionState = ExtensionRetractionStates.EXTENDED;
                            } else {
                                // the extended limit switch is pressed but the movement is supposed to be
                                // retract so do nothing. This allows retraction when the mechanism
                                // is fully extended. I.E. retraction is allowed when the mechanism
                                // is fully extended but more extension is not.
                            }
                        }

                        // check to make sure the retracted limit switch has not been tripped. If it has
                        // then something went wrong or someone gave a bad motor command.
                        if (retractedLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. If the movement is supposed to be
                            // retraction, then Stop the motor. Clear the command.
                            if (!isMechanismMovementExtension()) {
                                stopMechanism();
                                extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                                extensionRetractionState = ExtensionRetractionStates.RETRACTED;
                            } else {
                                // the retracted limit switch is pressed but the movement is supposed to be
                                // up so do nothing. This allows extension when the mechanism
                                // is fully retracted. I.E. extension is allowed when the mechanism
                                // is fully retracted but more retraction is not.
                            }
                        }
                        break;
                    case JOYSTICK:
                        processJoystick();
                        break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                }
                break;

            // this state does NOT mean that the mechanism is at the extended position
            // it means that the mechanism is moving to the extended position OR is at the extended
            // position
            case EXTENDED:
                switch (extensionRetractionCommand) {
                    case RESET:
                        // a reset can be requested at any time. Start the motor movement and change
                        // state
                        moveToFullRetract();
                        extensionRetractionState = ExtensionRetractionStates.RESET_MOVING_TO_RETRACTED;
                        break;
                    case GO_TO_RETRACTED:
                        // the mechanism has been requested to move to the retracted. The motor needs to be
                        // turned on and will run towards the retracted with just speed control, no position
                        // control
                        moveToFullRetract();
                        extensionRetractionState = ExtensionRetractionStates.RETRACTED;
                        break;
                    case GO_TO_EXTENDED:
                        // the mechanism has been sent to the extended without using a position command.
                        // It is just moving up until the motor is told to stop.
                        if (extendedLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. Stop the motor. Clear the command.
                            stopMechanism();
                            extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                        }
                        break;
                    case GO_TO_POSITION:
                        // the mechanism has been requested to move to a position. The motor has already
                        // been started in position control mode so we don't need to do anything
                        // with the motor. We just need to change extensionRetractionState.
                        extensionRetractionState = ExtensionRetractionStates.IN_BETWEEN;
                        break;
                    // the mechanism power is being set with a joystick. The mechanism must have hit the
                    // extension limit switch to be in this state
                    case JOYSTICK:
                        processJoystick();
                        break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                }
                break;
        }
        return extensionRetractionState;
    }

    /**
     * Write the state and command into the log file ONLY if the state has changed or the command has changed.
     *
     * @param extensionRetractionState
     * @param extensionRetractionCommand
     */
    private void logState(ExtensionRetractionStates extensionRetractionState, ExtensionRetractionCommands extensionRetractionCommand) {
        if (logFile != null && loggingOn) {
            if (extensionRetractionState != previousExtensionRetractionState || extensionRetractionCommand != previousExtensionRetractionCommand) {
                logFile.logData("Delivery Lift System", extensionRetractionState.toString(), extensionRetractionCommand.toString());
                previousExtensionRetractionState = extensionRetractionState;
                previousExtensionRetractionCommand = extensionRetractionCommand;
            }
        }
    }

    /**
     * A command that cannot be executed was received. Write the command to the log file and a
     * message saying this command has been ignored.
     *
     * @param extensionRetractionCommand
     */
    private void logIgnoreCommand(ExtensionRetractionCommands extensionRetractionCommand) {
        if (logFile != null && loggingOn) {
            logFile.logData("Ignoring command = ", extensionRetractionCommand.toString());
        }
    }

    /**
     * Has the mechanism arrived at the requested destination? If so log it and return true.
     *
     * @return
     */
    public boolean isMovementComplete() {
        if (extensionRetractionCommand == ExtensionRetractionCommands.NO_COMMAND) {
            // if the mechanism arrived message has already been logged don't log it again
            //NOTE if the mechanism is commanded to move to a position again, but is already at the
            // position, then the mechanism arrived may not be logged because arrivedAlreadyLogged
            // may not get set to false prior to the new movement command.
            if (logFile != null && loggingOn && !arrivedAlreadyLogged) {
                logFile.logData(mechanismName.toUpperCase() + " ARRIVED AT DESTINATION");
                arrivedAlreadyLogged = true;
            }
            return true;
        } else {
            arrivedAlreadyLogged = false;
            return false;
        }
    }

    /**
     * Process a joystick input.
     */
    private void processJoystick() {
        if (retractedLimitSwitch.isPressed()) {
            // if the mechanism is at the retracted, only allow it to move up
            if (joystickPower > 0) {
                extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                extensionRetractionMotor.setPower(joystickPower);
            } else {
                // the joystick power is either:
                // negative so the driver wants it to retract. But it is already fully retracted so we cannot retract more.
                // OR the joystick power is 0.
                // For both of these situations the motor power should be set to 0.
                extensionRetractionMotor.setPower(0);
                // and the command should be set to NO_COMMAND to indicate that the extension arm is not moving
                extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
            }
            extensionRetractionState = ExtensionRetractionStates.RETRACTED;
        } else {
            if (extendedLimitSwitch.isPressed()) {
                // if the mechanism is at the extended, only allow it to retract
                if (joystickPower < 0) {
                    extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    extensionRetractionMotor.setPower(joystickPower);
                } else {
                    // the joystick power is either:
                    // positive so the driver wants it to extend. But it is already fully extended so we cannot extend more.
                    // OR the joystick power is 0.
                    // For both of these situations the motor power should be set to 0.
                    extensionRetractionMotor.setPower(0);
                    // and the command should be set to NO_COMMAND to indicate that the extension arm is not moving
                    extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                }
                extensionRetractionState = ExtensionRetractionStates.EXTENDED;
            } else {
                // both limit switches are not pressed, allow it to move either way
                if (joystickPower != 0) {
                    extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    extensionRetractionMotor.setPower(joystickPower);
                } else {
                    // the joystick input is 0 so set the mechanism power to 0
                    extensionRetractionMotor.setPower(0);
                    // this fixes a bug: without resetting the command to NO_COMMAND, the command
                    // remains JOYSTICK. A call to isExtensionArmMovementComplete returns false even
                    // though the mechanism is not moving anymore (joystick command is 0). So any other
                    // code that checks for completion of the mechanism movement just sits and
                    // waits for isMovementComplete to return true. It never will. So
                    // we have to do this when the joystick power is 0:
                    extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                }
                extensionRetractionState = ExtensionRetractionStates.IN_BETWEEN;
            }
        }
    }

    /**
     * Add a telemetry command to display the state
     */
    public void displayState() {
        telemetry.addData(mechanismName.toUpperCase() + " State = ", extensionRetractionState.toString());
    }

    /**
     * Add a telemetry command to display the command to the mechanism
     */
    public void displayCommand() {
        telemetry.addData(mechanismName.toUpperCase() + " command = ", extensionRetractionCommand.toString());
    }


    //*********************************************************************************************]
    // tests for mechanism
    //**********************************************************************************************

    public void testMotorModeSwitch() {
        // reset the mechanism
        reset();
        while (!isMovementComplete()) {
            update();
        }

//        // move the mechanism 2 inches up and display
//        moveTwoInchesUp();
//        while (!isMovementComplete()) {
//            update();
//        }
//        telemetry.addLine("mechanism reset");
//        displayMotorEncoderValue();
//        displayPosition();
//        displayState();
//        telemetry.update();
//        delay(4000);

        // switch modes - hopefully the enocder value does not change
        extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        telemetry.addLine("mechanism motor mode switched to run without encoder");
        displayMotorEncoderValue();
        displayPosition();
        displayState();
        telemetry.update();
        delay(4000);

        // switch modes - hopefully the enocder value does not change
        telemetry.addLine("mechanism motor mode switched to run to position");
        extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        displayMotorEncoderValue();
        displayPosition();
        displayState();
        telemetry.update();
        delay(4000);

        // move the mechanism 2 inches up and display
//        goto5Inches();
//        while (!isMovementComplete()) {
//            update();
//        }
//        telemetry.addLine("moved to 5 inches");
//        displayMotorEncoderValue();
//        displayPosition();
//        displayState();
//        telemetry.update();
//        delay(4000);
    }

    public void testLimitSwitches() {
        if (retractedLimitSwitch.isPressed()) {
            telemetry.addLine("retracted limit switch pressed");
        } else {
            telemetry.addLine("retracted limit switch NOT pressed");
        }

        if (extendedLimitSwitch.isPressed()) {
            telemetry.addLine("extension limit switch pressed");
        } else {
            telemetry.addLine("extension limit switch NOT pressed");
        }
    }

    private void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
