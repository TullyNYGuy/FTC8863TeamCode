package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class AutonomousWarehouseVisionDeliverParkWarehouse implements AutonomousStateMachineFreightFrenzy {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        IDLE,
        START,
        MOVING_TO_DELIVERY_POSE_WHILE_EXTENDING,
        WAITING_FOR_DUMP_COMPLETE,
        MOVING_TO_WAYPOINT_BEFORE_PARK,
        MOVING_TO_ENTRY_TO_WAREHOUSE,
        MOVING_TO_PARK_IN_WAREHOUSE_WAYPOINT,
        MOVING_TO_PARK,
        COMPLETE
    }

    private enum HubLevel {
        TOP,
        MIDDLE,
        BOTTOM
    }

    private HubLevel hubLevel;

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

    private Pose2d startpose;
    private Pose2d hubDumpPose;

    private Trajectory trajectoryToDeliveryPose;
    private Trajectory trajectoryToEntryToWarehouse;
    private Trajectory trajectoryToParkInWarehouseWaypoint;
    private Trajectory trajectoryToParkInWarehouse;

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

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public AutonomousWarehouseVisionDeliverParkWarehouse(FreightFrenzyRobotRoadRunner robot, FreightFrenzyField field, Telemetry telemetry) {
        this.robot = robot;
        this.field = field;
        switch (PersistantStorage.getStartSpot()) {
            case BLUE_WALL:
                startpose = PoseStorageFF.BLUE_WALL_START_POSE;
                break;
            case BLUE_WAREHOUSE:
                startpose = PoseStorageFF.BLUE_WAREHOUSE_START_POSE;
                break;
            case RED_WALL:
                startpose = PoseStorageFF.RED_WALL_START_POSE;
                break;
            case RED_WAREHOUSE:
                startpose = PoseStorageFF.RED_WAREHOUSE_START_POSE;
                break;
        }
        switch (PersistantStorage.getShippingElementPosition()) {
            case CENTER:
                robot.freightSystem.setMiddle();
                hubLevel = HubLevel.MIDDLE;
                switch (PersistantStorage.getStartSpot()) {
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
                hubLevel = HubLevel.BOTTOM;
                robot.freightSystem.setBottom();
                switch (PersistantStorage.getStartSpot()) {
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
                hubLevel = HubLevel.TOP;
                robot.freightSystem.setTop();
                switch (PersistantStorage.getStartSpot()) {
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
            trajectoryToDeliveryPose = robot.mecanum.trajectoryBuilder(startpose)
                    .lineToLinearHeading(hubDumpPose)
                    .build();
            trajectoryToEntryToWarehouse = robot.mecanum.trajectoryBuilder(trajectoryToDeliveryPose.end())
                    .lineToLinearHeading(PoseStorageFF.BLUE_ENTRY_TO_WAREHOUSE_WAYPOINT)
                    .build();
            trajectoryToParkInWarehouseWaypoint = robot.mecanum.trajectoryBuilder(trajectoryToEntryToWarehouse.end())
                    .lineToLinearHeading(PoseStorageFF.BLUE_WAREHOUSE_PARK_WAYPOINT)
                    .build();
            trajectoryToParkInWarehouse = robot.mecanum.trajectoryBuilder(trajectoryToParkInWarehouseWaypoint.end())
                    .lineToLinearHeading(PoseStorageFF.BLUE_WAREHOUSE_PARK)
                    .build();


        } else {
            // red
            trajectoryToDeliveryPose = robot.mecanum.trajectoryBuilder(PoseStorageFF.START_POSE)
                    .lineToLinearHeading(hubDumpPose)
                    .build();
            trajectoryToParkInWarehouse = robot.mecanum.trajectoryBuilder(trajectoryToDeliveryPose.end())
                    .lineToLinearHeading(PoseStorageFF.WAYPOINT_BLUE_HUB)
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
                    robot.mecanum.setPoseEstimate(startpose);
                    robot.mecanum.followTrajectoryAsync(trajectoryToDeliveryPose);
                    robot.freightSystem.extend();
                    currentState = States.MOVING_TO_DELIVERY_POSE_WHILE_EXTENDING;
                    break;

                case MOVING_TO_DELIVERY_POSE_WHILE_EXTENDING:
                    if(!robot.mecanum.isBusy() && robot.freightSystem.isReadyToDump()) {
                        robot.freightSystem.dump();
                        currentState = States.WAITING_FOR_DUMP_COMPLETE;
                    }
                    break;

                case WAITING_FOR_DUMP_COMPLETE:
                    if (robot.freightSystem.isDumpComplete()) {
                        robot.mecanum.followTrajectoryAsync(trajectoryToEntryToWarehouse);
                        currentState = States.MOVING_TO_ENTRY_TO_WAREHOUSE;
                    }
                    break;

                case MOVING_TO_ENTRY_TO_WAREHOUSE:
                    if (!robot.mecanum.isBusy()) {
                        robot.mecanum.followTrajectoryAsync(trajectoryToParkInWarehouseWaypoint);
                        currentState = States.MOVING_TO_PARK_IN_WAREHOUSE_WAYPOINT;
                    }
                    break;

                case MOVING_TO_PARK_IN_WAREHOUSE_WAYPOINT:
                    if (!robot.mecanum.isBusy()) {
                        robot.mecanum.followTrajectoryAsync(trajectoryToParkInWarehouse);
                        currentState = States.MOVING_TO_PARK;
                    }
                    break;

                case MOVING_TO_PARK:
                    // todo Arm retracts here. Why?
                    // todo This state never completes. Why?
                    if (!robot.mecanum.isBusy() && robot.freightSystem.isReadyToCycle()) {
                        currentState = States.COMPLETE;
                    }
                    break;

                case COMPLETE:
                    isComplete = true;
                    break;
            }
        }
    }