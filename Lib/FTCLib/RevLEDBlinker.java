package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest.OffsetTest;

public class RevLEDBlinker {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum State {
        STARTED,
        STOPPED;
    }

    private State state = State.STOPPED;
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private OnOffRepeater onOffRepeater;
    private RevLEDDriver revLED;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public RevLEDBlinker(String port1Name, String port2Name, HardwareMap hardwareMap) {
        onOffRepeater = new OnOffRepeater(2);
        revLED = new RevLEDDriver(port1Name, port2Name, hardwareMap);
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
    public void setfrequency(double frequency) {
        onOffRepeater.setFrequency(frequency);
    }

    public void setColor(RevLEDDriver.Color color) {
        revLED.setColor(color);
    }

    public void update() {
        onOffRepeater.update();

        if (state == State.STARTED) {
            if (onOffRepeater.getOnOff() == OnOffRepeater.State.OFF) {
                revLED.off();
            }

            if (onOffRepeater.getOnOff() == OnOffRepeater.State.ON) {
                revLED.on();
            }
        }
        if (state == State.STOPPED) {
            revLED.off();
        }
    }

    public void start() {
        state = State.STARTED;
    }

    public void stop() {
        state = State.STOPPED;
    }
}