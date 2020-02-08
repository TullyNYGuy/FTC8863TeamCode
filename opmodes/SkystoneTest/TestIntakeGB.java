package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.IntakeWheels;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Intake GB", group = "Test")
//@Disabled
public class TestIntakeGB extends LinearOpMode {

    // Put your variable declarations here

    DcMotor8863 leftIntakeMotor;
    DcMotor8863 rightIntakeMotor;

    String currentStatus = "Stopped";

    @Override
    public void runOpMode() {

        leftIntakeMotor = new DcMotor8863("IntakeMotorLeft", hardwareMap, telemetry);
        rightIntakeMotor = new DcMotor8863("IntakeMotorRight", hardwareMap, telemetry);

        leftIntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftIntakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        rightIntakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightIntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            if (gamepad1.dpad_up) {
                leftIntakeMotor.setPower(1);
                rightIntakeMotor.setPower(1);
                currentStatus = "Intake";
            }

            if (gamepad1.dpad_down) {
                leftIntakeMotor.setPower(-1);
                rightIntakeMotor.setPower(-1);
                currentStatus = "Outtake";
            }

            if (gamepad1.dpad_right || gamepad1.dpad_left) {
                leftIntakeMotor.setPower(0);
                rightIntakeMotor.setPower(0);
                currentStatus = "Stopped";
            }

            telemetry.addData("Status = ", currentStatus);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
