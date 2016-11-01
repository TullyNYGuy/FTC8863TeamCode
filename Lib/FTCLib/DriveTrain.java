package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.ResQLib.RobotConfigMapping;

public class DriveTrain {

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private float rightPower = 0;
    private float leftPower = 0;
    private float cmPerRotation = 0;

    public DcMotor8863 rightDriveMotor;
    public DcMotor8863 leftDriveMotor;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getMotorPosition
    //*********************************************************************************************

    public float getRightPower(){
        return this.rightPower;
    }

    public void setRightPower(float power){
        this.rightPower = power;
    }

    public float getLeftPower(){
        return this.leftPower;
    }

    public void setLeftPower(float power){
        this.leftPower = power;
    }

    public float getCmPerRotation() {
        return cmPerRotation;
    }

    public void setCmPerRotation(float cmPerRotation) {
        this.cmPerRotation = cmPerRotation;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public DriveTrain(HardwareMap hardwareMap) {
        leftDriveMotor = new DcMotor8863(RobotConfigMapping.getLeftDriveMotorName(), hardwareMap);
        rightDriveMotor = new DcMotor8863(RobotConfigMapping.getRightDriveMotorName(), hardwareMap);

        rightDriveMotor.setDirection(DcMotor.Direction.REVERSE);
        rightDriveMotor.setMaxMotorPower(1);
        rightDriveMotor.setMinMotorPower(-1);
        rightDriveMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        rightDriveMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        rightDriveMotor.setEncoderTolerance(3);
        rightDriveMotor.setMovementPerRev(10);
        rightDriveMotor.setNextMotorState(DcMotor8863.NextMotorState.HOLD);

        leftDriveMotor.setDirection(DcMotor.Direction.FORWARD);
        leftDriveMotor.setMaxMotorPower(1);
        leftDriveMotor.setMinMotorPower(-1);
        leftDriveMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        leftDriveMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        leftDriveMotor.setEncoderTolerance(3);
        leftDriveMotor.setMovementPerRev(10);
        leftDriveMotor.setNextMotorState(DcMotor8863.NextMotorState.HOLD);
    }

    /**
     * This method is a factory method. It returns a driveTrain object that is setup to float the
     * motors after a movement is completed.
     * @param hardwareMap
     * @return Instance of a driveTrain (a driveTrain oject) optimized for TeleOp
     */
    public static DriveTrain DriveTrainTeleop(HardwareMap hardwareMap) {
        DriveTrain driveTrain = new DriveTrain(hardwareMap);
        driveTrain.rightDriveMotor.setNextMotorState(DcMotor8863.NextMotorState.FLOAT);
        driveTrain.leftDriveMotor.setNextMotorState(DcMotor8863.NextMotorState.FLOAT);
        return driveTrain;
    }

    /**
     * This method is a factory method. It returns a driveTrain object that is setup to float the
     * motors after a movement is completed.
     * @param hardwareMap
     * @return Instance of a driveTrain (a driveTrain oject) optimized for TeleOp
     */
    public static DriveTrain DriveTrainAutonomous(HardwareMap hardwareMap) {
        DriveTrain driveTrain = new DriveTrain(hardwareMap);
        driveTrain.rightDriveMotor.setNextMotorState(DcMotor8863.NextMotorState.HOLD);
        driveTrain.leftDriveMotor.setNextMotorState(DcMotor8863.NextMotorState.HOLD);
        return driveTrain;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    public void driveDistance(double power, double distance){
        rightDriveMotor.rotateToPosition(power, distance, DcMotor8863.NextMotorState.HOLD);
        rightDriveMotor.rotateToPosition(power, distance, DcMotor8863.NextMotorState.HOLD);
    }

/*    public boolean isRotationComplete() {
        if(rightDriveMotor.isRotationComplete() && leftDriveMotor.isRotationComplete()) {
            return true;
        } else {
            return false;
        }

    }*/

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    // Joystick info for reference.
    // Normally:
    // y ranges from -1 to 1, where -1 is full up and 1 is full down
    // x ranges from -1 to 1, where -1 is full left and 1 is full right
    // The Joystick object allow you to invert the sign on the joystick
    // I like to think of the Y up as positive so:
    // All code assumes that joystick y is positive when the joystick is up.
    // Use the JoyStick object INVERT_SIGN to accomplish that.

    // The differentialDrive is meant to use one joystick to control the drive train.
    // Moving the joystick forward and backward controls speed (throttle).
    // Moving the joystick left or right controls direction.
    /**
     * Differential drive has a master speed that gets applied to both motors. That speed is the
     * same. Then the speed to the left and right is adjusted up and down, opposite of each other
     * to turn the robot.
     * @param throttle Master speed applied to both motors.
     * @param direction Adjustment applied to the master speed. Add to left. Subtract from right.
     */
    public void differentialDrive(double throttle, double direction){

        // To steer the robot left, the left motor needs to reduce power and the right needs to increase.
        // To steer the robot right, the left motor needs to increase power and the left needs to reduce.
        // Since left on the joystick is negative, we need to add the direction for the left motor and
        // subtract from the right motor
        leftDriveMotor.setPower((float)(throttle + direction));
        rightDriveMotor.setPower((float)(throttle - direction));
    }

    // The tank drive uses the left joystick to control the left drive motor and the right joystick
    // to control the right drive motor.

    /**
     * The tank drive applies power values to the left and right motors separately.
     *
     * @param leftValue
     * @param rightValue
     */
    public void tankDrive(double leftValue, double rightValue){
        leftDriveMotor.setPower((float)leftValue);
        rightDriveMotor.setPower((float)rightValue);
    }
}
