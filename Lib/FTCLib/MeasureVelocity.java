package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

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

    protected Velocity velocity;

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

    public void startMeasure(double startDistance, DistanceUnit unit) {
        // convert distance from the input units to this classes internal units
        this.startDistance = distanceUnit.fromUnit(unit, startDistance);
        timer.reset();
    }

    public void stopMeasure(double endDistance, DistanceUnit unit) {
        // convert distance from the input units to this classes internal units
        this.endDistance = distanceUnit.fromUnit(unit, endDistance);
        acquistionTime = timer.seconds();
        // velocity will be in units of this classes distance units / second
        averageVelocity = (endDistance - startDistance) / acquistionTime;
        velocity = new Velocity(this.distanceUnit, averageVelocity, 0.0, 0.0, (long) acquistionTime);
    }
}

