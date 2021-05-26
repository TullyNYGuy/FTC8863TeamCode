package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.Shooter;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Offset Test", group = "Test")
//@Disabled
public class OffsetTest extends LinearOpMode {

    // Put your variable declarations her
public Shooter shooter;
public Pose2d robotPos;
public Pose2d shooterPos;
    @Override
    public void runOpMode() {


        // Put your initializations here
         shooter = new Shooter(UltimateGoalRobotRoadRunner.HardwareName.LEFT_SHOOTER_MOTOR.hwName, UltimateGoalRobotRoadRunner.HardwareName.RIGHT_SHOOTER_MOTOR.hwName, hardwareMap, telemetry);
robotPos = new Pose2d(50, 10, Math.toRadians(45));
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        shooterPos = shooter.getShooterPose(robotPos);
        telemetry.addData("x = ", shooterPos.getX());
        telemetry.addData("y = ", shooterPos.getY());
        telemetry.addData("angle = ", Math.toDegrees(shooterPos.getHeading()));
        telemetry.update();
        sleep(5000);
    }
}
