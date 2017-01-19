package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo;

/**
 * This opmode shows how to operate a CR Servo
 */
@TeleOp(name = "Test CR Servo Linear", group = "Test")
//@Disabled
public class TestCRServoLinearOpMode extends LinearOpMode {

    // Put your variable declarations here
    double noMovePositionReverse = .46;
    double noMovePositionForward = .51;
    double deadZone = .1;
    CRServo testServo;

    ElapsedTime timer;

    int step = 1;
    double command = 0;
    double startCommand = .4;
    double endCommand = .5;
    double commandIncrement = .01;
    int stepLength = 1000; // milliseconds

    int timeToRunTest = 4000; // milliseconds
    boolean lockTimer = false;

    @Override
    public void runOpMode() {


        // Put your initializations here
        testServo = new CRServo(RobotConfigMappingForGenericTest.getcrServoName(), hardwareMap,
                noMovePositionForward, noMovePositionReverse, deadZone, Servo.Direction.FORWARD,
                telemetry);
        //testServo.setDirection(Servo.Direction.REVERSE);
        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timer.reset();
        command = startCommand;

        testServo.setBackwardCMPerSecond(1.88);
        testServo.setForwardCMPerSecond(1.94);

        // Use this next call to determine the forward and reverse command that results in no movement
        // of the servo. It should be around .5 but will vary for each servo. This will need to be
        // part of the constructor when you create the servo.
//        findNoMovementCommand();

        double distance = 4.0; // in cm
        moveDistance(distance, CRServo.CRServoDirection.BACKWARD);
        telemetry.addData("Moved Distance = ", "%1.2f", distance);
        telemetry.update();
        sleep(2000);
        moveDistance(distance, CRServo.CRServoDirection.FORWARD);
        telemetry.addData("Moved Distance = ", "%1.2f", distance);
        telemetry.update();
        sleep(2000);

        moveDistance(4.0, CRServo.CRServoDirection.BACKWARD);
        sleep(2000);
        moveDistance(4.0, CRServo.CRServoDirection.FORWARD);
        sleep(2000);

        moveDistance(4.0, CRServo.CRServoDirection.BACKWARD);
        sleep(2000);
        moveDistance(4.0, CRServo.CRServoDirection.FORWARD);
        sleep(2000);

//        // runForTime runs in a loop and runs the servo for the given amount of time (timeToRunTest)
//        // at the power that is input. When it is done, the loop finishes
//        runForTime(-1.0);
//        // wait for 2 seconds before moving again so the user can look
//        sleep(2000);
//        timer.reset();
//
//        runForTime(-1.0);
//        sleep(2000);
//        timer.reset();
//
//        // 2nd time
//        runForTime(1.0);
//        sleep(2000);
//        timer.reset();
//
//        runForTime(-1.0);
//        sleep(2000);
//        timer.reset();
//
//        // 3rd time
//        runForTime(1.0);
//        sleep(2000);
//        timer.reset();
//
//        runForTime(-1.0);
//        sleep(2000);
//        timer.reset();
//
//        // 4th time
//        runForTime(1.0);
//        sleep(2000);
//        timer.reset();
//
//        runForTime(-1.0);
//        sleep(2000);
//        timer.reset();
//
//        // 5th time
//        runForTime(1.0);
//        sleep(2000);
//        timer.reset();
//
//        runForTime(-1.0);
//        sleep(2000);
//        timer.reset();

        // Display the current value
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">","Done");
        telemetry.update();
    }

    /**
     * Move a distance
     * @param distance distance to move in cm
     * @param direction direction to move
     */
    private void moveDistance(double distance, CRServo.CRServoDirection direction) {
        testServo.startMoveDistance(distance, direction);
        while (opModeIsActive() && !testServo.updateMoveDistance()) {
            idle();
        }
    }

    /**
     * Run the servo for a period of time
     * @param power
     */
    private void runForTime(double power) {
        while (opModeIsActive()) {
            if (timer.milliseconds() < timeToRunTest) {
                testServo.setSpeed(power);
                telemetry.addData("Time (sec) = ", "%3.2f", timer.milliseconds() / 1000);
                telemetry.addData(">", "Press Stop to end test.");
                telemetry.update();
            } else {
                testServo.setSpeed(0);
                telemetry.addData("Timer expired at (sec) = ", "%3.2f", timer.milliseconds() / 1000);
                telemetry.addData(">", "Press Stop to end test.");
                telemetry.update();
                break;
            }
            idle();
        }
    }
}
