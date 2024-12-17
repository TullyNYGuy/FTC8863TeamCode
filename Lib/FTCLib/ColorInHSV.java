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
    public Color name;
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
    private float saturationLowerLimit;

    public void setSaturationLowerLimit(float saturationLowerLimit) {
        this.saturationLowerLimit = saturationLowerLimit;
    }

    private float saturationUpperLimit;

    public void setSaturationUpperLimit(float saturationUpperLimit) {
        this.saturationUpperLimit = saturationUpperLimit;
    }

    /**
     * brightness; 0 = black, 1 = white
     */
    private float value;
    private float valueLowerLimit;

    public void setValueLowerLimit(float valueLowerLimit) {
        this.valueLowerLimit = valueLowerLimit;
    }

    private float valueUpperLimit;

    public void setValueUpperLimit(float valueUpperLimit) {
        this.valueUpperLimit = valueUpperLimit;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public ColorInHSV(Color name, float hueLowerLimit, float hueUpperLimit) {
        this.name = name;
        this.hueLowerLimit = hueLowerLimit;
        this.hueUpperLimit = hueUpperLimit;
    }

    public ColorInHSV(Color name,
                      float hueLowerLimit, float hueUpperLimit,
                      float saturationLowerLimit, float saturationUpperLimit,
                      float valueLowerLimit, float valueUpperLimit) {
        this.name = name;
        this.hueLowerLimit = hueLowerLimit;
        this.hueUpperLimit = hueUpperLimit;
        this.saturationLowerLimit = saturationLowerLimit;
        this.saturationUpperLimit = saturationUpperLimit;
        this.valueLowerLimit = valueLowerLimit;
        this.valueUpperLimit = valueUpperLimit;
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
