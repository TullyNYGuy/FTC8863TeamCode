package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class AutonomousWallVisionDuckSpinDeliverParkStorage implements AutonomousStateMachineFreightFrenzy {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        IDLE,
        START,
        MOVING_TO_DUCKS,
        DUCK_SPINNING,
        MOVING_TO_WAYPOINT_BEFORE_HUB,
        WAITING_TO_EXTEND,
        MOVING_TO_HUB,
        EXTENDING_LIFT,
        DUMPING,
        GOING_TO_WAYPOINT,
        MOVING_TO_WAYPOINT_BEFORE_PARK,
        MOVING_TO_PARK,
        COMPLETE
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
    private Trajectory trajectoryToParkInStorage;
    private Trajectory trajectoryToWaypoint;
    private Trajectory trajectoryToParkInStorageWaypoint;
    private Trajectory trajectoryToWaypointBeforePark;
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

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public AutonomousWallVisionDuckSpinDeliverParkStorage(FreightFrenzyRobotRoadRunner robot, FreightFrenzyField field, Telemetry telemetry) {
        this.robot = robot;
        this.field = field;
        switch (PersistantStorage.getShippingElementPosition()) {
            case CENTER:
                robot.freightSystem.setMiddle();
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
            trajectoryToDucks = robot.mecanum.trajectoryBuilder(PoseStorageFF.START_POSE)
                    .lineToLinearHeading(PoseStorageFF.DUCK_SPINNER_BLUE)
                    .build();
            trajectoryToWaypoint= robot.mecanum.trajectoryBuilder(trajectoryToDucks.end())
                    .lineToLinearHeading(PoseStorageFF.WAYPOINT_BLUE_HUB)
                    .build();
            trajectoryToHub = robot.mecanum.trajectoryBuilder(trajectoryToWaypoint.end())
                    .lineToLinearHeading(hubDumpPose)
                    .build();
//            trajectoryToWaypointBeforePark = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
//                    .lineToLinearHeading(PoseStorageFF.WAYPOINT_BLUE_HUB)
//                    .build();
            trajectoryToParkInStorage = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
                    .lineToLinearHeading(PoseStorageFF.STORAGE_BLUE)
                    .build();


        } else {
            trajectoryToDucks = robot.mecanum.trajectoryBuilder(PoseStorageFF.START_POSE)
                    .lineToLinearHeading(PoseStorageFF.DUCK_SPINNER_RED)
                    .build();
            trajectoryToWaypoint= robot.mecanum.trajectoryBuilder(trajectoryToDucks.end())
                    .lineToLinearHeading(PoseStorageFF.WAYPOINT_RED_HUB)
                    .build();
            trajectoryToHub = robot.mecanum.trajectoryBuilder(trajectoryToWaypoint.end())
                    .lineToLinearHeading(hubDumpPose)
                    .build();
//            trajectoryToWaypointBeforePark = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
//                    .lineToLinearHeading(PoseStorageFF.WAYPOINT_RED_HUB)
//                    .build();
            trajectoryToParkInStorageWaypoint = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
            .splineToLinearHeading(PoseStorageFF.WAYPOINT_RED_PARK, Math.toRadians(-100))
                    .build();
            trajectoryToParkInStorage = robot.mecanum.trajectoryBuilder(trajectoryToParkInStorageWaypoint.end())
                    .splineToLinearHeading(PoseStorageFF.STORAGE_RED, Math.toRadians(-100))
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

        // todo make the followTrajectory() followTrajectoryAsync() where needed so we can run
        // actions in parallel. For example, see the DUCK_SPINNING state. Once that is done, it
        // may be possible to combine some movements into a single trajectory.
        @Override
        public void update () {
            switch (currentState) {
                case START:
                    isComplete = false;
                    robot.mecanum.setPoseEstimate(PoseStorageFF.START_POSE);
                    robot.mecanum.followTrajectory(trajectoryToDucks);
                    currentState = States.MOVING_TO_DUCKS;
                    break;

                case MOVING_TO_DUCKS:
                    if (!robot.mecanum.isBusy()) {
                        robot.duckSpinner.autoSpin();
                        if(PersistantStorage.getAllianceColor() == AllianceColor.BLUE){
                            robot.freightSystem.extend();
                        }
                        currentState = States.DUCK_SPINNING;
                    }
                    break;

                case DUCK_SPINNING:
                    if (robot.duckSpinner.isComplete()) {
                        // The extend can be run in parallel with the trajectory due to the
                        // followTrajectoryAsync()
                        //robot.mecanum.followTrajectory(trajectoryToWaypoint);
                        robot.mecanum.followTrajectoryAsync(trajectoryToWaypoint);
                        currentState = States.MOVING_TO_WAYPOINT_BEFORE_HUB;
                    }
                    break;

                case MOVING_TO_WAYPOINT_BEFORE_HUB:
                    if(!robot.mecanum.isBusy()) {
                        //robot.freightSystem.extend();
                        if(PersistantStorage.getAllianceColor() == AllianceColor.RED) {
                            robot.freightSystem.extend();
                        }
                    }
                    currentState = States.WAITING_TO_EXTEND;
                    break;

                case WAITING_TO_EXTEND:
                    if(PersistantStorage.getAllianceColor() == AllianceColor.RED) {
                        robot.freightSystem.extend();
                    }
                    if (robot.freightSystem.isReadyToDump()) {
                        robot.mecanum.followTrajectory(trajectoryToHub);
                        currentState = States.MOVING_TO_HUB;
                    }
                    break;

                case MOVING_TO_HUB:
                    if (!robot.mecanum.isBusy()) {
                        robot.freightSystem.dump();
                        currentState = States.DUMPING;
                    }
                    break;

                case DUMPING:
                    if (robot.freightSystem.isDumpComplete()) {
                        if(PersistantStorage.getAllianceColor() == AllianceColor.RED){
                            robot.mecanum.followTrajectoryAsync(trajectoryToParkInStorageWaypoint);
                            currentState = States.GOING_TO_WAYPOINT;
                        }else {
                            robot.mecanum.followTrajectoryAsync(trajectoryToParkInStorage);
                            currentState = States.MOVING_TO_PARK;
                        }

                    }

                    break;

//                case MOVING_TO_WAYPOINT_BEFORE_PARK:
//                    if(!robot.mecanum.isBusy()) {
//                        robot.mecanum.followTrajectory(trajectoryToParkInStorage);
//                        currentState = States.MOVING_TO_PARK;
//                    }
//                    break;
                case GOING_TO_WAYPOINT:
                    if(!robot.mecanum.isBusy()){
                        robot.mecanum.followTrajectoryAsync(trajectoryToParkInStorage);
                        currentState = States.MOVING_TO_PARK;
                    }
                    break;
                case MOVING_TO_PARK:
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
// state machine before i changed it -kellen
   /* public void update () {
        switch (currentState) {
            case START:
                isComplete = false;
                robot.mecanum.setPoseEstimate(PoseStorageFF.START_POSE);
                robot.mecanum.followTrajectory(trajectoryToDucks);
                currentState = States.MOVING_TO_DUCKS;
                break;

            case MOVING_TO_DUCKS:
                if (!robot.mecanum.isBusy()) {
                    robot.duckSpinner.autoSpin();
                    currentState = States.DUCK_SPINNING;
                }
                break;

            case DUCK_SPINNING:
                if (robot.duckSpinner.isComplete()) {
                    // The extend can be run in parallel with the trajectory due to the
                    // followTrajectoryAsync()
                    robot.freightSystem.extend();
                    //robot.mecanum.followTrajectory(trajectoryToWaypoint);
                    robot.mecanum.followTrajectoryAsync(trajectoryToWaypoint);
                    currentState = States.MOVING_TO_WAYPOINT_BEFORE_HUB;
                }
                break;

            case MOVING_TO_WAYPOINT_BEFORE_HUB:
                if(!robot.mecanum.isBusy()) {
                    //robot.freightSystem.extend();
                    currentState = States.WAITING_TO_EXTEND;
                }
                break;

            case WAITING_TO_EXTEND:
                if (robot.freightSystem.isReadyToDump()) {
                    robot.mecanum.followTrajectory(trajectoryToHub);
                    currentState = States.MOVING_TO_HUB;
                }
                break;

            case MOVING_TO_HUB:
                if (!robot.mecanum.isBusy()) {
                    robot.freightSystem.dump();
                    currentState = States.DUMPING;
                }
                break;

            case DUMPING:
                if (robot.freightSystem.isDumpComplete()) {
                    robot.mecanum.followTrajectory(trajectoryToParkInStorage);
                    currentState = States.MOVING_TO_PARK;
                }
                break;

//                case MOVING_TO_WAYPOINT_BEFORE_PARK:
//                    if(!robot.mecanum.isBusy()) {
//                        robot.mecanum.followTrajectory(trajectoryToParkInStorage);
//                        currentState = States.MOVING_TO_PARK;
//                    }
//                    break;

            case MOVING_TO_PARK:
                // todo This state never completes. Why?
                if (!robot.mecanum.isBusy() && robot.freightSystem.isReadyToCycle()) {
                    currentState = States.COMPLETE;
                }
                break;

            case COMPLETE:
                isComplete = true;
                break;
        }
    }*/
