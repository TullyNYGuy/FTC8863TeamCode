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

    public enum Target {
        TARGET,
        NO_TARGET
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
    public double adjustAngle(double angle, double threshold, Target target) {
        double result = 0;
        if (target == Target.TARGET) {
            this.threshold = threshold;
            if (angle >= 0) {
                if (angle < threshold) {
                    angleRange = AngleRange.PLUS_TO_MINUS_180;
                    result = angle;

                }
                if (angle >= threshold) {
                    angleRange = AngleRange.ZERO_TO_PLUS_360;
                    result = angle;

                }
            }
            if (angle < 0) {
                if (angle < threshold) {
                    angleRange = AngleRange.ZERO_TO_MINUS_360;
                    result = angle;

                }
                if (angle >= threshold) {
                    angleRange = AngleRange.PLUS_TO_MINUS_180;
                    result = angle;
                }
            }


        }
        return result;
    }
    public double adjustAngle(double angle){
        double result = 0;
        if (Math.abs(angle) < Math.abs(threshold)){
            result = angle;

        }
        if (Math.abs(angle)>= Math.abs(threshold)){
            if(angleRange == AngleRange.ZERO_TO_PLUS_360){
                if (angle < 0){
                    result = 360 + angle;

                } else{
                    result = angle;
                }
            }
            if (angleRange == AngleRange.ZERO_TO_MINUS_360){
                if (angle < 0){
                    result = angle;

                } else{
                    result = angle - 360;
                }
            }
            if (angleRange == AngleRange.PLUS_TO_MINUS_180){
                result = angle;
            }
        }
        return result;
    }
}

