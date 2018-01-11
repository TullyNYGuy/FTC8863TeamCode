package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor;
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
 * "colorSensor". The type of sensor is "Adafruit Color Sensor".
 * Lastly, the core device interface module needs to be named "coreDIM" in your phone configuration.
 * <p>
 * The code will create 4 color sensor objects as you might expect. However, once the objects are
 * created, for some reason I don't understand, you always have to communicate with the sensor using
 * the last object created. In this case I create colorSensor1 to 4 in order. Once I create
 * colorSensor2, I can't communicate with color sensor 1 using the colorSensor1 object. I have to
 * use colorSensor2. The selection of which color sensor is communicated with is done only by
 * switching the mux port. Similarly, once I create colorSensor4, I have to use that object to
 * communicate with all 4 color sensors, using the mux port selection to pick which sensor I want
 * to talk to. I'm not sure I understand why. You think you could switch between objects freely.
 * I'm guessing there must be something in the way the hardware map and the objects it creates that
 * causes this issue.
 *
 */
@TeleOp(name = "Test Adafruit I2C Mux 4 Color Sensors", group = "Test")
@Disabled
public class TestAdafruitI2CMux4AdafruitColorSensor extends LinearOpMode {

    // Put your variable declarations here
    AdafruitI2CMux mux;
    private DeviceInterfaceModule coreDIM;

    // The name of the mux. This needs to be used when configuring the phone.
    String muxName = "mux";
    // The default address of the I2C mux. If you change that, then you have to change this address.
    byte muxAddress = 0x70;

    // A color sensor for use in testing the mux. This is my class and is wrapped around ColorSensor.
    // This class integrates both the color sensor and the led control using a discrete output port.
    AdafruitColorSensor colorSensor1;
    AdafruitColorSensor colorSensor2;
    AdafruitColorSensor colorSensor3;
    AdafruitColorSensor colorSensor4;
    // The next variable will point to one of the two colorSensors above; the one that is active
    AdafruitColorSensor activeColorSensor;
    AdafruitColorSensor actualColorSensor;
    // The name of the color sensor. This needs to be used when configuring the phone.
    String colorSensorName = "colorSensor";

    // The name of the core device interface module. This needs to be used when configuring the phone.
    String coreDIMName = "coreDIM";

    // Connect the wires for the led from the color sensor board to the discrete output pin on
    // channel of the core DIM listed below.
    final int CHANNEL_FOR_LED1 = 1;
    final int CHANNEL_FOR_LED2 = 2;
    final int CHANNEL_FOR_LED3 = 3;
    final int CHANNEL_FOR_LED4 = 4;
    int ioChannelForLed = CHANNEL_FOR_LED1;

    boolean xButtonIsReleased = false;
    boolean leftBumperIsReleased = false;
    boolean ledOn = false;

    @Override
    public void runOpMode() {

        coreDIM = hardwareMap.deviceInterfaceModule.get(coreDIMName);
        coreDIM.setDigitalChannelMode(ioChannelForLed, DigitalChannelController.Mode.OUTPUT);
        // Delay so the previous line can finish before setting the led off. Otherwise the LED does
        // not get shut off.
        delay(100);

        // Create an I2C mux and initialize it
        mux = new AdafruitI2CMux(hardwareMap, muxName, muxAddress);
        // disconnect all 8 ports
        mux.disablePorts();

        // connect only port 0. A color sensor (colorSensor1) has been wired to that port of the mux.
        mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT0);
        // create colorSensor1 and initialize it
        colorSensor1 = new AdafruitColorSensor(colorSensorName, coreDIMName, hardwareMap, CHANNEL_FOR_LED1);
        coreDIM.setDigitalChannelMode(CHANNEL_FOR_LED1, DigitalChannelController.Mode.OUTPUT);
        // Delay so the previous line can finish before setting the led off. Otherwise the LED does
        // not get shut off.
        delay(100);

        // Note that there are 4 color sensor objects. But the phone and hardware map only know about
        // one. That is because you are switching between them using the mux. I'm not sure if you could
        // get away with only 1 color sensor object but since I need to initialize both color sensors
        // it is easier to use 4 objects.

