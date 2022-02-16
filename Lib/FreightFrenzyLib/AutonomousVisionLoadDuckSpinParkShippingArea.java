package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Pose2d8863;
import org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.Pipelines.ShippingElementPipeline;

public class AutonomousVisionLoadDuckSpinParkShippingArea implements AutonomousStateMachineFreightFrenzy {

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
        APPROACHING_STORAGE,
        DEPOSITING,
        GO_TO_SHIPPING_AREA,
        EXTENDING_LIFT,
        DEPOSIT_DONE,
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
    private Pose2d hubDumpPose;
    private Trajectory trajectoryToHub;
    private Trajectory trajectoryToDucks;
    private Trajectory trajectoryToShippingArea;
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

    public AutonomousVisionLoadDuckSpinParkShippingArea(FreightFrenzyRobotRoadRunner robot, FreightFrenzyField field, Telemetry telemetry) {
        this.robot = robot;
        this.field = field;
        switch(PersistantStorage.getShippingElementPosition()){
            case CENTER:
            case LEFT:
                if(PersistantStorage.getAllianceColor() == AllianceColor.BLUE){
                    hubDumpPose = PoseStorageFF.DELIVER_TO_MID_AND_LOW_HUB_BLUE;
                }else if(PersistantStorage.getAllianceColor() == AllianceColor.RED){
                    hubDumpPose = PoseStorageFF.DELIVER_TO_MID_AND_LOW_HUB_RED;
                }

                break;
            case RIGHT:
                if(PersistantStorage.getAllianceColor() == AllianceColor.BLUE){
                    hubDumpPose = PoseStorageFF.DELIVER_TO_HIGH_HUB_BLUE;
                }else if(PersistantStorage.getAllianceColor() == AllianceColor.RED){
                    hubDumpPose = PoseStorageFF.DELIVER_TO_HIGH_HUB_RED;
                }
                break;
        }
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
                .lineTo(Pose2d8863.getVector2d(hubDumpPose))
                .build();
        if (PersistantStorage.getAllianceColor() == AllianceColor.BLUE) {
            trajectoryToDucks = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
                    .lineTo(Pose2d8863.getVector2d(PoseStorageFF.DUCK_SPINNER_BLUE))
                    .build();
            trajectoryToShippingArea = robot.mecanum.trajectoryBuilder(trajectoryToDucks.end())
                    .lineTo(Pose2d8863.getVector2d(PoseStorageFF.STORAGE_BLUE))
                    .build();


        } else {
            trajectoryToDucks = robot.mecanum.trajectoryBuilder(trajectoryToHub.end())
                    .lineTo(Pose2d8863.getVector2d(PoseStorageFF.DUCK_SPINNER_RED))
                    .build();
            trajectoryToShippingArea = robot.mecanum.trajectoryBuilder(trajectoryToDucks.end())
                    .lineTo(Pose2d8863.getVector2d(PoseStorageFF.STORAGE_RED))
                    .build();

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
            case EXTENDING_LIFT:
                switch (PersistantStorage.getShippingElementPosition()) {
                    case CENTER:
                        robot.lift.extendToMiddle();
                        break;
                    case LEFT:
                        robot.lift.extendToBottom();
                        break;
                    case RIGHT:
                        robot.lift.extendToTop();
                        break;
                }
                currentState = States.DEPOSITING;
                break;
            case DEPOSITING:
                if (robot.lift.isExtensionMovementComplete()) {
                    if (PersistantStorage.getShippingElementPosition() == ShippingElementPipeline.ShippingPosition.RIGHT) {
                        robot.lift.deliveryServoToDumpIntoTopPosition();
                    } else {
                        robot.lift.dump();
                    }
                    robot.intake.getOutOfWay();
                    currentState = States.DEPOSIT_DONE;
                }
                break;
            case DEPOSIT_DONE:
                if (robot.lift.isDeliverServoPositionReached()) {
                    robot.lift.retract();
                    currentState =States. MOVING_TO_DUCKS;
                }
                break;
            case MOVING_TO_DUCKS:
                robot.mecanum.followTrajectory(trajectoryToDucks);
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
                if (timer.milliseconds() > 2500) {
                    robot.duckSpinner.turnOff();
                    currentState = States.APPROACHING_STORAGE;
                }
                break;
            case APPROACHING_STORAGE:
                robot.mecanum.followTrajectory(trajectoryToShippingArea);
                if(!robot.mecanum.isBusy()){
                    currentState = States.COMPLETE;
                }
                break;

            case COMPLETE:
                isComplete = true;
        }
    }
}
