package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.VelocityVortexShooter;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This OpMode runs 2 motors at a given power, one in the opposite direction from the other.
 * It is meant to be used to test a drive train
 * <p>
 * This code assumes a DC motor configured with the name "leftMotor"  and "rightMotor"
 */
@TeleOp(name = "Test Shooter", group = "Test")
//@Disabled
public class TestShooter extends LinearOpMode {

    // enums for a state machine for the shooter motor

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    VelocityVortexShooter shooterMotor;

    double shooterPower = 0;
    double lastShooterPower = 0;
    double shooterPowerIncrement = .05;
    double desiredShooterPower = 0;

    // for use in debouncing the button. A long press will only result in one transition of the
    // shooterDirection
    boolean rightBumperIsReleased = true;
    boolean leftBumperIsReleased = true;
    boolean aButtonIsReleased = true;
    boolean bButtonIsReleased = true;
    boolean yButtonIsReleased = true;
    boolean xButtonIsReleased = true;

    // used to track the direction of the shooter. It can be run forwards to collect balls
    // or backwards to shoot balls
    boolean shooterDirectionForward = true;

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************
        shooterMotor = new VelocityVortexShooter(hardwareMap);

        // set the mode for the motor
        shooterMotor.init();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motors.");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user his play on the driver phone
        //*********************************************************************************************

        // Tank drive using gamepad joysticks
        while (opModeIsActive()) {

            //*************************************************************************************
            //  Process gamepad buttons - these control shooter motor power and direction
            // ************************************************************************************

            //gamepad button configuration:
            //               Y = increase shooter motor speed
            //  X = stop shooter
            //               A = decrease shooter motor speed
            //
            //   right bumper = toggle shooter direction

            // if the right bumper is pressed, reverse the direction of the motor.
            // I have to check the status of the bumper. It is possible to hold it down for a long
            // time and if I do, then I don't want to flip shooter direction back and forth.
            // So I make sure that the button has been released before doing anything to the
            // shooter direction.
            if (gamepad1.right_bumper) {
                if (rightBumperIsReleased) {
                    // set so that we know the button has been pressed. The button has to be
                    // released before the shooterDirection can be changed again
                    rightBumperIsReleased = false;
                    // The direction should be changed. What is it now? Change to the opposite
                    // direction by ramping power to the opposite sign power over a period of time
                    //shooterMotor.setupAndStartPowerRamp(shooterPower, -shooterPower, 1000);
                    shooterPower = -shooterPower;
                }
            } else {
                // The bumper is not pressed anymore; it has been released. So when it is pressed
                // again the shooter direction can be changed.
                rightBumperIsReleased = true;
            }

            // Use gamepad Y & A raise and lower  motor speed
            // Y = increase motor speed
            if (gamepad1.y) {
                if (yButtonIsReleased) {
                    // ignore a request to decrease power while a power ramp is running
                        shooterPower = shooterPower + shooterPowerIncrement;
                    yButtonIsReleased = false;
                }
            } else {
                yButtonIsReleased = true;
            }

            // A = decrease motor speed
            if (gamepad1.a) {
                if (aButtonIsReleased) {
                    // ignore a request to decrease power while a power ramp is running
                        shooterPower = shooterPower - shooterPowerIncrement;
                    aButtonIsReleased = false;
                }
            } else {
                aButtonIsReleased = true;
            }

            // x is used to stop the motor
            if (gamepad1.x) {
                if (xButtonIsReleased) {
                    shooterMotor.shutdown();
                    shooterPower = 0;
                    xButtonIsReleased = false;
                }
            } else {
                xButtonIsReleased = true;
            }

            // clip the shooterPower. The user cannot increase power higher or lower than this
            shooterPower = Range.clip(shooterPower, -1.0, 1.0);
            shooterMotor.setPower(shooterPower);

            // update the state machine for the shooter motor

            //*************************************************************************************
            //  Process joysticks into tank drive
            // ************************************************************************************

            // Display the current speeds
            telemetry.addData("Shooter Motor Speed = ", "%3.2f", shooterPower);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Turn off drivetrain and signal done;
        shooterMotor.stop();
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
     * @param power power to set the motor to
     */
    private void setPower(double power) {
        // only change the power if it has changed from the last power.
        // I.E. only set the power if it changes.
        if (power != lastShooterPower) {
            lastShooterPower = power;
            shooterMotor.setPower(power);
        }
    }
}

    //*********************************************************************************************
    //     State machine - it was easier to implement the logic for when to update power
    //     in a state machine. At least for me.
    //*********************************************************************************************
