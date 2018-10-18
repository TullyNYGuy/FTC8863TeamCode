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

    public enum Character {
        UNUSED1(0b0000000000000001),
        UNUSED2(0b0000000000000010),
        UNUSED3(0b0000000000000100),
        UNUSED4(0b0000000000001000),
        UNUSED5(0b0000000000010000),
        UNUSED6(0b0000000000100000),
        UNUSED7(0b0000000001000000),
        UNUSED8(0b0000000010000000),
        UNUSED9(0b0000000100000000),
        UNUSED10(0b0000001000000000),
        UNUSED11(0b0000010000000000),
        UNUSED12(0b0000100000000000),
        UNUSED13(0b0001000000000000),
        UNUSED14(0b0010000000000000),
        UNUSED15(0b0100000000000000),
        UNUSED16(0b1000000000000000),
        UNUSED17(0b0001001011001001),
        UNUSED18(0b0001010111000000),
        UNUSED19(0b0001001011111001),
        UNUSED20(0b0000000011100011),
        UNUSED21(0b0000010100110000),
        UNUSED22(0b0001001011001000),
        UNUSED23(0b0011101000000000),
        UNUSED24(0b0001011100000000),
        BLANK(0b0000000000000000),                  //
        EXCLAMATION(0b0000000000000110),            // !
        DOUBLE_QUOTE(0b0000001000100000),           // "
        NUMBER(0b0001001011001110),                 // #
        DOLLAR(0b0001001011101101),                 // $
        PERCENT(0b0000110000100100),                // %
        AMPERSAND(0b0010001101011101),              // &
        SINGLE_QUOTE(0b0000010000000000),           // '
        OPEN_PAREN(0b0010010000000000),             // (
        CLOSE_PAREN(0b0000100100000000),            // )
        ASTERISK(0b0011111111000000),               // *
        PLUS(0b0001001011000000),                   // +
        COMMA(0b0000100000000000),                  // ),
        MINUS(0b0000000011000000),                  // -
        PERIOD(0b0000000000000000),                 // .
        FORWARD_SLASH(0b0000110000000000),          // /
        ZERO(0b0000110000111111),                   // 0
        ONE(0b0000000000000110),                    // 1
        TWO(0b0000000011011011),                    // 2
        THREE(0b0000000010001111),                  // 3
        FOUR(0b0000000011100110),                   // 4
        FIVE(0b0010000001101001),                   // 5
        SIX(0b0000000011111101),                    // 6
        SEVEN(0b0000000000000111),                  // 7
        EIGHT(0b0000000011111111),                  // 8
        NINE(0b0000000011101111),                   // 9
        COLON(0b0001001000000000),                  // :
        SEMICOLON(0b0000101000000000),              // ;
        LESS_THAN(0b0010010000000000),              // <
        EQUALS(0b0000000011001000),                 // =
        GREATER_THAN(0b0000100100000000),           // >
        QUESTION(0b0001000010000011),               // ?
        AT(0b0000001010111011),                     // @
        A_UPPERCASE(0b0000000011110111),            // A
        B_UPPERCASE(0b0001001010001111),            // B
        C_UPPERCASE(0b0000000000111001),            // C
        D_UPPERCASE(0b0001001000001111),            // D
        E_UPPERCASE(0b0000000011111001),            // E
        F_UPPERCASE(0b0000000001110001),            // F
        G_UPPERCASE(0b0000000010111101),            // G
        H_UPPERCASE(0b0000000011110110),            // H
        I_UPPERCASE(0b0001001000000000),            // I
        J_UPPERCASE(0b0000000000011110),            // J
        K_UPPERCASE(0b0010010001110000),            // K
        L_UPPERCASE(0b0000000000111000),            // L
        M_UPPERCASE(0b0000010100110110),            // M
        N_UPPERCASE(0b0010000100110110),            // N
        O_UPPERCASE(0b0000000000111111),            // O
        P_UPPERCASE(0b0000000011110011),            // P
        Q_UPPERCASE(0b0010000000111111),            // Q
        R_UPPERCASE(0b0010000011110011),            // R
        S_UPPERCASE(0b0000000011101101),            // S
        T_UPPERCASE(0b0001001000000001),            // T
        U_UPPERCASE(0b0000000000111110),            // U
        V_UPPERCASE(0b0000110000110000),            // V
        W_UPPERCASE(0b0010100000110110),            // W
        X_UPPERCASE(0b0010110100000000),            // X
        Y_UPPERCASE(0b0001010100000000),            // Y
        Z_UPPERCASE(0b0000110000001001),            // Z
        OPEN_SQUARE_BRACKET(0b0000000000111001),    // [
        UNUSED25(0b0010000100000000),               //
        CLOSE_SQUARE_BRACKET(0b0000000000001111),   // ]
        CARROT(0b0000110000000011),                 // ^
        UNDERSCORE(0b0000000000001000),             // _
        REVERSE_SINGLE_QUOTE(0b0000000100000000),   // `
        A_LOWERCASE(0b0001000001011000),            // a
        B_LOWERCASE(0b0010000001111000),            // b
        C_LOWERCASE(0b0000000011011000),            // c
        D_LOWERCASE(0b0000100010001110),            // d
        E_LOWERCASE(0b0000100001011000),            // e
        F_LOWERCASE(0b0000000001110001),            // f
        G_LOWERCASE(0b0000010010001110),            // g
        H_LOWERCASE(0b0001000001110000),            // h
        I_LOWERCASE(0b0001000000000000),            // i
        J_LOWERCASE(0b0000000000001110),            // j
        K_LOWERCASE(0b0011011000000000),            // k
        L_LOWERCASE(0b0000000000110000),            // l
        M_LOWERCASE(0b0001000011010100),            // m
        N_LOWERCASE(0b0001000001010000),            // n
        O_LOWERCASE(0b0000000011011100),            // o
        P_LOWERCASE(0b0000000101110000),            // p
        Q_LOWERCASE(0b0000010010000110),            // q
        R_LOWERCASE(0b0000000001010000),            // r
        S_LOWERCASE(0b0010000010001000),            // s
        T_LOWERCASE(0b0000000001111000),            // t
        U_LOWERCASE(0b0000000000011100),            // u
        V_LOWERCASE(0b0010000000000100),            // v
        W_LOWERCASE(0b0010100000010100),            // w
        X_LOWERCASE(0b0010100011000000),            // x
        Y_LOWERCASE(0b0010000000001100),            // y
        Z_LOWERCASE(0b0000100001001000),            // z
        OPEN_SQUIRRLY_BRACKET(0b0000100101001001),  // {
        VERTICAL_BAR(0b0001001000000000),           // |
        CLOSE_SQUIRRLY_BRACKET(0b0010010010001001), // }
        TILDE(0b0000010100100000),                  // ~
        UNUSED26(0b0011111111111111);
        
        public final byte byteVal;

        Character(int i) {
            this.byteVal = (byte) i;
        }


    }

}
