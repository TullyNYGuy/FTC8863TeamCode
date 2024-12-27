package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import android.graphics.Color;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ColorDetectorHSV;

import java.util.Arrays;

public class ColorSensorUpdatable {

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
    private NormalizedColorSensor colorSensor;
    private ColorDetectorHSV colorDetectorHSV;
    private String sensorName;
    /**
     * HSV values are updated from the sensor once per update cycle to minimize the number of I2C
     * transactions.
     */
    private final float[] hsvValues = new float[3];

    public float[] getHsvValues() {
        return hsvValues;
    }

    private NormalizedRGBA colors;

    private DistanceUnit myDistanceUnit = DistanceUnit.CM;
    private double distance = 0;

    public double getDistance(DistanceUnit distanceUnit) {
        return distanceUnit.fromUnit(myDistanceUnit, distance);
    }

    private boolean sensorOn = false;

    public void turnSensorOn() {
        sensorOn = true;
        // turn on the led
        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }
    }

    public void turnSensorOff() {
        sensorOn = false;
        // put 0 in all the data to make the data as broken as possible.
        // Hopefully this will clue someone in if they are still trying to use the data when the
        // sensor is turned off and not updating the data.
//        distance = 0;
//        colors.red = 0;
//        colors.blue = 0;
//        colors.green = 0;
//        Arrays.fill(hsvValues, 0);
        // turn off the led to save power and indicate the sensor is off
        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(false);
        }
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * This constructor looks for a default set of colors using a default set of Hue ranges.
     * @param hardwareMap
     * @param telemetry
     * @param sensorName
     */
    public ColorSensorUpdatable(HardwareMap hardwareMap, Telemetry telemetry, String sensorName) {
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, sensorName);
        this.sensorName = sensorName;
        colorSensor.setGain(10);
        colorDetectorHSV = new ColorDetectorHSV();
        turnSensorOff();
    }

    /**
     * This constructor takes in a set of colors being looked for and their associated HSV ranges.
     * @param hardwareMap
     * @param telemetry
     * @param sensorName
     * @param possibleColors - an array of ColorInHSV, ie possible colors and their HSV ranges
     */
    public ColorSensorUpdatable(HardwareMap hardwareMap, Telemetry telemetry, String sensorName, ColorInHSV[] possibleColors) {
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, sensorName);
        this.sensorName = sensorName;
        colorSensor.setGain(10);
        colorDetectorHSV = new ColorDetectorHSV(possibleColors);
        turnSensorOff();
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
    public void displayColorSensorDistance(Telemetry telemetry) {
        telemetry.addData(sensorName + " distance =", distance);
    }

    public void displayColorData(Telemetry telemetry) {
        telemetry.addLine()
                .addData(sensorName, " ")
                .addData("Red", "%.3f", colors.red)
                .addData("Green", "%.3f", colors.green)
                .addData("Blue", "%.3f", colors.blue);
        telemetry.addLine()
                .addData("Hue", "%.3f", hsvValues[0])
                .addData("Saturation", "%.3f", hsvValues[1])
                .addData("Value", "%.3f", hsvValues[2]);
        telemetry.addData("Alpha", "%.3f", colors.alpha);
        telemetry.addLine();
    }

    public void update() {
        // to save time, only use the I2C bus when we need data
        if (sensorOn) {
            /* Get the red, green, and blue
             * normalized values from the sensor (in the range of 0 to 1), as well as the equivalent
             * HSV (hue, saturation and value) values. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
             * for an explanation of HSV color. */
            colors = colorSensor.getNormalizedColors();
            // Update the hsvValues array by passing it to Color.colorToHSV()
            Color.colorToHSV(colors.toColor(), hsvValues);
            distance = ((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM);
        }
    }
}
