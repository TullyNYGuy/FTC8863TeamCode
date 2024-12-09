package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.teamcode.Lib.Color;

public class ColorDetectorHSV {

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

    public ColorInHSV red;
    public ColorInHSV yellow;
    public ColorInHSV green;
    public ColorInHSV cyan;
    public ColorInHSV blue;
    public ColorInHSV magenta;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ColorDetectorHSV() {
        red = new ColorInHSV(Color.RED, 0, 60);
        yellow = new ColorInHSV(Color.YELLOW, 61, 120);
        green = new ColorInHSV(Color.GREEN, 121, 180);
        cyan = new ColorInHSV(Color.BLUE, 181,240 );
        blue = new ColorInHSV(Color.CYAN, 241, 300);
        magenta = new ColorInHSV(Color.MAGENTA, 301, 360);
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
    public Color getColor(float[] hsvValues) {
        Color result = Color.UNKNOWN;
        if(red.isColor(hsvValues)) {
            result = red.name;
        } else {
            if (yellow.isColor(hsvValues)) {
                result = yellow.name;
            } else {
                if (green.isColor(hsvValues)) {
                    result = green.name;
                } else {
                    if (cyan.isColor(hsvValues)) {
                        result = cyan.name;
                    } else {
                        if (blue.isColor(hsvValues)) {
                            result = blue.name;
                        } else {
                            if (magenta.isColor(hsvValues)) {
                                result = magenta.name;
                            }
                        }
                    }
                }
            }

        }
        return result;
    }

    public boolean isColor(Color color, float[] hsvValues) {
        boolean result = false;
        switch (color) {
            case RED:
                result = red.isColor(hsvValues);
                break;
            case YELLOW:
                result = yellow.isColor(hsvValues);
                break;
            case GREEN:
                result = green.isColor(hsvValues);
                break;
            case CYAN:
                result = cyan.isColor(hsvValues);
                break;
            case BLUE:
                result = blue.isColor(hsvValues);
                break;
            case MAGENTA:
                result = magenta.isColor(hsvValues);
                break;
        }
        return result;
    }
}
