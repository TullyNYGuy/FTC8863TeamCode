package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import android.graphics.Color;

import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.hardware.ams.AMSColorSensorImpl;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Uses the deprecated AdafruitI2cColorSensor class to read the color sensor. The gain is fixed
 * and the integration time is set to 600 ms. Not very useful. Dont' use this class.
 */
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

    //private AMSColorSensor colorSensor;
    private ColorSensor colorSensor;
    //private AMSColorSensorImpl colorSensor;
    //private I2cDevice i2cDevice;

    //private AMSColorSensor.Parameters parameters;
//    int AMS_TCS34725_ADDRESS = 0x29;
//    byte AMS_TCS34725_ID = 0x44;

    private DeviceInterfaceModule coreDIM;

    private boolean ledOn = false;
    private boolean controlLED = true;
    private int ioChannelForLed;
    private String colorSensorName;
    private String coreDIMName;
    private ElapsedTime updateTimer;
    private int lastAlpha = 0;
    public StatTracker updateTimeTracker;

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
        //parameters = AMSColorSensor.Parameters.createForAdaFruit();
        coreDIM = hardwareMap.deviceInterfaceModule.get(coreDIMName);
        // both of the calls below result in an abject instantiated from AdafruitI2cColorSensor.
        // This is not desirable since the update rate is 600 mSec. And because it is marked
        // deprecated. I really want an object of
        // AMSColorSensorImpl since it has a configurable update rate and it new.
        colorSensor = hardwareMap.colorSensor.get(colorSensorName);
        //colorSensor = hardwareMap.get(AMSColorSensor.class, colorSensorName);
        //i2cDevice = hardwareMap.get(I2cDevice.class, colorSensorName);
        //colorSensor = AMSColorSensorImpl.create(parameters, i2cDevice);
        coreDIM.setDigitalChannelMode(ioChannelForLed, DigitalChannelController.Mode.OUTPUT);
        // Delay so the init can finish before setting the led off. Otherwise the LED does not get
        // shut off.
        delay(100);
        coreDIM.setDigitalChannelState(ioChannelForLed, ledOn);
        updateTimer = new ElapsedTime();
        // A tracker used to track the update rate of the color sensor.
        updateTimeTracker = new StatTracker();
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Implements a delay
     *
     * @param mSec delay in milli Seconds
     */
    private void delay(int mSec) {
        try {
            Thread.sleep((int) (mSec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

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
        int alpha = colorSensor.alpha();
        if (alpha != lastAlpha) {
            // alpha changed so update the tracker
            updateTimeTracker.compareValue(updateTimer.milliseconds());
            updateTimer.reset();
            // Since alpha changed save the new lastAlpha
            lastAlpha = alpha;
        }
        return alpha;
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
        float hsvValues[] = {0F, 0F, 0F};
        // convert the RGB values to HSV values.
        Color.RGBToHSV((colorSensor.red() * 255) / 800, (colorSensor.green() * 255) / 800, (colorSensor.blue() * 255) / 800, hsvValues);
        return hsvValues;
    }

    public int color() {
        return Color.HSVToColor(alpha(), hsv(red(), green(), blue()));
    }

    public float hue() {
        return hsv(colorSensor.red(), colorSensor.green(), colorSensor.blue())[0];
    }

    public float saturation() {
        return hsv(colorSensor.red(), colorSensor.green(), colorSensor.blue())[1];
    }

    public float lightness() {
        return hsv(colorSensor.red(), colorSensor.green(), colorSensor.blue())[2];
    }
}
