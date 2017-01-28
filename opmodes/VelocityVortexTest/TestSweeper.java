package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This OpMode runs 2 motors at a given power, one in the opposite direction from the other.
 * It is meant to be used to test a drive train
 * <p>
 * This code assumes a DC motor configured with the name "leftMotor"  and "rightMotor"
 */
@TeleOp(name = "Test Sweeper", group = "Test")
//@Disabled
public class TestSweeper extends LinearOpMode {

    // enums for a state machine for the sweeper motor
    private enum SweeperState {
        NORMAL, //power ramp is not active, power ramp has not just completed
        POWER_RAMP_RUNNING, // power ramp is currently running
        POWER_RAMP_COMPLETE // power ramp just completed
    }

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    SweeperState currentState = SweeperState.NORMAL;

    DriveTrain myDriveTrain;
    DcMotor8863 sweeperMotor;

    DriveTrain.Status statusDrive = DriveTrain.Status.COMPLETE;
    double leftPower = 0;
    double rightPower = 0;

    double sweeperPower = 0;
    double lastSweeperPower = 0;
    double sweeperPowerIncrement = .1;
    double desiredSweeperPower = 0;

    // for use in debouncing the button. A long press will only result in one transition of the
    // sweeperDirection
    boolean rightBumperIsReleased = true;
    boolean leftBumperIsReleased = true;
    boolean aButtonIsReleased = true;
    boolean bButtonIsReleased = true;
    boolean yButtonIsReleased = true;
    boolean xButtonIsReleased = true;

    // used to track the direction of the sweeper. It can be run forwards to collect balls
    // or backwards to shoot balls
    boolean sweeperDirectionForward = true;

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************

        // Instantiate and initialize motors

        myDriveTrain = DriveTrain.DriveTrainTeleOp(hardwareMap, telemetry);
        myDriveTrain.setCmPerRotation(31.1); // cm

        sweeperMotor = new DcMotor8863(RobotConfigMappingForGenericTest.getthirdMotorName(), hardwareMap);
        sweeperMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        sweeperMotor.setMovementPerRev(360);
        sweeperMotor.setTargetEncoderTolerance(5);
        sweeperMotor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        sweeperMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        sweeperMotor.setMinMotorPower(-1);
        sweeperMotor.setMaxMotorPower(1);

        sweeperMotor.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motors.");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user his play on the driver phone
        //*********************************************************************************************

        // set the mode for the motor
        sweeperMotor.runAtConstantSpeed(0);

