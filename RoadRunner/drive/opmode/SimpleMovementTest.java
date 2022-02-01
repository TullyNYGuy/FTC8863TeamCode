package org.firstinspires.ftc.teamcode.RoadRunner.drive.opmode;

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
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyField;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyGamepad;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.MecanumDriveFreightFrenzy;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.Autonomous3RingsHighGoalPark1Wobble;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PoseStorage;
import org.firstinspires.ftc.teamcode.RoadRunner.drive.SampleMecanumDrive;

import java.util.List;

/*
 * Op mode for preliminary tuning of the follower PID coefficients (located in the drive base
 * classes). The robot drives back and forth in a straight line indefinitely. Utilization of the
 * dashboard is recommended for this tuning routine. To access the dashboard, connect your computer
 * to the RC's WiFi network. In your browser, navigate to https://192.168.49.1:8080/dash if you're
 * using the RC phone or https://192.168.43.1:8080/dash if you are using the Control Hub. Once
 * you've successfully connected, start the program, and your robot will begin moving forward and
 * backward. You should observe the target position (green) and your pose estimate (blue) and adjust
 * your follower PID coefficients such that you follow the target position as accurately as possible.
 * If you are using SampleMecanumDrive, you should be tuning TRANSLATIONAL_PID and HEADING_PID.
 * If you are using SampleTankDrive, you should be tuning AXIAL_PID, CROSS_TRACK_PID, and HEADING_PID.
 * These coefficients can be tuned live in dashboard.
 *
 * This opmode is designed as a convenient, coarse tuning for the follower PID coefficients. It
 * is recommended that you use the FollowerPIDTuner opmode for further fine tuning.
 */
@Config
@Autonomous(group = "remote auto")
public class SimpleMovementTest extends LinearOpMode {

    public enum States {
        START,
        MOVING_TO_SHIPPING_HUB,
        AT_HUB,
        AT_HUB_LINEDUP,
        MOVING_TO_CAROUSEL,
        AT_CAROUSEL,
        MOVIN_TO_WAREHOUSE_WP1,
        AT_WP1,
        MOVING_TO_WAREHOUSE_WP2,
        AT_WP2,
        END,
        IDLE,

    }

    Trajectory trajectoryTest;


    Trajectory trajectoryTestNegStart;

    Trajectory trajectoryTest270Heading;

    Trajectory TrajectoryToShippingHub;
    Trajectory TrajectoryToCarousel;
    Trajectory TrajectoryToWarehouseWaypoint1;
    Trajectory TrajectoryToWarehouseWaypoint2;

    public boolean isComplete;

    public void startAuto() {
        currentState = States.START;
        isComplete = false;
    }

    States currentState = States.START;

