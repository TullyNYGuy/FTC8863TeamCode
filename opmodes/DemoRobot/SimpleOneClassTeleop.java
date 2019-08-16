package org.firstinspires.ftc.teamcode.opmodes.DemoRobot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.RelicRecoveryRobotStJohnFisher;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by ball on 10/7/2017.
 */

@TeleOp(name = "Demo Bot Simple Teleop", group = "Run")
//@Disabled

public class SimpleOneClassTeleop extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations - THESE ARE JUST SETTING UP THE VARIABLES (RESERVING MEMORY FOR THEM)
    //*********************************************************************************************

    // Here are declarations for all of the hardware
    public DcMotor rightDriveMotor;
    public DcMotor leftDriveMotor;

    // Here is the declaration for the hardware map - it contains information about the configuration
    // of the robot and how to talk to each piece of hardware
    public HardwareMap hardwareMap;

    // GAMEPAD 1 - declare all of the objects on game pad 1

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
    // set some initial values for the joysticks. Later we will actually read the values from the
    // joysticks and set these variables equal to the readings. But for now we need something
    // so that the joysticks are in a "good" position when the robot actually  starts up.
    double gamepad1LeftJoyStickXValue = 0;
    double gamepad1LeftJoyStickYValue = 0;

    JoyStick gamepad1RightJoyStickX;
    JoyStick gamepad1RightJoyStickY;
    double gamepad1RightJoyStickXValue = 0;
    double gamepad1RightJoyStickYValue = 0;

    // GAMEPAD 2 - declare all of the objects on game pad 2

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
    // set some initial values for the joysticks. Later we will actually read the values from the
    // joysticks and set these variables equal to the readings. But for now we need something
    // so that the joysticks are in a "good" position when the robot actually  starts up.
    double gamepad2LeftJoyStickXValue = 0;
    double gamepad2LeftJoyStickYValue = 0;

    JoyStick gamepad2RightJoyStickX;
    JoyStick gamepad2RightJoyStickY;
    double gamepad2RightJoyStickXValue = 0;
    double gamepad2RightJoyStickYValue = 0;

    // declare variables to hold the drive train powers for tank drive and set them to 0 for a
    // safe starting value
    double leftPower = 0;
    double rightPower = 0;

    // drive train powers for differential drive power and direction and set them to 0 for a
    // safe starting value
    double throttle = 0;
    double direction = 0;

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************

        // create the robot. Tell the driver we are creating it since this can take a few seconds
        // and we want the driver to know what is going on.
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        // Create and Initialize Motors - use the hardware map in combination with the configuation
        // you setup on the phone to name each hardware device. What this does is tell sofware how
        // to address (talk to) the named object. For example, the first statement below tells the
        // software how to talk to the thing you named leftDriveMotor when you configured the phone.
        // The text in green is the name you typed in while configuring the phone.
        leftDriveMotor  = hardwareMap.get(DcMotor.class, "leftDriveMotor");
        rightDriveMotor = hardwareMap.get(DcMotor.class, "rightDriveMotor");
        // Since the motors are mounting in opposite orientations on the robot, a positive power for
        // one motor will make it drive the robot forward. But a positive power for the other motor
        // will make the robot drive backward. We would like both motors to move forward when we give
        // a positive power. So we tell the software to reverse the sense of one of the motors.
        leftDriveMotor.setDirection(DcMotor.Direction.REVERSE);

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

        // Create the Game Pad 1 joysticks
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

        // Create the Game Pad 2 joysticks
        gamepad2LeftJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad2LeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        gamepad2RightJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad2RightJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        // The initializing of all of the hardware is done. Tell the driver
        telemetry.addData("All done! ","Robot is initialized.");

        // Wait for the play button to be pressed on the driver station phone
        telemetry.addData(">", "Press start to run Teleop");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        // The user pressed play so we start the robot and then check to make sure he or she has
        // not pressed stop. If they press stop, then opModeIsActive() will return false. It can
        // also return false if there is some kind of error in the robot software or hardware.

        while (opModeIsActive()) {

            //*************************************************************************************
            // Gamepad 1 buttons - look for a button press on gamepad 1 and then do the action
            // for that button
            //*************************************************************************************

            // example for a button with multiple commands attached to it:
            // don't forget to change the new line with the number of commands attached like this:
            // gamepad1x = new GamepadButtonMultiPush(4);
            //                                        ^
            //                                        |
            //
//            if (gamepad1x.buttonPress(gamepad1.x)) {
//                if (gamepad1x.isCommand1()) {
//                    // call the first command you want to run
//                }
//                if (gamepad1x.isCommand2()) {
//                    // call the 2nd command you want to run
//                }
//                if (gamepad1x.isCommand3()) {
//                    // call the 3rd command you want to run
//                }
//                if (gamepad1x.isCommand4()) {
//                    // call the 4th command you want to run
//                }
//            }

            if (gamepad1RightBumper.buttonPress(gamepad1.right_bumper)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1LeftBumper.buttonPress(gamepad1.left_bumper)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1a.buttonPress(gamepad1.a)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here

            }

            if (gamepad1b.buttonPress(gamepad1.b)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1y.buttonPress(gamepad1.y)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1x.buttonPress(gamepad1.x)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1DpadUp.buttonPress(gamepad1.dpad_up)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                gamepad1LeftJoyStickX.setFullPower();
                gamepad1LeftJoyStickY.setFullPower();
                gamepad1RightJoyStickX.setFullPower();
                gamepad1RightJoyStickY.setFullPower();
            }

            if (gamepad1DpadDown.buttonPress(gamepad1.dpad_down)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                gamepad1LeftJoyStickX.set30PercentPower();
                gamepad1LeftJoyStickY.set30PercentPower();
                gamepad1RightJoyStickX.set30PercentPower();
                gamepad1RightJoyStickY.set30PercentPower();
            }

            if (gamepad1DpadLeft.buttonPress(gamepad1.dpad_left)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                gamepad1LeftJoyStickX.setHalfPower();
                gamepad1LeftJoyStickY.setHalfPower();
                gamepad1RightJoyStickX.setHalfPower();
                gamepad1RightJoyStickY.setHalfPower();
            }

            if (gamepad1DpadRight.buttonPress(gamepad1.dpad_right)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                gamepad1LeftJoyStickX.set20PercentPower();
                gamepad1LeftJoyStickY.set20PercentPower();
                gamepad1RightJoyStickX.set20PercentPower();
                gamepad1RightJoyStickY.set20PercentPower();
            }

            if (gamepad1LeftStickButton.buttonPress(gamepad1.left_stick_button)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1RightStickButton.buttonPress(gamepad1.right_stick_button)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            //**************************************************************************************
            // Gamepad 1 joysticks
            //**************************************************************************************

            gamepad1LeftJoyStickXValue = gamepad1LeftJoyStickX.scaleInput(gamepad1.left_stick_x);
            gamepad1LeftJoyStickYValue = gamepad1LeftJoyStickY.scaleInput(gamepad1.left_stick_y);

            gamepad1RightJoyStickXValue = gamepad1RightJoyStickX.scaleInput(gamepad1.right_stick_x);
            gamepad1RightJoyStickYValue = gamepad1RightJoyStickY.scaleInput(gamepad1.right_stick_y);

            //**************************************************************************************
            // Gamepad 2 buttons
            //**************************************************************************************

            // example for a button with multiple commands attached to it:
            // don't forget to change the new line with the number of commands attached like this:
            // gamepad1x = new GamepadButtonMultiPush(4);
            //                                        ^
            //                                        |
            //
//            if (gamepad1x.buttonPress(gamepad1.x)) {
//                if (gamepad1x.isCommand1()) {
//                    // call the first command you want to run
//                }
//                if (gamepad1x.isCommand2()) {
//                    // call the 2nd command you want to run
//                }
//                if (gamepad1x.isCommand3()) {
//                    // call the 3rd command you want to run
//                }
//                if (gamepad1x.isCommand4()) {
//                    // call the 4th command you want to run
//                }
//            }

            if (gamepad2RightBumper.buttonPress(gamepad2.right_bumper)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2LeftBumper.buttonPress(gamepad2.left_bumper)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2a.buttonPress(gamepad2.a)) {
                // this was a new button press, not a button held down for a while
                if (gamepad2a.isCommand1()) {
                }
                if (gamepad2a.isCommand2()) {
                }

                // put the command to be executed here

            }

            if (gamepad2b.buttonPress(gamepad2.b)) {
                if (gamepad2b.isCommand1()) {
                    // call the first command you want to run
                }
                if (gamepad2b.isCommand2()) {
                    // call the 2nd command you want to run
                }
            }

            if (gamepad2y.buttonPress(gamepad2.y)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2x.buttonPress(gamepad2.x)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2DpadUp.buttonPress(gamepad2.dpad_up)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2DpadDown.buttonPress(gamepad2.dpad_down)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2DpadLeft.buttonPress(gamepad2.dpad_left)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2DpadRight.buttonPress(gamepad2.dpad_right)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2LeftStickButton.buttonPress(gamepad2.left_stick_button)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad2RightStickButton.buttonPress(gamepad2.right_stick_button)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            //**************************************************************************************
            // Gamepad 2 joysticks
            //**************************************************************************************

            gamepad2LeftJoyStickXValue = gamepad2LeftJoyStickX.scaleInput(gamepad2.left_stick_x);
            gamepad2LeftJoyStickYValue = gamepad2LeftJoyStickY.scaleInput(gamepad2.left_stick_y);

            gamepad2RightJoyStickXValue = gamepad2RightJoyStickX.scaleInput(gamepad2.right_stick_x);
            gamepad2RightJoyStickYValue = gamepad2RightJoyStickY.scaleInput(gamepad2.right_stick_y);


            //*************************************************************************************
            //  Process joysticks into drive train commands
            // ************************************************************************************

            // assign the joysticks values to the motor powers - we really could just skip this but
            // let's leave it for future expansion
            leftPower = gamepad1LeftJoyStickYValue;
            rightPower = gamepad1RightJoyStickYValue;

            // update the drive motors with the new power
            leftDriveMotor.setPower(leftPower);
            rightDriveMotor.setPower(rightPower);

            // Display telemetry - display some information for the driver on the driver station
            // phone
            telemetry.addData("Left Motor Speed = ", "%3.2f", leftPower);
            telemetry.addData("Right Motor Speed = ", "%3.2f", rightPower);
            telemetry.addData("Power Reduction = ", "%1.2f", gamepad1LeftJoyStickY.getReductionFactor());
            telemetry.addData(">", "Press Stop to end.");
            telemetry.update();

            idle();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    //*********************************************************************************************
    //             Helper methods
    //*********************************************************************************************

}

