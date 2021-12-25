package org.firstinspires.ftc.teamcode.RoadRunner.drive.opmode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
 * This is a simple routine to test turning capabilities.
 */
@Config
@TeleOp(group = "drive")
public class FindGoalTest extends LinearOpMode {
    //public static double ANGLE = 90; // deg
    public static double ANGLE = 180; // deg

    public Vector2d robot = new Vector2d(0, 0);
    public Vector2d goal = new Vector2d(72.25, -12.39);

    public double distance = 0;
    public double angleBetween = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        distance = goal.distTo(robot);
        angleBetween = angleTo(goal, robot);
        telemetry.addData("distance =", distance);
        telemetry.addData("angleto =", Math.toDegrees(angleBetween));
        telemetry.update();
        waitForStart();
        while (opModeIsActive()) {

            distance = goal.distTo(robot);
            angleBetween = angleTo(goal, robot);
            telemetry.addData("distance =", distance);
            telemetry.addData("angleto =", Math.toDegrees(angleBetween));
            telemetry.update();
        }
        if (isStopRequested()) return;


    }

    public double angleTo(Vector2d vector1, Vector2d vector2) {
        Vector2d vectorDif = vector1.minus(vector2);
        return Math.atan(vectorDif.getY() / vectorDif.getX());
    }
}
