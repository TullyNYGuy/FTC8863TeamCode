package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Calibrate Shoulder Servo", group = "Calibrate")
//@Disabled
public class CalibrateShoulderServo extends LinearOpMode {

    // Put your variable declarations her
    Servo8863 shoulderServo;
    double shoulderUpPosition = .5;
    double shoulderDownPosition = .2;
    ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        shoulderServo = new Servo8863("shoulderServo",hardwareMap, telemetry);
        shoulderServo.setPositionOne(shoulderUpPosition);
        shoulderServo.setPositionTwo(shoulderDownPosition);

        shoulderServo.setUpServoCalibration(0, 1, .1,500);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            shoulderServo.updateServoCalibration();
            idle();
        }
    }
}
