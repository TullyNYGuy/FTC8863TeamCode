package org.firstinspires.ftc.teamcode.opmodes.RoverRuckus;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.StatTracker;

@Autonomous(name = "Rover Ruckus Autonomous", group = "Test")
//@Disabled
public class RoverRuckusAutonomous extends LinearOpMode {

    // Put your variable declarations here
    AdafruitIMU8863 imu;
    double heading = 0;
    double pitch = 0;
    double roll = 0;
    double headingOnGround;
    //DriveTrain driveTrain;
    DriveTrain.Status statusDrive;
    DataLogging logFile;
    double headingWhileHanging;
    RoverRuckusRobot robot;

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
//        imu = new AdafruitIMU8863(hardwareMap);
//        isConnected = imu.isIMUConnected();
//        driveTrain = DriveTrain.DriveTrainAutonomous(hardwareMap, telemetry);
//        driveTrain.setCmPerRotation(31.9); // cm
        logFile = new DataLogging("Autonomous", telemetry);
        robot = RoverRuckusRobot.createRobotForAutonomous(hardwareMap, telemetry, AllianceColor.TeamColor.RED, logFile);

        // 12/10/2017 for some reason this line is causing the robot controller app to crash
        //systemStatus = imu.getSystemStatus();

        loopTimeTracker = new StatTracker();
        loopTimer = new ElapsedTime();

        // Wait for the start button

        //       telemetry.addData("IMU status = ", String.valueOf(systemStatus));
//        if (isConnected) {
//            telemetry.addData("IMU is connected", "!");
//        } else {
//            telemetry.addData("IMU is NOT connected.", " Check the wiring");
//        }
        robot.driveTrain.imu.resetAngleReferences();
//        headingWhileHanging = robot.driveTrain.imu.getHeading();
//        telemetry.addData("Heading while hanging is", headingWhileHanging);

        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        logFile.startTimer();
        // Start the logging of measured acceleration
        robot.driveTrain.imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

        loopTimer.reset();

        //dehang the robot
        robot.dehang();

        driveToDepotDumpThenCrater();
        // driveToCraterFromLander();

        logFile.logData("headingWhileHanging " + Double.toString(headingWhileHanging));
        logFile.logData("headingOnGround " + Double.toString(headingOnGround));


//         while (opModeIsActive()) {
//
//                // Put your calls that need to run in a loop here
//                heading = imu.getHeading();
//                pitch = imu.getPitch();
//                roll = imu.getRoll();
//
//                // Display the current value
////                telemetry.addData("IMU mode = ", imu.getAngleMode().toString());
////                telemetry.addData("Heading = ", "%5.2f", heading);
////                telemetry.addData("Pitch = ", "%5.2f", pitch);
////                telemetry.addData("Roll = ", "%5.2f", roll);
////                telemetry.addData("Min loop time (mS) = ", "%3.3f", loopTimeTracker.getMinimum());
////                telemetry.addData("Max loop time (mS) = ", "%3.3f", loopTimeTracker.getMaximum());
////                telemetry.addData("Ave loop time (mS) = ", "%3.3f", loopTimeTracker.getAverage());
////                telemetry.addData(">", "Press Stop to end test.");
//
//
//            telemetry.update();
//
//            idle();
//        }

        // Put your cleanup code here - it runs as the application shuts down
        robot.shutdown();
        telemetry.addData(">", "Done");
        telemetry.update();

    }

    public void driveToDepotDumpThenCrater() {

        double headingAfterDrive = 0;
        double headingForTurn = 0;
        double compensatedHeading = 0;
        double desiredHeading = 0;

        driveStraight(5, .3);
        headingOnGround = robot.driveTrain.imu.getHeading();

        logFile.logData("headingFirstStraight " + Double.toString( robot.driveTrain.imu.getHeading()));
        //turnByDegrees(-headingOnGround, .3);
        //logFile.logData("headingFirstTurn " + Double.toString( robot.driveTrain.imu.getHeading()));
        headingForTurn = 67-headingOnGround;
        desiredHeading = 67;
        turnByDegrees(headingForTurn, .3);

        // head toward wall
        logFile.logData("headingSecondTurn " + Double.toString( robot.driveTrain.imu.getHeading()));
        headingAfterDrive = driveStraight(108, .3);
        compensatedHeading =headingAfterDrive- desiredHeading;
        logFile.logData("headingSecondStraight " + Double.toString( robot.driveTrain.imu.getHeading()));

        // turn toward crater
        //turnByDegrees(-61.38, .3);
        headingForTurn = -118.6;
        turnByDegrees(headingForTurn-compensatedHeading, .3);
        logFile.logData("headingThirdTurn " + Double.toString( robot.driveTrain.imu.getHeading()));

        // drive to depot backwards
        headingAfterDrive = driveStraight(-95, .3);


        //dump the marker
        robot.deliveryLiftSystem.deliveryBoxToDump();
        logFile.logData("headingThirdStraight " + Double.toString( robot.driveTrain.imu.getHeading()));
        sleep(1000);
        // drive to the crater
        headingAfterDrive = driveStraight(120, .3);
        logFile.logData("headingFourthTurn " + Double.toString( robot.driveTrain.imu.getHeading()));

        //lower the arm
        //robot.collectorArm.goToPark();
        //sleep(1000);
        //robot.collectorArm.floatArm();
        headingForTurn = 180.0;
        turnByDegrees(headingForTurn, .3);
        logFile.logData("headingFifthTurn " + Double.toString( robot.driveTrain.imu.getHeading()));
//
//        headingForTurn = 20;
//        turnByDegrees(headingForTurn, .3);
//        logFile.logData("headingSixthTurn " + Double.toString(robot.driveTrain.imu.getHeading()));
//        driveStraight(-35, .3);

    }


    public void driveToCraterFromLander() {
        turnByDegrees(-headingOnGround, .29);
        driveStraight(10, 0.3);
        turnByDegrees(55.5, .3);
        driveStraight(107, 0.3);
        turnByDegrees(-90, 0.3);
        driveStraight(35, 0.3);
    }


    public double driveStraight(double distance, double power) {
        robot.driveTrain.setupDriveDistance(power, distance, DcMotor8863.FinishBehavior.FLOAT);

        while (opModeIsActive()) {
            statusDrive = robot.driveTrain.updateDriveDistance();
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
        sleep(1000);
        return robot.driveTrain.imu.getHeading();
    }

    public void turnByDegrees(double angle, double power) {
        robot.driveTrain.setupTurn(angle, power, AdafruitIMU8863.AngleMode.RELATIVE);

        while (opModeIsActive() && !robot.driveTrain.updateTurn()) {
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
            telemetry.update();
            idle();
        }
    }

}

