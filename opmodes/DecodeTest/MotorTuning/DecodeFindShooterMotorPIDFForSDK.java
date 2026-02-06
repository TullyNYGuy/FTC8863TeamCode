package org.firstinspires.ftc.teamcode.opmodes.DecodeTest.MotorTuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeShooterMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDCoefficients;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDFController;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PeriodicTrapezoidGenerator;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Find Shooter Motor Constant for SDK", group = "Tune")
//@Disabled
public class DecodeFindShooterMotorPIDFForSDK extends LinearOpMode {

    // Put your variable declarations her
    DecodeShooterMotor shooterMotor;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    Telemetry dashboardTelemetry = dashboard.getTelemetry();
    PIDFCoefficients pidfCoefficients;
    double currentkF = 0;
    double nextkF = 0;
    double coursekFAdjustment = 1.0;
    double finekFAdjustment = .1;
    double currentkP = 0;
    double nextkP = 0;
    double coursekPAdjustment = .01;
    double finekPAdjustment = .001;
    double actualRPM = 0;
    double nextRPM = 0;
    double currentRPM = 0;
    int courseRPMAdjustment = 500;
    int fineRPMAdjustment = 100;

    @Override
    public void runOpMode() {

        // Put your initializations here
        shooterMotor = new DecodeShooterMotor(hardwareMap, telemetry);
        shooterMotor.init(null);
        pidfCoefficients = new PIDFCoefficients(0,0,0, 0);
        shooterMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {

            // adjust kF
            if (gamepad2.yWasPressed()) {
                nextkF = nextkF + coursekFAdjustment;
            }
            if (gamepad2.aWasPressed()) {
                nextkF = nextkF - coursekFAdjustment;
            }
            if (gamepad2.xWasPressed()) {
                nextkF = nextkF + finekFAdjustment;
            }
            if (gamepad2.bWasPressed()) {
                nextkF = nextkF - finekFAdjustment;
            }
            if (gamepad2.rightStickButtonWasPressed()) {
                pidfCoefficients = new PIDFCoefficients(currentkP, 0,0, nextkF);
                shooterMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
                currentkF = nextkF;
            }

            // adjust kP
            if (gamepad2.dpadUpWasPressed()) {
                nextkP = nextkP + coursekPAdjustment;
            }
            if (gamepad2.dpadDownWasPressed()) {
                nextkP = nextkP - coursekPAdjustment;
            }
            if (gamepad2.dpadLeftWasPressed()) {
                nextkP = nextkP + finekFAdjustment;
            }
            if (gamepad2.dpadRightWasPressed()) {
                nextkP = nextkP - finekFAdjustment;
            }
            if (gamepad2.leftStickButtonWasPressed()) {
                pidfCoefficients = new PIDFCoefficients(nextkP, 0,0, currentkF);
                shooterMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
                currentkP = nextkP;
            }

            // adjust RPM
            if (gamepad1.dpadLeftWasPressed()) {
                nextRPM = nextRPM + fineRPMAdjustment;
            }
            if (gamepad1.dpadRightWasPressed()) {
                nextRPM = nextRPM - fineRPMAdjustment;
            }
            if (gamepad1.dpadUpWasPressed()) {
                nextRPM = nextRPM + courseRPMAdjustment;
            }
            if (gamepad1.dpadDownWasPressed()) {
                nextRPM = nextRPM - courseRPMAdjustment;
            }
            if (gamepad1.leftStickButtonWasPressed()) {
                shooterMotor.setRPM(nextRPM);
                currentRPM = nextRPM;
            }
            actualRPM = shooterMotor.getActualRPM();

            telemetry.addData("gamepad 2 y/a: +/- kF", coursekFAdjustment);
            telemetry.addData("gamepad 2 x/b: +/- kF ", finekFAdjustment);
            telemetry.addData("left stick button: ", "send kF");
            telemetry.addLine();
            telemetry.addData("gamepad 2 dpad up/down: +/- kP ", coursekPAdjustment);
            telemetry.addData("gamepad 2 dpad left/right: +/- kP ", finekPAdjustment);
            telemetry.addData("right stick button ", "send kP");
            telemetry.addLine();
            telemetry.addData("gamepad 1 dpad up/down: +/- RPM ", courseRPMAdjustment);
            telemetry.addData("gamepad 1 dpad left/right: +/- ", fineRPMAdjustment);
            telemetry.addData("gamepad 1 left stick button ", " send RPM");
            telemetry.addLine();
            telemetry.addData("Current kF ", currentkF);
            telemetry.addData("Next kF ", nextkF);
            telemetry.addData("Current kP ", currentkP);
            telemetry.addData("Next kP ", nextkP);
            telemetry.addLine();
            telemetry.addData("commanded RPM ", currentRPM);
            telemetry.addData("Actual RPM ", actualRPM);
            telemetry.addData(">", "stop to finish");
            telemetry.update();

            // send to the FTC Dashboard. Also makes them graphable
            dashboardTelemetry.addData("Motor RPM ", actualRPM);
            dashboardTelemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
