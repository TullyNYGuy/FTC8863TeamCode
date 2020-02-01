package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Skystone Limit Switches", group = "Test")
//@Disabled
public class TestLimitSwitches extends LinearOpMode {

    // Put your variable declarations here
    public ExtensionRetractionMechanism extensionRetractionMechanismLeft;
    public ExtensionRetractionMechanism extensionRetractionMechanismRight;
    //public ExtensionRetractionMechanism extensionRetractionMechanismArm;
    public double spoolDiameter = 1.25 * 25.4;

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionRetractionMechanismLeft = new ExtensionRetractionMechanism(hardwareMap, telemetry, "liftLeft",
                "extensionLimitSwitchLiftLeft", "retractionLimitSwitchLiftLeft", "liftMotorLeft",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);

        extensionRetractionMechanismRight = new ExtensionRetractionMechanism(hardwareMap, telemetry, "liftRight",
                "extensionLimitSwitchLiftRight", "retractionLimitSwitchLiftRight", "liftMotorRight",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);

        //extensionRetractionMechanismArm = new ExtensionRetractionMechanism(hardwareMap,telemetry,"extensionArm",
        //        "extensionLimitSwitchArm", "retractionLimitSwitchArm", "extensionArmMotor",
        //        DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);

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
            //extensionRetractionMechanismArm.testLimitSwitches();
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
