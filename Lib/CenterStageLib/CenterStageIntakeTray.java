package org.firstinspires.ftc.teamcode.Lib.CenterStageLib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class CenterStageIntakeTray implements FTCRobotSubsystem {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum Command {
        ON,
        OFF
    }
    private Command command = Command.OFF;

    public enum State {
        PRE_INIT,
        READY,
        LEFT_PIXEL_PRESENT,
        LEFT_PIXEL_GRABBED,
        LEFT_PIXEL_RELEASED,
        RIGHT_PIXEL_PRESENT,
        RIGHT_PIXEL_GRABBED,
        RIGHT_PIXEL_RELEASED,
        BOTH_PIXELS_GRABBED
    }
    private State state = State.PRE_INIT;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private final String INTAKE_TRAY_NAME = "intakeTray";

    private CenterStageIntakeColorSensorLeft leftColorSensor;
    private CenterStageIntakeColorSensorRight rightColorSensor;
    private CenterStageFingerServoLeft leftFingerServo;
    private CenterStageFingerServoRight rightFingerServo;

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public CenterStageIntakeTray(HardwareMap hardwareMap, Telemetry telemetry) {
        leftColorSensor = new CenterStageIntakeColorSensorLeft(hardwareMap, telemetry);
        rightColorSensor = new CenterStageIntakeColorSensorRight(hardwareMap,telemetry);
        leftFingerServo = new CenterStageFingerServoLeft(hardwareMap, telemetry);
        rightFingerServo = new CenterStageFingerServoRight(hardwareMap, telemetry);

        command = Command.OFF;
        state = State.PRE_INIT;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void on() {
        command = Command.ON;
        state = State.READY;
    }

    public void off() {
        command = Command.OFF;
    }

    @Override
    public String getName() {
        return INTAKE_TRAY_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (leftFingerServo.isPositionReached() && rightFingerServo.isPositionReached()) {
            state = State.READY;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean init(Configuration config) {
        leftFingerServo.open();
        rightFingerServo.open();
        return true;
    }

    @Override
    public void update() {

    }

    @Override
    public void shutdown() {
        leftFingerServo.open();
        rightFingerServo.open();
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        enableLogging = true;
    }

    @Override
    public void disableDataLogging() {
        enableLogging = false;
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }
}
