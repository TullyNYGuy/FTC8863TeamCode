package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArm;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArmConstants;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

@TeleOp(name = "Test Extension Arm Read Encoder", group = "Test")
@Disabled
public class TestExtensionArmReadEncoder extends LinearOpMode {
    //Odometry Wheels


    double encoderValue = 0;

    //OdometryModule odometryModule;
    ExtensionArm extensionArm;


    @Override
    public void runOpMode() throws InterruptedException {

        //odometryModule = new OdometryModule(1440, 2.75*Math.PI, DistanceUnit.INCH, "ExtensionArmEncoder", hardwareMap);
        extensionArm = new ExtensionArm(hardwareMap, telemetry,
                ExtensionArmConstants.mechanismName,
                SkystoneRobot.HardwareName.EXT_ARM_EXTENSION_SWITCH.hwName,
                SkystoneRobot.HardwareName.EXT_ARM_RETRACTION_SWITCH.hwName,
                SkystoneRobot.HardwareName.EXT_ARM_MOTOR_NAME_FOR_ENCODER_PORT.hwName,
                ExtensionArmConstants.motorType,
                ExtensionArmConstants.movementPerRevolution);

        //Odometry System Calibration Init Complete
        encoderValue = extensionArm.getMotorEncoderValue();
        telemetry.addData("Odometry encoder value = ", encoderValue);
        telemetry.update();


        waitForStart();

        while (opModeIsActive()) {
            encoderValue = extensionArm.getMotorEncoderValue();
            telemetry.addData("Arm encoder value = ", encoderValue);
            telemetry.addData("Arm encoder Value (In)", String.format("%.2f", extensionArm.getPosition()));
            //telemetry.addData("Odometry Encoder Value (cm)", String.format("%.2f", odometryModule.getDistanceSinceReset(DistanceUnit.CM)));
            // telemetry.addData("value since last change (cm)", String.format("%.2f", odometryModule.getDistanceSinceLastChange(DistanceUnit.CM)));
            //Update values
            telemetry.update();
        }
    }
}

