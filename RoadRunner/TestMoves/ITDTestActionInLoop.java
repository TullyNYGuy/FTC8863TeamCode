package org.firstinspires.ftc.teamcode.RoadRunner.TestMoves;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RoadRunner.PinpointDrive;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Action in loop", group = "Test")
@Disabled
public final class ITDTestActionInLoop extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(32.5, 54.375, Math.toRadians(-90));
        PinpointDrive drive = new PinpointDrive(hardwareMap, beginPose);

        Action movement = drive.actionBuilder(beginPose)
                // start to delivery position
                .splineToLinearHeading(new Pose2d(48.5, 51.75, Math.toRadians(-135)), Math.PI / 2)
                .build();

        TelemetryPacket packet = new TelemetryPacket();
        boolean movementNotComplete = true;
        int loopCount = 0;

        waitForStart();

        while (opModeIsActive() && movementNotComplete) {
            if (movementNotComplete) {
                movementNotComplete = movement.run(packet);
                loopCount++;
            }
        }
        telemetry.addData("Number of times through loop = ", loopCount);
        telemetry.addData("If the number is greater than 1 it proves run() is not blocking","!");
        telemetry.update();

        while (opModeIsActive()) {
            idle();
        }
    }
}

