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
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.ResQLib.ResQRobot;

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
public class TestBarGrabberGB extends OpMode {

    public ResQRobot robot;
    double position = 0;
    double position_increment = .05;
    double position_max = 1.0;
    ElapsedTime timer;
    double timeLimit = 3.0;
    int i = 0;

    //public Servo barGrabberServo;

	/**
	 * Constructor
	 */
	public TestBarGrabberGB() {

	}

	/*
	 * Code to run when the op mode is first enabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {
        robot = ResQRobot.ResQRobotTeleop(hardwareMap, telemetry);
        timer = new ElapsedTime();
    }

    @Override
    public void start() {
        timer.reset();
        //robot.barGrabberServo.setPosition(position);
        telemetry.addData("servo", "cmd position" + String.format("%.2f", position));
    }

	@Override
	public void loop() {

/*		if (gamepad1.b) {
			robot.barGrabberServo.goHome();
		}

		if (gamepad1.a) {
			robot.barGrabberServo.goGrabBar();
		}*/
        // a comment
        //if (gamepad1.dpad_up) {
            // go at servo direct
        //}

/*        if (gamepad1.dpad_down) {
            // go at servo direct
        }*/


        while(i < 11) {
            if( timer.time() > timeLimit && position <= position_max) {
                position = (double)i/10;
                //robot.barGrabberServo.setPosition(position);
                timer.reset();
            }
        }

        telemetry.addData("servo cmd", "position" + String.format("%.2f", position));
        //telemetry.addData("servo actual", "position" + String.format("%.2f", robot.barGrabberServo.getPosition()));

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
