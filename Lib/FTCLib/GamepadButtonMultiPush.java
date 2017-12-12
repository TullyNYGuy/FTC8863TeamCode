package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.robocol.Command;

import org.firstinspires.ftc.robotcontroller.external.samples.PushbotAutoDriveByEncoder_Linear;

import java.net.PortUnreachableException;

/**
 * This class sets up a way of assigning a set of commands to one button. Each press of the button
 * will advance to the next command in the cycle. When the last command in the cycle is run, the
 * cycle starts over again. The class currently handles 4 commands tied to one button.
 *
 * Example use:
 *
 * For a normal situation where just one command executes when a game pad button is pushed:
 * declaration
 * public GamepadButtonMultiPush gamepad1x;
 *
 * initialization
 * gamepad1x = new GamepadButtonMultiPush(1)
 *
 * use
 *  if (gamepad1x.buttonPress(gamepad1.x)) {
 *       //this was a new button press, not a button held down for a while
 *       the command goes on this line;
 *  }
 *
 *  For a button that has mulitple commands tied to it
 *
 *  declaration
 * GamepadButtonMultiPush gamepad1x;
 *
 * initialization
 * gamepad1x = new GamepadButtonMultiPush(4);
 *
 * use
 * if (gamepad1x.buttonPress(gamepad1.x)) {
 *     if (gamepad1x.isCommand1()) {
 *         // call the first command you want to run
 *     }
 *     if (gamepad1x.isCommand2()) {
 *         // call the 2nd command you want to run
 *     }
 *     if (gamepad1x.isCommand3()) {
 *         // call the 3rd command you want to run
 *     }
 *     if (gamepad1x.isCommand4()) {
 *         // call the 4th command you want to run
 *     }
 * }
 */
public class GamepadButtonMultiPush {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum ButtonState {
        PRESSED, RELEASED
    }

    private enum CommandState {
        NOCOMMAND, COMMAND1, COMMAND2, COMMAND3, COMMAND4
    }


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private ButtonState currentButtonState;
    private ButtonState previousButtonState;

    private CommandState currentCommandState;
    private CommandState previousCommandState;

    private int numberOfCommandsForThisButton = 1;


    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public GamepadButtonMultiPush(int numberOfCommandsForThisButton) {
        this.numberOfCommandsForThisButton = numberOfCommandsForThisButton;
        currentButtonState = ButtonState.RELEASED;
        previousButtonState = ButtonState.RELEASED;
        currentCommandState = CommandState.NOCOMMAND;
        previousCommandState = CommandState.NOCOMMAND;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Determines if the button press is due to someone holding the button down for a long time or
     * if it really a new press.
     *
     * @return true if this is a new press
     */
    private boolean isButtonPressNew() {
        // The button press is new if the previous time the button was read it was released.
        // ie released then pressed = new press
        // press then press = just a continuation of a press
        if (previousButtonState == ButtonState.RELEASED && currentButtonState == ButtonState.PRESSED) {
            return true;
        } else {
            return false;
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * Process the button. This should be called from the opmode processing the buttons.
     *
     * @param buttonPress true if the button is pressed, false if it is released
     * @return true for a new button press, false if it is not a new press or if it is released
     */
    public boolean buttonPress(boolean buttonPress) {
        // since this ia a new update to the button, the previous state of the button has to be saved
        previousButtonState = currentButtonState;
        // has the button been pressed? true = yes
        if (buttonPress) {
            // the button has been pressed, update the current button state
            currentButtonState = ButtonState.PRESSED;
        } else {
            // the button is released, update the current button state
            currentButtonState = ButtonState.RELEASED;
        }
        if (isButtonPressNew()) {
            // update the state machine that is tracking which command should be executed
            updateCommandState();
            // Is this a button press? It won't be a new press if it was pressed the last time we checked
            // and it is still pressed now
            return true;
        } else {
            return false;
        }
    }

    /**
     * This is a state machine that keeps track of which command should be run next. Commands are run
     * in a cycle starting at command 1 and running up to the max number of commands the user chose
     * when they called the constructor to create this object. Each button press
     * advances the command one spot in the cycle of commands. The command will only advance one
     * spot if there is a new button press.
     * <p>
     * The currentCommandState will hold the next command to be run when this state machine update
     * completes
     */
    private void updateCommandState() {
        // this is a new button press so figure out what command is next
        // Note that the switch is actually testing the command that was just run. IN this case
        // current is not really current, it is the past command.
        switch (currentCommandState) {
            case NOCOMMAND:
                // the previous command was no command at all, the next command should be
                // COMMAND1
                currentCommandState = CommandState.COMMAND1;
                break;
            case COMMAND1:
                if (numberOfCommandsForThisButton > 1) {
                    // there are 2 or more commands for this button, the next command to
                    // be run should be COMMAND2
                    currentCommandState = CommandState.COMMAND2;
                } else {
                    // there is only 1 command for this button, the next command to run should
                    // be COMMAND1
                    currentCommandState = CommandState.COMMAND1;
                }
                break;
            case COMMAND2:
                if (numberOfCommandsForThisButton > 2) {
                    // there are 3 or more commands for this button, the next command to
                    // be run should be COMMAND3
                    currentCommandState = CommandState.COMMAND3;
                } else {
                    // there are only 2 commands for this button, the next command to be run is
                    // COMMAND1 - ie back to the beginning of the cycle
                    currentCommandState = CommandState.COMMAND1;
                }
                break;
            case COMMAND3:
                if (numberOfCommandsForThisButton > 3) {
                    // if there are 4 or more commands for this button, then the next command to
                    // be run should be COMMAND4
                    currentCommandState = CommandState.COMMAND4;
                } else {
                    // there are only 3 commands for this button, the next command to be run is
                    // COMMAND1 - ie back to the beginning of the cycle
                    currentCommandState = CommandState.COMMAND1;
                }
                break;
            case COMMAND4:
                // Since the last command to be run was COMMAND4 and there is a new button press
                // the next command to be run is COMMAND1 - ie back to the beginning of the
                // cycle
                currentCommandState = CommandState.COMMAND1;
                break;
        }
    }

    /**
     * Checks if the next command in the cycle is the 1st command
     * @return true if the command to be run is the 1st one in the cycle
     */
    public boolean isCommand1() {
        if (currentCommandState == CommandState.COMMAND1 && isButtonPressNew()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the next command in the cycle is the 2nd command
     * @return true if the command to be run is the 2nd one in the cycle
     */
    public boolean isCommand2() {
        if (currentCommandState == CommandState.COMMAND2 && isButtonPressNew()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the next command in the cycle is the 3rd command
     * @return true if the command to be run is the 3rd one in the cycle
     */
    public boolean isCommand3() {
        if (currentCommandState == CommandState.COMMAND3 && isButtonPressNew()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the next command in the cycle is the 4th command
     * @return true if the command to be run is the 4th one in the cycle
     */
    public boolean isCommand4() {
        if (currentCommandState == CommandState.COMMAND4 && isButtonPressNew()) {
            return true;
        } else {
            return false;
        }
    }

}
