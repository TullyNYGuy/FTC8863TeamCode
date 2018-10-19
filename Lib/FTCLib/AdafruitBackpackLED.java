package org.firstinspires.ftc.teamcode.Lib.FTCLib;

// The implementation of the LED circuit to the Adafruit Backpack driver is specific to the
// printed circuit board that Adafruit sells. So the control is specific to this board can't be
// used with any other board that uses the HT16K33 LED Controller.

import com.qualcomm.robotcore.hardware.LED;

import java.util.HashMap;
import java.util.Map;

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
    public enum Register {
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

    public enum SystemSetupCommand {
        SYSTEM_SETUP_OSCILLATOR_OFF(0x00),         // bit 8 = 0 TO TURN THE OSCILLATOR OFF
        SYSTEM_SETUP_OSCILLATOR_OFn(0x01);         // bit 8 = 1 TO TURN THE OSCILLATOR ON

        public final byte byteVal;

        SystemSetupCommand(int i) {
            this.byteVal = (byte) i;
        }
    }

    // I'm skipping the key data commands since they are never used.

    // I'm skipping the int commands since they are never used.

    // Commands for the Display setup register (page 11 of the datasheet)
    // NOTE that you have to OR (|) the display off/on bit with the Blinking bits to get a full
    // command to setup blinking.
    // example DisplaySetupCommand.BLINKING_1_HZ | DisplaySetupCommand.DISPLAY_ON to turn on the
    // the display and set it to blink once per second.
    // If you forget to do the OR, and send DisplaySetupCommand.BLINKING_1_HZ, the display on/off
    // bit is 0 and you turn the display off and set it to blink once per second. This is not very
    // useful :-)

    public enum DisplaySetupCommand {
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
    // These ccmmands handled by a method not an enum.

    // There are no commands for the test mode register.

    // Here are the codes for displaying a character on the LED. These codes are specific to the
    // Adafruit board. The code is the command to write after the display data register address.
    // I'm using a class to create and populate a lookup table. You pass in the character to
    // setStringToDisplay and then get the 2 bytes to write to the register that form the character on
    // the LED from getLEDCodeMostSignificantByte and getLEDCodeLeastSignificantByte.

    private class LEDCode {

        private String stringToDisplay;

        public void setStringToDisplay(String stringToDisplay) {
            this.stringToDisplay = stringToDisplay;
            byte[] LEDCodeAsBytes = getBytesForLEDCode(getLEDCode(stringToDisplay));
            this.LEDCodeLeastSignificantByte = LEDCodeAsBytes[1];
            this.LEDCodeMostSignificantByte = LEDCodeAsBytes[0];
        }

        private byte LEDCodeMostSignificantByte;

        public byte getLEDCodeMostSignificantByte() {
            return LEDCodeMostSignificantByte;
        }

        private byte LEDCodeLeastSignificantByte;

        public byte getLEDCodeLeastSignificantByte() {
            return LEDCodeLeastSignificantByte;
        }

        // define a lookup table to map a string to an LED code that will display the character on
        // the display
        private Map<String, Short> stringToLEDCode;

        // constructor
        // populate the lookup table - this comes from the Adafruit C++ library code located at
        // https://github.com/adafruit/Adafruit_LED_Backpack/blob/master/Adafruit_LEDBackpack.cpp

        private LEDCode() {
            // populate the lookup table that maps a string to an LED Code
            stringToLEDCode.put(" ", (short) 0b0000000000000000);            // (blank)
            stringToLEDCode.put("!", (short) 0b0000000000000110);            // !
            stringToLEDCode.put("\"", (short) 0b0000001000100000);           // \
            stringToLEDCode.put("#", (short) 0b0001001011001110);            // #
            stringToLEDCode.put("$", (short) 0b0001001011101101);            // $
            stringToLEDCode.put("%", (short) 0b0000110000100100);            // %
            stringToLEDCode.put("&", (short) 0b0010001101011101);            // &
            stringToLEDCode.put("'", (short) 0b0000010000000000);            // '
            stringToLEDCode.put("(", (short) 0b0010010000000000);            // (
            stringToLEDCode.put(")", (short) 0b0000100100000000);            // )
            stringToLEDCode.put("*", (short) 0b0011111111000000);            // *
            stringToLEDCode.put("+", (short) 0b0001001011000000);            // +
            stringToLEDCode.put(");", (short) 0b0000100000000000);           // );
            stringToLEDCode.put("-", (short) 0b0000000011000000);            // -
            stringToLEDCode.put(".", (short) 0b0000000000000000);            // .
            stringToLEDCode.put("/", (short) 0b0000110000000000);            // /
            stringToLEDCode.put("0", (short) 0b0000110000111111);            // 0
            stringToLEDCode.put("1", (short) 0b0000000000000110);            // 1
            stringToLEDCode.put("2", (short) 0b0000000011011011);            // 2
            stringToLEDCode.put("3", (short) 0b0000000010001111);            // 3
            stringToLEDCode.put("4", (short) 0b0000000011100110);            // 4
            stringToLEDCode.put("5", (short) 0b0010000001101001);            // 5
            stringToLEDCode.put("6", (short) 0b0000000011111101);            // 6
            stringToLEDCode.put("7", (short) 0b0000000000000111);            // 7
            stringToLEDCode.put("8", (short) 0b0000000011111111);            // 8
            stringToLEDCode.put("9", (short) 0b0000000011101111);            // 9
            stringToLEDCode.put(":", (short) 0b0001001000000000);            // :
            stringToLEDCode.put(";", (short) 0b0000101000000000);            // ;
            stringToLEDCode.put("<", (short) 0b0010010000000000);            // <
            stringToLEDCode.put("=", (short) 0b0000000011001000);            // =
            stringToLEDCode.put(">", (short) 0b0000100100000000);            // >
            stringToLEDCode.put("?", (short) 0b0001000010000011);            // ?
            stringToLEDCode.put("@", (short) 0b0000001010111011);            // @
            stringToLEDCode.put("A", (short) 0b0000000011110111);            // A
            stringToLEDCode.put("B", (short) 0b0001001010001111);            // B
            stringToLEDCode.put("C", (short) 0b0000000000111001);            // C
            stringToLEDCode.put("D", (short) 0b0001001000001111);            // D
            stringToLEDCode.put("E", (short) 0b0000000011111001);            // E
            stringToLEDCode.put("F", (short) 0b0000000001110001);            // F
            stringToLEDCode.put("G", (short) 0b0000000010111101);            // G
            stringToLEDCode.put("H", (short) 0b0000000011110110);            // H
            stringToLEDCode.put("I", (short) 0b0001001000000000);            // I
            stringToLEDCode.put("J", (short) 0b0000000000011110);            // J
            stringToLEDCode.put("K", (short) 0b0010010001110000);            // K
            stringToLEDCode.put("L", (short) 0b0000000000111000);            // L
            stringToLEDCode.put("M", (short) 0b0000010100110110);            // M
            stringToLEDCode.put("N", (short) 0b0010000100110110);            // N
            stringToLEDCode.put("O", (short) 0b0000000000111111);            // O
            stringToLEDCode.put("P", (short) 0b0000000011110011);            // P
            stringToLEDCode.put("Q", (short) 0b0010000000111111);            // Q
            stringToLEDCode.put("R", (short) 0b0010000011110011);            // R
            stringToLEDCode.put("S", (short) 0b0000000011101101);            // S
            stringToLEDCode.put("T", (short) 0b0001001000000001);            // T
            stringToLEDCode.put("U", (short) 0b0000000000111110);            // U
            stringToLEDCode.put("V", (short) 0b0000110000110000);            // V
            stringToLEDCode.put("W", (short) 0b0010100000110110);            // W
            stringToLEDCode.put("X", (short) 0b0010110100000000);            // X
            stringToLEDCode.put("Y", (short) 0b0001010100000000);            // Y
            stringToLEDCode.put("Z", (short) 0b0000110000001001);            // Z
            stringToLEDCode.put("[", (short) 0b0000000000111001);            // [
            stringToLEDCode.put("]", (short) 0b0000000000001111);            // ]
            stringToLEDCode.put("^", (short) 0b0000110000000011);            // ^
            stringToLEDCode.put("_", (short) 0b0000000000001000);            // _
            stringToLEDCode.put("`", (short) 0b0000000100000000);            // `
            stringToLEDCode.put("a", (short) 0b0001000001011000);            // a
            stringToLEDCode.put("b", (short) 0b0010000001111000);            // b
            stringToLEDCode.put("c", (short) 0b0000000011011000);            // c
            stringToLEDCode.put("d", (short) 0b0000100010001110);            // d
            stringToLEDCode.put("e", (short) 0b0000100001011000);            // e
            stringToLEDCode.put("f", (short) 0b0000000001110001);            // f
            stringToLEDCode.put("g", (short) 0b0000010010001110);            // g
            stringToLEDCode.put("h", (short) 0b0001000001110000);            // h
            stringToLEDCode.put("i", (short) 0b0001000000000000);            // i
            stringToLEDCode.put("j", (short) 0b0000000000001110);            // j
            stringToLEDCode.put("k", (short) 0b0011011000000000);            // k
            stringToLEDCode.put("l", (short) 0b0000000000110000);            // l
            stringToLEDCode.put("m", (short) 0b0001000011010100);            // m
            stringToLEDCode.put("n", (short) 0b0001000001010000);            // n
            stringToLEDCode.put("o", (short) 0b0000000011011100);            // o
            stringToLEDCode.put("p", (short) 0b0000000101110000);            // p
            stringToLEDCode.put("q", (short) 0b0000010010000110);            // q
            stringToLEDCode.put("r", (short) 0b0000000001010000);            // r
            stringToLEDCode.put("s", (short) 0b0010000010001000);            // s
            stringToLEDCode.put("t", (short) 0b0000000001111000);            // t
            stringToLEDCode.put("u", (short) 0b0000000000011100);            // u
            stringToLEDCode.put("v", (short) 0b0010000000000100);            // v
            stringToLEDCode.put("w", (short) 0b0010100000010100);            // w
            stringToLEDCode.put("x", (short) 0b0010100011000000);            // x
            stringToLEDCode.put("y", (short) 0b0010000000001100);            // y
            stringToLEDCode.put("z", (short) 0b0000100001001000);            // z
            stringToLEDCode.put("{", (short) 0b0000100101001001);            // {
            stringToLEDCode.put("!", (short) 0b0001001000000000);            // |
            stringToLEDCode.put("}", (short) 0b0010010010001001);            // }
            stringToLEDCode.put("~", (short) 0b0000010100100000);            // ~
        }

        private short getLEDCode(String string) {
            Short element = stringToLEDCode.get(string);
            if (element != null) {
                return element;
            } else {
                throw new IllegalArgumentException(String.format("Unknown LED code: %s in %s", string, LEDCode.class.toString()));
            }
        }

        private byte[] getBytesForLEDCode(short code) {
            byte[] returnValue = {0, 0};
            // 0th element are the msbs
            returnValue[0] = (byte) code; // casting to a byte chops off the 8 most significant bits
            // 1st element are the lsbs
            returnValue[1] = (byte) (code >> 8); // shift the 8 msbs into the 8 lsbs
            return returnValue;
        }
    }

}
