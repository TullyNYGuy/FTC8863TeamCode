package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class AngleAdjuster {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************


    /**
     * The IMU returns angles in a range from -180 to + 180 normally. But sometimes you may want
     * angles in the range of 0 to 360  or 0 to -360 instead. This enum allow you to pick one.
     */
    public enum AngleRange {
        PLUS_TO_MINUS_180,
        ZERO_TO_PLUS_360,
        ZERO_TO_MINUS_360
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*******************************************************************************************
    private double threshold = 0;
    private double angle = 0;
    private AngleRange angleRange = AngleRange.PLUS_TO_MINUS_180;
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
    public AngleAdjuster() {

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
    public double adjustAngle(double angle, double threshold, AngleRange angleRange) {
    return 1.1;
    }
}

