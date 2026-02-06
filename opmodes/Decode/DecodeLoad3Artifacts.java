package org.firstinspires.ftc.teamcode.opmodes.Decode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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

@Autonomous(name = "Load 3 artifacts", group = "B")
//@Disabled

public class DecodeLoad3Artifacts extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public DecodeRobot robot;
    public DecodeGamepad gamepad;
    public Configuration config = null;
    //public DecodeField field;
    public Pose2D startPose;

    private ElapsedTime timer;

    DataLogging dataLog = null;

    boolean preLoadIsFinished = false;

    @Override
    public void runOpMode() {

        MatchPhase.setMatchPhase(MatchPhase.AUTONOMOUS);

        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************

        // create the robot
        telemetry.addData("Initializing ...", "Wait for it ...");
        telemetry.update();

        dataLog = new DataLogging("Load3Artifacts", telemetry);
        config = null;
        config = new Configuration();
        if (!config.load()) {
        }
        timer = new ElapsedTime();

        robot = new DecodeRobot(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        //field = new DecodeField(PowerPlayPersistantStorage.getColorLocation());

        // create the robot and run the init for it
        robot.createRobot();
        robot.setAllianceColor(AllianceColorTeamLocation.getAllianceColor());

        startPose = new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0);
        robot.mecanumDrive.pinpoint.setPosition(startPose);

        // the sorter is empty. We are loading 3 artifacts
        robot.sorterController.setCurrentState(DecodeSorterController.SorterState.EMPTY_EMPTY_EMPTY);

        // default the pipeline for the limelight to a long shot
        robot.turntableTrackingController.setPipelineNumber(DecodeShotDistance.ShotType.LONG);
        // set a fixed angle on the turntable controller
        robot.turntableTrackingController.setRequestedTurntableAngle(0);
        robot.turntableTrackingController.setMode(DecodeTurntableTrackingController.ControlMode.ROTATE_TO_FIXED_ANGLE);

        robot.indicator.disableDataLogging();

        gamepad = new DecodeGamepad(gamepad1, gamepad2, robot);
        robot.turntableTrackingController.setGamepad(gamepad);

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        telemetry.addData(">", "Press start to pre-load 3 artifacts");
        telemetry.addLine();
        telemetry.addData(">", "MAKE SURE THE SHOOTER IS STRAIGHT!");
        telemetry.update();

        // Wait for the start button

        waitForStart();

        timer.reset();

        robot.turntableTrackingController.start(100);
        robot.loopTimer.startLoopTimer();

        // start the intake so the balls can preload
        robot.sorterController.intake();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************
        // run until the preload is finished
        while (opModeIsActive() && !robot.sorterController.isPreloadFinished()) {

            // update the gamepad. It has the commands to be run when a button is pressed so the
            // gamepad actually runs the robot commands.
            gamepad.update();
            // update the robot
            robot.update();

            // feedback on the driver station

            robot.sorterController.displayState(telemetry);
            robot.sorterController.displayCommand(telemetry);

            telemetry.addLine();

            robot.turntableTrackingController.displayShooterAngleToTarget(telemetry);
            robot.turntableMotor.displayTurntableAngle(telemetry);

            telemetry.addLine();

            telemetry.addData("Robot Pose X: ", robot.mecanumDrive.pinpoint.getPosition().getX(DistanceUnit.INCH));
            telemetry.addData("Robot Pose Y: ", robot.mecanumDrive.pinpoint.getPosition().getY(DistanceUnit.INCH));
            telemetry.addData("Robot Pose Heading: ", robot.mecanumDrive.pinpoint.getPosition().getHeading(AngleUnit.DEGREES));
            telemetry.addData("", "");
            telemetry.addData(">", "Press Stop to end.");
            telemetry.update();

            idle();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything. Note that some of the subsystem shutdowns may
        // write to the datalog so we can't close it just yet.
        // store the state of the sorter controller
        DecodeStoreBetweenMatches.sorterState = robot.sorterController.getCurrentState();
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


