package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;

import java.util.List;

@TeleOp(name = "Test Angle Adjuster Limit Switch", group = "Diagnostics")
//@Disabled

public class AngleAdjusterLimitSwitchTest extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public UltimateGoalRobotRoadRunner robot;
    public Configuration config;
    public Switch limitSwitch;

    private ElapsedTime timer;

    DataLogging dataLog = null;

   // private Pose2d startPose;

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

        timer = new ElapsedTime();

        robot = new UltimateGoalRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);
        FtcDashboard.start();
        // create the robot and run the init for it
        robot.createRobot();

        PersistantStorage.setShooterAngle(0,AngleUnit.DEGREES);
        PersistantStorage.setMotorTicks(0);

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        timer.reset();

        // Wait for the start button
        telemetry.addData(">", "Press start to display limit switch status");
        telemetry.update();

        waitForStart();

        while(opModeIsActive()){
            robot.shooter.displaySwitchStatus(telemetry);
            telemetry.update();
            idle();
        }

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


