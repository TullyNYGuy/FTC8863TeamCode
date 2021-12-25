package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.SmartJoystick;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.AutonomousController;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

@TeleOp(name = "PID move to", group = "ATest")
@Disabled

/*
 * Class for Skystone TeleOp mode
 * Gamepad 1 layout
 *    / Left JoystickX - robot moves left/right
 *    / Left JoystickY - robot moves forward/backward
 *    / Right JoystickX - robot rotation
 *    / DPad Up -  Change Drive Mode
 *    / DPad Left -
 *    / DPad Down - Reset heading
 *    / DPad Right -
 *    / A - Outtake
 *    / B - Start/Stop intake state machine
 *    / X - Change Power
 *    / Y -
 *  Gamepad 2 layout
 *    / Left JoystickX -
 *    / Left JoystickY -
 *    / Right JoystickX -
 *    / Right JoystickY - Extension arm in and out
 *    / DPad Up -
 *    / DPad Left -
 *    / DPad Down -
 *    / DPad Right-
 *    / A - add 1 to height counter
 *    / B - reset height counter to 1
 *    / X - confirm lift movement
 *    / Y -
 */
public class MoveToWithPID extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public SkystoneRobot robot;

    DataLogging dataLog = null;

    public Configuration config;

    private ElapsedTime timer;

    public int blockLevel = 0;

    // GAMEPAD 1

    // declare the buttons on the gamepad as multi push button objects
    // public GamepadButtonMultiPush gamepad1RightBumper;
    //public GamepadButtonMultiPush gamepad1LeftBumper;
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
    //public GamepadButtonMultiPush gamepad1LeftTriggerButton;

    // joystick and joystick value declarations - game pad 1
    final static double JOYSTICK_DEADBAND_VALUE = .15;
    final static double JOYSTICK_HALF_POWER = .5;
    final static double JOYSTICK_QUARTER_POWER = .25;

    SmartJoystick gamepad1LeftJoyStickX;
    SmartJoystick gamepad1LeftJoyStickY;
    double gamepad1LeftJoyStickXValue = 0;
    double gamepad1LeftJoyStickYValue = 0;

    SmartJoystick gamepad1RightJoyStickX;
    SmartJoystick gamepad1RightJoyStickY;
    double gamepad1RightJoyStickXValue = 0;
    double gamepad1RightJoyStickYValue = 0;

    // GAMEPAD 2

    // declare the buttons on the gamepad as multi push button objects
    //public GamepadButtonMultiPush gamepad2RightBumper;
    // public GamepadButtonMultiPush gamepad2LeftBumper;
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
    //JoyStick gamepad2LeftJoyStickY;
    SmartJoystick gamepad2LeftJoyStickY;
    double gamepad2LeftJoyStickXValue = 0;
    double gamepad2LeftJoyStickYValue = 0;

    JoyStick gamepad2RightJoyStickX;
    JoyStick gamepad2RightJoyStickY;
    double gamepad2RightJoyStickXValue = 0;
    double gamepad2RightJoyStickYValue = 0;

    // drive train powers for tank drive
    double leftPower = 0;
    double rightPower = 0;

    // drive train powers for differential drive
    double throttle = 0;
    double direction = 0;


    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************

        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        dataLog = new DataLogging("Teleop", telemetry);
        config = new Configuration();
        if (!config.load()) {
            telemetry.addData("ERROR", "Couldn't load config file");
            telemetry.update();
        }
        timer = new ElapsedTime();
        MecanumCommands commands = new MecanumCommands();

        robot = new SkystoneRobot(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        robot.enableDataLogging();
AutonomousController controller = new AutonomousController(robot, dataLog, telemetry, .03, 0,0);
controller.startController();
        // create the gamepad 1 buttons and tell each button how many commands it has
        // gamepad1RightBumper = new GamepadButtonMultiPush(1);
        // gamepad1LeftBumper = new GamepadButtonMultiPush(1);
        gamepad1a = new GamepadButtonMultiPush(1);
        gamepad1b = new GamepadButtonMultiPush(1);
         gamepad1y = new GamepadButtonMultiPush(2);
        gamepad1x = new GamepadButtonMultiPush(1);
        gamepad1DpadUp = new GamepadButtonMultiPush(1);
        gamepad1DpadDown = new GamepadButtonMultiPush(1);
        // gamepad1DpadLeft = new GamepadButtonMultiPush(1);
         gamepad1DpadRight = new GamepadButtonMultiPush(1);
        gamepad1LeftStickButton = new GamepadButtonMultiPush(1);
        gamepad1RightStickButton = new GamepadButtonMultiPush(1);
        // gamepad1LeftTriggerButton = new GamepadButtonMultiPush(1);

        // Game Pad 1 joysticks
        gamepad1LeftJoyStickX = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.X);
        gamepad1LeftJoyStickY = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.Y);

        gamepad1RightJoyStickX = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.X);
        gamepad1RightJoyStickY = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.Y);

        // create the gamepad 2 buttons and tell each button how many commands it has
        //gamepad2RightBumper = new GamepadButtonMultiPush(1);
        // gamepad2LeftBumper = new GamepadButtonMultiPush(1);
        gamepad2a = new GamepadButtonMultiPush(8);
        gamepad2b = new GamepadButtonMultiPush(1);
        gamepad2y = new GamepadButtonMultiPush(2);
        gamepad2x = new GamepadButtonMultiPush(1);
