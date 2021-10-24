package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyGamepad;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyIntake;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.AutomaticTeleopFunctions;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PoseStorage;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalField;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalGamepad;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;

import java.util.List;

@TeleOp(name = "Teleop Freight Frenzy", group = "AA")
//@Disabled

public class SimpleTeleopUsingRoadRunner extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    public FreightFrenzyIntake intake;
    public FreightFrenzyGamepad gamepad;
    public Configuration config = null;

    public AutomaticTeleopFunctions automaticTeleopFunctions;

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

        //dataLog = new DataLogging("Teleop", telemetry);
        config = null;
//        config = new Configuration();
//        if (!config.load()) {
//            telemetry.addData("ERROR", "Couldn't load config file");
//            telemetry.update();
//        }
        timer = new ElapsedTime();
        //MecanumCommands commands = new MecanumCommands();

        intake = new FreightFrenzyIntake(hardwareMap, telemetry);

        // create the robot and run the init for it
        intake.init(config);

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);

        // create the gamepad
        //gamepad = new UltimateGoalGamepad(gamepad1, gamepad2, robot);
        //robot.shooter.setMotorTicks(PersistantStorage.getMotorTicks());
        gamepad = new FreightFrenzyGamepad(gamepad1, gamepad2, intake);


        if (PersistantStorage.robotPose != null) {
            startPose = PersistantStorage.robotPose;
        } else {
            startPose = PoseStorage.START_POSE;
        }

        //robot.mecanum.setPoseEstimate(startPose);
        timer.reset();

        // the inits are run as part of createRobot(). They should not be needed here.
//        // run the state machines associated with the subsystems to allow the inits to complete
//        // NOTE, if a subsystem does not complete the init, it will hang the robot, so that is what
//        // the timer is for
//        while (!robot.isInitComplete()) {
//            robot.update();
//            if (timer.milliseconds() > 5000) {
//                // something went wrong with the inits. They never finished. Proceed anyway
//
//                dataLog.logData("Init failed to complete on time. Proceeding anyway!");
//                //How cheerful. How comforting...
//                break;
//            }
//            idle();
//        }

        // Wait for the start button
        telemetry.addData(">", "Press start to run Teleop");
        telemetry.update();
        double multiplier = -1;
        if (PersistantStorage.robotPose != null) {
            //robot.mecanum.setPoseEstimate(PersistantStorage.robotPose);
            multiplier = -1;
        }

        waitForStart();

        //robot.loopTimer.startLoopTimer();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        while (opModeIsActive()) {

            // update the gamepad. It has the commands to be run when a button is pressed so the
            // gamepad actually runs the robot commands.
            gamepad.update();
//            automaticTeleopFunctions.update();

            // The following code uses road runner to move the robot in a driver (field) centric
            // drive
//            if (!automaticTeleopFunctions.isBusy()) {
//                robot.mecanum.calculateMotorCommandsFieldCentric(
//                        gamepad.gamepad1LeftJoyStickYValue * multiplier,
//                        gamepad.gamepad1LeftJoyStickXValue * multiplier,
//                        gamepad.gamepad1RightJoyStickXValue
//                );
//            }


            // update the robot
            //robot.update();
            intake.update();

            // feedback on the driver station

            telemetry.addData(">", "Press Stop to end.");
            telemetry.update();

            idle();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything. Note that some of the subsystem shutdowns may
        // write to the datalog so we can't close it just yet.
        //robot.shutdown();
        //dataLog.closeDataLog();
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


