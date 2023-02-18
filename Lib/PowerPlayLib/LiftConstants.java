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

    public static double MAX_VELOCITY = 0; //in/sec
    public static double MAX_ACCELERATION = 0; // in/sec^2

    // These are the feedforward parameters
    public static double kV = 0;
    public static double kA = 0;
    public static double kG = 0;

    public static PIDCoefficients MOTION_PID = new PIDCoefficients(8, 0, 0);

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
