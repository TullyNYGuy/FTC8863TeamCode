package org.firstinspires.ftc.teamcode.opmodes.DecodeTest.MotorTuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeShooterMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDCoefficients;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDFController;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PeriodicSquareWaveGenerator;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Tune Shooter Motor PIDF", group = "Tune")
//@Disabled
public class DecodeTuneShooterMotorPIDF extends LinearOpMode {

    // Put your variable declarations her
    DecodeShooterMotor shooterMotor;
    PeriodicSquareWaveGenerator squareWaveGenerator;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    Telemetry dashboardTelemetry = dashboard.getTelemetry();
    PIDFController controller;
    double newMotorPower = 0;
    double actualRPM = 0;
    double commandedRPM = 0;

    @Override
    public void runOpMode() {


        // Put your initializations here
        shooterMotor = new DecodeShooterMotor(hardwareMap, telemetry);
        shooterMotor.init(null);
        // set up the velocity steps as 2000 RPM on top of a constant 3300 RPM (ie 3300 RPM to 5300 RPM)
        squareWaveGenerator = new PeriodicSquareWaveGenerator(5000, 500, 3500);
        controller = new PIDFController(new PIDCoefficients(0.0032,0,.00),.000185,0,0);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        // Put your calls here - they will not run in a loop
        squareWaveGenerator.start();

        while (opModeIsActive()) {

            actualRPM = shooterMotor.getActualRPM();
            commandedRPM = squareWaveGenerator.getY();

            controller.setTargetVelocity(commandedRPM);
            controller.setTargetPosition(commandedRPM);
            controller.setOutputBounds(-1,1);
            newMotorPower = controller.update(actualRPM, actualRPM);
            shooterMotor.setPower(newMotorPower);

            telemetry.addData("Actual RPM = ", shooterMotor.getActualRPM());
            telemetry.addData("Motor Command ", newMotorPower);
            telemetry.addData(">", "stop to finish");
            telemetry.update();

            // send to the FTC Dashboard. Also makes them graphable
            dashboardTelemetry.addData("Requested RPM ", squareWaveGenerator.getY());
            dashboardTelemetry.addData("Actual RPM ", actualRPM);
            dashboardTelemetry.addData("Motor command ", newMotorPower);
            dashboardTelemetry.addData("Motor Current ", shooterMotor.getCurrent());
            dashboardTelemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
