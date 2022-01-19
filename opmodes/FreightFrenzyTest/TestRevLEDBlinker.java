package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.RevLED;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RevLEDBlinker;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Rev LED Blinker", group = "Test")
@Disabled
public class TestRevLEDBlinker extends LinearOpMode {

    // Put your variable declarations her
    RevLEDBlinker led;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        led = new RevLEDBlinker(1, RevLED.Color.GREEN, hardwareMap, "ledPort1", "ledPort2");
        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 5000) {
            telemetry.addData("LED is ", "green");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();
            led.update();
            idle();
        }

        timer.reset();
        led.setColor(RevLED.Color.RED);
        while (opModeIsActive() && timer.milliseconds() < 5000) {
            telemetry.addData("LED is ", "red");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();
            led.update();
            idle();
        }

        timer.reset();
        led.setColor(RevLED.Color.AMBER);
        while (opModeIsActive() && timer.milliseconds() < 5000) {
            telemetry.addData("LED is ", "amber");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();
            led.update();
            idle();
        }

        timer.reset();
        led.setColor(RevLED.Color.AMBER);
        led.setFrequency(4.0);
        while (opModeIsActive() && timer.milliseconds() < 5000) {
            telemetry.addData("LED is ", "amber fast");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();
            led.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
