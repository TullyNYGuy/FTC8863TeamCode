package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test PP Lift Extension", group = "Test")
//@Disabled
public class TestLiftExtension extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanism arm;
    DataLogging log;

    @Override
    public void runOpMode() {


        // Put your initializations here

        log = new DataLogging("ArmLog");
        arm = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                "Arm",
                "leftLiftExtensionLimitSwitch",
                "leftLiftRetractionLimitSwitch",
                "leftLiftMotor",
                DcMotor8863.MotorType.GOBILDA_435,
                5.713);
        //arm.reverseMotorDirection();

        arm.setResetTimerLimitInmSec(5000);
        arm.setExtensionPower(1.0);
        arm.setExtensionPositionInMechanismUnits(35.0);
        arm.setDataLog(log);
        arm.enableDataLogging();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        arm.testExtension(this);

        // after the extension is complete, loop so the user can see the result
        while (opModeIsActive()){
            telemetry.addData("state = ", arm.getExtensionRetractionState().toString());
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
