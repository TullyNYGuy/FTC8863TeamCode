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
*/

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import com.qualcomm.robotcore.hardware.I2cWaitControl;

/**
 * This class provides an interface to the Adafruit TCA9548A I2C 1 to 8 mux. The input I2C bus can
 * be connected to one or more of the 8 ports. Most often, this will be used if you have more than
 * one I2C device with the same address. Wire the devices to the pins on the board. Then use the mux
 * to switch between the devices and talk to only one of them at a time (see selectAndEnableAPort())
 * This mux is very simple since it has only one register. So it makes a good learning tool to understand
 * how to read and write an I2C device. The default address for the mux is 0x70.
 *
 * https://learn.adafruit.com/adafruit-tca9548a-1-to-8-i2c-multiplexer-breakout/wiring-and-test?view=all
 *
 * You will need to configure your phone with a "I2C Device" on one of the I2C ports.
 * Give the device a name when you configure it on the phone. This will be the muxName you pass
 * into the constructor.
 *
 * WARNING: if you are using this mux to read multiple adafruit color sensors then be aware of a
 * possible issue. When you switch from one color sensor on one port to another color sensor on
 * another port, there will be a period of time needed before the data will be valid for the new
 * sensor. This is because it takes time to get data from the new color sensor and populate the
 * data in the data cache. Reports on the FTC forum indicate that it takes from 50 mSec to 150 mSec
 * to get valid data. If you read before then, you will likely get the data from the first color
 * sensor not the new one. In order to avoid this switch the mux port well before you need valid
 * data or put a delay in your code after switching mux ports and before reading color values.
 */
public class AdafruitI2CMux {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    /**
     * Create a symbolic name for each bit on the control register.
     * Each bit in the control register enables one port on the mux. There can be one or more than
     * one port enabled at a time. In most cases you will only want one port enabled.
     */
    public enum PortNumber {
        NOPORT (0x00), // inicates that no port have been selected
        PORT0 (0x01),
        PORT1 (0x02),
        PORT2 (0x04),
        PORT3 (0x08),
        PORT4 (0x10),
        PORT5 (0x20),
        PORT6 (0x40),
        PORT7 (0x80),
        MANYPORT (0xFF); // just a flag that more than 1 port has been selected

        private final byte bVal;

        PortNumber(int i) {
            this.bVal = (byte) i;
        }
    }

    /**
     * This device only has one register. So this enum is totally not needed. But I'm doing it anyway
     * to demonstrate how a more complex device with more registers could be implemented.
     */
    private enum Register
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

    /**
     * The I2C address for the mux is by default 0x70. By pulling up the input pins A0, A1, and
     * A2 on the header of the board, you can change the address. Pulling up the pin can be
     * accomplished by wiring the header pin to 5V through a 1K or 10K resistor. The address
     * can range from:
     * A2  A1  A0  Address
     *  L   L   L   0x70 (default)
     *  L   L   H   0x71
     *  L   H   L   0x72
     *  L   H   H   0x73
     *  H   L   L   0x74
     *  H   L   H   0x75
     *  H   H   L   0x76
     *  H   H   H   0x77
     */
    private I2cAddr muxAddress;

    /**
     * The byte used to control the mux. This gets written to the control register.
     */
    private byte controlByte = 0x00;

    /**
     * In an effort to minimize traffic on the I2C bus, I keep track of the last byte written and
     * only write commands that change from the last command. This also can be read instead of
     * reading the actiual device and creating more bus traffic.
     */
    private byte lastControlByte = 0x00;

    /**
     * The mux is an I2cDevice
     */
    private I2cDevice mux;

    /**
     * The client performs the reads and writes to the I2cDevice.
     */
    private I2cDeviceSynch muxClient;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields
    //*********************************************************************************************

