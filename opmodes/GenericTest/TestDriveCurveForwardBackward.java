package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

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
@TeleOp(name = "Test Drive Curve Forward Then Backward", group = "Test")
//@Disabled
public class TestDriveCurveForwardBackward extends LinearOpMode {

    // Put your variable declarations here
    DriveCurve driveCurve;
    AdafruitIMU8863 imu;
    DataLogging logFile;
    DriveTrain driveTrain;


    @Override
    public void runOpMode() {

        // Put your initializations here
        logFile = new DataLogging( "Test Drive Curve Forward Backward", telemetry);
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        driveTrain.setLogFile(logFile);
        driveTrain.enableLogDrive();

        // at power = .3 remove 1 degree from the turn (found 1 degree per 40ms cycle - .0256 deg/mS rate of turn)
        // at power = .5 remove 2 degree from the turn (found 1.8 degree per 40ms cycle- .0445 deg/mS rate of turn)
        double curveAngle = -89.0;
        double speed = 0.1;
        double curveRadius = 100; // cm
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
        driveCurve.startDriveCurve();

        while(opModeIsActive() && !driveCurve.isCurveComplete()) {
            driveCurve.update();
            telemetry.addData(">", "Curving Forwards ..." );
            telemetry.update();
            idle();
        }

        driveCurve.stopCurve(DcMotor8863.FinishBehavior.HOLD);
        sleep(2000);

        curveAngle = 0.0;
        driveCurve = new DriveCurve(curveAngle, speed, curveRadius, DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD, wheelbase, driveTrain.imu, logFile, driveTrain);
        driveCurve.enableLogging();
        driveCurve.enablePID();
        //driveCurve.disablePID();

        // Put your calls here - they will not run in a loop
        driveCurve.startDriveCurve();

        while(opModeIsActive() && !driveCurve.isCurveComplete()) {
            driveCurve.update();
            telemetry.addData(">", "Curving Backwards ..." );
            telemetry.update();
            idle();
        }

        driveCurve.stopCurve(DcMotor8863.FinishBehavior.HOLD);

        sleep(2000);

        // Put your cleanup code here - it runs as the application shuts down
        logFile.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
