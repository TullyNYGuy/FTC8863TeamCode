package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzy;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
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
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.TestAuto;
import org.firstinspires.ftc.teamcode.RoadRunner.drive.SampleMecanumDrive;

import java.util.List;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Autonomous(name = "auto work plz", group = "AA")
@Disabled
public class GetAutoWorking extends LinearOpMode {

    // Put your variable declarations her
    public FreightFrenzyRobotRoadRunner robot;
    public FreightFrenzyGamepad gamepad;
    public Configuration config;

    private ElapsedTime timer;

    DataLogging dataLog = null;

    public FreightFrenzyField field;
    public TestAuto autonomous;

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
        field = new FreightFrenzyField();

        // Put your initializations here
        // create the robot and run the init for it
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        //enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        // the angle changer knows how to do this. My opinion is that you should not be down in these
        // details
        //robot.shooter.setMotorTicks(PersistantStorage.getMotorTicks());
        // This is the level of detail you need to know:


        // todo Change the constructor call to change out to a different autonomous
        //autonomous = new Autonomous3RingsPowerShotsPark1Wobble(robot, field, telemetry, Autonomous3RingsPowerShotsPark1Wobble.Mode.AUTONOMOUS);
        //autonomous = new TestAuto(robot,field, telemetry);

        timer.reset();
        //robot.loopTimer.startLoopTimer();
       Trajectory trajectoryTest = drive.trajectoryBuilder(new Pose2d(-17.5,63.75,Math.toRadians(270)))
                .lineToLinearHeading(new Pose2d(-12, 49, Math.toRadians(270)))
                .build();

        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        // Wait for the start button
        waitForStart();

        // Put your calls here - they will not run in a loop
       // autonomous.start();
        drive.followTrajectory(trajectoryTest);
        while (opModeIsActive()) {
           // telemetry.addData("current auto state", autonomous.getState());

            telemetry.update();
            //robot.update();

            //autonomous.update();
        }

        // save the pose so we can use it to start out in teleop


        // save the shooter angle so we can use it later in teleop
        // the angle changer knows how to do this. My opinion is that you should not be down in these
        // details
        //PersistantStorage.setMotorTicks(robot.shooter.getMotorTicks());


        //robot.shutdown();
        //dataLog.closeDataLog();
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
