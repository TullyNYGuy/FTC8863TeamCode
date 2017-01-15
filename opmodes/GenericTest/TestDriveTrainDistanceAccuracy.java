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
@TeleOp(name = "Test Drive Train Distance Accuracy", group = "Test")
//@Disabled
public class TestDriveTrainDistanceAccuracy extends LinearOpMode {

    // Put your variable declarations here

    DriveTrain driveTrain;
    double correction;
    DriveTrain.Status statusDrive;


    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        driveTrain.setCmPerRotation(32.25); // cm

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        driveStraight(100, 0.1);
        telemetry.addData("Finished driving 100 cm", "!");
        telemetry.update();
        sleep(2000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
    }

    public void driveStraight(double distance, double power){
        driveTrain.setupDriveDistance(power, distance, DcMotor8863.FinishBehavior.HOLD);
        telemetry.addData("Driving", "...");
        telemetry.update();

        while(opModeIsActive() && driveTrain.updateDriveDistance() != DriveTrain.Status.COMPLETE) {
            idle();
        }
    }
}
