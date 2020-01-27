package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import java.util.ArrayList;
import java.util.List;

public class PairedList {

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

    private ArrayList<DoubleIntegerData> doubleIntegerDataList;

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

    public PairedList() {
        doubleIntegerDataList = new ArrayList<DoubleIntegerData>();
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
    public void add(double valueA, int valueB) {
        doubleIntegerDataList.add(new DoubleIntegerData(valueA, valueB));
    }

    public void writeToCSVFile(CSVDataFile csvDataFile) {
        for (DoubleIntegerData doubleIntegerDataItem : doubleIntegerDataList) {
            csvDataFile.writeData(doubleIntegerDataItem.toCSVString());
        }
    }
}
