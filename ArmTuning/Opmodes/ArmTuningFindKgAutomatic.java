package org.firstinspires.ftc.teamcode.ArmTuning.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.ArmMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LimitPosition;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MovementLimit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.ArmConstants;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
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

    ArmMotor armMotor;
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
     * The amount to increase/decrease the arm motor power by
     */
    public static double POWER_INCREMENT = .01;

    double lastPosition = 0;
    double currentPosition = 0;

    @Override
    public void runOpMode() {

        // Put your initializations here
        armMotor = new ArmMotor(hardwareMap, telemetry);
        armMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        timer = new ElapsedTime();
        limitPositionAtVertical = new LimitPosition(ArmConstants.VERTICAL_POSITION, MovementLimit.Direction.LIMIT_INCREASING_POSITIONS, "Vertical limit" );


        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if(!limitPositionAtVertical.isLimitReached(armMotor.getPosition(AngleUnit.DEGREES))) {
                armMotor.holdAtVertical();
                reasonForStop = ReasonForStop.LIMIT_TRIPPED;
                break;
            }
            switch (state) {
                case IDLE:
                    break;
                case INCREASE_POWER:
                    armPower = armPower + POWER_INCREMENT;
                    armMotor.setPower(armPower);
                    timer.reset();
                    state = States.LOOK_FOR_MOVEMENT;
                    break;
                case LOOK_FOR_MOVEMENT:
                        currentPosition = armMotor.getPosition(AngleUnit.DEGREES);
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
                    currentPosition = armMotor.getPosition(AngleUnit.DEGREES);
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
        }
        if (reasonForStop == ReasonForStop.LIMIT_TRIPPED) {
        }

        telemetry.addData("Arm angle when arm is horizontal = ", angleWhenArmIsHorizontal);
        telemetry.addData("Arm angle when arm is vertical = ", angleWhenArmIsVertical);
        telemetry.addData("Press Stop when you have entered these values into the arm constants file", ".");
        telemetry.update();
        while (opModeIsActive()) {
            idle();
        }
    }

    public boolean hasMoved(double currentPosition) {
        if (currentPosition > lastPosition + CHANGE_IN_ANGLE_TO_CALL_IT_MOVEMENT) {
            return true;
        } else {
            return false;
        }
    }
}
