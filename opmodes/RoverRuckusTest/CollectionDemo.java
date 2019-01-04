package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorGB;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "AAA Collection Demo", group = "Test")
//@Disabled
public class CollectionDemo extends LinearOpMode {

    // Put your variable declarations here
    public CollectorGB collector;
    public DataLogging logfile;
    public CollectorGB.CollectorState collectorState;

    @Override
    public void runOpMode() {

        // Put your initializations here
        logfile = new DataLogging("collectorTest", telemetry);
        collector = new CollectorGB(hardwareMap, telemetry);
        collector.initialize();
        collector.setDataLogging(logfile);
        collector.setLoggingOn();
        collector.setDebugOn();
        collector.setDesiredMineralColorToSilver();
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        //collector.testSetup();
        //collector.testMovements(telemetry);

        collector.turnCollectorOn();

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here

            collectorState = collector.update();

            telemetry.addLine("Collecting Silver Minerals");
            telemetry.addData(">", "Press Stop to end test." );
            telemetry.addData("Collector State = ", collectorState.toString());
            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        collector.shutdown();
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
