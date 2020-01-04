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
@TeleOp(name = "Test DcMotor8863 Run Constant Speed", group = "Test")
//@Disabled
public class TestDCMotor8863RunConstantSpeed extends LinearOpMode {

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

    private DcMotor8863.MotorState motorState;

    public void runOpMode() {

        runningTimer = new ElapsedTime(0);

        //**************************************************************
        // You need the initializations inside this block

        // Instantiate and initialize motors
        motor = new DcMotor8863("testMotor", hardwareMap);
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
        motor.setupPowerRamp(initialPower, finalPower, rampTime);
        //**************************************************************

        // test internal routines from DcMotor8863
        //value = motor.getEncoderCountForDegrees(-400);
        //telemetry.addData("Encoder count for degrees = ", "%d", value);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motor.");
        telemetry.update();
        waitForStart();

        // Run the motor at a constant speed using encoder feedback. If there is a load on the motor the
        // power will be increased in an attempt to maintain the speed. Note that a command for a high
        // speed may require more power than is available to run the motor at that speed. So it may
        // result in the PID not being able to control the motor and the motor will lose speed under
        // load.
        // If you use the power ramp you MUST use the update() method in a loop
        motor.setupPowerRamp(initialPower, finalPower, rampTime);
        motor.startPowerRamp();
        motor.runAtConstantSpeed(powerToRunAt);
        runningTimer.reset();
        // You need to run this loop in order to use the power ramp.
        while (opModeIsActive() && !motor.isMotorStateComplete()) {
            motorState = motor.update();

            // you won't need the next section. I'm using it to stop the motor after 5 seconds for
            // my test. I doubt you will want to stop the motor based on time
            if (runningTimer.milliseconds() > 5000) {
                // Remove power the motor and let it spin freely
                // If you wanted it to stop and hold its position use stop()
                // See the next example for another way to do this
                motor.interrupt();
                // motor.stop();
                break;
            }

            // display some information on the driver phone
            telemetry.addData(">", "Run at constant speed, use power ramp");
            telemetry.addData("Motor State = ", motorState.toString() );
            telemetry.addData("Motor Speed = ", "%5.2f", motor.getCurrentPower());
            telemetry.addData("feedback = ", "%5.2f", motor.getPositionInTermsOfAttachment());
            telemetry.addData("Encoder Count = ", "%5d", motor.getCurrentPosition());
            telemetry.addData("Elapsed time = ", "%5.0f", runningTimer.milliseconds());
            telemetry.addData("Rotation complete = ", Boolean.toString(motor.isRotationComplete()));
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            // You MUST call idle() in the loop so the other tasks the controller runs can be
            // performed
            idle();
        }


        // wait for 2 second
        sleep(2000);
        runningTimer.reset();

        // Turn off motor, let it float and signal done;
        motor.setMotorToFloat();
        telemetry.addData(">", "Done");

        telemetry.update();

    }
}
