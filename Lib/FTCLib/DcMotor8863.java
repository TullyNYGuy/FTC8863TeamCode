package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by ball on 11/28/2015.
 */
public class DcMotor8863 {

    /**
     * Defines the type of motor.
     */
    public enum MotorType {
        NXT, ANDYMARK_20, ANDYMARK_40, ANDYMARK_60, TETRIX
    }

    /**
     * Defines the state of the motor. For use in the state machine
     * IDLE = not moving, able to move freely (float)
     * HOLD = not moving but actively holding a position under PID control
     * MOVING = actvitely rotating
     * STALLED = a stall of the motor was detected
     * COMPLETE_HOLD = movement of the motor has completed (target reached) and the motor is actively
     * holding its position under PID control
     * COMPLETE_FLOAT = movement of the motor has completed (target reached) and the motor is allowed
     * to float freely
     */
    public enum MotorState {
        IDLE, HOLD, MOVING, STALLED, COMPLETE_HOLD, COMPLETE_FLOAT
    }

    /**
     * What to do when the current motor movement finishes
     * COAST = power removed, the motor will move freely
     * HOLD = motor is powered and actively holding a position under PID control
     */
    public enum FinishBehavior {
        FLOAT, HOLD
    }

    /**
     * Does the motor track a series of movements with its encoders?
     * ABSOLUTE = track movements so encoders will not be reset
     * RELATIVE = every movement is individual so encoders will be reset
     */
    public enum MotorMoveType {
        ABSOLUTE, RELATIVE
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private com.qualcomm.robotcore.hardware.DcMotor FTCDcMotor;

    /**
     * Type of motor. Controls the encoder counts per revolution
     */
    private MotorType motorType = MotorType.ANDYMARK_40;

    /**
     * Encoder counts per shaft revolution for this type of motor
     */
    private int countsPerRev = 0;

    /**
     * Number of cm or degrees or whatever moved for each motor shaft revolution
     */
    private double MovementPerRev = 0;

    /**
     * Holds the desired encoder count for RUN_TO_POSITION
     */
    private int targetEncoderCount = 0;

    /**
     * The tolerance range for saying if the encoder count target has been reached.
     */
    private int targetEncoderTolerance = 0;

    /**
     * The current state of the motor.
     */
    private MotorState currentMotorState = MotorState.IDLE;

    /**
     * The desired action of the motor after the rotation is finished.
     */
    private FinishBehavior finishBehavior = FinishBehavior.FLOAT;

    /**
     * Relative or absolute movements
     */
    private MotorMoveType motorMoveType = MotorMoveType.RELATIVE;

    /**
     * Minimum power for this motor
     */
    private double minMotorPower = -1;

    /**
     * Maximum power for this motor
     */
    private double maxMotorPower = 1;

    /**
     * motor direction
     */
    private DcMotor.Direction direction = com.qualcomm.robotcore.hardware.DcMotor.Direction.FORWARD;

    /**
     * last encoder value
     */
    private int lastEncoderValue = 0;

    /**
     * current value of the encoder. NOTE: this may not be the same as the encoder value of the
     * underlying DCMotor. This is a separate copy of the encoder value. It may not be updated with
     * the value of the encoder on the actual motor so it may not match. Or this value can be
     * manipulated so that it is set to 0 even though the underlying encoder has not been reset.
     * Note that for whatever reason the SDK forces the motor to stop when the actual encoder is
     * reset. If we are just keeping track of a series of movements, we may not want the motor to
     * stop even though we want the encoder to be set to 0 again.
     * In essence, this is a virtual encoder.
     * Setting this value to the actual motor encoder value before starting a movement, and making
     * the target encoder value = currentEncoderValue + Encoder Ticks needed for movement effectively
     * implements a relative movement. Like saying go 2 miles to the stop sign, turn right, and then
     * go 10 miles to
     */
    private int currentEncoderValue = 0;

    /**
     * enables whether you detect a stall
     */
    private boolean stallDetectionEnabled = false;

    /**
     * timer used for stall detection
     */
    private ElapsedTime stallTimer;

    /**
     * if the motor is not moving for longer than this time limit the motor is stalled
     */
    private double stallTimeLimit = 0;

    /**
     * if the encoder changes less than this since the last time we read it than we call it a stall.
     */
    private int stallDetectionTolerance = 5;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //*********************************************************************************************

