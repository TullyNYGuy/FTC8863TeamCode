package org.firstinspires.ftc.teamcode.opmodes.PowerPlay;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.Pipelines.SignalConePipeline;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayAutonomousNoVisionParkLocationOneOrThreeStateMachine;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayAutonomousNoVisionParkLocationTwo;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayAutonomousStateMachine;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayField;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayGamepad;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.List;

@Autonomous(name = "Autonomous Power Play", group = "AA")
//@Disabled

public class PowerPlayAutonomous extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public PowerPlayRobot robot;
    public PowerPlayGamepad gamepad;
    public Configuration config = null;
    public PowerPlayField field;
    private PowerPlayAutonomousStateMachine autonomousStateMachine;
    private SignalConePipeline pipeline;

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
        PowerPlayPersistantStorage.setMatchPhase(MatchPhase.AUTONOMOUS);

        dataLog = new DataLogging("Autonomous", telemetry);
        config = null;
        config = new Configuration();
        if (!config.load()) {
            telemetry.addData("ERROR", "Couldn't load config file");
            telemetry.update();
        }
        timer = new ElapsedTime();
        MecanumCommands commands = new MecanumCommands();

        robot = new PowerPlayRobot(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        // create the robot and run the init for it
        robot.createRobot();

        gamepad = new PowerPlayGamepad(gamepad1, gamepad2, robot);
        field = new PowerPlayField(PowerPlayPersistantStorage.getColorLocation());

        // Here is where you set the state machine you are going to run for the autonomous
        autonomousStateMachine = new PowerPlayAutonomousNoVisionParkLocationTwo(robot, field, telemetry);

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        // set the start location of the robot
        if (PowerPlayPersistantStorage.getRobotPose() != null) {
            startPose = PowerPlayPersistantStorage.getRobotPose();
        } else {
            startPose = field.getStartPose();
        }
        robot.mecanum.setPoseEstimate(startPose);

        timer.reset();

        // put the webcam stuff here

        // Create the pipeline to use to process the images coming from the webcam. It should be a
        // statement that starts like this:
        pipeline = new SignalConePipeline(telemetry);

        // start the webcam processing images through the pipeline.
        robot.webcam.openCamera(OpenCvCameraRotation.UPRIGHT, pipeline);

        // Wait for the start button

        telemetry.addData(">", "Press start to run Auto (make sure you ran the position setter first!)");
        telemetry.update();

        // If you have nothing to do while waiting for the start button to be pressed use:
        // waitForStart();
        // On the other hand, if you do have stuff to do (like display things on the driver station
        // screen while a pipeline is running), use this:
        while (!isStarted()) {
            telemetry.addData(">", "Press start to run Auto (make sure you ran the position setter first!)");
            telemetry.addLine();
            telemetry.addData("Alliance color = ", PowerPlayPersistantStorage.getAllianceColor().toString());
            telemetry.addData("Team Location  = ", PowerPlayPersistantStorage.getTeamLocation().toString());
            telemetry.addLine();
            telemetry.addData("Cone color     = ", pipeline.getConeColor().toString());
            telemetry.addLine();
            pipeline.displayDebugTelemetry();
            telemetry.update();
            idle();
        }

        // Play button has been pressed so autononous has started
        robot.loopTimer.startLoopTimer();
        // Turn off the webcam and pipeline processing to save CPU cycles
        robot.webcam.closeCamera();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************
        robot.coneGrabber.carryPosition();
        autonomousStateMachine.start();
        while (opModeIsActive() && !autonomousStateMachine.isComplete()) {
            autonomousStateMachine.update();
            telemetry.addData("current state is", autonomousStateMachine.getCurrentState());
            telemetry.update();
            robot.update();
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


