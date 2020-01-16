package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DcServoMotor extends DcMotor8863 {
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


    public DcServoMotor(String motorName, String servoName, double centerValueForward, double centerValueReverse, double deadBandRange, HardwareMap hardwareMap, Telemetry telemetry) {
        super(motorName, hardwareMap, telemetry);
        servoMotor = hardwareMap.get(Servo.class, servoName);
        this.centerValueForward = centerValueForward;
        this.centerValueReverse = centerValueReverse;
        this.deadBandRange = deadBandRange;

    }

    public void setPower(double power) {
        power = Range.clip(power, getMinMotorPower(), getMaxMotorPower());
        this.currentPower = power;
        switch (getCurrentRunMode()) {
            case RUN_WITHOUT_ENCODER:
                setServoSpeed(power);
                break;
            case RUN_TO_POSITION:

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

    @Deprecated
    public void setPowerFloat() {
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior ZeroPowerBehavior) {

    }

    public void setTargetPosition(int position) {
        // set the field holding the desired rotation
        setTargetEncoderCount(position);

    }

    public int getCurrentPosition() {
        return (FTCDcMotor.getCurrentPosition() - baseEncoderPosition);
    }

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

}
