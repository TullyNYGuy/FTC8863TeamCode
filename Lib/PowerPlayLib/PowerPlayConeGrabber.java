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

        // moving states
        MOVING_TO_CARRY,

        MOVING_TO_OPEN,
        MOVING_TO_READY_TO_GRAB,

        MOVING_TO_GRAB,
        MOVING_TO_READY_TO_RELEASE,

        MOVING_TO_RELEASE,
        MOVING_TO_POST_RELEASE
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
        coneGrabberServo.store();
        coneGrabberArmServo.store();
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

    /**
     * Move the arm to carry position
     */
    public void carry() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("cone grabber to carry");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_CARRY;
            coneGrabberArmServo.carry();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("Carry command ignored");
        }
    }

    /**
     * Open the grabber, then move the arm to the position where it can grab
     */
    public void readyToGrab() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("cone grabber to ready to grab");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_OPEN;
            coneGrabberServo.open();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("ready to grab command ignored");
        }
    }

    /**
     * Close the grabber
     */
    public void grab() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("cone grabber to grab");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_GRAB;
            coneGrabberServo.close();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("grab command ignored");
        }
    }

    /**
     * Move the arm to position where it can release the cone, but still hold the cone
     */
    public void readyToRelease() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("cone grabber to ready to release");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_READY_TO_RELEASE;
            coneGrabberArmServo.grabOrRelease();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("ready to release command ignored");
        }
    }

    /**
     * Release the cone, then move the arm to the carry position
     */
    public void release() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("cone grabber to release");
            commandComplete = false;
            //command to start extension
            coneGrabberState = ConeGrabberState.MOVING_TO_RELEASE;
            coneGrabberServo.open();
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("ready to grab command ignored");
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
            // carry states
            //********************************************************************************

            case MOVING_TO_CARRY: {
                if (coneGrabberArmServo.isPositionReached()) {
                    commandComplete = true;
                    coneGrabberState = ConeGrabberState.READY;
                }
            }
            break;

            //********************************************************************************
            // ready to grab states
            //********************************************************************************

            case MOVING_TO_OPEN: {
                if (coneGrabberServo.isPositionReached()) {
                    coneGrabberArmServo.grabOrRelease();
                    coneGrabberState = ConeGrabberState.MOVING_TO_READY_TO_GRAB;
                }
            }
            break;

            case MOVING_TO_READY_TO_GRAB: {
                if (coneGrabberArmServo.isPositionReached()) {
                    commandComplete = true;
                    coneGrabberState = ConeGrabberState.READY;
                }
            }
            break;

            //********************************************************************************
            // grab states
            //********************************************************************************

            case MOVING_TO_GRAB: {
                if (coneGrabberServo.isPositionReached()) {
                    commandComplete = true;
                    coneGrabberState = ConeGrabberState.READY;
                }
            }
            break;

            //********************************************************************************
            // ready to release states
            //********************************************************************************

            case MOVING_TO_READY_TO_RELEASE: {
                if (coneGrabberArmServo.isPositionReached()) {
                    commandComplete = true;
                    coneGrabberState = ConeGrabberState.READY;
                }
            }
            break;

            //********************************************************************************
            // release states
            //********************************************************************************

            case MOVING_TO_RELEASE: {
                if (coneGrabberServo.isPositionReached()) {
                    coneGrabberArmServo.carry();
                    coneGrabberState = ConeGrabberState.MOVING_TO_POST_RELEASE;
                }
            }
            break;

            case MOVING_TO_POST_RELEASE: {
                if (coneGrabberArmServo.isPositionReached()) {
                    commandComplete = true;
                    coneGrabberState = ConeGrabberState.READY;
                }
            }
            break;
        }
    }
}
