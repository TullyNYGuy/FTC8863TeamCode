package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Go To Positions", group = "Test")
//@Disabled
public class TestGoToPositions extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanism lift;

    @Override
    public void runOpMode() {
        double[] positionsArray = new double[]{15,10,12,18 };

        // Put your initializations here
        lift = new ExtensionRetractionMechanism(hardwareMap,
                telemetry,
                "lift",
                "ExtensionLimitSwitch",
                "RetractionLimitSwitch",
                "LiftMotor",
                DcMotor8863.MotorType.ANDYMARK_20_ORBITAL,
                4.1223);

      //  lift.reverseMotorDirection();
        lift.setExtensionPower(1);
        lift.setExtensionPositionInMechanismUnits(20);
        lift.setRetractionPower(1);
        lift.setRetractionPositionInMechanismUnits(1);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        lift.testGoToPositions(this,positionsArray,.5);

        while (opModeIsActive()) {
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
