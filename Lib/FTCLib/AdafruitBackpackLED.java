package org.firstinspires.ftc.teamcode.Lib.FTCLib;

// The implementation of the LED circuit to the Adafruit Backpack driver is specific to the
// printed circuit board that Adafruit sells. So the control is specific to this board can't be
// used with any other board that uses the HT16K33 LED Controller.

import android.view.Display;
import android.widget.DialerFilter;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.hardware.I2cWaitControl;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.TypeConversion;

import org.firstinspires.ftc.teamcode.opmodes.GenericTest.DifferentialDrive;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AdafruitBackpackLED {

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
     * REGISTER provides symbolic names for device registers
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

    // Commands for the System setup register (page 10 of the datasheet)

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

    // Commands for the Display setup register (page 11 of the datasheet)
    // NOTE that you have to OR (|) the display off/on bit with the Blinking bits to get a full
    // command to setup blinking.
    // example DisplaySetupCommand.BLINKING_1_HZ | DisplaySetupCommand.DISPLAY_ON to turn on the
    // the display and set it to blink once per second.
    // If you forget to do the OR, and send DisplaySetupCommand.BLINKING_1_HZ, the display on/off
    // bit is 0 and you turn the display off and set it to blink once per second. This is not very
    // useful :-)

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

    // Here are the codes for displaying a character on the LED. These codes are specific to the
    // Adafruit board. The code is the command to write after the display data register address.
    // I'm using a class to create and populate a lookup table. You pass in the character to
    // setStringToDisplay and then get the 2 bytes to write to the register that form the character on
    // the LED from getLEDCodeMostSignificantByte and getLEDCodeLeastSignificantByte.

    private class LEDCode {
//
//        private String stringToDisplay;
//
//        public void setStringToDisplay(String stringToDisplay) {
//            this.stringToDisplay = stringToDisplay;
//            byte[] LEDCodeAsBytes = getBytesForLEDCode(getLEDCode(stringToDisplay));
//            this.LEDCodeLeastSignificantByte = LEDCodeAsBytes[1];
//            this.LEDCodeMostSignificantByte = LEDCodeAsBytes[0];
//        }
//
//        private byte LEDCodeMostSignificantByte;
//
//        public byte getLEDCodeMostSignificantByte() {
//            return LEDCodeMostSignificantByte;
//        }
//
//        private byte LEDCodeLeastSignificantByte;
//
//        public byte getLEDCodeLeastSignificantByte() {
//            return LEDCodeLeastSignificantByte;
//        }

        // define a lookup table to map a string to an LED code that will display the character on
        // the display
        private Map<Character, Short> stringToLEDCode;

        // constructor
        // populate the lookup table - this comes from the Adafruit C++ library code located at
        // https://github.com/adafruit/Adafruit_LED_Backpack/blob/master/Adafruit_LEDBackpack.cpp

        private LEDCode() {
            // populate the lookup table that maps a string to an LED Code
            stringToLEDCode.put(' ', (short) 0b0000000000000000);            // (blank)
            stringToLEDCode.put('!', (short) 0b0000000000000110);            // !
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
            stringToLEDCode.put('.', (short) 0b0000000000000000);            // .
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
            stringToLEDCode.put('?', (short) 0b0001000010000011);            // ?
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
            stringToLEDCode.put('^', (short) 0b0000110000000011);            // ^
            stringToLEDCode.put('_', (short) 0b0000000000001000);            // _
            stringToLEDCode.put('`', (short) 0b0000000100000000);            // `
            stringToLEDCode.put('a', (short) 0b0001000001011000);            // a
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
            stringToLEDCode.put('x', (short) 0b0010100011000000);            // x
            stringToLEDCode.put('y', (short) 0b0010000000001100);            // y
            stringToLEDCode.put('z', (short) 0b0000100001001000);            // z
            stringToLEDCode.put('{', (short) 0b0000100101001001);            // {
            stringToLEDCode.put('!', (short) 0b0001001000000000);            // |
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
         * Get the set of LED codes for a string
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
        public byte[] getLEDCodesAsBytes(String string) {
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
                byteCodesFromLEDCodes[i] = byteCodesFromLEDCode[0];
                byteCodesFromLEDCodes[i + 1] = byteCodesFromLEDCode[1];
            }
            return byteCodesFromLEDCodes;
        }

        private Set<Character> getCharacterSet() {
            return stringToLEDCode.keySet();
        }
    }

    public enum LEDSwitch {
        ON,
        OFF,
    }

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

//    /**
//     * This class maps the publicly exposed LEDBlinkRate enum to the private DisplaySetupCommand enum.
//     * This is done for the reasons described above in the commenst for the LEDBlinkRate enum.
//     */
//    private class BlinkCodeMap {
//
//        // create a lookup table for mapping one to the other
//        private Map<LEDBlinkRate, DisplaySetupCommand> LEDBlinkMap;
//
//        /**
//         * use the constructor to populate the lookup table
//         */
//        private BlinkCodeMap() {
//            LEDBlinkMap.put(LEDBlinkRate.NO_BLINK, DisplaySetupCommand.DISPLAY_OFF);
//            LEDBlinkMap.put(LEDBlinkRate.ONCE_PER_SECOND, DisplaySetupCommand.BLINKING_1_HZ);
//            LEDBlinkMap.put(LEDBlinkRate.TWICE_PER_SECOND, DisplaySetupCommand.BLINKING_2_HZ);
//            LEDBlinkMap.put(LEDBlinkRate.ONCE_PER_TWO_SECONDS, DisplaySetupCommand.BLINKING_HALF_HZ);
//        }
//
//        /**
//         * Return the DisplaySetupCommand enum associated with the given LEDBlinkRate enum
//         *
//         * @param ledBlinkRate
//         * @return The DisplaySetupCommand enum.
//         */
//        private DisplaySetupCommand getDisplaySetupCommandEnum(LEDBlinkRate ledBlinkRate) {
//            return LEDBlinkMap.get(ledBlinkRate);
//        }
//
//        /**
//         * Get the command bits from the DisplaySetupCommand that can be used to set the display to
//         * the requested blink rate
//         * LEDBlinkRate enum
//         *
//         * @param ledBlinkRate the requested blink rate
//         * @return command bits for setting the LED Display to the given blink rate
//         */
//        private int getDisplaySetupCommandInt(LEDBlinkRate ledBlinkRate) {
//            return LEDBlinkMap.get(ledBlinkRate).byteVal;
//        }
//    }


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

    //
    // controller status
    //

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

    private LEDSwitch ledSwitch = LEDSwitch.OFF;

    public LEDSwitch getLedSwitch() {
        return ledSwitch;
    }

    private LEDBlinkRate ledBlinkRate = LEDBlinkRate.NO_BLINK;

    //
    // BLINK RATE
    //

    public LEDBlinkRate getLedBlinkRate() {
        return ledBlinkRate;
    }

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

    public String getDisplayString() {
        return displayString;
    }

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
     * Inserts a character into the display string at the position specified.
     *
     * @param displayCharacter character to insert
     * @param displayPosition  position to insert the character
     */
    public void setDisplayString(char displayCharacter, DisplayPosition displayPosition) {
        // Note that a char cannot be empty so I don't check for that
        StringBuilder builder = new StringBuilder(this.displayString);
        switch (displayPosition) {
            case LEFT:
                builder.setCharAt(3, displayCharacter);
                break;
            case MIDDLE_LEFT:
                builder.setCharAt(2, displayCharacter);
                break;
            case MIDDLE_RIGHT:
                builder.setCharAt(1, displayCharacter);
                break;
            case RIGHT:
                builder.setCharAt(0, displayCharacter);
                break;
        }
        setDisplayString(builder.toString());
    }

    //
    // I2C ADDRESS
    //

    private I2cAddr i2cAddr;

    public I2cAddr getI2cAddr() {
        return i2cAddr;
    }

    public void setI2cAddr(I2cAddr i2cAddr) {
        this.i2cAddr = i2cAddr;
    }

    public void setI2cAddr(int i2cAddr) {
        this.i2cAddr = I2cAddr.create7bit(i2cAddr);
    }

    //
    // I2C DEVICE
    //

    private I2cDevice backpack;

    private I2cDeviceSynch backpackClient;

    boolean isOwned = false;

    LEDCode ledCode;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public AdafruitBackpackLED(I2cAddr ledControllerAddress, HardwareMap hardwareMap, String backpackName) {
        setI2cAddr(ledControllerAddress);
        initialize(hardwareMap, backpackName);
    }

    public AdafruitBackpackLED(int ledControllerAddress, HardwareMap hardwareMap, String backpackName) {
        setI2cAddr(ledControllerAddress);
        initialize(hardwareMap, backpackName);
    }

    public AdafruitBackpackLED(HardwareMap hardwareMap, String backpackName) {
        // default address
        setI2cAddr(0x70);
        initialize(hardwareMap, backpackName);
    }

    //*********************************************************************************************
    //          helper methods
    //
    //*********************************************************************************************

    private void initialize(HardwareMap hardwareMap, String backpackName) {
        // create the client to talk over I2C to the controller chip
        createClient(hardwareMap, backpackName);
        // create the LED code mapping object
        this.ledCode = new LEDCode();
        // turn the system oscillator on
        turnOscillatorOn();
        // check to make sure there is an led controller at this address
        // turn the display off
        turnLEDsOff();
        // turn the blinking off;
        setLedBlinkRate(LEDBlinkRate.NO_BLINK);
        // clear the characters from the display
        setDisplayString("    ");
        // set the brightness
        setBrightnessLevel(15);
    }

    private void createClient(HardwareMap hardwareMap, String backpackName) {
        backpack = hardwareMap.get(I2cDevice.class, backpackName);
        backpackClient = new I2cDeviceSynchImpl(backpack, i2cAddr, isOwned);
        backpackClient.engage();
    }

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

    private void turnOscillatorOn() {
        byte byteToWrite = (byte) (Register.SYSTEM_SETUP.byteVal | SystemSetupCommand.SYSTEM_SETUP_OSCILLATOR_ON.byteVal);
        writeSingleByte(byteToWrite);
    }

    private void turnOscillatorOff() {
        // for the controller chip, the data to write is a single byte containing both the address and command
        byte byteToWrite = (byte) (Register.SYSTEM_SETUP.byteVal | SystemSetupCommand.SYSTEM_SETUP_OSCILLATOR_OFF.byteVal);
        writeSingleByte(byteToWrite);
    }

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

    private void writeBlinkRate(DisplaySetupCommand blinkCommand) {
        byte byteToWrite;
        // for the controller chip, the data to write is a single byte containing both the address and command
        // I want to keep the current LED display on or off so I have to create a command that does not change that
        if (getLedSwitch() == LEDSwitch.OFF) {
            byteToWrite = (byte) (Register.DISPLAY_SETUP.byteVal | blinkCommand.byteVal | DisplaySetupCommand.DISPLAY_OFF.byteVal);
        } else {
            byteToWrite = (byte) (Register.DISPLAY_SETUP.byteVal | blinkCommand.byteVal | DisplaySetupCommand.DISPLAY_ON.byteVal);
        }
        // write the address/command via I2C
        writeSingleByte(byteToWrite);
    }

//    private void setBrightnessLevel(int brightnessLevel) {
//        byte byteToWrite;
//        // create the single byte address/command
//        byteToWrite = (byte) (Register.DIMMING_SET.byteVal | brightnessLevel);
//        // write the address/command via I2C
//        writeSingleByte(byteToWrite);
//    }

    private void displayLEDString(String stringToDisplay) {
        byte[] displayCodeBuffer;
        // get an array of LED codes for the string in this.displayString. Codes are bytes
        displayCodeBuffer = this.ledCode.getLEDCodesAsBytes(stringToDisplay);
        // write the codes
        write(Register.DISPLAY_DATA.byteVal, displayCodeBuffer);
    }

    //*********************************************************************************************
    //          I2C low level read and write methods
    //*********************************************************************************************

    private synchronized byte read8(int register) {
        return backpackClient.read8(register);
    }

    private synchronized byte[] read(int register, int numberReadLocations) {
        return backpackClient.read(register, numberReadLocations);
    }

    /**
     * The SDK does not have a I2C method to write a single byte address/command. So I'm going to
     * cob it by writing the byte twice. I think it should work.
     *
     * @param addressCommandData
     */
    private synchronized void writeSingleByte(byte addressCommandData) {
        backpackClient.write8(addressCommandData, addressCommandData, I2cWaitControl.WRITTEN);
    }

    private synchronized void write8(byte register, int data) {
        backpackClient.write8(register, data, I2cWaitControl.WRITTEN);
    }

    private synchronized void write(byte register, byte[] data) {
        backpackClient.write(register, data, I2cWaitControl.WRITTEN);
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
    public void testBlinkingSetup() {
        // 5 seconds between changes in blink rate
        timeInterval = 5000;
        // set initial state
        state = LEDBlinkRate.NO_BLINK;
        // create the timer and set it to 0
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        // display a string
        setDisplayString("NoBl");
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
    public boolean testBlinkingUpdate() {
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
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        // display a string
        setDisplayString("15");
        // no blinking
        setLedBlinkRate(LEDBlinkRate.NO_BLINK);
        setBrightnessLevel(15);
        testComplete = false;
    }

    /**
     * Brightness level test / demo.
     * Call the setup method above one time. Then call this update method in a loop in the opmode.
     *
     * @return true when all phases of the test are complete
     */
    public boolean brightnessLevelTestUpdate() {
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
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        // set the initial state
        characterDisplayState = CharacterDisplayStates.START;
        // display nothing
        setDisplayString("    ");
        // no blinking
        setLedBlinkRate(LEDBlinkRate.NO_BLINK);
        setBrightnessLevel(15);
        // get the list of characters and an iterator to move across the characters in the set
        characters = ledCode.getCharacterSet();
        iterator = characters.iterator();
        testComplete = false;
    }

    /**
     * Display each character on the display, wait a bit, then display the next one.
     * Make sure to call the setup above first. Then call this method in a loop to display each
     * character. Once it returns true you can stop your loop. (It is a state machine.)
     *
     * @return true if all characters have been displayed
     */
    public boolean updateCharacterTest() {
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
                    setDisplayString(new StringBuilder().append(characterToDisplay).append(characterToDisplay).append(characterToDisplay).append(characterToDisplay).toString());
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
}
