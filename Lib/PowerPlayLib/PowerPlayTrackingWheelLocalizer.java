package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;
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
public class PowerPlayTrackingWheelLocalizer extends ThreeTrackingWheelLocalizer {
    // just below the class statement
    public static double TICKS_PER_REV = 8192;
    public static double WHEEL_RADIUS = .748; // in
    public static double GEAR_RATIO = 1; // output (wheel) speed / input (encoder) speed

    public static double LATERAL_DISTANCE = 10.7969; // in; distance between the left and right wheels
    public static double FORWARD_OFFSET = -5.5; // in; offset of the lateral wheel

    // use these to adjust for wheel radius differences
    //public static double LEFT_X_MULTIPLIER = 1.00722888; // Multiplier in the X direction
    //public static double RIGHT_X_MULTIPLIER = 1.010419368; // Multiplier in the X direction
    public static double LEFT_X_MULTIPLIER = 1.0026 * 1.005; // Multiplier in the X direction
    public static double RIGHT_X_MULTIPLIER = 1.0026; // Multiplier in the X direction
    //public static double Y_MULTIPLIER = 1.006938983050; // Multiplier in the Y direction
    public static double Y_MULTIPLIER = 1; // Multiplier in the Y direction

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

    public PowerPlayTrackingWheelLocalizer(HardwareMap hardwareMap) {
        super(Arrays.asList(
                new Pose2d(0, LATERAL_DISTANCE / 2, 0), // left
                new Pose2d(0, -LATERAL_DISTANCE / 2, 0), // right
                new Pose2d(FORWARD_OFFSET, 0, Math.toRadians(90)) // front
        ));

        leftEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, FreightFrenzyRobotRoadRunner.HardwareName.ODOMETRY_MODULE_LEFT.hwName));
        rightEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, FreightFrenzyRobotRoadRunner.HardwareName.ODOMETRY_MODULE_RIGHT.hwName));
        frontEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, FreightFrenzyRobotRoadRunner.HardwareName.ODOMETRY_MODULE_BACK.hwName));

        // reverse any encoders using Encoder.setDirection(Encoder.Direction.REVERSE)
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
                encoderTicksToInches(leftEncoder.getCurrentPosition()) * LEFT_X_MULTIPLIER,
                encoderTicksToInches(rightEncoder.getCurrentPosition()) * RIGHT_X_MULTIPLIER,
                encoderTicksToInches(frontEncoder.getCurrentPosition()) * Y_MULTIPLIER
        );
    }

    @NonNull
    @Override
    public List<Double> getWheelVelocities() {
        //  If your encoder velocity can exceed 32767 counts / second (such as the REV Through Bore and other
        //  competing magnetic encoders), change Encoder.getRawVelocity() to Encoder.getCorrectedVelocity() to enable a
        //  compensation method

        return Arrays.asList(
                encoderTicksToInches(leftEncoder.getCorrectedVelocity()) * LEFT_X_MULTIPLIER,
                encoderTicksToInches(rightEncoder.getCorrectedVelocity()) * RIGHT_X_MULTIPLIER,
                encoderTicksToInches(frontEncoder.getCorrectedVelocity()) * Y_MULTIPLIER
        );
    }
}
