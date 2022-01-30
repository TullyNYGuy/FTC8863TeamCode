package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.RevLEDDriver;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name =  "Test LED Driver", group = "Test")
//@Disabled
public class TestRevLEDDriver extends LinearOpMode {

    // Put your variable declarations her

    @Override
    public void runOpMode() {


        // Put your initializations here
        RevLEDDriver revLEDDriver=new RevLEDDriver("LEDport1","LEDport2",hardwareMap);
        ElapsedTime timer=new ElapsedTime();
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
    revLEDDriver.on(RevLEDDriver.Color.AMBER);
    timer.reset();
        while (opModeIsActive() && timer.seconds() <3) {

            // Put your calls that need to run in a loop here

            telemetry.addData("color=AMBER","!");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
        revLEDDriver.on(RevLEDDriver.Color.GREEN);
        timer.reset();
        while (opModeIsActive() && timer.seconds() <3) {

            // Put your calls that need to run in a loop here

            telemetry.addData("color=GREEN","!");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
        revLEDDriver.on(RevLEDDriver.Color.RED);
        timer.reset();
        while (opModeIsActive() && timer.seconds() <3) {

            // Put your calls that need to run in a loop here

            telemetry.addData("color=RED","!");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
        revLEDDriver.off();
        timer.reset();
        while (opModeIsActive() && timer.seconds() <3) {

            // Put your calls that need to run in a loop here

            telemetry.addData("color=off","!");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
        revLEDDriver.off();
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
