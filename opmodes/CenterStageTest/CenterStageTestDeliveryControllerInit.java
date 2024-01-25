package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageDeliveryController;
import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageLift;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Center Stage Test Delivery Controller Init", group = "Test")
//@Disabled
public class CenterStageTestDeliveryControllerInit extends LinearOpMode {

    // Put your variable declarations here
    CenterStageDeliveryController deliveryController;
    DataLogging log;

    @Override
    public void runOpMode() {


        // Put your initializations here
        log = new DataLogging("LiftLog");
        deliveryController = new CenterStageDeliveryController(hardwareMap, telemetry);

        deliveryController.setDataLog(log);
        deliveryController.enableDataLogging();

        deliveryController.init(null);
        while (!deliveryController.isInitComplete()) {
            deliveryController.update();
            telemetry.addData("state = ", deliveryController.getState().toString());
            if (deliveryController.isInitComplete()) {
                telemetry.addData("deliveryController init = ", "complete");
            } else {
                telemetry.addData("deliveryController init = ", "NOT complete");
            }
            telemetry.update();
        }

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop


        // after the reset is complete just loop so the user can see the state
        while (opModeIsActive()) {
            deliveryController.update();

            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
