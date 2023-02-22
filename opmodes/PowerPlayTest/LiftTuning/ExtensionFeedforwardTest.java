package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest.LiftTuning;

import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAXIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_ACCELERATION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_VELOCITY;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MINIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.getKg;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kA;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.kV;
import static org.firstinspires.ftc.teamcode.RoadRunner.drive.DriveConstants.kStatic;

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
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863Interface;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DualMotorGearbox;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanismGenericMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotorConstants;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;

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
public class ExtensionFeedforwardTest extends LinearOpMode {

    private enum PhaseOfOperation {
        RESET,
        MOVING,
        MOVEMENT_COMPLETE,
        PAST_EXTENSION_POSITION,
        AT_EXTENSION_LIMIT,
        CALCULATIONS_DONE
    }

    private PhaseOfOperation phaseOfOperation = PhaseOfOperation.RESET;

    private enum Direction {
        EXTENDING,
        RETRACTING
    }
    private Direction direction = Direction.EXTENDING;

    public static double EXTENSION_POSITION = 20; // in
    public static double RETRACTION_POSITION = 0; // in

    private FtcDashboard dashboard = FtcDashboard.getInstance();

    ExtensionRetractionMechanismGenericMotor lift;
    private DcMotor8863Interface liftMotor;
    private CSVDataFile csvDataFile;

    private double liftPosition = 0;
    private double targetPower = 0;

    private PIDFController motionController;
    private PIDCoefficients pidCoefficients;

    // motion profile generator for back and forth movement
    private static MotionProfile generateProfile(boolean movingForward) {
        MotionState start = new MotionState(movingForward ? RETRACTION_POSITION : EXTENSION_POSITION, 0, 0, 0);
        MotionState goal = new MotionState(movingForward ? EXTENSION_POSITION : RETRACTION_POSITION, 0, 0, 0);
        return MotionProfileGenerator.generateSimpleMotionProfile(start, goal, MAX_VELOCITY, MAX_ACCELERATION);
    }

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
        csvDataFile.headerStrings("time (S)", "lift position (in)", "power");

        // set the limits for protecting the hardware
        lift.setExtensionPositionInMechanismUnits(MAXIMUM_LIFT_POSITION);
        lift.setRetractionPositionInMechanismUnits(MINIMUM_LIFT_POSITION);

        // tuning the PID portion comes later, so all 0 for now
        pidCoefficients = new PIDCoefficients(0,0,0);
        motionController = new PIDFController(pidCoefficients, kV, kA, 0);
        motionController.setOutputBounds(-1, 1);

        dataLog = new DataLogging("liftData");
        lift.setDataLog(dataLog);
        lift.enableDataLogging();


        // init the lift, sets the zero on the encoder once it hits the retraction limit switch
        lift.init();
        while (!lift.isInitComplete()) {
            lift.update();
            idle();
        }
        phaseOfOperation = PhaseOfOperation.MOVING;

        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        NanoClock clock = NanoClock.system();

        // Wait for the start button
        telemetry.addData("Init complete", ".");
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        //telemetry.clearAll();

        waitForStart();

        telemetry.clearAll();
        boolean movingForwards = true;
        MotionProfile activeProfile = generateProfile(true);
        double profileStart = clock.seconds();
        lift.enableCollectData();

        while (opModeIsActive()) {
            lift.update();
            liftPosition = lift.getCurrentPosition();
            // calculate and set the motor power
            double profileTime = clock.seconds() - profileStart;

            switch (phaseOfOperation) {
                case MOVING: {
                    MotionState motionState = activeProfile.get(profileTime);
                    double targetVelocity = motionState.getV();
                    motionController.setTargetVelocity(targetVelocity);
                    motionController.setTargetAcceleration(motionState.getA());
                    targetPower = Kinematics.calculateMotorFeedforward(targetVelocity, motionState.getA(), kV, kA, kStatic) + getKg(liftPosition);

                    if (liftPosition > EXTENSION_POSITION) {
                        targetPower = getKg(liftPosition);
                        phaseOfOperation = PhaseOfOperation.PAST_EXTENSION_POSITION;
                        lift.disableCollectData();
                    }
                    if (profileTime > activeProfile.duration()) {
                        targetPower = getKg(liftPosition);
                        phaseOfOperation = PhaseOfOperation.MOVEMENT_COMPLETE;
                        lift.disableCollectData();
                    }
                    lift.setPowerUsingJoystick(targetPower);
                }
                break;

                case MOVEMENT_COMPLETE: {
                    csvDataFile.writeData(lift.getTimeData(), lift.getPositionData(), lift.getPowerData());
                    csvDataFile.closeDataLog();
                }
                break;

                case PAST_EXTENSION_POSITION: {

                }
                break;

                case AT_EXTENSION_LIMIT: {

                }
                break;

                case CALCULATIONS_DONE: {

                }
                break;
            }

            // update telemetry
            telemetry.addData("direction = ", direction.toString());
            telemetry.addData("lift state = ", lift.getExtensionRetractionState().toString());
            telemetry.addData("motor power = ", targetPower);
            telemetry.update();
        }
    }
}