package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.JewelArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Calibrate Jewel Arm Angles", group = "Test")
//@Disabled
public class CalibrateJewelArmAngles extends LinearOpMode {
    public AdafruitIMU8863 imu;
    double heading = 0;
    double pitch = 0;
    double roll = 0;
    // GAMEPAD 1

    // declare the buttons on the gamepad as multi push button objects
    public GamepadButtonMultiPush gamepad1RightBumper;
    public GamepadButtonMultiPush gamepad1LeftBumper;
    public GamepadButtonMultiPush gamepad1a;
    public GamepadButtonMultiPush gamepad1b;
    public GamepadButtonMultiPush gamepad1y;
    public GamepadButtonMultiPush gamepad1x;
    public GamepadButtonMultiPush gamepad1DpadUp;
    public GamepadButtonMultiPush gamepad1DpadDown;
    public GamepadButtonMultiPush gamepad1DpadLeft;
    public GamepadButtonMultiPush gamepad1DpadRight;
    public GamepadButtonMultiPush gamepad1LeftStickButton;
    public GamepadButtonMultiPush gamepad1RightStickButton;

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

    // GAMEPAD 2

    // declare the buttons on the gamepad as multi push button objects
    public GamepadButtonMultiPush gamepad2RightBumper;
    public GamepadButtonMultiPush gamepad2LeftBumper;
    public GamepadButtonMultiPush gamepad2a;
    public GamepadButtonMultiPush gamepad2b;
    public GamepadButtonMultiPush gamepad2y;
    public GamepadButtonMultiPush gamepad2x;
    public GamepadButtonMultiPush gamepad2DpadUp;
    public GamepadButtonMultiPush gamepad2DpadDown;
    public GamepadButtonMultiPush gamepad2DpadLeft;
    public GamepadButtonMultiPush gamepad2DpadRight;
    public GamepadButtonMultiPush gamepad2LeftStickButton;
    public GamepadButtonMultiPush gamepad2RightStickButton;

    // joystick and joystick value declarations - game pad 2
    JoyStick gamepad2LeftJoyStickX;
    JoyStick gamepad2LeftJoyStickY;
    double gamepad2LeftJoyStickXValue = 0;
    double gamepad2LeftJoyStickYValue = 0;

    JoyStick gamepad2RightJoyStickX;
    JoyStick gamepad2RightJoyStickY;
    double gamepad2RightJoyStickXValue = 0;
    double gamepad2RightJoyStickYValue = 0;

    JewelArm leftJewelArm;

    public void runOpMode() {

        // Put your initializations here
        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();
        imu = new AdafruitIMU8863(hardwareMap);
        telemetry.addData("IMU Initialized", "!");

        // create the gamepad 1 buttons and tell each button how many commands it has
        gamepad1RightBumper = new GamepadButtonMultiPush(1);
        gamepad1LeftBumper = new GamepadButtonMultiPush(1);
        gamepad1a = new GamepadButtonMultiPush(1);
        gamepad1b = new GamepadButtonMultiPush(1);
        gamepad1y = new GamepadButtonMultiPush(1);
        gamepad1x = new GamepadButtonMultiPush(1);
        gamepad1DpadUp = new GamepadButtonMultiPush(1);
        gamepad1DpadDown = new GamepadButtonMultiPush(1);
        gamepad1DpadLeft = new GamepadButtonMultiPush(1);
        gamepad1DpadRight = new GamepadButtonMultiPush(1);
        gamepad1LeftStickButton = new GamepadButtonMultiPush(1);
        gamepad1RightStickButton = new GamepadButtonMultiPush(1);

        // Game Pad 1 joysticks
        gamepad1LeftJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1LeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        gamepad1RightJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1RightJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        // create the gamepad 2 buttons and tell each button how many commands it has
        gamepad2RightBumper = new GamepadButtonMultiPush(1);
        gamepad2LeftBumper = new GamepadButtonMultiPush(1);
        gamepad2a = new GamepadButtonMultiPush(2);
        gamepad2b = new GamepadButtonMultiPush(2);
        gamepad2y = new GamepadButtonMultiPush(1);
        gamepad2x = new GamepadButtonMultiPush(1);
        gamepad2DpadUp = new GamepadButtonMultiPush(1);
        gamepad2DpadDown = new GamepadButtonMultiPush(1);
        gamepad2DpadLeft = new GamepadButtonMultiPush(1);
        gamepad2DpadRight = new GamepadButtonMultiPush(1);
        gamepad2LeftStickButton = new GamepadButtonMultiPush(1);
        gamepad2RightStickButton = new GamepadButtonMultiPush(1);

        // Game Pad 2 joysticks
        gamepad2LeftJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad2LeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        gamepad2RightJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad2RightJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);
        ;

