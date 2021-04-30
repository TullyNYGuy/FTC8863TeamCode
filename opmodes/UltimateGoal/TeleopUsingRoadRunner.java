package org.firstinspires.ftc.teamcode.opmodes.UltimateGoal;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.JoyStick;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.SmartJoystick;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.IntakeWheels;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.GamepadUltimateGoal;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;

@TeleOp(name = "Teleop", group = "AARun")
//@Disabled

public class TeleopUsingRoadRunner extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public UltimateGoalRobotRoadRunner robot;
    public GamepadUltimateGoal gamepad;
    public Configuration config;

    private ElapsedTime timer;

    DataLogging dataLog = null;

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************

        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        dataLog = new DataLogging("Teleop", telemetry);
        config = new Configuration();
        if (!config.load()) {
            telemetry.addData("ERROR", "Couldn't load config file");
            telemetry.update();
        }
        timer = new ElapsedTime();
        MecanumCommands commands = new MecanumCommands();

        robot = new UltimateGoalRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);

        robot.createRobot();
        // start the inits for the robot subsytems
        robot.init();
        timer.reset();

        // run the state machines associated with the subsystems to allow the inits to complete
        // NOTE, if a subsystem does not complete the init, it will hang the robot, so that is what
        // the timer is for
        while (!robot.isInitComplete()) {
            robot.update();
            if (timer.milliseconds() > 5000) {
                // something went wrong with the inits. They never finished. Proceed anyway

                dataLog.logData("Init failed to complete on time. Proceeding anyway!");
                //How cheerful. How comforting...
                break;
            }
            idle();
        }

        // create the gamepad
        gamepad = new GamepadUltimateGoal(gamepad1, gamepad2, robot);

        // Wait for the start button
        telemetry.addData(">", "Press start to run Teleop");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        while (opModeIsActive()) {

            // update the gamepad. It has the commands to be run when a button is pressed so the
            // gamepad actually runs the robot commands.
            gamepad.update();

             //*************************************************************************************
            //  Process joysticks into drive commands
            // ************************************************************************************

            // The following code uses road runner to move the robot in a driver (field) centric
            // drive
            robot.mecanum.calculateMotorCommandsFieldCentric(gamepad.gamepad1LeftJoyStickYValue, gamepad.gamepad1LeftJoyStickXValue, gamepad.gamepad1RightJoyStickXValue);

            // update the robot
            robot.update();

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
}


