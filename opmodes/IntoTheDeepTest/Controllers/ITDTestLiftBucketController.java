package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Controllers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDExtensionArmIntakeController;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeBucketController;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDLiftBucketArmBucketGateController;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Controller Lift Bucket", group = "Test")
//@Disabled
public class ITDTestLiftBucketController extends LinearOpMode {

    // Put your variable declarations here
    public ITDExtensionArmIntakeController extensionArmIntakeController;
    public ITDLiftBucketArmBucketGateController liftBucketArmBucketGateController;
    public ITDIntakeBucketController intakeBucketController;
    public DataLogging dataLog;
    public ElapsedTime timer;

    public boolean delayStarted = false;
    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionArmIntakeController = new ITDExtensionArmIntakeController(hardwareMap, telemetry);
        intakeBucketController = new ITDIntakeBucketController(hardwareMap, telemetry);
        liftBucketArmBucketGateController = new ITDLiftBucketArmBucketGateController(hardwareMap, telemetry);

        // setup for communications between controllers and this controller
        extensionArmIntakeController.setIntakeBucketController(intakeBucketController);
        liftBucketArmBucketGateController.setIntakeBucketController(intakeBucketController);

        // setup for data logging
        dataLog = new DataLogging("ControllerLiftBucket", telemetry);
        intakeBucketController.setDataLog(dataLog);
        extensionArmIntakeController.setDataLog(dataLog);
        liftBucketArmBucketGateController.setDataLog(dataLog);
        intakeBucketController.enableDataLogging();
        extensionArmIntakeController.enableDataLogging();
        liftBucketArmBucketGateController.enableDataLogging();

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            //intakeBucketController.update();
            extensionArmIntakeController.update();
            liftBucketArmBucketGateController.update();

            if (gamepad1.a) {
                liftBucketArmBucketGateController.setupForInit();
            }

            if (gamepad1.x) {
                liftBucketArmBucketGateController.init(null);
            }

            if (gamepad1.b) {
                liftBucketArmBucketGateController.getReadyToRun();
            }

            if (gamepad1.y) {
                liftBucketArmBucketGateController.setupForDelivery();
            }

            if (gamepad1.dpad_down) {
                liftBucketArmBucketGateController.setupForDelivery();
            }

            if (gamepad1.dpad_left) {
                liftBucketArmBucketGateController.deliverSample();
            }

            if (gamepad1.dpad_right) {

            }

            if (gamepad1.dpad_up) {

            }

            if (gamepad1.right_bumper) {

            }

            extensionArmIntakeController.displayState(telemetry);
            liftBucketArmBucketGateController.displayState(telemetry);
            intakeBucketController.displayState(telemetry);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
