package org.firstinspires.ftc.teamcode.opmodes.UltimateGoalTest;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.GamepadButtonMultiPush;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.HaloControls;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Mecanum;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MecanumCommands;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometrySystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RampControl;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.SmartJoystick;

/*
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test Motors", group = "ATest")
//@Disabled
public class TestMotors extends LinearOpMode {

    // Put your variable declarations here
    private Configuration config = new Configuration();

    @Override
    public void runOpMode() {


        // Put your initializations here
        //config.delete();
        config.load();

        DcMotor8863 frontLeft = DcMotor8863.createMotorFromFile(config, "FLMotor", hardwareMap);
        DcMotor8863 backLeft = DcMotor8863.createMotorFromFile(config, "BLMotor", hardwareMap);
        DcMotor8863 frontRight = DcMotor8863.createMotorFromFile(config, "FRMotor", hardwareMap);
        DcMotor8863 backRight = DcMotor8863.createMotorFromFile(config, "BRMotor", hardwareMap);

        // Game Pad 1 joysticks
        GamepadButtonMultiPush gamepad1x = new GamepadButtonMultiPush(4);

        waitForStart();
        while (opModeIsActive()){
            if (gamepad1x.buttonPress(gamepad1.x)) {
                if (gamepad1x.isCommand1()) {
                    frontLeft.setPower(0);
                    backLeft.setPower(0);
                    frontRight.setPower(0);
                    backRight.setPower(0);

                    frontLeft.setPower(0.3);
                    telemetry.addData(">", "FL is runnning forward.");

                    // call the first command you want to run
                }
                if (gamepad1x.isCommand2()) {
                    frontLeft.setPower(0);
                    backLeft.setPower(0);
                    frontRight.setPower(0);
                    backRight.setPower(0);

                    backLeft.setPower(0.3);
                    telemetry.addData(">", "BL is runnning forward.");

                    // call the first command you want to run
                }
                if (gamepad1x.isCommand3()) {
                    frontLeft.setPower(0);
                    backLeft.setPower(0);
                    frontRight.setPower(0);
                    backRight.setPower(0);

                    frontRight.setPower(0.3);
                    telemetry.addData(">", "FR is runnning forward.");

                    // call the first command you want to run
                }
                if (gamepad1x.isCommand4()) {
                    frontLeft.setPower(0);
                    backLeft.setPower(0);
                    frontRight.setPower(0);
                    backRight.setPower(0);

                    backRight.setPower(0.3);
                    telemetry.addData(">", "BR is runnning forward.");

                    // call the first command you want to run
                }
            }
            telemetry.update();
            idle();
        }
        frontLeft.stop();
        backLeft.stop();
        frontRight.stop();
        backRight.stop();
    }
}
