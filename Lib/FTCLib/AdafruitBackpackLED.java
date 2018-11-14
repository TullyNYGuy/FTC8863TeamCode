package org.firstinspires.ftc.teamcode.Lib.FTCLib;


/* This I2C driver communicates and controls the Adafruit Backpack LED display.
This display is a 4 character LED alphanumeric display available from Adafruit.
The Adafruit Backpack LED display is available in several different LED colors. Here is the
yellow version: https://www.adafruit.com/product/2158

   Phone configuration:
   Backpack display plugged into the I2C bus on a port and that port configured as:
   I2C port type: Adafruit Backpack LED Display

   If you want to make the display brighter, then wire the 5V pin and a second ground wire from the
   backpack board into the 5V port on the REV expansion hub. Without the extra power, the display
   will not be as bright.
*/
// The LED display is controlled by the HT16K33 LED controller. It has more capability than is used
// in the Adafruit display.
// The implementation of the LED circuit to the Adafruit Backpack driver is specific to the
// printed circuit board that Adafruit sells. So the control is specific to this board can't be
// used with any other board that uses the HT16K33 LED Controller. The Adafruit Backpack LED display
// is available in several different LED colors. Here is the yellow version:
// https://www.adafruit.com/product/2158

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cWaitControl;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice;
import com.qualcomm.robotcore.hardware.configuration.I2cSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "unused"}) // Ignore access and unused warnings
// This line set the name of the display when it shows up in the phone configuration.
@I2cSensor(name = "Adafruit Backpack LED Display", description = "Adafruit Backpack LED Display", xmlTag = "BackpackLED")

public class AdafruitBackpackLED extends I2cDeviceSynchDevice<I2cDeviceSynch> {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    // The enums below provide symbolic names for device registers and for register bits and values.
    // All information is taken from the datasheet for the device.
    // https://cdn-shop.adafruit.com/datasheets/ht16K33v110.pdf

    // For some strange reason the datasheet defines the registers as bits 8:15 even though they are
    // 8 bit registers. I would have defined them as 0:8. But since the datasheet uses 8:15 I'm
    // going to keep the bit numbering scheme so it is easy to match the datasheet to the code

    // The HT16K33 is a bit odd to me. For some registers, the register address and the command data that go into the
    // register are combined into one 8 bit byte. The most significant 4 bits are the address of the
    // register and the commands are contained in the least significant 4 bits.

    // Here are the register addresses. The command bits are in enums that follow this. To get a
    // full 8 bit byte to write to the chip, OR the register address and the command bits together.
    // This is from the chip datasheet, pages 30 and 31.

    /**
     * REGISTER provides symbolic names for device registers addresses
     */
    private enum Register {
        DISPLAY_DATA(0x00), // The starting address for the LED display data register. There are 16 registers 0x00 to 0x0F.
        // Each pair of two registers holds the bits controlling the display of a single ASCII character.
        // So the chip can control up to 8 16-segment LEDS.
        // However, the Adafruit board only implements 4 14-segment LEDs. These are on addresses:
        // 0x00 / 0x01 - left most LED
        // 0x02 / 0x03 - left middle LED
        // 0x04 / 0x05 - right middle LED
        // 0x06 / 0x07 - right most LED
        // 0x08 - 0x0F - not used (no LED on these addresses)
        // To write a character to the LED, follow this address with 2 8 bit bytes of data that
        // turn on the LED segments to make an ASCII character.
        LEFT_LED_CHARACTER(0x00),
        LEFT_MIDDLE_LED_CHARACTER(0x02),
        RIGHT_MIDDLE_LED_CHARACTER(0x04),
        RIGHT_LED_CHARACTER(0x06),

        SYSTEM_SETUP(0x20), // The most significant 4 bits are the address of the register for controlling the chip.
        // The least significant 4 bits are the commands. They are defined in another enum below.
        // OR this address with the command to get a full 8 bit piece of data to write.

        //KEY_DATA(0x40), // the chip can read a matrix of keys, but this is not implemented on the Adafruit board.
        // So this register address is here just for completeness. It is never used.
        //INT_FLAG(0x60), // the chip can generate an interrupt, but this is not implemented on the Adafruit board.
        // So this register address is here just for completeness. It is never used.

        DISPLAY_SETUP(0x80), // The most significant 4 bits are the address of the register for the controlling the LED display
        // The least significant 4 bits are the commands. They are defined in another enum below.
        // OR this address with the command to get a full 8 bit piece of data to write.

        //ROW_INT_SET(0xA0), // the chip has a pin that can be configured as an interrupt of a row pin,
        // but this is not implemented on the Adafruit board.
        // So this register address is here just for completeness. It is never used.

        DIMMING_SET(0xE0); // The most significant 4 bits are the address of the register for the brightness of the LEDs.
        // The least significant 4 bits are the dimming commands. The values are not going to be enums.
        // Dimmest = 0, Brightest = 15 (0xF)
        // OR this address with the command to get a full 8 bit byte to write.

        //TEST_MODE(0xD9); // This register is for HOLTEK (the chip manufacturer) use only. It is
        // documented here but it is never used.

        public final byte byteVal;

        Register(int i) {
            this.byteVal = (byte) i;
        }
    }

    // Commands for the Display data register
    // The display data commands are full 8 bit commands. They are actually bits that turn on
    // specific LED segments to form a character such as a "5" or "A".
    // The mapping of a ASCII character to a command is handled by a method below rather than enums.
    // So I'm skipping this register

    /**
     * Commands for the System setup register (page 10 of the datasheet)
     */
    private enum SystemSetupCommand {
        SYSTEM_SETUP_OSCILLATOR_OFF(0x00),         // bit 8 = 0 TO TURN THE OSCILLATOR OFF
        SYSTEM_SETUP_OSCILLATOR_ON(0x01);         // bit 8 = 1 TO TURN THE OSCILLATOR ON

        public final byte byteVal;

        SystemSetupCommand(int i) {
            this.byteVal = (byte) i;
        }
    }

    // I'm skipping the key matrix data commands since they are never used.

    // I'm skipping the int commands since they are never used.


