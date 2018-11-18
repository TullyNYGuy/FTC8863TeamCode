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

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

@TeleOp(name = "Test generic servo", group = "Test")
@Disabled

public class TestServo8863 extends OpMode {
    boolean genericServoActive = true;
    double upPosition = .8;
    double downPosition = .2;
    double homePosition = .8;
    double lowerRepelPosition = .4;
    double middleRepelPosition = .5;
    double upperRepelPosition = .6;
    double initPosition = homePosition;
    Servo8863 genericServo;

	/**
	 * Constructor
	 */
	public TestServo8863() {

	}

	/*
	 * Code to run when the op mode is first enabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {
        genericServo = new Servo8863("GenericServo", hardwareMap, telemetry, homePosition, upPosition, downPosition, initPosition, Servo.Direction.REVERSE);

        genericServo.setPositionOne(lowerRepelPosition);
        genericServo.setPositionTwo(middleRepelPosition);
        genericServo.setPositionThree(upperRepelPosition);
	}

    @Override
    public void start(){
        genericServo.goHome();
    }

	@Override
	public void loop() {

        genericServo.updateWiggle();

        if (gamepad2.a) {
                genericServo.goPositionOne();
                telemetry.addData("leftRepel", "is lower");
        }

        if (gamepad2.b) {
            genericServo.goHome();
            telemetry.addData("leftRepel", "home");
        }

        if (gamepad2.x) {
                genericServo.goPositionTwo();
                telemetry.addData("leftRepel", "is position 2");
        }

        if (gamepad2.y) {
                genericServo.goPositionThree();
                telemetry.addData("leftRepel", "is position 3");
        }

        if (gamepad2.left_bumper) {
            double wiggleDelay = .5;
            double wiggleDelta = -.5;
            double wiggleTime = 5.0;

            genericServo.startWiggle(upPosition, wiggleDelay, wiggleDelta, wiggleTime);
        }

        if (gamepad2.right_bumper) {
            genericServo.stopWiggle();
        }

        telemetry.addData("Position",  "Position: " + String.format("%.2f", genericServo.getPosition()));
	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {
        genericServo.goHome();
    }
}
