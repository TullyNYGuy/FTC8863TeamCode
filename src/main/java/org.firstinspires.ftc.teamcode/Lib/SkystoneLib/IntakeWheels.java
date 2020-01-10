package org.firstinspires.ftc.teamcode.Lib.SkystoneLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

public class IntakeWheels {
    private DcMotor8863 right;
    private DcMotor8863 left;
    final private double motorSpeed = 0.7;
    private boolean direction;

    public IntakeWheels(DcMotor8863 right, DcMotor8863 left) {
        this.right = right;
        this.left = left;

    }

    public void init() {
        right.setMovementPerRev(360);
        left.setMovementPerRev(360);
        right.setPower(motorSpeed);
        left.setPower(motorSpeed);
        right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        left.runAtConstantPower(motorSpeed);
        right.runAtConstantPower(motorSpeed);
        intake();

    }

    public void intake() {
        right.setDirection(DcMotorSimple.Direction.FORWARD);
        left.setDirection(DcMotorSimple.Direction.REVERSE);
        direction = true;
    }

    public void outtake() {
        right.setDirection(DcMotorSimple.Direction.REVERSE);
        left.setDirection(DcMotorSimple.Direction.FORWARD);
        direction = false;
    }

    public void shutdown() {
        right.stop();
        left.stop();
    }

    public void switchDirection() {
        if (direction == false) {
            intake();
        } else {
            outtake();
        }
    }
}
