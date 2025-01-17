package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Servos;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDArmDeployServoLeft;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Setup Arm Deploy Servo Left Positions", group = "Setup")
//@Disabled

/*
    This class allows you to setup the servo positions
    See
 */
public class ITDSetPositionsForLeftDeployServo extends LinearOpMode {

    // Put your variable declarations here
    ITDArmDeployServoLeft servo;

    @Override
    public void runOpMode() {

        // Put your initializations here
        servo = new ITDArmDeployServoLeft(hardwareMap, telemetry);

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
