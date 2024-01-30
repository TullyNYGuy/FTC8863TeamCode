package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageHang;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

import java.util.Locale;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Center Stage Test Hang", group = "Test")
//@Disabled
public class CenterStageTestHang extends LinearOpMode {

    // Put your variable declarations here
    CenterStageHang hang;

    @Override
    public void runOpMode() {


        // Put your initializations here
        hang = new CenterStageHang(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here
            hang.update();

            if (gamepad1.y) {
                hang.hang();
            }
            if (gamepad1.a) {
                hang.stop();
            }

            telemetry.addData("state = ", hang.getState().toString());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
