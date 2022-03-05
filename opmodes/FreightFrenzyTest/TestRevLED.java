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
@TeleOp(name = "Test Rev LED", group = "Test")
@Disabled
public class TestRevLED extends LinearOpMode {

    // Put your variable declarations her
    RevLED led;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        led = new RevLED(hardwareMap,  "ledPort1", "ledPort2");
        timer = new ElapsedTime();

        led.off();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        led.on(RevLED.Color.GREEN);
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 5000) {
            telemetry.addData("LED is ", "green");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();
            idle();
        }

        timer.reset();
        led.on(RevLED.Color.RED);
        while (opModeIsActive() && timer.milliseconds() < 5000) {
            telemetry.addData("LED is ", "red");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();
            idle();
        }

        timer.reset();
        led.on(RevLED.Color.AMBER);
        while (opModeIsActive() && timer.milliseconds() < 5000) {
            telemetry.addData("LED is ", "amber");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();
            idle();
        }

        led.off();
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 5000) {
            telemetry.addData("LED is ", "off");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
