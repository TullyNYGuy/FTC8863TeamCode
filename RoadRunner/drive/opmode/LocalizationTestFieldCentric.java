package org.firstinspires.ftc.teamcode.RoadRunner.drive.opmode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.RoadRunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.RoadRunner.util.JoyStick;
import org.firstinspires.ftc.teamcode.RoadRunner.util.UltimateGoalField;

/**
 * This is a simple teleop routine for testing localization. Drive the robot around like a normal
 * teleop routine and make sure the robot's estimated pose matches the robot's actual pose (slight
 * errors are not out of the ordinary, especially with sudden drive motions). The goal of this
 * exercise is to ascertain whether the localizer has been configured properly (note: the pure
 * encoder localizer heading may be significantly off if the track width has not been tuned).
 */
@TeleOp(group = "drive")
public class LocalizationTestFieldCentric extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        JoyStick leftStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE);
        JoyStick leftStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE);
        JoyStick rightStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE);

        Pose2d shooterPose = new Pose2d(0 , 0,0);

        UltimateGoalField ultimateGoalField = new UltimateGoalField();
        waitForStart();

        while (!isStopRequested()) {

            // Read pose
            Pose2d poseEstimate = drive.getPoseEstimate();

            // Create a vector from the gamepad x/y inputs
            // Then, rotate that vector by the inverse of that heading
            Vector2d input = new Vector2d(
                    -leftStickY.scaleInput(gamepad1.left_stick_y),
                    -leftStickX.scaleInput(gamepad1.left_stick_x)
            ).rotated(-poseEstimate.getHeading());

            // Pass in the rotated input + right stick value for rotation
            // Rotation is not part of the rotated input thus must be passed in separately
            drive.setWeightedDrivePower(
                    new Pose2d(
                            input.getX(),
                            input.getY(),
                            -gamepad1.right_stick_x
                    )
            );

            drive.update();

            telemetry.addData("x", poseEstimate.getX());
            telemetry.addData("y", poseEstimate.getY());
            telemetry.addData("heading", Math.toDegrees(poseEstimate.getHeading()));
            telemetry.addData("distance to goal", ultimateGoalField.distanceToGoal(poseEstimate.plus(shooterPose)));
            telemetry.addData("Angle to goal", Math.toDegrees(ultimateGoalField.angleToGoal(poseEstimate.plus(shooterPose))));
            telemetry.update();
        }
    }

    /**
     * Rotate a Pose2d from one coordinate frame to another.
     * Used to translate the x,y left joystick commands from field centric to x,y commands that are
     * relative to the robot's frame of reference (coordinate frame)
     * xrobot = +xfield * cos(theta) - yfield * sin(theta)
     * yrobot = +xfield * sin(theta) + yfield * sin(theta)
     * Heading is on the right stick and does not need an adjustment since
     * reference https://doubleroot.in/lessons/coordinate-geometry-basics/rotation-of-axes/ except
     * that in this page, the x is positive to the right and in our system x is positive to the left.
     * This means that some signs are different in the derived equations that we use.
     *
     * @param originalPose - the x, y, and heading to be rotated (the joystick commands)
     * @param angle - the angle between the two coordinate frames (which is the heading of the robot)
     * @return - Pose2d (x, y, heading) which is now relative to the robot's coordinate frame
     */
    public Pose2d rotatePose2dToCoordinateFrame(Pose2d originalPose, double angle) {
        return new Pose2d(
                originalPose.getX() * Math.cos(angle) - originalPose.getY() * Math.sin(angle),
                originalPose.getX() * Math.sin(angle) + originalPose.getY() * Math.cos(angle),
                originalPose.getHeading()
        );
    }
}
