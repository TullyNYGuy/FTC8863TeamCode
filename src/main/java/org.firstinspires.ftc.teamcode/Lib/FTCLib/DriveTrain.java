package org.firstinspires.ftc.teamcode.Lib.FTCLib;

import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DriveTrain {

    public enum Status {
        MOVING, COMPLETE
    }

    public enum DriveDirection {
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

    double cmPerRotationCompetitionRobot = 31.9;

    // debug counters
    int startRampCounter = 0;
    int rampUpCounter = 0;
    int constantSpeedCounter = 0;
    int rampDownCounter = 0;
    int movingUntilCompleteCounter = 0;
    int completeCounter = 0;

    double distanceAtRampUp = 0;
    double distanceAtConstantSpeed = 0;
    double distanceAtRampDown = 0;
    double distanceAtMovingUntilComplete = 0;
    double distanceAtComplete = 0;

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
    private AngleAdjustedIMU angleAdjustedIMU;

    private double driveTrainPower;
    private double distanceToDrive;
    private double distanceDriven;
    private double totalDistanceDriven;
    private double distanceRemaining;
    private double lastDistanceDriven;
    private double initialDistanceDriven;


    private boolean hasLoopRunYet = false;

    private Telemetry telemetry;
    private Torcelli torcelli;
    private boolean rampDown = false;
    private double deltaX;
    private DrivingState drivingState;
    private boolean debug = false;
    private double rampDownStartOffset = 0;

    private DataLogging logFile = null;

    private boolean logTurns = false;
    private boolean logDrive = false;

    private double rightDriveMotorSpeed = 0;
    private double leftDriveMotorSpeed = 0;

    private DcMotor8863.FinishBehavior finishBehavior;

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

    public DriveDirection getDriveDirection() {
        return driveDirection;
    }

    public double getTotalDistanceDriven() {
        return totalDistanceDriven;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void enableLogTurns() {
        logTurns = true;
    }

    public void disableLogTurns() {
        logTurns = false;
    }

    public void enableLogDrive() {
        logDrive = true;
    }

    public void disableLogDrive() {
        logDrive = false;
    }

    public void setLogFile(DataLogging logFile) {
        this.logFile = logFile;
    }

    public DcMotor8863.FinishBehavior getFinishBehavior() {
        return finishBehavior;
    }

    public void setFinishBehavior(DcMotor8863.FinishBehavior finishBehavior) {
        this.finishBehavior = finishBehavior;
        leftDriveMotor.setFinishBehavior(finishBehavior);
        rightDriveMotor.setFinishBehavior(finishBehavior);
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
        leftDriveMotor = new DcMotor8863("leftMotor", hardwareMap);
        rightDriveMotor = new DcMotor8863("rightMotor", hardwareMap);

        // for the competition robot
        this.setCmPerRotation(cmPerRotationCompetitionRobot);
        // for the development robot
        //this.cmPerRotation = 31.1;

        rightDriveMotor.setDirection(DcMotor.Direction.REVERSE);
        rightDriveMotor.setMaxMotorPower(1);
        rightDriveMotor.setMinMotorPower(-1);
        rightDriveMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        rightDriveMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        rightDriveMotor.setTargetEncoderTolerance(10);
        rightDriveMotor.setMovementPerRev(cmPerRotation);
        rightDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);

        leftDriveMotor.setDirection(DcMotor.Direction.FORWARD);
        leftDriveMotor.setMaxMotorPower(1);
        leftDriveMotor.setMinMotorPower(-1);
        leftDriveMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        leftDriveMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        leftDriveMotor.setTargetEncoderTolerance(10);
        leftDriveMotor.setMovementPerRev(cmPerRotation);
        leftDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);

        pidControl = new PIDControl();
        pidControl.setKp(0.01);

        this.imuPresent = imuPresent;

        if (imuPresent) {
            imu = new AdafruitIMU8863(hardwareMap);
            telemetry.addData("IMU Initialized", "!");
            angleAdjustedIMU = new AngleAdjustedIMU(imu);
        }
        rampControl = new RampControl(0, 0, 0);

        driveDirection = DriveDirection.FORWARD;

        this.telemetry = telemetry;
        torcelli = new Torcelli(0, 0, 0, telemetry);
        rampDown = false;
        debug = false;
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

    public static DriveTrain DriveTrainTeleOpNoIMU(HardwareMap hardwareMap, Telemetry telemetry) {
        DriveTrain driveTrain = new DriveTrain(hardwareMap, false, telemetry);
        driveTrain.teleopInit();
        return driveTrain;
    }

    /**
     * This method is not part of the DriveTrainTeleOp because it can be called separately to
     * reinitialize teleop after the drive train has been created.
     */
    public void teleopInit() {
        // Set the motors to run at constant power - there is no PID control over them
        // This needs to be set here because the teleop methods only adjust the power to the motors.
        // They don't set the mode which is why it is done here.
        // Note there is a bug in the SDK. If you set the mode on a motor too soon after the motor
        // is created in the hardware map, the mode set command does not get run. So this is the
        // reason for the delay below.
        // Based on testing, 300mSec is not enough delay. 500mSec is.
        delay(500);
        rightDriveMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftDriveMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Set the motors to hold after the power gets set to 0
        // This way the robot will stop when the joystick go to 0 and not continue to coast
        rightDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        leftDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);

        // Setting power = 0 makes sure the motors don't actually move.
        rightDriveMotor.setPower(0);
        leftDriveMotor.setPower(0);

        telemetry.addData("Drive Train Initialized", "!");
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
        telemetry.addData("Drive Train Initialized", "!");
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
        telemetry.addData("Drive Train Initialized", "!");
        return driveTrain;
    }

    //*********************************************************************************************
    // Helper Methods
    //********************************************************************************************

    /**
     * Implements a delay
     *
     * @param mSec delay in milli Seconds
     */
    private void delay(int mSec) {
        try {
            Thread.sleep((int) (mSec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //*********************************************************************************************
    // General methods
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
     * When you are trying to change the speed in a linear way (constant acceleration), you have to
     * re-calculate the power to the motors given the
     * distance past the point that the change in velocity started.
     *
     * @param distancePastStartOfVelocityChange the location of the robot using 0 for where the
     *                                          start of the velocity change occurred
     * @return power needed to achieve constant acceleration
     */
    private double getCurrentPowerForChangeInPower(double distancePastStartOfVelocityChange) {
        return torcelli.getPower(distancePastStartOfVelocityChange);
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
     * Set the speed for teh drive motor. THIS DOES NOT APPLY THE SPEED TO THE MOTOR SO THE MOTOR
     * WILL NOT START TURNING. Use applyPowersToMotors() to actually apply it.
     *
     * @param speed
     */
    public void setRightDriveMotorSpeed(double speed) {
        this.rightDriveMotorSpeed = speed;
    }

    /**
     * Set the speed for teh drive motor. THIS DOES NOT APPLY THE SPEED TO THE MOTOR SO THE MOTOR
     * WILL NOT START TURNING. Use applyPowersToMotors() to actually apply it.
     *
     * @param speed
     */
    public void setLeftDriveMotorSpeed(double speed) {
        this.leftDriveMotorSpeed = speed;
    }

    /**
     * Applies the speeds that have been previously set to the drive motors. Speeds were set with
     * setRightDriveMotorSpeed(0 and setLeftDriveMotorSpeed()
     */
    public void applyPowersToMotors() {
        double[] speeds = new double[]{leftDriveMotorSpeed, rightDriveMotorSpeed};
        applyPowersToMotors(speeds);
    }

    /**
     * Apply a power to the drive motors, using the direction of travel to determine whether to
     * swap the left and right motors. If the direction is backwards, then the left and right
     * get swapped.
     *
     * @param drivePowers - an array with left, right powers
     */
    private void applyPowersToMotors(double[] drivePowers) {
        //setting new powers to the motors - is the robot going forwards or backwards?
        if (driveDirection == DriveDirection.FORWARD) {
            // forwards
            leftDriveMotor.setPower(drivePowers[0]);
            rightDriveMotor.setPower(drivePowers[1]);
        } else {
            // if driving backwards the left and right drive motors are effectively swapped
            leftDriveMotor.setPower(drivePowers[1]);
            rightDriveMotor.setPower(drivePowers[0]);
        }
    }

    public void setDriveMotorMode(DcMotor.RunMode mode) {
        leftDriveMotor.setMode(mode);
        rightDriveMotor.setMode(mode);
    }

    public double getRightPower() {
        return rightDriveMotor.getCurrentPower();
    }

    public double getLeftPower() {
        return leftDriveMotor.getCurrentPower();
    }

    public void getEncoderCounts() {
        telemetry.addData("left encoder count = ", "%d", leftDriveMotor.getCurrentPosition());
        telemetry.addData("right encoder count = ", "%d", rightDriveMotor.getCurrentPosition());
    }

    public void shutdown() {
        rightDriveMotor.shutDown();
        leftDriveMotor.shutDown();
    }

    private void zeroDistanceDriven() {
        distanceDriven = 0;
    }

    /**
     * Calculate the average distance the drivetrain has moved since the motors were commanded to move.
     *
     * @return
     */
    private double calculateDistanceDriven() {
        return (leftDriveMotor.getPositionInTermsOfAttachmentRelativeToLast() + rightDriveMotor.getPositionInTermsOfAttachmentRelativeToLast()) / 2;
    }

    /**
     * Update the distance driven. This method is typically called from within a loop while a movement
     * is occurring.
     */
    public double updateDistanceDriven() {
        distanceDriven = calculateDistanceDriven();
        return distanceDriven;
    }

    /**
     * Sometimes you may want to know the change in distance from a certain point in time. You need to
     * establish the point by establishing the distance at that point in time.
     */
    public void setDistanceDrivenReference() {
        lastDistanceDriven = calculateDistanceDriven();
    }

    /**
     * Get the distance driven since the last check on the distance driven. This call can be used
     * to get the change in distance driven. Typically you would use it over the course of the movement
     * to follow the change in distance.
     *
     * @return
     */
    public double getDistanceDrivenSinceLast() {
        distanceDriven = calculateDistanceDriven();
        double distanceDrivenSinceLast = distanceDriven - lastDistanceDriven;
        lastDistanceDriven = distanceDriven;
        return distanceDrivenSinceLast;
    }

    /**
     * Adds the distance driven in a move to the total distance driven
     */
    private void updateTotalDistanceDriven() {
        totalDistanceDriven = totalDistanceDriven + distanceDriven;
    }

    //*********************************************************************************************
    // Autonomous Methods - driving a straight line
    //*********************************************************************************************

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
        zeroDistanceDriven();
        if (logDrive && logFile != null) {
            logFile.logData("Setup for drive straight = " + distance + " at power = " + power);
            logFile.logData("Starting heading = " + imu.getHeading());
        }
    }

    /**
     * Method to log the current heading and position of the robot before starting to drive.
     */
    public void startDriveDistance() {
        if (logFile != null && logDrive) {
            logFile.logData("DRIVE_STRAIGHT_USING_IMU INITIAL_HEADING_DISTANCE", angleAdjustedIMU.getHeading(), updateDistanceDriven());
        }
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
        double currentHeading = angleAdjustedIMU.getHeading();
        distanceDriven = calculateDistanceDriven();
        if (logFile != null && logDrive) {
            logFile.logData("DRIVE_STRAIGHT_USING_IMU HEADING_DISTANCE", currentHeading, getDistanceDriven());
        }
        if (this.isDriveTrainComplete()) {
            if (logDrive && logFile != null) {
                logFile.logData("DRIVE_STRAIGHT_USING_IMU FINAL_HEADING_DISTANCE", currentHeading, getDistanceDriven());
                logFile.blankLine();

                logFile.logData("Drove distance = " + distanceDriven);
                logFile.logData("Finish heading = " + imu.getHeading());
            }
            updateTotalDistanceDriven();
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

    //*********************************************************************************************
    // Autonomous Methods - driving a heading
    //*********************************************************************************************

    /**
     * Drive on a heading using the IMU to give heading feedback. This will only stop when you tell
     * it to stop using stopDriveUsingIMU(). The heading can be relative to the robot's heading at
     * the start, or can be relative to whenever the IMU heading was last set to 0. Once the robot
     * has driven more than the distance specified, the updateDriveUsingIMU() will return true. It
     * does not actually shut off the motors. The robot just keeps on driving until you tell it to
     * do something else. That way you can chain together other movements after this one and the
     * robot just continues to run the movements without stopping.
     * <p>
     * This is the setup for the drive. You call this once. Then after this you call
     * updateDriveUsingIMU() in a loop so that it can update the PID and check on the distance
     * traveled.
     *
     * @param heading        heading in degrees. Counter clockwise is +. Clockwise is -. Range from -180 to
     *                       +180. Note that if you input 180 it will have problems navigating due to the
     *                       sign change at the 180 boundary. If you really need to do that then there will
     *                       have to be some code written to control the range on the IMU.
     * @param distance       when the movement hits this distance, the update will return true
     * @param speed          the speed to run the motors. This should be a positive number. Drive direction will
     *                       take care of the sign of the speed.
     *                       Positive is forwards. Negative is backwards.
     * @param driveDirection
     * @param headingType    RAW, ABSOLUTE or RELATIVE. See AngleMode for desscription.
     */
    // THIS METHOD HAS A BUG. IT DOES NOT DRIVE BACKWARDS PROPERLY. NEED TO FIX IT
    public void setupDriveUsingIMU(double heading, double distance, double speed, DriveDirection driveDirection, AdafruitIMU8863.AngleMode headingType) {

        if (imuPresent) {
            // set the mode for the motors so they run using speed control. Without this they may not move.
            rightDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            // setup the angle adjusted IMU
            angleAdjustedIMU.setTargetAngle(heading);
            // setup PID control
            pidControl.setSetpoint(heading);
            pidControl.setMaxCorrection(speed);
            // threshold is not meaningful in this movement. It is normally used to say when the
            // movement is complete but since this movement goes forever, threshold does nothing.
            // But it has to be set to something!
            pidControl.setThreshold(10);
            //pidControl.setKp(0.011);
            pidControl.setKp(0.012);
            pidControl.setKi(0.05 / 1000000);
            // reset the integrator before starting
            pidControl.reset();

            switch (headingType) {
                // since the turn is relative to the current heading, reset the angle references to 0
                case RELATIVE:
                    angleAdjustedIMU.imu.setAngleMode(AdafruitIMU8863.AngleMode.RELATIVE);
                    angleAdjustedIMU.imu.resetAngleReferences();
                    break;
                // since the movement is running on an absolute heading, we don't want to reset
                // the angle references. Just tell the IMU to give us headings in a relative mode
                case ABSOLUTE:
                    angleAdjustedIMU.imu.setAngleMode(AdafruitIMU8863.AngleMode.ABSOLUTE);
                    break;
                // not sure why you would want to use this mode
                case RAW:
                    angleAdjustedIMU.imu.setAngleMode(AdafruitIMU8863.AngleMode.RAW);
                    break;
            }

            // set the proper sign on the speed for the motors
            if (driveDirection == DriveDirection.FORWARD) {
                driveTrainPower = Math.abs(speed);
            } else {
                driveTrainPower = -Math.abs(speed);
            }

            // set the distance target
            this.distanceToDrive = distance;
            // since this is movement that is relative to its starting point, we have to know the starting point
            initialDistanceDriven = updateDistanceDriven();
            // distance driven is calculated by averaging the encoder counts on the left and
            // right wheels relative to the last time the encoder count tracker in the motor was set
            // to 0. That 0 automatically happens when a movement is to a position. But in this case
            // we are just turning the motors on so the DcMotor8863 does not zero the encoder tracker.
            // I have to do it manually.
            //rightDriveMotor.setLastEncoderCountToCurrentPostion();
            //leftDriveMotor.setLastEncoderCountToCurrentPostion();
            if (logFile != null && logDrive) {
                logFile.blankLine();
                logFile.logData("DRIVE_STRAIGHT_USING_IMU DESIRED Heading = " + Double.toString(heading) + " Speed = " + Double.toString(speed) + " distance = " + Double.toString(distanceToDrive) + " drive_direction = " + driveDirection.toString());
            }
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
    }

    /**
     * Method to log the current heading and position of the robot before starting to drive.
     */
    public void startDriveUsingIMU() {
        if (logFile != null && logDrive) {
            logFile.logData("DRIVE_STRAIGHT_USING_IMU INITIAL_HEADING_DISTANCE", angleAdjustedIMU.getHeading(), updateDistanceDriven());
        }
    }

    /**
     * You must call this method in a loop after you call setupDriveUsingIMU() in order to get the
     * movement to work properly. That is because the PID needs to be constantly updated and the
     * distance driven has to be constantly updated.
     *
     * @return true if the distance traveled is greater than the distance desired (ie the distance
     * to drive set in the setup.
     */
    public boolean updateDriveUsingIMU() {
        double distanceDrivenSinceStart;
        double currentHeading = angleAdjustedIMU.getHeading();
        // I have to reverse the sign since the differential drive method expects a negative
        // joystick input for a left turn (joystick left = negative number, not what you would
        // expect).
        // get the correction from the PID
        double correction = -pidControl.getCorrection(currentHeading);
//            if (driveTrainPower < 0) {
//                // sign on correction has to flip or feedback is wrong direction
//                correction = correction * -1;
//            }
        // apply the correction to the motors
        differentialDrive(driveTrainPower, correction);
        // THERE IS A BUG HERE. THE DISTANCE BEING REPORTED IS CUMULATIVE NOT RELATIVE TO THE START OF THE MOVEMENT
        distanceDrivenSinceStart = updateDistanceDriven() - initialDistanceDriven;
        // for negative distances (driving backwards) distance driven will start out at 0 and be larger than distance to drive (-150)
        // so we have to take absolute values
        if (Math.abs(distanceDrivenSinceStart) > Math.abs(distanceToDrive)) {
            if (logFile != null && logDrive) {
                logFile.logData("DRIVE_STRAIGHT_USING_IMU FINAL_HEADING_DISTANCE", currentHeading, getDistanceDriven());
                logFile.logData("Distance driven during drive straight = " + Double.toString(distanceDrivenSinceStart));
                logFile.blankLine();
            }
            return true;
        } else {
            if (logFile != null && logDrive) {
                logFile.logData("DRIVE_STRAIGHT_USING_IMU HEADING_DISTANCE", currentHeading, getDistanceDriven());
            }
            return false;
        }
    }

    /**
     * Call this after you complete the drive distance using IMU so that the total distance traveled
     * by the robot is updated.
     */
    public void completeDriveUsingIMU() {
        updateTotalDistanceDriven();
    }

    /**
     * Stop the movement of the robot
     */
    public void stopDriveUsingIMU() {
        updateTotalDistanceDriven();
        shutdown();
    }

    /**
     * Drive a distance on a heading using the IMU for heading feedback and the RUN_TO_POSITION of
     * the motor controller to control distance. This method also has a ramp up of the power to
     * eliminate wheel slip at startup. It can have a ramp down in power to gradually slow the
     * robot down at the end of the drive if you use the overloaded method.
     *
     * @param heading                      drive at this heading
     * @param maxPower                     power to drive at (-1 to 1) Positive is forwards. Negative is backwards.
     * @param distance                     distance to drive
     * @param headingType                  is the heading relative to where the robot is starting or absolute. Absolute
     *                                     means it is relative to the startup of the robot
     * @param powerAtStartRamp             power at the start of the ramp
     * @param powerAtFinishRamp            power at the end of the ramp. Typically you make this equal to the
     *                                     maxPower.
     * @param timeToReachFinishPowerInmSec how long to run the ramp up in power (in milliseconds)
     */
    public void setupDriveDistanceUsingIMU(double heading, double maxPower, double distance,
                                           AdafruitIMU8863.AngleMode headingType, double powerAtStartRamp,
                                           double powerAtFinishRamp, double timeToReachFinishPowerInmSec) {
        //If the IMU is present we proceed. If not we give the user an error
        if (imuPresent) {
            // Setup the ramp control to ramp the power up from 0 to maxPower
            rampControl.setup(powerAtStartRamp, powerAtFinishRamp, timeToReachFinishPowerInmSec);
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
            zeroDistanceDriven();
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
            // a negative distance means the robot is driving backwards
            if (distance < 0) {
                driveDirection = DriveDirection.REVERSE;
            } else {
                driveDirection = DriveDirection.FORWARD;
            }
            drivingState = DrivingState.START_RAMP;
            //If IMU isn't present
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
    }

    /**
     * Drive a distance on a heading using the IMU for heading feedback and the RUN_TO_POSITION of
     * the motor controller to control distance. This method also has a ramp up of the power to
     * eliminate wheel slip at startup. This overloaded method allows you to setup a ramp down also.
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
     * @param initialPower                 start the ramp down at this power
     * @param finalPower                   finish the ramp down at this power
     * @param distanceToRampDownOver       ramp down the power over this distance
     */
    public void setupDriveDistanceUsingIMU(double heading, double maxPower, double distance,
                                           AdafruitIMU8863.AngleMode headingType, double valueAtStartTime,
                                           double valueAtFinishTime, double timeToReachFinishValueInmSec,
                                           double initialPower, double finalPower, double distanceToRampDownOver) {
        setupDriveDistanceUsingIMU(heading, maxPower, distance, headingType, valueAtStartTime,
                valueAtFinishTime, timeToReachFinishValueInmSec);
        this.deltaX = distanceToRampDownOver;
        this.rampDownStartOffset = getRampDownStartOffset(maxPower);
        setupChangeInPower(initialPower, finalPower, distanceToRampDownOver);
    }

    public DrivingState updateDriveDistanceUsingIMUState() {
        //setting up an array so that we can hold onto the left and right motor drive powers
        double[] drivePowers = new double[2];
        double power = driveTrainPower;
        double distancePastRampDownStart = 0;

        if (imuPresent) {
            // Figure out where the robot is at now during the drive
            distanceDriven = calculateDistanceDriven();
            if (debug) {
                telemetry.addData("distance driven = ", "%3.1f", distanceDriven);
            }
            // calculate the distance the robot is from the point the ramp down will start
            // if this is positive it is past the point and the ramp down should be running
            // if this is negative the point has not been reached yet
            // I'm forcing the ramp to start a little early (-rampDownStartOffset) so that the robot
            // will not end the ramp down right at the end point of the drive. In that case it might
            // actually roll past the end point and have to back up.
            distancePastRampDownStart = Math.abs(distanceDriven) - (Math.abs(distanceToDrive) - deltaX - rampDownStartOffset);
            // run the state machine
            switch (drivingState) {
                case START_RAMP:
                    startRampCounter++;
                    // if ramp up is enabled but not running start it and then start the motors
                    if (rampControl.isEnabled() && !rampControl.isRunning()) {
                        rampControl.start();
                        power = rampControl.getRampValueLinear(driveTrainPower);
                        if (debug) {
                            telemetry.addData("ramp up output = ", "%1.2f", power);
                        }
                        // run the PID to adjust powers to keep on heading
                        drivePowers = adjustPowerUsingPID(power);
                        if (debug) {
                            telemetry.addData("PID left output = ", "%1.2f", drivePowers[0]);
                            telemetry.addData("PID right output = ", "%1.2f", drivePowers[1]);
                        }
                        //we start the motors moving using the powers calculated above
                        // check for forwards or backwards movement
                        if (driveDirection == DriveDirection.FORWARD) {
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
                    rampUpCounter++;
                    if (distanceAtRampUp == 0) {
                        distanceAtRampUp = distanceDriven;
                    }
                    // if ramp is finished then we are in the constant speed mode next time
                    if (rampControl.isFinished()) {
                        drivingState = DrivingState.CONSTANT_SPEED;
                    }
                    // The robot has momentum and will roll past the desired distance unless we
                    // start the ramp down a bit before the user requested. So if the robot is past
                    // the point where the ramp down should start + a little margin
                    // (-rampDownStartPoint from the calculation of distancePastRampDownStart),
                    // change the state for the next update
                    if (distancePastRampDownStart >= 0 && rampDown) {
                        drivingState = DrivingState.RAMP_DOWN;
                    }
                    // if the movement of the motors is complete then move to the complete state
                    if (isDriveTrainComplete()) {
                        drivingState = DrivingState.COMPLETE;
                    }
                    // only calculate a new ramp power power if we are still in that state
                    // This means we skip a power update for the time slot
                    if (drivingState == DrivingState.RAMP_UP) {
                        power = rampControl.getRampValueLinear(driveTrainPower);
                        if (debug) {
                            telemetry.addData("ramp up output = ", "%1.2f", power);
                        }
                        drivePowers = adjustPowerUsingPID(power);
                        if (debug) {
                            telemetry.addData("PID left output = ", "%1.2f", drivePowers[0]);
                            telemetry.addData("PID right output = ", "%1.2f", drivePowers[1]);
                        }
                        applyPowersToMotors(drivePowers);
                    }
                    break;
                case CONSTANT_SPEED:
                    if (distanceAtConstantSpeed == 0) {
                        distanceAtConstantSpeed = distanceDriven;
                    }
                    constantSpeedCounter++;
                    // The robot has momentum and will roll past the desired distance unless we
                    // start the ramp down a bit before the user requested. So if the robot is past
                    // the point where the ramp down should start + a little margin
                    // (-rampDownStartPoint from the calculation of distancePastRampDownStart),
                    // change the state for the next update
                    if (distancePastRampDownStart >= 0 && rampDown) {
                        drivingState = DrivingState.RAMP_DOWN;
                    }
                    // if the movement of the motors is complete then move to the complete state
                    if (isDriveTrainComplete()) {
                        drivingState = DrivingState.COMPLETE;
                    }
                    // cruise at constant speed, adjusting motors to maintain heading
                    drivePowers = adjustPowerUsingPID(driveTrainPower);
                    if (debug) {
                        telemetry.addData("PID left output = ", "%1.2f", drivePowers[0]);
                        telemetry.addData("PID right output = ", "%1.2f", drivePowers[1]);
                    }
                    applyPowersToMotors(drivePowers);
                    break;
                case RAMP_DOWN:
                    if (distanceAtRampDown == 0) {
                        distanceAtRampDown = distanceDriven;
                    }
                    rampDownCounter++;
                    // if the movement of the motors is complete then move to the complete state
                    if (isDriveTrainComplete()) {
                        drivingState = DrivingState.COMPLETE;
                    }
                    // If the robot is driving backwards, the distances are negative and the distance
                    // remaining will be negative. But this does not matter since Torcelli takes
                    // absolute value of the distances.
                    // Only update the ramp if we are still in this state
                    if (drivingState == DrivingState.RAMP_DOWN) {
                        power = torcelli.getPower(distancePastRampDownStart);
                        if (debug) {
                            telemetry.addData("ramp down output = ", "%1.2f", power);
                        }
                        if (Double.isNaN(power)) {
                            telemetry.addData("torcelli.getPower is NAN, distance = ", "%2.2f", distancePastRampDownStart);
                            telemetry.update();
                            throw new IllegalArgumentException("torcelli.getPower returned NaN");
                        }
                        if (torcelli.isComplete()) {
                            drivingState = DrivingState.MOVING_UNTIL_COMPLETE;
                        }
                        drivePowers = adjustPowerUsingPID(power);
                        if (debug) {
                            telemetry.addData("PID left output = ", "%1.2f", drivePowers[0]);
                            telemetry.addData("PID right output = ", "%1.2f", drivePowers[1]);
                        }
                        if (Double.isNaN(drivePowers[0]) || Double.isNaN(drivePowers[1])) {
                            telemetry.addData("PID ajusted power (left) = ", "%1.2", drivePowers[0]);
                            telemetry.addData("PID ajusted power (left) = ", "%1.2", drivePowers[1]);
                            telemetry.update();
                            throw new IllegalArgumentException("PID returned NaN");
                        }
                        applyPowersToMotors(drivePowers);
//                        telemetry.addData("Left motor isComplete = ", leftDriveMotor.isRotationComplete());
//                        telemetry.addData("Left motor target = ", "%5d", leftDriveMotor.getTargetEncoderCount());
//                        telemetry.addData("Left motor position = ", "%5d", leftDriveMotor.getCurrentPosition());
//                        telemetry.addData("Right motor isComplete = ", rightDriveMotor.isRotationComplete());
//                        telemetry.addData("Right motor target = ", "%5d", rightDriveMotor.getTargetEncoderCount());
//                        telemetry.addData("Right motor position = ", "%5d", rightDriveMotor.getCurrentPosition());
                    }
                    break;
                case MOVING_UNTIL_COMPLETE:
                    if (distanceAtMovingUntilComplete == 0) {
                        distanceAtMovingUntilComplete = distanceDriven;
                    }
                    movingUntilCompleteCounter++;
                    // in this state the ramp down has finished but the robot is not quite at the
                    // final destination
                    // if the movement of the motors is complete then move to the complete state
                    if (isDriveTrainComplete()) {
                        drivingState = DrivingState.COMPLETE;
                    } else {
                        power = torcelli.getFinalPower();
                        if (debug) {
                            telemetry.addData("ramp down output = ", "%1.2f", power);
                        }
                        drivePowers = adjustPowerUsingPID(power);
                        if (debug) {
                            telemetry.addData("PID left output = ", "%1.2f", drivePowers[0]);
                            telemetry.addData("PID right output = ", "%1.2f", drivePowers[1]);
                        }
                        applyPowersToMotors(drivePowers);
                    }
//                    telemetry.addData("Left motor isComplete = ", leftDriveMotor.isRotationComplete());
//                    telemetry.addData("Left motor target = ", "%5d", leftDriveMotor.getTargetEncoderCount());
//                    telemetry.addData("Left motor position = ", "%5d", leftDriveMotor.getCurrentPosition());
//                    telemetry.addData("Right motor isComplete = ", rightDriveMotor.isRotationComplete());
//                    telemetry.addData("Right motor target = ", "%5d", rightDriveMotor.getTargetEncoderCount());
//                    telemetry.addData("Right motor position = ", "%5d", rightDriveMotor.getCurrentPosition());
                    break;
                case COMPLETE:
                    if (distanceAtComplete == 0) {
                        distanceAtComplete = distanceDriven;
                    }
                    completeCounter++;
                    stopDriveDistanceUsingIMU();
                    break;
            }
            // update the motor state machines
            leftDriveMotor.update();
            rightDriveMotor.update();
            // If IMU isn't present
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
        //
        if (debug) {
            telemetry.addData("State = ", drivingState.toString());
            telemetry.addData("START_RAMP counter = ", "%5d", startRampCounter);
            telemetry.addData("RAMP_UP counter = ", "%5d", rampUpCounter);
            telemetry.addData("CONSTANT_SPEED counter = ", "%4d", constantSpeedCounter);
            telemetry.addData("RAMP_DOWN counter = ", "%4d", rampDownCounter);
            telemetry.addData("MOVING_UNTIL_COMPLETE counter = ", "%4d", movingUntilCompleteCounter);
            telemetry.addData("COMPLETE counter = ", "%4d", completeCounter);
            telemetry.addData("Left drive power = ", "%2.2f", drivePowers[0]);
            telemetry.addData("Right drive power = ", "%2.2f", drivePowers[1]);
            telemetry.addData("Heading = ", "%3.1f", imu.getHeading());
            telemetry.addData("distance = ", "%3.1f", distanceDriven);
            //telemetry.addData("Left motor isRotationComplete = ", leftDriveMotor.isRotationComplete());
            //telemetry.addData("Left motor state = ", leftDriveMotor.getCurrentMotorState().toString());
            telemetry.addData("Left motor target = ", "%5d", leftDriveMotor.getTargetEncoderCount());
            telemetry.addData("Left motor position = ", "%5d", leftDriveMotor.getCurrentPosition());
            //telemetry.addData("Right motor isRotationComplete = ", rightDriveMotor.isRotationComplete());
            //telemetry.addData("Right motor state = ", rightDriveMotor.getCurrentMotorState().toString());
            telemetry.addData("Right motor target = ", "%5d", rightDriveMotor.getTargetEncoderCount());
            telemetry.addData("Right motor position = ", "%5d", rightDriveMotor.getCurrentPosition());
            // did the momentum of the robot carry it past the desired ramp down distance?
            if (distancePastRampDownStart > deltaX) {
                telemetry.addData("robot location = ", "%3.1f", distancePastRampDownStart);
                telemetry.addData("deltaX = ", "%3.1f", deltaX);
            }
            telemetry.addData("Distance at ramp up = ", "%3.1f", distanceAtRampUp);
            telemetry.addData("Distance at constant speed = ", "%3.1f", distanceAtConstantSpeed);
            telemetry.addData("Distance at ramp down = ", "%3.1f", distanceAtRampDown);
            telemetry.addData("Distance at moving until complete = ", "%3.1f", distanceAtMovingUntilComplete);
            telemetry.addData("Distance at complete = ", "%3.1f", distanceAtComplete);

        }

        return drivingState;
    }

    /**
     * During a ramp down from running at a power, I actually don't want the ramp down to last right
     * until the end of the drive. I would like a little buffer so that if the robot runs past the
     * end of the drive it does not have to back up to reach the final position. The robot can
     * overrun the desired distance due to varying times in the robot loop. The more momentum
     * (higher the speed) the robot has the more likely it is to overrun the end of the drive. So
     * start the ramp down a distance before the user wants to so that the robot will glide into
     * the end point and not overrun. The distance before is going to depend on the requested speed.
     *
     * @param maxPower - power the robot will run at before the ramp down starts
     * @return the number of cm to allow the robot glide into the desired distance
     */
    private double getRampDownStartOffset(double maxPower) {
        double result = 0;
        if (0 <= maxPower && maxPower <= .5) {
            result = 3; // cm of glide in to desired destination
        }
        if (.5 < maxPower && maxPower <= .7) {
            result = 4; // cm of glide in to desired destination
        }
        if (.7 < maxPower && maxPower <= .9) {
            result = 12; // cm of glide in to desired destination
        }
        if (.9 < maxPower && maxPower <= 1.0) {
            result = 18; // cm of glide in to desired destination
        }
        return result;
    }

    /**
     * You must run this update in a loop in order for the drive distance to work.
     *
     * @return true if movement is complete
     */
    public boolean updateDriveDistanceUsingIMU() {
        //setting up an array so that we can hold onto the left and right motor drive powers
        double[] drivePowers;
        if (imuPresent) {
            //If the IMU is present we proceed if not we give the user an error
            if (!hasLoopRunYet) {
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
            if (leftDriveMotor.isMotorStateComplete() && rightDriveMotor.isMotorStateComplete()) {
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
        updateTotalDistanceDriven();
        shutdown();
    }

    //*********************************************************************************************
    // Autonomous Methods - turns
    //*********************************************************************************************

    /**
     * @param turnAngle in a range from -180 to 180. Negative angles run clockwise from 0 to -180.
     *                  Positive angles run counter clockwise from 0 to 180.
     * @param maxPower  max power you want for the turn
     * @param angleMode Is the starting point for this turn going to be 0 degrees? (relative)? OR is
     *                  it going to be so that 0 degrees is whatever was set in the beginning when
     *                  the robot was first turned on (absolute)?
     */
    public void setupTurn(double turnAngle, double maxPower, AdafruitIMU8863.AngleMode angleMode) {

        if (imuPresent) {
            // set the mode for the motors during the turn. Without this they may not move.
            rightDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            leftDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            pidControl.setSetpoint(turnAngle);
            pidControl.setMaxCorrection(maxPower);
            pidControl.setThreshold(1.0);
            //pidControl.setKp(0.025);
            //pidControl.setKi(0.0000000015);
//            pidControl.setKp(0.0125);
//////            pidControl.setKi(0.00000000025);
            pidControl.setKp(0.009);
            pidControl.setKi(0.05 / 1000000);
            pidControl.reset();
            zeroDistanceDriven();

            imu.setAngleMode(angleMode);
            if (angleMode == AdafruitIMU8863.AngleMode.RELATIVE) {
                if (turnAngle > 90) {
                    // make the angles reported from the IMU 0 to +360
                    imu.setAngleRange(AdafruitIMU8863.AngleRange.ZERO_TO_PLUS_360);
                } else {
                    if (turnAngle < -90) {
                        // make the angles reported from the IMU 0 to -360
                        imu.setAngleRange(AdafruitIMU8863.AngleRange.ZERO_TO_MINUS_360);
                    } else {
                        // turn angle is between -90 and 90 so make the angles reported from the IMU
                        // -180 to 180
                        imu.setAngleRange(AdafruitIMU8863.AngleRange.PLUS_TO_MINUS_180);
                    }
                }
                imu.resetAngleReferences();
            }
            if (logTurns && logFile != null) {
                logFile.logData("SPIN_TURN DESIRED heading = " + turnAngle + " power = " + maxPower);
                logFile.logData("SPIN_TURN INITIAL_HEADING", imu.getHeading());
            }
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
    }

    public void resetTurnTimer() {
        logFile.startTimer();
    }

    public boolean updateTurn() {

        if (imuPresent) {
            double currentHeading = imu.getHeading();
            double correction = -pidControl.getCorrection(currentHeading);
            if (logTurns && logFile != null) {
                logFile.logData("SPIN_TURN HEADING_CORRECTION", currentHeading, correction);
            }
            differentialDrive(0, correction);
            //return correction;
            if (pidControl.isFinished()) {
                if (logTurns && logFile != null) {
                    logFile.logData("SPIN_TURN FINAL_HEADING", imu.getHeading());
                }
            }
            return pidControl.isFinished();
        } else {
            shutdown();
            throw new IllegalArgumentException("No Imu found");
        }
    }

    public void stopTurn() {
        updateTotalDistanceDriven();
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

        distanceDriven = calculateDistanceDriven();
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

}
