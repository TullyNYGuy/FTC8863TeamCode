package org.firstinspires.ftc.teamcode.opmodes.RoverRuckus;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

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
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.AutonomousMovements;

@Autonomous(name = "wicked cool test for some teacherz( no Piddazle)", group = "Test")
@Disabled
public class gefTeacherAutonomousWithoutIMU extends LinearOpMode {

    // Put your variable declarations here
    RoverRuckusRobot robot;
    DataLogging logFile;
    AutonomousMovements autonomousMovements;

    @Override
    public void runOpMode() {

        // driver has pressed init, run initialization
        // Put your initializations here

        logFile = new DataLogging("Autonomous", telemetry);

        robot = RoverRuckusRobot.createRobotForAutonomous(hardwareMap, telemetry, AllianceColor.TeamColor.RED, logFile);
        // set the imu angles to 0 when the robot is placed on the ground in front of the lander
        robot.driveTrain.imu.resetAngleReferences();

        autonomousMovements = new AutonomousMovements(robot, logFile, telemetry);
        robot.driveTrain.enableLogDrive();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // driver has pressed play - run the autonomous

        logFile.startTimer();
        // Start the logging of measured acceleration
        robot.driveTrain.imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

        robot.driveTrain.setupDriveDistance(.5, 150 * 2.54, DcMotor8863.FinishBehavior.FLOAT);
        robot.driveTrain.startDriveDistance();

        while (opModeIsActive() && (robot.driveTrain.updateDriveDistance() != DriveTrain.Status.COMPLETE)) {
            // Put your calls that need to run in a loop here
            idle();

        }
        robot.driveTrain.stopDriveDistance();
    }
}

