package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FFArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test FF arm", group = "Test")
//@Disabled
public class TestFFArm extends LinearOpMode {

    // Put your variable declarations her
    FFArm arm;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        arm = new FFArm(hardwareMap, telemetry);
        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        arm.storage();
        while (opModeIsActive() && !arm.isPositionReached()) {
            arm.update();
            telemetry.addData("moving to storage position","!");
            telemetry.update();
            idle();
        }

        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            arm.update();
            telemetry.addData("at storage position","!");
            telemetry.update();
            idle();
        }

        arm.carry();
        while (opModeIsActive() && !arm.isPositionReached()) {
            arm.update();
            telemetry.addData("moving to carry position","!");
            telemetry.update();
            idle();
        }

        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            arm.update();
            telemetry.addData("at carry position","!");
            telemetry.update();
            idle();
        }

//        arm.pickup();
//        while (opModeIsActive() && !arm.isPositionReached()) {
//            idle();
//        }
//
//
//        timer.reset();
//        while (opModeIsActive() && timer.milliseconds() < 2000) {
//            idle();
//        }
//
//        arm.carry();
//        while (opModeIsActive() && !arm.isPositionReached()) {
//            idle();
//        }
//
//        timer.reset();
//        while (opModeIsActive() && timer.milliseconds() < 2000) {
//            idle();
//        }
//
//        arm.dropoff();
//        while (opModeIsActive() && !arm.isPositionReached()) {
//            idle();
//        }
//
//        timer.reset();
//        while (opModeIsActive() && timer.milliseconds() < 2000) {
//            idle();
//        }
//
//        arm.openClaw();
//
//        while (opModeIsActive()) {
//            idle();
//        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
