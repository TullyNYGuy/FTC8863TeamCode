package org.firstinspires.ftc.teamcode.opmodes.PowerPlay;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DrivingMode;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.TeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyField;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyGamepad;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyMatchInfo;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyStartSpot;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PoseStorage;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayField;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayGamepad;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobotModes;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlaySpeedController;

import java.util.List;

@TeleOp(name = "Teleop Power Play", group = "AA")
//@Disabled

public class PowerPlayTeleop extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public PowerPlayRobot robot;
    public PowerPlayGamepad gamepad;
    public Configuration config = null;
    public PowerPlayField field;

    // public AutomaticTeleopFunctions automaticTeleopFunctions;
    //set color for each game
    //private FreightFrenzyStartSpot color = PersistantStorage.getStartSpot();
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

        // set the persistant storage variable saying this is the teleop phase
        PowerPlayPersistantStorage.setMatchPhase(MatchPhase.TELEOP);

        dataLog = new DataLogging("Teleop", telemetry);
        config = null;
        config = new Configuration();
        if (!config.load()) {
            telemetry.addData("ERROR", "Couldn't load config file");
            telemetry.update();
        }
        timer = new ElapsedTime();
        MecanumCommands commands = new MecanumCommands();

        robot = new PowerPlayRobot(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        field = new PowerPlayField(PowerPlayPersistantStorage.getColorLocation());

        // create the robot and run the init for it
        robot.createRobot();
        gamepad = new PowerPlayGamepad(gamepad1, gamepad2, robot);

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        //automaticTeleopFunctions = new AutomaticTeleopFunctions(robot, field, telemetry);

        if (PersistantStorage.robotPose != null) {
            startPose = PowerPlayPersistantStorage.getRobotPose();
        } else {
            startPose = field.getStartPose();
        }

        // Setting the pose makes field centric drive 90 degrees out.
        //robot.mecanum.setPoseEstimate(startPose);
        timer.reset();

        // set the driver joystick controls to either normal or inverted
        if (PowerPlayPersistantStorage.getTeamLocation() == TeamLocation.LEFT) {
            robot.robotModes.setDirectionSwap(PowerPlayRobotModes.DirectionSwap.INVERSED);
        } else {
            // Right side of field
            robot.robotModes.setDirectionSwap(PowerPlayRobotModes.DirectionSwap.NORMAL);
        }

        // Wait for the start button

        telemetry.addData(">", "Press start to run Teleop (make sure you ran the position setter first!)");
        telemetry.update();

        //robot.setColor(color);
        waitForStart();

        robot.loopTimer.startLoopTimer();
        robot.coneGrabber.carryPosition();
        robot.leftLift.moveToPickup();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************
        while (opModeIsActive()) {

            // update the gamepad. It has the commands to be run when a button is pressed so the
            // gamepad actually runs the robot commands.
            gamepad.update();
            // update the robot
            robot.update();

            // The following code uses road runner to move the robot in a driver (field) centric
            // drive

            telemetry.addData("Max Power = ", robot.robotModes.getCurrentMaxPower());
            telemetry.addData("Speed Controller state = ", robot.speedController.getSpeedControllerState());
            telemetry.addLine();

            if (gamepad.getDrivingMode() == DrivingMode.ROBOT_CENTRIC) {
                telemetry.addData("Direction swap = ", robot.robotModes.getDirectionSwap());
                telemetry.addData("ROBOT CENTRIC driving", "!");

                robot.mecanum.calculateMotorCommandsRobotCentric(
                        gamepad.gamepad1LeftJoyStickYValue * robot.robotModes.getDirectionSwapMultiplier(),
                        gamepad.gamepad1LeftJoyStickXValue * robot.robotModes.getDirectionSwapMultiplier(),
                        gamepad.gamepad1RightJoyStickXValue
                );
            }
            if (gamepad.getDrivingMode() == DrivingMode.FIELD_CENTRIC) {
                telemetry.addData("FIELD CENTRIC driving", "!");
                robot.mecanum.calculateMotorCommandsFieldCentric(
                        gamepad.gamepad1LeftJoyStickYValue,
                        gamepad.gamepad1LeftJoyStickXValue,
                        gamepad.gamepad1RightJoyStickXValue
                );
            }

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


