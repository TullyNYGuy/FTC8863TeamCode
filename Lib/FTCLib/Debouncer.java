package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class Debouncer {

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
    private boolean buttonReleased = true;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public Debouncer() {

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
    public boolean isPressed(boolean button) {
        boolean debouncedButton = false;
        if (button && buttonReleased) {
            buttonReleased = false;
            debouncedButton = true;
        }
        if (!button) {
            buttonReleased = true;
        }
        return debouncedButton;
    }

}
