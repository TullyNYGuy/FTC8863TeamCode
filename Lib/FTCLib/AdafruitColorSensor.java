package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class AdafruitColorSensor {

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

    private ColorSensor colorSensor;
    private DeviceInterfaceModule coreDIM;
    private boolean ledOn = false;
    private boolean controlLED = true;
    private int ioChannelForLed;
    private String colorSensorName;
    private String coreDIMName;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public AdafruitColorSensor(String colorSensorName, String coreDIMName, HardwareMap hardwareMap, int ioChannelForLed) {
        this.ledOn = false;
        this.colorSensorName = colorSensorName;
        this.coreDIMName = coreDIMName;
        this.ioChannelForLed = ioChannelForLed;
        this.controlLED = true;
        coreDIM = hardwareMap.deviceInterfaceModule.get(coreDIMName);
        colorSensor = hardwareMap.colorSensor.get(colorSensorName);
        coreDIM.setDigitalChannelMode(ioChannelForLed, DigitalChannelController.Mode.OUTPUT);
        coreDIM.setDigitalChannelState(ioChannelForLed, ledOn);
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
     public void turnLEDOn() {
         if (!this.ledOn && controlLED) {
             this.ledOn = true;
             coreDIM.setDigitalChannelState(ioChannelForLed, ledOn);
         }
     }

    public void turnLEDOff() {
        if (this.ledOn && controlLED) {
            this.ledOn = false;
            coreDIM.setDigitalChannelState(ioChannelForLed, ledOn);
        }
    }

    public int alpha() {
        return colorSensor.alpha();
    }

    public int red() {
        return colorSensor.red();
    }

    public int green() {
        return colorSensor.green();
    }

    public int blue() {
        return colorSensor.blue();
    }

    public float[] hsv(int red, int green, int blue) {
        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F,0F,0F};
        // convert the RGB values to HSV values.
        Color.RGBToHSV((colorSensor.red() * 255) / 800, (colorSensor.green() * 255) / 800, (colorSensor.blue() * 255) / 800, hsvValues);
        return hsvValues;
    }

    public int color () {
        return Color.HSVToColor(alpha(), hsv(red(), green(), blue()));
    }

    public float hue() {
        return hsv(colorSensor.red(), colorSensor.green(), colorSensor.blue())[0];
    }

    public float saturation() {
        return hsv(colorSensor.red(), colorSensor.green(), colorSensor.blue())[1];
    }

    public float value() {
        return hsv(colorSensor.red(), colorSensor.green(), colorSensor.blue())[2];
    }
}
