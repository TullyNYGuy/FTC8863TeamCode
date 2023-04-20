package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoseStorage.RED_LEFT_JUNCTION_POLE_LOCATION;
import static org.firstinspires.ftc.teamcode.Lib.PowerPlayLib.PowerPlayPoseStorage.RED_RIGHT_JUNCTION_POLE_LOCATION;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.MatchPhase;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.TeamLocation;

import java.util.function.DoubleToLongFunction;

public class PowerPlayAutonomousCenterOnPole implements PowerPlayAutonomousStateMachine{

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        READY,
        START,
        LOOKING_FOR_POLE,
        WAITING_FOR_JUNCTION_TO_BE_CENTERED,
        FIXING_POSITION,
        WAIT_FOR_COMPLETE,
        DONE
    }

    private States currentState = States.READY;

    private PowerPlayRobot robot;
    private Telemetry telemetry;

    // how far away from the pole should the robot be?
    public static double IDEAL_DISTANCE_TO_POLE = 174; // in mm
    // what is the allowed variation?
    public static double IDEAL_DISTANCE_TOLERANCE = 12; // in mm

    private Pose2d robotPose;
    private Vector2d adjustedRobotLocation;
    private double distanceToMove = 0;
    private ElapsedTime timer;
    private Trajectory returnMove;

    private boolean isComplete = false;

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;

    private PowerPlayPoleLocationDetermination.PoleLocation poleLocation;
    private double distanceToPole = 0;
    private double sensorDifference = 0;
    
    private boolean foundPole = false;

    public boolean isFound_pole() {
        return foundPole;
    }
    
    private boolean centeredOnPole = false;

    public boolean isCenteredOnPole() {
        return centeredOnPole;
    }

    private boolean fixDistanceToPole = false;
    private boolean useRobotHeadingToFixDistance = false;

    public void enableFixDistanceToPole(boolean useRobotHeadingToFixDistance) {
        this.fixDistanceToPole = true;
        this.useRobotHeadingToFixDistance = useRobotHeadingToFixDistance;
    }
    public void disableFixDistanceToPole() {
        this.fixDistanceToPole = false;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS AND SETTERS and GETTERS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public PowerPlayAutonomousCenterOnPole(PowerPlayRobot robot, Telemetry telemetry) {
        this.robot = robot;
        this.telemetry = telemetry;
        timer = new ElapsedTime();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    
    private Vector2d getLocationToMoveTo(Pose2d currentPose, double distanceToMove) {
        Vector2d newLocation;
        Pose2d newPose;
        //PowerPlayPersistantStorage.getMatchPhase()
        //PowerPlayPersistantStorage.getTeamLocation()

        if (PowerPlayPersistantStorage.getTeamLocation() == TeamLocation.RIGHT) {
            Pose2d movement = new Pose2d(distanceToMove, 0, 0);
            newPose = currentPose.plus(movement);
        } else {
            Pose2d movement = new Pose2d(distanceToMove, 1, 0);
            newPose = currentPose.minus(movement);
        }

        newLocation = new Vector2d(newPose.getX(), newPose.getY());
        return newLocation;
    }

    private void stopRobot() {
        robot.mecanum.setWeightedDrivePower(
                new Pose2d(
                        0,
                        0,
                        0
                )
        );
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    @Override
    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public String getCurrentState() {
        return currentState.toString();
    }
    
    @Override
    public void setParkLocation(PowerPlayField.ParkLocation parkLocation) {
    }

    @Override
    public String getName() {
        return "CenterOnPole";
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
    
    @Override
    public void createTrajectories() {
        
    }

    @Override
    public void start() {
        currentState = States.START;
        isComplete = false;
        logCommand("start");
    }

    public void update() {
        robotPose = robot.mecanum.getPoseEstimate();
        logState();
        poleLocation = robot.poleLocationDetermination.getPoleLocation();
        distanceToPole = robot.poleLocationDetermination.getDistanceFromPole(DistanceUnit.MM);
        sensorDifference = robot.poleLocationDetermination.getSensorDifference(DistanceUnit.MM);
        
        switch (currentState) {

            case READY: {
                // just wait for a start
            }
            break;

            case START: {
                robot.poleLocationDetermination.enablePoleLocationDetermination();
                centeredOnPole = false;
                foundPole = false;
                currentState = States.WAITING_FOR_JUNCTION_TO_BE_CENTERED;

            }
            break;
            
            case LOOKING_FOR_POLE: {
                if (poleLocation == PowerPlayPoleLocationDetermination.PoleLocation.LEFT ||
                poleLocation == PowerPlayPoleLocationDetermination.PoleLocation.RIGHT) {
                    foundPole = true;
                    currentState = States.WAITING_FOR_JUNCTION_TO_BE_CENTERED;
                }
            }
            break;

            case WAITING_FOR_JUNCTION_TO_BE_CENTERED: {
                if (poleLocation == PowerPlayPoleLocationDetermination.PoleLocation.CENTER) {
                    foundPole = true;
                    // centered on the pole in y direction
                    robotPose = robot.mecanum.getPoseEstimate();
                    // note that this will cause isBusy to return false
                    robot.mecanum.cancelFollowing();
                    stopRobot();
                    if (fixDistanceToPole) {
                        currentState = States.FIXING_POSITION;
                    } else {
                        // user does not want the distance to the pole fixed so we are done
                        centeredOnPole = true;
                        logCommand("robot is centered left right on pole = " + poleLocation.toString());
                        logCommand("Dual sensor difference = " + Double.toString(sensorDifference));
                        logCommand("Distance to pole = " + Double.toString(distanceToPole));
                        currentState = States.DONE;
                    }
                }
                // did the robot reached the end of the tracjectory without finding the pole?
                if (!robot.mecanum.isBusy() && !foundPole) {
                    // yes, move back to the ideal junction pole location from where we are
                    logCommand("Pole was not found, going back to known location");
                    logCommand("current pose = " + Double.toString(robotPose.getX()) + ", " + robotPose.getY() + " " + Math.toDegrees(robotPose.getHeading()));
                    if (PowerPlayPersistantStorage.getTeamLocation() == TeamLocation.RIGHT) {
                         returnMove = robot.mecanum.trajectoryBuilder(robotPose)
                                .lineToLinearHeading(RED_RIGHT_JUNCTION_POLE_LOCATION)
                                .build();
                    } else {
                         returnMove = robot.mecanum.trajectoryBuilder(robotPose)
                                .lineToLinearHeading(RED_LEFT_JUNCTION_POLE_LOCATION)
                                .build();
                    }

                    robot.mecanum.followTrajectoryHighAccuracy(returnMove);
                    currentState = States.WAIT_FOR_COMPLETE;
                }
            }
            break;

            case FIXING_POSITION: {
                if (distanceToPole <= IDEAL_DISTANCE_TO_POLE + IDEAL_DISTANCE_TOLERANCE && distanceToPole >= IDEAL_DISTANCE_TO_POLE - IDEAL_DISTANCE_TOLERANCE) {
                    // the robot is within the tolerance of the distance to pole so no adjustment is needed
                    logCommand("robot location in range of pole, no adjustment needed");
                } else {
                    // adjust the robot position so that it is within the range of the pole
                    distanceToMove = (IDEAL_DISTANCE_TO_POLE - distanceToPole) / 25.4;
                    adjustedRobotLocation = getLocationToMoveTo(robotPose, distanceToMove);
                    logCommand("Distance to pole = " + Double.toString(distanceToPole));
                    logCommand("distance to adjust robot position = " + Double.toString(distanceToMove));
                    logCommand("current pose = " + Double.toString(robotPose.getX()) + ", " + robotPose.getY() + " " + Math.toDegrees(robotPose.getHeading()));
                    logCommand("will adjust to location = " + Double.toString(adjustedRobotLocation.getX()) + ", " + adjustedRobotLocation.getY());
                    Trajectory smallmove = robot.mecanum.trajectoryBuilder(robotPose)
                            .lineTo(adjustedRobotLocation)
                            .build();

                    robot.mecanum.followTrajectoryHighAccuracy(smallmove);
                    //if done with movement, then move to next state
                    currentState = States.WAIT_FOR_COMPLETE;
                }
            }
            break;

            case WAIT_FOR_COMPLETE: {
                if (!robot.mecanum.isBusy()) {
                    centeredOnPole = true;
                    isComplete = true;
                    // shut off the sensors so we don't wait time processing them
                    robot.poleLocationDetermination.disablePoleLocationDetermination();
                    logCommand("After adjustment distance to pole (may not be correct) = " + Double.toString(distanceToPole));
                    logCommand("After adjustement pose = " + Double.toString(robotPose.getX()) + ", " + robotPose.getY() + " " + Math.toDegrees(robotPose.getHeading()));
                    currentState = States.DONE;
                }
            }

            break;
            case DONE: {
                // do nothing
            }
            break;
        }
    }
}
