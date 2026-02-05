package org.firstinspires.ftc.teamcode.opmodes.DecodeTest.MotorTuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeTurntableMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDCoefficients;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDFController;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PeriodicTrapezoidGenerator;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Tune Sorter Motor Feedforward", group = "Tune")
//@Disabled
public class DecodeTuneSorterMotorFeedfoward extends LinearOpMode {

    // Put your variable declarations her
    DecodeTurntableMotor sorterMotor;
    PeriodicTrapezoidGenerator trapezoidWave;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    Telemetry dashboardTelemetry = dashboard.getTelemetry();
    PIDFController controller;
    double newMotorPower = 0;
    double normalizedRPM = 0;


    @Override
    public void runOpMode() {

        // Put your initializations here
        sorterMotor = new DecodeTurntableMotor(hardwareMap, telemetry);
        sorterMotor.init(null);
        trapezoidWave = new PeriodicTrapezoidGenerator(4000, .8);
        // kStatic was already found experimentally
        controller = new PIDFController(new PIDCoefficients(0,0,0),0,0,.004);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        // Put your calls here - they will not run in a loop
        trapezoidWave.start();

        while (opModeIsActive()) {

            controller.setTargetVelocity(trapezoidWave.getY());
            newMotorPower = controller.update(sorterMotor.getPositionInTermsOfAttachment());
            sorterMotor.setPower(newMotorPower);
            normalizedRPM = sorterMotor.getActualRPM()/ sorterMotor.getNoLoadRPM();


            sorterMotor.displayTurntableAngle(telemetry);
            telemetry.addData("kV ", controller.getkV());
            telemetry.addData("kA ", controller.getkA());
            telemetry.addData("kStatic ", controller.getkStatic());
            telemetry.addData("Motor Command ", newMotorPower);
            telemetry.addData("Actual RPM = ", sorterMotor.getActualRPM());
            telemetry.addData(">", "stop to finish");
            telemetry.update();

            // send to the FTC Dashboard. Also makes them graphable
            dashboardTelemetry.addData("Requested Velocity ", trapezoidWave.getY());
            dashboardTelemetry.addData("Normalized Velocity ", normalizedRPM);
            dashboardTelemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