        leftJewelArm = new JewelArm(JewelArm.RobotSide.LEFT, hardwareMap, telemetry, AllianceColor.TeamColor.RED);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1a.buttonPress(gamepad1.a)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                // center 0 degrees
                leftJewelArm.elbowServo.setupMoveBySteps(0, .01, 50);
                leftJewelArm.upDownServo.setupMoveBySteps(.5, .01, 50);
                leftJewelArm.frontBackServo.setupMoveBySteps(.48, .01, 50);
            }

            if (gamepad1b.buttonPress(gamepad1.b)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                // front 45 degrees
                leftJewelArm.elbowServo.setupMoveBySteps(0, .01, 50);
                leftJewelArm.upDownServo.setupMoveBySteps(.5, .01, 50);
                leftJewelArm.frontBackServo.setupMoveBySteps(.25, .01, 50);
            }

            if (gamepad1y.buttonPress(gamepad1.y)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1x.buttonPress(gamepad1.x)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                // back 45 degrees
                leftJewelArm.elbowServo.setupMoveBySteps(0, .01, 50);
                leftJewelArm.upDownServo.setupMoveBySteps(.5, .01, 50);
                leftJewelArm.frontBackServo.setupMoveBySteps(.70, .01, 50);
            }

            if (gamepad1DpadUp.buttonPress(gamepad1.dpad_up)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                // up 0 degrees
                leftJewelArm.elbowServo.setPosition(0);
                //leftJewelArm.elbowServo.setupMoveBySteps(0, .01, 50);
                leftJewelArm.upDownServo.setPosition(.044);
                //leftJewelArm.upDownServo.setupMoveBySteps(.044, .01, 50);
                //leftJewelArm.frontBackServo.setupMoveBySteps(.48, .01, 50);
                leftJewelArm.frontBackServo.setPosition(48);
            }

            if (gamepad1DpadDown.buttonPress(gamepad1.dpad_down)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                // down
                leftJewelArm.upDownServo.setPosition(.5);
            }

            if (gamepad1DpadLeft.buttonPress(gamepad1.dpad_left)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                imu.resetAngleReferences();
            }

            if (gamepad1DpadRight.buttonPress(gamepad1.dpad_right)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                // center 90 degrees
                leftJewelArm.elbowServo.setupMoveBySteps(0, .01, 50);
                leftJewelArm.upDownServo.setupMoveBySteps(.47, .01, 50);
                leftJewelArm.frontBackServo.setupMoveBySteps(.48, .01, 50);
            }

            leftJewelArm.elbowServo.updateMoveBySteps();
            leftJewelArm.upDownServo.updateMoveBySteps();
            leftJewelArm.frontBackServo.updateMoveBySteps();
            heading = imu.getHeading();
            pitch = imu.getPitch();
            roll = imu.getRoll();

            // Display the current value
            telemetry.addData("IMU mode = ", imu.getAngleMode().toString());
            telemetry.addData("Heading = ", "%5.2f", heading);
            telemetry.addData("Pitch = ", "%5.2f", pitch);
            telemetry.addData("Roll = ", "%5.2f", roll);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();


    }
}
