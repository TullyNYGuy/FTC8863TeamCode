package org.firstinspires.ftc.teamcode.opmodes.RoverRuckus;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.CollectorGB;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Collection test GB", group = "Test")
//@Disabled
public class CollectionTestGB extends LinearOpMode {

    // Put your variable declarations here
    public CollectorGB collector;

    @Override
    public void runOpMode() {


        // Put your initializations here
        collector = new CollectorGB(hardwareMap, telemetry);
        collector.initialize();
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        collector.testSetup();
        //collector.testMovements(telemetry);

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here
            //collector.testMineralDetection(telemetry);
            //collector.testReadColorSensor(telemetry);
            //collector.testMineralColorDetection(telemetry);
            testActions();

            telemetry.addData(">", "Press Stop to end test." );

            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }

    public void testActions() {
        // answer should be stored
        collector.testActionToTake(CollectorGB.MineralColor.GOLD, CollectorGB.MineralColor.GOLD, 0, telemetry);
        // answer should be hold
        collector.testActionToTake(CollectorGB.MineralColor.GOLD, CollectorGB.MineralColor.GOLD, 1, telemetry);
        // answer should be eject
        collector.testActionToTake(CollectorGB.MineralColor.GOLD, CollectorGB.MineralColor.SILVER, 0, telemetry);
        // answer should be eject
        collector.testActionToTake(CollectorGB.MineralColor.GOLD, CollectorGB.MineralColor.SILVER, 1, telemetry);
        // answer should be stored
        collector.testActionToTake(CollectorGB.MineralColor.SILVER, CollectorGB.MineralColor.SILVER, 0, telemetry);
        // answer should be hold
        collector.testActionToTake(CollectorGB.MineralColor.SILVER, CollectorGB.MineralColor.SILVER, 1, telemetry);
        // answer should be eject
        collector.testActionToTake(CollectorGB.MineralColor.SILVER, CollectorGB.MineralColor.GOLD, 0, telemetry);
        // answer should be eject
        collector.testActionToTake(CollectorGB.MineralColor.SILVER, CollectorGB.MineralColor.GOLD, 1, telemetry);
    }
}
