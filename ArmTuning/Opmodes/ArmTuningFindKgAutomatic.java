package org.firstinspires.ftc.teamcode.ArmTuning.Opmodes;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.SampleArm;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LimitPosition;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MovementLimit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.ArmConstants;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Config
@TeleOp(name = "Find Kg - Automatic", group = "Arm Tuning")
//@Disabled
public class ArmTuningFindKgAutomatic extends LinearOpMode {

    // Put your variable declarations here

    enum States {
        IDLE,
        INCREASE_POWER,
        LOOK_FOR_MOVEMENT,
        LOOK_FOR_STATIC_POSITION,
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

    LimitPosition limitPositionAtVertical;

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
     */
    public static double STARTING_ARM_POWER = .05;
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
        limitPositionAtVertical = new LimitPosition(ArmConstants.VERTICAL_POSITION, MovementLimit.Direction.LIMIT_INCREASING_POSITIONS, "Vertical limit" );


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

        while (opModeIsActive()) {
            if(limitPositionAtVertical.isLimitReached(sampleArm.getPosition(AngleUnit.DEGREES))) {
                sampleArm.holdAtVertical();
                reasonForStop = ReasonForStop.LIMIT_TRIPPED;
                break;
            }
            switch (state) {
                case IDLE:
                    break;
                case INCREASE_POWER:
                    armPower = armPower + POWER_INCREMENT;
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
                            // there was movement, see if the arm hangs out at this position
                            timer.reset();
                            lastPosition = currentPosition;
                            state = States.LOOK_FOR_STATIC_POSITION;
                        }
                    break;
                case LOOK_FOR_STATIC_POSITION:
                    currentPosition = sampleArm.getPosition(AngleUnit.DEGREES);
                    telemetry.addData("Arm power = ", armPower);
                    telemetry.addData("Angle of arm = ", currentPosition);
                    if (!hasMoved(currentPosition)) {
                        if (timer.milliseconds() > TIME_TO_LOOK_FOR_MOVEMENT) {
                            // there was no movement within the time window
                            // record this as a power, position data point
                            state = States.INCREASE_POWER;
                        }
                    } else {
                        // there was movement. The arm has not stabilized at a position. Keep looking
                        // for a stable position
                        timer.reset();
                        lastPosition = currentPosition;
                        state = States.LOOK_FOR_STATIC_POSITION; // just reset the timer and stay in this state
                    }
                    break;
            }
            idle();
            telemetry.update();
        }
        if (reasonForStop == ReasonForStop.LIMIT_TRIPPED) {
            telemetry.addData("Position limit was tripped. Aborting", "!");
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