    public void buildTrajectories() {
        this.trajectoryTest = robot.mecanum.trajectoryBuilder(new Pose2d(0, 0, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(10, 0, Math.toRadians(0)))
                .build();


        trajectoryTestNegStart = robot.mecanum.trajectoryBuilder(new Pose2d(-10, -10, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(10, -10, Math.toRadians(0)))
                .build();

        trajectoryTest270Heading = robot.mecanum.trajectoryBuilder(new Pose2d(0, -0, Math.toRadians(270)))
                .lineToLinearHeading(new Pose2d(10, 0, Math.toRadians(270)))
                .build();

        TrajectoryToShippingHub = robot.mecanum.trajectoryBuilder(org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PoseStorageFF.START_POSE)
                .lineToLinearHeading(new Pose2d(-12, 44, Math.toRadians(270)))
                .build();
        TrajectoryToCarousel = robot.mecanum.trajectoryBuilder(TrajectoryToShippingHub.end())
                .lineToLinearHeading(new Pose2d(-59.75, 57.75, Math.toRadians(90)))
                .build();
        TrajectoryToWarehouseWaypoint1 = robot.mecanum.trajectoryBuilder(TrajectoryToCarousel.end())
                .lineToLinearHeading(new Pose2d(-20, 60, Math.toRadians(0)))
                .build();
        TrajectoryToWarehouseWaypoint2 = robot.mecanum.trajectoryBuilder(TrajectoryToWarehouseWaypoint1.end())
                .splineToLinearHeading(new Pose2d(8.75, 65, Math.toRadians(0)), Math.toRadians(0))
                .splineToLinearHeading(new Pose2d(35, 65, Math.toRadians(0)), Math.toRadians(0))
                .build();
    }

    public void update() {
        switch (currentState) {
            case START:
                robot.mecanum.setPoseEstimate(org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PoseStorageFF.START_POSE);
                currentState = States.MOVING_TO_SHIPPING_HUB;
                break;
            case MOVING_TO_SHIPPING_HUB:
                if (!robot.mecanum.isBusy()) {
                    robot.mecanum.followTrajectory(TrajectoryToShippingHub);
                    currentState = States.AT_HUB;
                }
                break;
            case AT_HUB:
                if (!robot.mecanum.isBusy()) {
                    robot.intake.lineUpToEjectIntoLevel1();
                    currentState = States.AT_HUB_LINEDUP;
                }
                break;
            case AT_HUB_LINEDUP:
                if (robot.intake.isComplete()) {
                    robot.intake.ejectIntoLevel1();
                    currentState = States.MOVING_TO_CAROUSEL;
                }
                break;


            case MOVING_TO_CAROUSEL:
                if (robot.intake.isComplete()) {
                    robot.mecanum.followTrajectory(TrajectoryToCarousel);
                    currentState = States.AT_CAROUSEL;
                }
                break;
            case AT_CAROUSEL:
                if (!robot.mecanum.isBusy()) {
                    timer.reset();
                    robot.duckSpinner.turnOn();
                    currentState = States.MOVIN_TO_WAREHOUSE_WP1;
                }
                break;
            case MOVIN_TO_WAREHOUSE_WP1:
                if (timer.milliseconds() > 3500) {
                    robot.duckSpinner.turnOff();
                    robot.mecanum.followTrajectory(TrajectoryToWarehouseWaypoint1);
                    currentState = States.AT_WP1;
                }
                break;
            case AT_WP1:
                if (!robot.mecanum.isBusy()) {
                    currentState = States.MOVING_TO_WAREHOUSE_WP2;
                }
                break;
            case MOVING_TO_WAREHOUSE_WP2:
                if (!robot.mecanum.isBusy()) {
                    robot.mecanum.followTrajectory(TrajectoryToWarehouseWaypoint2);
                    currentState = States.AT_WP2;
                }
                break;
            case AT_WP2:
                if (!robot.mecanum.isBusy()) {
                    isComplete = true;
                    currentState = States.END;
                }
                break;
            case END:
                break;
        }

    }

    public FreightFrenzyRobotRoadRunner robot;
    public FreightFrenzyGamepad gamepad;
    public Configuration config;
    private ElapsedTime timer;

    DataLogging dataLog = null;

    public FreightFrenzyField field;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        dataLog = new DataLogging("Teleop", telemetry);
        config = null;

        timer = new ElapsedTime();
        field = new FreightFrenzyField();

        // Put your initializations here
        // create the robot and run the init for it
        robot = new FreightFrenzyRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        robot.createRobot();
        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);
        buildTrajectories();
        // robot = new FreightFrenzyRobotRoadRunner(hardwareMap,telemetry,config, dataLog, DistanceUnit.INCH, this);

//        MecanumDriveFreightFrenzy drive = new MecanumDriveFreightFrenzy(
//                FreightFrenzyRobotRoadRunner.HardwareName.CONFIG_FL_MOTOR.hwName,
//                FreightFrenzyRobotRoadRunner.HardwareName.CONFIG_BL_MOTOR.hwName,
//                FreightFrenzyRobotRoadRunner.HardwareName.CONFIG_FR_MOTOR.hwName,
//                FreightFrenzyRobotRoadRunner.HardwareName.CONFIG_BR_MOTOR.hwName,
//                hardwareMap);


        //Trajectory TrajectoryToWarehouseFinalDestination = drive.trajectoryBuilder(TrajectoryToWarehouseWaypoint2.end())
        // .lineToLinearHeading(new Pose2d(60, 65, Math.toRadians(0)))
        //.build();
        timer.reset();
        robot.loopTimer.startLoopTimer();


        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        startAuto();
        //tPoseEstimate(org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PoseStorageFF.START_POSE);
//        drive.followTrajectory(trajectoryTestNegStart);

        ///robot.mecanum.followTrajectory(TrajectoryToShippingHub);
//      Trajectory TrajectoryToCarousel = drive.trajectoryBuilder(drive.getPoseEstimate())
//                .lineToLinearHeading(new Pose2d(-59.75, 56.75, Math.toRadians(90)))
//               .build();
        ///robot.mecanum.followTrajectory(TrajectoryToCarousel);
//        Trajectory TrajectoryToWarehouseWaypoint1 = drive.trajectoryBuilder(drive.getPoseEstimate())
//                .lineToLinearHeading(new Pose2d(-20, 60, Math.toRadians(0)))
//                .build();
        // robot.mecanum.followTrajectory(TrajectoryToWarehouseWaypoint1);
//        Trajectory TrajectoryToWarehouseWaypoint2 = drive.trajectoryBuilder(drive.getPoseEstimate())
//                .splineToLinearHeading(new Pose2d(8.75, 65, Math.toRadians(0)),Math.toRadians(0))
//                .splineToLinearHeading(new Pose2d(35, 65, Math.toRadians(0)),Math.toRadians(0))
//                .build();
        // robot.mecanum.followTrajectory(TrajectoryToWarehouseWaypoint2);

        //drive.followTrajectory(TrajectoryToWarehouseFinalDestination);

        while (opModeIsActive() && isComplete == false) {
            robot.update();
            update();
            // TANYA - need the idle so we don't hog all the CPU
            idle();
        }
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