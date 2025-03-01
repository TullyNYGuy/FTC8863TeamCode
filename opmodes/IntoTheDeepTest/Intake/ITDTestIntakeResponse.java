package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Intake;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeSweeperVertical;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Intake Anti-Jam", group = "Test")
//@Disabled
public class ITDTestIntakeResponse extends LinearOpMode {

    // Put your variable declarations here

    enum ServoState {
        RUNNING_FORWARDS,
        RUNNING_BACKWARDS,
        RUNNING_FORWARDS_AGAIN,
        STOPPED
    }

    ServoState state = ServoState.STOPPED;
    public ITDIntakeSweeperVertical intake;

    public ElapsedTime timer;
    public double reverseTime = 0;
    public double reversePower = -1;
    public String speed = String.format("%.1f", reversePower);

    // index when arm is vertical and servo = .52

    @Override
    public void runOpMode() {

        // Put your initializations here

        // These debounce the buttons so that you only see a single press even if a button is held
        // down for a long time.
        Debouncer debouncedY = new Debouncer();
        Debouncer debouncedB = new Debouncer();
        Debouncer debouncedX = new Debouncer();
        Debouncer debouncedA = new Debouncer();
        Debouncer debouncedDpadUp = new Debouncer();
        Debouncer debouncedDpadDown = new Debouncer();
        Debouncer debouncedDpadLeft = new Debouncer();

        timer = new ElapsedTime();

        intake = new ITDIntakeSweeperVertical(hardwareMap, telemetry,1);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();


        while (opModeIsActive()) {
            update();
            if (debouncedY.isPressed(gamepad1.y)) {
                reverseTime = reverseTime + 100;
            }
            if (debouncedB.isPressed(gamepad1.b)) {
                reverseTime = reverseTime - 100;
            }
            if (debouncedX.isPressed(gamepad1.x)) {
                reverseTime = reverseTime + 10;
            }
            if (debouncedA.isPressed(gamepad1.a)) {
                reverseTime = reverseTime - 10;
            }
            if (debouncedDpadUp.isPressed(gamepad1.dpad_up)) {
                reversePower = reversePower - .1;
                reversePower = Range.clip(reversePower, -1, 0);
                speed = String.format("%.1f", reversePower);
            }
            if (debouncedDpadDown.isPressed(gamepad1.dpad_down)) {
                reversePower = reversePower + .1;
                reversePower = Range.clip(reversePower, -1, 0);
                speed = String.format("%.1f", reversePower);
            }

            if (debouncedDpadLeft.isPressed(gamepad1.dpad_left)) {
                intake.setIntakeSweeperSpeed(1);
                timer.reset();
                state = ServoState.RUNNING_FORWARDS;
            }

            telemetry.addData("Y = ", "+100");
            telemetry.addData("X = ", "+ 10");
            telemetry.addData("B = ", "-100");
            telemetry.addData("A = ", "- 10");

            telemetry.addData("Dpad up = ", "Increase reverse speed");
            telemetry.addData("Dpad down = ", "Decrease reverse speed");
            telemetry.addData("Dpad left = ", "Run servo backwards for " + reverseTime + " at power = " + speed);
            telemetry.addLine();
            telemetry.addData(">", "stop to finish");
            telemetry.update();
            idle();
        }
    }

    public void update() {
        switch (state) {
            case STOPPED:
                break;
            case RUNNING_FORWARDS:
                if (timer.milliseconds() > 1000) {
                    intake.setIntakeSweeperSpeed(reversePower);
                    timer.reset();
                    state = ServoState.RUNNING_BACKWARDS;
                }
                break;
            case RUNNING_BACKWARDS:
                if (timer.milliseconds() > reverseTime) {
                    intake.setIntakeSweeperSpeed(1);
                    timer.reset();
                    state = ServoState.RUNNING_FORWARDS_AGAIN;
                }
                break;
            case RUNNING_FORWARDS_AGAIN:
                if (timer.milliseconds() > 500) {
                    intake.setIntakeSweeperSpeed(0);
                    state = ServoState.STOPPED;
                }
                break;
        }
    }
}
