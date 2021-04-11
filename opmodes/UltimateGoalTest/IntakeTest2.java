package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalIntake;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Ultamate Goal Intake Test 2", group = "Test")
//@Disabled
public class IntakeTest2 extends LinearOpMode {

    // Put your variable declarations here
    public UltimateGoalIntake intake;
    public ElapsedTime timeMachine;

    @Override
    public void runOpMode() {


        // Put your initializations here
        intake = new UltimateGoalIntake(hardwareMap, telemetry);
        timeMachine = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run :)");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        intake.requestTurnStage12On();
        timeMachine.reset();
        waitForTime(5000);

        intake.requestTurnStage123On();
        timeMachine.reset();
        waitForTime(5000);

        intake.requestTurnStage23On();
        timeMachine.reset();
        waitForTime(5000);

        intake.requestTurnStage3On();
        timeMachine.reset();
        waitForTime(5000);

        intake.requestTurnIntakeOFF();
        timeMachine.reset();
        waitForTime(5000);

        intake.requestTurnStage12On();
        timeMachine.reset();
        waitForTime(5000);

        intake.requestTurnIntakeOFF();
        timeMachine.reset();
        waitForTime(5000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }

    public void waitForTime(double time) {
        while (opModeIsActive() && timeMachine.milliseconds()<time) {

            // Put your calls that need to run in a loop here
            intake.updateIntake();
            // Display the current value
            telemetry.addData(">", "Press Stop to terminate.");

            telemetry.update();

            idle();
        }
    }
}
