package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest;

import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayField.getVector2d;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.Pipelines.SignalConePipeline;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayAutonomousStateMachine;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayAutonomousVisionOneCyclePark;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayField;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayGamepad;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoseStorage;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.List;

@Config
@TeleOp(name = "Test Small Movement", group = "Test")
//@Disabled

public class TestSmallTrajectory extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public PowerPlayRobot robot;
    public PowerPlayGamepad gamepad;
    public Configuration config = null;
    public PowerPlayField field;
    private PowerPlayAutonomousStateMachine autonomousStateMachine;
    private SignalConePipeline pipeline;
    public SignalConePipeline.ConeColor coneColor;

    private ElapsedTime timer;

    DataLogging dataLog = null;

    private Pose2d startPose = new Pose2d(0,0, Math.toRadians(0));

    public static double X_MOVEMENT = 1.0;
    public static double Y_MOVEMENT = 1.0;

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
        robot = new PowerPlayRobot(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        robot.createRobot();

        // create the gamepad
        gamepad = new PowerPlayGamepad(gamepad1, gamepad2, robot);

        // create the power play field. This sets the locations for our particular alliance color
        // and team location (left or right)
        field = new PowerPlayField(PowerPlayPersistantStorage.getColorLocation());


        // Allow reads of all of the motor data in one read.
        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        robot.mecanum.setPoseEstimate(startPose);

        Trajectory smallmove = robot.mecanum.trajectoryBuilder(startPose)
                .lineTo(new Vector2d(X_MOVEMENT, Y_MOVEMENT))
                .build();

        // Wait for the start button
        telemetry.addData("Init complete", "!");
        telemetry.update();

        waitForStart();


        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        // Get the cone grabber inside the robot so it does not get hit by anything
        robot.coneGrabber.carryPosition();
        robot.mecanum.followTrajectoryHighAccuracy(smallmove);

        while (opModeIsActive() && !robot.mecanum.isBusy()) {
            telemetry.update();
            robot.update();
            telemetry.addData(">", "Done");
            telemetry.addData("final position = ", " ");
            telemetry.addData("    x = ",robot.mecanum.getPoseEstimate().getX());
            telemetry.addData("    y = ",robot.mecanum.getPoseEstimate().getY());
            telemetry.addData("    heading = ",Math.toDegrees(robot.mecanum.getPoseEstimate().getHeading()));
            telemetry.update();
            idle();
        }
        while(opModeIsActive()) {
            idle();
        }
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


