package org.firstinspires.ftc.teamcode.opmodes.DecodeTest.MotorTuning;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeTurntableMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Tune Turntable Motor Kstatic", group = "Tune")
//@Disabled
public class DecodeTuneTurntableMotorKStatic extends LinearOpMode {

    // Put your variable declarations her
    DecodeTurntableMotor turntableMotor;

    @Override
    public void runOpMode() {
        double power = 0;
        double nextPower = 0;
        double coursePowerAdjustment = .01;
        double finePowerAdjustment = .001;

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
                nextPower = nextPower + coursePowerAdjustment;
            }
            if (debouncedA.isPressed(gamepad2.a)) {
                nextPower = nextPower - coursePowerAdjustment;
            }
            if (debouncedX.isPressed(gamepad2.x)) {
                nextPower = 312;
            }
            if (debouncedB.isPressed(gamepad2.b)) {
                nextPower = 0;
            }
            if (debouncedDpadUp.isPressed(gamepad2.dpad_up)) {
                nextPower = nextPower + finePowerAdjustment;
            }
            if (debouncedDpadDown.isPressed(gamepad2.dpad_down)) {
                nextPower = nextPower - finePowerAdjustment;
            }

            // limit the power between 0 and 1
            nextPower = Range.clip(nextPower, 0, 1);

            if (debouncedDpadLeft.isPressed(gamepad2.dpad_left)) {
                power = nextPower;
                turntableMotor.setPower(power);
            }

            telemetry.addData("Y = ", "+" + Double.toString(coursePowerAdjustment));
            telemetry.addData("X = ", "1");
            telemetry.addData("B = ", "-" + Double.toString(coursePowerAdjustment));
            telemetry.addData("A = ", "0");
            telemetry.addData("Dpad up = ", "+" + Double.toString(finePowerAdjustment));
            telemetry.addData("Dpad down = ", "-" + Double.toString(finePowerAdjustment));
            telemetry.addData("Dpad left = ", "Set motor to next power");
            telemetry.addLine();
            telemetry.addData("Current Speed = ", power);
            telemetry.addData("Next Speed = ", nextPower);
            telemetry.addData("Actual RPM = ", turntableMotor.getCommandedRPM());
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
