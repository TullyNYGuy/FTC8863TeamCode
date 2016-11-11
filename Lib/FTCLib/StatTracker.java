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

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getMotorPosition
    //*********************************************************************************************

    public int getMaximum() {
        return maximum;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getAverage() {
        return average;
    }

    public int getCount() {
        return count;
    }

    public int getSum() {
        return sum;
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
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private int compareValue(double value1) {
        if (count == 0) {
            value1 = maximum;
            value1 = minimum;
            value1 = average;
            value1 = sum;
        }
        if (count > 0) {
            sum = value1 + sum;
            //what should I do?? finish the sencond if statement ^^//
    }
        //if (value1 > value2) {
        //    if (count == 0) {
        //        value1 = maximum;
        //        value2 = minimum;
        //   }
        //} else {
        //    if (count == 0) {
        //       value2 = maximum;
        //        value1 = minimum;
        //    }
        //}

        //count++;

    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************


}
