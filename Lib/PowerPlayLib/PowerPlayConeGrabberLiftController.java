package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class PowerPlayConeGrabberLiftController implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum ControllerState {
        READY,

        // INIT STATES
        PRE_INIT,
        INIT_COMPLETE,

        // lift states
        MOVING_TO_HIGH,
        MOVING_TO_MEDIUM,
        MOVING_TO_LOW,
        MOVING_TO_GROUND,
        MOVING_TO_PICKUP,

        // cone grabber states
        MOVING_TO_RELEASE_POSITION,
        RELEASING_THEN_CARRY,
        MOVING_TO_CLOSE
    }

    private ControllerState controllerState = ControllerState.PRE_INIT;

    private enum Phase {
        TELEOP,
        AUTONOMOUS
    }

    private Phase phase = Phase.TELEOP;

    private boolean commandComplete = true;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private PowerPlayConeGrabber coneGrabber;
    private PowerPlayLeftLift lift;
    private PowerPlayCycleTracker cycleTracker;

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private ElapsedTime timer;

    // flags used in this class
    private boolean coneGrabberPositionReached = false;
    private boolean liftPositionReached = false;

    // initialization is complete
    private boolean initComplete = false;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public PowerPlayConeGrabberLiftController(PowerPlayConeGrabber coneGrabber, PowerPlayLeftLift lift, PowerPlayCycleTracker cycleTracker) {

        this.coneGrabber = coneGrabber;
        this.lift = lift;
        this.cycleTracker = cycleTracker;

        timer = new ElapsedTime();

        controllerState = ControllerState.PRE_INIT;
        // init has not been started yet
        initComplete = false;
        // the lift can be commanded to do something, like the init
        commandComplete = true;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    @Override
    public String getName() {
        return "ConeGrabberLiftController";
    }

    @Override
    public void shutdown() {
        // This is not the hardware so there is nothing to do;
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        enableLogging = true;
    }

    @Override
    public void disableDataLogging() {
        enableLogging = false;
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    private void logState() {
        if (enableLogging && logFile != null) {
            logStateOnChange.log(getName() + " state = " + controllerState.toString());
        }
    }

    private void logCommand(String command) {
        if (enableLogging && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public String getConeGrabberState() {
        return controllerState.toString();
    }

    @Override
    /**
     * Move the arm to the init position, move the cone grabber to the init position.
     */
    public boolean init(Configuration config) {
        // start the init for the extension retraction mechanism
        logCommand("Init starting");
        // There is no direct control of hardware so there is no init. It goes directly to complete.
        commandComplete = false;
        controllerState = ControllerState.INIT_COMPLETE;
        logCommand("Init");
        return false;
    }

    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
    }

    public boolean isPositionReached() {
        if (controllerState == ControllerState.READY) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isCommandComplete() {
        return commandComplete;
    }

    public void setPhaseAutonomous() {
        phase = Phase.AUTONOMOUS;
    }

    public void setPhaseTeleop() {
        phase = Phase.TELEOP;
    }

    //********************************************************************************
    // Public commands for controlling the CONE GRABBER and LIFT - PREPARING TO SCORE
    //********************************************************************************

    public void moveToHighThenPrepareToRelease() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("lift to high, prepare to drop");
            commandComplete = false;
            //command to start lift
            controllerState = ControllerState.MOVING_TO_HIGH;
            lift.moveToHigh();
           cycleTracker.setPhaseOfCycleToScoring();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("lift to high, prepare to drop command ignored");
        }
    }

    public void moveToMediumThenPrepareToRelease() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("lift to medium, prepare to drop");
            commandComplete = false;
            //command to start lift
            controllerState = ControllerState.MOVING_TO_MEDIUM;
            lift.moveToMedium();
           cycleTracker.setPhaseOfCycleToScoring();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("lift to medium, prepare to drop command ignored");
        }
    }

    public void moveToLowThenPrepareToRelease() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("lift to low, prepare to drop");
            commandComplete = false;
            //command to start lift
            controllerState = ControllerState.MOVING_TO_LOW;
            lift.moveToLow();
           cycleTracker.setPhaseOfCycleToScoring();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("lift to low, prepare to drop command ignored");
        }
    }

    public void moveToGroundThenPrepareToRelease() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("lift to ground, prepare to drop");
            commandComplete = false;
            //command to start lift
            controllerState = ControllerState.MOVING_TO_GROUND;
            lift.moveToGround();
           cycleTracker.setPhaseOfCycleToScoring();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("lift to ground, prepare to drop command ignored");
        }
    }

    //********************************************************************************
    // Public commands for controlling the CONE GRABBER and LIFT - SCORING
    //********************************************************************************

    public void releaseThenMoveToPickup() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("drop, lift to pickup");
            commandComplete = false;
            //command to move cone grabber
            controllerState = ControllerState.RELEASING_THEN_CARRY;
            coneGrabber.openThenCarryPosition();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("drop, lift to pickup command ignored");
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////state machine//////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update() {
        // update the positions
        coneGrabberPositionReached = coneGrabber.isPositionReached();
        liftPositionReached = lift.isPositionReached();
        logState();
        switch (controllerState) {
            //********************************************************************************
            // INIT states - just shell for now. Doesn't really do anything.
            //********************************************************************************

            case PRE_INIT: {
                // unlock the commands so a new command will be acted upon
                commandComplete = true;
                // do nothing, waiting to get the init command
            }
            break;

            case INIT_COMPLETE: {
                // do nothing. The lift is waiting for a command
                commandComplete = true;
                initComplete = true;
            }
            break;

            //********************************************************************************
            // lift to position, then ready to drop states
            //********************************************************************************
            case MOVING_TO_HIGH:
            case MOVING_TO_MEDIUM:
            case MOVING_TO_LOW:
            case MOVING_TO_GROUND: {
                if (liftPositionReached) {
                    commandComplete = false;
                    coneGrabber.releasePosition();
                    controllerState = ControllerState.MOVING_TO_RELEASE_POSITION;
                }
            }
            break;
            
            case MOVING_TO_RELEASE_POSITION: {
                if (coneGrabberPositionReached) {
                    commandComplete = true;
                    controllerState = ControllerState.READY;
                }
            }
            break;

            //********************************************************************************
            // DROP, then lift to pickup position and cone grabber closed
            //********************************************************************************

            case RELEASING_THEN_CARRY: {
                if (coneGrabberPositionReached) {
                    // allow another command. The drivers want to put the arm down to prep for a
                    // cone pickup. This will allow them to do it.
                    commandComplete = true;
                    // close the cone grabber in preparation for lining up over the cone
                    coneGrabber.close();
                    // at the same time lower the lift to the pickup position
                    lift.moveToPickup();
                    controllerState = ControllerState.MOVING_TO_PICKUP;
                }
            }
            break;

            case MOVING_TO_PICKUP: {
                if (liftPositionReached && coneGrabberPositionReached) {
                    commandComplete = true;
                    controllerState = ControllerState.READY;
                }
            }
            break;
        }
    }
}
