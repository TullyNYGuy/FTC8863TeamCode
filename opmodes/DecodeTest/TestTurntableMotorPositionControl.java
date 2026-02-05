package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeIMU;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeLimelight;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeTurntableMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Turntable Position Control", group = "Test")
//@Disabled
public class TestTurntableMotorPositionControl extends LinearOpMode {

    private enum Mode {
        FORCE_OFF_TARGET,
        SEEK_TARGET
    }

    Mode mode = Mode.SEEK_TARGET;

    // Put your variable declarations her
    DecodeTurntableMotor turntableMotor;
    DecodeLimelight limelight;
    DecodeIMU imu;

    @Override
    public void runOpMode() {
        double targetPositionToGoal = 0;
        final double TARGET_POSITION_TO_APRILTAG = 0;
        double targetPositionCenter = 0;
        double targetPositionCCW = 45;
        double targetPositionCW = -45;
        double targetPosition = 0;

        double positionError = 0;
        double actualPosition = 0;

        boolean aprilTagAcquired;

        // These debounce the buttons so that you only see a single press even if a button is held
        // down for a long time.
        Debouncer debouncedY = new Debouncer();
        Debouncer debouncedB = new Debouncer();
        Debouncer debouncedX = new Debouncer();
        Debouncer debouncedA = new Debouncer();
        Debouncer debouncedDpadUp = new Debouncer();
        Debouncer debouncedDpadDown = new Debouncer();
        Debouncer debouncedDpadLeft = new Debouncer();
        Debouncer debouncedDpadRight = new Debouncer();

        // Put your initializations here
        turntableMotor = new DecodeTurntableMotor(hardwareMap, telemetry);
        turntableMotor.init(null);

        imu = new DecodeIMU(hardwareMap, telemetry);
        limelight = new DecodeLimelight(hardwareMap, telemetry, imu);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        limelight.limelight.setPollRateHz(100);
        limelight.limelight.start();
        limelight.limelight.pipelineSwitch(0);

        // Put your calls here - they will not run in a loop

        while (opModeIsActive()) {
            switch (mode) {

                // obey the target set by the gamepad
                case FORCE_OFF_TARGET:
                    targetPosition = targetPositionToGoal;
                    actualPosition = turntableMotor.getPositionInTermsOfAttachment();
                    break;

                // start with the target set by the gamepad and then switch to limelight controlling the PID
                case SEEK_TARGET:
                    // get limelight data
                    LLResult result = limelight.limelight.getLatestResult();
                    if (result != null && result.isValid()) {
                        double tx = result.getTx(); // How far left or right the target is (degrees)
                        double ty = result.getTy(); // How far up or down the target is (degrees)
                        double ta = result.getTa(); // How big the target looks (0%-100% of the image)
                        aprilTagAcquired = true;

                        telemetry.addData("Target X", tx);
                        telemetry.addData("Target Y", ty);
                        telemetry.addData("Target Area", ta);
                    } else {
                        telemetry.addData("Limelight", "No Targets");
                        aprilTagAcquired = false;
                    }

                    // if the apriltag is in view control the turntable with its feedback
                    // If not, then continue turning to the target set by the gamepad buttons
                    if (aprilTagAcquired) {
                        targetPosition = TARGET_POSITION_TO_APRILTAG;
                        actualPosition = result.getTx();
                    } else {
                        targetPosition = targetPositionToGoal;
                        actualPosition = turntableMotor.getPositionInTermsOfAttachment();
                    }
                    break;
            }

            // set the position and run the PID control
            turntableMotor.setTargetPosition(targetPosition);
            turntableMotor.updateWithPosition(actualPosition);
            positionError = targetPosition - actualPosition;

            // set the target position
            if (debouncedDpadLeft.isPressed(gamepad2.dpad_left)) {
                targetPositionToGoal = targetPositionCCW;
                turntableMotor.setTargetPosition(targetPositionToGoal);
            }

            if (debouncedDpadRight.isPressed(gamepad2.dpad_right)) {
                targetPositionToGoal = targetPositionCW;
                turntableMotor.setTargetPosition(targetPositionToGoal);
            }

            if (debouncedDpadUp.isPressed(gamepad2.dpad_up)) {
                targetPositionToGoal = targetPositionCenter;
                turntableMotor.setTargetPosition(targetPositionToGoal);
            }

            // set the mode
            if (gamepad2.aWasPressed()) {
                mode = Mode.FORCE_OFF_TARGET;
            }

            if (gamepad2.xWasPressed()) {
                mode = Mode.SEEK_TARGET;
            }

            telemetry.addData("Dpad up = ", "center");
            telemetry.addData("Dpad left = ", "left / CCW");
            telemetry.addData("Dpad right = ", "right / CW");
            telemetry.addLine();
            telemetry.addData("Actual position = ", actualPosition);
            telemetry.addData("Position error = ", positionError);
            telemetry.addData("Current used = ", turntableMotor.getCurrent());
            turntableMotor.displayTurntableAngle(telemetry);
            telemetry.addData(">", "stop to finish");
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
