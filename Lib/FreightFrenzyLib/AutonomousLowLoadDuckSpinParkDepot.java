package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Pose2d8863;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.AutonomousStateMachineFreightFrenzy;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PersistantStorage;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PoseStorageFF;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyField;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.FreightFrenzyRobotRoadRunner;

public class AutonomousLowLoadDuckSpinParkDepot implements AutonomousStateMachineFreightFrenzy {

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
        MOVING_TO_DEPOT,
        AT_DEPOT,
        DEPOSITING,
        COMPLETE;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private States currentState;
    private FreightFrenzyRobotRoadRunner robot;
    private FreightFrenzyField field;

    private DistanceUnit distanceUnits;
    private AngleUnit angleUnits;

    private Trajectory trajectoryToHub;
    private Trajectory trajectoryToDucks;
    private Trajectory trajectoryToDucksTwo;
    private Trajectory trajectoryToDepot;

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

    public AutonomousLowLoadDuckSpinParkDepot(FreightFrenzyRobotRoadRunner robot, FreightFrenzyField field, Telemetry telemetry) {
        this.robot = robot;
        this.field = field;
        currentState = States.IDLE;
        distanceUnits = DistanceUnit.INCH;
        angleUnits = AngleUnit.DEGREES;



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
        trajectoryToHub = robot.mecanum.trajectoryBuilder(org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PoseStorage.START_POSE, false)
                .lineTo(Pose2d8863.getVector2d(org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PoseStorageFF.HUB_BLUE))
                               //.lineTo(Pose2d8863.getVector2d(PoseStorage.SHOOTING_AT_HIGH_GOAL))
                .build();

        trajectoryToDucks = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
                .lineTo(Pose2d8863.getVector2d(PoseStorageFF.START_POSE))
                .build();
        trajectoryToDucksTwo = robot.mecanum.trajectoryBuilder(trajectoryToDucks.end())
                .lineTo(Pose2d8863.getVector2d(PoseStorageFF.DUCK_SPINNER_BLUE))
                .build();
        trajectoryToDepot = robot.mecanum.trajectoryBuilder(trajectoryToDucks.end())
                .lineTo(Pose2d8863.getVector2d(org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.PoseStorage.PARK_POSE))
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
                robot.mecanum.setPoseEstimate(PoseStorage.START_POSE);
                // start the movement. Note that this starts the angle change after the movement starts
                //robot.mecanum.followTrajectoryAsync(trajectoryToShootPosition);

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
                    currentState = States.MOVING_TO_DUCKS;
                }
                    break;
            case MOVING_TO_DUCKS:
                if (!robot.mecanum.isBusy()) {
                    robot.duckSpinner.turnOn();
                    //robot.mecanum.followTrajectoryAsync(trajectoryToParkPosition);
                    //currentState = States.PARKING;
                }
                break;
            case AT_DUCK:
                if (!robot.mecanum.isBusy()) {

                    ///currentState = States.DROPPING_WOBBLE_GOAL;
                }
                break;
            case DUCK_SPINNING:
                //if () {
                    currentState = States.COMPLETE;
                    isComplete = true;
                //}
                break;
            case AT_DEPOT:
                break;
            case MOVING_TO_DEPOT:
                break;
            case COMPLETE:
                break;
        }
    }
}
