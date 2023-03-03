package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest.LiftTuning;

import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAXIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MINIMUM_LIFT_POSITION;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863Interface;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DualMotorGearbox;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanismGenericMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotorConstants;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;

/**
 * This Opmode is for finding the motor power that will hold the lift up and prevent it from falling.
 * That motor power is called kG (gravity constant).
 */
@Config
@TeleOp(name = "Find Kg", group = "Lift Tuning")
@Disabled
public class FindKg extends LinearOpMode {

    // Put your variable declarations here
    ExtensionRetractionMechanismGenericMotor lift;
    private DcMotor8863Interface liftMotor;

    public static double MOTOR_POWER = 0;

    @Override
    public void runOpMode() {
        // create the motor for the lift
        liftMotor = new DualMotorGearbox(
                PowerPlayRobot.HardwareName.LIFT_MOTOR_LEFT.hwName,
                PowerPlayRobot.HardwareName.LIFT_MOTOR_RIGHT.hwName,
                hardwareMap,
                telemetry,
                MotorConstants.MotorType.GOBILDA_1150);


        // Put your initializations here
        lift = new ExtensionRetractionMechanismGenericMotor(hardwareMap, telemetry,
                "lift",
                PowerPlayRobot.HardwareName.LIFT_LIMIT_SWITCH_EXTENSION.hwName,
                PowerPlayRobot.HardwareName.LIFT_LIMIT_SWITCH_RETRACTION.hwName,
                liftMotor,
                5.93);

        lift.reverseMotorDirection();

        // set the limits for the lift so that it will not get broken. The lift software will stop
        // the lift and hold it at this position if it extends or retracts too far.
        lift.setExtensionPositionInMechanismUnits(MAXIMUM_LIFT_POSITION);
        lift.setRetractionPositionInMechanismUnits(MINIMUM_LIFT_POSITION);

        // retract the lift until the limit switch is tripped
        lift.init();
        while (!lift.isInitComplete()) {
            lift.update();
            idle();
        }

        // Wait for the start button
        telemetry.addData("Init complete", ".");
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // apply the motor power. You should use the FTC Dashboard to change the MOTOR_POWER constant.
        // Gradually increase the power until the lift holds its own weight. Do this near the bottom
        // of the lift and near the top of the lift, but short of the MAXIMUM and MINIMUM lift positions.
        // If they are significantly different then you will need to come up with a function for kG.
        // Plot the motor powers on the y axis and the lift positions where you found them on the
        // x axis. Find the equation of the line for these points.
        // y = m*x + b
        // kG = m * lift position + intercept
        // Fill this in the LiftConstants file.
        // Note that if they are close you can probably just use one number.
        while (opModeIsActive()){
            lift.setPowerUsingJoystick(MOTOR_POWER);
            lift.update();
            telemetry.addData("state = ", lift.getExtensionRetractionState().toString());
            telemetry.addData("power = ", MOTOR_POWER);
            telemetry.addData("lift extention = ", lift.getCurrentPosition());
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
