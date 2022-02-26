package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class DataLogOnChange {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private String previousStringToLog = "";
    private DataLogging logFile;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public DataLogOnChange(DataLogging logFile) {
        this.logFile = logFile;
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

    public void log(String stringToLog) {
        if (!stringToLog.equals(previousStringToLog)){
            logFile.logData(stringToLog);
            previousStringToLog = stringToLog;
        }
    }
}
