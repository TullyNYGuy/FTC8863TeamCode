package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

/**
 * This OpMode runs 2 motors at a given power, one in the opposite direction from the other.
 * It is meant to be used to test a double ball shooter
 * <p>
 * This code assumes a DC motor configured with the name "leftDriveMotor"  and "rightDriveMotor"
 */
@TeleOp(name = "Test 2 motors", group = "Test")
//@Disabled
public class TestTwoMotors extends LinearOpMode {

    DcMotor8863 leftDriveMotor;
    DcMotor8863 rightDriveMotor;
    double leftSpeedToRunAt = 1.0; // stgart up at this speed
    double rightSpeedToRunAt = 1.0; // start up at this speed
    double speedIncrement = .1;

    // multiply by this factor. If positive the motor speed will increase. If negative the motor
    // speed will decrease. Increase or decrease is controlled by the right bumper on the gamepad.
    double speedIncrementDirection = 1.0;

    // for use in debouncing the button. A long press will only result in one transition of the
    // speedIncrementDirection
    boolean rightBumperIsReleased = true;

    boolean aButtonIsReleased = true;
    boolean bButtonIsReleased = true;
    boolean yButtonIsReleased = true;
    boolean xButtonIsReleased = true;


    @Override
    public void runOpMode() {


        // Instantiate and initialize motors
        leftDriveMotor = new DcMotor8863("leftDriveMotor", hardwareMap);
        leftDriveMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        leftDriveMotor.setMovementPerRev(360);
        leftDriveMotor.setTargetEncoderTolerance(5);
        leftDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        leftDriveMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        leftDriveMotor.setMinMotorPower(-1);
        leftDriveMotor.setMaxMotorPower(1);

        leftDriveMotor.setDirection(DcMotor.Direction.FORWARD);

        rightDriveMotor = new DcMotor8863("rightDriveMotor", hardwareMap);
        rightDriveMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        rightDriveMotor.setTargetEncoderTolerance(5);
        rightDriveMotor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        rightDriveMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        rightDriveMotor.setMinMotorPower(-1);
        rightDriveMotor.setMaxMotorPower(1);

        rightDriveMotor.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motors.");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            //gamepad button configuration:
            //      Y
            //  X      B
            //      A

            // if the right bumper is pressed, reverse the direction of the motor speed increase
            // or decrease. Example. Suppose that X and B presses are increasing the speed of the
            // motors. Press the right bumper. Now presses of the X or B buttons will decrease the
            // speed of the motors. Press the right bumper again and the X or B buttons will now
            // increase the speed.
            // I have to check the status of the bumper. It is possible to hold it down for a long
            // time and if I do, then I don't want to flip speedIncrementDirection back and forth.
            // So I make sure that the button has been released before doing anything to the
            // speedIncrementDirection.
            if (gamepad1.right_bumper) {
                if (rightBumperIsReleased) {
                    // set so that we know the button has been pressed. The button has to be
                    // released before the speedIncrementDirection can be changed again
                    rightBumperIsReleased = false;
                    if (speedIncrementDirection == -1.0) {
                        speedIncrementDirection = +1.0;
                    } else {
                        speedIncrementDirection = -1.0;
                    }
                }
            } else {
                // The bumper is not pressed anymore; it has been released. So when it is pressed
                // again the speedIncrementDirection can be changed.
                rightBumperIsReleased = true;
            }

            // Use gamepad Y & A raise and lower both motor speeds
            // Y = increase both motor speeds at the same time
            if (gamepad1.y) {
                if (yButtonIsReleased) {
                    leftSpeedToRunAt = leftSpeedToRunAt + speedIncrement;
                    rightSpeedToRunAt = rightSpeedToRunAt + speedIncrement;
                    yButtonIsReleased = false;
                }
            } else {
                yButtonIsReleased = true;
            }
            // A = decrease both motor speeds at the same time
            if (gamepad1.a) {
                if (aButtonIsReleased) {
                    leftSpeedToRunAt = leftSpeedToRunAt - speedIncrement;
                    rightSpeedToRunAt = rightSpeedToRunAt - speedIncrement;
                    aButtonIsReleased = false;
                }
            } else {
                aButtonIsReleased = true;
            }

            // Use gamepad X & B to raise or lower each side's motor speed individually
            // X controls the left motor
            if (gamepad1.x) {
                if (xButtonIsReleased) {
                    rightSpeedToRunAt = rightSpeedToRunAt + speedIncrement * speedIncrementDirection;
                    xButtonIsReleased = false;
                }
            } else {
                xButtonIsReleased = true;
            }

            // B controls the right motor
            if (gamepad1.b) {
                if (bButtonIsReleased) {
                    leftSpeedToRunAt = leftSpeedToRunAt + speedIncrement * speedIncrementDirection;
                    bButtonIsReleased = false;
                }
            } else {
                bButtonIsReleased = true;
            }

            // clip the speeds to 0 for min and 1 for max
            leftSpeedToRunAt = Range.clip(leftSpeedToRunAt, 0, 1);
            rightSpeedToRunAt = Range.clip(rightSpeedToRunAt, 0, 1);

            // apply the new speeds to the motors
            leftDriveMotor.runAtConstantSpeed(leftSpeedToRunAt);
            rightDriveMotor.runAtConstantSpeed(rightSpeedToRunAt);

            // Display the current speeds
            telemetry.addData("Left Motor Speed = ", "%3.2f", leftSpeedToRunAt);
            telemetry.addData("Right Motor Speed = ", "%3.2f", rightSpeedToRunAt);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Turn off motor and signal done;
        rightDriveMotor.setMotorToFloat();
        leftDriveMotor.setMotorToFloat();
        telemetry.addData(">", "Done");

        telemetry.update();

    }
}
