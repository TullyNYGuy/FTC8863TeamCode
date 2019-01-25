package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "AAA Extension Arm Demo", group = "Test")
//@Disabled
public class ExtensionArmDemo extends LinearOpMode {

    // Put your variable declarations here
    public CollectorArm collectorArm;
    public ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        timer = new ElapsedTime();

        collectorArm = new CollectorArm(hardwareMap, telemetry);
        collectorArm.init();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();
        collectorArm.goToExtensionArm10Inches();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("move to 10 inches");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Display the current value
        telemetry.addLine("Done");
        collectorArm.displayExtensionMotorEncoder();
        collectorArm.displayExtensionArmPosition();
        collectorArm.displayExtensionArmState();
        telemetry.addData("Time to move 10 inches (mS) = ", timer.milliseconds());
        telemetry.addData(">", "Press Stop to end test.");
        telemetry.update();
        sleep(1000);

        timer.reset();
        collectorArm.goToExtensionArmHome();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            telemetry.addLine("move to home");
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Display the current value
        telemetry.addLine("Done");
        collectorArm.displayExtensionMotorEncoder();
        collectorArm.displayExtensionArmPosition();
        collectorArm.displayExtensionArmState();
        telemetry.addData("Time to move 9.5 inches (mS) = ", timer.milliseconds());
        telemetry.update();
        sleep(4000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
