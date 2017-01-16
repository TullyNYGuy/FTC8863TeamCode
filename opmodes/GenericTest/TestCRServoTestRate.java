package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo;

/**
 * This opmode shows how to operate a CR Servo
 */
@TeleOp(name = "Test CR Servo Test Rate", group = "Test")
//@Disabled
public class TestCRServoTestRate extends LinearOpMode {

    // Put your variable declarations here
    double noMovePositionReverse = .48;
    double noMovePositionForward = .51;
    double deadZone = .1;
    CRServo testServo;

    ElapsedTime timer;

    int step = 1;
    double command = 0;
    double startCommand = .4;

    int timeToRunTest = 4000; // milliseconds

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

        testServo.setForwardCMPerSecond(1.90);
        testServo.setBackwardCMPerSecond(2.07);

        double distance = 4.0; // in cm

//        moveDistance(distance, CRServo.CRServoDirection.FORWARD);
//        telemetry.addData("Moved Distance = ", "%1.2f", distance);
//        telemetry.update();
//        sleep(2000);
        moveDistance(distance, CRServo.CRServoDirection.BACKWARD);
        telemetry.addData("Moved Distance = ", "%1.2f", distance);
        telemetry.update();
        sleep(2000);

//        moveDistance(4.0, CRServo.CRServoDirection.FORWARD);
//        sleep(2000);
//        moveDistance(4.0, CRServo.CRServoDirection.BACKWARD);
//        sleep(2000);

//        moveDistance(4.0, CRServo.CRServoDirection.FORWARD);
//        sleep(2000);
//        moveDistance(4.0, CRServo.CRServoDirection.BACKWARD);
//        sleep(2000);


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
}
