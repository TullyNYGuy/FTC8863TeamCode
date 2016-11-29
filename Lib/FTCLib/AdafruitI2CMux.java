package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.I2cAddr;

public class AdafruitI2CMux {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    /**
     * Each bit in the control register enables one port on the mux. There can be one or more than
     * one port enabled at a time. This enum sets up a port with its corresponding control bit.
     */
    public enum PortNumber {
        NOPORT (0x00),
        PORT0 (0x01),
        PORT1 (0x02),
        PORT2 (0x04),
        PORT3 (0x08),
        PORT4 (0x10),
        PORT5 (0x20),
        PORT6 (0x40),
        PORT7 (0x80);

        private final byte bVal;

        PortNumber(int i) {
            this.bVal = (byte) i;
        }
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    // The I2C address for the mux is default 0x70. By pulling up the input pins A0, A1, and A2 on the
    // header of the board, you can change the address. Pulling up the pin can be accomplished by
    // connecting the header pin to 5V through a 1K resistor. The address can range from:
    // A2  A1  A0  Address
    //  L   L   L   0x70 (default)
    //  L   L   H   0x71
    //  L   H   L   0x72
    //  L   H   H   0x73
    //  H   L   L   0x74
    //  H   L   H   0x75
    //  H   H   L   0x76
    //  H   H   H   0x77
    private I2cAddr muxAddress;

    private byte controlByte = 0x00;

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
