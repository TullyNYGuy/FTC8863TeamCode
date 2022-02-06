package org.firstinspires.ftc.teamcode.ArmTuning.Lib;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/*
 * Constants shared between multiple drive types.
 *
 * TODO: Tune or adjust the following constants to fit your robot. Note that the non-final
 * fields may also be edited through the dashboard (connect to the robot's WiFi network and
 * navigate to https://192.168.49.1:8080/dash). Make sure to save the values here after you
 * adjust them in the dashboard; **config variable changes don't persist between app restarts**.
 */
@Config
public class ArmConstants {

    public static AngleUnit units = AngleUnit.DEGREES;

    /*
     * These come from the opmode ArmTuningSetupPositions. They are angles in degrees with 0 degrees
     * corresponding to the start position of the arm. These should be positive values.
     */
    public static final double HORIZONTAL_POSITION = 45.3; // degrees

    public static double getHorizontalPosition(AngleUnit units) {
        if (units == AngleUnit.DEGREES) {
            return HORIZONTAL_POSITION;
        } else {
            return Math.toRadians(HORIZONTAL_POSITION);
        }
    }

    public static final double VERTICAL_POSITION = 134.7; // degrees

    public static double getVerticalPosition(AngleUnit units) {
        if (units == AngleUnit.DEGREES) {
            return VERTICAL_POSITION;
        } else {
            return Math.toRadians(VERTICAL_POSITION);
        }
    }

    /**
     * Get the angle, in your desired units, from the current position to the horizontal position.
     * The horizontal position is the one closest to the start position of the arm, not 180 degrees
     * plus a little from it.
     * @param currentAngle
     * @param units - units of the currentAngle and of the return value
     * @return - angle from horizontal. Negative = below horizontal. Positive = above horizontal
     */
    public static double getAngleToHorizontal(double currentAngle, AngleUnit units) {
        if (units == AngleUnit.DEGREES) {
            return currentAngle - getHorizontalPosition(AngleUnit.DEGREES);
        } else {
            return currentAngle - getHorizontalPosition(AngleUnit.RADIANS);
        }
    }

    /**
     * Get the angle, in your desired units, from the current position to the vertical up position.
     * @param currentAngle
     * @param units - units of the currentAngle and of the return value
     * @return - angle from vertical up. Negative = arm on side of starting position.
     *           Positive = arm on side opposite starting position
     */
    public static double getAngleToVertical(double currentAngle, AngleUnit units) {
        if (units == AngleUnit.DEGREES) {
            return currentAngle - getVerticalPosition(AngleUnit.DEGREES);
        } else {
            return currentAngle - getVerticalPosition(AngleUnit.RADIANS);
        }
    }

//    public static PIDFCoefficients MOTOR_VELO_PID = new PIDFCoefficients(0, 0, 0,
//            getMotorVelocityF(MAX_RPM / 60 * TICKS_PER_REV));

    public static double kS = 0.09;
    //public static double kSPluskG = 0.14; // with no weights
    public static double kSPluskG = 0.30; // with weights

    public static double getKg() {
        if (kSPluskG - kS < 0) {
            return 0;
        } else {
            return kSPluskG - kS;
        }
    }

    /*
     * These are the feedforward parameters used to model the drive motor behavior. If you are using
     * the built-in velocity PID, *these values are fine as is*. However, if you do not have drive
     * motor encoders or have elected not to use them for velocity control, these values should be
     * empirically tuned.
     */
    //public static double kV = 1.0 / rpmToVelocity(MAX_RPM);
    public static double kV = .016;
    public static double kA = 0.00375365;
    public static double kStatic = 0.075;

    /*
     * These values are used to generate the trajectories for you robot. To ensure proper operation,
     * the constraints should never exceed ~80% of the robot's actual capabilities. While Road
     * Runner is designed to enable faster autonomous motion, it is a good idea for testing to start
     * small and gradually increase them later after everything is working. All distance units are
     * inches.
     */
    public static double MAX_VEL = 30; // measured 101 using MaxVelocityTuner Kf = 15.97
    public static double MAX_ACCEL = 30;
    public static double MAX_ANG_VEL = Math.toRadians(60); //measured 475 using MaxAngularVeloTuner
    public static double MAX_ANG_ACCEL = Math.toRadians(60);

    public static double getMotorVelocityF(double ticksPerSecond) {
        // see https://docs.google.com/document/d/1tyWrXDfMidwYyP_5H4mZyVgaEswhOC35gvdmP-V-5hA/edit#heading=h.61g9ixenznbx
        return 32767 / ticksPerSecond;
    }
}
