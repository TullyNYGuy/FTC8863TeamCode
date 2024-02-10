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
        FINISH_INTAKE,
        DELIVER_BOTH_PIXELS,
        DELIVER_LEFT_PIXEL,
        DELIVER_RIGHT_PIXEL,
        OUTAKE,
        OUTAKE_COMPLETE

    }

    private Command command = Command.OFF;

    public Command getCommand() {
        return command;
    }

    public enum State {
        PRE_INIT,
        OFF,
        INTAKING,
        GRABBING_LEFT_PIXEL,
        GRABBING_RIGHT_PIXEL,
        GRABBING_BOTH_PIXELS,
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
    private CenterStageDeliveryController deliveryController;

    public void setDeliveryController(CenterStageDeliveryController deliveryController) {
        this.deliveryController = deliveryController;
    }

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

    //*****************************************************************************************
    //   commands
    //*****************************************************************************************

    @Override
    public boolean init(Configuration config) {
        state = State.PRE_INIT;
        update();
        return true;
    }

    public void off() {
        command = Command.OFF;
        logCommand("Off");
    }

    public void intake() {
        command = Command.INTAKE;
        logCommand("Intaking");
    }

    public void finishIntake() {
        command = Command.FINISH_INTAKE;
        logCommand("Finish Intake");
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

    public void aidSetupForDelivery() {
        intakeMotor.liftAssist();
    }

    public void stopIntakeMotor() {
        intakeMotor.off();
    }

    //*****************************************************************************************
    //   status
    //*****************************************************************************************

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

    public String getLeftPixelGrabberCommandAsString() {
        return pixelGrabberLeft.getCommandAsString();
    }

    public String getRightPixelGrabberCommandAsString() {
        return pixelGrabberRight.getCommandAsString();
    }

    public boolean isPixelPresentRight() {
        return pixelGrabberRight.isPixelPresent();
    }

    public boolean isPixelPresentLeft() {
        return pixelGrabberLeft.isPixelPresent();
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

                    case FINISH_INTAKE:
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
                        if (pixelGrabberLeft.isPixelPresent() && pixelGrabberRight.isPixelPresent()) {
                            pixelGrabberLeft.grabPixel();
                            pixelGrabberRight.grabPixel();
                            intakeMotor.off();
                            state = State.GRABBING_BOTH_PIXELS;
                        }
                        break;

                    case FINISH_INTAKE:
                        // The driver gives this command when only 1 pixel is in the intake and they want
                        // to terminate the intake
                        if (pixelGrabberLeft.isPixelPresent()) {
                            pixelGrabberLeft.grabPixel();
                            state = State.GRABBING_LEFT_PIXEL;
                        } else {
                            if (pixelGrabberRight.isPixelPresent()) {
                                pixelGrabberRight.grabPixel();
                                state = State.GRABBING_RIGHT_PIXEL;
                            }
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

            case GRABBING_LEFT_PIXEL:
                switch (command) {

                    case OFF:
                        break;

                    case INTAKE:
                    case FINISH_INTAKE:
                        if (pixelGrabberLeft.isPixelGrabbed()) {
                            state = State.LEFT_PIXEL_GRABBED;
                        } else {
                            if (pixelGrabberLeft.didPixelGrabFail()) {
                                // pixel grab failed , return to intaking
                                command = Command.INTAKE;
                                state = State.INTAKING;
                            }
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

            case GRABBING_RIGHT_PIXEL:
                switch (command) {

                    case OFF:
                        break;

                    case INTAKE:
                    case FINISH_INTAKE:
                        if (pixelGrabberRight.isPixelGrabbed()) {
                            state = State.RIGHT_PIXEL_GRABBED;
                        } else {
                            if (pixelGrabberRight.didPixelGrabFail()) {
                                // pixel grab failed , return to intaking
                                command = Command.INTAKE;
                                state = State.INTAKING;
                            }
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

            case GRABBING_BOTH_PIXELS:
                switch (command) {

                    case OFF:
                        break;

                    case INTAKE:
                        if (pixelGrabberLeft.isPixelGrabbed() && pixelGrabberRight.isPixelGrabbed()) {
                            state = State.BOTH_PIXELS_GRABBED;
                        } else {
                            if (pixelGrabberLeft.didPixelGrabFail() || pixelGrabberRight.didPixelGrabFail()) {
                                // pixel grab failed , return to intaking
                                command = Command.INTAKE;
                                state = State.INTAKING;
                            }
                        }
                        break;

                    case FINISH_INTAKE:
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
                            if (pixelGrabberRight.isPixelPresent()) {
                                intakeMotor.off();
                                pixelGrabberRight.grabPixel();
                                // reissue the grab pixel for the left. It could be in a running
                                // state if the intake process was restarted.
                                pixelGrabberLeft.grabPixel();
                                state = State.GRABBING_BOTH_PIXELS;
                            } else {
                                intakeMotor.intake();
                                pixelGrabberLeft.on();
                                pixelGrabberRight.on();
                                //state = State.INTAKING;
                            }
                            break;

                        case FINISH_INTAKE:
                            intakeMotor.off();
                            break;

                        case DELIVER_RIGHT_PIXEL:
                            break;

                        case DELIVER_LEFT_PIXEL:
                            pixelGrabberLeft.deliverPixel();
                            state = State.DELIVERING_LEFT_PIXEL;
                            break;

                        case DELIVER_BOTH_PIXELS:
                            intakeMotor.off();
                            pixelGrabberLeft.deliverPixel();
                            pixelGrabberRight.deliverPixel();
                            state = State.DELIVERING_BOTH_PIXELS;
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
                            if (pixelGrabberLeft.isPixelPresent()) {
                                intakeMotor.off();
                                pixelGrabberLeft.grabPixel();
                                // reissue the grab pixel for the right. It could be in a running
                                // state if the intake process was restarted.
                                pixelGrabberRight.grabPixel();
                                state = State.GRABBING_BOTH_PIXELS;
                            } else {
                                intakeMotor.intake();
                                pixelGrabberLeft.on();
                                pixelGrabberRight.on();
                                //state = State.INTAKING;
                            }
                            break;

                        case FINISH_INTAKE:
                            intakeMotor.off();
                            break;

                        case DELIVER_RIGHT_PIXEL:
                            pixelGrabberRight.deliverPixel();
                            state = State.DELIVERING_RIGHT_PIXEL;
                            break;

                        case DELIVER_LEFT_PIXEL:
                            break;

                        case DELIVER_BOTH_PIXELS:
                            intakeMotor.off();
                            pixelGrabberLeft.deliverPixel();
                            pixelGrabberRight.deliverPixel();
                            state = State.DELIVERING_BOTH_PIXELS;
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

                    case FINISH_INTAKE:
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

                        case FINISH_INTAKE:
                            break;

                        case OUTAKE:
                            break;

                        case DELIVER_BOTH_PIXELS:
                            if (pixelGrabberLeft.isDeliveryComplete() && pixelGrabberRight.isDeliveryComplete()) {
                                state = State.OFF;
                                commandComplete = true;
                                command = Command.OFF;
                                deliveryController.returnToIntakePosition();
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

                        case FINISH_INTAKE:
                            break;

                        case OUTAKE:
                            break;

                        case DELIVER_BOTH_PIXELS:
                            if (pixelGrabberLeft.isDeliveryComplete() && pixelGrabberRight.isDeliveryComplete()) {
                                state = State.OFF;
                                commandComplete = true;
                                command = Command.OFF;
                                deliveryController.returnToIntakePosition();
                            }
                            break;

                        case DELIVER_RIGHT_PIXEL:
                            break;

                        case DELIVER_LEFT_PIXEL:
                            if (pixelGrabberLeft.isDeliveryComplete()) {
                                if (pixelGrabberRight.isPixelGrabbed()) {
                                    state = State.RIGHT_PIXEL_GRABBED;
                                }
                                else {
                                    state = State.OFF;
                                    deliveryController.returnToIntakePosition();
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

                        case FINISH_INTAKE:
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
                                    deliveryController.returnToIntakePosition();
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

                    case FINISH_INTAKE:
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
        pixelGrabberLeft.setDataLog(logFile);
        pixelGrabberRight.setDataLog(logFile);
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        pixelGrabberLeft.enableDataLogging();
        pixelGrabberRight.enableDataLogging();
        enableLogging = true;
    }

    @Override
    public void disableDataLogging() {
        pixelGrabberLeft.disableDataLogging();
        pixelGrabberRight.disableDataLogging();
        enableLogging = false;
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }
}
