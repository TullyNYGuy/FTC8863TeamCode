package org.firstinspires.ftc.teamcode.ArmTuning.Opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.acmerobotics.roadrunner.profile.MotionProfile;
import com.acmerobotics.roadrunner.profile.MotionProfileGenerator;
import com.acmerobotics.roadrunner.profile.MotionState;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.ArmConstants;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.SampleArm;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AngleUtilities;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.functions.Function2;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Config
@TeleOp(name = "Apply feedfoward Debug", group = "Arm Tuning")
//@Disabled
public class ArmTuningApplyFeedForwardDebug extends LinearOpMode {

    // Put your variable declarations here

    SampleArm sampleArm;

    double correction = 0;
    double position = 0;
    double velocity = 0;
    PIDCoefficients noPID;

    private FtcDashboard dashboard;

    private PIDFController rotationController;

    public static double ARM_POSITION = 0;
    public static double ARM_VELOCITY = 0;

    @Override
    public void runOpMode() {

        // Put your initializations here
        sampleArm = new SampleArm(hardwareMap, telemetry);

        // Get the instance of the running FTC Dashboard
        dashboard = FtcDashboard.getInstance();
        dashboard.setTelemetryTransmissionInterval(25);
        // merge the normal FTC SDK telemetry with the FTC Dashboard telemetry
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        noPID = new PIDCoefficients(0,0,0);

        rotationController = new PIDFController(noPID, 0, 0, 0, new Function2<Double, Double, Double>() {
            @Override
            public Double invoke(Double position, Double velocity) {
                return ArmConstants.calculateFeedForwardDebug(position, AngleUnit.RADIANS, velocity, telemetry);
            }
        });

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            // apply the feedforward to the motor as the arm is moved around
            //position = sampleArm.getPosition(AngleUnit.RADIANS);
            position = Math.toRadians(ARM_POSITION);
            velocity = ARM_VELOCITY;
            rotationController.setTargetPosition(position);

            correction = rotationController.update(position, velocity);
            sampleArm.armMotor.setPower(correction);

            position = Math.toDegrees(position);

            // putting these values to telemetry, specifically the FTC Dashboard telemetry, allows
            // them to be graphed in the FTC Dashboard
            telemetry.addData("Position", AngleUtilities.convertAngle(position, AngleUnit.DEGREES));
            telemetry.addData("Motor Power = ", correction);
            telemetry.update();
            idle();
        }
    }
}
