package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzy;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyStartSpot;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyField;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyMatchInfo;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;
//import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutomaticTeleopFunctions;
//import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyGamepad;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PoseStorage;


import java.util.List;

@TeleOp(name = "Teleop Roadrunner Freight Frenzy", group = "AA")
//@Disabled

public class TeleopUsingRoadRunnerFreightFrenzy extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public FreightFrenzyRobotRoadRunner robot;
    public FreightFrenzyField field;
    public FreightFrenzyGamepad gamepad;
    public Configuration config = null;

   // public AutomaticTeleopFunctions automaticTeleopFunctions;
    //set color for each game
    private FreightFrenzyStartSpot color = PersistantStorage.getStartSpot();
    private ElapsedTime timer;

    DataLogging dataLog = null;

    private Pose2d startPose;

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************

        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        // set the persistant storage variable saying this is the teleop phase
        FreightFrenzyMatchInfo.setMatchPhase(FreightFrenzyMatchInfo.MatchPhase.TELEOP);

        dataLog = new DataLogging("Teleop", telemetry);
        config = null;
        config = new Configuration();
        if (!config.load()) {
            telemetry.addData("ERROR", "Couldn't load config file");
            telemetry.update();
        }
        timer = new ElapsedTime();
        MecanumCommands commands = new MecanumCommands();

        robot = new FreightFrenzyRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        gamepad = new FreightFrenzyGamepad(gamepad1, gamepad2, robot);
        // create the robot and run the init for it

        robot.createRobot();

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        field= new FreightFrenzyField();

        //automaticTeleopFunctions = new AutomaticTeleopFunctions(robot, field, telemetry);

        // create the gamepad
        //gamepad = new UltimateGoalGamepad(gamepad1, gamepad2, robot);
        //robot.shooter.setMotorTicks(PersistantStorage.getMotorTicks());
        //robot.shooter.restoreAngleInfo();
        //gamepad = new FreightFrenzyGamepad(gamepad1, gamepad2, robot, automaticTeleopFunctions);


        if (PersistantStorage.robotPose != null) {
            startPose = PersistantStorage.robotPose;
        } else {
            startPose = PoseStorage.START_POSE;
        }

        robot.mecanum.setPoseEstimate(startPose);
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

        telemetry.addData(">", "Press start to run Teleop (make sure you ran the position setter first!)");
        telemetry.update();
        double multiplier = 1;
        /*
        if (PersistantStorage.robotPose != null) {
            robot.mecanum.setPoseEstimate(PersistantStorage.robotPose);
            multiplier = -1;
        }

*/

        robot.setColor(color);
        waitForStart();

        robot.loopTimer.startLoopTimer();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************
       robot.arm.storageWithElement();
        while (opModeIsActive()) {

            // update the gamepad. It has the commands to be run when a button is pressed so the
            // gamepad actually runs the robot commands.
            gamepad.update();
           // automaticTeleopFunctions.update();

            // The following code uses road runner to move the robot in a driver (field) centric
            // drive

                robot.mecanum.calculateMotorCommandsFieldCentric(
                        gamepad.gamepad1LeftJoyStickYValue * multiplier,
                        gamepad.gamepad1LeftJoyStickXValue * multiplier,
                        gamepad.gamepad1RightJoyStickXValue
                );

            // update the robot
            robot.update();

            // feedback on the driver station

            gamepad.displayGamepad1JoystickValues(telemetry);

            telemetry.addData( "freightSystem mode =", robot.freightSystem.getMode());
            telemetry.addData( "freightSystem state =", robot.freightSystem.getState());
            telemetry.addData( "freightSystem target =", robot.freightSystem.getLevel());






            telemetry.addData(">", "Press Stop to end.");
            telemetry.update();

            idle();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything. Note that some of the subsystem shutdowns may
        // write to the datalog so we can't close it just yet.
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


