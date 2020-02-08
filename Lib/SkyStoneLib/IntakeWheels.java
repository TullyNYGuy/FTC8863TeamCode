package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class IntakeWheels implements FTCRobotSubsystem {

    private final static String SUBSYSTEM_NAME = "IntakeWheels";

    private DcMotor8863 rightIntakeMotor;
    private DcMotor8863 leftIntakeMotor;
    final private double motorSpeed = 1.0;
    private boolean direction;

    public IntakeWheels(HardwareMap hardwareMap, String rightIntakeMotorName, String leftIntakeMotorName) {
        this.rightIntakeMotor = new DcMotor8863(rightIntakeMotorName, hardwareMap);
        this.leftIntakeMotor = new DcMotor8863(leftIntakeMotorName, hardwareMap);

        rightIntakeMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        rightIntakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightIntakeMotor.setMovementPerRev(360);
        rightIntakeMotor.runAtConstantPower(0);

        leftIntakeMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        leftIntakeMotor.setMovementPerRev(360);
        leftIntakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftIntakeMotor.runAtConstantPower(0);

    }

    @Override
    public boolean init(Configuration config) {
        return true;
    }

    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public void update() {
    }

    @Override
    public void shutdown() {
        rightIntakeMotor.stop();
        leftIntakeMotor.stop();
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