    /**
     * Commands for the Display setup register (page 11 of the datasheet)
     * NOTE that you have to OR (|) the display off/on bit with the Blinking bits to get a full
     * command to setup blinking.
     * example DisplaySetupCommand.BLINKING_1_HZ | DisplaySetupCommand.DISPLAY_ON to turn on the
     * the display and set it to blink once per second.
     * If you forget to do the OR, and send DisplaySetupCommand.BLINKING_1_HZ, the display on/off
     * bit is 0 and you turn the display off and set it to blink once per second. This is not very
     * useful :-)
     */
    private enum DisplaySetupCommand {
        BLINKING_OFF(0b0000),         // Turn LED blinking off (ie they are on steadily)
        BLINKING_2_HZ(0b0010),        // Set LED blinking to 2 times per second
        BLINKING_1_HZ(0b0100),        // Set LED blinking to 1 times per second
        BLINKING_HALF_HZ(0b0110),     // Set LED blinking to 1 times per second
        DISPLAY_OFF(0b0000),          // Turn the display completely off
        DISPLAY_ON(0b0001);           // Turn the display on

        public final byte byteVal;

        DisplaySetupCommand(int i) {
            this.byteVal = (byte) i;
        }
    }

    // Commands for the Row/Int set register (page 10 of the datasheet)
    // Skipping these because they are not used on the Adafruit board

    // Commands for the Dimming setup register (page 15 of the datasheet)
    // These ccmmands handled by a method not an enum. The reason for this is that the dimming
    // (brightness) level is an int ranging from 0 - 15.

    // There are no commands for the test mode register.

    /**
     * Here are the codes for displaying a character on the LED. These codes are specific to the
     * Adafruit board. The code is the command to write after the display data register address.
     * I'm using a class to create and populate a lookup table. You pass in the character to
     * setStringToDisplay and then get the 2 bytes to write to the register that form the character on
     * the LED from getLEDCodeMostSignificantByte and getLEDCodeLeastSignificantByte.
     */
    private class LEDCode {

        // define a lookup table to map a string to an LED code that will display the character on
        // the display
        private Map<Character, Short> stringToLEDCode = new HashMap<>();

        // constructor
        // populate the lookup table - this comes from the Adafruit C++ library code located at
        // https://github.com/adafruit/Adafruit_LED_Backpack/blob/master/Adafruit_LEDBackpack.cpp
        // In some cases I felt the adafruit font was not really readable. So I substituted my own.
        // In those cases I commented out the adafruit font.

        private LEDCode() {
            // populate the lookup table that maps a string to an LED Code
            stringToLEDCode.put(' ', (short) 0b0000000000000000);            // (blank)
            stringToLEDCode.put('!', (short) 0b0100000000000110);            // !
            //stringToLEDCode.put('!', (short) 0b0000000000000110);            // !
            stringToLEDCode.put('"', (short) 0b0000001000100000);           // "
            stringToLEDCode.put('#', (short) 0b0001001011001110);            // #
            stringToLEDCode.put('$', (short) 0b0001001011101101);            // $
            stringToLEDCode.put('%', (short) 0b0000110000100100);            // %
            stringToLEDCode.put('&', (short) 0b0010001101011101);            // &
            stringToLEDCode.put('\'', (short) 0b0000010000000000);           // '
            stringToLEDCode.put('(', (short) 0b0010010000000000);            // (
            stringToLEDCode.put(')', (short) 0b0000100100000000);            // )
            stringToLEDCode.put('*', (short) 0b0011111111000000);            // *
            stringToLEDCode.put('+', (short) 0b0001001011000000);            // +
            stringToLEDCode.put(',', (short) 0b0000100000000000);            // ,
            stringToLEDCode.put('-', (short) 0b0000000011000000);            // -
            stringToLEDCode.put('.', (short) 0b0100000000000000);            // .
            //stringToLEDCode.put('.', (short) 0b0000000000000000);            // .
            stringToLEDCode.put('/', (short) 0b0000110000000000);            // /
            stringToLEDCode.put('0', (short) 0b0000110000111111);            // 0
            stringToLEDCode.put('1', (short) 0b0000000000000110);            // 1
            stringToLEDCode.put('2', (short) 0b0000000011011011);            // 2
            stringToLEDCode.put('3', (short) 0b0000000010001111);            // 3
            stringToLEDCode.put('4', (short) 0b0000000011100110);            // 4
            stringToLEDCode.put('5', (short) 0b0010000001101001);            // 5
            stringToLEDCode.put('6', (short) 0b0000000011111101);            // 6
            stringToLEDCode.put('7', (short) 0b0000000000000111);            // 7
            stringToLEDCode.put('8', (short) 0b0000000011111111);            // 8
            stringToLEDCode.put('9', (short) 0b0000000011101111);            // 9
            stringToLEDCode.put(':', (short) 0b0001001000000000);            // :
            stringToLEDCode.put(';', (short) 0b0000101000000000);            // ;
            stringToLEDCode.put('<', (short) 0b0010010000000000);            // <
            stringToLEDCode.put('=', (short) 0b0000000011001000);            // =
            stringToLEDCode.put('>', (short) 0b0000100100000000);            // >
            stringToLEDCode.put('?', (short) 0b0101000010000011);            // ?
//            stringToLEDCode.put('?', (short) 0b0001000010000011);            // ?
            stringToLEDCode.put('@', (short) 0b0000001010111011);            // @
            stringToLEDCode.put('A', (short) 0b0000000011110111);            // A
            stringToLEDCode.put('B', (short) 0b0001001010001111);            // B
            stringToLEDCode.put('C', (short) 0b0000000000111001);            // C
            stringToLEDCode.put('D', (short) 0b0001001000001111);            // D
            stringToLEDCode.put('E', (short) 0b0000000011111001);            // E
            stringToLEDCode.put('F', (short) 0b0000000001110001);            // F
            stringToLEDCode.put('G', (short) 0b0000000010111101);            // G
            stringToLEDCode.put('H', (short) 0b0000000011110110);            // H
            stringToLEDCode.put('I', (short) 0b0001001000000000);            // I
            stringToLEDCode.put('J', (short) 0b0000000000011110);            // J
            stringToLEDCode.put('K', (short) 0b0010010001110000);            // K
            stringToLEDCode.put('L', (short) 0b0000000000111000);            // L
            stringToLEDCode.put('M', (short) 0b0000010100110110);            // M
            stringToLEDCode.put('N', (short) 0b0010000100110110);            // N
            stringToLEDCode.put('O', (short) 0b0000000000111111);            // O
            stringToLEDCode.put('P', (short) 0b0000000011110011);            // P
            stringToLEDCode.put('Q', (short) 0b0010000000111111);            // Q
            stringToLEDCode.put('R', (short) 0b0010000011110011);            // R
            stringToLEDCode.put('S', (short) 0b0000000011101101);            // S
            stringToLEDCode.put('T', (short) 0b0001001000000001);            // T
            stringToLEDCode.put('U', (short) 0b0000000000111110);            // U
            stringToLEDCode.put('V', (short) 0b0000110000110000);            // V
            stringToLEDCode.put('W', (short) 0b0010100000110110);            // W
            stringToLEDCode.put('X', (short) 0b0010110100000000);            // X
            stringToLEDCode.put('Y', (short) 0b0001010100000000);            // Y
            stringToLEDCode.put('Z', (short) 0b0000110000001001);            // Z
            stringToLEDCode.put('[', (short) 0b0000000000111001);            // [
            stringToLEDCode.put(']', (short) 0b0000000000001111);            // ]
            stringToLEDCode.put('^', (short) 0b0010100000000000);            // ^
            //stringToLEDCode.put('^', (short) 0b0000110000000011);            // ^
            stringToLEDCode.put('_', (short) 0b0000000000001000);            // _
            stringToLEDCode.put('`', (short) 0b0000000100000000);            // `
            stringToLEDCode.put('a', (short) 0b0000000011011111);            // a my version of a
            //stringToLEDCode.put('a', (short) 0b0001000001011000);            // a Adafruit's a does not look like an a to me.
            stringToLEDCode.put('b', (short) 0b0010000001111000);            // b
            stringToLEDCode.put('c', (short) 0b0000000011011000);            // c
            stringToLEDCode.put('d', (short) 0b0000100010001110);            // d
            stringToLEDCode.put('e', (short) 0b0000100001011000);            // e
            stringToLEDCode.put('f', (short) 0b0000000001110001);            // f
            stringToLEDCode.put('g', (short) 0b0000010010001110);            // g
            stringToLEDCode.put('h', (short) 0b0001000001110000);            // h
            stringToLEDCode.put('i', (short) 0b0001000000000000);            // i
            stringToLEDCode.put('j', (short) 0b0000000000001110);            // j
            stringToLEDCode.put('k', (short) 0b0011011000000000);            // k
            stringToLEDCode.put('l', (short) 0b0000000000110000);            // l
            stringToLEDCode.put('m', (short) 0b0001000011010100);            // m
            stringToLEDCode.put('n', (short) 0b0001000001010000);            // n
            stringToLEDCode.put('o', (short) 0b0000000011011100);            // o
            stringToLEDCode.put('p', (short) 0b0000000101110000);            // p
            stringToLEDCode.put('q', (short) 0b0000010010000110);            // q
            stringToLEDCode.put('r', (short) 0b0000000001010000);            // r
            stringToLEDCode.put('s', (short) 0b0010000010001000);            // s
            stringToLEDCode.put('t', (short) 0b0000000001111000);            // t
            stringToLEDCode.put('u', (short) 0b0000000000011100);            // u
            stringToLEDCode.put('v', (short) 0b0010000000000100);            // v
            stringToLEDCode.put('w', (short) 0b0010100000010100);            // w
//            stringToLEDCode.put('x', (short) 0b0010100011000000);            // x
            stringToLEDCode.put('x', (short) 0b0010110100000000);            // x
            stringToLEDCode.put('y', (short) 0b0010000000001100);            // y
            stringToLEDCode.put('z', (short) 0b0000100001001000);            // z
            stringToLEDCode.put('{', (short) 0b0000100101001001);            // {
            stringToLEDCode.put('}', (short) 0b0010010010001001);            // }
            stringToLEDCode.put('~', (short) 0b0000010100100000);            // ~
        }

