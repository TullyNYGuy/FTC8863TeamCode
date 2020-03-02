package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Orientation2D {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum ReferenceAxis {
        EAST,
        NORTH
    }


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private double angle = 0;

    public double getAngle(AngleUnit desiredUnit) {
        return desiredUnit.fromUnit(unit, this.angle);
    }

    public void setAngle(double angle, AngleUnit unit) {
        this.angle = angle;
        this.unit = unit;
    }

    private AngleUnit unit = AngleUnit.RADIANS;

    public AngleUnit getUnit() {
        return unit;
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

    public Orientation2D(double angle, AngleUnit unit) {
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
