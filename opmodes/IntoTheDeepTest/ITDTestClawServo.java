package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageHangMechanism;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ClawServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Into The Deep Test Claw Servo", group = "Test")
//@Disabled
public class ITDTestClawServo extends LinearOpMode {

    // Put your variable declarations here
    public ClawServo clawServo;

    @Override
    public void runOpMode() {


        // Put your initializations here
        clawServo = new ClawServo(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here
            //clawServo.update();

            if (gamepad1.y) {
                clawServo.open();
            }
            if (gamepad1.a) {
                clawServo.clamp();
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
