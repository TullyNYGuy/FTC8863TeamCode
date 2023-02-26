package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest.LiftTuning;

import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAXIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_ACCELERATION_EXTENSION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_VELOCITY_EXTENSION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_VELOCITY_RETRACTION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MINIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MOVEMENT_PER_REVOLUTION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.getKg;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kAExtension;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kStatic;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kVExtension;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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
 * This routine is designed to tune the open-loop feedforward coefficients for the lift when it is
 * retracting. These are kV and kG. Note that you already found kG experimentally. This will calculate
 * it. They should be roughly the same.
 * This opmode applies a ramping power to the lift motor. The theory is that the acceleration will
 * be minimal, or almost 0. Using this formula:
 * Power = kV * V + kA * A + kG
 * If kA =0 then this is a formula for a line:
 * Power = kV * V + kG
 * This opmode takes position and power data while the ramping power is applied. After this is
 * collected, the opmode calculates the velocities and performs a linear regression of the velocity
 * and power data to find the slope of the line (kV) and the y intercept of the line (kG). They won't
 * be exact for a couple of reasons:
 *  - the acceleration is not really 0
 *  - the kG actually includes static friction
 * But they are close enough for a starting point to use in refining them more with some manual
 * testing.
 *
 * You can only run this routing after you have tuned the extension, including the kP. It uses a
 * motion profile to extend the lift up and then hold it there before retracting.
 *
 * NEVER TESTED THIS !
 */
@Config
@Autonomous(group = "Lift Tuning")
public class LiftRetractionAutomaticFeedforwardConstantCalculator extends LinearOpMode {

    private enum PhaseOfOperation {
        RESET,
        WAIT,
        EXTENDING,
        EXTENSION_COMPLETE,
        PAST_EXTENSION_POSITION,
        AT_EXTENSION_LIMIT,
        RETRACTING,
        PAST_RETRACTION_POSITION,
        AT_RETRACTION_LIMIT,
        ALL_DONE
    }

    private PhaseOfOperation phaseOfOperation = PhaseOfOperation.RESET;

    // the starting point and finishing points for the movement
    public static double EXTENSION_FINISH_POSITION = 35; // in
    public static double EXTENSION_START_POSITION = 0; // in
    public static double RETRACTION_FINISH_POSITION = 10; // in

    // This is the maximum power that will be applied to the lift motor at the end of the ramp. In
    // actuality the power may go higher than this, but don't worry, it will be ok.
    public static double MAX_POWER = -0.2;

    // a wait time to allow the user to setup the FTC dashboard to graph the data. Better be quick!
    public static double WAIT_TIME = 5.0;

    private FtcDashboard dashboard = FtcDashboard.getInstance();

    ExtensionRetractionMechanismGenericMotor lift;
    private DcMotor8863Interface liftMotor;
    private CSVDataFile csvDataFile;

    private PIDFController motionController;
    private PIDCoefficients pidCoefficients;
    private MotionProfileFollower follower;

    // motion profile generator for extension movement
    private static MotionProfile generateProfile() {
        MotionState start = new MotionState(EXTENSION_START_POSITION, 0, 0, 0);
        MotionState goal = new MotionState(EXTENSION_FINISH_POSITION, 0, 0, 0);
        return MotionProfileGenerator.generateSimpleMotionProfile(start, goal, MAX_VELOCITY_EXTENSION, MAX_ACCELERATION_EXTENSION);
    }

    private DataLogging dataLog;
    private ElapsedTime timer;
    private double calculatedVelocity;
    private double acceleration;
    private double movementDistance;
    private double finalVelocity;
    private double targetPower;
    private double liftPosition;

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

        csvDataFile = new CSVDataFile("liftRetractionAutoConstantCalculator");
        csvDataFile.headerStrings("time (S)", "lift position (in)", "lift velocity (in/sec)", "power");

