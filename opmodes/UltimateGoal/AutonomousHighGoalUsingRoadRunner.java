package org.firstinspires.ftc.teamcode.opmodes.UltimateGoal;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.Autonomous3RingsHighGoalPark1Wobble;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.AutonomousStateMachine;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalField;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalGamepad;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;

import java.util.List;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "High Goal Autonomous", group = "AA")
@Disabled
public class AutonomousHighGoalUsingRoadRunner extends LinearOpMode {

    // Put your variable declarations her
    public UltimateGoalRobotRoadRunner robot;
    public UltimateGoalGamepad gamepad;
    public Configuration config;

    private ElapsedTime timer;

    DataLogging dataLog = null;

    public UltimateGoalField field;
    public AutonomousStateMachine autonomous;

    public double distance = 0;
    public double angleBetween = 0;
    private Pose2d shooterPose = new Pose2d(-10, -6);

    @Override
    public void runOpMode() {
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        dataLog = new DataLogging("Teleop", telemetry);
        config = null;

        timer = new ElapsedTime();
        field = new UltimateGoalField();

        // Put your initializations here
        // create the robot and run the init for it
        robot = new UltimateGoalRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        robot.createRobot();
        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        // the angle changer knows how to do this. My opinion is that you should not be down in these
        // details
        //robot.shooter.setMotorTicks(PersistantStorage.getMotorTicks());
        // This is the level of detail you need to know:
        robot.shooter.restoreAngleInfo();

        // todo Change the constructor call to change out to a different autonomous
        //autonomous = new Autonomous3RingsPowerShotsPark1Wobble(robot, field, telemetry, Autonomous3RingsPowerShotsPark1Wobble.Mode.AUTONOMOUS);
        autonomous = new Autonomous3RingsHighGoalPark1Wobble(robot, field, telemetry);

        timer.reset();
        robot.loopTimer.startLoopTimer();


        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        // Wait for the start button
        waitForStart();

        // Put your calls here - they will not run in a loop
        autonomous.start();

        while (opModeIsActive() && !autonomous.isComplete()) {
            robot.update();
            autonomous.update();
        }

        // save the pose so we can use it to start out in teleop
        PersistantStorage.robotPose = robot.mecanum.getPoseEstimate();

        // save the shooter angle so we can use it later in teleop
        // the angle changer knows how to do this. My opinion is that you should not be down in these
        // details
        //PersistantStorage.setMotorTicks(robot.shooter.getMotorTicks());
        robot.shooter.saveAngleInfoForLater();

        robot.shutdown();
        dataLog.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    public void enableBulkReads(HardwareMap hardwareMap, LynxModule.BulkCachingMode mode) {
        // set bulk read mode for the sensor reads - speeds up the loop
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(mode);
        }
    }
}