        // connect only port 1. A color sensor (colorSensor2) has been wired to that port of the mux.
        mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT1);
        // create colorSensor2 and initialize it
        colorSensor2 = new AdafruitColorSensor(colorSensorName, coreDIMName, hardwareMap, CHANNEL_FOR_LED2);
        coreDIM.setDigitalChannelMode(CHANNEL_FOR_LED2, DigitalChannelController.Mode.OUTPUT);
        // Delay so the previous line can finish before setting the led off. Otherwise the LED does
        // not get shut off.
        delay(100);

        // connect only port 2. A color sensor (colorSensor3) has been wired to that port of the mux.
        mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT2);
        // create colorSensor2 and initialize it
        colorSensor3 = new AdafruitColorSensor(colorSensorName, coreDIMName, hardwareMap, CHANNEL_FOR_LED3);
        coreDIM.setDigitalChannelMode(CHANNEL_FOR_LED3, DigitalChannelController.Mode.OUTPUT);
        // Delay so the previous line can finish before setting the led off. Otherwise the LED does
        // not get shut off.
        delay(100);

        // connect only port 3. A color sensor (colorSensor4) has been wired to that port of the mux.
        mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT3);
        // create colorSensor2 and initialize it
        colorSensor4 = new AdafruitColorSensor(colorSensorName, coreDIMName, hardwareMap, CHANNEL_FOR_LED4);
        coreDIM.setDigitalChannelMode(CHANNEL_FOR_LED4, DigitalChannelController.Mode.OUTPUT);
        // Delay so the previous line can finish before setting the led off. Otherwise the LED does
        // not get shut off.
        delay(100);

        // connect only port 0. A color sensor has been wired to that port.
        mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT0);

        // Use the last color sensor created to talk to any of the 4 color sensors. The color sensor
        // you talk to is the one connected to the active mux port. You will not be able to use
        // color sensors 1-3 to talk to a color sensor. See intro for description.
        activeColorSensor = colorSensor4;
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Use gamepad left bumper to toggle the led on or off
            if (gamepad1.left_bumper) {
                if (leftBumperIsReleased) {
                    toggleLED(ioChannelForLed);
                    leftBumperIsReleased = false;
                }
            } else {
                leftBumperIsReleased = true;
            }

            // Use gamepad Y to select the color sensor on port 0
            if (gamepad1.y) {
                mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT0);
                actualColorSensor = colorSensor1;
                colorSensorName = "Color Sensor 1";
                ioChannelForLed = CHANNEL_FOR_LED1;
            }

            // Use gamepad x to select the color sensor on port 1
            // I'm using the alternative set of methods to select and enable a port just to
            // show how they are used.
            if (gamepad1.x) {
                mux.disablePorts();
                mux.selectPort(AdafruitI2CMux.PortNumber.PORT1);
                mux.enablePorts();
                actualColorSensor = colorSensor2;
                colorSensorName = "Color Sensor 2";
                ioChannelForLed = CHANNEL_FOR_LED2;
            }

            // Use gamepad a to select the color sensor on port 2
            if (gamepad1.a) {
                mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT2);
                actualColorSensor = colorSensor3;
                colorSensorName = "Color Sensor 3";
                ioChannelForLed = CHANNEL_FOR_LED3;
            }

            // Use gamepad B to select the color sensor on port 3
            if (gamepad1.b) {
                mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT3);
                actualColorSensor = colorSensor4;
                colorSensorName = "Color Sensor 4";
                ioChannelForLed = CHANNEL_FOR_LED4;
            }

            // Use gamepad right bumper to disable all ports
            if (gamepad1.right_bumper) {
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

    /**
     * Turn the led on the color sensor on. The led pin on the color sensor is connected to a
     * digital input port on the core DIM.
     */
    public void turnLEDOn(int ioChannelForLed) {
        // only put commands on the bus if there is a change to be made
        if (!this.ledOn) {
            // the led is off so it makes sense to turn it on
            this.ledOn = true;
            coreDIM.setDigitalChannelState(ioChannelForLed, ledOn);
        }
    }

    /**
     * Turn the led on the color sensor off. The led pin on the color sensor is connected to a
     * digital input port on the core DIM.
     */
    public void turnLEDOff(int ioChannelForLed) {
        // only put commands on the bus if there is a change to be made
        if (this.ledOn) {
            // the led is on so it makes sense to turn it off
            this.ledOn = false;
            coreDIM.setDigitalChannelState(ioChannelForLed, ledOn);
        }
    }

    /**
     * Toggle the led on the color sensor. The led pin on the color sensor is connected to a
     * digital input port on the core DIM.
     */
    public void toggleLED(int ioChannelForLED) {
        if (this.ledOn) {
            turnLEDOff(ioChannelForLED);
        } else {
            turnLEDOn(ioChannelForLED);
        }
    }

    /**
     * delay() implements a delay
     */
    void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
