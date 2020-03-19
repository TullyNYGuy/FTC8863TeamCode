package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Pose {

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

    private Point position;

    public Point getPosition() {
        return position;
    }

    public double getXLocation() {
        return this.position.getX();
    }

    public void setXLocation(DistanceUnit distanceUnit, double xLocation) {
        this.position.setX(xLocation, distanceUnit);
    }

    public double getYLocation() {
        return this.position.getY();
    }

    public void setYLocation(DistanceUnit distanceUnit, double yLocation) {
        this.position.setY(yLocation, distanceUnit);
    }

    private Angle orientation;

    public Angle getOrientation() {
        return orientation;
    }

    public double getOrientation(AngleUnit desiredAngleUnit) {
        return this.orientation.getAngle(desiredAngleUnit);
    }

    public void setOrientation(AngleUnit angleUnit, double orientation) {
        this.orientation.setAngle(orientation, angleUnit);
    }

    public AngleUnit getAngleUnit() {
        return orientation.getUnit();
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

    public Pose(DistanceUnit distanceUnit, double XPosition, double YPosition, AngleUnit angleUnit, double orientation) {
        this.position = new Point(XPosition, YPosition, distanceUnit);
        this.orientation = new Angle(orientation, angleUnit);
    }

    public Pose() {
        this(DistanceUnit.INCH, 0, 0, AngleUnit.RADIANS, 0);
    }

    public Pose(Point position, Angle angle) {
        this.position = position;
        this.orientation = angle;
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

    public Angle headingTo(Pose otherPose) {
        return position.angleTo(otherPose.position);
    }

    public Distance distanceTo(Pose otherPose) {
        return position.distanceTo(otherPose.position);
    }

}