package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

public class CollectorArm {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum ExtensionArmCommands {
        NO_COMMAND,
        GO_TO_EXTEND,
        GO_TO_RETRACT,
        GO_TO_POSITION,
        RESET,
        JOYSTICK
    }

    private enum ExtensionArmStates {
        RESET,
        RESET_MOVING_TO_RETRACT,
        RETRACT,
        IN_BETWEEN,
        EXTEND
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    public DcMotor8863 rotationArmMotor;
    private Telemetry telemetry;

    private double initPosition = 0;
    private double homePosition = -25;
    private double collectPosition = -140;
    private double transferPosition = -80;
    private double dehangPosition = -42;
    private double clearStarPosition = -55;

    private DcMotor8863 extensionArmMotor;

    private Switch extensionLimitSwitch;
    private Switch retractionLimitSwitch;

    private ExtensionArmCommands command;
    private ExtensionArmStates state;
    private double desiredExtensionArmPosition = 0;
    private double extensionArmPower = 0;

    private double extensionArmSpeed = .7;
    private boolean debugMode = false;

    private DataLogging logFile;
    private boolean loggingOn = false;

    private double extensionArmJoystickPower = 0;

    private ExtensionArmStates previousExtensionArmState;
    private ExtensionArmCommands previousExtensionCommand;

    private CollectorExtensionArmStates previousCollectorExtensionArmState;
    private CollectorExtensionArmCommands previousCollectorExtensionArmCommand;


    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public boolean isDebugMode() {
        return debugMode;
    }

    public void enableDebugMode() {
        this.debugMode = true;
        this.extensionArmSpeed = .3;
        // normally the extension arm has to be reset before it will accept any commands.
        // This forces it to locate its 0 position before any other commands will
        // run. But when debugging you may not want the extension arm to have to reset before
        // running any commands. So if the extension arm is in debug mode, force the state machine to think
        // the extension arm is IN_BETWEEN so any command sent to the extension arm will run.
        state = ExtensionArmStates.IN_BETWEEN;
    }

    public void disableDebugMode() {
        this.debugMode = false;
        this.extensionArmSpeed = .5;
    }

    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    public void enableDataLogging() {
        this.loggingOn = true;
    }

    public void disableDataLogging() {
        this.loggingOn = false;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public CollectorArm(HardwareMap hardwareMap, Telemetry telemetry){
        rotationArmMotor = new DcMotor8863("collectorArmRotationMotor", hardwareMap, telemetry);
        rotationArmMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_60);
        rotationArmMotor.setMovementPerRev(360*48/128);
        rotationArmMotor.setMotorToHold();
        rotationArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rotationArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        extensionArmMotor =new DcMotor8863("extensionArmMotor", hardwareMap, telemetry);
        extensionArmMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_3_7_ORBITAL);
        //72tooth gear on motor and 56tooth gear on lead screw and lead screw moves 8mm per rev
        extensionArmMotor.setMovementPerRev((72/56)*(8/25.4)*1/.78);
        extensionArmMotor.setMotorToHold();
        extensionArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extensionArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.telemetry = telemetry;

        extensionLimitSwitch = new Switch(hardwareMap, "extensionArmExtendLimitSwitch", Switch.SwitchType.NORMALLY_OPEN);
        retractionLimitSwitch = new Switch(hardwareMap, "extensionArmRetractLimitSwitch", Switch.SwitchType.NORMALLY_OPEN);

        state = ExtensionArmStates.RESET;
        command = ExtensionArmCommands.NO_COMMAND;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

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
    public void init() {
        log("Collector Arm system initializing");
        if (!isDebugMode()) {
            extensionArmReset();
            while (!isExtensionArmMovementComplete()) {
                updateExtensionArm();
            }
        }
    }

    public void shutdown() {

    }

    public void update() {
        updateRotationArm();
        updateExtensionArm();
        rotationExtensionArmUpdate();
    }

    //**********************************************************************************************
    //**********************************************************************************************
    //**********************************************************************************************
    // ROTATION
    //**********************************************************************************************
    //**********************************************************************************************
    //**********************************************************************************************

