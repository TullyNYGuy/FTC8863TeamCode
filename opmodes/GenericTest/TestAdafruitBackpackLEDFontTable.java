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
@TeleOp(name = "Test Adafruit Backpack LED fonts", group = "Test")
//@Disabled
public class TestAdafruitBackpackLEDFontTable extends LinearOpMode {

    // Put your variable declarations here
    AdafruitBackpackLED backpackLED;
    char[] chars = {
            ' ',
            '!',
            '"',
            '#',
            '$',
            '%',
            '&',
            '\'',
            '(',
            ')',
            '*',
            '+',
            ',',
            '-',
            '.',
            '/',
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            ':',
            ';',
            '<',
            '=',
            '>',
            '?',
            '@',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F',
            'G',
            'H',
            'I',
            'J',
            'K',
            'L',
            'M',
            'N',
            'O',
            'P',
            'Q',
            'R',
            'S',
            'T',
            'U',
            'V',
            'W',
            'X',
            'Y',
            'Z',
            '[',
            ']',
            '^',
            '_',
            '`',
            'a',
            'b',
            'c',
            'd',
            'e',
            'f',
            'g',
            'h',
            'i',
            'j',
            'k',
            'l',
            'm',
            'n',
            'o',
            'p',
            'q',
            'r',
            's',
            't',
            'u',
            'v',
            'w',
            'x',
            //'y',
            'z',
            '{',
            '!',
            '}',
            '~'
    };

    String[] strings = {
      "ON!",
      "OFF!"
    };

    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        backpackLED = hardwareMap.get(AdafruitBackpackLED.class,"AdafruitBackpackLED");

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();
        timer.reset();

        // Put your calls here - the ones you do not want to run in a loop

        // the first test will be just to turn the display on and off - 3 times
        telemetry.addData("Test of font table. Compare the display to the character on the phone ", "!");
        telemetry.update();

        sleep(2000);
        backpackLED.turnLEDsOn();
        backpackLED.setBrightnessLevel(1);

        for (char item : chars) {
            backpackLED.setDisplayCharacter(item, AdafruitBackpackLED.DisplayPosition.LEFT);
            telemetry.addData("Displaying: ", item);
            telemetry.update();
            sleep(5000);
        }
        telemetry.addData("Font table test are complete", "!");
        sleep(2000);

        for (String item : strings) {
            backpackLED.setDisplayString(item);
            telemetry.addData("Displaying: ", item);
            telemetry.update();
            sleep(5000);
        }
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
        sleep(5000);
        backpackLED.turnLEDsOff();
    }
}
