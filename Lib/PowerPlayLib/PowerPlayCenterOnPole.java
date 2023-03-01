package org.firstinspires.ftc.teamcode.Lib.PowerPlayLib;


import android.util.Log;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class PowerPlayCenterOnPole {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        START,
        SEARCHING_FOR_JUNCTION,
        WAITING_FOR_JUNCTION_TO_BE_CENTERED,
        DONE
    }

    private States currentState = States.START;

    private PowerPlayRobot robot;
    private Telemetry telemetry;
    // y location where the robot start to search for the pole
    public static double START_SEARCH_LOCATION = 12;
    public static double SLOW_MOVEMENT = 0.2;
    private Pose2d location;

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
                if (location.getX() > START_SEARCH_LOCATION) {
                    //robot.mecanum.cancelFollowing();
                    robot.mecanum.setWeightedDrivePower(
                            new Pose2d(
                                    SLOW_MOVEMENT,
                                    0,
                                    0
                            )
                    );
                    robot.poleLocationDetermination.enablePoleLocationDetermination();
                    currentState = States.SEARCHING_FOR_JUNCTION;
                }

            }
            break;
            case SEARCHING_FOR_JUNCTION: {
                if (robot.poleLocationDetermination.getPoleLocation() == PowerPlayPoleLocationDetermination.PoleLocation.LEFT) {
                    currentState = States.WAITING_FOR_JUNCTION_TO_BE_CENTERED;
                }
            }
            break;
            case WAITING_FOR_JUNCTION_TO_BE_CENTERED: {
                if (robot.poleLocationDetermination.getPoleLocation() == PowerPlayPoleLocationDetermination.PoleLocation.CENTER) {
                    robot.mecanum.setWeightedDrivePower(
                            new Pose2d(
                                    0,
                                    0,
                                    0
                            )
                    );
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
