package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;

public class I2cDeviceCommunicator {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private I2cDevice i2cDevice;
    private I2cDeviceSynch i2cDeviceClient;
    boolean isOwned = false;

    /**
     * the address at which the sensor resides on the I2C bus.
     */
    private I2cAddr i2cAddr;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public I2cDevice getI2cDevice() {
        return i2cDevice;
    }

    public I2cDeviceSynch getI2cDeviceClient() {
        return i2cDeviceClient;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public I2cDeviceCommunicator(HardwareMap hardwareMap, String i2cDeviceName, I2cAddr i2cAddr) {
        this.i2cDevice = hardwareMap.get(I2cDevice.class, i2cDeviceName);
        this.i2cDeviceClient = new I2cDeviceSynchImpl(i2cDevice, i2cAddr, isOwned);
    }
}
