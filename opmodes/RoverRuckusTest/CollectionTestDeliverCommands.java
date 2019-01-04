package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorGB;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Collection test deliver commands", group = "Test")
//@Disabled
public class CollectionTestDeliverCommands extends LinearOpMode {

    // Put your variable declarations here
    public CollectorGB collector;
    public DataLogging logfile;
    public CollectorGB.CollectorState collectorState;
    public ElapsedTime timer;

    @Override
    public void runOpMode() {


        // Put your initializations here

        timer = new ElapsedTime();
        logfile = new DataLogging("collectorTest", telemetry);
        collector = new CollectorGB(hardwareMap, telemetry);
        collector.initialize();
        collector.setDataLogging(logfile);
        collector.setLoggingOn();
        collector.setDebugOn();
        collector.setDesiredMineralColorToGold();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        timer.reset();
        collector.deliverMineralsOn();
        while (opModeIsActive() && timer.milliseconds() < 4000) {

            collectorState = collector.update();

            telemetry.addData(">", "Press Stop to end test.");
            collector.displayCollectorState();
            collector.displayCollectorCommand();
            telemetry.update();

            idle();
        }
        sleep(1000);

        timer.reset();
        collector.deliverMineralsOff();
        while (opModeIsActive()&& timer.milliseconds() < 1000) {
            collectorState = collector.update();
            telemetry.addData(">", "Press Stop to end test.");
            collector.displayCollectorState();
            collector.displayCollectorCommand();
            telemetry.update();

            idle();
        }
        sleep(1000);

        timer.reset();
        collector.fixTransferJam();
        while (opModeIsActive() && timer.milliseconds() < 4000) {
            collectorState = collector.update();
            telemetry.addData(">", "Press Stop to end test.");
            collector.displayCollectorState();
            collector.displayCollectorCommand();
            telemetry.update();

            idle();
        }
        sleep(1000);

        timer.reset();
        collector.deliverMineralsOff();
        while (opModeIsActive() && timer.milliseconds() < 1000) {
            collectorState = collector.update();
            telemetry.addData(">", "Press Stop to end test.");
            collector.displayCollectorState();
            collector.displayCollectorCommand();
            telemetry.update();

            idle();
        }
        sleep(2000);
    }
}
