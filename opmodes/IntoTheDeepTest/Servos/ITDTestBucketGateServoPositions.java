package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Servos;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDBucketGateServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Bucket Gate Servo Positions", group = "Test")
@Disabled
public class ITDTestBucketGateServoPositions extends LinearOpMode {

    // Put your variable declarations here
    public ITDBucketGateServo bucketGateServo;

    @Override
    public void runOpMode() {


        // Put your initializations here
        bucketGateServo = new ITDBucketGateServo(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here
            bucketGateServo.update();

            if (gamepad1.a) {
                bucketGateServo.initPosition();
            }

            if (gamepad1.x) {
                bucketGateServo.closePosition();
            }

            if (gamepad1.b) {
                bucketGateServo.openPosition();
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
