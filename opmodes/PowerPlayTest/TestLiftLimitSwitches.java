package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863Interface;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanismGenericMotor;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test PP Lift Switches", group = "Test")
//@Disabled
public class TestLiftLimitSwitches extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanismGenericMotor lift;
    private DcMotor8863Interface liftMotor;

    @Override
    public void runOpMode() {
        // create the motor for the lift
        liftMotor = new DcMotor8863(PowerPlayRobot.HardwareName.LIFT_MOTOR.hwName, hardwareMap, telemetry);
        liftMotor.setMotorType(DcMotor8863.MotorType.GOBILDA_1150);


        // Put your initializations here
        lift = new ExtensionRetractionMechanismGenericMotor(hardwareMap, telemetry,
                "lift",
                PowerPlayRobot.HardwareName.LIFT_LIMIT_SWITCH_EXTENSION.hwName,
                PowerPlayRobot.HardwareName.LIFT_LIMIT_SWITCH_RETRACTION.hwName,
                liftMotor,
                5.713);

        lift.forwardMotorDirection();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {

            // Put your calls that need to run in a loop here

            lift.testLimitSwitches();
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
