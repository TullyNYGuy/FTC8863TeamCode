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
        // if position error is less than this, then the turntable is on target - in degrees
        final double POSITION_ERROR_LIMIT_FOR_ON_TARGET = 5;
        boolean onTarget = false;
        if (Math.abs(positionError) < 5) {
            onTarget = true;
        }
        return onTarget;
    }

    public void displayIsOnTarget(Telemetry telemetry){
        if (isOnTarget()) {
            telemetry.addData("Shooter ON target", "");
        } else {
            telemetry.addData("Shooter NOT on target", "");
        }
    }

    public void displayTargettingMethod(Telemetry telemetry) {
        if (isAprilTagAcquired()) {
            telemetry.addData("APRILTAG targetting", "");
        } else {
            telemetry.addData("ROBOT POSE targetting", "");
        }
    }

    public void displayShooterAngleToTarget(Telemetry telemetry) {
        telemetry.addData("Shooter angle to target ", shooterAngleToTarget);
    }

    public void joystickControlShooter(double joystickValue) {
        shooterAngleToTarget = joystickValue * -MAX_TURNTABLE_ANGLE;
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
            robotRangeToTarget = goal.getRangeToTarget(robotPose, DistanceUnit.INCH);
            shooterAngleToTarget = ShooterAngleCalculator.getShooterAngleToTarget(robotBearingToTarget, AngleUnit.DEGREES);
            //logComment("shooter angle to target " + shooterAngleToTarget);
            //logComment("Pinpoint Control");

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

        // if the apriltag is in view control the turntable with its feedback
        // If not, then continue turning to the target using either joystick control or shooter bearing to target

        turntableAngle = turntableMotor.getPositionInTermsOfAttachment();
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
                }  else {
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
            if (Math.abs(shooterAngleToTarget) < MAX_TURNTABLE_ANGLE - 5) {
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


        // set the position and run the PID control
        //logComment("target position " + targetPosition);
        //logComment("actual Position " + actualPosition);

        positionError = targetPosition - actualPosition;
        // if the shooter error is less than +/- some number of degrees, indicate it is ok to fire
        // Note that there are two ways to home in on the goal, the april tag and just the calculation of the
        // shooter bearing to target. Either way, if the error is low, tell the drivers to shoot
        if (isOnTarget() ) {
            indicator.setColor(DecodeRGBIndicator.IndicatorColor.GREEN);
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
