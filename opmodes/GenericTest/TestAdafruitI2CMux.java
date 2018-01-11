package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.I2cAddr;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitI2CMux;

/**
 * This opmode demonstates how to use the AdafruitI2CMux class to talk to an I2C mux.
 * It assumes that you have 2 adafruit color sensors. One connected to port 0 on the mux board and the
 * other is connected to port 1.
 * The gamepad will be used to switch between the 2 sensors.
 * The phone needs to be configured with an I2C Device on the port the mux is connected to. The name
 * of the mux is "mux".
 * And it also needs an Adafruit Color Sensor on one of the other ports. It does not matter which
 * one. The name of the color sensor is "colorSensor".
 * Lastly, the core device interface module needs to be named "coreDIM".
 *
 * WARNING: When you switch from one color sensor on one port to another color sensor on
 * another port, there will be a period of time needed before the data will be valid for the new
 * sensor. This is because it takes time to get data from the new color sensor and populate the
 * data in the data cache. Reports on the FTC forum indicate that it takes from 50 mSec to 150 mSec
 * to get valid data. If you read before then, you will likely get the data from the first color
 * sensor not the new one. In order to avoid this switch the mux port well before you need valid
 * data or put a delay in your code after switching mux ports and before reading color values.
 *
 */
@TeleOp(name = "Test Adafruit I2C Mux", group = "Test")
@Disabled
public class TestAdafruitI2CMux extends LinearOpMode {

    // Put your variable declarations here
    AdafruitI2CMux mux;
    // The name of the mux. This needs to be used when configuring the phone.
    String muxName = "mux";
    // The default address of the I2C mux. If you change that, then you have to change this address.
    byte muxAddress = 0x70;

    // A color sensor for use in testing the mux. This is my class and is wrapped around ColorSensor.
    // This class integrates both the color sensor and the led control using a discrete output port.
    AdafruitColorSensor colorSensor1;
    AdafruitColorSensor colorSensor2;
    // The next variable will point to one of the two colorSensors above; the one that is active
    AdafruitColorSensor activeColorSensor;
    // The name of the color sensor. This needs to be used when configuring the phone.
    String colorSensorName = "colorSensor";

    // The name of the core device interface module. This needs to be used when configuring the phone.
    String coreDIMName = "coreDIM";
    String activePort;

    // Connect the wires for the led from each color sensor board to the discrete output pin on
    // channels 4 and 5 of the core DIM.
    final int CHANNEL_FOR_LED1 = 5;
    final int CHANNEL_FOR_LED2 = 4;

    boolean xButtonIsReleased = false;
    boolean ledState = false;

    @Override
    public void runOpMode() {

        // Create an I2C mux and initialize it
        mux = new AdafruitI2CMux(hardwareMap, muxName, muxAddress);
        // disconnect all 8 ports
        mux.disablePorts();

        // connect only port 0. A color sensor (colorSensor1) has been wired to that port.
        mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT0);
        // create colorSensor1 and initialize it
        colorSensor1 = new AdafruitColorSensor(colorSensorName, coreDIMName, hardwareMap, CHANNEL_FOR_LED1);

        // Note that there are 2 color sensor objects. But the phone and hardware map only know about
        // one. That is because you are switching between them using the mux. I'm not sure if you could
        // get away with only 1 color sensor object but since I need to initialize both color sensors
        // it is easier to use 2 objects.

        // connect only port 1. A color sensor (colorSensor2) has been wired to that port.
        mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT1);

        // create colorSensor2 and initialize it
        colorSensor2 = new AdafruitColorSensor(colorSensorName, coreDIMName, hardwareMap, CHANNEL_FOR_LED2);
        // connect only port 0. A color sensor has been wired to that port.
        mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT0);

        // start off with the active colorSensor = colorSensor1
        activeColorSensor = colorSensor1;
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Use gamepad X to toggle the led on or off
            if (gamepad1.x) {
                if (xButtonIsReleased) {
                    if (!ledState) {
                        activeColorSensor.turnLEDOn();
                        ledState = true;
                    } else {
                        activeColorSensor.turnLEDOff();
                        ledState = false;
                    }
                    xButtonIsReleased = false;
                }
            } else {
                xButtonIsReleased = true;
            }

            // Use gamepad Y to select the color sensor on port 0
            if (gamepad1.y) {
                mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT0);
                activeColorSensor = colorSensor1;
            }

            // Use gamepad A to select the color sensor on port 1
            if (gamepad1.a) {
                mux.disablePorts();
                mux.selectPort(AdafruitI2CMux.PortNumber.PORT1);
                mux.enablePorts();
                activeColorSensor = colorSensor2;
            }

            // Use gamepad B to disable all ports
            if (gamepad1.b) {
                mux.disablePorts();
            }

            // Display the current values from the sensor
            telemetry.addData("Active Port = ", mux.getActivePortAsString());
            telemetry.addData("Red = ", activeColorSensor.red());
            telemetry.addData("Blue = ", activeColorSensor.blue());
            telemetry.addData("Green = ", activeColorSensor.green());
            telemetry.addData("Opaqueness = ", activeColorSensor.alpha());
            telemetry.addData("Hue = ", activeColorSensor.hue());
            telemetry.addData("Color = ", activeColorSensor.color());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        mux.disablePorts();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
