
package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;

import java.util.Locale;

@TeleOp(name = "Test 3 odometry modules", group = "Diagnostics")
//@Disabled
public class Read3OdometryModules extends LinearOpMode {
    //Odometry Wheels


    double odometryModuleRightValue = 0;
    double odometryModuleBackValue = 0;
    double odometryModuleLeftValue = 0;
    OdometrySystem system;
    OdometryModule odometryModuleRight;
    OdometryModule odometryModuleBack;
    OdometryModule odometryModuleLeft;
    DcMotor8863 FrontRight;
    DcMotor8863 BackRight;
    DcMotor8863 FrontLeft;
    DcMotor8863 BackLeft;
    @Override
    public void runOpMode() {
system = new OdometrySystem(DistanceUnit.INCH, odometryModuleLeft, odometryModuleRight, odometryModuleBack);
        odometryModuleRight = new OdometryModule(1440, 3.8 * Math.PI, DistanceUnit.CM, "BackLeft", hardwareMap);
        odometryModuleBack = new OdometryModule(1440, 3.8 * Math.PI, DistanceUnit.CM, "BackRight", hardwareMap);
        odometryModuleLeft = new OdometryModule(1440, 3.8 * Math.PI, DistanceUnit.CM, "FrontLeft", hardwareMap);
        FrontLeft = new DcMotor8863("FrontLeft", hardwareMap, telemetry);
        FrontRight = new DcMotor8863("FrontRight", hardwareMap,telemetry);
        BackLeft = new DcMotor8863("BackLeft", hardwareMap,telemetry);
        BackRight = new DcMotor8863("BackRight",hardwareMap,telemetry);
        FrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        FrontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        BackRight.setDirection(DcMotorSimple.Direction.REVERSE);
        BackLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        system =new OdometrySystem(DistanceUnit.INCH, odometryModuleLeft, odometryModuleRight, odometryModuleBack);
        //Odometry System Calibration Init Complete
        system.initializeRobotGeometry(DistanceUnit.INCH, 1,1, DcMotorSimple.Direction.REVERSE, 1,1, DcMotorSimple.Direction.FORWARD, 1,1, DcMotorSimple.Direction.REVERSE);
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
            telemetry.addData("Odometry Encoder Right (In)", String.format(Locale.getDefault(), "%.2f", odometryModuleRightValue / 1440 * 1.5 * Math.PI));
            telemetry.addData("Odometry Encoder Right (cm)", String.format(Locale.getDefault(), "%.2f", odometryModuleRight.getDistanceSinceReset(DistanceUnit.CM)));
            telemetry.addData("Odometry encoder Back = ", odometryModuleBackValue);
            telemetry.addData("Odometry Encoder Back (In)", String.format(Locale.getDefault(), "%.2f", odometryModuleBackValue / 1440 * 1.5 * Math.PI));
            telemetry.addData("Odometry Encoder Back (cm)", String.format(Locale.getDefault(), "%.2f", odometryModuleBack.getDistanceSinceReset(DistanceUnit.CM)));
            telemetry.addData("Odometry encoder Left = ", odometryModuleLeftValue);
            telemetry.addData("Odometry Encoder Left (In)", String.format(Locale.getDefault(), "%.2f", odometryModuleLeftValue / 1440 * 1.5 * Math.PI));
            telemetry.addData("Odometry Encoder Left (cm)", String.format(Locale.getDefault(), "%.2f", odometryModuleLeft.getDistanceSinceReset(DistanceUnit.CM)));
            //Update values
            telemetry.addData("system says X is: ", system.getCurrentX(DistanceUnit.CM));
            telemetry.addData("system says y is: ", system.getCurrentY(DistanceUnit.CM));
            telemetry.update();
        }
    }
}

