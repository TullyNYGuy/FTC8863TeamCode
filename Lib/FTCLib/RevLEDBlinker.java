package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import android.graphics.Xfermode;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class RevLEDBlinker implements FTCRobotSubsystem{

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum Mode {
        BLINKING,
        STEADY
    }
    private Mode mode = Mode.STEADY;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private RevLED led;
    private OnOffCycler blinker;

    private DataLogging logFile;
    private boolean loggingOn = false;
    private DataLogOnChange logCommandOnchange;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public RevLEDBlinker(double frequency, RevLED.Color color, HardwareMap hardwareMap, String port1Name, String port2Name) {
        led = new RevLED(hardwareMap, port1Name, port2Name);
        led.setColor(color);
        blinker = new OnOffCycler(frequency);
        mode = Mode.STEADY;
        off();
    }

    public RevLEDBlinker(double frequency, RevLED led) {
        this.led = led;
        blinker = new OnOffCycler(frequency);
        blinker.start();
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

    public void setColor(RevLED.Color color) {
        led.setColor(color);
    }

    public void setFrequency(double frequency) {
        blinker.setFrequency(frequency);
    }

    public void off() {
        led.off();
    }

    public void steadyRed() {
        mode = Mode.STEADY;
        led.on(RevLED.Color.RED);
    }

    public void steadyAmber() {
        mode = Mode.STEADY;
        led.on(RevLED.Color.AMBER);
    }

    public void steadyGreen() {
        mode = Mode.STEADY;
        led.on(RevLED.Color.GREEN);
    }

    public void startBlinking() {
        mode = Mode.BLINKING;
        blinker.start();
    }

    public void startBlinking(RevLED.Color color, double frequency) {
        setColor(color);
        setFrequency(frequency);
        startBlinking();
    }

    @Override
    public void update() {
        if (mode == Mode.BLINKING) {
            if (blinker.getState() == OnOffCycler.State.ON) {
                led.on();
            }
            if (blinker.getState() == OnOffCycler.State.OFF) {
                led.off();
            }
        }
    }

    @Override
    public String getName() {
        return "LED";
    }

    @Override
    public boolean isInitComplete() {
        logCommand("Init complete");
        return true;
    }

    @Override
    public boolean init(Configuration config) {
        logCommand("Init starting");
        off();
        return true;
    }

    @Override
    public void shutdown() {
        off();
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
