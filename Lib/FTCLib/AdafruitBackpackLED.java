package org.firstinspires.ftc.teamcode.Lib.FTCLib;

// The implementation of the LED circuit to the Adafruit Backpack driver is specific to the
// printed circuit board that Adafruit sells. So the control is specific to this board can't be
// used with any other board that uses the HT16K33 LED Controller.

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

    // The HT16K33 is a bit odd to me. The register address and the command data that go into the
    // register are combined into one 8 bit byte. The most significant 4 bits are the address of the
    // register and the commands are contained in the least significant 4 bits.

    // Here are the register addresses. The command bits are in enums that follow this. To get a
    // full 8 bit byte to write to the chip, OR the register address and the command bits together.
    // This is from the chip datasheet, pages 30 and 31.
    /**
     * REGISTER provides symbolic names for device registers
     */
    public enum Register {
        DISPLAY_DATA(0x00),
        SYSTEM_SETUP(0x20),
        KEY_DATA(0x40), // the chip can read a matrix of keys, but this is not implemented on the Adafruit board.
                        // So this register address is here just for completeness. It is never used.
        INT_FLAG(0x60), // the chip can generate an interrupt, but this is not implemented on the Adafruit board.
                        // So this register address is here just for completeness. It is never used.
        DISPLAY_SETUP(0x80),
        ROW_INT_SET(0xA0),

        DIMMING_SET(0xE0),
        TEST_MODE(0xD9); // This register is for HOLTEK (the chip manufacturer) use only. It is
                         // documented here but it is never used.

        public final byte byteVal;

        Register(int i) {
            this.byteVal = (byte) i;
        }
    }

    // Commands for the Display data register
    // The display data commands are full 8 bit commands. They are actually bits that turn on
    // specific LED segments to form a character such as a "5" or "A".

    public enum DisplayDataCommand {
        SYSTEM_SETUP_OSCILLATOR_OFF(0x00),         // bit 8 = 0 TO TURN THE OSCILLATOR OFF
        SYSTEM_SETUP_OSCILLATOR_OFn(0x01);         // bit 8 = 1 TO TURN THE OSCILLATOR ON

        public final byte byteVal;

        DisplayDataCommand(int i) {
            this.byteVal = (byte) i;
        }
    }

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

    // Commands for the Dimming setup register (page 15 of the datasheet)

    // There are no commands for the test mode register.
}
