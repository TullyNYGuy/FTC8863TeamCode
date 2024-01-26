package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.drive.DriveSignal;
import com.acmerobotics.roadrunner.drive.MecanumDrive;
import com.acmerobotics.roadrunner.followers.HolonomicPIDVAFollower;
import com.acmerobotics.roadrunner.followers.TrajectoryFollower;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.ProfileAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryAccelerationConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayTrackingWheelLocalizer;
import org.firstinspires.ftc.teamcode.RoadRunner.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.RoadRunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.RoadRunner.trajectorysequence.TrajectorySequenceRunner;
import org.firstinspires.ftc.teamcode.RoadRunner.util.DashboardUtil;
import org.firstinspires.ftc.teamcode.RoadRunner.util.LynxModuleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/*
 * Simple mecanum drive hardware implementation for REV hardware.
 */
@Config
public class CenterStageMecanumDrive extends MecanumDrive implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    public enum Mode {
        IDLE,
        TURN,
        FOLLOW_TRAJECTORY
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    // this is our addition
    private final String MECANUM_DRIVE_NAME = CenterStageRobot.HardwareName.MECANUM_DRIVE.hwName;

    // THESE COME FROM THE SAMPLE MECANUM DRIVE CLASS IN road-runner-quickstart

    //public static PIDCoefficients TRANSLATIONAL_PID = new PIDCoefficients(8, 0, 1);
    //public static PIDCoefficients HEADING_PID = new PIDCoefficients(8, 0, 0);

    public static PIDCoefficients TRANSLATIONAL_PID = new PIDCoefficients(8, 0, 1);
    public static PIDCoefficients HEADING_PID = new PIDCoefficients(8, 0, 0);

    public static double LATERAL_MULTIPLIER = 1.011973299396;

    public static double VX_WEIGHT = 1;
    public static double VY_WEIGHT = 1;
    public static double OMEGA_WEIGHT = 1;

    private TrajectorySequenceRunner trajectorySequenceRunner;

    public static int POSE_HISTORY_LIMIT = 100;



    private FtcDashboard dashboard;
    private NanoClock clock;

    private SampleMecanumDrive.Mode mode;

    private PIDFController turnController;
    private MotionProfile turnProfile;
    private double turnStart;

    private TrajectoryVelocityConstraint velConstraint;
    private TrajectoryVelocityConstraint velConstraintSlow;

    public TrajectoryVelocityConstraint getVelConstraintSlow() {
        return velConstraintSlow;
    }

    private TrajectoryAccelerationConstraint accelConstraint;

    public TrajectoryAccelerationConstraint getAccelConstraint() {
        return accelConstraint;
    }

    private TrajectoryFollower follower;
    private TrajectoryFollower followerHighAccuracy;
    private TrajectoryFollower followerToUse;

    private LinkedList<Pose2d> poseHistory;

    private DcMotorEx leftFront, leftRear, rightRear, rightFront;
    private List<DcMotorEx> motors;
    private BNO055IMU imu;

    private VoltageSensor batteryVoltageSensor;

    private Pose2d lastPoseOnTurn;

    // THESE ARE OUR OWN ADDITIONS

