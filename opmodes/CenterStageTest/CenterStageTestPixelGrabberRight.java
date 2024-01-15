package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStagePixelGrabberLeft;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStagePixelGrabberRight;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;

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
        }

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();

        // after the reset is complete just loop so the user can see the state
        while (opModeIsActive()) {
            pixelGrabber.update();
            pixelGrabber.on();

            // Put your calls here - they will not run in a loop
            if (gamepad1.x) {
                // turn the pixel grabber on so it will handle an incoming pixel
                pixelGrabber.on();
            }

            if (gamepad1.y) {
                pixelGrabber.deliverPixel();
                // note that this turns the pixel grabber off after the release is complete
            }

            switch (state) {
                case WAITING_TO_GRAB:
                    if (pixelGrabber.getState() == CenterStagePixelGrabberRight.State.PIXEL_GRABBED) {
                        timer.reset();
                        state = State.PIXEL_GRABBED;
                    }
                    break;
                    // 5 seconds before pixel is released
                case PIXEL_GRABBED:
                    if (timer.milliseconds() > 5000) {
                        pixelGrabber.deliverPixel();
                        timer.reset();
                        state = State.WAITING_TO_REMOVE_PIXEL;
                    }
                    break;
                    // 5 seconds for you to remove the pixel
                case WAITING_TO_REMOVE_PIXEL:
                    if (timer.milliseconds() > 5000) {
                        pixelGrabber.on();
                        state = State.WAITING_TO_GRAB;
                    }
                    break;
            }

            telemetry.addData("state = ", pixelGrabber.getState().toString());
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
