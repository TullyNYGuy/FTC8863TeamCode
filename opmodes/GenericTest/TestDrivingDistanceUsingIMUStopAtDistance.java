package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Driving with IMU stop at distance", group = "Test")
//@Disabled
public class TestDrivingDistanceUsingIMUStopAtDistance extends LinearOpMode {

    // Put your variable declarations here

    DriveTrain driveTrain;
    double correction;
    DriveTrain.Status statusDrive;


    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);

        telemetry.addData("heading = ", driveTrain.imu.getHeading());

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        driveUsingIMU(0, -.2, 100); // heading, power, distance in cm

        telemetry.addData("Finished Forwards", "...");
        telemetry.update();
        sleep(1000);

        // drive backwards
        driveUsingIMU(0, 0.2, 100); // heading, power, distance in cm

        telemetry.addData("Finished Backwards", "!");
        telemetry.update();
        sleep(1000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
        telemetry.update();
        sleep(2000);
    }

    public void driveUsingIMU(double heading, double power, double distanceToTravel){
        double distance = 0;
        driveTrain.setupDriveUsingIMU(heading, power, AdafruitIMU8863.AngleMode.RELATIVE);
        // for testing backwards drop the Kp
        // NOT WORKING, NO CLUE WHY
        driveTrain.pidControl.setKp(.001);

        while(opModeIsActive() && driveTrain.updateDriveUsingIMU() < distanceToTravel) {
            telemetry.addData("distance = ", driveTrain.getDistanceDriven());
            telemetry.addData("heading = ", driveTrain.imu.getHeading());
            telemetry.addData("left motor power = ", "%f1.2", driveTrain.getLeftPower());
            telemetry.addData("right motor power = ", "%f1.2", driveTrain.getRightPower());
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            idle();
        }
        driveTrain.stopDriveUsingIMU();
        telemetry.addData("distance = ", driveTrain.getDistanceDriven());
        telemetry.addData("heading = ", driveTrain.imu.getHeading());
        telemetry.addData(">", "Press Stop to end test." );
        telemetry.update();
        sleep(3000);
    }
}
