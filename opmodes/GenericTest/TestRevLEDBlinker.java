package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.RevLEDBlinker;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RevLEDDriver;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Rev LED Blinker", group = "Test")
//@Disabled
public class TestRevLEDBlinker extends LinearOpMode {

    // Put your variable declarations her
    RevLEDBlinker LEDBlinker;

    @Override
    public void runOpMode() {


        // Put your initializations here
        LEDBlinker = new RevLEDBlinker("LEDport1", "LEDport2", hardwareMap);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        LEDBlinker.setColor(RevLEDDriver.Color.RED);
        LEDBlinker.setfrequency(1);
        LEDBlinker.start();
        while (opModeIsActive()) {
            LEDBlinker.update();
            // Put your calls that need to run in a loop here

            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
