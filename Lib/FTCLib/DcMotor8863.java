package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import static java.lang.Thread.sleep;

/**
 * Created by ball on 11/28/2015.
 */
public class DcMotor8863 {

    /**
     * Defines the type of motor.
     */
    public enum MotorType {
        NXT, ANDYMARK_20,
        ANDYMARK_40,
        ANDYMARK_60,
        TETRIX,
        ANDYMARK_20_ORBITAL,
        ANDYMARK_3_7_ORBITAL,
        ANDYMARK_3_7_ORBITAL_OLD
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
     * MOVING_NO_PID_POWER_RAMP = motor is moving not under PID control and a power ramp is active
     */
    public enum MotorState {
        IDLE, HOLD, MOVING_PID_NO_POWER_RAMP, MOVING_PID_POWER_RAMP, MOVING_NO_PID_NO_POWER_RAMP,
        MOVING_NO_PID_POWER_RAMP, STALLED, COMPLETE_HOLD, COMPLETE_FLOAT
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
    /**
     * A DcMotor from the qualcomm code
     */
    protected com.qualcomm.robotcore.hardware.DcMotor FTCDcMotor;

    /**
     * Type of motor. Controls the encoder counts per revolution
     */
    private MotorType motorType = MotorType.ANDYMARK_40;

    /**
     * Encoder counts per shaft revolution for this type of motor
     */
    private int countsPerRev = 0;

    /**
     * The no load RPM for the motor as given by the motor datasheet
     */
    private int noLoadRPM = 0;

    /**
     * The no load max speed in encoder ticks per second
     */
    private int maxEncoderTicksPerSecond = 0;

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
     * The current run mode of the motor. This mirrors the actual mode that the motor is set to.
     * I could just call DcMotor.getMode() and return this from the controller. But this creates
     * extra traffic on the bus and since we can store the value locally when it is set there is no
     * need to make that call.
     */
    private DcMotor.RunMode currentRunMode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;

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
    protected DcMotor.Direction direction = com.qualcomm.robotcore.hardware.DcMotor.Direction.FORWARD;

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

    /**
     * This object give the motor the ability to ramp the power up gradually. It only gets created
     * if the user calls enablepowerRamp()
     */
    private RampControl powerRamp;

    private double desiredPowerAfterRamp = 0;

    /**
     * The current power the motor has been commanded to run at
     */
    protected double currentPower = 0;

    /**
     * A timer used in the isRotationComplete method
     */
    private ElapsedTime completionTimer;

    /**
     * The amount of time in mSec that the motor has to be on its target in order for me to call
     * the movement to that target complete.
     */
    private double completionTimeoutInmSec = 100;

    /**
     * Telemetry object for user feedback
     */
    private Telemetry telemetry = null;

    /**
     * An optional log file for logging data
     */
    private DataLogging dataLog = null;

    /**
     * Should data be logged into the log file
     */
    private boolean logFlag = false;

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
        setNoLoadRPMForMotorType(motorType);
        setMaxEncoderTicksPerSecond(getMotorSpeedInEncoderTicksPerSec(getCountsPerRev(), getNoLoadRPM()));
    }

    /**
     * Set the number of encoder counts per revolution of the shaft based on the type of motor.
     *
     * @param motorType Type of motor.
     * @return Number of encoder counts per revolution of the output shaft of the motor
     */
    private int setCountsPerRevForMotorType(MotorType motorType) {
        switch (motorType) {
            case NXT:
                this.countsPerRev = 360;
                break;
            case ANDYMARK_20:
                // http://www.andymark.com/NeveRest-20-12V-Gearmotor-p/am-3102.htm
                this.countsPerRev = 560;
                break;
            case ANDYMARK_40:
                // http://www.andymark.com/NeveRest-40-Gearmotor-p/am-2964a.htm
                this.countsPerRev = 1120;
                break;
            case ANDYMARK_60:
                // http://www.andymark.com/NeveRest-60-Gearmotor-p/am-3103.htm
                this.countsPerRev = 1680;
                break;
            case TETRIX:
                // http://www.cougarrobot.com/attachments/328_Tetrix_DC_Motor_V2.pdf
                this.countsPerRev = 1440;
                break;
            case ANDYMARK_20_ORBITAL:
                this.countsPerRev = 537;
                break;
            case ANDYMARK_3_7_ORBITAL:
                this.countsPerRev = 103;
                break;
            case ANDYMARK_3_7_ORBITAL_OLD:
                this.countsPerRev = 44;
                break;
            default:
                this.countsPerRev = 0;
                break;
        }
        return getCountsPerRev();
    }

    public int getCountsPerRev() {
        return countsPerRev;
    }

    public int getNoLoadRPM() {
        return this.noLoadRPM;
    }

