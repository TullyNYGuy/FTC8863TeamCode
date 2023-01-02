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
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayAllianceColor;

public class PowerPlayLeftLift implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum LiftState {
        READY,

        // INIT STATES
        PRE_INIT,
        WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE,
        WAITING_FOR_EXTENSION_ARM_HOME_POSITION_REACHED,
        WAITING_FOR_BUCKET_INIT_POSITION_REACHED,
        INIT_COMPLETE,

        // Moving to states
        MOVING_TO_HIGH,
        MOVING_TO_MEDIUM,
        MOVING_TO_LOW,
        MOVING_TO_GROUND,
        MOVING_TO_PICKUP,

        // retraction states
        RETRACTING
    }

    private enum Phase {
        TELEOP,
        AUTONOMOUS
    }

    private LiftState liftState = LiftState.PRE_INIT;

    // so we can remember which extend command was given
    private enum ExtendCommand {
        NONE,
        MOVE_TO_HIGH,
        MOVE_TO_MEDIUM,
        MOVE_TO_LOW,
        MOVE_TO_GROUND,
        MOVE_TO_PICKUP,
        RETRACT
    }

    private ExtendCommand extendCommand = ExtendCommand.NONE;

    private boolean commandComplete = true;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private ExtensionRetractionMechanism leftLift;
    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private ElapsedTime timer;
    private PowerPlayAllianceColor allianceColor;

    // flags used in this class

    // initialization is complete
    private boolean initComplete = false;
    // arm is fully retracted
    private boolean retractionComplete = true;

    //extension arm positions
    private double highPosition;
    private double mediumPosition;
    private double lowPosition;
    private double groundPosition;
    private double pickupPosition;
    private double initPosition;
    private double homePosition;
    private double initPower;
    private double extendPower;
    private double retractPower;

    private Phase phase = Phase.TELEOP;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public PowerPlayLeftLift(HardwareMap hardwareMap, Telemetry telemetry) {

        leftLift = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                "lift",
                PowerPlayRobot.HardwareName.LEFT_LIFT_LIMIT_SWITCH_EXTENSION.hwName,
                PowerPlayRobot.HardwareName.LEFT_LIFT_LIMIT_SWITCH_RETRACTION.hwName,
                PowerPlayRobot.HardwareName.LEFT_LIFT_MOTOR.hwName,
                DcMotor8863.MotorType.GOBILDA_435,
                5.713);
        leftLift.forwardMotorDirection();
        
        //*********************************************
        // SET the lift positions here
        //*********************************************
        highPosition = 27.7;
        mediumPosition = 12;
        lowPosition = 6;
        groundPosition = 2;
        pickupPosition = 0.5;
        initPosition = 0.1;
        homePosition = 0.5;

        //*********************************************
        // SET the lift powers here
        //*********************************************
        initPower = 0.3;
        extendPower = 0.9;
        retractPower = -0.5;
        leftLift.setExtensionPower(extendPower);
        leftLift.setRetractionPower(retractPower);
        
        //*********************************************
        // SET the lift max and min positions here
        //*********************************************
        leftLift.setExtensionPositionInMechanismUnits(31.0);
        leftLift.setRetractionPositionInMechanismUnits(0.5);

        leftLift.setResetTimerLimitInmSec(5000);
        timer = new ElapsedTime();

        liftState = LiftState.PRE_INIT;
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

    public boolean isRetractionComplete() {
        return retractionComplete;
    }

    @Override
    public String getName() {
        return "LeftLift";
    }

    @Override
    public void shutdown() {
        retract();
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
        leftLift.setDataLog(logFile);
    }

    @Override
    public void enableDataLogging() {
        enableLogging = true;
        leftLift.enableDataLogging();
    }

    @Override
    public void disableDataLogging() {
        enableLogging = false;
        leftLift.disableDataLogging();
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    private void logState() {
        if (enableLogging && logFile != null) {
            logStateOnChange.log(getName() + " state = " + liftState.toString());
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
    public String getLiftState() {
        return liftState.toString();
    }

    @Override
    public boolean init(Configuration config) {
        // start the init for the extension retraction mechanism
        logCommand("Init starting");
        // start the init of the extension retraction mechanism
        leftLift.init();
        commandComplete = false;
        liftState = LiftState.WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE;
        logCommand("Init");
        return false;
    }

    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
    }

    public void setPhaseAutonomous() {
        phase = Phase.AUTONOMOUS;
    }

    public void setPhaseTeleop() {
        phase = Phase.TELEOP;
    }

    //********************************************************************************
    // Public commands for controlling the lift
    //********************************************************************************

    public void moveToHigh() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to high");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.MOVING_TO_HIGH;
            // remember the command for later
            extendCommand = ExtendCommand.MOVE_TO_HIGH;
            logCommand(extendCommand.toString());
            leftLift.goToPosition(highPosition, extendPower);
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void moveToMedium() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to medium");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.MOVING_TO_MEDIUM;
            // remember the command for later
            extendCommand = ExtendCommand.MOVE_TO_MEDIUM;
            logCommand(extendCommand.toString());
            leftLift.goToPosition(mediumPosition, extendPower);
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void moveToLow() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to Low");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.MOVING_TO_LOW;
            // remember the command for later
            extendCommand = ExtendCommand.MOVE_TO_LOW;
            logCommand(extendCommand.toString());
            leftLift.goToPosition(lowPosition, extendPower);
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void moveToGround() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to ground");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.MOVING_TO_GROUND;
            // remember the command for later
            extendCommand = ExtendCommand.MOVE_TO_GROUND;
            logCommand(extendCommand.toString());
            leftLift.goToPosition(groundPosition, extendPower);
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void moveToPickup() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to pickup");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.MOVING_TO_PICKUP;
            // remember the command for later
            extendCommand = ExtendCommand.MOVE_TO_PICKUP;
            logCommand(extendCommand.toString());
            leftLift.goToPosition(pickupPosition, extendPower);
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void retract() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Retract");
            retractionComplete = false;
            commandComplete = false;
            liftState = LiftState.RETRACTING;
            extendCommand = ExtendCommand.RETRACT;
            logCommand(extendCommand.toString());
            leftLift.goToPosition(pickupPosition, retractPower);
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("Retract command ignored");
        }
    }


    //********************************************************************************
    // Public commands for testing the lift
    //********************************************************************************

    public boolean isStateIdle() {
        //this is just for use in the freight system.
        if (liftState == LiftState.READY) {
            return true;
        } else {
            return false;
        }
    }

    public void extendToPosition(double position, double power) {
        leftLift.goToPosition(position, power);
    }

    public boolean isMovementComplete() {
        return leftLift.isMovementComplete();
    }

    public boolean isCommandComplete() {
        return commandComplete;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////state machine//////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update() {
        leftLift.update();
        logState();
        switch (liftState) {
            //********************************************************************************
            // INIT states
            //********************************************************************************

            case PRE_INIT: {
                // unlock the commands so a new command will be acted upon
                commandComplete = true;
                // do nothing, waiting to get the init command
            }
            break;

            case WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE: {
                if (leftLift.isInitComplete()) {
                    leftLift.goToPosition(initPosition, initPower);
                    liftState = LiftState.WAITING_FOR_EXTENSION_ARM_HOME_POSITION_REACHED;
                }
            }
            break;

            case WAITING_FOR_EXTENSION_ARM_HOME_POSITION_REACHED: {
                if (leftLift.isPositionReached()) {
                    liftState = LiftState.INIT_COMPLETE;
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
            // Extend to high states
            //********************************************************************************

            case MOVING_TO_HIGH: {
                if (leftLift.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
            break;

            //********************************************************************************
            // Extend to medium states
            //********************************************************************************

            case MOVING_TO_MEDIUM: {
                if (leftLift.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
            break;

            //********************************************************************************
            // Extend to low states
            //********************************************************************************

            case MOVING_TO_LOW: {
                if (leftLift.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
            break;

            //********************************************************************************
            // Extend to ground states
            //********************************************************************************

            // same as middle right now but may need to change later so make it separate from middle
            case MOVING_TO_GROUND: {
                if (leftLift.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
            break;

            //********************************************************************************
            // Extend to pickup states
            //********************************************************************************

            case MOVING_TO_PICKUP: {
                if (leftLift.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
            break;
        }
    }
}
