package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.tfod.Timer;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.LoopTimer;

public class DuckSpinner implements FTCRobotSubsystem {
    private enum SpinnerState{
        ON,
        OFF,
        AUTO_SPIN
    }
    private SpinnerState spinnerState;

    private CRServo duckSpinner;
    private DataLogging logFile;
    private boolean loggingOn = false;
    private boolean initComplete = false;
    private AllianceColor color = PersistantStorage.getAllianceColor();
    private final String  DUCK_SPINNER_NAME = "Duck Spinner";

    // flags used by this class

    // says if the automatic duck spin is complete
    private boolean autoSpinComplete = false;

    public DuckSpinner(HardwareMap hardwareMap, Telemetry telemetry){
        timer = new ElapsedTime();
        duckSpinner = hardwareMap.get(CRServo.class,FreightFrenzyRobotRoadRunner.HardwareName.DUCK_SPINNER.hwName);
        switch(color){
            case BLUE:duckSpinner.setDirection(DcMotorSimple.Direction.FORWARD);
            break;
            case RED:duckSpinner.setDirection(DcMotorSimple.Direction.REVERSE);
            break;
        }
        duckSpinner.setPower(0);
       initComplete = true;
       spinnerState = SpinnerState.OFF;
       autoSpinComplete = false;
    }

    // Turns off the duck spinner
    public void turnOff(){
        duckSpinner.setPower(0);
        spinnerState = SpinnerState.OFF;
        duckDone = true;
    }

    // Turns on the duck spinner
    public void turnOn() {
        duckSpinner.setPower(1);
        spinnerState = SpinnerState.ON;
        duckDone = false;
        timer.reset();
    }

    /**
     * Spin the duck spinner and automatically turn it off when complete. The determination of
     * complete is based on a timer.
     */
    public void autoSpin() {
        autoSpinComplete = false;
        timer.reset();
        turnOn();
        spinnerState = SpinnerState.AUTO_SPIN;
    }

    /**
     * Is the duck spinner complete?
     * @return
     */
    public boolean isComplete() {
        return autoSpinComplete;
    }


    private ElapsedTime timer;
    private boolean duckDone;
    public boolean spinTimeReached(){
        if(timer.milliseconds() > 3000){
            duckDone = true;
        }
        return duckDone;
    }


    //toggles the duck spinner
    public void toggleDuckSpinner(){
        if (spinnerState == SpinnerState.OFF){turnOn();}
        else if (spinnerState == SpinnerState.ON){turnOff();}
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
        switch (spinnerState) {
            case ON:
                break;
            case OFF:
                break;
            case AUTO_SPIN:
                if (timer.milliseconds() > 3000) {
                    // turnOff() also modifies the state to OFF so we don't need to do that here
                    autoSpinComplete = true;
                    turnOff();
                }
                break;
        }
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
