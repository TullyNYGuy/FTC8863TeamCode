package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveCurve;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Crater Side Lander to Depot Sample Right Mineral", group = "Test")
//@Disabled
public class TestDriveCurveCraterSideLanderToDepotAndSampleRightMineral extends LinearOpMode {

    // Put your variable declarations here
    DriveCurve driveCurve;
    AdafruitIMU8863 imu;
    DataLogging logFile;
    DriveTrain driveTrain;
    double curveAngle;

    @Override
    public void runOpMode() {

        // Put your initializations here
        logFile = new DataLogging( "Test Drive CraterSide Lander To right mineral to Depot", telemetry);
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        driveTrain.setLogFile(logFile);
        driveTrain.enableLogDrive();
        driveTrain.setFinishBehavior(DcMotor8863.FinishBehavior.HOLD);
        // at power = .3 remove 1 degree from the turn (found 1 degree per 40ms cycle - .0256 deg/mS rate of turn)
        // at power = .5 remove 2 degree from the turn (found 1.8 degree per 40ms cycle- .0445 deg/mS rate of turn)

        double speed = 0.3;

        // CCW curve forward
        curveAngle = -55.0;
        driveCurve = new DriveCurve(curveAngle, speed, 24.69 * 2.54, DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.FORWARD, driveTrain.imu, logFile, driveTrain);

        driveCurve.enableLogging();
        driveCurve.enablePID();

                // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        logFile.startTimer();

        // curve from lander onto lane to hit right mineral
        driveCurve.setupDriveCurve(curveAngle, speed, 24.69 * 2.54, DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.FORWARD);
        driveCurve.startDriveCurve();
        while(opModeIsActive() && !driveCurve.isCurveComplete()) {
            driveCurve.update();
            telemetry.addData(">", "Curving ..." );
            telemetry.update();
            idle();
        }

        // curve from mineral back to lane in front of lander
        curveAngle = -90;
        driveCurve.setupDriveCurve(curveAngle, speed, 31.15 * 2.54, DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.BACKWARD);
        driveCurve.startDriveCurve();
        while(opModeIsActive() && !driveCurve.isCurveComplete()) {
            driveCurve.update();
            telemetry.addData(">", "Curving ..." );
            telemetry.update();
            idle();
        }

        // drive straight after CCW curve forward
        // drive on lane in front of lander towards wall
        driveTrain.setupDriveUsingIMU(-90, 31.13 * 2.54, speed, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
        driveTrain.startDriveUsingIMU();
        while (opModeIsActive()&& !driveTrain.updateDriveUsingIMU()) {
            // Display the current value
            telemetry.addData(">", "Driving straight ...");
            telemetry.update();
            idle();
        }

        // curve onto lane near wall heading towards depot
        curveAngle = -45;
        driveCurve.setupDriveCurve(curveAngle, speed, 38.84 * 2.54, DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD);
        driveCurve.startDriveCurve();
        while(opModeIsActive() && !driveCurve.isCurveComplete()) {
            driveCurve.update();
            telemetry.addData(">", "Curving ..." );
            telemetry.update();
            idle();
        }

        // drive along lane into depot
        driveTrain.setupDriveUsingIMU(-45, 13 * 2.54, speed, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
        driveTrain.startDriveUsingIMU();
        while (opModeIsActive()&& !driveTrain.updateDriveUsingIMU()) {
            // Display the current value
            telemetry.addData(">", "Driving straight ...");
            telemetry.update();
            idle();
        }

        driveTrain.stopDriveDistanceUsingIMU();
        sleep(1000);

        // Put your cleanup code here - it runs as the application shuts down
        logFile.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
