package org.firstinspires.ftc.teamcode.opmodes.GenericDiagnostics;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

/**
 * This opmode will display the encoder value as you turn the output shaft. If you want to see
 * what type of motor it is, turn the output shaft around as close to 1 revolution as you can.
 */
@TeleOp(name = "Check encoder and motor type", group = "Diagnostics")
//@Disabled
public class CheckEncoderAndMotorType extends LinearOpMode {

    // Put your variable declarations her
    DcMotor8863 motor;

    @Override
    public void runOpMode() {

        // Put your initializations here
        motor = new DcMotor8863("MotorToTest", hardwareMap);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        motor.testEncoderAndMotorType(this);

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
