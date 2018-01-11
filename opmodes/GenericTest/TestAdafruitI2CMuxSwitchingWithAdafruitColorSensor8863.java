package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

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
 * The phone needs to be configured with an "I2C Device" on the core device interface module port
 * that the mux is connected to. The name of the mux is "mux".
 * It also needs an Adafruit Color Sensor configured on one of the other I2C ports of the core
 * device interface module. It does not matter which port. The name of the color sensor is
 * "colorSensor". The type of sensor is "I2C Device".
 * Lastly, the core device interface module needs to be named "coreDIM" in your phone configuration.
 * <p>
 * You can test that that mux is switching by disconnecting the SDA and SCL pins from a color sensor.
 * If it comes back with a failure on the port then you know the switching is working.
 *
 * WARNING: When you switch from one color sensor on one port to another color sensor on
 * another port, there will be a period of time needed before the data will be valid for the new
 * sensor. This is because it takes time to get data from the new color sensor and populate the
 * data in the data cache. Reports on the FTC forum indicate that it takes from 50 mSec to 150 mSec
 * to get valid data. If you read before then, you will likely get the data from the first color
 * sensor not the new one. In order to avoid this switch the mux port well before you need valid
 * data or put a delay in your code after switching mux ports and before reading color values.
 */
@TeleOp(name = "Test Adafruit I2C Mux switching", group = "Test")
@Disabled
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
    AdafruitColorSensor8863 colorSensor2;
    AdafruitColorSensor8863 colorSensor3;
    AdafruitColorSensor8863 colorSensor4;

    // The next variable will point to one of the two colorSensors above; the one that is active
    AdafruitColorSensor8863 activeColorSensor;

    // The name of the color sensor. This needs to be used when configuring the phone.
    String colorSensorName = "colorSensor";

    // The name of the core device interface module. This needs to be used when configuring the phone.
    String coreDIMName = "coreDIM";

    final int CHANNEL_FOR_LED1 = 0;
    final int CHANNEL_FOR_LED2 = 1;
    final int CHANNEL_FOR_LED3 = 2;
    final int CHANNEL_FOR_LED4 = 3;

    // controls the number of color sensors attached to the mux. This can range from 1 to 4.
    int numberOfColorSensors = 4;

    @Override
    public void runOpMode() {

        // Create an I2C mux and initialize it
        mux = new AdafruitI2CMux(hardwareMap, muxName, muxAddress);
        // disconnect all 8 ports
        mux.disablePorts();

        // connect only port 0. A color sensor (colorSensor1) has been wired to that port of the mux.
        mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT0);
        // create colorSensor1 and initialize it
        colorSensor1 = new AdafruitColorSensor8863(hardwareMap, colorSensorName, coreDIMName, CHANNEL_FOR_LED1);
        // report whether the color sensor id and data are valid
        activeColorSensor = colorSensor1;
        activeColorSensor.reportStatus("Color Sensor 1", telemetry);

        if (numberOfColorSensors >= 2) {
            // connect only port 1. A color sensor (colorSensor2) has been wired to that port of the mux.
            mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT1);
            // create colorSensor1 and initialize it
            colorSensor2 = new AdafruitColorSensor8863(hardwareMap, colorSensorName, coreDIMName, CHANNEL_FOR_LED2);
            // report whether the color sensor id and data are valid
            activeColorSensor = colorSensor2;
            activeColorSensor.reportStatus("Color Sensor 2", telemetry);
        }

        if (numberOfColorSensors >= 3) {
            // connect only port 2. A color sensor (colorSensor2) has been wired to that port of the mux.
            mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT2);
            // create colorSensor1 and initialize it
            colorSensor3 = new AdafruitColorSensor8863(hardwareMap, colorSensorName, coreDIMName, CHANNEL_FOR_LED3);
            // report whether the color sensor id and data are valid
            activeColorSensor = colorSensor3;
            activeColorSensor.reportStatus("Color Sensor 3", telemetry);
        }

        if (numberOfColorSensors >= 4) {
            // connect only port 4. A color sensor (colorSensor2) has been wired to that port of the mux.
            mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT3);
            // create colorSensor1 and initialize it
            colorSensor4 = new AdafruitColorSensor8863(hardwareMap, colorSensorName, coreDIMName, CHANNEL_FOR_LED4);
            // report whether the color sensor id and data are valid
            activeColorSensor = colorSensor4;
            activeColorSensor.reportStatus("Color Sensor 4", telemetry);
        }

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT0);
            // you may need a delay here to get valid data. See warning in intro section
            activeColorSensor.reportStatus("Color Sensor 1", telemetry);

            mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT1);
            // you may need a delay here to get valid data. See warning in intro section
            activeColorSensor.reportStatus("Color Sensor 2", telemetry);

            mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT2);
            // you may need a delay here to get valid data. See warning in intro section
            activeColorSensor.reportStatus("Color Sensor 3", telemetry);

            mux.selectAndEnableAPort(AdafruitI2CMux.PortNumber.PORT3);
            // you may need a delay here to get valid data. See warning in intro section
            activeColorSensor.reportStatus("Color Sensor 4", telemetry);

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        mux.disablePorts();
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    // example method used to implement a delay
    void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
