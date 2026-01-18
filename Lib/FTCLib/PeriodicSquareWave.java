package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Generate a periodic trapezoidal wave with 4 equal sections:
 * 1st quarter of the wave = rising ramp
 * 2nd quarter of the wave = flat top at +amplitude
 * 3rd quarter of the wave = falling ramp
 * 4th quarter of the wave = flat bottom at -amplitude
 * The wave is shifted in phase so that at t=0, y=0
 * The period (in mSec) and amplitude can be controlled
 */

@Config
public class PeriodicSquareWave {

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
     * The period of the wave in milliseconds
     */
    public static double periodInMilliseconds = 1000;

    public double getPeriodInMilliseconds() {
        return periodInMilliseconds;
    }

    public void setPeriodInMilliseconds(double periodInMilliseconds) {
        this.periodInMilliseconds = periodInMilliseconds;
    }

    /**
     * The amplitude of the wave in whatever units you are using.
     */
    public static double amplitude = 1;

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    private ElapsedTime timer;
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public PeriodicSquareWave(double periodInMilliseconds, double amplitude) {
        this.periodInMilliseconds = periodInMilliseconds;
        this.amplitude = amplitude;
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

    /**
     * Set time = 0
     */
    public void start() {
        timer.reset();
    }

    /**
     * Returns the y value given the time since the function was started (ie time = 0)
     *
     * @return
     */
    public double getY() {
        return (amplitude * Math.signum(Math.sin(2 * Math.PI * timer.milliseconds() / periodInMilliseconds)));
    }

}
