package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


public class UltimateGoalGoal {
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
    private double height = 0;
    private Pose2d pose2d;
    private DistanceUnit units;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getHeight(DistanceUnit desiredUnits) {
        return desiredUnits.fromUnit(this.units, height);
    }

    public double getX(DistanceUnit desiredUnits) {
        return desiredUnits.fromUnit(this.units, pose2d.getX());
    }

    public double getY(DistanceUnit desiredUnits) {
        return desiredUnits.fromUnit(this.units, pose2d.getY());
    }
    public Pose2d getPose2d() {
        return pose2d;
    }

    public DistanceUnit getUnits() {
        return units;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public UltimateGoalGoal(DistanceUnit units, double height, Pose2d pose2d) {
        // a pose2D is in units of inches so we will store the data in inches
        this.units = DistanceUnit.INCH;
        this.height = this.units.fromUnit(units, height);
        this.pose2d = pose2d;
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
