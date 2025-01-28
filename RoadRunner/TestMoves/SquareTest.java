package org.firstinspires.ftc.teamcode.RoadRunner.TestMoves;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RoadRunner.PinpointDrive;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Square test", group = "Test")
//@Disabled
public final class SquareTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(0, 0, 0);
            PinpointDrive drive = new PinpointDrive(hardwareMap, beginPose);

            waitForStart();

            Actions.runBlocking(
                    drive.actionBuilder(beginPose)
                            .lineToX(24)
                            .turn(Math.toRadians(90))
                            .lineToY(24)
                            .turn(Math.toRadians(90))
                            .lineToX(0)
                            .turn(Math.toRadians(90))
                            .lineToY(0)
                            .turn(Math.toRadians(90))
                            .build());
    }
}

