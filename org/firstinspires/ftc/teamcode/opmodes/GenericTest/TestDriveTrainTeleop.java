package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This OpMode runs 2 motors at a given power, one in the opposite direction from the other. 
 * It is meant to be used to test a drive train
 *
 * This code assumes a DC motor configured with the name "leftMotor"  and "rightMotor"
 *
 */
@TeleOp(name = "Test Drive Train Teleop", group = "Test")
//@Disabled
public class TestDriveTrainTeleop extends LinearOpMode {

    DriveTrain myDriveTrain;
    DriveTrain.Status statusDrive = DriveTrain.Status.COMPLETE;
    double leftPower = 0;
    double rightPower =0;
    double throttle = 0;
    double direction = 0;
    boolean differentialDrive = false;
    double powerFactor = 1.0;




    @Override
    public void runOpMode() {


        // Instantiate and initialize motors
        myDriveTrain = DriveTrain.DriveTrainTeleOp(hardwareMap, telemetry);

        myDriveTrain.setCmPerRotation(31.1); // cm

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motors." );
        telemetry.update();
        waitForStart();

        while(opModeIsActive()) {
            // Y = differential drive
            if (gamepad1.y) {
                differentialDrive = true;
            }

            // A = tank drive
            if (gamepad1.a) {
                differentialDrive = false;
            }

            // B = full power
            if (gamepad1.b) {
                powerFactor = 1.0;
            }

            // X = 30% power
            if (gamepad1.x) {
                powerFactor = 0.1;
            }

            if (differentialDrive == false) {
                leftPower = -gamepad1.left_stick_y* powerFactor;
                rightPower = -gamepad1.right_stick_y* powerFactor;
                myDriveTrain.tankDrive(leftPower,rightPower);
            }

            if (differentialDrive == true) {
                throttle = -gamepad1.right_stick_y* powerFactor;
                direction = gamepad1.right_stick_x* powerFactor;
                //myDriveTrain.differentialDrive(.0,correction);
                myDriveTrain.differentialDrive(throttle, direction);
            }

            telemetry.addData("Left Motor Speed = ", "%3.2f", leftPower);
            telemetry.addData("Right Motor Speed = ", "%3.2f", rightPower);
            telemetry.addData("Left Motor Speed = ", "%3.2f", throttle);
            telemetry.addData("Right Motor Speed = ", "%3.2f", direction);
            telemetry.update();
            idle();
        }

        // Turn off drivetrain and signal done;
        myDriveTrain.shutdown();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
