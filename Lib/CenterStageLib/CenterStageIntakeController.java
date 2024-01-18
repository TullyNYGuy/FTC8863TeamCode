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
        DELIVER_RIGHT_PIXEL

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
        DELIVERING_RIGHT_PIXEL
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
    private final String PIXEL_GRABBER_NAME = CenterStageRobot.HardwareName.LEFT_PIXEL_GRABBER.hwName;

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

    public String getLeftPixelGrabberStateAsString(){
        return pixelGrabberLeft.getStateAsString();
    }
    public String getRightPixelGrabberStateAsString(){
        return pixelGrabberRight.getStateAsString();
    }
    @Override
    public String getName() {
        return PIXEL_GRABBER_NAME;
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
                if (command == Command.INTAKE) {
                    intakeMotor.intake();
                    pixelGrabberLeft.on();
                    pixelGrabberRight.on();
                    state = State.INTAKING;
                }
                break;

            case INTAKING:
                if (command == Command.OFF) {
                    intakeMotor.off();
                    pixelGrabberLeft.off();
                    pixelGrabberRight.off();
                    state = State.OFF;
                } else {
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
                }
                break;

            case LEFT_PIXEL_GRABBED:
                if (pixelGrabberRight.isPixelGrabbed()) {
                    intakeMotor.off();
                    state = State.BOTH_PIXELS_GRABBED;
                } else {
                    if (command == Command.OFF) {
                        intakeMotor.off();
                        pixelGrabberLeft.off();
                        pixelGrabberRight.off();
                        state = State.LEFT_PIXEL_GRABBED;
                    }
                    if (command == Command.DELIVER_LEFT_PIXEL) {
                        pixelGrabberLeft.deliverPixel();
                        state = State.DELIVERING_LEFT_PIXEL;
                    }
                    if (command == Command.INTAKE) {
                        intakeMotor.intake();
                        pixelGrabberLeft.on();
                        pixelGrabberRight.on();
                        state = State.INTAKING;
                    }
                }
                break;

            case RIGHT_PIXEL_GRABBED:
                if (pixelGrabberLeft.isPixelGrabbed()) {
                    intakeMotor.off();
                    state = State.BOTH_PIXELS_GRABBED;
                } else {
                    if (command == Command.OFF) {
                        intakeMotor.off();
                        pixelGrabberLeft.off();
                        pixelGrabberRight.off();
                        state = State.RIGHT_PIXEL_GRABBED;
                    }
                    if (command == Command.DELIVER_RIGHT_PIXEL) {
                        pixelGrabberRight.deliverPixel();
                        state = State.DELIVERING_RIGHT_PIXEL;
                    }
                    if (command == Command.INTAKE) {
                        intakeMotor.intake();
                        pixelGrabberLeft.on();
                        pixelGrabberRight.on();
                        state = State.INTAKING;
                    }
                }
                break;

            case BOTH_PIXELS_GRABBED:
                if (command == Command.DELIVER_BOTH_PIXELS) {
                    intakeMotor.off();
                    pixelGrabberLeft.deliverPixel();
                    pixelGrabberRight.deliverPixel();
                    state = State.DELIVERING_BOTH_PIXELS;
                }
                break;

            case DELIVERING_BOTH_PIXELS:
                if (pixelGrabberLeft.isDeliveryComplete() && pixelGrabberRight.isDeliveryComplete()) {
                    state = State.OFF;
                }
                break;

            case DELIVERING_LEFT_PIXEL:
                if (pixelGrabberLeft.isDeliveryComplete()) {
                    state = State.INTAKING;
                }
                break;

            case DELIVERING_RIGHT_PIXEL:
                if (pixelGrabberRight.isDeliveryComplete()) {
                    state = State.INTAKING;
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
