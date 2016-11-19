package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This class is used to implement a ramp over time. A line is setup using an initial and final value
 * and a time to run the ramp over. The ramp is a limit. Any value that is passed in during the time
 * the ramp is operating will be tested against the boundary line. If the value is less than the line it is
 * ok and the value is passed back. If the value passed in is greater than the line, then it is clipped
 * to the line's value and the value on the boundary line is passed back instead.
 * <p>
 * |   value is too big,       *
 * |   return the line's    *
 * |   value             *
 * |   instead        *
 * |               *
 * |            *
 * |         *
 * |      *    value is ok (less than line)
 * |   *       return the value back again
 * -*-----------------------------------
 * time ->
 * This class can be used to implement a ceiling (maximum) that changes over time. A
 * gradual increase in motor power from startup is one example of a good use for it.
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
    private double valueAtStartTime = 0;

    /**
     * This is the endpoint of the line for the ramp function.
     */
    private double valueAtFinishTime = 0;

    /**
     * This is the time required to reach the final value. Slope of the line is determined from
     * (valueAtFinishTime - valueAtStartTime) / timeToReachFinishValueInmSec
     * units are milli-seconds (1000 mSec = 1 sec)
     */
    private double timeToReachFinishValueInmSec = 0;

    /**
     * The slope of the line that forms the ramp control boundary.
     */
    private double slope = 0;

    /**
     * The y intercept of the line that forms the ramp control boundary.
     */
    private double intercept = 0;

    /**
     * If armed, the ramp is setup and can be started.
     */
    private boolean armed = false;

    /**
     * If running, the ramp has been started and is currently active.
     */
    private boolean running = false;

    /**
     * If finished, the ramp has completed.
     */
    private boolean finished = false;

    /**
     * If enabled, then that ramp can be automatically started
     */
    private boolean enabled = false;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    private void setValueAtStartTime(double valueAtStartTime) {
        this.valueAtStartTime = valueAtStartTime;
    }

    public double getValueAtStartTime() {
        return valueAtStartTime;
    }

    private void setValueAtFinishTime(double valueAtFinishTime) {
        this.valueAtFinishTime = valueAtFinishTime;
    }

    public double getValueAtFinishTime() {
        return valueAtFinishTime;
    }

    private void setTimeToReachFinishValueInmSec(double timeToReachFinishValueInmSec) {
        this.timeToReachFinishValueInmSec = timeToReachFinishValueInmSec;
    }

    public double getTimeToReachFinishValueInmSec() {
        return timeToReachFinishValueInmSec;
    }

    public boolean isArmed() {
        return armed;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isEnabled() {
        return enabled;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public RampControl(double valueAtStartTime, double valueAtFinishTime, double timeToReachFinishValueInmSec) {
        this.valueAtStartTime = valueAtStartTime;
        this.valueAtFinishTime = valueAtFinishTime;
        this.timeToReachFinishValueInmSec = timeToReachFinishValueInmSec;
        setup(valueAtStartTime, valueAtFinishTime, timeToReachFinishValueInmSec);
        timer = new ElapsedTime();
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
     * Sets up the boundary line for the ramp control. Calculates the slope and y intercept of the
     * line. Note that you can setup the line once and use it over again by calling just start().
     *
     * @param valueAtStartTime             value at time = 0.
     * @param valueAtFinishTime            value when time reaches the time limit.
     * @param timeToReachFinishValueInmSec time the ramp takes to move from start time value to
     *                                     finish time value.
     */
    public void setup(double valueAtStartTime, double valueAtFinishTime, double timeToReachFinishValueInmSec) {
        this.valueAtStartTime = valueAtStartTime;
        this.valueAtFinishTime = valueAtFinishTime;
        this.timeToReachFinishValueInmSec = timeToReachFinishValueInmSec;
        this.slope = (valueAtFinishTime - valueAtStartTime) / timeToReachFinishValueInmSec;
        this.intercept = valueAtStartTime;
        // The remp is now ready to be started
        this.armed = true;
        // Obviously it is not running yet
        this.running = false;
        // And it is not finished yet
        this.finished = false;
    }

    /**
     * Start the ramp control
     */
    public void start() {
        timer.reset();
        this.running = true;
        // The ramp is running so it is no longer armed.
        this.armed = false;
        // Since it is running it can't be finished :-)
        this.finished = false;
    }

    private void finish() {
        this.armed = false;
        this.running = false;
        this.finished = true;
        // The ramp will not be able to be run again without a start() call or specifically enabling
        // it with enable()
        this.enabled = false;
    }

    /**
     * This method combines the setup and the start methods into one method for convenience.
     *
     * @param valueAtStartTime             value at time = 0.
     * @param valueAtFinishTime            value when time reaches the time limit.
     * @param timeToReachFinishValueInmSec time the ramp takes to move from start time value to
     *                                     finish time value.
     */
    public void setupAndStart(double valueAtStartTime, double valueAtFinishTime, double timeToReachFinishValueInmSec) {
        setup(valueAtStartTime, valueAtFinishTime, timeToReachFinishValueInmSec);
        start();
    }

    /**
     * Disable the ramp control. It cannot be started automatically by a method in another class.
     * It can still be started manually by calling start()
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * Enable the ramp control. If the ramp control is enabled, it can be automatically started
     * by a method in another class. It can also be started manually by calling start().
     */
    public void enable() {
        this.enabled = true;
    }

    /**
     * Has the ramp control's timer expired?
     *
     * @return timer expired = true
     */
    public boolean isTimeExpired() {
        if (timer.milliseconds() > timeToReachFinishValueInmSec) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Pass in a value. If the ramp control is enabled and the time has not expired, check the value
     * against a line deterimed by (valueAtFinishTime - intialValue) / timeToReachFinishValueInmSec * time + valueAtStartTime
     * If the value being tested is greater than the line's value, return the line's value. In other
     * words reduce the value to the line's value. If it is less just leave the value as is.
     *
     * @param value value to test against the line
     * @return
     */
    public double getRampValueLinear(double value) {

        double valueFromRamp;

        if (this.running) {
            // The ramp is running
            if (timer.milliseconds() < timeToReachFinishValueInmSec) {
                // if timer is less than time to reach the final value (so ramp should be in effect)
                // then calculate the value given by the ramp (valueFromRamp = m * elapsed time + valueAtStartTime) (y=mx+b)
                valueFromRamp = this.slope * timer.milliseconds() + this.intercept;
                // if the value input is less than the calculated ramp value then use the input, otherwise use the ramp value
                // There was a bug here when the input was negative, abs fixed it
                if (this.slope > 0) {
                    if (valueFromRamp < value) {
                        return valueFromRamp;
                    } else {
                        return value;
                    }
                } else {
                    if (valueFromRamp < value) {
                        return value;
                    } else {
                        return valueFromRamp;
                    }
                }
            } else {
                // if the time for the ramp has expired, disable the ramp and then just return the input
                finish();
                return value;
            }
        } else {
            // The ramp is not running so just return the input
            return value;
        }

    }
}
