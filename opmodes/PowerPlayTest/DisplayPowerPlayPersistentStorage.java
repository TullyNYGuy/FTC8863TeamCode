package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPersistantStorage;

@Autonomous(name = "Display Persistant Storage", group = "AARun")
@Disabled

public class DisplayPowerPlayPersistentStorage extends LinearOpMode {

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

        telemetry.addData("Alliance Color = ", PowerPlayPersistantStorage.getAllianceColor().toString());
        telemetry.addData("Team Location = ", PowerPlayPersistantStorage.getTeamLocation().toString());
        telemetry.addData("ColorTeam = ", PowerPlayPersistantStorage.getColorLocation().toString());
        telemetry.update();
        timer.reset();
        while (opModeIsActive() && timer.seconds() < 5) {
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





