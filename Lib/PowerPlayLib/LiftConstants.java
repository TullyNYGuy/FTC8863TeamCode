package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.control.PIDCoefficients;

@Config
public class LiftConstants {

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

    public static double MAX_VELOCITY = 60; //in/sec
    public static double MAX_ACCELERATION = 200; // in/sec^2

    public static double MOVEMENT_PER_REVOLUTION = 5.93; // in / motor revolution

    public static double MAX_RPM = 1150; // max rpm of motor

    // These are the feedforward parameters
    public static double kV = 0.014;
    public static double kA = .001;
    //public static double kStatic = 0.3347;
    public static double kStatic = 0;
    public static double kGAtRetraction = .241;
    //public static double kGAtRetraction = .3347;
    //public static double kGPerUnitExtension = .0031; // kG/in
    public static double kGPerUnitExtension = 0.0; // kG/in

    public static double getKg(double liftPostion) {
        return kGPerUnitExtension * liftPostion + kGAtRetraction;
    }

    public static PIDCoefficients MOTION_PID = new PIDCoefficients(0, 0, 0);

    public static double MAXIMUM_LIFT_POSITION = 37; // INCHES
    public static double MINIMUM_LIFT_POSITION = 2; // INCHES

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

}
