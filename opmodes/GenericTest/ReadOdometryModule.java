package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;

import java.io.File;

@TeleOp(name = "Odometry Test Read", group = "Test")
@Disabled
public class ReadOdometryModule extends LinearOpMode {
    //Odometry Wheels


    double odometryEncoderValue = 0;

    OdometryModule odometryModule;

    @Override
    public void runOpMode() throws InterruptedException {

        odometryModule = new OdometryModule(1440, 3.8, DistanceUnit.CM, "odometryModuleLeft", hardwareMap);

        //Odometry System Calibration Init Complete
        telemetry.addData("Odometry System Calibration Status", "Init Complete");
        odometryEncoderValue = odometryModule.getDistanceSinceReset(DistanceUnit.CM);
        telemetry.addData("Odometry encoder value = ", odometryEncoderValue);
        telemetry.update();


        waitForStart();

        while (opModeIsActive()) {
            odometryEncoderValue = odometryModule.getEncoderValue();
            telemetry.addData("Odometry encoder value = ", odometryEncoderValue);
            telemetry.addData("Odometry Encoder Value (In)", String.format("%.2f", odometryModule.getDistanceSinceReset(DistanceUnit.INCH)));
            telemetry.addData("Odometry Encoder Value (cm)", String.format("%.2f", odometryModule.getDistanceSinceReset(DistanceUnit.CM)));
            telemetry.addData("value since last change (cm)", String.format("%.2f", odometryModule.getDistanceSinceLastChange(DistanceUnit.CM)));
            //Update values
            telemetry.update();
        }
    }
}

