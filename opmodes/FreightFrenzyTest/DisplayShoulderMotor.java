package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.ShoulderMotor;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Display Shoulder Motor", group = "Test")
//@Disabled
public class DisplayShoulderMotor extends LinearOpMode {

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

        while (opModeIsActive() ) {
            shoulderMotor.displayPosition(telemetry);
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }

}
