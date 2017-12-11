package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;

/**
 * A button on a gamepad normally can only turn something on or off or execute one command. It would
 * be great to have a button cycle through a sequence of commands, one command each time it is
 * pushed. For example, a button could toggle a motor on and off. Push once, motor on. Push again,
 * motor off. Taken a step farther, maybe you want the button to control 4 positions on a servo.
 * <p>
 * This opmode tests a class that does that.
 */
@TeleOp(name = "Linear Gamepad Multi Push Button", group = "Test")
//@Disabled
public class TestGamepadButtonMultiPush extends LinearOpMode {

    // Put your variable declarations here
    public GamepadButtonMultiPush gamepad1x;
    public GamepadButtonMultiPush gamepad1y;
    public GamepadButtonMultiPush gamepad1a;
    public GamepadButtonMultiPush gamepad1b;

    @Override
    public void runOpMode() {

        // Put your initializations here

        gamepad1x = new GamepadButtonMultiPush(1);
        gamepad1y = new GamepadButtonMultiPush(2);
        gamepad1a = new GamepadButtonMultiPush(3);
        gamepad1b = new GamepadButtonMultiPush(4);

        int gamepad1xCount = 0;

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here

            // a normal button press with only one command can be checked like this:
            if (gamepad1x.buttonPress(gamepad1.x)) {
                // this was a new button press, not a button held down for a while
                telemetry.addData("x command 1", "!");
                gamepad1xCount++;
                telemetry.addData("Number of presses = ", "%d", gamepad1xCount);
            }

            // this is a button with 2 commands tied to it
            if (gamepad1y.buttonPress(gamepad1.y)) {
                // this was a new button press, not a button held down for a while
                if (gamepad1y.isCommand1()) {
                    // call the first command you want to run
                    telemetry.addData("y command 1", "!");
                }
                if (gamepad1y.isCommand2()) {
                    // call the 2nd command you want to run
                    telemetry.addData("y command 2", "!");
                }
            }

            // this is a button with 3 commands tied to it
            if (gamepad1a.buttonPress(gamepad1.a)) {
                // this was a new button press, not a button held down for a while
                if (gamepad1a.isCommand1()) {
                    // call the first command you want to run
                    telemetry.addData("a command 1", "!");
                }
                if (gamepad1a.isCommand2()) {
                    // call the 2nd command you want to run
                    telemetry.addData("a command 2", "!");
                }
                if (gamepad1a.isCommand3()) {
                    // call the 3rd command you want to run
                    telemetry.addData("a command 3", "!");
                }
                // This next command should never show up. The A button is configured for 3 commands,
                // not 4.
                if (gamepad1a.isCommand4()) {
                    // call the 4th command you want to run
                    telemetry.addData("a command 4", "!");
                }
            }

            // this is a button with 4 commands tied to it
            if (gamepad1b.buttonPress(gamepad1.b)) {
                // this was a new button press, not a button held down for a while
                if (gamepad1b.isCommand1()) {
                    // call the first command you want to run
                    telemetry.addData("b command 1", "!");
                }
                if (gamepad1b.isCommand2()) {
                    // call the 2nd command you want to run
                    telemetry.addData("b command 2", "!");
                }
                if (gamepad1b.isCommand3()) {
                    // call the 3rd command you want to run
                    telemetry.addData("b command 3", "!");
                }
                if (gamepad1b.isCommand4()) {
                    // call the 4th command you want to run
                    telemetry.addData("b command 4", "!");
                }
            }

            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