    /**
     * Put the data in for the no load RPM of each type of motor.
     *
     * @param motorType
     */
    private int setNoLoadRPMForMotorType(MotorType motorType) {
        switch (motorType) {
            case NXT:
                // http://www.philohome.com/nxtmotor/nxtmotor.htm
                noLoadRPM = 165;
                break;
            case ANDYMARK_20:
                // http://www.andymark.com/NeveRest-20-12V-Gearmotor-p/am-3102.htm
                noLoadRPM = 315;
                break;
            case ANDYMARK_40:
                // http://www.andymark.com/NeveRest-40-Gearmotor-p/am-2964a.htm
                noLoadRPM = 160;
                break;
            case ANDYMARK_60:
                // http://www.andymark.com/NeveRest-60-Gearmotor-p/am-3103.htm
                noLoadRPM = 105;
                break;
            case TETRIX:
                // http://www.cougarrobot.com/attachments/328_Tetrix_DC_Motor_V2.pdf
                noLoadRPM = 150;
                break;
            case ANDYMARK_20_ORBITAL:
                noLoadRPM = 340;
                break;
            case ANDYMARK_3_7_ORBITAL:
                noLoadRPM = 1784;
                break;
            default:
                noLoadRPM = 0;
                break;
        }
        return getNoLoadRPM();
    }

    private int getMaxEncoderTicksPerSecond() {
        return this.maxEncoderTicksPerSecond;
    }

    private void setMaxEncoderTicksPerSecond(int maxEncoderCountsPerSec) {
        this.maxEncoderTicksPerSecond = maxEncoderCountsPerSec;
    }

    public double getMovementPerRev() {
        return MovementPerRev;
    }

    public void setMovementPerRev(double MovementPerRev) {
        this.MovementPerRev = MovementPerRev;
    }

    public int getTargetEncoderCount() {
        return targetEncoderCount;
    }

