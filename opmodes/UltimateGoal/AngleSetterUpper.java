package org.firstinspires.ftc.teamcode.opmodes.UltimateGoal;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.util.Angle;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.AutomaticTeleopFunctions;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PoseStorage;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalGamepad;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;
import org.firstinspires.ftc.teamcode.opmodes.SkystoneDiagnostics.TestDualLiftLimitSwitches;

import java.util.List;

@TeleOp(name = "shooter angle set up", group = "AA")
//@Disabled

public class AngleSetterUpper extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public UltimateGoalRobotRoadRunner robot;
   // public UltimateGoalGamepad gamepad;
    public Configuration config;
public Switch limitSwitch;
    //public AutomaticTeleopFunctions automaticTeleopFunctions;

    private ElapsedTime timer;

    DataLogging dataLog = null;

   // private Pose2d startPose;

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************

        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        dataLog = new DataLogging("Teleop", telemetry);
        config = null;
//        config = new Configuration();
//        if (!config.load()) {
//            telemetry.addData("ERROR", "Couldn't load config file");
//            telemetry.update();
//        }
        timer = new ElapsedTime();
        //MecanumCommands commands = new MecanumCommands();
        PersistantStorage.setShooterAngle(0,AngleUnit.DEGREES);
        PersistantStorage.setMotorTicks(0);
        robot = new UltimateGoalRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        FtcDashboard.start();
        // create the robot and run the init for it
        robot.createRobot();


        PersistantStorage.setShooterAngle(0,AngleUnit.DEGREES);
        PersistantStorage.setMotorTicks(0);
        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

       // automaticTeleopFunctions = new AutomaticTeleopFunctions(robot);
        // create the gamepad
        //gamepad = new UltimateGoalGamepad(gamepad1, gamepad2, robot);
       // robot.shooter.setMotorTicks(PersistantStorage.getMotorTicks());
      //  gamepad = new UltimateGoalGamepad(gamepad1, gamepad2, robot, automaticTeleopFunctions);

/*
        if (PersistantStorage.robotPose != null) {
            startPose = PersistantStorage.robotPose;
        } else {
            startPose = PoseStorage.START_POSE;
        }

        robot.mecanum.setPoseEstimate(startPose);
        */

        timer.reset();

        // the inits are run as part of createRobot(). They should not be needed here.
//        // run the state machines associated with the subsystems to allow the inits to complete
//        // NOTE, if a subsystem does not complete the init, it will hang the robot, so that is what
//        // the timer is for
//        while (!robot.isInitComplete()) {
//            robot.update();
//            if (timer.milliseconds() > 5000) {
//                // something went wrong with the inits. They never finished. Proceed anyway
//
//                dataLog.logData("Init failed to complete on time. Proceeding anyway!");
//                //How cheerful. How comforting...
//                break;
//            }
//            idle();
//        }

        // Wait for the start button
        telemetry.addData(">", "Press start to run Teleop");
        telemetry.update();
        double multiplier = -1;
        if (PersistantStorage.robotPose != null) {
            robot.mecanum.setPoseEstimate(PersistantStorage.robotPose);
            multiplier = -1;
        }


        waitForStart();
        robot.shooter.angleLower();
        while(!robot.shooter.isSwitchTriggered() && opModeIsActive()){

            robot.update();
        }
        PersistantStorage.setShooterAngle( 0,AngleUnit.DEGREES);
        PersistantStorage.setMotorTicks(0);
        robot.shooter.resetMotor();
       // robot.shooter.setMotorTicks(robot.shooter.getMotorTicks());
        robot.loopTimer.startLoopTimer();
        robot.shooter.setAngle(AngleUnit.DEGREES,20);



        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        while (opModeIsActive()) {

            // update the gamepad. It has the commands to be run when a button is pressed so the
            // gamepad actually runs the robot commands.
//            automaticTeleopFunctions.update();

            // The following code uses road runner to move the robot in a driver (field) centric
            // drive
            /*
            if (!automaticTeleopFunctions.isBusy()) {
                robot.mecanum.calculateMotorCommandsFieldCentric(
                        gamepad.gamepad1LeftJoyStickYValue * multiplier,
                        gamepad.gamepad1LeftJoyStickXValue * multiplier,
                        gamepad.gamepad1RightJoyStickXValue
                );
            }
*/

            // update the robot
            robot.update();

            // feedback on the driver station
            //robot.displaySwitches();
           //  gamepad.displayGamepad1JoystickValues(telemetry);

            telemetry.addData(">", "Press Stop to end.");
            telemetry.update();

            idle();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything. Note that some of the subsystem shutdowns may
        // write to the datalog so we can't close it just yet.
        PersistantStorage.setMotorTicks(robot.shooter.getMotorTicks());
        robot.shutdown();
        dataLog.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    //*********************************************************************************************
    //             Helper methods
    //*********************************************************************************************

    public void enableBulkReads(HardwareMap hardwareMap, LynxModule.BulkCachingMode mode) {
        // set bulk read mode for the sensor reads - speeds up the loop
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(mode);
        }
    }
}


