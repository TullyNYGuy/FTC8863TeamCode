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
 * This routine is designed to help you find the open-loop feedfoward coefficients, kV and kG. It
 * will also get the maximum velocity for retraction.
 *
 * Like the other manual tuning routines, this op mode relies heavily upon the dashboard. To access
 * the dashboard, connect your computer to the RC's WiFi network. In your browser, navigate to
 * https://192.168.49.1:8080/dash if you're using the RC phone or https://192.168.43.1:8080/dash if
 * you are using the Control Hub.
 *
 * This routine applies a constant motor power to the lift motor. The lift will accelerate and then
 * reach a constant velocity that is proportional to the motor power. It will save a csv file of the
 * time, position, velocity and motor power. You can import the csv into a spreadsheet and look for
 * the constant velocity. You may want to average it over the various points in time. Record the
 * velocity and motor power.
 *
 * You will need to run this routine multiple times. Start with a power that is just below the kG
 * power. With each run decrease the power by 0.5. On the last run you the power will be -1.0. This
 * last run will also give you the max velocity for the retraction.
 *
 * Note that the data in this and all other feed forward tuning routines is heavily dependent on the
 * battery voltage. Try to use a battery that is at an "average charge". Like about 12.5V.
 *
 * You can only run this routing after you have tuned the extension, including the kP. It uses a
 * motion profile to extend the lift up and then hold it there before retracting.
 */
@Config
@Autonomous(group = "Lift Tuning")
public class LiftRetractionConstantPower extends LinearOpMode {

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
    public static double EXTENSION_FINISH_POSITION = 30; // in
    public static double EXTENSION_START_POSITION = 0; // in
    public static double RETRACTION_FINISH_POSITION = 10; // in

    // This is the constant power to be applied. Note that any power less than the kG will cause the
    // lift to retract. So the powers start out positive and work their way more negative.
    public static double POWER = .1;

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
    private double liftPosition;
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

        csvDataFile = new CSVDataFile("liftRetractionContantPower");
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

        timer = new ElapsedTime();

        dataLog = new DataLogging("liftRetraction");
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
                    // data to be collected during this period.
                    if (timer.seconds() > 3.0) {
                        // start collecting data
                        lift.enableCollectData();
                        lift.setPowerUsingJoystick(POWER);
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

                        phaseOfOperation = PhaseOfOperation.PAST_RETRACTION_POSITION;
                    } else {
                        // do nothing while the lift retracts
                    }
                }
                break;

                case PAST_RETRACTION_POSITION: {
                    if (lift.getExtensionRetractionState() == ExtensionRetractionMechanismGenericMotor.ExtensionRetractionStates.HOLDING_AT_RETRACT) {
                        // the lift continued retracting and it hit the limit. The lift held at that
                        // position to protect the hardware.
                        phaseOfOperation = PhaseOfOperation.AT_RETRACTION_LIMIT;
                    }
                    // write a csv file with all of the data collected so that it can be analysized
                    // if needed.
                    if (!dataWritten) {
                        csvDataFile.writeData(
                                lift.getTimeData(),
                                lift.getPositionData(),
                                lift.getVelocityData(),
                                lift.getPowerData());
                        csvDataFile.closeDataLog();
                        dataWritten = true;
                    }
                    // do nothing while the lift slows down or stops
                }
                break;

                case AT_RETRACTION_LIMIT: {
                    // write a csv file with all of the data collected so that it can be analysized
                    // if needed.
                    if (!dataWritten) {
                        csvDataFile.writeData(
                                lift.getTimeData(),
                                lift.getPositionData(),
                                lift.getVelocityData(),
                                lift.getPowerData());
                        csvDataFile.closeDataLog();
                        dataWritten = true;
                    }
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