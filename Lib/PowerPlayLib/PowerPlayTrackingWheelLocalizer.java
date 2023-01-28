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

    /**
     * Adjusts the radius of the side wheels to account for difference between actual distance
     * moved forward and reverse to distance measured by odometry modules. 
     */
    //public static double SIDE_WHEEL_ADJUSTMENT_FACTOR = 1.0026;
    public static double SIDE_WHEEL_ADJUSTMENT_FACTOR = 1.0;

    public double getSIDE_WHEEL_ADJUSTMENT_FACTOR() {
        return SIDE_WHEEL_ADJUSTMENT_FACTOR;
    }

    /**
     * Adjusts the radius of the left wheel vs the right wheel to account for difference in heading.
     * The heading change should be 0 when moving in a straight line but if it is not then this will
     * tweak it to 0.
     */
    //public static double LEFT_TO_RIGHT_WHEEL_ADJUSTMENT_FACTOR = 1.0044;
    public static double LEFT_TO_RIGHT_WHEEL_ADJUSTMENT_FACTOR = 1.0089;

    public static double getLeftToRightWheelAdjustmentFactor() {
        return LEFT_TO_RIGHT_WHEEL_ADJUSTMENT_FACTOR;
    }

    /**
     * Use the previous adjustement factors to come up with the overall left wheel radius adjustment
     */
    private double leftXMultiplier = SIDE_WHEEL_ADJUSTMENT_FACTOR * LEFT_TO_RIGHT_WHEEL_ADJUSTMENT_FACTOR;

    public void setLeftXMultiplier(double leftXMultiplier) {
        this.leftXMultiplier = leftXMultiplier;
    }

    /**
     * Use the previous adjustement factors to come up with the overall right wheel radius adjustment
     */
    private double rightXMultiplier = SIDE_WHEEL_ADJUSTMENT_FACTOR;

    public double getRightXMultiplier() {
        return rightXMultiplier;
    }

    /**
     * Adjusts the radius of the Y wheel, the one for strafing. Accounts for difference between
     * actual distance moved sideways to distance measured by odometry module.
     */
    public static double LATERAL_WHEEL_ADJUSTMENT_FACTOR = 1.0;

    public double getLATERAL_WHEEL_ADJUSTMENT_FACTOR() {
        return LATERAL_WHEEL_ADJUSTMENT_FACTOR;
    }

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

    private int initialLeftEncoderValue = 0;
    private int initialRightEncoderValue = 0;
    private int initialFrontEncoderValue = 0;

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

    /**
     * The encoder counts are not zero at the start of an opmode. This allows us to keep track of
     * the change in the encoder count since the start of an opmode.
     */
    public void zeroEncoderCounts() {
        initialLeftEncoderValue = leftEncoder.getCurrentPosition();
        initialRightEncoderValue = rightEncoder.getCurrentPosition();
        initialFrontEncoderValue = frontEncoder.getCurrentPosition();
    }

    /**
     * Get the current encoder count. Note that this is since power on of the hub, not since the
     * start of an opmode.
     * @return
     */
    public int getLeftEncoderCount() {
        return leftEncoder.getCurrentPosition();
    }

    /**
     * Get the change in encoder count since the last time it was zeroed. If you zero at the start
     * of an opmode this will get you the change in count since the start of the opmode.
     * @return
     */
    public int getLeftEncoderCountSinceZero() {
        return leftEncoder.getCurrentPosition() - initialLeftEncoderValue;
    }

    /**
     * Get the change in encoder count since the last time it was zeroed. If you zero at the start
     * of an opmode this will get you the change in count since the start of the opmode. This count
     * is then adjusted by the adjustment factors
     * @return
     */
    public int getLeftEncoderAdjustedCountSinceZero () {
        return (int)((leftEncoder.getCurrentPosition() - initialLeftEncoderValue) * leftXMultiplier);
    }

    public int getRightEncoderCount() {
        return rightEncoder.getCurrentPosition();
    }

    public int getRightEncoderCountSinceZero() {
        return rightEncoder.getCurrentPosition() - initialRightEncoderValue;
    }

    /**
     * Get the change in encoder count since the last time it was zeroed. If you zero at the start
     * of an opmode this will get you the change in count since the start of the opmode. This count
     * is then adjusted by the adjustment factors
     * @return
     */
    public int getRightEncoderAdjustedCountSinceZero () {
        return (int)((rightEncoder.getCurrentPosition() - initialRightEncoderValue) * rightXMultiplier);
    }


    public int getFrontEncoderCount() {
        return frontEncoder.getCurrentPosition();
    }

    public int getFrontEncoderCountSinceZero() {
        return frontEncoder.getCurrentPosition() - initialFrontEncoderValue;
    }

    /**
     * Get the change in encoder count since the last time it was zeroed. If you zero at the start
     * of an opmode this will get you the change in count since the start of the opmode. This count
     * is then adjusted by the adjustment factors
     * @return
     */
    public int getFrontEncoderAdjustedCountSinceZero () {
        return (int)((frontEncoder.getCurrentPosition() - initialFrontEncoderValue) * LATERAL_WHEEL_ADJUSTMENT_FACTOR);
    }


    public static double encoderTicksToInches(double ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        return Arrays.asList(
                encoderTicksToInches(leftEncoder.getCurrentPosition()) * leftXMultiplier,
                encoderTicksToInches(rightEncoder.getCurrentPosition()) * rightXMultiplier,
                encoderTicksToInches(frontEncoder.getCurrentPosition()) * LATERAL_WHEEL_ADJUSTMENT_FACTOR
        );
    }

    @NonNull
    @Override
    public List<Double> getWheelVelocities() {
        //  If your encoder velocity can exceed 32767 counts / second (such as the REV Through Bore and other
        //  competing magnetic encoders), change Encoder.getRawVelocity() to Encoder.getCorrectedVelocity() to enable a
        //  compensation method

        return Arrays.asList(
                encoderTicksToInches(leftEncoder.getCorrectedVelocity()) * leftXMultiplier,
                encoderTicksToInches(rightEncoder.getCorrectedVelocity()) * rightXMultiplier,
                encoderTicksToInches(frontEncoder.getCorrectedVelocity()) * LATERAL_WHEEL_ADJUSTMENT_FACTOR
        );
    }
}
