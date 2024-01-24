package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;


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

public class CenterStageLift implements FTCRobotSubsystem {

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
        INIT_COMPLETE,

        // Moving to states
        MOVING_TO_HIGH,
        MOVING_TO_MEDIUM,
        MOVING_TO_LOW,
        MOVING_TO_INTAKE,

        // retraction states
        RETRACTING
    }

    private enum Phase {
        TELEOP,
        AUTONOMOUS
    }

    private LiftState liftState = LiftState.PRE_INIT;

    // so we can remember which extend command was given
    private enum Command {
        NONE,
        MOVE_TO_HIGH,
        MOVE_TO_MEDIUM,
        MOVE_TO_LOW,
        MOVE_TO_INTAKE,
        RETRACT
    }

    private Command command = Command.NONE;

    private boolean commandComplete = true;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private final String LIFT_NAME = CenterStageRobot.HardwareName.LIFT.hwName;

    private ExtensionRetractionMechanism extensionRetractionMechanism;
    private DcMotor8863Interface liftMotor;
    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private ElapsedTime timer;

    // flags used in this class

    // initialization is complete
    private boolean initComplete = false;
    // arm is fully retracted
    private boolean retractionComplete = true;

    //extension arm positions
    private double highPosition;
    private double mediumPosition;
    private double lowPosition;
    private double intakePosition;
    private double initPosition;
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
    public CenterStageLift(HardwareMap hardwareMap, Telemetry telemetry) {

        // create the motor for the lift
        liftMotor = new DcMotor8863(CenterStageRobot.HardwareName.LIFT_MOTOR.hwName, hardwareMap, telemetry);
        liftMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_435);

        extensionRetractionMechanism = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                "extensionRetractionMechanism",
                CenterStageRobot.HardwareName.LIFT_LIMIT_SWITCH_EXTENSION.hwName,
                CenterStageRobot.HardwareName.LIFT_LIMIT_SWITCH_RETRACTION.hwName,
                CenterStageRobot.HardwareName.LIFT_MOTOR.hwName,
                DcMotor8863.MotorType.GOBILDA_435,
                4.75);
        extensionRetractionMechanism.forwardMotorDirection();

        //*********************************************
        // SET the lift positions here
        //*********************************************
        // todo figure out the positions
        highPosition = 15.0;
        mediumPosition = 0.0;
        lowPosition = 5.0;
        intakePosition = 0.05;
        initPosition = 0;

        //*********************************************
        // SET the lift powers here
        //*********************************************
        initPower = .2;
        extendPower = 1.0;
        retractPower = -1.0;
        extensionRetractionMechanism.setExtensionPower(extendPower);
        extensionRetractionMechanism.setRetractionPower(retractPower);

        //*********************************************
        // SET the lift max and min positions here
        //*********************************************
        extensionRetractionMechanism.setExtensionPositionInMechanismUnits(16.5);
        extensionRetractionMechanism.setRetractionPositionInMechanismUnits(0.04);
        // Go with standard encoder tolerance for now
        //extensionRetractionMechanism.setTargetEncoderTolerance(30);

        extensionRetractionMechanism.setResetTimerLimitInmSec(5000);
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
        return LIFT_NAME;
    }

    @Override
    public void shutdown() {
        //todo figure out what to do for shutdown
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
        extensionRetractionMechanism.setDataLog(logFile);
    }

    @Override
    public void enableDataLogging() {
        enableLogging = true;
        extensionRetractionMechanism.enableDataLogging();
    }

    @Override
    public void disableDataLogging() {
        enableLogging = false;
        extensionRetractionMechanism.disableDataLogging();
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
        extensionRetractionMechanism.init();
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
     public boolean isliftRetractionLimitSwitchPressed() {
        return isliftRetractionLimitSwitchPressed();
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
            command = Command.MOVE_TO_HIGH;
            logCommand(command.toString());
            extensionRetractionMechanism.goToPosition(highPosition, extendPower);
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
            command = Command.MOVE_TO_MEDIUM;
            logCommand(command.toString());
            extensionRetractionMechanism.goToPosition(mediumPosition, extendPower);
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
            command = Command.MOVE_TO_LOW;
            logCommand(command.toString());
            extensionRetractionMechanism.goToPosition(lowPosition, extendPower);
        } else {
            // you can't start a new command when the old one is not finished
        }
    }

    public void moveToIntake() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to intake");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.MOVING_TO_INTAKE;
            // remember the command for later
            command = Command.MOVE_TO_INTAKE;
            logCommand(command.toString());
            extensionRetractionMechanism.goToPosition(intakePosition, extendPower);
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
            command = Command.RETRACT;
            logCommand(command.toString());
            extensionRetractionMechanism.goToPosition(intakePosition, retractPower);
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
        extensionRetractionMechanism.goToPosition(position, power);
    }

    public boolean isMovementComplete() {
        return extensionRetractionMechanism.isMovementComplete();
    }

    public boolean isCommandComplete() {
        return commandComplete;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////state machine//////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update() {
        extensionRetractionMechanism.update();
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
                if (extensionRetractionMechanism.isInitComplete()) {
                    // working around bug where we command the lift to 0 and it locks up
                    // instead just leave the lift at its init position
                    //lift.goToPosition(initPosition, initPower);
                    //liftState = LiftState.WAITING_FOR_EXTENSION_ARM_HOME_POSITION_REACHED;
                    commandComplete = true;
                    initComplete = true;
                    liftState = LiftState.INIT_COMPLETE;
                }
            }
            break;

            case INIT_COMPLETE: {
                // do nothing. The lift is waiting for a command
            }
            break;

            case READY:
                // do nothing. The lift is waiting for a command
                break;

            //********************************************************************************
            // Extend to high states
            //********************************************************************************

            case MOVING_TO_HIGH: {
                if (extensionRetractionMechanism.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
            break;

            //********************************************************************************
            // Extend to medium states
            //********************************************************************************

            case MOVING_TO_MEDIUM: {
                if (extensionRetractionMechanism.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
            break;

            //********************************************************************************
            // Extend to low states
            //********************************************************************************

            case MOVING_TO_LOW: {
                if (extensionRetractionMechanism.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
            break;

            //********************************************************************************
            // Extend to INTAKE states
            //********************************************************************************
            
            case MOVING_TO_INTAKE: {
                if (extensionRetractionMechanism.isPositionReached()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                }
            }
            break;
        }
    }
}
