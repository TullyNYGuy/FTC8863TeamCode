package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DcServoMotor extends DcMotor8863 {

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private Servo servoMotor;

    private int baseEncoderPosition = 0;
    /**
     * The last command sent to the servo. If the new command = last command then we don't actually
     * send out the new command.
     */
    private double lastThrottleCommand = 0;
    /**
     * The value that causes no movement of the servo when the direction of the servo is set to
     * forwards.
     */
    private double centerValueForward = 0.51;

    /**
     * The value that causes no movement of the servo when the direction of the servo is set to
     * backwards.
     */
    private double centerValueReverse = 0.46;

    /**
     * Store the current value that causes no movement of the servo
     */
    private double centerValue;

    /**
     * If the speed of the servo is set between -deadBandRange and + deadBandRange then the actual
     * speed of the servo is set to stop
     */
    private double deadBandRange = 0.1;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public DcServoMotor(String motorName, String servoName, double centerValueForward, double centerValueReverse, double deadBandRange, HardwareMap hardwareMap, Telemetry telemetry) {
        // the motor is only going to be used for reading the encoder port since the encoder is
        // plugged into it.
        super(motorName, hardwareMap, telemetry);
        // program the servo for continuous rotation and config the phone for a normal servo, NOT
        // a continuous rotation servo
        servoMotor = hardwareMap.get(Servo.class, servoName);
        this.centerValueForward = centerValueForward;
        this.centerValueReverse = centerValueReverse;
        this.deadBandRange = deadBandRange;
        // must set a default direction. This forces the centervalue to be set to centerValueForward
        setDirection(DcMotorSimple.Direction.FORWARD);
        // get the encoder position and set that base encoder position to that
        baseEncoderPosition = getCurrentPosition();
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    @Override
    public void setPower(double power) {
        // there is no PID here so we don't need to clip the power
        //power = Range.clip(power, getMinMotorPower(), getMaxMotorPower());
        this.currentPower = power;
        switch (getCurrentRunMode()) {
            case RUN_WITHOUT_ENCODER:
            case RUN_USING_ENCODER:
                setServoSpeed(power);
                break;
            case RUN_TO_POSITION:
                setServoSpeed(power);
                break;
        }

    }

    private void setServoSpeed(double throttle) {
        double servoCommand;
        // if the servo command is within the deadband range for the servo, then send out the
        // center value (value that produces no movement) instead. The deadband is a range around 0.
        if (-deadBandRange < throttle && throttle < deadBandRange) {
            // only send out a command to the servo if the value has changed from the last value sent
            // this saves some bandwidth on the bus.
            if (centerValue != lastThrottleCommand) {
                servoMotor.setPosition(centerValue);
                lastThrottleCommand = centerValue;
            }
        } else {
            // I have to translate the -1 to +1 input range to a 0 to +1 range that a servo can take.
            servoCommand = 0.5 * throttle + 0.5;
            servoCommand = Range.clip(servoCommand, 0, 1);
            // only send out a command to the servo if the value has changed from the last value sent
            // this saves some bandwidth on the bus.
            if (throttle != lastThrottleCommand) {
                servoMotor.setPosition(servoCommand);
                lastThrottleCommand = throttle;
            }
        }
    }

    /**
     * Set the direction the motor turns when it is sent a positive command. This method re-defines
     * the meaning of forwards and backwards.
     *
     * @param direction
     */
    @Override
    public void setDirection(DcMotor.Direction direction) {
        if (direction == DcMotor.Direction.FORWARD) {
            centerValue = centerValueForward;
            servoMotor.setDirection(Servo.Direction.FORWARD);
        } else {
            centerValue = centerValueReverse;
            servoMotor.setDirection(Servo.Direction.REVERSE);
        }
        this.direction = direction;

    }

    @Override
    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior ZeroPowerBehavior) {
    }

    @Override
    public void setTargetPosition(int position) {
        // set the field holding the desired rotation
        // this does NOT interact with the motor that the encoder is plugged into
        setTargetEncoderCount(position);
    }

    @Override
    public int getCurrentPosition() {
        return (FTCDcMotor.getCurrentPosition() - baseEncoderPosition);
    }

    @Override
    public void setMode(DcMotor.RunMode mode) {
        if (mode != getCurrentRunMode()) {
            switch (mode) {
                case RUN_USING_ENCODER:
                    mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
                    break;
                case STOP_AND_RESET_ENCODER:
                    setServoSpeed(0);
                    baseEncoderPosition = FTCDcMotor.getCurrentPosition();
                    break;
            }
            setCurrentRunMode(mode);
        }
    }

    @Override
    public MotorState update() {
        return MotorState.COMPLETE_HOLD;
    }

    // moveToPosition is needed in order for the servo to move the attachment to a certain position.
    // Using a normal motor, the movement is controlled by a PID. The normal motor sequence of events
    // is:
    // set the target encoder count
    // set the motor mode to RUN_TO_POSITION
    // set the power
    // use isMotorStateComplete to detemine if the attachment has arrived at the desired position.

    // moveToPosition calls rotateToEncoderCount. It in turn calls several methods that have to be
    // overridden
    // isMotorStateMoving()
    // setFinishBehavior()
    // setTargetPosition()
    // setMode()
    // setMotorSate()
    // setPower()

    // OR just override rotateToEncoderCount

    private boolean isEncoderAtTarget(int desiredEncoderCount) {
        if (Math.abs(desiredEncoderCount - getCurrentPosition()) <= 40) {
            setPower(0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean rotateToEncoderCount(double power, int encoderCount, FinishBehavior afterCompletion) {
        setTargetEncoderCount(encoderCount);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if (!isEncoderAtTarget(encoderCount)) {
            if (encoderCount > getCurrentPosition()) {
                setPower(power);
            } else {
                setPower(-power);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isMotorStateComplete() {
        return isEncoderAtTarget(getTargetEncoderCount());
    }

}
