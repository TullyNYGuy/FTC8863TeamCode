package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Shoulder Servo", group = "Test")
//@Disabled
public class TestShoulderServo extends LinearOpMode {

    // Put your variable declarations her
    Servo8863 shoulderServo;
    double shoulderUpPosition = .5;
    double shoulderDownPosition = .2;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        shoulderServo = new Servo8863("shoulderServo",hardwareMap, telemetry);
        shoulderServo.setPositionOne(shoulderUpPosition);
        shoulderServo.setPositionTwo(shoulderDownPosition);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();
        shoulderDown();
        // Put your calls here - they will not run in a loop

        while (opModeIsActive() && timer.milliseconds() < 5000) {
            idle();
        }

        wristUp();
        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < 5000) {
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }

    public void shoulderDown() {
        shoulderServo.goPositionTwo();
    }

    public void wristUp() {
        shoulderServo.goPositionOne();
    }
}
