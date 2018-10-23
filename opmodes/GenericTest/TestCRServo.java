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
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo8863;
import org.firstinspires.ftc.teamcode.Lib.ResQLib.RobotConfigMapping;

import static org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest.getcrServoName;
import static org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest.getleftMotorName;

@Deprecated // use TestCRServoLinearOpMode instead

@TeleOp(name = "Test CR Servo", group = "Test")
@Disabled
public class TestCRServo extends OpMode {

    double noMovePositionReverse = .46;
    double noMovePositionForward = .50;
    double deadZone = .1;
    CRServo8863 testServo;

	ElapsedTime timer;

    int step = 1;
    double command = 0;
    double startCommand = .4;
    double endCommand = .5;
    double commandIncrement = .01;
    int stepLength = 1000; // milliseconds

	/**
	 * Constructor
	 */
	public TestCRServo() {

	}

	/*
	 * Code to run when the op mode is first enabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
	 */
	@Override
	public void init() {

        testServo = new CRServo8863(RobotConfigMappingForGenericTest.getgenericServoName(),hardwareMap,
                noMovePositionForward, noMovePositionReverse, deadZone, Servo.Direction.REVERSE,
                telemetry);
        timer = new ElapsedTime();
        testServo.setPosition(testServo.getCenterValue());

    }

    @Override
    public void start() {
        timer.reset();
        command = startCommand;
    }

	@Override
	public void loop() {



        if (timer.milliseconds() < 2000) {
            testServo.setSpeed(1.0);
            telemetry.addData("Time = ", "%3.2f", timer.milliseconds()/1000);
            telemetry.update();
        }

        testServo.setSpeed(noMovePositionForward);

//            if (timer.milliseconds() > step * stepLength && command <= endCommand) {
//                step++;
//                command = command + commandIncrement;
//                testServo.setPosition(command);
//            }
//            telemetry.addData("Step = ", "%d", step);
//            telemetry.addData("Command = ", "%3.2f", command);
//            telemetry.update();
//
//        if(command == 1.0) {
//            stop();
//        }

//        telemetry.addData("Text", "*** Robot Data***");
//        telemetry.addData("slide", "position:  " + String.format("%.2f", testServo.getPosition()));

	}

	/*
	 * Code to run when the op mode is first disabled goes here
	 * 
	 * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
	 */
	@Override
	public void stop() {

    }

    public void findNoMovementCommand() {
        timer.reset();
        double step = 1;
        double command = 0;
        double commandIncrement = .05;
        int stepLength = 500; // milliseconds
        while (command <= 1.0) {
            if (timer.milliseconds() > step * 500) {
                step++;
                testServo.setPosition(command);
            }
            telemetry.addData("Step = ", "%d", step);
            telemetry.addData("Command = ", "%3.2f", command);
            telemetry.update();
        }
    }
}
