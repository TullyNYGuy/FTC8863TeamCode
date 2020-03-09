package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Distance {

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

    private double distance = 0;

    public double getDistance(DistanceUnit desiredUnit) {
        return desiredUnit.fromUnit(this.unit, distance);
    }

    private DistanceUnit unit = DistanceUnit.CM;

    public DistanceUnit getUnit() {
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

    public Distance(double distance, DistanceUnit unit) {
        this.distance = distance;
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
