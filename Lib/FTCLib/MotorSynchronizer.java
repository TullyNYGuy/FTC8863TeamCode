package org.firstinspires.ftc.teamcode.Lib.FTCLib;


/**
 * This class is intended to synchronize the position of two motors as they move so that they move
 * together. It assumes that the motors are in RUN_TO_POSITION mode. It manipulates the max power
 * of each motor to attempt to keep them in the same position at each instance in time.
 */
public class MotorSynchronizer {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum MovementDirection {
        INCREASING_ENCODER,
        DECREASING_ENCODER
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private DcMotor8863 motor1;
    private DcMotor8863 motor2;

    private PIDControl pidControl;

    private int motor1Position;
    private int motor2Position;

    /**
     * Once the PID has been setup is complete, this gets set to true.
     */
    private boolean setupPIDComplete = false;

    /**
     * The correction produced by the PID. It gets applied to the motor powers.
     */
    private double correction = 0;

    public double getCorrection() {
        return correction;
    }

    /**
     * The motor power for both motors that is the one you want them to run at.
     */
    private double motorPowerDesired = 0;

    public double getMotorPowerDesired() {
        return motorPowerDesired;
    }

    public void setMotorPowerDesired(double motorPowerDesired) {
        this.motorPowerDesired = motorPowerDesired;
    }

    private double motor1PowerAdjusted = 0;

    /**
     * The motor power for motor 1 after being adjusted by the PID
     *
     * @return
     */
    public double getMotor1PowerAdjusted() {
        return motor1PowerAdjusted;
    }

    /**
     * The motor power for motor 2 after being adjusted by the PID
     *
     * @return
     */
    private double motor2PowerAdjusted = 0;

    public double getMotor2PowerAdjusted() {
        return motor2PowerAdjusted;
    }

    /**
     * The adjustment of the desired power depends on the direction the motors are moving.
     * If the encoder values are increasing due to the movement, then
     * lagging motor has the lower encoder count, leading has the higher
     * increase power to motor with lower encoder count, reduce power to the one with higher count
     * <p>
     * If the encoder values are decreasing due to the movement, then
     * lagging motor has the higher encoder count, leading has the lower
     * increase power to motor with higher encoder count, reduce power to the one with lower count
     * <p>
     * This variable controls the adjustment to get the proper power given the direction.
     */
    private MovementDirection movementDirection = MovementDirection.INCREASING_ENCODER;

    public MovementDirection getMovementDirection() {
        return movementDirection;
    }

    public void setMovementDirection(MovementDirection movementDirection) {
        this.movementDirection = movementDirection;
    }

    /**
     * This is the targeted difference between two encoder counts. Normally this is 0. But in some
     * situations you may want to maintain a difference. IMPORTANT: This value is assumed to be
     * motor 1 encoder value - motor 2 encoder value
     */
    private int targetEncoderDifference = 0;

    public int getTargetEncoderDifference() {
        return targetEncoderDifference;
    }

    public void setTargetEncoderDifference(int targetEncoderDifference) {
        this.targetEncoderDifference = targetEncoderDifference;
    }
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public MotorSynchronizer(DcMotor8863 motor1, DcMotor8863 motor2) {
        this.motor1 = motor1;
        this.motor2 = motor2;
        pidControl = new PIDControl();
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

    /**
     * Constructor that creates an object intended to make sure that 2 motor move at the same rate.
     *
     * @param targetEncoderDifference - ASSUMED TO BE MOTOR 1 ENCODER - MOTOR 2 ENCODER
     * @param kp
     * @param ki
     * @param kd
     */
    public void setupPID(int targetEncoderDifference, double kp, double ki, double kd) {
        this.targetEncoderDifference = targetEncoderDifference;
        // the max that a motor power can be is 1.0
        pidControl.setMaxCorrection(1.0);
        pidControl.setKp(kp);
        pidControl.setKi(ki);
        pidControl.setKd(kd);
        pidControl.setSetpoint(targetEncoderDifference);
        setupPIDComplete = true;
    }

    /**
     * Run the PIDControl and use the result to calculate the adjusted motor powers.
     * Use this version of the method when you don't have the motor positions and want to let the
     * method retrieve them. Getting the motor encoder values is slow so if you have them already,
     * use the other version of the method.
     */
    public void adjustPowers() {
        motor1Position = motor1.getCurrentPosition();
        motor2Position = motor2.getCurrentPosition();
        adjustPowers(motor1Position, motor2Position);
    }

    /**
     * Run the PIDControl and use the result to calculate the adjusted motor powers.
     * Use this version of the method when you already have the motor encoder values.
     * Getting the motor encoder values is slow so you don't want to get them more than you need to.
     */
    public void adjustPowers(int motor1Position, int motor2Position) {
        // note that the correction produced by the PIDControl is - if motor1Position-motor2Position
        // is positive!
        correction = pidControl.getCorrection(motor1Position - motor2Position);
        if (movementDirection == MovementDirection.INCREASING_ENCODER) {
            // if motor1 is leading then its encoder count is greater than motor2.
            // motor1Position - motor2Position will be positive. Correction will be negative.
            // So reduce its power. Correction is negative so add it!
            motor1PowerAdjusted = motorPowerDesired + correction;
            // if motor2 is lagging then its encoder count is less than motor1
            // So increase its power. Correction is negative so subtract it!
            motor2PowerAdjusted = motorPowerDesired - correction;
        }
        if (movementDirection == MovementDirection.DECREASING_ENCODER) {
            // if motor1 is leading then its encoder count is less than motor2.
            // motor1Position - motor2Position will be negative. Correction will be positive.
            // So decrease its power. Correction is positive so subtract it!
            motor1PowerAdjusted = motorPowerDesired - correction;
            // if motor2 is lagging then its encoder count is greater than motor1.
            // motor1Position - motor2Position will be negative. Correction will be positive.
            // So increase its power. Correction is positive so add it!
            motor2PowerAdjusted = motorPowerDesired + correction;
        }
    }
}
