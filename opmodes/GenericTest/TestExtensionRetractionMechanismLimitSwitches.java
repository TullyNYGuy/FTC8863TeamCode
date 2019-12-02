package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Extension Retraction Limit Switches", group = "Test")
//@Disabled
public class TestExtensionRetractionMechanismLimitSwitches extends LinearOpMode {

    // Put your variable declarations here
    public ExtensionRetractionMechanism extensionRetractionMechanism;
    public double spoolDiameter = 1.25 *25.4;

    @Override
    public void runOpMode() {


        // Put your initializations here
        extensionRetractionMechanism = new ExtensionRetractionMechanism(hardwareMap,telemetry,"extensionRetraction",
                "extensionLimitSwitch", "retractionLimitSwitch", "extensionRetractionMotor",
                DcMotor8863.MotorType.ANDYMARK_40, spoolDiameter * Math.PI);
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while(opModeIsActive()) {

            // Put your calls that need to run in a loop here
            extensionRetractionMechanism.testLimitSwitches();
            telemetry.update();
            
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
