package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.RelicRecoveryRobotStJohnFisher;

/**
 * Autonomous for competition
 */
@Autonomous(name = "Relic Recovery Autonomous Blue 01", group = "Run")
//@Disabled
public class RelicRecoveryAutonomousBLUE01 extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    RelicRecoveryRobotStJohnFisher robot;
    DriveTrain.Status statusDrive;
    ElapsedTime timer;
    int columnNumber = 0;

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************

        robot = robot.createRobotForAutonomous(hardwareMap, telemetry);
        timer = new ElapsedTime();

        // Wait for the start button
        robot.allianceColorSwitch.displayAllianceSwitch(telemetry);
        telemetry.addData(">", "Press start to run Autonomous");
        telemetry.update();
        waitForStart();
        timer.reset();

        //*********************************************************************************************
        //             Robot Running after the user his play on the driver phone
        //*********************************************************************************************
            //jewel arm knocks proper ball off
            //read pictograph
            //determine 1, 2, or 3
            
            //move
            if(columnNumber == 1){
                column1BLUE02();
            }
            else if(columnNumber == 2){
                column2BLUE02();
            }
            else if(columnNumber == 3){
                column3BLUE02();
            }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything
        robot.shutdown();
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    //*********************************************************************************************
    //             Helper methods
    //*********************************************************************************************
    public void column1BLUE02(){
        //drive straight
        driveStraight(53.34, 0.5);
        sleep(1000);
        //turn
        spinTurn(45, 0.5, AdafruitIMU8863.AngleMode.ABSOLUTE);
        sleep(1000);
        //drive straight
        driveStraight(39.37, 0.5);
        sleep(1000);
    }

    public void column2BLUE02() {
        //drive straight
        driveStraight(73.66, 0.5);
        sleep(1000);
        //turn
        spinTurn(45, 0.5, AdafruitIMU8863.AngleMode.ABSOLUTE);
        sleep(1000);
        //drive straight
        driveStraight(39.37, 0.5);
        sleep(1000);
    }
    public void column3BLUE02(){
        driveStraight(73.66,0.5);
        sleep(1000);
        //turn
        spinTurn(45, 0.5, AdafruitIMU8863.AngleMode.ABSOLUTE);
        sleep(1000);
        //drive straight
        driveStraight(39.37, 0.5);
        sleep(1000);
    }

    /**
     * Does a spin turn about the center of the robot. Uses the IMU and a PI control to make it
     * work.
     *
     * @param angle
     * @param power
     * @param angleMode RELATIVE - 0 degrees is where the robot is now or ABSOLUTE - 0 is where
     *                  the robot started up
     */
    public void spinTurn(double angle, double power, AdafruitIMU8863.AngleMode angleMode) {
        robot.driveTrain.setupTurn(angle, power, angleMode);

        while (opModeIsActive() && !robot.driveTrain.updateTurn()) {
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
            telemetry.update();
            idle();
        }
        robot.driveTrain.stopTurn();
        telemetry.addData("Turn Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.update();
    }


    /**
     * Drive at a heading for a certain distance. The IMU is read for feedback to PID control for
     * controlling the heading. The start of the drive has a ramp up for the power to the motors
     * to help with wheel slip.
     *
     * @param heading
     * @param power
     * @param distance
     */
    public void driveDistanceUsingIMU(double heading, double power, double distance) {
        // heading, power, distance, angle mode, ramp start power, ramp finish power, time to ramp in mSec
        robot.driveTrain.setupDriveDistanceUsingIMU(heading, power, distance, AdafruitIMU8863.AngleMode.RELATIVE, 0, power, 2000);

        // updateDriveDistanceUsingIMU return true when the robot has traveled the distance that was requested
        while (opModeIsActive() && !robot.driveTrain.updateDriveDistanceUsingIMU()) {
            telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
            telemetry.addData("distance = ", robot.driveTrain.getDistanceDriven());
            telemetry.update();
            idle();
        }
        telemetry.addData("distance = ", robot.driveTrain.getDistanceDriven());
        telemetry.addData("Final heading = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.addData("Running time = ", "%2.1f", timer.seconds());
        telemetry.update();
    }

    /**
     * not used anymore
     *
     * @param angle
     * @param power
     */
    public void anyTurn(double angle, double power) {
        robot.driveTrain.setupTurn(angle, power, AdafruitIMU8863.AngleMode.RELATIVE);

        while (opModeIsActive() && !robot.driveTrain.updateTurn()) {
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
            telemetry.update();
            idle();
        }
        telemetry.addData("Finished Turn", "0");
        telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.addData("Running time = ", "%2.1f", timer.seconds());
        telemetry.update();
    }


    /**
     * NOT used anymore
     *
     * @param distance
     * @param power
     */
    public void driveStraight(double distance, double power) {
        robot.driveTrain.setupDriveDistance(power, distance, DcMotor8863.FinishBehavior.FLOAT);

        while (opModeIsActive()) {
            statusDrive = robot.driveTrain.updateDriveDistance();
            if (statusDrive == DriveTrain.Status.COMPLETE) {
                break;
            }
            //telemetry.addData(">", "Press Stop to end test.");
            telemetry.addData("Status = ", statusDrive.toString());
            telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
            telemetry.update();
            idle();
        }
    }
}
