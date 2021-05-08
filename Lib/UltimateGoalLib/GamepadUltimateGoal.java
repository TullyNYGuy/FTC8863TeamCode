package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;

/*
 * Class for Ultimate Goal TeleOp mode
 * Gamepad 1 layout
 *    / Left JoystickX - robot moves left/right
 *    / Left JoystickY - robot moves forward/backward
 *    / Right JoystickX - robot rotation
 *    / DPad Up - 100% power
 *    / DPad Left - 50% power
 *    / DPad Down - 30% power
 *    / DPad Right - 20% power
 *    / A - EStop
 *    / B - fire 3
 *    / X - fire 1
 *    / Y - fire 2
 *    /Left Bumper- intake on/off
 *    /Right Bumper- shooter on/off
 *
 *  Gamepad 2 layout
 *    / Left JoystickX -
 *    / Left JoystickY -
 *    / Right JoystickX -
 *    / Right JoystickY -
 *    / DPad Up -
 *    / DPad Left -
 *    / DPad Down -
 *    / DPad Right-
 *    / A -
 *    / B -
 *    / X -
 *    / Y -
 */

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.SmartJoystick;

public class GamepadUltimateGoal {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    // joystick and joystick value declarations - game pad 1
    final static double JOYSTICK_DEADBAND_VALUE = .15;
    final static double JOYSTICK_HALF_POWER = .5;
    final static double JOYSTICK_QUARTER_POWER = .25;

    // GAMEPAD 1
    public Gamepad gamepad1 = null;

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
    public GamepadButtonMultiPush gamepad1LeftTriggerButton;
    public GamepadButtonMultiPush gamepad1RightStickButton;

    // joystick and joystick value declarations - game pad 1
    SmartJoystick gamepad1LeftJoyStickX;
    SmartJoystick gamepad1LeftJoyStickY;
    public double gamepad1LeftJoyStickXValue = 0;
    public double gamepad1LeftJoyStickYValue = 0;
    SmartJoystick gamepad1RightJoyStickX;
    SmartJoystick gamepad1RightJoyStickY;
    public double gamepad1RightJoyStickXValue = 0;
    public double gamepad1RightJoyStickYValue = 0;

    // GAMEPAD 2

    public Gamepad gamepad2 = null;
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
    SmartJoystick gamepad2LeftJoyStickX;
    SmartJoystick gamepad2LeftJoyStickY;
    public double gamepad2LeftJoyStickXValue = 0;
    public double gamepad2LeftJoyStickYValue = 0;
    SmartJoystick gamepad2RightJoyStickX;
    SmartJoystick gamepad2RightJoyStickY;
    public double gamepad2RightJoyStickXValue = 0;
    public double gamepad2RightJoyStickYValue = 0;

    private UltimateGoalRobotRoadRunner robot;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public GamepadUltimateGoal(Gamepad gamepad1, Gamepad gamepad2, UltimateGoalRobotRoadRunner robot) {
        this.robot = robot;
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;

        //
        //YOU WILL HAVE TO CONFIGURE THE GAMEPAD BUTTONS FOR TOGGLING IF YOU WANT THAT. DO THAT HERE.
        //

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
        gamepad1LeftTriggerButton = new GamepadButtonMultiPush(1);
        gamepad1LeftBumper = new GamepadButtonMultiPush(2);
        gamepad1RightBumper = new GamepadButtonMultiPush(2);

        // Game Pad 1 joysticks
        gamepad1LeftJoyStickX = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.X);
        //road runner expects left push is positive and right push is negative
        gamepad1LeftJoyStickX.setInvertSign(JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1LeftJoyStickY = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.Y);

