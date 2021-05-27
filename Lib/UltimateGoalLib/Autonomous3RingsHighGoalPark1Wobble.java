package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Pose2d8863;

public class Autonomous3RingsHighGoalPark1Wobble implements AutonomousStateMachine {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        IDLE,
        START,
        MOVING_TO_SHOOT_POSITION,
        READY_TO_SHOOT,
        SHOOTING,
        SHOOTING_1ST_RING,
        SHOOTING_2ND_RING,
        SHOOTING_3RD_RING,
        PARKING,
        DROPPING_WOBBLE_GOAL,
        COMPLETE;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private States currentState;
    private UltimateGoalRobotRoadRunner robot;
    private UltimateGoalField field;

    private DistanceUnit distanceUnits;
    private AngleUnit angleUnits;

    private final Pose2d START_POSE = new Pose2d(-62, -18.9, Math.toRadians(180));
    private final Pose2d SHOOTING_POSE = new Pose2d(0, -18.9, Math.toRadians(180));
    private final Pose2d PARK_POSE = new Pose2d(15, -18.9, Math.toRadians(180));

    private Trajectory trajectoryToShootPosition;
    private Trajectory trajectoryToParkPosition;

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

    public Autonomous3RingsHighGoalPark1Wobble(UltimateGoalRobotRoadRunner robot, UltimateGoalField field, Telemetry telemetry) {
        this.robot = robot;
        this.field = field;
        currentState = States.IDLE;
        distanceUnits = DistanceUnit.INCH;
        angleUnits = AngleUnit.DEGREES;

        distanceToTopGoal = field.distanceTo(DistanceUnit.METER, SHOOTING_POSE, field.topGoal.getPose2d());
        //angleOfShot = robot.shooter.calculateAngle(distanceToTopGoal, DistanceUnit.METER, field.topGoal);
        angleOfShot = 5.0;
        telemetry.addData("angle of shot = ", angleOfShot);

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
        trajectoryToShootPosition = robot.mecanum.trajectoryBuilder(START_POSE)
                .lineTo(Pose2d8863.getVector2d(SHOOTING_POSE))
                .build();

        trajectoryToParkPosition = robot.mecanum.trajectoryBuilder(trajectoryToShootPosition.end())
                .lineTo(Pose2d8863.getVector2d(PARK_POSE))
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
                robot.mecanum.setPoseEstimate(START_POSE);
                // start the movement. Note that this starts the angle change after the movement starts
                robot.mecanum.followTrajectoryAsync(trajectoryToShootPosition);
                robot.shooter.setAngle(angleOfShot);
                currentState = States.MOVING_TO_SHOOT_POSITION;
                break;
            case MOVING_TO_SHOOT_POSITION:
                if (!robot.mecanum.isBusy() && robot.shooter.isAngleAdjustmentComplete()) {
                    robot.shooterOn();
                    currentState = States.READY_TO_SHOOT;
                }
                break;
            case READY_TO_SHOOT:
                robot.fire1();
                currentState = States.SHOOTING_1ST_RING;
                break;
            case SHOOTING_1ST_RING:
                if (robot.isIntakeOrFireComplete()) {
                    robot.fire1();
                    currentState = States.SHOOTING_2ND_RING;
                }
                break;
            case SHOOTING_2ND_RING:
                if (robot.isIntakeOrFireComplete()) {
                    robot.fire1();
                    currentState = States.SHOOTING_3RD_RING;
                }
                break;
            case SHOOTING_3RD_RING:
                if (robot.isIntakeOrFireComplete()) {
                    robot.shooterOff();
                    robot.mecanum.followTrajectoryAsync(trajectoryToParkPosition);
                    currentState = States.PARKING;
                }
                break;
            case PARKING:
                if (!robot.mecanum.isBusy()) {
                    robot.dropWobbleGoal();
                    currentState = States.DROPPING_WOBBLE_GOAL;
                }
                break;
            case DROPPING_WOBBLE_GOAL:
                if (robot.wobbleGoalGrabber.isComplete()) {
                    currentState = States.COMPLETE;
                    isComplete = true;
                }
                break;
            case COMPLETE:
                break;
        }
    }
}
