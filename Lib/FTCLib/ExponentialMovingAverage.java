package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.acmerobotics.dashboard.config.Config;

@Config
public class ExponentialMovingAverage {

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
    /**
     * How much weight to give to the latest value. The more you weight it, the more the new value
     * will affect the average. The lower the weight, the more past values affect the average. The
     * more past values affect the average the more low pass filtering you get.
     * Public static so that the FTC Dashboard can be used to tune the value.
     * Weight should be between 0 and 1:
     * 0 - don't do this, but close to zero and you get only the slowest moving data
     * 1 - near 1 there is very little low pass filtering
     */
    public static double WEIGHT_OF_NEW_VALUE;

    public static void setWeightOfNewValue(double weightOfNewValue) {
        if (weightOfNewValue < 1) {
            weightOfNewValue = 1;
        }
        if (weightOfNewValue <= 0) {
            weightOfNewValue = .01; // abitrarily low
        }
        WEIGHT_OF_NEW_VALUE = weightOfNewValue;
    }

    private Double oldValue;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ExponentialMovingAverage(double weightOfNew) {
        WEIGHT_OF_NEW_VALUE = weightOfNew;
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
    public double average(double value) {
        if (oldValue == null) {
            oldValue = value;
            return value;
        }
        double newValue = oldValue + WEIGHT_OF_NEW_VALUE * (value - oldValue);
        oldValue = newValue;
        return newValue;
    }

}
