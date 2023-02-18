package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863Interface;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanismGenericMotor;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test PP Lift Retraction", group = "Test")
//@Disabled
public class TestLiftRetraction extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanismGenericMotor lift;
    private DcMotor8863Interface liftMotor;
    DataLogging log;

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

        lift.setResetTimerLimitInmSec(25000);
        lift.setExtensionPower(0.1);
        lift.setExtensionPositionInMechanismUnits(15);
        lift.setRetractionPower(-0.75);
        lift.setDataLog(log);
        lift.enableDataLogging();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        lift.testRetraction(this);

        // after the retraction is complete, loop so the user can see the result
        while (opModeIsActive()){
            telemetry.addData("state = ", lift.getExtensionRetractionState().toString());
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
