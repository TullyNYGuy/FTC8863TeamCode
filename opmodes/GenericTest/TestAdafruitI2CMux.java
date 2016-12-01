package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.I2cAddr;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitI2CMux;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Adafruit I2C Mux", group = "Test")
//@Disabled
public class TestAdafruitI2CMux extends LinearOpMode {

    // Put your variable declarations here
    AdafruitI2CMux mux;
    String muxName = "mux";
    byte muxAddress = 0x70;

    // A color sensor for use in testing the mux
    AdafruitColorSensor colorSensor1;
    int red = 0;
    int green = 0;
    int blue = 0;
    final int CHANNEL_FOR_LED = 5;
    boolean xButtonIsReleased = false;
    boolean ledState = false;

    @Override
    public void runOpMode() {


        // Put your initializations here
        mux = new AdafruitI2CMux(hardwareMap, muxName, muxAddress);
        // disconnect all 8 ports
        mux.disablePorts();
        // connect only port 0. A color sensor has been wired to that port.
        mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT0);

        colorSensor1 = new AdafruitColorSensor(RobotConfigMappingForGenericTest.getadafruitColorSensorName(),
                RobotConfigMappingForGenericTest.getCoreDeviceInterfaceName(), hardwareMap, CHANNEL_FOR_LED );
        
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
                        colorSensor1.turnLEDOn();
                        ledState = true;
                    } else {
                        colorSensor1.turnLEDOff();
                        ledState = false;
                    }
                    xButtonIsReleased = false;
                }
            } else {
                xButtonIsReleased = true;
            }

            // Display the current values from the sensor
            telemetry.addData("Red = ", colorSensor1.red());
            telemetry.addData("Blue = ", colorSensor1.blue());
            telemetry.addData("Green = ", colorSensor1.green());
            telemetry.addData("Opaqueness = ", colorSensor1.alpha());
            telemetry.addData("Hue = ", colorSensor1.hue());
            telemetry.addData("Color = ", colorSensor1.color());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
