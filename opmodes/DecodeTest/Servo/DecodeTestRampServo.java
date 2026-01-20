package org.firstinspires.ftc.teamcode.opmodes.DecodeTest.Servo;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeRampServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Ramp Servo Positions", group = "Test")
//@Disabled
public class DecodeTestRampServo extends LinearOpMode {

    // Put your variable declarations here
    public DecodeRampServo rampServo;

    @Override
    public void runOpMode() {


        // Put your initializations here
        rampServo = new DecodeRampServo(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here
            rampServo.update();

            if (gamepad1.a) {
                rampServo.initPosition();
            }

            if (gamepad1.x) {
                rampServo.upPosition();
            }

            if (gamepad1.b) {
                rampServo.downPosition();
            }

            if (gamepad1.y) {
            }

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }


        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
