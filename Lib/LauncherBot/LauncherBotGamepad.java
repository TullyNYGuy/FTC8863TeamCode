package org.firstinspires.ftc.teamcode.Lib.LauncherBot;

/*
 * Class for LauncherBot mode
 * Gamepad 1 layout
 *    / Left JoystickX   - robot moves left/right
 *    / Left JoystickY   - robot moves forward/backward
 *    / Right JoystickX  - robot rotation
 *    / Right JoystickY  -
 *    / DPad Up          -
 *    / DPad Left        -
 *    / DPad Down        -
 *    / DPad Right       -
 *    / A                -
 *    / B                -
 *    / X                -
 *    / Y                -
 *    /Left Bumper       -
 *    /Right Bumper      -
 *    /Left stick button - full power (1st press), half power (2nd press)
 *
 *  Gamepad 2 layout
 *    / Left JoystickX   -
 *    / Left JoystickY   -
 *    / Right JoystickX  -
 *    / Right JoystickY  -
 *    / DPad Up          -
 *    / DPad Left        -
 *    / DPad Down        -
 *    / DPad Right       -
 *    / A                -
 *    / B                -
 *    / X                -
 *    / Y                -
 *   /Left Bumper        -
 *   /Right Bumper       -
 */

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.SmartJoystick;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;
//import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutomaticTeleopFunctions;


public class LauncherBotGamepad {

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

    public LauncherBotRobot robot;

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

    public LauncherBotGamepad(Gamepad gamepad1, Gamepad gamepad2, LauncherBotRobot robot) {
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
        this.robot = robot;
        //this.automaticTeleopFunctions = automaticTeleopFunctions;

        //
        //YOU WILL HAVE TO CONFIGURE THE GAMEPAD BUTTONS FOR TOGGLING IF YOU WANT THAT. DO THAT HERE.
        //

        // create the gamepad 1 buttons and tell each button how many commands it has
        gamepad1RightBumper = new GamepadButtonMultiPush(2);
        gamepad1LeftBumper = new GamepadButtonMultiPush(1);
        gamepad1a = new GamepadButtonMultiPush(1);
        gamepad1b = new GamepadButtonMultiPush(2);
        gamepad1y = new GamepadButtonMultiPush(1);
        gamepad1x = new GamepadButtonMultiPush(2);
        gamepad1DpadUp = new GamepadButtonMultiPush(1);
        gamepad1DpadDown = new GamepadButtonMultiPush(1);
        gamepad1DpadLeft = new GamepadButtonMultiPush(1);
        gamepad1DpadRight = new GamepadButtonMultiPush(1);
        gamepad1LeftStickButton = new GamepadButtonMultiPush(2);
        gamepad1RightStickButton = new GamepadButtonMultiPush(1);
        gamepad1LeftTriggerButton = new GamepadButtonMultiPush(1);

        // Game Pad 1 joysticks
        gamepad1LeftJoyStickX = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.X);
        //road runner handles the joystick sign inversions
        gamepad1LeftJoyStickX.setInvertSign(JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1LeftJoyStickY = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.Y);
        //road runner handles the joystick sign inversions
        gamepad1LeftJoyStickY.setInvertSign(JoyStick.InvertSign.INVERT_SIGN);

