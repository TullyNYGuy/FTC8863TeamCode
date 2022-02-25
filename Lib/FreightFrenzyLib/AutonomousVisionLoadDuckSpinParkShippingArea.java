package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Pose2d8863;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.Pipelines.ShippingElementPipeline;

public class AutonomousVisionLoadDuckSpinParkShippingArea implements AutonomousStateMachineFreightFrenzy {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        IDLE,
        START,
        MOVING_TO_HUB,
        READY_TO_DEPOSIT,
        MOVING_TO_DUCKS,
        AT_DUCK,
        DUCK_SPINNING,
        APPROACHING_STORAGE,
        DEPOSITING,
        GO_TO_SHIPPING_AREA,
        EXTENDING_LIFT,
        DEPOSIT_DONE,
        COMPLETE;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Pose2d START_POSE;
    private States currentState;
    private FreightFrenzyRobotRoadRunner robot;
    private FreightFrenzyField field;
    private ElapsedTime timer;
    private DistanceUnit distanceUnits;
    private AngleUnit angleUnits;
    private Pose2d hubDumpPose;
    private Trajectory trajectoryToHub;
    private Trajectory trajectoryToDucks;
    private Trajectory trajectoryToShippingArea;
    private Trajectory trajectoryToPassage;
    private Trajectory trajectoryToWarehoue;
    private Trajectory trajectoryToWaypoint;
    private Trajectory trajectoryToWaypointReturn;
    private double distanceToTopGoal = 0;
    private double distanceToLeftPowerShot = 0;
    private double angleOfShot = 0;
    private boolean isComplete = false;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public AutonomousVisionLoadDuckSpinParkShippingArea(FreightFrenzyRobotRoadRunner robot, FreightFrenzyField field, Telemetry telemetry) {
        this.robot = robot;
        this.field = field;
        switch (PersistantStorage.getShippingElementPosition()) {
            case CENTER:
                robot.freightSystem.setMiddle();
                switch(PersistantStorage.getStartSpot()){
                    case BLUE_WALL:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_MID_BLUE_WALL;
                        break;
                    case RED_WALL:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_MID_RED_WALL;
                        break;
                    case BLUE_WAREHOUSE:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_MID_BLUE_WAREHOUSE;
                        break;
                    case RED_WAREHOUSE:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_MID_RED_WAREHOUSE;
                        break;
                }
                break;
            case LEFT:
                robot.freightSystem.setBottom();
                switch(PersistantStorage.getStartSpot()){
                    case BLUE_WALL:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_LOW_BLUE_WALL;
                        break;
                    case RED_WALL:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_LOW_RED_WALL;
                        break;
                    case BLUE_WAREHOUSE:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_LOW_BLUE_WAREHOUSE;
                        break;
                    case RED_WAREHOUSE:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_LOW_RED_WAREHOUSE;
                        break;
                }
                break;
            case RIGHT:
                robot.freightSystem.setTop();
                switch(PersistantStorage.getStartSpot()){
                    case BLUE_WALL:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_HIGH_HUB_BLUE_WALL;
                        break;
                    case RED_WALL:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_HIGH_HUB_RED_WALL;
                        break;
                    case BLUE_WAREHOUSE:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_HIGH_HUB_BLUE_WAREHOUSE;
                        break;
                    case RED_WAREHOUSE:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_HIGH_HUB_RED_WAREHOUSE;
                        break;
                }
                break;
        }
        currentState = States.IDLE;
        distanceUnits = DistanceUnit.INCH;
        angleUnits = AngleUnit.DEGREES;
        timer = new ElapsedTime();
        START_POSE = PersistantStorage.getStartPosition();
        PoseStorageFF.retreiveStartPose();
        createTrajectories();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    /**
     * Place all of the trajectories for the autonomous opmode in this method. This method gets
     * called from the constructor so that the trajectories are created when the autonomous object
     * is created.
     */
    @Override
    public void createTrajectories() {

        //  THIS IS LOOKING GOOD TANYA. A COUPLE OF COMMENTS (in the form of // todo).


        if (PersistantStorage.getAllianceColor() == AllianceColor.BLUE) {
            trajectoryToWaypoint = robot.mecanum.trajectoryBuilder(PoseStorageFF.START_POSE)
                    .lineToLinearHeading(PoseStorageFF.WAYPOINT_BLUE_HUB)
                    .build();
            trajectoryToHub = robot.mecanum.trajectoryBuilder(trajectoryToWaypoint.end())
                    // todo What about heading? Will it always stay the same?
                    .lineToLinearHeading(hubDumpPose)
                    .build();
            trajectoryToWaypointReturn = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
                    .lineToLinearHeading(PoseStorageFF.WAYPOINT_BLUE_HUB)
                    .build();
            trajectoryToDucks = robot.mecanum.trajectoryBuilder(trajectoryToWaypointReturn.end())
                    .lineToLinearHeading(PoseStorageFF.DUCK_SPINNER_BLUE)
                    .build();
            trajectoryToShippingArea = robot.mecanum.trajectoryBuilder(trajectoryToDucks.end())
                    .lineToLinearHeading(PoseStorageFF.STORAGE_BLUE)
                    .build();


        } else {
            trajectoryToWaypoint = robot.mecanum.trajectoryBuilder(PoseStorageFF.START_POSE)
                    .lineToLinearHeading(PoseStorageFF.WAYPOINT_RED_HUB)
                    .build();
            trajectoryToHub = robot.mecanum.trajectoryBuilder(trajectoryToWaypoint.end())
                    // todo What about heading? Will it always stay the same?
                    .lineToLinearHeading(hubDumpPose)
                    .build();
            trajectoryToWaypointReturn = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
                    .lineToLinearHeading(PoseStorageFF.WAYPOINT_RED_HUB)
                    .build();
            trajectoryToDucks = robot.mecanum.trajectoryBuilder(trajectoryToWaypointReturn.end())
                    .lineToLinearHeading(PoseStorageFF.DUCK_SPINNER_RED)
                    .build();
            trajectoryToShippingArea = robot.mecanum.trajectoryBuilder(trajectoryToDucks.end())
                    .lineToLinearHeading(PoseStorageFF.STORAGE_RED)
                    .build();

        }
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    @Override
    public void start() {
        currentState = States.START;
        isComplete = false;
    }
    @Override
    public String getCurrentState(){
        return currentState.toString();
    }

    @Override
    public void update() {
        switch (currentState) {
            case START:
                isComplete = false;
                robot.mecanum.setPoseEstimate(PoseStorageFF.START_POSE);
                robot.mecanum.followTrajectory(trajectoryToWaypoint);

                currentState = States.MOVING_TO_HUB;
                break;
            case MOVING_TO_HUB:
                if (!robot.mecanum.isBusy()) {
                    robot.mecanum.followTrajectory(trajectoryToHub);
                    // todo Check the next state. Is it correct?
                    currentState = States.EXTENDING_LIFT;
                }
                break;
            case EXTENDING_LIFT:
                if(!robot.mecanum.isBusy()) {
                    robot.freightSystem.extend();
                    currentState = States.DEPOSITING;
                }
                break;
            case DEPOSITING:
                if (robot.freightSystem.isReadyToDump()) {
                    robot.freightSystem.dump();
                    currentState = States.DEPOSIT_DONE;
                }
                break;
            case DEPOSIT_DONE:
                if(robot.freightSystem.isRetractionComplete()) {
                    robot.mecanum.followTrajectory(trajectoryToWaypointReturn);
                    currentState = States.MOVING_TO_DUCKS;
                }
                //no. bad. the lift automatically retracts you dont need to tell it to//
/*
                if (robot.freightSystem.isDumpComplete()) {
                    robot.lift.retract();

                    currentState = States.MOVING_TO_DUCKS;
                }*/
                break;
            case MOVING_TO_DUCKS:

                if (robot.freightSystem.isRetractionComplete() &&!robot.mecanum.isBusy()) {
                    robot.mecanum.followTrajectory(trajectoryToDucks);

                    //robot.mecanum.followTrajectoryAsync(trajectoryToParkPosition);
                    currentState = States.AT_DUCK;
                }
                break;
            case AT_DUCK:
                if (!robot.mecanum.isBusy()) {
                    // todo you should not have to know anything about how the duck spinner operates
                    // It knows how to do that.
                    //robot.duckSpinner.turnOn();
                    robot.duckSpinner.autoSpin();
                    currentState = States.DUCK_SPINNING;
                }
                break;
            case DUCK_SPINNING:
                // todo you should not have to know anything about how the duck spinner operates
                // It knows how to do that. So you should don't have to know about times or how it
                // does it's thing. All you need to know is that it did its thing and it is done.
                //if (robot.duckSpinner.spinTimeReached()) {
                    //robot.duckSpinner.turnOff();
                if (robot.duckSpinner.isComplete()) {
                    robot.mecanum.followTrajectory(trajectoryToShippingArea);
                    currentState = States.APPROACHING_STORAGE;
                }
                break;
            case APPROACHING_STORAGE:
                if (!robot.mecanum.isBusy()) {
                    currentState = States.COMPLETE;
                }
                break;

            case COMPLETE:
                isComplete = true;
                break;
        }
    }
}
