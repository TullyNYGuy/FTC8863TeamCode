package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * This class contains an angle and its associated unites (radians for degrees).
 * Through the AngleUnit class it can convert between the two units.
 * An angle of 0 can be defined to be at one of 4 of the axes. The four axes are what we think of as
 * +x +y -x and -y. Most commonly, in our robots, 0 degrees is the + y axis. By comparison most of
 * us think of +x as the 0 axis.
 * <p>
 * +y
 * |
 * |
 * -x <----------|------------> +x
 * |
 * |
 * -y
 * <p>
 * This class translates between different reference axes.
 */
public class Angle {

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

    private double angle = 0;

    public double getAngle(AngleUnit desiredUnit) {
        return desiredUnit.fromUnit(this.unit, this.angle);
    }

    public void setAngle(double angle, AngleUnit unit) {
        this.angle = angle;
        this.unit = unit;
    }

    private AngleUnit unit = AngleUnit.RADIANS;

    public AngleUnit getUnit() {
        return unit;
    }

    private AngleReference angleReference = AngleReference.PLUSY;

    public AngleReference getAngleReference() {
        return angleReference;
    }

    public AngleRange angleRange = AngleRange.PLUS_TO_MINUS_180;

    public AngleRange getAngleRange() {
        return angleRange;
    }

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

    public Angle(double angle, AngleUnit unit) {
        this.angle = angle;
        this.unit = unit;
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
