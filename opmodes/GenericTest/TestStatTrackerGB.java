package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.StatTrackerGB;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Stat Tracker", group = "Test")
@Disabled
public class TestStatTrackerGB extends LinearOpMode {

    // Put your variable declarations here

    // This is a timer that will run a collection of stats over a given number of seconds.
    ElapsedTime programTimer;

    // This is a timer that will be used to time the interval between loop executions.
    ElapsedTime loopTimer;

    // an object that collects and processes a stream of data
    StatTrackerGB statTracker;

    // How long to run the collection (in mSec)
    double programTimeLimit = 5000;

    @Override
    public void runOpMode() {


        // Put your initializations here

        // Instantiate the  objects
        programTimer = new ElapsedTime();
        loopTimer = new ElapsedTime();
        statTracker = new StatTrackerGB();
        
        // Wait for the user to press the start button on the phone
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        programTimer.reset();
        loopTimer.reset();

        //****************************************************************
        // first test processing the stream of values as they are generated
        // do the calculations for each value in a set of small methods
        //*****************************************************************

        // the loop quits when the user presses the stop button on the phone or when the time limit
        // has expired
        while(opModeIsActive() && programTimer.milliseconds() < programTimeLimit) {

            // Put your calls that need to run in a loop here

            // tracking the time between loop executions
            statTracker.updateStats(loopTimer.milliseconds());
            // reset the timer to 0
            loopTimer.reset();

            // Display
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();

            // Let the background tasks run
            idle();
        }

        // now that the loop has finished and data collection is done, display the results on the
        // driver station phone
        telemetry.addData("Minimum loop time = ", "%5.2f", statTracker.getMinimum());
        telemetry.addData("Maximum loop time = ", "%5.2f", statTracker.getMaximum());
        telemetry.addData("Average loop time = ", "%5.2f", statTracker.getAverage());
        telemetry.addData("Total loop time = ", "%5.2f", statTracker.getSum());
        telemetry.addData("Number of loops = ", "%5.2f", statTracker.getCount());
        telemetry.update();

        // give the user 5 seconds to look at the results displayed on the phone
        sleep(5000);

        //****************************************************************
        // next test processing the value on the fly as each value is generated
        // do the calculations in one big method to avoid the overhead of calling
        // small methods
        //*****************************************************************

        programTimer.reset();

        // the loop quits when the user presses the stop button on the phone or when the time limit
        // has expired
        while(opModeIsActive() && programTimer.milliseconds() < programTimeLimit) {

            // Put your calls that need to run in a loop here

            // tracking the time between loop executions
            statTracker.updateStatsUnified(loopTimer.milliseconds());
            // reset the timer to 0
            loopTimer.reset();

            // Display
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();

            // Let the background tasks run
            idle();
        }

        // now that the loop has finished and data collection is done, display the results on the
        // driver station phone
        telemetry.addData("Minimum loop time = ", "%5.2f", statTracker.getMinimum());
        telemetry.addData("Maximum loop time = ", "%5.2f", statTracker.getMaximum());
        telemetry.addData("Average loop time = ", "%5.2f", statTracker.getAverage());
        telemetry.addData("Total loop time = ", "%5.2f", statTracker.getSum());
        telemetry.addData("Number of loops = ", "%5.2f", statTracker.getCount());
        telemetry.update();

        // give the user 5 seconds to look at the results displayed on the phone
        sleep(5000);

        //****************************************************************
        // last test processing the stream of values after they are all known
        // just store each value and then process them all at once.
        // Will this have less impact on the loop?
        //*****************************************************************

        programTimer.reset();

        // the loop quits when the user presses the stop button on the phone or when the time limit
        // has expired
        while(opModeIsActive() && programTimer.milliseconds() < programTimeLimit) {

            // Put your calls that need to run in a loop here

            // tracking the time between loop executions
            statTracker.updateList(loopTimer.milliseconds());
            // reset the timer to 0
            loopTimer.reset();

            // Display
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.update();

            // Let the background tasks run
            idle();
        }

        // now that the loop has finished and data collection is done, display the results on the
        // driver station phone
        telemetry.addData("Minimum loop time = ", "%5.2f", statTracker.getMinimum());
        telemetry.addData("Maximum loop time = ", "%5.2f", statTracker.getMaximum());
        telemetry.addData("Average loop time = ", "%5.2f", statTracker.getAverage());
        telemetry.addData("Total loop time = ", "%5.2f", statTracker.getSum());
        telemetry.addData("Number of loops = ", "%5.2f", statTracker.getCount());
        telemetry.update();

        // give the user 5 seconds to look at the results displayed on the phone
        sleep(5000);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
