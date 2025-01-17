package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Servos;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDBucketGateServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Setup Bucket Gate Servo", group = "Setup")
//@Disabled
public class ITDSetupBucketGateServo extends LinearOpMode {

    // Put your variable declarations here
    public ITDBucketGateServo intakeArmServo;

    @Override
    public void runOpMode() {


        // Put your initializations here
        intakeArmServo = new ITDBucketGateServo(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        intakeArmServo.setupServoPositionsUsingGamepad(this);


        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
