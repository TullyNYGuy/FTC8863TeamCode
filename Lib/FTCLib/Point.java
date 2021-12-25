package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Point {

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
    private double x = 0;

    public double getX(DistanceUnit unit) {
        return distanceUnit.fromUnit(unit, x);
    }

    public void setX(DistanceUnit theirUnit, double x) {
        this.x = distanceUnit.fromUnit(theirUnit, x);
    }

    private DistanceUnit distanceUnit = DistanceUnit.CM;

    public DistanceUnit getDistanceUnit() {
        return distanceUnit;
    }

    private double y = 0;

    public double getY(DistanceUnit unit) {
        return distanceUnit.fromUnit(unit, y);
    }

    public void setY(DistanceUnit theirUnit, double y) {
        this.y = distanceUnit.fromUnit(theirUnit, y);
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

    public Point(DistanceUnit distanceUnit, double x, double y) {
        this.x = x;
        this.distanceUnit = distanceUnit;
        this.y = y;
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
    public Distance getDistance(DistanceUnit distanceUnit, double x, double y) {
        double hypot = Math.hypot(x - distanceUnit.fromUnit(this.distanceUnit, this.x), y - distanceUnit.fromUnit(this.distanceUnit, this.y));
        Distance distance = new Distance(distanceUnit, hypot);
        return distance;
    }

    public double getAngle(DistanceUnit distanceUnit, double x, double y) {
        double angle = Math.atan2(y - distanceUnit.fromUnit(this.distanceUnit, this.y), x - distanceUnit.fromUnit(this.distanceUnit, this.x));
        return angle;
    }
}
