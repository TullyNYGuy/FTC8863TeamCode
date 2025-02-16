package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Intake;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDExtensionArmIntakeController;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeBucketController;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeSweeperVertical;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Intake Vertical New", group = "Test")
//@Disabled
public class IDTTestIntakeVerticalNew extends LinearOpMode {

    // Put your variable declarations here
    public ITDIntakeSweeperVertical intakeSweeperVertical;
    public ITDExtensionArmIntakeController extensionArmIntakeController;
    public ITDIntakeBucketController intakeBucketController;

    public DataLogging logFile;

    public ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        intakeSweeperVertical = new ITDIntakeSweeperVertical(hardwareMap, telemetry);
        extensionArmIntakeController = new ITDExtensionArmIntakeController(hardwareMap, telemetry);
        intakeBucketController = new ITDIntakeBucketController(hardwareMap, telemetry);
        timer = new ElapsedTime();
        intakeSweeperVertical.setAllianceColor(Color.RED);
        intakeSweeperVertical.setController(extensionArmIntakeController);
        extensionArmIntakeController.setIntakeBucketController(intakeBucketController);

        logFile = new DataLogging("IntakeTest");
        intakeSweeperVertical.setDataLog(logFile);
        intakeSweeperVertical.enableDataLogging();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        // turn the color sensor LED on
        intakeSweeperVertical.colorSensorOn();

        while (opModeIsActive()) {
            intakeSweeperVertical.update();
            intakeBucketController.update();
            extensionArmIntakeController.update();

            if (gamepad1.y) {
                intakeSweeperVertical.intake();
            }
            if (gamepad1.a) {
                intakeSweeperVertical.stop();
            }
            if (gamepad1.b) {
                intakeSweeperVertical.outtake();
            }
            if (gamepad1.x) {
                intakeSweeperVertical.transfer();
            }
            if (gamepad1.dpad_up) {
                intakeSweeperVertical.runIntakeServos();
            }

            intakeSweeperVertical.displayState(telemetry);
            intakeSweeperVertical.displayDistanceToSample(telemetry);
            intakeSweeperVertical.displayColorData(telemetry);
            intakeSweeperVertical.displaySampleColor(telemetry);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
