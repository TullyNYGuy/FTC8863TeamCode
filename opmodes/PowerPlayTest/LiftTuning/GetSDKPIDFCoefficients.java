package org.firstinspires.ftc.teamcode.opmodes.PowerPlayTest.LiftTuning;

import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MAXIMUM_LIFT_POSITION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.LiftConstants.MINIMUM_LIFT_POSITION;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863Interface;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DualMotorGearbox;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanismGenericMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MotorConstants;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Get PIDF Coefficients", group = "Lift Tuning")
//@Disabled
public class GetSDKPIDFCoefficients extends LinearOpMode {

    // Put your variable declarations here
    private DcMotorEx liftMotor;
    private PIDFCoefficients pidfCoefficients;

    public static double motorPower = 0;

    @Override
    public void runOpMode() {
        // create the motor for the lift
        liftMotor = hardwareMap.get(DcMotorEx.class, "liftMotorLeft");

        pidfCoefficients = liftMotor.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION);


        // Wait for the start button
        telemetry.addData("P = ", pidfCoefficients.p);
        telemetry.addData("I = ", pidfCoefficients.i);
        telemetry.addData("D = ", pidfCoefficients.d);
        telemetry.addData("F = ", pidfCoefficients.f);
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // after the extension is complete, loop so the user can see the result
        while (opModeIsActive()){
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
