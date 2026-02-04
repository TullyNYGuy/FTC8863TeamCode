package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeBallShooter;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeIntakeMotor;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeRampServo;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeSorterMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Ball Shooter", group = "Test")
//@Disabled
public class TestBallShooter extends LinearOpMode {

    // Put your variable declarations her
    DecodeBallShooter ballShooter;
    DecodeSorterMotor sorterMotor;
    DecodeIntakeMotor intakeMotor;
    DecodeRampServo rampServo;

    @Override
    public void runOpMode() {
        int rpm = 0;
        int nextRPM = 0;
        int courseRPMAdjustment = 500;
        int fineRPMAdjustment = 50;

        // These debounce the buttons so that you only see a single press even if a button is held
        // down for a long time.
        Debouncer debouncedY = new Debouncer();
        Debouncer debouncedB = new Debouncer();
        Debouncer debouncedX = new Debouncer();
        Debouncer debouncedA = new Debouncer();
        Debouncer debouncedDpadUp = new Debouncer();
        Debouncer debouncedDpadDown = new Debouncer();
        Debouncer debouncedDpadLeft = new Debouncer();

        // Put your initializations here
        ballShooter = new DecodeBallShooter(hardwareMap, telemetry);
        ballShooter.init(null);
        sorterMotor = new DecodeSorterMotor(hardwareMap, telemetry);
        intakeMotor = new DecodeIntakeMotor(hardwareMap, telemetry);
        rampServo = new DecodeRampServo(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        sorterMotor.setRPM(140);
        intakeMotor.setRPM(500);
        rampServo.upPosition();
        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            if (debouncedY.isPressed(gamepad1.y)) {
                nextRPM = nextRPM + courseRPMAdjustment;
            }
            if (debouncedA.isPressed(gamepad1.a)) {
                nextRPM = nextRPM - courseRPMAdjustment;
            }
            if (debouncedX.isPressed(gamepad1.x)) {
                nextRPM = 6000;
            }
            if (debouncedB.isPressed(gamepad1.b)) {
                nextRPM = 0;
            }
            if (debouncedDpadUp.isPressed(gamepad1.dpad_up)) {
                nextRPM = nextRPM + fineRPMAdjustment;
            }
            if (debouncedDpadDown.isPressed(gamepad1.dpad_down)) {
                nextRPM = nextRPM - fineRPMAdjustment;
            }
            if (gamepad2.dpad_up){
                ballShooter.setHoodPosition(DecodeBallShooter.HoodPositions.LONG);
            }
            if (gamepad2.dpad_down){
                ballShooter.setHoodPosition(DecodeBallShooter.HoodPositions.SHORT);
            }

            if (gamepad2.dpadLeftWasPressed()) {
                ballShooter.setHoodPosition(DecodeBallShooter.HoodPositions.MEDIUM);
            }

            // limit the rpm between 0 and 1
            nextRPM = Range.clip(nextRPM, 0, 6000);

            if (debouncedDpadLeft.isPressed(gamepad1.dpad_left)) {
                rpm = nextRPM;
                ballShooter.setRPM(rpm);
            }

            telemetry.addData("Y = ", "+" + Integer.toString(courseRPMAdjustment));
            telemetry.addData("X = ", "6000");
            telemetry.addData("B = ", "-" + Integer.toString(courseRPMAdjustment));
            telemetry.addData("A = ", "0");
            telemetry.addData("Dpad up = ", "+" + Integer.toString(fineRPMAdjustment));
            telemetry.addData("Dpad down = ", "-" + Integer.toString(fineRPMAdjustment));
            telemetry.addData("Dpad left = ", "Set motor to next rpm");
            telemetry.addLine();
            telemetry.addData("Current Speed = ", rpm);
            telemetry.addData("Next Speed = ", nextRPM);
            telemetry.addData("Actual RPM = ", ballShooter.getRPM());
            telemetry.addData("Encoder count = ", ballShooter.getShooterMotorEncoderCount());
            telemetry.addData(">", "stop to finish");
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
