package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import android.provider.ContactsContract;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
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
@TeleOp(name = "Test Drive Curves", group = "Test")
//@Disabled
public class TestDriveCurvesAndrew extends LinearOpMode {

    // Put your variable declarations here
    DriveCurve driveCurve;
    AdafruitIMU8863 imu;
    DataLogging logfile;
    DriveTrain driveTrain;


    @Override
    public void runOpMode() {

        // Put your initializations here
        logfile = new DataLogging( "Test Drive Curve", telemetry);
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);

        double curveAngle = 45.0;
        double speed = 0.3;
        double curveRadius = 100; // cm
        double wheelbase = 45.72; // 18 cm
        driveCurve = new DriveCurve(curveAngle, speed, curveRadius, wheelbase, driveTrain.imu, logfile, driveTrain);

                // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        driveTrain.setLeftDriveMotorSpeed(driveCurve.getLeftWheelSpeed());
        driveTrain.setRightDriveMotorSpeed(driveCurve.getRightWheelSpeed());
        driveTrain.applyPowersToMotors();

        while(opModeIsActive() && !driveCurve.update()) {
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
