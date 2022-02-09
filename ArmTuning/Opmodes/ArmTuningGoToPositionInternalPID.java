package org.firstinspires.ftc.teamcode.ArmTuning.Opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.util.NanoClock;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.SampleArm;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

import java.util.ArrayList;
import java.util.List;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Config
@TeleOp(name = "Go to position", group = "Arm Tuning")
//@Disabled
public class ArmTuningGoToPositionInternalPID extends LinearOpMode {

    // Put your variable declarations here

    SampleArm sampleArm;
    double angleWhenArmIsHorizontal = 0;
    double angleWhenArmIsVertical = 0;

    // These variables will appear in the FTC Dashboard and can be changed via the dashboard
    public static double ARM_POWER = 0.2;
    public static double TARGET_POSITION = 0; // in degrees

    double error = 0;
    double maxError = 0;
    double position = 0;
    double startTime = 0;
    double elapsedTime = 0;

    private FtcDashboard dashboard;

    List<Double> timeSamples = new ArrayList<>();
    List<Double> positionSamples = new ArrayList<>();
    List<Double> errorSamples = new ArrayList<>();

    NanoClock clock = NanoClock.system();

    CSVDataFile csvDataFile = new CSVDataFile("ArmPosition");

    @Override
    public void runOpMode() {

        // Put your initializations here
        sampleArm = new SampleArm(hardwareMap, telemetry);

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
        sampleArm.armMotor.moveToPosition(ARM_POWER, TARGET_POSITION, DcMotor8863.FinishBehavior.HOLD);
        while (opModeIsActive()) {
            // this did not seem to work. It actually made the overshoot slightly greater
            //sampleArm.setSDKkG();
            elapsedTime = clock.seconds() - startTime;
            position = sampleArm.getPosition(AngleUnit.DEGREES);
            error = TARGET_POSITION - position;
            timeSamples.add(elapsedTime);
            positionSamples.add(position);
            errorSamples.add(error);

            // look for the max overshoot. Overshoot is when position > TARGET_POSITION
            if (error < 0 && error < maxError) {
                maxError = error;
            }
            // putting these values to telemetry, specifically the FTC Dashboard telemetry, allows
            // them to be graphed in the FTC Dashboard
            telemetry.addData("Target", TARGET_POSITION);
            telemetry.addData("Position", position);
            telemetry.addData("Error", error);
            telemetry.addData("Max error", maxError);
            telemetry.addData("Write CSV file ", csvDataFile.getPathName());
            telemetry.update();
            idle();
        }

        csvDataFile.headerStrings("Time", "Position", "Error");
        csvDataFile.writeData(timeSamples, positionSamples, errorSamples);
        csvDataFile.closeDataLog();
    }
}
