package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.Shooter;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalField;

import static org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner.HardwareName.LEFT_SHOOTER_MOTOR;
import static org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner.HardwareName.RIGHT_SHOOTER_MOTOR;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "AngleTesting Localized", group = "Test")
//@Disabled
public class AngleChangerPlusShooterTestWithLocalization extends LinearOpMode {

    // Put your variable declarations her
    public Shooter shooter;
    public UltimateGoalField field;
    public ElapsedTime timer;
    public Pose2d robotPose;
    public double distance = 0;
    public double angleBetween = 0;

    @Override
    public void runOpMode() {


        // Put your initializations here
        shooter = new Shooter(LEFT_SHOOTER_MOTOR.hwName, RIGHT_SHOOTER_MOTOR.hwName, hardwareMap, telemetry);
        field = new UltimateGoalField();
        timer = new ElapsedTime();
        robotPose = new Pose2d(-62, -18.9, 0);

        distance = field.distanceTo(DistanceUnit.METER, robotPose, field.topGoal.getPose2d());
        angleBetween = field.angleTo(AngleUnit.DEGREES, robotPose, field.topGoal.getPose2d());

        telemetry.addData("distance =", distance);
        telemetry.addData("angleto =", Math.toDegrees(angleBetween));
        telemetry.addData("Angle", shooter.calculateAngle(distance, DistanceUnit.METER, field.topGoal));
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        // Wait for the start button
        waitForStart();

        // Put your calls here - they will not run in a loop
        shooter.requestFire(distance, DistanceUnit.METER, field.topGoal);
        while (opModeIsActive() && !shooter.isAngleAdjustmentComplete()) {

            // Put your calls that need to run in a loop here
            shooter.update();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        shooter.setSpeed(5000);
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 10000) {
            shooter.update();
            idle();
        }
        shooter.shutdown();
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
