package org.firstinspires.ftc.teamcode.Lib.DecodeLib;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class DecodeTarget {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Pose2D targetPose;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public DecodeTarget(Pose2D targetPose) {
        this.targetPose = targetPose;
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
    public double getBearingToTarget(Pose2D robotPose, AngleUnit unit) {
        double bearingInFieldCoordinates = Math.atan2(targetPose.getY(DistanceUnit.MM) - robotPose.getY(DistanceUnit.MM),
                targetPose.getX(DistanceUnit.MM) - robotPose.getX(DistanceUnit.MM));
        double bearingInRobotCoordinates = bearingInFieldCoordinates - robotPose.getHeading(AngleUnit.RADIANS);
        return unit.fromUnit(AngleUnit.RADIANS, bearingInRobotCoordinates);
    }

    public double getRangeToTarget(Pose2D robotPose, DistanceUnit unit) {
        double range = Math.sqrt(Math.pow(targetPose.getX(DistanceUnit.MM) - robotPose.getX(DistanceUnit.MM), 2) +
                Math.pow(targetPose.getY(DistanceUnit.MM) - robotPose.getY(DistanceUnit.MM), 2));
        return unit.fromUnit(DistanceUnit.MM, range);
    }


}
