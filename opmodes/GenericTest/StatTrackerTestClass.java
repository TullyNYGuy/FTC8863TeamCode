package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.StatTracker;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Matt's Test Code", group = "Test")
@Disabled
public class StatTrackerTestClass extends LinearOpMode {

    // Put your variable declarations here
    StatTracker statTrackerTest;
    ElapsedTime elapsedTime;
    double timemilliseconds = 0;
    ElapsedTime looptimer;
    @Override
    public void runOpMode() {


        // Put your initializations here
        statTrackerTest = new StatTracker();
        elapsedTime = new ElapsedTime ();
        looptimer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        elapsedTime.reset();
        looptimer.reset();
        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here
            if (looptimer.milliseconds() > 7000) {
                break;
            }
            timemilliseconds = elapsedTime.milliseconds();
            statTrackerTest.compareValue(timemilliseconds);
            elapsedTime.reset();
            // Display the current value
            //telemetry.addData("Motor Speed = ", "%5.2f", powerToRunAt);
            //telemetry.addData("Encoder Count=", "%5d", motor.getCurrentPosition());
            telemetry.addData(">", "Press Stop to end Stat Tracker." );
            telemetry.update();

            idle();
        }
        //Prints the results of the loop's calculations (average, minimum, maximum, etc.)
        telemetry.addData("Maximum Value =", "%5.2f", statTrackerTest.getMaximum());
        telemetry.addData("Minimum Value =", "%5.2f", statTrackerTest.getMinimum());
        telemetry.addData("Average Value =", "%5.2f", statTrackerTest.getAverage());
        telemetry.addData("Count Value =", "%d", statTrackerTest.getCount());
        telemetry.addData("Total Value =", "%5.2f", statTrackerTest.getSum());

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

        //Lets the user see the data called above rather than having it disappear almost instantly
        sleep(10000);
    }
}
