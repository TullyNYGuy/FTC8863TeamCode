package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import android.graphics.Color;

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

    // The enums below provide symbolic names for device registers and for register bits and values.
    // All information is taken from the datasheet for the device.

    /**
     * Command Register (address = I2C address of device)
     * This defines the bits in the command register and defines an enum for their function. OR them
     * together to get the full command byte. To address another register, OR that address into the
     * command byte.
     */
    enum CommandRegister {
        AMS_COLOR_COMMAND_BIT(0x80),                // bit 7 must be 1 when writing to this register
        AMS_COLOR_COMMAND_REPEAT(0x00),             // bits 6:5 = 00 to specify a repeated byte protocol transaction
        AMS_COLOR_COMMAND_AUTO_INCREMENT(0x10),     // bits 6:5 = 01 to specify an auto-increment protocol transaction
        AMS_COLOR_COMMAND_SPECIAL_FUNCTION(0x30),   // bits 6:5 = 11 to specify a special function
        AMS_COLOR_COMMAND_CLEAR_CHANNEL_INTERRUPT(0x05); // bits 4:0 to clear the channel interrupt or is the address of the register of interest

        public final byte byteVal;

        CommandRegister(int i) {
            this.byteVal = (byte) i;
        }
    }

    /**
     * Enable Register (address = 0x00)
     * The enable register is used primarily to power the TCS3472 device on and off, and enable
     * functions and interrupts as shown below.
     * Provides symbolic names for the values the register can have.
     * OR the enums together to get the full enable byte.
     */
    enum EnableRegister {
        // bits 7:5: reserved
        AMS_COLOR_ENABLE_PIEN(0x20),        // bit 5: Proximity interrupt enable (not in the datasheet)
        AMS_COLOR_ENABLE_AIEN(0x10),        // bit 4: RGBC Interrupt Enable
        AMS_COLOR_ENABLE_WEN(0x08),         // bit 3: Wait enable - Writing 1 activates the wait timer
        AMS_COLOR_ENABLE_PEN(0x04),         // bit 2: Proximity enable  (not in the datasheet)
        AMS_COLOR_ENABLE_AEN(0x02),         // bit 1: RGBC Enable - Writing 1 actives the ADC, 0 disables it
        AMS_COLOR_ENABLE_PON(0x01);         // bit 0: Power on - Writing 1 activates the internal oscillator, 0 disables it

        public final byte byteVal;

        EnableRegister(int i) {
            this.byteVal = (byte) i;
        }
    }

    /**
     * RBGC timing register (address = 0x01) (INTEGRATION_TIME)
     * Definition of bits in register is not relevant. Values are.
     * The RGBC timing register controls the internal integration time of the RBGC clear and IR
     * channel ADCs in 2.4ms increments. Max RGBC count = (256-ATIME) x 1024 up to a max of 65535.
     * You can trade off longer integtration time for a more accurate reading. The downside is that
     * the response times from the color sensor become slower. In a typical Opmode loop you would
     * reading the sensor every time through the loop, you will need to have the sensor at the 24ms
     * integration time.
     * Provides symbolic names for the values the register can have.
     */
    enum IntegrationTime {
        AMS_COLOR_ITIME_2_4MS(0xFF), //2.4 mSec
        AMS_COLOR_ITIME_24MS(0xF6), // 24 mSec
        AMS_COLOR_ITIME_50MS(0xEB), // 50 mSec
        AMS_COLOR_ITIME_101MS(0xD5), // 101 mSec
        AMS_COLOR_ITIME_154MS(0xC0), // 154 mSec
        AMS_COLOR_ITIME_700MS(0x00); // 700 mSec

        public final byte byteVal;

        IntegrationTime(int i) {
            this.byteVal = (byte) i;
        }
    }

    /**
     * Wait time register (address = 0x03) (WAIT_TIME)
     * Definition of bits in register is not relevant. Values are.
     * Wait time register values. Wait time is set in 2.4ms increments unless WLONG bit is set, in
     * which case the wait times are 12X longer. WTIME is programmed as a 2's complement number.
     * These enum values are preset for you. You can also calculate and set your own.
     * Provides symbolic names for the values the register can have.
     * NOTE TO ME: WHAT DOES WAIT TIME DO?
     */
    enum WaitTime {
        AMS_COLOR_WTIME_2_4MS(0xFF),     // if WLONG=0, wait = 2.4ms; if WLONG=1 wait = 0.029s
        AMS_COLOR_WTIME_204MS(0xAB),     // if WLONG=0, wait = 204ms; if WLONG=1 wait = 2.45s
        AMS_COLOR_WTIME_614MS(0x00);     // if WLONG=0, wait = 614ms; if WLONG=1 wait = 7.4s

        public final byte byteVal;

        WaitTime(int i) {
            this.byteVal = (byte) i;
        }
    }

    // RGBC Interrupt Threshold Registers (addresses = 0x04 - 0x07)
    // The RGBC interrupt threshold restoers provide the values to be used as the high and low 
    // trigger points for the comparison function for interrupt generation. If the value generated
    // by the clear channel crosses below the lower threshold specified, or above the higher 
    // threshold, an interrupt is asserted on the interrupt pin. 
    // See addresses below THRESHOLD_AILTL, AILTH, AIHTL, AIHTH

    /**
     * Persistence Register (address = 0x0C)
     * The persistence register controls the filtering interrupt capabilities of the device.
     * Configurable filtering is provided to allow interrupts to be generated after each integration
     * cycle or if the integration has produced a result that is outside of the values specified
     * by the threshold register for the specified amount of time.
     * Provides symbolic names for the values the register can have.
     */
    enum Persistence {
        // bits 7:4 - reserved
        // bits 3:0 - interrupt persistence. Controls rate of interrupt 
        AMS_COLOR_PERS_NONE(0b0000),        // Every RGBC cycle generates an interrupt                                
        AMS_COLOR_PERS_1_CYCLE(0b0001),     // 1 clean channel value outside threshold range generates an interrupt   
        AMS_COLOR_PERS_2_CYCLE(0b0010),     // 2 clean channel values outside threshold range generates an interrupt  
        AMS_COLOR_PERS_3_CYCLE(0b0011),     // 3 clean channel values outside threshold range generates an interrupt  
        AMS_COLOR_PERS_5_CYCLE(0b0100),     // 5 clean channel values outside threshold range generates an interrupt  
        AMS_COLOR_PERS_10_CYCLE(0b0101),    // 10 clean channel values outside threshold range generates an interrupt 
        AMS_COLOR_PERS_15_CYCLE(0b0110),    // 15 clean channel values outside threshold range generates an interrupt 
        AMS_COLOR_PERS_20_CYCLE(0b0111),    // 20 clean channel values outside threshold range generates an interrupt 
        AMS_COLOR_PERS_25_CYCLE(0b1000),    // 25 clean channel values outside threshold range generates an interrupt 
        AMS_COLOR_PERS_30_CYCLE(0b1001),    // 30 clean channel values outside threshold range generates an interrupt 
        AMS_COLOR_PERS_35_CYCLE(0b1010),    // 35 clean channel values outside threshold range generates an interrupt 
        AMS_COLOR_PERS_40_CYCLE(0b1011),    // 40 clean channel values outside threshold range generates an interrupt 
        AMS_COLOR_PERS_45_CYCLE(0b1100),    // 45 clean channel values outside threshold range generates an interrupt 
        AMS_COLOR_PERS_50_CYCLE(0b1101),    // 50 clean channel values outside threshold range generates an interrupt 
        AMS_COLOR_PERS_55_CYCLE(0b1110),    // 55 clean channel values outside threshold range generates an interrupt 
        AMS_COLOR_PERS_60_CYCLE(0b1111);    // 60 clean channel values outside threshold range generates an interrupt 

        public final byte byteVal;

        Persistence(int i) {
            this.byteVal = (byte) i;
        }
    }

    /**
     * Configuration Register (address = 0x0D)
     * The configuration register sets the wait long time.
     * Provides symbolic names for the values the register can have.
     */
    enum Configuration {
        // bits 7:2 reserved, write as 0
        // bit 1 - WLONG, if set wait times increased by 12x
        // bit 0 - reserved, write as 0
        AMS_COLOR_CONFIG_NORMAL(0x00),    // normal wait times
        AMS_COLOR_CONFIG_WLONG(0x02);     // Extended wait time(12x normal wait times via AMS_COLOR_WTIME

        public final byte byteVal;

        Configuration(int i) {
            this.byteVal = (byte) i;
        }
    }

    /**
     * Control Register (address = 0x0F)
     * The control register provides eight bits of miscellaneous control to the analog block. These
     * bits typically control functions such as the gain setting and/or diode selection.
     */
    enum Gain {
        // bits 7:2 reserved, write as 0
        // bits 1:0 - RGBC gain control
        AMS_COLOR_GAIN_1(0x00),  // 1X
        AMS_COLOR_GAIN_4(0x01),  // 4X
        AMS_COLOR_GAIN_16(0x02), // 16X
        AMS_COLOR_GAIN_64(0x03); // 64X

        public final byte byteVal;

        Gain(int i) {
            this.byteVal = (byte) i;
        }
    }

    /**
     * ID Register (address = 0x12)
     * The ID register provides the value for the part number. The ID register is a read-only
     * register.
     */
    enum DeviceID {
        AMS_COLOR_TCS34721_5_ID(0x44),   // TCS34721 and TCS34725 ID
        AMS_COLOR_TCS34723_7_ID(0x4D);   // TCS34723 and TCS34727 ID

        public final byte byteVal;

        DeviceID(int i) {
            this.byteVal = (byte) i;
        }
    }

    /**
     * Status Register (address = 0x13)
     * The status register provides the internal status of the device. This register is read only.
     */
    enum Status {
        // bits 7:5 - reserved
        // bit 4 - AINT
        // bits 3:1 - reserved
        // bit 0 - AVALID
        AMS_COLOR_STATUS_A(0x10),        // RGBC Clear channel interrupt
        AMS_COLOR_STATUS_AVALID(0x01);  // Indicates that the RGBC channels have completed an integration cycle

        public final byte byteVal;

        Status(int i) {
            this.byteVal = (byte) i;
        }
    }

    // RGBC Channel Data Registers (addresses = 0x14 - 0x1B)
    // Clear, red, green and blue data is stored as 16 bit values. To ensure the data is read
    // correctly, a two-byte read I2C transaction should be used with a read word protocol bit set
    // in the command register. With this operation, when the lower byte register is read, the
    // upper eight bits are stred into a shadow register, which is read by a subsequent read to the
    // upper byte. The upper register will read the correct value even if additional ADC integration
    // cycles end between the reading of the lower and upper bytes.
    // see below for detailed addressing

    /**
     * REGISTER provides symbolic names for device registers
     */
    enum Register {
        ENABLE(0x00),
        INTEGRATION_TIME(0x01),
        WAIT_TIME(0x03),
        THRESHOLD_AILTL(0x04),
        THRESHOLD_AILTH(0x05),
        THRESHOLD_AIHTL(0x06),
        THRESHOLD_AIHTH(0x07),
        PERSISTENCE(0x0C),
        CONFIGURATION(0x0D),
        CONTROL(0x0F),
        DEVICE_ID(0x12),
        STATUS(0x13),
        CLEARL(0x14),   // clear data value low byte
        CLEARH(0x15),   // clear data value high byte
        REDL(0x16),     // red data value low byte
        REDH(0x17),     // red data value high byte
        GREENL(0x18),   // green data value low byte
        GREENH(0x19),   // green data value high byte
        BLUEL(0x1A),    // blue data value low byte
        BLUEH(0x1B);    // blue data value high byte

        public final byte byteVal;

        Register(int i) {
            this.byteVal = (byte) i;
        }
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private I2cDevice colorSensor;
    private I2cDeviceSynch colorSensorClient;
    private AMSColorSensorParameters parameters;
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
        parameters = AMSColorSensorParameters.createForAdaFruit();
        colorSensor = hardwareMap.get(I2cDevice.class, colorSensorName);
        colorSensorClient = new I2cDeviceSynchImpl(colorSensor, parameters.getI2cAddr(), isOwned);
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
        setIntegrationTime(parameters.getIntegrationTime());
        setGain(parameters.getGain());
        // set up a read ahead?
        enable();
    }

    private void setIntegrationTime(IntegrationTime time) {
        this.write8(Register.INTEGRATION_TIME, time.byteVal);
    }

    private void setGain(Gain gain) {
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
        if ((id != parameters.getDeviceId())) {
            RobotLog.e("unexpected AMS color sensor chipid: found=%d expected=%d", id, parameters.getDeviceId());
            return false;
        } else {
            return true;
        }
    }

    private synchronized void enable() {
        this.write8(Register.ENABLE, EnableRegister.AMS_COLOR_ENABLE_PON.byteVal);
        delayLore(6); // Adafruit's sample implementation uses 3ms
        this.write8(Register.ENABLE, EnableRegister.AMS_COLOR_ENABLE_PON.byteVal | EnableRegister.AMS_COLOR_ENABLE_AEN.byteVal);
    }

    private synchronized void disable() {
        /* Turn the device off to save power */
        byte reg = colorSensorClient.read8(Register.ENABLE.byteVal);
        this.write8(Register.ENABLE, reg & ~(EnableRegister.AMS_COLOR_ENABLE_PON.byteVal | EnableRegister.AMS_COLOR_ENABLE_AEN.byteVal));
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

    private void doSomething() {

    }

    public synchronized byte read8(final Register reg) {
        return colorSensorClient.read8(reg.byteVal | CommandRegister.AMS_COLOR_COMMAND_BIT.byteVal);
    }

    public synchronized byte[] read(final Register reg, final int cb) {
        return colorSensorClient.read(reg.byteVal | CommandRegister.AMS_COLOR_COMMAND_BIT.byteVal, cb);
    }

    public synchronized void write8(Register reg, int data) {
        colorSensorClient.write8(reg.byteVal | CommandRegister.AMS_COLOR_COMMAND_BIT.byteVal, data);
        colorSensorClient.waitForWriteCompletions();
    }

    public synchronized void write(Register reg, byte[] data) {
        colorSensorClient.write(reg.byteVal | CommandRegister.AMS_COLOR_COMMAND_BIT.byteVal, data);
        colorSensorClient.waitForWriteCompletions();
    }

    public int readUnsignedShort(Register reg) {
        byte[] bytes = this.read(reg, 2);
        int result = 0;
        if (bytes.length == 2) {
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

    public synchronized int red() {
        return this.readColorRegister(Register.REDL);
    }

    public synchronized int green() {
        return this.readColorRegister(Register.GREENL);
    }

    public synchronized int blue() {
        return this.readColorRegister(Register.BLUEL);
    }

    public synchronized int alpha() {
        return this.readColorRegister(Register.CLEARL);
    }

    private int readColorRegister(Register reg) {
        return readUnsignedShort(reg);
    }

    public synchronized int argb() {
        byte[] bytes = read(Register.CLEARL, 8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        //
        int clear = TypeConversion.unsignedShortToInt(buffer.getShort());
        int red = TypeConversion.unsignedShortToInt(buffer.getShort());
        int green = TypeConversion.unsignedShortToInt(buffer.getShort());
        int blue = TypeConversion.unsignedShortToInt(buffer.getShort());
        //
        return Color.argb(clear, red, green, blue);
    }
}
