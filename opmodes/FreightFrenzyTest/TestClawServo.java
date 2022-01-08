package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.ClawServo;

import java.util.Optional;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Claw Servo", group = "Test")
//@Disabled
public class TestClawServo extends LinearOpMode {

    // Put your variable declarations her
    ClawServo clawServo;

    ElapsedTime timer;

    @Override
    public void runOpMode() {

        // Put your initializations here
        clawServo = new ClawServo(hardwareMap, telemetry);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        clawServo.openClaw();
        // Put your calls here - they will not run in a loop

        timer.reset();

        while (opModeIsActive() && !clawServo.isMovementComplete()) {
            idle();
        }

        clawServo.closeClaw();
        timer.reset();

        while (opModeIsActive() && !clawServo.isMovementComplete()) {
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
