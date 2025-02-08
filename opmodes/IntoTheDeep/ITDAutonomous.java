package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeep;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDAutonomousStateMachine;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDGamepad;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDRobot;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.List;

@Autonomous(name = "ITD Autonomous", group = "AA")
//@Disabled

public class ITDAutonomous extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public ITDRobot robot;
    public ITDGamepad gamepad;

    public Configuration config = null;
    //public ITDField field;
    private ITDAutonomousStateMachine autonomousStateMachine;

    private ElapsedTime timer;

    DataLogging dataLog = null;

    private Pose2d startPose;

    @Override
    public void runOpMode() {
        MatchPhase.setMatchPhase(MatchPhase.AUTONOMOUS);

        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************

        // setup the log file
        dataLog = new DataLogging("Autonomous", telemetry);
        config = null;
        config = new Configuration();
        if (!config.load()) {
            telemetry.addData("ERROR", "Couldn't load config file");
            telemetry.update();
        }
        timer = new ElapsedTime();

        // create the robot and run the init for it
        robot = new ITDRobot(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        robot.createRobot();
        robot.setAllianceColor(AllianceColorTeamLocation.getAllianceColor());

        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        // create the gamepad
        gamepad = new ITDGamepad(gamepad1, gamepad2, robot);

        // create the power play field. This sets the locations for our particular alliance color
        // and team location (left or right)
        //field = new ITDField(ITDPersistantStorage.getColorLocation());

        // Here is where you create the state machine that is going to be run.
        // Change this state machine out and the robot will do something different.
        autonomousStateMachine = new ITDAutonomousStateMachine(robot, telemetry);
        autonomousStateMachine.setDataLog(dataLog);
        autonomousStateMachine.enableDataLogging();
        // tell the intake bucket controller about the autonomousStateMachine
        // It needs to know this so it can tell the autonomousStateMachine that a gliding
        // intake has failed.
        robot.intakeBucketController.setAutonomousStateMachine(autonomousStateMachine);

        // Allow reads of all of the motor data in one read.
        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        // set the persistant storage variable saying this is the teleop phase
        MatchPhase.setMatchPhase(MatchPhase.AUTONOMOUS);

        // set the start location of the robot

//        if (ITDPersistantStorage.getRobotPose() != null) {
//            startPose = ITDPersistantStorage.getRobotPose();
//        } else {
//            startPose = field.getStartPose();
//        }

        //robot.mecanum.setPoseEstimate(startPose);
        timer.reset();

        // put the webcam stuff here

        // Create the pipeline to use to process the images coming from the webcam. It should be a
        // statement like this:
        //pipeline = new SignalConePipeline(telemetry);

        // start the webcam processing images through the pipeline.
        //robot.webcam.openCamera(OpenCvCameraRotation.UPRIGHT, pipeline);

        // Wait for the start button

        // If you have nothing to do while waiting for the start button to be pressed use:
        // waitForStart();

        // On the other hand, if you do have stuff to do (like display things on the driver station
        // screen while a pipeline is running), use this:
        while (!isStarted()) {

            telemetry.addData(">", "Press start to run Auto (make sure you ran the position setter first!)");
            telemetry.addLine();
            // display the alliance color and team location for the drivers to double check
            telemetry.addData("Alliance color = ", AllianceColorTeamLocation.getAllianceColor().toString());
            telemetry.addData("Team Location  = ", AllianceColorTeamLocation.getTeamLocation().toString());
            telemetry.update();

            // update persistant storage with the current park location
            idle();
        }

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        robot.loopTimer.startLoopTimer();
        dataLog.logData("Auto start" );
        timer.reset();
        // Turn off the webcam and pipeline processing to save CPU cycles
        //robot.webcam.closeCamera();

        // Start the state machine
        autonomousStateMachine.start();
        while (opModeIsActive() && !autonomousStateMachine.isComplete()) {
            autonomousStateMachine.update();
            telemetry.addData("current state is", autonomousStateMachine.getCurrentState());
            telemetry.update();
            robot.update();
            idle();
        }

        dataLog.logData("Auto complete in ", timer.milliseconds() );
        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit or auto is complete, shutdown everything. Note that some of the subsystem shutdowns may
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


