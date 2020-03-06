package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PurePursuit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RobotPosition;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.AutonomousController;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

import java.util.ArrayList;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "Autonomous Smol Test w/ PP", group = "A Test")
//@Disabled

public class AunomousSmolTestPurePusuit extends LinearOpMode {
    static double distance(RobotPosition p1, RobotPosition p2) {
        double dist;
        dist = Math.hypot(p2.y-p1.y, p2.x-p1.x);
        return dist;
    }

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
        ArrayList<RobotPosition> positions = new ArrayList<RobotPosition>();

       // PurePursuit pursuit = new PurePursuit()
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
        AutonomousController controller = new AutonomousController(robot, dataLog, telemetry, 0.03,0,0);
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
        AutonomousController.Color color = AutonomousController.Color.RED;
controller.setAllegiance(AutonomousController.Color.RED);
controller.initPlaces();

        RobotPosition buildsite;
        RobotPosition bridge ;
        RobotPosition platform;
        RobotPosition home;
        RobotPosition nearCentreBridge;

        if (color == AutonomousController.Color.BLUE) {

            buildsite= new RobotPosition(DistanceUnit.CM, 121.92, 121.92, AngleUnit.RADIANS, 0);
            bridge =new RobotPosition(DistanceUnit.CM, 0, 121.92, AngleUnit.RADIANS, 0);
            platform = new RobotPosition(DistanceUnit.CM, 121.92, 30.48, AngleUnit.RADIANS, 0);
           home =new RobotPosition(DistanceUnit.CM, -121.92, 0, AngleUnit.RADIANS, 0);
             nearCentreBridge =new RobotPosition(DistanceUnit.CM, 0, 152.4, AngleUnit.RADIANS, 0);
        } else {
            buildsite= new RobotPosition(DistanceUnit.CM, 121.92, -121.92, AngleUnit.RADIANS, 0);
             bridge = new RobotPosition(DistanceUnit.CM, 0, -121.92, AngleUnit.RADIANS, 0);
             platform = new RobotPosition(DistanceUnit.CM, 121.92, -30.48, AngleUnit.RADIANS, 0);
             home = new RobotPosition(DistanceUnit.CM, -121.92, 0, AngleUnit.RADIANS, 0);
             nearCentreBridge = new RobotPosition(DistanceUnit.CM, 0, -152.4, AngleUnit.RADIANS, 0);
        }
        double dist;
        RobotPosition point1 = new RobotPosition(DistanceUnit.CM, 20, 0, AngleUnit.DEGREES, 0);
        RobotPosition point2 = new RobotPosition(DistanceUnit.CM, 20, 20, AngleUnit.DEGREES, 0);
        RobotPosition point3 = new RobotPosition(DistanceUnit.CM, 0, 20, AngleUnit.DEGREES, 0);
        RobotPosition point4 = new RobotPosition(DistanceUnit.CM, 0, 0, AngleUnit.DEGREES, 0);
        positions.add(point1);
        positions.add(point2);
        positions.add(point3);
        positions.add(point4);

        positions.add(nearCentreBridge);
        positions.add( home);
        RobotPosition current = new RobotPosition();
        current.distanceUnit = DistanceUnit.CM;
        current.angleUnit = AngleUnit.DEGREES;

        PurePursuit pursuit = new PurePursuit(4, positions);
        robot.getCurrentRobotPosition(current);
        pursuit.getNextPosition(current);

        waitForStart();

controller.goTo(AutonomousController.Areas.HOME);
        do {
            robot.getCurrentRobotPosition(current);
            PurePursuit.ResultPosition res = pursuit.getNextPosition(current);
            controller.moveTo(res.pos.distanceUnit, res.pos.x, res.pos.y);

            dist= distance(res.pos, current);
            if(res.reached == true){

                break;
            }

            idle();
        }while(dist>1);

controller.pickUpBlock();
controller.goTo(AutonomousController.Areas.BRIDGE);
        do {
            robot.getCurrentRobotPosition(current);
            PurePursuit.ResultPosition res = pursuit.getNextPosition(current);
            controller.moveTo(res.pos.distanceUnit, res.pos.x, res.pos.y);

            dist= distance(res.pos, current);
            if(res.reached == true){

                break;
            }

            idle();
        }while(dist>1);
controller.goTo(AutonomousController.Areas.PLATFORM);
        do {
            robot.getCurrentRobotPosition(current);
            PurePursuit.ResultPosition res = pursuit.getNextPosition(current);
            controller.moveTo(res.pos.distanceUnit, res.pos.x, res.pos.y);

            dist= distance(res.pos, current);
            if(res.reached == true){

                break;
            }

            idle();
        }while(dist>1);
controller.putBlockOnBase();
controller.moveBaseRotate();
controller.goTo(AutonomousController.Areas.BRIDGE);
        do {
            robot.getCurrentRobotPosition(current);
            PurePursuit.ResultPosition res = pursuit.getNextPosition(current);
            controller.moveTo(res.pos.distanceUnit, res.pos.x, res.pos.y);

            dist= distance(res.pos, current);
            if(res.reached == true){

                break;
            }

            idle();
        }while(dist>1);
controller.goTo(AutonomousController.Areas.HOME);
        do {
            robot.getCurrentRobotPosition(current);
            PurePursuit.ResultPosition res = pursuit.getNextPosition(current);
            controller.moveTo(res.pos.distanceUnit, res.pos.x, res.pos.y);

            dist= distance(res.pos, current);
            if(res.reached == true){

                break;
            }

            idle();
        }while(dist>1);
controller.pickUpBlock();
controller.goTo(AutonomousController.Areas.BRIDGE);
        do {
            robot.getCurrentRobotPosition(current);
            PurePursuit.ResultPosition res = pursuit.getNextPosition(current);
            controller.moveTo(res.pos.distanceUnit, res.pos.x, res.pos.y);

            dist= distance(res.pos, current);
            if(res.reached == true){

                break;
            }

            idle();
        }while(dist>1);
controller.goTo(AutonomousController.Areas.PLATFORM);
        do {
            robot.getCurrentRobotPosition(current);
            PurePursuit.ResultPosition res = pursuit.getNextPosition(current);
            controller.moveTo(res.pos.distanceUnit, res.pos.x, res.pos.y);

            dist= distance(res.pos, current);
            if(res.reached == true){

                break;
            }

            idle();
        }while(dist>1);
controller.putBlockOnBase();
controller.parkWithArm();
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        dataLog.closeDataLog();
        controller.stopController();
        robot.shutdown();
        telemetry.update();

    }
}
