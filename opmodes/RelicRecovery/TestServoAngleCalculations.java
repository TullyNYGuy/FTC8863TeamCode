package org.firstinspires.ftc.teamcode.opmodes.RelicRecovery;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib.JewelArm;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Jewel Arm Servo Angle Calculations", group = "Test")
//@Disabled
public class TestServoAngleCalculations extends LinearOpMode {

    JewelArm jewelArm;
    DataLogging dataLog;

    public void runOpMode() {

        // Put your initializations here
        dataLog = new DataLogging("jewelArmServoAngleCalcTest", telemetry);
        jewelArm = new JewelArm(JewelArm.RobotSide.LEFT, hardwareMap, telemetry, dataLog);
        jewelArm.init();

        jewelArm.getServoAngles(41.5);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();

            idle();
        }

        dataLog.closeDataLog();
    }
}
