package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;


import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayField.getVector2d;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoseStorage.RED_RIGHT_PARK_LOCATION_2;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoseStorage.RED_RIGHT_PARK_LOCATION_3;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayAutonomousStateMachine;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayField;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoseStorageForPowerPlayDrive;
import org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayRobot;

public class ITDAutoStateMachines {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        IDLE,
        START,
        WAIT_FOR_GET_READY_2_RUN,
        WAIT_FOR_M0VE_2_DELIVERY_POS,
        WAIT_FOR_DELIVERY_JOE,
        DELIVERY,
        WAIT_FOR_MOVE_2_SAMPLE,
        WAIT_FOR_SETUP_FOR_INTAKE,
        WAIT_FOR_INTAKE,
        WAIT_FOR_MOVE_2_SAMPLE2,
        WAIT_FOR_MOVE_2_SAMPLE3,
        WAIT_TO_MOVE_TO_SUBMERISLBE,

        COMPLETE
    }

    private States currentState;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private ITDRobot robot;
    private ElapsedTime timer;
    private DistanceUnit distanceUnits;
    private AngleUnit angleUnits;
    private boolean isComplete = false;

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;
    private int sampleNum = 0;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public boolean isComplete() {
        return isComplete;
    }

    public String getCurrentState() {
        return currentState.toString();
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ITDAutoStateMachines(ITDRobot robot, Telemetry telemetry) {
        this.robot = robot;

        currentState = States.IDLE;
        distanceUnits = DistanceUnit.INCH;
        angleUnits = AngleUnit.DEGREES;
        timer = new ElapsedTime();

        createTrajectories();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    public String getName() {
        return "Auto";
    }

    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
    }

    public void enableDataLogging() {
        enableLogging = true;
    }

    public void disableDataLogging() {
        enableLogging = false;
    }

    private void logState() {
        if (enableLogging && logFile != null) {
            logStateOnChange.log(getName() + " state = " + currentState.toString());
        }
    }

    private void logCommand(String command) {
        if (enableLogging && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    /**
     * Place all of the trajectories for the autonomous opmode in this method. This method gets
     * called from the constructor so that the trajectories are created when the autonomous object
     * is created.
     */
    public void createTrajectories() {
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void start() {
        currentState = States.START;
        isComplete = false;
        logCommand("start");
    }

    public void update() {
        switch (currentState) {
//            IDLE,
//                    START,
//                    WAIT_FOR_GET_READY_2_RUN,
//                    WAIT_FOR_M0VE_2_DELIVERY_POS,
//                    WAIT_FOR_DELIVERY_JOE,
//                    DELIVERY,
//                    WAIT_FOR_MOVE_2_SAMPLE,
//                    WAIT_FOR_SETUP_FOR_INTAKE,
//                    WAIT_FOR_INTAKE,
//                    WAIT_FOR_MOVE_2_SAMPLE2,
//                    WAIT_FOR_MOVE_2_SAMPLE3,
//                    WAIT_TO_MOVE_TO_SUBMERISLBE,
//
//                    COMPLETE
            case START:
                robot.intakeBucketController.getReadyToRun();
                currentState = States.WAIT_FOR_GET_READY_2_RUN;
                break;
            case WAIT_FOR_GET_READY_2_RUN:
                if (robot.intakeBucketController.isGetReadyToRunComplete()) {
                    // move to the delivery
                    currentState = States.WAIT_FOR_M0VE_2_DELIVERY_POS;
                }
                break;
            case WAIT_FOR_M0VE_2_DELIVERY_POS:
                // check if that delivery complETE!!
                robot.intakeBucketController.setupForDrivingBeforeDelivery();
                currentState = States.WAIT_FOR_DELIVERY_JOE;
                break;
            case WAIT_FOR_DELIVERY_JOE:
                if (robot.intakeBucketController.isSetupForDeliveryComplete()) {
                    robot.intakeBucketController.deliverSample();
                    currentState = States.DELIVERY;
                }
                break;
            case DELIVERY:
                if (robot.intakeBucketController.isDeliveryComplete()) {
                    sampleNum = sampleNum + 1;
                    switch (sampleNum) {
                        case 1:
                            // move to sample 1
                            break;
                        case 2:
                            // move to sample 2
                            break;
                        case 3:
                            //you get the point
                            break;
                        case 4:
                            currentState=States.COMPLETE;
                            break;
                    }
                    currentState = States.WAIT_FOR_MOVE_2_SAMPLE;
                }
                break;
            case WAIT_FOR_MOVE_2_SAMPLE:
                // check if that movement complete!!!
                robot.intakeBucketController.setupForIntake();
                currentState = States.WAIT_FOR_SETUP_FOR_INTAKE;
                break;
            case WAIT_FOR_SETUP_FOR_INTAKE:
                if (robot.intakeBucketController.isSetupForIntakeComplete()) {
                    robot.intakeBucketController.intake();
                    currentState = States.WAIT_FOR_INTAKE;
                }
                break;
            case WAIT_FOR_INTAKE:
                if(robot.intakeBucketController.isTransferComplete()){
                    ///movetodelivery
                currentState=States.WAIT_FOR_M0VE_2_DELIVERY_POS;
                }
                break;

            case WAIT_TO_MOVE_TO_SUBMERISLBE:
                break;
            case COMPLETE:
                break;

        }
    }
}