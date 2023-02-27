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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863Interface;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DualMotorGearbox;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanismGenericMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotorConstants;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;

/*
 * This routine is designed to help you find the open-loop feedfoward coefficients, kV and kG. It
 * will also get the maximum velocity for extension.
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
 * You will need to run this routine multiple times. Start with a power this is just above the kG
 * power. With each run increase the power by 0.5. On the last run you the power will be 1.0. This
 * last run will also give you the max velocity for the extension.
 *
 * Note that the data in this and all other feed forward tuning routines is heavily dependent on the
 * battery voltage. Try to use a battery that is at an "average charge". Like about 12.5V.
 */
@Config
@Autonomous(group = "Lift Tuning")
@Disabled
public class LiftExtensionConstantPower extends LinearOpMode {
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
        DONE
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

        double profileStart = clock.seconds();
        lift.enableCollectData();

        while (opModeIsActive()) {
            elapsedTime = clock.seconds() - profileStart;
            lift.update();
            liftPosition = lift.getPosition();

            switch (phaseOfOperation) {
                // apply a constant motor power to the lift until it hits the EXTENSION_POSITION,
                // then apply the previously determined kG to the motor to hold the lift in position
                case CONSTANT_POWER_DATA_COLLECTION: {
                    // If the lift has passed the destination extension position, then set the power
                    // to the previously determined kG so that it holds in place
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

                // now that the lift is at EXTENSION_POSITION, double check to make sure it does not
                // continue moving and hit the max extension limit
                case CONSTANT_POWER_PAST_EXTENSION_POSITION: {
                    if (liftPosition >= MAXIMUM_LIFT_POSITION) {
                        phaseOfOperation = PhaseOfOperation.CONSTANT_POWER_AT_EXTENSION_LIMIT;
                        lift.disableCollectData();
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

                    telemetry.addData("You can press stop.", "But hold on to the lift in case it falls!");
                    telemetry.update();

                    phaseOfOperation = PhaseOfOperation.DONE;
                }
                break;

                case DONE: {
                    // wait for the user to stop the opmode
                }
            }
        }
    }
}