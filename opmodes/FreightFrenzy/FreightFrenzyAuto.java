package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzy;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyField;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyGamepad;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;


import org.openftc.easyopencv.OpenCvWebcam;

import java.util.List;

@Autonomous(name = "Auto Roadrunner Freight Frenzy", group = "AARun")
@Disabled

public class FreightFrenzyAuto extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************
    public OpenCvWebcam webcam;
    public FreightFrenzyRobotRoadRunner robot;
    public FreightFrenzyGamepad gamepad;
    public FreightFrenzyField field;
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

        dataLog = new DataLogging("Auto", telemetry);
        config = null;
//        config = new Configuration();
//        if (!config.load()) {
//            telemetry.addData("ERROR", "Couldn't load config file");
//            telemetry.update();
//        }
        timer = new ElapsedTime();
        //MecanumCommands commands = new MecanumCommands();



        robot = new FreightFrenzyRobotRoadRunner(hardwareMap, telemetry, config, dataLog, DistanceUnit.CM, this);

        // create the robot and run the init for it
        robot.createRobot();

        enableBulkReads(hardwareMap, LynxModule.BulkCachingMode.AUTO);


        field = new FreightFrenzyField();

        // create the gamepad
        gamepad = new FreightFrenzyGamepad(gamepad1, gamepad2, robot );

        timer.reset();

        // run the state machines associated with the subsystems to allow the inits to complete
        // NOTE, if a subsystem does not complete the init, it will hang the robot, so that is what
        // the timer is for
        while (!robot.isInitComplete()) {
            robot.update();
            if (timer.milliseconds() > 5000) {
                // something went wrong with the inits. They never finished. Proceed anyway

                dataLog.logData("Init failed to complete on time. Proceeding anyway!");
                break;
            }
            idle();
        }

        // Wait for the start button
        telemetry.addData(">", "Press start to run Teleop");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************
        timer.reset();
        while (opModeIsActive()) {
            //use ShippingElementPipeline to find the position of shipping element (left,right,center)
            // robot goes to depot
            //robot intakes a thing
            //robot goes to tower
            //insert SWITCH CASE:
            //case(left){
            // put into top level of tower}
            //case(right){
            // put into bottom level of tower}
            //case(middle){
            // put into middle of tower}
            //

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

    public void enableBulkReads(HardwareMap hardwareMap, LynxModule.BulkCachingMode mode) {
        // set bulk read mode for the sensor reads - speeds up the loop
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(mode);
        }
    }
}


