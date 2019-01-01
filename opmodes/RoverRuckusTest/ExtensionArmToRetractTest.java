package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorArm;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.DeliveryLiftSystem;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Extension Arm To Retract Test", group = "Test")
@Disabled
public class ExtensionArmToRetractTest extends LinearOpMode {

    // Put your variable declarations here
    public CollectorArm collectorArm;

    @Override
    public void runOpMode() {


        // Put your initializations here
        collectorArm = new CollectorArm(hardwareMap, telemetry);
        collectorArm.enableDebugMode();
        collectorArm.init();


        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        collectorArm.goToRetract();

        // when the lift reaches its position, the loop will stop running and the next set of code
        // will run
        while (opModeIsActive() && !collectorArm.isExtensionArmMovementComplete()) {

            // Put your calls that need to run in a loop here
            collectorArm.update();

            // Display the current value
            collectorArm.displayExtensionMotorEncoder();
            collectorArm.displayExtensionArmPosition();
            collectorArm.displayExtensionArmState();;
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // after the lift reaches its position, the loop stops and this code runs
        telemetry.addData(">", "Extension Arm located at retract");
        collectorArm.displayExtensionMotorEncoder();
        collectorArm.displayExtensionArmPosition();
        collectorArm.displayExtensionArmState();
        telemetry.update();
        // give the user time to read the driver station
        sleep(4000);
    }
}
