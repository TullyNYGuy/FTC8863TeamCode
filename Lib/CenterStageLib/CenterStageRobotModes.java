package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;

/**
 * This class exists as a way to communicate between the gamepad and the teleop
 * opmode. It cannot be part of the gamepad because of the chicken and egg
 * thing. This stuff has to be created the gamepad is created.
 */
public class CenterStageRobotModes {

    //*********************************************************************************************
    // Direction swap - robot driving forwards as normal, or backwards as normal (inverse)
    //*********************************************************************************************

    public enum DirectionSwap {
        NORMAL, // joystick directions are normal
        INVERSED // joystick directions are opposite
    }

    private DirectionSwap directionSwap = DirectionSwap.NORMAL;

    public DirectionSwap getDirectionSwap() {
        return directionSwap;
    }

    public void setDirectionSwap( DirectionSwap directionSwap) {
        this.directionSwap = directionSwap;
        if (directionSwap == DirectionSwap.NORMAL) {
            directionSwapMultiplier = +1;
        } else {
            // direction swap is INVERSED
            directionSwapMultiplier = -1;
        }
    }

    private double directionSwapMultiplier = +1;

    public double getDirectionSwapMultiplier () {
        return directionSwapMultiplier;
    }

    //*********************************************************************************************
    // Driving power
    //*********************************************************************************************
    private double previousMaxPower;

    public double getPreviousMaxPower() {
        return previousMaxPower;
    }

    // default power when starting is 75%
    private double currentMaxPower = 0.75;

    public double getCurrentMaxPower() {
        return currentMaxPower;
    }

    public void setMaxDrivingPower (double maxDrivingPower) {
        if (maxDrivingPower <= 1.0 && maxDrivingPower >= 0) {
            previousMaxPower = currentMaxPower;
            currentMaxPower = maxDrivingPower;
        }
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public CenterStageRobotModes(){

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

}
