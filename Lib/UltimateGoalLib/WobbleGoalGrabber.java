package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class WobbleGoalGrabber implements FTCRobotSubsystem {


    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum States {
        HOME,
        GOAL_STORED,
        DROPPING_GOAL,
        EXTENDING_ARM,
        OPENING_GRABBER,
        RETRACTING_ARM,
        GOAL_DROPPED
    }

    private States currentState = States.GOAL_STORED;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private Servo8863 grabberServo;
    private final double GRABBER_CLOSE_POSITION = .3;
    private final double GRABBER_OPEN_POSITION = 0;

    private Servo8863 armRotationServo;
    private final double ARM_RETRACT_POSITION = .75;
    private final double ARM_EXTEND_POSITION = .13;

    private DataLogging logFile;
    private boolean loggingOn = false;

    private boolean commandComplete = true;
    private ElapsedTime timer;

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

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public WobbleGoalGrabber(HardwareMap hardwareMap, Telemetry telemetry) {
        grabberServo = new Servo8863(UltimateGoalRobotRoadRunner.HardwareName.GRABBER_SERVO.hwName, hardwareMap, telemetry);
        grabberServo.setDirection(Servo.Direction.FORWARD);
        grabberServo.setHomePosition(GRABBER_CLOSE_POSITION);
        grabberServo.setPositionOne(GRABBER_OPEN_POSITION);

        armRotationServo = new Servo8863(UltimateGoalRobotRoadRunner.HardwareName.ARM_ROTATION_SERVO.hwName, hardwareMap, telemetry);
        armRotationServo.setDirection(Servo.Direction.FORWARD);
        armRotationServo.setHomePosition(ARM_RETRACT_POSITION);
        armRotationServo.setPositionOne(ARM_EXTEND_POSITION);

        timer = new ElapsedTime();
        commandComplete = true;
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    public void extendArm() {
        armRotationServo.goPositionOne();
    }

    public void retractArm() {
        armRotationServo.goHome();
    }

    public void openGrabber() {
        grabberServo.goPositionOne();
    }

    public void closeGrabber() {
        grabberServo.goHome();
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void reset() {
        retractArm();
        closeGrabber();
    }

    @Override
    public boolean init(Configuration config) {
        reset();
        return true;
    }

    public void preloadWobbleGoalStart() {

    }

    public void preloadWobbleGoalComplete() {

    }

    public void dropGoal() {
        if (commandComplete) {
            commandComplete = false;
            currentState = States.DROPPING_GOAL;
        }
    }

    @Override
    public void update() {
        switch (currentState) {
            case HOME:
                break;
            case GOAL_STORED:
                break;
            case DROPPING_GOAL:
                extendArm();
                timer.reset();
                currentState = States.EXTENDING_ARM;
                break;
            case EXTENDING_ARM:
                if (timer.milliseconds() > 1000) {
                    openGrabber();
                    timer.reset();
                    currentState = States.OPENING_GRABBER;
                }
                break;
            case OPENING_GRABBER:
                if (timer.milliseconds() > 500) {
                    retractArm();
                    timer.reset();
                    currentState = States.RETRACTING_ARM;
                }
                break;
            case RETRACTING_ARM:
                if (timer.milliseconds() > 1000) {
                    closeGrabber();
                    timer.reset();
                    currentState = States.HOME;
                    commandComplete = true;
                }
                break;
        }
    }

    public boolean isComplete() {
        return commandComplete;
    }

    @Override
    public String getName() {
        return "wobble goal grabber";
    }

    @Override
    public boolean isInitComplete() {
        return true;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void timedUpdate(double timerValueMsec) {
    }
}
