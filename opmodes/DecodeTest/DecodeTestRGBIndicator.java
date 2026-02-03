package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.robocol.RobocolParsableBase;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeRGBIndicator;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test RGB indicator", group = "Test")
//@Disabled
public class DecodeTestRGBIndicator extends LinearOpMode {

    // Put your variable declarations here
    public DecodeRGBIndicator RGBIndicator;
    public DataLogging logfile;

    @Override
    public void runOpMode() {


        // Put your initializations here
        RGBIndicator   = new DecodeRGBIndicator(hardwareMap, telemetry);
        logfile = new DataLogging("RGBIndicator");
        RGBIndicator.setDataLog(logfile);
        RGBIndicator.enableDataLogging();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here
            RGBIndicator.update();


            if (gamepad1.aWasPressed()) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicatorColor.GREEN);
            }

            if (gamepad1.xWasPressed()) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicatorColor.BLUE);
            }

            if (gamepad1.bWasPressed()) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicatorColor.RED);
            }

            if (gamepad1.yWasPressed()) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicatorColor.YELLOW);
            }
            if (gamepad1.dpadDownWasPressed()) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicatorColor.VIOLET);
            }
            if (gamepad1.dpadUpWasPressed()) {
                RGBIndicator.setFrequency(3);
                RGBIndicator.setMode(DecodeRGBIndicator.Mode.BLINKING);
            }
            if (gamepad1.dpadLeftWasPressed()) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicatorColor.WHITE);
            }
            if (gamepad1.dpadRightWasPressed()) {
                RGBIndicator.setColor(DecodeRGBIndicator.IndicatorColor.ORANGE);
            }
            if (gamepad1.leftStickButtonWasPressed()) {
                RGBIndicator.setFrequency(6);
                RGBIndicator.setMode(DecodeRGBIndicator.Mode.BLINKING);
            }
            if (gamepad1.rightStickButtonWasPressed()) {
                RGBIndicator.setMode(DecodeRGBIndicator.Mode.SOLID);
            }

            telemetry.addData("dpad up = 3 hz blink","");
            telemetry.addLine("left stick button = 6 hz blink");
            telemetry.addLine("right stich button = solid");
            telemetry.addData("dpad down = violet","");
            telemetry.addData("dpad right = orange","");
            telemetry.addData("dpad left = white","");
            telemetry.addData("a = green", "");
            telemetry.addData("b = red", "");
            telemetry.addData("x = blue", "");
            telemetry.addData("y = yellow", "");

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }


        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        logfile.closeDataLog();
        telemetry.update();
    }
}
