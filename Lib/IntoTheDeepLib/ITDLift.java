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
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;

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
        JOYSTICK_CONTROL,
        IDLE,
        MOVING_TO_DELIVERY_POSITION,
        MOVING_TO_TRANSFER_POSITION
    }

    private LiftState state;

    public enum Basket {
        HIGH_AUTO,
        HIGH_TELEOP,
        LOW_TELEOP;
     }
    private Basket deliveryHeight = Basket.HIGH_AUTO;

    public void setDeliveryHeight(Basket deliveryHeight) {
        this.deliveryHeight = deliveryHeight;
        switch(deliveryHeight) {
            case HIGH_AUTO:
                readyToDeliverPosition = readyToDeliverPositionHighBasketAuto;
                positionWhereWeSayDeliveryMovementIsCloseEnough = readyToDeliverPositionHighBasketAuto - 3;
                break;
            case HIGH_TELEOP:
                readyToDeliverPosition = readyToDeliverPositionHighBasketTeleop;
                positionWhereWeSayDeliveryMovementIsCloseEnough = readyToDeliverPositionHighBasketTeleop - 3;
                break;
            case LOW_TELEOP:
                readyToDeliverPosition = readyToDeliverPositionLowBasketTeleop;
                positionWhereWeSayDeliveryMovementIsCloseEnough = readyToDeliverPositionLowBasketTeleop - 2;
                break;
        }
    }

    public Basket getDeliveryHeight() {
        return deliveryHeight;
    }

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
    // private double readyToDeliverPosition = 18.5;
    // private double readyToDeliverPosition = 23.0;
    private double readyToDeliverPositionLowBasketTeleop = 2.25;
    private double readyToDeliverPositionHighBasketAuto= 19.75;
    private double readyToDeliverPositionHighBasketTeleop = 20.5;
    private double readyToDeliverPosition = 20.5;
    private double lowBarHangPosition = 6.0;
    private double highBarHangPosition = 4.0;

    // Old singlelift heights
    //    private double readyToDeliverPositionHighBasketTeleop = 24.5;
    //    // private double readyToDeliverPosition = 18.5;
    //    // private double readyToDeliverPosition = 23.0;
    //    private double readyToDeliverPositionLowBasketTeleop = 5;
    //    private double readyToDeliverPositionHighBasketAuto= 22.75;
    //    private double readyToDeliverPosition = 24.5;
    //    private double lowBarHangPosition = 6.0;
    //    private double highBarHangPosition = 4.0;

    // The lift hunts for its final position for a while. We don't want that to delay the start of
    // of the transfer. So we will call the lift movement complete when it is not quite to the
    // final position yet.
    private double positionWhereWeSayDeliveryMovementIsCloseEnough;

    // The bucket arm movement to the transfer position will be triggered when the lift passes
    // this position on the way down.
    private double positionToMoveBucketArmToTransfer = 3.0;


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ITDLift(HardwareMap hardwareMap, Telemetry telemetry) {
        // old lift movementPerRevolution = 4.517
        lift = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                ITDRobot.HardwareName.LIFT.hwName,
                ITDRobot.HardwareName.LIFT_LIMIT_SWITCH_EXTENSION.hwName,
                ITDRobot.HardwareName.LIFT_LIMIT_SWITCH_RETRACTION.hwName,
                ITDRobot.HardwareName.LIFT_MOTOR.hwName,
                DcMotor8863.MotorType.GOBILDA_1150,
                7.125);
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
        if (MatchPhase.getMatchPhase()==MatchPhase.TELEOP){
            setDeliveryHeight(Basket.HIGH_TELEOP);
        }else {
            setDeliveryHeight(Basket.HIGH_AUTO);
        }
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
                state = LiftState.MOVING_TO_TRANSFER_POSITION;
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
            // allow the command if the lift is:
            case IDLE:
            case JOYSTICK_CONTROL:
                logCommand("Delivery position");
                readyToDeliverPositionAction();
                controller.setLiftPositionReached(false);
                state = LiftState.MOVING_TO_DELIVERY_POSITION;
                break;
                // do not allow the command if the lift is:
            case MOVING:
            case RESETING:
                // don't do anything, ignore the command
                break;
        }
    }

    private void readyToDeliverPositionAction() {
        // since the lift is up in the air, the motor needs to work against gravity or it will fall
        lift.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        // raise the lift to the height needed to delivery the sample into the basket
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
            case MOVING_TO_DELIVERY_POSITION:
                // short cut the lift hunting for final position
                if (lift.getCurrentPosition() >= positionWhereWeSayDeliveryMovementIsCloseEnough) {
                    // tell the controller that the lift has reached its position
                    controller.setLiftPositionReached(true);
                    state = LiftState.IDLE;
                }
                break;
            case MOVING_TO_TRANSFER_POSITION:
                // short cut the lift hunting for final position
                if (lift.getCurrentPosition() <= positionToMoveBucketArmToTransfer) {
                    // tell the controller that the lift has reached its position
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
