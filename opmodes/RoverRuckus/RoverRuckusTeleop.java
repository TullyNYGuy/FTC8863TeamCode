package org.firstinspires.ftc.teamcode.opmodes.RoverRuckus;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorGB;

@TeleOp(name = "Rover Ruckus Teleop", group = "Run")
//@Disabled

public class RoverRuckusTeleop extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    enum DriveTrainMode {
        TANK_DRIVE,
        DIFFERENTIAL_DRIVE;
    }

    DriveTrainMode driveTrainMode = RoverRuckusTeleop.DriveTrainMode.TANK_DRIVE;

    public RoverRuckusRobot robot;

    DataLogging dataLog = null;

    // GAMEPAD 1

    // declare the buttons on the gamepad as multi push button objects
    public GamepadButtonMultiPush gamepad1RightBumper;
    public GamepadButtonMultiPush gamepad1LeftBumper;
    public GamepadButtonMultiPush gamepad1a;
    public GamepadButtonMultiPush gamepad1b;
    public GamepadButtonMultiPush gamepad1y;
    public GamepadButtonMultiPush gamepad1x;
    public GamepadButtonMultiPush gamepad1DpadUp;
    public GamepadButtonMultiPush gamepad1DpadDown;
    public GamepadButtonMultiPush gamepad1DpadLeft;
    public GamepadButtonMultiPush gamepad1DpadRight;
    public GamepadButtonMultiPush gamepad1LeftStickButton;
    public GamepadButtonMultiPush gamepad1RightStickButton;

    // joystick and joystick value declarations - game pad 1
    final static double JOYSTICK_DEADBAND_VALUE = .15;
    final static double JOYSTICK_HALF_POWER = .5;
    final static double JOYSTICK_QUARTER_POWER = .25;

    JoyStick gamepad1LeftJoyStickX;
    JoyStick gamepad1LeftJoyStickY;
    double gamepad1LeftJoyStickXValue = 0;
    double gamepad1LeftJoyStickYValue = 0;

    JoyStick gamepad1RightJoyStickX;
    JoyStick gamepad1RightJoyStickY;
    double gamepad1RightJoyStickXValue = 0;
    double gamepad1RightJoyStickYValue = 0;

    // GAMEPAD 2

    // declare the buttons on the gamepad as multi push button objects
    public GamepadButtonMultiPush gamepad2RightBumper;
    public GamepadButtonMultiPush gamepad2LeftBumper;
    public GamepadButtonMultiPush gamepad2a;
    public GamepadButtonMultiPush gamepad2b;
    public GamepadButtonMultiPush gamepad2y;
    public GamepadButtonMultiPush gamepad2x;
    public GamepadButtonMultiPush gamepad2DpadUp;
    public GamepadButtonMultiPush gamepad2DpadDown;
    public GamepadButtonMultiPush gamepad2DpadLeft;
    public GamepadButtonMultiPush gamepad2DpadRight;
    public GamepadButtonMultiPush gamepad2LeftStickButton;
    public GamepadButtonMultiPush gamepad2RightStickButton;

    // joystick and joystick value declarations - game pad 2
    JoyStick gamepad2LeftJoyStickX;
    JoyStick gamepad2LeftJoyStickY;
    double gamepad2LeftJoyStickXValue = 0;
    double gamepad2LeftJoyStickYValue = 0;

    JoyStick gamepad2RightJoyStickX;
    JoyStick gamepad2RightJoyStickY;
    double gamepad2RightJoyStickXValue = 0;
    double gamepad2RightJoyStickYValue = 0;

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

        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        dataLog = new DataLogging("Teleop", telemetry);
        robot = robot.createRobotForTeleop(hardwareMap, telemetry, AllianceColor.TeamColor.RED, dataLog);
        robot.enableDataLogging();

        // create the gamepad 1 buttons and tell each button how many commands it has
        gamepad1RightBumper = new GamepadButtonMultiPush(1);
        gamepad1LeftBumper = new GamepadButtonMultiPush(1);
        gamepad1a = new GamepadButtonMultiPush(2);
        gamepad1b = new GamepadButtonMultiPush(2);
        gamepad1y = new GamepadButtonMultiPush(2);
        gamepad1x = new GamepadButtonMultiPush(1);
        gamepad1DpadUp = new GamepadButtonMultiPush(1);
        gamepad1DpadDown = new GamepadButtonMultiPush(1);
        gamepad1DpadLeft = new GamepadButtonMultiPush(1);
        gamepad1DpadRight = new GamepadButtonMultiPush(1);
        gamepad1LeftStickButton = new GamepadButtonMultiPush(1);
        gamepad1RightStickButton = new GamepadButtonMultiPush(1);

        // Game Pad 1 joysticks
        gamepad1LeftJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1LeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        gamepad1RightJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1RightJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        // create the gamepad 2 buttons and tell each button how many commands it has
        gamepad2RightBumper = new GamepadButtonMultiPush(1);
        gamepad2LeftBumper = new GamepadButtonMultiPush(1);
        gamepad2a = new GamepadButtonMultiPush(3);
        gamepad2b = new GamepadButtonMultiPush(1);
        gamepad2y = new GamepadButtonMultiPush(1);
        gamepad2x = new GamepadButtonMultiPush(1);
        gamepad2DpadUp = new GamepadButtonMultiPush(2);
        gamepad2DpadDown = new GamepadButtonMultiPush(1);
        gamepad2DpadLeft = new GamepadButtonMultiPush(1);
        gamepad2DpadRight = new GamepadButtonMultiPush(1);
        gamepad2LeftStickButton = new GamepadButtonMultiPush(1);
        gamepad2RightStickButton = new GamepadButtonMultiPush(1);

        // Game Pad 2 joysticks
        gamepad2LeftJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad2LeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        gamepad2RightJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad2RightJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);
        gamepad2RightJoyStickY.setHalfPower();

        // default the wheels to 30% power
        gamepad1LeftJoyStickX.set30PercentPower();
        gamepad1LeftJoyStickY.set30PercentPower();
        gamepad1RightJoyStickX.set30PercentPower();
        gamepad1RightJoyStickY.set30PercentPower();

        // Wait for the start button
        telemetry.addData(">", "Press start to run Teleop");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        // set the positions that the various systems need to be in when the robot is running
        robot.setupForRun();

        while (opModeIsActive()) {

            //*************************************************************************************
            // Gamepad 1 buttons
            //*************************************************************************************

            // example for a button with multiple commands attached to it:
            // don't forget to change the new line with the number of commands attached like this:
            // gamepad1x = new GamepadButtonMultiPush(4);
            //                                        ^
            //                                        |
            //
//            if (gamepad1x.buttonPress(gamepad1.x)) {
//                if (gamepad1x.isCommand1()) {
//                    // call the first command you want to run
//                }
//                if (gamepad1x.isCommand2()) {
//                    // call the 2nd command you want to run
//                }
//                if (gamepad1x.isCommand3()) {
//                    // call the 3rd command you want to run
//                }
//                if (gamepad1x.isCommand4()) {
//                    // call the 4th command you want to run
//                }
//            }

            if (gamepad1RightBumper.buttonPress(gamepad1.right_bumper)) {
                robot.collector.resetCollector();
            }

            if (gamepad1LeftBumper.buttonPress(gamepad1.left_bumper)) {
                robot.deliveryLiftSystem.liftReset();
            }

            if (gamepad1a.buttonPress(gamepad1.a)) {
                if (gamepad1a.isCommand1()) {
                    robot.collector.turnCollectorOn();
               }
                if (gamepad1a.isCommand2()) {
                    robot.collector.turnCollectorOff();
                }
            }

            if (gamepad1b.buttonPress(gamepad1.b)) {
                if (gamepad1b.isCommand1()) {
                    robot.collector.setDesiredMineralColorToGold();
                }
                if (gamepad1b.isCommand2()) {
                    robot.collector.setDesiredMineralColorToSilver();
                }
            }

            if (gamepad1y.buttonPress(gamepad1.y)) {
                if (gamepad1y.isCommand1()) {
                    robot.deliveryLiftSystem.goToSetupHang();
                    robot.collector.gateServoToResetPosition();
                    robot.collectorArm.extensionArmReset();
                    sleep(2000);
                    robot.collectorArm.rotationArmGoToHome();
                }
                if (gamepad1y.isCommand2()) {
                    robot.deliveryLiftSystem.goToLatch();
                }
            }

            if (gamepad1x.buttonPress(gamepad1.x)) {
                robot.deliveryLiftSystem.goToHang();
            }

            if (gamepad1DpadUp.buttonPress(gamepad1.dpad_up)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                gamepad1LeftJoyStickX.setFullPower();
                gamepad1LeftJoyStickY.setFullPower();
                gamepad1RightJoyStickX.setFullPower();
                gamepad1RightJoyStickY.setFullPower();
            }

            if (gamepad1DpadDown.buttonPress(gamepad1.dpad_down)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                gamepad1LeftJoyStickX.set30PercentPower();
                gamepad1LeftJoyStickY.set30PercentPower();
                gamepad1RightJoyStickX.set30PercentPower();
                gamepad1RightJoyStickY.set30PercentPower();
            }

            if (gamepad1DpadLeft.buttonPress(gamepad1.dpad_left)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                gamepad1LeftJoyStickX.setHalfPower();
                gamepad1LeftJoyStickY.setHalfPower();
                gamepad1RightJoyStickX.setHalfPower();
                gamepad1RightJoyStickY.setHalfPower();
            }

            if (gamepad1DpadRight.buttonPress(gamepad1.dpad_right)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                gamepad1LeftJoyStickX.set20PercentPower();
                gamepad1LeftJoyStickY.set20PercentPower();
                gamepad1RightJoyStickX.set20PercentPower();
                gamepad1RightJoyStickY.set20PercentPower();
            }

            if (gamepad1LeftStickButton.buttonPress(gamepad1.left_stick_button)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            if (gamepad1RightStickButton.buttonPress(gamepad1.right_stick_button)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
            }

            //**************************************************************************************
            // Gamepad 1 joysticks
            //**************************************************************************************

            gamepad1LeftJoyStickXValue = gamepad1LeftJoyStickX.scaleInput(gamepad1.left_stick_x);
            gamepad1LeftJoyStickYValue = gamepad1LeftJoyStickY.scaleInput(gamepad1.left_stick_y);

            gamepad1RightJoyStickXValue = gamepad1RightJoyStickX.scaleInput(gamepad1.right_stick_x);
            gamepad1RightJoyStickYValue = gamepad1RightJoyStickY.scaleInput(gamepad1.right_stick_y);

            //**************************************************************************************
            // Gamepad 2 buttons
            //**************************************************************************************

            // example for a button with multiple commands attached to it:
            // don't forget to change the new line with the number of commands attached like this:
            // gamepad1x = new GamepadButtonMultiPush(4);
            //                                        ^
            //                                        |
            //
//            if (gamepad1x.buttonPress(gamepad1.x)) {
//                if (gamepad1x.isCommand1()) {
//                    // call the first command you want to run
//                }
//                if (gamepad1x.isCommand2()) {
//                    // call the 2nd command you want to run
//                }
//                if (gamepad1x.isCommand3()) {
//                    // call the 3rd command you want to run
//                }
//                if (gamepad1x.isCommand4()) {
//                    // call the 4th command you want to run
//                }
//            }

            if (gamepad2RightBumper.buttonPress(gamepad2.right_bumper)) {
                robot.collectorArm.extensionArmReset();
            }

            if (gamepad2LeftBumper.buttonPress(gamepad2.left_bumper)) {
                robot.resetToColletionPositionControl();
                robot.resetTransferScoringControl();
            }

            if (gamepad2a.buttonPress(gamepad2.a)) {
                if (gamepad2a.isCommand1()) {
                    robot.startTransferMinerals();
                }
                if (gamepad2a.isCommand2()) {
                    robot.finishTransferMinerals();
                }
                if (gamepad2a.isCommand3()) {
                    robot.confirmTransferSuccess();
                }
            }

            if (gamepad2b.buttonPress(gamepad2.b)) {
                robot.clearTransferJam();
            }

            if (gamepad2y.buttonPress(gamepad2.y)) {
                robot.score();
            }

            if (gamepad2x.buttonPress(gamepad2.x)) {
                robot.deliveryLiftSystem.deliveryBoxToDump();
                sleep(1000);
                robot.deliveryLiftSystem.deliveryBoxToHome();
            }

            if (gamepad2DpadUp.buttonPress(gamepad2.dpad_up)) {
                if (gamepad2DpadUp.isCommand1()) {
                    robot.collectorArm.raiseOffGround();
                }
                if (gamepad2DpadUp.isCommand2()) {
                    robot.collectorArm.rotationArmFloatArm();
                }
            }

            if (gamepad2DpadDown.buttonPress(gamepad2.dpad_down)) {
                robot.lowerCollectorArmToCollect();
            }

            if (gamepad2DpadLeft.buttonPress(gamepad2.dpad_left)) {
                robot.collector.turnIntakeOnSpitOut();
            }

            if (gamepad2DpadRight.buttonPress(gamepad2.dpad_right)) {
                robot.collector.turnIntakeOnSuckIn();
            }

            if (gamepad2LeftStickButton.buttonPress(gamepad2.left_stick_button)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                robot.adjustCollectorArmTranferAngleTowardsStop();
            }

            if (gamepad2RightStickButton.buttonPress(gamepad2.right_stick_button)) {
                // this was a new button press, not a button held down for a while
                // put the command to be executed here
                robot.adjustCollectorArmTranferAngleTowardsFloor();
            }

            //**************************************************************************************
            // Gamepad 2 joysticks
            //**************************************************************************************

            gamepad2LeftJoyStickXValue = gamepad2LeftJoyStickX.scaleInput(gamepad2.left_stick_x);
            gamepad2LeftJoyStickYValue = gamepad2LeftJoyStickY.scaleInput(gamepad2.left_stick_y);

            gamepad2RightJoyStickXValue = gamepad2RightJoyStickX.scaleInput(gamepad2.right_stick_x);
            gamepad2RightJoyStickYValue = gamepad2RightJoyStickY.scaleInput(gamepad2.right_stick_y);


            //*************************************************************************************
            //  Process joysticks into drive train commands
            // ************************************************************************************

            // joysticks to tank drive
            leftPower = gamepad1LeftJoyStickYValue;
            rightPower = gamepad1RightJoyStickYValue;

            // joysticks to differential drive
            throttle = gamepad1RightJoyStickYValue;
            direction = gamepad1RightJoyStickXValue;

            // update the drive motors
            if (driveTrainMode == RoverRuckusTeleop.DriveTrainMode.TANK_DRIVE) {
                robot.driveTrain.tankDrive(leftPower, rightPower);
            } else {
                // differential drive
                robot.driveTrain.differentialDrive(throttle, direction);
            }

            robot.collectorArm.setExtensionArmPowerUsingJoystick(gamepad2RightJoyStickYValue);

            // update the robot
            robot.update();

            // Display telemetry
            robot.collector.displayWhichMineralCollecting();
            telemetry.addData("Left Motor Speed = ", "%3.2f", leftPower);
            telemetry.addData("Right Motor Speed = ", "%3.2f", rightPower);
            telemetry.addData("Drive train mode = ", driveTrainMode.toString());
            telemetry.addData("Drive Forward / Reverse = ", robot.driveTrain.getDriveDirection().toString());
            telemetry.addData("Power Reduction = ", "%1.2f", gamepad1LeftJoyStickY.getReductionFactor());
            //telemetry.addData("Collector State = ", robot.collector.update().toString());
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
        if (driveTrainMode == RoverRuckusTeleop.DriveTrainMode.DIFFERENTIAL_DRIVE) {
            driveTrainMode = RoverRuckusTeleop.DriveTrainMode.TANK_DRIVE;
        } else
            driveTrainMode = RoverRuckusTeleop.DriveTrainMode.DIFFERENTIAL_DRIVE;
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

