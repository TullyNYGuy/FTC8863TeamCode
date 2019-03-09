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
@TeleOp(name = "Test Crater Side Lander to Depot Sample Left Mineral", group = "Test")
//@Disabled
public class TestDriveCurveCraterSideLanderToDepotAndSampleLeftMineral extends LinearOpMode {

    // Put your variable declarations here
    DriveCurve driveCurve;
    AdafruitIMU8863 imu;
    DataLogging logFile;
    DriveTrain driveTrain;
    double curveAngle;

    @Override
    public void runOpMode() {

        // Put your initializations here
        logFile = new DataLogging( "Test Drive CraterSide Lander To left mineral to Depot", telemetry);
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

        //
        driveCurve.setupDriveCurve(70, .1, 23 * 2.54, DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.FORWARD);
        driveCurve.startDriveCurve();
        while(opModeIsActive() && !driveCurve.isCurveComplete()) {
            driveCurve.update();
            telemetry.addData(">", "Curving ..." );
            telemetry.update();
            idle();
        }

        driveTrain.setupTurn(-65, .7, AdafruitIMU8863.AngleMode.ABSOLUTE);
        while(opModeIsActive() && !driveTrain.updateTurn()) {
            telemetry.addData(">", "Turning ..." );
            telemetry.update();
            idle();
        }

        // drive straight after CCW curve forward
        // drive on lane in front of lander towards wall
        driveTrain.setupDriveUsingIMU(-65, 2.25 * 2.54, speed, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
        driveTrain.startDriveUsingIMU();
        while (opModeIsActive()&& !driveTrain.updateDriveUsingIMU()) {
            // Display the current value
            telemetry.addData(">", "Driving straight ...");
            telemetry.update();
            idle();
        }
        //
        driveCurve.setupDriveCurve(-90, speed, 47.897 * 2.54, DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.BACKWARD);
        driveCurve.startDriveCurve();
        while(opModeIsActive() && !driveCurve.isCurveComplete()) {
            driveCurve.update();
            telemetry.addData(">", "Curving ..." );
            telemetry.update();
            idle();
        }
        driveCurve.setupDriveCurve(-45, speed, 38.841 * 2.54, DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD);
        driveCurve.startDriveCurve();
        while(opModeIsActive() && !driveCurve.isCurveComplete()) {
            driveCurve.update();
            telemetry.addData(">", "Curving ...");
            telemetry.update();
            idle();
        }
        // drive straight after CCW curve forward
        // drive on lane in front of lander towards wall
        driveTrain.setupDriveUsingIMU(-45, (16.3)* 2.54, speed, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
        driveTrain.startDriveUsingIMU();
        while (opModeIsActive()&& !driveTrain.updateDriveUsingIMU()) {
            // Display the current value
            telemetry.addData(">", "Driving straight ...");
            telemetry.update();
            idle();
        }

//        // curve onto lane near wall heading towards depot
//        curveAngle = -45;
//        driveCurve.setupDriveCurve(curveAngle, speed, 38.84 * 2.54, DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD);
//        driveCurve.startDriveCurve();
//        while(opModeIsActive() && !driveCurve.isCurveComplete()) {
//            driveCurve.update();
//            telemetry.addData(">", "Curving ..." );
//            telemetry.update();
//            idle();
//        }
//
//        // drive along lane into depot
//        driveTrain.setupDriveUsingIMU(-45, 13 * 2.54, speed, DriveTrain.DriveDirection.FORWARD, AdafruitIMU8863.AngleMode.ABSOLUTE);
//        driveTrain.startDriveUsingIMU();
//        while (opModeIsActive()&& !driveTrain.updateDriveUsingIMU()) {
//            // Display the current value
//            telemetry.addData(">", "Driving straight ...");
//            telemetry.update();
//            idle();
//        }

        driveTrain.stopDriveDistanceUsingIMU();
        sleep(1000);

        // Put your cleanup code here - it runs as the application shuts down
        logFile.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
