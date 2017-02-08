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

    public enum DrivingState {
        START_RAMP,
        RAMP_UP,
        CONSTANT_SPEED,
        RAMP_DOWN,
        MOVING_UNTIL_COMPLETE,
        COMPLETE
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
    private double distanceRemaining;


    private boolean hasLoopRunYet = false;

    private Telemetry telemetry;
    private Torcelli torcelli;
    private boolean rampDown = false;
    private DrivingState drivingState;
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

        // for the competition robot
        this.setCmPerRotation(32.05);
        // for the development robot
        //this.cmPerRotation = 31.1;

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
        rampControl = new RampControl(0, 0, 0);

        driveDirection = DriveDirection.FORWARD;

        this.telemetry = telemetry;
        torcelli = new Torcelli(0, 0, 0);
        rampDown = false;
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
    // Autonomous Methods - driving a heading or a straight line
    //*********************************************************************************************

    /**
     * Torcelli's equation can be used to calculate the power for ramping down or up to get a
     * constant acceleration over a certain distance. Looked at another way if you want to change
     * speed over a certain distance using a constant acceleration, you can use this method to setup
     * the change and getCurrentPowerForChangeInPower to update the powers as the distance to the
     * target changes.
     * Vfinal^2 = Vinitial^2 + 2*a*deltaX
     * In this case you specify the distance for the ramp (deltaX), and the final and initial powers
     * and the method will calculate the acceleration needed to achieve it.
     *
     * @param finalPower
     * @param initialPower
     * @param distanceTotarget
     */
    private void setupChangeInPower(double initialPower, double finalPower, double distanceTotarget) {
        torcelli.setupTorcelli(initialPower, finalPower, distanceTotarget);
        rampDown = true;
    }

    /**
     * As the speed changes you constantly have to recalculate the power to the motors given the
     * distance that remains to the target distance.
     *
     * @param distanceRemaining
     * @return power needed to achieve constant acceleration
     */
    private double getCurrentPowerForChangeInPower(double distanceRemaining) {
        return torcelli.getPower(distanceRemaining);
    }

    /**
     * This method is mostly used to determine the circumference of the drive wheels. Give it
     * 3600 degrees to rotate (10 revolutions) and then measure the distance traveled with a
     * measuring tape. Divide by 10 and you have the circumference.
     *
     * @param power           0 to 1, keep it low for an accurate measurement
     * @param degreesToRotate
     * @param finishBehavior  HOLD the motors in place or FLOAT them to allow them to spin
     */
    public void rotateNumberOfDegrees(double power, double degreesToRotate, DcMotor8863.FinishBehavior finishBehavior) {
        rightDriveMotor.rotateNumberOfDegrees(power, degreesToRotate, finishBehavior);
        leftDriveMotor.rotateNumberOfDegrees(power, degreesToRotate, finishBehavior);
    }

    /**
     * Use this method to manually change the power while the drive train is in the middle of a
     * movement.
     *
     * @param power
     */
    public void setDriveTrainPower(double power) {
        rightDriveMotor.setPower(power);
        leftDriveMotor.setPower(power);
    }

    /**
     * Drives a straight line by applying the same power to both motors
     *
     * @param power          the power to move at. This can only be positive.
     * @param distance       the distance to move. If you want to go backwards then make the distance
     *                       negative. The distance to travel is relative to where the robot is located
     *                       now. In other words, 0 is where the robot is at right at the beginning
     *                       of the movement.
     * @param finishBehavior whether to lock the motors and hold position or float the motors and
     *                       let them roll when the movement is done
     */
    public void setupDriveDistance(double power, double distance, DcMotor8863.FinishBehavior finishBehavior) {
        rightDriveMotor.moveByAmount(power, distance, finishBehavior);
        leftDriveMotor.moveByAmount(power, distance, finishBehavior);
        this.distanceToDrive = distance;
        // reset the distance traveled
        this.distanceDriven = 0;
    }

    /**
     * You must call this method in a loop after you call setupDriveDistance() in order to get the
     * movement to work properly.
     *
     * @return COMPLETE if distance has been reached, MOVING if not
     */
    public DriveTrain.Status updateDriveDistance() {
        rightMotorState = rightDriveMotor.update();
        leftMotorState = leftDriveMotor.update();
        // the distance driven has to be relative to the start of the movement
        // THERE IS A BUG HERE. THE DISTANCE IS NOT RELATIVE TO THE START. IT IS CUMULATIVE. NEED TO FIX IT
        distanceDriven = (leftDriveMotor.getPositionInTermsOfAttachmentRelativeToLast() + rightDriveMotor.getPositionInTermsOfAttachmentRelativeToLast()) / 2;
        if (this.isDriveTrainComplete()) {
            return Status.COMPLETE;
        } else {
            return Status.MOVING;
        }
    }

    /**
     * Stop the movement of the robot
     *
     * @return
     */
    public DriveTrain.Status stopDriveDistance() {
        shutdown();
        return Status.COMPLETE;
    }

    /**
     * Drive on a heading using the IMU to give heading feedback. This will only stop when you tell
     * it to stop using stopDriveUsingIMU(). The heading can be relative to the robot's heading at
     * the start, or can be relative to whenever the IMU heading was last set to 0.
     *
     * @param heading     heading in degrees. Counter clockwise is +. Clockwise is -. Range from -180 to
     *                    +180. Note that if you input 180 it will have problems navigating due to the
     *                    sign change at the 180 boundary. If you really need to do that then there will
     *                    have to be some code written to control the range on the IMU.
     * @param maxPower    the max power to apply to the motors. This really corresponds to speed.
     *                    Positive is forwards. Negative is backwards.
     * @param headingType RAW, ABSOLUTE or RELATIVE. See AngleMode for desscription.
     */
    // THIS METHOD HAS A BUG. IT DOES NOT DRIVE BACKWARDS PROPERLY. NEED TO FIX IT
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

            // the power determines which direction the robot will drive. If going backwards the
            // left and right drive motors have to swap and this set the flags to do that
            if (maxPower > 0) {
                //going forwards
                driveDirection = DriveDirection.FORWARD;
            } else {
                // driving backwards
                driveDirection = DriveDirection.REVERSE;
            }
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
    }

    /**
     * You must call this method in a loop after you call setupDriveDistance() in order to get the
     * movement to work properly.
     *
     * @return distance driven
     */
    public double updateDriveUsingIMU() {
        if (imuPresent) {
            double currentHeading = imu.getHeading();
            // I have to reverse the sign since the differential drive method expects a negative
            // joystick input for a left turn (joystick left = negative number, not what you would
            // expect).
            double correction = -pidControl.getCorrection(currentHeading);
//            if (driveTrainPower < 0) {
//                // sign on correction has to flip or feedback is wrong direction
//                correction = correction * -1;
//            }
            differentialDrive(driveTrainPower, correction);
            // THERE IS A BUG HERE. THE DISTANCE BEING REPORTED IS CUMULATIVE NOT RELATIVE TO THE START OF THE MOVEMENT
            distanceDriven = (leftDriveMotor.getPositionInTermsOfAttachmentRelativeToLast() + rightDriveMotor.getPositionInTermsOfAttachmentRelativeToLast()) / 2;
            return distanceDriven;
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
    }

    /**
     * Stop the movement of the robot
     */
    public void stopDriveUsingIMU() {
        shutdown();
    }

    /**
     * Drive a distance on a heading using the IMU for heading feedback and the RUN_TO_POSITION of
     * the motor controller to control distance. This method also has a ramp up of the power to
     * eliminate wheel slip at startup. It can have a ramp down in power to gradually slow the
     * robot down at the end of the drive.
     *
     * @param heading                      drive at this heading
     * @param maxPower                     power to drive at (-1 to 1) Positive is forwards. Negative is backwards.
     * @param distance                     distance to drive
     * @param headingType                  is the heading relative to where the robot is starting or absolute. Absolute
     *                                     means it is relative to the startup of the robot
     * @param valueAtStartTime             power at the start of the ramp
     * @param valueAtFinishTime            power at the end of the ramp. Typically you make this equal to the
     *                                     maxPower.
     * @param timeToReachFinishValueInmSec how long to run the ramp up in power (in milliseconds)
     */
    public void setupDriveDistanceUsingIMU(double heading, double maxPower, double distance,
                                           AdafruitIMU8863.AngleMode headingType, double valueAtStartTime,
                                           double valueAtFinishTime, double timeToReachFinishValueInmSec) {
        //If the IMU is present we proceed. If not we give the user an error
        if (imuPresent) {
            // Setup the ramp control to ramp the power up from 0 to maxPower
            rampControl.setup(valueAtStartTime, valueAtFinishTime, timeToReachFinishValueInmSec);
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
            rampDown = false;
            drivingState = DrivingState.START_RAMP;
            //If IMU isn't present
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
    }

    /**
     * An overload that allows you to setup a ramp down of the power also. See other version of this
     * method for a full description.
     *
     * @param heading                      drive at this heading
     * @param maxPower                     power to drive at (-1 to 1) Positive is forwards. Negative is backwards.
     * @param distance                     distance to drive
     * @param headingType                  is the heading relative to where the robot is starting or absolute. Absolute
     *                                     means it is relative to the startup of the robot
     * @param valueAtStartTime             power at the start of the ramp
     * @param valueAtFinishTime            power at the end of the ramp. Typically you make this equal to the
     *                                     maxPower.
     * @param timeToReachFinishValueInmSec how long to run the ramp up in power (in milliseconds)
     * @param initialPower start the ramp down at this power
     * @param finalPower finish the ramp down at this power
     * @param distanceTotarget ramp down the power over this distance
     */
    public void setupDriveDistanceUsingIMU(double heading, double maxPower, double distance,
                                           AdafruitIMU8863.AngleMode headingType, double valueAtStartTime,
                                           double valueAtFinishTime, double timeToReachFinishValueInmSec,
                                           double initialPower, double finalPower, double distanceTotarget) {
        setupDriveDistanceUsingIMU(heading, maxPower, distance, headingType, valueAtStartTime,
                valueAtFinishTime, timeToReachFinishValueInmSec);
        setupChangeInPower(initialPower, finalPower, distanceTotarget);
    }

    public DrivingState updateDriveDistanceUsingIMUState() {
        //setting up an array so that we can hold onto the left and right motor drive powers
        double[] drivePowers;
        double power = driveTrainPower;

        if (imuPresent) {
            // Figure out where the robot is at now during the drive
            distanceDriven = (leftDriveMotor.getPositionInTermsOfAttachmentRelativeToLast() + rightDriveMotor.getPositionInTermsOfAttachmentRelativeToLast()) / 2;
            distanceRemaining = distanceToDrive - distanceDriven;
            // run the state machine
            switch (drivingState) {
                case START_RAMP:
                    // if ramp up is enabled but not running start it and then start the motors
                    if (rampControl.isEnabled() && !rampControl.isRunning()) {
                        rampControl.start();
                        power = rampControl.getRampValueLinear(driveTrainPower);
                        // run the PID to adjust powers to keep on heading
                        drivePowers = adjustPowerUsingPID(power);
                        //we start the motors moving using the powers calculated above
                        // check for forwards or backwards movement
                        if (distanceToDrive > 0) {
                            // forwards
                            leftDriveMotor.moveByAmount(drivePowers[0], distanceToDrive, DcMotor8863.FinishBehavior.HOLD);
                            rightDriveMotor.moveByAmount(drivePowers[1], distanceToDrive, DcMotor8863.FinishBehavior.HOLD);
                        } else {
                            // if driving backwards the left and right drive motors are effectively swapped
                            leftDriveMotor.moveByAmount(drivePowers[1], distanceToDrive, DcMotor8863.FinishBehavior.HOLD);
                            rightDriveMotor.moveByAmount(drivePowers[0], distanceToDrive, DcMotor8863.FinishBehavior.HOLD);
                        }
                    }
                    drivingState = DrivingState.RAMP_UP;
                    break;
                case RAMP_UP:
                    // if ramp is finished then we are in the constant speed mode next time
                    if (rampControl.isFinished()) {
                        drivingState = DrivingState.CONSTANT_SPEED;
                    }
                    // if the remaining distance to be driven is into the zone where the ramp down
                    // should start, change the state for the next update
                    if (Math.abs(distanceRemaining) < torcelli.getDistanceToChangeVelocityOver() && rampDown) {
                        drivingState = DrivingState.RAMP_DOWN;
                    }
                    // if the movement of the motors is complete then move to the complete state
                    if (isDriveTrainComplete()) {
                        drivingState = DrivingState.COMPLETE;
                    }
                    power = rampControl.getRampValueLinear(driveTrainPower);
                    drivePowers = adjustPowerUsingPID(power);
                    //setting new powers to the motors - is the robot going forwards or backwards?
                    if (distanceToDrive > 0) {
                        // forwards
                        leftDriveMotor.setPower(drivePowers[0]);
                        rightDriveMotor.setPower(drivePowers[1]);
                    } else {
                        // if driving backwards the left and right drive motors are effectively swapped
                        leftDriveMotor.setPower(drivePowers[1]);
                        rightDriveMotor.setPower(drivePowers[0]);
                    }
                    break;
                case CONSTANT_SPEED:
                    // if the remaining distance to be driven is into the zone where the ramp down
                    // should start, change the state for the next update
                    if (Math.abs(distanceRemaining) < torcelli.getDistanceToChangeVelocityOver() && rampDown) {
                        drivingState = DrivingState.RAMP_DOWN;
                    }
                    // if the movement of the motors is complete then move to the complete state
                    if (isDriveTrainComplete()) {
                        drivingState = DrivingState.COMPLETE;
                    }
                    drivePowers = adjustPowerUsingPID(driveTrainPower);
                    //setting new powers to the motors - is the robot going forwards or backwards?
                    if (distanceToDrive > 0) {
                        // forwards
                        leftDriveMotor.setPower(drivePowers[0]);
                        rightDriveMotor.setPower(drivePowers[1]);
                    } else {
                        // if driving backwards the left and right drive motors are effectively swapped
                        leftDriveMotor.setPower(drivePowers[1]);
                        rightDriveMotor.setPower(drivePowers[0]);
                    }
                    break;
                case RAMP_DOWN:
                    // if the movement of the motors is complete then move to the complete state
                    if (isDriveTrainComplete()) {
                        drivingState = DrivingState.COMPLETE;
                    }
                    power = torcelli.getPower(distanceRemaining);
                    if (torcelli.isComplete()) {
                        drivingState = DrivingState.MOVING_UNTIL_COMPLETE;
                    }
                    drivePowers = adjustPowerUsingPID(power);
                    //setting new powers to the motors - is the robot going forwards or backwards?
                    if (distanceToDrive > 0) {
                        // forwards
                        leftDriveMotor.setPower(drivePowers[0]);
                        rightDriveMotor.setPower(drivePowers[1]);
                    } else {
                        // if driving backwards the left and right drive motors are effectively swapped
                        leftDriveMotor.setPower(drivePowers[1]);
                        rightDriveMotor.setPower(drivePowers[0]);
                    }
                    break;
                case MOVING_UNTIL_COMPLETE:
                    // if the movement of the motors is complete then move to the complete state
                    if (isDriveTrainComplete()) {
                        drivingState = DrivingState.COMPLETE;
                    }
                    power = torcelli.getFinalPower();
                    drivePowers = adjustPowerUsingPID(power);
                    //setting new powers to the motors - is the robot going forwards or backwards?
                    if (distanceToDrive > 0) {
                        // forwards
                        leftDriveMotor.setPower(drivePowers[0]);
                        rightDriveMotor.setPower(drivePowers[1]);
                    } else {
                        // if driving backwards the left and right drive motors are effectively swapped
                        leftDriveMotor.setPower(drivePowers[1]);
                        rightDriveMotor.setPower(drivePowers[0]);
                    }
                    break;
                case COMPLETE:
                    stopDriveDistanceUsingIMU();
                    break;
            }
            // If IMU isn't present
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
        return drivingState;
    }

    /**
     * You must run this update in a loop in order for the drive distance to work.
     * @return true if movement is complete
     */
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
                //setting new powers to the motors - is the robot going forwards or backwards?
                if (distanceToDrive > 0) {
                    // forwards
                    leftDriveMotor.setPower(drivePowers[0]);
                    rightDriveMotor.setPower(drivePowers[1]);
                } else {
                    // if driving backwards the left and right drive motors are effectively swapped
                    leftDriveMotor.setPower(drivePowers[1]);
                    rightDriveMotor.setPower(drivePowers[0]);
                }

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

    /**
     * Runs the ramp up for the power and then adjusts the power to each motor to maintain the
     * heading using a PID.
     *
     * @return left and right motor powers
     */
    private double[] calculatePowerUsingRampAndPID() {
        //this is what we use to calculate the ramp power
        //after the ramp finishes running we get our drive train power applied back to the motors again
        double rampPower = rampControl.getRampValueLinear(driveTrainPower);
        double currentHeading = imu.getHeading();
        //this is what we use to calculate the correction to the ramp power to adjust the steering
        double correction = -pidControl.getCorrection(currentHeading);
        telemetry.addData("correction = ", "%3.1f", correction);
        double leftDrivePower = rampPower + correction;
        double rightDrivePower = rampPower - correction;
        double drivePowers[] = new double[2];
        drivePowers[0] = leftDrivePower;
        drivePowers[1] = rightDrivePower;
        return drivePowers;
    }

    private double[] adjustPowerUsingPID(double power) {
        double currentHeading = imu.getHeading();
        //this is what we use to calculate the correction to the power to adjust the steering
        double correction = -pidControl.getCorrection(currentHeading);
        telemetry.addData("correction = ", "%3.1f", correction);
        double leftDrivePower = power + correction;
        double rightDrivePower = power - correction;
        double drivePowers[] = new double[2];
        drivePowers[0] = leftDrivePower;
        drivePowers[1] = rightDrivePower;
        return drivePowers;
    }

    /**
     * Stop driving
     */
    public void stopDriveDistanceUsingIMU() {
        shutdown();
    }

    //*********************************************************************************************
    // Autonomous Methods - turns
    //*********************************************************************************************

    public void setupTurn(double turnAngle, double maxPower, AdafruitIMU8863.AngleMode angleMode) {

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

            imu.setAngleMode(angleMode);
            if (angleMode == AdafruitIMU8863.AngleMode.RELATIVE) {
                imu.resetAngleReferences();
            }
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
    }

    public boolean updateTurn() {

        if (imuPresent) {
            double currentHeading = imu.getHeading();
            double correction = -pidControl.getCorrection(currentHeading);
            differentialDrive(0, correction);
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


    //*********************************************************************************************
    // Autonomous Methods - status
    //*********************************************************************************************

    private boolean isDriveTrainComplete() {
        if (rightDriveMotor.isMotorStateComplete() && leftDriveMotor.isMotorStateComplete()) {
            return true;
        } else {
            return false;
        }

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
     * @param throttle  Master speed applied to both motors. Positive is forwards. Negative is
     *                  backwards.
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
        if (driveDirection == DriveDirection.FORWARD) {
            leftDriveMotor.setPower(throttle + direction);
            rightDriveMotor.setPower(throttle - direction);
        } else {
            // driveDirection == DriveDirection.REVERSE
            // this means swap the left and right motors
            rightDriveMotor.setPower(throttle + direction);
            leftDriveMotor.setPower(throttle - direction);
        }

        distanceDriven = (leftDriveMotor.getPositionInTermsOfAttachmentRelativeToLast() + rightDriveMotor.getPositionInTermsOfAttachmentRelativeToLast()) / 2;
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
        if (driveDirection == DriveDirection.FORWARD) {
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
