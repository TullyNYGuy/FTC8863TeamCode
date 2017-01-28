package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo;

/**
 * This opmode shows how to operate a CR Servo
 */
@TeleOp(name = "Test CR Servo Find No Movement Position", group = "Test")
//@Disabled
public class TestCRServoFindNoMovementPosition extends LinearOpMode {

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
        findNoMovementCommand();

        // Display the current value
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    public void findNoMovementCommand() {
//        telemetry.addData("Fowards ", "Look for no movement. Note the command for it.");
//        telemetry.update();
//        sleep(1000);
        // first test the forward direction of the servo
        testServo.setDirection(Servo.Direction.FORWARD);
        testServo.setupFindNoMovementCommand(CRServo.CRServoStep.FINE);
        while (opModeIsActive() && !testServo.updateFindNoMovementCommand("Forward")) {
            idle();
        }
        sleep(500);
        // now test the reverse direction of the servo
//        telemetry.addData("Reverse ", "Look for no movement. Note the command for it.");
//        telemetry.update();
//        sleep(1000);
        testServo.setDirection(Servo.Direction.REVERSE);
        testServo.setupFindNoMovementCommand(CRServo.CRServoStep.FINE);
        while (opModeIsActive() && !testServo.updateFindNoMovementCommand("Reverse")) {
            idle();
        }
    }
}
