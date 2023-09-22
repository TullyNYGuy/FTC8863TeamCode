package org.firstinspires.ftc.teamcode.opmodes.LauncherBotTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.LauncherBot.LauncherBotDualMotorGearBox;
import org.firstinspires.ftc.teamcode.Lib.LauncherBot.LauncherBotShooterServo;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalDualMotorGearBox;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Launcher Bot shooter test", group = "Test")
//@Disabled
public class TestLauncherBotDualMotorGearBox extends LinearOpMode {

    // Put your variable declarations here
    public LauncherBotDualMotorGearBox dualMotorGearBox;
    // public ElapsedTime timer;
    public int motorRPM;
    public LauncherBotShooterServo shooterServo;
    public ElapsedTime timer;
    public int time= 150;

    @Override
    public void runOpMode() {


        // Put your initializations here
        dualMotorGearBox = new LauncherBotDualMotorGearBox("leftShooterMotor", "rightShooterMotor", hardwareMap, telemetry);
        shooterServo = new LauncherBotShooterServo(hardwareMap, telemetry);
        timer = new ElapsedTime();
        motorRPM = 6000;

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        dualMotorGearBox.setSpeed(motorRPM);
        shooterServo.init();
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 5000) {

            // Put your calls that need to run in a loop here

            // Display the current value
            telemetry.addData("Gear box running at ", motorRPM + " RPM");
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
        shooterServo.kick();
//        dualMotorGearBox.stopGearbox();
//        timer.reset();
//
//        while (opModeIsActive() && timer.milliseconds() < 5000) {
//
//            // Put your calls that need to run in a loop here
//
//            // Display the current value
//            telemetry.addData("Gear box stopping", "...");
//            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
//            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
//            telemetry.addData(">", "Press Stop to end test.");
//
//            telemetry.update();
//
//            idle();
//        }
//        dualMotorGearBox.setDirection(UltimateGoalDualMotorGearBox.Direction.REVERSE);
//        dualMotorGearBox.setSpeed(motorRPM);
        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < time) {

            // Put your calls that need to run in a loop here

            // Display the current value
            telemetry.addData("Gear box running at ", motorRPM + " RPM");
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        shooterServo.retract();

        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < time) {

            // Put your calls that need to run in a loop here

            // Display the current value
            telemetry.addData("Gear box running at ", motorRPM + " RPM");
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        shooterServo.kick();

        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < time) {

            // Put your calls that need to run in a loop here

            // Display the current value
            telemetry.addData("Gear box running at ", motorRPM + " RPM");
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        shooterServo.retract();

        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < time) {

            // Put your calls that need to run in a loop here

            // Display the current value
            telemetry.addData("Gear box running at ", motorRPM + " RPM");
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }


        shooterServo.kick();

        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < time) {

            // Put your calls that need to run in a loop here

            // Display the current value
            telemetry.addData("Gear box running at ", motorRPM + " RPM");
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        shooterServo.retract();

        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < time) {

            // Put your calls that need to run in a loop here

            // Display the current value
            telemetry.addData("Gear box running at ", motorRPM + " RPM");
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
//        // Put your cleanup code here - it runs as the application shuts down
//        dualMotorGearBox.stopGearbox();
//        telemetry.addData(">", "Done");
//        telemetry.update();

    }
}
