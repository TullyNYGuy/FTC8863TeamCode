package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.ReadPictograph;
import org.firstinspires.ftc.teamcode.Lib.VelocityVortexLib.VelocityVortexShooter;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Read Pictograph", group = "Test")
//@Disabled
public class TestReadPictograph extends LinearOpMode {

    // enums for a state machine for the shooter motor

    //*********************************************************************************************
    //             Declarations
    //*********************************************************************************************
    ReadPictograph readPictograph;
    //
    @Override
    public void runOpMode() {

        //*********************************************************************************************
        //  Initializations after the pogram is selected by the user on the driver phone
        //*********************************************************************************************
        readPictograph = new ReadPictograph(hardwareMap, telemetry);

        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();

        readPictograph.runAtStart();

        //*********************************************************************************************
        //             Robot Running after the user his play on the driver phone
        //*********************************************************************************************
        while (opModeIsActive()){
            readPictograph.getvuMark();
        }

        //*************************************************************************************
        //  Stop everything after the user hits the stop button on the driver phone
        // ************************************************************************************
        telemetry.addData(">", "Done");
        telemetry.update();
    }
}