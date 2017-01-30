package org.firstinspires.ftc.teamcode.opmodes.VelocityVortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.VelocityVortexRobot;

/**
 * Autonomous for competition - not complete, in fact not really started
 */
@Autonomous(name = "Velocity Vortex Autonomous", group = "Run")
//@Disabled
public class VelocityVortexAutonomous extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    enum DriveTrainMode {
        TANK_DRIVE,
        DIFFERENTIAL_DRIVE;
    }

    DriveTrainMode driveTrainMode = DriveTrainMode.TANK_DRIVE;

    VelocityVortexRobot robot;

    DriveTrain.Status statusDrive;

    // drive train powers for tank drive
    double leftPower = 0;
    double rightPower = 0;

    // drive train powers for differential drive
    double throttle = 0;
    double direction = 0;

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************

        robot = robot.createRobotForAutonomous(hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press start to run Autonomous");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user his play on the driver phone
        //*********************************************************************************************

        driveStraight(39, 0.5);
        telemetry.addData("Finished Straight", "1");
        telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.update();
        sleep(1000);

        anyTurn(56, 0.4);
        telemetry.addData("Finished Turn", "1");
        telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.update();
        sleep(1000);

        driveStraight(130, 0.5);
        telemetry.addData("Finished Straight", "1");
        telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.update();
        sleep(1000);

        anyTurn(-56, 0.4);
        telemetry.addData("Finished Turn", "1");
        telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.update();
        sleep(1000);

        driveStraight(161, 0.5);
        telemetry.addData("Finished Straight", "1");
        telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.update();
        sleep(1000);

        anyTurn(36, 0.4);
        telemetry.addData("Finished Turn", "1");
        telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.update();
        sleep(1000);

        driveStraight(-170, 0.5);
        telemetry.addData("Finished Straight", "1");
        telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.update();
        sleep(1000);

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

    /**
     * Change from differential drive mode to tank drive, or tank drive to differential
     */
    private void toggleDriveTrainMode() {
        if (driveTrainMode == DriveTrainMode.DIFFERENTIAL_DRIVE) {
            driveTrainMode = DriveTrainMode.TANK_DRIVE;
        } else
            driveTrainMode = DriveTrainMode.DIFFERENTIAL_DRIVE;
    }

    public void anyTurn(double angle, double power) {
        robot.driveTrain.setupTurn(angle, power);

        while (opModeIsActive() && !robot.driveTrain.updateTurn()) {
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
            telemetry.update();
            idle();
        }
        telemetry.addData("Finished Turn", "0");
        telemetry.addData("Angle = ", "%3.1f", robot.driveTrain.imu.getHeading());
        telemetry.update();
    }

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
