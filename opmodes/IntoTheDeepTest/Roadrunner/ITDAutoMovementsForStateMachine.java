package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Roadrunner;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RoadRunner.PinpointDrive;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD AutoMovements For State Machine", group = "Test")
//@Disabled
public final class ITDAutoMovementsForStateMachine extends LinearOpMode {

    private Pose2d startAutoPose = new Pose2d(32.5, 54.375, Math.toRadians(-90));
    //  private Pose2d deliveryPose = new Pose2d(48.5, 51.75, Math.toRadians(-135));
    private Pose2d deliveryPose = new Pose2d(49.25, 48.5, Math.toRadians(-135));
    private Pose2d sample1IntakePose = new Pose2d(47.75, 39, Math.toRadians(-90));
    // private Pose2d sample2IntakePose=new Pose2d(59, 39.25, Math.toRadians(-90));
    private Pose2d sample2IntakePose=new Pose2d(57.5, 39.25, Math.toRadians(-90));
    private Pose2d sample3IntakePose=new Pose2d(60, 34.5, Math.toRadians(-45));
    Pose2d parkPose=new Pose2d(16.5, 11.25, Math.toRadians(-180));

    // Define the action needed for a movement from point a to point b
    private Action startToDelivery;
    private Action deliveryToSample1;
    private Action sample1ToDelivery;
    private Action deliveryToSample2;
    private Action sample2ToDelivery;
    private Action deliveryToSample3;
    private Action sample3ToDelivery;
    private Action deliveryToPark;


    @Override
    public void runOpMode() throws InterruptedException {

        PinpointDrive mecanumDrive = new PinpointDrive(hardwareMap, startAutoPose);

        startToDelivery = mecanumDrive.actionBuilder(startAutoPose)
                .splineToLinearHeading(deliveryPose, Math.PI / 2)
                .build();

        deliveryToSample1 = mecanumDrive.actionBuilder(deliveryPose)
                .strafeToLinearHeading(sample1IntakePose.position, sample1IntakePose.heading)
                .build();

        sample1ToDelivery = mecanumDrive.actionBuilder(sample1IntakePose)
                .strafeToLinearHeading(deliveryPose.position, deliveryPose.heading)
                .build();

        deliveryToSample2 = mecanumDrive.actionBuilder(deliveryPose)
                .strafeToLinearHeading(sample2IntakePose.position, sample2IntakePose.heading)
                .build();

        sample2ToDelivery = mecanumDrive.actionBuilder(sample2IntakePose)
                .strafeToLinearHeading(deliveryPose.position, deliveryPose.heading)
                .build();

        deliveryToSample3 = mecanumDrive.actionBuilder(deliveryPose)
                .strafeToLinearHeading(sample3IntakePose.position, sample3IntakePose.heading)
                .build();

        sample3ToDelivery = mecanumDrive.actionBuilder(sample3IntakePose)
                .strafeToLinearHeading(deliveryPose.position, deliveryPose.heading)
                .build();
        deliveryToPark = mecanumDrive.actionBuilder(deliveryPose)
                .splineToLinearHeading(parkPose, -Math.PI)
                .build();

        Pose2d beginPose = new Pose2d(32.5, 54.375, Math.toRadians(-90));


        waitForStart();

        Actions.runBlocking(startToDelivery);
        Actions.runBlocking(deliveryToSample1);
        Actions.runBlocking(sample1ToDelivery);
        Actions.runBlocking(deliveryToSample2);
        Actions.runBlocking(sample2ToDelivery);
        Actions.runBlocking(deliveryToSample3);
        Actions.runBlocking(sample3ToDelivery);
        Actions.runBlocking(deliveryToPark);
    }
}

