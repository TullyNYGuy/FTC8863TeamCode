package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayField.getVector2d;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoseStorage.RED_LEFT_PARK_LOCATION_2;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoseStorage.RED_LEFT_PARK_LOCATION_3;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoseStorage.RED_LEFT_START_DUAL_SENSORS_LOCATION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoseStorage.RED_LEFT_STOP_JUNCTION_POLE_TRAJECTORY_LOCATION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoseStorage.RED_RIGHT_PARK_LOCATION_2;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoseStorage.RED_RIGHT_START_DUAL_SENSORS_LOCATION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoseStorage.RED_RIGHT_STOP_JUNCTION_POLE_TRAJECTORY_LOCATION;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColorTeamLocation;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.TeamLocation;

public class PowerPlayAutonomousVisionOneCycleParkForPowerPlayDriveTestDualSensor implements PowerPlayAutonomousStateMachine {

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
        MOVING_TO_POSE_TO_START_SENSOR,
        RAISING_LIFT_TO_LOOK_AT_HIGH,
        WAIT_BEFORE_LOOKING_FOR_POLE,
        WAIT_FOR_CENTER_ON_POLE,
        LOOKING_FOR_POLE,
        FIXING_POSITION,
        RAISING_LIFT,
        DROPPING_FOUR_INCHES,
        RELEASING_OPEN_LIFT,
        MOVING_TO_PARKING,
        WAIT_FOR_COMPLETE,
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
    private Telemetry telemetry;
    private PowerPlayField field;
    private PowerPlayAutonomousCenterOnPole powerPlayAutonomousCenterOnPole;

    private ElapsedTime timer;
    private DistanceUnit distanceUnits;
    private AngleUnit angleUnits;

    private States currentState;
    private boolean isComplete = false;

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    public Pose2d poleCenterLocation;
    public Pose2d stopLocation;
    public Pose2d currentPose = new Pose2d();

    private PowerPlayField.ParkLocation parkLocation;
    private double yLocationToStartLookingForPole;

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

    public PowerPlayAutonomousVisionOneCycleParkForPowerPlayDriveTestDualSensor(PowerPlayRobot robot, PowerPlayField field, Telemetry telemetry) {
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

        this.powerPlayAutonomousCenterOnPole = new PowerPlayAutonomousCenterOnPole(robot, telemetry);
        powerPlayAutonomousCenterOnPole.enableFixDistanceToPole(false);

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
        powerPlayAutonomousCenterOnPole.setDataLog(logFile);
    }

    @Override
    public void enableDataLogging() {
        enableLogging = true;
        powerPlayAutonomousCenterOnPole.enableDataLogging();
    }

