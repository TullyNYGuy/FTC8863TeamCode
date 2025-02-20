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
@TeleOp(name = "ITD Test Controller Extension Arm Intake", group = "Test")
//@Disabled
public class ITDTestExtensionArmIntakeController extends LinearOpMode {

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

        extensionArmIntakeController.setIntakeBucketController(intakeBucketController);
        liftBucketArmBucketGateController.setIntakeBucketController(intakeBucketController);

        // setup for data logging
        dataLog = new DataLogging("ControllerExtensionArmIntake", telemetry);
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
            intakeBucketController.update();
            extensionArmIntakeController.update();
            //liftBucketArmBucketGateController.update();

            if (gamepad1.a) {
                extensionArmIntakeController.setupForInitBucketClearance();
                delayStarted = false;
            }
            if (gamepad1.x) {
                extensionArmIntakeController.completeSetupForInit();
            }
            if (gamepad1.b) {
                extensionArmIntakeController.init(null);
            }
            if (gamepad1.y) {
                extensionArmIntakeController.transfer();
            }

            if (gamepad1.dpad_down) {
                extensionArmIntakeController.getReadyToRun();
            }

            if (gamepad1.dpad_left) {
                extensionArmIntakeController.completeGetReadyToRun();
            }

            if (gamepad1.dpad_right) {
                extensionArmIntakeController.setupForIntake();
            }

            if (gamepad1.dpad_up) {
                extensionArmIntakeController.intakeLowAltitude();
            }

            if (gamepad1.right_bumper) {
                extensionArmIntakeController.setupForOuttake();
            }

            extensionArmIntakeController.displayState(telemetry);
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
