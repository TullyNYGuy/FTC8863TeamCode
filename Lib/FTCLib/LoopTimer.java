package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class LoopTimer implements FTCRobotSubsystem{

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

    private double runningTotalOfLoopTimes = 0;
    private double numberOfLoops = 0;

    private ElapsedTime loopTimer;

    private DataLogging logFile = null;
    private boolean loggingOn = false;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public LoopTimer() {
        loopTimer = new ElapsedTime();
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

    public void resetLoopTimer() {
        runningTotalOfLoopTimes = 0;
        numberOfLoops = 0;
        loopTimer.reset();
    }

    public void startLoopTimer() {
        resetLoopTimer();
    }

    @Override
    public void update() {
        runningTotalOfLoopTimes = loopTimer.milliseconds();
        numberOfLoops++;
        loopTimer.reset();
    }

    public int getAverageLoopTime() {
        return (int)Math.round(runningTotalOfLoopTimes/numberOfLoops);
    }

    public void displayAverageLoopTime(Telemetry telemetry) {
        telemetry.addData("average loop time = ", getAverageLoopTime());
    }

    public void logAverageLoopTime() {
        if(logFile != null && loggingOn) {
            logFile.logData("Average Loop time = ", getAverageLoopTime());
        }
    }

    @Override
    public String getName() {
        return "loop timer";
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public void shutdown() {
        logAverageLoopTime();
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
    }

    @Override
    public boolean init(Configuration config) {
        resetLoopTimer();
        return true;
    }

}
