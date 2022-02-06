package org.firstinspires.ftc.teamcode.ArmTuning.Opmodes;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.ArmConstants;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.SampleArm;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LimitPosition;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MovementLimit;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Config
@TeleOp(name = "Find Ks - Automatic", group = "Arm Tuning")
//@Disabled
public class ArmTuningFindKsAutomatic extends LinearOpMode {

    // Put your variable declarations here

    enum States {
        IDLE,
        INCREASE_POWER,
        LOOK_FOR_MOVEMENT,
        WAITING_TO_HIT_POSITION_LIMIT,
        OFF_STOP;
    }
    States state = States.IDLE;

    enum ReasonForStop {
        NONE,
        LIMIT_TRIPPED;
    }
    ReasonForStop reasonForStop = ReasonForStop.NONE;

    SampleArm sampleArm;
    ElapsedTime timer;
    double angleWhenArmIsHorizontal = 0;
    double angleWhenArmIsVertical = 0;
    double armPower = 0;

    LimitPosition limitPositionAtHorizontal;

    // The following are public static so that the FTC Dashboard can be used to change them on the
    // fly.
    /**
     * We are looking for movement of the arm. Any change in angle greater than this is movement.
     */
    public static double CHANGE_IN_ANGLE_TO_CALL_IT_MOVEMENT = 2; // degrees

    /**
     * We will look for any movement within this time. If none occurs we say the arm did not move.
     */
    public static double TIME_TO_LOOK_FOR_MOVEMENT = 2000; // milliseconds

    /**
     * A guess about the starting arm power. You can save some time with a decent guess.
     * THIS NEEDS TO BE A NEGATIVE NUMBER!
     */
    public static double STARTING_ARM_POWER = 0.0;
    /**
     * The amount to increase/decrease the arm motor power by
     */
    public static double POWER_INCREMENT = .01;

    double lastPosition = 0;
    double currentPosition = 0;

    @Override
    public void runOpMode() {

        // Put your initializations here
        sampleArm = new SampleArm(hardwareMap, telemetry);
        sampleArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        timer = new ElapsedTime();
        limitPositionAtHorizontal = new LimitPosition(ArmConstants.HORIZONTAL_POSITION, MovementLimit.Direction.LIMIT_DECREASING_POSITIONS, "Horizontal limit" );


        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();
        state = States.INCREASE_POWER;
        armPower = STARTING_ARM_POWER;

        while (opModeIsActive() && !gamepad1.a) {
            telemetry.addData("Put the arm in its starting position. ", "Press A when completed");
            telemetry.update();
            idle();
        }
        // The arm is at its starting position.
        sampleArm.resetEncoder();
        if (gamepad1.a) {
            while (opModeIsActive() && gamepad1.a) {
                //eat the rest of the gamepad a press
                idle();
            }
        }

        while (opModeIsActive() && !gamepad1.a) {
            telemetry.addData("The arm should be in its starting position", ".");
            telemetry.addData("Move the arm to the vertical position from the start position. ", "Press A when completed");
            telemetry.addData("Arm position (degrees) = ", sampleArm.getPosition(AngleUnit.DEGREES));
            telemetry.addData("Arm encoder count = ", sampleArm.getCounts());
            telemetry.update();
            idle();
        }
        // The arm is at vertical relative to the ground.
        if (gamepad1.a) {
            while (opModeIsActive() && gamepad1.a) {
                //eat the rest of the gamepad a press
                idle();
            }
        }

        while (opModeIsActive() && !gamepad1.a) {
            telemetry.addData("You have rotated the arm to vertical", ".");
            telemetry.addData("Get people away from the arm. ", "Press A when completed");
            telemetry.update();
            idle();
        }
        // It is safe to move the arm now. Start the automatic procedure.
        lastPosition = sampleArm.getPosition(AngleUnit.DEGREES);
        if (gamepad1.a) {
            while (opModeIsActive() && gamepad1.a) {
                //eat the rest of the gamepad a press
                idle();
            }
        }

        while (opModeIsActive()) {
            // if the arm hits the horizontal position stop it there
            currentPosition = sampleArm.getPosition(AngleUnit.DEGREES);
            // todo there is a bug where the arm hits the limit before I think it should
            if(limitPositionAtHorizontal.isLimitReached(currentPosition)) {
                // todo temp line for debug
                sampleArm.setPower(0);
                // todo there is a bug the is sending the arm in the wrong direction to the limit
                sampleArm.holdAtHorizontal();
                reasonForStop = ReasonForStop.LIMIT_TRIPPED;
                break;
            }
            switch (state) {
                case IDLE:
                    break;
                case INCREASE_POWER:
                    // rotate the arm back towards the starting position
                    armPower = armPower - POWER_INCREMENT;
                    sampleArm.setPower(armPower);
                    telemetry.addData("Arm power increased to ", armPower);
                    telemetry.addData("Angle of arm = ", currentPosition);
                    timer.reset();
                    state = States.LOOK_FOR_MOVEMENT;
                    break;
                case LOOK_FOR_MOVEMENT:
                        currentPosition = sampleArm.getPosition(AngleUnit.DEGREES);
                        telemetry.addData("Arm power = ", armPower);
                        telemetry.addData("Angle of arm = ", currentPosition);
                        if (!hasMoved(currentPosition)) {
                            if (timer.milliseconds() > TIME_TO_LOOK_FOR_MOVEMENT) {
                                // there was no movement within the time window
                                state = States.INCREASE_POWER;
                            }
                        } else {
                            // there was movement, let the arm rotate until it hits the limit position
                            // or until the user kills the program
                            timer.reset();
                            lastPosition = currentPosition;
                            state = States.WAITING_TO_HIT_POSITION_LIMIT;
                        }
                    break;
                case WAITING_TO_HIT_POSITION_LIMIT:
                    currentPosition = sampleArm.getPosition(AngleUnit.DEGREES);
                    telemetry.addData("Arm power = ", armPower);
                    telemetry.addData("Angle of arm = ", currentPosition);
                    telemetry.addData("Ks = ", Math.abs(armPower));
                    break;
            }
            idle();
            telemetry.update();
        }
        if (reasonForStop == ReasonForStop.LIMIT_TRIPPED) {
            telemetry.addData("Complete", "!");
            telemetry.addData("Ks = ", Math.abs(armPower));
            telemetry.addData("Change KSTATIC constant in arm constants file", "!");
            telemetry.update();
        }
        while (opModeIsActive()) {
            idle();
        }
    }

    public boolean hasMoved(double currentPosition) {
        if (Math.abs(currentPosition - lastPosition) > CHANGE_IN_ANGLE_TO_CALL_IT_MOVEMENT) {
            return true;
        } else {
            return false;
        }
    }
}
