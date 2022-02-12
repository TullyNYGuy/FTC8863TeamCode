package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class RevLED {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum Color {
        RED,
        GREEN,
        AMBER
    }

    // default color is red
    private Color color = Color.RED;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private DigitalChannel revLEDPort1;
    private DigitalChannel revLEDPort2;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
     public RevLED (HardwareMap hardwareMap, String port1Name, String port2Name) {
         revLEDPort1 = hardwareMap.get(DigitalChannel.class, port1Name);
         revLEDPort2 = hardwareMap.get(DigitalChannel.class, port2Name);
         revLEDPort1.setMode(DigitalChannel.Mode.OUTPUT);
         revLEDPort2.setMode(DigitalChannel.Mode.OUTPUT);
         off();
     }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private void outputColor() {
         switch (color) {
             case RED:
                 revLEDPort1.setState(false);
                 revLEDPort2.setState(true);
                 break;
             case GREEN:
                 revLEDPort1.setState(true);
                 revLEDPort2.setState(false);
                 break;
             case AMBER:
                 revLEDPort1.setState(false);
                 revLEDPort2.setState(false);
                 break;
         }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void off() {
        revLEDPort1.setState(true);
        revLEDPort2.setState(true);
    }

    public void on() {
         outputColor();
    }

    public void on(Color color) {
         setColor(color);
         on();
    }
}
