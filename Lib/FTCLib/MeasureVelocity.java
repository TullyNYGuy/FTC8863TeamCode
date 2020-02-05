package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

import java.util.concurrent.TimeUnit;

public class MeasureVelocity {

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

    protected double startTime = 0;

    public double getStartTime(TimeUnit unit) {
        TimeUnit returnTime = unit;
        return returnTime.convert((long) startTime, this.timeUnit);
    }

    protected double acquistionTime = 0;

    public double getAcquistionTime(TimeUnit unit) {
        TimeUnit returnTime = unit;
        return returnTime.convert((long) acquistionTime, this.timeUnit);
    }


    protected double startDistance = 0;

    public double getStartDistance(DistanceUnit desiredDistanceUnit) {
        return distanceUnit.fromUnit(desiredDistanceUnit, startDistance);
    }

    protected double endDistance = 0;

    public double getEndDistance(DistanceUnit desiredDistanceUnit) {
        return distanceUnit.fromUnit(desiredDistanceUnit, endDistance);
    }

    protected Position startPosition;
    protected Position endPosition;
    protected Velocity8863 velocity;

    protected double averageVelocity = 0;

    public double getGetAverageVelocity(DistanceUnit desiredDistanceUnit) {
        return distanceUnit.fromUnit(desiredDistanceUnit, averageVelocity);
    }

    protected DistanceUnit distanceUnit;

    protected TimeUnit timeUnit;

    protected ElapsedTime timer;

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

    /**
     * Create a
     */
    public MeasureVelocity() {
        this.distanceUnit = DistanceUnit.CM;
        this.timeUnit = TimeUnit.MILLISECONDS;
        timer = new ElapsedTime();
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
     * Starting position for the distance measurement that is to be used in calculating the velocity.
     *
     * @param distanceUnit   - units of the positions that are passed in as parameters
     * @param startPositionX
     * @param startPositionY
     * @param startPositionZ
     */
    public void startMeasure(DistanceUnit distanceUnit, double startPositionX, double startPositionY, double startPositionZ) {
        // convert distance from the input units to this classes internal units
        this.distanceUnit = distanceUnit;
        startPosition = new Position(distanceUnit, startPositionX, startPositionY, startPositionZ, System.nanoTime());
    }

    /**
     * @param distanceUnit
     * @param endPositionX
     * @param endPositionY
     * @param endPositionZ
     */
    public void stopMeasure(DistanceUnit distanceUnit, double endPositionX, double endPositionY, double endPositionZ) {
        endPosition = new Position(distanceUnit, endPositionX, endPositionY, endPositionZ, System.nanoTime());
        // velocity will be in units of this class's distance units / second
        velocity = new Velocity8863(this.distanceUnit, endPosition, startPosition);
    }
}

