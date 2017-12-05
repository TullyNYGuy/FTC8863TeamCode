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

    JewelArm leftJewelArm;

    @Override
    public void runOpMode() {


        // Put your initializations here
        leftJewelArm = new JewelArm(JewelArm.RobotSide.LEFT, hardwareMap, telemetry);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here

            leftJewelArm.initialize();

            sleep(2000);

            leftJewelArm.knockFrontBall();

            sleep(2000);

            leftJewelArm.resetPosition();

            sleep(2000);

            leftJewelArm.knockBackBall();

            sleep(2000);




//            leftJewelArm.armDown();
//
//            sleep(2000);
//
//            leftJewelArm.armUp();
//
//            sleep(2000);
//
//            leftJewelArm.resetPosition();
//
//            sleep(2000);
//
//            leftJewelArm.armFront();
//
//            sleep(2000);
//
//            leftJewelArm.armBack();
//
//            sleep(2000);
//
//            leftJewelArm.armCenter();
//
//            sleep(2000);

            leftJewelArm.shutdown();

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
