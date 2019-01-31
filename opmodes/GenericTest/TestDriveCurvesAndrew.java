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
        driveCurve = new DriveCurve(45, .3, 100, 45.72, imu, logfile);
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        logfile = new DataLogging( "Test Drive Curve Andrew", telemetry);
                // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        // can i please have wheel speeds but only once
        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here

            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
