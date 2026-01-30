package org.firstinspires.ftc.teamcode.opmodes.DecodeTest.MotorTesting;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeTurntableMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Motor - Turntable", group = "Test")
//@Disabled
public class TestTurntableMotor extends LinearOpMode {

    // Put your variable declarations her
    DecodeTurntableMotor turntableMotor;

    @Override
    public void runOpMode() {
        int rpm = 0;
        int nextRPM = 0;
        int courseRPMAdjustment = 10;
        int fineRPMAdjustment = 2;

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
        turntableMotor = new DecodeTurntableMotor(hardwareMap, telemetry);
        turntableMotor.init(null);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            if (debouncedY.isPressed(gamepad2.y)) {
                nextRPM = nextRPM + courseRPMAdjustment;
            }
            if (debouncedA.isPressed(gamepad2.a)) {
                nextRPM = nextRPM - courseRPMAdjustment;
            }
            if (debouncedX.isPressed(gamepad2.x)) {
                nextRPM = 312;
            }
            if (debouncedB.isPressed(gamepad2.b)) {
                nextRPM = 0;
            }
            if (debouncedDpadUp.isPressed(gamepad2.dpad_up)) {
                nextRPM = nextRPM + fineRPMAdjustment;
            }
            if (debouncedDpadDown.isPressed(gamepad2.dpad_down)) {
                nextRPM = nextRPM - fineRPMAdjustment;
            }

            // limit the rpm between 0 and 1
            nextRPM = Range.clip(nextRPM, 0, 312);

            if (debouncedDpadLeft.isPressed(gamepad2.dpad_left)) {
                rpm = nextRPM;
                turntableMotor.setRPM(rpm);
            }

            telemetry.addData("Y = ", "+" + Integer.toString(courseRPMAdjustment));
            telemetry.addData("X = ", "312");
            telemetry.addData("B = ", "-" + Integer.toString(courseRPMAdjustment));
            telemetry.addData("A = ", "0");
            telemetry.addData("Dpad up = ", "+" + Integer.toString(fineRPMAdjustment));
            telemetry.addData("Dpad down = ", "-" + Integer.toString(fineRPMAdjustment));
            telemetry.addData("Dpad left = ", "Set motor to next rpm");
            telemetry.addLine();
            telemetry.addData("Current Speed = ", rpm);
            telemetry.addData("Next Speed = ", nextRPM);
            telemetry.addData("Actual RPM = ", turntableMotor.getActualRPM());
            telemetry.addData("Current used = ", turntableMotor.getCurrent());
            turntableMotor.displayTurntableAngle();
            telemetry.addData(">", "stop to finish");
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
