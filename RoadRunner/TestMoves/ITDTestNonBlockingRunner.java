package org.firstinspires.ftc.teamcode.RoadRunner.TestMoves;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.RRNonBlockingRunner;
import org.firstinspires.ftc.teamcode.RoadRunner.PinpointDrive;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Non Blocking Action Runner", group = "Test")
//@Disabled
public final class ITDTestNonBlockingRunner extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(32.5, 54.375, Math.toRadians(-90));
        Pose2d endPose = new Pose2d(48.5, 51.75, Math.toRadians(-135));
        PinpointDrive drive = new PinpointDrive(hardwareMap, beginPose);

        // Define an action. In this case the action is a movement to be run by the drive train
        Action movement = drive.actionBuilder(beginPose)
                // start to delivery position
                .splineToLinearHeading(endPose, Math.PI / 2)
                .build();

        // Define a non-blocking runner to run the action
        RRNonBlockingRunner movementRunner = new RRNonBlockingRunner(movement);

        int loopCount = 0;

        waitForStart();

        // Run the action/move
        // If the action/movement is complete, this loop will terminate
        while (opModeIsActive() && !movementRunner.isComplete()) {
            // Run a single iteration of the action
            movementRunner.runNonBlocking();
            loopCount++;
        }
        telemetry.addData("Number of times through loop = ", loopCount);
        telemetry.addData("If the number is greater than 1 it proves run() is not blocking","!");
        telemetry.update();

        while (opModeIsActive()) {
            idle();
        }
    }
}

