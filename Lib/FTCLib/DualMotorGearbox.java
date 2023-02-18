package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * This class is a kludge. It makes two motors in a gearbox look like a single motor.
 * You will need to connect the power to one of the motor opposite of the normal connections:
 * red to black
 * black to red.
 * This forces one motor to spin opposite of the other for the same motor command as well as makes
 * the encoders move in the same direction.
 */
public class DualMotorGearbox implements DcMotor8863Interface{

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private DcMotor8863 leftMotor;
    private DcMotor8863 rightMotor;
    private MotorConstants motorConstants;

    private DcMotor.Direction direction = DcMotor.Direction.FORWARD;

    /**
     * Shows which direction the output shaft is turning.
     *
     * @return The direction that the motor is spinning
     */
    public DcMotor.Direction getDirection() {
        return direction;
    }

    /**
     * Set the motors to spin the output shaft forward or backward.
     * @param direction
     */
    public void setDirection(DcMotor.Direction direction) {
        if (direction == DcMotor.Direction.FORWARD) {
            leftMotor.setDirection(DcMotor.Direction.FORWARD);
            rightMotor.setDirection(DcMotor.Direction.FORWARD);
            this.direction = DcMotor.Direction.FORWARD;
        }

        if (direction == DcMotor.Direction.REVERSE) {
            leftMotor.setDirection(DcMotor.Direction.REVERSE);
            rightMotor.setDirection(DcMotor.Direction.REVERSE);
            this.direction = DcMotor.Direction.REVERSE;
        }

    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public DualMotorGearbox(String leftMotorName,
                            String rightMotorName,
                            HardwareMap hardwareMap,
                            Telemetry telemetry,
                            MotorConstants.MotorType motorType){
        leftMotor = new DcMotor8863(leftMotorName, hardwareMap, telemetry);
        leftMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_1150);
        rightMotor = new DcMotor8863(rightMotorName, hardwareMap, telemetry);
        rightMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_1150);
        motorConstants = new MotorConstants(motorType);
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

    @Override
    public void setBaseEncoderCount(int baseEncoderCount) {
        leftMotor.setBaseEncoderCount(baseEncoderCount);
        rightMotor.setBaseEncoderCount(baseEncoderCount);
    }

    @Override
    public int getBaseEncoderCount() {
        // left relative base count is the same as right
        return leftMotor.getBaseEncoderCount();
    }

    @Override
    public void setTargetPosition(int virtualTargetEncoderCount) {
        leftMotor.setTargetPosition(virtualTargetEncoderCount);
        rightMotor.setTargetPosition(virtualTargetEncoderCount);
    }

    @Override
    public int getCurrentPosition() {
        // average of the encoders. This assumes they move in the same direction (both positive or
        // both negative.
        return (int)((leftMotor.getCurrentPosition() + rightMotor.getCurrentPosition())/2);
    }

    @Override
    public double getPositionInTermsOfAttachment() {
        // right motor is the same as the left motor since we are using the average of the left
        // and right encoders
        return leftMotor.getMovementForEncoderCount(getCurrentPosition());
    }

    @Override
    public void setMotorType(DcMotor8863.MotorType motorType) {
        // Do nothing. The motor type was specified in the constructor.
    }

    @Override
    public int getCountsPerRev() {
        return motorConstants.getCountsPerRev();
    }

    @Override
    public void setMovementPerRev(double movementPerRev) {
        leftMotor.setMovementPerRev(movementPerRev);
        rightMotor.setMovementPerRev(movementPerRev);
    }

    @Override
    public void setTargetEncoderTolerance(int targetEncoderTolerance) {
        leftMotor.setTargetEncoderTolerance(targetEncoderTolerance);
        rightMotor.setTargetEncoderTolerance(targetEncoderTolerance);
    }

    @Override
    public void setFinishBehavior(DcMotor8863.FinishBehavior finishBehavior) {
        leftMotor.setFinishBehavior(finishBehavior);
        rightMotor.setFinishBehavior(finishBehavior);
    }

    @Override
    public DcMotor8863.MotorState getCurrentMotorState() {
        DcMotor8863.MotorState combinationState = DcMotor8863.MotorState.MOVING_PID_NO_POWER_RAMP;
        DcMotor8863.MotorState leftState = leftMotor.getCurrentMotorState();
        DcMotor8863.MotorState rightState = rightMotor.getCurrentMotorState();
        if (leftState == DcMotor8863.MotorState.IDLE && rightState == DcMotor8863.MotorState.IDLE) {
            combinationState = DcMotor8863.MotorState.IDLE;
        }
        if (leftState == DcMotor8863.MotorState.HOLD && rightState == DcMotor8863.MotorState.HOLD) {
            combinationState = DcMotor8863.MotorState.HOLD;
        }
        if (leftState == DcMotor8863.MotorState.MOVING_PID_NO_POWER_RAMP && rightState == DcMotor8863.MotorState.MOVING_PID_NO_POWER_RAMP) {
            combinationState = DcMotor8863.MotorState.MOVING_PID_NO_POWER_RAMP;
        }
        if (leftState == DcMotor8863.MotorState.COMPLETE_FLOAT && rightState == DcMotor8863.MotorState.COMPLETE_FLOAT) {
            combinationState = DcMotor8863.MotorState.COMPLETE_FLOAT;
        }
        if (leftState == DcMotor8863.MotorState.COMPLETE_HOLD && rightState == DcMotor8863.MotorState.COMPLETE_HOLD) {
            combinationState = DcMotor8863.MotorState.COMPLETE_HOLD;
        }
        return combinationState;
    }

    @Override
    public boolean moveToPosition(double power, double targetPosition, DcMotor8863.FinishBehavior afterCompletion) {
        boolean leftReturn;
        boolean rightReturn;
        leftReturn = leftMotor.moveToPosition(power,targetPosition, afterCompletion);
        rightReturn = rightMotor.moveToPosition(power,targetPosition, afterCompletion);
        return leftReturn && rightReturn;
    }

    @Override
    public boolean rotateNumberOfRevolutions(double power, double revs, DcMotor8863.FinishBehavior afterCompletion) {
        boolean leftReturn;
        boolean rightReturn;
        leftReturn = leftMotor.rotateNumberOfRevolutions(power, revs, afterCompletion);
        rightReturn = rightMotor.rotateNumberOfRevolutions(power, revs, afterCompletion);
        return leftReturn && rightReturn;
    }

    @Override
    public DcMotor8863.MotorState update() {
        DcMotor8863.MotorState leftState;
        DcMotor8863.MotorState rightState;
        leftState = leftMotor.update();
        rightState = rightMotor.update();
        return getCurrentMotorState();
    }

    @Override
    public boolean isMovementComplete() {
        return leftMotor.isMovementComplete() && rightMotor.isMovementComplete();
    }

    @Override
    public void setMode(DcMotor.RunMode mode) {
        leftMotor.setMode(mode);
        rightMotor.setMode(mode);
    }

    @Override
    public void setPower(double power) {
        leftMotor.setPower(power);
        rightMotor.setPower(power);
    }
}
