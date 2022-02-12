package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Pose2d8863;

public class AutonomousLowLoadDuckSpinNoParkRedNearWall implements AutonomousStateMachineFreightFrenzy {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        IDLE,
        START,
        MOVING_TO_HUB,
        READY_TO_DEPOSIT,
        MOVING_TO_DUCKS,
        AT_DUCK,
        DUCK_SPINNING,
        APPROACHING_SIDE,
        GOING_TO_PASSAGE,
        DEPOSITING,
        GO_TO_WAREHOUSE,
        COMPLETE;
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

    private Trajectory trajectoryToHub;
    private Trajectory trajectoryToDucks;
    private Trajectory trajectoryToPassageApproach;
    private Trajectory trajectoryToPassage;
    private Trajectory trajectoryToWarehoue;

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

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public AutonomousLowLoadDuckSpinNoParkRedNearWall(FreightFrenzyRobotRoadRunner robot, FreightFrenzyField field, Telemetry telemetry) {
        this.robot = robot;
        this.field = field;
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

        trajectoryToHub = robot.mecanum.trajectoryBuilder(PoseStorageFF.START_POSE)
                .lineTo(Pose2d8863.getVector2d(PoseStorageFF.HUB_RED_INTAKE_DUMP))
                //.lineTo(Pose2d8863.getVector2d(PoseStorage.SHOOTING_AT_HIGH_GOAL))
                .build();

        trajectoryToDucks = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
                .lineTo(Pose2d8863.getVector2d(PoseStorageFF.DUCK_SPINNER_RED))
                .build();

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
            case START:
                isComplete = false;
                robot.mecanum.setPoseEstimate(PoseStorageFF.START_POSE);
                robot.mecanum.followTrajectory(trajectoryToHub);

                currentState = States.MOVING_TO_HUB;
                break;
            case MOVING_TO_HUB:
                if (!robot.mecanum.isBusy()) {
                    currentState = States.READY_TO_DEPOSIT;
                }
                break;
            case READY_TO_DEPOSIT:
                robot.intake.ejectIntoLevel1();
                currentState = States.DEPOSITING;
                break;
            case DEPOSITING:
                if(!robot.intake.isComplete()){
                    robot.intake.getOutOfWay();
                    robot.mecanum.followTrajectory(trajectoryToDucks);
                    currentState = States.MOVING_TO_DUCKS;
                }
                break;
            case MOVING_TO_DUCKS:
                if (!robot.mecanum.isBusy()) {
                    robot.duckSpinner.turnOn();
                    //robot.mecanum.followTrajectoryAsync(trajectoryToParkPosition);
                    currentState = States.AT_DUCK;
                }
                break;
            case AT_DUCK:
                if (!robot.mecanum.isBusy()) {
                    timer.reset();
                    currentState = States.DUCK_SPINNING;
                }
                break;
            case DUCK_SPINNING:
                if (timer.milliseconds()>2500) {
                    robot.duckSpinner.turnOff();
                    currentState = States.COMPLETE;
                }
                break;
            case COMPLETE:
                isComplete = true;
        }
    }
}
