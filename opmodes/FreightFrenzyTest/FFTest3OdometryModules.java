
package org.firstinspires.ftc.teamcode.opmodes.FreightFrenzyTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobot;

import java.util.Locale;

@TeleOp(name = "Test 3 odometry modules - freight frenzy", group = "Diagnostics")
//@Disabled
public class FFTest3OdometryModules extends LinearOpMode {

    @Override
    public void runOpMode() {

        DcMotor8863 frontLeft = new DcMotor8863(FreightFrenzyRobotRoadRunner.HardwareName.CONFIG_FL_MOTOR.hwName, hardwareMap, telemetry);
        DcMotor8863 backLeft = new DcMotor8863(FreightFrenzyRobotRoadRunner.HardwareName.CONFIG_BL_MOTOR.hwName, hardwareMap, telemetry);
        DcMotor8863 frontRight = new DcMotor8863(FreightFrenzyRobotRoadRunner.HardwareName.CONFIG_FR_MOTOR.hwName, hardwareMap, telemetry);
        DcMotor8863 backRight = new DcMotor8863(FreightFrenzyRobotRoadRunner.HardwareName.CONFIG_BR_MOTOR.hwName, hardwareMap, telemetry);


        waitForStart();

        while (opModeIsActive()) {

            telemetry.addData("Odometry encoder Right = ", backRight.getCurrentPosition());
            telemetry.addData("Odometry encoder Back = ", backLeft.getCurrentPosition());
            telemetry.addData("Odometry encoder Left = ", frontLeft.getCurrentPosition());
            telemetry.update();
        }
    }
}

