package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * This class allows you to read and control an encoder that is plugged into a motor port without
 * affecting the motor itself. Tt can alsobe used with a motor to enhance its functionality.
 * <p>
 * It was refactored out of DcMotor8863 so that it would be easier to reset just the encoder and
 * set target encoder value and get encoder values that are adjusted for the value of the encoder
 * when it was reset.
 */
public class EncoderWithoutMotor {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private DcMotor motor;

    private String motorName;

    public String getMotorName() {
        return motorName;
    }

    private DcMotor8863.MotorType motorType = DcMotor8863.MotorType.ANDYMARK_40;

    public DcMotor8863.MotorType getMotorType() {
        return motorType;
    }

    public void setMotorType(DcMotor8863.MotorType motorType) {
        this.motorType = motorType;
        setCountsPerRevForMotorType(motorType);
    }

    private DcMotorSimple.Direction direction = DcMotorSimple.Direction.FORWARD;

    public DcMotorSimple.Direction getDirection() {
        return direction;
    }

    public void setDirection(DcMotorSimple.Direction direction) {
        this.direction = direction;
    }

    private int encoderMultiplier = +1;

    /**
     * This is the encoder value when the last reset() was issued. It is used to adjust the
     * encoder value so that they look as though they are based on 0 which was established
     * at the last reset().
     */
    private int baseEncoderValue = 0;

    public void setBaseEncoderValue(int baseEncoderValue) {
        this.baseEncoderValue = baseEncoderValue;
    }

    public int getBaseEncoderValue() {
        return baseEncoderValue;
    }

    /**
     * The no load max speed in encoder ticks per second
     */
    private int maxEncoderTicksPerSecond = 0;

    public int getMaxEncoderTicksPerSecond() {
        return this.maxEncoderTicksPerSecond;
    }

    public void setMaxEncoderTicksPerSecond(int maxEncoderCountsPerSec) {
        this.maxEncoderTicksPerSecond = maxEncoderCountsPerSec;
    }

    /**
     * Number of cm or degrees or whatever moved for each motor shaft revolution
     */
    private double movementPerRev = 0;

    public double getMovementPerRev() {
        return movementPerRev;
    }

    public void setMovementPerRev(double movementPerRev) {
        this.movementPerRev = movementPerRev;
    }

    /**
     * Encoder counts per shaft revolution for this type of motor
     */
    private int countsPerRev = 0;

    public int getCountsPerRev() {
        return countsPerRev;
    }

    /**
     * Holds the desired encoder count for RUN_TO_POSITION. The encoder count is from the point of
     * view of the encoder. It is not raw motor encoder count.
     */
    private int targetEncoderCount = 0;

    public int getTargetEncoderCount() {
        return targetEncoderCount;
    }

    protected void setTargetEncoderCount(int targetEncoderCount) {
        this.targetEncoderCount = targetEncoderCount;
    }

    /**
     * The tolerance range for saying if the encoder count target has been reached.
     */
    private int targetEncoderTolerance = 10;

    public int getTargetEncoderTolerance() {
        return targetEncoderTolerance;
    }

    public void setTargetEncoderTolerance(int targetEncoderTolerance) {
        this.targetEncoderTolerance = targetEncoderTolerance;
    }

    /**
     * last encoder value
     */
    private int lastEncoderValue = 0;

    public int getLastEncoderValue() {
        return lastEncoderValue;
    }

