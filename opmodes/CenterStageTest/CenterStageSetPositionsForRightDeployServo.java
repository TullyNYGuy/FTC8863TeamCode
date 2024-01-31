package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageArmDeployServoRight;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Center Stage Setup Arm Deploy Servo Right Positions", group = "Test")
//@Disabled

/*
    This class allows you to setup the servo positions
    See
 */
public class CenterStageSetPositionsForRightDeployServo extends LinearOpMode {

    // Put your variable declarations here
    CenterStageArmDeployServoRight servo;

    @Override
    public void runOpMode() {

        // Put your initializations here
        servo = new CenterStageArmDeployServoRight(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to go");
        telemetry.update();
        waitForStart();

        // testPositionUsingJoystick runs the while loop and updates telemety
        servo.setupServoPositionsUsingGamepad(this);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}
