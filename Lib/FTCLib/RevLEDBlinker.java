package org.firstinspires.ftc.teamcode.Lib.FTCLib;


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

    public void setColor(RevLEDDriver.Color color){
        revLED.setColor( color);
    }
}
