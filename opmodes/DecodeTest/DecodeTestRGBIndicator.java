package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeRGBIndicator;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test RGB indicator", group = "Test")
//@Disabled
public class DecodeTestRGBIndicator extends LinearOpMode {

    // Put your variable declarations here
    public DecodeRGBIndicator RGBIndicator;

    @Override
    public void runOpMode() {


        // Put your initializations here
        RGBIndicator   = new DecodeRGBIndicator(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here


            if (gamepad1.a) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicaterColor.GREEN);
            }

            if (gamepad1.x) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicaterColor.BLUE);
            }

            if (gamepad1.b) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicaterColor.RED);
            }

            if (gamepad1.y) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicaterColor.YELLOW);
            }
            if (gamepad1.dpad_down) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicaterColor.VIOLET);
            }
            if (gamepad1.dpad_up) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicaterColor.BLACK);
            }
            if (gamepad1.dpad_left) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicaterColor.WHITE);
            }
            if (gamepad1.dpad_right) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicaterColor.ORANGE);
            }
            telemetry.addData("dpad up = black","");
            telemetry.addData("dpad down = violet","");
            telemetry.addData("dpad right = orange","");
            telemetry.addData("dpad left = white","");
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }


        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
