package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest.LiftTuning;

import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAXIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_ACCELERATION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_VELOCITY;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MINIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.getKg;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kA;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kV;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kStatic;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.kinematics.Kinematics;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863Interface;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DualMotorGearbox;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanismGenericMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotionProfileFollower;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotorConstants;
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

    public static double EXTENSION_POSITION = 20; // in
    public static double RETRACTION_POSITION = 0; // in

    public static double WAIT_TIME = 5.0;

    private FtcDashboard dashboard = FtcDashboard.getInstance();

    ExtensionRetractionMechanismGenericMotor lift;
    private DcMotor8863Interface liftMotor;
    private CSVDataFile csvDataFile;
    private List<Double> profileVelocities;

    private double liftPosition = 0;
    private double targetPower = 0;

    private PIDFController motionController;
    private PIDCoefficients pidCoefficients;
    private MotionProfileFollower follower;

    // motion profile generator for back and forth movement
    private static MotionProfile generateProfile(boolean movingForward) {
        MotionState start = new MotionState(movingForward ? RETRACTION_POSITION : EXTENSION_POSITION, 0, 0, 0);
        MotionState goal = new MotionState(movingForward ? EXTENSION_POSITION : RETRACTION_POSITION, 0, 0, 0);
        return MotionProfileGenerator.generateSimpleMotionProfile(start, goal, MAX_VELOCITY, MAX_ACCELERATION);
    }

    private MotionState motionState;

    private DataLogging dataLog;

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
        csvDataFile.headerStrings("time (S)", "lift position (in)", "profile position", "velocity (in/sec)", "profile velocity (in/s)", "power");

        // set the limits for protecting the hardware
        lift.setExtensionPositionInMechanismUnits(MAXIMUM_LIFT_POSITION);
        lift.setRetractionPositionInMechanismUnits(MINIMUM_LIFT_POSITION);

        // tuning the PID portion comes later, so all 0 for now
        pidCoefficients = new PIDCoefficients(0, 0, 0);
        motionController = new PIDFController(pidCoefficients, kV, kA, kStatic, new Function2<Double, Double, Double>() {
            @Override
            public Double invoke(Double position, Double velocity) {
                return getKg(position);
            }
        });
        motionController.setOutputBounds(-1, 1);
        MotionProfile activeProfile = generateProfile(true);
        follower = new MotionProfileFollower(motionController);
        follower.setProfile(activeProfile, "extension");

        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        NanoClock clock = NanoClock.system();

        dataLog = new DataLogging("liftData");
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

        // get a dummy motionState for use in telemetry during wait
        motionState = activeProfile.get(0);
        double profileStart = clock.seconds();

        while (opModeIsActive()) {
            lift.update();
            double profileTime = clock.seconds() - profileStart;

            switch (phaseOfOperation) {
                case WAIT: {
                    if (profileTime > WAIT_TIME) {
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
                        phaseOfOperation = PhaseOfOperation.MOVEMENT_COMPLETE;
                        //lift.disableCollectData();
                    }
                }
                break;

                case MOVEMENT_COMPLETE: {
                    csvDataFile.writeData(
                            lift.getTimeData(),
                            lift.getPositionData(),
                            lift.getMotionProfilePositions(),
                            lift.getVelocityData(),
                            lift.getMotionProfileVelocities(),
                            lift.getPowerData());
                    csvDataFile.closeDataLog();
                    phaseOfOperation = PhaseOfOperation.ALL_DONE;
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

            // update telemetry
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