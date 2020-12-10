package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Extension Retraction Limit Switches", group = "Test")
@Disabled
public class TestExtensionRetractionMechanismLimitSwitches extends LinearOpMode {

    // Put your variable declarations here
    public ExtensionRetractionMechanism extensionRetractionMechanismLeft;
    public ExtensionRetractionMechanism extensionRetractionMechanismRight;
    public ExtensionRetractionMechanism extensionRetractionMechanismArm;
    public double spoolDiameter = 1.25 * 25.4;

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionRetractionMechanismLeft = new ExtensionRetractionMechanism(hardwareMap, telemetry, "extensionRetractionLeft",
                "extensionLimitSwitchLeft", "retractionLimitSwitchLeft", "extensionRetractionMotorLeft",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);

        extensionRetractionMechanismRight = new ExtensionRetractionMechanism(hardwareMap, telemetry, "extensionRetractionRight",
                "extensionLimitSwitchRight", "retractionLimitSwitchRight", "extensionRetractionMotorRight",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);

        extensionRetractionMechanismArm = new ExtensionRetractionMechanism(hardwareMap, telemetry, "extensionRetractionArm",
                "extensionLimitSwitchArm", "retractionLimitSwitchArm", "extensionRetractionMotorArm",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here
            telemetry.addData("left", ":");
            extensionRetractionMechanismLeft.testLimitSwitches();
            telemetry.addData("right", ":");
            extensionRetractionMechanismRight.testLimitSwitches();
            telemetry.addData("arm", ":");
            extensionRetractionMechanismArm.testLimitSwitches();
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
