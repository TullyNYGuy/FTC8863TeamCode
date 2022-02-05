package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.ArmConstants;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.ArmMotor;
import org.firstinspires.ftc.teamcode.ArmTuning.Opmodes.ArmTuningFindKgManual;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LimitPosition;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MovementLimit;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Limit Position", group = "Arm Tuning")
@Disabled
public class TestLimitPosition extends LinearOpMode {

    // Put your variable declarations her
    ArmMotor armMotor;
    LimitPosition limitPositionAtVertical;

    @Override
    public void runOpMode() {


        // Put your initializations here
        armMotor = new ArmMotor(hardwareMap, telemetry);
        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        limitPositionAtVertical = new LimitPosition(ArmConstants.VERTICAL_POSITION, MovementLimit.Direction.LIMIT_INCREASING_POSITIONS, "Vertical limit" );

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        telemetry.addData(">", "Press Stop to end test.");
        telemetry.update();

        while (opModeIsActive()) {
            if(!limitPositionAtVertical.isLimitReached(armMotor.getPosition(AngleUnit.DEGREES))) {
                armMotor.holdAtVertical();
                break;
            }
            idle();
        }

        while (opModeIsActive()){
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
