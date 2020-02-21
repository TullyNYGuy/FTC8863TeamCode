package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class ProfileFunctionTrapezoidal implements ProfileFunction {

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

    private double startValueX = 0;

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

    private double finishValueX = 0;
    private double finishValueY = 0;

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
        return ProfileType.TRAPEZOIDAL;
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

    public ProfileFunctionTrapezoidal(double startValueX, double startValueY,
                                      double rampUpFinishAtX,
                                      double flatTopValueY,
                                      double rampDownStartAtX,
                                      double finishValueX, double finishValueY) {
        this.rampUpSlope = (flatTopValueY - startValueY) / (rampUpFinishAtX - startValueX);
        this.rampUpYIntercept = startValueY;
        this.startValueX = startValueX;
        this.rampUpTransitionX = rampUpFinishAtX;
        this.flatTopValueY = flatTopValueY;
        this.rampDownSlope = (finishValueY - flatTopValueY) / (rampDownStartAtX - finishValueX);
        this.rampDownYIntercept = flatTopValueY;
        this.rampDownTransitionX = rampDownStartAtX;
        this.finishValueX = finishValueX;
        this.finishValueY = finishValueY;
    }

    public ProfileFunctionTrapezoidal ProfileFunctionTrapezoidalByPercent(double startValueX, double startValueY,
                                                                          double percentOfTotalMovementToFlatTopTransition,
                                                                          double flatTopValueY,
                                                                          double percenOfTotalMovementToRampDownTransition,
                                                                          double finishValueX, double finishValueY) {
        double rampUpFinishAtX = (finishValueX - startValueX) * percentOfTotalMovementToFlatTopTransition / 100;
        double rampDownStartAtX = (finishValueX - startValueX) * percenOfTotalMovementToRampDownTransition / 100;
        return new ProfileFunctionTrapezoidal(startValueX, startValueY, rampUpFinishAtX, flatTopValueY, rampDownStartAtX, finishValueX, finishValueY);
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
     * to a finishing value. The starting value and finishing value don't have to be the same
     * although they could be.
     *
     * @param xValue
     * @return the Y value for the function
     */
    @Override
    public double getYValue(double xValue) {
        double yValue = 0;
        if (xValue < startValueX) {
            yValue = getRampUpYIntercept();
            isFinished = false;
        }
        if (xValue >= startValueX && xValue < rampUpTransitionX) {
            yValue = rampUpSlope * xValue + rampUpYIntercept;
            isFinished = false;
        }
        if (xValue >= rampUpTransitionX && xValue < rampDownTransitionX) {
            yValue = flatTopValueY;
            isFinished = false;
        }
        if (xValue >= rampDownTransitionX && xValue < finishValueX) {
            yValue = rampDownSlope * xValue + rampDownYIntercept;
            isFinished = false;
        }
        if (xValue >= finishValueX) {
            yValue = finishValueY;
            isFinished = true;
        }
        return yValue;
    }
}
