package org.firstinspires.ftc.teamcode.Lib.DecodeLib;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
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
    final double MAX_MOTOR_POSITION = 45; // in degrees
    double targetPositionCenter = 0;
    double targetPositionCCW = 45;
    double targetPositionCW = -45;
    double targetPosition = 0;

    double positionError = 0;
    double actualPosition = 0;

    boolean aprilTagAcquired;
    DecodeTurntableMotor turntableMotor;
    DecodeLimelight limelight;
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
                                             DecodePinpointDrive pinpointDrive) {
        this.limelight = limelight;
        this.turntableMotor = turntableMotor;
        this.imu = imu;
        this.pinpointDrive = pinpointDrive;
        this.goal = goal;
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

    public void joystickControlShooter(double joystickValue) {
        shooterAngleToTarget = joystickValue * -MAX_MOTOR_POSITION;
    }

    //*********************************************************************************************
    //          METHODS needed to implement FTCRobotSubsystem
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    @Override
    public void update() {
        if (controlMode == ControlMode.PINPOINT_CONTROL) {
            robotPose = pinpointDrive.pinpoint.getPosition();
            robotBearingToTarget = goal.getBearingToTarget(robotPose, AngleUnit.DEGREES);
            shooterAngleToTarget = ShooterAngleCalculator.getShooterAngleToTarget(robotBearingToTarget, AngleUnit.DEGREES);
        }
        // get limelight data
        LLResult result = limelight.limelight.getLatestResult();
        if (result != null && result.isValid()) {
            double tx = result.getTx(); // How far left or right the target is (degrees)
            double ty = result.getTy(); // How far up or down the target is (degrees)
            double ta = result.getTa(); // How big the target looks (0%-100% of the image)
            aprilTagAcquired = true;

        } else {
            aprilTagAcquired = false;
        }

        // if the apriltag is in view control the turntable with its feedback
        // If not, then continue turning to the target set by the gamepad buttons
        if (aprilTagAcquired) {
            targetPosition = TARGET_POSITION_TO_APRILTAG;
            actualPosition = result.getTx();
        } else {
            targetPosition = Range.clip(shooterAngleToTarget, -MAX_MOTOR_POSITION, +MAX_MOTOR_POSITION);
            actualPosition = turntableMotor.getPositionInTermsOfAttachment();
        }

// set the position and run the PID control
        turntableMotor.setTargetPosition(targetPosition);
        turntableMotor.updateWithPosition(actualPosition);

        positionError = targetPosition - actualPosition;
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
