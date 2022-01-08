package org.firstinspires.ftc.teamcode.RoadRunner.drive.opmode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.MecanumDriveUltimateGoal;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;

/*
 * This is a simple routine to test turning capabilities.
 */
@Config
@Autonomous(group = "drive")
public class TurnTestUltimateGoal extends LinearOpMode {
    //public static double ANGLE = 90; // deg
    public static double ANGLE = 180; // deg

    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDriveUltimateGoal drive = new MecanumDriveUltimateGoal(
                UltimateGoalRobotRoadRunner.HardwareName.CONFIG_FL_MOTOR.hwName,
                UltimateGoalRobotRoadRunner.HardwareName.CONFIG_BL_MOTOR.hwName,
                UltimateGoalRobotRoadRunner.HardwareName.CONFIG_FR_MOTOR.hwName,
                UltimateGoalRobotRoadRunner.HardwareName.CONFIG_BR_MOTOR.hwName,
                hardwareMap);

        waitForStart();

        if (isStopRequested()) return;

        drive.turn(Math.toRadians(ANGLE));
    }
}
