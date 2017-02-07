package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Driving Differential", group = "Test")
//@Disabled
public class TestDriveTrainDifferential extends LinearOpMode {

    // Put your variable declarations here

    DriveTrain driveTrain;
    double correction;
    DriveTrain.Status statusDrive;
    ElapsedTime timer;
    double throttle = .1;
    double direction = 0;


    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        timer = new ElapsedTime();

        telemetry.addData("heading = ", driveTrain.imu.getHeading());

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();

        // forwards
        while (opModeIsActive() && timer.milliseconds() < 4000) {
            driveTrain.differentialDrive(throttle, direction);
            telemetry.addData("distance = ", driveTrain.getDistanceDriven());
            telemetry.addData("heading = ", driveTrain.imu.getHeading());
            telemetry.addData("left motor power = ", "%f1.2", driveTrain.getLeftPower());
            telemetry.addData("right motor power = ", "%f1.2", driveTrain.getRightPower());
            telemetry.update();
            idle();
        }
        driveTrain.shutdown();
        sleep(3000);

        timer.reset();

        // backwards
        while (opModeIsActive() && timer.milliseconds() < 4000) {
            driveTrain.differentialDrive(-throttle, direction);
            telemetry.addData("distance = ", driveTrain.getDistanceDriven());
            telemetry.addData("heading = ", driveTrain.imu.getHeading());
            telemetry.addData("left motor power = ", "%f1.2", driveTrain.getLeftPower());
            telemetry.addData("right motor power = ", "%f1.2", driveTrain.getRightPower());
            telemetry.update();
            idle();
        }
        driveTrain.shutdown();
        sleep(3000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
        telemetry.update();
        sleep(3000);
    }
}