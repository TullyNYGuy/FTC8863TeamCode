package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalIntake;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Intake Switch", group = "Test")
//@Disabled
public class TestIntakeSwitch extends LinearOpMode {

    // Put your variable declarations here
    public UltimateGoalIntake intake;

    @Override
    public void runOpMode() {

        // Put your initializations here
        intake = new UltimateGoalIntake(hardwareMap,telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        intake.turnIntake123On();

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            intake.updateIntake();
            if (intake.ringAtStage2()) {
                intake.turnIntakeOff();
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