    //*********************************************************************************************]
    // rotationArm motor position feedback
    //**********************************************************************************************

    public void displayRotationArmEncoder(){
        telemetry.addData("encoder value ", rotationArmMotor.getCurrentPosition());
        telemetry.addData("arm angle ", rotationArmMotor.getPositionInTermsOfAttachment());
    }

    public double getRotationArmAngle() {
        return rotationArmMotor.getPositionInTermsOfAttachment();
    }

    public void displayRotationArmCompletion(){
        telemetry.addData("Rotation Arm Motor State = ", rotationArmMotor.getCurrentMotorState().toString());
    }

    //*********************************************************************************************]
    // rotation arm motor commands
    //**********************************************************************************************

    public void rotationArmRunMotorUsingEncoder(){
        rotationArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rotationArmMotor.setPower(0.2);
    }

    public void rotationArmRunMotorUsingPosition(){
        rotationArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rotationArmMotor.moveToPosition(0.2,-100, DcMotor8863.FinishBehavior.HOLD);
    }


    public void rotationArmGoToHome(){
        log("COMMANDED ROTATION ARM TO HOME POSITION (-25)");
        rotationArmMotor.moveToPosition(0.2, homePosition, DcMotor8863.FinishBehavior.HOLD);
    }

    public void rotationArmGoToCollect(){
        log("COMMANDED ROTATION ARM TO COLLECT POSITION (-135)");
        rotationArmMotor.moveToPosition(0.2, -135.00, DcMotor8863.FinishBehavior.HOLD);
    }

    public void rotationArmGoToPark() {
        rotationArmMotor.moveToPosition(0.2, -130, DcMotor8863.FinishBehavior.HOLD);
    }

    public void rotationArmGoToTransfer(){
        log("COMMANDED ROTATION ARM TO TRANSFER POSITION (-47)");
        rotationArmMotor.moveToPosition(0.2, -47, DcMotor8863.FinishBehavior.HOLD);
    }

    public void rotationArmGoToDehang(){
        rotationArmMotor.moveToPosition(0.2, dehangPosition, DcMotor8863.FinishBehavior.HOLD);
    }

    public void rotationArmGoToClearStar(){
        rotationArmMotor.moveToPosition(0.2, clearStarPosition, DcMotor8863.FinishBehavior.HOLD);
    }

    public void raiseOffGround(){
        log("COMMANDED ROTATION ARM TO RAISE OFF GROUND POSITION (-115)");
        rotationArmMotor.moveToPosition(0.2, -115.00, DcMotor8863.FinishBehavior.HOLD);
    }

    public void rotationArmFloatArm(){
        log("COMMANDED ROTATION ARM TO FLOAT");
        rotationArmMotor.setMotorToFloat();
    }


    //*********************************************************************************************]
    // rotation arm motor state machine
    //**********************************************************************************************

    public void updateRotationArm() {
        rotationArmMotor.update();
    }

    public boolean isRotationArmMovementComplete() {
        if (rotationArmMotor.isMotorStateComplete()) {
            log("ROTATION ARM ARRIVED AT DESTINATION");
            log("Rotation arm angle = " + Double.toString(getRotationArmAngle()));
            return true;
        } else {
            return false;
        }
    }

    //**********************************************************************************************
    //**********************************************************************************************
    //**********************************************************************************************
    // EXTENSION / RETRACTION
    //**********************************************************************************************
    //**********************************************************************************************
    //**********************************************************************************************

    //*********************************************************************************************]
    // extension motor position feedback
    //**********************************************************************************************

    public int getExtensionArmMotorEncoder() {
        return extensionArmMotor.getCurrentPosition();
    }

    public void displayExtensionMotorEncoder() {
        telemetry.addData("Encoder = ", getExtensionArmMotorEncoder());
        telemetry.addData("Inches = ", extensionArmMotor.getPositionInTermsOfAttachment());
    }

