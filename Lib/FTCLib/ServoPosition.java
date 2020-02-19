package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.concurrent.TimeUnit;

public class ServoPosition {

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
     * The position associated with this ServoPosition. This is the numeric value that gets sent
     * to the servo.
     */
    private double position;

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    /**
     * An estimate of how long it will take the servo to reach this position.
     */
    public double timeToReachPosition;

    public double getTimeToReachPosition() {
        return timeToReachPosition;
    }

    public void setTimeToReachPosition(double timeToReachPosition) {
        this.timeToReachPosition = timeToReachPosition;
    }

    /**
     * A flag that says whether a move to a position has been started.
     */
    private boolean startedMovement = false;

    /**
     * The internal units for time in this class.
     */
    private TimeUnit timeUnitInternal = TimeUnit.MILLISECONDS;

    /**
     * A timer for tracking time while the movement is taking place
     */
    private ElapsedTime timer;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ServoPosition(double position, long timeToReachPosition, TimeUnit timeUnit) {
        this.position = position;
        // convert the user supplied timeToPositon from their units to the internal units in this
        // class (milliseconds)
        this.timeToReachPosition = timeUnitInternal.convert(timeToReachPosition, timeUnit);
        timer = new ElapsedTime();
        startedMovement = false;
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    @Override
    public int hashCode() {
        return Double.valueOf(position).hashCode()
                ^ Double.valueOf(timeToReachPosition).hashCode()
                ^ timeUnitInternal.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;
        ServoPosition pos = (ServoPosition) o;
        return (pos.position == position)
                && (pos.timeToReachPosition == timeToReachPosition)
                && (pos.timeUnitInternal == timeUnitInternal);
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * Start a movement to a position. That started movement flag ensures that if someone calls
     * isPositionReached before a movement has been sent to the servo, that isPositionReached will
     * not return true.
     */
    public void startMoveToPosition() {
        timer.reset();
        startedMovement = true;
    }

    /**
     * Check whether the movement has gone on for more than the specified time. If so, then we
     * call the movement complete. Note that it may or may not actually be complete. If there is
     * a bigger load on the servo than normal, it may not be complete by this time. But this is the
     * best we can do since a servo does not include position feedback.
     *
     * @return
     */
    public boolean isPositionReached() {
        // make sure the user started a movement so that we know the timer was reset, otherwise
        // the timer could report a long time has elapsed but no movement was ever started. This
        // would lead to reported a movement as complete that was never started
        if (timer.milliseconds() > timeToReachPosition && startedMovement) {
            return true;
        } else {
            return false;
        }
    }
}
