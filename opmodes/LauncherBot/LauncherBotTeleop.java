package org.firstinspires.ftc.teamcode.opmodes.LauncherBot;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DrivingMode;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.TeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.LauncherBot.LauncherBotGamepad;
import org.firstinspires.ftc.teamcode.Lib.LauncherBot.LauncherBotRobot;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayField;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayGamepad;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobotModes;

import java.util.List;

@TeleOp(name = "Teleop Launcher Bot", group = "AA")
//@Disabled

public class LauncherBotTeleop extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public LauncherBotRobot robot;
    public LauncherBotGamepad gamepad;
    public Configuration config = null;

    private ElapsedTime timer;

    DataLogging dataLog = null;

    private Pose2d startPose;

    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************

        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        dataLog = new DataLogging("Teleop", telemetry);
        config = null;
        config = new Configuration();
        if (!config.load()) {
            telemetry.addData("ERROR", "Couldn't load config file");
            telemetry.update();
        }
        timer = new ElapsedTime();
        MecanumCommands commands = new MecanumCommands();

        robot = new LauncherBotRobot(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);

        // create the robot and run the init for it
        robot.createRobot();
        gamepad = new LauncherBotGamepad(gamepad1, gamepad2, robot);

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        timer.reset();

        // Wait for the start button

        telemetry.addData(">", "Press start to run Teleop (make sure you ran the position setter first!)");
        telemetry.update();

        //robot.setColor(color);
        waitForStart();

        robot.loopTimer.startLoopTimer();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************
        while (opModeIsActive()) {

            // update the gamepad. It has the commands to be run when a button is pressed so the
            // gamepad actually runs the robot commands.
            gamepad.update();
            // update the robot
            robot.update();

            telemetry.addLine();

            robot.mecanum.calculateMotorCommandsRobotCentric(
                    gamepad.gamepad1LeftJoyStickYValue,
                    gamepad.gamepad1LeftJoyStickXValue,
                    gamepad.gamepad1RightJoyStickXValue
            );


            // feedback on the driver station

            gamepad.displayGamepad1JoystickValues(telemetry);
            telemetry.addData(">", "Press Stop to end.");
            telemetry.update();

            idle();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything. Note that some of the subsystem shutdowns may
        // write to the datalog so we can't close it just yet.
        robot.shutdown();
        dataLog.closeDataLog();
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    //*********************************************************************************************
    //             Helper methods
    //*********************************************************************************************

    public void enableBulkReads(HardwareMap hardwareMap, LynxModule.BulkCachingMode mode) {
        // set bulk read mode for the sensor reads - speeds up the loop
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(mode);
        }
    }
}


