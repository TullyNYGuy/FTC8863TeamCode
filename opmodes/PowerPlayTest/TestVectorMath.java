package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest;

import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Vector2D;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPersistantStorage;

@TeleOp(name = "Test Vector Math", group = "Test")
//@Disabled

public class TestVectorMath extends LinearOpMode {

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************

    Vector2d startLocation = new Vector2d(0, 0);
    double heading = Math.toRadians(100-90.0);
    double movement = 2.0;
    Vector2d destination;


    @Override
    public void runOpMode() {
        //*********************************************************************************************
        //  Initializations after the program is selected by the user on the driver phone
        //*********************************************************************************************

        destination = startLocation.minus(new Vector2d(movement).rotated(-heading));


        telemetry.addData("press start", "now");
        telemetry.update();
        waitForStart();


        //*********************************************************************************************
        //             Robot Running after the user hits play on the driver phone
        //*********************************************************************************************


        while (opModeIsActive()) {
            telemetry.addData("new x location = ", destination.getX() + " " + destination.getY());
            telemetry.update();
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





