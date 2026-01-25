package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Servos;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeArmServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Intake Arm Servo Positions", group = "Test")
@Disabled
public class ITDTestIntakeArmServoPositions extends LinearOpMode {

    // Put your variable declarations here
    public ITDIntakeArmServo intakeArmServo;

    @Override
    public void runOpMode() {


        // Put your initializations here
        intakeArmServo = new ITDIntakeArmServo(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here
            intakeArmServo.update();

            if (gamepad1.y) {
                intakeArmServo.initPosition();
            }

            if (gamepad1.b) {
                intakeArmServo.bucketClearancePosition();
            }

            if (gamepad1.x) {
                intakeArmServo.transferPosition();
            }

            if (gamepad1.dpad_down) {
                intakeArmServo.intakePositionLowAltitude();
            }

            if (gamepad1.dpad_up) {
                intakeArmServo.intakePositionHighAltitude();
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
