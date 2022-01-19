package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.EventListener;

public class OnOffCycler {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum State {
        ON,
        OFF
    }

    private State state = State.OFF;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    /**
     * The frequency of the on-off cycle in number of cycles per second - hertz
     */
    private double frequency = 1;

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    /**
     * The time between changes in state in milliseconds
     */
    private double interval;

    private ElapsedTime timer;
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public OnOffCycler(double frequency) {
        this.frequency = frequency;
        // the time between a change in state (in milliseconds)
        interval = 1 / frequency * 1000;
        timer = new ElapsedTime();
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
     public void start() {
        timer.reset();
     }

     public State getState() {
        if (state == State.OFF & timer.milliseconds() > interval) {
            state = State.ON;
            timer.reset();
        }
        if (state == State.ON && timer.milliseconds() > interval) {
            state = State.OFF;
            timer.reset();
        }
        return state;
     }

}
