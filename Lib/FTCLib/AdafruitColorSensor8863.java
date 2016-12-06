package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import android.graphics.Color;

import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.hardware.ams.AMSColorSensorImpl;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AdafruitColorSensor8863 {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************


    /**
     * This defines the bits in the command register and defines an enum for their function. OR them
     * together to get the full command byte. To address another register, OR that address into the
     * command byte.
     */
    enum CommandRegister {
        AMS_COLOR_COMMAND_BIT(0x80), // bit 7 must be 1 when writing to this register
        AMS_COLOR_COMMAND_REPEAT(0x00), // bits 6:5 = 00 to specify a repeated byte protocol transaction
        AMS_COLOR_COMMAND_AUTO_INCREMENT(0x10), // bits 6:5 = 01 to specify an auto-increment protocol transaction
        AMS_COLOR_COMMAND_SPECIAL_FUNCTION(0x30), // bits 6:5 = 11 to specify a special function
        AMS_COLOR_COMMAND_CLEAR_CHANNEL_INTERRUPT(0x05); // bits 4:0 to clear the channel interrupt

        public final byte byteVal;
        CommandRegister(int i) {
            this.byteVal = (byte) i;
        }
    }

    /**
     * This defines the bits in the enable register and defines an enum for their function. OR them
     * together to get the full enable byte.
     */
    enum EnableRegister {
        AMS_COLOR_ENABLE_PIEN(0x20),        /* Proximity interrupt enable */
        AMS_COLOR_ENABLE_AIEN(0x10),        /* RGBC Interrupt Enable */
        AMS_COLOR_ENABLE_WEN(0x08),         /* Wait enable - Writing 1 activates the wait timer */
        AMS_COLOR_ENABLE_PEN(0x04),         /* Proximity enable */
        AMS_COLOR_ENABLE_AEN(0x02),         /* RGBC Enable - Writing 1 actives the ADC, 0 disables it */
        AMS_COLOR_ENABLE_PON(0x01);         /* Power on - Writing 1 activates the internal oscillator, 0 disables it */

        public final byte byteVal;
        EnableRegister(int i) {
            this.byteVal = (byte) i;
        }
    }

    /**
     * REGISTER provides symbolic names for interesting device registers
     */
    enum Register
    {
        ENABLE(0x00),
        ATIME(0x01),
        CONFIGURATION(0x0D),
        CONTROL(0x0F),
        DEVICE_ID(0x12),
        STATUS(0x13),
        CLEAR(0x14),   //clear color value: 0x14 = low byte, 0x15 = high byte
        RED(0x16),     //red color value: 0x16 = low byte, 0x17 = high byte
        GREEN(0x18),   //etc.
        BLUE(0x1A);

        public final byte byteVal;
        Register(int i) { this.byteVal = (byte) i; }
    }

//    enum Gain {
//        GAIN_1(0x00), // 1X
//        GAIN_4(0x01), // 4X
//        GAIN_16(0x02), // 16X
//        GAIN_64(0x03); // 64X
//
//        public final byte byteVal;
//
//        Gain(int i) {
//            this.byteVal = (byte) i;
//        }
//    }
//
//    enum IntegrationTime {
//        MS_2_4(0xFF), //2.4 mSec
//        MS_24(0xF6), // 24 mSec
//        MS_50(0xEB), // 50 mSec
//        MS_101(0xD5), // 101 mSec
//        MS_154(0xC0), // 154 mSec
//        MS_700(0x00); // 700 mSec
//
//        public final byte byteVal;
//
//        IntegrationTime(int i) {
//            this.byteVal = (byte) i;
//        }
//    }


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private I2cDevice colorSensor;
    private I2cDeviceSynch colorSensorClient;
    private AMSColorSensor.Parameters parameters;
    boolean isOwned = false;
    boolean waitForWriteCompletion = true;
    private AMSColorSensorImpl fred;

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

    public AdafruitColorSensor8863(HardwareMap hardwareMap, String colorSensorName) {
        parameters = AMSColorSensor.Parameters.createForAdaFruit();
        colorSensor = hardwareMap.get(I2cDevice.class, colorSensorName);
        colorSensorClient = new I2cDeviceSynchImpl(colorSensor, parameters.i2cAddr, isOwned);
        colorSensorClient.engage();
        initialize();
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private void initialize() {
        // check if isArmed() ?
        // check if the proper chip is out there by checking the chip id
        if (!checkDeviceId()) {
            // do something, not sure what yet
            doSomething();
        }
        // Set the gain and integration time
        setIntegrationTime(parameters.integrationTime);
        setGain(parameters.gain);
        // set up a read ahead?
        enable();
    }

    private void setIntegrationTime(AMSColorSensor.IntegrationTime time) {
        this.write8(Register.ATIME, time.byteVal);
    }

    private void setGain(AMSColorSensor.Gain gain) {
        this.write8(Register.CONTROL, gain.byteVal);
    }

    public byte getDeviceID() {
        return this.read8(Register.DEVICE_ID);
    }

    /**
     * Verify that that's a color sensor!
     */
    private boolean checkDeviceId() {
        byte id = this.getDeviceID();
        if ((id != parameters.deviceId)) {
            RobotLog.e("unexpected AMS color sensor chipid: found=%d expected=%d", id, parameters.deviceId);
            return false;
        } else {
            return true;
        }
    }

    private synchronized void enable() {
        this.write8(Register.ENABLE, AMSColorSensor.AMS_COLOR_ENABLE_PON);
        delayLore(6); // Adafruit's sample implementation uses 3ms
        this.write8(Register.ENABLE, AMSColorSensor.AMS_COLOR_ENABLE_PON | AMSColorSensor.AMS_COLOR_ENABLE_AEN);
    }

    private synchronized void disable() {
        /* Turn the device off to save power */
        byte reg = colorSensorClient.read8(Register.ENABLE.byteVal);
        this.write8(Register.ENABLE, reg & ~(AMSColorSensor.AMS_COLOR_ENABLE_PON | AMSColorSensor.AMS_COLOR_ENABLE_AEN));
    }

    /**
     * delay() implements a delay that is specified in the device datasheet and therefore should be correct
     *
     * @see #delayLore(int)
     */
    void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * delayLore() implements a delay that only known by lore and mythology to be necessary.
     *
     * @see #delay(int)
     */
    private void delayLore(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void doSomething(){

    }

    public synchronized byte read8(final Register reg)
    {
        return colorSensorClient.read8(reg.byteVal | CommandRegister.AMS_COLOR_COMMAND_BIT.byteVal);
    }

    public synchronized byte[] read(final Register reg, final int cb)
    {
        return colorSensorClient.read(reg.byteVal | CommandRegister.AMS_COLOR_COMMAND_BIT.byteVal, cb);
    }

    public synchronized void write8(Register reg, int data)
    {
        colorSensorClient.write8(reg.byteVal | CommandRegister.AMS_COLOR_COMMAND_BIT.byteVal, data);
        colorSensorClient.waitForWriteCompletions();
    }

    public synchronized void write(Register reg, byte[] data)
    {
        colorSensorClient.write(reg.byteVal | CommandRegister.AMS_COLOR_COMMAND_BIT.byteVal, data);
        colorSensorClient.waitForWriteCompletions();
    }

    public int readUnsignedShort(Register reg)
    {
        byte[] bytes = this.read(reg, 2);
        int result = 0;
        if (bytes.length == 2)
        {
            ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
            result = TypeConversion.unsignedShortToInt(buffer.getShort());
        }
        return result;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public synchronized int red()
    {
        return this.readColorRegister(Register.RED);
    }

    public synchronized int green()
    {
        return this.readColorRegister(Register.GREEN);
    }

    public synchronized int blue()
    {
        return this.readColorRegister(Register.BLUE);
    }

    public synchronized int alpha()
    {
        return this.readColorRegister(Register.CLEAR);
    }

    private int readColorRegister(Register reg)
    {
        return readUnsignedShort(reg);
    }

    public synchronized int argb()
    {
        byte[] bytes = read(Register.CLEAR, 8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        //
        int clear = TypeConversion.unsignedShortToInt(buffer.getShort());
        int red   = TypeConversion.unsignedShortToInt(buffer.getShort());
        int green = TypeConversion.unsignedShortToInt(buffer.getShort());
        int blue  = TypeConversion.unsignedShortToInt(buffer.getShort());
        //
        return Color.argb(clear, red, green, blue);
    }
//    private class AMSColorSensorParameters {
//        // The 7-bit I2C address of this device
//        int AMS_TCS34725_ADDRESS = 0x29;
//        int AMS_TMD37821_ADDRESS = 0x39;
//
//        byte AMS_TCS34725_ID = 0x44;
//        byte AMS_TMD37821_ID = 0x60;
//        byte AMS_TMD37823_ID = 0x69;
//
//        int AMS_COLOR_COMMAND_BIT = 0x80;
//
//        int AMS_COLOR_ENABLE = 0x00;
//        int AMS_COLOR_ENABLE_PIEN = 0x20;        /* Proximity interrupt enable */
//        int AMS_COLOR_ENABLE_AIEN = 0x10;        /* RGBC Interrupt Enable */
//        int AMS_COLOR_ENABLE_WEN = 0x08;         /* Wait enable - Writing 1 activates the wait timer */
//        int AMS_COLOR_ENABLE_PEN = 0x04;         /* Proximity enable */
//        int AMS_COLOR_ENABLE_AEN = 0x02;         /* RGBC Enable - Writing 1 actives the ADC, 0 disables it */
//        int AMS_COLOR_ENABLE_PON = 0x01;         /* Power on - Writing 1 activates the internal oscillator, 0 disables it */
//        int AMS_COLOR_ATIME = 0x01;              /* Integration time */
//        int AMS_COLOR_WTIME = 0x03;              /* Wait time = if AMS_COLOR_ENABLE_WEN is asserted; */
//        int AMS_COLOR_WTIME_2_4MS = 0xFF;        /* WLONG0 = 2.4ms   WLONG1 = 0.029s */
//        int AMS_COLOR_WTIME_204MS = 0xAB;        /* WLONG0 = 204ms   WLONG1 = 2.45s  */
//        int AMS_COLOR_WTIME_614MS = 0x00;        /* WLONG0 = 614ms   WLONG1 = 7.4s   */
//        int AMS_COLOR_AILTL = 0x04;              /* Clear channel lower interrupt threshold */
//        int AMS_COLOR_AILTH = 0x05;
//        int AMS_COLOR_AIHTL = 0x06;              /* Clear channel upper interrupt threshold */
//        int AMS_COLOR_AIHTH = 0x07;
//        int AMS_COLOR_PERS = 0x0C;               /* Persistence register - basic SW filtering mechanism for interrupts */
//        int AMS_COLOR_PERS_NONE = 0b0000;        /* Every RGBC cycle generates an interrupt                                */
//        int AMS_COLOR_PERS_1_CYCLE = 0b0001;     /* 1 clean channel value outside threshold range generates an interrupt   */
//        int AMS_COLOR_PERS_2_CYCLE = 0b0010;     /* 2 clean channel values outside threshold range generates an interrupt  */
//        int AMS_COLOR_PERS_3_CYCLE = 0b0011;     /* 3 clean channel values outside threshold range generates an interrupt  */
//        int AMS_COLOR_PERS_5_CYCLE = 0b0100;     /* 5 clean channel values outside threshold range generates an interrupt  */
//        int AMS_COLOR_PERS_10_CYCLE = 0b0101;    /* 10 clean channel values outside threshold range generates an interrupt */
//        int AMS_COLOR_PERS_15_CYCLE = 0b0110;    /* 15 clean channel values outside threshold range generates an interrupt */
//        int AMS_COLOR_PERS_20_CYCLE = 0b0111;    /* 20 clean channel values outside threshold range generates an interrupt */
//        int AMS_COLOR_PERS_25_CYCLE = 0b1000;    /* 25 clean channel values outside threshold range generates an interrupt */
//        int AMS_COLOR_PERS_30_CYCLE = 0b1001;    /* 30 clean channel values outside threshold range generates an interrupt */
//        int AMS_COLOR_PERS_35_CYCLE = 0b1010;    /* 35 clean channel values outside threshold range generates an interrupt */
//        int AMS_COLOR_PERS_40_CYCLE = 0b1011;    /* 40 clean channel values outside threshold range generates an interrupt */
//        int AMS_COLOR_PERS_45_CYCLE = 0b1100;    /* 45 clean channel values outside threshold range generates an interrupt */
//        int AMS_COLOR_PERS_50_CYCLE = 0b1101;    /* 50 clean channel values outside threshold range generates an interrupt */
//        int AMS_COLOR_PERS_55_CYCLE = 0b1110;    /* 55 clean channel values outside threshold range generates an interrupt */
//        int AMS_COLOR_PERS_60_CYCLE = 0b1111;    /* 60 clean channel values outside threshold range generates an interrupt */
//        int AMS_COLOR_CONFIG = 0x0D;
//        int AMS_COLOR_CONFIG_NORMAL = 0x00;      /* normal wait times */
//        int AMS_COLOR_CONFIG_WLONG = 0x02;       /* Extended wait time = 12x normal wait times via AMS_COLOR_WTIME */
//        int AMS_COLOR_CONTROL = 0x0F;            /* Set the gain level for the sensor */
//        int AMS_COLOR_GAIN_1 = 0x00;             /* normal gain */
//        int AMS_COLOR_GAIN_4 = 0x01;             /* 4x gain */
//        int AMS_COLOR_GAIN_16 = 0x02;            /* 16x gain */
//        int AMS_COLOR_GAIN_60 = 0x03;            /* 60x gain */
//        int AMS_COLOR_ID = 0x12;                 /* 0x44 = TCS34721/AMS_COLOR, 0x4D = TCS34723/TCS34727 */
//        int AMS_COLOR_STATUS = 0x13;
//        int AMS_COLOR_STATUS_AINT = 0x10;        /* RGBC Clean channel interrupt */
//        int AMS_COLOR_STATUS_AVALID = 0x01;      /* Indicates that the RGBC channels have completed an integration cycle */
//        int AMS_COLOR_CDATAL = 0x14;             /* Clear channel data */
//        int AMS_COLOR_CDATAH = 0x15;
//        int AMS_COLOR_RDATAL = 0x16;             /* Red channel data */
//        int AMS_COLOR_RDATAH = 0x17;
//        int AMS_COLOR_GDATAL = 0x18;             /* Green channel data */
//        int AMS_COLOR_GDATAH = 0x19;
//        int AMS_COLOR_BDATAL = 0x1A;             /* Blue channel data */
//        int AMS_COLOR_BDATAH = 0x1B;
//
//        // Registers we used to read-ahead, if supported
//        int IREG_READ_FIRST = AMS_COLOR_CDATAL;
//        int IREG_READ_LAST = AMS_COLOR_BDATAH;
//
//        /**
//         * the device id expected to be reported by the color sensor chip
//         */
//        public final int deviceId;
//
//        /**
//         * the address at which the sensor resides on the I2C bus.
//         */
//        public I2cAddr i2cAddr;
//
//        /**
//         * the integration time to use
//         */
//        public IntegrationTime integrationTime = IntegrationTime.MS_24;
//
//        /**
//         * the gain level to use
//         */
//        public Gain gain = Gain.GAIN_4;
//
//        /**
//         * set of registers to read in background, if supported by underlying I2cDeviceSynch
//         */
//        public I2cDeviceSynch.ReadWindow readWindow = new I2cDeviceSynch.ReadWindow(IREG_READ_FIRST, IREG_READ_LAST - IREG_READ_FIRST + 1, I2cDeviceSynch.ReadMode.REPEAT);
//
//        /**
//         * debugging aid: enable logging for this device?
//         */
//        public boolean loggingEnabled = false;
//
//        /**
//         * debugging aid: the logging tag to use when logging
//         */
//        public String loggingTag = "AMSColorSensor";
//
//        //*********************************************************************************************
//        //          Constructors
//        //
//        // the function that builds the class when an object is created
//        // from it
//        //*********************************************************************************************
//
//        private AMSColorSensorParameters(I2cAddr i2cAddr, int deviceId) {
//            this.i2cAddr = i2cAddr;
//            this.deviceId = deviceId;
//        }
//
//        public static AMSColorSensorParameters createForAdaFruit() {
//            int AMS_TCS34725_ADDRESS = 0x29;
//            int AMS_TMD37821_ADDRESS = 0x39;
//
//            byte AMS_TCS34725_ID = 0x44;
//            byte AMS_TMD37821_ID = 0x60;
//            byte AMS_TMD37823_ID = 0x69;
//
//            AMSColorSensorParameters parameters = new AMSColorSensorParameters(I2cAddr.create7bit(AMS_TCS34725_ADDRESS), AMS_TCS34725_ID);
//            return parameters;
//        }
//
//        public static AMSColorSensorParameters createForLynx() {
//            int AMS_TCS34725_ADDRESS = 0x29;
//            int AMS_TMD37821_ADDRESS = 0x39;
//
//            byte AMS_TCS34725_ID = 0x44;
//            byte AMS_TMD37821_ID = 0x60;
//            byte AMS_TMD37823_ID = 0x69;
//
//            AMSColorSensorParameters parameters = new AMSColorSensorParameters(I2cAddr.create7bit(AMS_TMD37821_ADDRESS), AMS_TMD37821_ID);
//            return parameters;
//        }
//    }
}
