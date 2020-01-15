package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest.vision;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.GoldMineralDetection;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Gold Mineral Detection", group = "Test")
//@Disabled
public class TestGoldMineralDetection extends LinearOpMode {

    // Put your variable declarations here
    GoldMineralDetection goldMineralDetection;
    DataLogging logFile;

    @Override
    public void runOpMode() {

        // Put your initializations here
        logFile = new DataLogging("GoldDetection", telemetry);
        goldMineralDetection = new GoldMineralDetection(hardwareMap, telemetry, logFile);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        // run object recognitions for 1.5 seconds
        goldMineralDetection.activate(1500);
        logFile.startTimer();

        while (opModeIsActive() && !goldMineralDetection.isRecognitionComplete()) {
            goldMineralDetection.getRecognition(2);
            idle();
        }

        goldMineralDetection.shutdown();
        telemetry.addData("Most likely gold location = ", goldMineralDetection.getMostLikelyGoldPosition().toString());
        telemetry.update();
        sleep(4000);
    }
}
