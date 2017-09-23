package org.firstinspires.ftc.teamcode.Lib.FTCLib;
/*
Copyright (c) 2017 Glenn Ball
Written to support FTC team 8863, Tully Precision Cut-Ups

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Some of this code has been borrowed from the FTC SDK.
*/

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

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.lang.reflect.GenericArrayType;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Things to do:
//    investigate the update rate - it appears odd
//    put in an instantaneous update rate

/**
 * This class provides an interface to the Adafruit TCS34725 Color Sensor:
 * https://www.adafruit.com/products/1334
 * There are a number of classes that have been written for this color sensor, including two that
 * are in the FTC SDK (AdafruitI2cColorSensor and AMSColorSensor). FYI, AdafruitI2cColorSensor is
 * what you get when you config a phone for Adafruit Color Sensor. You cannot set the gain or update
 * rate (integration time) using this class. The update rate used in that class is 600 mSec and is
 * way too slow to be useable for a robot. AMSColorSensor is a class in the SDK that allows you
 * to set the gain and integration time, but there is no way to get an instance of this class. The
 * other third party classes are not based on the newer I2C communication classes. None of the classes
 * I found properly scale the RGB values read from the sensor given the gain and integration time
 * that have been selected. So I have chosen to write my own class.
 * <p>
 * This class assumes that you want to control the LED on the adafruit circuit board. In order to do
 * this you must connect a wire from the LED pin on the circuit board to a digital input port on the
 * modern robotics core device interface (DIM) module. The wire gets connected to the "SIGNAL" pin
 * of the DIM digital input/output port. You then pass in the core DIM name you configured
 * on your phone and the port number you connected the wire to.
 * <p>
 * Another way to control the LED and not use up a valuable core DIM digital port is to use the
 * interrupt PIN to drive the LED pin on the circuit board. This is a bit of a trick but it works.
 * You need to install a 2 pin shorting jumper between the LED pin and the INT pin of the circuit
 * board. Use the turnLEDOnByInterrupt() and turnLEDOffByInterrupt() methods.
 * <p>
 * IF YOU JUST WANT TO GET STARTED USING THE COLOR SENSOR SKIP RIGHT DOWN TO THE SECTION TITLED
 * MAJOR METHODS.
 * <p>
 * If you care to explore and understand the sensor read on. PARTICULARLY IMPORTANT
 * TO GETTING THE BEST RESULTS FROM THIS SENSOR ARE UNDERSTANDING INTEGRATION TIME AND GAIN. See
 * below and the descriptions in the associated registers in the enum section.
 * <p>
 * The TCS34725 color sensor chip allows you to set the gain (how much amplification the color
 * signals get) and the integration time (in simple terms how long the color sensor values are
 * averaged between readings). If light levels are low you can pick a higher gain to get better
 * resolution of the color readings. BUT if you go too high you will saturate the color sensor. In
 * other words you will get the same reading no matter what the actual color value is. It is a
 * tradeoff. More gain = better resolution unless you saturate and then your readings are garbage.
 * <p>
 * If you want more accurate color values you can pick a longer
 * integration time. The trade off there is that you don't get values as quickly and that can be a
 * problem for your robot. For example if you choose a 600 mSec integration time, you only get a
 * color reading once every 600mSec. A lot can happen in that time so a long integration time is
 * only good if you don't expect much to change very quickly. I found that a nice balance is 24
 * mSec. That is about one robot loop cycle.
 * <p>
 * The raw RGB (red, green, blue) and clear values read from each of the
 * 4 sensors built into the chip have a max value that is dependent on the gain and integration time.
 * The max value occurs for a long integration time and is 65535. For a 24 mSec integration time
 * the max value will be 10240. If you vary the integration time, you should not just use
 * the raw readings. They need to be scaled from the maximum. For example, you read a raw red value
 * of 1200. If your integration time is 24 mSec then the max possible value is 10240. Your scaled
 * reading is 1200/10240. If your integration time is 600 mSec then your scaled reading is
 * 1200/65535. That is a whole lot different from the 24 mSec reading. If you are just comparing the
 * red value to the blue and the green values, the scaling does not really matter. But if you are
 * going to calculate HSV color then it is crucial. HSV (Hue, Saturation, Value) is a different way
 * of representing color than RGB and in some cases is better and easier to use. This driver properly
 * scales the reading for you. It can also give you HSV values. None of the other drivers I found
 * did this correctly.
 * <p>
 * The register definitions below offer more description of the sensor and help you determine what
 * is important for FTC and what is not.
 * <p>
 * For support, email ftc8863@gmail.com. I can't promise support but I'll do my best.
 * Glenn Ball
 */
public class AdafruitColorSensor8863 {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    // The enums below provide symbolic names for device registers and for register bits and values.
    // All information is taken from the datasheet for the device.
    // https://cdn-shop.adafruit.com/datasheets/TCS34725.pdf

    /**
     * Command Register (address = I2C address of device)
     * This defines the bits in the command register and defines an enum for their function. OR them
     * together to get the full command byte. To address another register, OR that address into the
     * command byte.
     */
    public enum CommandRegister {
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
    public enum EnableRegister {
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
     * Equation for calculating ATIME (byte value of register) = (256-(integration time in ms)) / 2.4ms
     */
    public enum IntegrationTime {
        AMS_COLOR_ITIME_2_4MS(0xFF), //2.4 mSec, max possible value = 1024
        AMS_COLOR_ITIME_24MS(0xF6), // 24 mSec, max possible value = 10240
        AMS_COLOR_ITIME_50MS(0xEB), // 50 mSec, max possible value = 21504
        AMS_COLOR_ITIME_101MS(0xD5), // 101 mSec, max possible value = 44032 (data sheet has error in table 6)
        AMS_COLOR_ITIME_154MS(0xC0), // 154 mSec, max possible value = 65535
        AMS_COLOR_ITIME_307MS(0x80), // 307 mSec, max possible value = 65535
        AMS_COLOR_ITIME_460MS(0x40), // 460 mSec, max possible value = 65535
        AMS_COLOR_ITIME_537MS(0x20), // 537 mSec, max possible value = 65535
        AMS_COLOR_ITIME_700MS(0x00), // 700 mSec, max possible value = 65535
        AMS_COLOR_ITIME_UNKNOWN(0xFE); // Not really unknown but not a preset value so treat it as
        // unknown

        public final byte byteVal;

        // declare a lookup table so I can lookup the enum value given the byte value
        private static final Map<Byte, IntegrationTime> lookup = new HashMap<Byte, IntegrationTime>();

        // populate the lookup table using a static block
        static {
            for (IntegrationTime integrationTimeEnum : IntegrationTime.values()) {
                lookup.put(integrationTimeEnum.byteVal, integrationTimeEnum);
            }
        }

        // constructor for the class
        IntegrationTime(int i) {
            this.byteVal = (byte) i;
        }

