package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeHoodServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Hood Servo Positions", group = "Test")
//@Disabled
public class DecodeTestHoodServo extends LinearOpMode {

    // Put your variable declarations here
    public DecodeHoodServo hoodServo;

    @Override
    public void runOpMode() {


        // Put your initializations here
        hoodServo = new DecodeHoodServo(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here
            hoodServo.update();

            if (gamepad1.a) {
                hoodServo.initPosition();
            }

            if (gamepad1.x) {
                hoodServo.shortPosition();
            }

            if (gamepad1.b) {
                hoodServo.longPosition();
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
