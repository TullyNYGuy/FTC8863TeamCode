package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class CenterStagePixelGrabberRight implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum Command {
        ON,
        DELIVER_PIXEL,
        OFF
    }
    private Command command = Command.OFF;

    public enum State {
        PRE_INIT,
        OFF,
        RUNNING,
        CLOSING,
        CHECK_PIXEL_GRABBED,
        PIXEL_GRABBED,
        OPENING,
        DELIVERING
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
    private final String PIXEL_GRABBER_NAME = CenterStageRobot.HardwareName.RIGHT_PIXEL_GRABBER.hwName;;

    private CenterStageFingerServoRight fingerServo;
    private CenterStageIntakeColorSensorRight colorSensor;

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private boolean commandComplete = true;
    private boolean deliveryComplete = false;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public CenterStagePixelGrabberRight(HardwareMap hardwareMap, Telemetry telemetry) {
        fingerServo = new CenterStageFingerServoRight(hardwareMap, telemetry);
        colorSensor = new CenterStageIntakeColorSensorRight(hardwareMap, telemetry);

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

    public void deliverPixel() {
        logCommand("Deliver Pixel");
        command = Command.DELIVER_PIXEL;
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
    public boolean isDeliveryComplete() {
        return deliveryComplete;
    }

    public String getStateAsString(){
        return state.toString();
    }

//    public boolean isDeliveryComplete() {
//        if (state == CenterStagePixelGrabberRight.State.OFF) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    @Override
    public String getName() {
        return PIXEL_GRABBER_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (state == State.OFF || state == State.PIXEL_GRABBED) {
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
                if (colorSensor.isPixelPresent()) {
                    fingerServo.close();
                    commandComplete = false;
                    state = CenterStagePixelGrabberRight.State.CLOSING;
                } else {
                    fingerServo.open();
                    commandComplete = false;
                    state = CenterStagePixelGrabberRight.State.OPENING;
                }
                break;

            case CLOSING:
                if (fingerServo.isPositionReached()) {
                    state = CenterStagePixelGrabberRight.State.CHECK_PIXEL_GRABBED;
                }
                break;

            case CHECK_PIXEL_GRABBED:
                if (colorSensor.isPixelPresent()) {
                    state = CenterStagePixelGrabberRight.State.PIXEL_GRABBED;
                    commandComplete = true;
                    deliveryComplete = false;
                } else {
                    // lost the pixel somehow
                    fingerServo.open();
                    commandComplete = false;
                    state = CenterStagePixelGrabberRight.State.OPENING;
                }
                break;

            case OPENING:
                if (fingerServo.isPositionReached()) {
                    commandComplete = true;
                    if (command == CenterStagePixelGrabberRight.Command.ON) {
                        state = CenterStagePixelGrabberRight.State.RUNNING;
                    }
                    if (command == CenterStagePixelGrabberRight.Command.OFF) {
                        state = CenterStagePixelGrabberRight.State.OFF;
                    }
                }
                break;

            case RUNNING:
                if (colorSensor.isPixelPresent()) {
                    fingerServo.close();
                    state = CenterStagePixelGrabberRight.State.CLOSING;
                }
                if (command == CenterStagePixelGrabberRight.Command.OFF) {
                    state = CenterStagePixelGrabberRight.State.OFF;
                }
                break;

            case OFF:
                if (command == CenterStagePixelGrabberRight.Command.ON) {
                    commandComplete = true;
                    state = CenterStagePixelGrabberRight.State.RUNNING;
                }
                if (command == CenterStagePixelGrabberRight.Command.DELIVER_PIXEL) {
                    commandComplete = false;
                    fingerServo.open();
                    state = CenterStagePixelGrabberRight.State.DELIVERING;
                }
                break;

            case PIXEL_GRABBED:
                if (command == CenterStagePixelGrabberRight.Command.DELIVER_PIXEL){
                    fingerServo.open();
                    state = CenterStagePixelGrabberRight.State.DELIVERING;
                }
                if (command == CenterStagePixelGrabberRight.Command.OFF) {
                    state = CenterStagePixelGrabberRight.State.OFF;
                }
                break;

            case DELIVERING:
                if (fingerServo.isPositionReached()) {
                    off();
                    commandComplete = true;
                    state = CenterStagePixelGrabberRight.State.OFF;
                    deliveryComplete = true;

                }
                break;
        }
    }

    @Override
    public void shutdown() {
        fingerServo.open();
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
