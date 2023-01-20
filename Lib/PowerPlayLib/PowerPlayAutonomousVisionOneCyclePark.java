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
        MOVING_TO_JUNCTION_POLE_FOR_SCORE,
        RAISING_LIFT,
        DROPPING_FOUR_INCHES,
        RELEASING_OPEN_LIFT,
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
    private Trajectory trajectoryToParkingLocation1;
    private Trajectory trajectoryToParkingLocation2;
    private Trajectory trajectoryToParkingLocation3;

    private AllianceColorTeamLocation.ColorLocation colorLocation;

    private PowerPlayRobot robot;
    private PowerPlayField field;

    private ElapsedTime timer;
    private DistanceUnit distanceUnits;
    private AngleUnit angleUnits;

    private States currentState;
    private boolean isComplete = false;

    private PowerPlayField.ParkLocation parkLocation;

    /**
     * This method is needed because the trajectories are calculated at the time the autonomous is created.
     * This is before the signal cone is randomized. So the robot sits in init, looking at the cone the entire
     * time. Finally once play is pressed the robot knows that the signal cone is set and it quickly reads the
     * image (color in our case). We can finally determine which of the previoulsy calculated trajectories to
     * the parking location is the one the robot actually has to use. This method is called after the final
     * signal cone image is read and the parking location is determined. Basically call it just after play is
     * pressed on the driver station.
     * @param parkLocation
     */
    @Override
    public void setParkLocation(PowerPlayField.ParkLocation parkLocation) {
        this.parkLocation = parkLocation;

        // set the actual trajectory to the parking location to one of the 3 previously calculated
        // trajectories, depending on which one was determined from the signal cone
        switch (parkLocation) {
            case ONE: {
                trajectoryToParkingLocation = trajectoryToParkingLocation1;
            }
            break;

            case TWO: {
                trajectoryToParkingLocation = trajectoryToParkingLocation2;
            }
            break;

            case THREE: {
                trajectoryToParkingLocation = trajectoryToParkingLocation3;
            }
            break;
        }
    }
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

                trajectoryToParkingLocation1 = robot.mecanum.trajectoryBuilder(trajectoryToJunctionPoleFromStart.end())
                        .lineToLinearHeading(PowerPlayPoseStorage.RED_RIGHT_PARK_LOCATION_1)
                        .build();

                trajectoryToParkingLocation2 = robot.mecanum.trajectoryBuilder(trajectoryToJunctionPoleFromStart.end())
                        // end tangent forms a nice curve
                        .splineToConstantHeading(new Vector2d(23.5, -11.75), Math.toRadians(0))
                        // end tangent forms a nice curve
                        .splineToConstantHeading(PowerPlayField.getVector2d(PowerPlayPoseStorage.RED_RIGHT_PARK_LOCATION_2), Math.toRadians(270))
                        .build();
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
                currentState = States.MOVING_TO_JUNCTION_POLE_FOR_SCORE;
            }
            break;

            case MOVING_TO_JUNCTION_POLE_FOR_SCORE: {
                if (!robot.mecanum.isBusy()) {
                    currentState = States.RAISING_LIFT;
                    robot.coneGrabberArmController.moveToHighThenPrepareToRelease();
                }
            }
            break;

            case RAISING_LIFT: {
                if (robot.coneGrabberArmController.isCommandComplete()) {
                    robot.leftLift.droppingOnPole();
                    currentState = States.DROPPING_FOUR_INCHES;
                }
            }
            break;

            case DROPPING_FOUR_INCHES: {
                if (robot.leftLift.isCommandComplete()) {
                    robot.coneGrabberArmController.releaseThenMoveToPickup();
                    currentState = States.RELEASING_OPEN_LIFT;
                }
            }
            break;

            case RELEASING_OPEN_LIFT: {
                if (robot.coneGrabberArmController.isCommandComplete()) {
                    robot.mecanum.followTrajectory(trajectoryToParkingLocation);
                    currentState = States.MOVING_TO_PARKING;
                }
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
    }
}
