package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Pose2d8863;

public class AutomaticTeleopFunctions {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    private enum States {
        IDLE,
        START,
        MOVING_TO_ZERO
    }

    private States currentState = States.IDLE;

    private enum Commands {
        MOVE_TO_ZERO,
        NO_COMMAND
    }

    private Commands currentCommand = Commands.NO_COMMAND;
    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private boolean commandComplete = true;
    private UltimateGoalRobotRoadRunner robot;
    private Trajectory trajectory;
    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************
    public AutomaticTeleopFunctions(UltimateGoalRobotRoadRunner robot) {
        this.robot = robot;
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
    public void goToZero() {
        if (commandComplete) {
            currentCommand = Commands.MOVE_TO_ZERO;
            currentState = States.START;
            commandComplete = false;
        }
    }

    public boolean isBusy() {
        return !commandComplete;
    }

    public void update() {
        switch (currentCommand) {
            case MOVE_TO_ZERO:
                switch (currentState) {
                    case IDLE:
                        break;
                    case START:
                        trajectory = robot.mecanum.trajectoryBuilder(robot.mecanum.getPoseEstimate())
                                .lineToLinearHeading(PoseStorage.ZERO_POSE)
                                .build();
                        robot.mecanum.followTrajectoryAsync(trajectory);
                        currentState = States.MOVING_TO_ZERO;
                        break;
                    case MOVING_TO_ZERO:
                        if (!robot.mecanum.isBusy()) {

                            commandComplete = true;
                            currentState = States.IDLE;
                            currentCommand = Commands.NO_COMMAND;
                        }
                        break;
                }
                break;
            case NO_COMMAND:
                switch (currentState) {
                    case IDLE:
                        break;
                    case START:
                        break;
                    case MOVING_TO_ZERO:
                        break;


                }
                break;
        }
    }
}
