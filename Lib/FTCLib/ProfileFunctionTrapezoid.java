package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class ProfileFunctionTrapezoid implements ProfileFunction {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    // can be accessed only by this class, or by using th
    //    //e public
    // getter and setter methods
    //*********************************************************************************************
    private double rampUpStartPoint;

    public double getRampUpStartPoint() {
        return rampUpStartPoint;
    }

//    public void setRampUpStartPoint(double rampUpStartPoint) {
//        this.rampUpStartPoint = rampUpStartPoint;
//    }

    private double rampUpEndPoint;

    public double getRampUpEndPoint() {
        return rampUpEndPoint;
    }

//    public void setRampUpEndPoint(double rampUpEndPoint) {
//        this.rampUpEndPoint = rampUpEndPoint;
//    }

    private double rampDownStartPoint;

    public double getRampDownStartPoint() {
        return rampDownStartPoint;
    }

//    public void setRampDownStartPoint(double rampDownStartPoint) {
//        this.rampDownStartPoint = rampDownStartPoint;
//    }

    private double rampDownEndPoint;

    public double getRampDownEndPoint() {
        return rampDownEndPoint;
    }

//    public void setRampDownEndPoint(double rampDownEndPoint) {
//        this.rampDownEndPoint = rampDownEndPoint;
//    }

    private double maxYValue;

    public double getMaxYValue() {
        return maxYValue;
    }

//    public void setMaxYValue(double maxYValue) {
//        this.maxYValue = maxYValue;
//    }

    private double startingYValue;

    public double getStartingYValue() {
        return endingYValue;
    }

//    public void setStartingYValue(double endingYValue) {
//        this.endingYValue = endingYValue;
//    }

    private double endingYValue;

    public double getEndingYValue() {
        return endingYValue;
    }

//    public void setEndingYValue(double endingYValue) {
//        this.endingYValue = endingYValue;
//    }

    private double startPoint;

    public double getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(double startPoint) {
        this.startPoint = startPoint;
    }

    private double endPoint;


    public double getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(double endPoint) {
        this.endPoint = endPoint;
    }

    private double rampUpMValue;

    private double rampDownMValue;

    private double rampUpBValue;

    private double rampDownBValue;
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

    public ProfileFunctionTrapezoid(double rampUpStartPoint, double rampUpEndPoint,
                                    double rampDownStartPoint, double rampDownEndPoint,
                                    double maxYValue, double startingYValue, double endingYValue,
                                    double startPoint, double endPoint) {
        this.rampUpStartPoint = rampUpStartPoint;
        this.rampUpEndPoint = rampUpEndPoint;
        this.rampDownStartPoint = rampDownStartPoint;
        this.rampDownEndPoint = rampDownEndPoint;
        this.maxYValue = maxYValue;
        this.startingYValue = startingYValue;
        this.endingYValue = endingYValue;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        //calculations for line formulas////
        rampUpMValue = calculateMValue(maxYValue, startingYValue, rampUpEndPoint, rampUpStartPoint);
        rampDownMValue = calculateMValue(maxYValue, endingYValue, rampDownEndPoint, rampDownStartPoint);

    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    public double calculateMValue(double endingYValue, double startingYValue, double endingXValue, double startingXValue) {
        if (endingXValue - startingXValue == 0) {
            return 1000000000000.0;
        }
        return (endingYValue - startingYValue) / (endingXValue - startingXValue);
    }
    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    @Override
    public ProfileType getProfileType() {
        return ProfileType.TRAPEZOIDAL;
    }

    @Override
    public boolean isFinished(double xValue) {
        if (xValue > endPoint) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public double getYValue(double xValue) {
        return 0;
    }
}
