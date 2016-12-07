package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDControl;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Autonomous Turn", group = "Test")
//@Disabled
public class TestAutonomousTurn extends LinearOpMode {

    // Put your variable declarations here

    DriveTrain driveTrain;
    double correction;


    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        driveTrain.setupTurn(90,0.1);

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            driveTrain.updateTurn();

            // Put your calls that need to run in a loop here
//            if(driveTrain.updateTurn()) {
//                driveTrain.shutdown();
//                break;
//            }
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.addData("Angle = ", "%3.1f", driveTrain.imu8863.getHeading());
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.addData("Angle = ", "%3.1f", driveTrain.imu8863.getHeading());
        telemetry.update();
        sleep(3000);
    }
}
