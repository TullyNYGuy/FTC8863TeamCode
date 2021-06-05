package org.firstinspires.ftc.teamcode.opmodes.UltimateGoal;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.util.Angle;
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
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.AutomaticTeleopFunctions;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PoseStorage;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalGamepad;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;
import org.firstinspires.ftc.teamcode.opmodes.SkystoneDiagnostics.TestDualLiftLimitSwitches;

import java.util.List;

@TeleOp(name = "Shooter angle set up", group = "Setup")
//@Disabled

public class AngleSetterUpper extends LinearOpMode {

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

        // Exposing the angle changer guts to the world is a bit scary. You just don't know what
        // Kellen is going to do to it. Instead make those dangerous calls private and only let him
        // have access in a controlled way. It is
        // less dangerous - cause he is a dangerous kind of guy!
        robot.shooter.clearAngleChanger();
        //PersistantStorage.setShooterAngle(0,AngleUnit.DEGREES);
        //PersistantStorage.setMotorTicks(0);

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        timer.reset();

        // Wait for the start button
        telemetry.addData(">", "Press start to set the shooter angle to 0");
        telemetry.update();

        waitForStart();

        robot.shooter.resetAngleToZero();
        while(opModeIsActive() && !robot.shooter.isResetToZeroComplete()){
            robot.update();
            idle();
        }

        // let the angle changer take care of this. It knows how to do it. You should not have to
        // know how to do it.
        // The idea is that the angle changer should be responsible for all of its operations. No
        // outside code should be handling the details of operating it.

//        PersistantStorage.setShooterAngle( 0,AngleUnit.DEGREES);
//        PersistantStorage.setMotorTicks(0);
//        robot.shooter.resetMotor();

//        robot.shooter.setAngle(AngleUnit.DEGREES,20);

        telemetry.addData("Shooter angle is now 0", "!");
        telemetry.addData("Press A on gamepad 1 to setup the start angle for the shooter", "!");
        telemetry.update();

        while(opModeIsActive() && !gamepad1.a) {
            idle();
        }

        // a has been pressed so change the shooter angle to the start angle

        robot.shooter.setToStartAngle();
        while(opModeIsActive() && !robot.shooter.isStartAngleReached()) {
            robot.update();
            idle();
        }

        telemetry.addData("Start angle reached = ", robot.shooter.getStartAngle(AngleUnit.DEGREES));
        telemetry.addData("Start angle and motor encoder saved to persistant storage.", "");
        telemetry.addData("Press A to end this routine", "!");
        telemetry.update();


        while(opModeIsActive() && !gamepad1.a) {
            idle();
        }

//        while (opModeIsActive()) {
//            // update the robot
//            robot.update();
//
//            telemetry.addData(">", "Press Stop to end.");
//            telemetry.update();
//
//            idle();
//        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything. Note that some of the subsystem shutdowns may
        // write to the datalog so we can't close it just yet.

        // The angle changer knows how to do this. You should not have to.
        // PersistantStorage.setMotorTicks(robot.shooter.getMotorTicks());

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


