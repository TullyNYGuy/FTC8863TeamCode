package org.firstinspires.ftc.teamcode.opmodes.RoverRuckus;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.StatTracker;

@TeleOp(name = "Rover Ruckus Autonomous", group = "Test")
//@Disabled
public class Autonomous extends LinearOpMode {

    // Put your variable declarations here
    AdafruitIMU8863 imu;
    double heading = 0;
    double pitch = 0;
    double roll = 0;
    double headingOnGround;
    DriveTrain driveTrain;
    DriveTrain.Status statusDrive;
    DataLogging logFile;
    double headingWhileHanging;


    Orientation angles;
    Acceleration gravity;
    BNO055IMU.SystemStatus systemStatus;
    boolean isConnected;

    boolean yButtonIsReleased = true;
    boolean xButtonIsReleased = true;
    boolean aButtonIsReleased = true;
    boolean bButtonIsReleased = true;

    StatTracker loopTimeTracker;
    ElapsedTime loopTimer;

    @Override
    public void runOpMode() {

        // Put your initializations here
        imu = new AdafruitIMU8863(hardwareMap);
        isConnected = imu.isIMUConnected();
        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
        driveTrain.setCmPerRotation(31.9); // cm
        logFile = new DataLogging("Autonomous", telemetry);

        // 12/10/2017 for some reason this line is causing the robot controller app to crash
        //systemStatus = imu.getSystemStatus();

        loopTimeTracker = new StatTracker();
        loopTimer = new ElapsedTime();

        // Wait for the start button

        //       telemetry.addData("IMU status = ", String.valueOf(systemStatus));
        if (isConnected) {
            telemetry.addData("IMU is connected", "!");
        } else {
            telemetry.addData("IMU is NOT connected.", " Check the wiring");
        }
        imu.resetAngleReferences();
        headingWhileHanging = imu.getHeading();
        telemetry.addData("Heading while hanging is", headingWhileHanging);

        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        logFile.startTimer();
        // Start the logging of measured acceleration
        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

        loopTimer.reset();

        telemetry.addData("twist the robot", "hey hey get to doin em boy");
        telemetry.update();
        sleep(6000);

        headingOnGround = imu.getHeading();

        telemetry.addData("Heading on ground is ", headingOnGround);
        telemetry.update();
        // driveToCraterFromLander();

        logFile.logData("headingWhileHanging " + Double.toString(headingWhileHanging));
        logFile.logData("headingOnGround " + Double.toString(headingOnGround));

        turnByDegrees(-headingOnGround, .29);
        logFile.logData("headingFirstTurn " + Double.toString(imu.getHeading()));
        driveStraight(10, .3);
        logFile.logData("headingFirstStraight " + Double.toString(imu.getHeading()));
        turnByDegrees(-67, .3);
        logFile.logData("headingSecondTurn " + Double.toString(imu.getHeading()));
        driveStraight(108, .3);
        logFile.logData("headingSecondStraight " + Double.toString(imu.getHeading()));
        turnByDegrees(-61.38, .3);
        logFile.logData("headingThirdTurn " + Double.toString(imu.getHeading()));
        driveStraight(95, .3);
        sleep(2000);
        logFile.logData("headingThirdStraight " + Double.toString(imu.getHeading()));
        driveStraight(-115, .3);
        logFile.logData("headingFourthTurn " + Double.toString(imu.getHeading()));

        // while (opModeIsActive()) {

        //if(systemStatus != BNO055IMU.SystemStatus.UNKNOWN) {
//            if (isConnected) {
//                loopTimeTracker.compareValue(loopTimer.milliseconds());
//                loopTimer.reset();
//
//                // Y BUTTON IS RELATIVE ANGLES, RELATIVE TO THE LAST TIME THE REFERENCE WAS RESET AND
//                // THEN NORMALIZED TO -180 (RIGHT TURN) TO +180 (LEFT TURN)
//                if (gamepad1.y) {
//                    if (yButtonIsReleased) {
//                        imu.setAngleMode(AdafruitIMU8863.AngleMode.RELATIVE);
//                        yButtonIsReleased = false;
//                    }
//                } else {
//                    yButtonIsReleased = true;
//                }
//
//                // B BUTTON IS ABSOLUTE ANGLES READ FROM IMU, RELATIVE TO THE POSITION OF THE IMU
//                // WHEN IT WAS INITIALIZED, AND THEN NORMALIZED TO -180 (RIGHT TURN) TO +180 (LEFT TURN)
//                if (gamepad1.b) {
//                    if (bButtonIsReleased) {
//                        imu.setAngleMode(AdafruitIMU8863.AngleMode.RELATIVE);
//                        headingOnGround = imu.getHeading();
//                        bButtonIsReleased = false;
//                    }
//                } else {
//                    bButtonIsReleased = true;
//                }
//
//                // A BUTTON IS RAW ANGLES AS READ FROM THE IMU
//                if (gamepad1.a) {
//                    if (aButtonIsReleased) {
//                        imu.setAngleMode(AdafruitIMU8863.AngleMode.RAW);
//                        aButtonIsReleased = false;
//                    }
//                } else {
//                    aButtonIsReleased = true;
//                }
//
//                // X button resets the reference angles
//                if (gamepad1.x) {
//                    if (xButtonIsReleased) {
//                        // toggle the mode
//                        imu.resetAngleReferences();
//                        xButtonIsReleased = false;
//                    }
//                } else {
//                    xButtonIsReleased = true;
//                }

           /*     // Put your calls that need to run in a loop here
                heading = imu.getHeading();
                pitch = imu.getPitch();
                roll = imu.getRoll();

                // Display the current value
                telemetry.addData("IMU mode = ", imu.getAngleMode().toString());
                telemetry.addData("Heading = ", "%5.2f", heading);
                telemetry.addData("Pitch = ", "%5.2f", pitch);
                telemetry.addData("Roll = ", "%5.2f", roll);
                telemetry.addData("Min loop time (mS) = ", "%3.3f", loopTimeTracker.getMinimum());
                telemetry.addData("Max loop time (mS) = ", "%3.3f", loopTimeTracker.getMaximum());
                telemetry.addData("Ave loop time (mS) = ", "%3.3f", loopTimeTracker.getAverage());
                telemetry.addData(">", "Press Stop to end test.");
            } else {
                telemetry.addData("IMU is not connected! ", "Check wiring!");
            }

            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();*/

    }