    protected void setTargetEncoderCount(int targetEncoderCount) {
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

    protected DcMotor.RunMode getCurrentRunMode() {
        return currentRunMode;
    }

    protected void setCurrentRunMode(DcMotor.RunMode currentRunMode) {
        this.currentRunMode = currentRunMode;
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

    public double getCurrentPower() {
        return currentPower;
    }

    public double getCompletionTimeoutInmSec() {
        return completionTimeoutInmSec;
    }

    public void setCompletionTimeoutInmSec(double completionTimeoutInmSec) {
        this.completionTimeoutInmSec = completionTimeoutInmSec;
    }

    public DataLogging getDataLog() {
        return dataLog;
    }

    public void setDataLog(DataLogging dataLog) {
        this.dataLog = dataLog;
    }

    //*********************************************************************************************
    //          Constructors
    //*********************************************************************************************

    public DcMotor8863(String motorName, HardwareMap hardwareMap, Telemetry telemetry) {
        this(motorName, hardwareMap);
        this.telemetry = telemetry;
    }

    public DcMotor8863(String motorName, HardwareMap hardwareMap) {
        FTCDcMotor = hardwareMap.dcMotor.get(motorName);
        stallTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        completionTimer = new ElapsedTime();
        powerRamp = new RampControl(0, 0, 0);
        initMotorDefaults();
        // FTC SDK 3.4 removed setMaxSpeed - I was never sure what it did anyway
        //this.setMaxSpeed(this.getMaxEncoderTicksPerSecond());
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
        // reset the encoder and force the motor to be stopped
        // 1/15/2019 since the setMode in this class will only set the mode when it changes, to
        // set an initial value, I am skipping the setMode in this class and sending it straight
        // to the motor controller
        //this.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FTCDcMotor.setMode((DcMotor.RunMode.STOP_AND_RESET_ENCODER));
        currentRunMode = DcMotor.RunMode.STOP_AND_RESET_ENCODER;
        this.setPower(0);
    }

    //*********************************************************************************************
    //          Helper Methods
    //*********************************************************************************************

    /**
     * Implements a delay
     *
     * @param mSec delay in milli Seconds
     */
    private void delay(int mSec) {
        try {
            Thread.sleep((int) (mSec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Calculate the motor speed in encoder ticks per second given the number of ticks per revolution
     * and the speed of the motor in RPM.
     *
     * @param countsPerRev
     * @param motorRPM
     * @return motor speed in encoder ticks / sec
     */
    // tested
    private int getMotorSpeedInEncoderTicksPerSec(int countsPerRev, int motorRPM) {
        return (int) Math.round((double) motorRPM * 1 / 60 * countsPerRev);
    }

    /**
     * Calculate the number or motor revolutions needed to move whatever is attached to the motor
     * a certain amount. It uses the MovementPerRev value for the calculation.
     *
     * @param movement The amount to move whatever is attached. It could be degrees, cm or any
     *                 other units.
     * @return Number of motor revolutions to turn.
     */
    // tested
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
    // tested
    public int getEncoderCountForMovement(double movement) {
        return (int) Math.round(getCountsPerRev() * getRevsForMovement(movement));
    }

    /**
     * Calculate the "movement" of whatever is attached to the motor based on the
     * encoder counts given. The movement can be the number of degrees the motor has moved, the
     * number of cm a wheel attached to the motor has turned etc. It uses the MovementPerRev and
     * CountsPerRev defined when the motor object is setup.
     *
     * @param encoderCount The position of the motor as given by the encoder count
     * @return How far the motor has moved whatever is attached to it.
     */
    // tested
    public double getMovementForEncoderCount(int encoderCount) {
        // note that I have to cast encoderCount to a double in order to get a double answer
        // If I did not then 1000/300 = 3 rather than 3.3333 because 1000 and 300 are integers in the
        // equation below. The compiler makes the answer int also and drops the .3333. So you get
        // the wrong answer. Casting the numerator forces the compiler to do double math and you
        // get the correct answer (3.333).
        return (double) encoderCount / getCountsPerRev() * getMovementPerRev();
    }

    /**
     * Provide a method to manually set the last encoder value to the current position.
     */
    public void setLastEncoderCountToCurrentPostion() {
        lastEncoderValue = getCurrentPosition();
    }

    /**
     * Get the current motor position in terms of the position of whatever is attached to it. The
     * position can be the number of degrees, the position of a wheel in cm etc.
     *
     * @return position in units of whatever is attached to it
     */
    // tested
    public double getPositionInTermsOfAttachment() {
        return getMovementForEncoderCount(getCurrentPosition());
    }

    /**
     * Get the current motor position in terms of the position of whatever is attached to it. The
     * position can be the number of degrees, the position of a wheel in cm etc. The position is
     * relative to the last position. In other words, the position is the current position - the
     * position of the object before the last movement started.
     *
     * @return position in units of whatever is attached to it
     */
    // tested
    public double getPositionInTermsOfAttachmentRelativeToLast() {
        return getMovementForEncoderCount(getCurrentPosition() - this.lastEncoderValue);
    }

    /**
     * Get the current encoder value relative to the last encoder value. In other words, the
     * position is the current position - the position of the encoder  before the last movement
     * started.
     *
     * @return encoder value - encoder value before the last movement started
     */
    public int getCurrentPositionRelativeToLast() {
        return this.getCurrentPosition() - this.lastEncoderValue;
    }


    /**
     * Gets the number of encoder counts for a certain number of revolutions.
     *
     * @param revs number of revolutions
     * @return encoder counts
     */
    // tested
    public int getEncoderCountForRevs(double revs) {
        return (int) Math.round((getCountsPerRev() * revs));
    }

    /**
     * Gets the number of encoder counts corresponding to a movement of a given number of degrees.
     *
     * @param degrees number of degrees
     * @return encoder counts
     */
    //tested
    public int getEncoderCountForDegrees(double degrees) {
        return (int) Math.round(getCountsPerRev() * degrees / 360);
    }

    /**
     * If the motor is set for relative movement, the encoder will be reset. But if the motor
     * is set for absolute movement, the encoder needs to keep track of where the motor is, so
     * it cannot be reset.
     */
    @Deprecated
    private void resetEncoder() {
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
    @Deprecated
    private void resetEncoder(boolean override) {
        if (override) {
            this.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        } else {
            resetEncoder();
        }
    }

    /**
     * After the motor stops moving it will be able to spin freely
     */
    public void setAfterCompletionToFloat() {
        setFinishBehavior(FinishBehavior.FLOAT);
    }

    /**
     * After the motor stops moving it will resist any movement or load.
     */
    public void setAfterCompletionToHold() {
        setFinishBehavior(FinishBehavior.HOLD);
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
     * This method starts the movement. Once it is started there are 4 ways to stop it:
     * stop()
     * interrupt()
     * motor stalls and stall detection is enabled
     * movement completes
     * <p>
     * WARNING: Do not call this method repeatedly in a loop and use update() in the same loop. If you do,
     * the movement will complete and update() will set the mode to COMPLETE_HOLD or COMPLETE_FLOAT
     * and as soon as this method is encountered in the loop it will start the movement all over
     * again since motorState != MOVING.
     * <p>
     * NOTE: FinishBehavior will not take affect unless the update() method is called in a loop
     * after this method.
     * <p>
     * NOTE: You can change the power while the movement is going on by calling setPower().
     *
     * @param power           Power input for the motor. Note that it will be clipped to less than +/-0.8.
     * @param targetPosition  Motor will be rotated so that this position of the object is reached.
     * @param afterCompletion What to do after this movement is completed: HOLD or FLOAT
     * @return true if the movement is started and not already ongoing
     */
    // tested
    public boolean moveToPosition(double power, double targetPosition, FinishBehavior afterCompletion) {
        // figure out what the encoder count is that corresponds to the target position
        int encoderCountForPosition = getEncoderCountForMovement(targetPosition);
        this.lastEncoderValue = this.getCurrentPosition();
        return rotateToEncoderCount(power, encoderCountForPosition, afterCompletion);
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
     * This method starts the movement. Once it is started there are 4 ways to stop it:
     * stop()
     * interrupt()
     * motor stalls and stall detection is enabled
     * movement completes
     * <p>
     * WARNING: Do not call this method repeatedly in a loop and use update() in the same loop. If you do,
     * the movement will complete and update() will set the mode to COMPLETE_HOLD or COMPLETE_FLOAT
     * and as soon as this method is encountered in the loop it will start the movement all over
     * again since motorState != MOVING.
     * <p>
     * NOTE: FinishBehavior will not take affect unless the update() method is called in a loop
     * after this method.
     * <p>
     * NOTE: You can change the power while the movement is going on by calling setPower().
     *
     * @param power
     * @param movement
     * @param afterCompletion
     * @return
     */
    // tested
    public boolean moveByAmount(double power, double movement, FinishBehavior afterCompletion) {
        int currentPosition = this.getCurrentPosition();
        // figure out what the encoder count is that corresponds to the amount to be moved
        int encoderCountForMovement = getEncoderCountForMovement(movement);
        // add that to the current encoder count
        int encoderCountForPosition = encoderCountForMovement + currentPosition;
        this.lastEncoderValue = currentPosition;
        return rotateToEncoderCount(power, encoderCountForPosition, afterCompletion);

    }

    /**
     * A relative movement:
     * Rotate the motor a given number of degrees. This is a relative movement.
     *
     * @param power
     * @param degrees
     * @param afterCompletion
     * @return
     */
    // tested
    public boolean rotateNumberOfDegrees(double power, double degrees, FinishBehavior afterCompletion) {
        int currentPosition = this.getCurrentPosition();
        // figure out what the encoder count is that corresponds to the amount to be moved
        int encoderCountForDegrees = getEncoderCountForDegrees(degrees);
        // add that to the current encoder count
        int encoderCountForPosition = encoderCountForDegrees + currentPosition;
        this.lastEncoderValue = currentPosition;
        return rotateToEncoderCount(power, encoderCountForPosition, afterCompletion);
    }

    /**
     * A relative movement:
     * Makes the motor rotate to a certain amount of revolutions.
     *
     * @param power           The power at which the motor moves
     * @param revs            The amount of revolutions you want it to go.
     * @param afterCompletion Whether it holds or floats after completion.
     * @return If return is true then it actually did it.
     */
    // tested
    public boolean rotateNumberOfRevolutions(double power, double revs, FinishBehavior afterCompletion) {
        int currentPosition = this.getCurrentPosition();
        // figure out what the encoder count is that corresponds to the amount to be moved
        int encoderCountForRevs = getEncoderCountForRevs(revs);
        // add that to the current encoder count
        int encoderCountForPosition = encoderCountForRevs + currentPosition;
        this.lastEncoderValue = currentPosition;
        return rotateToEncoderCount(power, encoderCountForPosition, afterCompletion);
    }

    /**
     * Rotate the motor so the encoder gets to a certain count.
     *
     * @param power           Power input for the motor. Note that it will be clipped to less than +/-0.8.
     * @param encoderCount    Motor will be rotated so that it results in a movement of this distance.
     * @param afterCompletion What to do after this movement is completed: HOLD or COAST
     * @return true if successfully completed
     * <p>
     * This method starts the movement. Once it is started there are 4 ways to stop it:
     * stop()
     * interrupt()
     * motor stalls and stall detection is enabled
     * movement completes
     * <p>
     * WARNING: Do not call this method repeatedly in a loop and use update() in the same loop. If you do,
     * the movement will complete and update() will set the mode to COMPLETE_HOLD or COMPLETE_FLOAT
     * and as soon as this method is encountered in the loop it will start the movement all over
     * again since motorState != MOVING.
     * <p>
     * NOTE: FinishBehavior will not take affect unless the update() method is called in a loop
     * after this method.
     * <p>
     * NOTE: You can change the power while the movement is going on by calling setPower().
     */
    // tested
    public boolean rotateToEncoderCount(double power, int encoderCount, FinishBehavior afterCompletion) {
        // If the motor is already moving then make sure that another movement command cannot be issued.
        if (!isMotorStateMoving()) {
            // set what to do after the rotation completes
            setFinishBehavior(afterCompletion);
            // set the desired encoder position
            this.setTargetPosition(encoderCount);
            // set the run mode
            this.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            // clip the power so that it does not exceed 80% of max. The reason for this is that the
            // PID controller inside the core motor controller needs to have some room to increase
            // the power when it makes its PID adjustments. If you set the power to 100% then there is
            // no room to increase the power if needed and PID control will not work.
            power = Range.clip(power, -.8, .8);
            // reset the completion timer since we are starting a motor movement that will end
            // once rotation is detected as complete
            completionTimer.reset();
            // reset the stall timer since the motor is about to start moving
            // this was a bug discovered 1/5/2018. It was not here.
            stallTimer.reset();
            // Is there is a power ramp setup to automatically start with the motor is turned on?
            if (powerRamp.isEnabled()) {
                // yes there is
                this.setMotorState(MotorState.MOVING_PID_POWER_RAMP);
                // if there is a power ramp enabled then the initial power will be from the ramp, not
                // the one the user passed in.
                setInitialPower(power);
            } else {
                // No power ramp is set to automatically be started.
                this.setMotorState(MotorState.MOVING_PID_NO_POWER_RAMP);
                // Turn the motor on
                this.setPower(power);
            }
            logFlag = true;
            if (dataLog != null) {
                dataLog.logData("Rotate to encoder count = " + Integer.toString(encoderCount) + " from encoder count = " + Integer.toString(getCurrentPosition()) + " at power " + Double.toString(power) + "in");
            }
            return true;
        } else {
            // This method was called again while the movement was taking place. You can't do that.
            return false;
        }

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
     * <p>
     * This method starts the movement. Once it is started there are 3 ways it can stop:
     * stop()
     * interrupt()
     * motor stalls and stall detection is enabled
     * Remember that movement cannot complete since this mode runs until specifically stopped.
     * <p>
     * NOTE: You can change the power while the movement is going on by calling setPower().
     *
     * @param power Power input for the motor.
     * @return true if successfully completed
     */
    // tested
    public boolean runAtConstantSpeed(double power) {
        // If the motor is already moving then make sure that another movement command cannot be issued.
        if (!isMotorStateMoving()) {
            // set the run mode
            this.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            // Is there is a power ramp setup to automatically start with the motor is turned on?
            // Is there is a power ramp setup to automatically start with the motor is turned on?
            if (powerRamp.isEnabled()) {
                // yes there is
                this.setMotorState(MotorState.MOVING_PID_POWER_RAMP);
                // if there is a power ramp enabled then the initial power will be from the ramp, not
                // the one the user passed in.
                setInitialPower(power);
            } else {
                // No power ramp is set to automatically be started.
                this.setMotorState(MotorState.MOVING_PID_NO_POWER_RAMP);
                // Turn the motor on
                this.setPower(power);
            }
            return true;
        } else {
            return false;
        }
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
     * Run the motor at a constant power without any encoder feedback. If there is a load on the
     * motor the speed will decrease. If you don't want the speed to decrease, use
     * runAtConstantSpeed instead.
     * <p>
     * This method starts the movement. Once it is started there are 3 ways it can stop:
     * stop()
     * interrupt()
     * motor stalls and stall detection is enabled
     * Remember that movement cannot complete since this mode runs until specifically stopped.
     *
     * @param power Power input for the motor.
     * @return true if successfully completed
     */
    // tested
    public boolean runAtConstantPower(double power) {
        // If the motor is already moving then make sure that another movement command cannot be issued.
        if (!isMotorStateMoving()) {
            // set the run mode
            this.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            // Is there is a power ramp setup to automatically start with the motor is turned on?
            if (powerRamp.isEnabled()) {
                // yes there is
                this.setMotorState(MotorState.MOVING_NO_PID_POWER_RAMP);
                // if there is a power ramp enabled then the initial power will be from the ramp, not
                // the one the user passed in.
                setInitialPower(power);
            } else {
                // No power ramp is set to automatically be started.
                this.setMotorState(MotorState.MOVING_NO_PID_NO_POWER_RAMP);
                // Turn the motor on
                this.setPower(power);
            }
            return true;
        } else {
            return false;
        }
    }

    //*********************************************************************************************
    //          power ramp related methods
    //*********************************************************************************************

    /**
     * Enable the power ramp and setup the line that controls power vs time.
     *
     * @param initialPower   The power the motor starts out at for time = 0.
     * @param finalPower     The power that corresponds to the end of the ramp time. The ramp will finish
     *                       at this power.
     * @param rampTimeInmSec The length of time it takes to ramp up the power.
     */
    public void setupPowerRamp(double initialPower, double finalPower, double rampTimeInmSec) {
        powerRamp.setup(initialPower, finalPower, rampTimeInmSec);
        // allow the ramp to be automatically started
        powerRamp.enable();
    }

    /**
     * Start a power ramp. It must be setup first though!
     */
    public void startPowerRamp() {
        powerRamp.start();
    }

    /**
     * Setup and start a power ramp. Combines setupPowerRamp() and startPowerRamp() in one
     * method for ease of use.
     *
     * @param initialPower   The power the motor starts out at for time = 0.
     * @param finalPower     The power that corresponds to the end of the ramp time. The ramp will finish
     *                       at this power.
     * @param rampTimeInmSec The length of time it takes to ramp up the power.
     */
    public void setupAndStartPowerRamp(double initialPower, double finalPower, double rampTimeInmSec) {
        setupPowerRamp(initialPower, finalPower, rampTimeInmSec);
        this.desiredPowerAfterRamp = finalPower;
        startPowerRamp();
    }

    /**
     * Disable the power ramp. This does not stop a running power ramp. It just prevents a ramp
     * that is setup from starting.
     */
    public void disablePowerRamp() {
        powerRamp.disable();
    }

    /**
     * Is there a power ramp enabled but not running yet?
     *
     * @return true = power ramp enabled
     */
    public boolean isPowerRampEnabled() {
        return powerRamp.isEnabled();
    }

    /**
     * Check to see if there is a power ramp running. Since the motor maintains its own state, this
     * is the only way for a user to tell if there is a power ramp running or if it is finished.
     *
     * @return true = power ramp is still running
     */
    public boolean isPowerRampRunning() {
        return powerRamp.isRunning();
    }

    /**
     * Get the power to apply to the motor. It will be either a power from the ramp equation, or
     * the desired power previously set, whichever is less.
     * Note that the power ramp will disable itself once the ramp time has expired.
     */
    private void updatePowerRamp() {
        this.setPower(powerRamp.getRampValueLinear(desiredPowerAfterRamp));
    }

    /**
     * Set the initial power for the motor. This should only be called when a power ramp is enabled
     * and the motor is being turned on.
     *
     * @param power The power to run the motor at after the ramp is over.
     */
    private void setInitialPower(double power) {
        // save the desired power for use after the power ramp finishes
        this.desiredPowerAfterRamp = power;
        // now get the initial power for the power ramp
        power = powerRamp.getValueAtStartTime();
        // start the power ramp
        powerRamp.start();
        // turn the motor on
        this.setPower(power);
    }

    /**
     * A method that can be called that does nothing. Used for debug to set breakpoints on.
     */
    private void doNothing() {
        return;
    }

    //*********************************************************************************************
    //          Methods to help out the other methods that move a motor
    //*********************************************************************************************

    public void setupStallDetection(double stallTimeLimit, int stallDetectionTolerance) {
        setStallDetectionEnabled(true);
        setStallDetectionTolerance(stallDetectionTolerance);
        setStallTimeLimit(stallTimeLimit);
        stallTimer.reset();
        this.lastEncoderValue = this.getCurrentPosition();
    }

    public boolean isStalled() {
        if (telemetry != null) {
            telemetry.addData("motor is moving = ", isMotorStateMoving());
            telemetry.addData("motor stall detection enabled = ", isStallDetectionEnabled());
            telemetry.addData("stall timer = ", "%5.2f", stallTimer.time());
            telemetry.addData("stall time limit = ", "%5.2f", stallTimeLimit);
        }
        int currentEncoderValue = this.getCurrentPosition();
        if (isMotorStateMoving() && isStallDetectionEnabled()) {
            if (telemetry != null) {
                telemetry.addData("checking for a stall", "!");
            }
            // if the motor has not moved since the last time the position was read
            if (Math.abs(currentEncoderValue - lastEncoderValue) < stallDetectionTolerance) {
                if (telemetry != null) {
                    telemetry.addData("motor is not moving", "!");
                }
                // motor has not moved, checking to see how long the motor has been stalled for
                if (stallTimer.time() > stallTimeLimit) {
                    if (telemetry != null) {
                        telemetry.addData("stall timer has expired", "!");
                    }
                    // it has been stalled for more than the time limit
                    return true;
                } else {
                    if (telemetry != null) {
                        telemetry.addData("stall time has NOT expired", "!");
                    }
                }
            } else {
                if (telemetry != null) {
                    telemetry.addData("motor is still moving.", " Resetting stall timer.");
                }
                // reset the timer because the motor is not stalled
                stallTimer.reset();
            }
        }
        this.lastEncoderValue = currentEncoderValue;
        return false;
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
    // BUG isBusy returns false if the mode is RUN_USING_ENCODERS (constant speed) and the mode and power
    // have just been set. It looks like a race condition between setting the mode and power and
    // isBusy returning true. It must take a while for isBusy to get flagged as true even though the
    // motor is already starting to turn. The net result of this is that the state machine starts
    // into MOVING_PID_NO_POWER_RAMP or MOVING_PID_POWER_RAMP, immediately checks for
    // isRotationComplete and it returns true. The state machine then moves to COMPLETE_FLOAT or
    // COMPLETE_HOLD and the motor immediately shuts down. For this reason I have to filter the
    // isBusy. It only is valid for RUN_TO_POSITION.
    // isBusy also does not return an accurate indication of completion on RUN_TO_POSITION in certain
    // situations. So, I will have to write my own instead of using DcMotor.isBusy()
    //
    // 2/5/17 - looks like there might be a bug in this method. It was observed in the ball shooter.
    // The pinion gear is presenting a heavy load to the motor until the point when the area with
    // no teeth rotates into position. At that point the load goes to 0 and the motor overshoots its
    // goal of 360 rotation. It takes some time to come back to 360. But my test for completion is
    // a simple "Did it hit the target?". It did when it was shooting past the target. Then I shut
    // flag rotation complete and the motor gets shut down before the PID has a chance to recover
    // from the overshoot.
    // The new algorithm is to check that it is in position for a specific period of time.
    // Specifically, has the motor been at the target for greater than X mSec.
    public boolean isRotationComplete() {
        boolean result = false;
        switch (currentRunMode) {
            case RUN_TO_POSITION:
                // This is the only mode that will eventually have the motor stop turning. This next
                // section of code is my replacement for DcMotor.isBusy().
                // get the current encoder position
                int currentEncoderCount = this.getCurrentPosition();
                // is the current position within the tolerance limit of the desired position? and
                // has it been there for longer than the completion timeout?
                if (Math.abs(targetEncoderCount - currentEncoderCount) < targetEncoderTolerance) {
                    if (completionTimer.milliseconds() > completionTimeoutInmSec) {
                        // movement is complete
                        result = true;
                    } else {
                        // if on target but the timer is not yet expired just let it run
                    }
                } else {
                    // movement is not finished yet
                    completionTimer.reset();
                    result = false;
                }
                break;
            case RUN_USING_ENCODER:
                // Running at constant speed can never be complete. The motor is just running
                // continuously
                result = false;
                break;
            case RUN_WITHOUT_ENCODER:
                // Running at constant power can never be complete. The motor is just running
                // continuously.
                result = false;
                break;
            case STOP_AND_RESET_ENCODER:
                // hmmm. I guess the fact that the motor is stopped indicates it is complete?
                result = true;
                break;
        }
        return result;
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

    //*********************************************************************************************
    //          Methods for stopping a motor
    //*********************************************************************************************

    /**
     * Stop the motor. The motor will stop and hold at whatever position it was in at the time
     * of the stop command.
     */
    //tested
    public void stop() {
        setFinishBehavior(FinishBehavior.HOLD);
        // motor state will be HOLD after this
        setMotorToHold();
    }

    /**
     * Interrupt the motor. The motor power will be set to 0 and the motor will coast or float.
     */
    // tested
    public void interrupt() {
        setFinishBehavior(FinishBehavior.FLOAT);
        // motor state will be FLOAT after this
        setMotorToFloat();
    }

    /**
     * Turn off the motor and leave it either floating or holding.
     */
    // tested
    public void shutDown() {
        if (getFinishBehavior() == FinishBehavior.FLOAT) {
            setMotorToFloat();
        } else {
            setMotorToHold();
        }
    }

    /**
     * Set the motor to stop and actively hold its position. If something tries to turn the motor,
     * it will resist that by applying enough power to hold its position.
     */
    // tested
    // 12/20/2017 there is a bug. If the motor is going to hold, it has to have power applied to it.
    // Setting it to brake just shorts the power leads together so it gives an instanteous slowdown
    // but will not actually hold a position. In order to hold, the PID has to be running: motor
    // mode must be RON_TO_POSITION and the target must be reached with power still applied.
    public void setMotorToHold() {
        this.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // get the curent position and set the target to that
        this.setTargetPosition(getCurrentPosition());
        // change the motor mode so the PID is enabled
        this.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // set the power to the last known power
        this.setPower(currentPower);
        currentMotorState = MotorState.HOLD;
    }

    /**
     * Remove power from the motor and just coast. It will turn freely. If there is something attached to
     * the motor when power is turned off it will slowly come to a stop.
     */
    // tested
    public void setMotorToFloat() {
        this.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.setPower(0);
        currentMotorState = MotorState.IDLE;
    }

    //*********************************************************************************************
    //          State Machine
    //*********************************************************************************************

    /**
     * Implement a state machine to track the motor. See enum declarations for a description of each
     * state.
     *
     * @return the state that the motor is in currently
     */
    public MotorState update() {
        switch (this.currentMotorState) {
            // Idle state means the motor is not moving and it will turn if a load is applied.
            case IDLE:
                break;
            // Hold state means the motor is not moving but it is actively holding its position.
            case HOLD:
                break;
            // Moving state means the motor is currently moving. IE it is in the middle of a command.
            // Moving state is entered by giving the motor a command. Each command method adjusts
            // the state of the motor.
            case MOVING_PID_NO_POWER_RAMP:
                // If a power ramp has just been started, set the next motor state.
                if (powerRamp.isRunning()) {
                    setMotorState(MotorState.MOVING_PID_POWER_RAMP);
                }

                if (stallDetectionEnabled && isStalled()) {
                    shutDown();
                    setMotorState(MotorState.STALLED);
                }

                // Rotation can only be complete if the motor is set to RUN_TO_POSITION
                if (isRotationComplete()) {
                    // !!!!!!!!!!!!!!!!!!!!!!!!!
                    // IT LOOKS LIKE I NEED A DELAY HERE BEFORE SHUTTING DOWN THE MOTOR TO ALLOW
                    // THE PID TO FINISH ADJUSTING THE POSITION OF THE MOTOR. OTHERWISE, I'M GETTING
                    // SLIGHT INACCURACY IN THE FINAL POSITION. Or a change in the way isRotationComplete
                    // works.
                    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    // Bug 12/20/2017 in order to hold the motor must be left in RUN_TO_POSITION mode
                    // with power applied. It cannot be shut down.
                    if (getFinishBehavior() == FinishBehavior.FLOAT) {
                        shutDown();
                        setMotorState(MotorState.COMPLETE_FLOAT);
                    } else {
                        // motor mode is still RUN_TO_POSITION with power applied in order to hold
                        setMotorState(MotorState.COMPLETE_HOLD);
                    }
                }
                break;
            case MOVING_PID_POWER_RAMP:
                // The only reason to have this state is to avoid the performance penalty of calling
                // updatePowerRamp all the time. With 2 states I only call it when a power ramp is
                // actually running.
                updatePowerRamp();
                // Is the power ramp still running?
                if (!powerRamp.isRunning()) {
                    //no, power ramp is now complete. Set the new running power and transition state
                    this.setPower(desiredPowerAfterRamp);
                    setMotorState(MotorState.MOVING_PID_NO_POWER_RAMP);
                }

                if (stallDetectionEnabled && isStalled()) {
                    shutDown();
                    setMotorState(MotorState.STALLED);
                }

                // Rotation can only be complete if the motor is set to RUN_TO_POSITION
                if (isRotationComplete()) {
                    // !!!!!!!!!!!!!!!!!!!!!!!!!
                    // IT LOOKS LIKE I NEED A DELAY HERE BEFORE SHUTTING DOWN THE MOTOR TO ALLOW
                    // THE PID TO FINISH ADJUSTING THE POSITION OF THE MOTOR. OTHERWISE, I'M GETTING
                    // SLIGHT INACCURACY IN THE FINAL POSITION. Or a change in the way isRotationComplete
                    // works.
                    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    // Bug 12/20/2017 in order to hold the motor must be left in RUN_TO_POSITION mode
                    // with power applied. It cannot be shut down.
                    if (getFinishBehavior() == FinishBehavior.FLOAT) {
                        shutDown();
                        setMotorState(MotorState.COMPLETE_FLOAT);
                    } else {
                        // motor mode is still RUN_TO_POSITION with power applied in order to hold
                        setMotorState(MotorState.COMPLETE_HOLD);
                    }
                }
                break;
            case MOVING_NO_PID_NO_POWER_RAMP:
                // If a power ramp has just been started, set the next motor state.
                if (powerRamp.isRunning()) {
                    setMotorState(MotorState.MOVING_NO_PID_POWER_RAMP);
                }

                if (stallDetectionEnabled && isStalled()) {
                    shutDown();
                    setMotorState(MotorState.STALLED);
                }
                break;
            case MOVING_NO_PID_POWER_RAMP:
                // The only reason to have this state is to avoid the performance penalty of calling
                // updatePowerRamp all the time. With 2 states I only call it when a power ramp is
                // actually running.
                updatePowerRamp();

                // Is the power ramp still running?
                if (!powerRamp.isRunning()) {
                    //power ramp is now complete. Set the new running power and transition state
                    this.setPower(desiredPowerAfterRamp);
                    setMotorState(MotorState.MOVING_NO_PID_NO_POWER_RAMP);
                }

                if (stallDetectionEnabled && isStalled()) {
                    shutDown();
                    setMotorState(MotorState.STALLED);
                }
                break;
            // Stalled state means that a stall was detected. The motor was not moving for a certain
            // period of time due to excessive load being applied.
            case STALLED:
                if (dataLog != null && logFlag) {
                    dataLog.logData("Motor Stalled!");
                }
                break;
            // Complete_float state means that the motor movement has completed and the motor will
            // turn if a load is applied.
            case COMPLETE_FLOAT:
                break;
            case COMPLETE_HOLD:
                break;
        }
        return getCurrentMotorState();
    }

    //*********************************************************************************************
    //          State Machine - methods to return status of the state machine
    //*********************************************************************************************

    /**
     * Return whether the motor has completed its movement
     *
     * @return true = movement completed
     */
    // tested
    public boolean isMotorStateComplete() {
        if (this.currentMotorState == MotorState.COMPLETE_FLOAT || this.currentMotorState == MotorState.COMPLETE_HOLD) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return whether the motor stalled and has been shutdown
     *
     * @return true = stall was detected and motor has been shut down
     */
    public boolean isMotorStateStalled() {
        if (this.currentMotorState == MotorState.STALLED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return whether the motor is currently moving.
     *
     * @return true = motor is currently moving
     */
    // tested
    public boolean isMotorStateMoving() {
        if (this.currentMotorState == MotorState.MOVING_PID_NO_POWER_RAMP ||
                this.currentMotorState == MotorState.MOVING_PID_POWER_RAMP ||
                this.currentMotorState == MotorState.MOVING_NO_PID_NO_POWER_RAMP ||
                this.currentMotorState == MotorState.MOVING_NO_PID_POWER_RAMP) {
            return true;
        } else {
            return false;
        }
    }

    //*********************************************************************************************
    //          Wrapper Methods
    //*********************************************************************************************
    public void setMode(DcMotor.RunMode mode) {
        if (mode == DcMotor.RunMode.STOP_AND_RESET_ENCODER) {
            this.setMotorState(MotorState.IDLE);
        }
        // There appears to be a bug that the motor controller gets confused when sending it mode
        // changes over and over quickly. So this code will detect a change and only send the change.
        // 1/15/2019
        if (mode != currentRunMode) {
            FTCDcMotor.setMode(mode);
        }
        // I suspect there is a timing bug and that sometimes the setMode to the motor controller
        // does not work. So make an attempt to verify it did and if not resent the command once.
        // This may not work since it looks like they cache the mode anyway and I don't have a way
        // of directly reading the controller.
        // 1/15/2019
        if (FTCDcMotor.getMode() != mode) {
            FTCDcMotor.setMode(mode);
        }
        // Save the mode locally so that I don't have to make a call to the controller later and
        // take up bus bandwidth to get the value. I can just look at this variable locally.
        this.currentRunMode = mode;
    }

    public DcMotor.RunMode getMode() {
        return FTCDcMotor.getMode();
    }

    public void setPower(double power) {
        power = Range.clip(power, getMinMotorPower(), getMaxMotorPower());
        this.currentPower = power;
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

    // FTC SDK 3.4 removed these calls so I'm commenting out this code
    /*    *//**
     * When the motor is running in one of the <a href="https://en.wikipedia.org/wiki/PID_controller">PID modes</a>
     * the value set using the {@link #setPower(double) setPower()} method is indicative of a
     * desired motor <em>velocity</em> rather than a raw <em>power</em> level. In those modes, the
     * {@link #setMaxSpeed(int) setMaxSpeed()} method provides the interpretation of the speed to which
     * a value of 1.0 passed to {@link #setPower(double) setPower()} should correspond.
     *
     * @param encoderTicksPerSecond the maximum targetable speed for this motor when the motor is
     *                              in one of the PID modes, in units of encoder ticks per second.
     * @see com.qualcomm.robotcore.hardware.DcMotor.RunMode#RUN_USING_ENCODER
     * @see com.qualcomm.robotcore.hardware.DcMotor.RunMode#RUN_TO_POSITION
     * @see #getMaxSpeed()
     *//*
    public void setMaxSpeed(int encoderTicksPerSecond) {
        FTCDcMotor.setMaxSpeed(encoderTicksPerSecond);
    }

    public int getMaxSpeed() {
        return FTCDcMotor.getMaxSpeed();
    }*/
}
