package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863Interface;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanismGenericMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class PowerPlayDualMotorLift implements FTCRobotSubsystem {

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
        INIT_RAISE_LIFT,
        WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE,
        WAITING_FOR_EXTENSION_ARM_HOME_POSITION_REACHED,
        INIT_COMPLETE,

        // Moving to states
        MOVING_TO_HIGH,
        MOVING_TO_MEDIUM,
        MOVING_TO_LOW,
        MOVING_TO_GROUND,
        MOVING_TO_PICKUP,
        DROPPING_ON_POLE,

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

    private ExtensionRetractionMechanismGenericMotor lift;
    private DcMotor8863Interface liftMotor;
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
    public PowerPlayDualMotorLift(HardwareMap hardwareMap, Telemetry telemetry) {

        // create the motor for the lift
        liftMotor = new DcMotor8863(PowerPlayRobot.HardwareName.LEFT_LIFT_MOTOR.hwName, hardwareMap, telemetry);
        liftMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_435);

        lift = new ExtensionRetractionMechanismGenericMotor(hardwareMap, telemetry,
                "lift",
                PowerPlayRobot.HardwareName.LEFT_LIFT_LIMIT_SWITCH_EXTENSION.hwName,
                PowerPlayRobot.HardwareName.LEFT_LIFT_LIMIT_SWITCH_RETRACTION.hwName,
                liftMotor,
                5.713);
        lift.forwardMotorDirection();

        //*********************************************
        // SET the lift positions here
        //*********************************************
        highPosition = 34.25;
        mediumPosition = 24.0;
        lowPosition = 14.75;
        groundPosition = 2.0;
        pickupPosition = 1.0;
        initPosition = 0;
        homePosition = 0.5;

        //*********************************************
        // SET the lift powers here
        //*********************************************
        initPower = .2;
        extendPower = 1.0;
        retractPower = -1.0;
        lift.setExtensionPower(extendPower);
        lift.setRetractionPower(retractPower);

        //*********************************************
        // SET the lift max and min positions here
        //*********************************************
        lift.setExtensionPositionInMechanismUnits(36.0);
        lift.setRetractionPositionInMechanismUnits(0.04);
        lift.setTargetEncoderTolerance(30);

        lift.setResetTimerLimitInmSec(5000);
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
        return "lift";
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
        lift.setDataLog(logFile);
    }

    @Override
    public void enableDataLogging() {
        enableLogging = true;
        lift.enableDataLogging();
    }

    @Override
    public void disableDataLogging() {
        enableLogging = false;
        lift.disableDataLogging();
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
        lift.init();
        liftState = LiftState.WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE;

        commandComplete = false;
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
        if (liftState == LiftState.READY) {
            return true;
        } else {
            return false;
        }
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
            lift.goToPosition(highPosition, extendPower);
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
            lift.goToPosition(mediumPosition, extendPower);
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
            lift.goToPosition(lowPosition, extendPower);
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
            lift.goToPosition(groundPosition, extendPower);
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
            lift.goToPosition(pickupPosition, extendPower);
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void droppingOnPole() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        double currentLiftPosition = lift.getCurrentPosition();
        if (commandComplete) {
            logCommand("test the drop");
            retractionComplete = false;
            commandComplete = false;
            lift.goToPosition(currentLiftPosition - 4.0, retractPower);
            liftState = LiftState.DROPPING_ON_POLE;
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
            lift.goToPosition(pickupPosition, retractPower);
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
        lift.goToPosition(position, power);
    }

    public boolean isMovementComplete() {
        return lift.isMovementComplete();
    }

    public boolean isCommandComplete() {
        return commandComplete;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////state machine//////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update() {
        lift.update();
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

            case INIT_RAISE_LIFT: {
                if (lift.isPositionReached()) {
                    lift.init();
                    liftState = LiftState.WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE;
                }
            }
            break;

            case WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE: {
                if (lift.isInitComplete()) {
                    // working around bug where we command the lift to 0 and it locks up
                    // instead just leave the lift at its init position
                    //lift.goToPosition(initPosition, initPower);
                    //liftState = LiftState.WAITING_FOR_EXTENSION_ARM_HOME_POSITION_REACHED;
                    liftState = LiftState.INIT_COMPLETE;
                }
            }
            break;

            case WAITING_FOR_EXTENSION_ARM_HOME_POSITION_REACHED: {
                if (lift.isPositionReached()) {
                    liftState = LiftState.INIT_COMPLETE;
                    commandComplete = true;
                    initComplete = true;
                }
            }
            break;

            case INIT_COMPLETE: {
                liftState = LiftState.INIT_COMPLETE;
                commandComplete = true;
                initComplete = true;
                // do nothing. The lift is waiting for a command
            }
            break;

            //********************************************************************************
            // Extend to high states
            //********************************************************************************

            case MOVING_TO_HIGH: {
                if (lift.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
            break;

            //********************************************************************************
            // Extend to medium states
            //********************************************************************************

            case MOVING_TO_MEDIUM: {
                if (lift.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
            break;

            //********************************************************************************
            // Extend to low states
            //********************************************************************************

            case MOVING_TO_LOW: {
                if (lift.isPositionReached()) {
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
                if (lift.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
            break;

            //********************************************************************************
            // Extend to pickup states
            //********************************************************************************

            case MOVING_TO_PICKUP: {
                if (lift.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
            break;

            case DROPPING_ON_POLE: {
                if (lift.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
        }
    }
}