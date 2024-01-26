package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageArmServo;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageWristServo;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Center Stage Setup Intake Position", group = "Test")
//@Disabled

/*
    This class allows you to setup the servo positions
    See
 */
public class CenterStageSetPositionsForIntake extends LinearOpMode {

    // Put your variable declarations here
    CenterStageArmServo armServo;
    CenterStageWristServo wristServo;

    @Override
    public void runOpMode() {

        // Put your initializations here
        armServo = new CenterStageArmServo(hardwareMap, telemetry);
        wristServo = new CenterStageWristServo(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to go");
        telemetry.update();
        waitForStart();

        // testPositionUsingJoystick runs the while loop and updates telemety

        armServo.intakePosition();
        wristServo.intakePosition();
        while (opModeIsActive()){
            idle();
        }
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}
