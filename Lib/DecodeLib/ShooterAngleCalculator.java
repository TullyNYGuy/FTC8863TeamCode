package org.firstinspires.ftc.teamcode.Lib.DecodeLib;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ShooterAngleCalculator {

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
 //   private double shooterAngleToTarget;
//    private double intakeRangeToTarget;
//    private double intakeBearingToTarget;
//    private double robotHeadingInFieldCoordinates;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

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

    public static double getShooterAngleToTarget(double intakeBearingToTarget, AngleUnit unit) {
        double intakeBearingToTargetInDegrees = unit.toDegrees(intakeBearingToTarget);
        double shooterAngleToTarget = 0;
        if (intakeBearingToTargetInDegrees == 180) {
            shooterAngleToTarget = 0;
        }
        if (intakeBearingToTargetInDegrees > 0) {
            shooterAngleToTarget = -1 * (180 - intakeBearingToTargetInDegrees);
        }
        else {
            shooterAngleToTarget = -1 * (-180 - intakeBearingToTargetInDegrees);
        }
        return unit.fromDegrees(shooterAngleToTarget);
    }
}
