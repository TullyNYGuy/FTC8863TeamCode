package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class FFBlinkinLed implements FTCRobotSubsystem {

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
    private DataLogging logFile;
    private boolean loggingOn = false;
    private DataLogOnChange logCommandOnchange;

    private boolean initComplete = false;
    private AllianceColor allianceColor = PersistantStorage.getAllianceColor();
    private final String  LED_SYSTEM_NAME = "LED Strip";

    private RevBlinkinLedDriver ledSystem;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public FFBlinkinLed(HardwareMap hardwareMap){
        ledSystem = hardwareMap.get(RevBlinkinLedDriver.class, FreightFrenzyRobotRoadRunner.HardwareName.LED_STRIP.hwName);
        // turn the led strip off
        ledSystem.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
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

    public void setPattern(RevBlinkinLedDriver.BlinkinPattern pattern) {
        ledSystem.setPattern(pattern);
    }

    @Override
    public String getName() {
        return LED_SYSTEM_NAME;
    }

    @Override
    public boolean init(Configuration config) {
        logCommand("Init starting");
        return true;
    }

    @Override
    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
    }

    @Override
    public void update() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    private void logCommand(String command) {
        if (loggingOn && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
    }
}
