package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.JewelArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test for Jewel Arm", group = "Test")
//@Disabled
public class TestJewelArm extends LinearOpMode {
    public enum AllianceColor {
        BLUE, RED
    }
    JewelArm leftJewelArm;

    @Override
    public void runOpMode() {

        // Put your initializations here
        leftJewelArm = new JewelArm(JewelArm.RobotSide.LEFT, hardwareMap, telemetry, AllianceColor.BLUE);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        leftJewelArm.initialize();
        // Put your calls here - they will not run in a loop
        sleep(2000);

       leftJewelArm.knockOffBall();

        sleep (2000);

       leftJewelArm.shutdown();
        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here



            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