        // Tank drive using gamepad joysticks
        while (opModeIsActive()) {

            //*************************************************************************************
            //  Process gamepad buttons - these control sweeper motor power and direction
            // ************************************************************************************

            //gamepad button configuration:
            //               Y = increase sweeper motor speed
            //  X = stop sweeper
            //               A = decrease sweeper motor speed
            //
            //   right bumper = toggle sweeper direction

            // if the right bumper is pressed, reverse the direction of the motor.
            // I have to check the status of the bumper. It is possible to hold it down for a long
            // time and if I do, then I don't want to flip sweeper direction back and forth.
            // So I make sure that the button has been released before doing anything to the
            // sweeper direction.
            if (gamepad1.right_bumper) {
                if (rightBumperIsReleased) {
                    // set so that we know the button has been pressed. The button has to be
                    // released before the sweeperDirection can be changed again
                    rightBumperIsReleased = false;
                    // The direction should be changed. What is it now? Change to the opposite
                    // direction by ramping power to the opposite sign power over a period of time
                    //sweeperMotor.setupAndStartPowerRamp(sweeperPower, -sweeperPower, 1000);
                    sweeperPower = -sweeperPower;
                }
            } else {
                // The bumper is not pressed anymore; it has been released. So when it is pressed
                // again the sweeper direction can be changed.
                rightBumperIsReleased = true;
            }

            // Use gamepad Y & A raise and lower  motor speed
            // Y = increase motor speed
            if (gamepad1.y) {
                if (yButtonIsReleased) {
                    // ignore a request to decrease power while a power ramp is running
                    if (!sweeperMotor.isPowerRampRunning()) {
                        sweeperPower = sweeperPower + sweeperPowerIncrement;
                    }
                    yButtonIsReleased = false;
                }
            } else {
                yButtonIsReleased = true;
            }

            // A = decrease motor speed
            if (gamepad1.a) {
                if (aButtonIsReleased) {
                    // ignore a request to decrease power while a power ramp is running
                    if (!sweeperMotor.isPowerRampRunning()) {
                        sweeperPower = sweeperPower - sweeperPowerIncrement;
                    }
                    aButtonIsReleased = false;
                }
            } else {
                aButtonIsReleased = true;
            }

            // x is used to stop the motor
            if (gamepad1.x) {
                if (xButtonIsReleased) {
                    sweeperMotor.interrupt();
                    sweeperPower = 0;
                    xButtonIsReleased = false;
                }
            } else {
                xButtonIsReleased = true;
            }

            // clip the sweeperPower. The user cannot increase power higher or lower than this
            sweeperPower = Range.clip(sweeperPower, -1.0, 1.0);

            // update the state machine for the sweeper motor
            update();

            //*************************************************************************************
            //  Process joysticks into tank drive
            // ************************************************************************************

            // process the joysticks for the tank drive
            leftPower = -gamepad1.left_stick_y;
            rightPower = -gamepad1.right_stick_y;
            // upddat the tank drive
            myDriveTrain.tankDrive(leftPower, rightPower);

//            statusDrive = myDriveTrain.update();
//            if (statusDrive == DriveTrain.Status.COMPLETE) {
//                break;
//            }

            // Display the current speeds
            telemetry.addData("Left Motor Speed = ", "%3.2f", leftPower);
            telemetry.addData("Right Motor Speed = ", "%3.2f", rightPower);
            telemetry.addData("Sweeper Motor Speed = ", "%3.2f", sweeperPower);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Turn off drivetrain and signal done;
        myDriveTrain.shutdown();
        sweeperMotor.shutDown();
        telemetry.addData(">", "Done");
        telemetry.update();

    }

    //*********************************************************************************************
    //             Helper methods
    //*********************************************************************************************

    /**
     * This method will only send a power to the motor controller if the power changes from the
     * last power that was sent. Not really needed to function but it cuts down on the traffic to
     * the I2C devices.
     *
     * @param power power to set the motor to
     */
    private void setPower(double power) {
        // only change the power if it has changed from the last power.
        // I.E. only set the power if it changes.
        if (power != lastSweeperPower) {
            lastSweeperPower = power;
            sweeperMotor.setPower(power);
        }
    }

    //*********************************************************************************************
    //     State machine - it was easier to implement the logic for when to update power
    //     in a state machine. At least for me.
    //*********************************************************************************************

    private void update() {

        // let the sweeper motor update itself
        sweeperMotor.update();

        // run the state macnine for the sweeper motor
        switch (currentState) {
            case NORMAL:
                // Set the power on the motor to whatever the user picked by using the buttons.
                // But Don't override the power ramp if it is running. If it is running it
                // updates the power itself.
                if (!sweeperMotor.isPowerRampRunning()) {
                    setPower(sweeperPower);
                } else {
                    // If there is a power ramp active then move to that state.
                    currentState = SweeperState.POWER_RAMP_RUNNING;
                }
                break;
            case POWER_RAMP_RUNNING:
                // In this state the sweeper motor has a power ramp running. Check to see if it has
                // completed yet. If so then the next state is POWER_RAMP_COMPLETE.
                // If not then the power ramp is taking care of setting the power so we don't need
                // to.
                if (!sweeperMotor.isPowerRampRunning()) {
                    currentState = SweeperState.POWER_RAMP_COMPLETE;
                }
                break;
            case POWER_RAMP_COMPLETE:
                // Get the last power applied to the motor at the end of the power ramp. Use this
                // as the power for the motor now that the power ramp has completed. As soon as this
                // is done move back to the normal state
                sweeperPower = sweeperMotor.getCurrentPower();
                setPower(sweeperPower);
                currentState = SweeperState.NORMAL;
                break;
        }
    }
}
