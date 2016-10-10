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

package org.firstinspires.ftc.teamcode.opmodes.ResQTest;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
public class SweeperTest extends OpMode {

	/*
	 * Note: the configuration of the servos is such that
	 * as the arm servo approaches 0, the arm position moves up (away from the floor).
	 * Also, as the claw servo approaches 0, the claw opens up (drops the game element).
	 */

	DcMotor motorSweeper;

	double sweeperPower = 0;

	/**
	 * Constructor
	 */
	public SweeperTest() {

	}

	public enum testState {
		STATE1, STATE2, STATE3, STATE4, STATE5
	}

	public testState currentState = testState.STATE1;

    public ElapsedTime stateTimer;


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
		 *   There are two motors "motor_1" and "motor_2"
		 *   "motor_1" is on the right side of the bot.
		 *   "motor_2" is on the left side of the bot and reversed.
		 *   
		 * We also assume that there are two servos "servo_1" and "servo_6"
		 *    "servo_1" controls the arm joint of the manipulator.
		 *    "servo_6" controls the claw joint of the manipulator.
		 */
		motorSweeper = hardwareMap.dcMotor.get("rightDriveMotor");
		//motorLeft = hardwareMap.dcMotor.get("leftDriveMotor");
		motorSweeper.setDirection(DcMotor.Direction.REVERSE);

		motorSweeper.setMode(DcMotor.RunMode.RESET_ENCODERS);
		//motorLeft.setMode(DcMotorController.RunMode.RESET_ENCODERS);

		motorSweeper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
		//motorLeft.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        stateTimer = new ElapsedTime(0);

	}

    public void start() {
        stateTimer.reset();
    }

	/*
	 * This method will be called repeatedly in a loop
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
	 */
	@Override
	public void loop() {

		sweeperPower = -gamepad1.left_stick_y;
		sweeperPower = Range.clip(sweeperPower, -1, 1);
		motorSweeper.setPower(sweeperPower);

       /* switch (currentState) {
			case STATE1:
				motorSweeper.setPower(1.0);
				//motorLeft.setPower(-.50);
                currentState = testState.STATE2;
				break;

			case STATE2:
                //if timer > 10 sec then
                // set a new target position
                // set a power
                // set the currentState to the next state
                // reset the timer to 0
                // if not > 10 sec do nothing
                if (stateTimer.time() > 5.0) {
					motorSweeper.setPower(0);

                    currentState = testState.STATE3;
                    stateTimer.reset();
                }
				break;

			case STATE3:
                //if timer > 10 sec then
                // set a new target position
                // set a power
                // set the currentState to the next state
                // reset the timer to 0
                // if not > 10 sec do nothing
                if (stateTimer.time() > 5.0) {
					motorSweeper.setPower(-1.0);

                    currentState = testState.STATE4;
                    stateTimer.reset();
                }
				break;

			case STATE4:
                //if timer > 10 sec then
                // set a new target position
                // set a power
                // set the currentState to the next state
                // reset the timer to 0
                // if not > 10 sec do nothing
                if (stateTimer.time() > 5.0) {
					motorSweeper.setPower(0);
                    currentState = testState.STATE5;

                    stateTimer.reset();
                }
				break;

			case STATE5:

				break;

		}*/



		/*
		 * Send telemetry data back to driver station. Note that if we are using
		 * a legacy NXT-compatible motor controller, then the getPower() method
		 * will return a null value. The legacy NXT-compatible motor controllers
		 * are currently write only.
		 */
        telemetry.addData("Current", "State: " + currentState.toString());
        telemetry.addData("right", "Encoder: " + String.format("%1$d", motorSweeper.getCurrentPosition()));
        //telemetry.addData("left", "Encoder: " + String.format("%1$d", motorLeft.getCurrentPosition()));

	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {

	}
}
