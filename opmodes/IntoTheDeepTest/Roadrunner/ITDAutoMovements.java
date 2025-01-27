package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Roadrunner;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RoadRunner.PinpointDrive;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD AutoMovements", group = "Test")
//@Disabled
public final class ITDAutoMovements extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(32.5, 54.375, Math.toRadians(-90));
            PinpointDrive drive = new PinpointDrive(hardwareMap, beginPose);

            waitForStart();

            Actions.runBlocking(
                    drive.actionBuilder(beginPose)
                            // start to delivery Joe
                            .splineToLinearHeading(new Pose2d(48.5, 51.75, Math.toRadians(-135)), Math.PI / 2)
                            //first pickup
                            .strafeToLinearHeading(new Vector2d(47.75, 39), Math.toRadians(-90))
                            //delivery Joe
                            .strafeToLinearHeading(new Vector2d(48.5, 51.75), Math.toRadians(-135))
                            //second pickup
                            .strafeToLinearHeading(new Vector2d(59, 39.25), Math.toRadians(-90))
                            //delivery Joe
                            .strafeToLinearHeading(new Vector2d(48.5, 51.75), Math.toRadians(-135))
                            //third pickup
                            .strafeToLinearHeading(new Vector2d(60, 34.5), Math.toRadians(-45))
                            //delivery Joe
                            .strafeToLinearHeading(new Vector2d(48.5, 51.75), Math.toRadians(-135))
                            .build());
    }
}

