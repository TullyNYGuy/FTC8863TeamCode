package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;

import static org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863.Gain.AMS_COLOR_GAIN_1;

/**
 * This opmode can be used to test the Adafruit color sensor using AdafruitColorSensor8863 registers.
 * Note that if you have more than one color sensor you will have to use an I2C mux since
 * the address for this color sensor is fixed and you can't have two sensors with the same address
 * on the bus.
 *
 * Phone configuration:
 * core device interface module name: coreDIM
 * I2C port type: I2C DEVICE
 * I2C device name: colorSensor
 *
 * 12/10/2017 - gb - It appears that this test is not working.
 */
@TeleOp(name = "Test Adafruit Color Sensor 8863 read write registers", group = "Test")
@Disabled
public class TestAdafruitColorSensor8863ReadWriteRegisters extends LinearOpMode {

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
        colorSensor = new AdafruitColorSensor8863(hardwareMap, colorSensorName, AdafruitColorSensor8863.LEDControl.INTERRUPT);
        // check if the color sensor is attached
        isColorSensorAttached = colorSensor.isColorSensorAttached(telemetry);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Color sensor initialized");
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        timer.reset();

        // Display the current values from the sensor
        if(isColorSensorAttached) {
            colorSensor.displayRegisterValues(telemetry);
            telemetry.update();
            sleep(5000);

            colorSensor.testReadWriteRegisters(telemetry);
        } else {
            telemetry.addData("ERROR - color sensor is not connected!", " Check the wiring.");
            telemetry.update();
        }
    }
}