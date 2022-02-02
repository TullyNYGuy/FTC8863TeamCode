package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RevLEDDriver {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum Color {
        RED,
        GREEN,
        AMBER;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private DigitalChannel port1;
    private DigitalChannel port2;
    private Color color = Color.AMBER;

    public void setColor(Color color) {
        this.color = color;
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public RevLEDDriver(String port1Name, String port2Name, HardwareMap hardwareMap) {
        port1 = hardwareMap.get(DigitalChannel.class, port1Name);
        port1.setMode(DigitalChannel.Mode.OUTPUT);
        port2 = hardwareMap.get(DigitalChannel.class, port2Name);
        port2.setMode(DigitalChannel.Mode.OUTPUT);
        off();
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    private void sendColor() {
        if (color == Color.GREEN) {
            port1.setState(true);
            port2.setState(false);
        }
        if (color == Color.RED) {
            port1.setState(false);
            port2.setState(true);
        }
        if (color == Color.AMBER) {
            port1.setState(false);
            port2.setState(false);
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public Color getColor() {
        return color;
    }

    public void off() {
        port1.setState(true);
        port2.setState(true);
    }

    public void on() {
        sendColor();
    }

    public void on(Color color) {
        setColor(color);
        sendColor();
    }
}
