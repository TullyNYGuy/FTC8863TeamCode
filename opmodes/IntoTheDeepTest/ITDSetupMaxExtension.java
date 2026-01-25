package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDBucketArm;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDBucketArmServo;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDExtensionArm;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDExtensionArmIntakeController;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Setup Max Extension", group = "Setup")
@Disabled
public class ITDSetupMaxExtension extends LinearOpMode {

    // Put your variable declarations here
    ITDExtensionArm extensionArm;
    ITDExtensionArmIntakeController extensionArmIntakeController;
    ITDBucketArmServo bucketArmServo;
    DataLogging log;

    @Override
    public void runOpMode() {

        // Put your initializations here

        extensionArm = new ITDExtensionArm(hardwareMap, telemetry);
        bucketArmServo = new ITDBucketArmServo(hardwareMap, telemetry);
        extensionArmIntakeController = new ITDExtensionArmIntakeController(hardwareMap, telemetry);
        extensionArm.setController(extensionArmIntakeController);

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

        extensionArm.intakePosition();
        bucketArmServo.deliveryPosition();

        while (opModeIsActive()) {
            extensionArm.update();
            bucketArmServo.isPositionReached();

            telemetry.addData("Check for 42", " limit!");
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
