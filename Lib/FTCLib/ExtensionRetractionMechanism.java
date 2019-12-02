package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ExtensionRetractionMechanism {

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
        START_RESET_SEQUENCE, //
        PERFORMING_PRE_RESET_ACTIONS, // actions that need to be run before mechanism can be moved to reset position
        MOVING_TO_RESET_POSITION, //
        PERFORMING_POST_RESET_ACTIONS, // actions that need to be run after the movement to the reset positon is complete
        RESET_COMPLETE, // reset movement and post reset actions are complete
        START_RETRACTION_SEQUENCE, //
        PERFORMING_PRE_RETRACTION_ACTIONS, // actions that need to be run before mechanism can be moved to retracted position
        RETRACTING, // in process of retracting
        PERFORMING_POST_RETRACTION_ACTIONS, // actions that need to be run after the movement to full retraction is complete
        FULLY_RETRACTED, // fully retracted
        START_EXTENSION_SEQUENCE, //
        PERFORMING_PRE_EXTENSION_ACTIONS, // actions that need to be run before mechanism can be moved extended position
        EXTENDING, // in process of extending
        PERFORMING_POST_EXTENSION_ACTIONS, // actions that need to be run after the movement to full extension is complete
        FULLY_EXTENDED, // fully extended
        START_GO_TO_POSITION, //
        MOVING_TO_POSITION, // moving to a specified position
        AT_POSITION, // arrived at the specified position
        START_JOYSTICK, //
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

    /**
     * The motor driving the mechanism can actively hold a postition at the end of a movement. This
     * requires power from the motor to hold position. Or it cn just float. In other words, no power
     * will be applied to the mechanism when it finishes moving. So if a force is applied to the
     * mechanism, such as gravity, the mechansim may not hold position.
     */
    private DcMotor8863.FinishBehavior finishBehavior = DcMotor8863.FinishBehavior.HOLD;

    public DcMotor8863.FinishBehavior getFinishBehavior() {
        return finishBehavior;
    }

    public void setFinishBehavior(DcMotor8863.FinishBehavior finishBehavior) {
        this.finishBehavior = finishBehavior;
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
        this.extensionRetractionPower = .2;
        // normally the mechanism has to be reset before it will accept any commands.
        // This forces it to locate its 0 position before any other commands will
        // run. But when debugging you may not want the mechanism to have to reset before
        // running any commands. So if the mechanism is in debug mode, force the state machine to think
        // the mechanism is AT_POSITION so any command sent to the mechanism will run.
        extensionRetractionState = ExtensionRetractionStates.AT_POSITION;
    }

    public void disableDebugMode() {
        this.debugMode = false;
        this.extensionRetractionPower = .5;
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

    /**
     * Use this constructor when the mechanism has retraction and extension limit switches.
     *
     * @param hardwareMap               the hardware map that is provided in the opmode
     * @param telemetry                 the telemetry object this is provided in the opmode
     * @param mechanismName             the name of this mechanism. Will be used in the log file.
     * @param extensionLimitSwitchName  the name of the switch you setup in the config of the phone
     * @param retractionLimitSwitchName the name of the switch you setup in the config of the phone
     * @param motorName                 the name of the motor you setup in the config of the phone
     * @param motorType                 the type of motor
     * @param movementPerRevolution     the distance this mechanism moves for each revolution of the motor
     *                                  Note that the units can be anything. But you must always use
     *                                  the same units when working with the mechanism.
     */
    public ExtensionRetractionMechanism(HardwareMap hardwareMap, Telemetry telemetry, String mechanismName,
                                        String extensionLimitSwitchName, String retractionLimitSwitchName,
                                        String motorName, DcMotor8863.MotorType motorType, double movementPerRevolution) {
        // set all of the private variables using the parameters passed into the constructor
        createExtensionRetractionMechanismCommonCommands(hardwareMap, telemetry, mechanismName, motorName, motorType, movementPerRevolution);

        // create the limit switches
        retractedLimitSwitch = new Switch(hardwareMap, retractionLimitSwitchName, Switch.SwitchType.NORMALLY_OPEN);
        extendedLimitSwitch = new Switch(hardwareMap, extensionLimitSwitchName, Switch.SwitchType.NORMALLY_OPEN);
    }

    /**
     * Use this constructor when the mechanism does not have retraction and extension limit switches.
     * Instead you give a position for the extension and retraction limits. Note this is risky since
     * the zero position is set by where the mechanism is located when it is created.
     *
     * @param hardwareMap           the hardware map that is provided in the opmode
     * @param telemetry             the telemetry object this is provided in the opmode
     * @param mechanismName         the name of this mechanism. Will be used in the log file.
     * @param retractionPosition    the name of the switch you setup in the config of the phone
     * @param extensionPosition     the name of the switch you setup in the config of the phone
     * @param motorName             the name of the motor you setup in the config of the phone
     * @param motorType             the type of motor
     * @param movementPerRevolution the distance this mechanism moves for each revolution of the motor
     *                              Note that the units can be anything. But you must always use
     *                              the same units when working with the mechanism.
     */
    public ExtensionRetractionMechanism(HardwareMap hardwareMap, Telemetry telemetry, String mechanismName,
                                        Double retractionPosition, Double extensionPosition,
                                        String motorName, DcMotor8863.MotorType motorType, double movementPerRevolution) {
        // set all of the private variables using the parameters passed into the constructor
        createExtensionRetractionMechanismCommonCommands(hardwareMap, telemetry, mechanismName, motorName, motorType, movementPerRevolution);

        this.retractionPosition = retractionPosition;
        this.extensionPosition = extensionPosition;
    }

    private void createExtensionRetractionMechanismCommonCommands(HardwareMap hardwareMap, Telemetry telemetry, String mechanismName,
                                                                  String motorName, DcMotor8863.MotorType motorType, double movementPerRevolution) {
        // set all of the private variables using the parameters passed into the constructor
        this.motorName = motorName;
        this.motorType = motorType;
        this.extensionLimitSwitchName = extensionLimitSwitchName;
        this.retractionLimitSwitchName = retractionLimitSwitchName;
        this.movementPerRevolution = movementPerRevolution;
        this.mechanismName = mechanismName;
        this.telemetry = telemetry;

        // create the motor
        extensionRetractionMotor = new DcMotor8863(motorName, hardwareMap, telemetry);
        extensionRetractionMotor.setMotorType(motorType);
        extensionRetractionMotor.setMovementPerRev(movementPerRevolution);

        // set the initial state of the state machine
        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
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
     * initialization. NOte that this method blocks any other method from running due to the while
     * loop. If this is a problem, we should look into starting a new thread for it.
     */
    public void init() {
        log(mechanismName + "Extension retraction system initializing");
        if (!isDebugMode()) {
            if (!isResetComplete()) {
                reset();
            }
            while (!isResetComplete()) {
                update();
            }
        } else {
            // in debug mode no reset occurs
        }
    }

    /**
     * This method is called as part of the shutdown sequence of the robot. You will need to fill
     * in your code here. The code that is here is a suggestion. It also blocks any other method
     * from running due to the while loop. If this is a problem we should look into starting it on a
     * separate thread.
     */
    public void shutdown() {
        reset();
        while (!isResetComplete()) {
            update();
        }
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
        goToPosition(homePosition, 1);
    }

    /**
     * Move the mechanism to a position using PID control to control the position.
     *
     * @param position            the desired position in units that make sense for this mechanism
     * @param moveToPositionPower the power to use during the movement
     */
    public void goToPosition(double position, double moveToPositionPower) {
        log("COMMANDED " + mechanismName.toUpperCase() + " TO GO TO POSITION " + position);
        // set the properties so they can be used later
        this.desiredPosition = position;
        this.moveToPositionPower = moveToPositionPower;
        // the next execution of the state machine will pick up this new command and execute it
        extensionRetractionCommand = ExtensionRetractionCommands.GO_TO_POSITION;
    }


    /**
     * Move to a position based on zero which is set when the mechanism is all the way retracted,
     * You must call the update() method in a loop after this.
     *
     * @param position                 desired position; 0 is the fully retracted position units are
     *                                 whatever you setup the mechanism for initially
     * @param extensionRetractionPower max power for the motor
     */
/*
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
*/

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
    // status methods - These methods are available to get the status of the mechanism
    //**********************************************************************************************

    /**
     * Is the mechanism reset cycle completed?
     *
     * @return true if complete
     */
    public boolean isResetComplete() {
        if (extensionRetractionState == ExtensionRetractionStates.RESET_COMPLETE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Is the mechanism retraction cycle completed?
     *
     * @return true if complete
     */
    public boolean isRetractionComplete() {
        if (extensionRetractionState == ExtensionRetractionStates.FULLY_RETRACTED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Is the mechanism extension cycle completed?
     *
     * @return true if complete
     */
    public boolean isExtensionComplete() {
        if (extensionRetractionState == ExtensionRetractionStates.FULLY_EXTENDED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Has the mechanism arrived at the requested position?
     *
     * @return true if complete
     */
    public boolean isPositionReached() {
        if (extensionRetractionState == ExtensionRetractionStates.AT_POSITION) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Is the mechanism being run by joystick?
     *
     * @return true if complete
     */
    public boolean isInJoystickMode() {
        if (extensionRetractionState == ExtensionRetractionStates.JOYSTICK) {
            return true;
        } else {
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
    //**********************************************************************************************
    // mechanism methods - These methods are called by the states in the state machine
    //**********************************************************************************************

    //**********************************************************************************************
    // reset methods - These methods are called by the states in the state machine
    //**********************************************************************************************

    /**
     * This method is called before the start of a reset sequence to check if the mechanism is
     * ok to reset. Put your code for determining that here. The code here is a suggestion. It
     * assumes that the reset fully retracts the mechanism.
     *
     * @return
     */
    private boolean isOKToReset() {
        return !isRetractionLimitReached();
    }

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
        if (isRetractionLimitReached()) {
            log("Reset movement complete " + mechanismName);
            return true;
        } else {
            return false;
        }
    }

    private void performActionsToCompleteResetMovement() {
        // your actions to complete the reset movement must be coded here. These are suggested
        // actions.
        // after a reset, there is no reason to keep the motor powered and holding a position of 0
        // so set the motor to float
        setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
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
     * You should fill in your code to check whether it is ok to retract. The code shown here is a
     * suggestion.
     *
     * @return true if it is ok to retract
     */
    private boolean isOKToRetract() {
        return !isRetractionLimitReached();
    }

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
    private void performActionsToCompleteRetractMovement() {
        // your actions to complete the retract movement must be coded here. These are suggested
        // actions. You can override these if you need to.
        // Assuming that the full retraction is when the mechanism is at the reset position, there
        // is no need to keep the motor powered and holding that position so float the motor.
        // NOTE if a full retraction is not on the reset position, then you may have to change this
        // and actively hold the position.
        setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
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
     * You should fill in your code to check whether it is ok to extend. The code shown here is a
     * suggestion.
     *
     * @return true if it is ok to extend
     */
    private boolean isOKToExtend() {
        return !isExtensionLimitReached();
    }

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
    private void performActionsToCompleteExtendMovement() {
        // your actions to complete the extend movement must be coded here. These are suggested
        // actions. You can override these if you need to.
        // set the target position for the motor to be held at to the current position
        extensionRetractionMotor.setTargetPosition(extensionRetractionMotor.getCurrentPosition());
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

    /**
     * Check to see if the mechanism is ok to move to a position. It is if one of these is true:
     * its position is in between the retraction limit and the extension limit OR
     * its position is at the retraction limit but the position requires it to extend OR
     * its position is at the extension limite but the position requires it to retract
     * Note this assumes that:
     * the most negative position is the retraction limit
     * the most positive position is the extension limit
     *
     * @return true if ok to go to position
     */
    private boolean isOkToGoToPosition() {
        boolean result = true;
        if (isRetractionLimitReached() && desiredPosition <= getPosition()) {
            result = false;
        }
        if (isExtensionLimitReached() && desiredPosition >= getPosition()) {
            result = false;
        }
        return result;
    }

    /**
     * Move the mechanism to the position specified at the power specified. When the movement is
     * finished the mechanism will either actively hold position or will float.
     */
    private void moveToPosition() {
        extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        extensionRetractionMotor.moveToPosition(moveToPositionPower, desiredPosition, finishBehavior);
    }

    /**
     * Is the move to position complete?
     *
     * @return true if complete
     */
    private boolean isMoveToPositionComplete() {
        return extensionRetractionMotor.isMotorStateComplete();
    }

    //**********************************************************************************************
    // Joystick control methods - These methods are called by the states in the state machine
    //**********************************************************************************************

    /**
     * Check if it is ok for the driver to use a joystick to apply power to the mechanism. It is ok
     * if:
     * the mechanism is between fully retracted and fully extended OR
     * the mechanism is fully retracted but the joystick command (power) is positive, meaning extend OR
     * the mechanism is fully extended but the joystick command (power) is negative, meaning retract
     *
     * @return true if ok to use joystick power
     */
    private boolean isOkToJoystick() {
        boolean result = true;
        if (isRetractionLimitReached()) {
            // if the mechanism is at the retracted, only allow it to move up
            if (joystickPower >= 0) {
                result = true;
            } else {
                // negative so the driver wants it to retract. But it is already fully retracted so we cannot retract more.
                result = false;
            }
        }
        if (isExtensionLimitReached()) {
            // if the mechanism is at the retracted, only allow it to move up
            if (joystickPower <= 0) {
                result = true;
            } else {
                // positive so the driver wants it to extend. But it is already fully extended so we cannot extend more.
                result = false;
            }
        }
        // If neither of the above conditions were entered then the mechanism is somewhere between
        // fully extended and fully retracted and it is ok to joystick. That is why the default
        // result is true.
        return result;

    }

    /**
     * Process a joystick input.
     */
    private void processJoystick() {
        if (isOkToJoystick()) {
            extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            extensionRetractionMotor.setPower(joystickPower);
            // not sure about this anymore since the state machine has completely changed. Need to
            // investigate it.
            /*
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
             */
        }
    }

    //**********************************************************************************************
    // supporting methods
    //**********************************************************************************************

    /**
     * Cause the mechanism to stop moving.
     */
    private void stopMechanism() {
        if (finishBehavior == DcMotor8863.FinishBehavior.FLOAT) {
            extensionRetractionMotor.setPower(0);
        } else {
            // the motor is going to have to actively hold position
            extensionRetractionMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            extensionRetractionMotor.setTargetPosition(extensionRetractionMotor.getCurrentPosition());
            extensionRetractionMotor.setPower(1.0);
        }
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

            case START_RESET_SEQUENCE:
                switch (extensionRetractionCommand) {
                    case RESET:
                        if (isOKToReset()) {
                            // start the reset sequence
                            performPreResetActions();
                            extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RESET_ACTIONS;
                        } else {
                            // the mechanism is already at the reset position so assume it has already
                            // been reset and the reset is complete.
                            extensionRetractionState = ExtensionRetractionStates.RESET_COMPLETE;
                            extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
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
                            performActionsToCompleteResetMovement();
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
                            extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
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
                // In case this command is interrupted by another command, and then that command
                // cannot be run for some reason, save this state and command so that it can be
                // resumed.
                previousExtensionRetractionState = ExtensionRetractionStates.RESET_COMPLETE;
                previousExtensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;

                switch (extensionRetractionCommand) {
                    case RESET:
                        // ignore another reset command since the mechanism is already reset
                        logIgnoreCommand(ExtensionRetractionCommands.RESET);
                        extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                        break;
                    // The mechansim is waiting for another command to be received. When one is
                    // received after a reset, the command is processed here.
                    case GO_TO_RETRACTED:
                        // save the state and command so that if the requested command cannnot be run,
                        // the mechanism can return to this state and resume where it left off
                        extensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                        break;
                    case GO_TO_EXTENDED:
                        extensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                    case JOYSTICK:
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                        break;
                    case NO_COMMAND:
                        // do nothing, just wait for a new command
                        break;
                }
                break;

            // -------------------------
            //   RETRACTION STATES
            //--------------------------

            case START_RETRACTION_SEQUENCE:
                switch (extensionRetractionCommand) {
                    case RESET:
                        // The retraction has been interrupted by a reset command. Setup for a reset.
                        // A reset can be requested at any time.
                        previousExtensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_RETRACTED;
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
                        break;
                    case GO_TO_RETRACTED:
                        if (isOKToRetract()) {
                            // the mechanism is not retracted yet so it is ok to retract
                            performPreRetractActions();
                            extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RETRACTION_ACTIONS;
                        } else {
                            // already retracted so can't retract. Ignore command, and move directly
                            // to the fully retracted state.
                            logIgnoreCommand(ExtensionRetractionCommands.GO_TO_RETRACTED);
                            extensionRetractionState = previousExtensionRetractionState;
                            // the GO_TO_RETRACTED command is complete. Clear the command.
                            extensionRetractionCommand = previousExtensionRetractionCommand;
                        }
                        break;
                    // The retraction command can be interrupted by another command.
                    // Note that this command is interrupting the retraction. So if there are pre retraction
                    // actions being run they are interrupted too. Interrupting those actions may put whatever they are
                    // operating on into an unknown state.
                    case GO_TO_EXTENDED:
                        previousExtensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_RETRACTED;
                        extensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        previousExtensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_RETRACTED;
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        break;
                    case JOYSTICK:
                        previousExtensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_RETRACTED;
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }

                // You can perform actions before the retraction movement starts. For example, if you have
                // another mechanism attached to this one, and it has to turn a certain way in order
                // to avoid a collision with part of the robot while this mechanism moves to the retraction
                // position, you can perform that turn and then watch for its completion here. This is
                // optional. If you don't have any actions, just make arePreResetActionsComplete() return
                // true in all cases.
            case PERFORMING_PRE_RETRACTION_ACTIONS:
                // In case this command is interrupted by another command, and then that command
                // cannot be run for some reason, save this state and command so that it can be
                // resumed.
                previousExtensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_RETRACTION_ACTIONS;
                previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_RETRACTED;

                switch (extensionRetractionCommand) {
                    // The retraction has been interrupted by a reset command. Setup for a reset.
                    case RESET:
                        // a reset can be requested at any time.
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
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
                    // Note that this command is interrupting the retraction. So if there are pre retraction
                    // actions being run they are interrupted too. Interrupting those actions may put whatever they are
                    // operating on into an unknown state.
                    case GO_TO_EXTENDED:
                        extensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        break;
                    case JOYSTICK:
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }
                break;

            // In this state the mechanism is moving to the retracted position.
            case RETRACTING:
                // In case this command is interrupted by another command, and then that command
                // cannot be run for some reason, save this state and command so that it can be
                // resumed.
                previousExtensionRetractionState = ExtensionRetractionStates.RETRACTING;
                previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_RETRACTED;

                switch (extensionRetractionCommand) {
                    // The retraction has been interrupted by a reset command. Setup for a reset.
                    case RESET:
                        // a reset can be requested at any time.
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
                        break;
                    case GO_TO_RETRACTED:
                        if (isMoveToRetractComplete()) {
                            logArrivedAtDestination();
                            performActionsToCompleteRetractMovement();
                            // movement to the retraction position is complete, start the post retraction actions
                            performPostRetractActions();
                            extensionRetractionState = ExtensionRetractionStates.PERFORMING_POST_RETRACTION_ACTIONS;
                        } else {
                            // retraction not complete yet, keep watching
                        }
                        break;
                    // The retraction command can be interrupted by another command.
                    case GO_TO_EXTENDED:
                        extensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        break;
                    case JOYSTICK:
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
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
                // In case this command is interrupted by another command, and then that command
                // cannot be run for some reason, save this state and command so that it can be
                // resumed.
                previousExtensionRetractionState = ExtensionRetractionStates.PERFORMING_POST_RETRACTION_ACTIONS;
                previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_RETRACTED;

                switch (extensionRetractionCommand) {
                    // The retraction has been interrupted by a reset command. Setup for a reset.
                    case RESET:
                        // a reset can be requested at any time.
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
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
                    // The retraction command can be interrupted by another command.
                    // Note that this command is interrupting the retraction. So if there are post retraction
                    // actions being run they are interrupted too. Interrupting those actions may put whatever they are
                    // operating on into an unknown state.
                    case GO_TO_EXTENDED:
                        extensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        break;
                    case JOYSTICK:
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }
                break;

            // This state means that the pre retraction actions are done, the movement to the retraction
            // position is done, the post retraction actions are done, and the whole retraction is done.
            case FULLY_RETRACTED:
                // In case this command is interrupted by another command, and then that command
                // cannot be run for some reason, save this state and command so that it can be
                // resumed.
                previousExtensionRetractionState = ExtensionRetractionStates.FULLY_RETRACTED;
                previousExtensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;

                switch (extensionRetractionCommand) {
                    // A reset command has been received. Setup for a reset.
                    case RESET:
                        // a reset can be requested at any time.
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
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
                        extensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        break;
                    case JOYSTICK:
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }
                break;

            // -------------------------
            //   EXTENSION STATES
            //--------------------------

            case START_EXTENSION_SEQUENCE:
                switch (extensionRetractionCommand) {
                    case RESET:
                        // The retraction has been interrupted by a reset command. Setup for a reset.
                        // A reset can be requested at any time.
                        previousExtensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_EXTENDED;
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
                        break;
                    case GO_TO_EXTENDED:
                        if (isOKToExtend()) {
                            // the mechanism is not retracted yet so it is ok to retract
                            performPreExtendActions();
                            extensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_EXTENSION_ACTIONS;
                        } else {
                            // already extended so can't extend. Ignore command, and move directly
                            // to the fully extended state.
                            logIgnoreCommand(ExtensionRetractionCommands.GO_TO_EXTENDED);
                            extensionRetractionState = previousExtensionRetractionState;
                            // the GO_TO_EXTENDED command is complete. Clear the command.
                            extensionRetractionCommand = previousExtensionRetractionCommand;
                        }
                        break;
                    // The extension command can be interrupted by another command.
                    // Note that this command is interrupting the extension. So if there are pre extension
                    // actions being run they are interrupted too. Interrupting those actions may put whatever they are
                    // operating on into an unknown state.
                    case GO_TO_RETRACTED:
                        previousExtensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_EXTENDED;
                        extensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        previousExtensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_EXTENDED;
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        break;
                    case JOYSTICK:
                        previousExtensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_EXTENDED;
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }

                // You can perform actions before the extension movement starts. For example, if you have
                // another mechanism attached to this one, and it has to turn a certain way in order
                // to avoid a collision with part of the robot while this mechanism moves to the extension
                // position, you can perform that turn and then watch for its completion here. This is
                // optional. If you don't have any actions, just make arePreResetActionsComplete() return
                // true in all cases.
            case PERFORMING_PRE_EXTENSION_ACTIONS:
                // In case this command is interrupted by another command, and then that command
                // cannot be run for some reason, save this state and command so that it can be
                // resumed.
                previousExtensionRetractionState = ExtensionRetractionStates.PERFORMING_PRE_EXTENSION_ACTIONS;
                previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_EXTENDED;

                switch (extensionRetractionCommand) {
                    // The retraction has been interrupted by a reset command. Setup for a reset.
                    case RESET:
                        // a reset can be requested at any time.
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
                        break;
                    case GO_TO_EXTENDED:
                        if (arePreExtendActionsComplete()) {
                            // pre retraction actions are complete, start the movement to retracted position
                            moveToFullExtend();
                            extensionRetractionState = ExtensionRetractionStates.EXTENDING;
                        } else {
                            // pre retraction actions not complete yet, keep watching
                        }
                        break;
                    // The retraction command can be interrupted by another command.
                    // Note that this command is interrupting the retraction. So if there are pre retraction
                    // actions being run they are interrupted too. Interrupting those actions may put whatever they are
                    // operating on into an unknown state.
                    case GO_TO_RETRACTED:
                        extensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        break;
                    case JOYSTICK:
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }
                break;

            // In this state the mechanism is moving to the retracted position.
            case EXTENDING:
                // In case this command is interrupted by another command, and then that command
                // cannot be run for some reason, save this state and command so that it can be
                // resumed.
                previousExtensionRetractionState = ExtensionRetractionStates.EXTENDING;
                previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_EXTENDED;

                switch (extensionRetractionCommand) {
                    // The retraction has been interrupted by a reset command. Setup for a reset.
                    case RESET:
                        // a reset can be requested at any time.
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
                        break;
                    case GO_TO_EXTENDED:
                        if (isMoveToExtendComplete()) {
                            logArrivedAtDestination();
                            performActionsToCompleteExtendMovement();
                            // movement to the retraction position is complete, start the post retraction actions
                            performPostExtendActions();
                            extensionRetractionState = ExtensionRetractionStates.PERFORMING_POST_EXTENSION_ACTIONS;
                        } else {
                            // extension not complete yet, keep watching
                        }
                        break;
                    // The extension command can be interrupted by another command.
                    case GO_TO_RETRACTED:
                        extensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        break;
                    case JOYSTICK:
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
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
            case PERFORMING_POST_EXTENSION_ACTIONS:
                // In case this command is interrupted by another command, and then that command
                // cannot be run for some reason, save this state and command so that it can be
                // resumed.
                previousExtensionRetractionState = ExtensionRetractionStates.PERFORMING_POST_EXTENSION_ACTIONS;
                previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_EXTENDED;

                switch (extensionRetractionCommand) {
                    // The retraction has been interrupted by a reset command. Setup for a reset.
                    case RESET:
                        // a reset can be requested at any time.
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
                        break;
                    case GO_TO_EXTENDED:
                        if (arePostExtendActionsComplete()) {
                            // post retraction actions are complete
                            extensionRetractionState = ExtensionRetractionStates.FULLY_EXTENDED;
                            // the GO_TO_EXTENDED command is complete. Clear the command.
                            extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                        } else {
                            // post extension action not complete yet, keep watching
                        }
                        break;
                    // The retraction command can be interrupted by another command.
                    // Note that this command is interrupting the retraction. So if there are post retraction
                    // actions being run they are interrupted too. Interrupting those actions may put whatever they are
                    // operating on into an unknown state.
                    case GO_TO_RETRACTED:
                        extensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        break;
                    case JOYSTICK:
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }
                break;

            // This state means that the pre retraction actions are done, the movement to the retraction
            // position is done, the post retraction actions are done, and the whole retraction is done.
            case FULLY_EXTENDED:
                // In case this command is interrupted by another command, and then that command
                // cannot be run for some reason, save this state and command so that it can be
                // resumed.
                previousExtensionRetractionState = ExtensionRetractionStates.FULLY_EXTENDED;
                previousExtensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;

                switch (extensionRetractionCommand) {
                    // A reset command has been received. Setup for a reset.
                    case RESET:
                        // a reset can be requested at any time.
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
                        break;
                    case GO_TO_EXTENDED:
                        // The mechanism is already extended. This command is not relevant. Ignore
                        // this command.
                        // The command gets set to NO_COMMAND since nothing will happen until a new
                        // command is received.
                        logIgnoreCommand(ExtensionRetractionCommands.GO_TO_EXTENDED);
                        extensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;
                        break;
                    case GO_TO_RETRACTED:
                        extensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        break;
                    case JOYSTICK:
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }
                break;

            // -------------------------
            //   MOVING TO POSITION STATES
            //--------------------------

            // this state checks to see if the mechanism can run a go to position.
            case START_GO_TO_POSITION:
                switch (extensionRetractionCommand) {
                    // The retraction has been interrupted by a reset command. Setup for a reset.
                    case RESET:
                        previousExtensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_POSITION;
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
                        break;
                    case GO_TO_RETRACTED:
                        previousExtensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_POSITION;
                        extensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                        break;
                    case GO_TO_EXTENDED:
                        previousExtensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_POSITION;
                        extensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        if (isOkToGoToPosition()) {
                            moveToPosition();
                            extensionRetractionState = ExtensionRetractionStates.MOVING_TO_POSITION;
                        } else {
                            extensionRetractionState = previousExtensionRetractionState;
                            extensionRetractionCommand = previousExtensionRetractionCommand;
                        }

                    case JOYSTICK:
                        previousExtensionRetractionState = extensionRetractionState;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_POSITION;
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                        break;
                    case NO_COMMAND:
                        // do nothing. This command should never be active in this state.
                        break;
                }
                break;

            case MOVING_TO_POSITION:
                // In case this command is interrupted by another command, and then that command
                // cannot be run for some reason, save this state and command so that it can be
                // resumed.
                previousExtensionRetractionState = ExtensionRetractionStates.MOVING_TO_POSITION;
                previousExtensionRetractionCommand = ExtensionRetractionCommands.GO_TO_POSITION;

                switch (extensionRetractionCommand) {
                    case RESET:
                        // a reset can be requested at any time.
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
                        break;
                    // movement to a position can be interrupted by a command to fully extend or
                    // fully retract
                    case GO_TO_RETRACTED:
                        extensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                    case GO_TO_EXTENDED:
                        extensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
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
                            // The moveToPosition() that was called previously will take care of the
                            // motor so no other method calls are needed here.
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
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                        break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                }
                break;

            // this state is for when the mechanism has completed a move to a position
            case AT_POSITION:
                // In case this command is interrupted by another command, and then that command
                // cannot be run for some reason, save this state and command so that it can be
                // resumed.
                previousExtensionRetractionState = ExtensionRetractionStates.AT_POSITION;
                previousExtensionRetractionCommand = ExtensionRetractionCommands.NO_COMMAND;

                switch (extensionRetractionCommand) {
                    case RESET:
                        // a reset can be requested at any time.
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
                        break;
                    // movement to a position can be interrupted by a command to fully extend or
                    // fully retract
                    case GO_TO_RETRACTED:
                        extensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                    case GO_TO_EXTENDED:
                        extensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        // When the mechanism arrives at the desired position, the command is set to
                        // NO_COMMAND. So if this GO_TO_POSITION command is received, then this is a
                        // new move to position command. I.E. moving to a position from another
                        // position.
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        break;
                    case JOYSTICK:
                        extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                        break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                }
                break;

            // -------------------------
            //   JOYSTICK CONTROL
            //--------------------------
/*
            case START_JOYSTICK:
                switch (extensionRetractionCommand) {
                    case RESET:
                        // a reset can be requested at any time.
                        previousExtensionRetractionState = extensionRetractionState;
                        previousExtensionRetractionCommand = extensionRetractionCommand;
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
                        break;
                    // movement to a position can be interrupted by a command to fully extend or
                    // fully retract
                    case GO_TO_RETRACTED:
                        previousExtensionRetractionState = extensionRetractionState;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.JOYSTICK;
                        extensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        break;
                    case GO_TO_EXTENDED:
                        previousExtensionRetractionState = extensionRetractionState;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.JOYSTICK;
                        extensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        previousExtensionRetractionState = extensionRetractionState;
                        previousExtensionRetractionCommand = ExtensionRetractionCommands.JOYSTICK;
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        break;
                    case JOYSTICK:
                        if (isOkToJoystick()) {
                            extensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                        } else {
                            extensionRetractionState = previousExtensionRetractionState;
                            extensionRetractionCommand = previousExtensionRetractionCommand;
                        }
                        break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                    break;
                }
                break;
*/
            case JOYSTICK:
                // In case this command is interrupted by another command, and then that command
                // cannot be run for some reason, save this state and command so that it can be
                // resumed.
                previousExtensionRetractionState = ExtensionRetractionStates.JOYSTICK;
                previousExtensionRetractionCommand = ExtensionRetractionCommands.JOYSTICK;

                switch (extensionRetractionCommand) {
                    case RESET:
                        // a reset can be requested at any time.
                        previousExtensionRetractionState = extensionRetractionState;
                        previousExtensionRetractionCommand = extensionRetractionCommand;
                        extensionRetractionState = ExtensionRetractionStates.START_RESET_SEQUENCE;
                        break;
                    // movement to a position can be interrupted by a command to fully extend or
                    // fully retract
                    case GO_TO_RETRACTED:
                        extensionRetractionState = ExtensionRetractionStates.START_RETRACTION_SEQUENCE;
                        break;
                    case GO_TO_EXTENDED:
                        extensionRetractionState = ExtensionRetractionStates.START_EXTENSION_SEQUENCE;
                        break;
                    case GO_TO_POSITION:
                        extensionRetractionState = ExtensionRetractionStates.START_GO_TO_POSITION;
                        break;
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


    //*********************************************************************************************]
    // tests for mechanism
    //**********************************************************************************************

    public void testMotorModeSwitch() {
        // reset the mechanism
        reset();
        while (!isResetComplete()) {
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

    public void testReset(LinearOpMode opMode) {
        ExtensionRetractionStates extensionRetractionState;
        this.setResetPower(0.1);
        this.reset();
        while (opMode.opModeIsActive() && !isResetComplete()) {
            extensionRetractionState = this.update();
            opMode.telemetry.addData("state = ", extensionRetractionState.toString());
            opMode.telemetry.update();
            opMode.idle();
        }
        // this reset should be ignored
        this.reset();
        while (opMode.opModeIsActive()) {
            extensionRetractionState = this.update();
            opMode.telemetry.addData("state = ", extensionRetractionState.toString());
            opMode.telemetry.update();
            opMode.idle();
        }
    }

    public void testExtension(LinearOpMode opMode) {
        ExtensionRetractionStates extensionRetractionState;
        this.setResetPower(0.1);
        this.setExtensionPower(0.5);
        this.reset();
        while (opMode.opModeIsActive() && !this.isResetComplete()) {
            extensionRetractionState = this.update();
            opMode.telemetry.addData("state = ", extensionRetractionState.toString());
            opMode.telemetry.update();
            opMode.idle();
        }
        opMode.sleep(3000);
        this.goToFullExtend();
        while (opMode.opModeIsActive() && !this.isExtensionComplete()) {
            extensionRetractionState = this.update();
            opMode.telemetry.addData("state = ", extensionRetractionState.toString());
            opMode.telemetry.update();
            opMode.idle();
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
