package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageIntakeMotor;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStagePixelGrabberLeft;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStagePixelIntakeController;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Center Stage Test Intake Controller", group = "Test")
//@Disabled
public class CenterStageTestIntakeController extends LinearOpMode {

    // Put your variable declarations here
    CenterStagePixelIntakeController intakeController;
    DataLogging log;
    ElapsedTime timer;

    Configuration configuration = null;

    @Override
    public void runOpMode() {

        // Put your initializations here
        intakeController = new CenterStagePixelIntakeController(hardwareMap,telemetry);
        intakeController.init(configuration);
        while (!intakeController.isInitComplete()) {
            intakeController.update();
            telemetry.addData("state = ", intakeController.getState().toString());
            telemetry.update();
            idle();
        }

        log = new DataLogging("IntakeControllerLog");
        intakeController.setDataLog(log);
        intakeController.enableDataLogging();

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
            if (gamepad1.x) {
                // turn the pixel grabber on so it will handle an incoming pixel
                intakeController.intake();
            }

            if (gamepad1.y) {
                intakeController.deliverPixels();
                // note that this turns the pixel grabber off after the release is complete
            }

            if (gamepad1.b) {
                intakeController.off();
            }

            telemetry.addData("intake controller state = ", intakeController.getState().toString());
            telemetry.addData("left pixel grabber state = ", intakeController.getLeftPixelGrabberState());
            telemetry.addData("right pixel grabber state = ", intakeController.getRightPixelGrabberState());
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
