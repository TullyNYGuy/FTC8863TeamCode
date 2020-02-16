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

    private Servo servo;
    private double position;

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public double timeToReachPosition;

    public double getTimeToReachPosition() {
        return timeToReachPosition;
    }

    public void setTimeToReachPosition(double timeToReachPosition) {
        this.timeToReachPosition = timeToReachPosition;
    }

    private boolean startedMovement = false;

    private TimeUnit timeUnitInternal = TimeUnit.MILLISECONDS;
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

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void startMoveToPosition() {
        timer.reset();
        startedMovement = true;
    }

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
