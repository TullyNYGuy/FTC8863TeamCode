package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest;

//

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;

@Autonomous(name = "Verify Alliance Color Team Position", group = "AA")
@Disabled

public class SetTestAllianceColorTeamPosition extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    ElapsedTime timer;

    @Override
    public void runOpMode() {
        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************
        timer = new ElapsedTime();

        telemetry.addData("press start", "now");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************

        telemetry.addData("Team Color     = ", AllianceColorTeamLocation.getAllianceColor().toString());
        telemetry.addData("Team Location  = ", AllianceColorTeamLocation.getTeamLocation().toString());
        telemetry.addData("Color/Location = ", AllianceColorTeamLocation.getColorLocation().toString());
        telemetry.update();
        timer.reset();
        // display result for 5 seconds

        while (opModeIsActive() && timer.seconds() < 5) {
            idle();
        }
    }
}





