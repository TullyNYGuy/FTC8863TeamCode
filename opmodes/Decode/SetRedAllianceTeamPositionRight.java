package org.firstinspires.ftc.teamcode.opmodes.Decode;

//

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeStoreBetweenMatches;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Color;

@Autonomous(name = "Set RED alliance, RIGHT side", group = "AA")
@Disabled

public class SetRedAllianceTeamPositionRight extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    ElapsedTime timer;
    Pose2D startingPose;

    double poseX = 0; // in inches
    double poseY = 0; // in inches
    double heading = 0; // in degrees

    @Override
    public void runOpMode() {
        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************
        timer = new ElapsedTime();
        startingPose = new Pose2D(DistanceUnit.INCH, poseX, poseY, AngleUnit.DEGREES, heading);

        telemetry.addData("press start", "now");
        telemetry.update();
        waitForStart();

        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************
        DecodeStoreBetweenMatches.limelightPipelineNumber = 0;
        DecodeStoreBetweenMatches.startingPose = startingPose;
        AllianceColorTeamLocation.setAllianceColor(Color.RED);
        AllianceColorTeamLocation.setTeamLocation(AllianceColorTeamLocation.TeamLocation.RIGHT);
        telemetry.addData(">", "Red Alliance, Right side");
        telemetry.update();
        timer.reset();
        // display result for 5 seconds
        while (opModeIsActive() && timer.seconds() < 5) {
            idle();
        }
    }
}