    public double getExtensionArmPosition() {
        return extensionArmMotor.getPositionInTermsOfAttachment();
    }

    public void displayExtensionArmPosition() {
        telemetry.addData("Lift position (inches) = ", getExtensionArmPosition());
    }

    public void displayRequestedExtensionArmPosition() {
        telemetry.addData("Lift position requested (inches) = ", desiredExtensionArmPosition);
    }

    public void displayExtensionArmPower() {
        telemetry.addData("Lift power (inches) = ", extensionArmPower);
    }

    public void displayExtensionArmMotorState() {
        telemetry.addData("Motor state = ", extensionArmMotor.getCurrentMotorState().toString());
    }

    //*********************************************************************************************]
    // extension arm motor commands
    //**********************************************************************************************

    public void extensionArmReset() {
        log("COMMANDED EXTENSION ARM TO RESET");
        command = ExtensionArmCommands.RESET;
    }

    public void goToRetract() {
        log("COMMANDED EXTENSION ARM TO RETRACT POSITION");
        command = ExtensionArmCommands.GO_TO_RETRACT;
    }

    public void goToExtend() {
        log("COMMANDED EXTENSION ARM TO EXTEND POSITION");
        command = ExtensionArmCommands.GO_TO_EXTEND;
    }

    public void setExtensionArmPowerUsingJoystick(double power) {
        // if a command has been given to reset the extension arm, then do not allow the joystick to take
        // effect and override the reset
        extensionArmJoystickPower = 0;
        if (command != ExtensionArmCommands.RESET) {
            // A joystick command is only a real command if it is not 0. If the joystick value is 0
            // just ignore the stick
            if (power != 0) {
                command = ExtensionArmCommands.JOYSTICK;
                extensionArmJoystickPower = power;
            }
        }
    }

    /**
     * For testing a move to position
     */
    public void gotoExtensionArm5Inches() {
        moveToExtensionArmPosition(5.0, 1);
    }

    /**
     * For testing a move to position
     */
    public void gotoExtensionArm8Inches() {
        moveToExtensionArmPosition(8.0, .2);
    }

    public void goToExtensionArmHome() {
        log("COMMANDED EXTENSION ARM TO HOME POSITION");
        moveToExtensionArmPosition(0.5, 1);
    }

    public void goToExtensionArmTransfer() {
        log("COMMANDED EXTENSION ARM TO TRANSFER POSITION");
        moveToExtensionArmPosition(5.5, 1);
    }

    public void goToExtensionArm10Inches() {
        log("COMMANDED EXTENSION ARM TO 10 INCHES");
        moveToExtensionArmPosition(10.0, 1);
    }

    public void moveExtensionArmTwoInchesOut() {
        // since the motor starts in RESET state I have to force it into another state in order to
        // get movement
        state = ExtensionArmStates.IN_BETWEEN;
        moveToExtensionArmPosition(2, .5);
    }

    private void moveToRetract() {
        extensionArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extensionArmMotor.setPower(-extensionArmSpeed);
    }

    private void moveToExtend() {
        extensionArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extensionArmMotor.setPower(+extensionArmSpeed);
    }

    private void stopExtensionArm() {
        log("EXTENSION ARM ARRIVED AT DESTINATION");
        extensionArmMotor.setPower(0);
    }

    /**
     * Move to a position based on zero which is set when the extension arm is all the way down, must run
     * update rotuine in a loop after that.
     *
     * @param extensionInInches desired height above the 0 position
     * @param extensionArmPower      max power for the motor
     */
    public void moveToExtensionArmPosition(double extensionInInches, double extensionArmPower) {
        log("moving extension arm to position = " + extensionInInches);
        desiredExtensionArmPosition = extensionInInches;
        this.extensionArmPower = extensionArmPower;
        command = ExtensionArmCommands.GO_TO_POSITION;
        extensionArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        extensionArmMotor.moveToPosition(extensionArmPower, extensionInInches, DcMotor8863.FinishBehavior.FLOAT);
    }