        /**
         * Get the enum value given the byte value.
         *
         * @param byteVal
         * @return IntegrationTime enum value corresponding to the byte value of the integration
         * time or UNKNOWN if the byte value does not match any of the enums.
         */
        public static IntegrationTime valueOf(byte byteVal) {
            // Does the value from the register match one of the enums defined?
            if (lookup.containsKey(byteVal)) {
                return lookup.get(byteVal);
            } else {
                // no - return uknown
                return AMS_COLOR_ITIME_UNKNOWN;
            }
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
    public enum WaitTime {
        AMS_COLOR_WTIME_2_4MS(0xFF),     // if WLONG=0, wait = 2.4ms; if WLONG=1 wait = 0.029s
        AMS_COLOR_WTIME_204MS(0xAB),     // if WLONG=0, wait = 204ms; if WLONG=1 wait = 2.45s
        AMS_COLOR_WTIME_614MS(0x00),     // if WLONG=0, wait = 614ms; if WLONG=1 wait = 7.4s
        AMS_COLOR_WTIME_UNKNOWN(0xFE);   // Not really unknown but not a preset value so treat it as
        // unknown

        public final byte byteVal;

        // declare a lookup table so I can lookup the enum value given the byte value
        private static final Map<Byte, WaitTime> lookup = new HashMap<Byte, WaitTime>();

        // populate the lookup table using a static block
        static {
            for (WaitTime waitTimeEnum : WaitTime.values()) {
                lookup.put(waitTimeEnum.byteVal, waitTimeEnum);
            }
        }

        WaitTime(int i) {
            this.byteVal = (byte) i;
        }

        /**
         * Get the enum value given the byte value.
         *
         * @param byteVal
         * @return IntegrationTime enum value corresponding to the byte value of the wait
         * time or UNKNOWN if the byte value does not match any of the enums.
         */
        public static WaitTime valueOf(byte byteVal) {
            // Does the value from the register match one of the enums defined?
            if (lookup.containsKey(byteVal)) {
                return lookup.get(byteVal);
            } else {
                // no - return uknown
                return AMS_COLOR_WTIME_UNKNOWN;
            }
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
    public enum Persistence {
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
        AMS_COLOR_PERS_60_CYCLE(0b1111),    // 60 clean channel values outside threshold range generates an interrupt
        AMS_COLOR_PERS_UNKNOWN(0xFF);       // Not really unknown but not a preset value so treat it as unknown

        public final byte byteVal;


        // declare a lookup table so I can lookup the enum value given the byte value
        private static final Map<Byte, Persistence> lookup = new HashMap<Byte, Persistence>();

        // populate the lookup table using a static block
        static {
            for (Persistence persistenceEnum : Persistence.values()) {
                lookup.put(persistenceEnum.byteVal, persistenceEnum);
            }
        }

        Persistence(int i) {
            this.byteVal = (byte) i;
        }

        /**
         * Get the enum value given the byte value.
         *
         * @param byteVal
         * @return IntegrationTime enum value corresponding to the byte value of the persistence
         * or UNKNOWN if the byte value does not match any of the enums.
         */
        public static Persistence valueOf(byte byteVal) {
            // Does the value from the register match one of the enums defined?
            if (lookup.containsKey(byteVal)) {
                return lookup.get(byteVal);
            } else {
                // no - return uknown
                return AMS_COLOR_PERS_UNKNOWN;
            }
        }
    }

    /**
     * Configuration Register (address = 0x0D)
     * The configuration register sets the wait long time.
     * Provides symbolic names for the values the register can have.
     * See wait time register for an explanation of this. Short take: you almost certainly want
     * normal wait times for FTC.
     */
    public enum Configuration {
        // bits 7:2 reserved, write as 0
        // bit 1 - WLONG, if set wait times increased by 12x
        // bit 0 - reserved, write as 0
        AMS_COLOR_CONFIG_NORMAL(0x00),    // normal wait times
        AMS_COLOR_CONFIG_WLONG(0x02),     // Extended wait time(12x normal wait times via AMS_COLOR_WTIME
        AMS_COLOR_CONFIG_UNKNOWN(0xFF);   // Not really unknown but not a preset value so treat it as unknown

        public final byte byteVal;

        // declare a lookup table so I can lookup the enum value given the byte value
        private static final Map<Byte, Configuration> lookup = new HashMap<Byte, Configuration>();

        // populate the lookup table using a static block
        static {
            for (Configuration configurationEnum : Configuration.values()) {
                lookup.put(configurationEnum.byteVal, configurationEnum);
            }
        }

        // constructor for the class
        Configuration(int i) {
            this.byteVal = (byte) i;
        }

        /**
         * Get the enum value given the byte value.
         *
         * @param byteVal
         * @return IntegrationTime enum value corresponding to the byte value of the wait
         * time or UNKNOWN if the byte value does not match any of the enums.
         */
        public static Configuration valueOf(byte byteVal) {
            // Does the value from the register match one of the enums defined?
            if (lookup.containsKey(byteVal)) {
                return lookup.get(byteVal);
            } else {
                // no - return uknown
                return AMS_COLOR_CONFIG_UNKNOWN;
            }
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
    public enum Gain {
        // bits 7:2 reserved, write as 0
        // bits 1:0 - RGBC gain control
        AMS_COLOR_GAIN_1(0x00),  // 1X
        AMS_COLOR_GAIN_4(0x01),  // 4X
        AMS_COLOR_GAIN_16(0x02), // 16X
        AMS_COLOR_GAIN_64(0x03), // 64X
        AMS_COLOR_GAIN_UNKNOWN(0xFF); // Not really unknown but not a preset value so treat it as unknown

        public final byte byteVal;

        // declare a lookup table so I can lookup the enum value given the byte value
        private static final Map<Byte, Gain> lookup = new HashMap<Byte, Gain>();

        // populate the lookup table using a static block
        static {
            for (Gain gainEnum : Gain.values()) {
                lookup.put(gainEnum.byteVal, gainEnum);
            }
        }

        // constructor for the class
        Gain(int i) {
            this.byteVal = (byte) i;
        }

        /**
         * Get the enum value given the byte value.
         *
         * @param byteVal
         * @return IntegrationTime enum value corresponding to the byte value of the wait
         * time or UNKNOWN if the byte value does not match any of the enums.
         */
        public static Gain valueOf(byte byteVal) {
            // Does the value from the register match one of the enums defined?
            if (lookup.containsKey(byteVal)) {
                return lookup.get(byteVal);
            } else {
                // no - return uknown
                return AMS_COLOR_GAIN_UNKNOWN;
            }
        }
    }

    /**
     * ID Register (address = 0x12)
     * The ID register provides the value for the part number. The ID register is a read-only
     * register.
     */
    public enum DeviceID {
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
    public enum Status {
        // bits 7:5 - reserved
        // bit 4 - AINT
        // bits 3:1 - reserved
        // bit 0 - AVALID
        AMS_COLOR_STATUS_INTERRUPT(0x10),        // RGBC Clear channel interrupt
        AMS_COLOR_STATUS_DATA_VALID(0x01);  // Indicates that the RGBC channels have completed an integration cycle

        public final byte byteVal;

        Status(int i) {
            this.byteVal = (byte) i;
        }
    }

    // RGBC Channel Data Registers (addresses = 0x14 - 0x1B)
    // Clear, red, green and blue data is stored as 16 bit values. To ensure the data is read
    // correctly, a two-byte read I2C transaction should be used with a read word protocol bit set
    // in the command register. With this operation, when the lower byte register is read, the
    // upper eight bits are stored into a shadow register, which is read by a subsequent read to the
    // upper byte. The upper register will read the correct value even if additional ADC integration
    // cycles end between the reading of the lower and upper bytes.
    // see below for detailed addressing

    /**
     * REGISTER provides symbolic names for device registers
     */
    public enum Register {
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

    /**
     * The core DIM has a blue and a red led built into it. You can use these to give a visual
     * indication of what color the color sensor is reading. Turn the red led on if the color
     * sensor is reading red, or turn the blue led on if the core DIM is reading blue. Just one
     * idea of what to do with them.
     */
    private enum CoreDIMLEDChannel {
        BLUE(0x00),
        RED(0x01);

        public final byte byteVal;

        CoreDIMLEDChannel(int i) {
            this.byteVal = (byte) i;
        }
    }

    /**
     * Controls whether there is a full set of color data put into the telemetry object buffer or
     * just a minimum set.
     */
    public enum AmountOfDataToDisplay {
        NORMAL,
        MIN
    }

    /**
     * A quick and dirty color to be returned to the user
     */
    public enum ColorFromSensor {
        RED,
        GREEN,
        BLUE,
        UNKNOWN
    }

    /**
     * How is the LED on the Adafruit circuit board controlled?
     */
    public enum LEDControl {
        NONE, // no control
        CORE_DIM, // controlled by connecting the LED pin on the board to a port on the core DIM
        INTERRUPT // controlled by shorting the INT pin to the LED pin
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    // properties and initializations for this color sensor
    /**
     * Initial gain will be 64X
     */
    private Gain gain = Gain.AMS_COLOR_GAIN_64;

    /**
     * Initial integration time will be 24 mSec.
     */
    private IntegrationTime integrationTime = IntegrationTime.AMS_COLOR_ITIME_24MS;

    /**
     * The I2C address of the Adafruit board is fixed and cannot be changed - bummer when you have
     * more than one of the boards connected to the core DIM! For that you need an I2C mux in
     * between the color sensors and the core DIM.
     */
    private int AMS_TCS34725_ADDRESS = 0x29;

    /**
     * The Chip ID is also a fixed property of the color sensor chip.
     */
    private byte deviceID = 0x44;

    private int maxRGBCValue = 0;

    private I2cAddr i2cAddr = I2cAddr.create7bit(AMS_TCS34725_ADDRESS);
    private I2cDevice colorSensor;
    private I2cDeviceSynch colorSensorClient;
    boolean isOwned = false;

    // For controlling the LED on the adafruit circuit board
    private DeviceInterfaceModule coreDIM;
    private boolean ledOn = false;
    private int ioChannelForLed;
    private LEDControl ledControl;

    // For tracking the update rate
    private ElapsedTime updateTimer;
    private int lastAlpha = 0;
    private StatTracker updateTimeTracker;

    // For testing color that has been sensed
    private double isRedRatioLimit = 1.2;
    private double isBlueRatioLimit = .85;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public int getMaxRGBCValue() {
        return maxRGBCValue;
    }


    public double getIsRedRatioLimit() {
        return isRedRatioLimit;
    }

    public void setIsRedRatioLimit(double isRedRatioLimit) {
        this.isRedRatioLimit = isRedRatioLimit;
    }

    public double getIsBlueRatioLimit() {
        return isBlueRatioLimit;
    }

    public void setIsBlueRatioLimit(double isBlueRatioLimit) {
        this.isBlueRatioLimit = isBlueRatioLimit;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * This constructor is intended to be used when the LED is going to be controlled by using the
     * a digital input on the core DIM module. The LED is initialized to off.
     *
     * @param hardwareMap
     * @param colorSensorName
     * @param coreDIMName
     * @param ioChannelForLed
     */
    public AdafruitColorSensor8863(HardwareMap hardwareMap, String colorSensorName, String coreDIMName, int ioChannelForLed) {
        // set up for controlling the LED. The LED is on at power up.
        this.ledOn = true;
        this.ledControl = LEDControl.CORE_DIM;
        setupCoreDIM(hardwareMap, coreDIMName, ioChannelForLed);

        // setup for the color sensor
        createClient(hardwareMap, colorSensorName);
        initialize();

        // setup for tracking the update rate of the sensor
        // Timer used to time the update rate
        updateTimer = new ElapsedTime();
        // A tracker used to track the update rate of the color sensor.
        updateTimeTracker = new StatTracker();
    }

    /**
     * This constructor is intended to be used when the LED is going to be controlled by using the
     * interrupt pin on the circuit board. A 2 pin jumper must be installed on the INT and LED pins.
     * The advantage of this approach is that a core DIM digital input is not taken up to control
     * the LED. The LED is intialized to off.
     *
     * @param hardwareMap
     * @param colorSensorName string that was used to setup the color sensor on the phone
     * @param ledControlMode NONE = no control over LED, INTERRUPT = use interrupt pin to control
     *                       led. CORE_DIM is not a valid choice for this version of the constructor
     */
    public AdafruitColorSensor8863(HardwareMap hardwareMap, String colorSensorName, LEDControl ledControlMode) {

        // the user cannot use a digital port on the core DIM to control the LED when using this
        // constructor. Only NONE or INTERRUPT is allowed.
        if (ledControlMode == LEDControl.CORE_DIM) {
            throw new IllegalArgumentException("In order to control the color sensor LED with a Core DIM digital port, you must use the proper AdafruitColorSensor8863 constructor!");
        }
        // setup for the color sensor
        this.ledControl = ledControlMode;
        createClient(hardwareMap, colorSensorName);
        initialize();

        // setup for tracking the update rate of the sensor
        // Timer used to time the update rate
        updateTimer = new ElapsedTime();
        // A tracker used to track the update rate of the color sensor.
        updateTimeTracker = new StatTracker();

        // turn the led off. The LED is on at power up.
        this.ledOn = true;
        turnLEDOff();
    }

    /**
     * This constructor is intended to be used when the LED is going to be controlled by using the
     * interrupt pin on the circuit board. A 2 pin jumper must be installed on the INT and LED pins.
     * The advantage of this approach is that a core DIM digital input is not taken up to control
     * the LED. The LED is intialized to off.
     *
     * The difference between this constructor and the previous one is that this one sets up the core
     * DIM object so you can control the LEDs built into the core DIM.
     *
     * @param hardwareMap
     * @param colorSensorName string that was used to setup the color sensor on the phone
     * @param coreDIMName the name of the core DIM as configured on the phone. You will be able to
     *                    control the built in blue and red LEDs if you use this version of the
     *                    constructor
     * @param ledControlMode NONE = no control over LED, INTERRUPT = use interrupt pin to control
     *                       led. CORE_DIM is not a valid choice for this version of the constructor
     */
    public AdafruitColorSensor8863(HardwareMap hardwareMap, String colorSensorName, String coreDIMName, LEDControl ledControlMode) {
        // the constructor is the same as the previous one - except for the core DIM
        this(hardwareMap, colorSensorName, ledControlMode);
        // the core DIM leds can be turned on and off using this constructor
        coreDIM = hardwareMap.deviceInterfaceModule.get(coreDIMName);
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private void setupCoreDIM(HardwareMap hardwareMap, String coreDIMName, int ioChannelForLed) {
        this.ioChannelForLed = ioChannelForLed;
        coreDIM = hardwareMap.deviceInterfaceModule.get(coreDIMName);
        coreDIM.setDigitalChannelMode(ioChannelForLed, DigitalChannelController.Mode.OUTPUT);
        // Delay so the previous line can finish before setting the led off. Otherwise the LED does
        // not get shut off.
        delay(100);
        coreDIM.setDigitalChannelState(ioChannelForLed, ledOn);
    }

    private void createClient(HardwareMap hardwareMap, String colorSensorName) {
        colorSensor = hardwareMap.get(I2cDevice.class, colorSensorName);
        colorSensorClient = new I2cDeviceSynchImpl(colorSensor, i2cAddr, isOwned);
        colorSensorClient.engage();
    }

    private void initialize() {
        // check if isArmed() ?
        // check if the proper chip is out there by checking the chip id
        if (!checkDeviceId()) {
            // do something, not sure what yet
            doSomething();
        }
        // Set the gain and integration time
        setIntegrationTime(integrationTime);
        setGain(gain);
        // disable interrupt?
        // initialize needed?
        // persistence?
        // disable the wait timer?
        // set wait time register?
        // set the configuration to normal rather than wlong?
        enable();
    }

    /**
     * You may have a color sensor attached to the I2C bus, but it may not be initialized properly.
     * This method reads the data for the color sensors and takes a guess at whether the data is
     * valid or not. If it is not valid, odds are the color sensor was not initialized properly.
     *
     * @return true = data is valid
     */
    public boolean isDataValid() {
        int red = redScaled();
        int blue = blueScaled();
        int green = greenScaled();
        boolean result = true;
        if (red == 0 && blue == 0 && green == 0) {
            // They can't all be 0 if  the device is connected properly and initialized properly
            // Odds are there is something wrong
            result = false;
        }
        if (red == blue && red == green) {
            // It would be extremely unlikely that the values would all be equal to each other
            // Most likely the sensor is not responding
            result = false;
        }
        return result;
    }

    /**
     * Add a line to the telemetry buffer indicating is the color sensor is on the I2C bus and if
     * the data read from it is likely to be valid.
     *
     * @param colorSensorName
     * @param telemetry
     */
    public void reportStatus(String colorSensorName, Telemetry telemetry) {
        String buffer = colorSensorName + " device id is ";
        if (checkDeviceId()) {
            telemetry.addData(buffer, "ok");
        } else {
            telemetry.addData(buffer, "BAD!");
        }

        buffer = colorSensorName + " data is ";
        if (isDataValid()) {
            buffer = buffer + "valid ";
        } else {
            buffer = buffer + "NOT VALID ";
        }
        buffer = buffer + " " + redScaled() + "/" + greenScaled() + "/" + blueScaled();
        telemetry.addData(buffer, "!");
    }

    //---------------------------------------------------------------------------------
    //
    //  METHODS that communicate with the color sensor registers. These methods:
    //  - read and write raw bit values from and to the register
    //  - read and interpret the bit values into something more understandable
    //
    //---------------------------------------------------------------------------------

    // ENABLE REGISTER

    /**
     * Read the enable register and return it as a byte
     *
     * @return enable register bits as byte
     */
    private synchronized byte getEnableRegisterFromSensor() {
        byte reg = colorSensorClient.read8(Register.ENABLE.byteVal);
        return reg;
    }

    /**
     * Turn the color sensor on.
     */
    public synchronized void enable() {
        this.write8(Register.ENABLE, EnableRegister.AMS_COLOR_ENABLE_PON.byteVal);
        delayLore(6); // Adafruit's sample implementation uses 3ms
        this.write8(Register.ENABLE, EnableRegister.AMS_COLOR_ENABLE_PON.byteVal | EnableRegister.AMS_COLOR_ENABLE_AEN.byteVal);
    }

    /**
     * Turn the color sensor off. Don't do this. There is not a need to save power for FTC because
     * the power the color sensor uses is minimal.
     */
    private synchronized void disable() {
        /* Turn the device off to save power */
        byte reg = getEnableRegisterFromSensor();
        this.write8(Register.ENABLE, reg & ~(EnableRegister.AMS_COLOR_ENABLE_PON.byteVal | EnableRegister.AMS_COLOR_ENABLE_AEN.byteVal));
    }

    /**
     * Is the color sensor powered on? (Technically is the oscillator running?)
     *
     * @return true if powered on
     */
    private synchronized boolean isPowerEnabled() {
        byte reg = getEnableRegisterFromSensor();
        if ((reg & EnableRegister.AMS_COLOR_ENABLE_PON.byteVal) == EnableRegister.AMS_COLOR_ENABLE_PON.byteVal) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Is the color sensor enabled:
     *
     * @return true if enabled
     */
    private synchronized boolean isColorSensorEnabled() {
        byte reg = getEnableRegisterFromSensor();
        if ((reg & EnableRegister.AMS_COLOR_ENABLE_AEN.byteVal) == EnableRegister.AMS_COLOR_ENABLE_AEN.byteVal) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Enable the interrupt that gets set when the clear value is under or over the threshold. This
     * allows the INT pin on the circuit board to get driven.
     */
    private synchronized void enableInterrupt() {
        byte reg = getEnableRegisterFromSensor();
        this.write8(Register.ENABLE, reg | EnableRegister.AMS_COLOR_ENABLE_AIEN.byteVal);
    }

    /**
     * Disable the interrupt. The INT pin on the circuit board will not be driven and will float.
     */
    private synchronized void disableInterrupt() {
        byte reg = getEnableRegisterFromSensor();
        this.write8(Register.ENABLE, reg & ~EnableRegister.AMS_COLOR_ENABLE_AIEN.byteVal);
    }

    /**
     * Is the interrupt enabled?
     *
     * @return true if enabled
     */
    public synchronized boolean isInterruptEnabled() {
        byte reg = getEnableRegisterFromSensor();
        if ((reg & EnableRegister.AMS_COLOR_ENABLE_AIEN.byteVal) == EnableRegister.AMS_COLOR_ENABLE_AIEN.byteVal) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * The wait timer controls if there is a period of time that the sensor waits before starting
     * a new color integration cycle. You can enable it or disable it. The benefit of this is that
     * it saves power. For FTC we don't care about saving power. Keep the wait timer disabled.
     *
     * @return true if enabled
     */
    private synchronized boolean isWaitTimerEnabled() {
        byte reg = getEnableRegisterFromSensor();
        if ((reg & EnableRegister.AMS_COLOR_ENABLE_WEN.byteVal) == EnableRegister.AMS_COLOR_ENABLE_WEN.byteVal) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Enable the wait timer as described in isWaitTimerEnabled(). Not recommended for FTC. Leave it
     * disabled.
     */
    private synchronized void enableWaitTimer() {
        byte reg = getEnableRegisterFromSensor();
        this.write8(Register.ENABLE, (reg | EnableRegister.AMS_COLOR_ENABLE_WEN.byteVal));
    }

    /**
     * Disable the wait timer as described in isWaitTimerEnabled(). For FTC, you want to leave it
     * disabled.
     */
    private synchronized void disableWaitTimer() {
        byte reg = getEnableRegisterFromSensor();
        this.write8(Register.ENABLE, (reg & ~EnableRegister.AMS_COLOR_ENABLE_WEN.byteVal));
    }

    // INTEGRATION TIME REGISTER

    /**
     * Read the integration time register and return it as a byte
     *
     * @return integration time register bits as byte
     */
    private synchronized byte getIntegrationTimeRegisterFromSensor() {
        byte reg = colorSensorClient.read8(Register.INTEGRATION_TIME.byteVal);
        return reg;
    }

    /**
     * Read the integration time register and return it as the enum value corresponding to the
     * data in the register.
     *
     * @return IntegrationTime enum value
     */
    public synchronized IntegrationTime getIntegrationTimeValueFromSensor() {
        byte reg = getIntegrationTimeRegisterFromSensor();
        return IntegrationTime.valueOf(reg);
    }

    /**
     * Write an integration time into the integration time register
     *
     * @param time an IntegrationTime enum value
     */
    public void setIntegrationTime(IntegrationTime time) {
        this.write8(Register.INTEGRATION_TIME, time.byteVal);
        // calculate maximum possible color value for use later in scaling
        this.maxRGBCValue = calculateMaxRGBCCount(time);
    }

    // WAIT TIME REGISTER

    /**
     * Read the wait time register and return it as a byte
     *
     * @return wait time register bits as byte
     */
    private synchronized byte getWaitTimeRegisterFromSensor() {
        byte reg = colorSensorClient.read8(Register.WAIT_TIME.byteVal);
        return reg;
    }

    /**
     * Read the wait time register and return it as the enum value corresponding to the
     * data in the register.
     *
     * @return WaitTime enum value
     */
    private synchronized WaitTime getWaitTimeValueFromSensor() {
        byte reg = getIntegrationTimeRegisterFromSensor();
        return WaitTime.valueOf(reg);
    }

    /**
     * Write the specified wait time into the register
     *
     * @param waitTime
     */
    private synchronized void setWaitTimeRegister(WaitTime waitTime) {
        this.write8(Register.WAIT_TIME, waitTime.byteVal);
    }

    // INTERRUPT LOW AND HIGH THRESHOLD REGISTERS

    /**
     * Read the low byte of low threshold register and return it as a byte
     *
     * @return low byte of low threshold register
     */
    private synchronized byte getLowThresholdLowByteRegisterFromSensor() {
        byte reg = colorSensorClient.read8(Register.THRESHOLD_AILTL.byteVal);
        return reg;
    }

    /**
     * Set the low byte of the low interrupt threshold
     *
     * @param byteVal
     */
    private synchronized void setLowThresholdLowByte(byte byteVal) {
        this.write8(Register.THRESHOLD_AILTL, byteVal);
    }

    /**
     * Read the high byte of low threshold register and return it as a byte
     *
     * @return high byte of low threshold register
     */
    private synchronized byte getLowThresholdHighByteRegisterFromSensor() {
        byte reg = colorSensorClient.read8(Register.THRESHOLD_AILTH.byteVal);
        return reg;
    }

    /**
     * Set the high byte of the low interrupt threshold
     *
     * @param byteVal
     */
    private synchronized void setLowThresholdHighByte(byte byteVal) {
        this.write8(Register.THRESHOLD_AILTH, byteVal);
    }

    /**
     * Read the high and low bytes from the low threshold registers and return as an int.
     *
     * @return int formed from the high and low bytes of the threshold registers
     */
    public synchronized int getLowThresholdFromSensor() {
        return readUnsignedShort(Register.THRESHOLD_AILTL);
    }

    /**
     * Read the low byte of high threshold register and return it as a byte
     *
     * @return low byte of high threshold register
     */
    private synchronized byte getHighThresholdLowByteRegisterFromSensor() {
        byte reg = colorSensorClient.read8(Register.THRESHOLD_AIHTL.byteVal);
        return reg;
    }

    /**
     * Set the low byte of the high interrupt threshold
     *
     * @param byteVal
     */
    private synchronized void setHighThresholdLowByte(byte byteVal) {
        this.write8(Register.THRESHOLD_AIHTL, byteVal);
    }

    /**
     * Read the high byte of high threshold register and return it as a byte
     *
     * @return high byte of high threshold register
     */
    private synchronized byte getHighThresholdHighByteRegisterFromSensor() {
        byte reg = colorSensorClient.read8(Register.THRESHOLD_AIHTH.byteVal);
        return reg;
    }

    /**
     * Set the high byte of the high interrupt threshold
     *
     * @param byteVal
     */
    private synchronized void setHighThresholdHighByte(byte byteVal) {
        this.write8(Register.THRESHOLD_AIHTH, byteVal);
    }


    /**
     * Read the high and low bytes from the low threshold registers and return as an int.
     *
     * @return int formed from the high and low bytes of the threshold registers
     */
    public synchronized int getHighThresholdFromSensor() {
        return readUnsignedShort(Register.THRESHOLD_AIHTL);
    }

    // PERSISTENCE REGISTER

    /**
     * Read the persistence register from the sensor and return it as a byte
     *
     * @return persistence register bits as byte
     */
    private synchronized byte getPersistenceRegisterFromSensor() {
        byte reg = colorSensorClient.read8(Register.PERSISTENCE.byteVal);
        return reg;
    }

    /**
     * Read the persistence register from the sensor and return it as one of the persistence values.
     *
     * @return persistence as one of the persistence enum values
     */
    private synchronized Persistence getPersistenceValueFromSensor() {
        byte reg = getPersistenceRegisterFromSensor();
        return Persistence.valueOf(reg);
    }

    /**
     * Set the persistence value. This controls how many times in a row an interrupt condition must
     * persist before the interrupt pin is actually set.
     *
     * @param persistence
     */
    private synchronized void setPersistence(Persistence persistence) {
        this.write8(Register.PERSISTENCE, persistence.byteVal);
    }

    // CONFIGURATION

    private synchronized byte getConfigurationRegisterFromSensor() {
        byte reg = colorSensorClient.read8(Register.CONFIGURATION.byteVal);
        return reg;
    }

    private synchronized void setConfiguration(Configuration configuration) {
        this.write8(Register.CONFIGURATION, configuration.byteVal);
    }

    public synchronized Configuration getConfigurationValueFromSensor() {
        // setup a mask to yeild only bits 1:0
        byte configMask = 0x03;
        // read the gain
        byte reg = colorSensorClient.read8(Register.CONFIGURATION.byteVal);
        // mask off the 7:2 bits
        reg = (byte) (reg & configMask);
        return Configuration.valueOf(reg);

    }

    // CONTROL / GAIN REGISTER

    /**
     * Read the contents of the Control register and return it as a byte
     *
     * @return control register bits as byte
     */
    private synchronized byte getControlRegisterFromSensor() {
        byte reg = colorSensorClient.read8(Register.CONTROL.byteVal);
        return reg;
    }

    /**
     * The gain is set using bits 1:0 in the control register. Read the gain and return the enum
     * for it.
     *
     * @return Gain enum value corresponding to the gain bits in the register
     */
    public synchronized Gain getGainValueFromSensor() {
        // setup a mask to yeild only bits 1:0
        byte gainMask = 0x03;
        // read the gain
        byte reg = colorSensorClient.read8(Register.CONTROL.byteVal);
        // mask off the 7:2 bits
        reg = (byte) (reg & gainMask);
        return Gain.valueOf(reg);
    }

    /**
     * Set the gain applied to the color sensors
     *
     * @param gain
     */
    private void setGain(Gain gain) {
        this.write8(Register.CONTROL, gain.byteVal);
        // save the gain
        this.gain= gain;
    }

    // DEVICE ID REGISTER

    /**
     * Get the device id built into the sensor
     *
     * @return
     */
    public byte getDeviceID() {
        return this.read8(Register.DEVICE_ID);
    }


    /**
     * Verify that that's a color sensor!
     *
     * @return true if the color sensor is attached to the I2C bus
     */
    public boolean checkDeviceId() {
        byte id = this.getDeviceID();
        if ((id != deviceID)) {
            RobotLog.e("unexpected AMS color sensor chipid: found=%d expected=%d", id, deviceID);
            return false;
        } else {
            return true;
        }
    }

    // STATUS REGISTER

    /**
     * Read the contents of the Status register and return it as a byte
     *
     * @return status register bits as byte
     */
    private synchronized byte getStatusRegisterFromSensor() {
        byte reg = colorSensorClient.read8(Register.STATUS.byteVal);
        return reg;
    }

    /**
     * Get the interrupt status from the status register. Note that if the interrupt is not enabled
     * onto the INT pin, then the status bit will not match the INT pin.
     */
    public synchronized boolean isInterruptSetInStatusRegister() {
        // read the status register
        byte reg = colorSensorClient.read8(Register.STATUS.byteVal);
        if ((reg & Status.AMS_COLOR_STATUS_INTERRUPT.byteVal) == Status.AMS_COLOR_STATUS_INTERRUPT.byteVal) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * The status register has a bit that indicates if there is valid data from an integration
     * cycle. This method checks to see if there is valid data available.
     *
     * @return true if there is valid data available
     */
    public synchronized boolean isDataValidInStatusRegister() {
        // read the status register
        byte reg = colorSensorClient.read8(Register.STATUS.byteVal);
        if ((reg & Status.AMS_COLOR_STATUS_DATA_VALID.byteVal) == Status.AMS_COLOR_STATUS_DATA_VALID.byteVal) {
            return true;
        } else {
            return false;
        }
    }

    // COLOR REGISTERS - THESE ARE RAW UNSCALED VALUES - YOU PROBABLY WANT TO USE THE SCALED
    // VALUES INSTEAD OF THESE

    private int readColorRegister(Register reg) {
        return readUnsignedShort(reg);
    }

    /**
     * Read the red registers
     *
     * @return red value - raw and not scaled to 0-255 standard RGB value range
     */
    public synchronized int red() {
        return this.readColorRegister(Register.REDL);
    }

    /**
     * Read the green registers
     *
     * @return green value - raw and not scaled to 0-255 standard RGB value range
     */
    public synchronized int green() {
        return this.readColorRegister(Register.GREENL);
    }

    /**
     * Read the blue registers
     *
     * @return blue value - raw and not scaled to 0-255 standard RGB value range
     */
    public synchronized int blue() {
        return this.readColorRegister(Register.BLUEL);
    }

    /**
     * Read the clear (alpha) registers
     *
     * @return clear or alpha value - raw and not scaled to 0-255 standard RGB value range
     */
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

    /**
     * Read the 4 color values (clear + red, green and blue) from the registers and return them as
     * an array. The advantage of this method is that one read is a lot faster than individual reads
     * from each register as done in the red(), green(), blue() and alpha() methods.
     *
     * @return array of values [clear, red, green, blue]
     */
    public synchronized int[] getClearRGB() {
        int[] result = new int[4];
        byte[] bytes = read(Register.CLEARL, 8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        //
        int clear = TypeConversion.unsignedShortToInt(buffer.getShort());
        int red = TypeConversion.unsignedShortToInt(buffer.getShort());
        int green = TypeConversion.unsignedShortToInt(buffer.getShort());
        int blue = TypeConversion.unsignedShortToInt(buffer.getShort());
        //
        result[0] = clear;
        result[1] = red;
        result[2] = green;
        result[3] = blue;
        return result;
    }

    public String rgbValuesAsString() {
        return red() + " / " + blue() + " / " + green();
    }

    //*********************************************************************************************
    //          I2C low level read and write methods
    //*********************************************************************************************

    private synchronized byte read8(final Register reg) {
        return colorSensorClient.read8(reg.byteVal | CommandRegister.AMS_COLOR_COMMAND_BIT.byteVal);
    }

    private synchronized byte[] read(final Register reg, final int cb) {
        return colorSensorClient.read(reg.byteVal | CommandRegister.AMS_COLOR_COMMAND_BIT.byteVal, cb);
    }

    private synchronized void write8(Register reg, int data) {
        colorSensorClient.write8(reg.byteVal | CommandRegister.AMS_COLOR_COMMAND_BIT.byteVal, data);
        colorSensorClient.waitForWriteCompletions();
    }

    private synchronized void write(Register reg, byte[] data) {
        colorSensorClient.write(reg.byteVal | CommandRegister.AMS_COLOR_COMMAND_BIT.byteVal, data);
        colorSensorClient.waitForWriteCompletions();
    }

    private int readUnsignedShort(Register reg) {
        byte[] bytes = this.read(reg, 2);
        int result = 0;
        if (bytes.length == 2) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
            result = TypeConversion.unsignedShortToInt(buffer.getShort());
        }
        return result;
    }

    //---------------------------------------------------------------------------------
    //  Utility methods
    //---------------------------------------------------------------------------------

    /**
     * Force an interrupt to be output on the INT pin of the circuit board. The interrupt is active
     * low. If there is no interrupt, the output is floating (open drain). If the INT pin is jumpered
     * to the LED pin, then the interrupt can be used to turn the LED on and off. Tricky huh?
     * interrupt = LED off
     * no interrupt = LED on
     */
    private synchronized void forceInterrupt() {
        // set the clear persistence interrupt filter so that an interrupt is generated the first
        // time the threshold is exceeded
        setPersistence(Persistence.AMS_COLOR_PERS_NONE);
        // set the low interrupt threshold way high so an interrupt is guaranteed
        setLowThresholdLowByte((byte) 0xFF);
        setLowThresholdHighByte((byte) 0xFF);
        // set the high interrupt threshold low so it is not evaluated
        setHighThresholdLowByte((byte) 0x00);
        setHighThresholdHighByte((byte) 0x00);
        // enable the interrupt
        enableInterrupt();
    }

    /**
     * Figure out what the maximum possible color value is given the integration time that is chosen.
     * If you actually read this max value then it is likely that your gain is set too high and
     * you are saturating the sensor. In that case choose a lower gain.
     *
     * @param integrationTime
     * @return max possible value for a color sensor reading.
     */
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

    /**
     * Standard RBG values range from 0 to 255. The raw color sensor values can range from 0 to
     * 65535 depending on the integration time selected. This method scales a raw color sensor
     * value reading to the range of 0 to 255. This normalizes
     * the reading so it can be compared to readings using different integration times.
     *
     * @param colorValue
     * @return scaled color value
     */
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
        // yeah well it actually does nothing for now. Some day maybe ...
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    //*********************************************************************************************
    //          LED Control - control the led on the Adafruit board and the leds in the modern
    //          robotics core dim module
    //*********************************************************************************************

    /**
     * Turn the led on the color sensor circuit board on.
     */
    public void turnLEDOn() {
        // if the led is not on already go ahead and turn it on. This check saves bus transactions
        // if the led is already on.
        if (!this.ledOn) {
            switch (ledControl) {
                // the LED on the adafruit board is controlled using a digital port on the Modern
                // Robotics core DIM
                case CORE_DIM:
                    // only put commands on the bus if there is a change to be made
                    // the led is off so it makes sense to turn it on
                    coreDIM.setDigitalChannelState(ioChannelForLed, ledOn);
                    break;
                // the LED is controlled using the INT pin on the adafruit circuit board
                case INTERRUPT:
                    // the interrupt pin is open drain (active low). Disabling it allows the pullup
                    // resistor on the gate of the FET controlling LED to pull high, turning on the
                    // FET and the LED
                    disableInterrupt();
                    break;
                // there is no control over the led
                case NONE:
                    break;
            }
            this.ledOn = true;

        }
    }

    /**
     * Turn the led on the color sensor circuit board off.
     */
    public void turnLEDOff() {
        // if the led is not off already go ahead and turn it off. This check saves bus transactions
        // if the led is already off.
        if (this.ledOn) {
            switch (ledControl) {
                // the LED on the adafruit board is controlled using a digital port on the Modern
                // Robotics core DIM
                case CORE_DIM:
                    // only put commands on the bus if there is a change to be made
                    // the led is on so it makes sense to turn it off
                    coreDIM.setDigitalChannelState(ioChannelForLed, ledOn);
                    break;
                // the LED is controlled using the INT pin on the adafruit circuit board
                case INTERRUPT:
                    // the interrupt pin is open drain (active low). Forcing an interrupt pulls
                    // the gate of the FET controlling LED low, turning off the FET and the LED
                    forceInterrupt();
                    break;
                // there is no control over the led
                case NONE:
                    break;
            }
            this.ledOn = false;
        }
    }

    /**
     * Toggle the led on the color sensor. The led pin on the color sensor is connected to a
     * digital input port on the core DIM.
     */
    public void toggleLED() {
        if (this.ledOn) {
            turnLEDOff();
        } else {
            turnLEDOn();
        }
    }

    /**
     * Turn the blue led in the core DIM on
     */
    public void turnCoreDIMBlueLEDOn() {
        if (coreDIM != null) {
            coreDIM.setLED(CoreDIMLEDChannel.BLUE.byteVal, true);
        }
    }

    /**
     * Turn the blue led in the core DIM off
     */
    public void turnCoreDIMBlueLEDOff() {
        if (coreDIM != null) {
            coreDIM.setLED(CoreDIMLEDChannel.BLUE.byteVal, false);
        }
    }

    /**
     * Turn the red led in the core DIM on
     */
    public void turnCoreDIMRedLEDOn() {
        if (coreDIM != null) {
            coreDIM.setLED(CoreDIMLEDChannel.RED.byteVal, true);
        }
    }

    /**
     * Turn the red led in the core DIM off
     */
    public void turnCoreDIMRedLEDOff() {
        if (coreDIM != null) {
            coreDIM.setLED(CoreDIMLEDChannel.RED.byteVal, false);
        }
    }

    //*********************************************************************************************
    //          Reading colors - these are the scaled values - USE THESE INSTEAD OF RAW VALUES
    //*********************************************************************************************

    /**
     * RGB is typically defined as a range from 0 to 255. This method returns a scaled value based
     * on the integration time selected.
     *
     * @return RGB value in the range from 0 to 255
     */
    public int redScaled() {
        return calculateScaledRGBColor(red());
    }

    /**
     * RGB is typically defined as a range from 0 to 255. This method returns a scaled value based
     * on the integration time selected.
     *
     * @return RGB value in the range from 0 to 255
     */
    public int greenScaled() {
        return calculateScaledRGBColor(green());
    }

    /**
     * RGB is typically defined as a range from 0 to 255. This method returns a scaled value based
     * on the integration time selected.
     *
     * @return RGB value in the range from 0 to 255
     */
    public int blueScaled() {
        return calculateScaledRGBColor(blue());
    }

    /**
     * RGB is typically defined as a range from 0 to 255. This method returns a scaled value based
     * on the integration time selected.
     *
     * @return RGB value in the range from 0 to 255
     */
    public int alphaScaled() {
        return calculateScaledRGBColor(alpha());
    }

    /**
     * Format a string with the scaled RGB values
     *
     * @return
     */
    public String rgbValuesScaledAsString() {
        return redScaled() + " / " + greenScaled() + " / " + blueScaled();
    }

    /**
     * Get an HSV color
     *
     * @return array with hue [0], saturation [1], value [2]
     */
    public float[] hsvScaled() {
        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};
        // convert the RGB values to HSV values.
        Color.RGBToHSV(redScaled(), greenScaled(), blueScaled(), hsvValues);
        return hsvValues;
    }

    /**
     * Format a string with the HSV values
     *
     * @return
     */
    public String hsvValuesScaledAsString() {
        float hsvValues[] = hsvScaled();
        return String.format("%4.1f", hsvValues[0]) + " / " + String.format("%4.2f", hsvValues[1]) + " / " + String.format("%4.2f", hsvValues[2]);
    }


    /**
     * Get the hue
     *
     * @return
     */
    public float hueScaled() {
        return hsvScaled()[0];
    }

    /**
     * Get the saturation
     *
     * @return
     */
    public float saturatationScaled() {
        return hsvScaled()[1];
    }

    /**
     * Get the value
     *
     * @return
     */
    public float valueScaled() {
        return hsvScaled()[2];
    }

    //*********************************************************************************************
    //          Color Testing using RGB as the method - should probably put this into another class
    //*********************************************************************************************

    /**
     * A utility method that takes in 3 values and returns the one that is the max of the 3.
     *
     * @param a
     * @param b
     * @param c
     * @return max value of the three inputs
     */
    private int getMaxOfThree(int a, int b, int c) {
        int max = a;
        int which = 1;
        if (b > a) {
            max = b;
            which = 2;
        }
        if (c > max) {
            max = c;
            which = 3;
        }
        return which;
    }

    /**
     * Determine if the color seen is red by looking at the ratio of red to blue. If the ratio is
     * larger than a certain limit then we call it red.
     *
     * @return true = red, false = not red
     */
    public boolean isRedUsingRGB() {
        boolean result = false;
        // need to cast one operand as float in order to get a float result
        if ((float) redScaled() / blueScaled() > isRedRatioLimit) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Determine if the color seen is green by comparing the RGB values. The one that is the max is
     * judged to be the color you are reading. In this case check if green is the max.
     *
     * @return true = green, false = not green
     */
    public boolean isGreenUsingRGB() {
        boolean result = false;
        if (getMaxOfThree(redScaled(), greenScaled(), blueScaled()) == 2) {
            result = true;
        }
        return result;
    }

    /**
     * Determine if the color seen is blue by looking at the ratio of red to blue. If the ratio is
     * less than a certain limit then we call it blue.
     *
     * @return true = blue, false = not blue
     */
    public boolean isBlueUsingRGB() {
        boolean result = false;
        if ((float) redScaled() / blueScaled() < isBlueRatioLimit) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public ColorFromSensor getSimpleColor() {
        ColorFromSensor result = ColorFromSensor.UNKNOWN;
        if (isRedUsingRGB()) {
            result = ColorFromSensor.RED;
        } else {
            if (isBlueUsingRGB()) {
                result = ColorFromSensor.BLUE;
            } else {
                if (isGreenUsingRGB()) {
                    result = ColorFromSensor.GREEN;
                }
            }
        }
        // if none of the test were match the result is unknown
        return result;
    }

    /**
     * Return a caption to be used for a telemetry.addData call
     *
     * @return
     */
    public String colorTestResultUsingRGBCaption() {
        return "Red? / Green? / Blue? (RGB values)";
    }

    /**
     * Return a string with the results of testing the color for red, green and blue.
     *
     * @return a true will appear where the color is matched.
     */
    public String colorTestResultUsingRGB() {
        return String.valueOf(isRedUsingRGB()) + " / " + String.valueOf(isGreenUsingRGB()) + " / " + String.valueOf(isBlueUsingRGB());
    }

    public String colorRatiosUsingRGBCaption() {
        return "Red / Blue ratio: ";
    }

    public String colorRatiosUsingRGBValues() {
        return String.format("%2.2f", (float) redScaled() / blueScaled());
    }
    //*********************************************************************************************
    //          Color Testing using HSV as the method - should probably put this into another class
    //          for more info on HSV see http://www.tech-faq.com/hsv.html
    //*********************************************************************************************

    /**
     * Using HSV, determine if the color seen is red
     *
     * @return true = red
     */
    public boolean isRedUsingHSV() {
        float hue = hueScaled();
        if (hue >= 0 && hue <= 60 || hue >= 340 && hue <= 360) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Using HSV, determine if the color seen is green
     *
     * @return true = green
     */
    public boolean isGreenUsingHSV() {
        float hue = hueScaled();
        if (hue > 120 && hue <= 180) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Using HSV, determine if the color seen is blue
     *
     * @return true = blue
     */
    public boolean isBlueUsingHSV() {
        float hue = hueScaled();
        if (hue > 220 && hue <= 300) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return a caption to be used for a telemetry.addData call
     *
     * @return
     */
    public String colorTestResultUsingHSVCaption() {
        return "Red? / Green? / Blue? (HSV values)";
    }

    /**
     * Return a string with the results of testing the color for red, green and blue. HSV is used
     * for the test
     *
     * @return a true will appear where the color is matched.
     */
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

    /**
     * Use the stored value for the gain to return a string representing the gain. Note this is not
     * reading from the color sensor register, it is just the cache of the value in the register.
     *
     * @return String with the gain
     */
    public String getCurrentGainAsString() {
        String gainAsString = "nothing";
        switch (gain) {
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

    public void setIntegrationTime2_4ms() {
        setIntegrationTime(IntegrationTime.AMS_COLOR_ITIME_2_4MS);
    }

    public void setIntegrationTime24ms() {
        setIntegrationTime(IntegrationTime.AMS_COLOR_ITIME_24MS);
    }

    public void setIntegrationTime50ms() {
        setIntegrationTime(IntegrationTime.AMS_COLOR_ITIME_50MS);
    }

    public void setIntegrationTime101ms() {
        setIntegrationTime(IntegrationTime.AMS_COLOR_ITIME_101MS);
    }

    public void setIntegrationTime154ms() {
        setIntegrationTime(IntegrationTime.AMS_COLOR_ITIME_154MS);
    }

    public void setIntegrationTime307ms() {
        setIntegrationTime(IntegrationTime.AMS_COLOR_ITIME_307MS);
    }

    public void setIntegrationTime460ms() {
        setIntegrationTime(IntegrationTime.AMS_COLOR_ITIME_460MS);
    }

    public void setIntegrationTime537ms() {
        setIntegrationTime(IntegrationTime.AMS_COLOR_ITIME_537MS);
    }

    public void setIntegrationTime700ms() {
        setIntegrationTime(IntegrationTime.AMS_COLOR_ITIME_700MS);
    }

    /**
     * Use the stored value for the integration time to return a string representing the integration
     * time. Note this is not reading from the color sensor register, it is just the cache of the
     * value in the register.
     *
     * @return String with the integration time
     */
    public String getCurrentIntegrationTimeAsString() {
        String integrationTimeAsString = "nothing";
        switch (integrationTime) {
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
    //          Update Rate tracking - test how often the sensor is giving you new data
    //          This appears to have a bug in the minimum time that I have yet to look into.
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

    public double getUpdateRateInstantaneous() {
        return updateTimeTracker.getInstantaneous();
    }

    public String updateRateString() {
        return String.format("%4.0f", getUpdateRateMin()) + " / " + String.format("%4.0f", getUpdateRateAve())
                + " / " + String.format("%4.0f", getUpdateRateMax()) + " / " + String.format("%4.0f", getUpdateRateInstantaneous());
    }

    //*********************************************************************************************
    //          Putting data onto the driver station / test methods
    //*********************************************************************************************

    /**
     * On the driver station phone display most of the pertinent data from a color sensor. Use this
     * for debug.
     *
     * @param telemetry
     */
    public void displayColorSensorData(Telemetry telemetry, AmountOfDataToDisplay amountOfDataToDisplay) {
        if (amountOfDataToDisplay == AmountOfDataToDisplay.NORMAL) {
            telemetry.addData("Color sensor gain = ", getCurrentGainAsString());
            telemetry.addData("Color sensor integration time = ", getCurrentIntegrationTimeAsString());
            telemetry.addData("Max possible color value = ", getMaxRGBCValue());
            telemetry.addData("Opaqueness = ", alpha());
            telemetry.addData("Red / Green / Blue ", rgbValuesAsString());
            telemetry.addData("Red / Green / Blue (scaled)", rgbValuesScaledAsString());
            telemetry.addData("Hue / Sat / Value (scaled)", hsvValuesScaledAsString());
            telemetry.addData(colorRatiosUsingRGBCaption(), colorRatiosUsingRGBValues());
            telemetry.addData(colorTestResultUsingRGBCaption(), colorTestResultUsingRGB());
            telemetry.addData(colorTestResultUsingHSVCaption(), colorTestResultUsingHSV());
            telemetry.addData("Update min/ave/max/instant (mS) = ", updateRateString());
            telemetry.addData(">", "Press Stop to end test.");
        }
        if (amountOfDataToDisplay == AmountOfDataToDisplay.MIN) {
            telemetry.addData("Red / Green / Blue (scaled)", rgbValuesScaledAsString());
            telemetry.addData(colorRatiosUsingRGBCaption(), colorRatiosUsingRGBValues());
            telemetry.addData(colorTestResultUsingRGBCaption(), colorTestResultUsingRGB());
            telemetry.addData(colorTestResultUsingHSVCaption(), colorTestResultUsingHSV());
        }
    }

    public void displayRegisterByteValues(Telemetry telemetry) {
        telemetry.addData("ENABLE register =           ", "%02X ", getEnableRegisterFromSensor());
        telemetry.addData("INTEGRATION_TIME register = ", "%02X ", getIntegrationTimeRegisterFromSensor());
        telemetry.addData("WAIT_TIME register =        ", "%02X ", getWaitTimeRegisterFromSensor());
        telemetry.addData("THRESHOLD_AILTL register =  ", "%02X ", getLowThresholdLowByteRegisterFromSensor());
        telemetry.addData("THRESHOLD_AILTH register =  ", "%02X ", getLowThresholdHighByteRegisterFromSensor());
        telemetry.addData("THRESHOLD_AIHTL register =  ", "%02X ", getHighThresholdLowByteRegisterFromSensor());
        telemetry.addData("THRESHOLD_AIHTH register =  ", "%02X ", getHighThresholdLowByteRegisterFromSensor());
        telemetry.addData("PERSISTENCE register =      ", "%02X ", getPersistenceRegisterFromSensor());
        telemetry.addData("CONFIGURATION register =    ", "%02X ", getConfigurationRegisterFromSensor());
        telemetry.addData("CONTROL register =          ", "%02X ", getControlRegisterFromSensor());
        telemetry.addData("DEVICE_ID register =        ", "%02X ", getDeviceID());
        telemetry.addData("STATUS register =           ", "%02X ", getStatusRegisterFromSensor());
        telemetry.addData("Clear data =                ", "%5d", alpha());
        telemetry.addData("Red data =                  ", "%5d", red());
        telemetry.addData("Green data =                ", "%5d", green());
        telemetry.addData("Blue data =                 ", "%5d", blue());
    }

    public void displayRegisterValues(Telemetry telemetry) {
        telemetry.addData("ENABLE register: ", " ");
        telemetry.addData("  Wait timer enabled =   ", isWaitTimerEnabled());
        telemetry.addData("  Interrupt enabled =    ", isInterruptEnabled());
        telemetry.addData("  Color sensor enabled = ", isColorSensorEnabled());
        telemetry.addData("  Powered on  =          ", isPowerEnabled());
        telemetry.addData("Integration time =       ", getIntegrationTimeValueFromSensor().toString());
        telemetry.addData("Wait time =              ", getWaitTimeValueFromSensor().toString());
        telemetry.addData("Low threshold =          ", "%5d", getLowThresholdFromSensor());
        telemetry.addData("High threshold =         ", "%5d", getHighThresholdFromSensor());
        telemetry.addData("Persistence =            ", getPersistenceValueFromSensor().toString());
        telemetry.addData("Configuration =          ", getConfigurationValueFromSensor());
        telemetry.addData("Gain =                   ", getGainValueFromSensor());
        telemetry.addData("Device ok =              ", checkDeviceId());
        telemetry.addData("STATUS register:         ", " ");
        telemetry.addData("  Interrupt status =     ", isInterruptSetInStatusRegister());
        telemetry.addData("  Data valid =           ", isDataValidInStatusRegister());
        telemetry.addData("Clear (scaled) data =    ", "%5d", alphaScaled());
        telemetry.addData("Red (scaled) data =      ", "%5d", redScaled());
        telemetry.addData("Green (scaled) data =    ", "%5d", greenScaled());
        telemetry.addData("Blue (scaled) data =     ", "%5d", blueScaled());
    }

    public void testReadWriteRegisters(Telemetry telemetry) {

        //read and write the enable register

        telemetry.addData("Enable register tests", "!");
        sleep(2000);
        telemetry.addData("Color sensor default after power up", "!");
        telemetry.addData("ENABLE register =           ", "%02X ", getEnableRegisterFromSensor());
        telemetry.addData("ENABLE register: ", " ");
        telemetry.addData("  Wait timer enabled =   ", isWaitTimerEnabled());
        telemetry.addData("  Interrupt enabled =    ", isInterruptEnabled());
        telemetry.addData("  Color sensor enabled = ", isColorSensorEnabled());
        telemetry.addData("  Powered on  =          ", isPowerEnabled());
        telemetry.update();
        sleep(5000);
        enableInterrupt();
        telemetry.addData("Interrupt enabled", "...");
        telemetry.addData("ENABLE register =           ", "%02X ", getEnableRegisterFromSensor());
        telemetry.addData("ENABLE register: ", " ");
        telemetry.addData("  Wait timer enabled =   ", isWaitTimerEnabled());
        telemetry.addData("  Interrupt enabled =    ", isInterruptEnabled());
        telemetry.addData("  Color sensor enabled = ", isColorSensorEnabled());
        telemetry.addData("  Powered on  =          ", isPowerEnabled());
        telemetry.update();
        sleep(5000);
        disableInterrupt();
        telemetry.addData("Interrupt disabled", "...");
        telemetry.addData("ENABLE register =           ", "%02X ", getEnableRegisterFromSensor());
        telemetry.addData("ENABLE register: ", " ");
        telemetry.addData("  Wait timer enabled =   ", isWaitTimerEnabled());
        telemetry.addData("  Interrupt enabled =    ", isInterruptEnabled());
        telemetry.addData("  Color sensor enabled = ", isColorSensorEnabled());
        telemetry.addData("  Powered on  =          ", isPowerEnabled());
        telemetry.update();
        sleep(5000);
        enableWaitTimer();
        telemetry.addData("Wait Timer enabled", "...");
        telemetry.addData("ENABLE register =           ", "%02X ", getEnableRegisterFromSensor());
        telemetry.addData("ENABLE register: ", " ");
        telemetry.addData("  Wait timer enabled =   ", isWaitTimerEnabled());
        telemetry.addData("  Interrupt enabled =    ", isInterruptEnabled());
        telemetry.addData("  Color sensor enabled = ", isColorSensorEnabled());
        telemetry.addData("  Powered on  =          ", isPowerEnabled());
        telemetry.update();
        sleep(5000);
        disableWaitTimer();
        telemetry.addData("Wait Timer disabled", "...");
        telemetry.addData("ENABLE register =           ", "%02X ", getEnableRegisterFromSensor());
        telemetry.addData("ENABLE register: ", " ");
        telemetry.addData("  Wait timer enabled =   ", isWaitTimerEnabled());
        telemetry.addData("  Interrupt enabled =    ", isInterruptEnabled());
        telemetry.addData("  Color sensor enabled = ", isColorSensorEnabled());
        telemetry.addData("  Powered on  =          ", isPowerEnabled());
        telemetry.update();
        sleep(5000);

        // read and write the integration time register

        telemetry.addData("Integration time register tests", "!");
        telemetry.update();
        sleep(2000);
        telemetry.addData("INTEGRATION_TIME register = ", "%02X ", getIntegrationTimeRegisterFromSensor());
        telemetry.addData("Integration time =       ", getIntegrationTimeValueFromSensor().toString());
        telemetry.update();
        sleep(5000);
        setIntegrationTime(AdafruitColorSensor8863.IntegrationTime.AMS_COLOR_ITIME_700MS);
        telemetry.addData("700mSec integration time", "...");
        telemetry.addData("INTEGRATION_TIME register = ", "%02X ", getIntegrationTimeRegisterFromSensor());
        telemetry.addData("Integration time =       ", getIntegrationTimeValueFromSensor().toString());
        telemetry.update();
        sleep(5000);
        setIntegrationTime(AdafruitColorSensor8863.IntegrationTime.AMS_COLOR_ITIME_24MS);
        telemetry.addData("24mSec integration time", "...");
        telemetry.addData("INTEGRATION_TIME register = ", "%02X ", getIntegrationTimeRegisterFromSensor());
        telemetry.addData("Integration time =       ", getIntegrationTimeValueFromSensor().toString());
        telemetry.update();
        sleep(5000);
        setIntegrationTime537ms();
        telemetry.addData("537mSec integration time", "...");
        telemetry.addData("INTEGRATION_TIME register = ", "%02X ", getIntegrationTimeRegisterFromSensor());
        telemetry.addData("Integration time =       ", getIntegrationTimeValueFromSensor().toString());
        telemetry.update();
        sleep(5000);
        setIntegrationTime24ms();
        telemetry.addData("24mSec integration time", "...");
        telemetry.addData("INTEGRATION_TIME register = ", "%02X ", getIntegrationTimeRegisterFromSensor());
        telemetry.addData("Integration time =       ", getIntegrationTimeValueFromSensor().toString());
        telemetry.update();
        sleep(5000);

        // Wait time register

        telemetry.addData("Wait time register tests", "!");
        telemetry.update();
        sleep(2000);
        telemetry.addData("Defaults", "...");
        telemetry.addData("WAIT_TIME register = ", "%02X ", getWaitTimeRegisterFromSensor());
        telemetry.addData("Wait time =          ", getWaitTimeValueFromSensor().toString());
        telemetry.update();
        sleep(5000);
        telemetry.addData("614mSec wait time", "...");
        setWaitTimeRegister(AdafruitColorSensor8863.WaitTime.AMS_COLOR_WTIME_614MS);
        telemetry.addData("WAIT_TIME register = ", "%02X ", getWaitTimeRegisterFromSensor());
        telemetry.addData("Wait time =          ", getWaitTimeValueFromSensor().toString());
        telemetry.update();
        sleep(5000);
        telemetry.addData("2.4mSec wait time", "...");
        setWaitTimeRegister(AdafruitColorSensor8863.WaitTime.AMS_COLOR_WTIME_2_4MS);
        telemetry.addData("WAIT_TIME register = ", "%02X ", getWaitTimeRegisterFromSensor());
        telemetry.addData("Wait time =          ", getWaitTimeValueFromSensor().toString());
        telemetry.update();
        sleep(5000);

        // high and low bytes of low threshold

        telemetry.addData("Low interrupt threshold tests", "!");
        telemetry.update();
        sleep(2000);
        telemetry.addData("Defaults", "...");
        telemetry.addData("THRESHOLD_AILTL register = ", "%02X ", getLowThresholdLowByteRegisterFromSensor());
        telemetry.addData("THRESHOLD_AILTH register = ", "%02X ", getLowThresholdHighByteRegisterFromSensor());
        telemetry.addData("Low threshold =            ", "%5d", getLowThresholdFromSensor());
        telemetry.update();
        sleep(5000);
        setLowThresholdLowByte((byte) 0xFF);
        setLowThresholdHighByte((byte) 0xFF);
        telemetry.addData("FFFF", "...");
        telemetry.addData("THRESHOLD_AILTL register = ", "%02X ", getLowThresholdLowByteRegisterFromSensor());
        telemetry.addData("THRESHOLD_AILTH register = ", "%02X ", getLowThresholdHighByteRegisterFromSensor());
        telemetry.addData("Low threshold =            ", "%5d", getLowThresholdFromSensor());
        telemetry.update();
        sleep(5000);
        setLowThresholdLowByte((byte) 0x00);
        setLowThresholdHighByte((byte) 0x00);
        telemetry.addData("0000", "...");
        telemetry.addData("THRESHOLD_AILTL register = ", "%02X ", getLowThresholdLowByteRegisterFromSensor());
        telemetry.addData("THRESHOLD_AILTH register = ", "%02X ", getLowThresholdHighByteRegisterFromSensor());
        telemetry.addData("Low threshold =            ", "%5d", getLowThresholdFromSensor());
        telemetry.update();
        sleep(5000);

        telemetry.addData("High interrupt threshold tests", "!");
        telemetry.update();
        sleep(2000);
        telemetry.addData("Defaults", "...");
        telemetry.addData("THRESHOLD_AIHTL register = ", "%02X ", getHighThresholdLowByteRegisterFromSensor());
        telemetry.addData("THRESHOLD_AIHTH register = ", "%02X ", getHighThresholdHighByteRegisterFromSensor());
        telemetry.addData("High threshold =           ", "%5d", getHighThresholdFromSensor());
        telemetry.update();
        sleep(5000);
        setHighThresholdLowByte((byte) 0xFF);
        setHighThresholdHighByte((byte) 0xFF);
        telemetry.addData("FFFF", "...");
        telemetry.addData("THRESHOLD_AIHTL register = ", "%02X ", getHighThresholdLowByteRegisterFromSensor());
        telemetry.addData("THRESHOLD_AIHTH register = ", "%02X ", getHighThresholdHighByteRegisterFromSensor());
        telemetry.addData("High threshold =           ", "%5d", getHighThresholdFromSensor());
        telemetry.update();
        sleep(5000);
        setHighThresholdLowByte((byte) 0x00);
        setHighThresholdHighByte((byte) 0x00);
        telemetry.addData("0000", "...");
        telemetry.addData("THRESHOLD_AIHTL register = ", "%02X ", getHighThresholdLowByteRegisterFromSensor());
        telemetry.addData("THRESHOLD_AIHTH register = ", "%02X ", getHighThresholdHighByteRegisterFromSensor());
        telemetry.addData("High threshold =           ", "%5d", getHighThresholdFromSensor());
        telemetry.update();
        sleep(5000);

        // persistence register

        telemetry.addData("Persistence register tests", "!");
        telemetry.update();
        sleep(2000);
        telemetry.addData("Defaults", "...");
        telemetry.addData("PERSISTENCE register = ", "%02X ", getPersistenceRegisterFromSensor());
        telemetry.addData("Persistence =          ", getPersistenceValueFromSensor().toString());
        telemetry.update();
        sleep(5000);
        telemetry.addData("Persistence = 10 cycles", "...");
        setPersistence(AdafruitColorSensor8863.Persistence.AMS_COLOR_PERS_10_CYCLE);
        telemetry.addData("PERSISTENCE register = ", "%02X ", getPersistenceRegisterFromSensor());
        telemetry.addData("Persistence =          ", getPersistenceValueFromSensor().toString());
        telemetry.update();
        sleep(5000);
        telemetry.addData("Persistence = 0 cycles ", "...");
        setPersistence(AdafruitColorSensor8863.Persistence.AMS_COLOR_PERS_NONE);
        telemetry.addData("PERSISTENCE register = ", "%02X ", getPersistenceRegisterFromSensor());
        telemetry.addData("Persistence =          ", getPersistenceValueFromSensor().toString());
        telemetry.update();
        sleep(5000);

        // configuration register

        telemetry.addData("Configuration register tests", "!");
        telemetry.update();
        sleep(2000);
        telemetry.addData("Defaults", "...");
        telemetry.addData("CONFIGURATION register =    ", "%02X ", getConfigurationRegisterFromSensor());
        telemetry.addData("Configuration =          ", getConfigurationValueFromSensor());
        telemetry.update();
        sleep(5000);
        telemetry.addData("WLONG set", "...");
        setConfiguration(AdafruitColorSensor8863.Configuration.AMS_COLOR_CONFIG_WLONG);
        telemetry.addData("CONFIGURATION register =    ", "%02X ", getConfigurationRegisterFromSensor());
        telemetry.addData("Configuration =          ", getConfigurationValueFromSensor());
        telemetry.update();
        sleep(5000);
        telemetry.addData("NORMAL set", "...");
        setConfiguration(AdafruitColorSensor8863.Configuration.AMS_COLOR_CONFIG_NORMAL);
        telemetry.addData("CONFIGURATION register =    ", "%02X ", getConfigurationRegisterFromSensor());
        telemetry.addData("Configuration =          ", getConfigurationValueFromSensor());
        telemetry.update();
        sleep(5000);

        // Control / gain register

        telemetry.addData("Control / gain register tests", "!");
        telemetry.update();
        sleep(2000);
        telemetry.addData("Defaults", "...");
        telemetry.addData("CONTROL / gain register =          ", "%02X ", getControlRegisterFromSensor());
        telemetry.addData("Gain =                   ", getGainValueFromSensor());
        telemetry.update();
        sleep(5000);
        telemetry.addData("Gain = 16X", "...");
        setGain16x();
        telemetry.addData("CONTROL / gain register =          ", "%02X ", getControlRegisterFromSensor());
        telemetry.addData("Gain =                   ", getGainValueFromSensor());
        telemetry.update();
        sleep(5000);
        telemetry.addData("Gain = 64X", "...");
        setGain64x();
        telemetry.addData("CONTROL / gain register =          ", "%02X ", getControlRegisterFromSensor());
        telemetry.addData("Gain =                   ", getGainValueFromSensor());
        telemetry.update();
        sleep(5000);

        // Device ID register - read only

        telemetry.addData("Device ID register tests", "!");
        telemetry.update();
        sleep(2000);
        telemetry.addData("Device ID = ", getDeviceID());
        telemetry.addData("Is device ID OK = ", checkDeviceId());
        telemetry.update();
        sleep(5000);

        // status register

        telemetry.addData("Status register tests", "!");
        telemetry.update();
        sleep(2000);
        telemetry.addData("Defaults", "...");
        telemetry.addData("STATUS register =    ", "%02X ", getStatusRegisterFromSensor());
        telemetry.addData("  Interrupt status = ", isInterruptSetInStatusRegister());
        telemetry.addData("  Data valid =       ", isDataValidInStatusRegister());
        telemetry.update();
        sleep(5000);

        // set the integration time to be long and see if the data valid toggles

        telemetry.addData("Set long integration time to see if data valid toggles", "!");
        telemetry.update();
        sleep(2000);
        setIntegrationTime700ms();
        telemetry.addData("  Data valid =       ", isDataValidInStatusRegister());
        telemetry.update();
        sleep(5000);
    }

    /**
     * Sleeps for the given amount of milliseconds, or until the thread is interrupted. This is
     * simple shorthand for the operating-system-provided {@link Thread#sleep(long) sleep()} method.
     *
     * @param milliseconds amount of time to sleep, in milliseconds
     * @see Thread#sleep(long)
     */
    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
