package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Encoder Without Motor", group = "Test")
//@Disabled
public class TestEncoderWithoutMotor extends LinearOpMode {

    DcMotor8863 motor;

    @Override
    public void runOpMode() {


        motor = new DcMotor8863(SkystoneRobot.HardwareName.EXT_ARM_MOTOR_NAME_FOR_ENCODER_PORT.hwName,
                DcMotor8863.MotorType.USDIGITAL_360PPR_ENCODER,
                hardwareMap, telemetry);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        motor.encoder.test(this);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
