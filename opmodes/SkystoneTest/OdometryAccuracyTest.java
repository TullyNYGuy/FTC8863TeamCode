package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.AutonomousController;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Super Complicated Autonomous", group = "A Test")
//@Disabled

public class OdometryAccuracyTest extends LinearOpMode {

    // Put your variable declarations here
    public SkystoneRobot robot;
    public Configuration config;
    DataLogging dataLog = null;
    Position placeholder;
    @Override
    public void runOpMode() {


        // Put your initializations here
        MecanumCommands mecanumCommands = new MecanumCommands();
        boolean intakeState = false;

        /*
        gamepad1LeftJoyStickX = new JoyStick(gamepad1, JoyStick.JoystickSide.LEFT, JoyStick.JoystickAxis.X);
        gamepad1LeftJoyStickY = new JoyStick(gamepad1, JoyStick.JoystickSide.LEFT, JoyStick.JoystickAxis.Y);

        gamepad1RightJoyStickX = new JoyStick(gamepad1, JoyStick.JoystickSide.RIGHT, JoyStick.JoystickAxis.X);
        gamepad1RightJoyStickY = new JoyStick(gamepad1, JoyStick.JoystickSide.RIGHT, JoyStick.JoystickAxis.Y);
  */    dataLog = new DataLogging("Autonomous", telemetry);
        robot = new SkystoneRobot(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        AdafruitIMU8863 imu = new AdafruitIMU8863(hardwareMap);
        MecanumCommands commands = new MecanumCommands();

        ElapsedTime outtakeTimer = new ElapsedTime();

        //Switch intakeLimitSwitchLeft = new Switch(hardwareMap, "IntakeSwitchLeft", Switch.SwitchType.NORMALLY_OPEN);
        //Switch intakeLimitSwitchRight = new Switch(hardwareMap, "IntakeSwitchRight", Switch.SwitchType.NORMALLY_OPEN);

        boolean inOuttake = false;
        final double OUTTAKE_TIME = 2.0;


        // Note from Glenn:
        // None of the following are needed using the class AdafruitIMU8863. They are handled in the
        // initialization of the imu as part of the constructor.

        //**************************************************************
        AutonomousController controller = new AutonomousController(robot, dataLog, telemetry);
        robot.createRobot();
        // start the inits for the robot subsytems
        outtakeTimer.reset();
        robot.init();
        while (!robot.isInitComplete()) {
            robot.update();
            if (outtakeTimer.milliseconds() > 5000) {
                // something went wrong with the inits. They never finished. Proceed anyway
                dataLog.logData("Init failed to complete on time. Proceeding anyway!");
                //How cheerful. How comforting...
                break;
            }
            idle();
        }

        controller.startController();
        robot.setPosition(0,0,0);
        controller.setAllegiance(AutonomousController.Color.RED);
        controller.initPlaces();
        waitForStart();
        while (opModeIsActive()) {
            controller.moveTo(DistanceUnit.CM, 50, 0);
            outtakeTimer.reset();
            while (outtakeTimer.seconds() > 5) {
                idle();
            }
            controller.moveTo(DistanceUnit.CM, 0, 50);
            outtakeTimer.reset();
            while (outtakeTimer.seconds() > 5) {
                idle();
            }
            controller.moveTo(DistanceUnit.CM, -50, 0);
            outtakeTimer.reset();
            while (outtakeTimer.seconds() > 5) {
                idle();
            }
            controller.moveTo(DistanceUnit.CM, 0, -50);
            outtakeTimer.reset();
            while (outtakeTimer.seconds() > 5) {
                idle();
            }
        }
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        dataLog.closeDataLog();
        controller.stopController();
        robot.shutdown();
        telemetry.update();

    }
}
