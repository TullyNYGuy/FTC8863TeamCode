package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Servos;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDBucketGateServo;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeGateServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Setup Intake Gate Servo", group = "Setup")
@Disabled
public class ITDSetupIntakeGateServo extends LinearOpMode {

    // Put your variable declarations here
    public ITDIntakeGateServo intakeGateServo;

    @Override
    public void runOpMode() {


        // Put your initializations here
        intakeGateServo = new ITDIntakeGateServo(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        intakeGateServo.setupServoPositionsUsingGamepad(this);


        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