    private boolean isExtensionArmMovementExtend() {
        // if the position that we want to move to is greater than the current position of the extension arm,
        // then the movement of the extension arm will be up. For example, desired position is 10. Current
        // position is 5. So the extension arm has to move up to get there.
        if (desiredExtensionArmPosition - getExtensionArmPosition() > 0) {
            return true;
        } else {
            return false;
        }
    }

    //*********************************************************************************************]
    // extension arm motor state machine
    //**********************************************************************************************

    public ExtensionArmStates updateExtensionArm() {
        DcMotor8863.MotorState motorState = extensionArmMotor.update();
        logState(state, command);

        switch (state) {
            case RESET:
                switch (command) {
                    case RESET:
                        log("Resetting extension arm");
                        // send the extension arm moving down
                        moveToRetract();
                        state = ExtensionArmStates.RESET_MOVING_TO_RETRACT;
                        break;
                    // all other commands are ignored when a reset is issued. Basically force
                    // the command back to a reset
                    case GO_TO_RETRACT:
                        logIgnoreCommand(ExtensionArmCommands.GO_TO_RETRACT);
                        command = ExtensionArmCommands.RESET;
                        break;
                    case GO_TO_EXTEND:
                        logIgnoreCommand(ExtensionArmCommands.GO_TO_EXTEND);
                        command = ExtensionArmCommands.RESET;
                        break;
                    case GO_TO_POSITION:
                        logIgnoreCommand(ExtensionArmCommands.GO_TO_POSITION);
                        command = ExtensionArmCommands.RESET;
                        break;
                    case JOYSTICK:
                        logIgnoreCommand(ExtensionArmCommands.JOYSTICK);
                        command = ExtensionArmCommands.RESET;
                        break;
                    case NO_COMMAND:
                        break;
                }
                break;

            // This state means that a reset was requested and the extension arm has already started
            // retracting. It is here so that a moveToRetract() is not repeatedly called.
            case RESET_MOVING_TO_RETRACT:
                switch (command) {
                    case RESET:
                        // the extension arm has been sent to retract from a reset command.
                        // It is just retracting until the limit switch is pressed and the motor
                        // is told to stop.
                        if (retractionLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. Stop the motor and reset the
                            // encoder to 0. Clear the command.
                            stopExtensionArm();
                            extensionArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                            command = ExtensionArmCommands.NO_COMMAND;
                            state = ExtensionArmStates.RETRACT;
                        }
                        break;
                    // all other commands are ignored when a reset is issued. Basically force
                    // the command back to a reset
                    case GO_TO_RETRACT:
                        logIgnoreCommand(ExtensionArmCommands.GO_TO_RETRACT);
                        command = ExtensionArmCommands.RESET;
                        break;
                    case GO_TO_EXTEND:
                        logIgnoreCommand(ExtensionArmCommands.GO_TO_EXTEND);
                        command = ExtensionArmCommands.RESET;
                        break;
                    case GO_TO_POSITION:
                        logIgnoreCommand(ExtensionArmCommands.GO_TO_POSITION);
                        command = ExtensionArmCommands.RESET;
                        break;
                    case JOYSTICK:
                        logIgnoreCommand(ExtensionArmCommands.JOYSTICK);
                        command = ExtensionArmCommands.RESET;
                        break;
                    case NO_COMMAND:
                        break;
                }
                break;

                // this state does NOT mean that the extension arm is at retract
            // it means that the extension arm is moving to retract OR at retract
            case RETRACT:
                switch (command) {
                    case RESET:
                        moveToRetract();
                        state = ExtensionArmStates.RESET_MOVING_TO_RETRACT;
                        break;
                    case GO_TO_RETRACT:
                        // the extension arm has been sent to retract without using a position command.
                        // It is just moving down until the motor is told to stop.
                        if (retractionLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. Stop the motor. Clear the command.
                            stopExtensionArm();
                            command = ExtensionArmCommands.NO_COMMAND;
                        }
                        break;
                    case GO_TO_EXTEND:
                        // the extension arm has been requested to move to extend. The motor needs to be
                        // turned on and will run towards extend with just speed control, no position
                        // control
                        moveToExtend();
                        state = ExtensionArmStates.EXTEND;
                        break;
                    case GO_TO_POSITION:
                        // the extension arm has been requested to move to a position. The motor has already
                        // been started in position control mode so we don't need to do anything
                        // with the motor. We just need to change state.
                        state = ExtensionArmStates.IN_BETWEEN;
                        break;
                    case JOYSTICK:
                        processExtensionArmJoystick();
                        break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                }
                break;

            // this state is for when the extension arm is located somewhere in between extend and retract
            // and is not moving to extend or moving to retract or being reset
            case IN_BETWEEN:
                switch (command) {
                    case RESET:
                        // a reset can be requested at any time. Start the motor movement and change
                        // state
                        moveToRetract();
                        state = ExtensionArmStates.RESET_MOVING_TO_RETRACT;
                        break;
                    case GO_TO_RETRACT:
                        // the extension arm has been requested to move to retract. The motor needs to be
                        // turned on and will run towards retract with just speed control, no position
                        // control
                        moveToRetract();
                        state = ExtensionArmStates.RETRACT;
                        break;
                    case GO_TO_EXTEND:
                        // the extension arm has been requested to move to extend. The motor needs to be
                        // turned on and will run towards extend with just speed control, no position
                        // control
                        moveToExtend();
                        state = ExtensionArmStates.EXTEND;
                        break;
                    case GO_TO_POSITION:
                        // the extension arm has been requested to move to a position. The motor has already
                        // been started in position control mode so we need to watch to determine
                        // the motor actually reaches the position
                        if (extensionArmMotor.isMotorStateComplete()) {
                            // the movement is finished and the motor stopped in the position, but
                            // it still has power applied to it. Stop the motor.
                            stopExtensionArm();
                            command = ExtensionArmCommands.NO_COMMAND;
                        }

                        // check to make sure extend limit switch has not been tripped. If it has
                        // then something went wrong or someone gave a bad motor command.
                        if (extensionLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. If the movement is supposed to be
                            // up, then Stop the motor. Clear the command.
                            if (isExtensionArmMovementExtend()) {
                                stopExtensionArm();
                                command = ExtensionArmCommands.NO_COMMAND;
                                state = ExtensionArmStates.EXTEND;
                            } else {
                                // extend limit switch is pressed but the movement is supposed to be
                                // down so do nothing. This allows downward movement.
                            }
                        }

                        // check to make sure retract limit switch has not been tripped. If it has
                        // then something went wrong or someone gave a bad motor command.
                        if (retractionLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. If the movement is supposed to be
                            // down, then Stop the motor. Clear the command.
                            if (!isExtensionArmMovementExtend()) {
                                stopExtensionArm();
                                command = ExtensionArmCommands.NO_COMMAND;
                                state = ExtensionArmStates.RETRACT;
                            } else {
                                // retract limit switch is pressed but the movement is supposed to be
                                // up so do nothing. This allows upward movement.
                            }
                        }
                        break;
                    case JOYSTICK:
                        processExtensionArmJoystick();
                        break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                }
                break;

            // this state does NOT mean that the extension arm is at extend
            // it means that the extension arm is moving to extend OR is at extend
            case EXTEND:
                switch (command) {
                    case RESET:
                        // a reset can be requested at any time. Start the motor movement and change
                        // state
                        moveToRetract();
                        state = ExtensionArmStates.RESET_MOVING_TO_RETRACT;
                        break;
                    case GO_TO_RETRACT:
                        // the extension arm has been requested to move to retract. The motor needs to be
                        // turned on and will run towards retract with just speed control, no position
                        // control
                        moveToRetract();
                        state = ExtensionArmStates.RETRACT;
                        break;
                    case GO_TO_EXTEND:
                        // the extension arm has been sent to extend without using a position command.
                        // It is just moving up until the motor is told to stop.
                        if (extensionLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. Stop the motor. Clear the command.
                            stopExtensionArm();
                            command = ExtensionArmCommands.NO_COMMAND;
                        }
                        break;
                    case GO_TO_POSITION:
                        // the extension arm has been requested to move to a position. The motor has already
                        // been started in position control mode so we don't need to do anything
                        // with the motor. We just need to change state.
                        state = ExtensionArmStates.IN_BETWEEN;
                        break;
                    // the extension arm power is being set with a joystick. The extension arm must have hit the
                    // upper limit switch to be in this state
                    case JOYSTICK:
                        processExtensionArmJoystick();
                        break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                }
                break;
        }
        return state;
    }

