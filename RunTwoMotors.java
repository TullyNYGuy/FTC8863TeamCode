package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

/**
 * This OpMode runs 2 motors at a given power, one in the opposite direction from the other. 
 * It is meant to be used to test a double ball shooter
 *
 * This code assumes a DC motor configured with the name "leftShooterMotor"  and "rightShooterMotor"
 *
 */
@TeleOp(name = "Test: Run 2 Motors", group = "Test")
//@Disabled
public class RunTwoMotors extends LinearOpMode {

    DcMotor8863 leftShooterMotor;
    DcMotor8863 rightShooterMotor;
    double speedToRunAt = .8; // 80% of full speed

    @Override
    public void runOpMode() {


        // Instantiate and initialize motors
        leftShooterMotor = new DcMotor8863("leftShooterMotor", hardwareMap);
        leftShooterMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        leftShooterMotor.setUnitsPerRev(360);
        leftShooterMotor.setDesiredEncoderCount(0);
        leftShooterMotor.setEncoderTolerance(5);
        leftShooterMotor.setNextMotorState(DcMotor8863.NextMotorState.FLOAT);
        leftShooterMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        leftShooterMotor.setMinMotorPower(-1);
        leftShooterMotor.setMaxMotorPower(1);

        leftShooterMotor.setDirection(DcMotor.Direction.FORWARD);

        rightShooterMotor = new DcMotor8863("rightShooterMotor", hardwareMap);
        rightShooterMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        rightShooterMotor.setUnitsPerRev(360);
        rightShooterMotor.setDesiredEncoderCount(0);
        rightShooterMotor.setEncoderTolerance(5);
        rightShooterMotor.setNextMotorState(DcMotor8863.NextMotorState.FLOAT);
        rightShooterMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        rightShooterMotor.setMinMotorPower(-1);
        rightShooterMotor.setMaxMotorPower(1);
        
        rightShooterMotor.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motors." );
        telemetry.update();
        waitForStart();

        // Ramp motor speeds till stop pressed.
        while(opModeIsActive()) {

            leftShooterMotor.runAtConstantSpeed(speedToRunAt);
            rightShooterMotor.runAtConstantSpeed(speedToRunAt);

            // Display the current value
            telemetry.addData("Motor Speed = ", "%5.2f", speedToRunAt);
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }

        // Turn off motor and signal done;
        rightShooterMotor.setMotorToFloat();
        leftShooterMotor.setMotorToFloat();
        telemetry.addData(">", "Done");
        
        telemetry.update();

    }
}
