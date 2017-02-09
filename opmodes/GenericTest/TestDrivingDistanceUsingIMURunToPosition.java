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
@TeleOp(name = "Test Driving with IMU for distance using run_to_position", group = "Test")
//@Disabled
public class TestDrivingDistanceUsingIMURunToPosition extends LinearOpMode {

    // Put your variable declarations here

    DriveTrain driveTrain;

    @Override
    public void runOpMode() {

        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);

        telemetry.addData("heading = ", driveTrain.imu.getHeading());

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        driveDistanceUsingIMU(0, .3 , -100); //heading, power, distance
        sleep(5000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
        sleep(1000);
    }

    public void driveDistanceUsingIMU(double heading, double power, double distance){
        driveTrain.setupDriveDistanceUsingIMU(heading, power, distance, AdafruitIMU8863.AngleMode.RELATIVE, 0, power, 2000);

        telemetry.addData(">", "Press Stop to end test." );
        telemetry.update();
        sleep(1000);

        while(opModeIsActive()) {
            boolean isDestinationReached = driveTrain.updateDriveDistanceUsingIMU();
            if (isDestinationReached){
                //driveTrain.stopDriveDistanceUsingIMU();
                break;
            }

            telemetry.update();
            idle();
        }

        telemetry.addData(">", "Destination Reached" );
        telemetry.addData("distance = ", driveTrain.getDistanceDriven());
        telemetry.addData("Heading = ", "%3.1f", driveTrain.imu.getHeading());
        telemetry.update();
    }
}
