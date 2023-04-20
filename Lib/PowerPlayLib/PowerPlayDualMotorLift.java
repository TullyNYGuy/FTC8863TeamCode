package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.EXTENSION_PID_COEFFICENTS;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAXIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_ACCELERATION_EXTENSION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_ACCELERATION_RETRACTION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_VELOCITY_EXTENSION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.EXTENSION_PID_COEFFICENTS;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_VELOCITY_RETRACTION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MINIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MOVEMENT_PER_REVOLUTION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.getKg;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kAExtension;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kStatic;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kVExtension;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDCoefficients;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDFController;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863Interface;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DualMotorGearbox;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanismGenericMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotionProfileFollower;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotorConstants;

public class PowerPlayDualMotorLift implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum LiftState {
        READY,

        // INIT STATES
        PRE_INIT,
        INIT_RAISE_LIFT,
        WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE,
        WAITING_FOR_EXTENSION_ARM_HOME_POSITION_REACHED,
        INIT_COMPLETE,

        // Moving to states
        MOVING_TO_HIGH,
        MOVING_TO_LOOK_AT_HIGH,
        MOVING_TO_MEDIUM,
        MOVING_TO_LOW,
        MOVING_TO_GROUND,
        MOVING_TO_PICKUP,
        DROPPING_ON_POLE,
        MOVING_UP_ONE_INCH,

