package org.firstinspires.ftc.teamcode.opmodes.RoverRuckusTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.Collector;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "AAA Collection Delivery Demo", group = "Test")
@Disabled
public class CollectionDeliveryTest extends LinearOpMode {

    // Put your variable declarations here
    public Collector collector;
    public DataLogging logfile;
    public Collector.CollectorState collectorState;
    public GamepadButtonMultiPush gamepad1a;
    public GamepadButtonMultiPush gamepad1b;
    public GamepadButtonMultiPush gamepad1y;
    public GamepadButtonMultiPush gamepad1x;

    @Override
    public void runOpMode() {

        // Put your initializations here
        logfile = new DataLogging("collectorTest", telemetry);
        collector = new Collector(hardwareMap, telemetry);
        collector.init();
        collector.setDataLog(logfile);
        collector.enableDataLogging();
        collector.setDebugOn();
        collector.setDesiredMineralColorToGold();
        // collector.forceStoreOnly();

        gamepad1a = new GamepadButtonMultiPush(1);
        gamepad1b = new GamepadButtonMultiPush(1);
        gamepad1y = new GamepadButtonMultiPush(1);
        gamepad1x = new GamepadButtonMultiPush(1);


        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        //collector.testSetup();
        //collector.testMovements(telemetry);

        collector.turnCollectorOn();

        while (opModeIsActive()) {
            if (gamepad1a.buttonPress(gamepad1.a)) {
                collector.deliverMineralsOn();
            }

            if (gamepad1b.buttonPress(gamepad1.b)) {
                collector.deliverMineralsComplete();
            }

            if (gamepad1y.buttonPress(gamepad1.y)) {
            }

            if (gamepad1x.buttonPress(gamepad1.x)) {
            }

            // Put your calls that need to run in a loop here

            collectorState = collector.update();

            telemetry.addLine("Collecting Silver Minerals");
            telemetry.addData(">", "Press Stop to end test.");
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
