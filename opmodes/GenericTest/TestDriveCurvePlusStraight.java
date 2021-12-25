package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import android.app.backup.BackupAgent;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveCurve;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Drive Curve + Straight", group = "Test")
@Disabled
public class TestDriveCurvePlusStraight extends LinearOpMode {

    // Put your variable declarations here
    DriveCurve driveCurve;
    AdafruitIMU8863 imu;
    DataLogging logFile;
    DriveTrain driveTrain;
    double curveAngle;

    @Override
    public void runOpMode() {

        // Put your initializations here
        logFile = new DataLogging("Test Drive Curve + Straight", telemetry);
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        driveTrain.setLogFile(logFile);
        driveTrain.enableLogDrive();

        // at power = .3 remove 1 degree from the turn (found 1 degree per 40ms cycle - .0256 deg/mS rate of turn)
        // at power = .5 remove 2 degree from the turn (found 1.8 degree per 40ms cycle- .0445 deg/mS rate of turn)

        double speed = 0.3;
        double curveRadius = 100; // cm

        // CW curve forward
        //curveAngle = -89.0;
        //driveCurve = new DriveCurve(curveAngle, speed, curveRadius, DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.FORWARD, driveTrain.imu, logFile, driveTrain);

        // CCW curve forward
        //curveAngle = 89.0;
        //driveCurve = new DriveCurve(curveAngle, speed, curveRadius, DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.FORWARD, driveTrain.imu, logFile, driveTrain);

        // CW curve backward
        //curveAngle = -89.0;
        //driveCurve = new DriveCurve(curveAngle, speed, curveRadius, DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.BACKWARD, driveTrain.imu, logFile, driveTrain);

        // CW curve backward
        curveAngle = 89.0;
        driveCurve = new DriveCurve(curveAngle, speed, curveRadius, DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD, driveTrain.imu, logFile, driveTrain);

        driveCurve.enableLogging();
        driveCurve.enablePID();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        logFile.startTimer();

        // Put your calls here - they will not run in a loop
        driveCurve.startDriveCurve();

        while (opModeIsActive() && !driveCurve.isCurveComplete()) {
            driveCurve.update();
            telemetry.addData(">", "Curving ...");
            telemetry.update();
            idle();
        }

        // drive straight after CW curve forward
        //driveTrain.setupDriveUsingIMU(-90, 150, speed, DriveTrain.DriveDirection.FORWARD, AdafruitIMU8863.AngleMode.ABSOLUTE);

        // drive straight after CCW curve forward
        //driveTrain.setupDriveUsingIMU(90, 50, speed, DriveTrain.DriveDirection.FORWARD, AdafruitIMU8863.AngleMode.ABSOLUTE);

        // drive straight after CW curve backward
        //driveTrain.setupDriveUsingIMU(-90, -150, speed, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);

        // drive straight after CCW curve backward
        driveTrain.setupDriveUsingIMU(90, -30, speed, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);

        while (opModeIsActive() && !driveTrain.updateDriveUsingIMU()) {
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
