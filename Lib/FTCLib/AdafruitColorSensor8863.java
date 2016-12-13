package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import android.graphics.Color;

import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.TypeConversion;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

// Things to do:
//    investigate the update rate - it appears odd
//    put in an instantaneous update rate
//    add to test routine so that button controls gain and integration time
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
     * Normal RGB color values range from 0 to 255. The color sensor returns values that are 2 bytes
     * long. The max possible value is much bigger than 255. In order to get normal RGB values, I
     * have to scale them down. The scaling factor is given by (256-ATIME) x 1024.
     * Provides symbolic names for the values the register can have.
     * <p>
     * For FTC this is the most important setting. Longer integration times = more accuracy and more
     * resolution (higher max color values). Shorter integration = less accuracy and less resolution.
     * But nothing comes for free right? Longer times make the sensor slower to respond. 700ms
     * integration time means you will get a new reading a little less than once every second. Is
     * that ok? Depends on your application. If you are line following, then no way. You want new
     * readings very quickly so you can respond if you get off course. But if you have
     * to take the color of something once then it may be ok. So take a look at your application.
     * Trade off the accuracy vs the time to get a new reading. Test it. Change if needed.
     * A good starting point is 24ms. That is around the time it takes to run one opmode loop.
     * Equation for calculating integration time in milliseconds (ms) = (256-ATIME) * 2.4ms
     * Equation for calculating ATIME (byte value of register) = (256-(integratin time in ms)) / 2.4ms
     */
    enum IntegrationTime {
        AMS_COLOR_ITIME_2_4MS(0xFF), //2.4 mSec, max possible value = 1024
        AMS_COLOR_ITIME_24MS(0xF6), // 24 mSec, max possible value = 10240
        AMS_COLOR_ITIME_50MS(0xEB), // 50 mSec, max possible value = 21504
        AMS_COLOR_ITIME_101MS(0xD5), // 101 mSec, max possible value = 44032 (data sheet has error in table 6)
        AMS_COLOR_ITIME_154MS(0xC0), // 154 mSec, max possible value = 65535
        AMS_COLOR_ITIME_307MS(0x80), // 307 mSec, max possible value = 65535
        AMS_COLOR_ITIME_460MS(0x40), // 460 mSec, max possible value = 65535
        AMS_COLOR_ITIME_537MS(0x20), // 537 mSec, max possible value = 65535
        AMS_COLOR_ITIME_700MS(0x00); // 700 mSec, max possible value = 65535

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
     * <p>
     * WHAT DOES WAIT TIME DO? The wait time is the time the device waits between integration cycles.
     * In other words, the device goes to sleep for a while before producing a new set of color
     * values. For battery powered devices, this is good. Sleep reduces the power the sensor uses.
     * But for FTC, we don't care. The power the sensor uses is miniscule compared to the motors and
     * servos. We do care about speed though. We most likely want it fast. Our sensor does not get
     * any sleep :-). So typically you want to set this wait time to 2.4ms and keep WLONG = 0.
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

    // In FTC we don't have any interrupts. So these registers are not a concern to us.

    /**
     * Persistence Register (address = 0x0C)
     * The persistence register controls the filtering interrupt capabilities of the device.
     * Configurable filtering is provided to allow interrupts to be generated after each integration
     * cycle or if the integration has produced a result that is outside of the values specified
     * by the threshold register for the specified amount of time.
     * Provides symbolic names for the values the register can have.
     * <p>
     * In FTC we don't have any interrupts. So these registers are not a concern to us.
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
     * See wait time register for an explanation of this. Short take: you almost certainly want
     * normal wait times for FTC.
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
     * <p>
     * This setting is important for FTC. You want as high a gain as possible. But you don't want it
     * so high that the values saturate at the max associated with the integration time you choose.
     * So it is a tightrope walk. Too low and your color values will not be as good as they can be.
     * Too high and they are total garbage.
     * My recommendation: pick a gain and then look at the color values you get when you put the
     * sensor into an environment where it reads the colors from an object of interest. Lighting
     * in the room will have a big effect on this. Make sure it is similar to your competition
     * lighting. Then increase the gain a step. Keep doing this until your color values saturate,
     * then back down a step on the gain. You can tell what the max values will be by shining a
     * bright flashlight at the sensor. The values will max out (saturate). You don't want that to
     * happen while you are reading colors for real. Note that you may never saturate, even with
     * gain at 64X. It all depends on the lighting on the object you read.
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

    private int maxRGBCValue = 0;

    private ArrayList<Gain> gainArrayList;
    private int currentGainIndex = 0;
    private ArrayList<IntegrationTime> integrationTimeArrayList;
    private int currentIntegrationTimeIndex = 0;

    private I2cDevice colorSensor;
    private I2cDeviceSynch colorSensorClient;
    private AMSColorSensorParameters parameters;
    boolean isOwned = false;

    // For controlling the LED
    private DeviceInterfaceModule coreDIM;
    private boolean ledOn = false;
    private int ioChannelForLed;

    // For tracking the update rate
    private ElapsedTime updateTimer;
    private int lastAlpha = 0;
    private StatTracker updateTimeTracker;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public int getMaxRGBCValue() {
        return maxRGBCValue;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public AdafruitColorSensor8863(HardwareMap hardwareMap, String colorSensorName, String coreDIMName, int ioChannelForLed) {
        // set up for controlling the LED
        this.ledOn = false;
        this.ioChannelForLed = ioChannelForLed;
        coreDIM = hardwareMap.deviceInterfaceModule.get(coreDIMName);
        coreDIM.setDigitalChannelMode(ioChannelForLed, DigitalChannelController.Mode.OUTPUT);
        // Delay so the previous line can finish before setting the led off. Otherwise the LED does
        // not get shut off.
        delay(100);
        coreDIM.setDigitalChannelState(ioChannelForLed, ledOn);

        // setup for the color sensor
        parameters = AMSColorSensorParameters.createForAdaFruit();
        colorSensor = hardwareMap.get(I2cDevice.class, colorSensorName);
        colorSensorClient = new I2cDeviceSynchImpl(colorSensor, parameters.getI2cAddr(), isOwned);
        colorSensorClient.engage();
        initialize();

        // setup for tracking the update rate of the sensor
        // Timer used to time the update rate
        updateTimer = new ElapsedTime();
        // A tracker used to track the update rate of the color sensor.
        updateTimeTracker = new StatTracker();
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
        // Get the index into the enum for the selected integration time
        // This may get used later to change the integration time
        setGain(parameters.getGain());
        // Get the index into the enum for the selected gain
        // This may get used later to change the gain
        // set up a read ahead?
        enable();
    }

    private void setIntegrationTime(IntegrationTime time) {
        this.write8(Register.INTEGRATION_TIME, time.byteVal);
        // calculate maximum possible color value for use later in scaling
        this.maxRGBCValue = calculateMaxRGBCCount(time);
    }

    // following is start of code needed to be able to increment through Gains and Integration times
    // It is not complete yet.
    private void createGainArrayList() {
        gainArrayList = new ArrayList<Gain>();
        for(Gain g : Gain.values()) {
            gainArrayList.add(g);
        }
    }

    private int getCurrentGainIndex(Gain gain) {
        int index = 0;
        int result = 0;
        for(Gain g: Gain.values()) {
            if (g.equals(gain)) {
                result = index;
            } else {
                index ++;
            }
        }
        return result;
    }

    private void createIntegrationTimeArrayList() {
        integrationTimeArrayList = new ArrayList<IntegrationTime>();
        for(IntegrationTime i : IntegrationTime.values()) {
            integrationTimeArrayList.add(i);
        }
    }

    private int getCurrentIntegrationTimeIndex(IntegrationTime integrationTime) {
        int index = 0;
        int result = 0;
        for(IntegrationTime i: IntegrationTime.values()) {
            if (i.equals(integrationTime)) {
                result = index;
            } else {
                index ++;
            }
        }
        return result;
    }

//    public void nextGain() {
//        currentIntegrationTimeIndex ++;
//        setGain(gainArrayList[currentIntegrationTimeIndex].value);
//    }

    private void setGain(Gain gain) {
        this.write8(Register.CONTROL, gain.byteVal);
        // save the gain
        parameters.setGain(gain);
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

    private int calculateMaxRGBCCount(IntegrationTime integrationTime) {
        // since byte is 2's complement in java, have to convert it to unsigned for this equation
        // Casting to int and & 0xFF does that.
        return calculateMaxRGBCCount((int) integrationTime.byteVal & 0xFF);
    }

    /**
     * The maximum possible value of a color depends on the integration time that is selected. This
     * function returns that value given the integration time. This will get used in scaling the
     * values from the sensor to standard RGB (0-255).
     * Equation: Max possible RGBC value = (256 - integration time (hex->decimal)) * 1024
     *
     * @param integrationTime max possible value of a color
     * @return
     */
    private int calculateMaxRGBCCount(int integrationTime) {
        this.maxRGBCValue = (256 - integrationTime) * 1024;
        if (maxRGBCValue > 65535) {
            this.maxRGBCValue = 65535;
        }
        return maxRGBCValue;
    }

    private int calculateScaledRGBColor(int colorValue) {
        double scaled;
        // have to cast one of the numbers to double in order to get a double result
        scaled = (double) colorValue / maxRGBCValue * 255;
        // cast the result back to int
        return (int) Math.round(scaled);
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
    //*********************************************************************************************
    //          LED Control
    //*********************************************************************************************

    public void turnLEDOn() {
        // only put commands on the bus if there is a change to be made
        if (!this.ledOn) {
            // the led is off so it makes sense to turn it on
            this.ledOn = true;
            coreDIM.setDigitalChannelState(ioChannelForLed, ledOn);
        }
    }

    public void turnLEDOff() {
        // only put commands on the bus if there is a change to be made
        if (this.ledOn) {
            // the led is on so it makes sense to turn it off
            this.ledOn = false;
            coreDIM.setDigitalChannelState(ioChannelForLed, ledOn);
        }
    }

    //*********************************************************************************************
    //          Reading colors
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
        int alpha = this.readColorRegister(Register.CLEARL);
        //use reads of alpha to update the tracker that is tracking update times
        if (alpha != lastAlpha) {
            // alpha changed so update the tracker
            updateTimeTracker.compareValue(updateTimer.milliseconds());
            updateTimer.reset();
            // Since alpha changed save the new lastAlpha
            lastAlpha = alpha;
        }
        return alpha;
    }

    public String rgbValuesAsString() {
        return red() + " / " + blue() + " / " + green();
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

    public int redScaled() {
        return calculateScaledRGBColor(red());
    }

    public int greenScaled() {
        return calculateScaledRGBColor(green());
    }

    public int blueScaled() {
        return calculateScaledRGBColor(blue());
    }

    public int alphaScaled() {
        return calculateScaledRGBColor(alpha());
    }

    public String rgbValuesScaledAsString() {
        return redScaled() + " / " + greenScaled() + " / " + blueScaled();
    }

    public float[] hsvScaled() {
        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};
        // convert the RGB values to HSV values.
        Color.RGBToHSV(redScaled(), greenScaled(), blueScaled(), hsvValues);
        return hsvValues;
    }

    public String hsvValuesScaledAsString() {
        float hsvValues[] = hsvScaled();
        return String.format("%4.1f", hsvValues[0]) + " / " + String.format("%4.2f", hsvValues[1]) + " / " + String.format("%4.2f", hsvValues[2]);
    }

    public float hueScaled() {
        return hsvScaled()[0];
    }

    public float saturatationScaled() {
        return hsvScaled()[1];
    }

    public float valueScaled() {
        return hsvScaled()[2];
    }

    //*********************************************************************************************
    //          Color Testing - should probably put this into another class
    //*********************************************************************************************

    private int getMaxOfThree(int a, int b, int c) {
        int max = a;
        int which = 1;
        if(b > a) {
            max = b;
            which = 2;
        }
        if (c > max ) {
            max = c;
            which = 3;
        }
        return which;
    }

    public boolean isRedUsingRGB() {
        boolean result = false;
        if(getMaxOfThree(redScaled(), greenScaled(), blueScaled()) == 1) {
            result = true;
        }
        return result;
    }

    public boolean isGreenUsingRGB() {
        boolean result = false;
        if(getMaxOfThree(redScaled(), greenScaled(), blueScaled()) == 2) {
            result = true;
        }
        return result;
    }

    public boolean isBlueUsingRGB() {
        boolean result = false;
        if(getMaxOfThree(redScaled(), greenScaled(), blueScaled()) == 3) {
            result = true;
        }
        return result;
    }

    public String colorTestResultUsingRGBCaption() {
        return "Red? / Green? / Blue? (RGB values)";
    }

    public String colorTestResultUsingRGB() {
        return String.valueOf(isRedUsingRGB()) + " / " + String.valueOf(isGreenUsingRGB()) + " / " + String.valueOf(isBlueUsingRGB());
    }

    public boolean isRedUsingHSV() {
        float hue = hueScaled();
        if(hue >= 0 && hue <= 60 || hue >= 340 && hue <= 360) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isGreenUsingHSV() {
        float hue = hueScaled();
        if(hue > 120 && hue <= 180) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBlueUsingHSV() {
        float hue = hueScaled();
        if(hue > 220 && hue <= 300) {
            return true;
        } else {
            return false;
        }
    }

    public String colorTestResultUsingHSVCaption() {
        return "Red? / Green? / Blue? (HSV values)";
    }

    public String colorTestResultUsingHSV() {
        return String.valueOf(isRedUsingHSV()) + " / " + String.valueOf(isGreenUsingHSV()) + " / " + String.valueOf(isBlueUsingHSV());
    }

    //*********************************************************************************************
    //          setting gain and integration time
    //*********************************************************************************************

    public void setGain1x() {
        setGain(Gain.AMS_COLOR_GAIN_1);
    }

    public void setGain4x() {
        setGain(Gain.AMS_COLOR_GAIN_4);
    }

    public void setGain16x() {
        setGain(Gain.AMS_COLOR_GAIN_16);
    }

    public void setGain64x() {
        setGain(Gain.AMS_COLOR_GAIN_64);
    }

    public String getCurrentGainAsString() {
        String gainAsString = "nothing";
        switch (parameters.getGain()) {
            case AMS_COLOR_GAIN_1:
                gainAsString = "1X";
                break;
            case AMS_COLOR_GAIN_4:
                gainAsString = "4X";
                break;
            case AMS_COLOR_GAIN_16:
                gainAsString = "16X";
                break;
            case AMS_COLOR_GAIN_64:
                gainAsString = "64X";
                break;
        }
        return gainAsString;
    }

    public String getCurrentIntegrationTimeAsString() {
        String integrationTimeAsString = "nothing";
        switch (parameters.getIntegrationTime()) {
            case AMS_COLOR_ITIME_2_4MS:
                integrationTimeAsString = "2.4ms";
                break;
            case AMS_COLOR_ITIME_24MS:
                integrationTimeAsString = "24ms";
                break;
            case AMS_COLOR_ITIME_50MS:
                integrationTimeAsString = "50ms";
                break;
            case AMS_COLOR_ITIME_101MS:
                integrationTimeAsString = "101ms";
                break;
            case AMS_COLOR_ITIME_154MS:
                integrationTimeAsString = "154ms";
                break;
            case AMS_COLOR_ITIME_307MS:
                integrationTimeAsString = "307ms";
                break;
            case AMS_COLOR_ITIME_460MS:
                integrationTimeAsString = "460ms";
                break;
            case AMS_COLOR_ITIME_537MS:
                integrationTimeAsString = "537ms";
                break;
            case AMS_COLOR_ITIME_700MS:
                integrationTimeAsString = "700ms";
                break;
        }
        return integrationTimeAsString;
    }

    //*********************************************************************************************
    //          Update Rate tracking
    //*********************************************************************************************

    public double getUpdateRateMin() {
        return updateTimeTracker.getMinimum();
    }

    public double getUpdateRateMax() {
        return updateTimeTracker.getMaximum();
    }

    public double getUpdateRateAve() {
        return updateTimeTracker.getAverage();
    }

    public String updateRateString() {
        return String.format("%4.0f", getUpdateRateMin()) + " / " + String.format("%4.0f", getUpdateRateAve()) + " / " + String.format("%4.0f", getUpdateRateMax());
    }
}
