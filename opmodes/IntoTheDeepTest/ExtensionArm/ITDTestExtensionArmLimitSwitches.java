package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest.ExtensionArm;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Extension Arm Switches", group = "Test")
@Disabled
public class ITDTestExtensionArmLimitSwitches extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanism extensionArm;

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionArm = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                "ExtensionArm",
                "extensionArmExtensionLimitSwitch",
                "extensionArmRetractionLimitSwitch",
                "extensionArmMotor",
                DcMotor8863.MotorType.GOBILDA_1150,
                4.80);
        // This is for the blue alliance
        //extensionArm.reverseMotorDirection();

        // this is for the red alliance
        //extensionArm.reverseMotorDirection();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here

            extensionArm.testLimitSwitches();
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
