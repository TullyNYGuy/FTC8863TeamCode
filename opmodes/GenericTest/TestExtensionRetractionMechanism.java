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
 *
 *
 */
@TeleOp(name = "Test Extension Retraction Mechanism", group = "Test")
//@Disabled
public class TestExtensionRetractionMechanism extends LinearOpMode {

    // Put your variable declarations here
    public ExtensionRetractionMechanism extensionRetractionMechanism;
    public DataLogging logFile;
    public double spoolDiameter = 1.25 *25.4;
    public ElapsedTime timer;
    public double startTime = 0;
    public double endUpTime = 0;
    public double endDownTime = 0;

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionRetractionMechanism = new ExtensionRetractionMechanism(hardwareMap,telemetry,"extensionRetraction",
                "extensionLimitSwitch", "retractionLimitSwitch", "extensionRetractionMotor",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);
        logFile = new DataLogging("ExtensionRetractionTest", telemetry);
        timer = new ElapsedTime();
        extensionRetractionMechanism.setDataLog(logFile);
        extensionRetractionMechanism.enableDataLogging();
        extensionRetractionMechanism.setResetPower(+0.1);
        extensionRetractionMechanism.setRetractionPower(+1.0);
        extensionRetractionMechanism.setExtensionPower(-1.0);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        extensionRetractionMechanism.testReset(this);
        sleep(3000);
        timer.reset();
        extensionRetractionMechanism.testExtension(this);
        endUpTime = timer.milliseconds();
        sleep(3000);
        timer.reset();
        extensionRetractionMechanism.testRetraction(this);
        endDownTime = timer.milliseconds();

//        while(opModeIsActive()) {
//
//            // Put your calls that need to run in a loop here
//
//            telemetry.addData(">", "Press Stop to end test." );
//
//            telemetry.update();
//
//            idle();
//        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData("time up = ", endUpTime);
        telemetry.addData("time down = ", endDownTime);
        telemetry.addData(">", "Done");
        telemetry.update();
        sleep(5000);
    }
}