        /**
         * Get the 16 bit LED code for a single character
         *
         * @param character
         * @return 16 bit led code that will light up that character on the LED display
         */
        private short getLEDCode(char character) {
            Short element = stringToLEDCode.get(character);
            if (element != null) {
                return element;
            } else {
                throw new IllegalArgumentException(String.format("Unknown LED code: %s in %s", character, LEDCode.class.toString()));
            }
        }

        /**
         * Get the set of 16 bit LED codes for a string
         *
         * @param string
         * @return an array of 16 bit LED codes that will light up the string on the LED display
         */
        private short[] getLEDCodes(String string) {
            short[] returnValue = new short[string.length()];
            // for each character in string, lookup the LED code and
            // put that into the array of LED codes.
            for (int i = 0; i < string.length(); i++) {
                returnValue[i] = getLEDCode(string.charAt(i));
            }
            return returnValue;
        }

        /**
         * Given a string, get an array of 8 bit LED codes that can be sent out via I2C to light up
         * that string on the LED display. Although the LED codes are 16 bits, they have to be sent
         * as 2 8 bit bytes per code.
         *
         * @param string
         * @return an array of 8 bit LED codes, 2 bytes for LED, that will display the string on
         * the LED
         */
        private byte[] getLEDCodesAsBytes(String string) {
            short[] LEDCodes;
            LEDCodes = getLEDCodes(string);
            return getBytesForLEDCodes(LEDCodes);
        }

        /**
         * For one 16 bit LED code, split the code into 2 8 bit bytes. This is needed needed
         * because I can only send 8 bit bytes via I2C.
         *
         * @param code a 16 bit LED code
         * @return two 8 bit LED codes; element 0 are LSB bits, element 1 are MSB bits
         */
        private byte[] getBytesForLEDCode(short code) {
            byte[] returnValue = {0, 0};
            // 0th element are the msbs
            returnValue[0] = (byte) code; // casting to a byte chops off the 8 most significant bits
            // 1st element are the lsbs
            returnValue[1] = (byte) (code >> 8); // shift the 8 msbs into the 8 lsbs
            return returnValue;
        }

        /**
         * For an array of 16 bit LED codes, split each LED code into 2 8 bit bytes. This is
         * needed because I can only send 8 bit bytes via I2C.
         *
         * @param codes array of 16 bit LED codes
         * @return array of 8 bit LED codes, 2 for each 16 bit code, ordering is MSB (n), LSB (n-1),
         * MSB (n-2), LSB (n-2)...
         */
        private byte[] getBytesForLEDCodes(short[] codes) {
            byte[] byteCodesFromLEDCodes = new byte[codes.length * 2];
            byte[] byteCodesFromLEDCode;
            for (int i = 0; i < codes.length; i++) {
                // split the 16 bit code into 2 bytes
                byteCodesFromLEDCode = getBytesForLEDCode(codes[i]);
                // add those codes to the array of 8 bit codes
                // note that I have to multiply i * 2 since there are 2 byte codes for each led
                // code
                byteCodesFromLEDCodes[i * 2] = byteCodesFromLEDCode[0];
                byteCodesFromLEDCodes[i * 2 + 1] = byteCodesFromLEDCode[1];
            }
            return byteCodesFromLEDCodes;
        }

