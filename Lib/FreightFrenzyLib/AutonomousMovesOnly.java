package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Pose2d8863;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.Pipelines.ShippingElementPipeline;

public class AutonomousMovesOnly implements AutonomousStateMachineFreightFrenzy {

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
        PARKED,
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

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public AutonomousMovesOnly(FreightFrenzyRobotRoadRunner robot, FreightFrenzyField field, Telemetry telemetry) {
        this.robot = robot;
        this.field = field;
        switch (PersistantStorage.getShippingElementPosition()) {
            case CENTER:
                if (PersistantStorage.getAllianceColor() == AllianceColor.BLUE) {
                    hubDumpPose = PoseStorageFF.DELIVER_TO_MID_BLUE_WALL;
                } else if (PersistantStorage.getAllianceColor() == AllianceColor.RED) {
                    hubDumpPose = PoseStorageFF.DELIVER_TO_MID_RED_WALL;
                }

                break;
            case LEFT:
                if (PersistantStorage.getAllianceColor() == AllianceColor.BLUE) {
                    hubDumpPose = PoseStorageFF.DELIVER_TO_LOW_BLUE_WALL;
                } else if (PersistantStorage.getAllianceColor() == AllianceColor.RED) {
                    hubDumpPose = PoseStorageFF.DELIVER_TO_LOW_RED_WALL;
                }

                break;
            case RIGHT:
                if (PersistantStorage.getAllianceColor() == AllianceColor.BLUE) {
                    hubDumpPose = PoseStorageFF.DELIVER_TO_HIGH_HUB_BLUE_WALL;
                } else if (PersistantStorage.getAllianceColor() == AllianceColor.RED) {
                    hubDumpPose = PoseStorageFF.DELIVER_TO_HIGH_HUB_RED_WALL;
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

        trajectoryToHub = robot.mecanum.trajectoryBuilder(PoseStorageFF.START_POSE)
                .lineToLinearHeading(hubDumpPose)
                .build();
        if (PersistantStorage.getAllianceColor() == AllianceColor.BLUE) {
            trajectoryToDucks = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
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
            trajectoryToDucks = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
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
                    robot.mecanum.followTrajectory(trajectoryToDucks);

                    currentState = States.MOVING_TO_DUCKS;
                    break;
                case MOVING_TO_HUB:
                    if (!robot.mecanum.isBusy()) {
                        currentState = States.EXTENDING_LIFT;
                    }
                    break;
                case EXTENDING_LIFT:

                    currentState = States.DEPOSITING;
                    break;
                case DEPOSITING:

                        currentState = States.DEPOSIT_DONE;

                    break;
                case DEPOSIT_DONE:


                        currentState = States.APPROACHING_SIDE;

                    break;
                case MOVING_TO_DUCKS:
                    //robot.mecanum.followTrajectory(trajectoryToDucks);
                    if (!robot.mecanum.isBusy()) {
                       currentState = States.AT_DUCK;
                    }
                    break;
                case AT_DUCK:
                    if (!robot.mecanum.isBusy()) {
                        timer.reset();
                        currentState = States.DUCK_SPINNING;
                    }
                    break;
                case DUCK_SPINNING:
                    if (timer.milliseconds() > 2500) {

                        currentState = States.MOVING_TO_HUB;
                    }
                    break;
                case APPROACHING_SIDE:


                        robot.mecanum.followTrajectory(trajectoryToPassageApproach);
                        currentState = States.GOING_TO_PASSAGE;

                    break;
                case GOING_TO_PASSAGE:

                    if (!robot.mecanum.isBusy()) {
                        robot.mecanum.followTrajectory(trajectoryToPassage);
                        currentState = States.GO_TO_WAREHOUSE;
                    }
                    break;
                case GO_TO_WAREHOUSE:
                    if (!robot.mecanum.isBusy()) {
                        robot.mecanum.followTrajectory(trajectoryToWarehoue);
                        currentState = States.PARKED;
                    }
                    break;

                case PARKED:
                    if(!robot.mecanum.isBusy()){
                        currentState = States.COMPLETE;
                    }
                case COMPLETE:
                    isComplete = true;
            }
        }
    }