    private void logState(ExtensionArmStates state, ExtensionArmCommands command) {
        if (logFile != null && loggingOn) {
            if(state != previousExtensionArmState ||command != previousExtensionCommand) {
                logFile.logData("Extension Arm",state.toString(), command.toString());
                previousExtensionArmState = state;
                previousExtensionCommand = command;
            }
        }
    }

    private void logIgnoreCommand(ExtensionArmCommands extensionArmCommand){
        if (logFile != null && loggingOn) {
            logFile.logData("Ignoring command = ", extensionArmCommand.toString());
        }
    }

    public boolean isExtensionArmMovementComplete() {
        if (command == ExtensionArmCommands.NO_COMMAND) {
            return true;
        } else {
            return false;
        }
    }

    private void processExtensionArmJoystick() {
        if (retractionLimitSwitch.isPressed()) {
            // if the extension arm is at retract, only allow it to move up
            if (extensionArmJoystickPower > 0) {
                extensionArmMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                extensionArmMotor.setPower(extensionArmJoystickPower);
            } else {
                // the extension arm power is negative so the driver wants it to move down. But it is already
                // at retract so force the motor power to 0.
                extensionArmMotor.setPower(0);
            }
            state = ExtensionArmStates.RETRACT;
        } else {
            if (extensionLimitSwitch.isPressed()) {
                // if the extension arm is at extend, only allow it to move down
                if (extensionArmJoystickPower < 0) {
                    extensionArmMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    extensionArmMotor.setPower(extensionArmJoystickPower);
                } else {
                    // the extension arm power is positive so the driver wants it to move up. But it is already
                    // at extend so force the motor power to 0.
                    extensionArmMotor.setPower(0);
                }
                state = ExtensionArmStates.EXTEND;
            } else {
                // both limit switches are not pressed, allow it to move either way
                if (extensionArmJoystickPower != 0) {
                    extensionArmMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    extensionArmMotor.setPower(extensionArmJoystickPower);
                } else {
                    // the joystick input is 0 so set the extension arm power to 0
                    extensionArmMotor.setPower(0);
                }
                state = ExtensionArmStates.IN_BETWEEN;
            }
        }
    }

