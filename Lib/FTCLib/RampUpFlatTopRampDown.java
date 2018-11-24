package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class RampUpFlatTopRampDown implements ProfileFunction{

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

    // variables associated with ramp up

    /**
     * y intercept of the line up (b in y = mx + b)
     */
    private double rampUpYIntercept = 0;

    public double getRampUpYIntercept() {
        return rampUpYIntercept;
    }

    /**
     * slope of the line up (m in y = mx + b)
     */
    private double rampUpSlope = 0;

    public double getRampUpSlope() {
        return rampUpSlope;
    }

    private double rampUpStartValueX = 0;

    /**
     * point at which the ramp up transitions to the next phase of the function
     */
    private double rampUpTransitionX = 0;

    public double getRampUpTransitionX() {
        return rampUpTransitionX;
    }

    // variables associated with ramp down

    /**
     * y intercept of the line down (b in y = mx + b)
     */
    private double rampDownYIntercept = 0;

    public double getRampDownYIntercept() {
        return rampDownYIntercept;
    }

    /**
     * slope of the line down (m in y = mx + b)
     */
    private double rampDownSlope = 0;

    public double getRampDownRampUpFlatTopRampDownSlope() {
        return rampUpSlope;
    }

    /**
     * point at which the ramp up transitions to the next phase of the function
     */
    private double rampDownTransitionX = 0;

    public double getRampDownTransitionX() {
        return rampUpTransitionX;
    }

    private double rampDownFinishValueX = 0;
    private double rampDownFinishValueY = 0;

    // values for a flat top function

    private double flatTopValueY = 0;

    private boolean isFinished = false;


    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    @Override
    public ProfileType getProfileType() {
        return ProfileType.RAMP_UP_DOWN_FLAT_TOP;
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public RampUpFlatTopRampDown(double rampUpStartValueX, double rampUpStartValueY,
                                 double rampUpFinishValueX, double rampUpFinishValueY,
                                 double flatTopValueY,
                                 double rampDownStartValueX, double rampDownStartValueY,
                                 double rampDownFinishValueX, double rampDownFinishValueY) {
        this.rampUpSlope = (rampUpFinishValueY - rampUpStartValueY)/ (rampUpFinishValueX - rampUpStartValueX);
        this.rampUpYIntercept = rampUpStartValueY;
        this.rampUpStartValueX = rampUpStartValueX;
        this.rampUpTransitionX = rampUpFinishValueX;
        this.flatTopValueY = flatTopValueY;
        this.rampDownSlope = (rampDownFinishValueY - rampDownStartValueY) / (rampDownStartValueX - rampDownFinishValueX);
        this.rampDownYIntercept = rampDownStartValueY;
        this.rampDownTransitionX = rampDownStartValueX;
        this.rampDownFinishValueX = rampDownFinishValueX;
        this.rampDownFinishValueY = rampDownFinishValueY;
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


    //
    //
    //          --------
    //        /         \
    //       /           \
    //      /             \
    // ----                \
    //                      \
    //                       -----

    /**
     * Implements a function that ramps up from a starting value to a maximum and then back down
     * to a finishing value.
     * @param xValue
     * @return the Y value for the function
     */
    @Override
    public double getYValue(double xValue) {
        double yValue = 0;
        if (xValue < rampUpStartValueX) {
            yValue = getRampUpYIntercept();
            isFinished = false;
        }
        if (xValue >= rampUpStartValueX && xValue < rampUpTransitionX) {
            yValue = rampUpSlope * xValue + rampUpYIntercept;
            isFinished = false;
        }
        if (xValue >= rampUpTransitionX && xValue < rampDownTransitionX) {
            yValue = flatTopValueY;
            isFinished = false;
        }
        if (xValue >= rampDownTransitionX && xValue < rampDownFinishValueX) {
            yValue = rampDownSlope * xValue + rampDownYIntercept;
            isFinished = false;
        }
        if (xValue >= rampDownFinishValueX) {
            yValue = rampDownFinishValueY;
            isFinished = true;
        }
        return yValue;
    }
}
