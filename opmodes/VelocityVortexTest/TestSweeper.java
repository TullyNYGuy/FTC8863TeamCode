package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.opmodes.GenericTest.RobotConfigMappingForGenericTest;

/**
 * This OpMode runs 2 motors at a given power, one in the opposite direction from the other. 
 * It is meant to be used to test a drive train
 *
 * This code assumes a DC motor configured with the name "leftMotor"  and "rightMotor"
 *
 */
@Autonomous(name = "Sweeper Test", group = "Test")
//@Disabled
public class TestSweeper extends LinearOpMode {

    DriveTrain myDriveTrain;
    DcMotor8863 sweeperMotor;

    DriveTrain.Status statusDrive = DriveTrain.Status.COMPLETE;
    double leftPower = 0;
    double rightPower =0;

    double sweeperPower = 0;
    double sweeperPowerIncrement = .1;
    double desiredSweeperPower = 0;

    // for use in debouncing the button. A long press will only result in one transition of the
    // sweeperDirection
    boolean rightBumperIsReleased = true;
    boolean leftBumperIsReleased = true;

    boolean sweeperDirectionForward = true;


    boolean aButtonIsReleased = true;
    boolean bButtonIsReleased = true;
    boolean yButtonIsReleased = true;
    boolean xButtonIsReleased = true;
    
    
    @Override
    public void runOpMode() {


        // Instantiate and initialize motors
        // myDriveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap);
        myDriveTrain.setCmPerRotation(31.1); // cm
        myDriveTrain = DriveTrain.DriveTrainTeleOp(hardwareMap);
        
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
        telemetry.addData(">", "Press Start to run Motors." );
        telemetry.update();
        waitForStart();
        
        // Tank drive using gamepad joysticks
        while(opModeIsActive()) {

            //gamepad button configuration:
            //               Y = increase sweeper motor speed
            //  X = increase or decrease left motor speed
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
                    sweeperMotor.setupAndStartPowerRamp(sweeperPower, -sweeperPower, 1000);
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
                    if(!sweeperMotor.isPowerRampEnabled()){
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
                    if(!sweeperMotor.isPowerRampEnabled()) {
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
                    xButtonIsReleased = false;
                }
            } else {
                xButtonIsReleased = true;
            }

            // clip the sweeperPower
            sweeperPower = Range.clip(sweeperPower, 1.0, -1.0);

            // update the sweeper motor
            sweeperMotor.update();
            
            // process the joysticks for the tank drive
            leftPower = -gamepad1.left_stick_y;
            rightPower = -gamepad1.right_stick_y;
            // upddat the tank drive
            myDriveTrain.tankDrive(leftPower,rightPower);

            statusDrive = myDriveTrain.update();
            if (statusDrive == DriveTrain.Status.COMPLETE) {
                break;
            }
            idle();
        }

        // Turn off drivetrain and signal done;
        myDriveTrain.shutdown();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
