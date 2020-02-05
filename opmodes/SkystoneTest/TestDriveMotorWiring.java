package org.firstinspires.ftc.teamcode.opmodes.SkystoneTest;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.CSVDataFile;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.OdometryModule;
import org.firstinspires.ftc.teamcode.Lib.SkyStoneLib.SkystoneRobot;
import org.openftc.revextensions2.ExpansionHubEx;

/**
 * This Opmode runs each drive train motor. It starts with the front left and proceeds in a clockwise
 * order (as viewed from the top of the robot). This allows you to verify the wiring and the forward
 * or reverse settings are correct. All motor should spin toward the front of the robot, as if it
 * were traveling forward.
 */
@TeleOp(name = "Test Motor wiring", group = "Test")
@Disabled
public class TestDriveMotorWiring extends LinearOpMode {

    DcMotor8863 frontLeft;
    DcMotor8863 frontRight;
    DcMotor8863 backLeft;
    DcMotor8863 backRight;

    @Override
    public void runOpMode() {


        // Put your initializations here

        ElapsedTime timer = new ElapsedTime();

        frontLeft = new DcMotor8863(SkystoneRobot.HardwareName.FRONT_LEFT_MOTOR.hwName, hardwareMap);
        frontRight = new DcMotor8863(SkystoneRobot.HardwareName.FRONT_RIGHT_MOTOR.hwName, hardwareMap);
        backLeft = new DcMotor8863(SkystoneRobot.HardwareName.BACK_LEFT_MOTOR.hwName, hardwareMap);
        backRight = new DcMotor8863(SkystoneRobot.HardwareName.BACK_RIGHT_MOTOR.hwName, hardwareMap);

        frontLeft.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        frontLeft.runAtConstantPower(0);
        frontRight.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        frontRight.runAtConstantPower(0);
        backLeft.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        backLeft.runAtConstantPower(0);
        backRight.setMotorType(DcMotor8863.MotorType.ANDYMARK_20_ORBITAL);
        backRight.runAtConstantPower(0);

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < 2000) {
            frontLeft.setPower(1.0);
            telemetry.addData(">", "Front left");
            telemetry.update();
            idle();
        }

        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < 2000) {
            frontRight.setPower(1.0);
            telemetry.addData(">", "Front right");
            telemetry.update();
            idle();
        }

        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < 2000) {
            backRight.setPower(1.0);
            telemetry.addData(">", "Back right");
            telemetry.update();
            idle();
        }

        timer.reset();

        while (opModeIsActive() && timer.milliseconds() < 2000) {
            backLeft.setPower(1.0);
            telemetry.addData(">", "Back Left");
            telemetry.update();
            idle();
        }


        // Put your cleanup code here - it runs as the application shuts down
        telemetry.addData(">", "Done");
        telemetry.update();

    }
}
