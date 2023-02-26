package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;

import com.acmerobotics.dashboard.config.Config;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDCoefficients;

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

    public static double MAX_VELOCITY_EXTENSION = 60; //in/sec
    public static double MAX_ACCELERATION_EXTENSION = 200; // in/sec^2

    public static double MAX_VELOCITY_RETRACTION = 30; //in/sec
    public static double MAX_ACCELERATION_RETRACTION = 100; // in/sec^2

    //public static double MOVEMENT_PER_REVOLUTION = 5.93; // in / motor revolution
    public static double MOVEMENT_PER_REVOLUTION = 5.867; // in / motor revolution

    public static double MAX_RPM = 1150; // max rpm of motor

    // These are the feedforward parameters
    public static double kVExtension = 0.014;
    public static double kVRetraction = 0.024;
    public static double kAExtension = .001;
    public static double kARetraction = .001;

    //public static double kStatic = 0.3347;
    public static double kStatic = 0;
    public static double kGAtRetraction = .241;
    //public static double kGAtRetraction = .3347;
    //public static double kGPerUnitExtension = .0031; // kG/in
    public static double kGPerUnitExtension = 0.0; // kG/in

    public static double getKg(double liftPosition) {
        return kGPerUnitExtension * liftPosition + kGAtRetraction;
    }

    public static PIDCoefficients MOTION_PID_COEFFICENTS = new PIDCoefficients(.6, 0, 0);

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