        gamepad1RightJoyStickX = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.X);
        //road runner handles the joystick sign inversions
        gamepad1RightJoyStickX.setInvertSign(JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1RightJoyStickY = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.Y);
        //road runner handles the joystick sign inversions
        gamepad1RightJoyStickY.setInvertSign(JoyStick.InvertSign.NO_INVERT_SIGN);

        //create the gamepad 2 buttons and tell each button how many commands it has
        gamepad2RightBumper = new GamepadButtonMultiPush(1);
        gamepad2LeftBumper = new GamepadButtonMultiPush(1);
        gamepad2a = new GamepadButtonMultiPush(1);
        gamepad2b = new GamepadButtonMultiPush(2);
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
        gamepad2LeftJoyStickY.setInvertSign(JoyStick.InvertSign.NO_INVERT_SIGN);

        gamepad2RightJoyStickX = new SmartJoystick(gamepad2, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.X);
        gamepad2RightJoyStickY = new SmartJoystick(gamepad2, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.Y);
        gamepad2RightJoyStickY.setInvertSign(JoyStick.InvertSign.NO_INVERT_SIGN);
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
            }
            if (gamepad1RightBumper.isCommand2()) {
            }
        }

        if (gamepad1LeftBumper.buttonPress(gamepad1.left_bumper)) {
        }

        if (gamepad1a.buttonPress(gamepad1.a)) {
        }

        if (gamepad1b.buttonPress(gamepad1.b)) {
        }

        if (gamepad1y.buttonPress(gamepad1.y)) {
        }

        if (gamepad1x.buttonPress(gamepad1.x)) {
            if (gamepad1x.isCommand1()) {
                robot.shooter.setSpeed(6000);
            }
            if (gamepad1x.isCommand2()) {
                robot.shooter.stopGearbox();
            }
        }

        if (gamepad1DpadUp.buttonPress(gamepad1.dpad_up)) {
        }

        if (gamepad1DpadDown.buttonPress(gamepad1.dpad_down)) {
        }

        if (gamepad1DpadLeft.buttonPress(gamepad1.dpad_left)) {
        }

        if (gamepad1DpadRight.buttonPress(gamepad1.dpad_right)) {;
        }

        if (gamepad1LeftStickButton.buttonPress(gamepad1.left_stick_button)) {
            if (gamepad1LeftStickButton.isCommand1()) {
                gamepad1LeftJoyStickX.setFullPower();
                gamepad1LeftJoyStickY.setFullPower();
                gamepad1RightJoyStickX.setFullPower();
                gamepad1RightJoyStickY.setFullPower();
            }
            if (gamepad1LeftStickButton.isCommand2()) {
                gamepad1LeftJoyStickX.setHalfPower();
                gamepad1LeftJoyStickY.setHalfPower();
                gamepad1RightJoyStickX.setHalfPower();
                gamepad1RightJoyStickY.setHalfPower();
            }
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

        gamepad1LeftJoyStickXValue = gamepad1LeftJoyStickX.getValue();
        gamepad1LeftJoyStickYValue = gamepad1LeftJoyStickY.getValue();

        gamepad1RightJoyStickXValue = gamepad1RightJoyStickX.getValue();
        gamepad1RightJoyStickYValue = gamepad1RightJoyStickY.getValue();

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
           
        }

        if (gamepad2LeftBumper.buttonPress(gamepad2.left_bumper)) {

        }

        if (gamepad2a.buttonPress(gamepad2.a)) {

        }

        if (gamepad2b.buttonPress(gamepad2.b)) {
            if (gamepad2b.isCommand1()) {
            }
            if (gamepad2b.isCommand2()) {
            }
        }

        if (gamepad2y.buttonPress(gamepad2.y)) {
        }

        if (gamepad2x.buttonPress(gamepad2.x)) {
        }

        if (gamepad2DpadUp.buttonPress(gamepad2.dpad_up)) {
        }

        if (gamepad2DpadDown.buttonPress(gamepad2.dpad_down)) {
        }

        if (gamepad2DpadLeft.buttonPress(gamepad2.dpad_left)) {
        }

        if (gamepad2DpadRight.buttonPress(gamepad2.dpad_right)) {
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

        gamepad2LeftJoyStickXValue = gamepad2LeftJoyStickX.getValue();
        gamepad2LeftJoyStickYValue = gamepad2LeftJoyStickY.getValue();

        gamepad2RightJoyStickXValue = gamepad2RightJoyStickX.getValue();
        gamepad2RightJoyStickYValue = gamepad2RightJoyStickY.getValue();
    }

    public void displayGamepad1JoystickValues(Telemetry telemetry) {
        telemetry.addData("1-leftJoyStickY  = ", gamepad1LeftJoyStickYValue);
        telemetry.addData("1-leftJoyStickX  = ", gamepad1LeftJoyStickXValue);
        telemetry.addData("1-rightJoyStickY = ", gamepad1RightJoyStickYValue);
        telemetry.addData("1-rightJoyStickX = ", gamepad1RightJoyStickXValue);
    }

    public void displayGamepad2JoystickValues(Telemetry telemetry) {
        telemetry.addData("2-leftJoyStickY  = ", gamepad2LeftJoyStickYValue);
        telemetry.addData("2-leftJoyStickX  = ", gamepad2LeftJoyStickXValue);
        telemetry.addData("2-rightJoyStickY = ", gamepad2RightJoyStickYValue);
        telemetry.addData("2-rightJoyStickX = ", gamepad2RightJoyStickXValue);
    }

}

