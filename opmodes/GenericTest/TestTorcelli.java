package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Torcelli;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Torcelli", group = "Test")
@Disabled
public class TestTorcelli extends LinearOpMode {

    // Put your variable declarations here
    Torcelli torcelli;
    ElapsedTime timer;
    double distance = 10;
    double distanceAlongWay = 0;

    @Override
    public void runOpMode() {

        // Put your initializations here
        torcelli = new Torcelli(1, .1, distance, telemetry); // initial power, finish power
        telemetry.addData("Torcelli 2a = ", "%2.3f", torcelli.getAccelerationTimesTwo());
        timer = new ElapsedTime();
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();
        timer.reset();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive() && distanceAlongWay <= distance) {

            //simulate travel by using the timer to create a fake distance
            distanceAlongWay = timer.seconds();

            telemetry.addData("power is now = ", "%2.2f", torcelli.getPower(distanceAlongWay));
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData("Final power = ", "%2.2f", torcelli.getPower(distanceAlongWay));
        telemetry.addData(">", "Done");
        telemetry.update();
        sleep(2000);

    }
}
