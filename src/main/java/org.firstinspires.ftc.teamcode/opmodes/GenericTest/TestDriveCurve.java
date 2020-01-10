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
 */
@TeleOp(name = "Test Drive Curve", group = "Test")
//@Disabled
public class TestDriveCurve extends LinearOpMode {

    // Put your variable declarations here
    DriveCurve driveCurve;
    AdafruitIMU8863 imu;
    DataLogging logFile;
    DriveTrain driveTrain;
    double curveAngle;


    @Override
    public void runOpMode() {

        // Put your initializations here
        logFile = new DataLogging("Test Drive Curve", telemetry);
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        driveTrain.setLogFile(logFile);
        driveTrain.enableLogDrive();

        // at power = .3 remove 1 degree from the turn (found 1 degree per 40ms cycle - .0256 deg/mS rate of turn)
        // at power = .5 remove 2 degree from the turn (found 1.8 degree per 40ms cycle- .0445 deg/mS rate of turn)


        double speed = 0.3;
        double curveRadius = 100; // cm

        // All 100 cm radius curves are actually ending up at 105-106 at speed = .3

        // CW curve backward
        // CW backward at speed = .3 -86 degrees ends up at -90
        //curveAngle = -86.0;
        //driveCurve = new DriveCurve(curveAngle, speed, curveRadius, DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.BACKWARD, driveTrain.imu, logFile, driveTrain);

        // CCW curve backward
        // CCW backward at speed = .3 86 degrees ends up at 89.5
        //curveAngle = 86.0;
        //driveCurve = new DriveCurve(curveAngle, speed, curveRadius, DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD, driveTrain.imu, logFile, driveTrain);

        // CW curve forward
        // CW forward at speed = .3 -86 degrees ends up at -91
        curveAngle = -86.0;
        driveCurve = new DriveCurve(curveAngle, speed, curveRadius, DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.FORWARD, driveTrain.imu, logFile, driveTrain);

        // CCW curve forward
        // CCW forward at speed = .3 86 degrees ends up at 90
        //curveAngle = 86.0;
        //driveCurve = new DriveCurve(curveAngle, speed, curveRadius, DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.FORWARD, driveTrain.imu, logFile, driveTrain);

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
            telemetry.addData(">", "Curving Backwards ...");
            telemetry.update();
            idle();
        }

        driveCurve.stopCurve(DcMotor8863.FinishBehavior.HOLD);
        sleep(2000);

        logFile.logData("heading after stop = " + Double.toString(driveTrain.imu.getHeading()) + " distance driven = ", Double.toString(driveTrain.getDistanceDriven()));

        // Put your cleanup code here - it runs as the application shuts down
        logFile.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
