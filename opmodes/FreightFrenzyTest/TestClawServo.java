package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

import java.util.Optional;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Claw Servo", group = "Test")
//@Disabled
public class TestClawServo extends LinearOpMode {

    // Put your variable declarations her
    Servo8863 clawServo;
    double openPosition = 0;
    double closePosition = .58;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        clawServo = new Servo8863("ClawServo",hardwareMap, telemetry);
        clawServo.setPositionOne(openPosition);
        clawServo.setPositionTwo(closePosition);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();
        openClaw();
        // Put your calls here - they will not run in a loop

        while (opModeIsActive() && timer.milliseconds() < 2000) {
            idle();
        }

        closeClaw();
        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < 2000) {
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }

    public void openClaw() {
        clawServo.goPositionOne();
    }

    public void closeClaw() {
        clawServo.goPositionTwo();
    }
}
