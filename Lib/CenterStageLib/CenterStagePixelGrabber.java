package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class CenterStagePixelGrabber implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum Mode {
        GRAB_PIXEL_RIGHT_AWAY,
        GRAB_PIXEL_WHEN_COMMANDED
    }

    private Mode mode = Mode.GRAB_PIXEL_WHEN_COMMANDED;

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public enum Command {
        INIT,
        ON,
        DELIVER_PIXEL,
        GRAB_PIXEL,
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
        PIXEL_PRESENT,
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

    private CenterStageFingerServo fingerServo;
    private CenterStageIntakeColorSensor colorSensor;
    private String pixelGrabberName;

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private boolean commandComplete = true;
    private boolean deliveryComplete = false;
    private boolean initComplete = false;
    private boolean pixelPresent = false;
    private boolean pixelGrabFailed = false;
    private int attemptCount = 0;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public CenterStagePixelGrabber(HardwareMap hardwareMap,
                                   Telemetry telemetry,
                                   CenterStageFingerServo fingerServo,
                                   CenterStageIntakeColorSensor colorSensor,
                                   String pixelGrabberName) {
        this.fingerServo = fingerServo;
        this.colorSensor = colorSensor;
        this.pixelGrabberName = pixelGrabberName;

        command = Command.INIT;
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

    //*******************************************************
    // commands
    //*******************************************************

    @Override
    public boolean init(Configuration config) {
        state = State.PRE_INIT;
        command = Command.INIT;
        update();
        return true;
    }

    public void on() {
        if (commandComplete) {
            command = Command.ON;
            logCommand("On");
        }else {
            // do not start a command while another one is not completed
        }
    }

    public void off() {
        if (commandComplete) {
            command = Command.OFF;
            logCommand("Off");
        }else {
            // do not start a command while another one is not completed
        }
    }

    public void grabPixel() {
        if (commandComplete) {
            logCommand("Grab Pixel");
            command = Command.GRAB_PIXEL;
            commandComplete = false;
        } else {
            // do not start a command while another one is not completed
        }
    }

    public void deliverPixel() {
        if (commandComplete) {
            logCommand("Deliver Pixel");
            command = Command.DELIVER_PIXEL;
            commandComplete = false;
        } else {
            // do not start a command while another one is not completed
        }
    }

    //*******************************************************
    // status
    //*******************************************************

    @Override
    public boolean isInitComplete() {
        return initComplete;
    }

    public boolean isCommandComplete() {
        return commandComplete;
    }

    public boolean isPixelPresent() {
        return pixelPresent;
    }

    public boolean isPixelGrabbed() {
        if (state == State.PIXEL_GRABBED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean didPixelGrabFail() {
        return pixelGrabFailed;
    }

    public boolean isDeliveryComplete() {
        return deliveryComplete;
    }

    public String getStateAsString(){
        return state.toString();
    }

    public String getCommandAsString(){
        return command.toString();
    }

//    public boolean isDeliveryComplete() {
//        if (state == State.OFF) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    @Override
    public String getName() {
        return pixelGrabberName;
    }

    //*******************************************************
    // state machine
    //*******************************************************

    @Override
    public void update() {
        logState();

        switch(state) {
            case PRE_INIT:
                if (colorSensor.isPixelPresent()) {
                    pixelPresent = true;
                    if (attemptCount > 1) {
                        // too many attempts to grab the pixel, give up
                        commandComplete = true;
                        initComplete = true;
                        state = State.OFF;
                    } else {
                        fingerServo.close();
                        commandComplete = false;
                        state = State.CLOSING;
                    }
                } else {
                    // no pixel is present, try once more then give up
                    if (attemptCount > 1) {
                        commandComplete = true;
                        initComplete = true;
                        state = State.OFF;
                    } else {
                        fingerServo.open();
                        commandComplete = false;
                        state = State.OPENING;
                        attemptCount ++;
                    }

                }
                break;

            case CLOSING:
                switch (command) {
                    case INIT:
                    case GRAB_PIXEL:
                        if (fingerServo.isPositionReached()) {
                            state = State.CHECK_PIXEL_GRABBED;
                            attemptCount++;
                        }
                        break;

                    case OFF:
                        // closing occurs in the middle of a command. OFF should not be allowed. And
                        // it should not ever be a valid command because of the command lockout.
                        break;

                    case ON:
                        // on occurs in the middle of a command. ON should not be allowed. And
                        // it should not ever be a valid command because of the command lockout.
                        break;

                    case DELIVER_PIXEL:
                        // deliver pixel occurs in the middle of a command. DELIVER_PIXEL should not be allowed. And
                        // it should not ever be a valid command because of the command lockout.
                        break;
                }
                break;

            case CHECK_PIXEL_GRABBED:
                switch (command) {
                    case INIT:
                    case GRAB_PIXEL:
                        if (colorSensor.isPixelPresent()) {
                            pixelPresent = true;
                            pixelGrabFailed = false;
                            state = State.PIXEL_GRABBED;
                            commandComplete = true;
                            deliveryComplete = false;
                            // note that I don't care about initComplete once the pixel grabber is past
                            // the init stage so this can be set every time, even when running normally
                            initComplete = true;
                            attemptCount = 0;
                        } else {
                            pixelPresent = false;
                            // lost the pixel somehow, try to grab it twice
                            if (attemptCount < 2) {
                                fingerServo.open();
                                commandComplete = false;
                                // note that I don't care about initComplete once the pixel grabber is past
                                // the init stage so this can be set every time, even when running normally
                                initComplete = false;
                                state = State.OPENING;
                            } else {
                                // tried to grab the pixel too many times, give up
                                logCommand("tried to grab pixel 2x and failed");
                                pixelGrabFailed = true;
                                commandComplete = true;
                                command = Command.ON;
                                state = State.RUNNING;
                            }

                        }
                        break;

                    case OFF:
                        // closing occurs in the middle of a command. OFF should not be allowed. And
                        // it should not ever be a valid command because of the command lockout.
                        break;

                    case ON:
                        // on occurs in the middle of a command. ON should not be allowed. And
                        // it should not ever be a valid command because of the command lockout.
                        break;

                    case DELIVER_PIXEL:
                        // deliver pixel occurs in the middle of a command. DELIVER_PIXEL should not be allowed. And
                        // it should not ever be a valid command because of the command lockout.
                        break;
                }
                break;

            case OPENING:
                switch (command) {
                    case INIT:
                        if (fingerServo.isPositionReached()) {
                            state = State.PRE_INIT;
                        }
                        break;

                    case OFF:
                        state = State.OFF;
                        break;

                    case ON:
                        //state = State.RUNNING;
                        // on occurs in the middle of a command. ON should not be allowed. And
                        // it should not ever be a valid command because of the command lockout.
                        break;

                    case GRAB_PIXEL:
                        // this state can be reached if the CHECK_PIXEL_GRABBED does not see a pixel
                        // successfully grabbed
                        if (fingerServo.isPositionReached()) {
                            attemptCount++;
                            state = State.RUNNING;
                        }
                        break;

                    case DELIVER_PIXEL:
                        // deliver pixel occurs in the middle of a command. DELIVER_PIXEL should not be allowed. And
                        // it should not ever be a valid command because of the command lockout.
                        break;
                }
                break;

            case RUNNING:
                switch (command) {
                    case INIT:
                        //should never end up here
                        break;

                    case OFF:
                        state = State.OFF;
                        break;

                    case ON:
                        if (colorSensor.isPixelPresent()) {
                            logCommand("pixel present");
                            pixelPresent = true;
                            if (mode == Mode.GRAB_PIXEL_RIGHT_AWAY) {
                                grabPixel();
                            } else {
                                // wait to grab the pixel, don't do anything now
                                // Just monitor the pixel presence and let the intake do its thing:
                                // try to keep the pixel there
                            }
                        } else {
                            pixelPresent = false;
                        }
                        break;

                    case GRAB_PIXEL:
                        // someone commanded me to grab the pixel
                        // try to grab the pixel if it is present
                        if (colorSensor.isPixelPresent()) {
                            pixelPresent = true;
                            fingerServo.close();
                            state = State.CLOSING;
                        } else {
                            pixelPresent = false;
                        }
                        break;

                    case DELIVER_PIXEL:
                        commandComplete = false;
                        fingerServo.open();
                        state = State.DELIVERING;
                        break;
                }
                break;

            case OFF:
                switch (command) {
                    case INIT:
                        //should never end up here
                        break;

                    case OFF:
                        // already off. Just ignore this command
                        // update the status of the pixel
                        if (colorSensor.isPixelPresent()) {
                            pixelPresent = true;
                        } else {
                            pixelPresent = false;
                        }
                        break;

                    case ON:
                        attemptCount = 0;
                        commandComplete = true;
                        state = State.RUNNING;
                        break;

                    case GRAB_PIXEL:
                        // someone commanded me to grab the pixel
                        // try to grab the pixel if it is present
                        if (colorSensor.isPixelPresent()) {
                            pixelPresent = true;
                            fingerServo.close();
                            state = State.CLOSING;
                        } else {
                            pixelPresent = false;
                        }
                        break;

                    case DELIVER_PIXEL:
                        commandComplete = false;
                        fingerServo.open();
                        state = State.DELIVERING;
                        break;
                }
                break;

            case PIXEL_GRABBED:
                switch (command) {
                    case INIT:
                        // don't do anything, I already have a pixel. Wait for another command;
                        break;

                    case OFF:
                        // don't do anything, I already have a pixel. Wait for another command;
                        break;

                    case ON:
                        // It is possible to restart the intake after a pixel has been grabbed.
                        // In this case, even though a pixel has already been grabbed, we want to
                        // act as though the intake process has started all over again.
                        state = State.RUNNING;
                        command = Command.ON;
                        fingerServo.open();
                        break;

                    case GRAB_PIXEL:
                        // don't do anything, I already have a pixel. Wait for another command;
                        // if we are already in pixel_grabbed state and someone commands a grab pixel,
                        // that set command_complete = false and locks out all other commands. so
                        commandComplete = true;
                        break;

                    case DELIVER_PIXEL:
                        fingerServo.open();
                        state = State.DELIVERING;
                        break;
                }
                break;

//            case PIXEL_PRESENT:
//                switch (command) {
//                    case INIT:
//                        break;
//
//                    case OFF:
//                        break;
//
//                    case ON:
//                        break;
//
//                    case GRAB_PIXEL:
//                        break;
//
//                    case DELIVER_PIXEL:
//                        break;
//                }
//                break;

            case DELIVERING:
                switch (command) {
                    case INIT:
                        //should never end up here
                        break;

                    case OFF:
                        // ignore this command while delivering a pixel
                        break;

                    case ON:
                        // ignore this command while delivering a pixel
                        break;

                    case GRAB_PIXEL:
                        // ignore this command while delivering a pixel
                        break;

                    case DELIVER_PIXEL:
                        // pixel is delivered once it is not seen by the color sensor
                        // NOte this could be a problem if something acts quickly on this since the
                        // pixel may not have fully exited the tray.
                        if (!colorSensor.isPixelPresent() || fingerServo.isPositionReached()) {
                            pixelPresent = false;
                            deliveryComplete = true;
                            commandComplete = true;
                            attemptCount = 0;
                            off();
                            state = State.OFF;                        }
                        break;
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
