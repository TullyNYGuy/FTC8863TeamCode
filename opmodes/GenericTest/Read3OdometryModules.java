package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;

@TeleOp(name = "Test 3 odometry modules", group = "Test")
//@Disabled
public class Read3OdometryModules extends LinearOpMode {
    //Odometry Wheels


    double odometryModuleRightValue = 0;
    double odometryModuleBackValue = 0;
    double odometryModuleLeftValue = 0;

    OdometryModule odometryModuleRight;
    OdometryModule odometryModuleBack;
    OdometryModule odometryModuleLeft;

    @Override
    public void runOpMode() throws InterruptedException {

        odometryModuleRight = new OdometryModule(1440, 3.8, DistanceUnit.CM, "BackRight", hardwareMap);
        odometryModuleBack = new OdometryModule(1440, 3.8, DistanceUnit.CM, "FrontRight", hardwareMap);
        odometryModuleLeft = new OdometryModule(1440, 3.8, DistanceUnit.CM, "FrontLeft", hardwareMap);

        //Odometry System Calibration Init Complete
        telemetry.addData("Odometry System Calibration Status", "Init Complete");
        odometryModuleRightValue = odometryModuleRight.getDistanceSinceReset(DistanceUnit.CM);
        odometryModuleBackValue = odometryModuleBack.getDistanceSinceReset(DistanceUnit.CM);
        odometryModuleLeftValue = odometryModuleLeft.getDistanceSinceReset(DistanceUnit.CM);
        telemetry.addData("Odometry encoder 1 value = ", odometryModuleRightValue);
        telemetry.addData("Odometry encoder 2 value = ", odometryModuleBackValue);
        telemetry.addData("Odometry encoder 3 value = ", odometryModuleLeftValue);
        telemetry.update();


        waitForStart();

        while (opModeIsActive()) {
            odometryModuleRightValue = odometryModuleRight.getEncoderValue();
            odometryModuleBackValue = odometryModuleBack.getEncoderValue();
            odometryModuleLeftValue = odometryModuleLeft.getEncoderValue();
            telemetry.addData("Odometry encoder Right = ", odometryModuleRightValue);
            telemetry.addData("Odometry Encoder Right (In)", String.format("%.2f", odometryModuleRightValue / 1440 * 1.5 * Math.PI));
            telemetry.addData("Odometry Encoder Right (cm)", String.format("%.2f", odometryModuleRight.getDistanceSinceReset(DistanceUnit.CM)));
            telemetry.addData("Odometry encoder Back = ", odometryModuleBackValue);
            telemetry.addData("Odometry Encoder Back (In)", String.format("%.2f", odometryModuleBackValue / 1440 * 1.5 * Math.PI));
            telemetry.addData("Odometry Encoder Back (cm)", String.format("%.2f", odometryModuleBack.getDistanceSinceReset(DistanceUnit.CM)));
            telemetry.addData("Odometry encoder Left = ", odometryModuleLeftValue);
            telemetry.addData("Odometry Encoder Left (In)", String.format("%.2f", odometryModuleLeftValue / 1440 * 1.5 * Math.PI));
            telemetry.addData("Odometry Encoder Left (cm)", String.format("%.2f", odometryModuleLeft.getDistanceSinceReset(DistanceUnit.CM)));
            //Update values
            telemetry.update();
        }
    }
}

