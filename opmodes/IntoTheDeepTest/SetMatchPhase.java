package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest;

//

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;

@Autonomous(name = "Set match phase", group = "Test")
//@Disabled

public class SetMatchPhase extends LinearOpMode {

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
        MatchPhase.setMatchPhase(MatchPhase.AUTONOMOUS);
        telemetry.addData("set match phase to ", "autonomous");
        telemetry.update();
        timer.reset();
        // display result for 5 seconds
        
        while (opModeIsActive() && timer.seconds() < 5) {
            idle();
        }
    }
}





