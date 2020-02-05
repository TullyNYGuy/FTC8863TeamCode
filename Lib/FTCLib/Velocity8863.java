package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import java.util.concurrent.TimeUnit;

public class Velocity8863 extends Velocity {

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

    protected double averageVelocity = 0;

    public double getAverageVelocity() {
        return averageVelocity;
    }

    // default units for a Velocity8863 is seconds
    protected TimeUnit timeUnit = TimeUnit.SECONDS;

    protected Position startPosition;
    protected Position endPosition;

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

    public Velocity8863() {
        super();
    }

    public Velocity8863(DistanceUnit distanceUnit) {
        super();
        this.unit = distanceUnit;
    }

    /**
     * Create a Velocity8863 object
     *
     * @param distanceUnit    - units of the velocity parameters
     * @param xVeloc          - x velocity
     * @param yVeloc          - y velocity
     * @param zVeloc          - z velocity
     * @param acquisitionTime - time of acquisition of the velocities in System.nanoTime()
     */
    public Velocity8863(DistanceUnit distanceUnit, double xVeloc, double yVeloc, double zVeloc, long acquisitionTime) {
        super(distanceUnit, xVeloc, yVeloc, zVeloc, acquisitionTime);
        this.averageVelocity = calculateAverageVelocity(xVeloc, yVeloc, zVeloc);
    }

    /**
     * Create a Velocity8863 object
     *
     * @param distanceUnit  - the desired units of distance for this velocity object (ie meter / sec,
     *                      cm / sec, etc)
     * @param startPosition - Position object representing the start position for this velocity
     * @param endPosition   - Position object representing the end position for this velocity
     */
    public Velocity8863(DistanceUnit distanceUnit, Position startPosition, Position endPosition) {
        super();
        this.unit = distanceUnit;
        calculateVelocityComponents(distanceUnit, startPosition, endPosition);
    }

    protected void calculateVelocityComponents(DistanceUnit distanceUnit, Position startPosition, Position endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        // convert difference of acquisition times to seconds. Position acquistion time units is
        // nanoseconds by definition of the Position class.
        // Note that Position has the distance units defined in the object
        long timeDelta = this.timeUnit.convert(endPosition.acquisitionTime - startPosition.acquisitionTime, TimeUnit.NANOSECONDS);
        if (timeDelta == 0) {
            xVeloc = 0;
            yVeloc = 0;
            zVeloc = 0;
            averageVelocity = 0;
        } else {
            // calculate velocity, making sure that units are the same in the position data
            xVeloc = (distanceUnit.fromUnit(endPosition.unit, endPosition.x) - distanceUnit.fromUnit(startPosition.unit, startPosition.x)) / timeDelta;
            yVeloc = (distanceUnit.fromUnit(endPosition.unit, endPosition.y) - distanceUnit.fromUnit(startPosition.unit, startPosition.y)) / timeDelta;
            ;
            zVeloc = (distanceUnit.fromUnit(endPosition.unit, endPosition.z) - distanceUnit.fromUnit(startPosition.unit, startPosition.z)) / timeDelta;
            ;
            averageVelocity = calculateAverageVelocity(xVeloc, yVeloc, zVeloc);
        }
        acquisitionTime = endPosition.acquisitionTime;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    public double calculateAverageVelocity(double xVeloc, double yVeloc, double zVeloc) {
        return Math.sqrt(xVeloc * xVeloc + yVeloc * yVeloc + zVeloc * zVeloc);
    }


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

}

