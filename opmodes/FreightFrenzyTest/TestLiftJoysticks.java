package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Lift Joysticks", group = "Test")
@Disabled
public class TestLiftJoysticks extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanism lift;

    @Override
    public void runOpMode() {
        double[] positionsArray = new double[]{15, 10, 12, 18};

        // Put your initializations here
        lift = new ExtensionRetractionMechanism(hardwareMap,
                telemetry,
                "lift",
                "ExtensionLimitSwitch",
                "RetractionLimitSwitch",
                "LiftMotor",
                DcMotor8863.MotorType.ANDYMARK_20_ORBITAL,
                4.1223);

        //  lift.reverseMotorDirection();
        lift.setExtensionPower(1);
        lift.setExtensionPositionInMechanismUnits(20);
        lift.setRetractionPower(1);
        lift.setRetractionPositionInMechanismUnits(.5);
        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        lift.init();

        while (opModeIsActive() && !lift.isInitComplete()) {
            lift.update();
            idle();
        }

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            lift.update();
            if (gamepad1.x) {
                lift.goToPosition(5, .75);
            }
            if (gamepad1.y) {
                lift.goToPosition(10, .75);
            }
            if (gamepad1.b) {
                lift.goToPosition(15, .75);
            }
            if (gamepad1.a) {
                lift.goToPosition(20, .75);
            }
            if (gamepad1.right_stick_y != 0) {
                if (gamepad1.right_stick_y < .2 && gamepad1.right_stick_y > -.2) {
                    lift.setPowerUsingJoystick(0);
                } else {
                    lift.setPowerUsingJoystick(-gamepad1.right_stick_y);
                }
            }
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
