package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Driving with IMU for distance with ramps", group = "Test")
//@Disabled
public class TestDrivingDistanceUsingIMURunToPositionRampUpDown extends LinearOpMode {

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

        driveDistanceUsingIMU(0, .4, 100, 1000, .1, 20); //heading, power, distance, ramp time, power after ramp down, distance to ramp down
        sleep(5000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();
        sleep(1000);
    }

    public void driveDistanceUsingIMU(double heading, double power, double distance,
                                      double timeToRampUpInmSec,
                                      double powerAfterRampDown, double distanceToRampDown) {
        DriveTrain.DrivingState drivingState;

        // setup the drive including the ramp up and the ramp down
        driveTrain.setupDriveDistanceUsingIMU(heading, power, distance,
                AdafruitIMU8863.AngleMode.RELATIVE,
                0, power, timeToRampUpInmSec, // ramp up
                power, powerAfterRampDown, distanceToRampDown); // ramp down

        telemetry.addData(">", "Press Stop to end test.");
        telemetry.update();

        while (opModeIsActive()) {
            drivingState = driveTrain.updateDriveDistanceUsingIMUState();
            if (drivingState == DriveTrain.DrivingState.COMPLETE) {
                //driveTrain.stopDriveDistanceUsingIMU();
                break;
            }
            telemetry.update();
            idle();
        }

        telemetry.addData(">", "Destination Reached");
        telemetry.addData("distance = ", driveTrain.getDistanceDriven());
        telemetry.addData("Heading = ", "%3.1f", driveTrain.imu.getHeading());
        telemetry.update();
    }
}
