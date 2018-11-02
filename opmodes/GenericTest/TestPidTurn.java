package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test PID Turn", group = "Test")
//@Disabled
public class TestPidTurn extends LinearOpMode {

    // Put your variable declarations here
    DriveTrain driveTrain;
    double correction;


    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        anyTurn(-90, 0.1);
        telemetry.addData("Finished Turn", "2");
        telemetry.update();
        sleep(1000);

//        driveTrain.setupTurn(90, 0.6); // angle, power
//
//        // Put your calls here - they will not run in a loop
//
//        while(opModeIsActive()) {
//
//            driveTrain.updateTurn();
//
//            // Put your calls that need to run in a loop here
////            if(driveTrain.updateTurn()) {
////                driveTrain.shutdown();
////                break;
////            }
//            // Display the current value
//            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
//            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
//            telemetry.addData(">", "Press Stop to end test." );
//            telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
//            telemetry.update();
//
//            idle();
//        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
        telemetry.update();
        sleep(3000);
    }

    public void anyTurn(double angle, double power) {
        driveTrain.setupTurn(angle,power, AdafruitIMU8863.AngleMode.RELATIVE);

        while(opModeIsActive() && !driveTrain.updateTurn()) {
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
            telemetry.update();
            idle();
        }

        driveTrain.stopTurn();

    }
}
