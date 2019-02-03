package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Driving Straight With an IMU", group = "Test")
//@Disabled
public class TestDriveStraightWithIMULenny extends LinearOpMode {

    // Put your variable declarations here
    public DriveTrain driveTrain;
    public DataLogging logFile;

    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        logFile = new DataLogging("Test_Driving_Straight_With_An_IMU", telemetry);
        driveTrain.enableLogDrive();
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        driveTrain.setupDriveUsingIMU(0, 182.88, 0.2, AdafruitIMU8863.AngleMode.RELATIVE);
        driveTrain.imu.resetAngleReferences();

        while (opModeIsActive()&& !driveTrain.updateDriveUsingIMU()) {
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();
            idle();
        }
        logFile.closeDataLog();
        driveTrain.stopDriveUsingIMU();
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
