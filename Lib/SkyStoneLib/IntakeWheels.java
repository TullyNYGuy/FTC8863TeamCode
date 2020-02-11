package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class IntakeWheels implements FTCRobotSubsystem {

    private enum IntakeDirection {
        INTAKE,
        OUTTAKE
    }

    private IntakeDirection intakeDirection;

    private final static String SUBSYSTEM_NAME = "IntakeWheels";

    private DcMotor8863 rightIntakeMotor;
    private DcMotor8863 leftIntakeMotor;
    final private double motorSpeed = 1.0;

    public IntakeWheels(HardwareMap hardwareMap, String rightIntakeMotorName, String leftIntakeMotorName) {
        this.rightIntakeMotor = new DcMotor8863(rightIntakeMotorName, hardwareMap);
        this.leftIntakeMotor = new DcMotor8863(leftIntakeMotorName, hardwareMap);

        rightIntakeMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        rightIntakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightIntakeMotor.setMovementPerRev(360);
        rightIntakeMotor.runAtConstantPower(0);

        leftIntakeMotor.setMotorType(DcMotor8863.MotorType.ANDYMARK_40);
        leftIntakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        leftIntakeMotor.setMovementPerRev(360);
        leftIntakeMotor.runAtConstantPower(0);

        intakeDirection = IntakeDirection.INTAKE;
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
        stop();
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    public void intake() {
        rightIntakeMotor.setPower(motorSpeed);
        leftIntakeMotor.setPower(motorSpeed);
        intakeDirection = IntakeDirection.INTAKE;
    }

    public void outtake() {
        rightIntakeMotor.setPower(-motorSpeed);
        leftIntakeMotor.setPower(-motorSpeed);
        intakeDirection = IntakeDirection.OUTTAKE;
    }

    public void stop() {
        rightIntakeMotor.setPower(0);
        leftIntakeMotor.setPower(0);
    }

    public void switchDirection() {
        if (intakeDirection == IntakeDirection.OUTTAKE) {
            intake();
        } else {
            outtake();
        }
    }
}