    public void setLastEncoderValue(int lastEncoderValue) {
        this.lastEncoderValue = lastEncoderValue;
    }

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
    ;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    /**
     * Set the number of encoder counts per revolution of the shaft based on the type of motor.
     *
     * @param motorType Type of motor.
     * @return Number of encoder counts per revolution of the output shaft of the motor
     */
    private int setCountsPerRevForMotorType(DcMotor8863.MotorType motorType) {
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
            case USDIGITAL_360PPR_ENCODER:
                this.countsPerRev = 1440;
                break;
            default:
                this.countsPerRev = 0;
                break;
        }
        return getCountsPerRev();
    }


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public EncoderWithoutMotor(String motorName, DcMotor8863.MotorType motorType,
                               DcMotorSimple.Direction direction,
                               HardwareMap hardwareMap) {
        this.motorName = motorName;
        this.motor = hardwareMap.get(DcMotor.class, motorName);
        this.motorType = motorType;
        this.direction = direction;
    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * This method will setup to adjust the encoder value when the encoder direction is set opposite
     * of the motor direction. This is only really valid when the encoder is attached to a motor
     * port but is being used independently of the motor. Then, you can set the encoder to be a
     * direction that is different from the motor. Since the encoder is really controlled by the
     * motor, its direction is controlled by the setting of the motor direction. But this method
     * will negate the value if the encoder, which is being used independently of the motor, and is
     * kind of like a "virtual encoder", has its direction set differently from the motor direction.
     * <p>
     * Note that when the encoder is uses as part of a DcMotor8863, its direction is synched to the
     * motor direction and can't be different.
     */
    private int adjustForDirection(int encoderValue) {
        if (motor.getDirection() != this.direction) {
            return encoderValue * -1;
        } else {
            return encoderValue;
        }
    }

    /**
     * When someone changes the motor direction, the base encoder value has to be negated. See the explaination
     * later in this code.
     *
     * @param direction
     */
    public void setDirectionDueToMotorDirectionChange(DcMotorSimple.Direction direction) {
        if (direction != this.direction) {
            baseEncoderValue = baseEncoderValue * -1;
            setDirection(direction);
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    /**
     * Get the raw encoder value from the motor and make that value the basis for all future
     * encoder actions by saving for use in later calcuations.
     */
    public void reset() {
        // We have to translate the value from the motor direction point of view
        // to the encoder (user) direction point of view. Examples:
        // motor direction = FORWARD, encoder direction = FORWARD, motor encoder value = 100 -> base = 100
        // motor direction = REVERSE, encoder direction = REVERSE, motor encoder value = -100 -> base = -100
        // motor direction = REVERSE, encoder direction = FORWARD, motor encoder value = -100 -> base = 100
        baseEncoderValue = adjustForDirection(motor.getCurrentPosition());
    }

    //*********************************************************************************************
    //  These methods are all dealing with the relationship between the encoder counts and a
    //  real world thing they are related to.
    //*********************************************************************************************

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
    public void setLastEncoderCountToCurrentPosition() {
        setLastEncoderValue(getCurrentPosition());
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
        return getMovementForEncoderCount(getCurrentPosition() - getLastEncoderValue());
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

    //**********************************************************************************************
    // Methods that interface to the encoder port on the motor
    //**********************************************************************************************

    /**
     * Get actual encoder count that is not adjusted for anything.
     *
     * @return
     */
    public int getCurrentPositionNotAdjusted() {
        return motor.getCurrentPosition();
    }

    // Thinking about the encoder vs the motor direction:
    //
    // Scenario A:
    // Suppose at time = 10 sec motor and encoder direction are FORWARD. An encoder reset() occurs.
    // This is not due to a motor reset(STOP_AND_RESET_ENCODER). The encoder count is captured and
    // stored in baseEncoderValue:
    // Shown below are the encoder counts from the motor and encoder points of view
    //
    // baseEncoderValue = 100
    // motor      0         100                                  1000
    // encoder -100           0                                   900
    //                        ^
    //                        |
    //                  base encoder value
    // getCurrentPosition = +1000 (motor)   - +100 (base)  = +900 (encoder)
    // getTargetPosition  =  +900 (encoder) + +100 (base) = +1000 (motor)
    //
    // Scenario B:
    // Sometime later the motor direction is reversed. Since the DcMotor8863
    // setDirection() also sets the encoder direction, both are now REVERSE. BUT, what was previously
    // encoder value of 100 is now -100. So the base encoder value needs to be adjusted when the
    // direction changes.
    // motor      0        -100                                 -1000
    // encoder +100           0                                  -900
    //                        ^
    //                        |
    //                  base encoder value
    // getCurrentPosition = -1000 (motor)   - -100 (base)  =  -900 (encoder)
    // getTargetPosition  =  -900 (encoder) + -100 (base)  = -1000 (motor)
    //
    // Scenario C:
    // Sometime after scenario A, the encoder, but not the motor direction is reversed. The motor is FORWARD.
    // The encoder is REVERSE. The previous base encoder value was 100. From the motor point of view
    // it is still 100. But from the user (encoder) point of view
    // motor      0        +100                                 +1000
    // encoder +100           0                                  -900
    //                        ^
    //                        |
    //                  base encoder value
    // getCurrentPosition = +1000 (motor)   - +100 (base)  =  +900 (encoder)
    //      This is not the correct value so there are 2 options:
    //      flip the sign on the answer +900 -> -900
    // getTargetPosition  =  -900 (encoder) + +100 (base)  =  -800 (motor)
    //      This is also not the correct answer. The only way to arrive at the correct answer
    //      is to flip the sign on the encoder value:
    // getTargetPosition  =  -(-900) (encoder) + +100 (base)  =  +1000 (motor)
    //
    // working it the other way, starting with both REVERSE
    //
    // Scenario A:
    // Suppose at time = 10 sec motor and encoder direction are REVERSE. An encoder reset() occurs.
    // This is not due to a motor reset(STOP_AND_RESET_ENCODER). The encoder count is captured and
    // stored in baseEncoderValue:
    // Shown below are the encoder counts from the motor and encoder points of view
    //
    // baseEncoderValue = -100
    // motor      0        -100                                 -1000
    // encoder +100           0                                  -900
    //                        ^
    //                        |
    //                  base encoder value
    // getCurrentPosition = -1000 (motor)   - -100 (base)  = -900 (encoder)
    // getTargetPosition  =  -900 (encoder) + -100 (base) = -1000 (motor)
    //
    // Scenario B:
    // Sometime later the motor direction is reversed. Since the DcMotor8863
    // setDirection() also sets the encoder direction, both are now FORWARD. BUT, what was previously
    // encoder value of -100 is now +100. So the base encoder value needs to be adjusted when the
    // direction changes.
    // motor      0        +100                                 +1000
    // encoder -100           0                                  +900
    //                        ^
    //                        |
    //                  base encoder value
    // getCurrentPosition = +1000 (motor)   - +100 (base)  =  +900 (encoder)
    // getTargetPosition  =  +900 (encoder) + +100 (base)  = +1000 (motor)
    //
    // Scenario C:
    // Sometime after scenario A, the encoder, but not the motor direction is reversed. The motor is REVERSE.
    // The encoder is FORWARD. The previous base encoder value was -100. From the motor point of view
    // it is still -100. But from the user (encoder) point of view
    // motor      0        -100                                 -1000
    // encoder -100           0                                  +900
    //                        ^
    //                        |
    //                  base encoder value
    // getCurrentPosition = -1000 (motor)   - -100 (base)  =  -900 (encoder)
    //      This is not the correct value so there are 2 options:
    //      flip the sign on the answer +900 -> -900
    //      flip the sign on the motor value during the calculation
    // getCurrentPosition = -(-1000) (motor)   - +100 (base)  =  +900 (encoder)
    // getTargetPosition  =   +900 (encoder)   + -100 (base)  =  +800 (motor)
    //      This is also not the correct answer. The only way to arrive at the correct answer
    //      is to flip the sign on the encoder value:
    // getTargetPosition  =  -(+900) (encoder) + -100 (base)  =  -1000 (motor)

    // Conclusions:
    //   1 - must flip the sign of the baseEncoderValue whenever the motor direction is changed
    //   2 - getCurrentPosition must flip the sign of the answer whenever the direction of the
    //       encoder and motor are different
    //   3 = getTargetPosition must flip the sign of the encoder value whenever the direcion of the
    //       motor and encoder are different.


    /**
     * Get the real encoder count from the encoder on the motor port and then adjust it for two
     * things:
     * the base encoder value captured when the encoder was reset
     * any direction difference between the motor and the encoder
     *
     * @return
     */
    public int getCurrentPosition() {
        // if the direction of the motor and encoder are different then flip the sign of the
        // answer based on the algorithm above
        return adjustForDirection(motor.getCurrentPosition() - baseEncoderValue);
    }

    /**
     * Get the target position after adjusting for the reset position of the encoder and any
     * difference in direction between the encoder and motor.
     *
     * @param positionToBeAdjusted - this is a position from the point of view of the encoder or
     *                             user
     * @return - target position from the point of view of the motor
     */
    public int getTargetPosition(int positionToBeAdjusted) {
        // flip the sign on the encoder referenced target based on the algorithm above
        return adjustForDirection(positionToBeAdjusted) + baseEncoderValue;
    }

    //**********************************************************************************************
    // Tests
    //**********************************************************************************************

    public void test(LinearOpMode opMode) {
        ElapsedTime timer = new ElapsedTime();
        timer.reset();
        baseEncoderValue = -100;
        double timerLimit = 10000;

        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        setDirectionDueToMotorDirectionChange(DcMotorSimple.Direction.FORWARD);
        // base = -100
        // expect to see + encoder values

        while (opMode.opModeIsActive() && timer.milliseconds() < timerLimit) {
            opMode.telemetry.addData("encoder and motor directions ", "are FORWARD");
            opMode.telemetry.addData("base encoder = ", baseEncoderValue);
            opMode.telemetry.addData("actual encoder = ", getCurrentPositionNotAdjusted());
            opMode.telemetry.addData("adjusted encoder = ", getCurrentPosition());
            opMode.telemetry.update();
            opMode.idle();
        }

        timer.reset();
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        setDirectionDueToMotorDirectionChange(DcMotorSimple.Direction.REVERSE);
        setDirection(DcMotorSimple.Direction.REVERSE);
        // base = +100
        // expect to see sign change on adjusted encoder value but value is same

        while (opMode.opModeIsActive() && timer.milliseconds() < timerLimit) {
            opMode.telemetry.addData("encoder and motor directions ", "are REVERSE");
            opMode.telemetry.addData("base encoder = ", baseEncoderValue);
            opMode.telemetry.addData("actual encoder = ", getCurrentPositionNotAdjusted());
            opMode.telemetry.addData("adjusted encoder = ", getCurrentPosition());
            opMode.telemetry.update();
            opMode.idle();
        }

        timer.reset();
        // motor is still reverse. base encoder = 100
        setDirection(DcMotorSimple.Direction.FORWARD);

        // expect to see +100 for encoder because the directions are different

        while (opMode.opModeIsActive() && timer.milliseconds() < timerLimit) {
            opMode.telemetry.addData("motor direction = ", "REVERSE");
            opMode.telemetry.addData("encoder direction = ", "FORWARD");
            opMode.telemetry.addData("base encoder = ", baseEncoderValue);
            opMode.telemetry.addData("actual encoder = ", getCurrentPositionNotAdjusted());
            opMode.telemetry.addData("adjusted encoder = ", getCurrentPosition());
            opMode.telemetry.update();
            opMode.idle();
        }

        timer.reset();
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        setDirectionDueToMotorDirectionChange(DcMotorSimple.Direction.FORWARD);
        // base = +100
        setDirection(DcMotorSimple.Direction.REVERSE);

        // expect to see +100 for encoder

        while (opMode.opModeIsActive() && timer.milliseconds() < timerLimit) {
            opMode.telemetry.addData("encoder and motor directions ", "are different");
            opMode.telemetry.addData("base encoder = ", baseEncoderValue);
            opMode.telemetry.addData("actual encoder = ", getCurrentPositionNotAdjusted());
            opMode.telemetry.addData("adjusted encoder = ", getCurrentPosition());
            opMode.telemetry.update();
            opMode.idle();
        }

        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        reset();
        timer.reset();

        // expect to see encoder track motor encoder

        while (opMode.opModeIsActive() && timer.milliseconds() < timerLimit) {
            opMode.telemetry.addData("reset", " encoder");
            opMode.telemetry.addData("encoder and motor directions ", "are FORWARD");
            opMode.telemetry.addData("base encoder = ", baseEncoderValue);
            opMode.telemetry.addData("actual encoder = ", getCurrentPositionNotAdjusted());
            opMode.telemetry.addData("adjusted encoder = ", getCurrentPosition());
            opMode.telemetry.update();
            opMode.idle();
        }
    }
}
