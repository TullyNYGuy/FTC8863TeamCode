package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.robocol.Command;

import org.firstinspires.ftc.robotcontroller.external.samples.PushbotAutoDriveByEncoder_Linear;

import java.net.PortUnreachableException;

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

    private boolean isButtonPressNew() {
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

    public boolean buttonPress(boolean buttonState) {
        // since this ia a new update to the button, the previous state of the button has to be saved
        previousButtonState = currentButtonState;
        // has the button been pressed? true = yes
        if (buttonState) {
            currentButtonState = ButtonState.PRESSED;
        } else {
            currentButtonState = ButtonState.RELEASED;
        }
        // update the command state
        updateCommandState();
        // Is this a button press? It won't be a new press if it was pressed the last time we checked
        // and it is still pressed now
        return isButtonPressNew();
    }

    private void updateCommandState() {

        if (!isButtonPressNew()) {
            // there is not a new button press, so no command should be run
            // if the current command is COMMAND1-4, then save it so we can use it later to see what
            // the last command was
            if (currentCommandState != CommandState.NOCOMMAND) {
                previousCommandState = currentCommandState;
            }
            currentCommandState = CommandState.NOCOMMAND;
        } else {
            // this is a new button press so figure out what command is next
            switch (previousCommandState) {
                case NOCOMMAND:
                    // if the previous command was no command at all, the new command should be
                    // COMMAND1
                    currentCommandState = CommandState.COMMAND1;
                    break;
                case COMMAND1:
                    if (numberOfCommandsForThisButton > 1) {
                        // if there are 2 or more commands for this button, then the next command to
                        // be run should be COMMAND2
                        currentCommandState = CommandState.COMMAND2;
                    } else {
                        // if there is only 1 command for this button, then the next command should
                        // remain at COMMAND1
                        currentCommandState = CommandState.COMMAND1;
                    }
                    break;
                case COMMAND2:
                    if (numberOfCommandsForThisButton > 2) {
                        // if there are 3 or more commands for this button, then the next command to
                        // be run should be COMMAND3
                        currentCommandState = CommandState.COMMAND3;
                    } else {
                        // if there is only 2 commands for this button, then the next command should
                        // swtich back to COMMAND1
                        currentCommandState = CommandState.COMMAND1;
                    }
                    break;
                case COMMAND3:
                    if (numberOfCommandsForThisButton > 3) {
                        // if there are 4 or more commands for this button, then the next command to
                        // be run should be COMMAND4
                        currentCommandState = CommandState.COMMAND4;
                    } else {
                        // if there is only 3 commands for this button, then the next command should
                        // swtich back to COMMAND1
                        currentCommandState = CommandState.COMMAND1;
                    }
                    break;
                case COMMAND4:
                    // Since the last command to be run was COMMAND4 and there is a new button press
                    // the next command to be run is COMMAND1
                    currentCommandState = CommandState.COMMAND1;
                    break;
            }
        }

    }

    public boolean isCommand1OK() {
        if (currentCommandState == CommandState.COMMAND1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isCommand2OK() {
        if (currentCommandState == CommandState.COMMAND2) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isCommand3OK() {
        if (currentCommandState == CommandState.COMMAND3) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isCommand4OK() {
        if (currentCommandState == CommandState.COMMAND4) {
            return true;
        } else {
            return false;
        }
    }

}
