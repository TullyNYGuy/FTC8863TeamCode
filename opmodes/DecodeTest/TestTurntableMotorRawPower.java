package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeIMU;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeLimelight;
import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeTurntableMotor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Debouncer;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Decode Test Turntable Motor Raw Power", group = "Test")
//@Disabled
public class TestTurntableMotorRawPower extends LinearOpMode {


    // Put your variable declarations her
    DecodeTurntableMotor turntableMotor;

    @Override
    public void runOpMode() {

        // Put your initializations here
        turntableMotor = new DecodeTurntableMotor(hardwareMap, telemetry);
        turntableMotor.init(null);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop
        turntableMotor.setPower(.2);


        while (opModeIsActive()) {

            telemetry.addData("Current used = ", turntableMotor.getCurrent());
            turntableMotor.displayTurntableAngle(telemetry);
            telemetry.addData(">", "stop to finish");
            telemetry.update();
            idle();
        }

        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
