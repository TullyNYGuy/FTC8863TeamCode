package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class CenterStagePixelGrabberLeft implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum Command {
        ON,
        RELEASE_PIXEL,
        OFF
    }
    private Command command = Command.OFF;

    public enum State {
        PRE_INIT,
        READY_AND_WAITING,
        CLOSING,
        PIXEL_CHECK,
        PIXEL_GRABBED,
        OPENING,
        RELEASING
    }
    private State state = State.PRE_INIT;

    public State getState() {
        return state;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private final String PIXEL_GRABBER_NAME = "leftPixelGrabber";

    private CenterStageFingerServoLeft leftFingerServo;
    private CenterStageIntakeColorSensorLeft leftColorSensor;

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private boolean commandComplete = true;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public CenterStagePixelGrabberLeft(HardwareMap hardwareMap, Telemetry telemetry) {
        leftFingerServo = new CenterStageFingerServoLeft(hardwareMap, telemetry);
        leftColorSensor = new CenterStageIntakeColorSensorLeft(hardwareMap, telemetry);

        command = Command.OFF;
        state = State.PRE_INIT;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private void logState() {
        if (enableLogging && logFile != null) {
            logStateOnChange.log(getName() + " state = " + state.toString());
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

    public void on() {
        command = Command.ON;
        logCommand("On");
    }

    public void off() {
        command = Command.OFF;
        logCommand("Off");
    }

    public void releasePixel() {
        logCommand("Release Pixel");
        command = Command.RELEASE_PIXEL;
        commandComplete = false;
    }

    public boolean isCommandComplete() {
        return commandComplete;
    }

    public boolean isPixelGrabbed() {
        if (state == State.PIXEL_GRABBED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getName() {
        return PIXEL_GRABBER_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (state == State.READY_AND_WAITING || state == State.PIXEL_GRABBED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean init(Configuration config) {
        state = State.PRE_INIT;
        update();
        return true;
    }

    @Override
    public void update() {
        logState();

        switch(state) {
            case PRE_INIT:
                if (leftColorSensor.isPixelPresent()) {
                    leftFingerServo.close();
                    commandComplete = false;
                    state = State.CLOSING;
                } else {
                    leftFingerServo.open();
                    commandComplete = false;
                    state = State.OPENING;
                }
                break;

            case CLOSING:
                if (leftFingerServo.isPositionReached()) {
                    state = State.PIXEL_CHECK;
                }
                break;

            case PIXEL_CHECK:
                if (leftColorSensor.isPixelPresent()) {
                    state = State.PIXEL_GRABBED;
                    commandComplete = true;
                } else {
                    // lost the pixel somehow
                    leftFingerServo.open();
                    commandComplete = false;
                    state = State.OPENING;
                }
                break;

            case OPENING:
                if (leftFingerServo.isPositionReached()) {
                    state = State.READY_AND_WAITING;
                    commandComplete = true;
                }
                break;

            case READY_AND_WAITING:
                if (command == Command.ON) {
                    if (leftColorSensor.isPixelPresent()) {
                        leftFingerServo.close();
                        commandComplete = false;
                        state = State.CLOSING;
                    }
                } else {
                    // command = OFF so don't do anything
                }
                break;

            case PIXEL_GRABBED:
                if (command == Command.RELEASE_PIXEL){
                    leftFingerServo.open();
                    state = State.RELEASING;
                }
                break;

            case RELEASING:
                if (leftFingerServo.isPositionReached()) {
                    off();
                    commandComplete = true;
                    state = State.READY_AND_WAITING;
                }
                break;

        }

    }

    @Override
    public void shutdown() {
        leftFingerServo.open();
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
}
