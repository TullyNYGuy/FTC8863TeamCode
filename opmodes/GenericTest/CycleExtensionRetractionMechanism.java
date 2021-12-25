package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Cycle Extension Retraction Mechanism", group = "Test")
@Disabled
public class CycleExtensionRetractionMechanism extends LinearOpMode {

    // Put your variable declarations here
    public ExtensionRetractionMechanism extensionRetractionMechanism;
    public DataLogging logFile;
    public double spoolDiameter = 1.25 * 25.4;
    public ElapsedTime timer;
    public double startTime = 0;
    public double endUpTime = 0;
    public double endDownTime = 0;

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionRetractionMechanism = new ExtensionRetractionMechanism(hardwareMap, telemetry, "extensionRetraction",
                "extensionLimitSwitch", "retractionLimitSwitch", "extensionRetractionMotor",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);
        //extensionRetractionMechanism.reverseMotor();
        logFile = new DataLogging("ExtensionRetractionCycle", telemetry);
        timer = new ElapsedTime();
        extensionRetractionMechanism.setDataLog(logFile);
        extensionRetractionMechanism.enableDataLogging();
        extensionRetractionMechanism.setResetPower(-0.1);
        extensionRetractionMechanism.setRetractionPower(-0.1);
        extensionRetractionMechanism.setExtensionPower(+0.1);
        extensionRetractionMechanism.setExtensionPositionInEncoderCounts(2650.0);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        extensionRetractionMechanism.testCycleFullExtensionRetraction(this, 100, 3000);
        telemetry.update();

        // sit and wait for the user to read the results
        while (opModeIsActive()) {
            idle();
        }
    }
}
