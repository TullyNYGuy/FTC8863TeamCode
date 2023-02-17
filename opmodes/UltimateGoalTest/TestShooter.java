package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalDualMotorGearBox;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Shooter Test", group = "Test")
@Disabled
public class TestShooter extends LinearOpMode {

    // Put your variable declarations here
    public UltimateGoalDualMotorGearBox dualMotorGearBox;
    public CRServo stage3CRServo;
    public ElapsedTime timer;
    public int motorRPM;

    @Override
    public void runOpMode() {


        // Put your initializations here
        dualMotorGearBox = new UltimateGoalDualMotorGearBox("LeftMotor", "RightMotor", hardwareMap, telemetry);
        stage3CRServo = hardwareMap.get(CRServo.class, "stage3CRServo");
        stage3CRServo.setDirection(DcMotorSimple.Direction.FORWARD);

        timer = new ElapsedTime();
        motorRPM = 8000;

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        dualMotorGearBox.setSpeed(motorRPM);
        stage3CRServo.setPower(1);
        timer.reset();
        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here

            // Display the current value
            telemetry.addData("Gear box set to run at ", motorRPM + " RPM");
            telemetry.addData("RPM is measured to be  ", dualMotorGearBox.getSpeed() + "RPM");
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }

//        dualMotorGearBox.stopGearbox();
//        timer.reset();
//
//        while (opModeIsActive() && timer.milliseconds() < 5000) {
//
//            // Put your calls that need to run in a loop here
//
//            // Display the current value
//            telemetry.addData("Gear box stopping", "...");
//            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
//            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
//            telemetry.addData(">", "Press Stop to end test.");
//
//            telemetry.update();
//
//            idle();
//        }
//        dualMotorGearBox.setDirection(DualMotorGearBox.Direction.REVERSE);
//        dualMotorGearBox.setSpeed(motorRPM);
//        timer.reset();
//
//        while (opModeIsActive() && timer.milliseconds() < 5000) {
//
//            // Put your calls that need to run in a loop here
//
//            // Display the current value
//            telemetry.addData("Gear box running at ", motorRPM + " RPM");
//            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
//            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
//            telemetry.addData(">", "Press Stop to end test.");
//
//            telemetry.update();
//
//            idle();
//        }
//        // Put your cleanup code here - it runs as the application shuts down
//        dualMotorGearBox.stopGearbox();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
