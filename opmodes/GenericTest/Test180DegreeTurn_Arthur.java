package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test 180 Degree Turn", group = "Test")
//@Disabled
public class Test180DegreeTurn_Arthur extends LinearOpMode {

    // Put your variable declarations here
    public DriveTrain driveTrain;

    @Override
    public void runOpMode() {


        // Put your initializations here
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();


        driveTrain.setupTurn(-90, 0.3, AdafruitIMU8863.AngleMode.ABSOLUTE);
        // Put your calls here - they will not run in a loop

        while (opModeIsActive() && driveTrain.updateTurn() != true) {

            // Put your calls that need to run in a loop here


            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        sleep(1000);
        driveTrain.setupTurn(-175, 0.3, AdafruitIMU8863.AngleMode.RELATIVE);
        // Put your calls here - they will not run in a loop

        while (opModeIsActive() && driveTrain.updateTurn() != true) {

            // Put your calls that need to run in a loop here


            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");

            telemetry.update();

            idle();
        }
        // Put your cleanup code here - it runs as the application shuts down
        driveTrain.shutdown();
        telemetry.addData("Angle = ", driveTrain.imu.getHeading());
        telemetry.addData(">", "Done");
        telemetry.update();
        sleep(10000);

    }
}
