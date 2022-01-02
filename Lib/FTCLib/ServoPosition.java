package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.util.concurrent.TimeUnit;

public class ServoPosition {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum State {
        IDLE,
        DELAYING,
        START_MOVEMENT,
        MOVING,
        COMPLETE
    }

    private State state = State.IDLE;

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

    private void setPosition(double position) {
        Range.clip(position, -1.0, 1.0);
        this.position = position;
    }

    /**
     * An estimate of how long it will take the servo to reach this position.
     */
    public double timeToReachPosition;

    public double getTimeToReachPosition() {
        return timeToReachPosition;
    }

    private void setTimeToReachPosition(double timeToReachPosition, TimeUnit timeUnit) {
        timeToReachPosition = timeUnitInternal.convert((long)timeToReachPosition, timeUnit);
        this.timeToReachPosition = timeToReachPosition;
    }

    /**
     * A delay in the start of the movement to the position. This is optional and defaults to 0.
     */
    public double timeToDelayStart = 0;

    public double getTimeToDelayStart() {
        return timeToReachPosition;
    }

    private void setTimeToDelayStart(double timeToDelayStart, TimeUnit timeUnit) {
        timeToDelayStart = timeUnitInternal.convert((long)timeToDelayStart, timeUnit);
        this.timeToDelayStart = timeToDelayStart;
    }

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

    public ServoPosition(double position, double timeToReachPosition, TimeUnit timeUnit) {
        this.position = position;
        // convert the user supplied timeToPositon from their units to the internal units in this
        // class (milliseconds)
        setTimeToReachPosition(timeToReachPosition, timeUnit);
        setTimeToDelayStart(0.0, timeUnit);
        timer = new ElapsedTime();
        state = State.IDLE;
    }

    public ServoPosition(double position, double timeToDelayStart, double timeToReachPosition, TimeUnit timeUnit) {
        this.position = position;
        // convert the user supplied timeToPositon from their units to the internal units in this
        // class (milliseconds)
        setTimeToReachPosition(timeToReachPosition, timeUnit);
        setTimeToDelayStart(timeToDelayStart, timeUnit);
        timer = new ElapsedTime();
        state = State.IDLE;
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    @Override
    public int hashCode() {
        return Double.valueOf(position).hashCode()
                ^ Double.valueOf(timeToDelayStart).hashCode()
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
                && (pos.timeToDelayStart == timeToDelayStart)
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
        state = State.DELAYING;
    }

    /**
     * Check whether the movement has gone on for more than the specified time. If so, then we
     * call the movement complete. Note that it may or may not actually be complete. If there is
     * a bigger load on the servo than normal, it may not be complete by this time. But this is the
     * best we can do since a servo does not include position feedback. This method runs a state
     * machine so it needs to be called repeatedly in a loop.
     *
     * @return
     */
    public State update() {
//        // make sure the user started a movement so that we know the timer was reset, otherwise
//        // the timer could report a long time has elapsed but no movement was ever started. This
//        // would lead to reported a movement as complete that was never started
//        if (timer.milliseconds() > timeToReachPosition && startedMovement) {
//            startedMovement = false;
//            return true;
//        } else {
//            return false;
//        }
        switch (state) {
            case IDLE:
                //do nothing. The state will only be idle between the time the servo is initialized
                // and when the first startMoveToPosition is called.
                break;
            case DELAYING:
                if (timer.milliseconds() > timeToDelayStart) {
                    // the delay is finished, or the delay was set to 0. Reset the timer and use
                    // it to time the movement.
                    timer.reset();
                    state = State.START_MOVEMENT;
                    // now the servo should set the servo position. I could put that here, but it
                    // seems like the servo should be the object setting the position
                }
                break;
            case START_MOVEMENT:
                // this state is only used to indicate that the servo using this class should start
                // the servo movement. The assumption is that it actually will issue the command.
                state = State.MOVING;
                break;
            case MOVING:
                if (timer.milliseconds() > timeToReachPosition) {
                    state = State.COMPLETE;
                }
                break;
            case COMPLETE:
                // just hang here until a new movement is started
                break;
        }
        return state;
    }
}