    @Override
    public void disableDataLogging() {
        enableLogging = false;
        powerPlayAutonomousCenterOnPole.disableDataLogging();
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
                yLocationToStartLookingForPole = RED_LEFT_START_DUAL_SENSORS_LOCATION.getY()-2;

//                trajectoryToJunctionPoleFromStart = robot.mecanum.trajectoryBuilder(startPose)
//                        .splineTo(new Vector2d(-11.75, -53), Math.toRadians(90))
//                        .splineToSplineHeading(junctionPolePose,Math.toRadians(90))
//                        .build();

                trajectoryToJunctionPoleFromStart = robot.mecanum.trajectoryBuilder(startPose)
                        .splineTo(new Vector2d(-13.75, -51), Math.toRadians(90))
                        .splineToSplineHeading(RED_LEFT_START_DUAL_SENSORS_LOCATION,Math.toRadians(90))
                        .splineToSplineHeading(RED_LEFT_STOP_JUNCTION_POLE_TRAJECTORY_LOCATION,Math.toRadians(90),
                                robot.mecanum.getVelConstraintSlow(), // slower than normal speed
                                robot.mecanum.getAccelConstraint())
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
                yLocationToStartLookingForPole = RED_RIGHT_START_DUAL_SENSORS_LOCATION.getY();

                trajectoryToJunctionPoleFromStart = robot.mecanum.trajectoryBuilder(startPose)
                        .splineTo(new Vector2d(11.75, -53), Math.toRadians(90))
                        .splineToSplineHeading(RED_RIGHT_START_DUAL_SENSORS_LOCATION,Math.toRadians(90))
                        .splineToSplineHeading(RED_RIGHT_STOP_JUNCTION_POLE_TRAJECTORY_LOCATION,Math.toRadians(90),
                                robot.mecanum.getVelConstraintSlow(), // slower than normal speed
                                robot.mecanum.getAccelConstraint())
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
                        .splineToConstantHeading(getVector2d(PowerPlayPoseStorageForPowerPlayDrive.RED_RIGHT_PARK_LOCATION_3), Math.toRadians(0))
                        .build();
            }
            break;
        }
    }

    public Trajectory createParkingTrajectoriesOnTheFly(PowerPlayField.ParkLocation parkLocation, Pose2d currentPose) {
        this.parkLocation = parkLocation;

        // calculate the parking trajectories using the current location of the robot as the starting
        // point
        if (PowerPlayPersistantStorage.getTeamLocation() == TeamLocation.LEFT) {
            switch (parkLocation) {
                case ONE: {
                    // stay where the robot is
                    trajectoryToParkingLocation = null;
                }
                break;

                case TWO: {
                    trajectoryToParkingLocation = robot.mecanum.trajectoryBuilder(currentPose)
                            .lineTo(new Vector2d(-11.75, -20.5))
                            .splineToConstantHeading(new Vector2d(-23.5, -10.75), Math.toRadians(180))
                            .splineToSplineHeading((RED_LEFT_PARK_LOCATION_2), Math.toRadians(180))
                            .build();
                }
                break;

                case THREE: {
                    trajectoryToParkingLocation = robot.mecanum.trajectoryBuilder(currentPose)
                            .lineTo(new Vector2d(-11.75, -20.5))
                            .splineToConstantHeading(new Vector2d(-23.5, -10.75), Math.toRadians(180))
                            .splineToSplineHeading((RED_LEFT_PARK_LOCATION_3), Math.toRadians(180))
                            .build();
                }
                break;
            }
        } else {
            // right
            switch (parkLocation) {
                case ONE: {
                    // stay where the robot is
                    trajectoryToParkingLocation = null;
                }
                break;

                case TWO: {
                    trajectoryToParkingLocation = robot.mecanum.trajectoryBuilder(currentPose)
                            // end tangent forms a nice curve
                            .splineToConstantHeading(new Vector2d(23.5, -10.75), Math.toRadians(0))
                            // end tangent forms a nice curve
                            .splineToConstantHeading(getVector2d(RED_RIGHT_PARK_LOCATION_2), Math.toRadians(270))
                            .build();
                }
                break;

                case THREE: {
                    trajectoryToParkingLocation = robot.mecanum.trajectoryBuilder(currentPose)
                            .splineToConstantHeading(new Vector2d(23.5, -10.75), Math.toRadians(0))
                            .splineToConstantHeading(getVector2d(PowerPlayPoseStorageForPowerPlayDrive.RED_RIGHT_PARK_LOCATION_3), Math.toRadians(0))
                            .build();
                }
                break;
            }
        }
        return trajectoryToParkingLocation;
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
        logState();
        powerPlayAutonomousCenterOnPole.update();
        currentPose = robot.mecanum.getPoseEstimate();

        switch (currentState) {

            case START: {
                isComplete = false;
                robot.mecanum.setPoseEstimate(startPose);
                robot.mecanum.followTrajectoryAsync(trajectoryToJunctionPoleFromStart);
                timer.reset();
                currentState = States.WAIT_BEFORE_LOOKING_FOR_POLE;
            }
            break;

//            case MOVING_TO_JUNCTION_POLE_FOR_SCORE: {
//                if (!robot.mecanum.isBusy()) {
//                    currentState = States.RAISING_LIFT;
//                    robot.coneGrabberArmController.moveToHighThenPrepareToRelease();
//                }
//            }
//            break;
//
//            case MOVING_TO_POSE_TO_START_SENSOR: {
//                if (!robot.mecanum.isBusy()) {
//                    currentState = States.LOOKING_FOR_POLE;
//                    robot.mecanum.followTrajectory(trajectoryToJunctionPoleFromStart);
//                }
//            }
//            break;

            case WAIT_BEFORE_LOOKING_FOR_POLE: {
                // has the robot reached the location to start looking for the pole?
                if (currentPose.getY() >= yLocationToStartLookingForPole) {
                    robot.coneGrabberArmController.moveToLookAtHigh();
                    //robot.coneGrabberArmController.moveToHighThenPrepareToRelease();
                    //currentState = States.RAISING_LIFT;
                    currentState = States.RAISING_LIFT_TO_LOOK_AT_HIGH;
                }
            }
            break;

            case RAISING_LIFT_TO_LOOK_AT_HIGH: {
                if (robot.coneGrabberArmController.isCommandComplete()) {
                    powerPlayAutonomousCenterOnPole.start();
                    currentState = States.WAIT_FOR_CENTER_ON_POLE;
                }
            }
            break;

            case WAIT_FOR_CENTER_ON_POLE: {
                if (powerPlayAutonomousCenterOnPole.isComplete()) {
                    // now centered on the pole, score
                    robot.coneGrabberArmController.moveToHighThenPrepareToRelease();
                    timer.reset();
                    currentState = States.RAISING_LIFT;
                }
            }
            break;

            case RAISING_LIFT: {
                // wait for 5 seconds before dropping so that lift will stop shaking
                if (robot.coneGrabberArmController.isCommandComplete() && timer.milliseconds() > 5000) {
                    robot.lift.droppingOnPole();
                    currentState = States.DROPPING_FOUR_INCHES;
                }
            }
            break;

//            case LOOKING_FOR_POLE: {
//                if (robot.poleLocationDetermination.getPoleLocation() == PowerPlayPoleLocationDetermination.PoleLocation.CENTER) {
//                    // found the pole
//                    poleCenterLocation = robot.mecanum.getPoseEstimate();
//                    robot.mecanum.cancelFollowing();
//                    robot.mecanum.setWeightedDrivePower(
//                            new Pose2d(
//                                    0,
//                                    0,
//                                    0
//                            )
//                    );
//
//                    timer.reset();
//                    //robot.coneGrabberArmController.moveToHighThenPrepareToRelease();
//                    //currentState = States.RAISING_LIFT;
//                    currentState = States.FIXING_POSITION;
//                }
////                logFile.logData("normal distance = ", robot.poleLocationDetermination.getNormalDistance(DistanceUnit.MM));
////                logFile.logData("inverse distance = ", robot.poleLocationDetermination.getInverseDistance(DistanceUnit.MM));
////                logFile.logData("sensor difference = ", robot.poleLocationDetermination.getSensorDifference(DistanceUnit.MM));
////                logCommandOnchange.log("pole location = " + robot.poleLocationDetermination.getPoleLocation().toString());
//            }
//            break;
//
//            case FIXING_POSITION: {
//                //calculate the difference
//                double distanceToMove = (162 - robot.poleLocationDetermination.getDistanceFromPole(DistanceUnit.MM))/25.4;
//                logFile.logData("distance to move " +distanceToMove);
//                //move to the location needing to go
//                poleCenterLocation = robot.mecanum.getPoseEstimate();
//                logFile.logData("robot position " +poleCenterLocation.getX() + " " + poleCenterLocation.getY());
//                Pose2d movement = new Pose2d(distanceToMove, 0, 0);
//                Pose2d newLocation = poleCenterLocation.plus(movement);
//                logFile.logData("new position " +newLocation.getX() + " " + newLocation.getY());
//                Trajectory smallmove = robot.mecanum.trajectoryBuilder(poleCenterLocation)
//                        .lineTo(new Vector2d(newLocation.getX(), newLocation.getY()))
//                        .build();
//                //logFile.logData("current location = " + Double.toString(poleCenterLocation.getX()) + ", " + poleCenterLocation.getY());
//                //logFile.logData("fixed location = " + Double.toString(newLocation.getX()) + ", " + newLocation.getY());
//                robot.mecanum.followTrajectoryHighAccuracy(smallmove);
//                timer.reset();
//                //if done with movement, then move to next state
//                currentState = States.WAIT_FOR_COMPLETE;
//            }
//            break;

            case DROPPING_FOUR_INCHES: {
                if (robot.lift.isCommandComplete()) {
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
                        trajectoryToParkingLocation = createParkingTrajectoriesOnTheFly(PowerPlayPersistantStorage.getParkLocation(), currentPose);
                        robot.mecanum.followTrajectoryAsync(trajectoryToParkingLocation);
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

            case WAIT_FOR_COMPLETE: {
                if (powerPlayAutonomousCenterOnPole.isComplete()) {
                    currentState = States.COMPLETE;
                }
            }
            break;

            case COMPLETE: {
                isComplete = true;
                robot.coneGrabber.close();
//                stopLocation = robot.mecanum.getPoseEstimate();
//                logFile.logData("normal distance = ", robot.poleLocationDetermination.getNormalDistance(DistanceUnit.MM));
//                logFile.logData("inverse distance = ", robot.poleLocationDetermination.getInverseDistance(DistanceUnit.MM));
//                logFile.logData("sensor difference = ", robot.poleLocationDetermination.getSensorDifference(DistanceUnit.MM));
//                logFile.logData("pole location = ",  robot.poleLocationDetermination.getPoleLocation().toString());
//                logFile.logData("location when pole center = " + Double.toString(poleCenterLocation.getX()) + " " + Double.toString(poleCenterLocation.getY()));
//                logFile.logData("location when stopped = " + Double.toString(stopLocation.getX()) + " " + Double.toString(stopLocation.getY()));
//                logFile.logData("distance from pole = " + Double.toString(robot.poleLocationDetermination.getDistanceFromPole(DistanceUnit.MM)));
//                logFile.logData("finished");
            }
            break;
        }
    }
}
