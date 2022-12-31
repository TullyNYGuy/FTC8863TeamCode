package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.WobbleGoalGrabber;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Wobble Goal Grabber", group = "Test")
@Disabled
public class TestWobbleGoalGrabber extends LinearOpMode {

    // Put your variable declarations here
    WobbleGoalGrabber theClaw;
    Configuration configuration;

    @Override
    public void runOpMode() {


        // Put your initializations here
        theClaw = new WobbleGoalGrabber(hardwareMap, telemetry);
        theClaw.init(configuration);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        theClaw.dropGoal();
        while (opModeIsActive() && !theClaw.isComplete()) {
            theClaw.update();
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
        sleep(2000);
        theClaw.pickUpArm();
        while (opModeIsActive() && !theClaw.isComplete()) {
            theClaw.update();
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
sleep(3000);
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
