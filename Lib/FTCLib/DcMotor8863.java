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
     * Defines the state of the motor
     * IDLE = not moving, able to move freely (float)
     * HOLD = not moving but actively holding a position under PID control
     * MOVING = actvitely rotating
     * INTERUPTED = not sure about this one yet, thinking it may be needed to indicate when the
     *    motor movement was interupted before it completed
     */
    public enum MotorState {
        IDLE, HOLDING, INTERRUPTED, MOVING, STALLED, FLOATING
    }

    /**
     * What to do when the current motor movement finishes
     * COAST = power removed, the motor will move freely
     * HOLD = motor is powered and actively holding a position under PID control
     */
    public enum NextMotorState {
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
    public com.qualcomm.robotcore.hardware.DcMotor FTCDcMotor;
    /**
     * Type of motor. Controls the encoder counts per revolution
     */
    private MotorType motorType = MotorType.ANDYMARK_40;
    /**
     *  Encoder counts per shaft revolution for this motor
     */
    private int countsPerRev = 0;

    /**
     *  Number of cm moved for each motor shaft revolution
     */
    private double UnitsPerRev = 0;

    /**
     * Hold the desired enocoder count for RUN_TO_POSITION
     */
    private int desiredEncoderCount = 0;

    /**
     * The tolerance range for saying of the encoder count has been reached.
     */
    private int encoderTolerance = 0;

    /**
     * The current state of the motor.
     */
    private MotorState currentMotorState = MotorState.IDLE;

    /**
     * The desired state of the motor after the rotation is finished.
     */
    private NextMotorState nextMotorState = NextMotorState.FLOAT;

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
     * enables whether you detect a stall
     */
    private boolean stallDetectionEnabled = false;

    /**
     * last encoder value
     */
    private int lastEncoderValue = 0;

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

    public void setCountsPerRev(int countsPerRev) {
        this.countsPerRev = countsPerRev;
    }

    public double getUnitsPerRev() {
        return UnitsPerRev;
    }

    public void setUnitsPerRev(double UnitsPerRev) {
        this.UnitsPerRev = UnitsPerRev;
    }

    public int getDesiredEncoderCount() {
        return desiredEncoderCount;
    }

    public void setDesiredEncoderCount(int desiredEncoderCount) {
        this.desiredEncoderCount = desiredEncoderCount;
    }

    public int getEncoderTolerance() {
        return encoderTolerance;
    }

    public void setEncoderTolerance(int encoderTolerance) {
        this.encoderTolerance = encoderTolerance;
    }

    public MotorState getMotorState() {
        return currentMotorState;
    }

    protected void setMotorState(MotorState currentMotorState) {
        this.currentMotorState = currentMotorState;
    }

    public NextMotorState getNextMotorState() {
        return nextMotorState;
    }

    public void setNextMotorState(NextMotorState nextMotorState) {
        this.nextMotorState = nextMotorState;
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
    private void initMotorDefaults(){
        setMotorType(MotorType.ANDYMARK_40);
        setUnitsPerRev(0);
        setStallDetectionEnabled(false);
        setDesiredEncoderCount(0);
        setEncoderTolerance(10);
        setMotorState(MotorState.IDLE);
        setNextMotorState(NextMotorState.FLOAT);
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

        int countsPerRev;

        switch (motorType){
            case NXT:
                setCountsPerRev(360);
                countsPerRev = 360;
                break;
            case ANDYMARK_20:
                setCountsPerRev(560);
                countsPerRev = 560;
                break;
            case ANDYMARK_40:
                setCountsPerRev(1120);
                countsPerRev = 1120;
                break;
            case ANDYMARK_60:
                setCountsPerRev(1680);
                countsPerRev = 1680;
                break;
            case TETRIX:
                setCountsPerRev(1440);
                countsPerRev = 1140;
                break;
            default:
                setCountsPerRev(0);
                countsPerRev = 0;
                break;
        }
        return countsPerRev;
    }

    /**
     * Calculate the number or motor revolutions needed to move whatever is attached to the motor
     * a certain distance. It uses the UnitsPerRev value for the calculation.
     *
     * @param distance The amount to move whatever is attached. Although "distance" implies a
     *                 distance, it could be angles, distance or any other units.
     * @return Number of motor revolutions to turn.
     */
    public double getRevsForDistance(double distance){
        return distance / getUnitsPerRev();
    }

    /**
     * Calculate the number of encoder counts needed to move whatever is attached to the motor
     * a certain distance. It uses the UnitsPerRev value and number of encoder counts per revolution
     * for the calculation. The number of encoder counts per rev is dependent on the motor type.
     *
     * @param distance The amount to move whatever is attached. Although "distance" implies a
     *                 distance, it could be angles, distance or any other units.
     * @return Number of enocder counts to turn.
     */
    public int getEncoderCountForDistance(double distance){
        return (int)((double)getCountsPerRev() * getRevsForDistance(distance));
    }

    /**
     * Gets the number of encoder counts for a certian number of revolutions.
     *
     * @param revs number of revolutions
     * @return encoder counts
     */
    public int getEncoderCountForRevs(double revs) {
        return (int)(getCountsPerRev() * revs);
    }

    /**
     * If the motor is set for relative movement, the encoder will be reset. But if the motor
     * is set for absolute movement, the encoder needs to keep track of where the motor is, so
     * it cannot be reset.
     */
    public void resetEncoder(){
        if (getMotorMoveType() == MotorMoveType.RELATIVE) {
            this.setMode(DcMotor.RunMode.RESET_ENCODERS);
        }
    }

    /**
     * If true is set, reset the encoder, no matter whether the motor is set for relative or
     * absolute movement.
     * Use this method to set the zero point on a motor that will be moved absolute from now on.
     *
     * @param override If true, then reset the encoder, no matter what.
     */
    public void resetEncoder(boolean override){
        if (override) {
            this.setMode(DcMotor.RunMode.RESET_ENCODERS);
        } else {
            resetEncoder();
        }
    }

    //*********************************************************************************************
    //          Methods for rotating the motor to a desired encoder count
    //*********************************************************************************************

    /**
     * Rotate the motor so that the output moves a certain distance. The encoder is used to
     * determine if the distance has been met.
     *
     * @param power Power input for the motor. Note that it will be clipped to less than +/-0.8.
     * @param distance Motor will be rotated so that it results in a movement of this distance.
     * @param afterCompletion What to do after this movement is completed: HOLD or COAST
     * @return true if successfully completed
     */
    public boolean rotateToDistance(double power, double distance, NextMotorState afterCompletion ) {
        if(this.currentMotorState != MotorState.MOVING) {
            int encoderCountForDistance = getEncoderCountForDistance(distance);
            return rotateToEncoderCount(power, encoderCountForDistance, afterCompletion);
        } else {
            return false;
        }
    }

    public boolean rotateNumberOfDegrees(double power, double degrees, NextMotorState afterCompletion) {
        return true;
    }

    /**
     * Makes the motor rotate to a certain amount of revolutions.
     * @param power The power at which the motor moves
     * @param revs The amount of revolutions you want it to go.
     * @param afterCompletion Whether it holds or floats after completion.
     * @return If return is true then it actually did it.
     */
    public boolean rotateNumberOfRevolutions(double power, double revs, NextMotorState afterCompletion){
        return rotateToEncoderCount(power, getEncoderCountForRevs(revs), afterCompletion);
    }

    /**
     * Rotate the motor so the encoder gets to a certain count.
     *
     * @param power Power input for the motor. Note that it will be clipped to less than +/-0.8.
     * @param encoderCount Motor will be rotated so that it results in a movement of this distance.
     * @param afterCompletion  What to do after this movement is completed: HOLD or COAST
     * @return true if successfully completed
     */
    public boolean rotateToEncoderCount(double power, int encoderCount, NextMotorState afterCompletion) {
        if(getMotorState() != MotorState.MOVING) {
            // set what to do after the rotation completes
            setNextMotorState(afterCompletion);
            // reset the encoder
            resetEncoder();
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
        } else {
            return false;
        }
    }


    /**
     * Checks to see if the rotation to encoder count has completed. If it has then it sets the
     * motor to hold that encoder count or it sets the motor so that it can move freely, depending
     * on the NextMotorState being set to HOLD or COAST.
     * Use this method in a loop to see if the movement has finished. Like and opmode loop() or in
     * a while do loop.
     *
     * @return true if movement complete
     */
    private boolean isRotationComplete() {
        if (FTCDcMotor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
            // The motor is moving to a certain encoder position so it will complete movement at
            // some point.
            // get the current encoder position
            int currentEncoderCount = this.getCurrentPosition();
            // is the current position within the tolerance limit of the desired position?
            if (Math.abs(getDesiredEncoderCount() - currentEncoderCount) <getEncoderTolerance()) {
                // movement is complete
                return true;
            } else {
                // movement is not finished yet
                return false;
            }
        } else {
            // The motor is not moving to a position so there cannot be a point when the rotation is
            // complete
            return false;
        }

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
     * Run the motor with encoder feedback. If there is a load on the motor the power will be
     * increased in an attempt to maintain the speed. Note that a high power command may result in
     * the PID not being able to control the motor.
     *
     * @param power Power input for the motor.
     * @return true if successfully completed
     */
     public boolean runUsingEncoder(double power) {
         if(getMotorState() != MotorState.MOVING) {
            // reset the encoder
            this.resetEncoder();
            // set the run mode
             this.setMode(DcMotor.RunMode.RUN_USING_ENCODERS);
             this.setMotorState(MotorState.MOVING);
             this.setPower(power);
            return true;
        } else {
            return false;
        }
    }

    //*********************************************************************************************
    //          Methods for rotating the motor without encoders - open loop
    //*********************************************************************************************

    /**
     * Run the motor without any encoder feedback. If there is a load on the motor the speed will
     * drop.
     *
     * @param power Power input for the motor.
     * @return true if successfully completed
     */
     public boolean runWithoutEncoder(double power) {
        if(getMotorState() != MotorState.MOVING) {
            // reset the encoder
            this.resetEncoder();
            // set the run mode
            this.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODERS);
            this.setMotorState(MotorState.MOVING);
            this.setPower(power);
            return true;
        } else {
            return false;
        }
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

        if (getNextMotorState() == NextMotorState.HOLD) {
            setMotorState(MotorState.HOLDING);
            rotateToEncoderCount(FTCDcMotor.getPower(), FTCDcMotor.getCurrentPosition(), NextMotorState.HOLD);
        } else {
            stopMotor();
        }
    }

    /**
     * Stop the motor. It will just coast, ie rotate freely.
     */
    public void stopMotor() {
        resetEncoder(true);
        setMotorState(MotorState.IDLE);
    }

    //*********************************************************************************************
    //          State Machine
    //*********************************************************************************************

    public MotorState updateMotor() {
        switch(getCurrentMotorState()) {
            case IDLE:
                break;
            case MOVING:
                if (isStalled()) {
                    setMotorState(MotorState.STALLED);
                }
                if (isRotationComplete()) {
                    if (getNextMotorState()== NextMotorState.FLOAT) {
                        setMotorState(MotorState.FLOATING);
                        this.setPowerFloat();
                    } else {
                        setMotorState(MotorState.HOLDING);
                    }
                }
                break;
            case STALLED:
                break;
            case INTERRUPTED:
                break;
            case HOLDING:
                break;
            case FLOATING:
                break;
        }
        return getCurrentMotorState();
    }

    //*********************************************************************************************
    //          Wrapper Methods
    //*********************************************************************************************
    public void setMode(DcMotor.RunMode mode) {
        if (mode == DcMotor.RunMode.RESET_ENCODERS) {
            this.setMotorState(MotorState.IDLE);
        }
        FTCDcMotor.setMode(mode);
    }

    public void setPower(double power) {
        power = Range.clip(power, getMinMotorPower(),getMaxMotorPower());
        FTCDcMotor.setPower(power);
    }

    public void setPowerFloat() {
        FTCDcMotor.setPowerFloat();
    }

    public void setTargetPosition(int position) {
        // set the field holding the desired rotation
        setDesiredEncoderCount(position);
        FTCDcMotor.setTargetPosition(position);
    }

    public int getCurrentPosition() {
        return FTCDcMotor.getCurrentPosition();
    }

    public void setDirection(DcMotor.Direction direction) {
        FTCDcMotor.setDirection(direction);
        this.direction = direction;
    }

}
