package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStagePixelGrabberRight;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Center Stage Test Pixel Grabber Right", group = "Test")
//@Disabled
public class CenterStageTestPixelGrabberRight extends LinearOpMode {

    // Put your variable declarations here
    CenterStagePixelGrabberRight pixelGrabber;
    DataLogging log;
    ElapsedTime timer;

    boolean waitingToRelease = false;

    enum State {
        PIXEL_GRABBED,
        PIXEL_RELEASED,
        WAITING_TO_REMOVE_PIXEL,
        WAITING_TO_GRAB
    }

    State state = State.WAITING_TO_GRAB;

    @Override
    public void runOpMode() {

        // Put your initializations here
        pixelGrabber = new CenterStagePixelGrabberRight(hardwareMap, telemetry);

        log = new DataLogging("PixelGrabberLog");
        pixelGrabber.setDataLog(log);
        pixelGrabber.enableDataLogging();

        pixelGrabber.init(null);
        while (!pixelGrabber.isInitComplete()) {
            pixelGrabber.update();
            telemetry.addData("state = ", pixelGrabber.getStateAsString());
            telemetry.addData("command = ", pixelGrabber.getCommandAsString());
            telemetry.addData("pixel present = ", pixelGrabber.isPixelPresent());
            telemetry.addData("command complete = ", pixelGrabber.isCommandComplete());
            telemetry.addData("init complete = ", pixelGrabber.isInitComplete());
            telemetry.addData("pixel grabbed = ", pixelGrabber.isPixelGrabbed());
            telemetry.update();
        }

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData("state = ", pixelGrabber.getStateAsString());
        telemetry.addData("command = ", pixelGrabber.getCommandAsString());
        telemetry.addData("pixel present = ", pixelGrabber.isPixelPresent());
        telemetry.addData("command complete = ", pixelGrabber.isCommandComplete());
        telemetry.addData("init complete = ", pixelGrabber.isInitComplete());
        telemetry.addData("pixel grabbed = ", pixelGrabber.isPixelGrabbed());
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();

        // after the reset is complete just loop so the user can see the state
        while (opModeIsActive()) {
            pixelGrabber.update();

            // Put your calls here - they will not run in a loop
            if (gamepad1.y) {
                // turn the pixel grabber on so it will handle an incoming pixel
                pixelGrabber.on();
            }

            if (gamepad1.b) {
                pixelGrabber.deliverPixel();
                // note that this turns the pixel grabber off after the release is complete
            }

            if (gamepad1.x) {
                pixelGrabber.grabPixel();
                // note that this turns the pixel grabber off after the release is complete
            }

            if (gamepad1.a) {
                pixelGrabber.off();
                // note that this turns the pixel grabber off after the release is complete
            }

            telemetry.addData("state = ", pixelGrabber.getStateAsString());
            telemetry.addData("command = ", pixelGrabber.getCommandAsString());
            telemetry.addData("pixel present = ", pixelGrabber.isPixelPresent());
            telemetry.addData("command complete = ", pixelGrabber.isCommandComplete());
            telemetry.addData("init complete = ", pixelGrabber.isInitComplete());
            telemetry.addData("pixel grabbed = ", pixelGrabber.isPixelGrabbed());
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
