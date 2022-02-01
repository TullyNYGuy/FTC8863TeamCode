package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

public class LimitPosition implements MovementLimit{

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

    private double limitPosition;
    private String directionString = "";
    private Direction limitDirection;

    private void setDirection( Direction limitDirection) {
        this.limitDirection = limitDirection;
        if (limitDirection == Direction.LIMIT_DECREASING_POSITIONS) {
            directionString = "Retraction";
        } else {
            directionString = "Extension";
        }
    }


    /**
     * The commands sent to the mechanism and the states it passes through can be logged so that
     * you can review them later. The log is a file stored on the phone.
     */
    protected DataLogging logFile;

    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    public void enableDataLogging() {
        this.loggingOn = true;
    }

    public void disableDataLogging() {
        this.loggingOn = false;
    }

    protected boolean loggingOn = false;
    private String lastLimitLogString = "";

    private String limitPositionName = "";

    public String getLimitPositionName() {
        return limitPositionName;
    }

    public void setLimitPositionName(String limitPositionName) {
        this.limitPositionName = limitPositionName;
    }
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public LimitPosition(double limitPosition, Direction limitDirection, String limitPositionName){
        this.limitPosition = limitPosition;
        setDirection(limitDirection);
        this.limitPositionName = limitPositionName;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Write a string into the log file. But make sure it only goes in once. So if something is
     * trying to write the same string over and over, this method only writes it once.
     * @param stringToLog
     */
    private void logLimitOnlyOnce(String stringToLog) {
        if (stringToLog != lastLimitLogString) {
            log(stringToLog);
            lastLimitLogString = stringToLog;
        }
    }

    /**
     * Write a string into the logfile.
     *
     * @param stringToLog
     */
    private void log(String stringToLog) {
        if (logFile != null && loggingOn) {
            logFile.logData(limitPositionName, stringToLog);
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    @Override
    public boolean isLimitReached(double currentPosition) {
        boolean limitReached = false;
            if (currentPosition >= limitPosition) {
                limitReached = true;
                logLimitOnlyOnce(directionString + " position limit tripped");
            } else {
                limitReached = false;
                logLimitOnlyOnce(directionString + " position limit cleared");
            }
        return limitReached;
    }

    @Override
    public boolean isOkToMove(double proposedPosition) {
        boolean result = false;
        if (limitDirection == Direction.LIMIT_DECREASING_POSITIONS) {
            if (proposedPosition <= limitPosition) {
                result = false;
            } else {
                result = true;
            }
        }
        if (limitDirection == Direction.LIMIT_INCREASING_POSITIONS) {
            if (proposedPosition >= limitPosition) {
                result = false;
            } else {
                result = true;
            }
        }
        return result;
    }
}