    public void displayExtensionArmState() {
        telemetry.addData("Extension Arm State = ", state.toString());
    }


    public void displayExtensionArmCommand() {
        telemetry.addData("Extension Arm command = ", command.toString());
    }


    //*********************************************************************************************]
    // tests for extension arm
    //**********************************************************************************************

    public void testExtensionArmLimitSwitches() {
        if (extensionLimitSwitch.isPressed()) {
            telemetry.addLine("extension limit switch pressed");
        } else {
            telemetry.addLine("extension limit switch NOT pressed");
        }

        if (retractionLimitSwitch.isPressed()) {
            telemetry.addLine("retraction limit switch pressed");
        } else {
            telemetry.addLine("retraction limit switch NOT pressed");
        }
    }

    //**********************************************************************************************
    //**********************************************************************************************
    //*********************************************************************************************]
    // coordination between extension and rotation
    //**********************************************************************************************
    //**********************************************************************************************
    //**********************************************************************************************

    private enum CollectorExtensionArmStates{
        START,
        EXTEND_RETRACT_ARM,
        RAISE_LOWER_ARM;
    }

    private enum CollectorExtensionArmCommands{
        DROP_ARM,
        RETURN_ARM_HOME,
        RAISE_ARM,
        TRANSFER,
        EMPTY;
    }

