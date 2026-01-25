package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Servos;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDIntakeArmServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Setup Intake Arm Servo", group = "Setup")
@Disabled
public class ITDSetupIntakeArmServo extends LinearOpMode {

    // Put your variable declarations here
    public ITDIntakeArmServo intakeArmServo;

    @Override
    public void runOpMode() {


        // Put your initializations here
        intakeArmServo = new ITDIntakeArmServo(hardwareMap, telemetry);

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
