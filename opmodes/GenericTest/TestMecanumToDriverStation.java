package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863.MotorType.ANDYMARK_20;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Mecanum to Driver Station", group = "Test")
//@Disabled
public class TestMecanumToDriverStation extends LinearOpMode {

    // Put your variable declarations here
    Mecanum mecanum;
    Mecanum.WheelVelocities wheelVelocities;
    HaloControls haloControls;
    MecanumCommands mecanumCommands;
    JoyStick gamepad1LeftJoyStickX;
    JoyStick gamepad1LeftJoyStickY;
    double gamepad1LeftJoyStickXValue = 0;
    double gamepad1LeftJoyStickYValue = 0;
DcMotor8863 frontLeft;
    JoyStick gamepad1RightJoyStickX;
    JoyStick gamepad1RightJoyStickY;
    double gamepad1RightJoyStickXValue = 0;
    double gamepad1RightJoyStickYValue = 0;

    @Override
    public void runOpMode() {


        // Put your initializations here
        mecanumCommands = new MecanumCommands();

        gamepad1LeftJoyStickX = new JoyStick(gamepad1, JoyStick.JoystickSide.LEFT, JoyStick.JoystickAxis.X);
        gamepad1LeftJoyStickY = new JoyStick(gamepad1, JoyStick.JoystickSide.LEFT, JoyStick.JoystickAxis.Y);

        gamepad1RightJoyStickX = new JoyStick(gamepad1, JoyStick.JoystickSide.RIGHT, JoyStick.JoystickAxis.X);
        gamepad1RightJoyStickY = new JoyStick(gamepad1, JoyStick.JoystickSide.RIGHT, JoyStick.JoystickAxis.Y);
        mecanum = new Mecanum();
        haloControls = new HaloControls(gamepad1RightJoyStickY, gamepad1RightJoyStickX, gamepad1LeftJoyStickX, this);
        frontLeft = new DcMotor8863("FrontLeft", hardwareMap );
        frontLeft.setMotorType(ANDYMARK_20);
        frontLeft.setMovementPerRev(360);
        frontLeft.setTargetEncoderTolerance(5);
        frontLeft.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);

       frontLeft.setMinMotorPower(-1);

        frontLeft.setMaxMotorPower(1);


        frontLeft.setDirection(DcMotor.Direction.FORWARD);

        double initialPower = 0;
        double finalPower = 1.0;
        double rampTime = 1000;
        frontLeft.setupPowerRamp(initialPower, finalPower, rampTime);
       // frontLeft.setupPowerRamp(initialPower, finalPower, rampTime);
        //**************************************************************
        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here

            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            haloControls.calculateMecanumCommands(mecanumCommands);
            // mecanum commands could come from joysticks or from autonomous calculations. That is why HaloControls is not part of Mecanum class
            //*****************************************************************
            // Is this any better than mecanum.getFrontLeft() etc?
            //*****************************************************************
            mecanum.calculateWheelVelocity(mecanumCommands);

            frontLeft.runAtConstantSpeed(mecanum.getFrontLeft());
            frontLeft.update();
            telemetry.addData("front left = ",  mecanum.getFrontLeft());
            telemetry.addData("front right = ",  mecanum.getFrontRight());
            telemetry.addData("back left = ",  mecanum.getBackLeft());
            telemetry.addData("back right = ",  mecanum.getBackRight());
            telemetry.addData(">", "Press Stop to end test.");
this.
            telemetry.update();

            idle();
        }
        frontLeft.stop();
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