    /**
     * @return Return the instance of the DCMotor associated with this instance of the DCMotor8863.
     */
    public DcMotor getMotorInstance() {
        return FTCDcMotor;
    }

    public MotorType getMotorType() {
        return motorType;
    }

    public void setMotorType(MotorType motorType) {
        this.motorType = motorType;
        setCountsPerRevForMotorType(motorType);
    }

    public int getCountsPerRev() {
        return countsPerRev;
    }

    private void setCountsPerRev(int countsPerRev) {
        this.countsPerRev = countsPerRev;
    }

    public double getMovementPerRev() {
        return MovementPerRev;
    }

    public void setMovementPerRev(double MovementPerRev) {
        this.MovementPerRev = MovementPerRev;
    }

    private int getTargetEncoderCount() {
        return targetEncoderCount;
    }

    private void setTargetEncoderCount(int targetEncoderCount) {
        this.targetEncoderCount = targetEncoderCount;
    }

    public int getTargetEncoderTolerance() {
        return targetEncoderTolerance;
    }

    public void setTargetEncoderTolerance(int targetEncoderTolerance) {
        this.targetEncoderTolerance = targetEncoderTolerance;
    }

    public MotorState getMotorState() {
        return currentMotorState;
    }

    private void setMotorState(MotorState currentMotorState) {
        this.currentMotorState = currentMotorState;
    }

    public FinishBehavior getFinishBehavior() {
        return finishBehavior;
    }

