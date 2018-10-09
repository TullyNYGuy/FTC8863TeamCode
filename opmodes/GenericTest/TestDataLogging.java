package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Data Logging", group = "Test")
//@Disabled
public class TestDataLogging extends LinearOpMode {

    // Put your variable declarations here
    DataLogging testDataLogging;

    @Override
    public void runOpMode() {


        // Put your initializations here
        testDataLogging = new DataLogging("test", telemetry);
        if(testDataLogging.isStatusOK()) {
            // Wait for the start button
            telemetry.addData(">", "Press Start to run" );
            telemetry.update();
            waitForStart();

            // Put your calls here - they will not run in a loop
            testDataLogging.startTimer();

            while(opModeIsActive()) {

                // Put your calls that need to run in a loop here

                testDataLogging.logData("Test");

                telemetry.addData(">", "Press Stop to end test." );
                telemetry.update();

                idle();
            }
            testDataLogging.closeDataLog();
            // Put your cleanup code here - it runs as the application shuts down
            telemetry.addData(">", "Done");
            telemetry.update();
        } else {
            telemetry.addData("Problem with log file", "!");
            telemetry.addData("DONE", "!");
            telemetry.update();
            sleep(5000);
        }
    }
}
