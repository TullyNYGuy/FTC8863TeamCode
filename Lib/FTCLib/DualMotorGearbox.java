package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

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
    private CSVDataFile encoderCSVFile;
    private boolean recordEncoderData = false;
    private ElapsedTime timer;

    PIDFController controller;

    public void setController(PIDFController controller) {
        this.controller = controller;
    }

    private int targetPosition = 0;
    private int targetEncoderTolerance = 0;

    @Override
    public void setRecordEncoderData(boolean recordEncoderData) {
        this.recordEncoderData = recordEncoderData;
        encoderCSVFile = new CSVDataFile("dualMotorEnoderData");
        encoderCSVFile.headerStrings("time", "left", "right", "difference", "left current mA", "right current mA", "current difference");
        timer = new ElapsedTime();
    }

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

    /**
     * This method cannot simply wrap the equivalent method for both motors. The target is a prelude
     * to setting the motor(s) to RUN_TO_POSITION mode. That mode is not viable because the motors
     * can fight each other if there is any difference between the encoders in the two motors. And
     * due to gear lash, we have seen differences. So RUN_TO_POSITION has to be treated as one single
     * controller feeding the same motor power to both motors. In other words, this class has to
     * implement RUN_TO_POSITION on its own.
     * @param virtualTargetEncoderCount
     */
    @Override
    public void setTargetPosition(int virtualTargetEncoderCount) {
        this.targetPosition = virtualTargetEncoderCount;
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

    /**
     * This method cannot simply wrap the equivalent method for both motors. The target tolerance is a prelude
     * to setting the motor(s) to RUN_TO_POSITION mode. That mode is not viable because the motors
     * can fight each other if there is any difference between the encoders in the two motors. And
     * due to gear lash, we have seen differences. So RUN_TO_POSITION has to be treated as one single
     * controller feeding the same motor power to both motors. In other words, this class has to
     * implement RUN_TO_POSITION on its own.
     * @param targetEncoderTolerance
     */
    @Override
    public void setTargetEncoderTolerance(int targetEncoderTolerance) {
        this.targetEncoderTolerance = targetEncoderTolerance;
    }

    // todo investigate how this plays with 2 motors. Will they fight each other?
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

    // todo This will not work. The motor PIDs and feed back will be indepent of each other and will fight each other.
    // todo This needs to be changed to use this controller.
    @Override
    public boolean moveToPosition(double power, double targetPosition, DcMotor8863.FinishBehavior afterCompletion) {
        boolean leftReturn;
        boolean rightReturn;
        leftReturn = leftMotor.moveToPosition(power,targetPosition, afterCompletion);
        rightReturn = rightMotor.moveToPosition(power,targetPosition, afterCompletion);
        if (recordEncoderData) {
           timer.reset();
        }
        return leftReturn && rightReturn;
    }

    // todo This will not work. The motor PIDs and feed back will be indepent of each other and will fight each other.
    // todo This needs to be changed to use this controller.
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
        int leftEncoder;
        int rightEncoder;
        int leftMotorCurrent;
        int rightMotorCurrent;
        leftState = leftMotor.update();
        rightState = rightMotor.update();
        if (recordEncoderData) {
            leftEncoder = leftMotor.getCurrentPosition();
            rightEncoder = rightMotor.getCurrentPosition();
            leftMotorCurrent = (int)leftMotor.getCurrent(CurrentUnit.MILLIAMPS);
            rightMotorCurrent = (int)rightMotor.getCurrent(CurrentUnit.MILLIAMPS);
            encoderCSVFile.writeData(
                    (int)timer.milliseconds(),
                    leftEncoder,
                    rightEncoder,
                    (leftEncoder - rightEncoder),
                    leftMotorCurrent,
                    rightMotorCurrent,
                    leftMotorCurrent - rightMotorCurrent);
        }
        return getCurrentMotorState();
    }

    @Override
    public boolean isMovementComplete() {
        return leftMotor.isMovementComplete() && rightMotor.isMovementComplete();
    }

    /**
     * This method cannot simply wrap the equivalent method for both motors. RUN_TO_POSITION and
     * RUN_USING_ENCODER are both modes the require a PID controller. That mode is not viable because the motors
     * can fight each other if there is any difference between the encoders in the two motors. And
     * due to gear lash, we have seen differences. So RUN_TO_POSITION has to be treated as one single
     * controller feeding the same motor power to both motors. In other words, this class has to
     * implement RUN_TO_POSITION on its own. Same for RUN_USING_ENCODER.
     * @param mode
     */
    @Override
    public void setMode(DcMotor.RunMode mode) {
        switch (mode) {
            case RUN_TO_POSITION:{
                // not handling this now. It is ok since the lift is moving under motion profile and
                // that will just keep on doing its thing, holding at position
                // todo handle this situation long term
            }
            break;
            case RUN_USING_ENCODER: {
                // todo handle this situation long term
            }
            break;
            case RUN_WITHOUT_ENCODER:
            case STOP_AND_RESET_ENCODER:
            case RESET_ENCODERS:
            case RUN_USING_ENCODERS:
            case RUN_WITHOUT_ENCODERS: {
                leftMotor.setMode(mode);
                rightMotor.setMode(mode);
            }
        }
        leftMotor.setMode(mode);
        rightMotor.setMode(mode);
    }

    @Override
    public void setPower(double power) {
        leftMotor.setPower(power);
        rightMotor.setPower(power);
    }
}
