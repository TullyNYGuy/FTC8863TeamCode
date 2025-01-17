package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.Lift;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDLift;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDExtensionArmIntakeController;
import org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib.ITDLiftBucketArmBucketGateController;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Lift Positions", group = "Test")
//@Disabled
public class ITDTestLiftPositions extends LinearOpMode {

    // Put your variable declarations here
    public ITDLift lift;
    public ITDLiftBucketArmBucketGateController liftBucketArmBucketGateController;
    public DataLogging log;

    @Override
    public void runOpMode() {


        // Put your initializations here
        lift = new ITDLift(hardwareMap, telemetry);
        liftBucketArmBucketGateController = new ITDLiftBucketArmBucketGateController(hardwareMap, telemetry);
        lift.setController(liftBucketArmBucketGateController);

        log = new DataLogging("LiftPositionTest");
        lift.setDataLog(log);
        lift.enableDataLogging();


        lift.reset();
        while (!lift.isResetComplete()) {
            lift.update();
            lift.displayState(telemetry);
            lift.displayPosition(telemetry);
            telemetry.update();
        }
        lift.displayState(telemetry);
        lift.displayPosition(telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        while (opModeIsActive()) {
            // Put your calls that need to run in a loop here
            lift.update();

            if (gamepad1.y) {
                lift.initPosition();
            }

            if (gamepad1.b) {
                lift.transferPosition();
            }

            if (gamepad1.x) {
                lift.readyToDeliverPosition();
            }

            if (gamepad1.a) {
            }

            if (gamepad1.dpad_up) {
            }

            lift.displayState(telemetry);
            lift.displayPosition(telemetry);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }
        log.closeDataLog();

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
