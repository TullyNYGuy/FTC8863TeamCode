package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitBackpackLED;

/**
 * This opmode can be used to test the Adafruit Backpack LED driver or to demo the capabilities of
 * the LED display. This display is a 4 character LED alphanumeric display available from Adafruit.
 * The Adafruit Backpack LED display is available in several different LED colors. Here is the
 * yellow version: https://www.adafruit.com/product/2158
 *
 * Phone configuration:
 * Backpack display plugged into the I2C bus on a port and that port configured as:
 * I2C port type: I2C DEVICE
 * I2C device name: backpackLEDDisplay
 *
 * If you want to make the display brighter, then wire the 5V pin and a second ground wire from the
 * backpack board into the 5V port on the REV expansion hub. Without the extra power, the display
 * will not be as bright.
 */
@TeleOp(name = "Adafruit Backpack LED Demo", group = "Test")
//@Disabled
public class AdafruitBackpackLEDDemo extends LinearOpMode {

    // Put your variable declarations here
    AdafruitBackpackLED backpackLED;

    @Override
    public void runOpMode() {


        // Put your initializations here
        backpackLED = hardwareMap.get(AdafruitBackpackLED.class,"AdafruitBackpackLED");

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - the ones you do not want to run in a loop

        backpackLED.turnLEDsOn();
        backpackLED.setBrightnessLevel(7);

        backpackLED.setDisplayString("TEAM");
        sleep(2000);
        backpackLED.setDisplayString("8863");
        sleep(2000);
        backpackLED.setDisplayString("rock");
        sleep(1000);
        backpackLED.setDisplayString("ROCK");
        sleep(1000);
        backpackLED.setLedBlinkRate(AdafruitBackpackLED.LEDBlinkRate.ONCE_PER_TWO_SECONDS);
        sleep(4000);
        backpackLED.setLedBlinkRate(AdafruitBackpackLED.LEDBlinkRate.ONCE_PER_SECOND);
        sleep(4000);
        backpackLED.setLedBlinkRate(AdafruitBackpackLED.LEDBlinkRate.TWICE_PER_SECOND);
        sleep(4000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
        backpackLED.turnLEDsOff();
    }
}
