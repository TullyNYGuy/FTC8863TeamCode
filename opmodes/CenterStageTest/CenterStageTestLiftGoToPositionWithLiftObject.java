package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.CenterStageLib.CenterStageLift;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Center Stage Test Lift Go To Positions with Object", group = "Test")
//@Disabled
public class CenterStageTestLiftGoToPositionWithLiftObject extends LinearOpMode {

    // Put your variable declarations here
    CenterStageLift lift;
    DataLogging log;

    @Override
    public void runOpMode() {


        // Put your initializations here
        log = new DataLogging("LiftLog");
        lift = new CenterStageLift(hardwareMap,telemetry);

        lift.setDataLog(log);
        lift.enableDataLogging();

        lift.init(null);
        while(opModeIsActive() && !lift.isInitComplete()) {
            lift.update();
            telemetry.addData("state = ", lift.getLiftState().toString());
            if (lift.isInitComplete()) {
                telemetry.addData("lift init = ", "complete");
            } else {
                telemetry.addData("lift init = ", "NOT complete");
            }
            telemetry.update();
        }

        // Wait for the start button
        telemetry.addData(">", "Init complete");
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        lift.moveToLow();
        while (opModeIsActive() && !lift.isPositionReached()){
            lift.update();
            telemetry.addData("moving to low position", "!");
            telemetry.addData("state = ", lift.getLiftState().toString());
            telemetry.update();
            idle();
        }

        lift.moveToIntake();
        while (opModeIsActive() && !lift.isPositionReached()){
            lift.update();
            telemetry.addData("moving to intake position", "!");
            telemetry.addData("state = ", lift.getLiftState().toString());
            telemetry.update();
            idle();
        }

        // after the reset is complete just loop so the user can see the state
        while (opModeIsActive()){
            lift.update();
            telemetry.addData("state = ", lift.getLiftState().toString());
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
