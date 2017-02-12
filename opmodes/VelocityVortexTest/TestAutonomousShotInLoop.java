package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.FrontBeaconPusherControl;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.VelocityVortexRobot;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.VelocityVortexShooter;

/**
 * Autonomous for competition
 */
@TeleOp(name = "Test Shooter Autonomous Shot Loop", group = "Test")
//@Disabled
public class TestAutonomousShotInLoop extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    VelocityVortexRobot robot;
    DriveTrain.Status statusDrive;
    ElapsedTime timer;
    FrontBeaconPusherControl.FrontBeaconControlState frontBeaconControlState;

    enum AutonomousState {
        START,
        MOVING_TO_LIMIT_SWITCH,
        MOVING_TO_FIRING_POSITION
    }

    AutonomousState autonomousState = AutonomousState.START;

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************

        robot = robot.createRobotForAutonomous(hardwareMap, telemetry);
        timer = new ElapsedTime();
        boolean stopStateMachine = false;

        // Wait for the start button
        robot.allianceColorSwitch.displayAllianceSwitch(telemetry);
        telemetry.addData(">", "Press start to run Autonomous");
        telemetry.update();
        waitForStart();
        timer.reset();

        //*********************************************************************************************
        //             Robot Running after the user his play on the driver phone
        //*********************************************************************************************

        while(opModeIsActive() && !stopStateMachine) {
                switch (autonomousState) {
                    case START:
                        robot.shooter.moveToLimitSwitchManual();
                        autonomousState = AutonomousState.MOVING_TO_LIMIT_SWITCH;
                        break;
                    case MOVING_TO_LIMIT_SWITCH:
                        // check to see if we got to the limit switch
                        if (robot.shooter.getShooterState() == VelocityVortexShooter.State.AT_SWITCH) {
                            robot.shooter.moveTo2Feet();
                            autonomousState = AutonomousState.MOVING_TO_FIRING_POSITION;
                        }
                        break;
                    case MOVING_TO_FIRING_POSITION:
                        if (robot.shooter.getShooterState() == VelocityVortexShooter.State.AT_2_FEET) {
                            stopStateMachine = true;
                        }
                        break;
                }
                robot.shooter.update();
        }

        telemetry.addData("Finished moving to Firing Point", "");
        telemetry.update();
        sleep(3000);

        //shootAndMoveToLoadingPosition();

        shootThenWait();
        telemetry.addData("Finished shooting", "");
        telemetry.update();
        sleep(3000);
        moveToLoadingPosition();
        telemetry.addData("Finished moving to loading point", "");
        telemetry.update();
        sleep(3000);
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
        while (opModeIsActive() && robot.shooter.update() != VelocityVortexShooter.State.AT_2_FEET){
            telemetry.addData("MOVING2FEET Shooter State", robot.shooter.getShooterState().toString());
            telemetry.addData("MOVING2FEET Current Power", "%2.2f", robot.shooter.aimingMotor.getCurrentPower());
            telemetry.addData("Is Auto OK?", robot.shooter.isAutoAimingOK());
            telemetry.addData("Counter for 2 Feet No Limit Switch", "%5d", robot.shooter.getMovingTo2FeetNoLimitSwitchCounter());
            telemetry.update();
            // wait for shooter to arrive at firing position
        }
    }

    public void moveToLimitSwitch() {
        //set 0 point
        while (opModeIsActive() && robot.shooter.update() != VelocityVortexShooter.State.AT_SWITCH){
            robot.shooter.moveToLimitSwitchManual();
            telemetry.addData("MOVELIMITSWITCH Shooter State", robot.shooter.getShooterState().toString());
            telemetry.addData("MOVELIMITSWITCH Current Power", "%2.2f", robot.shooter.aimingMotor.getCurrentPower());
            telemetry.update();
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
        while (opModeIsActive() && !robot.shooter.openBallGateAndWait()) {
            // wait for ball to be loaded
            // might need to wiggle the robot while waiting
        }
        robot.shooter.closeBallGate();
        telemetry.addData("Done with loading a ball", "!");
        telemetry.update();
    }

    public void shootThenWait() {
        robot.shooter.shoot();
        while(opModeIsActive()&&! robot.shooter.checkShotComplete()) {
            robot.shooter.shooterMotor.update();
        }
    }
}
