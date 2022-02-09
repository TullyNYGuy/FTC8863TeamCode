package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Lift Go To Multiple Positions", group = "Test")
//@Disabled
public class TestLiftGoToPositions extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanism lift;
    DataLogging log;

    @Override
    public void runOpMode() {


        // Put your initializations here

        log = new DataLogging("LiftLog");
        lift = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                "Lift",
                "ExtensionLimitSwitch",
                "RetractionLimitSwitch",
                "LiftMotor",
                DcMotor8863.MotorType.GOBILDA_435,
                4.517);
        lift.reverseMotorDirection();


        //lift.reverseMotorDirection();
        lift.setResetTimerLimitInmSec(25000);
        lift.setExtensionPower(1.0);
        lift.setExtensionPositionInMechanismUnits(18.0);
        lift.setRetractionPower(-0.5);
        lift.setRetractionPositionInMechanismUnits(2.0);
        lift.setDataLog(log);
        lift.enableDataLogging();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        double[] positions = new double[]{25.0, 3.0, 15.0};
        lift.testGoToPositions(this, positions, 0.3);

        // after the retraction is complete, loop so the user can see the result
        while (opModeIsActive()){
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}