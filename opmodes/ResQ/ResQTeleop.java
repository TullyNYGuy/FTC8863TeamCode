/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package org.firstinspires.ftc.teamcode.opmodes.ResQ;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

/**
 * TestJoyStick is meant to provide the driver wtih a way to test the various joystick control
 * methods.
 * Differential drive - one joystick for both speed and direction - use gamepad2 left joystick
 * Tank drive - two joysticks, one for left motor speed and one for right - use gamepad2 left
 *  and right joystick
 * Turn deadband compensation on or off (toggle) - gamepad2 left bumper
 *
 * It also allows the driver to control a repel servo
 *  gamepad2 a button = down
 *  gamepad2 y button = up
 *  gamepad2 x button = .5 (halfway)
 * <p>
 */
public class ResQTeleop extends OpMode {
    boolean leftRepelServoActive = true;

    double upPosition = .8;
    double downPosition = .2;
    double homePosition = .8;

    double lowerRepelPosition = .25;
    double middleRepelPosition = .1;
    double upperRepelPosition = .1;

    double leftSupportArmPositionHome = 1.0;
    double leftSupportArmPositionUp = 1.0;
    double leftSupportArmpositionDown = .35;


    double rightSupportArmPositionHome = 1.0;
    double rightSupportArmPositionUp = 1.0;
    double rightSupportArmpositionDown = .35;

    Servo8863 leftSupportArmServo;
    Servo8863 rightSupportArmServo;
    Servo8863 leftRepelServo;
    Servo8863 rightRepelServo;

    DcMotor motorRight;
    DcMotor motorLeft;

    JoyStick driverDiffLeftJoyStickY;
    JoyStick driverDiffLeftJoyStickX;

    JoyStick driverTankLeftJoyStickY;
    JoyStick driverTankRightJoyStickY;

    final static double JOYSTICK_DEADBAND_VALUE = .15;

    float throttle = 0;
    float direction = 0;

    float leftTank = 0;
    float rightTank = 0;
    float powerReductionFactor = 1;

    boolean useTankDrive = true;
	/**
	 * Constructor
	 */
	public ResQTeleop() {

	}

