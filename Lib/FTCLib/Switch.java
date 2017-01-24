package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This class supports a switch connected to a digital IO port on the core device interface module.
 * As a switch is pressed, the mechanical stuff inside it can make and lose contact for very short
 * periods of time (milli-seconds) before it finally makes contact for good. This is called switch
 * bounce. When you have switch bounce, it can look like the switch is pressed or not pressed
 * (released) depending on when you read it. This bouncing back and forth can give you false
 * readings. This class debounces the switch in software by ignoring and quick transitions to
 * pressed. It makes sure that the switch has been pressed for at least debounceLengthInMs (40
 * milli-seconds by default) before it will tell you the switch is actually pressed. The class
 * can tell you 3 things:
 *    pressed - switch is currently pressed
 *    released - switch is currently not pressed
 *    bumped - switch was pressed and released at some point in the past
 * As you can see, bumped has some memory. It remembers the switch pressed and released at some
 * point in the past. This memory is erased if you check for pressed or released though. The
 * assumption is that if you are checking for pressed or released you do not care about bumped at
 * that point in time. Checking for bumped will also erase the memory once you have checked it.
 *
 * Switches come in 2 flavors:
 *    normally open   - there is not a short between the two contacts when the switch is not pressed
 *                    - the contacts will short when the switch is pressed
 *    normally closed - there is a short between the two contacts when the switch is not pressed
 *                    - there is no short when the switch is pressed
 *
 * In order to interface a switch with a port on the core device interface module, there needs to be
 * a resistor that pulls one contact on the switch up to +5 volts through a resistor. Our cables
 * include this resistor and all of our switches are normally open (NO), at least as of 12/2016.
 * The class will handle all of the logic as long as you tell the constructor what type of switch
 * you have.
 *
 * To use a switch you have to connect the switch to one of the digital ports on the core device
 * interface module. Make sure that the dark wire is connected to the ground end. Configure your
 * phone so that there is a digital device on the digital port and give it a name. You will pass
 * that name into the constructor for the class.
 */
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
     * because it has to track the switch for debouncing and for bumped. If we were not doing that
     * we would not need a state machine and it would not have to be called every loop cycle.
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

    /**
     * If the switch is currently pressed return true. If it is not pressed return false. if the
     * switch is in the middle of debouncing the assumption is that it is not pressed so return
     * false.
     * @return true = switch pressed
     */
    public boolean isPressed() {
        // force an update to run so the state is correct
        updateSwitch();
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

    /**
     * If the switch is not pressed return true. If it is pressed return false. If it is currently
     * in the middle of debouncing the assumption is that it is not pressed yet so true is returned.
     * @return
     */
    public boolean isReleased() {
        // force an update to run so the state is correct
        updateSwitch();
        boolean result = false;
        if(currentState == SwitchState.RELEASED || currentState == SwitchState.DEBOUNCING) {
            result = true;
        }
        // Since the user is checking released, they don't care about bumped so reset it.
        bumped = false;
        return result;
    }

    /**
     * Returns true if the switch has been pressed and released since the last time it was checked.
     * Calling this will clear bumped after you get your answer.
     * @return true if pressed and released since the last time you called any method to check
     * the switch
     */
    public boolean isBumped() {
        // force an update to run so the state is correct
        updateSwitch();
        boolean result = bumped;
        // Since the user is checking bumped, they will get their answer but we now need to reset it
        bumped = false;
        return result;
    }
}
