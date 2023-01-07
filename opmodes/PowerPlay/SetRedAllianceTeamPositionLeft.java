package org.firstinspires.ftc.teamcode.opmodes.PowerPlay;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.TeamLocation;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPersistantStorage;

@Autonomous(name = "Set RED alliance, LEFT side", group = "AARun")


public class SetRedAllianceTeamPositionLeft extends LinearOpMode {

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

        PowerPlayPersistantStorage.setAllianceColor(AllianceColor.RED);
        PowerPlayPersistantStorage.setTeamLocation(TeamLocation.LEFT);
        PowerPlayPersistantStorage.setColorLocation(AllianceColorTeamLocation.getColorLocation(AllianceColor.RED, TeamLocation.LEFT));
        telemetry.addData(">", "Red Alliance, Left side");
        telemetry.update();
        timer.reset();
        while (opModeIsActive() && timer.seconds() < 5) {
            idle();
        }
    }
}




