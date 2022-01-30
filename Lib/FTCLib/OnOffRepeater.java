package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;

public class OnOffRepeater {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum State {
        ON,
        OFF;
    }

    private State state = State.OFF;
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private double frequency = 5;

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
        interval = 1 / frequency;
    }

    private double interval;
    ElapsedTime intervalTimer;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public OnOffRepeater(double frequency) {
        intervalTimer = new ElapsedTime();
        setFrequency(frequency);
        start();
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
    private void update() {
        switch (state) {
            case OFF:
                if (intervalTimer.seconds() > 0.5 * interval) {
                    state = State.ON;
                    intervalTimer.reset();
                }
                break;
            case ON:
                if (intervalTimer.seconds() > 0.5 * interval) {
                    state = State.OFF;
                    intervalTimer.reset();
                }
                break;
        }
    }

    public State getOnOff() {
        return state;
    }

    public void start() {
        intervalTimer.reset();
        state = State.OFF;
    }
}
