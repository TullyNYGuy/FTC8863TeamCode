package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.openftc.revextensions2.ExpansionHubEx;

import java.util.ArrayList;

/**
 * This class gets the expansion hub voltage and motor currents for the motor attached to the
 * expansion hub. You can display the info on the driver's station, write the info into a comma
 * separated value file, or both. It only handles 1 expansion hub. Create 2 objects from this class
 * if you want to monitor both expansion hubs.
 */
public class MotorCurrentVoltageMonitor {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    /**
     * Where the current and voltage data get output to.
     */
    public enum OutputTo {
        WRITE_CSV_FILE,
        DISPLAY,
        WRITE_CSV_FILE_AND_DISPLAY
    }

    private OutputTo mode;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    /**
     * Holds the list of motors that you want to get currents. Note that the max number of motors
     * is 4 since that is how many motor ports are on an expansion hub.
     */
    private ArrayList<DcMotor8863> motorList;

    /**
     * The expansion hub object
     */
    private ExpansionHubEx expansionHub;

    /**
     * A data file that the current and voltage can be written into.
     */
    private CSVDataFile dataFile;

    private Telemetry telemetry;

    /**
     * An array to store the motor currents.
     */
    private double[] motorCurrents;
    private double supplyVoltage;

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

    public MotorCurrentVoltageMonitor(HardwareMap hardwareMap, Telemetry telemetry, String expansionHubName, OutputTo mode) {
        expansionHub = hardwareMap.get(ExpansionHubEx.class, expansionHubName);
        this.telemetry = telemetry;
        motorList = new ArrayList<DcMotor8863>();
        this.mode = mode;
        motorCurrents = new double[4];
    }

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

    /**
     * Add a motor to the list of motors to be monitored. You have to manually add each motor
     * you want to monitor as part of the setup for an object created from this class.
     *
     * @param motor
     */
    public void addMotor(DcMotor8863 motor) {
        if (motorList.size() < 4) {
            motorList.add(motor);
        } else {
            throw new ArrayStoreException("Attempt to add 5th motor to MotorCurrentVoltageMonitor, only 4 are allowed");
        }
    }

    /**
     * The file prefix for the data file. Date and time get appended to the name. Call this method
     * AFTER you have added all of your motors. This is also part of the setup.
     *
     * @param filePrefix
     */
    public void setupCSVDataFile(String filePrefix) {
        dataFile = new CSVDataFile(filePrefix);
        writeCSVHeaderLine();
    }

    /**
     * Write a header line into the comma separated value file
     */
    private void writeCSVHeaderLine() {
        String buffer = "";
        if (dataFile != null) {
            for (DcMotor8863 motor : motorList) {
                buffer = buffer + motor.getMotorName() + " current,";
            }
            buffer = buffer + " Expansion hub voltage";
            dataFile.writeData(buffer);
        }
    }

    /**
     * Write the motor current and hub voltage data just gathered to the data file.
     */
    private void writeCSVData() {
        if (dataFile != null) {
            dataFile.writeData(motorCurrents[0], motorCurrents[1], motorCurrents[2], motorCurrents[3], supplyVoltage);
        }
    }

    /**
     * Add the current and voltage data to the telemetry data to be displayed on the driver's station.
     * Note that you will have to actually make it display using a telemetry.update().
     */
    public void fillDisplayBuffer() {
        int index = 0;
        for (DcMotor8863 motor : motorList) {
            telemetry.addData(motor.getMotorName() + " current = ", motorCurrents[index]);
            index++;
        }
        telemetry.addData("hub voltage = ", supplyVoltage);
    }

    /**
     * Get the motor currents and the hub voltage
     */
    private void getData() {
        int index = 0;
        for (DcMotor8863 motor : motorList) {
            motorCurrents[index] = expansionHub.getMotorCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS, motor.getPortNumber());
            index++;
        }
        supplyVoltage = expansionHub.read12vMonitor(ExpansionHubEx.VoltageUnits.VOLTS);
    }

    /**
     * Update the current and voltage data and output it based on the mode. Run this on each robot
     * loop.
     */
    public void update() {
        getData();
        if (mode == OutputTo.DISPLAY || mode == OutputTo.WRITE_CSV_FILE_AND_DISPLAY) {
            fillDisplayBuffer();
        }
        if (mode == OutputTo.WRITE_CSV_FILE || mode == OutputTo.WRITE_CSV_FILE_AND_DISPLAY) {
            writeCSVData();
        }
    }

    /**
     * Close the data file when you are done storing data
     */
    public void closeCSVData() {
        dataFile.closeDataLog();
    }
}
