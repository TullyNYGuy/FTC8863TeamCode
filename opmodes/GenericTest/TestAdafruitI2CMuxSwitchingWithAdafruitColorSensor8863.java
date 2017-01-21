package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitI2CMux;

/**
 * This opmode demonstates how to use the AdafruitI2CMux class to talk to an I2C mux.
 * It assumes that you have 4 adafruit color sensors connected on these ports of the mux (not the
 * core device interface module):
 * colorSensor1 - port 0
 * colorSensor2 - port 1
 * colorSensor3 - port 2
 * colorSensor4 - port 4
 * The gamepad will be used to switch between the 4 sensors.
 * The phone needs to be configured with an "I2C Device" on the core device interface module port
 * that the mux is connected to. The name of the mux is "mux".
 * It also needs an Adafruit Color Sensor configured on one of the other I2C ports of the core
 * device interface module. It does not matter which port. The name of the color sensor is
 * "colorSensor". The type of sensor is "I2C Device".
 * Lastly, the core device interface module needs to be named "coreDIM" in your phone configuration.
 *
 */
@TeleOp(name = "Test Adafruit I2C Mux 1 AdafruitColorSensor8863", group = "Test")
//@Disabled
public class TestAdafruitI2CMuxSwitchingWithAdafruitColorSensor8863 extends LinearOpMode {

    // Put your variable declarations here
    AdafruitI2CMux mux;
    // The name of the mux. This needs to be used when configuring the phone.
    String muxName = "mux";
    // The default address of the I2C mux. If you change that, then you have to change this address.
    byte muxAddress = 0x70;

    // A color sensor for use in testing the mux. This is my class and is wrapped around ColorSensor.
    // This class integrates both the color sensor and the led control using a discrete output port.
    AdafruitColorSensor8863 colorSensor1;

    // The next variable will point to one of the two colorSensors above; the one that is active
    AdafruitColorSensor8863 activeColorSensor;
    // The name of the color sensor. This needs to be used when configuring the phone.
    String colorSensorName = "colorSensor";

    // The name of the core device interface module. This needs to be used when configuring the phone.
    String coreDIMName = "coreDIM";

    // Connect the wires for the led from the color sensor board to the discrete output pin on
    // channel of the core DIM listed below.
    final int CHANNEL_FOR_LED1 = 1;

    boolean xButtonIsReleased = false;
    boolean ledState = false;

    int delayInMsec = 100;

    @Override
    public void runOpMode() {

        // Create an I2C mux and initialize it
        mux = new AdafruitI2CMux(hardwareMap, muxName, muxAddress);
        // disconnect all 8 ports
        mux.disablePorts();

        // connect only port 0. A color sensor (colorSensor1) has been wired to that port of the mux.
        mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT0);
        delay(delayInMsec);
        // create colorSensor1 and initialize it
        colorSensor1 = new AdafruitColorSensor8863(hardwareMap, colorSensorName, coreDIMName, CHANNEL_FOR_LED1);
        // I'm having trouble with the color sensors getting initialized. A delay seem to help some
        // of the time but not all.
        delay(delayInMsec);

        // start off with the active colorSensor = colorSensor1
        // This is used to point to one of the four color sensors; the one that is active
        activeColorSensor = colorSensor1;

        if (activeColorSensor.checkDeviceId()) {
            telemetry.addData("Color Sensor 1 id is ", "ok");
        } else {
            telemetry.addData("Color Sensor 1 id is ", "BAD!");
        }

        if (activeColorSensor.isDataValid()) {
            telemetry.addData("Color Sensor 1 data ", "valid");
        } else {
            telemetry.addData("Color Sensor 1 data ", "NOT VALID!");
        }
        
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
                        // note the activeColorSensor points to the colorSensor object that is active
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

            // Use gamepad right bumper to disable all ports
            if (gamepad1.right_bumper) {
                mux.disablePorts();
            }

            // Display the current values from the sensor
            telemetry.addData("Port = ", mux.getActivePortAsString());
            activeColorSensor.displayColorSensorData(telemetry);

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        mux.disablePorts();
        telemetry.addData(">", "Done");
        telemetry.update();

    }

    void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