        private Set<Character> getCharacterSet() {
            return stringToLEDCode.keySet();
        }
    }

    /**
     * An enum specifying whether to turn the LED display on or off.
     */
    public enum LEDSwitch {
        ON,
        OFF,
    }

    /**
     * An enum specifying whether to turn the controller chip for the display on or off. This is
     * different than the display itself. The display can be off but the controller can be on.
     * Practically, to be useful, the controller has to be on.
     */
    public enum ChipStatus {
        ON,
        OFF
    }

    /**
     * An enum describing the rate that the LED display blinks at.
     * I'm exposing a public blink rate enum in more kid friendly terms than the DisplaySetupCommand
     * enum. Also, I do not want public exposure of the DisplaySetupCommand enum.
     */
    public enum LEDBlinkRate {
        ONCE_PER_SECOND,
        TWICE_PER_SECOND,
        ONCE_PER_TWO_SECONDS,
        NO_BLINK
    }

    /**
     * An enum for specifying which of the four characters on the display to operate on.
     */
    public enum DisplayPosition {
        LEFT,
        MIDDLE_LEFT,
        MIDDLE_RIGHT,
        RIGHT
    }


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS  and GETTERS and SETTERS
    //
    // data fields can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    /**
     * controller status
     */
    private ChipStatus chipStatus;

    public ChipStatus getChipStatus() {
        return chipStatus;
    }

    //
    // BRIGHTNESS LEVEL
    //

    /**
     * Variable mirroring the brightness level on the controller
     */
    private int brightnessLevel = 7;

    public int getBrightnessLevel() {
        return brightnessLevel;
    }

    /**
     * Set the brightness level of the LEDs. 1 is dim. 15 is bright.
     * @param brightnessLevel 1 to 15 (dim to bright)
     */
    public void setBrightnessLevel(int brightnessLevel) {
        // the brightness level is a integer between 0 and 15
        // so make sure the requested level falls into that range
        if (brightnessLevel < 0) brightnessLevel = 0;
        if (brightnessLevel > 15) brightnessLevel = 15;

        this.brightnessLevel = brightnessLevel;

        // now actually set the brightness level on the controller
        byte byteToWrite;
        // create the single byte address/command
        byteToWrite = (byte) (Register.DIMMING_SET.byteVal | brightnessLevel);
        // write the address/command via I2C
        writeSingleByte(byteToWrite);
    }

    //
    // ON AND OFF
    //

    /**
     * Variable to indicate whether the LEDS are on or off.
     */
    private LEDSwitch ledSwitch = LEDSwitch.OFF;

    /**
     * Get whether the LEDs are on or off.
     * @return
     */
    public LEDSwitch getLedSwitch() {
        return ledSwitch;
    }

    //
    // BLINK RATE
    //

    /**
     * Variable to store the blink rate of the LEDs
     */
    private LEDBlinkRate ledBlinkRate = LEDBlinkRate.NO_BLINK;

    /**
     * Get the blink rate of the display.
     * @return enum indicating the current blink rate
     */
    public LEDBlinkRate getLedBlinkRate() {
        return ledBlinkRate;
    }

    /**
     * Set the blink rate of the display.
     * @param ledBlinkRate enum indicating which blink rate you want
     */
    public void setLedBlinkRate(LEDBlinkRate ledBlinkRate) {
        DisplaySetupCommand blinkCommand;
        // get the Display setup command given the requested blink rate
        blinkCommand = getBlinkSetupCommand(ledBlinkRate);
        // set the tracking variable
        this.ledBlinkRate = ledBlinkRate;
        writeBlinkRate(blinkCommand);
    }

    //
    // DISPLAY
    //

    /**
     * The display is 4 characters. I'm keeping a variable that mirrors the display characters. So
     * it will always be 4 characters long.
     */
    private String displayString = "   ";

    /**
     * Get the current display string
     * @return
     */
    public String getDisplayString() {
        return displayString;
    }

    /**
     * Effectively, this method clears the display and then display a string of up to 4 characters.
     * If your string has less then 4 characters, it puts a blank space in for the missing characters.
     * The display string is right justified. For sample if you display "15" you get "  15" on the
     * display, not "15  ".
     *
     * @param displayString a string of up to 4 characters. If you provide more than 4 characters,
     *                      you will only get the 4 rightmost characters displayed.
     */
    public void setDisplayString(String displayString) {
        // there are only 4 charagters available in the display,truncate any more characters
        if (displayString.length() > 4) this.displayString = displayString.substring(0, 3);

        // if the string to display is less than 4 characters, fill in with blanks
        if (displayString.length() == 0) this.displayString = "    ";
        if (displayString.length() == 1) this.displayString = "   " + displayString;
        if (displayString.length() == 2) this.displayString = "  " + displayString;
        if (displayString.length() == 3) this.displayString = "  " + displayString;
        this.displayString = displayString;

        // display the string on the LED display
        displayLEDString(this.displayString);
    }

    /**
     * Inserts a character into the display string at the position specified. Characters in the
     * other 3 positions are left unchanged.
     *
     * @param displayCharacter character to insert
     * @param displayPosition  position to insert the character
     */
    public void setDisplayCharacter(char displayCharacter, DisplayPosition displayPosition) {
        byte register = 0x0;
        // Note that a char cannot be empty so I don't check for that
        // insert the character to be displayed into the current display string
        StringBuilder builder = new StringBuilder(this.displayString);
        switch (displayPosition) {
            case LEFT:
                this.displayString = insertLEDCharAt(this.displayString, 0, displayCharacter);
                register = Register.LEFT_LED_CHARACTER.byteVal;
                break;
            case MIDDLE_LEFT:
                this.displayString = insertLEDCharAt(this.displayString, 1, displayCharacter);
                register = Register.LEFT_MIDDLE_LED_CHARACTER.byteVal;
                break;
            case MIDDLE_RIGHT:
                this.displayString = insertLEDCharAt(this.displayString, 2, displayCharacter);
                register = Register.RIGHT_MIDDLE_LED_CHARACTER.byteVal;
                break;
            case RIGHT:
                this.displayString = insertLEDCharAt(this.displayString, 3, displayCharacter);
                register = Register.RIGHT_LED_CHARACTER.byteVal;
                break;
        }

        //now that the display string is created, I could just sent all 4 characters to the display.
        // But that takes up I2C bandwidth with 8 bytes of data and I'm really only changing
        // 2 bytes. So it is worth it to make the code more complex and send only the single
        // character that is changing.
        displayLEDCharacter(displayCharacter, register);
    }


