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
    public boolean isLimitReached(double currentPosition) {
        // don't really care about the current position. Just check the switch. The only reason for
        // passing the currentPosition is to satisfy the interface
        return limitSwitch.isPressed();
    }

    @Override
    public boolean isOkToMove(double currentPosition, double proposedPosition) {
        boolean result = false;
        if(!limitSwitch.isPressed()) {
            result = true;
        } else {
            // The limit switch is tripped. It may be possible to move, as long as the movement
            // is not past the limit switch. It has to be in the direction opposite of the limit.

            // if this limit switch is limiting movement that decreases in position (ie below it)
            // then the only permitted movement is above the limit. Note that since the limit switch
            // is tripped, the current position is the position of the limit switch.
            if (limitDirection == Direction.LIMIT_DECREASING_POSITIONS) {
                if (proposedPosition <= currentPosition) {
                    result = false;
                } else {
                    result = true;
                }
            }
            // if this limit switch is limiting movement that increase in position (ie above it)
            // then the only permitted movement is below the limit. Note that since the limit switch
            // is tripped, the current position is the position of the limit switch.
            if (limitDirection == Direction.LIMIT_INCREASING_POSITIONS) {
                if (proposedPosition >= currentPosition) {
                    result = false;
                } else {
                    result = true;
                }
            }
        }
        return result;
    }
}
