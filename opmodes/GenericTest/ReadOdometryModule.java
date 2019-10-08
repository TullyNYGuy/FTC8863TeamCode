package org.firstinspires.ftc.teamcode.Opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


import java.io.File;

@TeleOp(name = "Odometry Test Read", group = "Test")
public class ReadOdometryModule extends LinearOpMode {
    //Odometry Wheels
    DcMotor odometryModule;

    //Hardware Map Names for drive motors and odometry wheels. THIS WILL CHANGE ON EACH ROBOT, YOU NEED TO UPDATE THESE VALUES ACCORDINGLY
    String odometryName = "odometryWheel";

    double odometryValue = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        //Initialize hardware map values. PLEASE UPDATE THESE VALUES TO MATCH YOUR CONFIGURATION
        odometryModule = hardwareMap.dcMotor.get(odometryName);

        //Odometry System Calibration Init Complete
        telemetry.addData("Odometry System Calibration Status", "Init Complete");
        telemetry.update();

        waitForStart();

        odometryValue = odometryModule.getCurrentPosition();

        while (opModeIsActive()) {
            odometryValue = odometryModule.getCurrentPosition();

            telemetry.addData("Odometry module value = ", odometryValue);
            //Update values
            telemetry.update();
        }
    }
}
