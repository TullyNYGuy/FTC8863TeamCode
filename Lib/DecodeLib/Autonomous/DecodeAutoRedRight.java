package org.firstinspires.ftc.teamcode.Lib.DecodeLib.Autonomous;

import org.firstinspires.ftc.teamcode.Lib.DecodeLib.DecodeRobot;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.FTCRobotSubsystem;

public class DecodeAutoRedRight implements DecodeAutonomousStateMachine {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
public enum AutoStates{
    WAIT_FOR_START,
    WAIT_FOR_LIMELIGHT_LOCK,
    WAIT_FOR_SHOOTER_SPIN,
    READY_TO_SHOOT,
    WAIT_FOR_SHOOT_TWO_COMPLETE,
    WAIT_FOR_SHOOTER_SPIN_AGAIN,
    WAIT_FOR_SHOOT_ONE_COMPLETE,
    WAIT_FOR_MOVEMENT_COMPLETE,
    COMPLETE;
    }
    private AutoStates currentState = AutoStates.WAIT_FOR_START;
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private DecodeRobot robot;

    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    //*********************************************************************************************
    //          PROPERTIES AND GETTER and SETTER Methods For Implementing DecodeAutonomousStateMachine
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    /**
     * Property that holds a log file
     */
    private DataLogging logFile;
    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
    }

    /**
     * Property that holds whether data is being logged into the log file.
     */
    private boolean loggingOn = false;

    @Override
    public void enableDataLogging() {
        this.loggingOn = true;
    }

    @Override
    public void disableDataLogging() {
        this.loggingOn = false;
    }

    private String subsystemName;

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public DecodeAutoRedRight(DecodeRobot robot) {
        this.robot = robot;
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


    //*********************************************************************************************
    //          METHODS needed to implement FTCRobotSubsystem
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    @Override
    public void start() {

    }

    @Override
    public void createMovements() {

    }

    @Override
    public void update() {
        switch (currentState) {
            case WAIT_FOR_START:
                //Check if play button is pressed
                currentState=AutoStates.WAIT_FOR_LIMELIGHT_LOCK;
                break;
            case WAIT_FOR_LIMELIGHT_LOCK:
                //Wait for the limelight to lock onto the april tag
                break;
            case WAIT_FOR_SHOOTER_SPIN:
                //Wait for the shooter wheel to get up to speed
                //If up to speed, shoot two
                currentState = AutoStates.WAIT_FOR_SHOOT_TWO_COMPLETE;
                break;
            case WAIT_FOR_SHOOT_TWO_COMPLETE:
                //If shoot two is done, wait for the shooter to come up to speed agian
                break;
            case WAIT_FOR_SHOOTER_SPIN_AGAIN:
                //If shooters up to speed, shoot one
                break;
            case WAIT_FOR_SHOOT_ONE_COMPLETE:
                //If done, turn motors on and start timer
                break;
            case WAIT_FOR_MOVEMENT_COMPLETE:
                //If done, stop the motors and do NOTHING
                break;
            case COMPLETE:
                break;
        }
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public String getCurrentState() {
        return "";
    }

    @Override
    public String getName() {
        return subsystemName;
    }

//    @Override
//    public boolean isInitComplete() {
//        return true;
//    }
//
//    @Override
//    public void shutdown() {
//    }
//
//    @Override
//    public boolean init(Configuration config) {
//        return true;
//    }
}
