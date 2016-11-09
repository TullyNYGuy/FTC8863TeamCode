package org.firstinspires.ftc.teamcode.Lib.FTCLib;


public class ToggleSwitch {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum SwitchState {
        PRESSED, RELEASED, BUMPED, RESET
    }

    public enum SwitchCommand {
        RESET, NORESET
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    /**
     * The state of the switch
     */
    private SwitchState switchState = SwitchState.RELEASED;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public SwitchState getSwitchstate() {
        return switchState;
    }

    public void setSwitchstate(SwitchState switchstate) {
        this.switchState = switchstate;
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ToggleSwitch() {
        setSwitchstate(SwitchState.RELEASED);
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

    public SwitchState updateSwitchState(boolean button, SwitchCommand switchCommand) {

        switch(switchState) {
            case RELEASED:
                break;
            case PRESSED:
                break;
            case BUMPED:
                break;
            case RESET:
                break;
        }
        return SwitchState.RELEASED;
    }
}
