package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Drive Beacon", group = "Test")
//@Disabled
public class TestDrivingWithSideBeacon extends LinearOpMode {

    // Put your variable declarations here

    DriveTrain driveTrain;
    double correction;
    DriveTrain.Status statusDrive;
    Servo8863 beaconServo;

    AdafruitColorSensor8863 colorSensor;


    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        driveTrain.setCmPerRotation(31.1); // cm

        beaconServo = new Servo8863("beaconServo", hardwareMap, telemetry);
        beaconServo.setHomePosition(0.6);
        beaconServo.setPositionOne(.8);
        beaconServo.goHome();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        beaconServo.goPositionOne();
        driveUsingIMU(0, .3);
        telemetry.addData("Finished Straight", "1");
        telemetry.update();
        sleep(1000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        //telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
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

    public void driveUsingIMU(double heading, double power){
        double distance;
        driveTrain.setupDriveUsingIMU(heading, power, AdafruitIMU8863.AngleMode.RELATIVE);

        while(opModeIsActive()) {
            distance = driveTrain.updateDriveUsingIMU();
//
//            if (distance > 200) {
//                driveTrain.stopDriveUsingIMU();
//                break;
//            }
//
//            telemetry.addData(">", "Press Stop to end test." );
//            telemetry.addData("Heading = ", driveTrain.imu.getHeading());
//            telemetry.addData("distance = ", distance);
            telemetry.update();
            idle();
        }
        telemetry.addData(">", "Press Stop to end test." );
        telemetry.addData("distance = ", driveTrain.getDistanceDriven());
        telemetry.update();
        sleep(5000);
    }
}
