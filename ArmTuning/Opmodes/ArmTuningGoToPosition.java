package org.firstinspires.ftc.teamcode.ArmTuning.Opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.SampleArm;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Go to position", group = "Arm Tuning")
//@Disabled
public class ArmTuningGoToPosition extends LinearOpMode {

    // Put your variable declarations here

    SampleArm sampleArm;
    ElapsedTime timer;
    double angleWhenArmIsHorizontal = 0;
    double angleWhenArmIsVertical = 0;

    // These variables will appear in the FTC Dashboard and can be changed via the dashboard
    public static double ARM_POWER = 0.2;
    public static double TARGET_POSITION = 0; // in degrees

    double error = 0;
    double position = 0;

    private FtcDashboard dashboard;

    @Override
    public void runOpMode() {

        // Put your initializations here
        sampleArm = new SampleArm(hardwareMap, telemetry);
        timer = new ElapsedTime();

        // Get the instance of the running FTC Dashboard
        dashboard = FtcDashboard.getInstance();
        dashboard.setTelemetryTransmissionInterval(25);
        // merge the normal FTC SDK telemetry with the FTC Dashboard telemetry
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        sampleArm.armMotor.moveToPosition(ARM_POWER, TARGET_POSITION, DcMotor8863.FinishBehavior.HOLD);
        while (opModeIsActive()) {
            position = sampleArm.getPosition(AngleUnit.DEGREES);
            error = TARGET_POSITION - position;
            // putting these values to telemetry, specifically the FTC Dashboard telemetry, allows
            // them to be graphed in the FTC Dashboard
            telemetry.addData("Target", TARGET_POSITION);
            telemetry.addData("Position", position);
            telemetry.addData("Error", error);
            telemetry.update();
            idle();
        }
    }
}
