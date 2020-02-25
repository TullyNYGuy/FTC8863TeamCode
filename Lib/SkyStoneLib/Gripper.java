package org.firstinspires.ftc.teamcode.Lib.SkyStoneLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class Gripper implements FTCRobotSubsystem {

    private final static String SUBSYSTEM_NAME = "Gripper";

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    enum State {
        IDLE,
        GRIPPING,
        RELEASED,
        GRIPPED,
        RELEASING,
        INITTING,
        INIT_FINISHED
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Servo8863 gripperServo;


    private double releasePosition = 0.43;
    private double initPos = releasePosition;
    private double gripPosition = 0.8;
    private double homePos = releasePosition;

    private State gripperState;
    private State previousGripperState;

    private Telemetry telemetry;
    private ElapsedTime timer;


    private DataLogging logFile = null;
    private boolean loggingOn = false;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

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

    public State getGripperState() {
        return gripperState;
    }

    //*********************************************************************************************
    //          Constructors
    //
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public Gripper(HardwareMap hardwareMap, String servoName, Telemetry telemetry) {
        gripperServo = new Servo8863(servoName, hardwareMap, telemetry, homePos, releasePosition, gripPosition, initPos, Servo.Direction.FORWARD);
        this.telemetry = telemetry;
        timer = new ElapsedTime();
        timer.reset();
        gripperState = State.IDLE;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************


    private void log(String stringToLog) {
        if (logFile != null && loggingOn) {
            logFile.logData(stringToLog);

        }
    }

    private void logState(State gripperState) {
        if (logFile != null && loggingOn) {
            if (gripperState != previousGripperState) {
                logFile.logData("Gripper state is now ", gripperState.toString());
                previousGripperState = gripperState;
            }
        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    private void release() {
        gripperServo.goUp();
    }

    private void grip() {
        gripperServo.goDown();
    }

    @Override
    public String getName() {
        return SUBSYSTEM_NAME;
    }

    @Override
    public boolean isInitComplete() {
        if (gripperState == State.INIT_FINISHED) {
            return true;
        } else return false;
    }

    @Override
    public boolean init(Configuration config) {
        log("Gripper commanded to init");
        gripperServo.goInitPosition();
        timer.reset();
        gripperState = State.INITTING;
        return true;
    }

    public void gripBlock() {
        log("Gripper commanded to grip");
        grip();
        timer.reset();
        gripperState = State.GRIPPING;

    }

    public void releaseBlock() {
        log("Gripper commanded to release");
        release();
        timer.reset();
        gripperState = State.RELEASING;
    }

    @Override
    public void shutdown() {
        log("Gripper commanded to shutdown");
        release();
    }

    @Override
    public void timedUpdate(double timerValueMsec) {

    }

    @Override
    public void update() {
        switch (gripperState) {
            case IDLE:
                break;
            case INITTING:
                if (timer.milliseconds() > 1500) {
                    gripperState = State.INIT_FINISHED;
                }
                break;
            case INIT_FINISHED:
                break;

            case RELEASING:
                if (timer.milliseconds() > 1000) {
                    gripperState = State.RELEASED;
                    timer.reset();
                }
                break;
            case RELEASED:
                break;

            case GRIPPING:
                if (timer.milliseconds() > 1000) {
                    gripperState = State.GRIPPED;
                    timer.reset();
                }
                break;
            case GRIPPED:
                break;
        }
        logState(gripperState);
    }

    public boolean isGripComplete() {
        if (gripperState == State.GRIPPED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isReleaseComplete() {
        if (gripperState == State.RELEASED) {
            return true;
        } else {
            return false;
        }
    }
}
