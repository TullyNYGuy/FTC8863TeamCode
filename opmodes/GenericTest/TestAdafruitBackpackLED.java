package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitBackpackLED;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;

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
@TeleOp(name = "Test Adafruit Backpack LED display", group = "Test")
//@Disabled
public class TestAdafruitBackpackLED extends LinearOpMode {

    // Put your variable declarations here
    AdafruitBackpackLED backpackLED;

    final String backpackName = "backpackLEDDisplay";

    boolean xButtonIsReleased = false;
    boolean yButtonIsReleased = false;
    boolean aButtonIsReleased = false;
    boolean bButtonIsReleased = false;

    boolean testComplete = false;

    boolean isColorSensorAttached;

    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        backpackLED = new AdafruitBackpackLED(hardwareMap, backpackName);

        timer = new ElapsedTime();
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();
        timer.reset();

        // Put your calls here - the ones you do not want to run in a loop

        // the first test will be just to turn the display on and off - 3 times
        telemetry.addData("Demo of turning LED display on and off ", "3X");
        telemetry.update();
        backpackLED.testOnOff();
        backpackLED.testOnOff();
        backpackLED.testOnOff();

        // the next test demos the blinking modes
        // setup
        backpackLED.setupBlinkingTest();
        telemetry.addData("Demo of blinking modes for LED display", "...");
        telemetry.update();

        // run the update for the state machine in the test
        while(opModeIsActive() && !backpackLED.updateBlinkingTest()) {
            idle();
        }

        // the next test demos the brightness modes
        // setup
        backpackLED.setupBrightnessLevelTest();
        telemetry.addData("Demo of brightness levels of LED display", "...");
        telemetry.update();

        // run the update for the state machine in the test
        while(opModeIsActive() && !backpackLED.updateBrightnessLevelTest()) {
            idle();
        }

        // the next test demos the available characters that can be displayed
        // setup
        backpackLED.setupCharacterTest();
        telemetry.addData("Demo of characters LED can display", "...");
        telemetry.update();

        // run the update for the state machine in the test
        while(opModeIsActive() && !backpackLED.updateCharacterTest()) {
            idle();
        }

        telemetry.addData("LED tests are complete", "!");
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}
