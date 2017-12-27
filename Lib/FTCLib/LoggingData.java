package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoggingData {

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

    private ElapsedTime recordTime;
    private PrintStream traceLog = null;

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

    public LoggingData(String fileprefix, String foldername) {
        openTraceLog(foldername, fileprefix);
        recordTime = new ElapsedTime();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * This method opens a log file for writing all the trace messages to it. The log file is written to the specified
     * folder. The file name will be formed by concatenating the specified file prefix and a date-time stamp.
     *
     * @param folderPath specifies the folder path.
     * @param filePrefix specifies the file name prefix.
     * @return true if log file is successfully opened, false if it failed.
     */
    public boolean openTraceLog(final String folderPath, final String filePrefix)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH-mm-ss", Locale.US);
        String logFilePath = folderPath + "/" + filePrefix + "_" + dateFormat.format(new Date()) + ".log";
        File folder = new File(folderPath);
        folder.mkdir();

        return openTraceLog(logFilePath);
    }   //openTraceLog

    /**
     * This method closes the trace log file.
     */
    public void closeTraceLog()
    {
        if (traceLog != null)
        {
            traceLog.close();
            traceLog = null;
        }
    }   //closeTraceLog

    /**
     * This method opens a log file for writing all the trace messages to it.
     *
     * @param traceLogName specifies the trace log file name.
     * @return true if log file is successfully opened, false if it failed.
     */
    public boolean openTraceLog(final String traceLogName)
    {
        boolean success = true;

        try
        {
            traceLog = new PrintStream(new File(traceLogName));
        }
        catch (FileNotFoundException e)
        {
            traceLog = null;
            success = false;
        }

        return success;
    }   //openTraceLog

    public void timerStart() {
        recordTime.reset();
    }

    public void logData(String dataToLog) {
        double timeStamp = recordTime.milliseconds();
        String stringToWrite = "Time = " + timeStamp + " " + dataToLog;
        traceLog.println(stringToWrite);
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
}
