package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

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
    private double x;

    public double getX() {
        return x;
    }

    public void setX(double x, DistanceUnit unit) {
        this.x = unit.fromUnit(unit, x);
    }

    private double y;

    public double getY() {
        return y;
    }

    public void setY(double y, DistanceUnit unit) {
        this.y = unit.fromUnit(unit, y);
    }

    private DistanceUnit unit;

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

    public Point(double x, double y, DistanceUnit unit) {
        this.x = x;
        this.y = y;
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

    /**
     * Return the distance from this Point to another Point. Distance is returned in the units of
     * the this Point, not the units of the other Point.
     *
     * @param otherPoint
     * @return
     */
    public double distanceTo(Point otherPoint) {
        return distanceTo(otherPoint.getX(), otherPoint.getY(), otherPoint.getUnit());
    }

    /**
     * Return the distance from this Point to another point, which is defined by its x and y
     * coordinates and its unit of measure. Distance is returned in the units of this Point,
     * not the units of the other point.
     *
     * @param otherX
     * @param otherY
     * @param otherUnit
     * @return
     */
    public double distanceTo(double otherX, double otherY, DistanceUnit otherUnit) {
        // figure out the distance between the points. Make sure the units are the same for the
        // distance calculations.
        double xDifference = this.unit.fromUnit(otherUnit, otherX) - this.x;
        double yDifference = this.unit.fromUnit(otherUnit, otherY) - this.y;
        ;
        return Math.hypot(xDifference, yDifference);
    }

    /**
     * Return the angle from this Point to another Point. The 0 for
     * the angle return is NOT the normal X axis. 0 is defined by the robot coordinate system, which
     * is the Y axis.
     *
     * @param - otherPoint
     * @return - angle to the other point in radians
     */
    public double angleTo(Point otherPoint) {
        return angleTo(otherPoint.getX(), otherPoint.getY(), otherPoint.getUnit());
    }

    /**
     * Return the angle from this Point to another point defined by its x and y location. The 0 for
     * the angle return is NOT the normal X axis. 0 is defined by the robot coordinate system, which
     * is the Y axis.
     *
     * @param x    - x coordinate of the other point
     * @param y    - y coordinate of the other point
     * @param unit - units of the other point's coordinates
     * @return - angle to the other point in radians
     */
    public double angleTo(double x, double y, DistanceUnit unit) {
        // figure out the distance between the points. Make sure the units are the same for the
        // distance calculations.
        double xDifference = this.unit.fromUnit(unit, x) - this.x;
        double yDifference = this.unit.fromUnit(unit, y) - this.y;

        // notice that I'm feeding the X value in for the Y parameter and the Y value in for the X
        // parameter. This is because 0 degrees for the robot is not to the right axis like normal.
        // 0 degrees to the robot is north, where 90 is normally.
        // normal atan2 arguments are (y, x)
        double angleToInRadians = Math.atan2(xDifference, yDifference);
        return angleToInRadians;
    }

    /**
     * Return the angle from another Point to this Point. The angle is from the point of view of the
     * other point! The 0 for the angle return is NOT the normal X axis. 0 is defined by the robot
     * coordinate system, which is the Y axis.
     *
     * @param otherPoint
     * @return
     */
    public double angleFrom(Point otherPoint) {
        return angleFrom(otherPoint.getX(), otherPoint.getY(), otherPoint.getUnit());
    }

    /**
     * Return the angle from another Point to this Point. The angle is from the point of view of the
     * other point! The other point is defined by its x and y coordinate and its unit of measure.
     * The 0 for the angle return is NOT the normal X axis. 0 is defined by the robot
     * coordinate system, which is the Y axis.
     *
     * @param x    - x coordinate of the other point
     * @param y    - y coordinate of the other point
     * @param unit - units of the other point's coordinates
     * @return - angle from the other point to this point in radians
     */
    public double angleFrom(double x, double y, DistanceUnit unit) {
        return Math.PI + angleTo(x, y, unit);
    }
}
