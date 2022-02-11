package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FFExtensionArm;

/**
 * Test the positions of the delivery servo
 */
@TeleOp(name = "Test Delivery Servo", group = "Test")
//@Disabled
public class TestDeliveryServo extends LinearOpMode {

    // Put your variable declarations here
    FFExtensionArm delivery;
    ElapsedTime timer;

    @Override
    public void runOpMode() {

        // Put your initializations here
        delivery = new FFExtensionArm(AllianceColor.BLUE, hardwareMap, telemetry);
        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        delivery.deliveryServoToTransferPosition();
        while (opModeIsActive() && !delivery.isDeliverServoPositionReached()) {
            telemetry.addData("Moving to Transfer position", ".");
            telemetry.update();
            delivery.update();
            idle();
        }

        timer.reset();
        while (opModeIsActive() && timer.seconds() < 5) {
            idle();
        }

        delivery.deliveryServoToParallelPosition();
        while (opModeIsActive() && !delivery.isDeliverServoPositionReached()) {
            telemetry.addData("Moving to Parallel position", ".");
            telemetry.update();
            delivery.update();
            idle();
        }


        timer.reset();
        while (opModeIsActive() && timer.seconds() < 30) {
            idle();
        }

        delivery.deliveryServoToDumpPosition();
        while (opModeIsActive() && !delivery.isDeliverServoPositionReached()) {
            telemetry.addData("Moving to Dump position", ".");
            telemetry.update();
            delivery.update();
            idle();
        }

        timer.reset();
        while (opModeIsActive() && timer.seconds() < 5) {
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
