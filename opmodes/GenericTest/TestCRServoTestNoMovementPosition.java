package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo8863;

/**
 * This opmode shows how to operate a CR Servo
 */
@TeleOp(name = "Test CR Servo Test No Movement Position", group = "Test")
@Disabled
public class TestCRServoTestNoMovementPosition extends LinearOpMode {

    // Put your variable declarations here
    double noMovePositionReverse = .48;
    double noMovePositionForward = .51;
    double deadZone = .1;
    CRServo8863 testServo;

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
        testServo = new CRServo8863(RobotConfigMappingForGenericTest.getcrServoName(), hardwareMap,
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

        telemetry.addData("Fowards ", "There should be no movement");
        telemetry.update();
        testServo.setDirection(Servo.Direction.FORWARD);
        testServo.setPosition(noMovePositionForward);
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            idle();
        }

        timer.reset();
        telemetry.addData("Reverse ", "There should be no movement");
        telemetry.update();
        testServo.setDirection(Servo.Direction.REVERSE);
        testServo.setPosition(noMovePositionReverse);
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            idle();
        }
        // Display the current value
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}
