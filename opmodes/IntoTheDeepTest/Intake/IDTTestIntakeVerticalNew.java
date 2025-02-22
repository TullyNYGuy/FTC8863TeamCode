package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Intake;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;
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

    Debouncer debouncedA;
    Debouncer debouncedB;
    Debouncer debouncedX;
    Debouncer debouncedY;
    Debouncer debouncedDpadUp;
    Debouncer debouncedDpadDown;
    Debouncer debouncedDpadLeft;
    Debouncer debouncedDpadRight;

    @Override
    public void runOpMode() {


        // Put your initializations here
        intakeSweeperVertical = new ITDIntakeSweeperVertical(hardwareMap, telemetry, 2);
        extensionArmIntakeController = new ITDExtensionArmIntakeController(hardwareMap, telemetry);
        intakeBucketController = new ITDIntakeBucketController(hardwareMap, telemetry);
        timer = new ElapsedTime();
        intakeSweeperVertical.setAllianceColor(Color.RED);
        intakeSweeperVertical.setController(extensionArmIntakeController);
        extensionArmIntakeController.setIntakeBucketController(intakeBucketController);

        logFile = new DataLogging("IntakeTest");
        intakeSweeperVertical.setDataLog(logFile);
        intakeSweeperVertical.enableDataLogging();

        debouncedA = new Debouncer();
        debouncedB = new Debouncer();
        debouncedX = new Debouncer();
        debouncedY = new Debouncer();
        debouncedDpadUp = new Debouncer();
        debouncedDpadLeft = new Debouncer();
        debouncedDpadRight = new Debouncer();
        debouncedDpadRight = new Debouncer();


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

            if (debouncedY.isPressed(gamepad1.y)) {
                intakeSweeperVertical.intake();
            }
            if (debouncedA.isPressed(gamepad1.a)) {
                intakeSweeperVertical.stop();
            }
            if (debouncedB.isPressed(gamepad1.b)) {
                intakeSweeperVertical.outtake();
            }
            if (debouncedX.isPressed(gamepad1.x)) {
                intakeSweeperVertical.transfer();
            }
            if (debouncedDpadUp.isPressed(gamepad1.dpad_up)) {
                intakeSweeperVertical.runIntakeServos();
            }

            intakeSweeperVertical.displayState(telemetry);
            intakeSweeperVertical.displayDistanceToSample(telemetry);
            intakeSweeperVertical.displayColorDataFront(telemetry);
            intakeSweeperVertical.displaySampleColorFront(telemetry);
            intakeSweeperVertical.displayColorDataRear(telemetry);
            intakeSweeperVertical.displaySampleColorRear(telemetry);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
