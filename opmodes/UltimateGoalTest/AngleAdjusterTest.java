package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.AngleChanger;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Angle Adjuster", group = "Test")
//@Disabled
public class AngleAdjusterTest extends LinearOpMode {

    // Put your variable declarations here
    public AngleChanger angleChanger;
    private ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        angleChanger = new AngleChanger(hardwareMap, telemetry);
        timer = new ElapsedTime();
        angleChanger.setAngleReference();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        angleChanger.setCurrentAngle(30);
        while (opModeIsActive() && !angleChanger.isAngleAdjustComplete()) {

            // Put your calls that need to run in a loop here
            angleChanger.update();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 1000){
            idle();
        }
        angleChanger.setCurrentAngle(20);
        while (opModeIsActive() && !angleChanger.isAngleAdjustComplete()) {

            // Put your calls that need to run in a loop here
            angleChanger.update();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 1000){
            idle();
        }
        angleChanger.setCurrentAngle(25);
        while (opModeIsActive() && !angleChanger.isAngleAdjustComplete()) {

            // Put your calls that need to run in a loop here
            angleChanger.update();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 1000){
            idle();
        }
        angleChanger.setCurrentAngle(20);
        while (opModeIsActive() && !angleChanger.isAngleAdjustComplete()) {

            // Put your calls that need to run in a loop here
            angleChanger.update();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 1000){
            idle();
        }
        angleChanger.setCurrentAngle(30);
        while (opModeIsActive() && !angleChanger.isAngleAdjustComplete()) {

            // Put your calls that need to run in a loop here
            angleChanger.update();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
