package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test DC Motor Run at Constant Speed", group = "Test")
@Disabled
public class TestDCMotorRunConstantSpeed extends LinearOpMode {

    // Put your variable declarations her
    private DcMotorEx motor;
    private double velocity;

    @Override
    public void runOpMode() {

        // Put your initializations here
        motor = hardwareMap.get(DcMotorEx.class, "intakeSweeperMotor");
        motor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // set velocity in ticks per second, Andymark 3.7 orbital is 103 ticks per rev
        // so this should be 5 rev per second or 5 rev/sec * 60 sec/min = 300 RPM
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setPower(.5);

        while (opModeIsActive()) {
            velocity = motor.getVelocity();
            telemetry.addData("Running at ticks/sec = ", velocity);
            // RPM = velocityInTicksPerSec * 60 sec / min * 1 rev / 103 ticks
            telemetry.addData("Running at RPM = ", velocity * 60 / 103);
            telemetry.update();
            idle();
        }
    }
}
