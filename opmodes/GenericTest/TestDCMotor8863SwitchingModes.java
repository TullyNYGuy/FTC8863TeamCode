package org.firstinspires.ftc.teamcode.opmodes.GenericTest;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

/**
 * This Opmode is a shell for a linear OpMode. Copy this file and fill in your code as indicated.
 */
@TeleOp(name = "Test DC Motor 8863 Switching Modes", group = "Test")
//@Disabled
public class TestDCMotor8863SwitchingModes extends LinearOpMode {

    // Put your variable declarations her
    private DcMotor8863 intakeSweeperMotor;
    private ElapsedTime timer;
    private double timerInterval = 2000;

    private double power = .5;
    private double speed = .2;
    private double RPM = 300;
    private double position = 360;

    @Override
    public void runOpMode() {

        // Put your initializations here
        intakeSweeperMotor = new DcMotor8863("intakeSweeperMotor", hardwareMap);
        intakeSweeperMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_3_7_ORBITAL);
        intakeSweeperMotor.setMovementPerRev(360);
        intakeSweeperMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        timer = new ElapsedTime();

        // Wait for the start button
        telemetry.addData(">", "Press Start to run");
        telemetry.update();
        waitForStart();

        // Put your calls here - they will not run in a loop

        timer.reset();
        power = .1;
        intakeSweeperMotor.runAtConstantPower(power);
        while (opModeIsActive() && timer.milliseconds() < timerInterval) {
            telemetry.addData("Running at power = ", power);
            telemetry.addData("RPM = ", intakeSweeperMotor.getCurrentRPM());
            telemetry.update();
            idle();
        }

        position = 90;
        intakeSweeperMotor.moveToPosition(.5, position, DcMotor8863.FinishBehavior.HOLD);
        while (opModeIsActive() && !intakeSweeperMotor.isMovementComplete()) {
            intakeSweeperMotor.update();
            telemetry.addData("Moving to position = ", power);
            telemetry.update();
        }

        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < timerInterval * 3) {
            telemetry.addData("At position = ", intakeSweeperMotor.getPositionInTermsOfAttachment());
            telemetry.update();
            idle();
        }

        timer.reset();
        power = .5;
        intakeSweeperMotor.runAtConstantPower(power);
        while (opModeIsActive() && timer.milliseconds() < timerInterval) {
            telemetry.addData("Running at power = ", power);
            telemetry.addData("RPM = ", intakeSweeperMotor.getCurrentRPM());
            telemetry.update();
            idle();
        }

        timer.reset();
        RPM = 1200;
        intakeSweeperMotor.runAtConstantRPM(RPM);
        while (opModeIsActive() && timer.milliseconds() < timerInterval * 2) {
            telemetry.addData("Running at RPM = ", RPM);
            telemetry.addData("RPM = ", intakeSweeperMotor.getCurrentRPM());
            telemetry.update();
            idle();
        }

        timer.reset();
        power = .1;
        intakeSweeperMotor.runAtConstantPower(power);
        while (opModeIsActive() && timer.milliseconds() < timerInterval) {
            telemetry.addData("Running at power = ", power);
            telemetry.addData("RPM = ", intakeSweeperMotor.getCurrentRPM());
            telemetry.update();
            idle();
        }

        // Run at constant speed does not work with a 3.7 motor since they have the constants wrong
        // It gives a speed that is not proportional to the max RPM of the motor.
        timer.reset();
        speed = .3;
        intakeSweeperMotor.runAtConstantSpeed(speed);
        while (opModeIsActive() && timer.milliseconds() < timerInterval) {
            telemetry.addData("Running at constant speed = ", speed);
            telemetry.addData("RPM = ", intakeSweeperMotor.getCurrentRPM());
            telemetry.update();
            idle();
        }

        timer.reset();
        speed = 1.0;
        intakeSweeperMotor.runAtConstantSpeed(speed);
        while (opModeIsActive() && timer.milliseconds() < timerInterval) {
            telemetry.addData("Running at constant speed = ", speed);
            telemetry.addData("RPM = ", intakeSweeperMotor.getCurrentRPM());
            telemetry.update();
            idle();
        }

        timer.reset();
        power = 1.0;
        intakeSweeperMotor.runAtConstantPower(power);
        while (opModeIsActive() && timer.milliseconds() < timerInterval) {
            telemetry.addData("Running at power = ", power);
            telemetry.addData("RPM = ", intakeSweeperMotor.getCurrentRPM());
            telemetry.update();
            idle();
        }
    }
}
