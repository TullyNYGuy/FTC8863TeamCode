package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;

import android.view.WindowManager;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ITDExtensionArm implements FTCRobotSubsystem {

    private static final Logger log = LoggerFactory.getLogger(ITDExtensionArm.class);

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum ExtensionArmState {
        RESETING,
        MOVING,
        JOYSTICK_CONTROL,
        IDLE
    }

    private ExtensionArmState state;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private ElapsedTime timer;

    private DataLogging logFile;
    private boolean loggingOn = false;
    private DataLogOnChange logDataOnchange;

    private boolean initComplete = false;
    private final String EXTENSION_ARM_NAME = ITDRobot.HardwareName.EXTENSION_ARM.hwName;
    ExtensionRetractionMechanism extensionArm;

    /**
     * We need a reference to the controller so that we can communicate with it.
     * @param controller
     */
    private ITDExtensionArmIntakeController controller;
    public void setController(ITDExtensionArmIntakeController controller) {
        this.controller = controller;
    }

    private double extendPower = .75;

    public double getExtendPower() {
        return extendPower;
    }

    private double retractPower = -.75;

    public double getRetractPower() {
        return retractPower;
    }

    private double initPower;

    private double maxPosition = 15.25;

    public double getMaxPosition() {
        return maxPosition;
    }

    private double initPosition = 0.0;
    private double transferPosition = .25;
    private double intakePosition = maxPosition;
    private double intakePositionSample3=5.25;
    private double bucketClearancePosition = 1.0;
    private double outtakePosition = 2.0;

    private boolean movingToTransfer = false;


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ITDExtensionArm(HardwareMap hardwareMap, Telemetry telemetry) {
        extensionArm = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                ITDRobot.HardwareName.EXTENSION_ARM.hwName,
                ITDRobot.HardwareName.EXTENSION_ARM_EXTENSION_LIMIT_SWITCH.hwName,
                ITDRobot.HardwareName.EXTENSION_ARM_RETRACTION_LIMIT_SWITCH.hwName,
                ITDRobot.HardwareName.EXTENSION_ARM_MOTOR.hwName,
                DcMotor8863.MotorType.GOBILDA_1150,
                4.80);

        extensionArm.setResetTimerLimitInmSec(10000);
        //extensionArm.setupStallDetection(1000, 30);
        //*********************************************
        // SET the lift powers here
        //*********************************************
        initPower = 0.2;

        extensionArm.setExtensionPower(extendPower);
        extensionArm.setRetractionPower(retractPower);
        //*********************************************
        // SET the lift max and min positions here
        //*********************************************
        extensionArm.setExtensionPositionInMechanismUnits(17.0);
        // remove the software retraction limit because the zero position is drifting out in front of the limit
        // switch and then the extension arm will never hit the limit switch
        //extensionArm.setRetractionPositionInMechanismUnits(0.05);
        extensionArm.setOverrideRetractionLimit(true);
        // the default PIDF for the 1120 motor stinks. Set our own.
        extensionArm.setPositionPIDFCoefficients(18.5);

        state = ExtensionArmState.IDLE;
        // init has not been started yet
        initComplete = false;

        timer = new ElapsedTime();
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    //*********************************************************************************************
    //          Commands
    //*********************************************************************************************

    public boolean isPositionReached() {
        if (state == ExtensionArmState.IDLE || state == ExtensionArmState.JOYSTICK_CONTROL) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isResetComplete() {
        return extensionArm.isResetComplete();
    }

    public void reset() {
        extensionArm.reset();
        controller.setExtensionArmResetComplete(false);
        state = ExtensionArmState.RESETING;
    }

    public void initPosition() {
        switch (state) {
            case IDLE:
            case JOYSTICK_CONTROL:
                logCommand("Init position");
                initPositionAction();
                controller.setExtensionArmPositionReached(false);
                state = ExtensionArmState.MOVING;
                break;
            case MOVING:
            case RESETING:
                logCommand("Init position command ignored");
                break;

        }
    }

    private void initPositionAction() {
        extensionArm.goToPosition(initPosition, initPower);
    }

    public void transferPosition() {
        switch (state) {
            case IDLE:
            case JOYSTICK_CONTROL:
                // allow a moving extension arm to be interrupted
            case MOVING:
                logCommand("Transfer position");
                transferPositionAction();
                controller.setExtensionArmPositionReached(false);
                state = ExtensionArmState.MOVING;
                break;

            case RESETING:
                logCommand("Transfer position command ignored");
                break;
        }
    }

    private void transferPositionAction() {
        extensionArm.goToFullRetractWithReset();
        movingToTransfer = true;
    }

    public void intakePosition() {
        switch (state) {
            case IDLE:
            case JOYSTICK_CONTROL:
                logCommand("Intake position");
                intakePositionAction();
                controller.setExtensionArmPositionReached(false);
                state = ExtensionArmState.MOVING;
                break;
            case MOVING:
            case RESETING:
                logCommand("Intake position command ignored");
                break;
        }
    }

    private void intakePositionAction() {
        extensionArm.goToPosition(intakePosition, extendPower);
    }

    public void bucketClearancePosition() {
        switch (state) {
            case IDLE:
            case JOYSTICK_CONTROL:
                logCommand("Bucket clearance position");
                bucketClearancePositionAction();
                controller.setExtensionArmPositionReached(false);
                state = ExtensionArmState.MOVING;
                break;
            case MOVING:
            case RESETING:
                logCommand("Bucket clearance position command ignored");
                break;

        }
    }

    private void bucketClearancePositionAction() {
        extensionArm.goToPosition(bucketClearancePosition, extendPower);
    }

    public void outtakePosition() {
        switch (state) {
            case IDLE:
            case JOYSTICK_CONTROL:
                logCommand("Outtake position");
                outtakePositionAction();
                controller.setExtensionArmPositionReached(false);
                state = ExtensionArmState.MOVING;
                break;
            case MOVING:
            case RESETING:
                logCommand("Outtake position command ignored");
                break;

        }
    }

    private void outtakePositionAction() {
        extensionArm.goToPosition(outtakePosition, extendPower);
    }

    public void goToPosition(double position) {
        goToPosition(position, extendPower);
    }

    public void goToPosition(double position, double power) {
        logCommand("Go to Position = " + position);
        controller.setExtensionArmPositionReached(false);
        extensionArm.goToPosition(position, power);
        state = ExtensionArmState.MOVING;
    }

    public void joystick(double power) {
        switch (state) {
            case IDLE:
            case JOYSTICK_CONTROL:
                joystickControlAction(power);
                controller.setExtensionArmPositionReached(false);
                state = ExtensionArmState.JOYSTICK_CONTROL;
                break;
            case MOVING:
            case RESETING:
                // don't do anything, ignore the command
                break;
        }
    }

    private void joystickControlAction(double power) {
        extensionArm.setPowerUsingJoystick(power);
    }

    @Override
    public boolean init(Configuration config) {
        // init is done by the Extension arm / Intake / Bucket controller
        return true;
    }

    @Override
    public void shutdown() {
        switch (state) {
            case IDLE:
            case MOVING:
            case JOYSTICK_CONTROL:
                logCommand("shutdown");
                transferPositionAction();
                controller.setExtensionArmPositionReached(false);
                state = ExtensionArmState.MOVING;
                break;
            case RESETING:
                break;
        }
    }

    //*********************************************************************************************
    //          Housekeeping stuff
    //*********************************************************************************************
    @Override
    public String getName() {
        return EXTENSION_ARM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logDataOnchange = new DataLogOnChange(logFile);
        extensionArm.setDataLog(logFile);
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
        extensionArm.enableDataLogging();
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
        extensionArm.disableDataLogging();
    }

    private void logState() {
        if (loggingOn && logFile != null) {
            logDataOnchange.log(getName() + " state = " + state.toString());
        }
    }

    private void logCommand(String command) {
        if (loggingOn && logFile != null) {
            logDataOnchange.log(getName() + " command = " + command);
        }
    }

    public void displayState(Telemetry telemetry) {
        telemetry.addData("Ext Arm State = ", state.toString());
    }

    public void displayPosition(Telemetry telemetry) {
        telemetry.addData("Ext Arm Pos = ", extensionArm.getPosition());
    }

    public void displayPIDF(Telemetry telemetry) {
        telemetry.addData("PIDF pos = ", extensionArm.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION).toString());
        telemetry.addData("PIDF vel = ", extensionArm.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER).toString());
    }

    public void setPositionPIDFCoefficients(double p) {
        extensionArm.setPositionPIDFCoefficients(p);
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    //*********************************************************************************************
    //          State machine
    //*********************************************************************************************

    @Override
    public void update() {
        extensionArm.update();
        logState();
        switch (state) {
            case RESETING:
                if (extensionArm.isResetComplete()) {
                    controller.setExtensionArmResetComplete(true);
                    state = ExtensionArmState.IDLE;
                }
                break;
            case IDLE:
                // don't do anything
                break;
            case MOVING:
                if (extensionArm.isPositionReached()) {
                    // tell the controller that the arm has reached its position
                    controller.setExtensionArmPositionReached(true);
                    // This is no longer needed. The extensionArmIntake controller will automatically
                    // start a bucket clearance after a transfer completes. This includes moving the
                    // extension arm away from the metal backstop.
//                    if (movingToTransfer) {
//                        extensionArm.goToPosition(0,.5);
//                        logCommand("move to 0");
//                        movingToTransfer = false;
//                    }
                    state = ExtensionArmState.IDLE;
                }
                break;
            case JOYSTICK_CONTROL:
                // nothing to do while running in joystick mode
                break;
        }

    }

}
