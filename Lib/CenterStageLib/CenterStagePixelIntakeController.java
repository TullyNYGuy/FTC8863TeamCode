package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class CenterStagePixelIntakeController implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum Command {
        OFF,
        INTAKE,
        DELIVER_PIXEL
    }
    private Command command = Command.OFF;

    public enum State {
        PRE_INIT,
        OFF,
        INTAKING,
        CLOSING,
        LEFT_PIXEL_GRABBED,
        RIGHT_PIXEL_GRABBED,
        INTAKE_FULL,
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
    private final String INTAKE_CONTROLLER_NAME = CenterStageRobot.HardwareName.INTAKE_CONTROLLER.hwName;

    private CenterStagePixelGrabberLeft pixelGrabberLeft;
    private CenterStagePixelGrabberRight pixelGrabberRight;
    private CenterStageIntakeMotor intakeMotor;

    private boolean leftPixelGrabbed = false;
    private boolean rightPixelGrabbed = false;

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
    public CenterStagePixelIntakeController(HardwareMap hardwareMap, Telemetry telemetry) {
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

    public void intake() {
        command = Command.INTAKE;
        logCommand("Intake");
    }

    public void off() {
        command = Command.OFF;
        logCommand("Off");
    }

    public void deliverPixels() {
        logCommand("Deliver Pixel");
        command = Command.DELIVER_PIXEL;
        commandComplete = false;
    }

    public boolean isCommandComplete() {
        return commandComplete;
    }

    public boolean isIntakeFull() {
        if (state == State.INTAKE_FULL) {
            return true;
        } else {
            return false;
        }
    }

    public String getLeftPixelGrabberState() {
        return pixelGrabberLeft.getState().toString();
    }

    public String getRightPixelGrabberState() {
        return pixelGrabberRight.getState().toString();
    }

        @Override
    public String getName() {
        return INTAKE_CONTROLLER_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (state == State.OFF ||
                state == State.LEFT_PIXEL_GRABBED ||
                state == State.RIGHT_PIXEL_GRABBED ||
                state == State.INTAKE_FULL) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean init(Configuration config) {
        pixelGrabberLeft.init(config);
        pixelGrabberRight.init(config);
        state = State.PRE_INIT;
        update();
        return true;
    }

    @Override
    public void update() {
        pixelGrabberLeft.update();
        pixelGrabberRight.update();
        logState();

        switch(state) {
            case PRE_INIT:
                leftPixelGrabbed = pixelGrabberLeft.isPixelGrabbed();
                rightPixelGrabbed = pixelGrabberRight.isPixelGrabbed();
                if (!leftPixelGrabbed && !rightPixelGrabbed) {
                    state = State.OFF;
                }
                if (leftPixelGrabbed && rightPixelGrabbed) {
                    state = State.INTAKE_FULL;
                }
                if (leftPixelGrabbed) {
                    state = State.LEFT_PIXEL_GRABBED;
                }
                if (rightPixelGrabbed) {
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
                }
                if (pixelGrabberLeft.isPixelGrabbed() && pixelGrabberRight.isPixelGrabbed()) {
                    intakeMotor.outtake();
                    state = State.INTAKE_FULL;
                }
                if (pixelGrabberLeft.isPixelGrabbed()) {
                    state = State.LEFT_PIXEL_GRABBED;
                }
                if (pixelGrabberRight.isPixelGrabbed()) {
                    state = State.RIGHT_PIXEL_GRABBED;
                }
                break;

            case LEFT_PIXEL_GRABBED:
                if (pixelGrabberRight.isPixelGrabbed()) {
                    intakeMotor.outtakeSlow();
                    state = State.INTAKE_FULL;
                }
                break;

            case RIGHT_PIXEL_GRABBED:
                if (pixelGrabberLeft.isPixelGrabbed()) {
                    intakeMotor.outtakeSlow();
                    state = State.INTAKE_FULL;
                }
                break;

            case INTAKE_FULL:
                if (command == Command.DELIVER_PIXEL) {
                    intakeMotor.liftAssist();
                    pixelGrabberRight.deliverPixel();
                    pixelGrabberLeft.deliverPixel();
                    state = State.DELIVERING;
                }
                break;

            case DELIVERING:
                if (pixelGrabberRight.isDeliveryComplete() && pixelGrabberLeft.isDeliveryComplete()) {
                    intakeMotor.outtake();
                    state = State.OFF;
                }
                break;
        }
    }

    @Override
    public void shutdown() {
        off();
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
