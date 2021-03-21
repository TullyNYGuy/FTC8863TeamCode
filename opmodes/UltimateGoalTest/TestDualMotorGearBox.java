package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.DualMotorGearBox;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Dual Motor Test", group = "Test")
//@Disabled
public class TestDualMotorGearBox extends LinearOpMode {

    // Put your variable declarations here
    public DualMotorGearBox dualMotorGearBox;
    public ElapsedTime timer;
    public double speed;

    @Override
    public void runOpMode() {


        // Put your initializations here
        dualMotorGearBox = new DualMotorGearBox("LeftMotor", "RightMotor", hardwareMap, telemetry);
        timer = new ElapsedTime();
        speed = 1.0;

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        dualMotorGearBox.setSpeed(speed);
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 5000) {

            // Put your calls that need to run in a loop here

            // Display the current value
            telemetry.addData("Gear box running at ", speed * 100 + "%");
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        dualMotorGearBox.stopGearbox();
        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < 5000) {

            // Put your calls that need to run in a loop here

            // Display the current value
            telemetry.addData("Gear box stopping", "...");
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        dualMotorGearBox.setDirection(DualMotorGearBox.Direction.REVERSE);
        dualMotorGearBox.setSpeed(speed);
        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < 5000) {

            // Put your calls that need to run in a loop here

            // Display the current value
            telemetry.addData("Gear box running at ", speed * 100 + "%");
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        // Put your cleanup code here - it runs as the application shuts down
        dualMotorGearBox.stopGearbox();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
