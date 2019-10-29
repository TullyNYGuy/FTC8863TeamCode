package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;

import java.io.File;

@TeleOp(name = "Odometry Test Read", group = "Test")
public class ReadOdometryModule extends LinearOpMode {
    //Odometry Wheels


    double odometryEncoderValue = 0;

    OdometryModule odometryModule;

    @Override
    public void runOpMode() throws InterruptedException {

         odometryModule = new OdometryModule(OdometryModule.Position.LEFT, 1440, 3.8, OdometryModule.Units.CM, "odometryModuleLeft", hardwareMap);

        //Odometry System Calibration Init Complete
        telemetry.addData("Odometry System Calibration Status", "Init Complete");
        odometryEncoderValue = odometryModule.getDistanceSinceReset();
        telemetry.addData("Odometry encoder value = ", odometryEncoderValue);
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            odometryEncoderValue = odometryModule.getDistanceSinceReset();
            telemetry.addData("Odometry encoder value = ", odometryEncoderValue);
            telemetry.addData("Odometry Encoder Value (In)", String.format("%.2f", odometryModule.getEncoderValue()) );
            telemetry.addData("Odometry Encoder Value (mm)", String.format("%.2f", odometryEncoderValue/ 1440 * 38 * Math.PI) );
            //Update values
            telemetry.update();
        }
    }
}