    /**
     * Inserts a single character into a string. For example, if the string is ABCD and you insert
     * '6' in position 1, you get AB6D.
     *
     * @param displayString usually the currently displayed string, but it could be any string
     * @param index         location to insert the character into. Note this is 0 indexed.
     * @param character     the character to insert into the string
     * @return the new string with the character inserted into it
     */
    private String insertLEDCharAt(String displayString, int index, char character) {
        StringBuilder builder = new StringBuilder(displayString);
        // make sure that the display string is long enough to hold the new character. You can't
        // insert a character into a location (index) that does not exist or you crash.
        if (index > displayString.length() - 1) {
            builder.setLength(index + 1);
        }
        builder.setCharAt(index, character);
        return builder.toString();
    }

    //
    // I2C ADDRESS
    //

    /**
     * The I2C address of the controller chip.
     */
    private I2cAddr i2cAddr;

    /**
     * Get the I2C address of the display.
     * @return
     */
    public I2cAddr getI2cAddr() {
        return i2cAddr;
    }

    /**
     * Set the I2c address of the controller chip. Don't do this unless you have soldered some
     * shorts onto the Adafruit board to set an address other than the default. This class will
     * default to the normal address for the board so no need to do anything if you have not altered
     * the board.
     *
     * @param i2cAddr
     */
    public void setI2cAddr(I2cAddr i2cAddr) {
        this.i2cAddr = i2cAddr;
        // I wonder what will happen if the I2C address is changed after the deviceClient is created?
        // I probably should check to see if the deviceClient exists and if so attempt to change
        // the address. But I have no clue what will happen. So I'm not investing time in this yet.
    }

    /**
     * Set the I2c address of the controller chip. Don't do this unless you have soldered some
     * shorts onto the Adafruit board to set an address other than the default. This class will
     * default to the normal address for the board so no need to do anything if you have not altered
     * the board.
     *
     * @param i2cAddr
     */
    public void setI2cAddr(int i2cAddr) {
        this.i2cAddr = I2cAddr.create7bit(i2cAddr);
        // I wonder what will happen if the I2C address is changed after the deviceClient is created?
        // I probably should check to see if the deviceClient exists and if so attempt to change
        // the address. But I have no clue what will happen. So I'm not investing time in this yet.
    }

    /**
     * declare a variable to hold the LED code lookup table for mapping characters to the display font
     */
    private LEDCode ledCode;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * This constructor is automatically called in the background when the opmode starts up.
     * Don't call it yourself unless you know what you are doing. Normal creation of the display
     * goes something like this:
     * backpackLED = hardwareMap.get(AdafruitBackpackLED.class,"AdafruitBackpackLED");
     * Don't forget to change the string to what you configured on your phone.
     */
    public AdafruitBackpackLED(I2cDeviceSynch deviceClient) {
        super(deviceClient, true);
        // set the default address in this instance of the class
        setI2cAddr(0x70);
        // set the I2C address for the deviceClient
        this.deviceClient.setI2cAddress(getI2cAddr());

        super.registerArmingStateCallback(false); // Deals with USB cables getting unplugged
        // Sensor starts off disengaged so we can change things like I2C address. Need to engage
        this.deviceClient.engage();
        // Since some other piece of code calls doInitialize(), this is not needed anymore.
        //initializeAdafruitBackpackLED();
    }

    //*********************************************************************************************
    //          helper methods
    //
    //*********************************************************************************************

    /**
     * This method is called by the code that creates the deviceClient. It in turn calls the
     * initialization method for the controller.
     *
     * @return true if success, false if not?
     */
    @Override
    protected synchronized boolean doInitialize() {
        initializeAdafruitBackpackLED();
        return true;
    }

    /**
     * Initialize the display to a good starting point
     */
    private void initializeAdafruitBackpackLED() {
        // create the LED code mapping object (lookup table for the fonts)
        this.ledCode = new LEDCode();
        // turn the system oscillator on
        turnOscillatorOn();
        // check to make sure there is an led controller at this address
        // turn the display off
        turnLEDsOff();
        // turn the blinking off;
        setLedBlinkRate(LEDBlinkRate.NO_BLINK);
        // clear the characters from the display
        clear();
        // set the brightness
        setBrightnessLevel(7);
    }

    // I need to find a way to check to see if there is a controller actually out on the bus and
    // write a method for that. There are not many registers that provide read data and none that
    // identify the controller. I think the only way will be to turn the display off, write a known
    // bit pattern to one of the display registers and then read it back to see if I get back what
    // I wrote. At least then I would know something is out there.

    /**
     * This method maps the blink rate from the more user friendly LEDBlinkRate enum to the less
     * friendly enum used in the register for the display setup
     *
     * @param ledBlinkRate
     * @return enum value from the DisplaySetupCommand register that corresponds to the LEDBlinkRate
     * enum.
     */
    private DisplaySetupCommand getBlinkSetupCommand(LEDBlinkRate ledBlinkRate) {
        DisplaySetupCommand blinkCommand = DisplaySetupCommand.BLINKING_OFF;
        switch (ledBlinkRate) {
            case NO_BLINK:
                blinkCommand = DisplaySetupCommand.BLINKING_OFF;
                break;
            case ONCE_PER_SECOND:
                blinkCommand = DisplaySetupCommand.BLINKING_1_HZ;
                break;
            case TWICE_PER_SECOND:
                blinkCommand = DisplaySetupCommand.BLINKING_2_HZ;
                break;
            case ONCE_PER_TWO_SECONDS:
                blinkCommand = DisplaySetupCommand.BLINKING_HALF_HZ;
                break;
        }
        return blinkCommand;
    }

    //*********************************************************************************************
    //          Control methods
    //
    // these methods send commands to the controller
    //*********************************************************************************************

    /**
     * This method brings the controller chip out of its sleep mode by turning on its oscillator.
     * In effect it turns the controller on.
     */
    private void turnOscillatorOn() {
        this.chipStatus = ChipStatus.ON;
        byte byteToWrite = (byte) (Register.SYSTEM_SETUP.byteVal | SystemSetupCommand.SYSTEM_SETUP_OSCILLATOR_ON.byteVal);
        writeSingleByte(byteToWrite);
    }

