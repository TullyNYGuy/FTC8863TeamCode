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
@TeleOp(name = "Test IMU Driving", group = "Test")
//@Disabled
public class TestDrivingDistanceUsingIMU extends LinearOpMode {

    // Put your variable declarations here

    DriveTrain driveTrain;

    @Override
    public void runOpMode() {

        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        driveTrain.setCmPerRotation(31.1); // cm

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        driveDistanceUsingIMU(0,1,250);
        telemetry.addData("Finished Straight", "1");
        telemetry.update();
        sleep(2000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
        telemetry.update();
        sleep(3000);
    }

    public void driveDistanceUsingIMU(double heading, double power, double distance){
        driveTrain.setupDriveDistanceUsingIMU(heading, power, distance, AdafruitIMU8863.AngleMode.RELATIVE, 0, power, 2000);
        while(opModeIsActive()) {
            boolean isDestinationReached = driveTrain.updateDriveDistanceUsingIMU();
            //boolean isDestinationReached = true;
            if (isDestinationReached){
                driveTrain.stopDriveDistanceUsingIMU();
                break;
            }

//            telemetry.addData(">", "Press Stop to end test." );
//            telemetry.addData("Heading = ", driveTrain.imu.getHeading());
//            telemetry.update();
            idle();
        }
        telemetry.addData(">", "Press Stop to end test." );
        //telemetry.addData("distance = ", driveTrain.getDistance());
        telemetry.update();
        sleep(5000);
    }
}
