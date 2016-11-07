package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDControl;

/**
 * This OpMode runs 1 motor to a given position. The motor is an andymark 40 so 1120 encoder
 * counts is 1 revolution (360 degrees).
 *
 * This code assumes a DC motor configured with the name "motor"  and "rightShooterMotor"
 *
 */
@TeleOp(name = "Run To Position Test", group = "Test")
//@Disabled
public class TestRunToPosition extends LinearOpMode {

    DcMotor motor;
    double powerToRunAt = .6; // 80% of full speed

    @Override
    public void runOpMode() {


        // Instantiate and initialize motors
        motor = hardwareMap.dcMotor.get(RobotConfigMappingForGenericTest.getleftMotorName());;

        //reset the encoder
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motors." );
        telemetry.update();
        waitForStart();

        // set the target position
        motor.setTargetPosition(1120);
        // set the run mode
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // set the power
        motor.setPower(powerToRunAt);

        while(opModeIsActive()) {

            // Display the current value
            telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Turn off motor and signal done;
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