    private CollectorExtensionArmStates collectorExtensionArmState = CollectorExtensionArmStates.START;
    private CollectorExtensionArmCommands collectorExtensionArmCommand = CollectorExtensionArmCommands.EMPTY;

    //*********************************************************************************************]
    // extension and rotation Commands
    //**********************************************************************************************

    public void dropArm(){
        log("Commanded to lower collector arm");
        collectorExtensionArmCommand = CollectorExtensionArmCommands.DROP_ARM;
    }
    public void raiseArm(){
        log("Commanded to raise collector arm");
        collectorExtensionArmCommand = CollectorExtensionArmCommands.RAISE_ARM;
    }
    //*********************************************************************************************]
    // extension and rotation Commands
    //**********************************************************************************************

    public void rotationExtensionArmUpdate(){
        logRotationExtensionState(collectorExtensionArmState, collectorExtensionArmCommand);

        switch (collectorExtensionArmState){
            case START:
                switch (collectorExtensionArmCommand){
                    case DROP_ARM:
                        rotationArmGoToCollect();
                        collectorExtensionArmState = CollectorExtensionArmStates.RAISE_LOWER_ARM;
                        break;
                    case RETURN_ARM_HOME:
                        break;
                    case EMPTY:
                        break;
                    case TRANSFER:
                        break;
                    case RAISE_ARM:
                        goToExtensionArmTransfer();
                        collectorExtensionArmState = CollectorExtensionArmStates.EXTEND_RETRACT_ARM;
                        break;
                }
                break;
            case RAISE_LOWER_ARM:
                switch (collectorExtensionArmCommand){
                    case DROP_ARM:
                        if (isRotationArmMovementComplete()){
                            gotoExtensionArm5Inches();
                            collectorExtensionArmState = CollectorExtensionArmStates.EXTEND_RETRACT_ARM;
                            }
                        break;
                    case RETURN_ARM_HOME:
                        break;
                    case EMPTY:
                        break;
                    case TRANSFER:
                        break;
                    case RAISE_ARM:
                        if (isRotationArmMovementComplete()){
                            collectorExtensionArmCommand = CollectorExtensionArmCommands.EMPTY;
                        }
                        break;
                }
                break;
            case EXTEND_RETRACT_ARM:
                switch (collectorExtensionArmCommand){
                    case DROP_ARM:
                        if (isExtensionArmMovementComplete()){
                            collectorExtensionArmState = CollectorExtensionArmStates.START;
                            collectorExtensionArmCommand = CollectorExtensionArmCommands.EMPTY;
                        }
                        break;
                    case RETURN_ARM_HOME:
                        break;
                    case EMPTY:
                        break;
                    case TRANSFER:
                        break;
                    case RAISE_ARM:
                        if (isExtensionArmMovementComplete()){
                            rotationArmGoToTransfer();
                            collectorExtensionArmState = CollectorExtensionArmStates.RAISE_LOWER_ARM;
                        }
                        break;
                }
                break;
        }
    }

    private void logRotationExtensionState(CollectorExtensionArmStates collectorExtensionArmState, CollectorExtensionArmCommands collectorExtensionArmCommand) {
        if (logFile != null && loggingOn) {
            if(collectorExtensionArmState != previousCollectorExtensionArmState ||collectorExtensionArmCommand != previousCollectorExtensionArmCommand) {
                logFile.logData("Collector Arm",collectorExtensionArmState.toString(), collectorExtensionArmCommand.toString());
                previousCollectorExtensionArmState = collectorExtensionArmState;
                previousCollectorExtensionArmCommand = collectorExtensionArmCommand;
            }
        }
    }

    public void displayState(){
        telemetry.addData("State = ", collectorExtensionArmState.toString());
    }

    public boolean isRotationExtensionComplete(){
        if (collectorExtensionArmCommand == CollectorExtensionArmCommands.EMPTY){
            log("Collector arm movement complete");
            return true;
        }
        else return false;
    }
}
