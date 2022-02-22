package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import static org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PoseStorageAutonomousPositionsDemo.DELIVER_TO_MID_AND_LOW_HUB_BLUE;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousStateMachineFreightFrenzy;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyField;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyGamepad;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyStartSpot;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PoseStorageAutonomousPositionsDemo;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PoseStorageFF;

import java.util.List;

/**
 * This Opmode is meant to demo the capability of using the FTC Dashboard to alter a 2d Pose. By
 * altering a pose that represents a destination or waypoint on the field, you can tweak the
 * destination very quickly, without having to change the code and spend the time downloading it
 * to the robot. For this demo, a class called PoseStorageAutonomousPositionsDemo holds the public
 * static pose constants. This class appears in the FTC Dashboard just like the constants for tuning
 * Roadrunner.
 *
 * Note that this code has not been tested so hopefully it works.
 */
@Autonomous(name = "Autonomous Positions Demo", group = "AA")
//@Disabled
@Config
public class AutonomousPositionsDemo extends LinearOpMode {

    // Put your variable declarations her
    private FreightFrenzyRobotRoadRunner robot;
    private FreightFrenzyGamepad gamepad;
    private Configuration config;
    private FreightFrenzyStartSpot startSpot;
    private ElapsedTime timer;
    public boolean autoDone;
    DataLogging dataLog = null;
    int cameraMonitorViewId;
    public FreightFrenzyField field;
    private double distance = 0;
    private double angleBetween = 0;
    private AutonomousStateMachineFreightFrenzy autonomous;
    public static double startX;
    public static double startY;
    public static double startHeadingDegrees;
    public static double destinationX;
    public static double destinationY;
    public static double destinationHeadingDegrees;
    private Pose2d start;
    private Pose2d destination;

    @Override
    public void runOpMode() {
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();
        dataLog = new DataLogging("Autonomous", telemetry);
        config = null;
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        timer = new ElapsedTime();
        field = new FreightFrenzyField();
        robot = new FreightFrenzyRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        robot.createRobot();

        timer.reset();
        robot.loopTimer.startLoopTimer();
        start = new Pose2d(startX, startY, Math.toRadians(startHeadingDegrees));
        destination = new Pose2d(destinationX, destinationY, Math.toRadians(destinationHeadingDegrees));
        // Build the trajectories using the constants defined in PoseStorageAutonomousPositionsDemo. Since those constants
        // are accessible from the FTC Dashboard, you can change them on the fly, without a download of new code. This makes
        // tweaking the positions very fast. Once you have them dialed in, you can alter the constants in the
        // Pose storage class and make the positions permanent.

        Trajectory trajectoryTodestination = robot.mecanum.trajectoryBuilder(start)
                .lineToLinearHeading(destination)
                .build();



        robot.mecanum.setPoseEstimate(start);


        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        // Wait for the start button
        waitForStart();

        // run the trajectories
        robot.mecanum.followTrajectory(trajectoryTodestination);


        robot.shutdown();
        dataLog.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}
