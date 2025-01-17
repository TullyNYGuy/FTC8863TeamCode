package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Controllers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDExtensionArmIntakeController;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeBucketController;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDLiftBucketArmBucketGateController;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Lift Bucket Controller", group = "Test")
//@Disabled
public class ITDTestLiftBucketController extends LinearOpMode {

    // Put your variable declarations here
    public ITDExtensionArmIntakeController extensionArmIntakeController;
    public ITDLiftBucketArmBucketGateController liftBucketArmBucketGateController;
    public ITDIntakeBucketController intakeBucketController;
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

            }

            if (gamepad1.x) {

            }

            if (gamepad1.b) {


            }

            if (gamepad1.y) {


            }

            if (gamepad1.dpad_down) {

            }

            if (gamepad1.dpad_left) {

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
