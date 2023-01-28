package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayMecanumDrive;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayTrackingWheelLocalizer;

/**
 * This opmode tests if a 2m distance sensor can be used to control the speed of the robot when it
 * sees a cone or a junction pole.
 */
@TeleOp(group = "drive")
public class LocalizationTestWith2mDistanceSensor extends LinearOpMode {
    public enum DrivingStates {
        FULL_POWER,
        FULL_POWER_LOCKED_IN,
        LOW_POWER
    }


    public static double DISTANCE_LIMIT = 300; //mm
    public static double HIGH_SPEED = 1.0;
    public static double LOW_SPEED = 0.25;
    public double joystickScale = HIGH_SPEED;

    ElapsedTime timer;

    public DrivingStates drivingStates = DrivingStates.FULL_POWER;

    @Override
    public void runOpMode() throws InterruptedException {
        DistanceSensor sensorRange;
        sensorRange = hardwareMap.get(DistanceSensor.class, "distanceSensor");
        Rev2mDistanceSensor sensorTimeOfFlight = (Rev2mDistanceSensor)sensorRange;

        timer = new ElapsedTime();

        double distanceRead = 0;

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

        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();

        while (!isStopRequested()) {
            distanceRead = sensorRange.getDistance(DistanceUnit.MM);

            switch (drivingStates) {
                case FULL_POWER: {
                    if (distanceRead < 300) {
                        drivingStates = DrivingStates.LOW_POWER;
                        joystickScale = LOW_SPEED;
                    }
                }
                break;

                case FULL_POWER_LOCKED_IN: {
                    if (timer.seconds() > 5) {
                        drivingStates = DrivingStates.FULL_POWER;
                        joystickScale = HIGH_SPEED;
                    }
                }
                break;

                case LOW_POWER: {
                    // a resets to high speed
                    if (gamepad1.a) {
                        drivingStates = DrivingStates.FULL_POWER_LOCKED_IN;
                        joystickScale = HIGH_SPEED;
                        timer.reset();
                    }
                }
                break;
            }

            drive.setWeightedDrivePower(getJoystickScaled(joystickScale));
            drive.update();

            Pose2d poseEstimate = drive.getPoseEstimate();
            telemetry.addData("driving mode = ", drivingStates.toString());
            telemetry.addData("x", poseEstimate.getX());
            telemetry.addData("y", poseEstimate.getY());
            telemetry.addData("heading", Math.toDegrees(poseEstimate.getHeading()));
            telemetry.addData("distance read = ", distanceRead);
            telemetry.addLine();
            telemetry.addData("left encoder ", localizer.getLeftEncoderCountSinceZero());
            telemetry.addData("right encoder ", localizer.getRightEncoderCountSinceZero());
            telemetry.addData("lateral encoder ", localizer.getFrontEncoderCountSinceZero());
            telemetry.addLine();
            telemetry.addData("left encoder after adjustment ", localizer.getLeftEncoderAdjustedCountSinceZero());
            telemetry.addData("right encoder after adjustment ", localizer.getRightEncoderAdjustedCountSinceZero());
            telemetry.addData("lateral encoder after adjustement ", localizer.getFrontEncoderAdjustedCountSinceZero());
            telemetry.update();
        }
    }

    public Pose2d getJoystickScaled(double scale) {
        return new Pose2d(
                -gamepad1.left_stick_y * scale,
                -gamepad1.left_stick_x * scale,
                -gamepad1.right_stick_x * scale
        );
    }
}