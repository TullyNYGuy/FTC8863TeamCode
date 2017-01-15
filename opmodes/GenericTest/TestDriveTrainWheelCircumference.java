package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Drive Train Wheel Circumference", group = "Test")
//@Disabled
public class TestDriveTrainWheelCircumference extends LinearOpMode {

    // Put your variable declarations here

    DriveTrain driveTrain;
    double correction;
    DriveTrain.Status statusDrive;


    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        driveTrain.setCmPerRotation(29.9); // cm

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        driveTrain.rotateNumberOfDegrees(.1, 3600, DcMotor8863.FinishBehavior.HOLD); //10 rotations

        while(opModeIsActive() && driveTrain.updateDriveDistance() != DriveTrain.Status.COMPLETE) {
            idle();
        }

        telemetry.addData(">", "10 Rotations of wheel completed" );
        telemetry.update();
        sleep(3000);
    }
}
