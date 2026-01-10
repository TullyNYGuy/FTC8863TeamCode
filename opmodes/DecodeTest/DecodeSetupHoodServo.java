package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeHoodServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Setup Hood Servo", group = "Setup")
//@Disabled
public class DecodeSetupHoodServo extends LinearOpMode {

    // Put your variable declarations here
    public DecodeHoodServo hoodServo;

    @Override
    public void runOpMode() {


        // Put your initializations here
        hoodServo = new DecodeHoodServo(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        hoodServo.setupServoPositionsUsingGamepad(this);


        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
