package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class StatTrackerGB {

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
    // allow access to private data fields in a controlled way
    //
    //*********************************************************************************************

    // These are all readonly properties so we only want a getter method
    public double getMaximum() {
        return maximum;
    }

    public double getMinimum() {
        return minimum;
    }

    public double getAverage() {
        return average;
    }

    public double getCount() {
        return count;
    }

    public double getSum() {
        return sum;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public StatTrackerGB() {
        // initialize the properties
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

    /**
     * Use the value passed in and the previous maximum to determine if the new value is a new maximum.
     *
     * @param newValue
     */
    private void updateMax(double newValue) {
        if (newValue > maximum) {
            maximum = newValue;
        }
    }

    /**
     * Use the value passed in and the previous minimum to determine if the new value is a new minimum.
     *
     * @param newValue
     */
    private void updateMin(double newValue) {
        if (newValue < minimum) {
            minimum = newValue;
        }
    }

    /**
     * Use the value passed in and the previously processed values to calculate a new average and
     * update the number of times the stat tracker has been called.
     *
     * @param newValue
     */
    private void updateAverage(double newValue) {
        sum = sum + newValue;
        count++;
        average = sum / count;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * This is public method that gets repeatedly called from a loop. Each time the lastest in a
     * series of values is passed in. This method updates all of the stats kept for the series of
     * values.
     *
     * @param newValue
     */
    public void updateStats(double newValue) {
        // The first time this is called there are no previous values so just set all of the
        // stats to the new value.
        if (count == 0) {
            maximum = newValue;
            minimum = newValue;
            sum = newValue;
            average = newValue;
            count = 1;
            // This is not the first time called so update the stats the normal way.
        } else {
            updateMax(newValue);
            updateMax(newValue);
            updateAverage(newValue);
        }
    }

}
