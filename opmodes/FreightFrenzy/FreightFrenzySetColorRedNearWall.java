package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzy;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyStartSpot;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;

@Autonomous(name = "SET RED ALLIANCE, near Wall", group = "AARun")


public class FreightFrenzySetColorRedNearWall extends LinearOpMode {

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
        telemetry.addData("press init", "now");
        telemetry.update();



        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        
            PersistantStorage.setStartSpot(FreightFrenzyStartSpot.RED_WALL);
            telemetry.addData(">", "Color set as red. Spot as near wall");
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





