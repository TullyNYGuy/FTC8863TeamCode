package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import org.firstinspires.ftc.teamcode.Lib.Color;

public class ColorInHSV {

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
    public Color color;
    private float hue; // color wheel in 360 degrees
    private float hueLowerLimit;

    public void setHueLowerLimit(float hueLowerLimit) {
        this.hueLowerLimit = hueLowerLimit;
    }

    private float hueUpperLimit;

    public void setHueUpperLimit(float hueUpperLimit) {
        this.hueUpperLimit = hueUpperLimit;
    }

    /**
     * amount of gray; 0 = gray, 1 = primary color
     */
    private float saturation;

    /**
     * brightness; 0 = black, 1 = white
     */
    private float value;
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public ColorInHSV(Color color, float hueLowerLimit, float hueUpperLimit) {
        this.color = color;
        this.hueLowerLimit = hueLowerLimit;
        this.hueUpperLimit = hueUpperLimit;
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

    /**
     * Given a set of HSV values, uses hue to determine if the object being viewed is this color.
     * @param hsvValues
     * @return true if is this color, false if not
     */
    public boolean isColor(float[] hsvValues) {
        this.hue = hsvValues[0];
        this.saturation = hsvValues[1];
        this.value = hsvValues[2];

        if(hue >= hueLowerLimit && hue <= hueUpperLimit) {
            return true;
        } else {
            return false;
        }
    }
}
