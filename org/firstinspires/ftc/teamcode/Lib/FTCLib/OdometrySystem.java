package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import android.icu.math.MathContext;

public class OdometrySystem {
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

    private OdometryModule left;
    private OdometryModule right;
    private OdometryModule back;
    private double leftEncoderValue;

    private double rightEncoderValue;

    private double backEncoderValue;

    private double distanceFromCenterToSideOdometryModule;

    private double distanceFromCenterToBackOdometryModule;

    private double angleOfRotation;

    private double leftEncoderValueRevised;

    private double rightEncoderValueRevised;

    private double backEncoderValueRevised;

    private double averageLREncoderValue;

    private double angleOfTranslation;

    private double lengthOfTranslation;

    private double currentX;
    private double currentY;
    private double currentRotation;
    OdometryModule.Units unit;

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
    public OdometrySystem(OdometryModule.Units unit, OdometryModule left, OdometryModule right, OdometryModule back) {
        this.left = left;
        this.right = right;
        this.back = back;
        this.unit = unit;

    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private void findAngleOfRotation() {
        //this will be used to alter the encoder values to provide info about the straight translation
        angleOfRotation = (leftEncoderValue - rightEncoderValue) / (2.0 * distanceFromCenterToSideOdometryModule);
    }

    public void cancelOutAngleFromMovement() {
        leftEncoderValueRevised = leftEncoderValue - distanceFromCenterToSideOdometryModule * angleOfRotation / 2.0;
        rightEncoderValueRevised = rightEncoderValue - distanceFromCenterToSideOdometryModule * angleOfRotation / 2.0;
        backEncoderValueRevised = backEncoderValue - distanceFromCenterToBackOdometryModule * angleOfRotation / 2.0;
    }

    public void caluclateMoveDistance() {
        leftEncoderValue = left.getDistanceSinceLastChange(unit);
        rightEncoderValue = right.getDistanceSinceLastChange(unit);
        backEncoderValue = back.getDistanceSinceLastChange(unit);
        findAngleOfRotation();
        cancelOutAngleFromMovement();


        averageLREncoderValue = (leftEncoderValueRevised + rightEncoderValueRevised) / 2;

        lengthOfTranslation = Math.sqrt(averageLREncoderValue * averageLREncoderValue + backEncoderValueRevised * backEncoderValueRevised);
        angleOfTranslation = Math.atan2(backEncoderValueRevised, averageLREncoderValue);
    }

    public void getMovement(MecanumData data) {
        data.setAngleOfTranslation(angleOfTranslation);
        data.setSpeed(lengthOfTranslation);
        data.setSpeedOfRotation(angleOfRotation);
    }

    public void resetCoordinates() {
        currentX = 0.0;
        currentY = 0.0;
        currentRotation = 0.0;

    }

    public void setCoordinates() {
        currentRotation = angleOfRotation;
        currentX = averageLREncoderValue;
        currentY = backEncoderValueRevised;

    }

    public void setCoordinates(double rotation, double x, double y) {
        currentRotation = rotation;
        currentX = x;
        currentY = y;
    }

    public void updateCoordinates() {
        currentRotation = currentRotation + angleOfRotation;
        currentY = currentY + backEncoderValueRevised;
        currentX = currentX + averageLREncoderValue;
    }

    public double getCurrentY() {
        return currentY;
    }

    public double getCurrentX() {
        return currentX;
    }

    public double getCurrentRotation() {
        return currentRotation;
    }
    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

}
