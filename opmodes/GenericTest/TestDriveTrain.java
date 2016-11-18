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
@TeleOp(name = "Test Drive Train", group = "Test")
//@Disabled
public class TestDriveTrain extends LinearOpMode {

    DriveTrain myDriveTrain;
    double powerToRunAt;
    double powerToRunAt1 = 0.3; // % of full speed
    double powerToRunAt2 = 0.3; // % of full speed
    double distanceToMove1 = 100; // cm
    double distanceToMove2 = -30; // cm
    DriveTrain.Status statusDrive = DriveTrain.Status.COMPLETE;
    double leftPower = 0;
    double rightPower =0;
    double throttle = 0;
    double direction = 0;



    @Override
    public void runOpMode() {


        // Instantiate and initialize motors
        // myDriveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap);
        myDriveTrain = DriveTrain.DriveTrainTeleOp(hardwareMap);


        myDriveTrain.setCmPerRotation(31.1); // cm

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motors." );
        telemetry.update();
        waitForStart();

        powerToRunAt = powerToRunAt1;
        //myDriveTrain.driveDistance(powerToRunAt1, distanceToMove1, DcMotor8863.FinishBehavior.FLOAT);
        //myDriveTrain.rotateNumberOfDegrees(powerToRunAt1, 3600, DcMotor8863.FinishBehavior.HOLD);

        while(opModeIsActive()) {

            //leftPower = -gamepad1.left_stick_y;
            //rightPower = -gamepad1.right_stick_y;
            //myDriveTrain.tankDrive(leftPower,rightPower);

            throttle = -gamepad1.right_stick_y;
            direction = gamepad1.right_stick_x;
            myDriveTrain.differentialDrive(throttle, direction);

            statusDrive = myDriveTrain.update();
            if (statusDrive == DriveTrain.Status.COMPLETE) {
                break;
            }
            idle();
        }

        telemetry.addData(">", "Finished movement 1" );
        telemetry.update();
        sleep(2000);

       /* powerToRunAt = powerToRunAt2;
        myDriveTrain.driveDistance(powerToRunAt2, distanceToMove2, DcMotor8863.FinishBehavior.FLOAT);

        while(opModeIsActive()) {
            statusDrive = myDriveTrain.update();
            if (statusDrive == DriveTrain.Status.COMPLETE) {
                break;
            }
            idle();
        }
        telemetry.addData(">", "Finished movement 2" );
        telemetry.update();
        sleep(2000); */

        while(opModeIsActive()) {
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();

            idle();
        }

        // Turn off drivetrain and signal done;
        myDriveTrain.shutdown();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