    /**
     * This method puts the controller chip into sleep mode by turning off its oscillator.
     * In effect it turns the controller off. Normally this is done to save power. But for FTC the
     * power usage is so small that it is not important. So practically the code does not ever call
     * this method.
     */
    private void turnOscillatorOff() {
        this.chipStatus = ChipStatus.OFF;
        // for the controller chip, the data to write is a single byte containing both the address and command
        byte byteToWrite = (byte) (Register.SYSTEM_SETUP.byteVal | SystemSetupCommand.SYSTEM_SETUP_OSCILLATOR_OFF.byteVal);
        writeSingleByte(byteToWrite);
    }

    /**
     * Turn the LED display off.
     */
    public void turnLEDsOff() {
        // for the controller chip, the data to write is a single byte containing both the address and command
        // I want to keep the current led blink rate so I have to create a command that gets the current rate too
        DisplaySetupCommand blinkCommand = getBlinkSetupCommand(this.ledBlinkRate);
        // create the address / command byte
        byte byteToWrite = (byte) (Register.DISPLAY_SETUP.byteVal | blinkCommand.byteVal | DisplaySetupCommand.DISPLAY_OFF.byteVal);

        // write the address/command via I2C
        writeSingleByte(byteToWrite);

        // set the class variable that traces the LED on/off status
        this.ledSwitch = LEDSwitch.OFF;
    }

    /**
     * Turn the LED display on.
     */
    public void turnLEDsOn() {
        // for the controller chip, the data to write is a single byte containing both the address and command
        // I want to keep the current led blink rate so I have to create a command that gets the current rate too
        DisplaySetupCommand blinkCommand = getBlinkSetupCommand(this.ledBlinkRate);
        // create the address / command byte
        byte byteToWrite = (byte) (Register.DISPLAY_SETUP.byteVal | blinkCommand.byteVal | DisplaySetupCommand.DISPLAY_ON.byteVal);

        // write the address/command via I2C
        writeSingleByte(byteToWrite);

        // set the class variable that traces the LED on/off status
        this.ledSwitch = LEDSwitch.ON;
    }

    /**
     * Clear the display - in other words do not show any characters on the display. Note that the
     * display can be on or off when you do this. Turning the display on or off is an independent
     * operation. Even if the display is cleared, it can still be on, ready to display characters
     * as soon as you want to.
     */
    public void clear() {
        setDisplayString("    ");
    }

    // I decided that these commands were redundant. I originally put them in because the user
    // would not have to know anything about the enum for the blinking. They just see a command and
    // use it. But it creates 4 more methods they have to sort through so I removed them for now.
    // Use setLedBlinkRate instead.

//    public void setLEDBlinkOncePerSecond() {
//        setBlinkingRate(DisplaySetupCommand.BLINKING_1_HZ);
//        this.ledBlinkRate = LEDBlinkRate.ONCE_PER_SECOND;
//    }
//
//    public void setLEDBlinkTwicePerSecond() {
//        setBlinkingRate(DisplaySetupCommand.BLINKING_2_HZ);
//        this.ledBlinkRate = LEDBlinkRate.TWICE_PER_SECOND;
//    }
//
//    public void setLEDBlinkOncePerTwoSeconds() {
//        setBlinkingRate(DisplaySetupCommand.BLINKING_HALF_HZ);
//        this.ledBlinkRate = LEDBlinkRate.ONCE_PER_TWO_SECONDS;
//    }
//
//    public void setLEDBlinkOFF() {
//        setBlinkingRate(DisplaySetupCommand.BLINKING_OFF);
//        this.ledBlinkRate = LEDBlinkRate.NO_BLINK;
//    }

    /**
     * Write the blink command out to the controller on the I2C bus. This will not affect whether
     * the display is on or off. You can set a blink rate even if the display is off. Then when you
     * turn it on, it immediately starts blinking. Note this is private. The public method for
     * controlling the blink rate is setLedBlinkRate
     *
     * @param blinkCommand the bit code associated with the desired blinking rate. See DisplaySetupCommand
     *                     enum.
     */
    private void writeBlinkRate(DisplaySetupCommand blinkCommand) {
        byte byteToWrite;
        // for this controller chip, the data to write is a single byte containing both the address and command.
        // The blink rate is contained in the same register as the LED on and off.
        // I want to keep the current LED display on or off so I have to create a command that does not change that
        if (getLedSwitch() == LEDSwitch.OFF) {
            byteToWrite = (byte) (Register.DISPLAY_SETUP.byteVal | blinkCommand.byteVal | DisplaySetupCommand.DISPLAY_OFF.byteVal);
        } else {
            byteToWrite = (byte) (Register.DISPLAY_SETUP.byteVal | blinkCommand.byteVal | DisplaySetupCommand.DISPLAY_ON.byteVal);
        }
        // write the address/command via I2C
        writeSingleByte(byteToWrite);
    }

    /**
     * This method accepts a string of up to 4 characters in length, converts the string into the
     * LED codes that turn on the individual LED segments in the display for each character, breaks
     * the LED codes, which are 16 bits long, into 2 8 bit bytes and then writes them to the
     * starting register of the LEDs in the controller. Note this is private. The public method to
     * display a string is setDisplayString.
     *
     * @param stringToDisplay
     */
    private void displayLEDString(String stringToDisplay) {
        // did someone ask me to display more than 4 characters?
        if (stringToDisplay.length() > 4) {
            // truncate the string to 4 characters only (the display is only 4 characters)
            stringToDisplay = stringToDisplay.substring(0, 3);
        }

        byte[] displayCodeBuffer;
        // get an array of LED codes for the string in this.displayString. Codes are initially 16
        // bits long, but since I2C only can send 8 bits per transaction, the 16 bits have to be
        // broken into two 8 bit bytes
        displayCodeBuffer = this.ledCode.getLEDCodesAsBytes(stringToDisplay);
        // write the codes to the I2C bus
        write(Register.DISPLAY_DATA.byteVal, displayCodeBuffer);
    }

    /**
     * This method is different than displayLEDString in that it will only change one character in
     * the 4 character display. The other 3 characters remain as they were. The character is
     * written to the specified address. First the 16 bit LED code is obtained and then broken into
     * two 8 bit bytes to be transmitted via I2C to the controller register specified. The
     * controller auto increments the address for the second byte so no need to handle that. I
     * strongly recommend you do not use this method directly. Use setDisplayCharacter instead.
     *
     * @param displayCharacter character to display
     * @param register         the address of the register to use for the display
     */
    private void displayLEDCharacter(char displayCharacter, byte register) {
        byte[] displayCodeBuffer;
        // get an array of LED codes for the string in this.displayString. Codes are bytes
        displayCodeBuffer = this.ledCode.getLEDCodesAsBytes(Character.toString(displayCharacter));
        // write the codes
        write(register, displayCodeBuffer);
    }

