package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;

/**
 * This opmode can be used to test the Adafruit color sensor using AdafruitColorSensor8863 as the
 * driver. Note that if you have more than one color sensor you will have to use an I2C mux since
 * the address for this color sensor is fixed and you can't have two sensors with the same address
 * on the bus.
 *
 */
@TeleOp(name = "Test Adafruit Color Sensor 8863 LED", group = "Test")
//@Disabled
public class TestAdafruitColorSensor8863LEDByInterrupt extends LinearOpMode {

    // Put your variable declarations here

    // You connect a 2 pin jumper from the pin on the circuit board labeled LED to the INT pin. If
    // you don't do this no biggie, the LED will just stay on all the time.
    final int CHANNEL_FOR_LED = 5;
    // configure your phone with this name for the core device interface module
    final String coreDIMName = "coreDIM";
    // configure your phone for an I2C Device type with this name
    final String colorSensorName = "colorSensor";

    AdafruitColorSensor8863 colorSensor;

    ElapsedTime timer;

    @Override
    public void runOpMode() {

        // Put your initializations here
        colorSensor = new AdafruitColorSensor8863(hardwareMap, colorSensorName,
                coreDIMName, CHANNEL_FOR_LED);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Color sensor initialized");
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        timer.reset();

        while (opModeIsActive()) {
            if (timer.milliseconds() > 2000) {
                colorSensor.turnLEDOffByInterrupt();
                telemetry.addData("LED is off", "!");
                telemetry.addData("Low threshold =          ", "%5d", colorSensor.getLowThresholdFromSensor());
                telemetry.addData("High threshold =         ", "%5d", colorSensor.getHighThresholdFromSensor());
                telemetry.addData("Interrupt enabled =    ", colorSensor.isInterruptEnabled());
                telemetry.update();
            }
            if (timer.milliseconds() > 4000) {
                colorSensor.turnLEDOnByInterrupt();
                telemetry.addData("LED is on", "!");
                telemetry.addData("Low threshold =          ", "%5d", colorSensor.getLowThresholdFromSensor());
                telemetry.addData("High threshold =         ", "%5d", colorSensor.getHighThresholdFromSensor());
                telemetry.addData("Interrupt enabled =    ", colorSensor.isInterruptEnabled());
                telemetry.update();
                timer.reset();
            }
        }
    }
}