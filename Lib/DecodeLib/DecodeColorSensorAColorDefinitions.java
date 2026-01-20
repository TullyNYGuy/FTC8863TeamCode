package org.firstinspires.ftc.teamcode.Lib.DecodeLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ColorInHSV;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ColorSensorUpdatable;

/**
 * The reason there are different color sensor classes is that we have found that each sensor reports
 * color differently. So it is easiest to create a customized class to describe each one. The sensor
 * has a label on it indicating which one it is: A, B, C, D
 */
public class DecodeColorSensorAColorDefinitions {

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

    private ColorInHSV red;
    private ColorInHSV yellow;
    private ColorInHSV blue;
    private ColorInHSV green;
    private ColorInHSV purple;
    private ColorInHSV[] possibleColors;
    public ColorSensorUpdatable sensor;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public DecodeColorSensorAColorDefinitions(HardwareMap hardwareMap, Telemetry telemetry, String name) {
        // customize the colors the sensor sees
        // define the colors the intake is looking for
        // f here means float instead of double type. HSV are float type.
        // for high slope intake lid
//        red = new ColorInHSV(Color.RED,
//                0, 60,
//                0.2f, 0.4f,
//                0.07f, 0.09f);
//        yellow = new ColorInHSV(Color.YELLOW,
//                60, 120,
//                0.5f, 0.65f,
//                .13f, .16f);
//
//        blue = new ColorInHSV(Color.BLUE,
//                180, 240,
//                0.54f, 0.66f,
//                0.08f, 0.2f);
        // for low slope intake lid
        red = new ColorInHSV(Color.RED,
                0, 60,
                0.6f, 0.8f,
                0.05f, 0.10f);
        yellow = new ColorInHSV(Color.YELLOW,
                60, 120,
                0.7f, 0.8f,
                .09f, .2f);

        blue = new ColorInHSV(Color.BLUE,
                180, 240,
                0.7f, 0.8f,
                0.05f, 0.09f);
        green = new ColorInHSV(Color.GREEN,
                190, 240,
                0.54f, 0.66f,
                0.08f, 0.2f);
        purple = new ColorInHSV(Color.PURPLE,
                190, 240,
                0.54f, 0.66f,
                0.08f, 0.2f);
        possibleColors = new ColorInHSV[]{green, purple};
        sensor = new ColorSensorUpdatable(hardwareMap, telemetry, name, possibleColors);
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
