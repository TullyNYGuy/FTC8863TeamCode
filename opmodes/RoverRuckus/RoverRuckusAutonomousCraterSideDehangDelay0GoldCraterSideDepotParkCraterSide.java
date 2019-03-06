package org.firstinspires.ftc.teamcode.opmodes.RoverRuckus;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.AutonomousConfigurationFile;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.AutonomousDirector;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.AutonomousMovementSteps;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.AutonomousMovements;

@Autonomous(name = "Auto Crater Dehang Crater Gold Depot Park Crater", group = "Test")
//@Disabled
public class RoverRuckusAutonomousCraterSideDehangDelay0GoldCraterSideDepotParkCraterSide extends LinearOpMode {

    // Put your variable declarations here
    RoverRuckusRobot robot;
    DataLogging logFile;
    AutonomousConfigurationFile configurationFile;
    AutonomousDirector autonomousDirector;
    AutonomousMovementSteps autonomousMovementSteps;

    @Override
    public void runOpMode() {

        // driver has pressed init, run initialization
        // Put your initializations here

        logFile = new DataLogging("Autonomous", telemetry);

        robot = RoverRuckusRobot.createRobotForAutonomous(hardwareMap, telemetry, AllianceColor.TeamColor.RED, logFile);
        // set the imu angles to 0 when the robot is placed on the ground in front of the lander
        robot.driveTrain.imu.resetAngleReferences();

        configurationFile = new AutonomousConfigurationFile();
        autonomousDirector = new AutonomousDirector(configurationFile);
        autonomousMovementSteps = new AutonomousMovementSteps(robot, autonomousDirector, logFile, hardwareMap, telemetry);

        // force the config file values since we are not actually reading a config file
        configurationFile.setHangLocation(AutonomousConfigurationFile.HangLocation.CRATER_SIDE);
        configurationFile.setSample(AutonomousConfigurationFile.Sample.CRATER_SIDE);
        configurationFile.setClaimDepot(true);
        configurationFile.setParkLocation(AutonomousConfigurationFile.ParkLocation.OUR_CRATER);
        configurationFile.setDelay(0);

        // translate the config file goals to robot tasks
        autonomousDirector.translator();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // driver has pressed play - run the autonomous

        logFile.startTimer();
        // Start the logging of measured acceleration
        robot.driveTrain.imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here
            robot.update();
            autonomousMovementSteps.update();
            idle();
        }
    }
}

