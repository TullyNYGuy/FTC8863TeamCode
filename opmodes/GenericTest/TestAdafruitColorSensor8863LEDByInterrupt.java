package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;

/**
 * This test demonstrates using the interrupt pin to control the led on the color sensor.
 * <p>
 * Note that if you have more than one color sensor you will have to use an I2C mux since
 * the address for this color sensor is fixed and you can't have two sensors with the same address
 * on the bus
 * <p>
 * Phone configuration:
 * core device interface module name: coreDIM
 * I2C port type: I2C DEVICE
 * I2C device name: colorSensor
 * <p>
 * Short the LED and INT pins on the Adafruit color sensor together using a jumper wire.
 */
@TeleOp(name = "Test Adafruit Color Sensor 8863 LED", group = "Test")
@Disabled
public class TestAdafruitColorSensor8863LEDByInterrupt extends LinearOpMode {

    // Put your variable declarations here

    // You connect a 2 pin jumper from the pin on the circuit board labeled LED to the INT pin. If
    // you don't do this no biggie, the LED will just stay on all the time.

    // configure your phone with this name for the core device interface module
    final String coreDIMName = "coreDIM";
    // configure your phone for an I2C Device type with this name
    final String colorSensorName = "colorSensor";

    AdafruitColorSensor8863 colorSensor;

    boolean isColorSensorAttached;

    ElapsedTime timer;

    @Override
    public void runOpMode() {

        // Put your initializations here
        colorSensor = new AdafruitColorSensor8863(hardwareMap, colorSensorName, coreDIMName, AdafruitColorSensor8863.LEDControl.INTERRUPT);

        // check if the color sensor is attached
        isColorSensorAttached = colorSensor.isColorSensorAttached(telemetry);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Color sensor initialized");
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        timer.reset();

        while (opModeIsActive()) {
            if (isColorSensorAttached) {
                if (timer.milliseconds() > 2000) {
                    colorSensor.turnLEDOff();
                    telemetry.addData("LED is off", "!");
                    telemetry.addData("Low threshold (s/b FFFF)=          ", "%5d", colorSensor.getLowThresholdFromSensor());
                    telemetry.addData("High threshold (s/b 0) =         ", "%5d", colorSensor.getHighThresholdFromSensor());
                    telemetry.addData("Interrupt enabled (s/b enabled)=    ", colorSensor.isInterruptEnabled());
                    telemetry.update();
                    colorSensor.turnCoreDIMRedLEDOff();
                    colorSensor.turnCoreDIMBlueLEDOff();
                }
                if (timer.milliseconds() > 4000) {
                    colorSensor.turnLEDOn();
                    telemetry.addData("LED is on", "!");
                    telemetry.addData("Low threshold =          ", "%5d", colorSensor.getLowThresholdFromSensor());
                    telemetry.addData("High threshold =         ", "%5d", colorSensor.getHighThresholdFromSensor());
                    telemetry.addData("Interrupt enabled (s/b disabled) =    ", colorSensor.isInterruptEnabled());
                    telemetry.update();
                    timer.reset();
                    colorSensor.turnCoreDIMRedLEDOn();
                    colorSensor.turnCoreDIMBlueLEDOn();
                }
            } else {
                telemetry.addData("ERROR - color sensor is not connected!", " Check the wiring.");
                telemetry.update();
            }
        }
    }
}