package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "TestHaloControls", group = "Test")
//@Disabled
public class TestHaloControls extends LinearOpMode {
    // Put your variable declarations here


    @Override
    public void runOpMode() {


        // Put your initializations here

        HaloControls controls = new HaloControls(gamepad1, null);
        MecanumCommands commands = new MecanumCommands();
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");
            controls.calculateMecanumCommands(commands);
            telemetry.addData("translation angle in degrees: ", commands.getAngleOfTranslationDegrees());
            telemetry.addData("translation angle for the gyro: ", commands.getAngleOfTranslationGyro());
            telemetry.addData("speed: ", commands.getSpeed());
            telemetry.addData("speed of rotation: ", commands.getSpeedOfRotation());
            telemetry.update();

            idle();
        }



        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
