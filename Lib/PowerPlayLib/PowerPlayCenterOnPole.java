package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import android.util.Log;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class PowerPlayCenterOnPole {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        START,
        FIXING_POSITION,
        WAITING_FOR_JUNCTION_TO_BE_CENTERED,
        DONE,
        WAIT_FOR_COMPLETE
    }

    private States currentState = States.START;

    private PowerPlayRobot robot;
    private Telemetry telemetry;
    // y location where the robot start to search for the pole
    public static double START_SEARCH_LOCATION = 12;
    public static double SLOW_MOVEMENT = 0.2;
    private Pose2d poleCenterLocation;
    private Pose2d location;
    private ElapsedTime timer;

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

    public PowerPlayCenterOnPole(PowerPlayRobot robot, Telemetry telemetry) {
        this.robot = robot;
        this.telemetry = telemetry;
        timer = new ElapsedTime();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void update() {
        location = robot.mecanum.getPoseEstimate();
        telemetry.addData("x = ", location.getX());
        telemetry.addData("state = ", currentState.toString());
        switch (currentState) {
            case START: {
                robot.poleLocationDetermination.enablePoleLocationDetermination();
                currentState = States.WAITING_FOR_JUNCTION_TO_BE_CENTERED;

            }
            break;

            case WAITING_FOR_JUNCTION_TO_BE_CENTERED: {
                if (robot.poleLocationDetermination.getPoleLocation() == PowerPlayPoleLocationDetermination.PoleLocation.CENTER) {
                    // found the pole
                    poleCenterLocation = robot.mecanum.getPoseEstimate();
                    robot.mecanum.cancelFollowing();
                    robot.mecanum.setWeightedDrivePower(
                            new Pose2d(
                                    0,
                                    0,
                                    0
                            )
                    );

                    timer.reset();
                    //robot.coneGrabberArmController.moveToHighThenPrepareToRelease();
                    //currentState = States.RAISING_LIFT;
                    currentState = States.FIXING_POSITION;
                }
            }
            break;

            case FIXING_POSITION: {
                double distanceToMove = (162 - robot.poleLocationDetermination.getDistanceFromPole(DistanceUnit.MM)) / 25.4;
                //logFile.logData("distance to move " +distanceToMove);
                //move to the location needing to go
                poleCenterLocation = robot.mecanum.getPoseEstimate();
                //logFile.logData("robot position " +poleCenterLocation.getX() + " " + poleCenterLocation.getY());
                Pose2d movement = new Pose2d(distanceToMove, 0, 0);
                Pose2d newLocation = poleCenterLocation.plus(movement);
                //logFile.logData("new position " +newLocation.getX() + " " + newLocation.getY());
                Trajectory smallmove = robot.mecanum.trajectoryBuilder(poleCenterLocation)
                        .lineTo(new Vector2d(newLocation.getX(), newLocation.getY()))
                        .build();
                //logFile.logData("current location = " + Double.toString(poleCenterLocation.getX()) + ", " + poleCenterLocation.getY());
                //logFile.logData("fixed location = " + Double.toString(newLocation.getX()) + ", " + newLocation.getY());
                robot.mecanum.followTrajectoryHighAccuracy(smallmove);
                timer.reset();
                //if done with movement, then move to next state
                currentState = States.WAIT_FOR_COMPLETE;
            }
            break;

            case WAIT_FOR_COMPLETE: {
                if (!robot.mecanum.isBusy()) {
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
