package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo;

/**
 * This opmode shows how to operate a CR Servo
 */
@TeleOp(name = "Test CR Servo Find Rate", group = "Test")
@Disabled
public class TestCRServoFindRate extends LinearOpMode {

    // Put your variable declarations here
    double noMovePositionReverse = .48;
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

    int timeToRunTest = 2000; // milliseconds
    boolean lockTimer = false;

    @Override
    public void runOpMode() {


        // Put your initializations here
        testServo = new CRServo(RobotConfigMappingForGenericTest.getcrServoName(), hardwareMap,
                noMovePositionForward, noMovePositionReverse, deadZone, Servo.Direction.FORWARD,
                telemetry);
        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        timer.reset();
//        testServo.setDirection(Servo.Direction.FORWARD);
//        runForTime(1.0);

        testServo.setDirection(Servo.Direction.REVERSE);
        runForTime(1.0);

        // Display the current value
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    /**
     * Run the servo for a period of time
     *
     * @param power
     */
    private void runForTime(double power) {
        while (opModeIsActive() && timer.milliseconds() < timeToRunTest) {
            testServo.setSpeed(power);
            telemetry.addData("Time (sec) = ", "%3.2f", timer.milliseconds() / 1000);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();
            idle();
        }
        testServo.setSpeed(0);
        telemetry.addData("Timer expired at (sec) = ", "%3.2f", timer.milliseconds() / 1000);
        telemetry.addData(">", "Press Stop to end test.");
        telemetry.update();
        sleep(3000);
    }
}
