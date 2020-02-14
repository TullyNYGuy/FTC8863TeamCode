package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Switch;

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

    private DataLogging logFile = null;
    private boolean loggingOn = false;

    private Switch intakeSwitchBackLeft;
    private Switch intakeSwitchBackRight;
    private Switch intakeSwitchFrontLeft;
    private Switch intakeSwitchFrontRight;

    private ElapsedTime timer;

    public IntakeWheels(HardwareMap hardwareMap, String rightIntakeMotorName, String leftIntakeMotorName,
                        String intakeSwitchBackLeftName, String intakeSwitchBackRightName,
                        String intakeSwitchFrontLeftName, String intakeSwitchFrontRightName) {
        this.intakeSwitchBackLeft = new Switch(hardwareMap, intakeSwitchBackLeftName, Switch.SwitchType.NORMALLY_OPEN);
        this.intakeSwitchBackRight = new Switch(hardwareMap, intakeSwitchBackRightName, Switch.SwitchType.NORMALLY_OPEN);
        this.intakeSwitchFrontLeft = new Switch(hardwareMap, intakeSwitchFrontLeftName, Switch.SwitchType.NORMALLY_OPEN);
        this.intakeSwitchFrontRight = new Switch(hardwareMap, intakeSwitchFrontLeftName, Switch.SwitchType.NORMALLY_OPEN);

        this.rightIntakeMotor = new DcMotor8863(rightIntakeMotorName, hardwareMap);
        this.leftIntakeMotor = new DcMotor8863(leftIntakeMotorName, hardwareMap);
        timer = new ElapsedTime();
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
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    private void log(String stringToLog) {
        if (logFile != null && loggingOn) {
            logFile.logData(stringToLog);

        }
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
        if ((intakeSwitchBackLeft.isPressed() || intakeSwitchBackRight.isPressed() ||
                intakeSwitchFrontLeft.isPressed() || intakeSwitchFrontRight.isPressed())
                && intakeDirection == IntakeDirection.INTAKE) {
            logFile.logData("Intake switch pressed, stopping intake");
            stop();
        }
    }

    @Override
    public void shutdown() {
        log("Intake wheels commanded to shutdown");
        stop();
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    public void intake() {
        rightIntakeMotor.setPower(motorSpeed);
        leftIntakeMotor.setPower(motorSpeed);
        intakeDirection = IntakeDirection.INTAKE;
        log("Intake wheels commanded to intake");
    }

    public void outtake() {
        rightIntakeMotor.setPower(-motorSpeed);
        leftIntakeMotor.setPower(-motorSpeed);
        intakeDirection = IntakeDirection.OUTTAKE;
        timer.reset();
        log("Intake wheels commanded to outtake");
        if (timer.milliseconds() > 2000) {
            intakeDirection = IntakeDirection.INTAKE;
            log("Intake wheels automatically set to intake");
        }
    }

    public void stop() {
        rightIntakeMotor.setPower(0);
        leftIntakeMotor.setPower(0);
        log("Intake wheels commanded to stop");
    }

    public void switchDirection() {
        if (intakeDirection == IntakeDirection.OUTTAKE) {
            intake();
        } else {
            outtake();
        }
    }
}
