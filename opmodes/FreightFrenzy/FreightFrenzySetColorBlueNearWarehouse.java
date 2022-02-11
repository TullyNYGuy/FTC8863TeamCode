package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzy;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyStartSpot;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;

@Autonomous(name = "SET BLUE ALLIANCE, near Warehouse", group = "AARun")


public class FreightFrenzySetColorBlueNearWarehouse extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    private ElapsedTime timer;


    @Override
    public void runOpMode() {
        timer = new ElapsedTime();
        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************

        // create the robot
        telemetry.addData("press start", "now");
        telemetry.update();
        waitForStart();


        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************


            PersistantStorage.setStartSpot(FreightFrenzyStartSpot.BLUE_WAREHOUSE);
            telemetry.addData(">", "Color set as blue. spot as near warehouse");
            telemetry.update();
            timer.reset();
            while(opModeIsActive() && timer.seconds() < 5){
            idle();
            }
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************

        // Stop has been hit, shutdown everything



    }

    //*********************************************************************************************
    //             Helper methods
    //*********************************************************************************************




