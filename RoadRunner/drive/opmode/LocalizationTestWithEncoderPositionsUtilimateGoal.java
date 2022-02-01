package org.firstinspires.ftc.teamcode.RoadRunner.drive.opmode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.MecanumDriveUltimateGoal;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;

/**
 * This is a simple teleop routine for testing localization. Drive the robot around like a normal
 * teleop routine and make sure the robot's estimated pose matches the robot's actual pose (slight
 * errors are not out of the ordinary, especially with sudden drive motions). The goal of this
 * exercise is to ascertain whether the localizer has been configured properly (note: the pure
 * encoder localizer heading may be significantly off if the track width has not been tuned).
 */
@TeleOp(group = "drive")
public class LocalizationTestWithEncoderPositionsUtilimateGoal extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDriveUltimateGoal drive = new MecanumDriveUltimateGoal(UltimateGoalRobotRoadRunner.HardwareName.CONFIG_FL_MOTOR.hwName, UltimateGoalRobotRoadRunner.HardwareName.CONFIG_BL_MOTOR.hwName, UltimateGoalRobotRoadRunner.HardwareName.CONFIG_FR_MOTOR.hwName, UltimateGoalRobotRoadRunner.HardwareName.CONFIG_BR_MOTOR.hwName, hardwareMap);;

        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Running https://www.learnroadrunner.com/dead-wheels.html#tuning-three-wheel
        // When tuning the 3 dead wheels we noticed that even though we were trying to keep the bot
        // moving in only 1 axes, we where showing movement in the other axes. So when x should be
        // the only dimension changing, we saw y changing a little too. Over 100" of x, we might see
        // 2-3" of y. This does not seem right since there was no visible physical movement of the
        // robot in y. So we need to see the raw dead wheel encoder values to help debug this.

        // I sure wish I could figure out how to get at the encoder ticks. It seems like I should
        // be able to get at them through the localizer but I can't figure out how. So I'm going to
        // get them from the motor encoder ports that they are plugged into.

        // enocoders are plugged into motor encoder port as follows
        // left  encoder into FrontLeft = 0 index in list of motors
        // right encoder into BackLeft  = 1 index in list of motors
        // rear  encoder into BackRight = 2 index in list of motors

        // apparently the encoder positions are not zeroed out as part of initialization so I will
        // zero them myself
        double leftEncoderStartPositionInInches = drive.getWheelPositions().get(0);
        double rightEncoderStartPositionInInches = drive.getWheelPositions().get(1);
        double rearEncoderStartPositionInInches = drive.getWheelPositions().get(2);
        double leftEncoderDeltaPositionInInches = 0;
        double rightEncoderDeltaPositionInInches = 0;
        double rearEncoderDeltaPositionInInches = 0;

        waitForStart();

        while (!isStopRequested()) {
            drive.setWeightedDrivePower(
                    new Pose2d(
                            -gamepad1.left_stick_y,
                            -gamepad1.left_stick_x,
                            -gamepad1.right_stick_x
                    )
            );

            drive.update();

            // calculate how far each encoder wheel has turned. Note the units are inches here.
            leftEncoderDeltaPositionInInches = leftEncoderStartPositionInInches - drive.getWheelPositions().get(0);
            rightEncoderDeltaPositionInInches = rightEncoderStartPositionInInches - drive.getWheelPositions().get(1);
            rearEncoderDeltaPositionInInches = rearEncoderStartPositionInInches - drive.getWheelPositions().get(2);

            Pose2d poseEstimate = drive.getPoseEstimate();
            telemetry.addData("x", poseEstimate.getX());
            telemetry.addData("y", poseEstimate.getY());
            telemetry.addData("heading", Math.toDegrees(poseEstimate.getHeading()));
            telemetry.addData("left encoder (in) ", leftEncoderDeltaPositionInInches);
            telemetry.addData("right encoder (in) ", rightEncoderDeltaPositionInInches);
            telemetry.addData("lateral encoder (in) ", rearEncoderDeltaPositionInInches);
            telemetry.update();
        }
    }
}
