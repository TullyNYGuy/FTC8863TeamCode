package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class MecanumData {

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
private double speed;
    private double angleOfTranslation;
    private double speedOfRotation;


    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAngleOfTranslation() {
        return angleOfTranslation;
    }

    public void setAngleOfTranslation(double angleOfTranslation) {
        this.angleOfTranslation = angleOfTranslation;
    }

    public double getSpeedOfRotation() {
        return speedOfRotation;
    }

    public void setSpeedOfRotation(double speedOfRotation) {
        this.speedOfRotation = speedOfRotation;
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public MecanumData(double speed, double angleOfTranslation, double speedOfRotation) {
        this.speed = speed;
        this.angleOfTranslation = angleOfTranslation;
        this.speedOfRotation = speedOfRotation;
    }

    public MecanumData() {
        this.speed = 0;
        this.angleOfTranslation = 0;
        this.speedOfRotation = 0;
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
}
