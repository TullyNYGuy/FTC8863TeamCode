package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.StatTrackerGB;

/**
 * This OpMode tests a DC motor and is meant to test the functionality of the DcMotor8863 class.
 * It also demonstrates the various methods available to control motor movement. Examples for each
 * of the major methods are given. Read the comments to understand the example.
 */
@TeleOp(name = "Test DcMotor8863 Target Accuracy", group = "Test")
//@Disabled
public class TestDCMotor8863TargetAccuracy extends LinearOpMode {

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

    StatTrackerGB targetTracker;
    int targetDifference = 0;

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

        // If you want the motor to slowly ramp up to speed enable a power ramp. If you don't
        // just comment out this line. The motor may not actually go to the final power. These
        // parameters set the slope of a line that motor power cannot go above during the ramp up
        // time. Y axis = power, X axis = time
        motor.enablePowerRamp(initialPower, finalPower, rampTime);
        //**************************************************************

        targetTracker = new StatTrackerGB();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motor.");
        telemetry.update();
        waitForStart();

        runningTimer.reset();

        for (int i = 0; i < 10; i++) {

            // When the motor was initialized, the encoder was set to 0. Absolute movements are always
            // done relative to that 0 position. Think of these types of commands as:
            // GO TO A POSITION
            // The position is in terms of what the motor is attached to. A claw position, a drive train
            // position etc.
            // These are absolute movement commands:

            motor.enablePowerRamp(initialPower, finalPower, rampTime);
            // An absolute movement to 1440 degrees. Requires 4 revolutions to get there.
            motor.moveToPosition(powerToRunAt, 1440, DcMotor8863.FinishBehavior.HOLD); //works
            // You need to run this loop in order to be able to tell when the motor reaches the position
            // you told it to go to.
            while (opModeIsActive() && !motor.isMotorStateComplete()) {
                motor.update();
                // display some information on the driver phone
                telemetry.addData(">", "Absolute move to 1440 degrees, use power ramp");
                telemetry.addData("Motor Speed = ", "%5.2f", motor.getActualPower());
                telemetry.addData("feedback = ", "%5.2f", motor.getPositionInTermsOfAttachment());
                telemetry.addData("Encoder Count = ", "%5d", motor.getCurrentPosition());
                telemetry.addData("Elapsed time = ", "%5.0f", runningTimer.milliseconds());
                telemetry.addData(">", "Press Stop to end test.");
                telemetry.update();

                // You MUST call idle() in the loop so the other tasks the controller runs can be
                // performed
                idle();
            }

            targetTracker.updateStats(motor.getCurrentPosition());

            // wait for 2 second
            sleep(2000);

            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            runningTimer.reset();
        }

        telemetry.addData(">", "Movement tests complete");
        telemetry.addData("Average = ", "%5.1f", targetTracker.getAverage());
        telemetry.addData("Minimum = ", "%5.1f", targetTracker.getMinimum());
        telemetry.addData("Maximum = ", "%5.1f", targetTracker.getMaximum());
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
