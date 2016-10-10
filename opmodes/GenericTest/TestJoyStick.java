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

package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;

/**
 * TestJoyStick is meant to provide the driver wtih a way to test the various joystick control
 * methods.
 * Differential drive - one joystick for both speed and direction - use gamepad1 left joystick
 * Tank drive - two joysticks, one for left motor speed and one for right - use gamepad2 left
 *  and right joystick
 * Turn deadband compensation on or off (toggle) - gamepad1 left bumper
 *
 * It also allows the driver to control a repel servo
 *  gamepad1 a button = down
 *  gamepad1 y button = up
 *  gamepad1 x button = .5 (halfway)
 * <p>
 */
public class TestJoyStick extends OpMode {

	/*
	 * Note: the configuration of the servos is such that
	 * as the arm servo approaches 0, the arm position moves up (away from the floor).
	 * Also, as the claw servo approaches 0, the claw opens up (drops the game element).
	 */
	// TETRIX VALUES.
	final static double REPEL_MIN  = 0.0;
	final static double REPEL_MAX  = 1.0;
    final static double JOYSTICK_DEADBAND_VALUE = .15;

	// position of the arm servo.
	double leftRepelServoPosition;

	DcMotor motorRight;
	DcMotor motorLeft;
	Servo leftRepelServo;

    JoyStick driverDiffLeftJoyStickY;
    JoyStick driverDiffLeftJoyStickX;

    JoyStick driverTankLeftJoyStickY;
    JoyStick driverTankRightJoyStickY;

    boolean deadBandOn = true;

	/**
	 * Constructor
	 */
	public TestJoyStick() {

	}

	/*
	 * Code to run when the op mode is first enabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {


		/*
		 * Use the hardwareMap to get the dc motors and servos by name. Note
		 * that the names of the devices must match the names used when you
		 * configured your robot and created the configuration file.
		 */
		
		/*
		 * For the demo Tetrix K9 bot we assume the following,
		 *   There are two motors "leftDriveMotor" and "rightDriveMotor"
		 *   "rightDriveMotor" is on the right side of the bot and reversed.
		 *   "leftDriveMotor" is on the left side of the bot.
		 *   
		 * We also assume that there is  one servo "leftRepelServo"
		 *    "leftRepelServo" controls the arm to hit the repel triggers on the left side
		 */
		motorRight = hardwareMap.dcMotor.get("rightDriveMotor");
		motorLeft = hardwareMap.dcMotor.get("leftDriveMotor");
		motorRight.setDirection(DcMotor.Direction.REVERSE);

		leftRepelServo = hardwareMap.servo.get("leftRepelServo");

        driverDiffLeftJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        driverDiffLeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        driverTankLeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);
        driverTankRightJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

		// assign the starting position of the repel servo
		leftRepelServoPosition = 0.5;

		// create a new joystick object

	}

    @Override
    public void start() {

        leftRepelServo.setPosition(leftRepelServoPosition);
    }

	@Override
	public void loop() {

        float right = 0;
        float left = 0;

        // Gamepad 1
        // Gamepad 1 controls the motors via the left stick, and it controls the
        // repel servo via the a,b, x, y buttons
        // Gamepad1 will be differential drive with 1 joystick controlling speed and direction

		// throttle: left_stick_y ranges from -1 to 1, where -1 is full up, and
		// 1 is full down
		// direction: left_stick_x ranges from -1 to 1, where -1 is full left
		// and 1 is full right
        float yStick = gamepad1.left_stick_y;
		float throttle = (float)driverDiffLeftJoyStickY.scaleInput(gamepad1.left_stick_y);
		float direction = (float)driverDiffLeftJoyStickX.scaleInput(gamepad1.left_stick_x);
		float rightDiff = throttle - direction;
		float leftDiff = throttle + direction;

        // Gamepad 2
        // The driver can use gamepad2 to control the drive motors. Gamepad2 will be tank drive.
        // The left joystick controls the left motor and the right joystick controls the right
        // motor.
        float leftTank = (float)driverTankLeftJoyStickY.scaleInput(gamepad2.left_stick_y);
        float rightTank = (float)driverTankRightJoyStickY.scaleInput(gamepad2.right_stick_y);

        // Now if there is input on gamepad1 use that, if not then see if there is input on
        // gamepad2 and use that. If neither of the gamepads have input then the motor powers are
        // set to 0.
        if (gamepad1.left_stick_x != 0 || gamepad1.left_stick_y != 0) {
            right = rightDiff;
            left = leftDiff;
            yStick = gamepad1.left_stick_y;
        } else {
            if (gamepad2.left_stick_y != 0 || gamepad2.right_stick_y != 0) {
                right = rightTank;
                left = leftTank;
                yStick = gamepad2.left_stick_y;
            } else {
                right = 0;
                left = 0;
                yStick = 0;
            }
        }
		// clip the right/left values so that the values never exceed +/- 1
		right = Range.clip(right, -1, 1);
		left = Range.clip(left, -1, 1);
		
		// write the values to the motors
		motorRight.setPower(right);
		motorLeft.setPower(left);

		// update the position of the left repel servo.
		if (gamepad1.a) {
			// if the A button is pushed on gamepad1, increment the position of
			// the arm servo.
			leftRepelServoPosition = REPEL_MIN;
		}

		if (gamepad1.y) {
			// if the Y button is pushed on gamepad1, decrease the position of
			// the arm servo.
			leftRepelServoPosition = REPEL_MAX;
		}

        // update the position of the left repel servo.
        if (gamepad1.x) {
            // if the A button is pushed on gamepad1, increment the position of
            // the arm servo.
            leftRepelServoPosition = 0.5;
        }

        // clip the position values so that they never exceed their allowed range.
		leftRepelServoPosition = Range.clip(leftRepelServoPosition, REPEL_MIN, REPEL_MAX);

		// write position value to the left repel servo
		leftRepelServo.setPosition(leftRepelServoPosition);

        // Check to see if the deadband compensation should be toggled on or off
        if (gamepad1.left_bumper) {
            deadBandOn = toggleDeadBandValue();
        }

		/*
		 * Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */
        telemetry.addData("arm", "leftRepel:  " + String.format("%.2f", leftRepelServoPosition));
        telemetry.addData("left Y joystick",  "joy Y: " + String.format("%.2f", yStick));
        telemetry.addData("left Y scaled", "joy Y: " + String.format("%.2f", left));
        if(deadBandOn) {
            telemetry.addData("Deadband:", "ON");
        } else {
            telemetry.addData("Deadband:", "OFF");
        }

	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {

    }

    public boolean toggleDeadBandValue () {

        boolean deadBandOn = true;

        if(driverDiffLeftJoyStickX.getDeadBand() == 0) {
            // deadband compensation is currently disabled, enable it
            driverDiffLeftJoyStickX.setDeadBand(JOYSTICK_DEADBAND_VALUE);
            driverDiffLeftJoyStickY.setDeadBand(JOYSTICK_DEADBAND_VALUE);
            driverTankLeftJoyStickY.setDeadBand(JOYSTICK_DEADBAND_VALUE);
            driverTankRightJoyStickY.setDeadBand(JOYSTICK_DEADBAND_VALUE);
            deadBandOn = true;
        } else {
            // deadband compensation is currenly endableo, disable it
            driverDiffLeftJoyStickX.setDeadBand(0);
            driverDiffLeftJoyStickY.setDeadBand(0);
            driverTankLeftJoyStickY.setDeadBand(0);
            driverTankRightJoyStickY.setDeadBand(0);
            deadBandOn = false;
        }
        return deadBandOn;
    }
}
