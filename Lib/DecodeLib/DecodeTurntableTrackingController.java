package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class DecodeTurntableTrackingController implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum ControlMode {
        JOYSTICK_POSITION_CONTROL_WITH_LIMELIGHT,
        //JOYSTICK_POSTIION_CONTROL_ONLY,
        //JOYSTICK_VELOCITY_CONTROL,
        PINPOINT_CONTROL_WITH_LIMELIGHT_PRIOITY,
        ROTATE_TO_FIXED_ANGLE
        ;
    }

    public ControlMode controlMode = ControlMode.JOYSTICK_POSITION_CONTROL_WITH_LIMELIGHT;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************
    private DecodeGamepad gamepad;

    public void setGamepad(DecodeGamepad gamepad) {
        this.gamepad = gamepad;
    }
    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods For Implementing FTCRobotSubsystem
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    /**
     * Property that holds a log file
     */
    private DataLogging logFile;

    @Override
    public void setDataLog(DataLogging logFile) {
        logCommandOnchange = new DataLogOnChange(logFile);
        logAprilTagAcquiredOnChange = new DataLogOnChange(logFile);
        logCommentOnChange = new DataLogOnChange(logFile);
        logTurntableShouldBeLockedOnChange = new DataLogOnChange(logFile);
        this.logFile = logFile;
    }

    /**
     * Property that holds whether data is being logged into the log file.
     */
    private boolean loggingOn = false;

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    private String subsystemName;

    double shooterAngleToTarget = 0;

    public double getShooterAngleToTarget() {
        return shooterAngleToTarget;
    }

    public void setShooterAngleToTarget(double shooterAngleToTarget, AngleUnit unit) {
        this.shooterAngleToTarget = unit.toDegrees(shooterAngleToTarget);
    }

    final double TARGET_POSITION_TO_APRILTAG = 0;

    /**
     * The maximum turntable rotation so that wiring does not get tangled up
     */
    final double MAX_TURNTABLE_ANGLE = 70; // in degrees
    double targetPositionCenter = 0;
    double targetPositionCCW = 45;
    double targetPositionCW = -45;
    double targetPosition = 0;

    double positionError = 0;
    double actualPosition = 0;

    public double getActualPosition() {
        return actualPosition;
    }

    boolean aprilTagAcquired;

    public boolean isAprilTagAcquired() {
        return aprilTagAcquired;
    }

    private DataLogOnChange logCommandOnchange;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommentOnChange;
    private DataLogOnChange logTurntableShouldBeLockedOnChange;
    private DataLogOnChange logAprilTagAcquiredOnChange;
    DecodeTurntableMotor turntableMotor;
    DecodeLimelight limelight;
    DecodeRGBIndicator indicator;
    DecodeIMU imu;

    DecodePinpointDrive pinpointDrive;
    private ElapsedTime timer;

    DecodeTarget goal;

    public void setGoal(DecodeTarget goal) {
        this.goal = goal;
    }

    Pose2D robotPose;

    double robotBearingToTarget;
    double robotRangeToTarget;

    public double getRobotRangeToTarget() {
        return robotRangeToTarget;
    }
    private double turntableAngle = 0;
    private double robotHeadingVelocity = 0;
    private double lastTx = 0;
    
    private boolean shouldTurnTableBeLocked = false;
    private boolean isTurnTableLocked = false;

    private double requestedTurntableAngleFromLimelight = 0;

    private double requestedTurntableAngle = 0;

    /**
     * Set fixed angle that the turntable should hold. There is no tracking of the apriltag.
     * @param requestedTurntableAngle
     */
    public void setRequestedTurntableAngle(double requestedTurntableAngle) {
        this.requestedTurntableAngle = requestedTurntableAngle;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public DecodeTurntableTrackingController(HardwareMap hardwareMap, Telemetry telemetry,
                                             DecodeLimelight limelight,
                                             DecodeTurntableMotor turntableMotor,
                                             DecodeIMU imu,
                                             DecodePinpointDrive pinpointDrive,
                                             DecodeRGBIndicator indicator) {
        this.limelight = limelight;
        this.turntableMotor = turntableMotor;
        // PIDF control requires RUN_WITHOUT_ENCODER
        this.turntableMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.imu = imu;
        this.pinpointDrive = pinpointDrive;
        this.indicator = indicator;
        timer = new ElapsedTime();
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
//    private void logState() {
//        if (loggingOn && logFile != null) {
//            logStateOnChange.log(getName() + " state = " + currentState.toString());
//        }
//    }
//
//    private void logCommand() {
//        if (loggingOn && logFile != null) {
//            logCommandOnchange.log(getName() + " command = " + currentCommand);
//        }
//    }

    private void logComment(String comment) {
        if (loggingOn && logFile != null) {
            logCommentOnChange.log(getName() + " " + comment);
        }
    }

    private void logTurntableLockShouldBeLocked(String comment) {
        if (loggingOn && logFile != null) {
            logTurntableShouldBeLockedOnChange.log(getName() + " Turntable should be locked: " + comment);
        }
    }

    private void logAprilTagAcquired(String comment) {
        if (loggingOn && logFile != null) {
            logAprilTagAcquiredOnChange.log(getName() + " " + comment);
        }
    }

    private void startAprilTagSeenTimer() {
        timer.reset();
    }

    private boolean wasAprilTagSeenRecently() {
        if (timer.milliseconds() < 200) {
            return true;
        } else {
            return false;
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public void setPipelineNumber(DecodeShotDistance.ShotType distance) {
        if (AllianceColorTeamLocation.getAllianceColor() == Color.RED) {
            if (distance == DecodeShotDistance.ShotType.LONG) {
                limelight.limelight.pipelineSwitch(0);
            }
        }
        if (AllianceColorTeamLocation.getAllianceColor() == Color.RED) {
            if (distance == DecodeShotDistance.ShotType.SHORT || distance == DecodeShotDistance.ShotType.MEDIUM) {
                limelight.limelight.pipelineSwitch(1);
            }

        }
        if (AllianceColorTeamLocation.getAllianceColor() == Color.BLUE) {
            if (distance == DecodeShotDistance.ShotType.LONG) {
                limelight.limelight.pipelineSwitch(2);
            }
        }
        if (AllianceColorTeamLocation.getAllianceColor() == Color.BLUE) {
            if (distance == DecodeShotDistance.ShotType.SHORT || distance == DecodeShotDistance.ShotType.MEDIUM) {
                limelight.limelight.pipelineSwitch(3);
            }
        }
    }

    public boolean isOnTarget() {
        boolean onTarget = false;
        final double POSITION_ERROR_LIMIT_FOR_ON_TARGET = 5;

        switch (controlMode) {
            case JOYSTICK_POSITION_CONTROL_WITH_LIMELIGHT:
                if (aprilTagAcquired) {
                    onTarget = true;
                } else {
                    onTarget = false;
                }
                break;
            case PINPOINT_CONTROL_WITH_LIMELIGHT_PRIOITY:
                // if position error is less than this, then the turntable is on target - in degrees
                if (Math.abs(positionError) < 5) {
                    onTarget = true;
                }
                break;
        }

        return onTarget;
    }

    public void displayIsOnTarget(Telemetry telemetry){
        if (isOnTarget()) {
            telemetry.addData("Shooter ON target!", "");
        } else {
            telemetry.addData("Shooter NOT on target!", "");
        }
    }

    public void displayTargettingMethod(Telemetry telemetry) {
        if (isAprilTagAcquired()) {
            telemetry.addData("APRILTAG targetting!", "");
        } else {
            telemetry.addData("ROBOT POSE targetting!", "");
        }
    }
    
    public void setMode(ControlMode mode) {
        this.controlMode = mode;
    }

    public void displayShooterAngleToTarget(Telemetry telemetry) {
        telemetry.addData("Shooter angle to target ", shooterAngleToTarget);
    }

    /**
     * Use the joystick value as the requested angle for the turntable. This is position control!
     * The joystick center = turntable straight ahead
     * @return
     */
    public double getTurnTableAngleFromJoystick() {
        // - is due to rotation of turntable opposite of joystick control (left on joystick is -, left on turntable is +)
        return gamepad.gamepad2LeftJoyStickXValue * -MAX_TURNTABLE_ANGLE;
    }

    /**
     * Use the joystick value as the requested angle for the turntable. This is position control!
     * The joystick center = turntable straight ahead
     * @param joystickValue
     * @return
     */
    public double joystickPositionControlShooter(double joystickValue) {
        return gamepad.gamepad2LeftJoyStickXValue * -MAX_TURNTABLE_ANGLE;
    }

    public void start(int pollRateInHz) {
        limelight.start(pollRateInHz);
    }

    //*********************************************************************************************
    //          METHODS needed to implement FTCRobotSubsystem
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    @Override
    public void update() {
        if (gamepad == null && controlMode == ControlMode.JOYSTICK_POSITION_CONTROL_WITH_LIMELIGHT) {
            // without access to the joystick to control the position, we have to switch to a mode that will work
            controlMode = ControlMode.ROTATE_TO_FIXED_ANGLE;
        }

        // get the robot position and heading
        robotPose = pinpointDrive.pinpoint.getPosition();
        // calculate the bearing of the robot (from the intake point of view) to the goal
        robotBearingToTarget = goal.getBearingToTarget(robotPose, AngleUnit.DEGREES);
        robotRangeToTarget = goal.getRangeToTarget(robotPose, DistanceUnit.INCH);
        // calculate the shooter bearing to the goal
        shooterAngleToTarget = ShooterAngleCalculator.getShooterAngleToTarget(robotBearingToTarget, AngleUnit.DEGREES);

        turntableAngle = turntableMotor.getPositionInTermsOfAttachment();

        // get limelight data
        LLResult result = limelight.limelight.getLatestResult();
        if (result != null && result.isValid()) {
            double tx = result.getTx(); // How far left or right the target is (degrees)
            double ty = result.getTy(); // How far up or down the target is (degrees)
            double ta = result.getTa(); // How big the target looks (0%-100% of the image)

            aprilTagAcquired = true;
            logAprilTagAcquired("April Tag Acquired");

            // calculate the turntable angle if the limelight were to home in on the apriltag and drive the error
            // to 0.
            // This will be used in determining if the limelight will cause the turntable angle to increase past the max or
            // decrease the turntable angle below the max.
            requestedTurntableAngleFromLimelight = turntableAngle + tx;

            // fast blinking indicates that the april tag is seen, but maybe not homed in yet
            //indicator.setMode(DecodeRGBIndicator.Mode.BLINKING);
            //indicator.setFrequency(6);
        } else {
            aprilTagAcquired = false;
            logAprilTagAcquired("April Tag NOT Acquired");

            // slow blinking indicates that the april tag is not seen
            // blinking does not seem to work well
            //indicator.setMode(DecodeRGBIndicator.Mode.BLINKING);
            //indicator.setFrequency(3);
            //logComment(robotPose.toString());
        }

        switch (controlMode) {
            case JOYSTICK_POSITION_CONTROL_WITH_LIMELIGHT:
                // check for turntable angle exceeding the max
                if (aprilTagAcquired) {
                    // check if the turntable is exceeding its max angle.
                    // When the apriltag is acquired, we are controlling the angle between the apriltag
                    // and the turntable heading, not the absolute turntable angle. We need to feed the 
                    // turntable lock checker a desired angle. The only absolute angle available is the shooterAngleToTarget.
                    if (shouldTurntableBeLocked(requestedTurntableAngleFromLimelight)) {
                        // the turntable should be locked. Turn off the motor.
                        lockTurntable();
                    } else {
                        // make sure the turntable lock check works properly next loop cycle
                        isTurnTableLocked = false;
                        // use the limelight to control the turntable angle
                        actualPosition = result.getTx();
                        targetPosition = 0;
                        turntableMotor.setTargetPosition(targetPosition);
                        turntableMotor.updateWithPosition(actualPosition);
                    }
                } else {
                    // april tag is not acquired. Use the joystick to control the turntable angle
                    // Note that joystick in the center means shooter pointed straight ahead of robot
                    double requestedTurntableAngle = getTurnTableAngleFromJoystick();
                    // check if the turntable exceeds its max angle. If it is already locked, then the
                    // checking method needs to know the requested angle to see if the turntable can be
                    // unlocked.
                    if (shouldTurntableBeLocked(requestedTurntableAngle)) {
                        lockTurntable();
                    } else {
                        // make sure the turntable lock check works properly next loop cycle
                        isTurnTableLocked = false;
                        // setup the controller using the joystick as the requested angle and run the controller
                        // Note that if the turntable angle is near the max and the limelight loses the apriltag,
                        // the turntable will snap around to angle = 0 because the joystick is likely commanding 0
                        // degrees.
                        actualPosition = turntableAngle;
                        targetPosition = requestedTurntableAngle;
                        turntableMotor.setTargetPosition(targetPosition);
                        turntableMotor.updateWithPosition(actualPosition);
                    }
                }
                break;
            case PINPOINT_CONTROL_WITH_LIMELIGHT_PRIOITY:
                if (Math.abs(turntableAngle) < MAX_TURNTABLE_ANGLE) {
                    // There can be an oscillation if the turntable sees an apriltag and then starts rapidly moving towards it.
                    // The motion blur can cause the april tag to be lost and then the control would be done by the calculated
                    // shooter angle to target. Instead of immediately switching, let the turntable continue to move to the point
                    // where it was headed when it saw the april tag. Give it some time to find the tag again.
                    if (aprilTagAcquired || wasAprilTagSeenRecently()) {
                        if (aprilTagAcquired) {
                            // we see an april tag, reset the timer that tracks how long ago we saw an april tag
                            startAprilTagSeenTimer();
                            actualPosition = result.getTx();
                            // save the actual position in case we lose the april tag temporarily
                            lastTx = actualPosition;
                        } else {
                            // we lost the april tag but are within the time allowed to find it. Use the last
                            // known Tx as the actual position
                            actualPosition = lastTx;
                        }
                        targetPosition = TARGET_POSITION_TO_APRILTAG;
                    } else {
                        // the april tag is not seen or was seen too long ago to be valid. Use the shooter angle to
                        // target to control the turntable
                        targetPosition = shooterAngleToTarget;
                        actualPosition = turntableAngle;
                    }
                    turntableMotor.setTargetPosition(targetPosition);
                    turntableMotor.updateWithPosition(actualPosition);
                } else {
                    // turntable is over the max rotation limit
                    // Setting the motor power to 0 to stop the rotation results in the turntable stuck at the limit.
                    // We have to check to see if the shooter angle to the target gets less than the rotation limit.
                    // If it does, then turn the PID back on again. To avoid an oscillation between the shooter angle to target
                    // and the max turntable angle, allow movement again when the shooter angle to target is a bit less than
                    // the max turn table angle.
                    if (Math.abs(shooterAngleToTarget) < MAX_TURNTABLE_ANGLE - 10) {
                        // The shooter angle to the target is less than the max turntable angle, run the PID again
                        if (aprilTagAcquired) {
                            targetPosition = TARGET_POSITION_TO_APRILTAG;
                            actualPosition = result.getTx();
                        } else {
                            targetPosition = shooterAngleToTarget;
                            actualPosition = turntableAngle;
                        }
                        turntableMotor.setTargetPosition(targetPosition);
                        turntableMotor.updateWithPosition(actualPosition);
                    } else {
                        // the shooter angle to target would force the turntable angle larger than the max. Turn the motor off.
                        turntableMotor.setPower(0);
                        actualPosition = turntableAngle;
                    }

                }
                break;
            case ROTATE_TO_FIXED_ANGLE:
                targetPosition = requestedTurntableAngle;
                actualPosition = turntableAngle;
                turntableMotor.setTargetPosition(targetPosition);
                turntableMotor.updateWithPosition(actualPosition);
                break;
        }
    }



//        // limit the turntable movement to a max angle. Use the calculated shooter bearing to target to control the limit.
//        if (Math.abs(shooterAngleToTarget) < MAX_TURNTABLE_ANGLE) {
//            // shooter is not at the limit of its rotation
//            if (aprilTagAcquired) {
//                // use the limelight to control the turntable angle
//                targetPosition = TARGET_POSITION_TO_APRILTAG;
//                actualPosition = result.getTx();
//            } else {
//                // april tag is not seen, use the calculated shooter angle to control the turntable
//                targetPosition = shooterAngleToTarget;
//                actualPosition = turntableMotor.getPositionInTermsOfAttachment();
//            }
//        } else {
//            // the turntable is over the max. Shut the motor down until the robot moves to a pose that results in an
//            // angle to the target that is less than the limit.
//            turntableMotor.setPower(0);
//        }

    
    private boolean shouldTurntableBeLocked(double requestedTurnTableAngle) {
        // turntable angle more than max and request is increasing the angle
        if ((Math.abs(turntableAngle) > MAX_TURNTABLE_ANGLE) && Math.abs(requestedTurnTableAngle) > MAX_TURNTABLE_ANGLE - 5){
            shouldTurnTableBeLocked = true;
        }
        // turntable angle more than max and request is decreasing the angle
        if ((Math.abs(turntableAngle) > MAX_TURNTABLE_ANGLE) && Math.abs(requestedTurnTableAngle) < MAX_TURNTABLE_ANGLE - 5) {
            shouldTurnTableBeLocked = false;
        }
        // turntable angle less than max and request is increasing the angle
        if ((Math.abs(turntableAngle) < MAX_TURNTABLE_ANGLE) && Math.abs(requestedTurnTableAngle) > MAX_TURNTABLE_ANGLE - 5) {
            shouldTurnTableBeLocked = false;
        }
        // turntable angle less than max and request is decreasing the angle
        if ((Math.abs(turntableAngle) < MAX_TURNTABLE_ANGLE) && Math.abs(requestedTurnTableAngle) < MAX_TURNTABLE_ANGLE - 5) {
            shouldTurnTableBeLocked = false;
        }
//        if (isTurnTableLocked) {
//            // see if the turntable can be unlocked. This is done if the requested angle is significantly smaller
//            // than the MAX_TURNTABLE_ANGLE. That makes sure that there is no oscillation as the turntable comes
//            // off lock. If the unlock occured at just a hair under the MAX_TURNTABLE_ANGLE, then it could
//            // immediately lock again the next cycle due to noise in the angle measurement.
//            if (Math.abs(requestedTurnTableAngle) < MAX_TURNTABLE_ANGLE - 5) {
//                shouldTurnTableBeLocked = false;
//            }
//        } else {
//            // turntable is not currently locked.
//            // see if the turntable needs to be locked
//            if (Math.abs(turntableAngle) > MAX_TURNTABLE_ANGLE) {
//                // turntable angle is more than max
//                shouldTurnTableBeLocked = true;
//            }
//        }
        logTurntableLockShouldBeLocked(Boolean.toString(shouldTurnTableBeLocked));
        return shouldTurnTableBeLocked;
    }

    private void lockTurntable() {
        // run the PIDF so that the turntable angle is limited to the max. This is needed since the turntable
        // can overshoot the max before the robot shuts the motor down. If you just shut the motor down, the turntable
        // angle might be over the max. Rather than that, run a PIDF to force the angle to the max.
//        targetPosition = MAX_TURNTABLE_ANGLE;
//        actualPosition = turntableAngle;
//        turntableMotor.setTargetPosition(targetPosition);
//        turntableMotor.updateWithPosition(actualPosition);
        turntableMotor.setPower(0);
        isTurnTableLocked = true;
    }

    @Override
    public String getName() {
        return subsystemName;
    }

    @Override
    public boolean isInitComplete() {
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
        return true;
    }
}
