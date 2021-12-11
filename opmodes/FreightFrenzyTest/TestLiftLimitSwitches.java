package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import android.graphics.HardwareRenderer;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Lift Limit Switches", group = "Test")
//@Disabled
public class TestLiftLimitSwitches extends LinearOpMode {

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
                DcMotor8863.MotorType.ANDYMARK_40,
                3.9375);


        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop


        while (opModeIsActive()) {
            lift.testLimitSwitches();
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
