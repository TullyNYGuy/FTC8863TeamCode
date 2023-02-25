package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import androidx.annotation.Nullable;

public class PIDFController {

    public interface FeedforwardFunction {
        Double compute(double position, @Nullable Double velocity);
    }


    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private final PIDCoefficients pid;
    private final double kV, kA, kStatic;
    private final FeedforwardFunction kF;

    private double errorSum;
    private long lastUpdateTs;

    private boolean inputBounded;
    private double minInput, maxInput;

    private boolean outputBounded;
    private double minOutput, maxOutput;

    /**
     * Target position (that is, the controller setpoint).
     */
    private double targetPosition;

    public void setTargetPosition(double targetPosition) {
        this.targetPosition = targetPosition;
    }

    /**
     * Target velocity.
     */
    private double targetVelocity;

    public void setTargetVelocity(double targetVelocity) {
        this.targetVelocity = targetVelocity;
    }

    /**
     * Target acceleration.
     */
    private double targetAcceleration;

    public void setTargetAcceleration(double targetAcceleration) {
        this.targetAcceleration = targetAcceleration;
    }

    /**
     * Error computed in the last call to {@link #update(long, double, Double)}
     */
    private double lastError;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * Feedforward parameters {@code kV}, {@code kA}, and {@code kStatic} correspond with a basic
     * kinematic model of DC motors. The general function {@code kF} computes a custom feedforward
     * term for other plants.
     *
     * @param pid     traditional PID coefficients
     * @param kV      feedforward velocity gain
     * @param kA      feedforward acceleration gain
     * @param kStatic additive feedforward constant
     * @param kF      custom feedforward that depends on position and velocity
     */
    public PIDFController(
            PIDCoefficients pid,
            double kV,
            double kA,
            double kStatic,
            FeedforwardFunction kF) {
        this.pid = pid;
        this.kV = kV;
        this.kA = kA;
        this.kStatic = kStatic;
        this.kF = kF;
    }

    public PIDFController(
            PIDCoefficients pid,
            double kV,
            double kA,
            double kStatic) {
        this(pid, kV, kA, kStatic, (x, v) -> 0.0);
    }

    public PIDFController(
            PIDCoefficients pid,
            FeedforwardFunction kF) {
        this(pid, 0, 0, 0, kF);
    }

    public PIDFController(
            PIDCoefficients pid) {
        this(pid, 0, 0, 0);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private double getPositionError(double measuredPosition) {
        double error = targetPosition - measuredPosition;
        if (inputBounded) {
            final double inputRange = maxInput - minInput;
            while (Math.abs(error) > inputRange / 2.0) {
                error -= Math.copySign(inputRange, error);
            }
        }
        return error;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * Sets bound on the input of the controller. When computing the error, the min and max are
     * treated as the same value. (Imagine taking the segment of the real line between min and max
     * and attaching the endpoints.)
     *
     * @param min minimum input
     * @param max maximum input
     */
    public void setInputBounds(double min, double max) {
        if (min < max) {
            inputBounded = true;
            minInput = min;
            maxInput = max;
        }
    }

    /**
     * Sets bounds on the output of the controller.
     *
     * @param min minimum output
     * @param max maximum output
     */
    public void setOutputBounds(double min, double max) {
        if (min < max) {
            outputBounded = true;
            minOutput = min;
            maxOutput = max;
        }
    }

    /**
     * Run a single iteration of the controller.
     *
     * @param timestamp        measurement timestamp as given by {@link System#nanoTime()}
     * @param measuredPosition measured position (feedback)
     * @param measuredVelocity measured velocity
     */
    public double update(
            long timestamp,
            double measuredPosition,
            @Nullable Double measuredVelocity) {
        final double error = getPositionError(measuredPosition);

        if (lastUpdateTs == 0) {
            lastError = error;
            lastUpdateTs = timestamp;
            return 0;
        }

        final double dt = timestamp - lastUpdateTs;
        errorSum += 0.5 * (error + lastError) * dt;
        final double errorDeriv = (error - lastError) / dt;

        lastError = error;
        lastUpdateTs = timestamp;

        double velError;
        if (measuredVelocity == null) {
            velError = errorDeriv;
        } else {
            velError = targetVelocity - measuredVelocity;
        }

        double baseOutput = pid.kP * error + pid.kI * errorSum + pid.kD * velError +
                kV * targetVelocity + kA * targetAcceleration +
                kF.compute(measuredPosition, measuredVelocity);

        double output = 0;
        if (Math.abs(baseOutput) > 1e-6) {
            output = baseOutput + Math.copySign(kStatic, baseOutput);
        }

        if (outputBounded) {
            return Math.max(minOutput, Math.min(output, maxOutput));
        }

        return output;
    }

    public double update(
            long timestamp,
            double measuredPosition) {
        return update(timestamp, measuredPosition, null);
    }

    public double update(double measuredPosition) {
        return update(System.nanoTime(), measuredPosition, null);
    }

    /**
     * Reset the controller's integral sum.
     */
    public void reset() {
        errorSum = 0;
        lastError = 0;
        lastUpdateTs = 0;
    }
}