        // set the limits for protecting the hardware
        lift.setExtensionPositionInMechanismUnits(MAXIMUM_LIFT_POSITION);
        lift.setRetractionPositionInMechanismUnits(MINIMUM_LIFT_POSITION);

        // create a PIDF Controller using the constants defined for the lift
        pidCoefficients = LiftConstants.MOTION_PID_COEFFICENTS;
        motionController = new PIDFController(pidCoefficients, kVExtension, kAExtension, kStatic, new PIDFController.FeedforwardFunction() {
            @Override
            public Double compute(double position, Double velocity) {
                return getKg(position);
            }
        });
        // limit the output to valid motor commands
        motionController.setOutputBounds(-1, 1);

        // create the motion profile
        MotionProfile activeProfile = generateProfile();
        // create a follower for the profile and pass the PIF controller to it.
        follower = new MotionProfileFollower(motionController);
        // set the profile for the follower to follow
        follower.setProfile(activeProfile, "extension");

        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        NanoClock clock = NanoClock.system();
        double elapsedTime = 0;

        dataLog = new DataLogging("liftRetractionAutoCalc");
        lift.setDataLog(dataLog);
        lift.enableDataLogging();

        timer = new ElapsedTime();

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

            switch (phaseOfOperation) {
                case WAIT: {
                    if (timer.seconds() > WAIT_TIME) {
                        // tell the lift to follow the Motion profile in the follower
                        lift.followProfile(follower);
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
                    // data to be collected for during this period.
                    if (timer.seconds() > 3.0) {
                        // calculate the power ramp
                        finalVelocity = MAX_POWER * MAX_VELOCITY_RETRACTION;
                        movementDistance = lift.getCurrentPosition() - RETRACTION_FINISH_POSITION;
                        acceleration = (finalVelocity * finalVelocity) / (2.0 * movementDistance);

                        // start collecting data
                        lift.enableCollectData();
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
                    liftPosition = lift.getCurrentPosition();
                    // is the lift at the end of the movement?
                    if(liftPosition <= RETRACTION_FINISH_POSITION ) {
                        // yes, slow the lift down
                        lift.setPowerUsingJoystick(getKg(liftPosition));
                        lift.disableCollectData();
                        // write a csv file with all of the data collected so that it can be analysized
                        // if needed.
                        csvDataFile.writeData(
                                lift.getTimeData(),
                                lift.getPositionData(),
                                lift.getVelocityData(),
                                lift.getPowerData());
                        csvDataFile.closeDataLog();
                        phaseOfOperation = PhaseOfOperation.PAST_RETRACTION_POSITION;
                    } else {
                        calculatedVelocity = acceleration * elapsedTime;
                        // negative power is needed for retraction
                        targetPower = -calculatedVelocity / MAX_VELOCITY_RETRACTION;
                        lift.setPowerUsingJoystick(targetPower);
                    }

                }
                break;

                case PAST_RETRACTION_POSITION: {
                    if (lift.getExtensionRetractionState() == ExtensionRetractionMechanismGenericMotor.ExtensionRetractionStates.HOLDING_AT_RETRACT) {
                        // the lift continued retracting and it hit the limit. The lift held at that
                        // position to protect the hardware.
                        phaseOfOperation = PhaseOfOperation.AT_RETRACTION_LIMIT;
                    }
                    // do nothing while the lift slows down or stops
                }
                break;

                case AT_RETRACTION_LIMIT: {
                    // just sit here until the user hits stop
                }
                break;
            }

            // update telemetry - this info will be updated continuously while the opmode runs
            telemetry.addData("lift state = ", lift.getExtensionRetractionState().toString());
            telemetry.addData("Test state = ", phaseOfOperation.toString());
            telemetry.addData("Actual Position ", lift.getPositionAtUpdate());
            telemetry.addData("Actual velocity ", lift.getVelocityAtUpdate());
            telemetry.addData("motor power = ", lift.getCurrentPower());
            telemetry.update();
        }
    }
}