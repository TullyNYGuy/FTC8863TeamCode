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

    /**
     * The last command sent to the servo. If the new command = last command then we don't actually
     * send out the new command.
     */
    private double lastServoCommand = 0;
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


        // it is assumed that the encoder attached to this servo in order to provide position
        // feedback is a US Digital encoder that puts out 1440 ticks per revolution
        super(motorName, MotorType.USDIGITAL_360PPR_ENCODER, hardwareMap, telemetry);
        resetEncoder();

        // program the servo for continuous rotation and config the phone for a normal servo, NOT
        // a continuous rotation servo
        servoMotor = hardwareMap.get(Servo.class, servoName);
        this.centerValueForward = centerValueForward;
        this.centerValueReverse = centerValueReverse;
        this.deadBandRange = deadBandRange;
        // must set a default direction. This forces the centervalue to be set to centerValueForward
        // this sets the direction of the servo, not the motor or the encoder
        setDirection(DcMotorSimple.Direction.FORWARD);
    }

    //*********************************************************************************************
    //     methods that interact with the servo.
    //*********************************************************************************************

    /**
     * Set the speed of the servo.
     *
     * @param power
     */
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

    private void setServoSpeed(double speed) {
        double servoCommand;
        // if the servo command is within the deadband range for the servo, then send out the
        // center value (value that produces no movement) instead. The deadband is a range around 0.
        if (-deadBandRange < speed && speed < deadBandRange) {
            // only send out a command to the servo if the value has changed from the last value sent
            // this saves some bandwidth on the bus.
            if (centerValue != lastServoCommand) {
                servoMotor.setPosition(centerValue);
                lastServoCommand = centerValue;
            }
        } else {
            // I have to translate the -1 to +1 input range to a 0 to +1 range that a servo can take.
            servoCommand = 0.5 * speed + 0.5;
            servoCommand = Range.clip(servoCommand, 0, 1);
            // only send out a command to the servo if the value has changed from the last value sent
            // this saves some bandwidth on the bus.
            if (servoCommand != lastServoCommand) {
                servoMotor.setPosition(servoCommand);
                lastServoCommand = servoCommand;
            }
        }
    }


    /**
     * This method actually set the direction of the servo, NOT the motor.
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

    //*********************************************************************************************
    //     methods that interact with the encoder.
    //*********************************************************************************************

    public void setEncoderDirection(DcMotorSimple.Direction direction) {
        encoder.setDirection(direction);
    }

    //*********************************************************************************************
    //     methods that interact with the motor.
    //*********************************************************************************************

    /**
     * The motor can't be affected by methods called from this class because something else is
     * actually using the motor. So override the method and remove its affect on the motor.
     * @param ZeroPowerBehavior
     */
    @Override
    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior ZeroPowerBehavior) {
    }

    @Override
    public void setTargetPosition(int position) {
        // set the field holding the desired rotation
        // this does NOT interact with the motor that the encoder is plugged into
        encoder.setTargetEncoderCount(position);
    }

    @Override
    public int getCurrentPosition() {
        return encoder.getCurrentPosition();
    }

    /**
     * The motor can't be affected by methods called from this class because something else is
     * actually using the motor. So override the method and remove its affect on the motor.
     * @param mode
     */
    @Override
    public void setMode(DcMotor.RunMode mode) {
        if (mode != getCurrentRunMode()) {
            switch (mode) {
                case RUN_USING_ENCODER:
                    mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
                    break;
                case STOP_AND_RESET_ENCODER:
                    // THIS doeds NOT reset the motor!
                    setServoSpeed(0);
                    resetEncoder();
                    break;
            }
            setCurrentRunMode(mode);
        }
    }

    /**
     * The motor can't be affected by methods called from this class because something else is
     * actually using the motor. So override the method and remove its affect on the motor.
     * @return
     */
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
        if (Math.abs(desiredEncoderCount - encoder.getCurrentPosition()) <= 40) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * The motor can't be affected by methods called from this class because something else is
     * actually using the motor. So override the method and remove its affect on the motor.
     *
     * Run the servo until the encoder count target is reached.
     * @param power           Power input for the motor. Note that it will be clipped to less than +/-0.8.
     * @param encoderCount    Motor will be rotated so that it results in a movement of this distance.
     * @param afterCompletion What to do after this movement is completed: HOLD or COAST
     * @return
     */
    @Override
    public boolean rotateToEncoderCount(double power, int encoderCount, FinishBehavior afterCompletion) {
        encoder.setTargetEncoderCount(encoderCount);
        setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // if we are not at the target encoder count already. Note this isEncoderAtTarget() uses
        // a tolerance zone and also shuts off the servo.
        if (!isEncoderAtTarget(encoderCount)) {
            // is the target in front of the current position?
            if (encoderCount > getCurrentPosition()) {
                // yes set the power so the serrvo extends the arm
                setPower(power);
            } else {
                // target is behind our current position, set the servo to retract the arm
                setPower(-power);
            }
            return true;
        }
        return false;
    }

    /**
     * Redefine completion of the movement. This method is called from the ExtensionRetractionMechanism
     * state machine in the MOVING TO POSITION section. isMoveToPositionComplete() is called every
     * update cycle to check if the position target has been reached. That in turn calls this method.
     *
     * @return
     */
    @Override
    public boolean isMotorStateComplete() {
        boolean result = false;
        if (isEncoderAtTarget(getTargetEncoderCount())) {
            // stop the servo if the target is reached
            setPower(0);
            result = true;
        }
        return result;
    }

}
