package org.firstinspires.ftc.teamcode.opmodes.UltimateGoal;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.MecanumDriveUltimateGoal;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.Shooter;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalField;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalGamepad;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;
import org.firstinspires.ftc.teamcode.RoadRunner.trajectorysequence.TrajectorySequence;

import java.util.List;

import static org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner.HardwareName.LEFT_SHOOTER_MOTOR;
import static org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner.HardwareName.RIGHT_SHOOTER_MOTOR;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "Ultimate Goal Autonomous", group = "Test")
//@Disabled
public class AutonomousMode extends LinearOpMode {
    public UltimateGoalRobotRoadRunner robot;
    public UltimateGoalGamepad gamepad;
    public Configuration config;

    private ElapsedTime timer;

    DataLogging dataLog = null;
    // Put your variable declarations her
    public UltimateGoalField field;
    public Pose2d robotPose;
    public double distance = 0;
    public double angleBetween = 0;
private Pose2d shooterPose = new Pose2d(-10,-6);
    //private Pose2d shooterPose = new Pose2d(10,-6);
   // private Pose2d shooterPose = new Pose2d(0,0);


    @Override
    public void runOpMode() {
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        dataLog = new DataLogging("Teleop", telemetry);
        config = null;
//        config = new Configuration();
//        if (!config.load()) {
//            telemetry.addData("ERROR", "Couldn't load config file");
//            telemetry.update();
//        }
        timer = new ElapsedTime();
        //MecanumCommands commands = new MecanumCommands();

        robot = new UltimateGoalRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);

        // create the robot and run the init for it
        robot.createRobot();

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        // create the gamepad


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


        robot.loopTimer.startLoopTimer();


        // Put your initializations here
        //MecanumDriveUltimateGoal drive = new MecanumDriveUltimateGoal(UltimateGoalRobotRoadRunner.HardwareName.CONFIG_FL_MOTOR.hwName, UltimateGoalRobotRoadRunner.HardwareName.CONFIG_BL_MOTOR.hwName, UltimateGoalRobotRoadRunner.HardwareName.CONFIG_FR_MOTOR.hwName, UltimateGoalRobotRoadRunner.HardwareName.CONFIG_BR_MOTOR.hwName, hardwareMap);
        field = new UltimateGoalField();
        timer = new ElapsedTime();
        robotPose = new Pose2d(-62, -18.9, Math.toRadians(180));
        //robotPose = new Pose2d(0, -18.9, 0);
        robot.mecanum.setPoseEstimate(robotPose);
        distance = field.distanceTo(DistanceUnit.METER, robotPose, field.topGoal.getPose2d());
       // double distance_10 = field.distanceTo(DistanceUnit.METER, robotPose.plus(shooterPose), field.topGoal.getPose2d());
        double distance_no10 = field.distanceTo(DistanceUnit.METER, robotPose.minus(shooterPose), field.topGoal.getPose2d());
        angleBetween = field.angleTo(AngleUnit.DEGREES, robotPose, field.topGoal.getPose2d());
       // distance = field.distanceTo(DistanceUnit.METER, robot.mecanum.getPoseEstimate().minus(shooterPose), field.topGoal.getPose2d());
    /*    telemetry.addData("distance =", distance);
        telemetry.addData("angle to =", Math.toDegrees(angleBetween));
        telemetry.addData("Angle", robot.shooter.calculateAngle(distance, DistanceUnit.METER, field.topGoal));

     */
        telemetry.addData("angle option+0",robot.shooter.calculateAngle(distance, DistanceUnit.METER, field.topGoal));
      // telemetry.addData("angle option+10",robot.shooter.calculateAngle(distance_10, DistanceUnit.METER, field.topGoal));
        telemetry.addData("angle option-10",robot.shooter.calculateAngle(distance_no10, DistanceUnit.METER, field.topGoal));
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        // Wait for the start button
        waitForStart();

        // Put your calls here - they will not run in a loop

        TrajectorySequence trajSeq = robot.mecanum.trajectorySequenceBuilder(robotPose)
                .strafeLeft(13)
                .back(45)
                .lineTo(new Vector2d(0,-18.9))


                .build();
        robot.mecanum.followTrajectorySequenceAsync(trajSeq);
        while(opModeIsActive() && robot.mecanum.isBusy()){
            robot.update();
        }


        distance = field.distanceTo(DistanceUnit.METER, robot.mecanum.getPoseEstimate().minus(shooterPose), field.topGoal.getPose2d());
        angleBetween = field.angleTo(AngleUnit.DEGREES, robot.mecanum.getPoseEstimate().minus(shooterPose), field.topGoal.getPose2d());
        robot.shooter.requestFire(distance, DistanceUnit.METER, field.topGoal);
        while (opModeIsActive() && !robot.shooter.isAngleAdjustmentComplete()) {

            // Put your calls that need to run in a loop here
            robot.shooter.update();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        robot.shooterOn();
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            robot.update();
            idle();
        }
        timer.reset();
        robot.fire1();
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            robot.update();
            idle();
        }
        timer.reset();
        robot.fire1();
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            robot.update();
            idle();
        }
        timer.reset();
        robot.fire1();
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            robot.update();
            idle();
        }
robot.shooterOff();
       // Put your cleanup code here - it runs as the application shuts down

        trajSeq = robot.mecanum.trajectorySequenceBuilder(robot.mecanum.getPoseEstimate())

                .lineTo(new Vector2d(15,-18.9))


                .build();
        robot.mecanum.followTrajectorySequenceAsync(trajSeq);
        while(opModeIsActive() && robot.mecanum.isBusy()){
            robot.update();
        }
        robot.dropWobbleGoal();
        while(opModeIsActive() && !robot.isWobbleGoalDropComplete()){
            robot.update();
        }
        PersistantStorage.robotPose = robot.mecanum.getPoseEstimate();
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
