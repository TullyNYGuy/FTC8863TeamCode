package org.firstinspires.ftc.teamcode.opmodes.SkystoneDiagnostics;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.DualLift;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.Lift;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Dual Lift Limit Switches", group = "Diagnostics")
@Disabled
public class TestDualLiftLimitSwitches extends LinearOpMode {

    // Put your variable declarations here
    public DualLift lift;

    @Override
    public void runOpMode() {


        // Put your initializations here
        lift = new DualLift(hardwareMap,
                SkystoneRobot.HardwareName.LIFT_RIGHT_NAME.hwName,
                SkystoneRobot.HardwareName.LIFT_RIGHT_MOTOR.hwName,
                SkystoneRobot.HardwareName.LIFT_RIGHT_EXTENSION_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_RIGHT_RETRACTION_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_NAME.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_MOTOR.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_EXTENSION_SWITCH.hwName,
                SkystoneRobot.HardwareName.LIFT_LEFT_RETRACTION_SWITCH.hwName,
                telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            lift.testLimitSwitches(this);
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
