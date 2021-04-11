
package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobot;

import java.util.Locale;

@TeleOp(name = "Test 3 odometry modules", group = "Diagnostics")
//@Disabled
public class Test3OdometryModules extends LinearOpMode {

    @Override
    public void runOpMode() {
/*
        DcMotor8863 frontLeft = DcMotor8863.createMotorFromFile(config, UltimateGoalRobot.HardwareName.CONFIG_FL_MOTOR.hwName, hardwareMap);
        DcMotor8863 backLeft = DcMotor8863.createMotorFromFile(config, UltimateGoalRobot.HardwareName.CONFIG_BL_MOTOR.hwName, hardwareMap);
        DcMotor8863 frontRight = DcMotor8863.createMotorFromFile(config, UltimateGoalRobot.HardwareName.CONFIG_FR_MOTOR.hwName, hardwareMap);
        DcMotor8863 backRight = DcMotor8863.createMotorFromFile(config, UltimateGoalRobot.HardwareName.CONFIG_BR_MOTOR.hwName, hardwareMap);
 */
        Configuration config = new Configuration();
        config.load();
        OdometryModule odometryModuleLeft = OdometryModule.createOdometryModuleFromFile(config, UltimateGoalRobot.HardwareName.CONFIG_LEFT_ODOMETRY_MODULE.hwName, hardwareMap);
        OdometryModule odometryModuleRight = OdometryModule.createOdometryModuleFromFile(config, UltimateGoalRobot.HardwareName.CONFIG_RIGHT_ODOMETRY_MODULE.hwName, hardwareMap);
        OdometryModule odometryModuleBack = OdometryModule.createOdometryModuleFromFile(config, UltimateGoalRobot.HardwareName.CONFIG_BACK_ODOMETRY_MODULE.hwName, hardwareMap);
        odometryModuleBack.setShiftValue((byte) 7);
        odometryModuleBack.resetEncoderValue();
        OdometrySystem system =new OdometrySystem(DistanceUnit.INCH, odometryModuleLeft, odometryModuleRight, odometryModuleBack);
        //Odometry System Calibration Init Complete
        system.initializeRobotGeometry(DistanceUnit.INCH, 1,1, DcMotorSimple.Direction.REVERSE, 1,1, DcMotorSimple.Direction.FORWARD, 1,1, DcMotorSimple.Direction.REVERSE);
        telemetry.addData("Odometry System Calibration Status", "Init Complete");
        double odometryModuleRightValue = odometryModuleRight.getDistanceSinceReset(DistanceUnit.CM);
        double odometryModuleBackValue = odometryModuleBack.getDistanceSinceReset(DistanceUnit.CM);
        double odometryModuleLeftValue = odometryModuleLeft.getDistanceSinceReset(DistanceUnit.CM);
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

