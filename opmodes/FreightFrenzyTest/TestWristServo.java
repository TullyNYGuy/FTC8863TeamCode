package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Wrist Servo", group = "Test")
//@Disabled
public class TestWristServo extends LinearOpMode {

    // Put your variable declarations her
    Servo8863 wristServo;
    double wristUpPosition = 0;
    double wristDownPosition = .58;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        wristServo = new Servo8863("wristServo",hardwareMap, telemetry);
        wristServo.setPositionOne(wristUpPosition);
        wristServo.setPositionTwo(wristDownPosition);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();
        wristDown();
        // Put your calls here - they will not run in a loop

        while (opModeIsActive() && timer.milliseconds() < 2000) {
            idle();
        }

        wristUp();
        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < 2000) {
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }

    public void wristDown() {
        wristServo.goPositionTwo();
    }

    public void wristUp() {
        wristServo.goPositionOne();
    }
}
