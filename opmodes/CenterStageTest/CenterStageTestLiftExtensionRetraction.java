package org.firstinspires.ftc.teamcode.opmodes.CenterStageTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Center Stage Test Lift ExtensionRetraction Cycle", group = "Test")
//@Disabled
public class CenterStageTestLiftExtensionRetraction extends LinearOpMode {

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
                "liftMotor",
                DcMotor8863.MotorType.GOBILDA_435,
                4.75);

      //  lift.reverseMotorDirection();
        lift.setResetTimerLimitInmSec(25000);
        lift.setExtensionPower(1.0);
        lift.setExtensionPositionInMechanismUnits(16.0);
        lift.setRetractionPower(-1.0);
        lift.setRetractionPositionInMechanismUnits(0.5);
        lift.setDataLog(log);
        lift.enableDataLogging();
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        lift.testCycleFullExtensionRetraction(this,5,10000);

        while (opModeIsActive()) {
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
