package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.SkystoneLib.ClawRotator;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Claw Rotator Test", group = "Test")
//@Disabled
public class ServoRotatorTest extends LinearOpMode {

    // Put your variable declarations here
    public ClawRotator clawRotator;

    @Override
    public void runOpMode() {


        // Put your initializations here
        clawRotator = new ClawRotator("clawRotator", hardwareMap, telemetry);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        clawRotator.init();
        sleep(5000);
        clawRotator.rotateFront();
        sleep(5000);
        clawRotator.rotateBack();
        sleep(5000);
        clawRotator.rotateFront();
        sleep(5000);
        clawRotator.shutdown();
        sleep(5000);
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
