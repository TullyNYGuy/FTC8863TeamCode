package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ExtensionRetractionMechanismNew {

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
        PERFORMING_PRE_RESET_ACTIONS, // actions that need to be run before mechanism can be moved to reset position
        MOVING_TO_RESET_POSITION, //
        PERFORMING_POST_RESET_ACTIONS, // actions that need to be run after the movement to the reset positon is complete
        RESET_COMPLETE, // reset movement and post reset actions are complete
        PERFORMING_PRE_RETRACTION_ACTIONS, // actions that need to be run before mechanism can be moved to retracted position
        RETRACTING, // in process of retracting
        PERFORMING_POST_RETRACTION_ACTIONS, // actions that need to be run after the movement to full retraction is complete
        FULLY_RETRACTED, // fully retracted
        PERFORMING_PRE_EXTENSION_ACTIONS, // actions that need to be run before mechanism can be moved extended position
        EXTENDING, // in process of extending
        PERFORMING_POST_EXTENSION_ACTIONS, // actions that need to be run after the movement to full extension is complete
        FULLY_EXTENDED, // fully extended
        MOVING_TO_POSITION, // moving to a specified position
        AT_POSITION, // arrived at the specified position
        JOYSTICK // under joystick control
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private DcMotor8863 extensionRetractionMotor;

    // null is shown for emphasis. Any object is null until is it created.
    private Switch retractedLimitSwitch = null;
    private Switch extendedLimitSwitch = null;

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
    private double moveToPositionPower = 0;

    /**
     * The power to use when resetting the mechanism.
     */
    private double resetPower = -1.0;

    public double getResetPower() {
        return resetPower;
    }

    public void setResetPower(double resetPower) {
        this.resetPower = resetPower;
    }

    /**
     * The power to use when retracting the mechanism
     */
    private double retractionPower = -1.0;

    public double getRetractionPower() {
        return retractionPower;
    }

    public void setRetractionPower(double retractionPower) {
        this.retractionPower = retractionPower;
    }

    /**
     * The power to use when extending the mechanism
     */
    private double extensionPower = +1.0;

    public double getExtensionPower() {
        return extensionPower;
    }

    public void setExtensionPower(double extensionPower) {
        this.extensionPower = extensionPower;
    }

    private double extensionRetractionPower = 0;
    private double extensionRetractionSpeed = .5;

    /**
     * You can set a position that is the limit for retraction. Do this when there is no retraction
     * limit switch installed. This value is used by the state machine to see if the mechanism has
     * moved to the retracted position. With no limit switch, this value defines the retracted
     * position. Note that is it of type Double (the class) rather than double (the primitive). This
     * allows it to be set to null. If the value is null then it is assumed that no retrationPosition
     * has been set.
     */
    private Double retractionPosition = null;

    public Double getRetractionPosition() {
        return retractionPosition;
    }

    public void setRetractionPosition(Double retractionPosition) {
        this.retractionPosition = retractionPosition;
    }

    /**
     * You can set a position that is the limit for extension. Do this when there is no extension
     * limit switch installed. This value is used by the state machine to see if the mechanism has
     * moved to the retracted position. With no limit switch, this value defines the retracted
     * position. Note that is it of type Double (the class) rather than double (the primitive). This
     * allows it to be set to null. If the value is null then it is assumed that no retrationPosition
     * has been set.
     */
    private Double extensionPosition = null;

    public Double getExtensionPosition() {
        return extensionPosition;
    }

    public void setExtensionPosition(Double extensionPosition) {
        this.extensionPosition = extensionPosition;
    }

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
        // the mechanism is AT_POSITION so any command sent to the mechanism will run.
        extensionRetractionState = ExtensionRetractionStates.AT_POSITION;
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

    private boolean actionsToCompleteResetFinished = false;

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
    public ExtensionRetractionMechanismNew(HardwareMap hardwareMap, Telemetry telemetry, String mechanismName, String motorName,
                                           String extensionLimitSwitchName, String retractionLimitSwitchName,
                                           DcMotor8863.MotorType motorType, double movementPerRevolution) {
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
        extensionRetractionState = ExtensionRetractionStates.MOVING_TO_RESET_POSITION;
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

    //**********************************************************************************************
    // mechanism methods - These methods are called by the states in the state machine
    //**********************************************************************************************

    //**********************************************************************************************
    // reset methods - These methods are called by the states in the state machine
    //**********************************************************************************************

    /**
     * This method will be executed before a reset starts. You need to write custom code for your
     * specific mechanism for the actions you want to perform. This gives you the opportunity to do
     * things that must happen before this mechanism moves to a reset position. For example, if you
     * have another mechanism attached to this one, and it has to turn a certain way in order
     * to avoid a collision with part of the robot while this mechanism moves to the reset
     * position, you can put that turn in this method.
     * This is optional. If you don't have any actions, just leave this method blank and return true
     * in arePreResetActionsComplete() in all cases.
     */
    private void performPreResetActions() {
        // put your actions that need to be performed here
    }

    /**
     * This method returns true when all the pre reset actions have completed. You need to write
     * custom code for your mechanism in this method. If you don't have
     * any actions to perform before a movement to a reset position, just return true.
     *
     * @return true when all pre reset actions are complete.
     */
    private boolean arePreResetActionsComplete() {
        // put your custom code to check whether the actions are complete here
        log("Pre reset actions complete " + mechanismName);
        return true;
    }

    private void moveToReset() {
        extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extensionRetractionMotor.setPower(resetPower);
        log("Resetting mechanism " + mechanismName);
    }

    private boolean isMoveToResetComplete() {
        // your method of determining whether the movement to the reset is complete must be
        // coded here
        log("Reset movement complete " + mechanismName);
        return true;
    }

    private void actionsToCompleteResetMovement() {
        // your actions to complete the reset movement must be coded here. These are suggested
        // actions.
        stopMechanism();
        extensionRetractionMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /**
     * This method will be executed after a reset movement completes. You need to write custom code
     * for your specific mechanism for the actions you want to perform. This gives you the
     * opportunity to do things that must happen after this mechanism moves to a reset position.
     * For example, if you have another mechanism attached to this one, and it has to turn a certain
     * way in order after the reset movement is complete, you can put that turn in this method.
     * This is optional. If you don't have any actions, just leave this method blank and return true
     * in arePostResetActionsComplete() in all cases.
     */
    private void performPostResetActions() {
        // put your actions that need to be performed here
    }

    /**
     * This method returns true when all the post reset actions have completed. You need to write
     * custom code for your mechanism in this method. If you don't have
     * any actions to perform after a movement to a reset position, just return true.
     *
     * @return true when all post reset actions are complete.
     */
    private boolean arePostResetActionsComplete() {
        // put your custom code to check whether the actions are complete here
        log("Post reset actions complete " + mechanismName);
        return true;
    }

    //**********************************************************************************************
    // retract methods - These methods are called by the states in the state machine
    //**********************************************************************************************

    /**
     * This method will be executed before a retract starts. You need to write custom code for your
     * specific mechanism for the actions you want to perform. This gives you the opportunity to do
     * things that must happen before this mechanism moves to a retract position. For example, if you
     * have another mechanism attached to this one, and it has to turn a certain way in order
     * to avoid a collision with part of the robot while this mechanism moves to the retract
     * position, you can put that turn in this method.
     * This is optional. If you don't have any actions, just leave this method blank and return true
     * in arePreResetActionsComplete() in all cases.
     */
    private void performPreRetractActions() {
        // put your actions that need to be performed here
    }

    /**
     * This method returns true when all the pre retract actions have completed. You need to write
     * custom code for your mechanism in this method. If you don't have
     * any actions to perform before a movement to a retract position, just return true.
     *
     * @return true when all pre retract actions are complete.
     */
    private boolean arePreRetractActionsComplete() {
        // put your custom code to check whether the actions are complete here
        log("Pre retract actions complete " + mechanismName);
        return true;
    }

    /**
     * This method is only called by the state machine. Cause the mechanism to start to move to a
     * fully retracted position.
     */
    private void moveToFullRetract() {
        // when the mechanism retracts you may want to do something with whatever is attached to it.
        extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extensionRetractionMotor.setPower(retractionPower);
    }

    /**
     * This method checks to see if the movement to a fully retracted position is complete.
     *
     * @return true if complete
     */
    private boolean isMoveToRetractComplete() {
        // your method of determining whether the movement to the retract is complete must be
        // coded here. This code is suggested but you can override it if your situation is different.
        log("Retract movement complete " + mechanismName);
        return isRetractionLimitReached();
    }

    /**
     * Run the actions that need to be run to complete the retraction movement. The code here is
     * a suggestion. You can override it to fit your specific situation.
     */
    private void actionsToCompleteRetractMovement() {
        // your actions to complete the retract movement must be coded here. These are suggested
        // actions. You can override these if you need to.
        stopMechanism();
    }

    /**
     * This method will be executed after a retract movement completes. You need to write custom code
     * for your specific mechanism for the actions you want to perform. This gives you the
     * opportunity to do things that must happen after this mechanism moves to a retract position.
     * For example, if you have another mechanism attached to this one, and it has to turn a certain
     * way in order after the retract movement is complete, you can put that turn in this method.
     * This is optional. If you don't have any actions, just leave this method blank and return true
     * in arePostResetActionsComplete() in all cases.
     */
    private void performPostRetractActions() {
        // put your actions that need to be performed here
    }

    /**
     * This method returns true when all the post retract actions have completed. You need to write
     * custom code for your mechanism in this method. If you don't have
     * any actions to perform after a movement to a retract position, just return true.
     *
     * @return true when all post retract actions are complete.
     */
    private boolean arePostRetractActionsComplete() {
        // put your custom code to check whether the actions are complete here
        log("Post retract actions complete " + mechanismName);
        return true;
    }

    /**
     * This method is the default for checking to see if the mechanism is in the retracted position.
     * You can override it if you have a different method. Note that the retraction limit position
     * is assumed to be the least value possible for all of the possible positions of the mechanism.
     *
     * @return true if EITHER extension limit switch is pressed OR if current position is equal to
     * or less than the extension position.
     */
    private boolean isRetractionLimitReached() {
        boolean retractionReached = false;
        // if a limit switch is not present, the retractedLimitSwitch object will be null.
        // Only check it if it is present.
        if (retractedLimitSwitch != null) {
            if (retractedLimitSwitch.isPressed()) {
                retractionReached = true;
            }
        }
        // If a retraction limit position has been set, it will not be null. If none has been set,
        // its value will be null and the check is skipped. Note that the retractionPosition is a
        // Double (class) not a double (primitive).
        if (retractionPosition != null) {
            if (extensionRetractionMotor.getPositionInTermsOfAttachment() <= retractionPosition) {
                retractionReached = true;
            }
        }
        return retractionReached;
    }

    //**********************************************************************************************
    // extend methods - These methods are called by the states in the state machine
    //**********************************************************************************************

    /**
     * This method will be executed before a extend starts. You need to write custom code for your
     * specific mechanism for the actions you want to perform. This gives you the opportunity to do
     * things that must happen before this mechanism moves to a extend position. For example, if you
     * have another mechanism attached to this one, and it has to turn a certain way in order
     * to avoid a collision with part of the robot while this mechanism moves to the extend
     * position, you can put that turn in this method.
     * This is optional. If you don't have any actions, just leave this method blank and return true
     * in arePreResetActionsComplete() in all cases.
     */
    private void performPreExtendActions() {
        // put your actions that need to be performed here
    }

    /**
     * This method returns true when all the pre extend actions have completed. You need to write
     * custom code for your mechanism in this method. If you don't have
     * any actions to perform before a movement to a extend position, just return true.
     *
     * @return true when all pre extend actions are complete.
     */
    private boolean arePreExtendActionsComplete() {
        // put your custom code to check whether the actions are complete here
        log("Pre extend actions complete " + mechanismName);
        return true;
    }

    /**
     * This method is only called by the state machine. Cause the mechanism to start to move to a
     * fully extended position.
     */
    private void moveToFullExtend() {
        // when the mechanism extends you may want to do something with whatever is attached to it.
        extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extensionRetractionMotor.setPower(extensionPower);
    }

    /**
     * This method checks to see if the movement to a fully extended position is complete.
     *
     * @return true if complete
     */
    private boolean isMoveToExtendComplete() {
        // your method of determining whether the movement to the extend is complete must be
        // coded here. This code is suggested but you can override it if your situation is different.
        log("Extend movement complete " + mechanismName);
        return isExtensionLimitReached();
    }

    /**
     * Run the actions that need to be run to complete the extension movement. The code here is
     * a suggestion. You can override it to fit your specific situation.
     */
    private void actionsToCompleteExtendMovement() {
        // your actions to complete the extend movement must be coded here. These are suggested
        // actions. You can override these if you need to.
        stopMechanism();
    }

    /**
     * This method will be executed after a extend movement completes. You need to write custom code
     * for your specific mechanism for the actions you want to perform. This gives you the
     * opportunity to do things that must happen after this mechanism moves to a extend position.
     * For example, if you have another mechanism attached to this one, and it has to turn a certain
     * way in order after the extend movement is complete, you can put that turn in this method.
     * This is optional. If you don't have any actions, just leave this method blank and return true
     * in arePostResetActionsComplete() in all cases.
     */
    private void performPostExtendActions() {
        // put your actions that need to be performed here
    }

    /**
     * This method returns true when all the post extend actions have completed. You need to write
     * custom code for your mechanism in this method. If you don't have
     * any actions to perform after a movement to a extend position, just return true.
     *
     * @return true when all post extend actions are complete.
     */
    private boolean arePostExtendActionsComplete() {
        // put your custom code to check whether the actions are complete here
        log("Post extend actions complete " + mechanismName);
        return true;
    }

    /**
     * This method is the default for checking to see if the mechanism is in the extended position.
     * You can override it if you have a different method. Note that the extension limit position
     * is assumed to be the greatest value possible for all of the possible positions of the mechanism.
     *
     * @return true if EITHER extension limit switch is pressed OR if current position is equal to
     * or greater than the extension position.
     */
    private boolean isExtensionLimitReached() {
        boolean extensionReached = false;
        // if a limit switch is not present, the retractedLimitSwitch object will be null.
        // Only check it if it is present.
        if (extendedLimitSwitch != null) {
            if (extendedLimitSwitch.isPressed()) {
                extensionReached = true;
            }
        }
        // If a extension limit position has been set, it will not be null. If none has been set,
        // its value will be null and the check is skipped. Note that the extensionPosition is a
        // Double (class) not a double (primitive).
        if (extensionPosition != null) {
            if (extensionRetractionMotor.getPositionInTermsOfAttachment() >= extensionPosition) {
                extensionReached = true;
            }
        }
        return extensionReached;
    }

    //**********************************************************************************************
    // Move to position methods - These methods are called by the states in the state machine
    //**********************************************************************************************

    private void moveToPosition() {
        extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        extensionRetractionMotor.moveToPosition(moveToPositionPower, desiredPosition, DcMotor8863.FinishBehavior.HOLD);
    }

    private boolean isMoveToPositionComplete() {
        return extensionRetractionMotor.isMotorStateComplete();
    }

    //**********************************************************************************************
    // Joystick control methods - These methods are called by the states in the state machine
    //**********************************************************************************************

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


    //**********************************************************************************************
    // supporting methods
    //**********************************************************************************************

    /**
     * Cause the mechanism to stop moving.
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

    /**
     * Write the state and command into the log file ONLY if the state has changed or the command has changed.
     *
     * @param extensionRetractionState
     * @param extensionRetractionCommand
     */
    private void logState(ExtensionRetractionStates extensionRetractionState, ExtensionRetractionCommands extensionRetractionCommand) {
        if (logFile != null && loggingOn) {
            if (extensionRetractionState != previousExtensionRetractionState || extensionRetractionCommand != previousExtensionRetractionCommand) {
                logFile.logData(mechanismName, extensionRetractionState.toString(), extensionRetractionCommand.toString());
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
     * Log when the mechanism has arrived at the desired desination.
     */
    private void logArrivedAtDestination() {
        if (logFile != null && loggingOn) {
            logFile.logData(mechanismName.toUpperCase() + " ARRIVED AT DESTINATION");
        }
    }

    //*********************************************************************************************]
    // mechanism state machine
    //**********************************************************************************************

    public ExtensionRetractionStates update() {

        // update the state machine for the motor
        DcMotor8863.MotorState motorState = extensionRetractionMotor.update();
        logState(extensionRetractionState, extensionRetractionCommand);

        switch (extensionRetractionState) {

            // -------------------------
            //   RESET STATES
            //--------------------------

            // You can perform actions before the reset movement starts. For example, if you have
            // another mechanism attached to this one, and it has to turn a certain way in order
            // to avoid a collision with part of the robot while this mechanism moves to the reset
            // position, you can perform that turn and then watch for its completion here. This is
            // optional. If you don't have any actions, just make arePreResetActionsComplete() return
            // true in all cases.
            case PERFORMING_PRE_RESET_ACTIONS:
                switch (extensionRetractionCommand) {
                    case RESET:
                        if (arePreResetActionsComplete()) {
                            // pre reset actions are complete, start the movement to reset position
                            moveToReset();
                            extensionRetractionState = ExtensionRetractionStates.MOVING_TO_RESET_POSITION;
                            extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        } else {
                            // reset not complete yet, keep watching
                        }
                        break;

                    // A command can be issued at any time. Since we want the reset to complete
                    // before any other commands are recognized, all other commands are ignored
                    // when a reset is issued. Basically force the command back to a reset command.
                    case GO_TO_RETRACTED:
                    case GO_TO_EXTENDED:
                    case GO_TO_POSITION:
                    case JOYSTICK:
                        logIgnoreCommand(extensionRetractionCommand);
                        extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }
                break;

            // In this state the mechanism is moving to the reset position.
            case MOVING_TO_RESET_POSITION:
                switch (extensionRetractionCommand) {
                    case RESET:
                        if (isMoveToResetComplete()) {
                            logArrivedAtDestination();
                            // movement to the reset position is complete, start the post reset actions
                            performPostResetActions();
                            extensionRetractionState = ExtensionRetractionStates.PERFORMING_POST_RESET_ACTIONS;
                            extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        } else {
                            // reset not complete yet, keep watching
                        }
                        break;

                    // A command can be issued at any time. Since we want the reset to complete
                    // before any other commands are recognized, all other commands are ignored
                    // when a reset is issued. Basically force the command back to a reset command.
                    case GO_TO_RETRACTED:
                    case GO_TO_EXTENDED:
                    case GO_TO_POSITION:
                    case JOYSTICK:
                        logIgnoreCommand(extensionRetractionCommand);
                        extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }
                break;

            // You can perform actions after the reset movement completes. For example, if you have
            // another mechanism attached to this one, and it has to turn a certain way after the
            // reset movement completes, you can perform that turn and then watch for its
            // completion here. This is optional. If you don't have any actions, just make
            // arePostResetActionsComplete() return true in all cases.
            case PERFORMING_POST_RESET_ACTIONS:
                switch (extensionRetractionCommand) {
                    case RESET:
                        if (arePostResetActionsComplete()) {
                            // post retraction actions are complete
                            extensionRetractionState = ExtensionRetractionStates.RESET_COMPLETE;
                            extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        } else {
                            // reset not complete yet, keep watching
                        }
                        break;

                    // A command can be issued at any time. Since we want the reset to complete
                    // before any other commands are recognized, all other commands are ignored
                    // when a reset is issued. Basically force the command back to a reset command.
                    case GO_TO_RETRACTED:
                    case GO_TO_EXTENDED:
                    case GO_TO_POSITION:
                    case JOYSTICK:
                        logIgnoreCommand(extensionRetractionCommand);
                        extensionRetractionCommand = ExtensionRetractionCommands.RESET;
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }
                break;

            // This state means that the pre reset actions are done, the movement to the reset
            // position is done, the post reset actions are done, and the whole reset is done.
            case RESET_COMPLETE:
                switch (extensionRetractionCommand) {
                    case RESET:
                        // the command gets set to NO_COMMAND since nothing will happen until a new
                        // command is received.
                        extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                        break;
                    // The mechansim is waiting for another command to be received. When one is
                    // received after a reset, the command is processed here.
                    case GO_TO_RETRACTED:
                        performPreRetractActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RETRACTION_ACTIONS;
                        break;
                    case GO_TO_EXTENDED:
                        performPreExtendActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_EXTENSION_ACTIONS;
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
                        // do nothing, just wait for a new command
                        break;
                }
                break;

            // -------------------------
            //   RETRACTION STATES
            //--------------------------

            // You can perform actions before the retraction movement starts. For example, if you have
            // another mechanism attached to this one, and it has to turn a certain way in order
            // to avoid a collision with part of the robot while this mechanism moves to the retraction
            // position, you can perform that turn and then watch for its completion here. This is
            // optional. If you don't have any actions, just make arePreResetActionsComplete() return
            // true in all cases.
            case PERFORMING_PRE_RETRACTION_ACTIONS:
                switch (extensionRetractionCommand) {
                    // The retraction has been interrupted by a reset command. Setup for a reset.
                    case RESET:
                        performPreResetActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RESET_ACTIONS;
                        break;
                    case GO_TO_RETRACTED:
                        if (arePreRetractActionsComplete()) {
                            // pre retraction actions are complete, start the movement to retracted position
                            moveToFullRetract();
                            extensionRetractionState = ExtensionRetractionStates.RETRACTING;
                        } else {
                            // pre retraction actions not complete yet, keep watching
                        }
                        break;
                    // The retraction command can be interrupted by another command.
                    case GO_TO_EXTENDED:
                        performPreExtendActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_EXTENSION_ACTIONS;
                        break;
                    case GO_TO_POSITION:
                        // note that this command is interrupting the retraction. So if there are pre retraction
                        // actions being run they are interrupted too. Those actions may put whatever they are
                        // operating on into an unkown state
                        moveToPosition();
                        extensionRetractionState = ExtensionRetractionStates.MOVING_TO_POSITION;
                        break;
                    case JOYSTICK:
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }
                break;

            // In this state the mechanism is moving to the retracted position.
            case RETRACTING:
                switch (extensionRetractionCommand) {
                    // The retraction has been interrupted by a reset command. Setup for a reset.
                    case RESET:
                        performPreResetActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RESET_ACTIONS;
                        break;
                    case GO_TO_RETRACTED:
                        if (isMoveToRetractComplete()) {
                            logArrivedAtDestination();
                            // movement to the retraction position is complete, start the post retraction actions
                            performPostRetractActions();
                            extensionRetractionState = ExtensionRetractionStates.PERFORMING_POST_RETRACTION_ACTIONS;
                        } else {
                            // retraction not complete yet, keep watching
                        }
                        break;
                    case GO_TO_EXTENDED:
                        performPreExtendActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_EXTENSION_ACTIONS;
                        break;
                    case GO_TO_POSITION:
                        // The position and the power for the move to position have already been
                        // stored in private variables during the processing of the user's method
                        // call.
                        moveToPosition();
                        extensionRetractionState = ExtensionRetractionStates.MOVING_TO_POSITION;
                        break;
                    case JOYSTICK:
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }
                break;

            // You can perform actions after the retraction movement completes. For example, if you have
            // another mechanism attached to this one, and it has to turn a certain way after the
            // retraction movement completes, you can perform that turn and then watch for its
            // completion here. This is optional. If you don't have any actions, just make
            // arePostResetActionsComplete() return true in all cases.
            case PERFORMING_POST_RETRACTION_ACTIONS:
                switch (extensionRetractionCommand) {
                    // The retraction has been interrupted by a reset command. Setup for a reset.
                    case RESET:
                        performPreResetActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RESET_ACTIONS;
                        break;
                    case GO_TO_RETRACTED:
                        if (arePostRetractActionsComplete()) {
                            // post retraction actions are complete
                            extensionRetractionState = ExtensionRetractionStates.FULLY_RETRACTED;
                            // the GO_TO_RETRACTED command is complete. Clear the command.
                            extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                        } else {
                            // post retraction action not complete yet, keep watching
                        }
                        break;
                    case GO_TO_EXTENDED:
                        performPreExtendActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_EXTENSION_ACTIONS;
                        break;
                    case GO_TO_POSITION:
                        // note that this command is interrupting the retraction. So if there are post retraction
                        // actions being run they are interrupted too. Those actions may put whatever they are
                        // operating on into an unkown state
                        // The position and the power for the move to position have already been
                        // stored in private variables during the processing of the user's method
                        // call.
                        moveToPosition();
                        extensionRetractionState = ExtensionRetractionStates.MOVING_TO_POSITION;
                    case JOYSTICK:
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }
                break;

            // This state means that the pre retraction actions are done, the movement to the retraction
            // position is done, the post retraction actions are done, and the whole retraction is done.
            case FULLY_RETRACTED:
                switch (extensionRetractionCommand) {
                    // A reset command has been received. Setup for a reset.
                    case RESET:
                        performPreResetActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RESET_ACTIONS;
                        break;
                    case GO_TO_RETRACTED:
                        // The mechanism is already retracted. This command is not relevant. Ignore
                        // this command.
                        // The command gets set to NO_COMMAND since nothing will happen until a new
                        // command is received.
                        logIgnoreCommand(ExtensionRetractionCommands.GO_TO_RETRACTED);
                        extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                        break;
                    case GO_TO_EXTENDED:
                        performPreExtendActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_EXTENSION_ACTIONS;
                        break;
                    case GO_TO_POSITION:
                        // The position and the power for the move to position have already been
                        // stored in private variables during the processing of the user's method
                        // call.
                        moveToPosition();
                        extensionRetractionState = ExtensionRetractionStates.MOVING_TO_POSITION;
                        break;
                    case JOYSTICK:
                        break;
                    case NO_COMMAND:
                        // do nothing, just wait for a new command
                        break;
                }
                break;

            // -------------------------
            //   EXTENSION STATES
            //--------------------------

            // -------------------------
            //   MOVING TO POSITION STATES
            //--------------------------

            // this state is for when the mechanism is located somewhere in between the extended and retracted
            // and is in the process of moving to a position
            case MOVING_TO_POSITION:
                switch (extensionRetractionCommand) {
                    case RESET:
                        // a reset can be requested at any time.
                        // reset the mechanism starts with doing the pre reset actions
                        performPreResetActions();
                        // Change to resetting state
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RESET_ACTIONS;
                        break;
                    // movement to a position can be interrupted by a command to fully extend or
                    // fully retract
                    case GO_TO_RETRACTED:
                        performPreRetractActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RETRACTION_ACTIONS;
                        break;
                    case GO_TO_EXTENDED:
                        performPreExtendActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_EXTENSION_ACTIONS;
                        break;
                    case GO_TO_POSITION:
                        // the mechanism has been requested to move to a position. The motor has already
                        // been started in position control mode so we need to watch to determine
                        // the motor actually reaches the position
                        if (isMoveToPositionComplete()) {
                            logArrivedAtDestination();
                            // the movement to the position is complete. But the power to the motor
                            // cannot be removed. The power is needed because the motor is holding
                            // the position and may need to act against a force (like gravity) to
                            // hold position.
                            extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                            extensionRetractionState = ExtensionRetractionStates.AT_POSITION;
                        } else {
                            // keep watching the move to position to see when it completes
                        }

                        // check to make sure the extended limit has not been reached. If it has
                        // then something went wrong or someone gave a bad motor command.
                        if (isExtensionLimitReached()) {
                            // the extension limit has been reached. This is probably not intentional.
                            // But the movement has to be stopped in order to protect the mechanism
                            // from damage. Clear the command.
                            stopMechanism();
                            extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                            extensionRetractionState = ExtensionRetractionStates.FULLY_EXTENDED;
                        }

                        // check to make sure the retracted limit switch has not been tripped. If it has
                        // then something went wrong or someone gave a bad motor command.
                        if (isRetractionLimitReached()) {
                            // the retraction limit has been reached. This is probably not intentional.
                            // But the movement has to be stopped in order to protect the mechanism
                            // from damage. Clear the command.
                            stopMechanism();
                            extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                            extensionRetractionState = ExtensionRetractionStates.FULLY_RETRACTED;
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

            // this state is for when the mechanism has completed a move to a position
            case AT_POSITION:
                switch (extensionRetractionCommand) {
                    case RESET:
                        // a reset can be requested at any time.
                        // reset the mechanism starts with doing the pre reset actions
                        performPreResetActions();
                        // Change to resetting state
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RESET_ACTIONS;
                        break;
                    // movement to a position can be interrupted by a command to fully extend or
                    // fully retract
                    case GO_TO_RETRACTED:
                        performPreResetActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RETRACTION_ACTIONS;
                        break;
                    case GO_TO_EXTENDED:
                        performPreExtendActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_EXTENSION_ACTIONS;
                        break;
                    case GO_TO_POSITION:
                        // When the mechanism arrives at the desired position, the command is set to
                        // NO_COMMAND. So if this GO_TO_POSITION command is received, then this is a
                        // new move to position command. I.E. moving to a position from another
                        // position.
                        // The position and the power for the move to position have already been
                        // stored in private variables during the processing of the user's method
                        // call.
                        moveToPosition();
                        extensionRetractionState = ExtensionRetractionStates.MOVING_TO_POSITION;
                        break;
                    case JOYSTICK:
                        processJoystick();
                        break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                }
                break;

            // -------------------------
            //   JOYSTICK CONTROL
            //--------------------------

            case JOYSTICK:
                switch (extensionRetractionCommand) {
                    case RESET:
                        // a reset can be requested at any time.
                        // reset the mechanism starts with doing the pre reset actions
                        performPreResetActions();
                        // Change to resetting state
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RESET_ACTIONS;
                        break;
                    // movement to a position can be interrupted by a command to fully extend or
                    // fully retract
                    case GO_TO_RETRACTED:
                        performPreResetActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RETRACTION_ACTIONS;
                        break;
                    case GO_TO_EXTENDED:
                        performPreExtendActions();
                        extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_EXTENSION_ACTIONS;
                        break;
                    case GO_TO_POSITION:
                        // The position and the power for the move to position have already been
                        // stored in private variables during the processing of the user's method
                        // call.
                        moveToPosition();
                        extensionRetractionState = ExtensionRetractionStates.MOVING_TO_POSITION;
                        break;
                    case JOYSTICK:
                        processJoystick();
                        break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                break;
        }
        return extensionRetractionState;
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
