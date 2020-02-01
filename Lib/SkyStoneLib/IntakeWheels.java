package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

public class IntakeWheels {
    private DcMotor8863 rightIntakeMotor;
    private DcMotor8863 leftIntakeMotor;
    final private double motorSpeed = 1.0;
    private boolean direction;

    public IntakeWheels(String rightIntakeMotorName, String leftIntakeMotorName, HardwareMap hardwareMap) {
        DcMotor8863 rightIntakeMotor = new DcMotor8863(rightIntakeMotorName, hardwareMap);
        DcMotor8863 leftIntakeMotor = new DcMotor8863(leftIntakeMotorName, hardwareMap);

        rightIntakeMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        leftIntakeMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        rightIntakeMotor.setMovementPerRev(360);
        leftIntakeMotor.setMovementPerRev(360);

        rightIntakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftIntakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightIntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftIntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftIntakeMotor.runAtConstantPower(motorSpeed);
        rightIntakeMotor.runAtConstantPower(motorSpeed);
    }

    public void init() {
    }

    public boolean isInitComplete() {
        return true;
    }

    public void update() {
    }

    public void intake() {
        rightIntakeMotor.setPower(motorSpeed);
        leftIntakeMotor.setPower(motorSpeed);
        direction = true;
    }

    public void outtake() {
        rightIntakeMotor.setPower(-motorSpeed);
        leftIntakeMotor.setPower(-motorSpeed);
        direction = false;
    }

    public void shutdown() {
        rightIntakeMotor.stop();
        leftIntakeMotor.stop();
    }

    public void stop() {
        rightIntakeMotor.setPower(0);
        leftIntakeMotor.setPower(0);
    }

    public void switchDirection() {
        if (direction == false) {
            intake();
        } else {
            outtake();
        }
        direction = !direction;
    }
}
