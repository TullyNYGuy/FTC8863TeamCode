package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Skystone {

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
    private static double HeightCM = 10.1;
    private static double HeightPlusNubCM = 12.5;
    private static double LengthtCM = 20;
    private static double WidthCM = 10;


    private static double HeightIN = 4;
    private static double HeightPlusNubIN = 5;
    private static double LengthIN = 8;
    private static double WidthIN = 4;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    public static double getHeightCM() {
        return HeightCM;
    }

    public static double getHeightPlusNubCM() {
        return HeightPlusNubCM;
    }

    public static double getLengthtCM() {
        return LengthtCM;
    }

    public static double getWidthCM() {
        return WidthCM;
    }

    public static double getHeightIN() {
        return HeightIN;
    }

    public static double getHeightPlusNubIN() {
        return HeightPlusNubIN;
    }

    public static double getLengthIN() {
        return LengthIN;
    }

    public static double getWidthIN() {
        return WidthIN;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public Skystone() {

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
