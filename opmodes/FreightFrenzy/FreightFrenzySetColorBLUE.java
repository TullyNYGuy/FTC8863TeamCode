package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzy;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyColor;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyField;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyGamepad;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotMode;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.Pipelines.ShippingElementPipeline;

import java.util.List;

@Autonomous(name = "SET BLUE ALLIANCE", group = "AARun")


public class FreightFrenzySetColorBLUE extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************




    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************

        // create the robot
        telemetry.addData("press init", "now");
        telemetry.update();

//        config = new Configuration();
//        if (!config.load()) {
//            telemetry.addData("ERROR", "Couldn't load config file");
//            telemetry.update();
//        }

        // create the robot and run the init for it




        // run the state machines associated with the subsystems to allow the inits to complete
        // NOTE, if a subsystem does not complete the init, it will hang the robot, so that is what
        // the timer is for


        // THE SHIPPING ELEMENT DETECTION SHOULD TAKE PLACE HERE, NOT AFTER waitForStart()
        // THE WEBCAM SHOULD BE SHUT DOWN AFTER THE ELEMENT IS LOCATED SO IT DOES NOT TAKE UP
        // RESOURCES
        //webcam.stopStreaming();
        //webcam.closeCameraDevice();

        //initializes the pipeline so the initial image is there
        //pipeline.init();
        // Wait for the start button
        telemetry.addData(">", "Press start to set color.");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        while (opModeIsActive()) {
            //use ShippingElementPipeline to find the position of shipping element (left,right,center)
            //robot deposits the preloaded blcok onto corresp
            // robot goes to depot
            //robot intakes a thing
            //robot goes to tower
            //Switch(position):
            //case(left){
            // put into top level of tower}
            //case(right){
            // put into bottom level of tower}
            //case(middle){
            // put into middle of tower}
            //

            // update the robot
           //ine refresh/continue
           // pipeline.processFrame(robot.webcam.)
            PersistantStorage.setColor(FreightFrenzyColor.BLUE);
            telemetry.addData(">", "Color set as blue. Press Stop");
            telemetry.update();

            idle();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything


        telemetry.addData(">", "color set as Blue");
        telemetry.update();
    }

    //*********************************************************************************************
    //             Helper methods
    //*********************************************************************************************


}


