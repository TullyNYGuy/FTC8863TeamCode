package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.PIDControl;

/**
 * This OpMode runs 2 motors at a given power, one in the opposite direction from the other. 
 * It is meant to be used to test a double ball shooter
 *
 * This code assumes a DC motor configured with the name "motor"  and "rightShooterMotor"
 *
 */
@TeleOp(name = "PIDMotorTest", group = "Test")
//@Disabled
public class PIDMotorTest extends LinearOpMode {

    DcMotor8863 motor;
    PIDControl pidControl;
    int feedback = 0;
    double powerToRunAt = 1.0; // 80% of full speed

    @Override
    public void runOpMode() {


        // Instantiate and initialize motors
        motor = new DcMotor8863("motor", hardwareMap);
        motor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        motor.setMovementPerRev(360);
        motor.setTargetEncoderTolerance(5);
        motor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        motor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        motor.setMinMotorPower(-1);
        motor.setMaxMotorPower(1);

        motor.setDirection(DcMotor.Direction.FORWARD);
        // setup the motor to be run at a constant power
        motor.runAtConstantPowerSetup();

        pidControl = new PIDControl(6,597.1);
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motors." );
        telemetry.update();
        waitForStart();

        // Ramp motor speeds till stop pressed.
        while(opModeIsActive()) {
            // Will not work - needs to have a proper argument!
            powerToRunAt = pidControl.getCorrection(0);

            motor.runAtConstantPower(powerToRunAt);

            // Display the current value
            telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }

        // Turn off motor and signal done;
        motor.setMotorToFloat();
        telemetry.addData(">", "Done");
        
        telemetry.update();

    }
}
