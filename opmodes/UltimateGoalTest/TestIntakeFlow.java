package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalIntake;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Intake Flow", group = "Test")
//@Disabled
public class TestIntakeFlow extends LinearOpMode {

    // Put your variable declarations here
    public UltimateGoalIntake intake;
    public ElapsedTime timer;

    @Override
    public void runOpMode() {

        // Put your initializations here
        intake = new UltimateGoalIntake(hardwareMap,telemetry);
        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        intake.requestTurnStage123On();

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            intake.updateIntake();
            if (intake.ringAtStage2()) {
                intake.requestTurnIntakeOFF();
                break;
            }

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            idle();
        }

        intake.requestTurnStage123On();
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            intake.updateIntake();
            idle();
        }

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            intake.updateIntake();
            if (intake.ringAtStage2()) {
                intake.requestTurnIntakeOFF();
                break;
            }

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            idle();
        }

        intake.requestTurnStage123On();
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            intake.updateIntake();
            idle();
        }

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            intake.updateIntake();
            if (intake.ringAtStage2()) {
                intake.requestTurnIntakeOFF();
                break;
            }

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            idle();
        }

        intake.requestTurnStage123On();
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < 2000) {
            intake.updateIntake();
            idle();
        }

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            intake.updateIntake();
            if (intake.ringAtStage2()) {
                intake.requestTurnIntakeOFF();
                break;
            }

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
