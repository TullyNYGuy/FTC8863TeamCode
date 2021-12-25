package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.RoadRunner.util.Encoder;

import java.util.Arrays;
import java.util.List;

/*
 * Sample tracking wheel localizer implementation assuming the standard configuration:
 *
 *    /--------------\
 *    |     ____     |
 *    |     ----     |
 *    | ||        || |
 *    | ||        || |
 *    |              |
 *    |              |
 *    \--------------/
 *
 */
@Config
public class TrackingWheelLocalizerUltimateGoal extends ThreeTrackingWheelLocalizer {
    public static double TICKS_PER_REV = 8192;
    public static double WHEEL_RADIUS = .748; // in
    public static double GEAR_RATIO = 1; // output (wheel) speed / input (encoder) speed

    // CAD says this
    //public static double LATERAL_DISTANCE = 16.2205; // in; distance between the left and right wheels
    // TrackingWheelLateralDistanceTuner says this for slow turning
    //public static double LATERAL_DISTANCE = 16.094; // in; distance between the left and right wheels
    // TrackingWheelLateralDistanceTuner says this for fast turning
    public static double LATERAL_DISTANCE = 16.018; // in; distance between the left and right wheels
    // CAD says this
    //public static double FORWARD_OFFSET = -7.380; // in; offset of the lateral wheel
    //TrackingWheelForwardOffsetTunerUlitmateGoal says this
    public static double FORWARD_OFFSET = -7.510; // in; offset of the lateral wheel

    // use these to adjust for wheel radius differences
    public static double X_MULTIPLIER = .9870; // Multiplier in the X direction
    public static double Y_MULTIPLIER = .9926; // Multiplier in the Y direction

    private Encoder leftEncoder, rightEncoder, frontEncoder;

    public Encoder getLeftEncoder() {
        return leftEncoder;
    }

    public Encoder getRightEncoder() {
        return rightEncoder;
    }

    public Encoder getFrontEncoder() {
        return frontEncoder;
    }

    public TrackingWheelLocalizerUltimateGoal(HardwareMap hardwareMap) {
        super(Arrays.asList(
                new Pose2d(0, LATERAL_DISTANCE / 2, 0), // left
                new Pose2d(0, -LATERAL_DISTANCE / 2, 0), // right
                new Pose2d(FORWARD_OFFSET, 0, Math.toRadians(90)) // front
        ));

        leftEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, UltimateGoalRobotRoadRunner.HardwareName.CONFIG_FL_MOTOR.hwName));
        rightEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, UltimateGoalRobotRoadRunner.HardwareName.CONFIG_BL_MOTOR.hwName));
        frontEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, UltimateGoalRobotRoadRunner.HardwareName.CONFIG_BR_MOTOR.hwName));

        // TODO: reverse any encoders using Encoder.setDirection(Encoder.Direction.REVERSE)
        frontEncoder.setDirection(Encoder.Direction.REVERSE);
        leftEncoder.setDirection(Encoder.Direction.REVERSE);
        rightEncoder.setDirection(Encoder.Direction.FORWARD);

    }

    public static double encoderTicksToInches(double ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        return Arrays.asList(
                encoderTicksToInches(leftEncoder.getCurrentPosition()) * X_MULTIPLIER,
                encoderTicksToInches(rightEncoder.getCurrentPosition()) * X_MULTIPLIER,
                encoderTicksToInches(frontEncoder.getCurrentPosition()) * Y_MULTIPLIER
        );
    }

    @NonNull
    @Override
    public List<Double> getWheelVelocities() {
        // TODO: If your encoder velocity can exceed 32767 counts / second (such as the REV Through Bore and other
        //  competing magnetic encoders), change Encoder.getRawVelocity() to Encoder.getCorrectedVelocity() to enable a
        //  compensation method

        return Arrays.asList(
                encoderTicksToInches(leftEncoder.getCorrectedVelocity()) * X_MULTIPLIER,
                encoderTicksToInches(rightEncoder.getCorrectedVelocity()) * X_MULTIPLIER,
                encoderTicksToInches(frontEncoder.getCorrectedVelocity()) * Y_MULTIPLIER
        );
    }
}
