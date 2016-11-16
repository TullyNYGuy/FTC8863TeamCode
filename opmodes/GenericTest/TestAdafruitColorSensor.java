package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Adafruit Color Sensor", group = "Test")
//@Disabled
public class TestAdafruitColorSensor extends LinearOpMode {

    // Put your variable declarations here
    int red = 0;
    int green = 0;
    int blue = 0;

    final int CHANNEL_FOR_LED = 5;

    boolean xButtonIsReleased = false;

    AdafruitColorSensor colorSensor;

    boolean ledState = false;

    @Override
    public void runOpMode() {


        // Put your initializations here
        colorSensor = new AdafruitColorSensor("colorSensor", "coreDIM", hardwareMap, CHANNEL_FOR_LED );
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here
            // Use gamepad X to toggle the led on or off
            if (gamepad1.x) {
                if (xButtonIsReleased) {
                    if (!ledState) {
                        colorSensor.turnLEDOn();
                        ledState = true;
                    } else {
                        colorSensor.turnLEDOff();
                        ledState = false;
                    }
                    xButtonIsReleased = false;
                }
            } else {
                xButtonIsReleased = true;
            }

            // Display the current values from the sensor
            telemetry.addData("Red = ", colorSensor.red());
            telemetry.addData("Blue = ", colorSensor.blue());
            telemetry.addData("Green = ", colorSensor.green());
            telemetry.addData("Opaqueness = ", colorSensor.alpha());
            telemetry.addData("Hue = ", colorSensor.hue());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
