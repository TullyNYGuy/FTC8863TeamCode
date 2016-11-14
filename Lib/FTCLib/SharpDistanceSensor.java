package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SharpDistanceSensor {

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
    private AnalogInput sharpDistanceSensor;
    private double voltageReading = 0;
    private double maxVoltagePossible = 3.1;
    private double minVoltagePossible = .5;
    private double distance = 0;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields
    //
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public SharpDistanceSensor(String sensorName, HardwareMap hardwareMap) {
        sharpDistanceSensor = hardwareMap.analogInput.get(sensorName);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private boolean verifyVoltageReading (double voltageReading) {
        if (voltageReading <= maxVoltagePossible && voltageReading >= minVoltagePossible) {
            return true;
        } else {
            return false;
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public double getDistance() {
        // get the voltage reading
        voltageReading = sharpDistanceSensor.getVoltage();
        // verify it is valid
        if (verifyVoltageReading(voltageReading)) {
            // calculate the distance, the equation is a curve fit that still needs to be determined
            this.distance = voltageReading;
        } else {
            this.distance = 999.0;
        }
        return this.distance;
    }
}
