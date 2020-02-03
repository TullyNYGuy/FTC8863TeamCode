package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.openftc.revextensions2.ExpansionHubEx;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Expansion hub voltage drop", group = "Test")
//@Disabled
public class TestRevVoltageMonitors extends LinearOpMode {

    // Put your variable declarations her
    ExpansionHubEx expansionHubPrimary;
    ExpansionHubEx expansionHubSecondary;

    CSVDataFile csvDataFile;

    @Override
    public void runOpMode() {


        // Put your initializations here
        expansionHubPrimary = hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 1");
        expansionHubSecondary = hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 2");

        csvDataFile = new CSVDataFile("RevVoltageCurrentMonitors", telemetry);
        csvDataFile.headerStrings("Primary Voltage", "Secondary Voltage", "Primary Current", "Secondary Current");

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            csvDataFile.writeData(
                    expansionHubPrimary.read12vMonitor(ExpansionHubEx.VoltageUnits.VOLTS),
                    expansionHubSecondary.read12vMonitor(ExpansionHubEx.VoltageUnits.VOLTS),
                    expansionHubPrimary.getTotalModuleCurrentDraw((ExpansionHubEx.CurrentDrawUnits.AMPS)),
                    expansionHubSecondary.getTotalModuleCurrentDraw((ExpansionHubEx.CurrentDrawUnits.AMPS))
            );
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        csvDataFile.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
