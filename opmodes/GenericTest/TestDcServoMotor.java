package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcServoMotor;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 *
 *
 */
@TeleOp(name = "Test Dc Servo Motor", group = "Test")
//@Disabled
public class TestDcServoMotor extends LinearOpMode {

    // Put your variable declarations here
    public DcServoMotor dcServoMotor;

    @Override
    public void runOpMode() {

        // Put your initializations here
        dcServoMotor = new DcServoMotor("extensionRetractionMotorRight", "servoMotor",
                0.5, 0.5, .1, hardwareMap, telemetry);
        dcServoMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        dcServoMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        // Wait for the start button
        telemetry.addData(">", "Press Start to run" );
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        dcServoMotor.setPower(0.0);
        telemetry.addData("power = ", "0");
        telemetry.update();
        sleep(4000);

        dcServoMotor.setPower(0.05);
        telemetry.addData("power = ", "0.05");
        telemetry.update();
        sleep(4000);

        dcServoMotor.setPower(0.2);
        telemetry.addData("power = ", "0.2");
        telemetry.update();
        sleep(2000);

        dcServoMotor.setPower(-0.2);
        telemetry.addData("power = ", "-0.2");
        telemetry.update();
        sleep(2000);

        dcServoMotor.setPower(1.0);
        telemetry.addData("power = ", "1.0");
        telemetry.update();
        sleep(2000);


        dcServoMotor.setPower(-1.0);
        telemetry.addData("power = ", "-1.0");
        telemetry.update();
        sleep(2000);

        dcServoMotor.setPower(0.0);


        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
