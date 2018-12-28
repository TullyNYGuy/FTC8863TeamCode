package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
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
        RESET
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
    private double dumpServoTransferPosition = 0.7;
    private Switch bottomLimitSwitch;
    private Switch topLimitSwitch;
    private Commands command;
    private Telemetry telemetry;
    private States state;

    private double liftSpeed = .5;
    private boolean debugMode = false;
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
    }

    public void disableDebugMode() {
        this.debugMode = false;
        this.liftSpeed = .5;
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
        liftMotor.setMovementPerRev(48.0/76.0*8.0/25.4);

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
        //liftReset();
    }

    public void shutdown() {
        //dumpServo.goHome();
        //liftReset();
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
    // lift motor commands
    //**********************************************************************************************

    public void getLiftMotorEncoder() {
        int encoderValue = liftMotor.getCurrentPosition();
        telemetry.addData("Encoder= ", encoderValue);
    }

    public void liftReset() {
        command = Commands.RESET;
    }

    public void goToBottom() {
        command = Commands.GO_TO_BOTTOM;
    }

    public void goToTop() {
        command = Commands.GO_TO_TOP;
    }

    public void goToLatch() {
        moveToPosition(10, 1);
    }

    public void dehang() {
        moveToPosition(11.25, 1);
    }

    public void undehang() {
        moveToPosition(.25, 1);
    }

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

    public double getLiftPosition() {
        return liftMotor.getPositionInTermsOfAttachment();
    }

    /**
     * Move to a position based on zero which is set when the lift is all the way down, must run
     * update rotuine in a loop after that.
     *
     * @param heightInInches how high the lift will go up relative to all the way down
     */
    public void moveToPosition(double heightInInches, double liftPower) {
        command = Commands.GO_TO_POSITION;
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.moveToPosition(liftPower, heightInInches, DcMotor8863.FinishBehavior.FLOAT);
    }

    public States update() {
        DcMotor8863.MotorState motorState = liftMotor.update();
        switch (state) {
            case RESET:
                switch (command) {
                    case RESET:
                        // send the lift moving down
                        moveToBottom();
                        state = States.BOTTOM;
                        break;
                        // all other commands are ignored when a reset is issued
                    case GO_TO_BOTTOM:
                        break;
                    case GO_TO_TOP:
                        break;
                    case GO_TO_POSITION:
                        break;
                    case NO_COMMAND:
                        break;
                }
                break;
                // this state does NOT mean that the lift is at the bottom
            // it means that the lift is moving to the bottom
            case BOTTOM:
                switch (command) {
                    case RESET:
                        // a reset has been requested, wait for the lift to move down and the limit
                        // switch to be pressed.
                        if (bottomLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. Stop the motor and reset the
                            // encoder to 0. Clear the command.
                            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                            command = Commands.NO_COMMAND;
                        }
                        break;
                    case GO_TO_BOTTOM:
                        // the lift has been sent to the bottom without using a position command.
                        // It is just moving down until the motor is told to stop.
                        if (bottomLimitSwitch.isPressed()) {
                            // the limit switch has been pressed. Stop the motor and reset the
                            // encoder to 0. Clear the command.
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
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                }
                break;
            case IN_BETWEEN:
                switch (command) {
                    case RESET:
                        // a reset can be requested at any time. Start the motor movement and change
                        // state
                        moveToBottom();
                        state = States.BOTTOM;
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
                        break;
                    case NO_COMMAND:
                        // don't do anything, just hang out
                        break;
                }
                break;
            case TOP:
                switch (command) {
                    case RESET:
                        // a reset can be requested at any time. Start the motor movement and change
                        // state
                        moveToBottom();
                        state = States.BOTTOM;
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
                            // the limit switch has been pressed. Stop the motor and reset the
                            // encoder to 0. Clear the command.
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
