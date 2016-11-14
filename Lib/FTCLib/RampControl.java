package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This class is used to implement a ramp over time. A line is setup using an intial and final value
 * and a time to run the ramp over. The ramp is a limit. Any value that is passed in during the time
 * the ramp is operating will be tested against the line. If the value is less than the line it is
 * ok the value is passed back. If the value passed in is greater than the line, then it is clipped
 * to the line's value and the value on the ramp line is passed back instead.
 *
 *  |   value is too big,       *
 *  |   return the line's    *
 *  |   value             *
 *  |   instead        *
 *  |               *
 *  |            *
 *  |         *
 *  |      *    value is ok (less than line)
 *  |   *       return the value back again
 *  -*-----------------------------------
 *         time ->
 *  This calls can be used to implement a ceiling (maximum) that gradually increases over time. A
 *  gradual increase in motor power from startup is one example of a good use for it.
 */

public class RampControl {

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

    /**
     * A timer for use in the ramp function
     */
    private ElapsedTime timer;

    /**
     * The y intercept of the ramp equation. This is the starting point of the ramp.
     */
    private double initialValue = 0;

    /**
     * This is the endpoint of the line for the ramp function.
     */
    private double finalValue = 0;

    /**
     * This is the time required to reach the final value. Slope of the line is determined from
     * (finalValue - initialValue) / timeToReachFinalValueInmSec
     * units are milli-seconds (1000 mSec = 1 sec)
     */
    private double timeToReachFinalValueInmSec = 0;

    /**
     * Whether the ramp is enabled or not. If not then a call to it with a value will return the
     * value right back.
     */
    private boolean enabled = false;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public void setInitialValue(double initialValue) {
        this.initialValue = initialValue;
    }

    public double getInitialValue() {
        return initialValue;
    }

    public void setFinalValue(double finalValue) {
        this.finalValue = finalValue;
    }

    public double getFinalValue() {
        return finalValue;
    }

    public void setTimeToReachFinalValueInmSec(double timeToReachFinalValueInmSec) {
        this.timeToReachFinalValueInmSec = timeToReachFinalValueInmSec;
    }

    public double getTimeToReachFinalValueInmSec() {
        return timeToReachFinalValueInmSec;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public RampControl(double initialValue, double finalValue, double timeToReachFinalValueInmSec) {
        this.initialValue = initialValue;
        this.finalValue = finalValue;
        this.timeToReachFinalValueInmSec = timeToReachFinalValueInmSec;
        timer = new ElapsedTime();
        // The ramp control is disabled to start out
        this.enabled = false;
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * Start the ramp control's timer
     */
    public void start() {
        timer.reset();
        this.setEnabled(true);
    }

    /**
     * Disable the ramp control
     */
    public void disable() {
        this.setEnabled(false);
    }

    /**
     * Enable the ramp control
     */
    public void enable() {
        this.setEnabled(true);
    }

    /**
     * Has the ramp control's timer expired?
     * @return timer expired = true
     */
    public boolean isTimeExpired (){
        if (timer.milliseconds() > timeToReachFinalValueInmSec) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Pass in a value. If the ramp control is enabled and the time has not expired, check the value
     * against a line deterimed by (finalValue - intialValue) / timeToReachFinalValueInmSec * time + initialValue
     * If the value being tested is greater than the line's value, return the line's value. In other
     * words reduce the value to the line's value. If it is less just leave the value as is.
     * @param value value to test against the line
     * @return
     */
    public double getRampValueLinear(double value) {

        double valueFromRamp;

        if (this.isEnabled()) {
            // The ramp is enabled
            if (timer.milliseconds() < timeToReachFinalValueInmSec) {
                // if timer is less than time to reach the final value (so ramp should not be in effect)
                // then calculate the value given by the ramp (valueFromRamp = m * elapsed time + initialValue) (y=mx+b)
                valueFromRamp = (finalValue - initialValue) / timeToReachFinalValueInmSec * timer.milliseconds() + initialValue;
                // if the value input is less than the calculated ramp value then use the input, otherwise use the ramp value
                if (value < valueFromRamp) {
                    return value;
                } else {
                    return valueFromRamp;
                }
            } else {
                // if the time for the ramp has expired, disable the ramp and then just return the input
                this.setEnabled(false);
                return value;
            }
        } else {
            // The ramp is not enabled so just return the input
            return value;
        }

    }
}
