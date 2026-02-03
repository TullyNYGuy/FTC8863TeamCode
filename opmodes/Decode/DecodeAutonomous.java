package org.firstinspires.ftc.teamcode.opmodes.Decode;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeGamepad;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeRobot;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeShotDistance;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeSorterController;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeStoreBetweenMatches;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;

import java.util.List;

@Autonomous(name = "Decode Autonomous", group = "AA")
//@Disabled

public class DecodeAutonomous extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public DecodeRobot robot;
    public DecodeGamepad gamepad;

    public Configuration config = null;
    //public DecodeField field;

    private ElapsedTime timer;

    DataLogging dataLog = null;

    private Pose2D startPose;

    @Override
    public void runOpMode() {
        MatchPhase.setMatchPhase(MatchPhase.AUTONOMOUS);
        if (startPose == null) {
            startPose = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
        }
        else {
            startPose = DecodeStoreBetweenMatches.startingPose;
        }

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
        robot = new DecodeRobot(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        robot.createRobot();
        robot.setAllianceColor(AllianceColorTeamLocation.getAllianceColor());
        robot.sorterController.setCurrentState(DecodeSorterController.SorterState.ARTIFACT_ARTIFACT_ARTIFACT);
        robot.mecanumDrive.pinpoint.setPosition(startPose);
        robot.turntableTrackingController.setPipelineNumber(DecodeShotDistance.ShotType.LONG);

        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        // create the gamepad
        gamepad = new DecodeGamepad(gamepad1, gamepad2, robot);

        // create the power play field. This sets the locations for our particular alliance color
        // and team location (left or right)
        //field = new DecodeField(DecodePersistantStorage.getColorLocation());

        // Here is where you create the state machine that is going to be run.

        // Allow reads of all of the motor data in one read.
        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        // set the persistant storage variable saying this is the teleop phase
        MatchPhase.setMatchPhase(MatchPhase.AUTONOMOUS);

        // set the start location of the robot

//        if (DecodePersistantStorage.getRobotPose() != null) {
//            startPose = DecodePersistantStorage.getRobotPose();
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
          //  telemetry.addData("Alliance color = ", AllianceColorTeamLocation.getAllianceColor().toString());
          //  telemetry.addData("Team Location  = ", AllianceColorTeamLocation.getTeamLocation().toString());
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
        robot.mecanumDrive.rightBack.setPower(1.0);
        robot.mecanumDrive.rightFront.setPower(1.0);
        robot.mecanumDrive.leftBack.setPower(1.0);
        robot.mecanumDrive.leftFront.setPower(1.0);

        boolean autoDone = false;
        // Start the state machine
        while (opModeIsActive()) {
         if (timer.milliseconds() >500 && autoDone == false) {
             robot.mecanumDrive.rightBack.setPower(0);
             robot.mecanumDrive.rightFront.setPower(0);
             robot.mecanumDrive.leftBack.setPower(0);
             robot.mecanumDrive.leftFront.setPower(0);
             autoDone = true;
         }
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
        DecodeStoreBetweenMatches.sorterState = robot.sorterController.getCurrentState();
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


