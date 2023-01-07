package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class PowerPlayConeGrabber implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum ConeGrabberState {
        READY,

        // INIT STATES
        PRE_INIT,
        WAITING_FOR_CONE_GRABBER_INIT_TO_COMPLETE,
        INIT_COMPLETE,

        // cone grabber servo states
        MOVING_TO_OPEN,
        MOVING_TO_CLOSE,

        // arm position states
        MOVING_TO_CARRY,
        MOVING_TO_LINEUP_FOR_PICKUP,
        MOVING_TO_PICKUP,
        MOVING_TO_RELEASE,

        // combination of arm position and grabber movements
        MOVING_TO_CLOSE_BEFORE_LINEUP_FOR_PICKUP, // line up the arm above the code so driver can see if he is positioned correctly
        MOVING_TO_OPEN_BEFORE_PICKUP, // prepare for a pickup
        MOVING_TO_CLOSE_BEFORE_CARRY, // pickup a cone
        MOVING_TO_OPEN_BEFORE_CARRY // release a cone
    }

    private ConeGrabberState coneGrabberState = ConeGrabberState.PRE_INIT;

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

    private ConeGrabberServo coneGrabberServo;
    private ConeGrabberArmServo coneGrabberArmServo;

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private ElapsedTime timer;
    private PowerPlayAllianceColor allianceColor;

    // flags used in this class
    private boolean coneGrabberServoPositionReached = false;
    private boolean coneGrabberArmServoPositionReached = false;

    // initialization is complete
    private boolean initComplete = false;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public PowerPlayConeGrabber(HardwareMap hardwareMap, Telemetry telemetry) {

        coneGrabberServo = new ConeGrabberServo(hardwareMap, telemetry);
        coneGrabberArmServo = new ConeGrabberArmServo(hardwareMap, telemetry);

        timer = new ElapsedTime();

        coneGrabberState = ConeGrabberState.PRE_INIT;
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
        return "ConeGrabber";
    }

    @Override
    public void shutdown() {
        coneGrabberServo.close();
        coneGrabberArmServo.carryPosition();
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
            logStateOnChange.log(getName() + " state = " + coneGrabberState.toString());
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
        return coneGrabberState.toString();
    }

    @Override
    /**
     * Move the arm to the init position, move the cone grabber to the init position.
     */
    public boolean init(Configuration config) {
        // start the init for the extension retraction mechanism
        logCommand("Init starting");
        // start the init of the extension retraction mechanism
        coneGrabberServo.init();
        coneGrabberArmServo.init();
        commandComplete = false;
        coneGrabberState = ConeGrabberState.WAITING_FOR_CONE_GRABBER_INIT_TO_COMPLETE;
        logCommand("Init");
        return false;
    }

    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
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
    // Public commands for controlling the CONE GRABBER
    //********************************************************************************

    public void open() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("cone grabber open");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_OPEN;
            coneGrabberServo.open();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("open command ignored");
        }
    }

    /**
     * Close the grabber
     */
    public void close() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("cone grabber close");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_CLOSE;
            coneGrabberServo.close();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("close command ignored");
        }
    }

    /**
     * Move the arm to carry position (carry the cone around while driving)
     */
    public void carryPosition() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("grabber to carry position");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_CARRY;
            coneGrabberArmServo.carryPosition();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("Carry position command ignored");
        }
    }

    /**
     * Move the arm to release position (where you can release the cone)
     */
    public void releasePosition() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("cone grabber to release position");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_RELEASE;
            coneGrabberArmServo.releasePosition();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("Release position command ignored");
        }
    }

    /**
     * Move the arm to position where it can pickup the cone
     */
    public void pickupPosition() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("cone grabber to pickup position");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_PICKUP;
            coneGrabberArmServo.pickupPosition();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("Pickup position command ignored");
        }
    }

    /**
     * Open the grabber, then move the arm to the position where it can grab.
     * This prepares for a pickup.
     */
    public void closeThenLineupForPickupPosition() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("cone grabber close then line up for pickup position");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_CLOSE_BEFORE_LINEUP_FOR_PICKUP;
            coneGrabberServo.close();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("cone grabber close then lineup for pickup command ignored");
        }
    }

    /**
     * Open the grabber, then move the arm to the position where it can grab.
     * This prepares for a pickup.
     */
    public void openThenPickupPosition() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("cone grabber open then pickup position");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_OPEN_BEFORE_PICKUP;
            coneGrabberServo.open();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("cone grabber open then pickup command ignored");
        }
    }

    /**
     * Release the cone, then move the arm to the carry position.
     * drop the cone
     * This prepares to lower the lift after releasing the cone.
     */
    public void openThenCarryPosition() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("cone grabber open then to carry position");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_OPEN_BEFORE_CARRY;
            coneGrabberServo.open();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("cone grabber open then to carry command ignored");
        }
    }

    /**
     * Pickup the cone, then move the arm to the carry position.
     * This prepares for driving after a pickup.
     */
    public void closeThenCarryPosition() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("cone grabber close then to carry position");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_CLOSE_BEFORE_CARRY;
            coneGrabberServo.close();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("cone grabber close then to carry command ignored");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////state machine//////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update() {
        // update the servos
        coneGrabberServoPositionReached = coneGrabberServo.isPositionReached();
        coneGrabberArmServoPositionReached = coneGrabberArmServo.isPositionReached();
        logState();
        switch (coneGrabberState) {
            //********************************************************************************
            // INIT states
            //********************************************************************************

            case PRE_INIT: {
                // unlock the commands so a new command will be acted upon
                commandComplete = true;
                // do nothing, waiting to get the init command
            }
            break;

            case WAITING_FOR_CONE_GRABBER_INIT_TO_COMPLETE: {
                if (coneGrabberServoPositionReached && coneGrabberArmServoPositionReached) {
                    coneGrabberState = ConeGrabberState.INIT_COMPLETE;
                    commandComplete = true;
                    initComplete = true;
                }
            }
            break;

            case INIT_COMPLETE: {
                // do nothing. The lift is waiting for a command
            }
            break;

            //********************************************************************************
            // cone grabber movement only states
            //********************************************************************************
            case MOVING_TO_OPEN:
            case MOVING_TO_CLOSE: {
                if (coneGrabberServo.isPositionReached()) {
                    commandComplete = true;
                    coneGrabberState = ConeGrabberState.READY;
                }
            }
            break;

            //********************************************************************************
            // arm movement only states
            //********************************************************************************

            case MOVING_TO_CARRY:
            case MOVING_TO_RELEASE:
            case MOVING_TO_LINEUP_FOR_PICKUP:
            case MOVING_TO_PICKUP: {
                if (coneGrabberArmServo.isPositionReached()) {
                    commandComplete = true;
                    coneGrabberState = ConeGrabberState.READY;
                }
            }
            break;

            //********************************************************************************
            // combination of arm movement and grabber movement states
            //********************************************************************************

            case MOVING_TO_CLOSE_BEFORE_LINEUP_FOR_PICKUP: {
                if (coneGrabberServo.isPositionReached()) {
                    coneGrabberArmServo.lineupForPickupPosition();
                    coneGrabberState = ConeGrabberState.MOVING_TO_LINEUP_FOR_PICKUP;
                }
            }
            break;

            case MOVING_TO_OPEN_BEFORE_PICKUP: {
                if (coneGrabberServo.isPositionReached()) {
                    coneGrabberArmServo.pickupPosition();
                    coneGrabberState = ConeGrabberState.MOVING_TO_PICKUP;
                }
            }
            break;

            case MOVING_TO_OPEN_BEFORE_CARRY:
            case MOVING_TO_CLOSE_BEFORE_CARRY: {
                if (coneGrabberServo.isPositionReached()) {
                    coneGrabberArmServo.carryPosition();
                    coneGrabberState = ConeGrabberState.MOVING_TO_CARRY;
                }
            }
            break;
        }
    }
}
