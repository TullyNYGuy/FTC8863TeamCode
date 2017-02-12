package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.VelocityVortexShooter;

/**
 * This OpMode runs 2 motors at a given power, one in the opposite direction from the other.
 * It is meant to be used to test a drive train
 * <p>
 * This code assumes a DC motor configured with the name "leftMotor"  and "rightMotor"
 */
@TeleOp(name = "Test Shooter Aiming System", group = "Test")
//@Disabled
public class TestShooterAimingSystem extends LinearOpMode {

    // enums for a state machine for the shooter motor

    public enum AimingMode {
        AUTOMATIC,
        MANUAL;
    }
    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    VelocityVortexShooter shooter;

    AimingMode aimingMode = AimingMode.MANUAL;

    // joystick and joystick value declarations - game pad 1
    final static double JOYSTICK_DEADBAND_VALUE = .15;
    final static double JOYSTICK_HALF_POWER = .5;
    final static double JOYSTICK_QUARTER_POWER = .25;

    JoyStick gamepad1LeftJoyStickX;
    JoyStick gamepad1LeftJoyStickY;
    double gamepad1LeftJoyStickXValue = 0;
    double gamepad1LeftJoyStickYValue = 0;

    JoyStick gamepad1RightJoyStickX;
    JoyStick gamepad1RightJoyStickY;
    double gamepad1RightJoyStickXValue = 0;
    double gamepad1RightJoyStickYValue = 0;

    // for use in debouncing the button. A long press will only result in one transition of the
    // shooterDirection
    boolean gamepad1aButtonIsReleased = true;
    boolean gamepad1bButtonIsReleased = true;
    boolean gamepad1yButtonIsReleased = true;
    boolean gamepad1xButtonIsReleased = true;

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************
        // Game Pad 1 joysticks
        gamepad1LeftJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1LeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        gamepad1RightJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1RightJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        shooter = new VelocityVortexShooter(hardwareMap, telemetry);
        shooter.init();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user his play on the driver phone
        //*********************************************************************************************

        while (opModeIsActive()) {

            if (gamepad1.y) {
                if (gamepad1yButtonIsReleased) {
                    shooter.moveToLoadPosition();
                    aimingMode = AimingMode.AUTOMATIC;
                    gamepad1yButtonIsReleased = false;
                }
            } else {
                gamepad1yButtonIsReleased = true;
            }


            if (gamepad1.x) {
                if (gamepad1xButtonIsReleased) {
                    shooter.moveToLimitSwitch();
                    aimingMode = AimingMode.AUTOMATIC;
                    gamepad1xButtonIsReleased = false;
                }
            } else {
                gamepad1xButtonIsReleased = true;
            }

            if (gamepad1.a) {
                if (gamepad1aButtonIsReleased) {
                    shooter.moveTo2Feet();
                    aimingMode = AimingMode.AUTOMATIC;
                    gamepad1aButtonIsReleased = false;
                }
            } else {
                gamepad1aButtonIsReleased = true;
            }


            if (gamepad1.b) {
                if (gamepad1bButtonIsReleased) {
                    shooter.moveTo6Feet();
                    aimingMode = AimingMode.AUTOMATIC;
                    gamepad1bButtonIsReleased = false;
                }
            } else {
                gamepad1bButtonIsReleased = true;
            }

            gamepad1LeftJoyStickXValue = gamepad1LeftJoyStickX.scaleInput(gamepad1.left_stick_x);
            gamepad1LeftJoyStickYValue = gamepad1LeftJoyStickY.scaleInput(gamepad1.left_stick_y);

            gamepad1RightJoyStickXValue = gamepad1RightJoyStickX.scaleInput(gamepad1.right_stick_x);
            gamepad1RightJoyStickYValue = gamepad1RightJoyStickY.scaleInput(gamepad1.right_stick_y);

            if (gamepad1RightJoyStickYValue != 0) {
                aimingMode = AimingMode.MANUAL;
            }
            shooter.aimShooter(gamepad1RightJoyStickYValue);

            shooter.update();
            telemetry.addData("Joystick value = ", "%2.3f", gamepad1RightJoyStickYValue);
            telemetry.addData("Cmd Motor Power = ", "%2.3f", shooter.getAutomaticAimingMotorPower());
            telemetry.addData("Actual Motor Power = ", "%2.3f", shooter.aimingMotor.getCurrentPower());
            telemetry.addData("Motor Mode = ", shooter.aimingMotor.getMode().toString());
            telemetry.addData("Actual Encoder Value = ", "%5d", shooter.aimingMotor.getCurrentPosition());
            telemetry.addData("Virtual Encoder Value = ", "%5d", shooter.getVirtualEncoderValue());
            telemetry.addData("Offset Encoder Value = ", "%5d", shooter.getEncoderOffset());
            telemetry.addData("Encoder Cmd = ", "%5d", shooter.getAdjustedEncoderCmd());
            telemetry.addData("At switch count = ", "%5d", shooter.getAtSwitchCount());
            telemetry.addData("Reset Encoder count = ", "%5d", shooter.getResetEncoderCount());
            telemetry.addData("Set Encoder Offset count = ", "%5d", shooter.getSetEncoderOffsetCount());
            telemetry.addData("Moving to 1 foot count = ", "%5d", shooter.getMovingTo1FootCounter());
            telemetry.addData("At 1 foot count = ", "%5d", shooter.getAt1FootCounter());
            telemetry.addData("aiming mode = ", aimingMode.toString());
            telemetry.addData("Shooter state = ", shooter.getShooterState().toString());
            telemetry.addData("Motor state = ", shooter.aimingMotor.getCurrentMotorState().toString());

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
    }
}

