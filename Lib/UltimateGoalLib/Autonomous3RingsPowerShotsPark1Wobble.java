package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Pose2d8863;

public class Autonomous3RingsPowerShotsPark1Wobble implements AutonomousStateMachine {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        IDLE,
        START,
        MOVING_TO_LEFT_POWER_SHOT,
        MOVING_TO_MIDDLE_POWER_SHOT,
        MOVING_TO_RIGHT_POWER_SHOT,
        SHOOTING_AT_LEFT_POWER_SHOT,
        SHOOTING_AT_MIDDLE_POWER_SHOT,
        SHOOTING_AT_RIGHT_POWER_SHOT,
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
    private final Pose2d SHOOTING_AT_LEFT_POWER_SHOT_POSE = new Pose2d(0, 14.5, Math.toRadians(180));
    private final Pose2d SHOOTING_AT_MIDDLE_POWER_SHOT_POSE = new Pose2d(0, 12-5.5, Math.toRadians(180));
    private final Pose2d SHOOTING_AT_RIGHT_POWER_SHOT_POSE = new Pose2d(0, 4.25-5.5, Math.toRadians(180));
    private final Pose2d PARK_POSE = new Pose2d(15, -18.9, Math.toRadians(180));

    private Trajectory trajectoryToLeftPowerShot;
    private Trajectory trajectoryToMiddlePowerShot;
    private Trajectory trajectoryToRightPowerShot;
    private Trajectory trajectoryToParkPosition;

    private double distanceToPowerShots = 0;
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

    public Autonomous3RingsPowerShotsPark1Wobble(UltimateGoalRobotRoadRunner robot, UltimateGoalField field, Telemetry telemetry) {
        this.robot = robot;
        this.field = field;
        currentState = States.IDLE;
        distanceUnits = DistanceUnit.INCH;
        angleUnits = AngleUnit.DEGREES;

        distanceToPowerShots = field.distanceTo(DistanceUnit.METER, SHOOTING_AT_LEFT_POWER_SHOT_POSE, field.powerShotLeft.getPose2d());
        angleOfShot = robot.shooter.calculateAngle(AngleUnit.DEGREES, distanceToPowerShots, DistanceUnit.METER, field.topGoal);
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
        trajectoryToLeftPowerShot = robot.mecanum.trajectoryBuilder(START_POSE)
                .lineTo(Pose2d8863.getVector2d(SHOOTING_AT_LEFT_POWER_SHOT_POSE))
                .build();

        trajectoryToMiddlePowerShot = robot.mecanum.trajectoryBuilder(SHOOTING_AT_LEFT_POWER_SHOT_POSE)
                .lineTo(Pose2d8863.getVector2d(SHOOTING_AT_MIDDLE_POWER_SHOT_POSE))
                .build();

        trajectoryToRightPowerShot = robot.mecanum.trajectoryBuilder(SHOOTING_AT_MIDDLE_POWER_SHOT_POSE)
                .lineTo(Pose2d8863.getVector2d(SHOOTING_AT_RIGHT_POWER_SHOT_POSE))
                .build();

        trajectoryToParkPosition = robot.mecanum.trajectoryBuilder(trajectoryToRightPowerShot.end())
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
                robot.mecanum.followTrajectoryAsync(trajectoryToLeftPowerShot);
                robot.shooter.setAngle(AngleUnit.DEGREES, angleOfShot);
                currentState = States.MOVING_TO_LEFT_POWER_SHOT;
                break;
            case MOVING_TO_LEFT_POWER_SHOT:
                if (!robot.mecanum.isBusy() && robot.shooter.isAngleAdjustmentComplete()) {
                    robot.shooterOn();
                    currentState = States.SHOOTING_AT_LEFT_POWER_SHOT;
                }
                break;
                //we ended here
            case SHOOTING_AT_LEFT_POWER_SHOT:
                robot.fire1();
                currentState = States.PARKING;
                break;
            case SHOOTING_AT_MIDDLE_POWER_SHOT:
                if (robot.isFireComplete()) {
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