    /**
     * Get the manufacturer of this display.
     *
     * @return
     */
    @Override
    public Manufacturer getManufacturer() {
        return Manufacturer.Adafruit;
    }

    /**
     * Get a string that describes the display.
     *
     * @return
     */
    @Override
    public String getDeviceName() {
        return "Adafruit Alphanumeric Display";
    }

    //*********************************************************************************************
    //          I2C low level read and write methods
    //*********************************************************************************************

    /**
     * Read 8 bits of data from the specified register.
     *
     * @param register address to read from
     * @return data in the register
     */
    private synchronized byte read8(int register) {
        return deviceClient.read8(register);
    }

    /**
     * Read bytes from registers starting at the given register address and then reading the next
     * higher register and so on until you have read the desired number of registers.
     *
     * @param register            starting address of the registers to read
     * @param numberReadLocations how many registers to read
     * @return an array of byte data contained in each register
     */
    private synchronized byte[] read(int register, int numberReadLocations) {
        return deviceClient.read(register, numberReadLocations);
    }

    /**
     * The SDK does not have a I2C method to write a single byte address/command. It can only write
     * an 8 bit address followed by 8 bits of data. So I'm going to
     * cob it by writing the byte twice. First the byte gets written in the address slot and then
     * it gets written in the data slot. This works although it creates extra traffic on the I2C
     * bus.
     *
     * @param addressCommandData
     */
    private synchronized void writeSingleByte(byte addressCommandData) {
        deviceClient.write8(addressCommandData, addressCommandData, I2cWaitControl.WRITTEN);
    }

    /**
     * Write a byte of data to the specified I2C register address.
     *
     * @param register
     * @param data
     */
    private synchronized void write8(byte register, int data) {
        deviceClient.write8(register, data, I2cWaitControl.WRITTEN);
    }

    /**
     * Write bytes of data starting at the specified register address and then incrementing up one
     * register at a time until there is no more data to write.
     *
     * @param register starting address of the register to write
     * @param data     an array of bytes of data to write
     */
    private synchronized void write(byte register, byte[] data) {
        deviceClient.write(register, data, I2cWaitControl.WRITTEN);
    }

    //*********************************************************************************************
    //          test and demo methods
    //*********************************************************************************************

    //**********************************************************************************************
    //* Display on/off test / demo
    //**********************************************************************************************

    /**
     * Test or demo the ability to turn the LED display on and off
     *
     * @return true when the test is complete
     */
    public boolean testOnOff() {
        setDisplayString("ON!");
        turnLEDsOff();
        delay(1000);
        turnLEDsOn();
        delay(1000);
        turnLEDsOff();
        return true;
    }

