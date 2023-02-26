package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest.LiftTuning;

import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAXIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAX_VELOCITY_EXTENSION;
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

/*
 * This routine is designed to tune the open-loop feedforward coefficients for the lift when it is
 * extending. These are kV and kG. Note that you already found kG experimentally. This will calculate
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
 * Note that the data in this and all other feed forward tuning routines is heavily dependent on the
 * battery voltage. Try to use a battery that is at an "average charge". Like about 12.5V.
 */
@Config
@Autonomous(group = "Lift Tuning")
public class LiftExtensionAutomaticFeedforwardConstantCalculator extends LinearOpMode {
    private enum Direction {
        EXTENDING,
        RETRACTING
    }

    private Direction direction = Direction.EXTENDING;

    private enum PhaseOfOperation {
        RESET,
        DATA_COLLECTION,
        PAST_EXTENSION_POSITION,
        AT_EXTENSION_LIMIT,
        CALCULATIONS_DONE
    }

    private PhaseOfOperation phaseOfOperation = PhaseOfOperation.RESET;

    // These are the starting and stopping positions for the lift during the extension.
    // These should be between the limits for the lift.
    public static double EXTENSION_FINISH_POSITION = 25; // in
    public static double EXTENSION_START_POSITION = 0; // in

    // This is the maximum power that will be applied to the lift motor at the end of the ramp. In
    // actuality the power may go higher than this, but don't worry, it will be ok.
    public static double MAX_POWER = 0.6;

    private FtcDashboard dashboard = FtcDashboard.getInstance();

    ExtensionRetractionMechanismGenericMotor lift;
    private DcMotor8863Interface liftMotor;
    private CSVDataFile csvDataFile;

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

        csvDataFile = new CSVDataFile("liftAutoTuner");
        csvDataFile.headerStrings("time (S)", "lift position (in)", "lift velocity (in/S)", "power");

        // set the limits for protecting the hardware
        lift.setExtensionPositionInMechanismUnits(MAXIMUM_LIFT_POSITION);
        lift.setRetractionPositionInMechanismUnits(MINIMUM_LIFT_POSITION);

        dataLog = new DataLogging("liftExtensionRampPowerData");
        lift.setDataLog(dataLog);
        lift.enableDataLogging();


        // init the lift, sets the zero on the encoder once it hits the retraction limit switch
        lift.init();
        while (!lift.isInitComplete()) {
            lift.update();
            idle();
        }
        phaseOfOperation = PhaseOfOperation.DATA_COLLECTION;

        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        NanoClock clock = NanoClock.system();

        // Wait for the start button
        telemetry.addData("Init complete", ".");
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        telemetry.clearAll();

        // in theory this would be the maximum velocity of the lift. However, with a load on the motor
        // the max rpm of the motor will not be hit. So you may want to substitute an experimentally 
        // determine max lift velocity
        //double maxVelocity = MAX_RPM / 60 * MOVEMENT_PER_REVOLUTION;
        double maxVelocity = MAX_VELOCITY_EXTENSION; // in/sec
        double finalVelocity = MAX_POWER * maxVelocity;
        double movementDistance = EXTENSION_FINISH_POSITION - EXTENSION_START_POSITION;
        double acceleration = (finalVelocity * finalVelocity) / (2.0 * movementDistance);
        // the ramp time did not seem to work well so I just ramped the power until the lift hit
        // the finish position
        //double rampTime = Math.sqrt(2.0 * movementDistance / acceleration);

        double profileStart = clock.seconds();
        lift.enableCollectData();

        while (opModeIsActive()) {
            elapsedTime = clock.seconds() - profileStart;
            lift.update();
            liftPosition = lift.getPosition();

            switch (phaseOfOperation) {
                case DATA_COLLECTION: {
                    // If the lift has passed the destination extension position, then drop the power down
                    // so that the power is at the kG when the lift extends to the extension limit
                    if (liftPosition > EXTENSION_FINISH_POSITION) {
                        lift.disableCollectData();
                        targetPower = getKg(liftPosition);
                        lift.setPowerUsingJoystick(targetPower);
                        phaseOfOperation = PhaseOfOperation.PAST_EXTENSION_POSITION;
                    } else {
                        // calculate the power for the ramp
                        calculatedVelocity = acceleration * elapsedTime;
                        targetPower = calculatedVelocity / maxVelocity;
                        lift.setPowerUsingJoystick(targetPower);
                    }
                    telemetry.addData("lift state = ", lift.getExtensionRetractionState().toString());
                    telemetry.addData("motor power = ", targetPower);
                    telemetry.update();
                }
                break;

                case PAST_EXTENSION_POSITION: {
                    if (liftPosition >= MAXIMUM_LIFT_POSITION) {
                        phaseOfOperation = PhaseOfOperation.AT_EXTENSION_LIMIT;
                    }
                    telemetry.addData("lift state = ", lift.getExtensionRetractionState().toString());
                    telemetry.addData("motor power = ", targetPower);
                    telemetry.update();
                }
                break;

                case AT_EXTENSION_LIMIT: {
                    // The lift should be in the hold at extension mode so don't do anything about
                    // lift power.
                    // Write a csv file of the lift data for use in manual analysis if needed.
                    csvDataFile.writeData(lift.getTimeData(), lift.getPositionData(), lift.getVelocityData(), lift.getPowerData());
                    csvDataFile.closeDataLog();

                    // Make the kV and kstatic calculations
                    boolean fitIntercept = true;
                    RegressionUtil.RampResult rampResult = RegressionUtil.fitRampData(
                            lift.getTimeData(), lift.getPositionData(), lift.getPowerData(), fitIntercept,
                            LoggingUtil.getLogFile(Misc.formatInvariant(
                                    "DriveRampRegression-%d.csv", System.currentTimeMillis())));

                    telemetry.clearAll();
                    telemetry.addLine("Quasi-static ramp up test complete");
                    telemetry.addLine(Misc.formatInvariant("kV = %.5f, kG = %.5f (R^2 = %.2f)",
                            rampResult.kV, rampResult.kStatic, rampResult.rSquare));
                    telemetry.addData("You can press stop.", "But hold on to the lift in case it falls!");
                    telemetry.update();

                    phaseOfOperation = PhaseOfOperation.CALCULATIONS_DONE;
                    // todo add kA calculation
                }
                break;

                case CALCULATIONS_DONE: {
                    // wait for the user to stop the opmode
                }
            }
        }
    }
}