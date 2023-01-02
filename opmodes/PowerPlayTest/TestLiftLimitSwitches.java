package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test PP Lift Switches", group = "Test")
//@Disabled
public class TestLiftLimitSwitches extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanism arm;

    @Override
    public void runOpMode() {


        // Put your initializations here
        arm = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                "Arm",
                "leftLiftExtensionLimitSwitch",
                "leftLiftRetractionLimitSwitch",
                "leftLiftMotor",
                DcMotor8863.MotorType.GOBILDA_435,
                5.713);
        // This is for the blue alliance
        //arm.reverseMotorDirection();

        // this is for the red alliance
        //arm.reverseMotorDirection();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here

            arm.testLimitSwitches();
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}