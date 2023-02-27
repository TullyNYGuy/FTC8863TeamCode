package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest.LiftTuning;

import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAXIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_ACCELERATION_EXTENSION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_ACCELERATION_RETRACTION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_VELOCITY_EXTENSION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_VELOCITY_RETRACTION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MINIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MOVEMENT_PER_REVOLUTION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.getKg;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kAExtension;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kARetraction;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kGRetraction0ToMinus60;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kStatic;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kVExtension;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kVRetraction0ToMinus60;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863Interface;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DualMotorGearbox;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanismGenericMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotionProfileFollower;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotorConstants;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDCoefficients;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDFController;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;

/*
 * This routine is designed to help you find the open-loop feedfoward coefficients, kV and kG. It
 * will also get the maximum velocity for retraction.
 *
 * Like the other manual tuning routines, this op mode relies heavily upon the dashboard. To access
 * the dashboard, connect your computer to the RC's WiFi network. In your browser, navigate to
 * https://192.168.49.1:8080/dash if you're using the RC phone or https://192.168.43.1:8080/dash if
 * you are using the Control Hub.
 *
 * This routine applies
 *
 * Note that the data in this and all other feed forward tuning routines is heavily dependent on the
 * battery voltage. Try to use a battery that is at an "average charge". Like about 12.5V.
 *
 * You can only run this routing after you have tuned the extension, including the kP. It uses a
 * motion profile to extend the lift up and then hold it there before retracting.
 */
@Config
@Autonomous(group = "Lift Tuning")
public class LiftRetractionFeedforwardTest extends LinearOpMode {

    private enum PhaseOfOperation {
        RESET,
        WAIT,
        EXTENDING,
        EXTENSION_COMPLETE,
        PAST_EXTENSION_POSITION,
        AT_EXTENSION_LIMIT,
        RETRACTING,
        PAST_RETRACTION_POSITION,
        RETRACTION_COMPLETE,
        ALL_DONE
    }

    private PhaseOfOperation phaseOfOperation = PhaseOfOperation.RESET;

    // the starting point and finishing points for the movement
    public static double EXTENSION_FINISH_POSITION = 35; // in
    public static double EXTENSION_START_POSITION = 0; // in
    public static double RETRACTION_FINISH_POSITION = 10; // in

    // a wait time to allow the user to setup the FTC dashboard to graph the data. Better be quick!
    public static double WAIT_TIME = 5.0;

    private FtcDashboard dashboard = FtcDashboard.getInstance();

    ExtensionRetractionMechanismGenericMotor lift;
    private DcMotor8863Interface liftMotor;
    private CSVDataFile csvDataFile;

    private PIDFController extensionMotionController;
    private PIDCoefficients extensionPidCoefficients;
    private MotionProfileFollower extensionFollower;

    private PIDFController retractionMotionController;
    private PIDCoefficients retractionPidCoefficients;
    private MotionProfileFollower retractionFollower;

    // create the motion profile for the retraction movement
    private MotionProfile extensionMotionProfile = MotionProfileGenerator.generateSimpleMotionProfile(
            new MotionState(EXTENSION_START_POSITION, 0, 0, 0),
            new MotionState(EXTENSION_FINISH_POSITION, 0, 0, 0),
            MAX_VELOCITY_EXTENSION,
            MAX_ACCELERATION_EXTENSION);

    // create the motion profile for the retraction movement
    private MotionProfile retractionMotionProfile = MotionProfileGenerator.generateSimpleMotionProfile(
            new MotionState(EXTENSION_FINISH_POSITION, 0, 0, 0),
            new MotionState(RETRACTION_FINISH_POSITION, 0, 0, 0),
            MAX_VELOCITY_RETRACTION,
            MAX_ACCELERATION_RETRACTION);

    private DataLogging dataLog;
    private ElapsedTime timer;
    private double liftPosition;
    private double desiredLiftVelocity;
    private boolean dataWritten = false;

