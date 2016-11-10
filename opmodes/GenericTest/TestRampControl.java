package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.RampControl;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Ramp Control", group = "Test")
//@Disabled
public class TestRampControl extends LinearOpMode {

    // Put your variable declarations here
    double value = 1;
    double rampValue = 0;
    RampControl rampControl;

    @Override
    public void runOpMode() {


        // Put your initializations here
        rampControl = new RampControl(.5, 1, 10000);
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        rampControl.start();

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here
            rampValue = rampControl.getRampValueLinear(value);

            // Display the current value
            telemetry.addData("Ramp = ", "%5.2f", rampValue);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
