package org.firstinspires.ftc.teamcode.Lib.FTCLib;

/**
 * Torcelli's equation allows you to calculate the velocity needed to achieve a constant acceleration
 * as a function of distance. Normally you would think of it as a function of time. This is very
 * handy for ramping up or down the power to a motor in order to get a constant acceleration.
 * Vf^2 = Vi^2 + 2 * a * deltaX
 * Vf = final velocity (essentially motor power if using RUN_USING_ENCODERS mode)
 * Vi = initial velocity (essentially motor power if using RUN_USING_ENCODERS mode)
 * a = acceleration
 * deltaX - the distance that the acceleration is to take place over
 * see https://en.wikipedia.org/wiki/Torricelli's_equation
 *
 */
public class Torcelli {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************



    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private double initialPower;
    private double initialPowerSquared;
    private double finalPower;
    private double deltaX;
    private double accelerationTimesTwo;
    private boolean complete = false;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getDistanceToChangeVelocityOver() {
        return deltaX;
    }

    public double getFinalPower() {
        return finalPower;
    }

    public double getAccelerationTimesTwo() {
        return accelerationTimesTwo;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public Torcelli(double initialPower, double finalPower, double distance) {
        setupTorcelli(initialPower, finalPower, distance);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Calculate the acceleration needed to achieve a constant acceleration (linear change in
     * velocity) over the distance specified.
     * @param initialPower
     * @param finalPower
     * @param deltaX
     * @return
     */
    private double get2a(double initialPower, double finalPower, double deltaX) {
        if (deltaX == 0) {
            return 0;
        } else {
            return (Math.pow(finalPower,2) - Math.pow(initialPower, 2)) / deltaX;
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * Setup the torcelli equation for the change in velocity you want over the distance you want.
     * @param initialPower the power you are starting with
     * @param finalPower the power you want to end up with
     * @param deltaX the distance to change the velocity over
     */
    public void setupTorcelli(double initialPower, double finalPower, double deltaX) {
        this.initialPower = initialPower;
        this.finalPower = finalPower;
        // force the distances to be positive. They could be negative but they have to be consistent.
        // In order to avoid bugs from the user passing in inconsistent signs, just make it +
        this.deltaX = Math.abs(deltaX);
        this.accelerationTimesTwo = get2a(initialPower, finalPower, deltaX);
        // precalculate initial power ^2 so it does not have to be run on every call to getPower
        this.initialPowerSquared = Math.pow(initialPower, 2);
        this.complete = false;
    }

    /**
     * Returns the power needed given the current distance in order to achieve a
     * constant acceleration from the initial velocity to the final velocity.
     * Vf = sqrt( Vi^2 + 2 * a * deltaX)
     * @param xLocation - distance from the point at which you started the change in velocity
     * @return power to set the motor to get the constant acceleration you wanted. However, the
     * power returned will never be less than the finalPower.
     */
    public double getPower(double xLocation) {
        double result;
        // force the distances to be positive. They could be negative but they have to be consistent.
        // In order to avoid bugs from the user passing in inconsistent signs, just make it +
        // Has the robot travelled beyond the stop point?
        if (Math.abs(xLocation) > deltaX) {
            // the robot has rolled past the desired stop point. Calculating the power will fail
            // because the sqrt will be operating on a negative number. Instead just set the power
            // to the final power and be done
            result = finalPower;
            complete = true;
        } else {
            double power = Math.sqrt(initialPowerSquared + (accelerationTimesTwo * Math.abs(xLocation)));
            // the resulting power will be at least the final power
            if (power <= finalPower) {
                result = finalPower;
                complete = true;
            } else {
                result = power;
            }
        }
        return result;
    }

    public boolean isComplete() {
        return complete;
    }
}
