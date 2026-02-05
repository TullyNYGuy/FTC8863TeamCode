package org.firstinspires.ftc.teamcode.opmodes.DecodeTest.MotorTuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeShooterMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDCoefficients;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDFController;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PeriodicTrapezoidGenerator;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Find Shooter Motor Feedforward kV", group = "Tune")
//@Disabled
public class DecodeFindShooterMotorFeedfoward extends LinearOpMode {

    // Put your variable declarations her
    DecodeShooterMotor shooterMotor;
    PeriodicTrapezoidGenerator trapezoidWave;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    Telemetry dashboardTelemetry = dashboard.getTelemetry();
    PIDFController controller;
    double newMotorPower = 0;
    double normalizedRPM = 0;
    double power = 0;
    double nextPower = 0;
    double coursePowerAdjustment = .1;
    double finePowerAdjustment = .01;
    double actualRPM = 0;
    double kV = 0;


    @Override
    public void runOpMode() {

        // Put your initializations here
        shooterMotor = new DecodeShooterMotor(hardwareMap, telemetry);
        shooterMotor.init(null);
        trapezoidWave = new PeriodicTrapezoidGenerator(4000, .8);
        // kStatic was already found experimentally
        controller = new PIDFController(new PIDCoefficients(0,0,0),0,0,.000);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        // Put your calls here - they will not run in a loop
        trapezoidWave.start();

        while (opModeIsActive()) {
            if (gamepad2.yWasPressed()) {
                nextPower = nextPower + coursePowerAdjustment;
            }
            if (gamepad2.aWasPressed()) {
                nextPower = nextPower - coursePowerAdjustment;
            }
            if (gamepad2.xWasPressed()) {
                nextPower = 1;
            }
            if (gamepad1.bWasPressed()) {
                nextPower = 0;
            }
            if (gamepad2.dpadUpWasPressed()) {
                nextPower = nextPower + finePowerAdjustment;
            }
            if (gamepad2.dpadDownWasPressed()) {
                nextPower = nextPower - finePowerAdjustment;
            }

            // limit the power between 0 and 1
            nextPower = Range.clip(nextPower, 0, 1);

            if (gamepad2.dpadLeftWasPressed()) {
                power = nextPower;
                shooterMotor.setPower(power);
            }

            actualRPM = shooterMotor.getActualRPM();
            kV = actualRPM/power;

            telemetry.addData("gamepad 2 y: + ", coursePowerAdjustment);
            telemetry.addData("gamepad 2 a: - ", coursePowerAdjustment);
            telemetry.addData("gamepad 2 x: 1.0 ", "");
            telemetry.addData("gamepad 2 dpad up: + ", finePowerAdjustment);
            telemetry.addData("gamepad 2 dpad down: - ", finePowerAdjustment);
            telemetry.addData("gamepad 2 dpad left: send power ", "");
            telemetry.addData("gamepad 2 b: stop ", "");

            telemetry.addData("Current Motor Power ", power);
            telemetry.addData("Next Motor Power ", nextPower);
            telemetry.addLine();
            telemetry.addData("Actual RPM ", actualRPM);
            telemetry.addData("Calculated kV ", kV);
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
