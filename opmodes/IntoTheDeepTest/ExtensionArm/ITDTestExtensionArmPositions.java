package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.ExtensionArm;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDExtensionArm;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDExtensionArmIntakeController;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeArmServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Extension Arm Positions", group = "Test")
@Disabled
public class ITDTestExtensionArmPositions extends LinearOpMode {

    // Put your variable declarations here
    public ITDExtensionArm extensionArm;
    public ITDExtensionArmIntakeController extensionArmIntakeController;
    public DataLogging log;

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionArm = new ITDExtensionArm(hardwareMap, telemetry);
        extensionArmIntakeController = new ITDExtensionArmIntakeController(hardwareMap, telemetry);
        extensionArm.setController(extensionArmIntakeController);

        log = new DataLogging("ExtensionArmPositionTest");
        extensionArm.setDataLog(log);
        extensionArm.enableDataLogging();


        extensionArm.reset();
        while (!extensionArm.isResetComplete()) {
            extensionArm.update();
            extensionArm.displayState(telemetry);
            telemetry.update();
        }
        extensionArm.displayState(telemetry);
        extensionArm.displayPosition(telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here
            extensionArm.update();

            if (gamepad1.y) {
                extensionArm.intakePosition();
            }

            if (gamepad1.b) {
                extensionArm.bucketClearancePosition();
            }

            if (gamepad1.x) {
                extensionArm.transferPosition();
            }

            if (gamepad1.a) {
                extensionArm.initPosition();
            }

            if (gamepad1.dpad_up) {
                extensionArm.outtakePosition();
            }

            extensionArm.displayState(telemetry);
            extensionArm.displayPosition(telemetry);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
        log.closeDataLog();

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
