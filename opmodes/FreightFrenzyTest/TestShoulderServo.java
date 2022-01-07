package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.ShoulderServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Shoulder Servo", group = "Test")
//@Disabled
public class TestShoulderServo extends LinearOpMode {

    // Put your variable declarations her
    ShoulderServo shoulderServo;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        shoulderServo = new ShoulderServo(hardwareMap, telemetry);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();
       shoulderServo.down();
        // Put your calls here - they will not run in a loop

        while (opModeIsActive() && !shoulderServo.isPositionReached()) {
            idle();
        }

       shoulderServo.up();
        timer.reset();
        while (opModeIsActive() && !shoulderServo.isPositionReached()) {
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }

}
