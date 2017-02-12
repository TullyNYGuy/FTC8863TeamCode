package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.AllianceColorSwitch;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.FrontBeaconPusherControl;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.VelocityVortexRobot;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.VelocityVortexShooter;

/**
 * Autonomous for competition
 */
@Autonomous(name = "Velocity Vortex Autonomous", group = "Run")
//@Disabled
public class TestAutonomousShot extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    VelocityVortexRobot robot;
    DriveTrain.Status statusDrive;
    ElapsedTime timer;
    FrontBeaconPusherControl.FrontBeaconControlState frontBeaconControlState;

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

        //moveToFiringPosition();
        //shootAndMoveToLoadingPosition();
        loadABall();

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

    public void moveToFiringPosition() {
        robot.shooter.moveTo2Feet();
        while (opModeIsActive() && robot.shooter.update() != VelocityVortexShooter.State.AT_2_FEET) {
            // wait for shooter to arrive at firing position
        }
    }

    public void moveToLoadingPosition() {
        robot.shooter.moveToLoadPosition();
        while (opModeIsActive() && !robot.shooter.isAtLoadingPosition()){
            robot.shooter.update();
            // wait for shooter to arrive at loading position
        }
    }

    public void shootAndMoveToLoadingPosition () {
        while (opModeIsActive() && !robot.shooter.shootThenMoveToLoad()) {
            // wait for shot to be taken and shooter to move to load position
        }
    }

    public void loadABall() {
        while (opModeIsActive() && !robot.shooter.loadABall()) {
            // wait for ball to be loaded
            // might need to wiggle the robot while waiting
        }
    }
}
