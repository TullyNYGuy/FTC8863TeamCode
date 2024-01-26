package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageFingerServoRight;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Center Stage Test Right Finger Servo", group = "Test")
@Disabled
public class CenterStageTestRightFingerServo extends LinearOpMode {

    // Put your variable declarations here
    public CenterStageFingerServoRight servo;

    @Override
    public void runOpMode() {


        // Put your initializations here
        //creata a finger servo*
        servo=new CenterStageFingerServoRight(hardwareMap,telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        servo.close();
        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here

            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
