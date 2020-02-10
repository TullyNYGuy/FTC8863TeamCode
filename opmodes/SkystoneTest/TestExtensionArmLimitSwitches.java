package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Extension Arm Limit Switches", group = "Test")
//@Disabled
public class TestExtensionArmLimitSwitches extends LinearOpMode {

    // Put your variable declarations here

    public ExtensionArm extensionArm;
    public double spoolDiameter = 2.75;

    @Override
    public void runOpMode() {

        extensionArm = new ExtensionArm(hardwareMap, telemetry, "extensionArm",
                "extensionLimitSwitchArm", "retractionLimitSwitchArm", "intakeMotorRight",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            telemetry.addData("arm", ":");
            extensionArm.testLimitSwitches();
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
