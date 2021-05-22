package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.AngleChanger;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PersistantStorage;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Angle Adjuster Reset", group = "Test")
//@Disabled
public class AngleAdjusterReset extends LinearOpMode {

    // Put your variable declarations here
    public AngleChanger angleChanger;

    @Override
    public void runOpMode() {


        // Put your initializations here
        angleChanger =  AngleChanger.createAngleChanger(hardwareMap, telemetry);

        angleChanger.setAngleReference();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        angleChanger.setAngleNegative(-36);
        while (opModeIsActive() && !angleChanger.isAngleAdjustComplete()) {

            // Put your calls that need to run in a loop here
            angleChanger.update();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        AngleChanger.clearAngleChanger();
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