    public void setFinishBehavior(FinishBehavior finishBehavior) {
        this.finishBehavior = finishBehavior;
        if (finishBehavior == FinishBehavior.FLOAT) {
            setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        } else {
            setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }

    public MotorMoveType getMotorMoveType() {
        return motorMoveType;
    }

    public void setMotorMoveType(MotorMoveType motorMoveType) {
        this.motorMoveType = motorMoveType;
    }

    public double getMinMotorPower() {
        return minMotorPower;
    }

    public void setMinMotorPower(double minMotorPower) {
        this.minMotorPower = minMotorPower;
    }

    public double getMaxMotorPower() {
        return maxMotorPower;
    }

    public void setMaxMotorPower(double maxMotorPower) {
        this.maxMotorPower = maxMotorPower;
    }

    public DcMotor.Direction getDirection() {
        return direction;
    }

    public double getStallTimeLimit() {
        return stallTimeLimit;
    }

    public void setStallTimeLimit(double stallTimeLimit) {
        this.stallTimeLimit = stallTimeLimit;
    }

    public boolean isStallDetectionEnabled() {
        return stallDetectionEnabled;
    }

    public void setStallDetectionEnabled(boolean stallDetectionEnabled) {
        this.stallDetectionEnabled = stallDetectionEnabled;
    }

    public int getStallDetectionTolerance() {
        return stallDetectionTolerance;
    }

    public void setStallDetectionTolerance(int stallDetectionTolerance) {
        this.stallDetectionTolerance = stallDetectionTolerance;
    }

    public int getLastEncoderValue() {
        return lastEncoderValue;
    }

    public MotorState getCurrentMotorState() {
        return currentMotorState;
    }

    //*********************************************************************************************
    //          Constructors
    //*********************************************************************************************

    public DcMotor8863(String motorName, HardwareMap hardwareMap) {
        FTCDcMotor = hardwareMap.dcMotor.get(motorName);
        stallTimer = new ElapsedTime();
        initMotorDefaults();
    }

    /**
     * Set some reasonable defaults for a motor. The user should then set the real values.
     */
    private void initMotorDefaults() {
        setMotorType(MotorType.ANDYMARK_40);
        setMovementPerRev(0);
        setStallDetectionEnabled(false);
        setTargetEncoderCount(0);
        setTargetEncoderTolerance(10);
        setMotorState(MotorState.IDLE);
        setFinishBehavior(FinishBehavior.FLOAT);
        setMotorMoveType(MotorMoveType.RELATIVE);
        setMinMotorPower(-1);
        setMaxMotorPower(1);
        setStallDetectionTolerance(5);
        setStallTimeLimit(0);
    }

    //*********************************************************************************************
    //          Helper Methods
    //*********************************************************************************************

    /**
     * Get the number of encoder counts per revolution of the shaft based on the type of motor.
     *
     * @param motorType Type of motor.
     * @return Number of encoder counts per revolution of the output shaft of the motor
     */
    public int setCountsPerRevForMotorType(MotorType motorType) {

        switch (motorType) {
            case NXT:
                setCountsPerRev(360);
                break;
            case ANDYMARK_20:
                setCountsPerRev(560);
                break;
            case ANDYMARK_40:
                setCountsPerRev(1120);
                break;
            case ANDYMARK_60:
                setCountsPerRev(1680);
                break;
            case TETRIX:
                setCountsPerRev(1440);
                break;
            default:
                setCountsPerRev(0);
                break;
        }
        return getCountsPerRev();
    }

    /**
     * Calculate the number or motor revolutions needed to move whatever is attached to the motor
     * a certain amount. It uses the MovementPerRev value for the calculation.
     *
     * @param movement The amount to move whatever is attached. It could be degrees, cm or any
     *                 other units.
     * @return Number of motor revolutions to turn.
     */
    public double getRevsForMovement(double movement) {
        return movement / getMovementPerRev();
    }

    /**
     * Calculate the number of encoder counts needed to move whatever is attached to the motor
     * a certain amount. It uses the MovementPerRev value and number of encoder counts per revolution
     * for the calculation. The number of encoder counts per rev is dependent on the motor type.
     *
     * @param movement The amount to move whatever is attached. It could be degrees, cm or any
     *                 other units.
     * @return Number of encoder counts to turn to create the movement.
     */
    public int getEncoderCountForMovement(double movement) {
        return (int) (getCountsPerRev() * getRevsForMovement(movement));
    }

    /**
     * Calculate the "movement" that whatever is attached to the motor has moved based on the
     * encoder counts given. The movement can be the number of degrees the motor has moved, the
     * number of cm a wheel attached to the motor has turned etc. It uses the MovementPerRev and
     * CountsPerRev defined when the motor object is setup.
     *
     * @param encoderCount The position of the motor as given by the encoder count
     * @return How far the motor has moved whatever is attached to it.
     */
    public double getMovementForEncoderCount(int encoderCount) {
        return (double) (encoderCount / getCountsPerRev() * getMovementPerRev());
    }   //10/26/16 Matt Was Here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    /**
     * Gets the number of encoder counts for a certain number of revolutions.
     *
     * @param revs number of revolutions
     * @return encoder counts
     */
    public int getEncoderCountForRevs(double revs) {
        return (int) (getCountsPerRev() * revs);
    }

    /**
     * If the motor is set for relative movement, the encoder will be reset. But if the motor
     * is set for absolute movement, the encoder needs to keep track of where the motor is, so
     * it cannot be reset.
     */
    public void resetEncoder() {
        if (getMotorMoveType() == MotorMoveType.RELATIVE) {
            this.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }

    /**
     * If true is set, reset the encoder, no matter whether the motor is set for relative or
     * absolute movement.
     * Use this method to set the zero point on a motor that will be moved absolute from now on.
     *
     * @param override If true, then reset the encoder, no matter what.
     */
    public void resetEncoder(boolean override) {
        if (override) {
            this.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        } else {
            resetEncoder();
        }
    }

    //*********************************************************************************************
    //          Methods for rotating the motor to a desired position
    //*********************************************************************************************

    // Movements can be relative or absolute.
    // Relative movement is a movement that assumes you are always starting your movement from 0 and
    // moving a certain amount. For example go 2 miles, turn right, then go 10 miles. Each one of the
    // distances assumes you are starting over from zero.
    // Absolute movement is a movement that assumes a certain 0 point and measures everything from
    // that 0 point. The 0 point never changes. For example, 0 feet altitude is sea level. All
    // altitudes are always measured from there. Go to 10,000 feet. Then go to 12,000 feet. You
    // don't say go to 10,000 feet. Then go 2000 feet more. That would be relative.
    //
    // The encoder on the motor starts at 0 when the motor is initialized. Resetting the enocoder
    // to 0 later on causes the motor to stop and enter an unknown state. So we don't want to reset
    // the encoder.
    //
    // Rather than resetting the encoder to 0 to make a relative movement, we use a formula instead:
    // target encoder value = current encoder value plus the number of encoder ticks needed to move
    // the desired amount.
    //
    // An absolute movement is:
    // target encoder value = number of encoder ticks for the desired position

    /**
     * An absolute movement:
     * Rotate the motor so that the thing connected to the motor moves to a certain position. The
     * encoder is used to determine if the position has been reached. This method provides a way
     * to move whatever the motor is connected to in terms of that thing's position rather than in
     * encoder counts which is the position of the motor.
     * This moves the object to a certain position, not by a certain amount. I.E. an absolute
     * movement.
     * For example, move a claw to 5 cm position.
     *
     * @param power           Power input for the motor. Note that it will be clipped to less than +/-0.8.
     * @param targetPosition  Motor will be rotated so that this position of the object is reached.
     * @param afterCompletion What to do after this movement is completed: HOLD or FLOAT
     * @return true if successfully completed
     */
    public boolean moveToPosition(double power, double targetPosition, FinishBehavior afterCompletion) {
        // Protect the movement from getting interrupted by another call to rotateToPosition. The
        // movement has to finish before another one can be started.
        if (this.currentMotorState != MotorState.MOVING) {
            // figure out what the encoder count is that corresponds to the target position
            int encoderCountForPosition = getEncoderCountForMovement(targetPosition);
            return rotateToEncoderCount(power, encoderCountForPosition, afterCompletion);
        } else {
            return false;
        }
    }

    /**
     * A relative movement:
     * Rotate the motor so that the thing connected to the motor moves a certain amount. The encoder
     * is used to determine when the movement is complete. This method provides a way
     * to move whatever the motor is connected to in terms of that thing's position rather than in
     * encoder counts which is the position of the motor.
     * This moves the object by a certain amount, not to a certain position. I.E. a relative
     * movement.
     * For example, open a claw by 5 cm.
     *
     * @param power
     * @param movement
     * @param afterCompletion
     * @return
     */
    public boolean moveByAmount(double power, double movement, FinishBehavior afterCompletion) {
        // Protect the movement from getting interrupted by another call to rotateToPosition. The
        // movement has to finish before another one can be started.
        if (this.currentMotorState != MotorState.MOVING) {
            // figure out what the encoder count is that corresponds to the amount to be moved
            int encoderCountForMovement = getEncoderCountForMovement(movement);
            // add that to the current encoder count
            int encoderCountForPosition = encoderCountForMovement + this.getCurrentPosition();
            return rotateToEncoderCount(power, encoderCountForPosition, afterCompletion);
        } else {
            return false;
        }
    }

    public boolean rotateNumberOfDegrees(double power, double degrees, FinishBehavior afterCompletion) {
        return true;
    }

    /**
     * Makes the motor rotate to a certain amount of revolutions.
     *
     * @param power           The power at which the motor moves
     * @param revs            The amount of revolutions you want it to go.
     * @param afterCompletion Whether it holds or floats after completion.
     * @return If return is true then it actually did it.
     */
    public boolean rotateNumberOfRevolutions(double power, double revs, FinishBehavior afterCompletion) {
        return rotateToEncoderCount(power, getEncoderCountForRevs(revs), afterCompletion);
    }

    /**
     * Rotate the motor so the encoder gets to a certain count.
     *
     * @param power           Power input for the motor. Note that it will be clipped to less than +/-0.8.
     * @param encoderCount    Motor will be rotated so that it results in a movement of this distance.
     * @param afterCompletion What to do after this movement is completed: HOLD or COAST
     * @return true if successfully completed
     */
    public boolean rotateToEncoderCount(double power, int encoderCount, FinishBehavior afterCompletion) {
        // set what to do after the rotation completes
        setFinishBehavior(afterCompletion);
        // reset the encoder
        //resetEncoder();
        // set the desired encoder position
        this.setTargetPosition(encoderCount);
        // set the run mode
        this.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // clip the power so that it does not exceed 80% of max. The reason for this is that the
        // PID controller inside the core motor controller needs to have some room to increase
        // the power when it makes its PID adjustments. If you set the power to 100% then there is
        // no room to increase the power if needed and PID control will not work.
        power = Range.clip(power, -.8, .8);
        this.setPower(power);
        setMotorState(MotorState.MOVING);
        return true;
    }

    /**
     * Checks to see if the rotation to encoder count has completed. If it has then it sets the
     * motor to hold that encoder count or it sets the motor so that it can move freely, depending
     * on the FinishBehavior being set to HOLD or COAST.
     * Use this method in a loop to see if the movement has finished. Like and opmode loop() or in
     * a while do loop.
     *
     * @return true if movement complete
     */
    private boolean isRotationComplete() {
        return !FTCDcMotor.isBusy();
//        if (FTCDcMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
//            // The motor is moving to a certain encoder position so it will complete movement at
//            // some point.
//            // get the current encoder position
//            int currentEncoderCount = this.getCurrentPosition();
//            // is the current position within the tolerance limit of the desired position?
//            if (Math.abs(getTargetEncoderCount() - currentEncoderCount) < getTargetEncoderTolerance()) {
//                // movement is complete
//                return true;
//            } else {
//                // movement is not finished yet
//                return false;
//            }
//        } else {
//            // The motor is not moving to a position so there cannot be a point when the rotation is
//            // complete
//            return false;
//        }

    }

    public void setupStallDetection(double stallTimeLimit, int stallDetectionTolerance) {
        setStallDetectionEnabled(true);
        setStallDetectionTolerance(stallDetectionTolerance);
        setStallTimeLimit(stallTimeLimit);
        stallTimer.reset();
        this.lastEncoderValue = this.getCurrentPosition();
    }

    private boolean isStalled() {
        int currentEncoderValue = this.getCurrentPosition();
        if (currentMotorState == MotorState.MOVING && isStallDetectionEnabled()) {
            // if the motor has not moved since the last time the position was read
            if (Math.abs(currentEncoderValue - lastEncoderValue) < stallDetectionTolerance) {
                // motor has not moved, checking to see how long the motor has been stalled for
                if (stallTimer.time() > stallTimeLimit) {
                    // it has been stalled for more than the time limit
                    return true;
                }
            } else {
                // reset the timer because the motor is not stalled
                stallTimer.reset();
            }
        }
        this.lastEncoderValue = currentEncoderValue;
        return false;
    }

    // How much does the encoder change in one cycle of the loop?

    // Example: motor is moving at 150 RPM, loop is 20 mSec.
    // 150 Rev / min * 1 min / 60 sec = 2.5 rev / sec.
    // An andymark 40 motor has 1120 encoder counts per rev so
    // 2.5 rev / sec * 1120 counts / rev = 2800 counts / sec
    // 2800 counts / sec * 1 sec / 1000 mSec = 2.8 counts / mSec
    // 2.8 counts / mSec * 20 mSec = 58 counts per loop
    // RESULT - encoder changes 58 counts in one cycle of the loop.

    //*********************************************************************************************
    //          Methods for rotating the motor at a constant speed
    //*********************************************************************************************

    /**
     * Run the motor at a constant speed using encoder feedback. If there is a load on the motor the
     * power will be increased in an attempt to maintain the speed. Note that a command for a high
     * speed may require more power than is available to run the motor at that speed. So it may
     * result in the PID not being able to control the motor and the motor will lose speed under
     * load.
     * <p>
     * Deprecated because the name of this method is not intuitive. RunAtConstantSpeed is a better
     * name.
     *
     * @param power Power input for the motor.
     * @return true if successfully completed
     */
    @Deprecated
    public boolean runUsingEncoder(double power) {
        return runAtConstantSpeed(power);
    }

    /**
     * Run the motor at a constant speed using encoder feedback. If there is a load on the motor the
     * power will be increased in an attempt to maintain the speed. Note that a command for a high
     * speed may require more power than is available to run the motor at that speed. So it may
     * result in the PID not being able to control the motor and the motor will lose speed under
     * load.
     *
     * @param power Power input for the motor.
     * @return true if successfully completed
     */
    public boolean runAtConstantSpeed(double power) {
        if (getMotorState() != MotorState.MOVING) {
            // reset the encoder
            this.resetEncoder();
        }
        // set the run mode
        this.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.setMotorState(MotorState.MOVING);
        this.setPower(power);
        return true;
    }

    //*********************************************************************************************
    //    Methods for rotating the motor without encoders - open loop - run at a constant power
    //*********************************************************************************************

    /**
     * Run the motor at a constant power without any encoder feedback. If there is a load on the
     * motor the speed will decrease. If you don't want the speed to decrease, use
     * runAtConstantSpeed instead.
     * <p>
     * Deprecated because the method name is not intuitive. runAtConstantPower is a better name.
     *
     * @param power Power input for the motor.
     * @return true if successfully completed
     */
    @Deprecated
    public boolean runWithoutEncoder(double power) {
        return (runAtConstantPower(power));
    }

    /**
     * This method sets the motor controller to the mode for running the motor at a constant power.
     * I could do this inside runAtConstantPower but since that is called inside a loop with updates
     * to the power from a PID output, I don't want to incur the performance penalty of doing it in
     * a loop a million times.
     * MAKE SURE YOU RUN THIS FIRST BEFORE CALLING runAtConstantPower
     */
    public void runAtConstantPowerSetup() {
        this.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
    /**
     * Run the motor at a constant power without any encoder feedback. If there is a load on the
     * motor the speed will decrease. If you don't want the speed to decrease, use
     * runAtConstantSpeed instead.
     * MAKE SURE TO CALL runAtConstantPowerSetup first.
     *
     * @param power Power input for the motor.
     * @return true if successfully completed
     */
    public boolean runAtConstantPower(double power) {
        // I want to be able to change the power even if the motor is already moving
        this.setMotorState(MotorState.MOVING);
        this.setPower(power);
        return true;
    }

    //*********************************************************************************************
    //          Methods for stopping a motor
    //*********************************************************************************************

    /**
     * Interrupt the motor.
     * If the next motor state is hold, then the encoder position at the time
     * of the interrupt is set as the hold point. The motor will actively hold at that encoder
     * position.
     * If the next motor state is coast, then just shut off the motor power and the motor will
     * move freely.
     */
    public void interruptMotor() {
        if (getFinishBehavior() == FinishBehavior.HOLD) {
            setMotorToHold();
        } else {
            setMotorToFloat();
        }
    }

    /**
     * Stop the motor. It will just coast, ie rotate freely.
     */
    public void stopMotor() {
        resetEncoder(true);
        setMotorState(MotorState.IDLE);
    }

    /**
     * Set the motor to stop and actively hold its position. If something tries to turn the motor,
     * it will resist that by applying enough power to hold its position.
     */
    public void setMotorToHold() {
        this.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        setMotorState(MotorState.HOLD);
        this.setPower(0);
    }

    /**
     * Set the motor to stop and just coast. It will turn freely. If there is something attached to
     * the motor when power is turned off it will slowly come to a stop.
     */
    public void setMotorToFloat() {
        this.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        setMotorState(MotorState.IDLE);
        this.setPower(0);
    }

    //*********************************************************************************************
    //          State Machine
    //*********************************************************************************************

    public MotorState update() {
        switch (getCurrentMotorState()) {
            case IDLE:
                break;
            case HOLD:
                break;
            case MOVING:
                if (stallDetectionEnabled && isStalled()) {
                    setMotorState(MotorState.STALLED);
                }
                // Rotation can only be complete if the motor is set to RUN_TO_POSITION
                if (isRotationComplete()) {
                    if (getFinishBehavior() == FinishBehavior.FLOAT) {
                        setMotorState(MotorState.COMPLETE_FLOAT);
                        // don't have to set the motor to actually float. That is done automatically
                        // by the controller once the RUN_TO_POSITION is complete
                        // But we do have to set the motor power to 0
                        setPower(0.0);
                    } else {
                        setMotorState(MotorState.COMPLETE_HOLD);
                        // don't have to set the motor to actually hold / brake. That is done automatically
                        // by the controller once the RUN_TO_POSITION is complete
                    }
                }
                break;
            case STALLED:
                break;
            case COMPLETE_FLOAT:
                break;
            case COMPLETE_HOLD:
                break;
        }
        return getCurrentMotorState();
    }

    //*********************************************************************************************
    //          Wrapper Methods
    //*********************************************************************************************
    public void setMode(DcMotor.RunMode mode) {
        if (mode == DcMotor.RunMode.STOP_AND_RESET_ENCODER) {
            this.setMotorState(MotorState.IDLE);
        }
        FTCDcMotor.setMode(mode);
    }

    public void setPower(double power) {
        power = Range.clip(power, getMinMotorPower(), getMaxMotorPower());
        FTCDcMotor.setPower(power);
    }

    @Deprecated
    public void setPowerFloat() {
        FTCDcMotor.setPowerFloat();
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior ZeroPowerBehavior) {
        FTCDcMotor.setZeroPowerBehavior(ZeroPowerBehavior);
    }

    public void setTargetPosition(int position) {
        // set the field holding the desired rotation
        setTargetEncoderCount(position);
        FTCDcMotor.setTargetPosition(position);
    }

    /**
     * Get the current encoder count for the motor.
     *
     * @return current encoder count
     */
    public int getCurrentPosition() {
        return FTCDcMotor.getCurrentPosition();
    }

    public void setDirection(DcMotor.Direction direction) {
        FTCDcMotor.setDirection(direction);
        this.direction = direction;
    }

}
