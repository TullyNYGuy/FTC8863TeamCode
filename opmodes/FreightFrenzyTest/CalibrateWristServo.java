package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Calibrate Wrist Servo", group = "Calibrate")
//@Disabled
public class CalibrateWristServo extends LinearOpMode {

    // Put your variable declarations her
    Servo8863 wristServo;
    double wristUpPosition = 0;
    double wristDownPosition = .58;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        wristServo = new Servo8863("wristServo", hardwareMap, telemetry);
        wristServo.setUpServoCalibration(0, 1, .1, 500);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            wristServo.updateServoCalibration();
            idle();
        }
    }
}
