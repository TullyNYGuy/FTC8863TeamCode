package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.util.ElapsedTime;

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
        MOTOR_SPINUP,
        SHOOTING_AT_LEFT_POWER_SHOT,
        SHOOTING_AT_MIDDLE_POWER_SHOT,
        SHOOTING_AT_RIGHT_POWER_SHOT,
        IS_THE_SHOOTER_READY,
        PARKING,
        DROPPING_WOBBLE_GOAL,
        COMPLETE;
    }

    public enum Mode {
        TELEOP,
        AUTONOMOUS;
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

    //we were trying to have the second shot knock down the third as well, saving the third ring to shoot in the high goal
    //private final Pose2d START_POSE = new Pose2d(-61.25, -17, Math.toRadians(180));
    //private final Pose2d SHOOTING_AT_LEFT_POWER_SHOT_POSE = new Pose2d(0, 15.5, Math.toRadians(180));
    //private final Pose2d SHOOTING_AT_MIDDLE_POWER_SHOT_POSE = new Pose2d(0, 5.75, Math.toRadians(180));
    //private final Pose2d SHOOTING_AT_RIGHT_POWER_SHOT_POSE = new Pose2d(0, -.25, Math.toRadians(180));
    //private final Pose2d PARK_POSE = new Pose2d(15, -18.9, Math.toRadians(180));

    //these numbers are for dead center on each power shot
//    private final Pose2d START_POSE = new Pose2d(-61.25, -17, Math.toRadians(180));
//    private final Pose2d SHOOTING_AT_LEFT_POWER_SHOT_POSE = new Pose2d(0, 15.5, Math.toRadians(180));
//    private final Pose2d SHOOTING_AT_MIDDLE_POWER_SHOT_POSE = new Pose2d(0, 7, Math.toRadians(180));
//    private final Pose2d SHOOTING_AT_RIGHT_POWER_SHOT_POSE = new Pose2d(0, -.25, Math.toRadians(180));
//    private final Pose2d PARK_POSE = new Pose2d(15, -18.9, Math.toRadians(180));
//

    private Trajectory trajectoryToLeftPowerShot;
    private Trajectory trajectoryToMiddlePowerShot;
    private Trajectory trajectoryToRightPowerShot;
    private Trajectory trajectoryToParkPosition;

    private double distanceToPowerShots = 0;
    private double distanceToLeftPowerShot = 0;
    private double angleOfShot = 0;
    private boolean isComplete = false;

    private ElapsedTime timer;

    private Mode currentMode= Mode.AUTONOMOUS;

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

    public Autonomous3RingsPowerShotsPark1Wobble(UltimateGoalRobotRoadRunner robot, UltimateGoalField field, Telemetry telemetry, Mode currentMode) {
        this.robot = robot;
        this.field = field;
        currentState = States.IDLE;
        distanceUnits = DistanceUnit.INCH;
        angleUnits = AngleUnit.DEGREES;
        this.currentMode= currentMode;

        timer = new ElapsedTime();

        // The shooter location needs to be used to calculate the distance to the goal, not the robot pose
        Pose2d shooterPose = robot.shooter.getShooterPose(PoseStorage.SHOOTING_AT_LEFT_POWER_SHOT_POSE);
        distanceToPowerShots = field.distanceTo(DistanceUnit.METER, shooterPose, field.powerShotLeft.getPose2d());

        // We were having trouble with calculated angle (25.5) so we experimentally found what works
        // However, I now realize that we were calculating the angle based on the robot pose, not
        // the shooter pose. So we need to see if using the shooter pose instead of the robot pose
        // gives a better angle calculation
        // todo - check the new calculation of shooter angle to see if it is closer to 23 degrees
        //angleOfShot = robot.shooter.calculateAngle(AngleUnit.DEGREES, distanceToPowerShots, DistanceUnit.METER, field.topGoal);
        angleOfShot=23;
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
        trajectoryToLeftPowerShot = robot.mecanum.trajectoryBuilder(PoseStorage.START_POSE)
                .lineTo(Pose2d8863.getVector2d(PoseStorage.SHOOTING_AT_LEFT_POWER_SHOT_POSE))
               // .splineTo(Pose2d8863.getVector2d(PoseStorage.WAY_POINT), Math.toRadians(0.0))
                //.splineTo(Pose2d8863.getVector2d(PoseStorage.SHOOTING_AT_LEFT_POWER_SHOT_POSE), Math.toRadians(0.0))
                .build();

        trajectoryToMiddlePowerShot = robot.mecanum.trajectoryBuilder(PoseStorage.SHOOTING_AT_LEFT_POWER_SHOT_POSE)
                .lineTo(Pose2d8863.getVector2d(PoseStorage.SHOOTING_AT_MIDDLE_POWER_SHOT_POSE))
                .build();

        trajectoryToRightPowerShot = robot.mecanum.trajectoryBuilder(PoseStorage.SHOOTING_AT_MIDDLE_POWER_SHOT_POSE)
                .lineTo(Pose2d8863.getVector2d(PoseStorage.SHOOTING_AT_RIGHT_POWER_SHOT_POSE))
                .build();

        trajectoryToParkPosition = robot.mecanum.trajectoryBuilder(trajectoryToRightPowerShot.end())
                .lineTo(Pose2d8863.getVector2d(PoseStorage.PARK_POSE))
                .build();
    }


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    @Override
    public void start() {
        if (currentMode== Mode.AUTONOMOUS) {
            currentState = States.START;
            isComplete = false;
            robot.mecanum.setPoseEstimate(PoseStorage.START_POSE);
        }

        if (currentMode== Mode.TELEOP) {
            trajectoryToLeftPowerShot = robot.mecanum.trajectoryBuilder(robot.mecanum.getPoseEstimate())
                    .lineTo(Pose2d8863.getVector2d(PoseStorage.SHOOTING_AT_LEFT_POWER_SHOT_POSE))
                    .build();
            currentState = States.START;
            isComplete = false;
        }
    }

    @Override
    public void update() {
        switch (currentState) {
            case START:
                // start the movement. Note that this starts the angle change after the movement starts
                robot.mecanum.followTrajectoryAsync(trajectoryToLeftPowerShot);
                //robot.shooter.setAngle(AngleUnit.DEGREES, angleOfShot);
                currentState = States.MOVING_TO_LEFT_POWER_SHOT;
                break;
            case MOVING_TO_LEFT_POWER_SHOT:
                if (!robot.mecanum.isBusy() && robot.shooter.isAngleAdjustmentComplete()) {
                    robot.shooterOn();
                    currentState = States.IS_THE_SHOOTER_READY;
                }
                break;
            case IS_THE_SHOOTER_READY:
                if (robot.shooter.isReady()) {
                    robot.fire1();
                    currentState = States.SHOOTING_AT_LEFT_POWER_SHOT;
                }
                break;
            case SHOOTING_AT_LEFT_POWER_SHOT:
                if (robot.isFireComplete()) {
                    currentState = States.MOVING_TO_MIDDLE_POWER_SHOT;
                    robot.mecanum.followTrajectoryAsync(trajectoryToMiddlePowerShot);
                }
                break;
            case MOVING_TO_MIDDLE_POWER_SHOT:
                if (!robot.mecanum.isBusy()) {
                    robot.fire1();
                    currentState = States.SHOOTING_AT_MIDDLE_POWER_SHOT;
                }
                break;
            case SHOOTING_AT_MIDDLE_POWER_SHOT:
                if (robot.isFireComplete()) {
                    robot.mecanum.followTrajectoryAsync(trajectoryToRightPowerShot);
                    currentState = States.MOVING_TO_RIGHT_POWER_SHOT;
                }
                break;
            case MOVING_TO_RIGHT_POWER_SHOT:
                if (!robot.mecanum.isBusy()) {
                    robot.fire1();
                    currentState = States.SHOOTING_AT_RIGHT_POWER_SHOT;
                }
                break;
            case SHOOTING_AT_RIGHT_POWER_SHOT:
                if (robot.isFireComplete()) {
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
