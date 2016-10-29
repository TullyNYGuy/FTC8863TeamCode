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
    float powerToRunAt = (float)1.0; // 80% of full speed

    @Override
    public void runOpMode() {


        // Instantiate and initialize motors
        myDriveTrain = new DriveTrain(hardwareMap);
        myDriveTrain.setRightPower(powerToRunAt);
        myDriveTrain.setLeftPower(powerToRunAt);
        myDriveTrain.setCmPerRotation(15);

        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motors." );
        telemetry.update();
        waitForStart();

        // Ramp motor speeds till stop pressed.
        while(opModeIsActive()) {
            myDriveTrain.driveDistance(0.7,30);
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
