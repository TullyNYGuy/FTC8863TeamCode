package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumData;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Mecanum", group = "Test")
//@Disabled
public class TestMecanum extends LinearOpMode {

    // Put your variable declarations here
    Mecanum mecanum;
    Mecanum.Wheels wheels;
    HaloControls haloControls;
    MecanumData mecanumData;
    JoyStick gamepad1LeftJoyStickX;
    JoyStick gamepad1LeftJoyStickY;
    double gamepad1LeftJoyStickXValue = 0;
    double gamepad1LeftJoyStickYValue = 0;

    JoyStick gamepad1RightJoyStickX;
    JoyStick gamepad1RightJoyStickY;
    double gamepad1RightJoyStickXValue = 0;
    double gamepad1RightJoyStickYValue = 0;

    @Override
    public void runOpMode() {


        // Put your initializations here
        gamepad1LeftJoyStickX = new JoyStick(gamepad1, JoyStick.JoystickSide.LEFT, JoyStick.JoystickAxis.X);
        gamepad1LeftJoyStickY = new JoyStick(gamepad1, JoyStick.JoystickSide.LEFT, JoyStick.JoystickAxis.Y);

        gamepad1RightJoyStickX = new JoyStick(gamepad1, JoyStick.JoystickSide.RIGHT, JoyStick.JoystickAxis.X);
        gamepad1RightJoyStickY = new JoyStick(gamepad1, JoyStick.JoystickSide.RIGHT, JoyStick.JoystickAxis.Y);
        mecanum = new Mecanum();
        haloControls = new HaloControls(gamepad1RightJoyStickY, gamepad1RightJoyStickX, gamepad1LeftJoyStickX, this);
        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here

            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            haloControls.getMechanumdata(mecanumData);
            mecanum.calculateWheelVelocity(mecanumData);
            telemetry.addData("front left = ", wheels.getFrontLeft());
            telemetry.addData("front right = ", wheels.getFrontRight());
            telemetry.addData("back left = ", wheels.getBackLeft());
            telemetry.addData("back right = ", wheels.getBackRight());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
