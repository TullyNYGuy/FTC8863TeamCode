package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.LoggingData;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Logging Data", group = "Test")
//@Disabled
public class TestLoggingData extends LinearOpMode {

    // Put your variable declarations here
    LoggingData testLoggingData;
    @Override
    public void runOpMode() {


        // Put your initializations here
        testLoggingData = new LoggingData("test", "/Phone/FIRST/Logs");
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        testLoggingData.timerStart();
        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here

            testLoggingData.logData("Test");

            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }
        testLoggingData.closeTraceLog();
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