        // retraction states
        RETRACTING
    }
    private LiftState liftState = LiftState.PRE_INIT;

    private enum Phase {
        TELEOP,
        AUTONOMOUS
    }
    private Phase phase = Phase.TELEOP;

    public enum LiftLocation{
        INIT,
        PICKUP,
        GROUND,
        LOW,
        MEDIUM,
        LOOK_AT_HIGH,
        HIGH,
        DROP,
        UP_ONE_INCH,
        IN_BETWEEN
    }
    private LiftLocation liftLocation;

    public LiftLocation getLiftLocation() {
        return liftLocation;
    }

    // so we can remember which extend command was given
    private enum ExtendCommand {
        NONE,
        MOVE_TO_HIGH,
        MOVE_TO_MEDIUM,
        MOVE_TO_LOW,
        MOVE_TO_GROUND,
        MOVE_TO_PICKUP,
        RETRACT
    }
    private ExtendCommand extendCommand = ExtendCommand.NONE;

    private boolean commandComplete = true;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private ExtensionRetractionMechanismGenericMotor lift;
    private DcMotor8863Interface liftMotor;

    private PIDFController extensionMotionController;
    private PIDCoefficients extensionPidCoefficients;
    private MotionProfileFollower extensionFollower;

    private PIDFController retractionMotionController;
    private PIDCoefficients retractionPidCoefficients;
    private MotionProfileFollower retractionFollower;

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private ElapsedTime timer;
    private PowerPlayAllianceColor allianceColor;

    // flags used in this class

    // initialization is complete
    private boolean initComplete = false;
    // arm is fully retracted
    private boolean retractionComplete = true;

    //extension arm positions
    private double highPosition;
    private double lookAtHighPolePosition;
    private double mediumPosition;
    private double lowPosition;
    private double groundPosition;
    private double pickupPosition;
    private double initPosition;
    private double homePosition;
    private double initPower;
    private double extendPower;
    private double retractPower;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public PowerPlayDualMotorLift(HardwareMap hardwareMap, Telemetry telemetry) {

        // create the motor for the lift
        liftMotor = new DualMotorGearbox(
                PowerPlayRobot.HardwareName.LIFT_MOTOR_LEFT.hwName,
                PowerPlayRobot.HardwareName.LIFT_MOTOR_RIGHT.hwName,
                hardwareMap,
                telemetry,
                MotorConstants.MotorType.GOBILDA_1150);
        liftMotor.setRecordEncoderData(true);

        lift = new ExtensionRetractionMechanismGenericMotor(hardwareMap, telemetry,
                "lift",
                PowerPlayRobot.HardwareName.LIFT_LIMIT_SWITCH_EXTENSION.hwName,
                PowerPlayRobot.HardwareName.LIFT_LIMIT_SWITCH_RETRACTION.hwName,
                liftMotor,
                MOVEMENT_PER_REVOLUTION);

        lift.reverseMotorDirection();
        // Disable the extension and retraction limits because the lift has a good bit of overshoot
        // and it occasionally trips the limits. The DualMotorGearbox has no clue how to hold a
        // position so it can't stop the lift and hold at position. I have enough confidence in the
        // lift so that I will just depend on the PIDF controller to get the position right after
        // the overshoot.
        lift.disableExtensionAndRetractionLimits();
        lift.setResetTimerLimitInmSec(10000);

        //*********************************************
        // SET the lift positions here
        //*********************************************
        highPosition = 36.25;
        lookAtHighPolePosition = 24.0;
        mediumPosition = 25.25;
        lowPosition = 15.5;
        groundPosition = 3.0;
        pickupPosition = 1.0;
        initPosition = 0;
        homePosition = 0.5;

        //*********************************************
        // SET the lift powers here
        //*********************************************
        initPower = .2;
        extendPower = 1.0;
        retractPower = -1.0;
        lift.setExtensionPower(extendPower);
        lift.setRetractionPower(retractPower);

        //*********************************************
        // SET the lift max and min positions here
        //*********************************************
        lift.setExtensionPositionInMechanismUnits(MAXIMUM_LIFT_POSITION);
        lift.setRetractionPositionInMechanismUnits(MINIMUM_LIFT_POSITION);
        // probably not relevant anymore since the lift is using motion profiles rather than
        // RUN_TO_POSITION
        lift.setTargetEncoderTolerance(30);

        lift.setResetTimerLimitInmSec(5000);
        timer = new ElapsedTime();

        liftState = LiftState.PRE_INIT;
        // init has not been started yet
        initComplete = false;
        // the lift can be commanded to do something, like the init
        commandComplete = true;

        // create an extension PIDF Controller using the constants defined for the lift
        extensionPidCoefficients = LiftConstants.EXTENSION_PID_COEFFICENTS;
        extensionMotionController = new PIDFController(extensionPidCoefficients, kVExtension, kAExtension, kStatic, new PIDFController.FeedforwardFunction() {
            @Override
            public Double compute(double position, Double velocity) {
                return getKg(position);
            }
        });
        // limit the output to valid motor commands
        extensionMotionController.setOutputBounds(-1, 1);
        // create a follower for the profile and pass the PIF controller to it.
        extensionFollower = new MotionProfileFollower(extensionMotionController);
        extensionFollower.setCompletionTimeout(0.5);

        // create a retraction PIDF Controller using the constants defined for the lift
        retractionPidCoefficients = LiftConstants.RETRACTION_PID_COEFFICENTS;
        retractionMotionController = new PIDFController(retractionPidCoefficients, kVExtension, kAExtension, kStatic, new PIDFController.FeedforwardFunction() {
            @Override
            public Double compute(double position, Double velocity) {
                return getKg(position);
            }
        });
        // limit the output to valid motor commands
        retractionMotionController.setOutputBounds(-1, 1);
        // create a follower for the profile and pass the PIF controller to it.
        retractionFollower = new MotionProfileFollower(retractionMotionController);
        retractionFollower.setCompletionTimeout(0.5);
        retractionFollower.setTargetTolerance(0.5);

    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    public boolean isRetractionComplete() {
        return retractionComplete;
    }

    @Override
    public String getName() {
        return "lift";
    }

    @Override
    public void shutdown() {
        retract();
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
        lift.setDataLog(logFile);
    }

    @Override
    public void enableDataLogging() {
        enableLogging = true;
        lift.enableDataLogging();
    }

    @Override
    public void disableDataLogging() {
        enableLogging = false;
        lift.disableDataLogging();
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    private void logState() {
        if (enableLogging && logFile != null) {
            logStateOnChange.log(getName() + " state = " + liftState.toString());
        }
    }

    private void logCommand(String command) {
        if (enableLogging && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    /**
     * Creates a motion profile for the movement. The constants are different for extension and
     * retraction. Then the motion profile is set in the appropriate follower, either extension or
     * retraction. Finally that follower is returned to be used.
     * @param finishLocation
     * @return
     */
    private MotionProfileFollower getFollower(double finishLocation) {
        double startLocation = lift.getCurrentPosition();
        MotionProfile motionProfile;
        MotionProfileFollower follower;
        double max_velocity = 40;
        double max_acceleration = 80;

        // extending
        if (finishLocation >= startLocation) {
            // only run the lift fast if the arm is in the carry position
            if (PowerPlayPersistantStorage.getCurrentArmPosition() == PowerPlayConeGrabber.ArmPosition.CARRY) {
                max_velocity = MAX_VELOCITY_EXTENSION;
                max_acceleration = MAX_ACCELERATION_EXTENSION;
            }
             motionProfile = MotionProfileGenerator.generateSimpleMotionProfile(
                    new MotionState(startLocation, 0, 0, 0),
                    new MotionState(finishLocation, 0, 0, 0),
                     max_velocity,
                     max_acceleration);
             extensionFollower.setProfile(motionProfile, "extension");
             follower = extensionFollower;
        } else {
            // retracting
            // only run the lift fast if the arm is in the carry position
            if (PowerPlayPersistantStorage.getCurrentArmPosition() == PowerPlayConeGrabber.ArmPosition.CARRY) {
                max_velocity = MAX_VELOCITY_RETRACTION;
                max_acceleration = MAX_ACCELERATION_RETRACTION;
            }
            // if this is short retraction we don't need all the jerking on the system
            if ((startLocation - finishLocation) < 6.0) {
                //retracting - difference is max velocity and max acceleration
                motionProfile = MotionProfileGenerator.generateSimpleMotionProfile(
                        new MotionState(startLocation, 0, 0, 0),
                        new MotionState(finishLocation, 0, 0, 0),
                        max_velocity,
                        max_acceleration);
                retractionFollower.setProfile(motionProfile, "retraction");
                follower = retractionFollower;
            } else {
                // run the lift fast
                //retracting - difference is max velocity and max acceleration
                motionProfile = MotionProfileGenerator.generateSimpleMotionProfile(
                        new MotionState(startLocation, 0, 0, 0),
                        new MotionState(finishLocation, 0, 0, 0),
                        max_velocity,
                        max_acceleration);
                retractionFollower.setProfile(motionProfile, "retraction");
                follower = retractionFollower;
            }

        }
        return follower;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public String getLiftState() {
        return liftState.toString();
    }

    @Override
    public boolean init(Configuration config) {
        // start the init for the extension retraction mechanism
        logCommand("Init starting");
        // todo is init ok or does it cause a motor fight? I think the motors float so no.
        lift.init();
        liftState = LiftState.WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE;

        commandComplete = false;
        logCommand("Init");
        return false;
    }

    public boolean isInitComplete() {
        if (initComplete) {
            logCommand("Init complete");
        }
        return initComplete;
    }

    public boolean isPositionReached() {
        if (liftState == LiftState.READY) {
            return true;
        } else {
            return false;
        }
    }

    public void setPhaseAutonomous() {
        phase = Phase.AUTONOMOUS;
    }

    public void setPhaseTeleop() {
        phase = Phase.TELEOP;
    }

    //********************************************************************************
    // Public commands for controlling the lift
    //********************************************************************************

    public void moveToHigh() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to high");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.MOVING_TO_HIGH;
            // remember the command for later
            extendCommand = ExtendCommand.MOVE_TO_HIGH;
            logCommand(extendCommand.toString());
            //lift.goToPosition(highPosition, extendPower);
            lift.followProfile(getFollower(highPosition));
            liftLocation = LiftLocation.IN_BETWEEN;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("move to high command ignored, previous command is not complete");
        }
    }

    public void moveToLookAtHighPole() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to look at high pole");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.MOVING_TO_HIGH;
            // remember the command for later
            extendCommand = ExtendCommand.MOVE_TO_HIGH;
            logCommand(extendCommand.toString());
            //lift.goToPosition(highPosition, extendPower);
            lift.followProfile(getFollower(lookAtHighPolePosition));
            liftLocation = LiftLocation.IN_BETWEEN;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("move to high command ignored, previous command is not complete");
        }
    }

    public void moveToMedium() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to medium");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.MOVING_TO_MEDIUM;
            // remember the command for later
            extendCommand = ExtendCommand.MOVE_TO_MEDIUM;
            logCommand(extendCommand.toString());
            //lift.goToPosition(mediumPosition, extendPower);
            lift.followProfile(getFollower(mediumPosition));
            liftLocation = LiftLocation.IN_BETWEEN;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("move to medium command ignored, previous command is not complete");
        }
    }

    public void moveToLow() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to Low");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.MOVING_TO_LOW;
            // remember the command for later
            extendCommand = ExtendCommand.MOVE_TO_LOW;
            logCommand(extendCommand.toString());
            //lift.goToPosition(lowPosition, extendPower);
            lift.followProfile(getFollower(lowPosition));
            liftLocation = LiftLocation.IN_BETWEEN;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("move to low command ignored, previous command is not complete");
        }
    }

    public void moveToGround() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to ground");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.MOVING_TO_GROUND;
            // remember the command for later
            extendCommand = ExtendCommand.MOVE_TO_GROUND;
            logCommand(extendCommand.toString());
            //lift.goToPosition(groundPosition, extendPower);
            lift.followProfile(getFollower(groundPosition));
            liftLocation = LiftLocation.IN_BETWEEN;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("move to ground command ignored, previous command is not complete");
        }
    }

    public void moveToPickup() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Extend to pickup");
            retractionComplete = false;
            commandComplete = false;
            //command to start extension
            liftState = LiftState.MOVING_TO_PICKUP;
            // remember the command for later
            extendCommand = ExtendCommand.MOVE_TO_PICKUP;
            logCommand(extendCommand.toString());
            //lift.goToPosition(pickupPosition, extendPower);
            lift.followProfile(getFollower(pickupPosition));
            liftLocation = LiftLocation.IN_BETWEEN;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("move to pickup command ignored, previous command is not complete");
        }
    }

    public void droppingOnPole() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        double currentLiftPosition = lift.getCurrentPosition();
        if (commandComplete) {
            logCommand("test the drop");
            retractionComplete = false;
            commandComplete = false;
            //lift.goToPosition(currentLiftPosition - 4.0, retractPower);
            lift.followProfile(getFollower(currentLiftPosition - 4.0));
            liftLocation = LiftLocation.IN_BETWEEN;
            liftState = LiftState.DROPPING_ON_POLE;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("test the drop command is ignored, previous command is not complete");
        }
    }

    public void upOneInch() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        double currentLiftPosition = lift.getCurrentPosition();
        // the current positon + the requested 1 inch increase has to be less than the max extension of the lift
        if (commandComplete && (currentLiftPosition + 1) < MAXIMUM_LIFT_POSITION) {
            logCommand("up one inch");
            retractionComplete = false;
            commandComplete = false;
            lift.followProfile(getFollower(currentLiftPosition + 1.0));
            liftLocation = LiftLocation.IN_BETWEEN;
            liftState = LiftState.MOVING_UP_ONE_INCH;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("up once inch command is ignored, too high or other command active");
        }
    }


    /**
     * Retract the lift to the pickup position. currently only using in robot shutdown.
     */
    public void retract() {
        // lockout for double hits on a command button. Downside is that the driver better hit the
        // right button the first time or they are toast
        if (commandComplete) {
            logCommand("Retract");
            retractionComplete = false;
            commandComplete = false;
            liftState = LiftState.RETRACTING;
            extendCommand = ExtendCommand.RETRACT;
            logCommand(extendCommand.toString());
            //lift.goToPosition(pickupPosition, retractPower);
            lift.followProfile(getFollower(pickupPosition));
            liftLocation = LiftLocation.IN_BETWEEN;
        } else {
            // you can't start a new command when the old one is not finished
            logCommand("Retract command ignored");
        }
    }


    //********************************************************************************
    // Public commands for testing the lift
    //********************************************************************************

    public boolean isStateIdle() {
        //this is just for use in the freight system.
        if (liftState == LiftState.READY) {
            return true;
        } else {
            return false;
        }
    }

    // would have to use a profile due to motors fighting each other.
    // so no longer available as a method
