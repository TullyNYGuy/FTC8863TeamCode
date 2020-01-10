package org.firstinspires.ftc.teamcode.Lib.SkystoneLib;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

public class IntakeWheels {
    private DcMotor8863 right;
    private DcMotor8863 left;
    final private double motorSpeed = 0.7;

    public IntakeWheels(DcMotor8863 right, DcMotor8863 left) {
        this.right = right;
        this.left = left;

    }

    public void init() {
        right.setPower(motorSpeed);
        left.setPower(motorSpeed);
        intake();
    }

    public void intake() {
        right.setDirection(DcMotorSimple.Direction.FORWARD);
        left.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void outtake() {
        right.setDirection(DcMotorSimple.Direction.REVERSE);
        left.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void shutdown() {
        right.stop();
        left.stop();
    }
}
