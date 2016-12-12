package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Switch {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum SwitchState {
        RELEASED, PRESSED, DEBOUNCING;
    }

    public enum SwitchType {
        NORMALLY_OPEN, NORMALLY_CLOSED;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private SwitchState currentState = SwitchState.RELEASED;
    private SwitchState nextState = SwitchState.RELEASED;
    private SwitchType switchType = SwitchType.NORMALLY_OPEN;
    private boolean bumped = false;
    private DigitalChannel switchInput;
    private ElapsedTime timer;
    private double debounceLengthInMs = 40;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getDebounceLengthInMs() {
        return debounceLengthInMs;
    }

    public void setDebounceLengthInMs(double debounceLengthInMs) {
        this.debounceLengthInMs = debounceLengthInMs;
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public Switch(HardwareMap hardwareMap, String switchName, SwitchType switchType) {
        switchInput = hardwareMap.get(DigitalChannel.class, switchName);
        switchInput.setMode(DigitalChannelController.Mode.INPUT);
        timer = new ElapsedTime();
        this.switchType = switchType;
        currentState = SwitchState.RELEASED;
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * State machine for the switch. This method should be called once every loop cycle. This is
     * because it has to track the switch for debouncing.
     */
    public void updateSwitch() {
        switch(currentState) {
            case RELEASED:
                // This is the only state where bumped can be true.
                // We don't know if the switch is bumped or not by just knowing it is released.
                // It depends on whether it was pressed before this or not. So we can't do anything
                // about bumped.
                if(getPressed()) {
                    // The switch read as pressed so start debouncing it. Reset the debounce timer.
                    timer.reset();
                    nextState = SwitchState.DEBOUNCING;
                }
                break;
            case PRESSED:
                if(!getPressed()) {
                    // If switch has been released, it has now been pressed and released so
                    // bumped is set
                    bumped = true;
                    nextState = SwitchState.RELEASED;
                } else {
                    // the switch is still pressed so it can't be bumped (pressed and released)
                    bumped = false;
                }
                break;
            case DEBOUNCING:
                // The switch has to be pressed for at least debounceLengthInMS in order to be
                // called pressed.

                // If the switch is in the middle of being debounced, it can't be bumped
                bumped = false;

                if(!getPressed()) {
                    // Switch has gone back to released so back to released state
                    nextState = SwitchState.RELEASED;
                }

                if(timer.milliseconds() < debounceLengthInMs && getPressed()) {
                    // timer has not expired on debounce and switch is still pressed so we are still
                    // debouncing.
                    nextState = SwitchState.DEBOUNCING;
                }

                if(timer.milliseconds() >= debounceLengthInMs && getPressed()) {
                    // switch is still pressed after timer has expired, so we call it pressed
                    nextState = SwitchState.PRESSED;
                }
                break;
        }
        currentState = nextState;
    }


    /**
     * The bit on the port is read and then the logic determines if the switch is pressed depending
     * on the type of the switch:
     * Normally open: released = 1, pressed = 0
     * Normally closed: released = 0, pressed = 1
     * @return true = pressed
     */
    private boolean getPressed() {
        boolean result = false;
        if(switchInput.getState()) {
            // The switch is registering a 1 on the port
            if(switchType == SwitchType.NORMALLY_OPEN) {
                // A NO switch will be pulled high when released
                result = false;
            } else {
                // A NC switch will be low normally but will be pulled high when pressed
                result = true;
            }
        } else {
            // The switch is registering a 0 on the port
            if(switchType == SwitchType.NORMALLY_OPEN) {
                // A NO switch will be grounded when pressed
                result = true;
            } else {
                // A NC switch will be grounded when released
                result = false;
            }
        }
        return result;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public boolean isPressed() {
        boolean result = false;
        // The switch is not pressed if the state is released or in the process of debouncing
        if(currentState == SwitchState.RELEASED || currentState == SwitchState.DEBOUNCING) {
            result = false;
        }
        // the switch is pressed if the state is pressed
        if(currentState == SwitchState.PRESSED) {
            result = true;
        }
        return result;
    }

    public boolean isReleased() {
        boolean result = false;
        if(currentState == SwitchState.RELEASED || currentState == SwitchState.DEBOUNCING) {
            result = true;
        }
        // Since the user is checking released, they don't care about bumped so reset it.
        bumped = false;
        return result;
    }

    public boolean isBumped() {
        boolean result = bumped;
        // Since the user is checking bumped, they will get their answer but we now need to reset it
        bumped = false;
        return result;
    }
}
