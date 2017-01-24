package org.firstinspires.ftc.teamcode.opmodes.VelocityVortexTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.VelocityVortex.MuxPlusColorSensors;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Mux Plus Color Sensors", group = "Test")
//@Disabled
public class TestMuxPlusColorSensors extends LinearOpMode {

    // Put your variable declarations here
    MuxPlusColorSensors muxPlusColorSensors;

    @Override
    public void runOpMode() {


        // Put your initializations here
        muxPlusColorSensors = new MuxPlusColorSensors(hardwareMap, telemetry);
        // display the initialization results
        telemetry.update();
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here
            muxPlusColorSensors.displayAllColorResults();

            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
