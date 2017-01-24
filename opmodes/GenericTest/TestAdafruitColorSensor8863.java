package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Adafruit Color Sensor 8863", group = "Test")
//@Disabled
public class TestAdafruitColorSensor8863 extends LinearOpMode {

    // Put your variable declarations here
    int red = 0;
    int green = 0;
    int blue = 0;

    final int CHANNEL_FOR_LED = 5;

    boolean xButtonIsReleased = false;
    boolean yButtonIsReleased = false;
    boolean aButtonIsReleased = false;
    boolean bButtonIsReleased = false;

    AdafruitColorSensor8863 colorSensor;

    boolean ledState = false;

    ElapsedTime timer;

    String buffer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        colorSensor = new AdafruitColorSensor8863(hardwareMap, RobotConfigMappingForGenericTest.getadafruitColorSensorName(),
            RobotConfigMappingForGenericTest.getCoreDeviceInterfaceName(), CHANNEL_FOR_LED);

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
            colorSensor.displayColorSensorData(telemetry);
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
