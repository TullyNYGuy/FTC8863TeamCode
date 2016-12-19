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
@TeleOp(name = "Test Drive Square", group = "Test")
//@Disabled
public class TestDrivingSquare extends LinearOpMode {

    // Put your variable declarations here

    DriveTrain driveTrain;
    double correction;
    DriveTrain.Status statusDrive;


    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap);
        driveTrain.setCmPerRotation(31.1); // cm

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();
        driveStraight(50, 0.5);
        telemetry.addData("Finished Straight", "1");
        telemetry.update();
        sleep(1000);
        anyTurn(-90, 0.4);
        telemetry.addData("Finished Turn", "1");
        telemetry.update();
        sleep(1000);

        driveStraight(50, 0.5);
        telemetry.addData("Finished Straight", "2");
        telemetry.update();
        sleep(1000);
        anyTurn(-90, 0.4);
        telemetry.addData("Finished Turn", "2");
        telemetry.update();
        sleep(1000);

        driveStraight(50, 0.5);
        telemetry.addData("Finished Straight", "3");
        telemetry.update();
        sleep(1000);
        anyTurn(-90, 0.4);
        telemetry.addData("Finished Turn", "3");
        telemetry.update();
        sleep(1000);

        driveStraight(50, 0.5);
        telemetry.addData("Finished Straight", "4");
        telemetry.update();
        sleep(1000);
        anyTurn(-90, 0.4);
        telemetry.addData("Finished Turn", "4");
        telemetry.update();
        sleep(1000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
        telemetry.update();
        sleep(3000);
    }

    public void anyTurn(double angle, double power) {
        driveTrain.setupTurn(angle,power);

        while(opModeIsActive() && !driveTrain.updateTurn()) {
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
            telemetry.update();
            idle();
        }
        telemetry.addData("Finished Turn", "0");
        telemetry.update();
    }

    public void driveStraight(double distance, double power){
        driveTrain.setupDriveDistance(power, distance, DcMotor8863.FinishBehavior.FLOAT);

        while(opModeIsActive()) {
            statusDrive = driveTrain.updateDriveDistance();
            if (statusDrive == DriveTrain.Status.COMPLETE) {
                break;
            }
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.addData("Status = ", statusDrive.toString());
            telemetry.update();
            idle();
        }
        telemetry.addData(">", "Press Stop to end test." );
        telemetry.addData("Status = ", statusDrive.toString());
        telemetry.update();
    }
}
