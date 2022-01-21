package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import android.graphics.Xfermode;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class RevLEDBlinker {

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

    private RevLED led;
    private OnOffCycler blinker;
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
        blinker.start();
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

    public void start() {
        blinker.start();
    }

    public void update() {
        if(blinker.getState() == OnOffCycler.State.ON) {
            led.on();
        }
        if (blinker.getState() == OnOffCycler.State.OFF) {
            led.off();
        }
    }

}
