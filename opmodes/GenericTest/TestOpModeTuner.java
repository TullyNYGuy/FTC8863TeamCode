package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import net.frogbots.ftcopmodetunercommon.opmode.TunableLinearOpMode;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test OpMode Tuner", group = "Test")
//@Disabled
public class TestOpModeTuner extends TunableLinearOpMode {

    // Put your variable declarations here

    @Override
    public void runOpMode() {


        // Put your initializations here
        int exampleInt;
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();



        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here

            exampleInt = getInt("testInt");

            // Display the current value
            telemetry.addData("Int = ", exampleInt);
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
