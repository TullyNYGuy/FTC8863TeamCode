package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.SmartJoystick;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "TestHaloControls", group = "Test")
@Disabled
public class TestHaloControls extends LinearOpMode {
    // Put your variable declarations here


    @Override
    public void runOpMode() {


        // Put your initializations here

        // Game Pad 1 joysticks
        SmartJoystick gamepad1LeftJoyStickX = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.X);
        SmartJoystick gamepad1LeftJoyStickY = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.Y);

        SmartJoystick gamepad1RightJoyStickX = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.X);
        SmartJoystick gamepad1RightJoyStickY = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.Y);
        HaloControls controls = new HaloControls(gamepad1LeftJoyStickX, gamepad1LeftJoyStickY, gamepad1RightJoyStickX, null, telemetry);
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
            telemetry.addData("translation angle in degrees: ", commands.getAngleOfTranslation(AngleUnit.DEGREES));
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
