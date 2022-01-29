package org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Pose2d8863;

public class TestAuto implements AutonomousStateMachineFreightFrenzy {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        IDLE,
        START,
        MOVING,
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

    private Trajectory trajectoryTest;
    private Trajectory trajectoryToDucks;
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

    public TestAuto(FreightFrenzyRobotRoadRunner robot, FreightFrenzyField field, Telemetry telemetry) {
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
        trajectoryTest = robot.mecanum.trajectoryBuilder(org.firstinspires.ftc.teamcode.Lib.FreightFrenzyLib.PoseStorageFF.START_POSE, false)
                .lineToLinearHeading(new Pose2d(-12, 49, Math.toRadians(-90)))
                .lineToLinearHeading(new Pose2d(-59.75, 56.75, Math.toRadians(90)))
                .lineToLinearHeading(new Pose2d(-20, 60, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(8.75, 65, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(60, 65, Math.toRadians(0)))
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

                currentState = States.MOVING;
                break;
            case MOVING:
                robot.mecanum.followTrajectoryAsync(trajectoryTest);
                if(!robot.mecanum.isBusy()){
                    currentState = States.COMPLETE;
                }
                break;
            case COMPLETE:
                break;
        }
    }
}