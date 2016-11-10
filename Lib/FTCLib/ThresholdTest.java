package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;

public class ThresholdTest {

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
     * Timer to be used in checking if we have been on target for long enough;
     */
    private ElapsedTime timer;

    /**
     * The time, in milliseconds, that we are supposed to at (or near) the target position before
     * we say we have arrived there and are steady there.
     */
    private double timeToBeAtThresholdInmS = 0;

    /**
     * The last position that was given
     */
    private double previousPosition = 0;

    /**
     * The position that is given now
     */
    private double currentPosition = 0;

    /**
     * The position that is supposed to be the destination
     */
    private double targetPosition = 0;

    /**
     * A position that is close enough to the destination to call it good. Like 5 away from 1120
     * is close enough
     */
    private double targetDelta = 0;

    /**
     * How many times in a row has the threshold been met?
     */
    private int numberOfThresholdsMet = 0;

    /**
     * How many times in a row must the threshold be met in order to say we are close enough to the
     * destination.
     */
    private int numberOfThresholdsToBeMet = 0;


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

    public ThresholdTest( double targetPosition, double targetDelta, double timeToBeAtThresholdInmS, int numberOfThresholdsToBeMet) {
        this.targetPosition = targetPosition;
        this.targetDelta = targetDelta;
        this.timeToBeAtThresholdInmS = timeToBeAtThresholdInmS;
        this.numberOfThresholdsToBeMet = numberOfThresholdsToBeMet;
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

    public void update(double currentPosition) {
        previousPosition = currentPosition;
    }

    private boolean isWithinRange(){
        return true;
    }

    private boolean isWithinRangeForElapsedTime() {
        return true;
    }

    private boolean isWithinRangeForNumberOfTimes() {
        return true;
    }

    private void reset(){

    }
}

