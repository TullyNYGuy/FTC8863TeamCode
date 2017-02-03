package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

public class StatTracker {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private double maximum;
    private double minimum;
    private double average;
    private int count;

    private double sum;
    private double instantaneous;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getMotorPosition
    //*********************************************************************************************

    public double getMaximum() {
        return maximum;
    }

    public double getMinimum() {
        return minimum;
    }

    public double getAverage() {
        return average;
    }

    public int getCount() {
        return count;
    }

    public double getSum() {
        return sum;
    }

    public double getInstantaneous() {
        return instantaneous;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public StatTracker() {
        maximum = 0;
        minimum = 0;
        average = 0;
        count = 0;
        sum = 0;
        instantaneous = 0;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    public void compareValue(double value1) {
        instantaneous = value1;
        if (count == 0) {
            maximum = value1;
            minimum = value1;
            average = value1;
            sum = value1;
            count++;
        }
        if (count > 0) {
            if (maximum < value1) {
                maximum = value1;
            }
            sum = value1 + sum;

            if (minimum > value1) {
                minimum = value1;

            }
            average = sum / count;
            count++;
        }
    }


    public void reset() {
        maximum = 0;
        minimum = 0;
        average = 0;
        count = 0;
        sum = 0;
        instantaneous = 0;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
}