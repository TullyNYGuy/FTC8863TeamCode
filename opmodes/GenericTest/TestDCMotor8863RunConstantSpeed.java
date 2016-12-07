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
@TeleOp(name = "Test DcMotor8863", group = "Test")
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

        // When the motor was initialized, the encoder was set to 0. Absolute movements are always
        // done relative to that 0 position. Think of these types of commands as:
        // GO TO A POSITION
        // The position is in terms of what the motor is attached to. A claw position, a drive train
        // position etc.
        // These are absolute movement commands:

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

        // wait for 2 second
        sleep(2000);
        runningTimer.reset();

        // Since the motor is at 1440 degrees already, it should not move at all due to this
        // command. The loop should run once since the motor is complete already.
        motor.moveToPosition(powerToRunAt, 1440, DcMotor8863.FinishBehavior.HOLD); //works
        // You need to run this loop in order to be able to tell when the motor reaches the position
        // you told it to go to.
        while (opModeIsActive() && !motor.isMotorStateComplete()) {
            motor.update();
            // display some information on the driver phone
            telemetry.addData(">", "You should not see this - 1440 absolute");
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

        // wait for 2 second
        sleep(2000);
        runningTimer.reset();

        // Another way to move the motor is by a relative amount. It is like the current position is
        // set to 0 and then the motor moves relative to its current position. Think of these types
        // of commands as
        // CHANGE POSITION BY A CERTAIN AMOUNT
        // Most of the motor commands are relative commands.

        motor.setupPowerRamp(initialPower, finalPower, rampTime);
        // A relative movement to change the position of whatever is attached to the motor by a
        // certain amount. In this case rotor by 1440 degrees.
        motor.moveByAmount(powerToRunAt, 1440, DcMotor8863.FinishBehavior.HOLD); // works
        // You need to run this loop in order to be able to tell when the motor reaches the position
        // you told it to go to.
        while (opModeIsActive() && !motor.isMotorStateComplete()) {
            motor.update();
            // display some information on the driver phone
            telemetry.addData(">", "Changing position by 1440 degrees, use power ramp");
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

        // wait for 2 second
        sleep(2000);
        runningTimer.reset();

        // A relative movement by 1440 degrees again but this time FLOAT the motor at the end of the
        // movement and do not use the ramp
        motor.disablePowerRamp();
        motor.moveByAmount(powerToRunAt, 1440, DcMotor8863.FinishBehavior.FLOAT); // works
        // You need to run this loop in order to be able to tell when the motor reaches the position
        // you told it to go to.
        while (opModeIsActive() && !motor.isMotorStateComplete()) {
            motor.update();
            // display some information on the driver phone
            telemetry.addData(">", "Changing position by 1440 degrees, no power ramp");
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

        // wait for 2 second
        sleep(2000);
        runningTimer.reset();

        // Because there are so many situation where you want to move the motor by a certain number
        // of revolutions or a certain number of degrees, there are special methods to do that.

        // A relative movement. Move the motor by 1440 degrees. Hold at end. Power ramp enabled.
        motor.setupPowerRamp(initialPower, finalPower, rampTime);
        motor.rotateNumberOfDegrees(powerToRunAt, 1440, DcMotor8863.FinishBehavior.HOLD); // works
        // You need to run this loop in order to be able to tell when the motor reaches the position
        // you told it to go to.
        while (opModeIsActive() && !motor.isMotorStateComplete()) {
            motor.update();
            // display some information on the driver phone
            telemetry.addData(">", "Changing position by 1440 degrees, use power ramp");
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

        // wait for 2 second
        sleep(2000);
        runningTimer.reset();

        // A relative movement. Move the motor by 2.5 revolutions. Float at end. Power ramp disabled.
        motor.disablePowerRamp();
        motor.rotateNumberOfRevolutions(powerToRunAt, 2.5, DcMotor8863.FinishBehavior.FLOAT); // works
        // You need to run this loop in order to be able to tell when the motor reaches the position
        // you told it to go to.
        while (opModeIsActive() && !motor.isMotorStateComplete()) {
            motor.update();
            // display some information on the driver phone
            telemetry.addData(">", "Changing position by 2.5 revolutions, no power ramp");
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

        // wait for 2 second
        sleep(2000);
        runningTimer.reset();

        // I'm going to use a timer to stop the motor during my test.

        // Run the motor at a constant speed using encoder feedback. If there is a load on the motor the
        // power will be increased in an attempt to maintain the speed. Note that a command for a high
        // speed may require more power than is available to run the motor at that speed. So it may
        // result in the PID not being able to control the motor and the motor will lose speed under
        // load.
        // If you use the power ramp you MUST use the update() method in a loop
        motor.setupPowerRamp(initialPower, finalPower, rampTime);
        motor.startPowerRamp();
        motor.runAtConstantSpeed(powerToRunAt);
        // You need to run this loop in order to use the power ramp.
        while (opModeIsActive() && !motor.isMotorStateComplete()) {
            motor.update();

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


        // wait for 2 second
        sleep(2000);
        runningTimer.reset();

        // I'm going to use a timer to stop the motor during my test.

        // Run the motor at a constant speed using encoder feedback. If there is a load on the motor the
        // power will be increased in an attempt to maintain the speed. Note that a command for a high
        // speed may require more power than is available to run the motor at that speed. So it may
        // result in the PID not being able to control the motor and the motor will lose speed under
        // load.
        // If you use the power ramp you MUST use the update() method in a loop
        motor.setupPowerRamp(initialPower, finalPower, rampTime);
        // Set the motor to spin freely when power is removed
        motor.setAfterCompletionToFloat();
        motor.runAtConstantPower(powerToRunAt);
        // You need to run this loop in order to use the power ramp.
        while (opModeIsActive() && !motor.isMotorStateComplete()) {
            motor.update();

            // you won't need the next section. I'm using it to stop the motor after 5 seconds for
            // my test. I doubt you will want to stop the motor based on time
            if (runningTimer.milliseconds() > 5000) {
                // Remove power the motor. Whether it spins freely or holds position is determined
                // by what you setup before the movement. Use either:
                // motor.setAfterCompletionToFloat();
                // motor.setAfterCompletionToHold();
                motor.shutDown();
                break;
            }

            // display some information on the driver phone
            telemetry.addData(">", "Run at constant power, use power ramp");
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

        sleep(2000);
        runningTimer.reset();

        // Next I will demonstrate / test the ability of a motor to change its speed not is a sudden
        // change but by a gradual ramp of the power.

        // Start the motor running in constant power mode (no PID). Then perform a series of
        // gradual power changes.

        boolean powerRampRan1x = false;
        boolean powerRampRan2x = false;
        boolean powerRampRan3x = false;
        boolean powerRampRan4x = false;

        String mode;

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
