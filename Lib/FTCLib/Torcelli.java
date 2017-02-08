package org.firstinspires.ftc.teamcode.Lib.FTCLib;


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

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public Torcelli(double initialPower, double finalPower, double distance) {
        setupTorcelli(initialPower,finalPower, distance);
        this.complete = false;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Calculate the acceleration needed to achieve a constant acceleration change in velocity
     * over the distance specified.
     * @param initialPower
     * @param finalPower
     * @param distance
     * @return
     */
    private double get2a(double initialPower, double finalPower, double distance) {
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
     * @param distance the distance to change the velocity over
     */
    public void setupTorcelli(double initialPower, double finalPower, double distance) {
        this.initialPower = initialPower;
        this.finalPower = finalPower;
        this.deltaX = distance;
        this.accelerationTimesTwo = get2a(initialPower, finalPower, distance);
        // precalculate initial power ^2
        this.initialPowerSquared = Math.pow(initialPower, 2);
    }
    /**
     * Returns the power needed given the current distance to the target in order to achieve a
     * constant acceleration from the initial velocity to the final velocity.
     * Vf = sqrt( Vi^2 + 2 * a * deltaX)
     * @param distanceToTarget
     * @return power to set the motor to get the constant acceleration you wanted. However, the
     * power returned will never be less than the finalPower.
     */
    public double getPower(double distanceToTarget) {
        double result;
        double power = Math.sqrt(initialPowerSquared + (accelerationTimesTwo * distanceToTarget));
        if (power <= finalPower) {
            result = finalPower;
            complete = true;
        } else {
            result = power;
        }
        return power;
    }

    public boolean isComplete() {
        return complete;
    }
}