    public void driveToCraterFromLander() {
        turnByDegrees(-headingOnGround, .29);
        driveStraight(10, 0.3);
        turnByDegrees(55.5, .3);
        driveStraight(107, 0.3);
        turnByDegrees(-90, 0.3);
        driveStraight(35, 0.3);
    }


    public void driveStraight(double distance, double power) {
        driveTrain.setupDriveDistance(power, distance, DcMotor8863.FinishBehavior.FLOAT);

        while (opModeIsActive()) {
            statusDrive = driveTrain.updateDriveDistance();
            if (statusDrive == DriveTrain.Status.COMPLETE) {
                break;
            }
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.addData("Status = ", statusDrive.toString());
            telemetry.update();
            idle();
        }
        telemetry.addData(">", "Press Stop to end test.");
        telemetry.addData("Status = ", statusDrive.toString());
        telemetry.update();
    }

    public void turnByDegrees(double angle, double power) {
        driveTrain.setupTurn(angle, power, AdafruitIMU8863.AngleMode.RELATIVE);

        while (opModeIsActive() && !driveTrain.updateTurn()) {
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.addData("Angle = ", "%3.1f", driveTrain.imu.getHeading());
            telemetry.update();
            idle();
        }
        telemetry.addData("Finished Turn", "0");
        telemetry.update();
    }

}

