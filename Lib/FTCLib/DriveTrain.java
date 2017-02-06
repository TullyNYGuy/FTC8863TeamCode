package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.ResQLib.RobotConfigMapping;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

public class DriveTrain {

    public enum Status {
        MOVING, COMPLETE
    }

    private enum DriveDirection {
        FORWARD, REVERSE
    }
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private double cmPerRotation = 0;
    private DriveDirection driveDirection = DriveDirection.FORWARD;

    private boolean driveLocked = false;

    private DcMotor8863 rightDriveMotor;
    private DcMotor8863 leftDriveMotor;

    private DcMotor8863.MotorState rightMotorState;
    private DcMotor8863.MotorState leftMotorState;

    public PIDControl pidControl;
    private RampControl rampControl;

    private boolean imuPresent = true;
    public AdafruitIMU8863 imu;

    private double driveTrainPower;
    private double distanceToDrive;
    private double distanceDriven;


    private boolean hasLoopRunYet = false;

    private Telemetry telemetry;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public double getCmPerRotation() {
        return cmPerRotation;
    }

    public void setCmPerRotation(double cmPerRotation) {
        this.cmPerRotation = cmPerRotation;
        leftDriveMotor.setMovementPerRev(cmPerRotation);
        rightDriveMotor.setMovementPerRev(cmPerRotation);
    }

    public double getdistanceToDrive() {
        return distanceToDrive;
    }

    public double getDistanceDriven() {
        return distanceDriven;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    /**
     * Construct a drive train. Create 2 motor objects and set them up.
     * The reason this is private is to force the user to call either DriveTrainTeleop or
     * DriveTrainAutonomous. Those methods then call this one. Those methods will optimize the
     *
     * @param hardwareMap
     */
    private DriveTrain(HardwareMap hardwareMap, boolean imuPresent, Telemetry telemetry) {
        leftDriveMotor = new DcMotor8863(RobotConfigMappingForGenericTest.getleftMotorName(), hardwareMap);
        rightDriveMotor = new DcMotor8863(RobotConfigMappingForGenericTest.getrightMotorName(), hardwareMap);

        rightDriveMotor.setDirection(DcMotor.Direction.REVERSE);
        rightDriveMotor.setMaxMotorPower(1);
        rightDriveMotor.setMinMotorPower(-1);
        rightDriveMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        rightDriveMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        rightDriveMotor.setTargetEncoderTolerance(10);
        rightDriveMotor.setMovementPerRev(cmPerRotation);
        rightDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);

        leftDriveMotor.setDirection(DcMotor.Direction.FORWARD);
        leftDriveMotor.setMaxMotorPower(1);
        leftDriveMotor.setMinMotorPower(-1);
        leftDriveMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        leftDriveMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        leftDriveMotor.setTargetEncoderTolerance(10);
        leftDriveMotor.setMovementPerRev(cmPerRotation);
        leftDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);

        pidControl = new PIDControl();
        pidControl.setKp(0.01);

        this.imuPresent = imuPresent;

        if (imuPresent) {
            imu = new AdafruitIMU8863(hardwareMap);
        }
        rampControl = new RampControl(0,0,0);

        driveDirection = DriveDirection.FORWARD;

        this.telemetry = telemetry;

