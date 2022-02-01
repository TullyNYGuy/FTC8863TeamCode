package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.HardwareMap;

public class LimitSwitch implements MovementLimit{

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

    public Switch limitSwitch;
    private MovementLimit.Direction limitDirection;
    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public LimitSwitch(String switchName, Switch.SwitchType switchType,  MovementLimit.Direction limitDirection, HardwareMap hardwareMap) {
        limitSwitch = new Switch(hardwareMap, switchName, switchType);
        this.limitDirection = limitDirection;
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

    public void update() {
        limitSwitch.updateSwitch();
    }

    @Override
    public boolean isLimitReached() {
        return limitSwitch.isPressed();
    }

    @Override
    public boolean isOkToMove() {
        return !limitSwitch.isPressed();
    }
}