	/*
	 * Code to run when the op mode is first enabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {

        //leftRepelServo = new Servo8863("leftRepelServo", hardwareMap, homePosition, upPosition, downPosition, Servo.Direction.REVERSE);
        //rightRepelServo =new Servo8863("rightRepelServo",hardwareMap, homePosition, upPosition, downPosition, Servo.Direction.FORWARD);

        //leftSupportArmServo =new Servo8863("leftSupportArmServo",hardwareMap, leftSupportArmPositionHome, leftSupportArmPositionUp, leftSupportArmpositionDown, Servo.Direction.FORWARD);
        //rightSupportArmServo =new Servo8863("rightSupportArmServo",hardwareMap, rightSupportArmPositionHome, rightSupportArmPositionUp, rightSupportArmpositionDown, Servo.Direction.REVERSE);

        driverDiffLeftJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        driverDiffLeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        driverTankLeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);
        driverTankRightJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        leftRepelServo.setPositionOne(lowerRepelPosition);
        leftRepelServo.setPositionTwo(middleRepelPosition);
        leftRepelServo.setPositionThree(upperRepelPosition);
        rightRepelServo.setPositionOne(lowerRepelPosition);
        rightRepelServo.setPositionTwo(middleRepelPosition);
        rightRepelServo.setPositionThree(upperRepelPosition);

        leftSupportArmServo.setUpPosition(leftSupportArmPositionUp);
        leftSupportArmServo.setDownPosition(leftSupportArmpositionDown);

        rightSupportArmServo.setUpPosition(rightSupportArmPositionUp);
        rightSupportArmServo.setDownPosition(rightSupportArmpositionDown);

        motorRight = hardwareMap.dcMotor.get("rightDriveMotor");
        motorLeft = hardwareMap.dcMotor.get("leftDriveMotor");
        motorRight.setDirection(DcMotor.Direction.REVERSE);

    }

    @Override
    public void start() {
        leftRepelServo.goHome();
        rightRepelServo.goHome();

        leftSupportArmServo.goUp();
        rightSupportArmServo.goUp();
    }

	@Override
	public void loop() {

        if (gamepad1.left_bumper) {
            useTankDrive = true;
        }

        if (gamepad1.right_bumper) {
            useTankDrive = false;
        }

        if (gamepad2.left_bumper) {
            leftRepelServoActive = true;
        }

        if (gamepad2.right_bumper) {
            leftRepelServoActive = false;
        }

        // write the values to the motors
        if (useTankDrive) {

            float rightTank = (float)driverTankRightJoyStickY.scaleInput(gamepad1.right_stick_y);
            float leftTank = (float)driverTankLeftJoyStickY.scaleInput(gamepad1.left_stick_y);

            rightTank = Range.clip(rightTank, -1, 1);
            leftTank = Range.clip(leftTank, -1, 1);

            motorRight.setPower(rightTank * powerReductionFactor);
            motorLeft.setPower(leftTank * powerReductionFactor);

        } else {
            float throttle = (float)driverDiffLeftJoyStickY.scaleInput(gamepad1.left_stick_y);
            float direction = (float)driverDiffLeftJoyStickX.scaleInput(gamepad1.left_stick_x);

            float rightDiff = throttle - direction;
            float leftDiff = throttle + direction;

            rightDiff = Range.clip(rightDiff, -1, 1);
            leftDiff = Range.clip(leftDiff, -1, 1);

            motorRight.setPower(rightDiff * powerReductionFactor);
            motorLeft.setPower(leftDiff * powerReductionFactor);
        }

        if (gamepad1.a) {
            powerReductionFactor = .5f;
        }

        if (gamepad1.b) {
            powerReductionFactor = 1f;
        }

        if (gamepad1.x) {
            powerReductionFactor = .7f;
        }

        if (gamepad2.a) {
            // if the A button is pushed on gamepad2, increment the position of
            // the arm servo.
            if (leftRepelServoActive) {
                leftRepelServo.goPositionOne();
                rightRepelServo.goHome();
                telemetry.addData("leftRepel", "is lower");
                telemetry.addData("rightRepel", "home");
            } else {
                rightRepelServo.goPositionOne();
                leftRepelServo.goHome();
                telemetry.addData("rightRepel", "is lower");
                telemetry.addData("leftRepel", "home");
            }
        }

        if (gamepad2.b) {
            // if the B button is pushed on gamepad2, increment the position of
            // the arm servo.
            leftRepelServo.goHome();
            rightRepelServo.goHome();
            telemetry.addData("leftRepel", "home");
            telemetry.addData("rightRepel", "home");
        }

        if (gamepad2.x) {
            // if the X button is pushed on gamepad2, decrease the position of
            // the arm servo.
            if (leftRepelServoActive) {
                leftRepelServo.goPositionTwo();
                rightRepelServo.goHome();
                telemetry.addData("leftRepel", "is middle");
                telemetry.addData("rightRepel", "home");
            } else {
                rightRepelServo.goPositionTwo();
                leftRepelServo.goHome();
                telemetry.addData("rightRepel", "is middle");
                telemetry.addData("leftRepel", "home");
            }
        }

        if (gamepad2.y) {
            // if the Y button is pushed on gamepad2, decrease the position of
            // the arm servo.
            if (leftRepelServoActive) {
                leftRepelServo.goPositionThree();
                rightRepelServo.goHome();
                telemetry.addData("leftRepel", "is upper");
                telemetry.addData("rightRepel", "home");
            } else {
                rightRepelServo.goPositionThree();
                leftRepelServo.goHome();
                telemetry.addData("rightRepel", "is upper");
                telemetry.addData("leftRepel", "home");
            }
        }

        if (gamepad2.dpad_up) {
            leftSupportArmServo.goUp();
            rightSupportArmServo.goUp();
            telemetry.addData("Suppport arm", "Up");
        }

        if (gamepad2.dpad_down) {
            leftSupportArmServo.goDown();
            rightSupportArmServo.goDown();
            telemetry.addData("Suppport arm", "Down");
        }
	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {
        leftRepelServo.goHome();
        rightRepelServo.goHome();

        leftSupportArmServo.goUp();
        rightSupportArmServo.goUp();
    }
}
