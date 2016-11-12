package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This OpMode runs 2 motors at a given power, one in the opposite direction from the other.
 * It is meant to be used to test a double ball shooter
 * <p>
 * This code assumes a DC motor configured with the name "leftShooterMotor"  and "rightShooterMotor"
 */
@TeleOp(name = "Test 2 motor shooter", group = "Test")
//@Disabled
public class TestTwoMotorShooter extends LinearOpMode {

    DcMotor8863 leftShooterMotor;
    DcMotor8863 rightShooterMotor;
    //DcMotor8863 conveyorMotor;
    
    double leftSpeedToRunAt = 1.0; // stgart up at this speed
    double rightSpeedToRunAt = 1.0; // start up at this speed
    double speedIncrement = .1;
    //double conveyorSpeed = .5;

    // multiply by this factor. If positive the motor speed will increase. If negative the motor
    // speed will decrease. Increase or decrease is controlled by the right bumper on the gamepad.
    double speedIncrementDirection = -1.0;

    // for use in debouncing the button. A long press will only result in one transition of the
    // speedIncrementDirection
    boolean rightBumperIsReleased = true;
    boolean leftBumperIsReleased = true;
    
    boolean aButtonIsReleased = true;
    boolean bButtonIsReleased = true;
    boolean yButtonIsReleased = true;
    boolean xButtonIsReleased = true;


    @Override
    public void runOpMode() {


        // Instantiate and initialize motors
        leftShooterMotor = new DcMotor8863(RobotConfigMappingForGenericTest.getleftMotorName(), hardwareMap);
        leftShooterMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_20);
        leftShooterMotor.setMovementPerRev(360);
        leftShooterMotor.setTargetEncoderTolerance(5);
        leftShooterMotor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        leftShooterMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        leftShooterMotor.setMinMotorPower(-1);
        leftShooterMotor.setMaxMotorPower(1);

        leftShooterMotor.setDirection(DcMotor.Direction.FORWARD);

        rightShooterMotor = new DcMotor8863(RobotConfigMappingForGenericTest.getrightMotorName(), hardwareMap);
        rightShooterMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_20);
        rightShooterMotor.setMovementPerRev(360);
        rightShooterMotor.setTargetEncoderTolerance(5);
        rightShooterMotor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
        rightShooterMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
        rightShooterMotor.setMinMotorPower(-1);
        rightShooterMotor.setMaxMotorPower(1);

        rightShooterMotor.setDirection(DcMotor.Direction.REVERSE);

//        conveyorMotor = new DcMotor8863("conveyorMotor", hardwareMap);
//        conveyorMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
//        conveyorMotor.setMovementPerRev(360);
//        conveyorMotor.setTargetEncoderTolerance(5);
//        conveyorMotor.setFinishBehavior(DcMotor8863.FinishBehavior.FLOAT);
//        conveyorMotor.setMotorMoveType(DcMotor8863.MotorMoveType.RELATIVE);
//        conveyorMotor.setMinMotorPower(-1);
//        conveyorMotor.setMaxMotorPower(1);
//
//        conveyorMotor.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run Motors.");
        telemetry.update();
        waitForStart();

        leftShooterMotor.runAtConstantSpeed(leftSpeedToRunAt);
        rightShooterMotor.runAtConstantSpeed(rightSpeedToRunAt);
//        conveyorMotor.runAtConstantSpeed(0);

        while (opModeIsActive()) {
            //gamepad button configuration:
            //                                     Y = increase both motor speeds
            //
            //  X = increase or decrease left motor speed     B = increase or decrease right motor speed
            //
            //                                     A = decrease both motor speeds
            
            // left bumper = toggle conveyor       right bumper = toggle X/B direction

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

            // left bumper controls the conveyor. When pressed the conveyor moves. When not pressed
            // the conveyor does not move.
            if (gamepad1.left_bumper) {
                if (leftBumperIsReleased) {
                    // set so that we know the button has been pressed.
                    leftBumperIsReleased = false;
                    // turn on the conveyor motor
//                    conveyorMotor.setPower(conveyorSpeed);
                }
            } else {
                // The bumper is not pressed anymore; it has been released. Stop the conveyor motor.
                leftBumperIsReleased = true;
//                conveyorMotor.setPower(0);
            }

            // clip the speeds to 0 for min and 1 for max
            leftSpeedToRunAt = Range.clip(leftSpeedToRunAt, 0, 1);
            rightSpeedToRunAt = Range.clip(rightSpeedToRunAt, 0, 1);

            // apply the new speeds to the motors
            leftShooterMotor.setPower(leftSpeedToRunAt);
            rightShooterMotor.setPower(rightSpeedToRunAt);

            // Display the current speeds
            telemetry.addData("Left Motor Speed = ", "%3.2f", leftSpeedToRunAt);
            telemetry.addData("Right Motor Speed = ", "%3.2f", rightSpeedToRunAt);
            telemetry.addData("Left Motor Encoder = ", "%d", leftShooterMotor.getCurrentPosition());
            telemetry.addData("Right Motor Encoder = ", "%d", rightShooterMotor.getCurrentPosition());
            telemetry.addData("Motor Difference = ", "%d", leftShooterMotor.getCurrentPosition() - rightShooterMotor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Turn off motor and signal done;
        rightShooterMotor.setMotorToFloat();
        leftShooterMotor.setMotorToFloat();
//        conveyorMotor.setMotorToFloat();
        telemetry.addData(">", "Done");

        telemetry.update();

    }
}
