package org.firstinspires.ftc.teamcode.opmodes.PowerPlay;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.apache.commons.math3.analysis.function.Power;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.TeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyStartSpot;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPersistantStorage;

@Autonomous(name = "Set BLUE alliance, LEFT side", group = "AARun")
@Disabled

public class SetBlueAllianceTeamPositionLeft extends LinearOpMode {

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

        PowerPlayPersistantStorage.setAllianceColor(AllianceColor.BLUE);
        PowerPlayPersistantStorage.setTeamLocation(TeamLocation.LEFT);
        PowerPlayPersistantStorage.setColorLocation(AllianceColorTeamLocation.getColorLocation(AllianceColor.BLUE, TeamLocation.LEFT));
        telemetry.addData(">", "Blue Alliance, Left side");
        telemetry.update();
        timer.reset();
        while (opModeIsActive() && timer.seconds() < 5) {
            idle();
        }
    }
}





