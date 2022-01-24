package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Lift Using Joystick", group = "Test")
//@Disabled
public class TestLiftUsingJoystick extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanism lift;
    DataLogging log;
    double moveToPostionPower = 0.3;
    double joystickY = 0;
    ExtensionRetractionMechanism.ExtensionRetractionStates extensionRetractionState;

    @Override
    public void runOpMode() {


        // Put your initializations here

        log = new DataLogging("LiftLog");
        lift = new ExtensionRetractionMechanism(hardwareMap, telemetry,
                "Lift",
                "ExtensionLimitSwitch",
                "RetractionLimitSwitch",
                "LiftMotor",
                DcMotor8863.MotorType.GOBILDA_435,
                4.517);
        lift.reverseMotorDirection();


        //lift.reverseMotorDirection();
        lift.setResetTimerLimitInmSec(25000);
        lift.setExtensionPower(1.0);
        lift.setExtensionPositionInMechanismUnits(19.0);
        lift.setRetractionPower(-0.5);
        lift.setRetractionPositionInMechanismUnits(3.0);
        lift.setDataLog(log);
        lift.enableDataLogging();

        lift.init();
        while (!lift.isInitComplete()) {
            lift.update();
            extensionRetractionState = lift.getExtensionRetractionState();
            telemetry.addData("state = ", extensionRetractionState.toString());
            telemetry.update();
            idle();
        }
        telemetry.addData("init complete", "!");

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()){
            lift.update();
            if (gamepad1.a) {

                lift.goToPosition(4.0, moveToPostionPower);
            }
            if (gamepad1.y) {
                lift.goToPosition(18.0, moveToPostionPower);
            }
            if (gamepad1.x) {
                lift.goToPosition(12.0, moveToPostionPower);
            }
            if (gamepad1.b) {
                lift.goToPosition(5.0, moveToPostionPower);
            }
            joystickY = -gamepad1.right_stick_y;
            if (joystickY < -0.1 || joystickY > 0.1) {
                lift.setPowerUsingJoystick(joystickY);
            } else {
                lift.setPowerUsingJoystick(0.0);
            }
            lift.displayCommand();
            lift.displayState();
            lift.displayPower();
            telemetry.addData("position = ", lift.getPosition());
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
