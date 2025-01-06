package org.firstinspires.ftc.teamcode.opmodes.IntoTheDeepTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "ITD Test Extension Arm Reset", group = "Test")
//@Disabled
public class ITDTestExtensionArmReset extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanism extensionArm;
    DataLogging log;

    @Override
    public void runOpMode() {


        // Put your initializations here
        log = new DataLogging("LiftLog");
        // Put your initializations here
        extensionArm = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                "ExtensionArm",
                "extensionArmExtensionLimitSwitch",
                "extensionArmRetractionLimitSwitch",
                "extensionArmMotor",
                DcMotor8863.MotorType.GOBILDA_1150,
                4.80);
        // This is for the blue alliance
        extensionArm.reverseMotorDirection();


        //extensionArm.reverseMotorDirection();
        extensionArm.setResetTimerLimitInmSec(5000);
        extensionArm.setExtensionPower(0.2);
        extensionArm.setExtensionPositionInMechanismUnits(20.0);
        extensionArm.setRetractionPower(-0.2);
        extensionArm.setRetractionPositionInMechanismUnits(0.0);
        extensionArm.setDataLog(log);
        extensionArm.enableDataLogging();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        extensionArm.testReset(this);

        // after the reset is complete just loop so the user can see the state
        while (opModeIsActive()){
            telemetry.addData("state = ", extensionArm.getExtensionRetractionState().toString());
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
