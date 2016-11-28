package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test CR Servo Linear", group = "Test")
//@Disabled
public class TestCRServoLinearOpMode extends LinearOpMode {

    // Put your variable declarations here
    double noMovePosition = .46;
    double deadZone = .1;
    CRServo testServo;

    ElapsedTime timer;

    int step = 1;
    double command = 0;
    double startCommand = .4;
    double endCommand = .5;
    double commandIncrement = .01;
    int stepLength = 1000; // milliseconds

    int timeToRunTest = 2000; // milliseconds
    boolean lockTimer = false;

    @Override
    public void runOpMode() {


        // Put your initializations here
        testServo = new CRServo(RobotConfigMappingForGenericTest.getgenericServoName(), hardwareMap, noMovePosition, deadZone);
        testServo.setDirection(Servo.Direction.REVERSE);
        timer = new ElapsedTime();
        testServo.setPosition(testServo.getCenterValue());

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timer.reset();
        command = startCommand;

        // runForTime runs in a loop and runs the servo for the given amount of time (timeToRunTest)
        // at the power that is input. When it is done, the loop finishes
        runForTime(1.0);
        // wait for 2 seconds before moving again so the user can look
        sleep(2000);
        timer.reset();

        runForTime(-1.0);
        sleep(2000);
        timer.reset();

        // 2nd time
        runForTime(1.0);
        sleep(2000);
        timer.reset();

        runForTime(-1.0);
        sleep(2000);
        timer.reset();

        // 3rd time
        runForTime(1.0);
        sleep(2000);
        timer.reset();

        runForTime(-1.0);
        sleep(2000);
        timer.reset();

        // 4th time
        runForTime(1.0);
        sleep(2000);
        timer.reset();

        runForTime(-1.0);
        sleep(2000);
        timer.reset();

        // 5th time
        runForTime(1.0);
        sleep(2000);
        timer.reset();

        runForTime(-1.0);
        sleep(2000);
        timer.reset();

        // Display the current value
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">","Done");
        telemetry.update();
        idle();
    }

    private void runForTime(double power) {
        while (opModeIsActive()) {
            if (timer.milliseconds() < timeToRunTest) {
                testServo.updatePosition(power);
                telemetry.addData("Time (sec) = ", "%3.2f", timer.milliseconds() / 1000);
                telemetry.addData(">", "Press Stop to end test.");
                telemetry.update();
            } else {
                testServo.updatePosition(0);
                telemetry.addData("Timer expired at (sec) = ", "%3.2f", timer.milliseconds() / 1000);
                telemetry.addData(">", "Press Stop to end test.");
                telemetry.update();
                break;
            }
            idle();
        }
    }

    public void findNoMovementCommand() {
        timer.reset();
        double step = 1;
        double command = 0;
        double commandIncrement = .05;
        int stepLength = 500; // milliseconds
        while (command <= 1.0) {
            if (timer.milliseconds() > step * 500) {
                step++;
                testServo.setPosition(command);
            }
            telemetry.addData("Step = ", "%d", step);
            telemetry.addData("Command = ", "%3.2f", command);
            telemetry.update();
        }
    }
}
