package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import static org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PoseStorageAutonomousPositionsDemo.DELIVER_TO_MID_AND_LOW_HUB_BLUE;

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
public class AutonomousPositionsDemo extends LinearOpMode {

    // Put your variable declarations her
    public FreightFrenzyRobotRoadRunner robot;
    public FreightFrenzyGamepad gamepad;
    public Configuration config;
    public FreightFrenzyStartSpot startSpot;
    private ElapsedTime timer;
    public boolean autoDone;
    DataLogging dataLog = null;
    int cameraMonitorViewId;
    public FreightFrenzyField field;
    public double distance = 0;
    public double angleBetween = 0;
    private AutonomousStateMachineFreightFrenzy autonomous;

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

        // Build the trajectories using the constants defined in PoseStorageAutonomousPositionsDemo. Since those constants
        // are accessible from the FTC Dashboard, you can change them on the fly, without a download of new code. This makes
        // tweaking the positions very fast. Once you have them dialed in, you can alter the constants in the
        // Pose storage class and make the positions permanent.
        Trajectory trajectoryToHub = robot.mecanum.trajectoryBuilder(PoseStorageFF.START_POSE)
                .lineToLinearHeading(PoseStorageAutonomousPositionsDemo.DELIVER_TO_MID_AND_LOW_HUB_BLUE)
                .build();

        Trajectory trajectoryToDuckSpinner = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
                .lineToLinearHeading(PoseStorageAutonomousPositionsDemo.DUCK_SPINNER_BLUE)
                .build();

        robot.mecanum.setPoseEstimate(PoseStorageFF.START_POSE);


        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        // Wait for the start button
        waitForStart();

        // run the trajectories
        robot.mecanum.followTrajectory(trajectoryToHub);
        robot.mecanum.followTrajectory(trajectoryToDuckSpinner);

        robot.shutdown();
        dataLog.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}
