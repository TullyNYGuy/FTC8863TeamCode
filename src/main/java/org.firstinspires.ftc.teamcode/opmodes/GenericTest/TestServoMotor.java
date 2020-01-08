package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcServoMotor;

/**
 * This OpMode tests a DC motor and is meant to test the functionality of the DcMotor8863 class.
 * It also demonstrates the various methods available to control motor movement. Examples for each
 * of the major methods are given. Read the comments to understand the example.
 */
@TeleOp(name = "Test ServoMotor", group = "Test")
//@Disabled
public class TestServoMotor extends LinearOpMode {

    //**************************************************************
    // You need these variables inside this block

    // declare the motor
    DcServoMotor motor;

    //**************************************************************

    double powerToRunAt = 0.8; // % of full speed
    int value = 0;

    @Override
    public void runOpMode() {

        ElapsedTime runningTimer = new ElapsedTime(0);

        //**************************************************************
        // You need the initializations inside this block

        // Instantiate and initialize motors
        motor = new DcServoMotor(
                "motor0",
                "servo0",
                0,
                0,
                0,
                hardwareMap,
                telemetry
        );
        // set the type of motor you are controlling
        motor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        // Motor power can range from -1.0 to +1.0. The minimum motor power for the motor. Any power
        // below this will be automatically set to this number.
        motor.setMinMotorPower(-1);
        // The maximum motor power that will be sent to the motor. Any motor power above this will be
        // autimatically trimmed back to this number.
        motor.setMaxMotorPower(1);

        // The direction the motor moves when it is sent a positive power. Can be FORWARD or
        // BACKWARD.
        motor.setDirection(DcMotor.Direction.FORWARD);

        // test internal routines from DcMotor8863
        //value = motor.getEncoderCountForDegrees(-400);
        //telemetry.addData("Encoder count for degrees = ", "%d", value);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motor.");
        telemetry.update();
        waitForStart();

        runningTimer.reset();

        // When the motor was initialized, the encoder was set to 0. Absolute movements are always
        // done relative to that 0 position. Think of these types of commands as:
        // GO TO A POSITION
        // The position is in terms of what the motor is attached to. A claw position, a drive train
        // position etc.
        // These are absolute movement commands:

        // Set power of the motor to the maximum.
        motor.setPower(1);
        // You need to run this loop in order to be able to tell when the motor reaches the position
        // you told it to go to.
        while (opModeIsActive()) {
            motor.update();
            // display some information on the driver phone
            telemetry.addData(">", "Servo should be running forward full speed");
            telemetry.addData("Motor Speed = ", "%5.2f", motor.getCurrentPower());
            telemetry.addData("feedback = ", "%5.2f", motor.getPositionInTermsOfAttachment());
            telemetry.addData(">", "Encoder count should be = 4480");
            telemetry.addData("Encoder Count = ", "%5d", motor.getCurrentPosition());
            telemetry.addData("Elapsed time = ", "%5.0f", runningTimer.milliseconds());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            // You MUST call idle() in the loop so the other tasks the controller runs can be
            // performed
            idle();
        }

        motor.stop();

        // Set power of the motor to the maximum.
        motor.setPower(-1);
        // You need to run this loop in order to be able to tell when the motor reaches the position
        // you told it to go to.
        while (opModeIsActive()) {
            motor.update();
            // display some information on the driver phone
            telemetry.addData(">", "Servo should be running backward full speed");
            telemetry.addData("Motor Speed = ", "%5.2f", motor.getCurrentPower());
            telemetry.addData("feedback = ", "%5.2f", motor.getPositionInTermsOfAttachment());
            telemetry.addData(">", "Encoder count should be = 4480");
            telemetry.addData("Encoder Count = ", "%5d", motor.getCurrentPosition());
            telemetry.addData("Elapsed time = ", "%5.0f", runningTimer.milliseconds());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            // You MUST call idle() in the loop so the other tasks the controller runs can be
            // performed
            idle();
        }

        motor.stop();

        // Set power of the motor to the maximum.
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // You need to run this loop in order to be able to tell when the motor reaches the position
        // you told it to go to.
        while (opModeIsActive()) {
            motor.update();
            // display some information on the driver phone
            telemetry.addData(">", "Servo should be stopped");
            telemetry.addData("Motor Speed = ", "%5.2f", motor.getCurrentPower());
            telemetry.addData("feedback = ", "%5.2f", motor.getPositionInTermsOfAttachment());
            telemetry.addData(">", "Encoder count should be = 4480");
            telemetry.addData("Encoder Count = ", "%5d", motor.getCurrentPosition());
            telemetry.addData("Elapsed time = ", "%5.0f", runningTimer.milliseconds());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            // You MUST call idle() in the loop so the other tasks the controller runs can be
            // performed
            idle();
        }

        motor.stop();

        telemetry.addData(">", "Done");

        telemetry.update();

    }
}