        // for the competition robot
        this.setCmPerRotation(32.05);
        // for the developement robot
        //this.cmPerRotation = 31.1;
    }

    /**
     * This method is a factory method. It returns a driveTrain object that is setup to float the
     * motors after a movement is completed. The motors are set to operate with a constant power.
     *
     * @param hardwareMap
     * @return Instance of a driveTrain (a driveTrain oject) optimized for TeleOp
     */
    public static DriveTrain DriveTrainTeleOp(HardwareMap hardwareMap, Telemetry telemetry) {
        DriveTrain driveTrain = new DriveTrain(hardwareMap, true, telemetry);
        driveTrain.teleopInit();
        return driveTrain;
    }

    /**
     * Set how the motors behave when power is set to 0 and set the mode of the motors.
     */
    public void teleopInit() {
        // Set the motors to hold after the power gets set to 0
        // This way the robot will stop when the joystick go to 0 and not continue to coast
        rightDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        leftDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);

        // Set the motors to run at constant power - there is no PID control over them
        // This needs to be set here because the teleop methods only adjust the power to the motors.
        // They don't set the mode which is why it is done here. Setting power = 0 makes sure the
        // motors don't actually move.
        rightDriveMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftDriveMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDriveMotor.setPower(0);
        leftDriveMotor.setPower(0);
    }

    /**
     * This method is a factory method. It returns a driveTrain object that is setup to float the
     * motors after a movement is completed.
     *
     * @param hardwareMap
     * @return Instance of a driveTrain (a driveTrain oject) optimized for Autonomous
     */
    public static DriveTrain DriveTrainAutonomous(HardwareMap hardwareMap, Telemetry telemetry) {
        DriveTrain driveTrain = new DriveTrain(hardwareMap, true, telemetry);

        // Set the motors to hold after the power gets set to 0
        driveTrain.rightDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        driveTrain.leftDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);

        // set the mode of the motors to run with encoder feedback, controller the speed of the motors
        driveTrain.rightDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveTrain.leftDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        return driveTrain;
    }

    public static DriveTrain DriveTrainAutonomousNoImu(HardwareMap hardwareMap, Telemetry telemetry) {
        DriveTrain driveTrain = new DriveTrain(hardwareMap, false, telemetry);

        // Set the motors to hold after the power gets set to 0
        driveTrain.rightDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        driveTrain.leftDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);

        // set the mode of the motors to run with encoder feedback, controller the speed of the motors
        driveTrain.rightDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveTrain.leftDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        return driveTrain;
    }

    //*********************************************************************************************
    // Autonomous Methods
    //*********************************************************************************************

    public void setupDriveDistance(double power, double distance, DcMotor8863.FinishBehavior finishBehavior) {
        rightDriveMotor.moveByAmount(power, distance, finishBehavior);
        leftDriveMotor.moveByAmount(power, distance, finishBehavior);
        this.distanceToDrive = distance;
    }

    public DriveTrain.Status updateDriveDistance() {
        rightMotorState = rightDriveMotor.update();
        leftMotorState = leftDriveMotor.update();
        distanceDriven = (leftDriveMotor.getPositionInTermsOfAttachment() + rightDriveMotor.getPositionInTermsOfAttachment()) / 2;
        if (this.isMotorStateComplete()) {
            return Status.COMPLETE;
        } else {
            return Status.MOVING;
        }
    }

    public DriveTrain.Status stopDriveDistance(){
        shutdown();
        return Status.COMPLETE;
    }

    public void rotateNumberOfDegrees(double power, double degreesToRotate, DcMotor8863.FinishBehavior finishBehavior) {
        rightDriveMotor.rotateNumberOfDegrees(power, degreesToRotate, finishBehavior);
        leftDriveMotor.rotateNumberOfDegrees(power, degreesToRotate, finishBehavior);
    }


    private boolean isMotorStateComplete() {
        if (rightDriveMotor.isMotorStateComplete() && leftDriveMotor.isMotorStateComplete()) {
            return true;
        } else {
            return false;
        }

    }

    public void setupTurn(double turnAngle, double maxPower) {

        if (imuPresent) {
            // set the mode for the motors during the turn. Without this they may not move.
            rightDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            pidControl.setSetpoint(turnAngle);
            pidControl.setMaxCorrection(maxPower);
            pidControl.setThreshold(0.5);
            //pidControl.setKp(0.025);
            //pidControl.setKi(0.0000000015);
            pidControl.setKp(0.0125);
            pidControl.setKi(0.00000000025);
            pidControl.reset();

            imu.setAngleMode(AdafruitIMU8863.AngleMode.RELATIVE);
            imu.resetAngleReferences();
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
    }

    public boolean updateTurn() {

        if (imuPresent) {
            double currentHeading = imu.getHeading();
            double correction = -pidControl.getCorrection(currentHeading);
            differentialDrive(0, correction);//correction);
            //return correction;
            return pidControl.isFinished();
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
    }

    public void stopTurn() {
        shutdown();
    }

    public void setupDriveUsingIMU(double heading, double maxPower, AdafruitIMU8863.AngleMode headingType) {

        if (imuPresent) {
            // set the mode for the motors during the turn. Without this they may not move.
            rightDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            pidControl.setSetpoint(heading);
            pidControl.setMaxCorrection(maxPower);
            pidControl.setThreshold(2);
            pidControl.setKp(0.03);
            driveTrainPower = maxPower;

            switch (headingType) {
                case RELATIVE:
                    imu.setAngleMode(AdafruitIMU8863.AngleMode.RELATIVE);
                    imu.resetAngleReferences();
                    break;
                case ABSOLUTE:
                    imu.setAngleMode(AdafruitIMU8863.AngleMode.ABSOLUTE);
                    break;
                case RAW:
                    imu.setAngleMode(AdafruitIMU8863.AngleMode.RAW);
                    break;
            }
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
    }

    public double updateDriveUsingIMU() {

        if (imuPresent) {
            double currentHeading = imu.getHeading();
            // I have to reverse the sign since the differential drive method expects a negative
            // joystick input for a left turn (joystick left = negative number, not what you would
            // expect).
            double correction = -pidControl.getCorrection(currentHeading);
            if (driveTrainPower < 0) {
                // sign on correction has to flip or feedback is wrong direction
                correction = correction * -1;
            }
            differentialDrive(driveTrainPower, correction);//correction);
            distanceDriven = (leftDriveMotor.getPositionInTermsOfAttachment() + rightDriveMotor.getPositionInTermsOfAttachment()) / 2;
            return distanceDriven;
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
    }

    public void stopDriveUsingIMU() {
        shutdown();
    }

    //Drive distance using IMU
    public void setupDriveDistanceUsingIMU(double heading, double maxPower, double distance,
                                           AdafruitIMU8863.AngleMode headingType, double valueAtStartTime,
                                           double valueAtFinishTime, double timeToReachFinishValueInmSec){
        //If the IMU is present we proceed. If not we give the user an error
        if (imuPresent) {
            // Setup the ramp control to ramp the power up from 0 to maxPower
            rampControl.setup(valueAtStartTime,valueAtFinishTime,timeToReachFinishValueInmSec);
            // Enable the ramp control so that it will start when the motors start moving
            rampControl.enable();
            // set the mode for the motors during the turn. Without this they may not move.
            rightDriveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            leftDriveMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            //setting up the PID
            pidControl.setSetpoint(heading);
            pidControl.setMaxCorrection(maxPower);
            pidControl.setThreshold(2);
            pidControl.setKp(0.015);
            //Saving power for later use
            driveTrainPower = maxPower;
            //Setting the flag to say the loop hasn't run yet
            hasLoopRunYet = false;
            //MATT never set the distance so it was 0 when we started the update
            this.distanceToDrive = distance;
            //Setting up IMU
            switch (headingType) {
                case RELATIVE:
                    imu.setAngleMode(AdafruitIMU8863.AngleMode.RELATIVE);
                    imu.resetAngleReferences();
                    break;
                case ABSOLUTE:
                    imu.setAngleMode(AdafruitIMU8863.AngleMode.ABSOLUTE);
                    break;
                case RAW:
                    imu.setAngleMode(AdafruitIMU8863.AngleMode.RAW);
                    break;
            }
            //If IMU isn't present
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }

    }
    public boolean updateDriveDistanceUsingIMU() {
        //setting up an array so that we can hold onto the left and right motor drive powers
        double[] drivePowers;
        if (imuPresent) {
            //If the IMU is present we proceed if not we give the user an error
            if(!hasLoopRunYet){
                //If we are running the loop for the first time
                //MATT the ramp start was after the calculatePowerUsingRampAndPID so initial power was 1
                rampControl.start();
                drivePowers = calculatePowerUsingRampAndPID();
                //we start the motors moving using the powers calculated above
                leftDriveMotor.moveByAmount(drivePowers[0], distanceToDrive, DcMotor8863.FinishBehavior.HOLD);
                rightDriveMotor.moveByAmount(drivePowers[1], distanceToDrive, DcMotor8863.FinishBehavior.HOLD);
                hasLoopRunYet = true;
            } else {
                //If we are running the loop or a second or third or more time
                drivePowers = calculatePowerUsingRampAndPID();
                //setting new powers to the motors
                leftDriveMotor.setPower(drivePowers[0]);
                rightDriveMotor.setPower(drivePowers[1]);
                // if the ramp has finished then setup a ramp for the de-acceleration
                // then enable it
                // start the ramp down of power when the distance has reached 80% of the target
            }
            distanceDriven = (leftDriveMotor.getPositionInTermsOfAttachment() + rightDriveMotor.getPositionInTermsOfAttachment()) / 2;
            //
            telemetry.addData("Left drive power = ", "%2.2f", drivePowers[0]);
            telemetry.addData("Right drive power = ", "%2.2f", drivePowers[1]);
            telemetry.addData("Heading = ", "%3.1f", imu.getHeading());
            telemetry.addData("distance = ", "%3.1f", distanceDriven);
            telemetry.update();
            // update the motor state machines
            leftDriveMotor.update();
            rightDriveMotor.update();
            // check to see if our desired distance has been met
            if (leftDriveMotor.isMotorStateComplete() && rightDriveMotor.isMotorStateComplete()){
                return true;
            } else {
                return false;
            }
            // If IMU isn't present
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
    }
    private double[] calculatePowerUsingRampAndPID(){
        //this is what we use to calculate the ramp power
        //after the ramp finishes running we get our drive train power applied back to the motors again
        double rampPower = rampControl.getRampValueLinear(driveTrainPower);
        double currentHeading = imu.getHeading();
        //this is what we use to calculate the correction to the ramp power to adjust the steering
        double correction = -pidControl.getCorrection(currentHeading);
        double leftDrivePower = rampPower + correction;
        double rightDrivePower = rampPower - correction;
        double drivePowers[] = new double[2];
        drivePowers[0] = leftDrivePower;
        drivePowers[1] = rightDrivePower;
        return drivePowers;
    }

    public void stopDriveDistanceUsingIMU() {
        shutdown();
    }

    //*********************************************************************************************
    //          Teleop methods
    //*********************************************************************************************

    // Joystick info for reference.
    // Normally:
    // y ranges from -1 to 1, where -1 is full up and 1 is full down
    // x ranges from -1 to 1, where -1 is full left and 1 is full right
    // The Joystick object allow you to invert the sign on the joystick
    // I like to think of the Y up as positive so:
    // All code assumes that joystick y is positive when the joystick is up.
    // Use the JoyStick object INVERT_SIGN to accomplish that.


    /**
     * The differentialDrive is meant to use one joystick to control the drive train.
     * Moving the joystick forward and backward controls speed (throttle).
     * Moving the joystick left or right controls direction.
     * <p>
     * Differential drive has a master speed that gets applied to both motors. That speed is the
     * same. Then the speed to the left and right is adjusted up and down, opposite of each other
     * to turn the robot.
     *
     * @param throttle  Master speed applied to both motors.
     * @param direction Adjustment applied to the master speed. Add to left. Subtract from right.
     */
    public void differentialDrive(double throttle, double direction) {
        // When going forwards:
        // To steer the robot left, the left motor needs to reduce power and the right needs to increase.
        // To steer the robot right, the left motor needs to increase power and the left needs to reduce.
        // When going backwards. the vewpoint of the driver switches; left becomes right and vice
        // versa:
        // To steer the robot left, the left motor needs to reduce power and the right needs to increase.
        // To steer the robot right, the left motor needs to increase power and the left needs to reduce.
        // Since left on the joystick is negative, we need to add the direction for the left motor and
        // subtract from the right motor
        leftDriveMotor.setPower(throttle + direction);
        rightDriveMotor.setPower(throttle - direction);
//        if (throttle >= 0) {
//            //going forwards
//            leftDriveMotor.setPower(throttle + direction);
//            rightDriveMotor.setPower(throttle - direction);
//        } else {
//            // throttle is negative so we are driving backwards
//            leftDriveMotor.setPower(throttle + direction);
//            rightDriveMotor.setPower(throttle - direction);
//        }

    }

    /**
     * The tank drive applies power values to the left and right motors separately.
     * The tank drive uses the left joystick to control the left drive motor and the right joystick
     * to control the right drive motor.
     *
     * @param leftValue  Power to apply to the left motor.
     * @param rightValue Power to apply tot the right motor.
     */
    public void tankDrive(double leftValue, double rightValue) {
        // if the drive is currently reverse, the left and right motors have to be swapped to match
        // the drivers joysticks
        if (driveDirection == DriveDirection.REVERSE) {
            leftDriveMotor.setPower(rightValue);
            rightDriveMotor.setPower(leftValue);
        } else {
            // the drive is forward - ie normal
            leftDriveMotor.setPower(leftValue);
            rightDriveMotor.setPower(rightValue);
        }
    }

    public void toggleDriveDirection() {
        // if the direction is currently forward switch it to reverse
        if(driveDirection == DriveDirection.FORWARD){
            leftDriveMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            rightDriveMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            driveDirection = DriveDirection.REVERSE;
        } else {
            // if the drive is currently reverse, switch it to forward
            leftDriveMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            rightDriveMotor.setDirection(DcMotorSimple.Direction.REVERSE);
            driveDirection = DriveDirection.FORWARD;
        }
    }

    /**
     * Lock the motors so they resist turning. Might be useful for holding position on an incline.
     */
    public void freezeDrive() {
        leftDriveMotor.setMotorToHold();
        rightDriveMotor.setMotorToHold();
        driveLocked = true;
    }

    // BUG after this method executes teleop via joysticks is very hesitant and jumpy
    public void unFreezeDrive() {
        if (driveLocked) {
            leftDriveMotor.setMotorToFloat();
            rightDriveMotor.setMotorToFloat();
            teleopInit();
        }
    }


    //*********************************************************************************************
    //          Other methods
    //*********************************************************************************************

    public double getRightPower() {
        return rightDriveMotor.getCurrentPower();
    }

    public double getLeftPower() {
        return leftDriveMotor.getCurrentPower();
    }

    public void shutdown() {
        rightDriveMotor.shutDown();
        leftDriveMotor.shutDown();
    }
}
