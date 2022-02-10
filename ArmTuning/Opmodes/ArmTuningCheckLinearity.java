package org.firstinspires.ftc.teamcode.ArmTuning.Opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.ArmConstants;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.SampleArm;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AngleUtilities;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LimitPosition;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MovementLimit;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.functions.Function2;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Config
@TeleOp(name = "Check linearity", group = "Arm Tuning")
//@Disabled
public class ArmTuningCheckLinearity extends LinearOpMode {

    // Put your variable declarations here

    SampleArm sampleArm;

    LimitPosition limitPosition;
    public static double LIMIT_POSITION = 0; // in degrees

    double position = 0;
    double velocity = 0;
    double motorPower = 0;
    double armPowerFeedforward = 0;
    public static double ARM_POWER = 0.2;

    private FtcDashboard dashboard;

    List<Double> timeSamples = new ArrayList<>();
    List<Double> positionSamples = new ArrayList<>();
    List<Double> velocitySamples = new ArrayList<>();

    CSVDataFile csvDataFile = new CSVDataFile("ArmVelocity");

    NanoClock clock = NanoClock.system();
    double startTime = 0;

    @Override
    public void runOpMode() {

        // Put your initializations here
        sampleArm = new SampleArm(hardwareMap, telemetry);
        sampleArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        limitPosition = new LimitPosition(LIMIT_POSITION, MovementLimit.Direction.LIMIT_INCREASING_POSITIONS, "Limit" );


        // Get the instance of the running FTC Dashboard
        dashboard = FtcDashboard.getInstance();
        dashboard.setTelemetryTransmissionInterval(25);
        // merge the normal FTC SDK telemetry with the FTC Dashboard telemetry
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        startTime = clock.seconds();
        while (opModeIsActive()) {
            // If the arm power increases too much then the arm will run away, you lose control of it.
            // Implement a limit to the movement. Snap the arm to vertical if it runs away and hits
            // the vertical position.
            if(limitPosition.isLimitReached(sampleArm.getPosition(AngleUnit.DEGREES))) {
                sampleArm.holdAtPosition(LIMIT_POSITION, AngleUnit.DEGREES);
                break;
            }

            // apply the feedforward to the motor as the arm is moved around
            position = sampleArm.getPosition(AngleUnit.RADIANS);
            velocity = sampleArm.getVelocity(AngleUnit.RADIANS);

            armPowerFeedforward = ArmConstants.calculateFeedForwardDebug(position, AngleUnit.RADIANS, velocity, telemetry);
            motorPower = armPowerFeedforward + ARM_POWER;
            sampleArm.armMotor.setPower(motorPower);

            time = clock.seconds() - startTime;
            position = Math.toDegrees(position);
            velocity = Math.toDegrees(velocity);
            timeSamples.add(time);
            positionSamples.add(position);
            velocitySamples.add(velocity);

            // putting these values to telemetry, specifically the FTC Dashboard telemetry, allows
            // them to be graphed in the FTC Dashboard
            telemetry.addData("Position", position);
            telemetry.addData("Velocity", velocity);
            telemetry.addData("Motor Power = ", motorPower);
            telemetry.update();
            idle();
        }

        csvDataFile.headerStrings("Time", "Position", "Velocity");
        csvDataFile.writeData(timeSamples, positionSamples, velocitySamples);
        csvDataFile.closeDataLog();
        // wait until the user stops the opmode
        while (opModeIsActive()) {
            idle();
        }
    }
}
