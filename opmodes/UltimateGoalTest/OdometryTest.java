package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.SmartJoystick;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.AutonomousController;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobot;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.TestMecanumToDrivetrain;
import org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest.TestOdometryModule;

/**
 * Created by ball on 10/7/2017.
 */

@TeleOp(name = "Odometry Test", group = "Run")
//@Disabled

public class OdometryTest extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations - THESE ARE JUST SETTING UP THE VARIABLES (RESERVING MEMORY FOR THEM)
    //*********************************************************************************************

    // Here are declarations for all of the hardware

    // Here is the declaration for the hardware map - it contains information about the configuration
    // of the robot and how to talk to each piece of hardware
    //public HardwareMap hardwareMap;

    // GAMEPAD 1 - declare all of the objects on game pad 1

    // declare the buttons on the gamepad as multi push button objects



    // GAMEPAD 2 - declare all of the objects on game pad 2


    class TestRobot implements FTCRobot {

        AdafruitIMU8863 imu;

        public TestRobot(AdafruitIMU8863 imu) {
            this.imu = imu;
        }

        @Override
        public boolean createRobot() {
            return false;
        }

        @Override
        public void init() {

        }

        @Override
        public boolean isInitComplete() {
            return false;
        }

        @Override
        public void update() {

        }

        @Override
        public void shutdown() {

        }

        @Override
        public void timedUpdate(double timerValueMsec) {

        }

        @Override
        public double getCurrentRotation(AngleUnit unit) {
            return unit.fromDegrees(imu.getHeading());
        }
    }

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************
        /*
        TestOdometryModule right = new TestOdometryModule(hardwareMap);
        TestOdometryModule left= new TestOdometryModule(hardwareMap);
        TestOdometryModule back = new TestOdometryModule(hardwareMap);
        */
        Configuration config = new Configuration();
        config.load();
        DistanceUnit units = DistanceUnit.CM;
        MecanumCommands mecanumCommands = new MecanumCommands();
        GamepadButtonMultiPush gamepad1DpadUp;
        GamepadButtonMultiPush gamepad1DpadDown;
        GamepadButtonMultiPush gamepad1DpadLeft;
        AdafruitIMU8863 imu = new AdafruitIMU8863(hardwareMap);
        DcMotor8863 frontLeft = DcMotor8863.createMotorFromFile(config, "FLMotor", hardwareMap);
        DcMotor8863 backLeft = DcMotor8863.createMotorFromFile(config, "BLMotor", hardwareMap);
        DcMotor8863 frontRight = DcMotor8863.createMotorFromFile(config, "FRMotor", hardwareMap);
        DcMotor8863 backRight = DcMotor8863.createMotorFromFile(config, "BRMotor", hardwareMap);

        SmartJoystick gamepad1LeftJoyStickX = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.X);
        SmartJoystick gamepad1LeftJoyStickY = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.LEFT, SmartJoystick.JoystickAxis.Y);

        SmartJoystick gamepad1RightJoyStickX = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.X);
        SmartJoystick gamepad1RightJoyStickY = new SmartJoystick(gamepad1, SmartJoystick.JoystickSide.RIGHT, SmartJoystick.JoystickAxis.Y);
        gamepad1DpadUp = new GamepadButtonMultiPush(1);
        gamepad1DpadDown = new GamepadButtonMultiPush(1);
        gamepad1DpadLeft = new GamepadButtonMultiPush(1);


/*
        OdometryModule left = new OdometryModule(1440, 3.8 * Math.PI, units, "FrontLeft", hardwareMap);
        OdometryModule right = new OdometryModule(1440, 3.8 * Math.PI, units, "BackLeft", hardwareMap);
        OdometryModule back = new OdometryModule(1440, 3.8 * Math.PI, units, "BackRight", hardwareMap);
        OdometrySystem odometry = new OdometrySystem(units, left, right, back);
        odometry.loadConfiguration(config);

 */
        DataLogging dataLog = new DataLogging("OdometryTest", telemetry);
        UltimateGoalRobot robot = new UltimateGoalRobot(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
    //    AutonomousController controller = new AutonomousController(robot, dataLog, telemetry, 0, 0, 0);
        HaloControls haloControls = new HaloControls(gamepad1LeftJoyStickY, gamepad1LeftJoyStickX, gamepad1RightJoyStickX, robot, telemetry);
        haloControls.setMode(HaloControls.Mode.DRIVER_MODE);
        telemetry.addData("Status:", "Initializing robot");
        telemetry.update();
        robot.createRobot();
        robot.init();
        while(!robot.isInitComplete()) {
            idle();
        }

/*
        OdometrySystem trial = new OdometrySystem(DistanceUnit.CM, left, right, back);
        trial.initializeRobotGeometry(DistanceUnit.CM, 0, 1, DcMotorSimple.Direction.REVERSE, 0, 1, DcMotorSimple.Direction.FORWARD, 1, 0, DcMotorSimple.Direction.REVERSE);
        left.setData(0);
        right.setData(0);
        back.setData(1);
        trial.calculateMoveDistance();
*/
        Position shower = new Position(DistanceUnit.INCH, 0, 0, 0, 0);
        shower.unit = DistanceUnit.CM;
        // create the robot. Tell the driver we are creating it since this can take a few seconds
        // and we want the driver to know what is going on.
       // telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.addData("Status:", "Initialization complete. Press Start.");
        telemetry.update();


        waitForStart();
  //      controller.startController();
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


            // update the drive motors with the new power
            haloControls.calculateMecanumCommands(mecanumCommands);
            // mecanum commands could come from joysticks or from autonomous calculations. That is why HaloControls is not part of Mecanum class
            //*****************************************************************
            // Is this any better than mecanum.getFrontLeft() etc?
            //*****************************************************************


            robot.setMovement(mecanumCommands);

            if (gamepad1DpadDown.buttonPress(gamepad1.dpad_down)) {
                haloControls.resetHeading();
            }
            if (gamepad1DpadUp.buttonPress(gamepad1.dpad_up)) {
                haloControls.toggleMode();
            }
            if (gamepad1DpadLeft.buttonPress(gamepad1.dpad_left)) {
                haloControls.togglePowerModifier();
            }


            robot.timedUpdate(0);

            robot.getCurrentPosition(shower);
//            odometry.getCurrentPosition(shower);
            telemetry.addData("robot moved: ", shower);
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

