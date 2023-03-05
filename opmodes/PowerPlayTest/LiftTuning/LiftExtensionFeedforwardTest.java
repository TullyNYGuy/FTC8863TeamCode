package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest.LiftTuning;

import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAXIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_ACCELERATION_EXTENSION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_VELOCITY_EXTENSION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MINIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.getKg;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kAExtension;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kVExtension;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kStatic;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDCoefficients;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDFController;
import com.acmerobotics.roadrunner.kinematics.Kinematics;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.acmerobotics.roadrunner.util.NanoClock;
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
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.functions.Function2;

/*
 * This routine is designed to tune the open-loop feedforward coefficients. Although it may seem unnecessary,
 * tuning these coefficients is just as important as the positional parameters. Like the other
 * manual tuning routines, this op mode relies heavily upon the dashboard. To access the dashboard,
 * connect your computer to the RC's WiFi network. In your browser, navigate to
 * https://192.168.49.1:8080/dash if you're using the RC phone or https://192.168.43.1:8080/dash if
 * you are using the Control Hub. Once you've successfully connected, start the program, and your
 * robot will begin moving forward and backward according to a motion profile. Your job is to graph
 * the velocity errors over time and adjust the feedforward coefficients. Once you've found a
 * satisfactory set of gains, add them to the appropriate fields in the DriveConstants.java file.
 *
 * Pressing Y/Δ (Xbox/PS4) will pause the tuning process and enter driver override, allowing the
 * user to reset the position of the bot in the event that it drifts off the path.
 * Pressing B/O (Xbox/PS4) will cede control back to the tuning process.
 */
@Config
@Autonomous(group = "Lift Tuning")
@Disabled
public class LiftExtensionFeedforwardTest extends LinearOpMode {

    private enum PhaseOfOperation {
        RESET,
        WAIT,
        MOVING,
        MOVEMENT_COMPLETE,
        PAST_EXTENSION_POSITION,
        AT_EXTENSION_LIMIT,
        ALL_DONE
    }

    private PhaseOfOperation phaseOfOperation = PhaseOfOperation.RESET;

    private enum Direction {
        EXTENDING,
        RETRACTING
    }

    private Direction direction = Direction.EXTENDING;

    // the starting point and finishing points for the movement
    public static double EXTENSION_FINISH_POSITION = 30; // in
    public static double EXTENSION_START_POSITION = 0; // in

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
                5.93);

        lift.reverseMotorDirection();

        csvDataFile = new CSVDataFile("liftExtensionFeedForward");
        csvDataFile.headerStrings("time (S)", "lift position (in)", "profile position", "lift velocity (in/sec)", "profile velocity (in/s)", "power");

        // set the limits for protecting the hardware
        lift.setExtensionPositionInMechanismUnits(MAXIMUM_LIFT_POSITION);
        lift.setRetractionPositionInMechanismUnits(MINIMUM_LIFT_POSITION);

        // create a PIDF Controller using the constants defined for the lift
        pidCoefficients = LiftConstants.EXTENSION_PID_COEFFICENTS;
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

        dataLog = new DataLogging("liftData");
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

        double profileStart = clock.seconds();

        while (opModeIsActive()) {
            lift.update();
            double profileTime = clock.seconds() - profileStart;

            switch (phaseOfOperation) {
                case WAIT: {
                    if (profileTime > WAIT_TIME) {
                        // tell the lift to follow the Motion profile in the follower
                        lift.followProfile(follower);
                        phaseOfOperation = PhaseOfOperation.MOVING;
                        // reset the clock to use in the profile
                        lift.enableCollectData();
                    }
                }
                break;

                case MOVING: {
                    // the motion profile could complete successfully or the lift hit the extension limit and is holding there
                    if (lift.isMotionProfileComplete() ||
                            lift.getExtensionRetractionState() == ExtensionRetractionMechanismGenericMotor.ExtensionRetractionStates.HOLDING_AT_EXTEND) {
                        timer.reset();
                        phaseOfOperation = PhaseOfOperation.MOVEMENT_COMPLETE;
                        //lift.disableCollectData();
                    }
                }
                break;

                case MOVEMENT_COMPLETE: {
                    // Wait for a period of time for the lift to settle into its position. This allows
                    // data to be collected for during this period.
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

                case PAST_EXTENSION_POSITION: {

                }
                break;

                case AT_EXTENSION_LIMIT: {

                }
                break;

                case ALL_DONE: {
                }
                break;
            }

            // update telemetry - this info will be updated continuously while the opmode runs
            telemetry.addData("lift state = ", lift.getExtensionRetractionState().toString());
            telemetry.addData("Test state = ", phaseOfOperation.toString());
            telemetry.addData("Profile Position ", lift.getMotionProfilePositionAtUpdate());
            telemetry.addData("Actual Position ", lift.getPositionAtUpdate());
            telemetry.addData("Profile Velocity ", lift.getMotionProfileVelocityAtUpdate());
            telemetry.addData("Actual velocity ", lift.getVelocityAtUpdate());
            telemetry.addData("motor power = ", lift.getCurrentPower());
            telemetry.update();
        }
    }
}