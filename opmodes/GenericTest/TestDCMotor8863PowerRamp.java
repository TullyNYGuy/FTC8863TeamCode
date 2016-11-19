package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

/**
 * This OpMode tests a DC motor and is meant to test the functionality of the DcMotor8863 class.
 * It also demonstrates the various methods available to control motor movement. Examples for each
 * of the major methods are given. Read the comments to understand the example.
 */
@TeleOp(name = "Test DcMotor8863 Ramp", group = "Test")
//@Disabled
public class TestDCMotor8863PowerRamp extends LinearOpMode {

    //**************************************************************
    // You need these variables inside this block

    // declare the motor
    DcMotor8863 motor;

    // for setting up a ramp up from 0 to the desired power
    double initialPower = 0;
    double finalPower = 1.0;
    double rampTime = 1000;
    //**************************************************************

    int feedback = 0;
    double powerToRunAt = 0.8; // % of full speed
    int value = 0;

    private ElapsedTime runningTimer;
    private double lastTime = 0;

    private String mode;

    @Override
    public void runOpMode() {

        runningTimer = new ElapsedTime(0);

        //**************************************************************
        // You need the initializations inside this block

        // Instantiate and initialize motors
        motor = new DcMotor8863(RobotConfigMappingForGenericTest.getleftMotorName(), hardwareMap);
        // set the type of motor you are controlling
        motor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        // for each revolution of the motor, it moves something. How far does it move it? In this case
        // I'm just testing the motor so it moves 360 degress per revolution. But it could be a drive
        // train that moves 10 cm per revolution. Look at what is attached and how far it moves per
        // revolution.
        motor.setMovementPerRev(360);
        // When the motor finishes its movement, how close to the goal does it need to be in order
        // to call it done? 5 encoder ticks? 1 encoder tick?
        motor.setTargetEncoderTolerance(5);
        // When the motor finishes its movement is the motor able to spin freely (FLOAT) or does it
        // actively resist being moved and hold its position (HOLD)?
        motor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        // Motor power can range from -1.0 to +1.0. The minimum motor power for the motor. Any power
        // below this will be automatically set to this number.
        motor.setMinMotorPower(-1);
        // The maximum motor power that will be sent to the motor. Any motor power above this will be
        // autimatically trimmed back to this number.
        motor.setMaxMotorPower(1);

        // The direction the motor moves when it is sent a positive power. Can be FORWARD or
        // BACKWARD.
        motor.setDirection(DcMotor.Direction.FORWARD);

        // If you want the motor to slowly ramp up to speed setup a power ramp. If you don't
        // just comment out this line. These parameters set the slope of a line that motor power
        // cannot go above during the ramp up time. Y axis = power, X axis = time
        motor.setupPowerRamp(initialPower, finalPower, rampTime);
        //**************************************************************

        // test internal routines from DcMotor8863
        //value = motor.getEncoderCountForDegrees(-400);
        //telemetry.addData("Encoder count for degrees = ", "%d", value);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motor.");
        telemetry.update();
        waitForStart();

        runningTimer.reset();

        // Next I will demonstrate / test the ability of a motor to change its speed not is a sudden
        // change but by a gradual ramp of the power.

        // Start the motor running in constant power mode (no PID). Then perform a series of
        // gradual power changes.

        boolean powerRampRan1x = false;
        boolean powerRampRan2x = false;
        boolean powerRampRan3x = false;
        boolean powerRampRan4x = false;

        // Set the motor to spin freely when power is removed
        motor.setAfterCompletionToFloat();
        motor.setupPowerRamp(0, 1.0, 4000);
        // Start the motor. Since a power ramp has been setup, the motor start out with a ramp up of
        // power.
        motor.runAtConstantPower(powerToRunAt);
        mode = "0 -> 0.8";
        // You need to run this loop in order to use the power ramp.
        while (opModeIsActive() && !motor.isMotorStateComplete()) {
            motor.update();

            // change motor direction
            if (runningTimer.milliseconds() > 6000 && !powerRampRan1x) {
                // start a power ramp. Power will gradually change from running forward to
                // running backward over 2 seconds
                motor.setupAndStartPowerRamp(1.0, -.8, 4000);
                mode = "1.0 -> -0.8";
                powerRampRan1x = true;
            }

            // After 9 seconds slow the motor down gradually
            if (runningTimer.milliseconds() > 12000 && !powerRampRan2x) {
                // start a power ramp. Power will gradually reduce to 30 %
                motor.setupAndStartPowerRamp(-.8, -.3, 4000);
                mode = "-0.8 -> -0.3";
                powerRampRan2x = true;
            }

            // After 13 seconds speed the motor up
            if (runningTimer.milliseconds() > 18000 && !powerRampRan3x) {
                // start a power ramp. Power will gradually increase to 100 %
                motor.setupAndStartPowerRamp(-.3, -1.0, 4000);
                mode = "-0.3 -> -1.0";
                powerRampRan3x = true;
            }

            // Stop the motor after 17 seconds by gradually bringing it to a stop
            if (runningTimer.milliseconds() > 24000 && !powerRampRan4x) {
                motor.setupAndStartPowerRamp(-1.0, 0, 4000);
                powerRampRan4x = true;
                mode = "-1.0 -> 0.0";
            }

            // Shutdown the motor
            if (runningTimer.milliseconds() > 30000) {
                motor.shutDown();
                break;
            }

            // display some information on the driver phone
            telemetry.addData(">", mode);
            telemetry.addData("Motor Speed = ", "%5.2f", motor.getActualPower());
            telemetry.addData("feedback = ", "%5.2f", motor.getPositionInTermsOfAttachment());
            telemetry.addData("Encoder Count = ", "%5d", motor.getCurrentPosition());
            telemetry.addData("Elapsed time = ", "%5.0f", runningTimer.milliseconds());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();
        }

        telemetry.addData(">", "Movement tests complete");
        telemetry.addData(">", "Press Stop to end test.");
        telemetry.update();

        while (opModeIsActive()) {
            idle();
        }

        // Turn off motor, let it float and signal done;
        motor.setMotorToFloat();
        telemetry.addData(">", "Done");

        telemetry.update();

    }
}
