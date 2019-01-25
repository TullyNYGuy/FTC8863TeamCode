package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Driving Straight Without an IMU", group = "Test")
//@Disabled
public class TestDriveStraightWithoutIMUIan extends LinearOpMode {

    // Put your variable declarations here
    public DriveTrain driveTrain;
    public DataLogging logFile;

    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        logFile = new DataLogging("Test_Driving_Straight_Without_An_IMU", telemetry);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        driveTrain.setupDriveDistance(0.2, 182.88, DcMotor8863.FinishBehavior.HOLD);
        driveTrain.imu.resetAngleReferences();

        while (opModeIsActive()&& driveTrain.updateDriveDistance() != DriveTrain.Status.COMPLETE) {


            logFile.logData(Double.toString(driveTrain.imu.getHeading()), Double.toString(driveTrain.getDistanceDriven()));


            // Put your calls that need to run in a loop here

            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        logFile.closeDataLog();
        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
