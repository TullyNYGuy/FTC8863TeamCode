package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * This class lets you use a Sharp Analog IR distance sensor.
 * @see <a href="https://www.adafruit.com/product/164">Sharp IR Distance Sensor</a>
 * This sensor shoots out a beam of IR light and looks for the light reflected back to it. It
 * outputs a voltage that corresponds to a distance. The voltage vs distance curve can be found here:
 * @see <a href="http://www.sharp-world.com/products/device/lineup/data/pdf/datasheet/gp2y0a21yk_e.pdf">
 *     datasheet</a>
 * The curve is not linear. We tested 2 sensors and then used a curve fit to create an equation
 * representing the distance vs voltage function.
 * The sensor should be connected to an analog input port on the Modern Robotics Core Device
 * Interface module.
 */
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
    /**
     * The sensor is on an analog input port on the core device interface module
     */
    private AnalogInput sharpDistanceSensor;

    /**
     * The voltage read from the port
     */
    private double voltageReading = 0;

    /**
     * The max voltage possible from the sensor. Used to verify we have a valid reading.
     */
    private double maxVoltagePossible = 3.1;

    // The min voltage possible from the sensor. Used to verify we have a valid reading.
    private double minVoltagePossible = .5;

    /**
     * The distance corresponding to the voltage reading
     */
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

    /**
     * Constructor
     * @param sensorName the string in the robot config for this sensor
     * @param hardwareMap the hardware map for the system
     */
    public SharpDistanceSensor(String sensorName, HardwareMap hardwareMap) {
        sharpDistanceSensor = hardwareMap.analogInput.get(sensorName);
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Verify that the voltage read is within a valid range
     * @param voltageReading voltage read from the port
     * @return valid = true
     */
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

    /**
     * Read the voltage, verify it, and then calculate the distance it represents.
     * @return distance in CM
     */
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
