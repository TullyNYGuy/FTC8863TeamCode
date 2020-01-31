package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;

public class IntakeWheels {
    private DcMotor8863 right;
    private DcMotor8863 left;
    final private double motorSpeed = 1.0;
    private boolean direction;

    public IntakeWheels(DcMotor8863 right, DcMotor8863 left) {
        this.right = right;
        this.left = left;

    }

    public void init() {
        right.setMovementPerRev(360);
        left.setMovementPerRev(360);

        right.setDirection(DcMotorSimple.Direction.FORWARD);
        left.setDirection(DcMotorSimple.Direction.FORWARD);
        right.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        left.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        left.runAtConstantPower(motorSpeed);
        right.runAtConstantPower(motorSpeed);
        intake();

    }

    public void intake() {
        right.setPower(motorSpeed);
        left.setPower(motorSpeed);
        direction = true;
    }

    public void outtake() {
        right.setPower(-motorSpeed);
        left.setPower(-motorSpeed);
        direction = false;
    }

    public void shutdown() {
        right.stop();
        left.stop();
    }

    public void stop() {
        right.setPower(0);
        left.setPower(0);
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
