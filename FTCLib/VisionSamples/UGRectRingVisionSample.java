package org.firstinspires.ftc.teamcode.FTCLib.VisionSamples;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.FTCLib.vision.UGRectDetector;

public class UGRectRingVisionSample extends LinearOpMode {

    org.firstinspires.ftc.teamcode.FTCLib.vision.UGRectDetector UGRectDetector;

    @Override
    public void runOpMode() {
        UGRectDetector = new UGRectDetector(hardwareMap);
        UGRectDetector.init();

        waitForStart();

        while (!isStopRequested() && opModeIsActive()) {
            UGRectDetector.Stack stack = UGRectDetector.getStack();
            switch (stack) {
                case ZERO:
                    break;
                case ONE:
                    break;
                case FOUR:
                    break;
                default:
                    break;
            }
            telemetry.addData("Rings", stack);
        }
    }

}
