package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Lift Retraction", group = "Test")
//@Disabled
public class TestLiftRetraction extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanism lift;

    @Override
    public void runOpMode() {


        // Put your initializations here
        lift = new ExtensionRetractionMechanism(hardwareMap,
                telemetry,
                "lift",
                "ExtensionLimitSwitch",
                "RetractionLimitSwitch",
                "LiftMotor",
                DcMotor8863.MotorType.ANDYMARK_20_ORBITAL,
                3.926);

        //lift.reverseMotorDirection();
        lift.setRetractionPower(.25);
        lift.setRetractionPositionInMechanismUnits(0.5);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        lift.testRetraction(this);

        while (opModeIsActive()) {
            telemetry.addData("Retraction Complete", "!");
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
