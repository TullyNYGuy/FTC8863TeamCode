package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayField.getVector2d;

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

public class PowerPlayAutonomousVisionTestSensor implements PowerPlayAutonomousStateMachine {

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
        COMPLETE,
        READING_POLE_WITH_INVERSE_SENSOR,
        READING_POLE_WITH_NORMAL_SENSOR
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
    private Telemetry telemetry;

    private ElapsedTime timer;
    private DistanceUnit distanceUnits;
    private AngleUnit angleUnits;

    private States currentState;
    private boolean isComplete = false;

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

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

    public PowerPlayAutonomousVisionTestSensor(PowerPlayRobot robot, PowerPlayField field, Telemetry telemetry) {
        this.robot = robot;
        this.field = field;
        this.telemetry = telemetry;
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
    @Override
    public String getName() {
        return "Auto";
    }

    @Override
    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
    }

    @Override
    public void enableDataLogging() {
        enableLogging = true;
    }

    @Override
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
    @Override
    public void createTrajectories() {
        switch (PowerPlayPersistantStorage.getColorLocation()) {
            case BLUE_LEFT:
            case RED_LEFT: {
                trajectoryToJunctionPoleFromStart = robot.mecanum.trajectoryBuilder(startPose)
                        .splineTo(new Vector2d(-11.75, -53), Math.toRadians(90))
                        .splineToSplineHeading(junctionPolePose,Math.toRadians(90))
                        .build();

                trajectoryToParkingLocation1 = null;

                trajectoryToParkingLocation2 = robot.mecanum.trajectoryBuilder(trajectoryToJunctionPoleFromStart.end())
                        .lineTo(new Vector2d(-11.75, -20.5))
                        .splineToConstantHeading(new Vector2d(-23.5, -10.75), Math.toRadians(-180))
                        .splineToConstantHeading(getVector2d(PowerPlayPoseStorageForPowerPlayDrive.RED_LEFT_PARK_LOCATION_2), Math.toRadians(0))
                        .build();

                trajectoryToParkingLocation3 = robot.mecanum.trajectoryBuilder(trajectoryToJunctionPoleFromStart.end())
                        .lineTo(new Vector2d(-11.75, -20.5))
                        .splineToConstantHeading(new Vector2d(-23.5, -10.75), Math.toRadians(180))
                        .splineToConstantHeading(getVector2d(PowerPlayPoseStorageForPowerPlayDrive.RED_LEFT_PARK_LOCATION_3), Math.toRadians(180))
                        .build();
            }
            break;

            case BLUE_RIGHT:
            case RED_RIGHT: {
                trajectoryToJunctionPoleFromStart = robot.mecanum.trajectoryBuilder(startPose)
                        .splineTo(new Vector2d(11.75, -53), Math.toRadians(90))
                        .splineToSplineHeading(junctionPolePose,Math.toRadians(90))
                        .build();

                // don't need to move since the robot is already in the parking location when it scores
                trajectoryToParkingLocation1 = null;
//                trajectoryToParkingLocation1 = robot.mecanum.trajectoryBuilder(trajectoryToJunctionPoleFromStart.end())
//                        .lineToLinearHeading(PowerPlayPoseStorage.RED_RIGHT_PARK_LOCATION_1)
//                        .build();

                trajectoryToParkingLocation2 = robot.mecanum.trajectoryBuilder(trajectoryToJunctionPoleFromStart.end())
                        // end tangent forms a nice curve
                        .splineToConstantHeading(new Vector2d(23.5, -10.75), Math.toRadians(0))
                        // end tangent forms a nice curve
                        .splineToConstantHeading(getVector2d(PowerPlayPoseStorageForPowerPlayDrive.RED_RIGHT_PARK_LOCATION_2), Math.toRadians(270))
                        .build();

                trajectoryToParkingLocation3 = robot.mecanum.trajectoryBuilder(trajectoryToJunctionPoleFromStart.end())
                        .splineToConstantHeading(new Vector2d(23.5, -10.75), Math.toRadians(0))
                        //.splineToSplineHeading(new Pose2d(47,-11.75, Math.toRadians(270)),Math.toRadians(0) )
                        .splineToConstantHeading(getVector2d(PowerPlayPoseStorageForPowerPlayDrive.RED_RIGHT_PARK_LOCATION_3), Math.toRadians(0))
                        .build();
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
        logCommand("start");
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
                    currentState = States.READING_POLE_WITH_INVERSE_SENSOR;
                    robot.distanceSensorForInverse.startAverage(5);
                    //robot.coneGrabberArmController.moveToHighThenPrepareToRelease();
                }
            }
            break;

            case READING_POLE_WITH_INVERSE_SENSOR: {
                if (robot.distanceSensorForInverse.isAverageReady()) {
                    robot.distanceSensorForNormal.startAverage(5);
                    currentState = States.READING_POLE_WITH_NORMAL_SENSOR;
                    //robot.coneGrabberArmController.moveToHighThenPrepareToRelease();
                }
            }
            break;

            case READING_POLE_WITH_NORMAL_SENSOR: {
                if (robot.distanceSensorForNormal.isAverageReady()) {
                    currentState = States.COMPLETE;
                    //robot.coneGrabberArmController.moveToHighThenPrepareToRelease();
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
                    if (trajectoryToParkingLocation == null) {
                        currentState = States.COMPLETE;
                    } else {
                        robot.mecanum.followTrajectory(trajectoryToParkingLocation);
                        currentState = States.MOVING_TO_PARKING;
                    }

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
                //robot.coneGrabber.close();
                logCommand("finished");
            }
            break;
        }
    }
}
