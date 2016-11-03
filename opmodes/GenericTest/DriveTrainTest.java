package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;

/**
 * This OpMode runs 2 motors at a given power, one in the opposite direction from the other. 
 * It is meant to be used to test a double ball shooter
 *
 * This code assumes a DC motor configured with the name "motor"  and "rightShooterMotor"
 *
 */
@Autonomous(name = "DriveTrainTest", group = "Test")
//@Disabled
public class DriveTrainTest extends LinearOpMode {

    DriveTrain myDriveTrain;
    double powerToRunAt;
    double powerToRunAt1 = 0.3; // % of full speed
    double powerToRunAt2 = 0.1; // % of full speed
    double distanceToMove1 = 30; // cm
    double distanceToMove2 = 60; // cm

    @Override
    public void runOpMode() {


        // Instantiate and initialize motors
        myDriveTrain = new DriveTrain(hardwareMap);
        myDriveTrain.setRightPower(powerToRunAt1);
        myDriveTrain.setLeftPower(powerToRunAt1);
        myDriveTrain.setCmPerRotation(15); // cm

        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motors." );
        telemetry.update();
        waitForStart();

        powerToRunAt = powerToRunAt1;
        myDriveTrain.driveDistance(powerToRunAt1, distanceToMove1, DcMotor8863.FinishBehavior.FLOAT);
        powerToRunAt = powerToRunAt2;
        myDriveTrain.driveDistance(powerToRunAt2, distanceToMove2, DcMotor8863.FinishBehavior.FLOAT);

        // Ramp motor speeds till stop pressed.
        while(opModeIsActive()) {

            // Display the current value
            telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }

        // Turn off motor and signal done;
        //myDriveTrain.setMotorToFloat();
        telemetry.addData(">", "Done");
        
        telemetry.update();

    }
}