//       gamepad2DpadDown = new GamepadButtonMultiPush(1);
        // gamepad2DpadLeft = new GamepadButtonMultiPush(1);
        //  gamepad2DpadRight = new GamepadButtonMultiPush(1);
        gamepad2LeftStickButton = new GamepadButtonMultiPush(1);
        gamepad2RightStickButton = new GamepadButtonMultiPush(1);

        // Game Pad 2 joysticks
        gamepad2LeftJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad2LeftJoyStickY = new SmartJoystick(gamepad2, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.Y);

        gamepad2RightJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad2RightJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);



        HaloControls haloControls = new HaloControls(gamepad1LeftJoyStickX, gamepad1LeftJoyStickY, gamepad1RightJoyStickX, robot, telemetry);
        robot.createRobot();
        // start the inits for the robot subsytems
        robot.init();
        timer.reset();

        // run the state machines associated with the subsystems to allow the inits to complete
        // NOTE, if a subsystem does not complete the init, it will hang the robot, so that is what
        // the timer is for
        while (!robot.isInitComplete()) {
            robot.update();
            if (timer.milliseconds() > 5000) {
                // something went wrong with the inits. They never finished. Proceed anyway

                dataLog.logData("Init failed to complete on time. Proceeding anyway!");
                //How cheerful. How comforting...
                break;
            }
            idle();
        }

        // Wait for the start button
        telemetry.addData(">", "Press start to run Teleop");
        telemetry.update();
        robot.setPosition(0,0,0);
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        while (opModeIsActive()) {

            //*************************************************************************************
            // Gamepad 1 buttons
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
            if (gamepad1DpadDown.buttonPress(gamepad1.dpad_down)) {
                haloControls.resetHeading();
            }
            if (gamepad1DpadUp.buttonPress(gamepad1.dpad_up)) {
                haloControls.toggleMode();
            }
            if (gamepad1a.buttonPress(gamepad1.a)) {
                robot.intakeSpitOut();
            }
            if (gamepad1b.buttonPress(gamepad1.b)) {
                robot.intakeBlock();
            }
            // if (gamepad1LeftTriggerButton.triggerPress(gamepad1.left_trigger)) {
            // }

            // if (gamepad1RightBumper.buttonPress(gamepad1.right_bumper)) {
            // }

            // if (gamepad1LeftBumper.buttonPress(gamepad1.left_bumper)) {
            //  }



            if (gamepad1y.buttonPress(gamepad1.y)) {
                if (gamepad1y.isCommand1()) {
                    robot.baseGrab();
                }
                if (gamepad1y.isCommand2()) {
                    robot.baseRelease();
                }
            }

            if (gamepad1x.buttonPress(gamepad1.x)) {

                haloControls.togglePowerModifier();


            }
/*
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
            */

/*
            if (gamepad1DpadLeft.buttonPress(gamepad1.dpad_left)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                gamepad1LeftJoyStickX.setHalfPower();
                gamepad1LeftJoyStickY.setHalfPower();
                gamepad1RightJoyStickX.setHalfPower();
                gamepad1RightJoyStickY.setHalfPower();
            }
*/

            if (gamepad1DpadRight.buttonPress(gamepad1.dpad_right)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed hererobot.moveTo();
                controller.moveTo(DistanceUnit.CM, 0, 100);
            }

/*
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

            // if (gamepad2RightBumper.buttonPress(gamepad2.right_bumper)) {
            // }

            // if (gamepad2LeftBumper.buttonPress(gamepad2.left_bumper)) {
            //  }
*/
            if (gamepad2a.buttonPress(gamepad2.a)) {
                robot.increaseDesiredHeightForLift();

            }

            if (gamepad2b.buttonPress(gamepad2.b)) {
                robot.resetSkyscraperLevel();
            }
            if (gamepad2y.buttonPress(gamepad2.y)) {
                if (gamepad2y.isCommand1()) {
                    robot.baseGrab();
                }
                if (gamepad2y.isCommand2()) {
                    robot.baseRelease();
                }

            }


            if (gamepad2x.buttonPress(gamepad2.x)) {
                robot.liftBlock();
            }
/*

            // if (gamepad2DpadUp.buttonPress(gamepad2.dpad_up)) {
            //   if (gamepad2DpadUp.isCommand1()) {
            // }
            // if (gamepad2DpadUp.isCommand2()) {
            //  }
            // }

            // if (gamepad2DpadDown.buttonPress(gamepad2.dpad_down)) {
            //  }

            // if (gamepad2DpadLeft.buttonPress(gamepad2.dpad_left)) {
            // }

            // if (gamepad2DpadRight.buttonPress(gamepad2.dpad_right)) {
            // }

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

            // joysticks to tank drive
            leftPower = gamepad1LeftJoyStickYValue;
            rightPower = gamepad1RightJoyStickYValue;

            // joysticks to differential drive
            throttle = gamepad1RightJoyStickYValue;
            direction = gamepad1RightJoyStickXValue;

 */
            haloControls.calculateMecanumCommands(commands);
            telemetry.addData("Mecanum", commands);
            //  telemetry.addData("left x joystick value: ", gamepad1LeftJoyStickX.getValue());
//            telemetry.addData("power modifier: ", haloControls.getPowerModifier());

            robot.setMovement(commands);

            // update the robot
            robot.update();

   telemetry.addData("mecanum commands are: ", commands);
            // Display telemetry
            telemetry.addData(">", "Press Stop to end.");
            telemetry.update();

            idle();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything
        dataLog.closeDataLog();
        controller.stopController();
        robot.shutdown();
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    //*********************************************************************************************
    //             Helper methods
    //*********************************************************************************************
}

