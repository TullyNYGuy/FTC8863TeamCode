package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor;
import org.firstinspires.ftc.teamcode.Lib.ResQLib.RobotConfigMapping;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Adafruit Color Sensor Deprecated", group = "Test")
@Disabled
public class TestAdafruitColorSensor extends LinearOpMode {

    // Put your variable declarations here
    int red = 0;
    int green = 0;
    int blue = 0;

    final int CHANNEL_FOR_LED = 5;

    boolean xButtonIsReleased = false;

    AdafruitColorSensor colorSensor;

    boolean ledState = false;

    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        colorSensor = new AdafruitColorSensor(RobotConfigMappingForGenericTest.getadafruitColorSensorName(),
                RobotConfigMappingForGenericTest.getCoreDeviceInterfaceName(), hardwareMap, CHANNEL_FOR_LED );

        timer = new ElapsedTime();
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();
        timer.reset();

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
            telemetry.addData("Sat = ", colorSensor.saturation());
            telemetry.addData("Light = ", colorSensor.lightness());
            telemetry.addData("Update interval (mS) = ", "%5.2f", colorSensor.updateTimeTracker.getAverage());
            telemetry.addData("Timer = ", "%3.1f", timer.seconds());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
