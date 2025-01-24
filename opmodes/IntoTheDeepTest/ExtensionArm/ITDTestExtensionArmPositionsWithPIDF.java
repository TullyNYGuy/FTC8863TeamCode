package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.ExtensionArm;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDExtensionArm;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDExtensionArmIntakeController;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Extension Arm Positions PIDF", group = "Test")
//@Disabled
public class ITDTestExtensionArmPositionsWithPIDF extends LinearOpMode {

    // Put your variable declarations here
    public ITDExtensionArm extensionArm;
    public ITDExtensionArmIntakeController extensionArmIntakeController;
    public DataLogging log;
    public double p = 10.0;

    public Debouncer debouncedDPadUp;
    public Debouncer debouncedDPadDown;

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionArm = new ITDExtensionArm(hardwareMap, telemetry);
        extensionArmIntakeController = new ITDExtensionArmIntakeController(hardwareMap, telemetry);
        extensionArm.setController(extensionArmIntakeController);

        log = new DataLogging("ExtensionArmPositionTest");
        extensionArm.setDataLog(log);
        extensionArm.enableDataLogging();

        debouncedDPadDown = new Debouncer();
        debouncedDPadUp = new Debouncer();


        extensionArm.reset();
        while (!extensionArm.isResetComplete()) {
            extensionArm.update();
            extensionArm.displayState(telemetry);
            telemetry.update();
        }
        extensionArm.displayState(telemetry);
        extensionArm.displayPosition(telemetry);
        extensionArm.displayPIDF(telemetry);

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

            if (gamepad1.dpad_right) {
                extensionArm.outtakePosition();
            }

            if (debouncedDPadUp.isPressed(gamepad1.dpad_up)) {
                p = p + .1;
            }

            if (debouncedDPadDown.isPressed(gamepad1.dpad_down)) {
                p = p - .1;
            }

            if (gamepad1.dpad_left) {
                extensionArm.setPositionPIDFCoefficients(p);
            }


            telemetry.addData("p = ", p);
            extensionArm.displayState(telemetry);
            extensionArm.displayPosition(telemetry);
            extensionArm.displayPIDF(telemetry);
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
