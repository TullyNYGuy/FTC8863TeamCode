package org.firstinspires.ftc.teamcode.Lib.DecodeLib;


import android.graphics.Color;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ColorDetectorHSV;

public class DecodeIntakeColorSensor {

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
    private NormalizedColorSensor intakeSensor;
    private ColorDetectorHSV colorDetectorHSV;

    final float[] hsvValues = new float[3];

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public DecodeIntakeColorSensor(HardwareMap hardwareMap, Telemetry telemetry, String sensorName) {
        intakeSensor = hardwareMap.get(NormalizedColorSensor.class, sensorName);
        if (intakeSensor instanceof SwitchableLight) {
            ((SwitchableLight) intakeSensor).enableLight(true);
        }
        intakeSensor.setGain(10);
        colorDetectorHSV = new ColorDetectorHSV();
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
        telemetry.addData("distance=", ((DistanceSensor) intakeSensor).getDistance(DistanceUnit.CM));
    }

    public double getDistance() {
        return ((DistanceSensor) intakeSensor).getDistance(DistanceUnit.CM);
    }

    public boolean isIntakeFull() {
        if (((DistanceSensor) intakeSensor).getDistance(DistanceUnit.CM) < 1.1) {
            return true;
        } else {
            return false;
        }
    }

    public void displayColors(Telemetry telemetry) {
        NormalizedRGBA colors = intakeSensor.getNormalizedColors();

        /* Use telemetry to display feedback on the driver station. We show the red, green, and blue
         * normalized values from the sensor (in the range of 0 to 1), as well as the equivalent
         * HSV (hue, saturation and value) values. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
         * for an explanation of HSV color. */

        // Update the hsvValues array by passing it to Color.colorToHSV()
        Color.colorToHSV(colors.toColor(), hsvValues);

        telemetry.addLine()
                .addData("Red", "%.3f", colors.red)
                .addData("Green", "%.3f", colors.green)
                .addData("Blue", "%.3f", colors.blue);
        telemetry.addLine()
                .addData("Hue", "%.3f", hsvValues[0])
                .addData("Saturation", "%.3f", hsvValues[1])
                .addData("Value", "%.3f", hsvValues[2]);
        telemetry.addData("Alpha", "%.3f", colors.alpha);
        telemetry.addLine();
        telemetry.addData("Color: ", colorDetectorHSV.getColorUsingHue(hsvValues).toString());
    }

    public org.firstinspires.ftc.teamcode.Lib.FTCLib.Color getColor() {
        return colorDetectorHSV.getColorUsingHue(hsvValues);
    }
}
