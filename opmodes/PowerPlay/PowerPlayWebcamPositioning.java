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
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.Pipelines.PowerPlayWebcamPositioningPipeline;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayAutonomousNoVisionParkLocationTwo;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayAutonomousStateMachine;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayField;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayGamepad;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.List;

@Autonomous(name = "Power Play Webcam Positioning", group = "AA")
//@Disabled

public class PowerPlayWebcamPositioning extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public PowerPlayRobot robot;
    public PowerPlayGamepad gamepad;
    public Configuration config = null;
    public PowerPlayField field;
    private PowerPlayAutonomousStateMachine autonomousStateMachine;
    private OpenCvPipeline pipeline;

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

        dataLog = new DataLogging("WebcamPositioning", telemetry);
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

        // Not needed for web cam positioning, but leaving it in just in case
        //gamepad = new PowerPlayGamepad(gamepad1, gamepad2, robot);
        //field = new PowerPlayField(PowerPlayPersistantStorage.getColorLocation());

        // Here is where you set the state machine you are going to run for the autonomous
        //autonomousStateMachine = new PowerPlayAutonomousNoVisionParkLocationTwo(robot, field, telemetry);

        //enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        // set the start location of the robot
//        if (PowerPlayPersistantStorage.getRobotPose() != null) {
//            startPose = PowerPlayPersistantStorage.getRobotPose();
//        } else {
//            startPose = field.getStartPose();
//        }
//        robot.mecanum.setPoseEstimate(startPose);

        timer.reset();

        // put the webcam stuff here

        // Create the pipeline to use to process the images coming from the webcam. It should be a
        // statement that starts like this:
        pipeline = new PowerPlayWebcamPositioningPipeline();

        // start the webcam processing images through the pipeline.
        robot.webcam.openCamera(OpenCvCameraRotation.UPRIGHT, pipeline);

        // Wait for the start button

        telemetry.addData(">", "Use this to positon the robot's camera", "Instead of pressing start, go to the 3 dots and press camera stream option");
        telemetry.addData(">", "When you are done, just hit stop");
        telemetry.addData(">","Don't press start unless you want a whole bunch of numbers and confusion!");
        telemetry.update();

        // If you have nothing to do while waiting for the start button to be pressed use:
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************
        while (opModeIsActive() && !gamepad1.a) {
            /*
             * Send some stats to the telemetry
             */
            telemetry.addData("Frame Count", robot.webcam.webcam.getFrameCount());
            telemetry.addData("FPS", String.format("%.2f", robot.webcam.webcam.getFps()));
            telemetry.addData("Total frame time ms", robot.webcam.webcam.getTotalFrameTimeMs());
            telemetry.addData("Pipeline time ms", robot.webcam.webcam.getPipelineTimeMs());
            telemetry.addData("Overhead time ms", robot.webcam.webcam.getOverheadTimeMs());
            telemetry.addData("Theoretical max FPS", robot.webcam.webcam.getCurrentPipelineMaxFps());
            telemetry.addLine("");
            telemetry.addData(">", "When you are done, press a on game pad 1");
            telemetry.update();
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


