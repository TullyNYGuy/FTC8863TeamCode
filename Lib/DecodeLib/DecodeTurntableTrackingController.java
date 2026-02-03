package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
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
        JOYSTICK_CONTROL,
        PINPOINT_CONTROL
    }

    public ControlMode controlMode = ControlMode.JOYSTICK_CONTROL;

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

    public void setShooterAngleToTarget(double shooterAngleToTarget, AngleUnit unit) {
        this.shooterAngleToTarget = unit.toDegrees(shooterAngleToTarget);
    }

    final double TARGET_POSITION_TO_APRILTAG = 0;

    /**
     * The maximum turntable rotation so that wiring does not get tangled up
     */
    final double MAX_MOTOR_POSITION = 70; // in degrees
    double targetPositionCenter = 0;
    double targetPositionCCW = 45;
    double targetPositionCW = -45;
    double targetPosition = 0;

    double positionError = 0;
    double actualPosition = 0;

    boolean aprilTagAcquired;

    private DataLogOnChange logCommandOnchange;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommentOnChange;
    private DataLogOnChange logAprilTagAcquiredOnChange;
    DecodeTurntableMotor turntableMotor;
    DecodeLimelight limelight;
    DecodeRGBIndicator indicator;
    DecodeIMU imu;

    DecodePinpointDrive pinpointDrive;

    DecodeTarget goal;

    public void setGoal(DecodeTarget goal) {
        this.goal = goal;
    }

    Pose2D robotPose;

    double robotBearingToTarget;
    double robotRangeToTarget;

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
        this.goal = goal;
        this.indicator = indicator;
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

    private void logAprilTagAcquired(String comment) {
        if (loggingOn && logFile != null) {
            logAprilTagAcquiredOnChange.log(getName() + " " + comment);
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
        // if position error is less than this, then the turntable is on target - in degrees
        final double POSITION_ERROR_LIMIT_FOR_ON_TARGET = 5;
        boolean onTarget = false;
        if (Math.abs(positionError) < 5) {
            onTarget = true;
        }
        return onTarget;
    }

    public void joystickControlShooter(double joystickValue) {
        shooterAngleToTarget = joystickValue * -MAX_MOTOR_POSITION;
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
        // the rough aiming of the turntable can be from either the robot position on the field or from a driver
        // controlled joystick
        if (controlMode == ControlMode.PINPOINT_CONTROL) {
            // get the robot position and heading
            robotPose = pinpointDrive.pinpoint.getPosition();
            // calculate the bearing of the robot (from the intake point of view) to the goal
            robotBearingToTarget = goal.getBearingToTarget(robotPose, AngleUnit.DEGREES);
            // calculate the shooter bearing to the goal
            shooterAngleToTarget = ShooterAngleCalculator.getShooterAngleToTarget(robotBearingToTarget, AngleUnit.DEGREES);
            logComment("Pinpoint Control");

        } else {
            logComment("Joystick Control");
            // logComment("Shooter Angle To Target: " + shooterAngleToTarget);
        }

        // get limelight data
        LLResult result = limelight.limelight.getLatestResult();
        if (result != null && result.isValid()) {
            double tx = result.getTx(); // How far left or right the target is (degrees)
            double ty = result.getTy(); // How far up or down the target is (degrees)
            double ta = result.getTa(); // How big the target looks (0%-100% of the image)
            aprilTagAcquired = true;
            logAprilTagAcquired("April Tag Acquired");

            // fast blinking indicates that the april tag is seen, but maybe not homed in yet
            indicator.setMode(DecodeRGBIndicator.Mode.BLINKING);
            indicator.setFrequency(6);
        } else {
            aprilTagAcquired = false;
            logAprilTagAcquired("April Tag NOT Acquired");

            // slow blinking indicates that the april tag is not seen
            indicator.setMode(DecodeRGBIndicator.Mode.BLINKING);
            indicator.setFrequency(3);
            //logComment(robotPose.toString());
        }

        // if the apriltag is in view control the turntable with its feedback
        // If not, then continue turning to the target using either joystick control or shooter bearing to target
        if (aprilTagAcquired) {
            targetPosition = TARGET_POSITION_TO_APRILTAG;
            actualPosition = result.getTx();
        } else {
            targetPosition = Range.clip(shooterAngleToTarget, -MAX_MOTOR_POSITION, +MAX_MOTOR_POSITION);
            actualPosition = turntableMotor.getPositionInTermsOfAttachment();
        }

        // set the position and run the PID control
        //logComment("target position " + targetPosition);
        //logComment("actual Position " + actualPosition);
        turntableMotor.setTargetPosition(targetPosition);
        turntableMotor.updateWithPosition(actualPosition);

        positionError = targetPosition - actualPosition;
        // if the shooter error is less than +/- some number of degrees, indicate it is ok to fire
        // Note that there are two ways to home in on the goal, the april tag and just the calculation of the
        // shooter bearing to target. Either way, if the error is low, tell the drivers to shoot
        if (isOnTarget() ) {
            indicator.setMode(DecodeRGBIndicator.Mode.SOLID);
        }
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
