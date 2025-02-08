package org.firstinspires.ftc.teamcode.Lib.FTCLib;


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
    public boolean isColorUsingHue(float[] hsvValues) {
        float hue = hsvValues[0];
        float saturation = hsvValues[1];
        float value = hsvValues[2];

        if(hue > hueLowerLimit && hue <= hueUpperLimit) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns a number 0-3 that says how many of the HSV color ranges were matched.
     * 0 - the HSV values did not fall within the hue saturation or value ranges. The values are
     *     not likely to be this color.
     * 1 - the HSV values fell within 1 of the 3 ranges
     * 2 - the HSV values fell within 2 of the 3 ranges
     * 3 - the HSV values fell within 3 of the 3 ranges. The values are likely to be this color.
     * @param hsvValues
     * @return
     */
    public int howLikelyIsColor(float[] hsvValues) {
        float hue = hsvValues[0];
        float saturation = hsvValues[1];
        float value = hsvValues[2];
        int numberRangesMatched = 0;

        if(hue > hueLowerLimit && hue <= hueUpperLimit) {
            numberRangesMatched++;
        }
        if(saturation > saturationLowerLimit && saturation <= saturationUpperLimit) {
            numberRangesMatched++;
        }
        if(value > valueLowerLimit && value <= valueUpperLimit) {
            numberRangesMatched++;
        }
        return numberRangesMatched;
    }
}
