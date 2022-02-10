package org.firstinspires.ftc.teamcode.ArmTuning.Opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.ArmConstants;
import org.firstinspires.ftc.teamcode.ArmTuning.Lib.SampleArm;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LimitPosition;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MovementLimit;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Config
@TeleOp(name = "Find Kg - Manual", group = "Arm Tuning")
//@Disabled
public class ArmTuningFindKgManual extends LinearOpMode {

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
    LimitPosition limitPositionAtVertical;

    private FtcDashboard dashboard;

    double angleToHorizontal = 0;

    // The following are public static so that the FTC Dashboard can be used to change them on the
    // fly.

    /**
     * The amount to increase/decrease the arm motor power by
     */
    public static double ARM_POWER = 0;

    @Override
    public void runOpMode() {

        // Put your initializations here
        sampleArm = new SampleArm(hardwareMap, telemetry);
        sampleArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Get the instance of the running FTC Dashboard
        dashboard = FtcDashboard.getInstance();
        dashboard.setTelemetryTransmissionInterval(25);
        // merge the normal FTC SDK telemetry with the FTC Dashboard telemetry
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        limitPositionAtVertical = new LimitPosition(ArmConstants.VERTICAL_POSITION, MovementLimit.Direction.LIMIT_INCREASING_POSITIONS, "Vertical limit" );

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // If the arm power increases too much then the arm will run away, you lose control of it.
            // Implement a limit to the movement. Snap the arm to vertical if it runs away and hits
            // the vertical position.
            if(limitPositionAtVertical.isLimitReached(sampleArm.getPosition(AngleUnit.DEGREES))) {
                sampleArm.holdAtVertical();
                reasonForStop = ReasonForStop.LIMIT_TRIPPED;
                break;
            }

            // You will gradually increase the arm power, using the FTC Dashboard, until the arm is
            // holding its position horizontal to the ground.
            // When the arm hits and holds horizontal, that motor power is Kg + Kstatic. Record Kg
            // in the ArmConstants file. Kg = motor power - Kstatic.
            sampleArm.setPower(ARM_POWER);

            // Output the position to telemetry
            angleToHorizontal = ArmConstants.getAngleToHorizontal(sampleArm.getPosition(AngleUnit.DEGREES), AngleUnit.DEGREES);
            telemetry.addData("When angle to horizontal = 0, kgPluskS is the arm power", ".");
            telemetry.addData("Angle to horizontal ", angleToHorizontal);
            telemetry.addData("Arm motor Power ", ARM_POWER);
            telemetry.update();
            idle();
        }

        // if the loop stopped then the arm hit the limit
        // hold it there
        while (opModeIsActive()) {
            idle();
        }
    }
}
