package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class ITDLift implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum LiftState {
        RESETING,
        MOVING,
        MOVING_TO_READY_TO_DELIVER_POSITION,
        JOYSTICK_CONTROL,
        IDLE
    }

    private LiftState state;

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
    private final String LIFT_NAME = ITDRobot.HardwareName.LIFT.hwName;
    ExtensionRetractionMechanism lift;

    /**
     * We need a reference to the controller so that we can communicate with it.
     * @param controller
     */
    private ITDLiftBucketArmBucketGateController controller;
    public void setController(ITDLiftBucketArmBucketGateController controller) {
        this.controller = controller;
    }

    private double extendPower;
    private double retractPower;
    private double initPower;

    private double initPosition = 0.0;
    private double transferPosition = 0;
    private double startMovingBucketArmPosition = 21.0;
    private double readyToDeliverPosition = 23.0;
    private double lowBarHangPosition = 6.0;
    private double highBarHangPosition = 4.0;


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ITDLift(HardwareMap hardwareMap, Telemetry telemetry) {
        lift = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                ITDRobot.HardwareName.LIFT.hwName,
                ITDRobot.HardwareName.LIFT_LIMIT_SWITCH_EXTENSION.hwName,
                ITDRobot.HardwareName.LIFT_LIMIT_SWITCH_RETRACTION.hwName,
                ITDRobot.HardwareName.LIFT_MOTOR.hwName,
                DcMotor8863.MotorType.GOBILDA_1150,
                4.517);
        lift.setResetTimerLimitInmSec(5000);
        //*********************************************
        // SET the lift powers here
        //*********************************************
        initPower = .2;
        extendPower =1;
        retractPower =-1;
        lift.setExtensionPower(extendPower);
        lift.setRetractionPower(retractPower);
        //*********************************************
        // SET the lift max and min positions here
        //*********************************************
        lift.setExtensionPositionInMechanismUnits(29);
        lift.setRetractionPositionInMechanismUnits(0.05);

        state = LiftState.IDLE;
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
        if (state == LiftState.IDLE || state == LiftState.JOYSTICK_CONTROL) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isResetComplete() {
        return lift.isResetComplete();
    }

    public void reset() {
        lift.reset();
        controller.setLiftResetComplete(false);
        logCommand("Reset");
        state = LiftState.RESETING;
    }

    public void initPosition() {
        switch (state) {
            case IDLE:
            case JOYSTICK_CONTROL:
                logCommand("Init position");
                initPositionAction();
                controller.setLiftPositionReached(false);
                state = LiftState.MOVING;
                break;
            case MOVING:
            case RESETING:
                // don't do anything, ignore the command
                break;

        }
    }

    private void initPositionAction() {
        // since the transfer position is at the bottom of the lift, and tolerances might cause the
        // lift to try to retract past the physical limit, we don't want to run the motor and hold
        // position
        lift.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        lift.goToPosition(initPosition, initPower);
    }

    public void transferPosition() {
        switch (state) {
            case IDLE:
            case JOYSTICK_CONTROL:
                logCommand("Transfer position");
                transferPositionAction();
                controller.setLiftPositionReached(false);
                state = LiftState.MOVING;
                break;
            case MOVING:
            case RESETING:
                // don't do anything, ignore the command
                break;
        }
    }

    private void transferPositionAction() {
        // since the transfer position is at the bottom of the lift, and tolerances might cause the
        // lift to try to retract past the physical limit, we don't want to run the motor and hold
        // position
        lift.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        lift.goToPosition(transferPosition, retractPower);
    }

    public void readyToDeliverPosition() {
        switch (state) {
            case IDLE:
            case JOYSTICK_CONTROL:
                logCommand("Delivery position");
                readyToDeliverPositionAction();
                controller.setLiftPositionReached(false);
                state = LiftState.MOVING_TO_READY_TO_DELIVER_POSITION;
                break;
            case MOVING:
            case RESETING:
                // don't do anything, ignore the command
                break;
        }
    }

    private void readyToDeliverPositionAction() {

        // since the lift is up in the air, the motor needs to work against gravity or it will fall
        lift.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        lift.goToPosition(readyToDeliverPosition, extendPower);
    }

    public void lowBarHangPosition() {
        switch (state) {
            case IDLE:
            case JOYSTICK_CONTROL:
                logCommand("Low bar hang position");
                lowBarHangPositionAction();
                controller.setLiftPositionReached(false);
                state = LiftState.MOVING;
                break;
            case MOVING:
            case RESETING:
                // don't do anything, ignore the command
                break;

        }
    }

    private void lowBarHangPositionAction() {
        // since the lift is up in the air, the motor needs to work against gravity or it will fall
        lift.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        lift.goToPosition(lowBarHangPosition, extendPower);
    }

    public void highBarHangPosition() {
        switch (state) {
            case IDLE:
            case JOYSTICK_CONTROL:
                logCommand("High bar hang position");
                highBarHangPositionAction();
                controller.setLiftPositionReached(false);
                state = LiftState.MOVING;
                break;
            case MOVING:
            case RESETING:
                // don't do anything, ignore the command
                break;

        }
    }

    private void highBarHangPositionAction() {
        // since the lift is up in the air, the motor needs to work against gravity or it will fall
        lift.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        lift.goToPosition(highBarHangPosition, extendPower);
    }

    public void joystick(double power) {
        switch (state) {
            case IDLE:
            case JOYSTICK_CONTROL:
                joystickControlAction(power);
                controller.setLiftPositionReached(false);
                state = LiftState.JOYSTICK_CONTROL;
                break;
            case MOVING:
            case RESETING:
                // don't do anything, ignore the command
                break;
        }
    }

    private void joystickControlAction(double power) {
        lift.setPowerUsingJoystick(power);
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
                controller.setLiftPositionReached(false);
                state = LiftState.MOVING;
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
        return LIFT_NAME;
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
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
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
        telemetry.addData("Lift state = ", state.toString());
    }

    public void displayPosition(Telemetry telemetry) {
        telemetry.addData("Lift Pos = ", lift.getPosition());
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    //*********************************************************************************************
    //          State machine
    //*********************************************************************************************

    @Override
    public void update() {
        logState();
        lift.update();
        switch (state) {
            case RESETING:
                if (lift.isResetComplete()) {
                    controller.setLiftResetComplete(true);
                    state = LiftState.IDLE;
                }
                break;
            case IDLE:
                // don't do anything
                break;
            case MOVING:
                if (lift.isPositionReached()) {
                    // tell the controller that the arm has reached its position
                    controller.setLiftPositionReached(true);
                    state = LiftState.IDLE;
                }
                break;
            case MOVING_TO_READY_TO_DELIVER_POSITION:
                // even though the lift has not reached its final position, the bucket arm servo
                // can be moved.
                // 1 - the lift has slowed down enough that the acceleration will not
                // damage the servo.
                // 2 - the lift seems to hunt for its final position and waiting
                // for it to get there delays the movement of the bucket arm to the delivery position.
                if (lift.getCurrentPosition() >= startMovingBucketArmPosition) {
                    // lie to the controller and tell it the lift has arrived at its final position
                    // Well it will soon enough
                    controller.setLiftPositionReached(true);
                    state = LiftState.IDLE;
                }
                break;
            case JOYSTICK_CONTROL:
                // nothing to do while running in joystick mode
                break;
        }

    }

}
