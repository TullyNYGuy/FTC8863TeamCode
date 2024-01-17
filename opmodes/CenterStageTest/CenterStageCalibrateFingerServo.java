package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageFingerServoLeft;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStagePlaneGUNservo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Center Stage Calibrate Finger Servo", group = "Test")
@Disabled

/*
    This class allows you to calibrate a servo.
    RIGHT Y joystick controls the servo position
    A button locks the position
    B button unlocks the position
 */
public class CenterStageCalibrateFingerServo extends LinearOpMode {

    // Put your variable declarations here
    CenterStageFingerServoLeft servo;

    @Override
    public void runOpMode() {

        // Put your initializations here
        servo = new CenterStageFingerServoLeft(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // testPositionUsingJoystick runs the while loop and updates telemety
        servo.testPositionUsingJoystick(this);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}
