package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CSVDataFile {

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

    private PrintStream csvDataFile = null;

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

    public CSVDataFile(String folderPath, String filePrefix, Telemetry telemetry) {
        this.folderPath = folderPath;
        this.filePrefix = filePrefix;
        csvDataFileSetup();
    }

    public CSVDataFile(String filePrefix, Telemetry telemetry) {
        this.folderPath = "/sdcard/FTC8863/";
        this.filePrefix = filePrefix;
        csvDataFileSetup();
    }

    private void csvDataFileSetup() {
        boolean result = true;
        // make sure the folder exists, if not create it, and create the log file itself
        this.status = openCSVDataFile(folderPath, filePrefix);
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
    private boolean openCSVDataFile(final String folderPath, final String filePrefix) {
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
            result = openCSVDataFile(logFilePath);
        }
        return result;
    }

    /**
     * This method opens a log file for writing all the messages to it.
     *
     * @param CSVDataFilePath specifies the data log file name.
     * @return true if log file is successfully opened, false if it failed.
     */
    private boolean openCSVDataFile(final String CSVDataFilePath) {
        boolean result = true;

        try {
            csvDataFile = new PrintStream(new File(CSVDataFilePath));
        } catch (FileNotFoundException exception) {
            telemetry.addData("Could not create log file: ", CSVDataFilePath);
            csvDataFile = null;
            result = false;
        }
        csvDataFile.println("Date and Time of log file start = " + dateFormat.format(new Date()));
        return result;
    }

    /**
     * If the directory/folder does not exist, create it.
     *
     * @param folderPath
     * @return
     */
    private boolean mkdir(String folderPath) {
        boolean result = true;

        // see if the folder already exists
        File folder = new File(folderPath);
        if (folder.exists()) {
            // the folder exists already so nothing to do
            result = true;
        } else {
            // the folder does not exist so create it
            try {
                folder.mkdir();
            } catch (Exception exception) {
                telemetry.addData("Could not make data file directory: ", folderPath);
                csvDataFile = null;
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
     * Write a header line into the log file. You can use this to give a column label to each data
     * field. The time header will automatically be inserted into the line.
     *
     * @param headerLine
     */
    public void headerLine(String headerLine) {
        csvDataFile.println(headerLine);
    }

    /**
     * Write a series of header strings into the log file. You can use this to give a column label
     * to each data field. The time header will automatically be inserted into the line.
     *
     * @param args
     */
    public void headerStrings(String... args) {
        // print each argument
        for (String arg : args) {
            csvDataFile.print(arg + ", ");
        }
        // print a newline
        csvDataFile.println();
    }

    /**
     * Add a blenk line to the datalog
     */
    public void blankLine() {
        csvDataFile.println();
    }

    /**
     * Write a piece of data or a debug message into the log file. It will get time stamped.
     *
     * @param dataToWrite
     */
    public void writeData(String dataToWrite) {
        String stringToWrite = String.format(dataToWrite);
        csvDataFile.println(stringToWrite);
    }

    /**
     * Write a double and an integer into the file
     * @param doubleData
     * @param intData
     */
    public void writeData(Double doubleData, Integer intData) {
        // print each argument
        csvDataFile.print(doubleData.toString() + ", " +intData.toString());
        // print a newline
        csvDataFile.println();
    }

    /**
     * Write a double and and int into the data log. Each piece of data will be followed by a comma and
     * a space.
     *
     * @param args a variable number of strings to write into the file in this line
     */
    public void writeData(String... args) {
        // print each argument
        for (String arg : args) {
            csvDataFile.print(arg + ", ");
        }
        // print a newline
        csvDataFile.println();
    }

    /**
     * Write a series of doubles into the data log. Each double will be followed by a comma and
     * a space.
     *
     * @param args a variable number of doubles to write into the file in this line
     */
    public void writeData(Double... args) {
        // print each argument
        for (Double arg : args) {
            csvDataFile.print(Double.toString(arg) + ", ");
        }
        // print a newline
        csvDataFile.println();
    }

    /**
     * Write a string and a series of doubles into the data log. The string and the each double will
     * be followed by a comma and a space.
     * @param string
     * @param args a variable number of doubles to write into the file in this line
     */
    public void writeData(String string, Double... args) {
        // print the string
        csvDataFile.print(string + ", ");
        // print the doubles
        for (Double arg : args) {
            csvDataFile.print(Double.toString(arg) + ", ");
        }
        // print a newline
        csvDataFile.println();
    }

    /**
     * This method closes the data log file. You have to do this after you are finished with logging.
     */
    public void closeDataLog() {
        writeData("CSV Data File Complete");
        if (csvDataFile != null) {
            csvDataFile.close();
            csvDataFile = null;
        }
    }
}
