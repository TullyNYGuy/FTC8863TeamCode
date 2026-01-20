package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PeriodicSquareWaveGenerator;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Square Wave", group = "Test")
//@Disabled
public class DecodeTestSquareWave extends LinearOpMode {

    // Put your variable declarations her

    public PeriodicSquareWaveGenerator periodicSquareWave;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    Telemetry dashboardTelemetry = dashboard.getTelemetry();


    @Override
    public void runOpMode() {


        // Put your initializations here

        periodicSquareWave = new PeriodicSquareWaveGenerator(1000, 2);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        periodicSquareWave.start();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here


            telemetry.addData("Y Value ", periodicSquareWave.getY());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();
            dashboardTelemetry.addData("Y Value ", periodicSquareWave.getY());
            dashboardTelemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
