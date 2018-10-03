package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.String.format;

public class DataLogging {

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

    private ElapsedTime timer;
    private PrintStream dataLog = null;
    
    private String folderPath = null;
    
    private String filePrefix = null;

    private Telemetry telemetry;

    private SimpleDateFormat dateFormat;

    private boolean status;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public boolean isStatusOK() {
        return status;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public DataLogging(String folderPath, String filePrefix, Telemetry telemetry) {
        this.folderPath = folderPath;
        this.filePrefix = filePrefix;
        dataLoggingSetup();
    }
    
    public DataLogging(String filePrefix, Telemetry telemetry) {
        this.folderPath = "/sdcard/FTC8863/";
        this.filePrefix = filePrefix;
//        dataLoggingSetup();
    }

    private void dataLoggingSetup() {
        boolean result = true;
        // make sure the folder exists, if not create it, and create the log file itself
        this.status = openDataLog(this.folderPath, this.filePrefix);
        // create a timer for use in time stamping data
        timer = new ElapsedTime();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * This method opens a log file for writing all the data and debug messages to it. The log file is written to the specified
     * folder. The file name will be formed by concatenating the specified file prefix and a date-time stamp.
     *
     * @param folderPath specifies the folder path.
     * @param filePrefix specifies the file name prefix.
     * @return true if log file is successfully opened, false if it failed.
     */
    private boolean openDataLog(final String folderPath, final String filePrefix) {
        boolean result = true;

        // create a file name for the data log using a date and time suffix that is added to the
        // filePrefix the user gave us.
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH-mm-ss", Locale.US);
        String logFilePath = folderPath + "/" + filePrefix + "_" + dateFormat.format(new Date()) + ".log";
        // create the folder that will hold the data log file
        if (!mkdir(folderPath)) {
            // can't create the folder
            result = false;
        } else {
            result = openDataLog(logFilePath);
        }
        return result;
    }

    /**
     * This method opens a log file for writing all the messages to it.
     *
     * @param dataLogFilePath specifies the data log file name.
     * @return true if log file is successfully opened, false if it failed.
     */
    private boolean openDataLog(final String dataLogFilePath) {
        boolean result = true;

        try {
            dataLog = new PrintStream(new File(dataLogFilePath));
        }
        catch (FileNotFoundException exception) {
            telemetry.addData("Could not create log file: ", dataLogFilePath);
            dataLog = null;
            result = false;
        }
        dataLog.println("Time is in milliseconds!");
        dataLog.println("Date and Time of log file start = " + dateFormat.format(new Date()));
        return result;
    }

    /**
     * If the directory/folder does not exist, create it.
     * @param folderPath
     * @return
     */
    private boolean mkdir(String folderPath) {
        boolean result = true;

        // see if the folder already exists
        File folder = new File(folderPath);
        if(folder.exists()) {
            // the folder exists already so nothing to do
            result = true;
        } else {
            // the folder does not exist so create it
            try {
                folder.mkdir();
            }
            catch (Exception exception) {
                telemetry.addData("Could not make log file directory: ", folderPath);
                dataLog = null;
                result = false;
            }
        }
        return result;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * Start the timer used for time stamping the data. Do this at the beginning of whatever you are
     * tyring to log data for. For example, do this at the very beginning of autonomous.
     */
    public void startTimer() {
        timer.reset();
    }

    /**
     * Write a piece of data or a debug message into the log file. It will get time stamped.
     * @param dataToLog
     */
    public void logData(String dataToLog) {
        double timeStamp = timer.milliseconds();
        String stringToWrite = "Time = " + String.format( "%.2f", timeStamp) + " " + dataToLog;
        dataLog.println(stringToWrite);
    }

    /**
     * This method closes the data log file. You have to do this after you are finished with logging.
     */
    public void closeDataLog() {
        logData("Log File Complete");
        if (dataLog != null) {
            dataLog.close();
            dataLog = null;
        }
    }
}