    private DataLogging logFile = null;
    private boolean loggingOn = false;
    private DataLogOnChange logCommandOnchange;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    private void logCommand(String command) {
        if (loggingOn && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    //*********************************************************************************************
    //          Constructors
    //
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public CenterStageMecanumDrive(String frontLeftMotorName, String backLeftMotorName, String frontRightMotorName, String backRightMotorName, HardwareMap hardwareMap) {
        super(DriveConstants.kV, DriveConstants.kA, DriveConstants.kStatic, DriveConstants.TRACK_WIDTH, DriveConstants.TRACK_WIDTH, LATERAL_MULTIPLIER);

        dashboard = FtcDashboard.getInstance();
        dashboard.setTelemetryTransmissionInterval(25);

        clock = NanoClock.system();

        mode = SampleMecanumDrive.Mode.IDLE;

        turnController = new PIDFController(HEADING_PID);
        turnController.setInputBounds(0, 2 * Math.PI);

        velConstraint = new MinVelocityConstraint(Arrays.asList(
                new AngularVelocityConstraint(DriveConstants.MAX_ANG_VEL),
                new MecanumVelocityConstraint(DriveConstants.MAX_VEL, DriveConstants.TRACK_WIDTH)
        ));
        velConstraintSlow = new MinVelocityConstraint(Arrays.asList(
                new AngularVelocityConstraint(DriveConstants.MAX_ANG_VEL),
                new MecanumVelocityConstraint(DriveConstants.MAX_VEL_SLOW, DriveConstants.TRACK_WIDTH)
        ));
        accelConstraint = new ProfileAccelerationConstraint(DriveConstants.MAX_ACCEL);
        follower = new HolonomicPIDVAFollower(TRANSLATIONAL_PID, TRANSLATIONAL_PID, HEADING_PID,
                new Pose2d(0.5, 0.5, Math.toRadians(2.5)), 1.0);
        // a follower with a higher accuracy and longer timeout in case you want a really accurate result
        followerHighAccuracy = new HolonomicPIDVAFollower(TRANSLATIONAL_PID, TRANSLATIONAL_PID, HEADING_PID,
                new Pose2d(0.25, 0.25, Math.toRadians(2.5)), 2.0);
        // default follower is the lower accuracy but faster one - the standard one shipped with roadrunner
        followerToUse = follower;

        poseHistory = new LinkedList<>();

        LynxModuleUtil.ensureMinimumFirmwareVersion(hardwareMap);

        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();

        for (LynxModule module : hardwareMap.getAll(LynxModule.class)) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        // TODO: adjust the names of the following hardware devices to match your configuration
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imu.initialize(parameters);

        // TODO: if your hub is mounted vertically, remap the IMU axes so that the z-axis points
        // upward (normal to the floor) using a command like the following:
        // BNO055IMUUtil.remapAxes(imu, AxesOrder.XYZ, AxesSigns.NPN);

        leftFront = hardwareMap.get(DcMotorEx.class, frontLeftMotorName);
        leftRear = hardwareMap.get(DcMotorEx.class, backLeftMotorName);
        rightRear = hardwareMap.get(DcMotorEx.class, backRightMotorName);
        rightFront = hardwareMap.get(DcMotorEx.class, frontRightMotorName);

        motors = Arrays.asList(leftFront, leftRear, rightRear, rightFront);

        for (DcMotorEx motor : motors) {
            MotorConfigurationType motorConfigurationType = motor.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
            motor.setMotorType(motorConfigurationType);
        }

        if (DriveConstants.RUN_USING_ENCODER) {
            setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        if (DriveConstants.RUN_USING_ENCODER && DriveConstants.MOTOR_VELO_PID != null) {
            setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, DriveConstants.MOTOR_VELO_PID);
        }

        // TODO: reverse any motors using DcMotor.setDirection()
        leftFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
        rightRear.setDirection(DcMotorSimple.Direction.FORWARD);

        // TODO: if desired, use setLocalizer() to change the localization method
        // for instance, setLocalizer(new ThreeTrackingWheelLocalizer(...));
        setLocalizer(new CenterStageTrackingWheelLocalizer(hardwareMap));
    }

    // todo TANYA - WE COMMENTED THIS OUT BECAUSE IT WAS CAUSING A CRASH
//    private Double heading = getExternalHeading();
    private Double adjustmentAngle = 0.0;


    public void resetAdjustAngle(){
        setExternalHeading(imu.getAngularOrientation().thirdAngle);
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

    // MOTION CONTROL METHODS - THESE COME FROM road-runner-quickstart


    public TrajectoryBuilder trajectoryBuilder(Pose2d startPose) {
        return new TrajectoryBuilder(startPose, velConstraint, accelConstraint);
    }

    public TrajectoryBuilder trajectoryBuilder(Pose2d startPose, boolean reversed) {
        return new TrajectoryBuilder(startPose, reversed, velConstraint, accelConstraint);
    }

    public TrajectoryBuilder trajectoryBuilder(Pose2d startPose, double startHeading) {
        return new TrajectoryBuilder(startPose, startHeading, velConstraint, accelConstraint);
    }

    public void turnAsync(double angle) {
        double heading = getPoseEstimate().getHeading();

        lastPoseOnTurn = getPoseEstimate();

        turnProfile = MotionProfileGenerator.generateSimpleMotionProfile(
                new MotionState(heading, 0, 0, 0),
                new MotionState(heading + angle, 0, 0, 0),
                DriveConstants.MAX_ANG_VEL,
                DriveConstants.MAX_ANG_ACCEL
        );

        turnStart = clock.seconds();
        mode = SampleMecanumDrive.Mode.TURN;
    }

    public void turn(double angle) {
        turnAsync(angle);
        waitForIdle();
    }

    public void followTrajectoryAsync(Trajectory trajectory) {
        followerToUse.followTrajectory(trajectory);
        mode = SampleMecanumDrive.Mode.FOLLOW_TRAJECTORY;
    }

    public void followTrajectory(Trajectory trajectory) {
        followerToUse = follower;
        followTrajectoryAsync(trajectory);
        waitForIdle();
    }

    public void followTrajectoryHighAccuracy(Trajectory trajectory) {
        followerToUse = followerHighAccuracy;
        followTrajectoryAsync(trajectory);
        waitForIdle();
    }

    public void cancelFollowing() {
        // force the state machine to IDLE
        mode = SampleMecanumDrive.Mode.IDLE;
        // set the motor powers to 0
        setDrivePower(new Pose2d());
    }

    public Pose2d getLastError() {
        switch (mode) {
            case FOLLOW_TRAJECTORY:
                return followerToUse.getLastError();
            case TURN:
                return new Pose2d(0, 0, turnController.getLastError());
            case IDLE:
                return new Pose2d();
        }
        throw new AssertionError();
    }

    public void update() {
        updatePoseEstimate();

        Pose2d currentPose = getPoseEstimate();
        Pose2d lastError = getLastError();

        poseHistory.add(currentPose);

        if (POSE_HISTORY_LIMIT > -1 && poseHistory.size() > POSE_HISTORY_LIMIT) {
            poseHistory.removeFirst();
        }

        TelemetryPacket packet = new TelemetryPacket();
        Canvas fieldOverlay = packet.fieldOverlay();

        packet.put("mode", mode);

        packet.put("x", currentPose.getX());
        packet.put("y", currentPose.getY());
        packet.put("heading (deg)", Math.toDegrees(currentPose.getHeading()));

        packet.put("xError", lastError.getX());
        packet.put("yError", lastError.getY());
        packet.put("headingError (deg)", Math.toDegrees(lastError.getHeading()));

        switch (mode) {
            case IDLE:
                // do nothing
                break;
            case TURN: {
                double t = clock.seconds() - turnStart;

                MotionState targetState = turnProfile.get(t);

                turnController.setTargetPosition(targetState.getX());

                double correction = turnController.update(currentPose.getHeading());

                double targetOmega = targetState.getV();
                double targetAlpha = targetState.getA();
                setDriveSignal(new DriveSignal(new Pose2d(
                        0, 0, targetOmega + correction
                ), new Pose2d(
                        0, 0, targetAlpha
                )));

                Pose2d newPose = lastPoseOnTurn.copy(lastPoseOnTurn.getX(), lastPoseOnTurn.getY(), targetState.getX());

                fieldOverlay.setStroke("#4CAF50");
                DashboardUtil.drawRobot(fieldOverlay, newPose);

                if (t >= turnProfile.duration()) {
                    mode = SampleMecanumDrive.Mode.IDLE;
                    setDriveSignal(new DriveSignal());
                }

                break;
            }
            case FOLLOW_TRAJECTORY: {
                setDriveSignal(followerToUse.update(currentPose, getPoseVelocity()));

                Trajectory trajectory = followerToUse.getTrajectory();

                fieldOverlay.setStrokeWidth(1);
                fieldOverlay.setStroke("#4CAF50");
                DashboardUtil.drawSampledPath(fieldOverlay, trajectory.getPath());
                double t = followerToUse.elapsedTime();
                DashboardUtil.drawRobot(fieldOverlay, trajectory.get(t));

                fieldOverlay.setStroke("#3F51B5");
                DashboardUtil.drawPoseHistory(fieldOverlay, poseHistory);

                if (!followerToUse.isFollowing()) {
                    mode = SampleMecanumDrive.Mode.IDLE;
                    setDriveSignal(new DriveSignal());
                }

                break;
            }
        }

        fieldOverlay.setStroke("#3F51B5");
        DashboardUtil.drawRobot(fieldOverlay, currentPose);

        dashboard.sendTelemetryPacket(packet);
    }

    public void waitForIdle() {
        while (!Thread.currentThread().isInterrupted() && isBusy()) {
            update();
        }
    }

    public boolean isBusy() {
        return mode != SampleMecanumDrive.Mode.IDLE;
    }

    public void setMode(DcMotor.RunMode runMode) {
        for (DcMotorEx motor : motors) {
            motor.setMode(runMode);
        }
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior) {
        for (DcMotorEx motor : motors) {
            motor.setZeroPowerBehavior(zeroPowerBehavior);
        }
    }

    public void setPIDFCoefficients(DcMotor.RunMode runMode, PIDFCoefficients coefficients) {
        PIDFCoefficients compensatedCoefficients = new PIDFCoefficients(
                coefficients.p, coefficients.i, coefficients.d,
                coefficients.f * 12 / batteryVoltageSensor.getVoltage()
        );
        for (DcMotorEx motor : motors) {
            motor.setPIDFCoefficients(runMode, compensatedCoefficients);
        }
    }

    public void setWeightedDrivePower(Pose2d drivePower) {
        Pose2d vel = drivePower;

        if (Math.abs(drivePower.getX()) + Math.abs(drivePower.getY())
                + Math.abs(drivePower.getHeading()) > 1) {
            // re-normalize the powers according to the weights
            double denom = VX_WEIGHT * Math.abs(drivePower.getX())
                    + VY_WEIGHT * Math.abs(drivePower.getY())
                    + OMEGA_WEIGHT * Math.abs(drivePower.getHeading());

            vel = new Pose2d(
                    VX_WEIGHT * drivePower.getX(),
                    VY_WEIGHT * drivePower.getY(),
                    OMEGA_WEIGHT * drivePower.getHeading()
            ).div(denom);
        }

        setDrivePower(vel);
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        List<Double> wheelPositions = new ArrayList<>();
        for (DcMotorEx motor : motors) {
            wheelPositions.add(DriveConstants.encoderTicksToInches(motor.getCurrentPosition()));
        }
        return wheelPositions;
    }

    public List<Integer> getEncoderCounts() {
        List<Integer> encoderCounts = new ArrayList<>();
        for (DcMotorEx motor : motors) {
            encoderCounts.add((motor.getCurrentPosition()));
        }
        return encoderCounts;
    }

    @Override
    public List<Double> getWheelVelocities() {
        List<Double> wheelVelocities = new ArrayList<>();
        for (DcMotorEx motor : motors) {
            wheelVelocities.add(DriveConstants.encoderTicksToInches(motor.getVelocity()));
        }
        return wheelVelocities;
    }

    @Override
    public void setMotorPowers(double v, double v1, double v2, double v3) {
        leftFront.setPower(v);
        leftRear.setPower(v1);
        rightRear.setPower(v2);
        rightFront.setPower(v3);
    }

    @Override
    public double getRawExternalHeading() {
        return imu.getAngularOrientation().firstAngle;
    }

    @Override
    public Double getExternalHeadingVelocity() {
        // TODO: This must be changed to match your configuration
        //                           | Z axis
        //                           |
        //     (Motor Port Side)     |   / X axis
        //                       ____|__/____
        //          Y axis     / *   | /    /|   (IO Side)
        //          _________ /______|/    //      I2C
        //                   /___________ //     Digital
        //                  |____________|/      Analog
        //
        //                 (Servo Port Side)
        //
        // The positive x axis points toward the USB port(s)
        //
        // Adjust the axis rotation rate as necessary
        // Rotate about the z axis is the default assuming your REV Hub/Control Hub is laying
        // flat on a surface

        return (double) imu.getAngularVelocity().zRotationRate;
    }

    // THE NEXT TWO METHODS ARE USED FOR TELEOP DRIVING THE ROBOT. THEY ARE ARE ADDITIONS

    /**
     * Calculate motor powers for driving in teleop using a joystick (x and y) that controls the direction of
     * movement of the robot (translation) and a joystick (x) that controls the heading of the robot.
     * The movement is relative to the driver or field (field centric or driver centric), not relative
     * to the robot.
     * @param translationJoystickYValue
     * @param translationJoystickXValue
     * @param rotationJoystickXValue
     */
    public void calculateMotorCommandsFieldCentric(double translationJoystickYValue, double translationJoystickXValue, double rotationJoystickXValue) {
        // Read pose
        Pose2d poseEstimate = getPoseEstimate();

        // Create a vector from the gamepad x/y inputs
        // Then, rotate that vector by the inverse of that heading
        Vector2d input = new Vector2d(
                -translationJoystickYValue,
                -translationJoystickXValue
        ).rotated(-poseEstimate.getHeading());

        // Pass in the rotated input + right stick value for rotation
        // Rotation is not part of the rotated input thus must be passed in separately
        setWeightedDrivePower(
                new Pose2d(
                        input.getX(),
                        input.getY(),
                        -rotationJoystickXValue
                )
        );
        //NOTE that the teleop or other calling code must call the drive update() method.
    }

    /**
     * Calculate motor powers for driving in teleop using a joystick (x and y) that controls the direction of
     * movement of the robot (translation) and a joystick (x) that controls the heading of the robot.
     * The movement is relative to the robot.
     * @param translationJoystickYValue
     * @param translationJoystickXValue
     * @param rotationJoystickXValue
     */
    public void calculateMotorCommandsRobotCentric(double translationJoystickYValue, double translationJoystickXValue, double rotationJoystickXValue) {
        setWeightedDrivePower(
                new Pose2d(
                        -translationJoystickYValue,
                        translationJoystickXValue,
                        -rotationJoystickXValue
                )
        );
        //NOTE that the teleop or other calling code must call the drive update() method.
    }

    //our methods are below here
    @Override
    public String getName() {
        return MECANUM_DRIVE_NAME;
    }

    @Override
    public boolean isInitComplete() {
        logCommand("Init Complete");
        return true;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
    }

    @Override
    public boolean init(Configuration config) {
        logCommand("Init starting");
        return true;
    }

    
}
