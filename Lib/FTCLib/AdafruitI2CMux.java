package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;

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

    /**
     * This device only has one register. So this is totally not needed. But I'm doing it anyway
     * to demonstrate how a more complex device with more registers could be implemented.
     * A list of the registers available in the device.
     */
    enum Register
    {
        CONTROL(0x00);

        public final byte byteVal;
        Register(int i) { this.byteVal = (byte) i; }
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

    private I2cDevice mux;
    private I2cDeviceSynch muxClient;

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

    public AdafruitI2CMux(HardwareMap hardwareMap, String muxName, byte muxAddress) {
        this.muxAddress = new I2cAddr(muxAddress);
        // no ports are enabled to start (all 0s)
        controlByte = 0x00;
        mux = hardwareMap.i2cDevice.get(muxName);
        muxClient = new I2cDeviceSynchImpl(mux, this.muxAddress, false);
        muxClient.engage();
        // turn all mux channels off
        writeMux(controlByte);
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private void writeMux(byte controlByte) {
        // declare a variable for readability's sake, otherwise it is hard for a newbie to tell
        // what true means in the write8 call
        boolean waitForCompletion = true;
        muxClient.write8(Register.CONTROL.byteVal, controlByte, waitForCompletion);
    }

    private byte readMux() {
        return muxClient.read8(Register.CONTROL.byteVal);
    }


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void selectPort(PortNumber portNumber) {
        controlByte = portNumber.bVal | controlByte;
    }

    public void disablePorts() {
        controlByte = PortNumber.NOPORT.bVal;
        writeMux(controlByte);
    }

    public void enablePorts() {
        writeMux(controlByte);
    }

    public void selectAndEnableAPort(PortNumber portNumber) {
        controlByte = portNumber.bVal;
        enablePorts();
    }
}
