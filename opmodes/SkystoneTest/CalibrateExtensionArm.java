package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArm;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Calibrate Extension Arm", group = "Calibrate")
//@Disabled
public class CalibrateExtensionArm extends LinearOpMode {

    // Put your variable declarations here
    public ExtensionArm extensionArm;

    public double movementPerRevolution = 2.75 * Math.PI * 2;

    @Override
    public void runOpMode() {

        // Put your initializations here
        extensionArm = new ExtensionArm(hardwareMap, telemetry, "Extension Arm", SkystoneRobot.HardwareName.EXT_ARM_EXTENSION_SWITCH.hwName,
                SkystoneRobot.HardwareName.INTAKE_LEFT_MOTOR.hwName, SkystoneRobot.HardwareName.EXT_ARM_SERVO.hwName, DcMotor8863.MotorType.ANDYMARK_40, movementPerRevolution);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();

        waitForStart();

        extensionArm.calibrate(360, .5, this);

        while (opModeIsActive()) {
            // sit here until the user has read the values and then they kill the opmode
            idle();
        }
    }
}
