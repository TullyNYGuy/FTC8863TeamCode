package org.firstinspires.ftc.teamcode.ArmTuning.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.SampleArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Set up positions", group = "Arm Tuning")
//@Disabled
public class ArmTuningSetupPositions extends LinearOpMode {

    // Put your variable declarations her
    SampleArm sampleArm;
    double angleWhenArmIsHorizontal = 0;
    double angleWhenArmIsVertical = 0;

    @Override
    public void runOpMode() {

        // Put your initializations here
        sampleArm = new SampleArm(hardwareMap, telemetry);
        sampleArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // First the arm starting position needs to be established. Once it is the encoder is reset
        // to 0.
        telemetry.addData("Locate the arm in its normal starting position. ", "Press A when ready to proceed");
        telemetry.update();
        while (opModeIsActive() && !gamepad1.a) {
            idle();
        }
        // reset the encoder to 0 since the arm is at its starting position
        if (gamepad1.a) {
            sampleArm.resetEncoder();
            //eat the rest of the gamepad a press
            while (opModeIsActive() && gamepad1.a) {
                idle();
            }
        }

        // Next the arm position needs to be determined when the arm is horizontal to the ground.

        while (opModeIsActive() && !gamepad1.a) {
            telemetry.addData("Move the arm to the closest horizontal position from the start position. ", "Press A when completed");
            telemetry.addData("Arm position (degrees) = ", sampleArm.getPosition(AngleUnit.DEGREES));
            telemetry.addData("Arm encoder count = ", sampleArm.getCounts());
            telemetry.update();
            idle();
        }
        // The arm is at horizontal relative to the ground. Get the position.
        if (gamepad1.a) {
            angleWhenArmIsHorizontal = sampleArm.getPosition(AngleUnit.DEGREES);
            while (opModeIsActive() && gamepad1.a) {
                //eat the rest of the gamepad a press
                idle();
            }
        }


        // The whole arm system assumes that when the arm moves from the start position it moves
        // in a positive direction. Check if this is actually the case or if they need to reverse
        // the motor.
        if (angleWhenArmIsHorizontal < 0) {
            telemetry.addData("Moving the arm from its initial position should result in positive encoder ticks, not negative","!");
            telemetry.addData("You must set the direction of the motor using setDirection() to opposite of what it is now", "!");
            telemetry.addData("You are done here. Go do that", "!");
            telemetry.update();
            // loop with no way out but hitting stop
            while (opModeIsActive()) {
                idle();
            }
        }

        //Finally the arm position needs to be determined when the arm is vertical to the ground.
        while (opModeIsActive() && !gamepad1.a) {
            telemetry.addData("Move the arm to the vertical position. ", "Press A when completed");
            telemetry.addData("Arm position (degrees) = ", sampleArm.getPosition(AngleUnit.DEGREES));
            telemetry.addData("Arm encoder count = ", sampleArm.getCounts());
            telemetry.update();
            idle();
        }
        // The arm is at vertical relative to the ground. Get the position.
        if (gamepad1.a) {
            angleWhenArmIsVertical = sampleArm.getPosition(AngleUnit.DEGREES);
            //eat the rest of the gamepad a press
            while (opModeIsActive() && gamepad1.a) {
                idle();
            }
        }


        // now display the values for the user so they can write them down and enter them in the
        // ArmConstants file
        telemetry.addData("Arm angle when arm is horizontal = ", angleWhenArmIsHorizontal);
        telemetry.addData("Arm angle when arm is vertical = ", angleWhenArmIsVertical);
        telemetry.addData("Press Stop when you have entered these values into the arm constants file", ".");
        telemetry.update();
        while (opModeIsActive()) {
            idle();
        }
    }
}
