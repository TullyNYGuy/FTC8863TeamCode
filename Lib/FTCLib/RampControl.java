package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;

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

    // consider adding enable control and status

    private ElapsedTime timer;

    private double initialValue = 0;

    private double finalValue = 0;

    private double timeToReachFinalValueInmSec = 0;

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

    public void start() {
        timer.reset();
        this.setEnabled(true);
    }

    public boolean isTimeExpired (){
        if (timer.milliseconds() > timeToReachFinalValueInmSec) {
            return true;
        } else {
            return false;
        }
    }

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
