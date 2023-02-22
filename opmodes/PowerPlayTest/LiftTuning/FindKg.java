package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest.LiftTuning;

import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAXIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MINIMUM_LIFT_POSITION;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863Interface;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DualMotorGearbox;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanismGenericMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotorConstants;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@Config
@TeleOp(name = "Find Kg", group = "Lift Tuning")
//@Disabled
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

        lift.setExtensionPositionInMechanismUnits(MAXIMUM_LIFT_POSITION);
        lift.setRetractionPositionInMechanismUnits(MINIMUM_LIFT_POSITION);

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

        // Put your calls here - they will not run in a loop



        // after the extension is complete, loop so the user can see the result
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
