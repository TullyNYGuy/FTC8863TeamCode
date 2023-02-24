package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest.LiftTuning;

import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAXIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MINIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MOVEMENT_PER_REVOLUTION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.getKg;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.internal.system.Misc;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863Interface;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DualMotorGearbox;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanismGenericMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotorConstants;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;
import org.firstinspires.ftc.teamcode.RoadRunner.util.LoggingUtil;
import org.firstinspires.ftc.teamcode.RoadRunner.util.RegressionUtil;
import org.firstinspires.ftc.teamcode.opmodes.SkystoneTest.TestExtensionArmGoToPosition;

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
public class LiftAutomaticFeedforwardTunerConstantPower extends LinearOpMode {
    private enum Direction {
        EXTENDING,
        RETRACTING
    }

    private Direction direction = Direction.EXTENDING;

    private enum PhaseOfOperation {
        RESET,
        CONSTANT_POWER_DATA_COLLECTION,
        CONSTANT_POWER_PAST_EXTENSION_POSITION,
        CONSTANT_POWER_AT_EXTENSION_LIMIT,
        CONSTANT_POWER_CALCULATIONS_DONE
    }

    private PhaseOfOperation phaseOfOperation = PhaseOfOperation.RESET;

    // These are the starting and stopping positions for the lift. These should be between the 
    // limits for the lift.
    public static double EXTENSION_POSITION = 25; // in
    public static double RETRACTION_POSITION = 0; // in

    public static double MAX_POWER = 0.6;

    private FtcDashboard dashboard = FtcDashboard.getInstance();

    ExtensionRetractionMechanismGenericMotor lift;
    private DcMotor8863Interface liftMotor;
    private CSVDataFile csvDataFile;

    private double currentVelocity = 0;
    private double liftPosition = 0;
    private double targetPower = 0;
    private double elapsedTime = 0;
    private double calculatedVelocity = 0;

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
                MOVEMENT_PER_REVOLUTION);

        lift.reverseMotorDirection();

        csvDataFile = new CSVDataFile("liftAutoTunerConstantPower");
        csvDataFile.headerStrings("time (S)", "lift position (in)", "power");

        // set the limits for protecting the hardware
        lift.setExtensionPositionInMechanismUnits(MAXIMUM_LIFT_POSITION);
        lift.setRetractionPositionInMechanismUnits(MINIMUM_LIFT_POSITION);

        dataLog = new DataLogging("liftData");
        lift.setDataLog(dataLog);
        lift.enableDataLogging();


        // init the lift, sets the zero on the encoder once it hits the retraction limit switch
        lift.init();
        while (!lift.isInitComplete()) {
            lift.update();
            idle();
        }
        phaseOfOperation = PhaseOfOperation.CONSTANT_POWER_DATA_COLLECTION;

        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        NanoClock clock = NanoClock.system();

        // Wait for the start button
        telemetry.addData("Init complete", ".");
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        //telemetry.clearAll();

        waitForStart();

        telemetry.clearAll();

        // in theory this would be the maximum velocity of the lift. However, with a load on the motor
        // the max rpm of the motor will not be hit. So you may want to substitute an experimentally 
        // determine max lift velocity
        //double maxVelocity = MAX_RPM / 60 * MOVEMENT_PER_REVOLUTION;
        double maxVelocity = 57; // in/sec
        double finalVelocity = MAX_POWER * maxVelocity;
        double movementDistance = EXTENSION_POSITION - RETRACTION_POSITION;
        double acceleration = (finalVelocity * finalVelocity) / (2.0 * movementDistance);
        double rampTime = Math.sqrt(2.0 * movementDistance / acceleration);

        // calculate a slope for ramping down the power so that the lift does not slam into the
        // extension limit at full speed. I want the lift to hold position at the top so I'm using
        // the previously determined kG as a function of extension formula
        double powerRampSlope = -(MAX_POWER - getKg(MAXIMUM_LIFT_POSITION)) / (MAXIMUM_LIFT_POSITION - EXTENSION_POSITION);

        double profileStart = clock.seconds();
        lift.enableCollectDataForPostProcesssing();

        while (opModeIsActive()) {
            elapsedTime = clock.seconds() - profileStart;
            lift.update();
            liftPosition = lift.getPosition();

            switch (phaseOfOperation) {
                case CONSTANT_POWER_DATA_COLLECTION: {
                    // If the lift has passed the destination extension position, then ramp the power down
                    // so that the power is at the kG when the lift extends to the extension limit
                    if (liftPosition > EXTENSION_POSITION) {
                        targetPower = getKg(liftPosition);
                        lift.setPowerUsingJoystick(targetPower);
                        phaseOfOperation = PhaseOfOperation.CONSTANT_POWER_PAST_EXTENSION_POSITION;
                    } else {
                        // calculate the power
                        targetPower = MAX_POWER;
                        lift.setPowerUsingJoystick(targetPower);
                    }
                    telemetry.addData("lift state = ", lift.getExtensionRetractionState().toString());
                    telemetry.addData("motor power = ", targetPower);
                    telemetry.update();
                }
                break;

                case CONSTANT_POWER_PAST_EXTENSION_POSITION: {
                    if (liftPosition >= MAXIMUM_LIFT_POSITION) {
                        phaseOfOperation = PhaseOfOperation.CONSTANT_POWER_AT_EXTENSION_LIMIT;
                        lift.disablecollectDataForPostProcessing();
                    }
                    telemetry.addData("lift state = ", lift.getExtensionRetractionState().toString());
                    telemetry.addData("motor power = ", targetPower);
                    telemetry.update();
                }
                break;

                case CONSTANT_POWER_AT_EXTENSION_LIMIT: {
                    // The lift should be in the hold at extension mode so don't do anything about
                    // lift power.
                    csvDataFile.writeData(lift.getTimeData(), lift.getPositionData(), lift.getPowerData());
                    csvDataFile.closeDataLog();

                    // Make the kV and kstatic calculations
                    boolean fitIntercept = true;
//                    RegressionUtil.RampResult rampResult = RegressionUtil.fitRampData(
//                            lift.getTimeData(), lift.getPositionData(), lift.getPowerData(), fitIntercept,
//                            LoggingUtil.getLogFile(Misc.formatInvariant(
//                                    "DriveRampRegression-%d.csv", System.currentTimeMillis())));
//
//                    telemetry.clearAll();
//                    telemetry.addLine("Quasi-static ramp up test complete");
//                    telemetry.addLine(Misc.formatInvariant("kV = %.5f, kStatic = %.5f (R^2 = %.2f)",
//                            rampResult.kV, rampResult.kStatic, rampResult.rSquare));
                    telemetry.addData("You can press stop.", "But hold on to the lift in case it falls!");
                    telemetry.update();

                    phaseOfOperation = PhaseOfOperation.CONSTANT_POWER_CALCULATIONS_DONE;
                }
                break;

                case CONSTANT_POWER_CALCULATIONS_DONE: {
                    // wait for the user to stop the opmode
                }
            }
        }

    }

    private double getRampDownPower(double liftPosition, double powerRampSlope) {
        return (targetPower + (MAXIMUM_LIFT_POSITION - liftPosition) * powerRampSlope);
    }
}