package org.firstinspires.ftc.teamcode.opmodes.Decode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeGamepad;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeRobot;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeShotDistance;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeSorterController;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeStoreBetweenMatches;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeTurntableTrackingController;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DrivingMode;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;


import java.util.List;

@TeleOp(name = "Teleop Decode", group = "AA")
//@Disabled

public class DecodeTeleop extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public DecodeRobot robot;
    public DecodeGamepad gamepad;
    public Configuration config = null;
    //public DecodeField field;
    public Pose2D startPose;

    FtcDashboard dashboard = FtcDashboard.getInstance();
    Telemetry dashboardTelemetry = dashboard.getTelemetry();

    // public AutomaticTeleopFunctions automaticTeleopFunctions;
    //set color for each game
    //private FreightFrenzyStartSpot color = PersistantStorage.getStartSpot();
    private ElapsedTime timer;

    DataLogging dataLog = null;


    //private Pose2d startPose;

    @Override
    public void runOpMode() {

        MatchPhase.setMatchPhase(MatchPhase.TELEOP);

        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************

        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        // set the persistant storage variable saying this is the teleop phase
//        PowerPlayPersistantStorage.setMatchPhase(MatchPhase.TELEOP);

        dataLog = new DataLogging("Teleop", telemetry);
        config = null;
        config = new Configuration();
        if (!config.load()) {
//            telemetry.addData("ERROR", "Couldn't load config file");
//            telemetry.update();
        }
        timer = new ElapsedTime();
//        MecanumCommands commands = new MecanumCommands();

        robot = new DecodeRobot(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        //field = new DecodeField(PowerPlayPersistantStorage.getColorLocation());

        // create the robot and run the init for it
        robot.createRobot();
        robot.setAllianceColor(AllianceColorTeamLocation.getAllianceColor());

        // default the sorter to no artifacts if there is no previous saved info
        if (DecodeStoreBetweenMatches.sorterState == null) {
            robot.sorterController.setCurrentState(DecodeSorterController.SorterState.EMPTY_EMPTY_EMPTY);
        }
        else {
            robot.sorterController.setCurrentState(DecodeStoreBetweenMatches.sorterState);
        }

        // default the pipeline for the limelight to a long shot
        robot.turntableTrackingController.setPipelineNumber(DecodeShotDistance.ShotType.LONG);

        // default the starting pose of the robot if there is no previously saved pose - like from auto
        startPose = DecodeStoreBetweenMatches.startingPose;
        if (startPose == null) {
            startPose = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
        }
        robot.mecanumDrive.pinpoint.setPosition(startPose);

        // control the rough positioning of the turntable using either the joystick (JOYSTICK_CONTROL) or the using a calculation of the
        // angle to the goal derived from the robot pose (PINPOINT_CONTROL)
        robot.turntableTrackingController.setMode(DecodeTurntableTrackingController.ControlMode.JOYSTICK_POSITION_CONTROL_WITH_LIMELIGHT);
        robot.indicator.disableDataLogging();

        gamepad = new DecodeGamepad(gamepad1, gamepad2, robot);
        robot.turntableTrackingController.setGamepad(gamepad);

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        //automaticTeleopFunctions = new AutomaticTeleopFunctions(robot, field, telemetry);

//        if (PersistantStorage.robotPose != null) {
//            startPose = PowerPlayPersistantStorage.getRobotPose();
//        } else {
//            startPose = field.getStartPose();
//        }

        // Setting the pose makes field centric drive 90 degrees out.
        //robot.mecanum.setPoseEstimate(startPose);
        timer.reset();

        // set the driver joystick controls to either normal or inverted
//        if (PowerPlayPersistantStorage.getTeamLocation() == TeamLocation.LEFT) {
//            robot.robotModes.setDirectionSwap(DecodeRobotModes.DirectionSwap.INVERSED);
//        } else {
//            // Right side of field
//            robot.robotModes.setDirectionSwap(DecodeRobotModes.DirectionSwap.NORMAL);
//        }

        // Wait for the start button

        telemetry.addData(">", "Press start to run Teleop (make sure you ran the position setter first!)");
        telemetry.update();

        //robot.setColor(color);
        waitForStart();

        robot.turntableTrackingController.start(100);

//            robot.intakeBucketController.getReadyToRun();
//            while(!robot.intakeBucketController.isGetReadyToRunComplete()) {
//                robot.update();
//            }
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

            // The following code uses road runner to move the robot in a driver (field) centric
            // drive


            telemetry.addLine();

            if (gamepad.getDrivingMode() == DrivingMode.ROBOT_CENTRIC) {
                telemetry.addData("Direction swap = ", robot.robotModes.getDirectionSwap());
                telemetry.addData("ROBOT CENTRIC driving", "!");

                robot.mecanumDrive.calculateMotorCommandsRobotCentric(
                        gamepad.gamepad1LeftJoyStickYValue * robot.robotModes.getDirectionSwapMultiplier(),
                        gamepad.gamepad1LeftJoyStickXValue * robot.robotModes.getDirectionSwapMultiplier(),
                        gamepad.gamepad1RightJoyStickXValue
                );

                // this bypasses the fancy mecanumdrive above. Make sure you comment out the lines above to disable the fancy
                // mecanum drive if you use this next line
              //  simpleMecanumDrive(gamepad1);
            }


//            if (gamepad.getDrivingMode() == DrivingMode.FIELD_CENTRIC) {
//                                    telemetry.addData("FIELD CENTRIC driving", "!");
//                robot.mecanumDrive.calculateMotorCommandsFieldCentric(
//                        gamepad.gamepad1LeftJoyStickYValue,
//                        gamepad.gamepad1LeftJoyStickXValue,
//                        gamepad.gamepad1RightJoyStickXValue
//                );
//            }

            // feedback on the driver station

            //gamepad.displayGamepad1JoystickValues(telemetry);
            //gamepad.displayGamepad2JoystickValues(telemetry);
            robot.sorterController.displayState(telemetry);
            robot.sorterController.displayCommand(telemetry);

            telemetry.addLine();

            robot.turntableTrackingController.displayIsOnTarget(telemetry);
            robot.turntableTrackingController.displayTargettingMethod(telemetry);
            robot.turntableMotor.displayMotorPower(telemetry);
            robot.turntableTrackingController.displayShooterAngleToTarget(telemetry);
            robot.turntableMotor.displayTurntableAngle(telemetry);

            telemetry.addLine();

            robot.ballShooter.displayActualRPM(telemetry);
            robot.ballShooter.displayHoodPosition(telemetry);

            telemetry.addLine();

            telemetry.addData("Robot Pose X: ", robot.mecanumDrive.pinpoint.getPosition().getX(DistanceUnit.INCH));
            telemetry.addData("Robot Pose Y: ", robot.mecanumDrive.pinpoint.getPosition().getY(DistanceUnit.INCH));
            telemetry.addData("Robot Pose Heading: ", robot.mecanumDrive.pinpoint.getPosition().getHeading(AngleUnit.DEGREES));
            telemetry.addData("", "");
            telemetry.addData(">", "Press Stop to end.");
            telemetry.addData("Robot Range To Target: ", robot.turntableTrackingController.getRobotRangeToTarget());
            telemetry.update();

            dashboardTelemetry.addData("Requested Angle ", robot.turntableTrackingController.getShooterAngleToTarget());
            dashboardTelemetry.addData("Actual Angle ", robot.turntableTrackingController.getActualPosition());
            dashboardTelemetry.addData("Motor Power ", robot.turntableMotor.newPower);
            dashboardTelemetry.update();

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

    public void simpleMecanumDrive(Gamepad gamepad1) {
        // I am working this is  code 0110011010001hhhhhhhhhhhhhhhhhhhhhhht code code codeing coding coding too 2 also me 2
        // multiple power commands by this factor
        double powerReductionFront = 1.0;
        double powerReductionRear = 0.3;

        double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
        double x = -gamepad1.right_stick_x * 1.1; // Counteract imperfect strafing
        double rx = -gamepad1.left_stick_x;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        robot.mecanumDrive.leftFront.setPower(frontLeftPower * powerReductionFront);
        robot.mecanumDrive.leftBack.setPower(backLeftPower * powerReductionRear);
        robot.mecanumDrive.rightFront.setPower(frontRightPower * powerReductionFront);
        robot.mecanumDrive.rightBack.setPower(backRightPower * powerReductionRear);
    }
}


