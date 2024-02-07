package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class CenterStageIntakeController implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum Command {
        OFF,
        INTAKE,
        DELIVER_BOTH_PIXELS,
        DELIVER_LEFT_PIXEL,
        DELIVER_RIGHT_PIXEL,
        OUTAKE,
        OUTAKE_COMPLETE

    }

    private Command command = Command.OFF;

    public enum State {
        PRE_INIT,
        OFF,
        INTAKING,
        LEFT_PIXEL_GRABBED,
        RIGHT_PIXEL_GRABBED,
        BOTH_PIXELS_GRABBED,
        DELIVERING_BOTH_PIXELS,
        DELIVERING_LEFT_PIXEL,
        DELIVERING_RIGHT_PIXEL,
        WAITING_FOR_OUTAKE
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
    private final String INTAKE_CONTROLLER_NAME = CenterStageRobot.HardwareName.INTAKE_CONTROLLER.hwName;

    private CenterStagePixelGrabberLeft pixelGrabberLeft;
    private CenterStagePixelGrabberRight pixelGrabberRight;
    private CenterStageIntakeMotor intakeMotor;

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
    public CenterStageIntakeController(HardwareMap hardwareMap, Telemetry telemetry) {
        pixelGrabberLeft = new CenterStagePixelGrabberLeft(hardwareMap, telemetry);
        pixelGrabberRight = new CenterStagePixelGrabberRight(hardwareMap, telemetry);
        intakeMotor = new CenterStageIntakeMotor(hardwareMap, telemetry);

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

    public void off() {
        command = Command.OFF;
        logCommand("Off");
    }

    public void intake() {
        command = Command.INTAKE;
        logCommand("Intaking");
    }

    public void deliverLeftPixel() {
        logCommand("Deliver Left Pixel");
        command = Command.DELIVER_LEFT_PIXEL;
        commandComplete = false;
    }

    public void deliverRightPixel() {
        logCommand("Deliver Right Pixel");
        command = Command.DELIVER_RIGHT_PIXEL;
        commandComplete = false;
    }

    public void deliverBothPixels() {
        logCommand("Deliver Both Pixels");
        command = Command.DELIVER_BOTH_PIXELS;
        commandComplete = false;
    }

    public void outake() {
        logCommand("Outake");
        command = Command.OUTAKE;
        commandComplete = false;
    }

    public void outakeComplete() {
        logCommand("Outake Complete");
        command = Command.OUTAKE_COMPLETE;
        commandComplete = true;
    }

    public boolean isCommandComplete() {
        return commandComplete;
    }

    public boolean isIntakeFull() {
        if (state == State.BOTH_PIXELS_GRABBED) {
            return true;
        } else {
            return false;
        }
    }

    public String getLeftPixelGrabberStateAsString() {
        return pixelGrabberLeft.getStateAsString();
    }

    public String getRightPixelGrabberStateAsString() {
        return pixelGrabberRight.getStateAsString();
    }

    @Override
    public String getName() {
        return INTAKE_CONTROLLER_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (state == State.OFF ||
                state == State.BOTH_PIXELS_GRABBED ||
                state == State.LEFT_PIXEL_GRABBED ||
                state == State.RIGHT_PIXEL_GRABBED) {
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
        pixelGrabberRight.update();
        pixelGrabberLeft.update();
        logState();

        //*****************************************************************************************
        //   STATE MACHINE
        //*****************************************************************************************

        switch (state) {
            case PRE_INIT:
                if (!pixelGrabberLeft.isPixelGrabbed() && !pixelGrabberRight.isPixelGrabbed()) {
                    state = State.OFF;
                }
                if (pixelGrabberLeft.isPixelGrabbed() && pixelGrabberRight.isPixelGrabbed()) {
                    state = State.BOTH_PIXELS_GRABBED;
                }
                if (pixelGrabberLeft.isPixelGrabbed()) {
                    state = State.LEFT_PIXEL_GRABBED;
                }
                if (pixelGrabberRight.isPixelGrabbed()) {
                    state = State.RIGHT_PIXEL_GRABBED;
                }
                break;

            case OFF:
                switch (command) {

                    case OFF:
                        break;

                    case OUTAKE:
                        pixelGrabberLeft.deliverPixel();
                        pixelGrabberRight.deliverPixel();
                        intakeMotor.outake();
                        state = State.WAITING_FOR_OUTAKE;
                        break;

                    case INTAKE:
                        intakeMotor.intake();
                        pixelGrabberLeft.on();
                        pixelGrabberRight.on();
                        state = State.INTAKING;
                        break;

                    case DELIVER_LEFT_PIXEL:
                        pixelGrabberLeft.deliverPixel();
                        state = State.DELIVERING_LEFT_PIXEL;

                        break;

                    case DELIVER_RIGHT_PIXEL:
                        pixelGrabberRight.deliverPixel();
                        state = State.DELIVERING_RIGHT_PIXEL;

                        break;

                    case DELIVER_BOTH_PIXELS:
                        pixelGrabberLeft.deliverPixel();
                         pixelGrabberRight.deliverPixel();
                        state = State.DELIVERING_BOTH_PIXELS;

                        break;
                }
                break;

            //*****************************************************************************************
            //   Intaking states
            //*****************************************************************************************

            case INTAKING:
                switch (command) {

                    case OFF:
                        intakeMotor.off();
                        pixelGrabberLeft.off();
                        pixelGrabberRight.off();
                        state = State.OFF;
                        break;

                    case INTAKE:
                        if (pixelGrabberLeft.isPixelGrabbed() && pixelGrabberRight.isPixelGrabbed()) {
                            intakeMotor.off();
                            state = State.BOTH_PIXELS_GRABBED;
                        }
                        if (pixelGrabberLeft.isPixelGrabbed()) {
                            state = State.LEFT_PIXEL_GRABBED;
                        }
                        if (pixelGrabberRight.isPixelGrabbed()) {
                            state = State.RIGHT_PIXEL_GRABBED;
                        }
                        break;

                    case OUTAKE:
                        break;

                    case DELIVER_BOTH_PIXELS:
                        break;

                    case DELIVER_RIGHT_PIXEL:
                        break;

                    case DELIVER_LEFT_PIXEL:
                        break;
                }
                break;

            case LEFT_PIXEL_GRABBED:
                    switch (command) {

                        case OFF:
                            intakeMotor.off();
                            pixelGrabberLeft.off();
                            pixelGrabberRight.off();
                            state = State.LEFT_PIXEL_GRABBED;
                            break;

                        case INTAKE:
                            if (pixelGrabberRight.isPixelGrabbed()) {
                                intakeMotor.off();
                                state = State.BOTH_PIXELS_GRABBED;
                            } else {
                                intakeMotor.intake();
                                pixelGrabberLeft.on();
                                pixelGrabberRight.on();
                                //state = State.INTAKING;
                            }
                            break;

                        case DELIVER_RIGHT_PIXEL:
                            break;

                        case DELIVER_LEFT_PIXEL:
                            pixelGrabberLeft.deliverPixel();
                            state = State.DELIVERING_LEFT_PIXEL;
                            break;

                        case DELIVER_BOTH_PIXELS:
                            break;

                        case OUTAKE:
                            pixelGrabberLeft.deliverPixel();
                            pixelGrabberRight.deliverPixel();
                            intakeMotor.outake();
                            state = State.WAITING_FOR_OUTAKE;
                            break;

                    }
                break;

            case RIGHT_PIXEL_GRABBED:
                    switch (command) {

                        case OFF:
                            intakeMotor.off();
                            pixelGrabberLeft.off();
                            pixelGrabberRight.off();
                            state = State.RIGHT_PIXEL_GRABBED;
                            break;

                        case INTAKE:
                            if (pixelGrabberLeft.isPixelGrabbed()) {
                                intakeMotor.off();
                                state = State.BOTH_PIXELS_GRABBED;
                            } else {
                                intakeMotor.intake();
                                pixelGrabberLeft.on();
                                pixelGrabberRight.on();
                                //state = State.INTAKING;
                            }

                            break;

                        case DELIVER_RIGHT_PIXEL:
                            pixelGrabberRight.deliverPixel();
                            state = State.DELIVERING_RIGHT_PIXEL;
                            break;

                        case DELIVER_LEFT_PIXEL:
                            break;

                        case DELIVER_BOTH_PIXELS:
                            break;

                        case OUTAKE:
                            pixelGrabberLeft.deliverPixel();
                            pixelGrabberRight.deliverPixel();
                            intakeMotor.outake();
                            state = State.WAITING_FOR_OUTAKE;
                            break;

                    }
//                    if (command == Command.OFF) {
//                        intakeMotor.off();
//                        pixelGrabberLeft.off();
//                        pixelGrabberRight.off();
//                        state = State.RIGHT_PIXEL_GRABBED;
//                    }
//                    if (command == Command.DELIVER_RIGHT_PIXEL) {
//                        pixelGrabberRight.deliverPixel();
//                        state = State.DELIVERING_RIGHT_PIXEL;
//                    }
//                    if (command == Command.INTAKE) {
//                        intakeMotor.intake();
//                        pixelGrabberLeft.on();
//                        pixelGrabberRight.on();
//                        state = State.INTAKING;
//                    }
//                    if (command == Command.OUTAKE) {
//                        intakeMotor.outake();
//                        pixelGrabberLeft.deliverPixel();
//                        pixelGrabberRight.deliverPixel();
//                        state = State.WAITING_FOR_OUTAKE;
//                    }
                break;

            case BOTH_PIXELS_GRABBED:
                switch (command) {

                    case OFF:
                        break;

                    case INTAKE:
                        break;

                    case DELIVER_LEFT_PIXEL:
                        intakeMotor.off();
                        pixelGrabberLeft.deliverPixel();
                        state = State.RIGHT_PIXEL_GRABBED;
                        break;

                    case DELIVER_RIGHT_PIXEL:
                        intakeMotor.off();
                        pixelGrabberRight.deliverPixel();
                        state = State.LEFT_PIXEL_GRABBED;
                        break;

                    case DELIVER_BOTH_PIXELS:
                        intakeMotor.off();
                        pixelGrabberLeft.deliverPixel();
                        pixelGrabberRight.deliverPixel();
                        state = State.DELIVERING_BOTH_PIXELS;
                        break;

                    case OUTAKE:
                        intakeMotor.outake();
                        pixelGrabberLeft.deliverPixel();
                        pixelGrabberRight.deliverPixel();
                        state = State.WAITING_FOR_OUTAKE;
                        break;

                }
                break;

            //*****************************************************************************************
            //   Delivery states
            //*****************************************************************************************

            case DELIVERING_BOTH_PIXELS:
                    switch (command) {

                        case OFF:
                            break;

                        case INTAKE:
                            intakeMotor.intake();
                            pixelGrabberLeft.on();
                            pixelGrabberRight.on();
                            state = State.INTAKING;
                            break;

                        case OUTAKE:
                            break;

                        case DELIVER_BOTH_PIXELS:
                            if (pixelGrabberLeft.isDeliveryComplete() && pixelGrabberRight.isDeliveryComplete()) {
                                state = State.OFF;
                                commandComplete = true;
                                command = Command.OFF;
                            }
                            break;

                        case DELIVER_RIGHT_PIXEL:
                            break;

                        case DELIVER_LEFT_PIXEL:
                            break;
                    }
                break;

            case DELIVERING_LEFT_PIXEL:
                    switch (command) {

                        case OFF:
                            break;

                        case INTAKE:
                            break;

                        case OUTAKE:
                            break;

                        case DELIVER_BOTH_PIXELS:
                            if (pixelGrabberLeft.isDeliveryComplete() && pixelGrabberRight.isDeliveryComplete()) {
                                state = State.OFF;
                                commandComplete = true;
                                command = Command.OFF;
                            }
                            break;

                        case DELIVER_RIGHT_PIXEL:
                            break;

                        case DELIVER_LEFT_PIXEL:
                            if (pixelGrabberRight.isDeliveryComplete()) {
                                if (pixelGrabberRight.isPixelGrabbed()) {
                                    state = State.RIGHT_PIXEL_GRABBED;
                                }
                                else {
                                    state = State.OFF;
                                }

                                commandComplete = true;
                            }
                            break;
                    }
                break;

            case DELIVERING_RIGHT_PIXEL:
                    switch (command) {

                        case OFF:
                            break;

                        case INTAKE:
                            break;

                        case OUTAKE:
                            break;

                        case DELIVER_BOTH_PIXELS:
                            if (pixelGrabberLeft.isDeliveryComplete() && pixelGrabberRight.isDeliveryComplete()) {
                                state = State.OFF;
                                commandComplete = true;
                                command = Command.OFF;
                            }
                            break;

                        case DELIVER_RIGHT_PIXEL:
                            if (pixelGrabberRight.isDeliveryComplete()) {
                                if (pixelGrabberLeft.isPixelGrabbed()) {
                                    state = State.LEFT_PIXEL_GRABBED;
                                }
                                else {
                                    state = State.OFF;
                                }

                                commandComplete = true;
                            }
                            break;

                        case DELIVER_LEFT_PIXEL:
                            break;
                    }
                break;

            //*****************************************************************************************
            //   Outtake states
            //*****************************************************************************************

            case WAITING_FOR_OUTAKE:
                switch (command) {

                    case OUTAKE_COMPLETE:
                    case OFF:
                        intakeMotor.off();
                        pixelGrabberLeft.off();
                        pixelGrabberRight.off();
                        state = State.OFF;
                        break;

                    case INTAKE:
                        break;

                    case OUTAKE:
                        break;

                    case DELIVER_BOTH_PIXELS:
                        break;

                    case DELIVER_RIGHT_PIXEL:
                        break;

                    case DELIVER_LEFT_PIXEL:
                        break;
                }
                break;
        }
    }

    @Override
    public void shutdown() {
        intakeMotor.off();
        pixelGrabberLeft.off();
        pixelGrabberRight.off();
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
