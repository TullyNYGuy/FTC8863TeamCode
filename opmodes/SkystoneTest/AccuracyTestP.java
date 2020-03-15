package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.SmartJoystick;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.AutonomousController;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

import java.util.Locale;

@TeleOp(name = "acc test (P only)", group = "ATest")
//@Disabled

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
public class AccuracyTestP extends LinearOpMode {

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

    static double distance(Position p1, Position p2) {
        double dist;
        dist = Math.hypot(p2.y-p1.y, p2.x-p1.x);
        return dist;
    }

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************

        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();
        double Kp = 0.03;
        double Ki = 0;
        double Kd = 0;
        dataLog = new DataLogging("Teleop", telemetry);
        config = new Configuration();
        if (!config.load()) {
            telemetry.addData("ERROR", "Couldn't load config file");
            telemetry.update();
        }
        timer = new ElapsedTime();
        MecanumCommands commands = new MecanumCommands();

        robot = new SkystoneRobot(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);

        AutonomousController controller = new AutonomousController(robot, dataLog, telemetry, Kp, Ki, Kd);

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
        // gamepad1DpadRight = new GamepadButtonMultiPush(1);
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


//        HaloControls haloControls = new HaloControls(gamepad1LeftJoyStickX, gamepad1LeftJoyStickY, gamepad1RightJoyStickX, robot, telemetry);
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

        Position cuurent = new Position();
        cuurent.unit = DistanceUnit.CM;
        Position destination = new Position();
        destination.unit = DistanceUnit.CM;

        // Wait for the start button
        telemetry.addData(">", "Press start to run Teleop");
        telemetry.update();
        waitForStart();

        controller.startController();
        destination.x = 50;
        destination.y = 0;
        double dist;
        controller.moveTo(DistanceUnit.CM, destination.x, destination.y);

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        do {

            //haloControls.calculateMecanumCommands(commands);
            //telemetry.addData("Mecanum", commands);
            //  telemetry.addData("left x joystick value: ", gamepad1LeftJoyStickX.getValue());
//            telemetry.addData("power modifier: ", haloControls.getPowerModifier());
            //robot.setMovement(commands);

            // update the robot
            robot.update();

            //telemetry.addData("mecanum commands are: ", commands);
            // Display telemetry

            idle();
            robot.getCurrentPosition(cuurent);
            dist= distance(destination, cuurent);
            telemetry.addData("Distance: ", String.format(Locale.ENGLISH, "%.2f", dist));
            telemetry.update();

        } while(/*dist > 1 &&*/ opModeIsActive());

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything
    //    dataLog.closeDataLog();
        robot.shutdown();
        controller.stopController();
        telemetry.addData(">", "Done");
        telemetry.update();
        stop();
    }

    //*********************************************************************************************
    //             Helper methods
    //*********************************************************************************************
}

