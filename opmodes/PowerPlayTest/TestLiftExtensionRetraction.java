package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test PP Lift ExtensionRetraction Cycle", group = "Test")
//@Disabled
public class TestLiftExtensionRetraction extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanism lift;

    @Override
    public void runOpMode() {


        // Put your initializations here
        lift = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                "Arm",
                "leftLiftExtensionLimitSwitch",
                "leftLiftRetractionLimitSwitch",
                "leftLiftMotor",
                DcMotor8863.MotorType.GOBILDA_435,
                5.713);

      //  lift.reverseMotorDirection();
        lift.setExtensionPower(.75);
        lift.setExtensionPositionInMechanismUnits(35);
        lift.setRetractionPower(.75);
        lift.setRetractionPositionInMechanismUnits(2);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        lift.testCycleFullExtensionRetraction(this,2,3000);

        while (opModeIsActive()) {
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
