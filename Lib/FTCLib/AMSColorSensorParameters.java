package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;

public class AMSColorSensorParameters {

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

    // Registers we used to read-ahead, if supported
    private int IREG_READ_FIRST = AdafruitColorSensor8863.Register.CLEARL.byteVal;
    private int IREG_READ_LAST = AdafruitColorSensor8863.Register.BLUEH.byteVal;

    /**
     * the device id expected to be reported by the color sensor chip
     */
    private final int deviceId;

    /**
     * the address at which the sensor resides on the I2C bus.
     */
    private I2cAddr i2cAddr;

    /**
     * the integration time to use, default to 24ms
     */
    private AdafruitColorSensor8863.IntegrationTime integrationTime = AdafruitColorSensor8863.IntegrationTime.AMS_COLOR_ITIME_460MS;

    /**
     * the gain level to use, default to 4x
     */
    private AdafruitColorSensor8863.Gain gain = AdafruitColorSensor8863.Gain.AMS_COLOR_GAIN_64;

    /**
     * set of registers to read in background, if supported by underlying I2cDeviceSynch
     */
    private I2cDeviceSynch.ReadWindow readWindow = new I2cDeviceSynch.ReadWindow(IREG_READ_FIRST, IREG_READ_LAST - IREG_READ_FIRST + 1, I2cDeviceSynch.ReadMode.REPEAT);

    /**
     * debugging aid: enable logging for this device?
     */
    private boolean loggingEnabled = false;

    /**
     * debugging aid: the logging tag to use when logging
     */
    private String loggingTag = "AMSColorSensor";

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public I2cAddr getI2cAddr() {
        return i2cAddr;
    }

    public AdafruitColorSensor8863.IntegrationTime getIntegrationTime() {
        return integrationTime;
    }

    public AdafruitColorSensor8863.Gain getGain() {
        return gain;
    }

    public void setGain(AdafruitColorSensor8863.Gain gain) {
        this.gain = gain;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public String getLoggingTag() {
        return loggingTag;
    }

    public int getIREG_READ_FIRST() {
        return IREG_READ_FIRST;
    }

    public int getIREG_READ_LAST() {
        return IREG_READ_LAST;
    }

    public I2cDeviceSynch.ReadWindow getReadWindow() {
        return readWindow;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    private AMSColorSensorParameters(I2cAddr i2cAddr, int deviceId) {
        this.i2cAddr = i2cAddr;
        this.deviceId = deviceId;
    }

    /**
     * A method to return parameters customized for the AdaFruit color sensor
     * @return parameters
     */
    public static AMSColorSensorParameters createForAdaFruit() {
        int AMS_TCS34725_ADDRESS = 0x29;
        byte AMS_TCS34725_ID = 0x44;

        AMSColorSensorParameters parameters = new AMSColorSensorParameters(I2cAddr.create7bit(AMS_TCS34725_ADDRESS), AMS_TCS34725_ID);
        return parameters;
    }
}
