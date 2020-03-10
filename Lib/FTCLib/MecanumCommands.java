package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class MecanumCommands {

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

    public double getAngleOfTranslation(AngleUnit angleUnit) {
        return angleUnit.fromRadians(angleOfTranslation);
    }

    public double getAngleOfTranslationGyro() {
        double translationAngleGyro = AngleUnit.DEGREES.fromRadians(angleOfTranslation) - 90.0;
        if (translationAngleGyro < -180) {
            translationAngleGyro += 360;
        }
        return translationAngleGyro;
    }

    public void setAngleOfTranslation(AngleUnit angleUnit, double angleOfTranslation) {
        this.angleOfTranslation = angleUnit.toRadians(angleOfTranslation);
    }

    public void setAngleOfTranslation(Angle angleOfTranslation) {
        this.setAngleOfTranslation(angleOfTranslation.getUnit(), angleOfTranslation.getAngle(angleOfTranslation.getUnit()));
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

    public MecanumCommands(double speed, double angleOfTranslation, double speedOfRotation) {
        this.speed = speed;
        this.angleOfTranslation = angleOfTranslation;
        this.speedOfRotation = speedOfRotation;
    }

    public MecanumCommands() {
        this.speed = 0;
        this.angleOfTranslation = 0;
        this.speedOfRotation = 0;
    }

//*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    @Override
    public String toString() {
        return String.format("SP: %.2f TR: %.2f ROT: %.2f", speed, AngleUnit.DEGREES.fromRadians(angleOfTranslation), speedOfRotation);
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
}
