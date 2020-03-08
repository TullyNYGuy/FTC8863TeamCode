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

    //*********************************************************************************************
    // The physical transition points that define the function
    //*********************************************************************************************

    /**
     * The point at which the function starts.
     */
    private double startValueX = 0;

    public double getStartValueX() {
        return startValueX;
    }

    public void setStartValueX(double startValueX) {
        this.startValueX = startValueX;
        calculateMathFunctions();
    }

    /**
     * The starting value of the function.
     */
    private double startValueY = 0;

    public double getStartValueY() {
        return startValueY;
    }

    public void setStartValueY(double startValueY) {
        this.startValueY = startValueY;
        calculateMathFunctions();
    }

    /**
     * Point at which the ramp up transitions to the maximum value
     */
    private double rampUpFinishAtX = 0;

    public double getrampUpFinishAtX() {
        return rampUpFinishAtX;
    }

    public void setrampUpFinishAtX(double rampUpFinishAtX) {
        this.rampUpFinishAtX = rampUpFinishAtX;
        calculateMathFunctions();
    }

    /**
     * maximum value of the profile
     */
    private double maximumY = 0;

    public double getmaximumY() {
        return maximumY;
    }

    public void setmaximumY(double maximumY) {
        this.maximumY = maximumY;
        calculateMathFunctions();
    }

    /**
     * point at which the function transitions to ramping down
     */
    private double rampDownStartAtX = 0;

    public double getrampDownStartAtX() {
        return rampDownStartAtX;
    }

    public void setrampDownStartAtX(double rampDownStartAtX) {
        this.rampDownStartAtX = rampDownStartAtX;
        calculateMathFunctions();
    }

    /**
     * Point at which the function stops ramping down
     */
    private double finishValueX = 0;

    public double getFinishValueX() {
        return finishValueX;
    }

    public void setFinishValueX(double finishValueX) {
        this.finishValueX = finishValueX;
        calculateMathFunctions();
    }

    /**
     * The final value that is held after the ramp down is finished.
     */
    private double finishValueY = 0;

    public double getFinishValueY() {
        return finishValueY;
    }

    public void setFinishValueY(double finishValueY) {
        this.finishValueY = finishValueY;
        calculateMathFunctions();
    }

    //*********************************************************************************************
    // The math values associated with the function. These are calculated from the physical points.
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

    private boolean isFinished = false;

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


    public ProfileFunctionTrapezoidal(
            double startValueX, double startValueY,
            double rampUpFinishAtX,
            double maximumY,
            double rampDownStartAtX,
            double finishValueX, double finishValueY) {
        this.startValueX = startValueX;
        this.startValueY = startValueY;
        this.rampUpFinishAtX = rampUpFinishAtX;
        this.maximumY = maximumY;
        this.rampDownStartAtX = rampDownStartAtX;
        this.finishValueX = finishValueX;
        this.finishValueY = finishValueY;
        calculateMathFunctions();
    }

    public ProfileFunctionTrapezoidal ProfileFunctionTrapezoidalByPercent(double startValueX, double startValueY,
                                                                          double percentOfTotalMovementToFlatTopTransition,
                                                                          double maximumY,
                                                                          double percenOfTotalMovementToRampDownTransition,
                                                                          double finishValueX, double finishValueY) {
        double rampUpFinishAtX = (finishValueX - startValueX) * percentOfTotalMovementToFlatTopTransition / 100;
        double rampDownStartAtX = (finishValueX - startValueX) * percenOfTotalMovementToRampDownTransition / 100;
        return new ProfileFunctionTrapezoidal(startValueX, startValueY, rampUpFinishAtX, maximumY, rampDownStartAtX, finishValueX, finishValueY);
    }

    private void calculateMathFunctions() {
        this.rampUpSlope = (maximumY - startValueY) / (rampUpFinishAtX - startValueX);
        this.rampUpYIntercept = startValueY;
        this.rampDownSlope = (finishValueY - maximumY) / (rampDownStartAtX - finishValueX);
        this.rampDownYIntercept = maximumY;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    @Override
    public ProfileType getProfileType() {
        return ProfileType.TRAPEZOIDAL;
    }

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
        if (xValue >= startValueX && xValue < rampUpFinishAtX) {
            yValue = rampUpSlope * xValue + rampUpYIntercept;
            isFinished = false;
        }
        if (xValue >= rampUpFinishAtX && xValue < rampDownStartAtX) {
            yValue = maximumY;
            isFinished = false;
        }
        if (xValue >= rampDownStartAtX && xValue < finishValueX) {
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
