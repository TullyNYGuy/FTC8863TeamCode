package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.RampControl;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Ramp Control", group = "Test")
@Disabled
public class TestRampControl extends LinearOpMode {

    // Put your variable declarations here
    double value = -1;
    double rampValue = 0;
    RampControl rampControl;
    double startValue = 1.0;
    double finishValue = -1.0;
    double lengthOfTimeToRamp = 10000; // in mSec

    ElapsedTime loopTimer;

    @Override
    public void runOpMode() {


        // Put your initializations here
        rampControl = new RampControl(startValue, finishValue, lengthOfTimeToRamp);
        loopTimer = new ElapsedTime();
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        rampControl.start();
        loopTimer.reset();

        while(opModeIsActive() && rampControl.isRunning()) {

            // Put your calls that need to run in a loop here
            rampValue = rampControl.getRampValueLinear(value);

            // Display the current value
            telemetry.addData("Ramp = ", "%5.2f", rampValue);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // After the ramp control time expires, the loop will terminate and we end up here
        telemetry.addData("Ramp time (in sec): ", "%5.2f", loopTimer.milliseconds()*1000);
        telemetry.update();
        sleep(2000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
