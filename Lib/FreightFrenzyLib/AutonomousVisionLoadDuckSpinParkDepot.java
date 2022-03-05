package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class AutonomousVisionLoadDuckSpinParkDepot implements AutonomousStateMachineFreightFrenzy {

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
        APPROACHING_SIDE,
        GOING_TO_PASSAGE,
        DEPOSITING,
        GO_TO_WAREHOUSE,
        COMPLETE,
        EXTENDING_LIFT,
        DEPOSIT_DONE,
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
    private Trajectory trajectoryToWaypoint;
    private Trajectory trajectoryToWaypointReturn;
    private Trajectory trajectoryToHub;
    private Trajectory trajectoryToDucks;
    private Trajectory trajectoryToPassageApproach;
    private Trajectory trajectoryToPassage;
    private Trajectory trajectoryToWarehoue;

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
    @Override
    public String getCurrentState(){
        return currentState.toString();
    }

    @Override
    public void checkShippingPositionAgain() {

    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public AutonomousVisionLoadDuckSpinParkDepot(FreightFrenzyRobotRoadRunner robot, FreightFrenzyField field, Telemetry telemetry) {
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
                        hubDumpPose = PoseStorageFF.DELIVER_TO_TOP_BLUE_WALL;
                        break;
                    case RED_WALL:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_TOP_RED_WALL;
                        break;
                    case BLUE_WAREHOUSE:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_TOP_BLUE_WAREHOUSE;
                        break;
                    case RED_WAREHOUSE:
                        hubDumpPose = PoseStorageFF.DELIVER_TO_TOP_RED_WAREHOUSE;
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


        if (PersistantStorage.getAllianceColor() == AllianceColor.BLUE) {
            trajectoryToWaypoint = robot.mecanum.trajectoryBuilder(PoseStorageFF.START_POSE)
                    .lineToLinearHeading(PoseStorageFF.WAYPOINT_BLUE_HUB)
                    .build();
            trajectoryToHub = robot.mecanum.trajectoryBuilder(trajectoryToWaypoint.end())
                    .lineToLinearHeading(hubDumpPose)
                    .build();
            trajectoryToWaypointReturn = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
                    .lineToLinearHeading(PoseStorageFF.WAYPOINT_BLUE_HUB)
                    .build();
            trajectoryToDucks = robot.mecanum.trajectoryBuilder(trajectoryToWaypointReturn.end())
                    .lineToLinearHeading(PoseStorageFF.DUCK_SPINNER_BLUE)
                    .build();
            trajectoryToPassageApproach = robot.mecanum.trajectoryBuilder(trajectoryToDucks.end())
                    .lineToLinearHeading(PoseStorageFF.BLUE_SIDE_PASSAGE_APPROACH)
                    .build();
            trajectoryToPassage = robot.mecanum.trajectoryBuilder(trajectoryToPassageApproach.end())
                    .lineToLinearHeading(PoseStorageFF.SIDE_PASSAGE_BLUE)
                    .build();
            trajectoryToWarehoue = robot.mecanum.trajectoryBuilder(trajectoryToPassage.end())
                    .lineToLinearHeading(PoseStorageFF.FREIGHT_BLUE)
                    .build();
        } else {
            trajectoryToWaypoint = robot.mecanum.trajectoryBuilder(PoseStorageFF.START_POSE)
                    .lineToLinearHeading(PoseStorageFF.WAYPOINT_RED_HUB)
                    .build();
            trajectoryToHub = robot.mecanum.trajectoryBuilder(trajectoryToWaypoint.end())
                    .lineToLinearHeading(hubDumpPose)
                    .build();
            trajectoryToWaypointReturn = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
                    .lineToLinearHeading(PoseStorageFF.WAYPOINT_RED_HUB)
                    .build();
            trajectoryToDucks = robot.mecanum.trajectoryBuilder(trajectoryToWaypointReturn.end())
                    .lineToLinearHeading(PoseStorageFF.DUCK_SPINNER_RED)
                    .build();
            trajectoryToPassageApproach = robot.mecanum.trajectoryBuilder(trajectoryToDucks.end())
                    .lineToLinearHeading(PoseStorageFF.RED_SIDE_PASSAGE_APPROACH)
                    .build();
            trajectoryToPassage = robot.mecanum.trajectoryBuilder(trajectoryToPassageApproach.end())
                    .lineToLinearHeading(PoseStorageFF.SIDE_PASSAGE_RED)
                    .build();
            trajectoryToWarehoue = robot.mecanum.trajectoryBuilder(trajectoryToPassage.end())
                    .lineToLinearHeading(PoseStorageFF.FREIGHT_RED)
                    .build();
        }
    }

        //*********************************************************************************************
        //          MAJOR METHODS
        //
        // public methods that give the class its functionality
        //*********************************************************************************************

        @Override
        public void start () {
            currentState = States.START;
            isComplete = false;
        }

        @Override
        public void update () {
            switch (currentState) {
                case START:
                    isComplete = false;
                    robot.mecanum.setPoseEstimate(PoseStorageFF.START_POSE);
                    robot.mecanum.followTrajectory(trajectoryToWaypoint);
                    // there was something unecesary here. it is gone now. - kellen
                    currentState = States.MOVING_TO_HUB;
                    break;
                case MOVING_TO_HUB:
                    if (!robot.mecanum.isBusy()) {
                        robot.mecanum.followTrajectory(trajectoryToHub);
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
                    if(robot.freightSystem.isDumpComplete()) {
                        robot.mecanum.followTrajectory(trajectoryToWaypointReturn);
                        currentState = States.MOVING_TO_DUCKS;
                    }


                    //no
                    /*if (robot.lift.isDeliverServoPositionReached()) {
                        robot.lift.retract();
                        robot.intake.getOutOfWay();
                        currentState = States.MOVING_TO_DUCKS;
                    }*/
                    break;
                case MOVING_TO_DUCKS:
                    if(robot.freightSystem.isRetractionComplete()&&!robot.mecanum.isBusy()) {
                        robot.mecanum.followTrajectory(trajectoryToDucks);
                        currentState = States.AT_DUCK;
                    }

                    break;
                case AT_DUCK:
                    if (!robot.mecanum.isBusy()) {
                        // there was something unecesary here. it is gone now. - kellen
                        robot.duckSpinner.turnOn();
                        currentState = States.DUCK_SPINNING;
                    }
                    break;
                case DUCK_SPINNING:
                    if (robot.duckSpinner.spinTimeReached()) {
                        robot.duckSpinner.turnOff();
                        currentState = States.APPROACHING_SIDE;
                    }
                    break;
                case APPROACHING_SIDE:
                    robot.mecanum.followTrajectory(trajectoryToPassageApproach);
                    if (!robot.mecanum.isBusy()) {
                        currentState = States.GOING_TO_PASSAGE;
                    }
                    break;
                case GOING_TO_PASSAGE:
                    robot.mecanum.followTrajectory(trajectoryToPassage);
                    if (!robot.mecanum.isBusy()) {
                        currentState = States.GO_TO_WAREHOUSE;
                    }
                    break;
                case GO_TO_WAREHOUSE:
                    robot.mecanum.followTrajectory(trajectoryToWarehoue);
                    if (!robot.mecanum.isBusy()) {
                        currentState = States.COMPLETE;
                    }
                    break;
                case COMPLETE:
                    isComplete = true;
            }
        }
    }
