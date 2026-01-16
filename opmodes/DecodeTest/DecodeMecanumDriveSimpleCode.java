package org.firstinspires.ftc.teamcode.opmodes.DecodeTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
@TeleOp(name = "Decode Test Mechanum Drive", group = "Test")
public class DecodeMecanumDriveSimpleCode extends LinearOpMode{
    @Override
    public void runOpMode() throws InterruptedException {

        // mutlipl power commands by this factor
        double powerReductionFront = 1.0;
        double powerReductionRear = 0.8;

        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("leftFrontMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("leftRearMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("rightFrontMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("rightRearMotor");

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
//        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
//        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
                double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
                double x = -gamepad1.right_stick_x * 1.1; // Counteract imperfect strafing
                double rx = -gamepad1.left_stick_x;

                // Denominator is the largest motor power (absolute value) or 1
                // This ensures all the powers maintain the same ratio,
                // but only if at least one is out of the range [-1, 1]
                double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
                double frontLeftPower = (y + x + rx) / denominator;
                double backLeftPower = (y - x + rx) / denominator;
                double frontRightPower = (y - x - rx) / denominator;
                double backRightPower = (y + x - rx) / denominator;

                frontLeftMotor.setPower(frontLeftPower * powerReductionFront);
                backLeftMotor.setPower(backLeftPower * powerReductionRear);
                frontRightMotor.setPower(frontRightPower * powerReductionFront);
                backRightMotor.setPower(backRightPower * powerReductionRear);
        }
    }
}