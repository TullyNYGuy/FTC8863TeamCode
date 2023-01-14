package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;

public class PowerPlayAutonomousVisionOneCyclePark implements PowerPlayAutonomousStateMachine {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        IDLE,
        START,
        MOVING_TO_PARKING,
        COMPLETE
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private Pose2d startPose;
    private Pose2d junctionPolePose;
    private Pose2d parkingLocationPose;
    private Trajectory trajectoryToJunctionPoleFromStart;
    private Trajectory trajectoryToParkingLocation;

    private AllianceColorTeamLocation.ColorLocation colorLocation;

    private PowerPlayRobot robot;
    private PowerPlayField field;

    private ElapsedTime timer;
    private DistanceUnit distanceUnits;
    private AngleUnit angleUnits;

    private States currentState;
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
    public String getCurrentState() {
        return currentState.toString();
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public PowerPlayAutonomousVisionOneCyclePark(PowerPlayRobot robot, PowerPlayField field, Telemetry telemetry) {
        this.robot = robot;
        this.field = field;
        this.colorLocation = PowerPlayPersistantStorage.getColorLocation();
        startPose = field.getStartPose();
        junctionPolePose = field.getJunctionPolePose();
        parkingLocationPose = field.getParkingLocationPose();

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

    /**
     * Place all of the trajectories for the autonomous opmode in this method. This method gets
     * called from the constructor so that the trajectories are created when the autonomous object
     * is created.
     */
    @Override
    public void createTrajectories() {
        switch (PowerPlayPersistantStorage.getColorLocation()) {
            case RED_LEFT: {

            }
            break;

            case RED_RIGHT: {
                trajectoryToJunctionPoleFromStart = robot.mecanum.trajectoryBuilder(startPose)
                        .splineTo(new Vector2d(11.75, -53), Math.toRadians(90))
                        .lineToLinearHeading(junctionPolePose)
                        .build();
                switch (PowerPlayPersistantStorage.getParkLocation()) {
                    case ONE: {
                        trajectoryToParkingLocation = robot.mecanum.trajectoryBuilder(trajectoryToJunctionPoleFromStart.end())
                                .lineToLinearHeading(parkingLocationPose)
                                .build();
                    }
                    break;

                    case TWO: {
                        trajectoryToParkingLocation = robot.mecanum.trajectoryBuilder(trajectoryToJunctionPoleFromStart.end())
                                // end tangent forms a nice curve
                                .splineToConstantHeading(new Vector2d(23.5, -11.75), Math.toRadians(0))
                                // end tangent forms a nice curve
                                .splineToConstantHeading(PowerPlayField.getVector2d(parkingLocationPose), Math.toRadians(270))
                                .build();
                    }
                    break;

                    case THREE:{

                    }
                    break;

                }

            }
            break;

            case BLUE_LEFT: {

            }
            break;

            case BLUE_RIGHT: {

            }
            break;

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
    public void update() {
        switch (currentState) {

            case START: {
                isComplete = false;
                robot.mecanum.setPoseEstimate(startPose);
                robot.mecanum.followTrajectory(trajectoryToJunctionPoleFromStart);
                currentState = States.MOVING_TO_PARKING;
            }
            break;

            case MOVING_TO_PARKING: {
                if (!robot.mecanum.isBusy()) {
                    currentState = States.COMPLETE;
                }

            }
            break;

            case COMPLETE: {
                isComplete = true;
                robot.coneGrabber.close();
            }
            break;
        }
//            switch (currentState) {
//                case START:
//                    isComplete = false;
//                    robot.mecanum.setPoseEstimate(PoseStorageFF.START_POSE);
//                    robot.mecanum.followTrajectory(trajectoryToWaypoint);
//                    // there was something unecesary here. it is gone now. - kellen
//                    currentState = States.MOVING_TO_HUB;
//                    break;
//                case MOVING_TO_HUB:
//                    if (!robot.mecanum.isBusy()) {
//                        robot.mecanum.followTrajectory(trajectoryToHub);
//                        currentState = States.EXTENDING_LIFT;
//                    }
//                    break;
//                case EXTENDING_LIFT:
//                    if(!robot.mecanum.isBusy()) {
//                        robot.freightSystem.extend();
//                        currentState = States.DEPOSITING;
//                    }
//                    break;
//                case DEPOSITING:
//                    if (robot.freightSystem.isReadyToDump()) {
//
//                        robot.freightSystem.dump();
//                        currentState = States.DEPOSIT_DONE;
//                    }
//                    break;
//                case DEPOSIT_DONE:
//                    if(robot.freightSystem.isDumpComplete()) {
//                        robot.mecanum.followTrajectory(trajectoryToWaypointReturn);
//                        currentState = States.MOVING_TO_DUCKS;
//                    }
//                    break;
//
//                case MOVING_TO_DUCKS:
//                    if(robot.freightSystem.isRetractionComplete()&&!robot.mecanum.isBusy()) {
//                        robot.mecanum.followTrajectory(trajectoryToDucks);
//                        currentState = States.AT_DUCK;
//                    }
//
//                    break;
//                case AT_DUCK:
//                    if (!robot.mecanum.isBusy()) {
//                        // there was something unecesary here. it is gone now. - kellen
//                        robot.duckSpinner.turnOn();
//                        currentState = States.DUCK_SPINNING;
//                    }
//                    break;
//                case DUCK_SPINNING:
//                    if (robot.duckSpinner.spinTimeReached()) {
//                        robot.duckSpinner.turnOff();
//                        currentState = States.APPROACHING_SIDE;
//                    }
//                    break;
//                case APPROACHING_SIDE:
//                    robot.mecanum.followTrajectory(trajectoryToPassageApproach);
//                    if (!robot.mecanum.isBusy()) {
//                        currentState = States.GOING_TO_PASSAGE;
//                    }
//                    break;
//                case GOING_TO_PASSAGE:
//                    robot.mecanum.followTrajectory(trajectoryToPassage);
//                    if (!robot.mecanum.isBusy()) {
//                        currentState = States.GO_TO_WAREHOUSE;
//                    }
//                    break;
//                case GO_TO_WAREHOUSE:
//                    robot.mecanum.followTrajectory(trajectoryToWarehoue);
//                    if (!robot.mecanum.isBusy()) {
//                        currentState = States.COMPLETE;
//                    }
//                    break;
//                case COMPLETE:
//                    isComplete = true;
//            }
    }
}
