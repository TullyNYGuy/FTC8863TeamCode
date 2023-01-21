package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class PowerPlayThrottleController implements FTCRobotSubsystem {

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

        LOOKING_FOR_CONE,

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

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private ElapsedTime timer;

    // initialization is complete
    private boolean initComplete = false;

    private final String CONE_GRABBER_SERVO_NAME = PowerPlayRobot.HardwareName.CONE_GRABBER_SERVO.hwName;
    // Rev 2m distance sensor
    private DistanceSensor distanceSensor;
    private Rev2mDistanceSensor rev2mDistanceSensor;
    private double distance;

    private PowerPlayGamepad gamepad;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public PowerPlayThrottleController(HardwareMap hardwareMap, Telemetry telemetry, PowerPlayGamepad gamepad) {
        this.gamepad = gamepad;
        distanceSensor = hardwareMap.get(DistanceSensor.class, "sensor_range");
        // cast the distance sensor to a rev 2m distance sensor
        rev2mDistanceSensor = (Rev2mDistanceSensor)distanceSensor;

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
        return "ThrottleController";
    }

    @Override
    public void shutdown() {
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
    // Public commands for controlling throttle
    //********************************************************************************

    public void start() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("start");
            commandComplete = false;
            //command to start lift
            controllerState = ControllerState.LOOKING_FOR_CONE;
            // reduce max speed of joysticks
            gamepad.setMaxDrivingPower(.50);
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("start command ignored");
        }
    }

    public void reset() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("reset");
            commandComplete = false;
            //command to start lift
            controllerState = ControllerState.READY;
            // reduce max speed of joysticks
            gamepad.setMaxDrivingPower(gamepad.getPreviousMaxPower());
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("reset command ignored");
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////state machine//////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update() {
        // update the positions
        distance = distanceSensor.getDistance(DistanceUnit.CM);
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
                // do nothing.
                commandComplete = true;
                initComplete = true;
            }
            break;

            case LOOKING_FOR_CONE: {

            }
            break;

        }
    }
}
