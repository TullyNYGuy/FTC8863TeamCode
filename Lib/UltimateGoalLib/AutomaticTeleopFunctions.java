package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Pose2d8863;
import org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib.UltimateGoalRobotRoadRunner;

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
        MOVING_TO_HIGH_GOAL,
        MOVING_TO_LEFT_POWER_SHOT,
        MOVING_TO_MIDDLE_POWER_SHOT,
        MOVING_TO_RIGHT_POWER_SHOT,
        IS_THE_SHOOTER_READY,
        SHOOTING_AT_LEFT_POWER_SHOT,
        SHOOTING_AT_MIDDLE_POWER_SHOT,
        SHOOTING_AT_RIGHT_POWER_SHOT,
        SHOOTING,
    }

    private States currentState = States.IDLE;

    private enum Commands {
        MOVE_TO_HIGH_GOAL,
        MOVE_TO_LEFT_POWER_SHOT,
        SHOOT_POWER_SHOTS,
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
    public UltimateGoalField field;
    private Trajectory trajectory;

    private Trajectory trajectoryToMiddlePowerShot;
    private Trajectory trajectoryToRightPowerShot;

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
    public AutomaticTeleopFunctions(UltimateGoalRobotRoadRunner robot, UltimateGoalField field, Telemetry telemetry) {
        this.robot = robot;
        this.field = field;
        createTrajectories();
    }
    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private void createTrajectories() {

        trajectoryToMiddlePowerShot = robot.mecanum.trajectoryBuilder(PoseStorage.SHOOTING_AT_LEFT_POWER_SHOT_POSE)
                .lineTo(Pose2d8863.getVector2d(PoseStorage.SHOOTING_AT_MIDDLE_POWER_SHOT_POSE))
                .build();

        trajectoryToRightPowerShot = robot.mecanum.trajectoryBuilder(PoseStorage.SHOOTING_AT_MIDDLE_POWER_SHOT_POSE)
                .lineTo(Pose2d8863.getVector2d(PoseStorage.SHOOTING_AT_RIGHT_POWER_SHOT_POSE))
                .build();
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************
    public void goingToHighGoal () {
        if (commandComplete) {
            currentCommand = Commands.MOVE_TO_HIGH_GOAL;
            currentState = States.START;
            commandComplete = false;
        }
    }

    public void moveToLeftPowerShot() {
        if (commandComplete) {
            currentCommand = Commands.MOVE_TO_LEFT_POWER_SHOT;
            currentState = States.START;
            commandComplete = false;
        }
    }

    public void shootPowerShots() {
        if (commandComplete) {
            currentCommand = Commands.SHOOT_POWER_SHOTS;
            currentState = States.START;
            commandComplete = false;
        }
    }

    public boolean isBusy() {
        return (!commandComplete);
    }

    public void update() {
        switch (currentCommand) {
            case MOVE_TO_HIGH_GOAL:
                switch (currentState) {
                    case IDLE:
                        break;
                    case START:
                        trajectory = robot.mecanum.trajectoryBuilder(robot.mecanum.getPoseEstimate())
                                .lineToLinearHeading(PoseStorage.SHOOTING_AT_HIGH_GOAL)
                                .build();
                        robot.mecanum.followTrajectoryAsync(trajectory);
                        //if (!robot.shooter.isReady()) {
                       //    robot.shooterOn();
                       // }
                       // robot.shooter.setAngle(AngleUnit.DEGREES, PersistantStorage.getHighGoalShooterAngle());
                        currentState = States.MOVING_TO_HIGH_GOAL;
                        break;
                    case MOVING_TO_HIGH_GOAL:
                        /*
                        if (!robot.mecanum.isBusy() && robot.shooter.isAngleAdjustmentComplete()) {
                            robot.fire3();
                            robot.quickFire3();
                            currentState = States.SHOOTING;
                        }

                         */
                        break;
                    case SHOOTING:
                        /*
                        if (robot.isFireComplete()) {
                            commandComplete = true;
                            currentState = States.IDLE;
                            currentCommand = Commands.NO_COMMAND;
                        }

                         */
                        break;
                }
                break;
            case MOVE_TO_LEFT_POWER_SHOT:
                switch (currentState) {
                    case IDLE:
                        break;
                    case START:
                        trajectory = robot.mecanum.trajectoryBuilder(robot.mecanum.getPoseEstimate())
                                .lineToLinearHeading(PoseStorage.SHOOTING_AT_LEFT_POWER_SHOT_POSE)
                                .build();
                        robot.mecanum.followTrajectoryAsync(trajectory);
                       /* if (!robot.shooter.isReady()) {
                            robot.shooterOn();
                        }

                        */
                       // robot.setGameAnglePowerShots();
                        currentState = States.MOVING_TO_LEFT_POWER_SHOT;
                        break;
                    case MOVING_TO_LEFT_POWER_SHOT:
                        if (!robot.mecanum.isBusy()){
                            commandComplete = true;
                            currentState = States.IDLE;
                            currentCommand = Commands.NO_COMMAND;
                        }
                        break;
                }
                break;
            case SHOOT_POWER_SHOTS:
                switch (currentState) {
                    case START:
                        /*
                        if (robot.shooter.isReady() && robot.shooter.isAngleAdjustmentComplete()) {
                            robot.fire1();
                            currentState = States.SHOOTING_AT_LEFT_POWER_SHOT;
                        }

                         */
                        break;
                    case SHOOTING_AT_LEFT_POWER_SHOT:
                        /*
                        if (robot.isFireComplete()) {
                            currentState = States.MOVING_TO_MIDDLE_POWER_SHOT;
                            robot.mecanum.followTrajectoryAsync(trajectoryToMiddlePowerShot);
                        }

                         */
                        break;
                    case MOVING_TO_MIDDLE_POWER_SHOT:
                        if (!robot.mecanum.isBusy()) {
                            robot.fire1();
                            currentState = States.SHOOTING_AT_MIDDLE_POWER_SHOT;
                        }
                        break;
                    case SHOOTING_AT_MIDDLE_POWER_SHOT:
                        /*
                        if (robot.isFireComplete()) {
                            robot.mecanum.followTrajectoryAsync(trajectoryToRightPowerShot);
                            currentState = States.MOVING_TO_RIGHT_POWER_SHOT;
                        }

                         */
                        break;
                    case MOVING_TO_RIGHT_POWER_SHOT:
                        if (!robot.mecanum.isBusy()) {
                            robot.fire1();
                            currentState = States.SHOOTING_AT_RIGHT_POWER_SHOT;
                        }
                        break;
                    case SHOOTING_AT_RIGHT_POWER_SHOT:
                       /* if (robot.isFireComplete()) {
                            commandComplete = true;
                            currentState = States.IDLE;
                            currentCommand = Commands.NO_COMMAND;
                        }*/
                        break;
                }
                break;
            case NO_COMMAND:
                break;
        }
    }
}
