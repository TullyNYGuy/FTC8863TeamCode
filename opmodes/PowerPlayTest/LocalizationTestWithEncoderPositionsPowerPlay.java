package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.MecanumDriveFreightFrenzy;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayMecanumDrive;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.RoadRunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.RoadRunner.drive.StandardTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.RoadRunner.util.Encoder;

/**
 * This is a simple teleop routine for testing localization. Drive the robot around like a normal
 * teleop routine and make sure the robot's estimated pose matches the robot's actual pose (slight
 * errors are not out of the ordinary, especially with sudden drive motions). The goal of this
 * exercise is to ascertain whether the localizer has been configured properly (note: the pure
 * encoder localizer heading may be significantly off if the track width has not been tuned).
 */
@TeleOp(group = "drive")
@Disabled
public class LocalizationTestWithEncoderPositionsPowerPlay extends LinearOpMode {
    public CSVDataFile csvDataFile = new CSVDataFile("Encoders");

    @Override
    public void runOpMode() throws InterruptedException {

        PowerPlayMecanumDrive drive = new PowerPlayMecanumDrive(
                PowerPlayRobot.HardwareName.CONFIG_FL_MOTOR.hwName,
                PowerPlayRobot.HardwareName.CONFIG_BL_MOTOR.hwName,
                PowerPlayRobot.HardwareName.CONFIG_FR_MOTOR.hwName,
                PowerPlayRobot.HardwareName.CONFIG_BR_MOTOR.hwName,
                hardwareMap);;

        //StandardTrackingWheelLocalizer localizer = (StandardTrackingWheelLocalizer)drive.getLocalizer();
        PowerPlayTrackingWheelLocalizer localizer = (PowerPlayTrackingWheelLocalizer)drive.getLocalizer();

        // zero the encoder counts since they are only zeroed at control hub power on
        localizer.zeroEncoderCounts();

        csvDataFile.headerStrings("left", "right", "lateral");

        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Running https://www.learnroadrunner.com/dead-wheels.html#tuning-three-wheel
        // When tuning the 3 dead wheels we noticed that even though we were trying to keep the bot
        // moving in only 1 axes, we where showing movement in the other axes. So when x should be
        // the only dimension changing, we saw y changing a little too. Over 100" of x, we might see
        // 2-3" of y. This does not seem right since there was no visible physical movement of the
        // robot in y. So we need to see the raw dead wheel encoder values to help debug this.

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

            Pose2d poseEstimate = drive.getPoseEstimate();
            telemetry.addData("x", poseEstimate.getX());
            telemetry.addData("y", poseEstimate.getY());
            telemetry.addData("heading", Math.toDegrees(poseEstimate.getHeading()));
            telemetry.addLine();
            telemetry.addData("left encoder ", localizer.getLeftEncoderCountSinceZero());
            telemetry.addData("right encoder ", localizer.getRightEncoderCountSinceZero());
            telemetry.addData("lateral encoder ", localizer.getFrontEncoderCountSinceZero());
            telemetry.addLine();
            telemetry.addData("left encoder after adjustement ", localizer.getLeftEncoderAdjustedCountSinceZero());
            telemetry.addData("right encoder after adjustment ", localizer.getRightEncoderAdjustedCountSinceZero());
            telemetry.addData("lateral encoder after adjustement ", localizer.getFrontEncoderAdjustedCountSinceZero());
            telemetry.update();

            csvDataFile.writeData(localizer.getLeftEncoderCountSinceZero(), localizer.getRightEncoderCountSinceZero(),localizer.getFrontEncoderCountSinceZero());
        }
        csvDataFile.closeDataLog();
    }
}