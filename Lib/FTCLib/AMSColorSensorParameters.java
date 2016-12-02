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

    // The 7-bit I2C address of this device
    int AMS_TCS34725_ADDRESS = 0x29;
    int AMS_TMD37821_ADDRESS = 0x39;

    byte AMS_TCS34725_ID = 0x44;
    byte AMS_TMD37821_ID = 0x60;
    byte AMS_TMD37823_ID = 0x69;

    int AMS_COLOR_COMMAND_BIT = 0x80;

    int AMS_COLOR_ENABLE = 0x00;
    int AMS_COLOR_ENABLE_PIEN = 0x20;        /* Proximity interrupt enable */
    int AMS_COLOR_ENABLE_AIEN = 0x10;        /* RGBC Interrupt Enable */
    int AMS_COLOR_ENABLE_WEN = 0x08;         /* Wait enable - Writing 1 activates the wait timer */
    int AMS_COLOR_ENABLE_PEN = 0x04;         /* Proximity enable */
    int AMS_COLOR_ENABLE_AEN = 0x02;         /* RGBC Enable - Writing 1 actives the ADC, 0 disables it */
    int AMS_COLOR_ENABLE_PON = 0x01;         /* Power on - Writing 1 activates the internal oscillator, 0 disables it */
    int AMS_COLOR_ATIME = 0x01;              /* Integration time */
    int AMS_COLOR_WTIME = 0x03;              /* Wait time = if AMS_COLOR_ENABLE_WEN is asserted; */
    int AMS_COLOR_WTIME_2_4MS = 0xFF;        /* WLONG0 = 2.4ms   WLONG1 = 0.029s */
    int AMS_COLOR_WTIME_204MS = 0xAB;        /* WLONG0 = 204ms   WLONG1 = 2.45s  */
    int AMS_COLOR_WTIME_614MS = 0x00;        /* WLONG0 = 614ms   WLONG1 = 7.4s   */
    int AMS_COLOR_AILTL = 0x04;              /* Clear channel lower interrupt threshold */
    int AMS_COLOR_AILTH = 0x05;
    int AMS_COLOR_AIHTL = 0x06;              /* Clear channel upper interrupt threshold */
    int AMS_COLOR_AIHTH = 0x07;
    int AMS_COLOR_PERS = 0x0C;               /* Persistence register - basic SW filtering mechanism for interrupts */
    int AMS_COLOR_PERS_NONE = 0b0000;        /* Every RGBC cycle generates an interrupt                                */
    int AMS_COLOR_PERS_1_CYCLE = 0b0001;     /* 1 clean channel value outside threshold range generates an interrupt   */
    int AMS_COLOR_PERS_2_CYCLE = 0b0010;     /* 2 clean channel values outside threshold range generates an interrupt  */
    int AMS_COLOR_PERS_3_CYCLE = 0b0011;     /* 3 clean channel values outside threshold range generates an interrupt  */
    int AMS_COLOR_PERS_5_CYCLE = 0b0100;     /* 5 clean channel values outside threshold range generates an interrupt  */
    int AMS_COLOR_PERS_10_CYCLE = 0b0101;    /* 10 clean channel values outside threshold range generates an interrupt */
    int AMS_COLOR_PERS_15_CYCLE = 0b0110;    /* 15 clean channel values outside threshold range generates an interrupt */
    int AMS_COLOR_PERS_20_CYCLE = 0b0111;    /* 20 clean channel values outside threshold range generates an interrupt */
    int AMS_COLOR_PERS_25_CYCLE = 0b1000;    /* 25 clean channel values outside threshold range generates an interrupt */
    int AMS_COLOR_PERS_30_CYCLE = 0b1001;    /* 30 clean channel values outside threshold range generates an interrupt */
    int AMS_COLOR_PERS_35_CYCLE = 0b1010;    /* 35 clean channel values outside threshold range generates an interrupt */
    int AMS_COLOR_PERS_40_CYCLE = 0b1011;    /* 40 clean channel values outside threshold range generates an interrupt */
    int AMS_COLOR_PERS_45_CYCLE = 0b1100;    /* 45 clean channel values outside threshold range generates an interrupt */
    int AMS_COLOR_PERS_50_CYCLE = 0b1101;    /* 50 clean channel values outside threshold range generates an interrupt */
    int AMS_COLOR_PERS_55_CYCLE = 0b1110;    /* 55 clean channel values outside threshold range generates an interrupt */
    int AMS_COLOR_PERS_60_CYCLE = 0b1111;    /* 60 clean channel values outside threshold range generates an interrupt */
    int AMS_COLOR_CONFIG = 0x0D;
    int AMS_COLOR_CONFIG_NORMAL = 0x00;      /* normal wait times */
    int AMS_COLOR_CONFIG_WLONG = 0x02;       /* Extended wait time = 12x normal wait times via AMS_COLOR_WTIME */
    int AMS_COLOR_CONTROL = 0x0F;            /* Set the gain level for the sensor */
    int AMS_COLOR_GAIN_1 = 0x00;             /* normal gain */
    int AMS_COLOR_GAIN_4 = 0x01;             /* 4x gain */
    int AMS_COLOR_GAIN_16 = 0x02;            /* 16x gain */
    int AMS_COLOR_GAIN_60 = 0x03;            /* 60x gain */
    int AMS_COLOR_ID = 0x12;                 /* 0x44 = TCS34721/AMS_COLOR, 0x4D = TCS34723/TCS34727 */
    int AMS_COLOR_STATUS = 0x13;
    int AMS_COLOR_STATUS_AINT = 0x10;        /* RGBC Clean channel interrupt */
    int AMS_COLOR_STATUS_AVALID = 0x01;      /* Indicates that the RGBC channels have completed an integration cycle */
    int AMS_COLOR_CDATAL = 0x14;             /* Clear channel data */
    int AMS_COLOR_CDATAH = 0x15;
    int AMS_COLOR_RDATAL = 0x16;             /* Red channel data */
    int AMS_COLOR_RDATAH = 0x17;
    int AMS_COLOR_GDATAL = 0x18;             /* Green channel data */
    int AMS_COLOR_GDATAH = 0x19;
    int AMS_COLOR_BDATAL = 0x1A;             /* Blue channel data */
    int AMS_COLOR_BDATAH = 0x1B;

    // Registers we used to read-ahead, if supported
    int IREG_READ_FIRST = AMS_COLOR_CDATAL;
    int IREG_READ_LAST = AMS_COLOR_BDATAH;

    /**
     * the device id expected to be reported by the color sensor chip
     */
    public final int deviceId;

    /**
     * the address at which the sensor resides on the I2C bus.
     */
    public I2cAddr i2cAddr;

    /**
     * the integration time to use
     */
    public AMSColorSensorImpl8863.IntegrationTime integrationTime = AMSColorSensorImpl8863.IntegrationTime.MS_24;

    /**
     * the gain level to use
     */
    public AMSColorSensorImpl8863.Gain gain = AMSColorSensorImpl8863.Gain.GAIN_4;

    /**
     * set of registers to read in background, if supported by underlying I2cDeviceSynch
     */
    public I2cDeviceSynch.ReadWindow readWindow = new I2cDeviceSynch.ReadWindow(IREG_READ_FIRST, IREG_READ_LAST - IREG_READ_FIRST + 1, I2cDeviceSynch.ReadMode.REPEAT);

    /**
     * debugging aid: enable logging for this device?
     */
    public boolean loggingEnabled = false;

    /**
     * debugging aid: the logging tag to use when logging
     */
    public String loggingTag = "AMSColorSensor";

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

    public AMSColorSensorParameters(I2cAddr i2cAddr, int deviceId) {
        this.i2cAddr = i2cAddr;
        this.deviceId = deviceId;
    }

    public static AMSColorSensorParameters createForAdaFruit() {
        int AMS_TCS34725_ADDRESS = 0x29;
        byte AMS_TCS34725_ID = 0x44;

        AMSColorSensorParameters parameters = new AMSColorSensorParameters(I2cAddr.create7bit(AMS_TCS34725_ADDRESS), AMS_TCS34725_ID);
        return parameters;
    }

    public static AMSColorSensorParameters createForLynx() {
        int AMS_TMD37821_ADDRESS = 0x39;
        byte AMS_TMD37821_ID = 0x60;

        AMSColorSensorParameters parameters = new AMSColorSensorParameters(I2cAddr.create7bit(AMS_TMD37821_ADDRESS), AMS_TMD37821_ID);
        return parameters;
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