        gamepad1RightJoyStickX = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.X);
        //road runner expects left push is positive and right push is negative
        gamepad1RightJoyStickX.setInvertSign(JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1RightJoyStickY = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.Y);

        //create the gamepad 2 buttons and tell each button how many commands it has
        gamepad2RightBumper = new GamepadButtonMultiPush(1);
        gamepad2LeftBumper = new GamepadButtonMultiPush(1);
        gamepad2a = new GamepadButtonMultiPush(1);
        gamepad2b = new GamepadButtonMultiPush(1);
        gamepad2y = new GamepadButtonMultiPush(1);
        gamepad2x = new GamepadButtonMultiPush(1);
        gamepad2DpadDown = new GamepadButtonMultiPush(1);
        gamepad2DpadUp = new GamepadButtonMultiPush(1);
        gamepad2DpadLeft = new GamepadButtonMultiPush(1);
        gamepad2DpadRight = new GamepadButtonMultiPush(1);
        gamepad2LeftStickButton = new GamepadButtonMultiPush(1);
        gamepad2RightStickButton = new GamepadButtonMultiPush(1);

        // Game Pad 2 joysticks
        gamepad2LeftJoyStickX = new SmartJoystick(gamepad2, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.X);
        gamepad2LeftJoyStickY = new SmartJoystick(gamepad2, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.Y);

        gamepad2RightJoyStickX = new SmartJoystick(gamepad2, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.X);
        gamepad2RightJoyStickY = new SmartJoystick(gamepad2, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.Y);
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void update() {

        //
        //YOU WILL HAVE TO CONFIGURE THE GAMEPAD BUTTONS WITH ROBOT COMMANDS. DO THAT HERE.
        //

        //*************************************************************************************
        // Gamepad 1 buttons
        //*************************************************************************************

        // example for a button with multiple commands attached to it:
        // don't forget to change the new line with the number of commands attached like this:
        // gamepad1x = new GamepadButtonMultiPush(4);
        //                                        ^
        //                                        |
        //
//        if (gamepad1x.buttonPress(gamepad1.x)) {
//            if (gamepad1x.isCommand1()) {
//                // call the first command you want to run
//            }
//            if (gamepad1x.isCommand2()) {
//                // call the 2nd command you want to run
//            }
//            if (gamepad1x.isCommand3()) {
//                // call the 3rd command you want to run
//            }
//            if (gamepad1x.isCommand4()) {
//                // call the 4th command you want to run
//            }
//        }

        if (gamepad1RightBumper.buttonPress(gamepad1.right_bumper)) {
            // this was a new button press, not a button held down for a while
            // put the command to be executed here
            if (gamepad1RightBumper.isCommand1()) {
                // call the first command you want to run
                robot.shooterOn();
            }
            if (gamepad1RightBumper.isCommand2()) {
                // call the 2nd command you want to run
                robot.shooterOff();
            }
        }

        if (gamepad1LeftBumper.buttonPress(gamepad1.left_bumper)) {
            // this was a new button press, not a button held down for a while
            // put the command to be executed here
            if (gamepad1LeftBumper.isCommand1()) {
                // call the first command you want to run
                robot.intakeOn();
            }
            if (gamepad1LeftBumper.isCommand2()) {
                // call the 2nd command you want to run
                robot.intakeOff();
            }
        }

        if (gamepad1a.buttonPress(gamepad1.a)) {
            // this was a new button press, not a button held down for a while
            // put the command to be executed here
            robot.eStop();
        }

        if (gamepad1b.buttonPress(gamepad1.b)) {
            // this was a new button press, not a button held down for a while
            // put the command to be executed here
            robot.fire3();
        }

        if (gamepad1y.buttonPress(gamepad1.y)) {
            // this was a new button press, not a button held down for a while
            // put the command to be executed here
            robot.fire2();
        }

        if (gamepad1x.buttonPress(gamepad1.x)) {
            //this was a new button press, not a button held down for a while
            //put the command to be executed here
            robot.fire1();
        }

        if (gamepad1DpadUp.buttonPress(gamepad1.dpad_up)) {
            // this was a new button press, not a
            //button held down for a while
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
//        if (gamepad1x.buttonPress(gamepad1.x)) {
//            if (gamepad1x.isCommand1()) {
//                // call the first command you want to run
//            }
//            if (gamepad1x.isCommand2()) {
//                // call the 2nd command you want to run
//            }
//            if (gamepad1x.isCommand3()) {
//                // call the 3rd command you want to run
//            }
//            if (gamepad1x.isCommand4()) {
//                // call the 4th command you want to run
//            }
//        }

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
            // put the command to be executed here
        }

        if (gamepad2b.buttonPress(gamepad2.b)) {
            // this was a new button press, not a button held down for a while
            // put the command to be executed here
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
    }

}
