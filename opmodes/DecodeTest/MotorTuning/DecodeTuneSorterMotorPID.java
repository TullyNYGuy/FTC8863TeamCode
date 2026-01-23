package org.firstinspires.ftc.teamcode.opmodes.DecodeTest.MotorTuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeTurntableMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDCoefficients;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDFController;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PeriodicSquareWaveGenerator;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Tune Sorter Motor PID", group = "Tune")
//@Disabled
public class DecodeTuneSorterMotorPID extends LinearOpMode {

    // Put your variable declarations her
    DecodeTurntableMotor sorterMotor;
    PeriodicSquareWaveGenerator squareWaveGenerator;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    Telemetry dashboardTelemetry = dashboard.getTelemetry();
    PIDFController controller;
    double newMotorPower = 0;


    @Override
    public void runOpMode() {
        int rpm = 0;
        int nextRPM = 0;
        int courseRPMAdjustment = 60;
        int fineRPMAdjustment = 20;

        // These debounce the buttons so that you only see a single press even if a button is held
        // down for a long time.
        Debouncer debouncedY = new Debouncer();
        Debouncer debouncedB = new Debouncer();
        Debouncer debouncedX = new Debouncer();
        Debouncer debouncedA = new Debouncer();
        Debouncer debouncedDpadUp = new Debouncer();
        Debouncer debouncedDpadDown = new Debouncer();
        Debouncer debouncedDpadLeft = new Debouncer();

        // Put your initializations here
        sorterMotor = new DecodeTurntableMotor(hardwareMap, telemetry);
        sorterMotor.init(null);
        squareWaveGenerator = new PeriodicSquareWaveGenerator(5000, 45);
        controller = new PIDFController(new PIDCoefficients(0.00,0,.00),0,0,.002);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        // Put your calls here - they will not run in a loop
        squareWaveGenerator.start();

        while (opModeIsActive()) {

            controller.setTargetPosition(squareWaveGenerator.getY());
            newMotorPower = controller.update(sorterMotor.getPositionInTermsOfAttachment());
            sorterMotor.setPower(newMotorPower);

            telemetry.addData("Actual RPM = ", sorterMotor.getCommandedRPM());
            sorterMotor.displayTurntableAngle();
            telemetry.addData("kStatic ", controller.getkStatic());
            telemetry.addData("Motor Command ", newMotorPower);
            telemetry.addData(">", "stop to finish");
            telemetry.update();

            // send to the FTC Dashboard. Also makes them graphable
            dashboardTelemetry.addData("Requested Angle ", squareWaveGenerator.getY());
            dashboardTelemetry.addData("Actual Angle ", sorterMotor.getPositionInTermsOfAttachment());
            dashboardTelemetry.addData("Motor Current ", sorterMotor.getCurrent());
            dashboardTelemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
