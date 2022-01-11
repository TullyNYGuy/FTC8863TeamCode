package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.CRServo8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class DuckSpinner implements FTCRobotSubsystem {

    private CRServo8863 duckSpinner;
    private DataLogging logFile;
    private boolean loggingOn = false;
    private boolean initComplete = false;
    private final String  DUCK_SPINNER_NAME = FreightFrenzyRobot.HardwareName.DUCK_SPINNER.hwName;

    public DuckSpinner(HardwareMap hardwareMap, Telemetry telemetry){
       duckSpinner = new CRServo8863(DUCK_SPINNER_NAME, hardwareMap,0.5, 0.5, .1, Servo.Direction.FORWARD, telemetry) ;
       duckSpinner.setSpeed(0);
       initComplete = true;
    }

    public void TurnOff(){
        duckSpinner.setSpeed(0);
    }

    public void TurnOn() {
        duckSpinner.setSpeed(1);
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
        duckSpinner.setSpeed(0);
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