    /**
     * Allow the user of the class to get read only access to the last control byte written.
     * @return last control byte written to the mux control register
     */
    public byte getLastControlByte() {
        return lastControlByte;
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public AdafruitI2CMux(HardwareMap hardwareMap, String muxName, byte muxAddress) {
        this.muxAddress = new I2cAddr(muxAddress);
        // no ports are enabled to start (all 0s). The chip should come up in this state but just to
        // be sure.
        controlByte = 0x00;
        lastControlByte = 0x00;
        // Get the mux from the hardware map
        mux = hardwareMap.i2cDevice.get(muxName);
        // Create a client to read and write to the mux at the address of the mux
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

    /**
     * Write the control byte to the mux register
     * @param controlByte the value to write into the control register
     */
    private void writeMux(byte controlByte) {
        // check to see if the command has actually changed. If not there is no need to write it.
        if (controlByte == lastControlByte) {
            // It is the same. We don't need to do anything.
            return;
        }
        // The command has changed since last command. Save the new controlByte for use later.
        lastControlByte = controlByte;
        // declare a variable for readability's sake, otherwise it is hard for a newbie to tell
        // what true means in the write8 call
        // FTC SDK 3.4 added enums for wait control instead of a boolean
        //boolean WAIT_FOR_COMPLETION = true;
        muxClient.write8(Register.CONTROL.byteVal, controlByte, I2cWaitControl.WRITTEN);
    }

    /**
     * Read what was last written to the control register. This does not read the control register
     * itself. It returns the value that was written from a property in this class
     * @return what was written to the control register last
     */
    private byte readMux() {
        // If I was going to read from the mux this is how.
        //return muxClient.read8(Register.CONTROL.byteVal);
        // However, the contents of the control register were saved in a property. Use that instead
        // of creating traffic on the bus to actually read the register on the device.
        // Also, this works around the problem I have where reading the register turns off all of
        // the ports.
        return lastControlByte;
    }

    // ARGH! For some reason reading from the device's register is forcing the mux to disable
    // all ports. No clue why!
//    private byte readControlRegister() {
//        return muxClient.read8(Register.CONTROL.byteVal);
//    }


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * If you want to turn on several ports, use a series of these calls to select the ports. One
     * call for each port to be turned on. Follow the last call with enablePorts() to actually turn
     * the ports on. Note that turning on more than one port at a time is unusual. If you want to
     * turn on only one port use selectAndEnableAPort().
     * @param portNumber the port number to be connected.
     */
    public void selectPort(PortNumber portNumber) {
        // Bitwise or the value associated with the port to the ports that have already been
        // selected. Note that a byte | byte yields an int so I have to cast the result back
        // to a byte
        controlByte = (byte)(portNumber.bVal | controlByte);
    }

    /**
     * Disconnect all ports from the input side of the I2C bus.
     */
    public void disablePorts() {
        controlByte = PortNumber.NOPORT.bVal;
        writeMux(controlByte);
    }

    /**
     * Write the control byte to the control register. Any bit that is a 1 will turn on the port
     * that corresponds to it. Data will then pass back and forth from input side to the enabled
     * ports. See selectPorts() to set which ports get turned on. Call selectPorts() before calling
     * enablePorts()
     */
    public void enablePorts() {
        writeMux(controlByte);
    }

    /**
     * Most often the user will want to connect one, and only one, port to the input I2C bus. This
     * method is a convenient "one call does it all" way to do that.
     * @param portNumber The port to connect.
     */
    public void selectAndEnableAPort(PortNumber portNumber) {
        controlByte = portNumber.bVal;
        enablePorts();
    }

    /**
     * Get the active ports from the mux.
     * @return active port in the form of PortNumber enum
     */
    public PortNumber getActivePort() {
        PortNumber result;
        // because byte is treated as a signed number in Java, I have to cast the data read from the
        // mux to int in order to compare to 0x80.
        switch ((int)readMux()) {
            case 0x00:
                result = PortNumber.NOPORT;
            break;
            case 0x01:
                result = PortNumber.PORT0;
            break;
            case 0x02:
                result = PortNumber.PORT1;
            break;
            case 0x04:
                result = PortNumber.PORT2;
            break;
            case 0x08:
                result = PortNumber.PORT3;
            break;
            case 0x10:
                result = PortNumber.PORT4;
            break;
            case 0x20:
                result = PortNumber.PORT5;
            break;
            case 0x40:
                result = PortNumber.PORT6;
            break;
            case 0x80:
                result = PortNumber.PORT7;
            break;
            default:
                result = PortNumber.MANYPORT;
                break;
        }
        return result;
    }

    /**
     * Get the active port(s) from the mux.
     * @return active port in the form of a string
     */
    public String getActivePortAsString() {
        return getActivePort().toString();
    }
}
