package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageIntakeController;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStagePixelGrabberLeft;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Center Stage Test Intake Controller", group = "Test")
//@Disabled
public class CenterStageTestIntakeController extends LinearOpMode {

    // Put your variable declarations here
    CenterStageIntakeController intakeController;
    DataLogging log;
    ElapsedTime timer;

    @Override
    public void runOpMode() {

        // Put your initializations here
        intakeController = new CenterStageIntakeController(hardwareMap, telemetry);

        log = new DataLogging("IntakeControllerLog");
        intakeController.setDataLog(log);
        intakeController.enableDataLogging();

        intakeController.init(null);
        while (!intakeController.isInitComplete()) {
            intakeController.update();
        }

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();

        // after the reset is complete just loop so the user can see the state
        while (opModeIsActive()) {
            intakeController.update();

            // Put your calls here - they will not run in a loop
            if (gamepad1.y) {
                intakeController.intake();
                // note that this turns the pixel grabber off after the release is complete
            }
            if (gamepad1.a) {
                intakeController.off();
                // note that this turns the pixel grabber off after the release is complete
            }
            if (gamepad1.x) {
                // turn the pixel grabber on so it will handle an incoming pixel
                intakeController.outake();
            }

            if (gamepad1.b) {
                intakeController.finishIntake();
                // note that this turns the pixel grabber off after the release is complete
            }
            if (gamepad1.dpad_right) {
                intakeController.deliverRightPixel();
                // note that this turns the pixel grabber off after the release is complete
            }
            if (gamepad1.dpad_left) {
                intakeController.deliverLeftPixel();
                // note that this turns the pixel grabber off after the release is complete
            }
            if (gamepad1.dpad_down) {
                intakeController.deliverBothPixels();
                // note that this turns the pixel grabber off after the release is complete
            }


            telemetry.addData("state = ", intakeController.getState().toString());
            telemetry.addData("command = ", intakeController.getCommand().toString());
            telemetry.addData("left pixel grabber state = ", intakeController.getLeftPixelGrabberStateAsString());
            telemetry.addData("left pixel grabber command = ", intakeController.getLeftPixelGrabberCommandAsString());
            telemetry.addData("left pixel present = ", intakeController.isPixelPresentLeft());
            telemetry.addData("Right pixel grabber state = ", intakeController.getRightPixelGrabberStateAsString());
            telemetry.addData("right pixel grabber command = ", intakeController.getRightPixelGrabberCommandAsString());
            telemetry.addData("right pixel present = ", intakeController.isPixelPresentRight());
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
