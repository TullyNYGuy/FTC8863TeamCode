package org.firstinspires.ftc.teamcode.opmodes.VelocityVortex;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.VelocityVortexRobot;

/**
 * Autonomous for competition - not complete, in fact not really started
 */
@TeleOp(name = "Velocity Vortex Teleop", group = "Run")
//@Disabled
public class VelocityVortexAutonomouse extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    enum DriveTrainMode {
        TANK_DRIVE,
        DIFFERENTIAL_DRIVE;
    }

    DriveTrainMode driveTrainMode = DriveTrainMode.TANK_DRIVE;

    VelocityVortexRobot robot;

    // for use in debouncing the button. A long press will only result in one transition of the
    // button

    boolean gamepad1RightBumperIsReleased = true;
    boolean gamepad1LeftBumperIsReleased = true;
    boolean gamepad1aButtonIsReleased = true;
    boolean gamepad1bButtonIsReleased = true;
    boolean gamepad1yButtonIsReleased = true;
    boolean gamepad1xButtonIsReleased = true;
    boolean gamepad1DpadUpIsReleased = true;
    boolean gamepad1DpadDownIsReleased = true;
    boolean gamepad1DpadLeftIsReleased = true;
    boolean gamepad1DpadRightIsReleased = true;
    boolean gamepad1LeftStickButtonIsReleased = true;
    boolean gamepad1RightStickButtonIsReleased = true;

    // joystick and joystick value declarations - game pad 1
    final static double JOYSTICK_DEADBAND_VALUE = .15;

    JoyStick gamepad1LeftJoyStickX;
    JoyStick gamepad1LeftJoyStickY;
    double gamepad1LeftJoyStickXValue = 0;
    double gamepad1LeftJoyStickYValue = 0;

    JoyStick gamepad1RightJoyStickX;
    JoyStick gamepad1RightJoyStickY;
    double gamepad1RightJoyStickXValue = 0;
    double gamepad1RightJoyStickYValue = 0;

    // for use in debouncing the button. A long press will only result in one transition of the
    // button
    boolean gamepad2RightBumperIsReleased = true;
    boolean gamepad2LeftBumperIsReleased = true;
    boolean gamepad2aButtonIsReleased = true;
    boolean gamepad2bButtonIsReleased = true;
    boolean gamepad2yButtonIsReleased = true;
    boolean gamepad2xButtonIsReleased = true;
    boolean gamepad2DpadUpIsReleased = true;
    boolean gamepad2DpadDownIsReleased = true;
    boolean gamepad2DpadLeftIsReleased = true;
    boolean gamepad2DpadRightIsReleased = true;
    boolean gamepad2LeftStickButtonIsReleased = true;
    boolean gamepad2RightStickButtonIsReleased = true;

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

        robot = robot.createRobotForTeleop(hardwareMap, telemetry);

        // Game Pad 1 joysticks
        gamepad1LeftJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1LeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        gamepad1RightJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad1RightJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        // Game Pad 2 joysticks
        gamepad2LeftJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad2LeftJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        gamepad2RightJoyStickX = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.NO_INVERT_SIGN);
        gamepad2RightJoyStickY = new JoyStick(JoyStick.JoyStickMode.SQUARE, JOYSTICK_DEADBAND_VALUE, JoyStick.InvertSign.INVERT_SIGN);

        // Wait for the start button
        telemetry.addData(">", "Press start to run Teleop");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user his play on the driver phone
        //*********************************************************************************************

        while (opModeIsActive()) {

            //*************************************************************************************
            // Gamepad 1 buttons
            //*************************************************************************************
            
            if (gamepad1.y) {
                if (gamepad1yButtonIsReleased) {
                    gamepad1yButtonIsReleased = false;
                }
            } else {
                gamepad1yButtonIsReleased = true;
            }

            if (gamepad1.a) {
                if (gamepad1aButtonIsReleased) {
                    gamepad1aButtonIsReleased = false;
                }
            } else {
                gamepad1aButtonIsReleased = true;
            }

            if (gamepad1.x) {
                if (gamepad1xButtonIsReleased) {
                    // stop the sweeper motor
                    robot.sweeper.stop();
                    gamepad1xButtonIsReleased = false;
                }
            } else {
                gamepad1xButtonIsReleased = true;
            }

            if (gamepad1.b) {
                if (gamepad1bButtonIsReleased) {
                    gamepad1bButtonIsReleased = false;
                }
            } else {
                gamepad1bButtonIsReleased = true;
            }

            if (gamepad1.right_bumper) {
                if (gamepad1RightBumperIsReleased) {
                    gamepad1RightBumperIsReleased = false;
                }
            } else {
                gamepad1RightBumperIsReleased = true;
            }

            if (gamepad1.left_bumper) {
                if (gamepad1LeftBumperIsReleased) {
                    // toggle the drive train mode: differential <-> tank drive
                    toggleDriveTrainMode();
                    gamepad1LeftBumperIsReleased = false;
                }
            } else {
                gamepad1LeftBumperIsReleased = true;
            }

            if (gamepad1.dpad_up) {
                if (gamepad1DpadUpIsReleased) {
                    // shoot balls 
                    robot.sweeper.shoot();
                    gamepad1DpadUpIsReleased = false;
                }
            } else {
                gamepad1DpadUpIsReleased = true;
            }

            if (gamepad1.dpad_down) {
                if (gamepad1DpadDownIsReleased) {
                    //collect balls
                    robot.sweeper.collect();
                    gamepad1DpadDownIsReleased = false;
                }
            } else {
                gamepad1DpadDownIsReleased = true;
            }

            if (gamepad1.dpad_left) {
                if (gamepad1DpadLeftIsReleased) {
                    gamepad1DpadLeftIsReleased = false;
                }
            } else {
                gamepad1DpadLeftIsReleased = true;
            }

            if (gamepad1.dpad_right) {
                if (gamepad1DpadRightIsReleased) {
                    gamepad1DpadRightIsReleased = false;
                }
            } else {
                gamepad1DpadRightIsReleased = true;
            }

            if (gamepad1.left_stick_button) {
                if (gamepad1LeftStickButtonIsReleased) {
                    gamepad1LeftStickButtonIsReleased = false;
                }
            } else {
                gamepad1LeftStickButtonIsReleased = true;
            }

            if (gamepad1.right_stick_button) {
                if (gamepad1RightStickButtonIsReleased) {
                    gamepad1RightStickButtonIsReleased = false;
                }
            } else {
                gamepad1RightStickButtonIsReleased = true;
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

            if (gamepad2.y) {
                if (gamepad2yButtonIsReleased) {
                    gamepad2yButtonIsReleased = false;
                }
            } else {
                gamepad2yButtonIsReleased = true;
            }

            if (gamepad2.a) {
                if (gamepad2aButtonIsReleased) {
                    gamepad2aButtonIsReleased = false;
                }
            } else {
                gamepad2aButtonIsReleased = true;
            }

            if (gamepad2.x) {
                if (gamepad2xButtonIsReleased) {
                    gamepad2xButtonIsReleased = false;
                }
            } else {
                gamepad2xButtonIsReleased = true;
            }

            if (gamepad2.b) {
                if (gamepad2bButtonIsReleased) {
                    gamepad2bButtonIsReleased = false;
                }
            } else {
                gamepad2bButtonIsReleased = true;
            }

            if (gamepad2.right_bumper) {
                if (gamepad2RightBumperIsReleased) {
                    gamepad2RightBumperIsReleased = false;
                }
            } else {
                gamepad2RightBumperIsReleased = true;
            }

            if (gamepad2.left_bumper) {
                if (gamepad2LeftBumperIsReleased) {
                    gamepad2LeftBumperIsReleased = false;
                }
            } else {
                gamepad2LeftBumperIsReleased = true;
            }

            if (gamepad2.dpad_up) {
                if (gamepad2DpadUpIsReleased) {
                    gamepad2DpadUpIsReleased = false;
                }
            } else {
                gamepad2DpadUpIsReleased = true;
            }

            if (gamepad2.dpad_down) {
                if (gamepad2DpadDownIsReleased) {
                    gamepad2DpadDownIsReleased = false;
                }
            } else {
                gamepad2DpadDownIsReleased = true;
            }

            if (gamepad2.dpad_left) {
                if (gamepad2DpadLeftIsReleased) {
                    gamepad2DpadLeftIsReleased = false;
                }
            } else {
                gamepad2DpadLeftIsReleased = true;
            }

            if (gamepad2.dpad_right) {
                if (gamepad2DpadRightIsReleased) {
                    gamepad2DpadRightIsReleased = false;
                }
            } else {
                gamepad2DpadRightIsReleased = true;
            }

            if (gamepad2.left_stick_button) {
                if (gamepad2LeftStickButtonIsReleased) {
                    gamepad2LeftStickButtonIsReleased = false;
                }
            } else {
                gamepad2LeftStickButtonIsReleased = true;
            }

            if (gamepad2.right_stick_button) {
                if (gamepad2RightStickButtonIsReleased) {
                    gamepad2RightStickButtonIsReleased = false;
                }
            } else {
                gamepad2RightStickButtonIsReleased = true;
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
            if(driveTrainMode == DriveTrainMode.TANK_DRIVE) {
                robot.driveTrain.tankDrive(leftPower, rightPower);
            } else {
                // differential drive
                robot.driveTrain.differentialDrive(throttle, direction);
            }
            
            // update the robot
            robot.update();
            
            // Display telemetry
            telemetry.addData("Left Motor Speed = ", "%3.2f", leftPower);
            telemetry.addData("Right Motor Speed = ", "%3.2f", rightPower);
            telemetry.addData("Sweeper Motor Speed = ", "%3.2f", robot.sweeper.getSweeperPower());
            telemetry.addData("Drive train mode = ", driveTrainMode.toString());
            telemetry.addData(">", "Press Stop to end.");
            telemetry.update();

            idle();
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

    /**
     * Change from differential drive mode to tank drive, or tank drive to differential
     */
    private void toggleDriveTrainMode() {
        if (driveTrainMode == DriveTrainMode.DIFFERENTIAL_DRIVE) {
            driveTrainMode = DriveTrainMode.TANK_DRIVE;
        } else
            driveTrainMode = DriveTrainMode.DIFFERENTIAL_DRIVE;
    }
}
