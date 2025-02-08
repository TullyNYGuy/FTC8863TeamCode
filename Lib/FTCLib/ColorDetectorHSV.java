package org.firstinspires.ftc.teamcode.Lib.FTCLib;


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

    // default colors to look for
    public ColorInHSV red;
    public ColorInHSV yellow;
    public ColorInHSV green;
    public ColorInHSV cyan;
    public ColorInHSV blue;
    public ColorInHSV magenta;

    // an array of possible colors
    private ColorInHSV[] possibleColors;

    private int minimumNumberMatchesForValidColor = 2;


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * This constructor sets up a default list of color definitions based only on Hue.
     */
    public ColorDetectorHSV() {
        red = new ColorInHSV(Color.RED, 0, 60);
        yellow = new ColorInHSV(Color.YELLOW, 60, 120);
        green = new ColorInHSV(Color.GREEN, 120, 180);
        cyan = new ColorInHSV(Color.BLUE, 180,240 );
        blue = new ColorInHSV(Color.CYAN, 240, 300);
        magenta = new ColorInHSV(Color.MAGENTA, 300, 360);
        possibleColors = new ColorInHSV[]{red, yellow, green, cyan, blue, magenta};
    }

    /**
     * This constructor requires you to pass in an array with color definitions in the array.
     * @param possibleColors
     */
    public ColorDetectorHSV(ColorInHSV[] possibleColors) {
        this.possibleColors = possibleColors;
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
     * Using hue value only, get the color.
     * @param hsvValues
     * @return
     */
    public Color getColorUsingHue(float[] hsvValues) {
        Color result = Color.UNKNOWN;
        for (ColorInHSV possibleColor : possibleColors){
            if (possibleColor.isColorUsingHue(hsvValues)) {
                result = possibleColor.name;
                break;
            }
        }
        return result;
    }

    public Color getMostLikelyColor(float[] hsvValues) {
        Color result = Color.UNKNOWN;
        Color possibleResult = Color.UNKNOWN;
        int numberMatchedRangesSoFar = 0;
        int numberMatchedRangesForThisColor = 0;
        for (ColorInHSV possibleColor : possibleColors){
            // Do Hue, Saturation and Value of this object fall into the ranges for this color?
            // If so how many fall in range? Is it more than any other previous color checked so far?
            numberMatchedRangesForThisColor = possibleColor.howLikelyIsColor(hsvValues);
            if (numberMatchedRangesForThisColor > numberMatchedRangesSoFar) {
                // This is the highest number of matched ranges so far, so make this the new
                // choice for the color.
                possibleResult = possibleColor.name;
                numberMatchedRangesSoFar = numberMatchedRangesForThisColor;
            }
        }
        // check to see if the number of matches is more than the minimum number of matches required
        // in order to say this is a valid color determination
        if (numberMatchedRangesSoFar >= minimumNumberMatchesForValidColor) {
            result = possibleResult;
        }
        return result;
    }
}
