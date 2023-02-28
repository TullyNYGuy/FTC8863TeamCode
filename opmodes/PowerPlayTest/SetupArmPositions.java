/*
Copyright (c) 2018 FIRST

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of FIRST nor the names of its contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.ConeGrabberArmServo;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.ConeGrabberServo;

/**
 * {@link SetupArmPositions} illustrates how to use the REV Robotics
 * Time-of-Flight Range Sensor.
 * <p>
 * The op mode assumes that the range sensor is configured with a name of "sensor_range".
 * <p>
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 *
 * @see <a href="http://revrobotics.com">REV Robotics Web Page</a>
 */
@TeleOp(name = "Setup PP Arm Positions", group = "Test")
//@Disabled
public class SetupArmPositions extends LinearOpMode {

    private ConeGrabberServo coneGrabber;
    private ConeGrabberArmServo coneGrabberArmServo;

    @Override
    public void runOpMode() {
        // you can use this as a regular DistanceSensor.
        coneGrabber = new ConeGrabberServo(hardwareMap, telemetry);
        coneGrabberArmServo = new ConeGrabberArmServo(hardwareMap, telemetry);

        telemetry.addData(">>", "Press start to continue");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            coneGrabber.close();
            if (coneGrabberArmServo.getServoPosition("Init").getPosition() != ConeGrabberArmServo.INIT_POSITION) {
                coneGrabberArmServo.changeServoPosition("Init", ConeGrabberArmServo.INIT_POSITION);
                coneGrabberArmServo.init();
            }
            if (coneGrabberArmServo.getServoPosition("Release").getPosition() != ConeGrabberArmServo.RELEASE_POSITION) {
                coneGrabberArmServo.changeServoPosition("Release", ConeGrabberArmServo.RELEASE_POSITION);
                coneGrabberArmServo.releasePosition();
            }
            if (coneGrabberArmServo.getServoPosition("Pickup").getPosition() != ConeGrabberArmServo.PICKUP) {
                coneGrabberArmServo.changeServoPosition("Pickup", ConeGrabberArmServo.PICKUP);
                coneGrabberArmServo.pickupPosition();
            }
            if (coneGrabberArmServo.getServoPosition("LineupForPickup").getPosition() != ConeGrabberArmServo.LINEUP_FOR_PICKUP) {
                coneGrabberArmServo.changeServoPosition("LineupForPickup", ConeGrabberArmServo.LINEUP_FOR_PICKUP);
                coneGrabberArmServo.lineupForPickupPosition();
            }
            if (coneGrabberArmServo.getServoPosition("Carry").getPosition() != ConeGrabberArmServo.CARRY) {
                coneGrabberArmServo.changeServoPosition("Carry", ConeGrabberArmServo.CARRY);
                coneGrabberArmServo.carryPosition();
            }

            if (gamepad1.a) {
                coneGrabberArmServo.init();
            }
            if (gamepad1.b) {
                coneGrabberArmServo.pickupPosition();
            }
            if (gamepad1.x) {
                coneGrabberArmServo.lineupForPickupPosition();
            }
            if (gamepad1.y) {
                coneGrabberArmServo.releasePosition();
            }
            if (gamepad1.dpad_up) {
                coneGrabberArmServo.carryPosition();
            }
            
            telemetry.addData("Gamepad 1 A = ", "init");
            telemetry.addData("Gamepad 1 B = ", "pickup");
            telemetry.addData("Gamepad 1 X = ", "lineup for pickup");
            telemetry.addData("Gamepad 1 Y = ", "release");
            telemetry.addData("Gamepad 1 dpad up = ", "carry");
            telemetry.update();
            idle();
        }
    }
}