    private void delay(int mSec) {
        try {
            Thread.sleep((int) (mSec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //**********************************************************************************************
    //* Display blinking test / demo
    //**********************************************************************************************

    // Demo / test the blinking capabilties of the display.

    // Variables for the blink rate test/demo that have to be visible for both the methods of the
    // test/demo. This means they have to be outside the scope of each method.
    private LEDBlinkRate state;
    private double timeInterval;
    private ElapsedTime timer;
    private boolean testComplete = false;

    /**
     * Call this method once to setup a demo of the blinking capabilities of the display.
     * After this is called, then you call the update inside a loop in the opmode.
     */
    public void setupBlinkingTest() {
        // 5 seconds between changes in blink rate
        timeInterval = 5000;
        // set initial state
        state = LEDBlinkRate.NO_BLINK;
        // create the timer and set it to 0
        timer = new ElapsedTime();
        timer.reset();
        // display a string
        setDisplayString("NoBl");
        setBrightnessLevel(7);
        turnLEDsOn();
        // start with no blinking
        setLedBlinkRate(LEDBlinkRate.NO_BLINK);
        testComplete = false;
    }

    /**
     * Demo the blinking capabilities of the display.
     * Call this method after calling testBlinkingSetup. Call it inside a loop in the opmode so it
     * gets called repeatedly. This method implements a state machine and each call runs the machine
     * one time.
     *
     * @return true when the test has completed all phases
     */
    public boolean updateBlinkingTest() {
        switch (state) {
            case NO_BLINK:
                // after time interval has passed move to blink once per 2 seconds
                if (timer.milliseconds() > timeInterval) {
                    state = LEDBlinkRate.ONCE_PER_TWO_SECONDS;
                    setDisplayString("2Sec");
                    setLedBlinkRate(state);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case ONCE_PER_TWO_SECONDS:
                // after time interval has passed move to blink once per second
                if (timer.milliseconds() > timeInterval) {
                    state = LEDBlinkRate.ONCE_PER_SECOND;
                    setDisplayString("1Sec");
                    setLedBlinkRate(state);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case ONCE_PER_SECOND:
                // after time interval has passed move to blink twice per second
                if (timer.milliseconds() > timeInterval) {
                    state = LEDBlinkRate.TWICE_PER_SECOND;
                    setDisplayString("Half");
                    setLedBlinkRate(state);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case TWICE_PER_SECOND:
                // after time interval has passed repeat the whole cycle
                if (timer.milliseconds() > timeInterval) {
                    state = LEDBlinkRate.NO_BLINK;
                    setDisplayString("NoBl");
                    setLedBlinkRate(state);
                    timer.reset();
                    testComplete = true;
                }
                break;
        }

        return testComplete;
    }

    //**********************************************************************************************
    //* Display brighness test / demo
    //**********************************************************************************************

    // Brightness level test / demo. Shows how the brightness levels (1-15) work and lets the user
    //see how bright they are.

    /**
     * States for the brightness level demo. Each one corresponds to a brightness level (1-15)
     */
    private enum BrightnessStates {
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        ELEVEN(11),
        TWELVE(12),
        THIRTEEN(13),
        FOURTEEN(14),
        FIFTEEN(15);


        public final int intVal;

        BrightnessStates(int i) {
            this.intVal = i;
        }
    }

    // Variables for the brightness level state machine
    private BrightnessStates brightnessState;

    /**
     * Brightness level test / demo.
     * Call this setup method one time. Then call the update method in a loop in the opmode.
     */
    public void setupBrightnessLevelTest() {
        // 2 seconds between changes in blink rate
        timeInterval = 2000;
        // set initial state
        brightnessState = BrightnessStates.FIFTEEN;
        // create the timer and set it to 0
        timer = new ElapsedTime();
        timer.reset();
        // display a string
        setDisplayString("15");
        turnLEDsOn();
        // no blinking
        setLedBlinkRate(LEDBlinkRate.NO_BLINK);
        setBrightnessLevel(15);
        testComplete = false;
    }

    /**
     * Brightness level test / demo. Shows how the brightness levels (1-15) work and lets the user
     * see how bright they are.
     * Call the setup method above one time. Then call this update method in a loop in the opmode.
     *
     * @return true when all phases of the test are complete
     */
    public boolean updateBrightnessLevelTest() {
        switch (brightnessState) {
            case FIFTEEN:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.FOURTEEN;
                    setDisplayString("14");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case FOURTEEN:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.THIRTEEN;
                    setDisplayString("13");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case THIRTEEN:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.TWELVE;
                    setDisplayString("12");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case TWELVE:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.ELEVEN;
                    setDisplayString("11");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case ELEVEN:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.TEN;
                    setDisplayString("10");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case TEN:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.NINE;
                    setDisplayString("9");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case NINE:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.EIGHT;
                    setDisplayString("8");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case EIGHT:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.SEVEN;
                    setDisplayString("7");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case SEVEN:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.SIX;
                    setDisplayString("6");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case SIX:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.FIVE;
                    setDisplayString("5");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case FIVE:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.FOUR;
                    setDisplayString("4");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case FOUR:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.THREE;
                    setDisplayString("3");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case THREE:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.TWO;
                    setDisplayString("2");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case TWO:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.ONE;
                    setDisplayString("1");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = false;
                }
                break;
            case ONE:
                // after time interval has passed move to lower brightness level
                if (timer.milliseconds() > timeInterval) {
                    brightnessState = BrightnessStates.FIFTEEN;
                    setDisplayString("15");
                    setBrightnessLevel(brightnessState.intVal);
                    timer.reset();
                    testComplete = true;
                }
                break;
        }
        return testComplete;
    }

    //**********************************************************************************************
    //* Character display test / demo
    //**********************************************************************************************

    // Displays each character that can be displayed. Some of them are kind of funky looking because
    // of the limits of the LED.

    /**
     * Enum for the list of states in a state machine used to run through all of the characters
     * that can be displayed and display each one.
     */
    private enum CharacterDisplayStates {
        START,
        CONTINUE,
        NEXT,
        DONE
    }

    private CharacterDisplayStates characterDisplayState;
    private Set<Character> characters;
    private Iterator<Character> iterator;
    private char characterToDisplay;
    private String charactersToDisplay;
    private StringBuilder displayChars;

    // get a list of all of the characters in the LEDCode map
    // iterate across them and display each one in turn

    /**
     * Character mapping test / demo display of characters. Call this setup once in your opmode.
     * Then call the associated update in a loop to display each character.
     */
    public void setupCharacterTest() {
        // 2 seconds between changes in character
        timeInterval = 2000;
        // create the timer and set it to 0
        timer = new ElapsedTime();
        timer.reset();
        // set the initial state
        characterDisplayState = CharacterDisplayStates.START;
        // display nothing
        setDisplayString("    ");
        // no blinking
        setLedBlinkRate(LEDBlinkRate.NO_BLINK);
        setBrightnessLevel(7);
        turnLEDsOn();
        // get the list of characters and an iterator to move across the characters in the set
        characters = ledCode.getCharacterSet();
        iterator = characters.iterator();
        displayChars = new StringBuilder("   ");
        testComplete = false;
    }

    /**
     * Display each character on the display, wait a bit, then display the next one.
     * Make sure to call the setup above first. Then call this method in a loop to display each
     * character. Once it returns true you can stop your loop. (It is a state machine.)
     *
     * @return true if all characters have been displayed
     */
    public boolean updateCharacterTest(Telemetry telemetry) {
        switch (characterDisplayState) {
            case START:
                characterDisplayState = CharacterDisplayStates.NEXT;
                break;
            case CONTINUE:
                // if the time has elapsed move on to the next character
                if (timer.milliseconds() > timeInterval) {
                    characterDisplayState = CharacterDisplayStates.NEXT;
                }
                break;
            case NEXT:
                // is there a next character in the set?
                if (iterator.hasNext()) {
                    // yes there is, get it
                    characterToDisplay = iterator.next();
                    // create a string of 4 of the characters and send the string to the display
                    // clear the string first
                    displayChars.setLength(0);
                    // then append the character to display 4 times
                    charactersToDisplay = displayChars.append(characterToDisplay).append(characterToDisplay).append(characterToDisplay).append(characterToDisplay).toString();
                    // send it to the led display
                    setDisplayString(charactersToDisplay);
                    // Driver station feedback
                    telemetry.addData("Now displaying: ", charactersToDisplay);
                    telemetry.update();
                    timer.reset();
                    characterDisplayState = CharacterDisplayStates.CONTINUE;
                } else {
                    // no more characters, done
                    setDisplayString("Done");
                    characterDisplayState = CharacterDisplayStates.DONE;
                    testComplete = true;
                }
                break;
            case DONE:
                break;
        }
        return testComplete;
    }

    //**********************************************************************************************
    //* Misc test / demo
    //**********************************************************************************************

    public void miscTests(Telemetry telemetry) {
        // test the clearing of the display
        // display something
        setDisplayString("CLR");
        delay(2000);
        clear();
        delay(2000);

        // test the manufacturer and description methods
        telemetry.addData("Manufacturer = ", getManufacturer().toString());
        telemetry.addData("Description = ", getDeviceName());
        telemetry.update();
        delay(2000);

        // test the single character replacement
        setDisplayString("9999");
        telemetry.addData("Display should be = ", "9999");
        telemetry.update();
        delay(2000);

        setDisplayCharacter('A', DisplayPosition.LEFT);
        telemetry.addData("Display should be = ", "A999");
        telemetry.update();
        delay(2000);

        setDisplayCharacter('B', DisplayPosition.MIDDLE_LEFT);
        telemetry.addData("Display should be = ", "AB99");
        telemetry.update();
        delay(2000);

        setDisplayCharacter('C', DisplayPosition.MIDDLE_RIGHT);
        telemetry.addData("Display should be = ", "ABC9");
        telemetry.update();
        delay(2000);

        setDisplayCharacter('D', DisplayPosition.RIGHT);
        telemetry.addData("Display should be = ", "ABCD");
        telemetry.update();
        delay(2000);
    }
}
