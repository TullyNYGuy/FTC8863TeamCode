package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

import java.security.acl.NotOwnerException;

public class DeliveryLiftSystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum Commands {
        NO_COMMAND,
        GO_TO_BOTTOM,
        GO_TO_TOP,
        GO_TO_POSITION,
        RESET,
        JOYSTICK
    }

    private enum States {
        RESET,
        BOTTOM,
        IN_BETWEEN,
        TOP
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private DcMotor8863 liftMotor;

    private Servo8863 dumpServo;
    private double dumpServoHomePosition = 0.9;
    private double dumpServoDumpPosition = 0.1;
    private double dumpServoInitPosition = 0.5;
    private double dumpServoTransferPosition = 0.88;

    private Switch bottomLimitSwitch;
    private Switch topLimitSwitch;
    private Commands command;
    private Telemetry telemetry;
    private States state;
    private double desiredPosition = 0;
    private double liftPower = 0;

    private double liftSpeed = .5;
    private boolean debugMode = false;

    private DataLogging dataLog;
    private boolean logData = false;

    private double joystickPower = 0;

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
        this.liftSpeed = .2;
        // normally the lift has to be reset before it will accept any commands.
        // This forces it to locate its 0 position before any other commands will
        // run. But when debugging you may not want the lift to have to reset before
        // running any commands. So if the lift is in debug mode, force the state machine to think
        // the lift is IN_BETWEEN so any command sent to the lift will run.
        state = States.IN_BETWEEN;
    }

    public void disableDebugMode() {
        this.debugMode = false;
        this.liftSpeed = .5;
    }

    public void setDataLog(DataLogging dataLog) {
        this.dataLog = dataLog;
    }

    public void enableDataLogging() {
        this.logData = true;
    }

    public void disableDataLogging() {
        this.logData = false;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public DeliveryLiftSystem(HardwareMap hardwareMap, Telemetry telemetry) {
        dumpServo = new Servo8863("dumpServo", hardwareMap, telemetry, dumpServoHomePosition, dumpServoDumpPosition, dumpServoInitPosition, dumpServoInitPosition, Servo.Direction.FORWARD);
        dumpServo.setPositionOne(dumpServoTransferPosition);

        liftMotor = new DcMotor8863("liftMotor", hardwareMap, telemetry);
        liftMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_3_7_ORBITAL_OLD);
        //gear ratio big gear on lift: 76 teeth, small is 48 on motor. lead screw moves 8mm per revolution
        // .19" movement per motor revolution. Need decimal points to force floating point math.
        liftMotor.setMovementPerRev(48.0 / 76.0 * 8.0 / 25.4);

        this.telemetry = telemetry;

        bottomLimitSwitch = new Switch(hardwareMap, "bottomLiftLimitSwitch", Switch.SwitchType.NORMALLY_OPEN);
        topLimitSwitch = new Switch(hardwareMap, "topLiftLimitSwitch", Switch.SwitchType.NORMALLY_OPEN);
        state = States.RESET;
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void init() {
        //dumpServo.goHome();
        if (!isDebugMode()) {
            liftReset();
            while (!isLiftMovementComplete()) {
                update();
            }
        }
    }

    public void shutdown() {
        //dumpServo.goHome();
    }

    //*********************************************************************************************]
    // dump servo commands
    //**********************************************************************************************

    public void deliveryBoxToDump() {
        dumpServo.goUp();
    }

    public void deliveryBoxToHome() {
        dumpServo.goHome();
    }

    public void deliveryBoxToTransfer() {
        dumpServo.goPositionOne();
    }

    public void testSystem() {
        deliveryBoxToDump();
        delay(2000);
        deliveryBoxToHome();
        delay(2000);
        moveToPosition(4, .5);
        delay(2000);
        moveToPosition(0, .5);
    }

    //*********************************************************************************************]
    // lift motor position feedback
    //**********************************************************************************************

    public int getLiftMotorEncoder() {
        return liftMotor.getCurrentPosition();
    }

    public void displayLiftMotorEncoder() {
        telemetry.addData("Encoder = ", getLiftMotorEncoder());
    }

    public double getLiftPosition() {
        return liftMotor.getPositionInTermsOfAttachment();
    }

    public void displayLiftPosition() {
        telemetry.addData("Lift position (inches) = ", getLiftPosition());
    }

    public void displayRequestedLiftPosition() {
        telemetry.addData("Lift position requested (inches) = ", desiredPosition);
    }

    public void displayLiftPower() {
        telemetry.addData("Lift power (inches) = ", liftPower);
    }

    public void displayMotorState() {
        telemetry.addData("Motor state = ", liftMotor.getCurrentMotorState().toString());
    }
    //*********************************************************************************************]
    // lift motor commands
    //**********************************************************************************************

    public void liftReset() {
        command = Commands.RESET;
    }

    public void goToBottom() {
        command = Commands.GO_TO_BOTTOM;
    }

    public void goToTop() {
        command = Commands.GO_TO_TOP;
    }

    public void setLiftPowerUsingJoystick(double power) {
        // if a command has been given to reset the lift, then do not allow the joystick to take
        // effect and override the reset
        joystickPower = 0;
        if (command != Commands.RESET) {
            // A joystick command is only a real command if it is not 0. If the joystick value is 0
            // just ignore the stick
            if (power != 0) {
                command = Commands.JOYSTICK;
                joystickPower = power;
            }
        }
    }

    public void goToLatch() { moveToPosition(10, 1); }

    public void goToHang() {
        moveToPosition(2.5, 1);
    }

    public void goToSetupHang(){moveToPosition(5.9, 1);}

    public void dehang() {
        moveToPosition(11.25, 1);
    }

    public void undehang() {
        moveToPosition(.25, 1);
    }

    /**
     * For testing a move to position
     */
    public void goto5Inches() {
        moveToPosition(5.0, .2);
    }

    /**
     * For testing a move to position
     */
    public void goto8Inches() {
        moveToPosition(8.0, .2);
    }

    public void goToHome() {
        moveToPosition(0.5, 1);
    }

    public void goTo9Inches() {
        moveToPosition(9.0, 1);
    }

    public void goToScoringPosition() {moveToPosition(10.5, 1);}

    public void moveTwoInchesUp() {
        // since the motor starts in RESET state I have to force it into another state in order to
        // get movement
        state = States.IN_BETWEEN;
        moveToPosition(2, .5);
    }

    private void moveToBottom() {
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setPower(-liftSpeed);
    }

    private void moveToTop() {
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setPower(+liftSpeed);
    }

    private void stopLift() {
        liftMotor.setPower(0);
    }

    /**
     * Move to a position based on zero which is set when the lift is all the way down, must run
     * update rotuine in a loop after that.
     *
     * @param heightInInches desired height above the 0 position
     * @param liftPower      max power for the motor
     */
    public void moveToPosition(double heightInInches, double liftPower) {
        desiredPosition = heightInInches;
        this.liftPower = liftPower;
        command = Commands.GO_TO_POSITION;
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.moveToPosition(liftPower, heightInInches, DcMotor8863.FinishBehavior.FLOAT);
    }

    private boolean isLiftMovementUp() {
        // if the position that we want to move to is greater than the current position of the lift,
        // then the movement of the lift will be up. For example, desired position is 10. Current
        // position is 5. So the lift has to move up to get there.
        if (desiredPosition - getLiftPosition() > 0) {
            return true;
        } else {
            return false;
        }
    }

    //*********************************************************************************************]
    // lift motor state machine
    //**********************************************************************************************

    public States update() {
        DcMotor8863.MotorState motorState = liftMotor.update();
        switch (state) {
            case RESET:
                switch (command) {
                    case RESET:
                        // send the lift moving down
                        moveToBottom();
                        // a reset has been requested, wait for the lift to move down and the limit
                        // switch to be pressed.
                        if (bottomLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. Stop the motor and reset the
                            // encoder to 0. Clear the command.
                            stopLift();
                            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                            command = Commands.NO_COMMAND;
                            state = States.BOTTOM;
                        }
                        break;
                    // all other commands are ignored when a reset is issued. Basically force
                    // the command back to a reset
                    case GO_TO_BOTTOM:
                        command = Commands.RESET;
                        break;
                    case GO_TO_TOP:
                        command = Commands.RESET;
                        break;
                    case GO_TO_POSITION:
                        command = Commands.RESET;
                        break;
                    case JOYSTICK:
                        command = Commands.RESET;
                        break;
                    case NO_COMMAND:
                        break;
                }
                break;

            // this state does NOT mean that the lift is at the bottom
            // it means that the lift is moving to the bottom OR at the bottom
            case BOTTOM:
                switch (command) {
                    case RESET:
                        moveToBottom();
                        state = States.RESET;
                        break;
                    case GO_TO_BOTTOM:
                        // the lift has been sent to the bottom without using a position command.
                        // It is just moving down until the motor is told to stop.
                        if (bottomLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. Stop the motor. Clear the command.
                            stopLift();
                            command = Commands.NO_COMMAND;
                        }
                        break;
                    case GO_TO_TOP:
                        // the lift has been requested to move to the top. The motor needs to be
                        // turned on and will run towards the top with just speed control, no position
                        // control
                        moveToTop();
                        state = States.TOP;
                        break;
                    case GO_TO_POSITION:
                        // the lift has been requested to move to a position. The motor has already
                        // been started in position control mode so we don't need to do anything
                        // with the motor. We just need to change state.
                        state = States.IN_BETWEEN;
                        break;
                    case JOYSTICK:
                        processJoystick();
                            break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                }
                break;

                // this state is for when the lift is located somewhere in between the top and bottom
            // and is not moving to the top or moving to the bottom or being reset
            case IN_BETWEEN:
                switch (command) {
                    case RESET:
                        // a reset can be requested at any time. Start the motor movement and change
                        // state
                        moveToBottom();
                        state = States.RESET;
                        break;
                    case GO_TO_BOTTOM:
                        // the lift has been requested to move to the bottom. The motor needs to be
                        // turned on and will run towards the bottom with just speed control, no position
                        // control
                        moveToBottom();
                        state = States.BOTTOM;
                        break;
                    case GO_TO_TOP:
                        // the lift has been requested to move to the top. The motor needs to be
                        // turned on and will run towards the top with just speed control, no position
                        // control
                        moveToTop();
                        state = States.TOP;
                        break;
                    case GO_TO_POSITION:
                        // the lift has been requested to move to a position. The motor has already
                        // been started in position control mode so we need to watch to determine
                        // the motor actually reaches the position
                        if (liftMotor.isMotorStateComplete()) {
                            // the movement is finished and the motor stopped in the position, but
                            // it still has power applied to it. Stop the motor.
                            stopLift();
                            command = Commands.NO_COMMAND;
                        }

                        // check to make sure the top limit switch has not been tripped. If it has
                        // then something went wrong or someone gave a bad motor command.
                        if (topLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. If the movement is supposed to be
                            // up, then Stop the motor. Clear the command.
                            if (isLiftMovementUp()) {
                                stopLift();
                                command = Commands.NO_COMMAND;
                                state = States.TOP;
                            } else {
                                // the top limit switch is pressed but the movement is supposed to be
                                // down so do nothing. This allows downward movement.
                            }
                        }

                        // check to make sure the bottom limit switch has not been tripped. If it has
                        // then something went wrong or someone gave a bad motor command.
                        if (bottomLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. If the movement is supposed to be
                            // down, then Stop the motor. Clear the command.
                            if (!isLiftMovementUp()) {
                                stopLift();
                                command = Commands.NO_COMMAND;
                                state = States.BOTTOM;
                            } else {
                                // the bottom limit switch is pressed but the movement is supposed to be
                                // up so do nothing. This allows upward movement.
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

            // this state does NOT mean that the lift is at the top
            // it means that the lift is moving to the top OR is at the top
            case TOP:
                switch (command) {
                    case RESET:
                        // a reset can be requested at any time. Start the motor movement and change
                        // state
                        moveToBottom();
                        state = States.RESET;
                        break;
                    case GO_TO_BOTTOM:
                        // the lift has been requested to move to the bottom. The motor needs to be
                        // turned on and will run towards the bottom with just speed control, no position
                        // control
                        moveToBottom();
                        state = States.BOTTOM;
                        break;
                    case GO_TO_TOP:
                        // the lift has been sent to the top without using a position command.
                        // It is just moving up until the motor is told to stop.
                        if (topLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. Stop the motor. Clear the command.
                            stopLift();
                            command = Commands.NO_COMMAND;
                        }
                        break;
                    case GO_TO_POSITION:
                        // the lift has been requested to move to a position. The motor has already
                        // been started in position control mode so we don't need to do anything
                        // with the motor. We just need to change state.
                        state = States.IN_BETWEEN;
                        break;
                        // the lift power is being set with a joystick. The lift must have hit the
                    // upper limit switch to be in this state
                    case JOYSTICK:
                        processJoystick();
                        break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                }
                break;
        }
        return state;
    }

    public boolean isLiftMovementComplete() {
        if (command == Commands.NO_COMMAND) {
            return true;
        } else {
            return false;
        }
    }

    private void processJoystick() {
        if (bottomLimitSwitch.isPressed()) {
            // if the lift is at the bottom, only allow it to move up
            if (joystickPower > 0) {
                liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                liftMotor.setPower(joystickPower);
            } else {
                // the lift power is negative so the driver wants it to move down. But it is already
                // at the bottom so force the motor power to 0.
                liftMotor.setPower(0);
            }
            state = States.BOTTOM;
        } else {
            if (topLimitSwitch.isPressed()) {
                // if the lift is at the top, only allow it to move down
                if (joystickPower < 0) {
                    liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    liftMotor.setPower(joystickPower);
                } else {
                    // the lift power is positive so the driver wants it to move up. But it is already
                    // at the top so force the motor power to 0.
                    liftMotor.setPower(0);
                }
                state = States.TOP;
            } else {
                // both limit switches are not pressed, allow it to move either way
                if (joystickPower != 0) {
                    liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    liftMotor.setPower(joystickPower);
                } else {
                    // the joystick input is 0 so set the lift power to 0
                    liftMotor.setPower(0);
                }
                state = States.IN_BETWEEN;
            }
        }
    }

    public void displayLiftState() {
        telemetry.addData("Lift State = ", state.toString());
    }


    public void displayLiftCommand() {
        telemetry.addData("Lift command = ", command.toString());
    }

    public void testMotorModeSwitch() {
        // reset the lift
        liftReset();
        while (!isLiftMovementComplete()) {
            update();
        }

        // move the lift 2 inches up and display
        moveTwoInchesUp();
        while (!isLiftMovementComplete()) {
            update();
        }
        telemetry.addLine("lift reset");
        displayLiftMotorEncoder();
        displayLiftPosition();
        displayLiftState();
        telemetry.update();
        delay(4000);

        // switch modes - hopefully the enocder value does not change
        liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        telemetry.addLine("lift motor mode switched to run without encoder");
        displayLiftMotorEncoder();
        displayLiftPosition();
        displayLiftState();
        telemetry.update();
        delay(4000);

        // switch modes - hopefully the enocder value does not change
        telemetry.addLine("lift motor mode switched to run to position");
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        displayLiftMotorEncoder();
        displayLiftPosition();
        displayLiftState();
        telemetry.update();
        delay(4000);

        // move the lift 2 inches up and display
        goto5Inches();
        while (!isLiftMovementComplete()) {
            update();
        }
        telemetry.addLine("moved to 5 inches");
        displayLiftMotorEncoder();
        displayLiftPosition();
        displayLiftState();
        telemetry.update();
        delay(4000);
    }

    public void testLiftLimitSwitches() {
        if (bottomLimitSwitch.isPressed()) {
            telemetry.addLine("bottom limit switch pressed");
        } else {
            telemetry.addLine("bottom limit switch NOT pressed");
        }

        if (topLimitSwitch.isPressed()) {
            telemetry.addLine("top limit switch pressed");
        } else {
            telemetry.addLine("top limit switch NOT pressed");
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
