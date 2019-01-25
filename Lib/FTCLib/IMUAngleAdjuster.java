package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class IMUAngleAdjuster {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    /**
     * The IMU returns angles in a range from -180 to + 180 normally. But sometimes you may want
     * angles in the range of 0 to 360 instead. This enum allow you to pick one or the other.
     */
    public enum AngleRange {
        PLUS_TO_MINUS_180,
        ZERO_TO_360,
        ZERO_TO_MINUS_360
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private double triggerAngle = 0;

    private AngleRange angleRange = AngleRange.PLUS_TO_MINUS_180;


    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getTriggerAngle() {
        return triggerAngle;
    }

    public void setTriggerAngle(double triggerAngle) {
        this.triggerAngle = triggerAngle;
    }

    public AngleRange getAngleRange() {
        return angleRange;
    }

    public void setAngleRange(AngleRange angleRange) {
        this.angleRange = angleRange;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************


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

    public double getAdjustedAngle(double angle) {
        // check the mode (0 to +360, 0 to 360, -180 to 180)
        // check when the adjustement should triggered
        if (angle > triggerAngle) {

        }
        return 1.0;
    }
}
