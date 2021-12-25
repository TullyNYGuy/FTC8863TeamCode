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

    public double getDistance(DistanceUnit unit) {
        return distanceUnit.fromUnit(unit, distance);
    }

    public void setDistance(DistanceUnit theirUnit, double distance) {
        this.distance = distanceUnit.fromUnit(theirUnit, distance);
    }

    private DistanceUnit distanceUnit = DistanceUnit.CM;

    public DistanceUnit getDistanceUnit() {
        return distanceUnit;
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
    public Distance(DistanceUnit distanceUnit, double distance) {
        this.distanceUnit = distanceUnit;
        this.distance = distance;
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
