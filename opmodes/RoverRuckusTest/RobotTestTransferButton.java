package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.opmodes.RoverRuckus.RoverRuckusRobot;

@TeleOp(name = "Robot Test Transfer Button", group = "Test")
@Disabled

public class RobotTestTransferButton extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    enum DriveTrainMode {
        TANK_DRIVE,
        DIFFERENTIAL_DRIVE;
    }

    enum States {
        START,
        LOWER_ARM_TO_COLLECT,
        TEST_TRANSFER,
        RAISE_ARM_TO_TRANSFER,
        TRANSFER,
        CONFIRM_TRANSFER,
        TEST_BUTTON_ON_SCORE
    }

    States state = States.LOWER_ARM_TO_COLLECT;

    DriveTrainMode driveTrainMode = RobotTestTransferButton.DriveTrainMode.TANK_DRIVE;

    public RoverRuckusRobot robot;

    DataLogging dataLog = null;

    // drive train powers for tank drive
    double leftPower = 0;
    double rightPower = 0;

    // drive train powers for differential drive
    double throttle = 0;
    double direction = 0;

    ElapsedTime timer;

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************

        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        dataLog = new DataLogging("Teleop", telemetry);
        robot = robot.createRobotForTeleop(hardwareMap, telemetry, AllianceColor.RED, dataLog);
        robot.enableDataLogging();

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press start to run");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        // set the positions that the various systems need to be in when the robot is running
        robot.setupForRun();

        while (opModeIsActive()) {

            // update the robot
            robot.update();

            switch (state) {
                case START:
                    break;
                case LOWER_ARM_TO_COLLECT:
                    robot.lowerCollectorArmToCollect();
                    timer.reset();
                    state = States.RAISE_ARM_TO_TRANSFER;
                    break;
                case RAISE_ARM_TO_TRANSFER:
                    if (timer.milliseconds() > 5000) {
                        // raise the arm to transfer position
                        robot.toggleTransferButtonCommand();
                        timer.reset();
                        state = States.TEST_TRANSFER;
                    }
                    break;
                case TEST_TRANSFER:
                    if (timer.milliseconds() > 250) {
                        // this should not do anything
                        robot.toggleTransferButtonCommand();
                        timer.reset();
                        state = States.TRANSFER;
                    }
                    break;
                case TRANSFER:
                    if (timer.milliseconds() > 4000) {
                        // start a transfer
                        robot.toggleTransferButtonCommand();
                        timer.reset();
                        state = States.CONFIRM_TRANSFER;
                    }
                    break;
                case CONFIRM_TRANSFER:
                    if (timer.milliseconds() > 2000) {
                        // confirm transfer complete
                        robot.toggleTransferButtonCommand();
                        timer.reset();
                        state = States.TEST_BUTTON_ON_SCORE;
                    }
                    break;
                case TEST_BUTTON_ON_SCORE:
                    if (timer.milliseconds() > 2000) {
                        // robot is now ready to score.
                        // this should not do anything
                        robot.toggleTransferButtonCommand();
                    }
                    break;
            }

            // Display telemetry
            telemetry.addData(">", "Press Stop to end.");
            telemetry.update();

            idle();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything
        dataLog.closeDataLog();
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
        if (driveTrainMode == RobotTestTransferButton.DriveTrainMode.DIFFERENTIAL_DRIVE) {
            driveTrainMode = RobotTestTransferButton.DriveTrainMode.TANK_DRIVE;
        } else
            driveTrainMode = RobotTestTransferButton.DriveTrainMode.DIFFERENTIAL_DRIVE;
    }

    public double actualTurnAngle;

//    public void relicAlignment() {
//        driveStraight(-30.5, 0.1);
//        spinTurn(-45, 0.1, AdafruitIMU8863.AngleMode.ABSOLUTE);
//        actualTurnAngle = robot.driveTrain.imu.getHeading();
//        sleep(1000);
//    }

    public void driveStraight(double distance, double power) {
        DriveTrain.Status statusDrive = DriveTrain.Status.COMPLETE;
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
    }

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
}

