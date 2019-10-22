package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


import java.io.File;

@TeleOp(name = "Odometry Test Read", group = "Test")
public class ReadOdometryModule extends LinearOpMode {
    //Odometry Wheels
    DcMotor odometryModule;

    double odometryEncoderValue = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        //Initialize hardware map values. PLEASE UPDATE THESE VALUES TO MATCH YOUR CONFIGURATION
        odometryModule = hardwareMap.dcMotor.get("odometryWheel");
        odometryModule.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Odometry System Calibration Init Complete
        telemetry.addData("Odometry System Calibration Status", "Init Complete");
        odometryEncoderValue = odometryModule.getCurrentPosition();
        telemetry.addData("Odometry encoder value = ", odometryEncoderValue);
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            odometryEncoderValue = odometryModule.getCurrentPosition();
            telemetry.addData("Odometry encoder value = ", odometryEncoderValue);
            telemetry.addData("Odometry Encoder Value (In)", String.format("%.2f", odometryEncoderValue/ 1440 * 1.5 * Math.PI) );
            //Update values
            telemetry.update();
        }
    }
}

