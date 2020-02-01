package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.ExtensionRetractionMechanism;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.ExtensionArm;

@TeleOp(name = "Test Extension Arm Read Encoder", group = "Test")
public class TestExtensionArmReadEncoder extends LinearOpMode {
    //Odometry Wheels


    double encoderValue = 0;

    //OdometryModule odometryModule;
    ExtensionArm extensionArm;


    @Override
    public void runOpMode() throws InterruptedException {

        //odometryModule = new OdometryModule(1440, 2.75*Math.PI, DistanceUnit.INCH, "ExtensionArmEncoder", hardwareMap);
        extensionArm = new ExtensionArm(hardwareMap, telemetry, "Extension Arm", "extensionLimitSwitchArm",
                "retractionLimitSwitchArm", "ExtensionArmEncoder", DcMotor8863.MotorType.ANDYMARK_40,
                6.985 * Math.PI);
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

