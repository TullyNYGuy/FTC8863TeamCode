package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.ShoulderMotor;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.ShoulderServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Shoulder Motor", group = "Test")
@Disabled
public class TestShoulderMotor extends LinearOpMode {

    // Put your variable declarations her
    ShoulderMotor shoulderMotor;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        shoulderMotor = new ShoulderMotor(hardwareMap, telemetry);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();
       shoulderMotor.down();
        // Put your calls here - they will not run in a loop

        while (opModeIsActive() && !shoulderMotor.isPositionReached()) {
            idle();
        }
        timer.reset();
        while (opModeIsActive() && timer.milliseconds()<3000) {
            idle();
        }

       shoulderMotor.up();
        while (opModeIsActive() && !shoulderMotor.isPositionReached()) {
            idle();
        }
        timer.reset();
        while (opModeIsActive() && timer.milliseconds()<3000) {
            idle();
        }
        shoulderMotor.storage();
        while (opModeIsActive() && !shoulderMotor.isPositionReached()) {
            idle();
        }
        timer.reset();
        while (opModeIsActive() && timer.milliseconds()<3000) {
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }

}
