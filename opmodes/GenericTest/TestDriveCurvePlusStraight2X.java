package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveCurve;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Drive Curve + Straight x2", group = "Test")
//@Disabled
public class TestDriveCurvePlusStraight2X extends LinearOpMode {

    // Put your variable declarations here
    DriveCurve driveCurve;
    AdafruitIMU8863 imu;
    DataLogging logFile;
    DriveTrain driveTrain;


    @Override
    public void runOpMode() {

        // Put your initializations here
        logFile = new DataLogging( "Test Drive Curve + Straight", telemetry);
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        driveTrain.setLogFile(logFile);
        driveTrain.enableLogDrive();

        // at power = .3 remove 1 degree from the turn (found 1 degree per 40ms cycle - .0256 deg/mS rate of turn)
        // at power = .5 remove 2 degree from the turn (found 1.8 degree per 40ms cycle- .0445 deg/mS rate of turn)
        double curveAngle = -86.0;
        double speed = 0.3;
        double curveRadius = 50; // cm
        double wheelbase = 37.38; // measured
        driveCurve = new DriveCurve(curveAngle, speed, curveRadius, DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.FORWARD, wheelbase, driveTrain.imu, logFile, driveTrain);
        driveCurve.enableLogging();
        driveCurve.enablePID();

                // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        logFile.startTimer();

        // Put your calls here - they will not run in a loop
        driveTrain.setLeftDriveMotorSpeed(driveCurve.getLeftWheelSpeed());
        driveTrain.setRightDriveMotorSpeed(driveCurve.getRightWheelSpeed());
        driveTrain.applyPowersToMotors();

        while(opModeIsActive() && !driveCurve.update()) {
            telemetry.addData(">", "Curving ..." );
            telemetry.update();
            idle();
        }

        driveTrain.setupDriveUsingIMU(-90, 50, speed, AdafruitIMU8863.AngleMode.ABSOLUTE);
        while (opModeIsActive()&& !driveTrain.updateDriveUsingIMU()) {
            // Display the current value
            telemetry.addData(">", "Driving straight ...");
            telemetry.update();
            idle();
        }

        // at power = .3 remove 1 degree from the turn (found 1 degree per 40ms cycle - .0256 deg/mS rate of turn)
        // at power = .5 remove 2 degree from the turn (found 1.8 degree per 40ms cycle- .0445 deg/mS rate of turn)
        curveAngle = +86.0;
        speed = 0.3;
        curveRadius = 50; // cm
        wheelbase = 37.38; // measured
        driveCurve = new DriveCurve(curveAngle, speed, curveRadius, DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.FORWARD, wheelbase, driveTrain.imu, logFile, driveTrain);
        driveCurve.enableLogging();
        driveCurve.enablePID();

        driveTrain.setLeftDriveMotorSpeed(driveCurve.getLeftWheelSpeed());
        driveTrain.setRightDriveMotorSpeed(driveCurve.getRightWheelSpeed());
        driveTrain.applyPowersToMotors();

        while(opModeIsActive() && !driveCurve.update()) {
            telemetry.addData(">", "Curving ..." );
            telemetry.update();
            idle();
        }

        driveTrain.setupDriveUsingIMU(0, 50, speed, AdafruitIMU8863.AngleMode.ABSOLUTE);
        while (opModeIsActive()&& !driveTrain.updateDriveUsingIMU()) {
            // Display the current value
            telemetry.addData(">", "Driving straight ...");
            telemetry.update();
            idle();
        }

        driveTrain.stopDriveUsingIMU();
        sleep(2000);

        // Put your cleanup code here - it runs as the application shuts down
        logFile.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