//    public void extendToPosition(double position, double power) {
//        lift.goToPosition(position, power);
//    }

    public boolean isMovementComplete() {
        return lift.isMotionProfileComplete();
    }

    public boolean isCommandComplete() {
        return commandComplete;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////state machine//////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update() {
        lift.update();
        logState();
        switch (liftState) {
            //********************************************************************************
            // INIT states
            //********************************************************************************

            case PRE_INIT: {
                // unlock the commands so a new command will be acted upon
                commandComplete = true;
                // do nothing, waiting to get the init command
            }
            break;

            case INIT_RAISE_LIFT: {
                if (lift.isPositionReached()) {
                    lift.init();
                    liftState = LiftState.WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE;
                    liftLocation = LiftLocation.IN_BETWEEN;
                }
            }
            break;

            case WAITING_FOR_EXTENSION_RETRACTION_MECHANISM_INIT_TO_COMPLETE: {
                if (lift.isInitComplete()) {
                    // working around bug where we command the lift to 0 and it locks up
                    // instead just leave the lift at its init position
                    //lift.goToPosition(initPosition, initPower);
                    //liftState = LiftState.WAITING_FOR_EXTENSION_ARM_HOME_POSITION_REACHED;
                    liftState = LiftState.INIT_COMPLETE;
                    liftLocation = LiftLocation.INIT;
                }
            }
            break;

            case WAITING_FOR_EXTENSION_ARM_HOME_POSITION_REACHED: {
                if (lift.isPositionReached()) {
                    liftState = LiftState.INIT_COMPLETE;
                    commandComplete = true;
                    initComplete = true;
                }
            }
            break;

            case INIT_COMPLETE: {
                liftState = LiftState.INIT_COMPLETE;
                commandComplete = true;
                initComplete = true;
                // do nothing. The lift is waiting for a command
            }
            break;

            //********************************************************************************
            // Extend to high states
            //********************************************************************************

            case MOVING_TO_HIGH: {
                // if the lift hits the extension limit it is fully extended and at the high position
                if (lift.isMotionProfileComplete() || lift.isExtensionComplete()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                    liftLocation = LiftLocation.HIGH;
                }
            }
            break;

            case MOVING_TO_LOOK_AT_HIGH: {
                // if the lift hits the extension limit it is fully extended and at the high position
                if (lift.isMotionProfileComplete() || lift.isExtensionComplete()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                    liftLocation = LiftLocation.LOOK_AT_HIGH;
                }
            }
            break;

            //********************************************************************************
            // Extend to medium states
            //********************************************************************************

            case MOVING_TO_MEDIUM: {
                if (lift.isMotionProfileComplete()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                    liftLocation = LiftLocation.MEDIUM;
                }
            }
            break;

            //********************************************************************************
            // Extend to low states
            //********************************************************************************

            case MOVING_TO_LOW: {
                if (lift.isMotionProfileComplete() || lift.isRetractionComplete()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                    liftLocation = LiftLocation.LOW;
                }
            }
            break;

            //********************************************************************************
            // Extend to ground states
            //********************************************************************************

            // same as middle right now but may need to change later so make it separate from middle
            case MOVING_TO_GROUND: {
                if (lift.isMotionProfileComplete() || lift.isRetractionComplete()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                    liftLocation = LiftLocation.GROUND;
                }
            }
            break;

            //********************************************************************************
            // Extend to pickup states
            //********************************************************************************

            case MOVING_TO_PICKUP: {
                if (lift.isMotionProfileComplete() || lift.isRetractionComplete()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                    liftLocation = LiftLocation.PICKUP;
                }
            }
            break;

            case DROPPING_ON_POLE: {
                if (lift.isMotionProfileComplete()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                    liftLocation = LiftLocation.DROP;
                }
            }

            case MOVING_UP_ONE_INCH: {
                if (lift.isMotionProfileComplete()) {
                    commandComplete = true;
                    liftState = LiftState.READY;
                    liftLocation = LiftLocation.UP_ONE_INCH;
                }
            }
        }
    }
}