    @Override
    public void runOpMode() {
        // create the motor for the lift
        liftMotor = new DualMotorGearbox(
                PowerPlayRobot.HardwareName.LIFT_MOTOR_LEFT.hwName,
                PowerPlayRobot.HardwareName.LIFT_MOTOR_RIGHT.hwName,
                hardwareMap,
                telemetry,
                MotorConstants.MotorType.GOBILDA_1150);

        // create the lift
        lift = new ExtensionRetractionMechanismGenericMotor(hardwareMap, telemetry,
                "lift",
                PowerPlayRobot.HardwareName.LIFT_LIMIT_SWITCH_EXTENSION.hwName,
                PowerPlayRobot.HardwareName.LIFT_LIMIT_SWITCH_RETRACTION.hwName,
                liftMotor,
                MOVEMENT_PER_REVOLUTION);

        lift.reverseMotorDirection();

        csvDataFile = new CSVDataFile("liftRetractionFeedForwardData");
        csvDataFile.headerStrings("time (S)", "lift position (in)", "profile position", "lift velocity (in/sec)", "profile velocity (in/s)", "power");


        // set the limits for protecting the hardware
        lift.setExtensionPositionInMechanismUnits(MAXIMUM_LIFT_POSITION);
        lift.setRetractionPositionInMechanismUnits(MINIMUM_LIFT_POSITION);

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
        // set the profile for the follower to follow
        extensionFollower.setProfile(extensionMotionProfile, "extension");

        // create a retraction PIDF Controller using the constants defined for the lift
        retractionPidCoefficients = LiftConstants.RETRACTION_PID_COEFFICENTS;
        // this controller is non-linear. kG and kV need to be updated every update as a function of velocity
//        retractionMotionController = new PIDFController(retractionPidCoefficients, kVRetraction0ToMinus60, kARetraction, kStatic, kGRetraction0ToMinus60);
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
        // set the profile for the follower to follow
        retractionFollower.setProfile(retractionMotionProfile, "retraction");

        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        timer = new ElapsedTime();

        dataLog = new DataLogging("liftRetractionFeedforwardLog");
        lift.setDataLog(dataLog);
        lift.enableDataLogging();

        // init the lift, sets the zero on the encoder once it hits the retraction limit switch
        lift.init();
        while (!lift.isInitComplete()) {
            lift.update();
            idle();
        }
        phaseOfOperation = PhaseOfOperation.WAIT;

        // Wait for the start button
        telemetry.addData("Init complete", ".");
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        telemetry.clearAll();

        timer.reset();

        while (opModeIsActive()) {
            lift.update();
            liftPosition = lift.getPositionAtUpdate();
            desiredLiftVelocity = lift.getMotionProfileVelocityAtUpdate();


            switch (phaseOfOperation) {
                case WAIT: {
                    if (timer.seconds() > WAIT_TIME) {
                        // tell the lift to follow the Motion profile in the follower
                        lift.followProfile(extensionFollower);
                        lift.enableCollectData();
                        phaseOfOperation = PhaseOfOperation.EXTENDING;
                    }
                }
                break;

                case EXTENDING: {
                    // the motion profile could complete successfully or the lift hit the extension limit and is holding there
                    if (lift.isMotionProfileComplete() ||
                            lift.getExtensionRetractionState() == ExtensionRetractionMechanismGenericMotor.ExtensionRetractionStates.HOLDING_AT_EXTEND) {
                        timer.reset();
                        phaseOfOperation = PhaseOfOperation.EXTENSION_COMPLETE;
                    }
                }
                break;

                case EXTENSION_COMPLETE: {
                    // Wait for a period of time for the lift to settle into its position. This allows
                    // data to be collected during this period.
                    if (timer.seconds() > 3.0) {
                        // start collecting data
                        lift.followProfile(retractionFollower);
                        phaseOfOperation = PhaseOfOperation.RETRACTING;
                    }
                }
                break;

                case PAST_EXTENSION_POSITION: {
                }
                break;

                case AT_EXTENSION_LIMIT: {
                }
                break;

                case RETRACTING: {
                    // Is the retraction motion profile complete or did the lift hit the MIN position limit?
                    if (lift.isMotionProfileComplete() ||
                            lift.getExtensionRetractionState() == ExtensionRetractionMechanismGenericMotor.ExtensionRetractionStates.HOLDING_AT_RETRACT) {
                        timer.reset();
                        phaseOfOperation = PhaseOfOperation.RETRACTION_COMPLETE;
                    } else {
//                        if (desiredLiftVelocity < 0) {
//                            Double[] feedforwardkVkG = LiftConstants.getkVkGForRetraction(liftPosition, desiredLiftVelocity);
//                            retractionMotionController.setkV(feedforwardkVkG[0]);
//                            retractionMotionController.setkG(feedforwardkVkG[1]);
//                        }
                    }
                }
                break;

                case PAST_RETRACTION_POSITION: {
                }
                break;

                case RETRACTION_COMPLETE: {
                    if (timer.milliseconds() > 3000) {
                        // write a csv file with all of the data collected so that it can be analysized
                        // if needed.
                        csvDataFile.writeData(
                                lift.getTimeData(),
                                lift.getPositionData(),
                                lift.getMotionProfilePositions(),
                                lift.getVelocityData(),
                                lift.getMotionProfileVelocities(),
                                lift.getPowerData());
                        csvDataFile.closeDataLog();
                        lift.disableCollectData();
                        phaseOfOperation = PhaseOfOperation.ALL_DONE;
                    }
                }
                break;

                case ALL_DONE: {
                }
                break;
            }

            telemetry.addData("lift state = ", lift.getExtensionRetractionState().toString());
            telemetry.addData("Test state = ", phaseOfOperation.toString());
            telemetry.addData("Profile Position ", lift.getMotionProfilePositionAtUpdate());
            telemetry.addData("Actual Position ", liftPosition);
            telemetry.addData("Profile Velocity ", lift.getMotionProfileVelocityAtUpdate());
            telemetry.addData("Actual velocity ", lift.getVelocityAtUpdate());
            telemetry.addData("motor power = ", lift.getCurrentPower());
            telemetry.update();
        }
    }
}