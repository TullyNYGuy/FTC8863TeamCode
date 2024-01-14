package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageIntakeMotor;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStagePlaneGUNservo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Center Stage Test Intake Motor", group = "Test")
//@Disabled
public class CenterStageTestIntakeMotor extends LinearOpMode {

    // Put your variable declarations here
    CenterStageIntakeMotor IntakeMotor;
    ElapsedTime timer;

    @Override
    public void runOpMode() {

        // Put your initializations here
        IntakeMotor = new CenterStageIntakeMotor(hardwareMap, telemetry);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();

        // after the reset is complete just loop so the user can see the state
        while (opModeIsActive()) {

            // Put your calls here - they will not run in a loop
            if (gamepad1.x) {
                // turn the pixel grabber on so it will handle an incoming pixel
                IntakeMotor.on();
            }

            if (gamepad1.b) {
                IntakeMotor.off();
                // note that this turns the pixel grabber off after the release is complete
            }

            if (gamepad1.a) {
            }

            if (gamepad1.y) {
            }


            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
