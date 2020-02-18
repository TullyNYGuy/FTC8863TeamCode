package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.openftc.revextensions2.ExpansionHubEx;

import java.util.ArrayList;

public class MotorCurrentVoltageMonitor {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum Mode {
        WRITE_CSV_FILE,
        DISPLAY,
        WRITE_CSV_FILE_AND_DISPLAY
    }

    private Mode mode;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private ArrayList<DcMotor8863> motorList;

    private ExpansionHubEx expansionHub;

    private CSVDataFile dataFile;

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

    public MotorCurrentVoltageMonitor(HardwareMap hardwareMap, Telemetry telemetry, String expansionHubName, Mode mode) {
        expansionHub = hardwareMap.get(ExpansionHubEx.class, expansionHubName);
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

    public void addMotor(DcMotor8863 motor) {
        if (motorList.size() < 4) {
            motorList.add(motor);
        } else {
            throw new ArrayStoreException("Attempt to add 5th motor to MotorCurrentVoltageMonitor, only 4 are allowed");
        }
    }

    public void setupCSVDataFile(String filePrefix) {
        dataFile = new CSVDataFile(filePrefix);
    }

    private void writeCSVHeaderLine() {

    }

    private void writeCSVData() {

    }

    public void fillDisplayBuffer() {

    }

    private void getData(ArrayList<DcMotor8863> motorList) {
        int index = 0;
        for (DcMotor8863 motor : motorList) {
            motorCurrents[index] = expansionHub.getMotorCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS, motor.getPortNumber());
        }
        supplyVoltage = expansionHub.read12vMonitor(ExpansionHubEx.VoltageUnits.VOLTS);
    }

    public void update() {

    }

}
