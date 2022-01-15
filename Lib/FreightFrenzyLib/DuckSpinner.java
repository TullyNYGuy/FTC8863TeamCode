package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;

public class DuckSpinner implements FTCRobotSubsystem {
    private enum SpinnerState{
        ON,
        OFF,
    }
    SpinnerState spinnerState = SpinnerState.OFF;
    private CRServo duckSpinner;
    private DataLogging logFile;
    private boolean loggingOn = false;
    private boolean initComplete = false;
    private final String  DUCK_SPINNER_NAME = "Duck Spinner";

    public DuckSpinner(HardwareMap hardwareMap, Telemetry telemetry){
        duckSpinner = hardwareMap.get(CRServo.class,FreightFrenzyRobotRoadRunner.HardwareName.DUCK_SPINNER.hwName);
        duckSpinner.setDirection(DcMotorSimple.Direction.FORWARD);
        duckSpinner.setPower(0);
       initComplete = true;
    }
    // Turns off the duck spinner
    public void turnOff(){
        duckSpinner.setPower(0);
        spinnerState = SpinnerState.OFF;
    }
    // Turns on the duck spinner
    public void turnOn() {
        duckSpinner.setPower(1);
        spinnerState = SpinnerState.ON;
    }
    //toggles the duck spinner
    public void toggleDuckSpinner(){
        if (spinnerState == SpinnerState.OFF){turnOn();}
        if (spinnerState == SpinnerState.ON){turnOff();}
    }
    @Override
    public String getName() {
        return DUCK_SPINNER_NAME;
    }

    @Override
    public boolean isInitComplete() {
        return initComplete;
    }

    @Override
    public boolean init(Configuration config) {
        return true;
    }

    @Override
    public void update() {

    }

    @Override
    public void shutdown() {
        duckSpinner.setPower(0);
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

    @Override
    public void timedUpdate(double timerValueMsec) {

    }
}
