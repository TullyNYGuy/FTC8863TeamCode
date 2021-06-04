package org.firstinspires.ftc.teamcode.Lib.UltimateGoalLib;


import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Configuration;
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
        MOVING_TO_HIGH_GOAL,
        SHOOTING,
    }

    private States currentState = States.IDLE;

    private enum Commands {
        MOVE_TO_HIGH_GOAL,
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
    public Autonomous3RingsPowerShotsPark1Wobble powerShots;

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
        powerShots= new Autonomous3RingsPowerShotsPark1Wobble (robot, field, telemetry, Autonomous3RingsPowerShotsPark1Wobble.Mode.TELEOP);
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
    public void goingToHighGoal () {
        if (commandComplete) {
            currentCommand = Commands.MOVE_TO_HIGH_GOAL;
            currentState = States.START;
            commandComplete = false;
        }
    }

    public void shootPowerShots() {
        powerShots.start();
    }

    public boolean isBusy() {
        return (!commandComplete || !powerShots.isComplete());
    }

    public void update() {
        powerShots.update();
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
                        if (!robot.shooter.isReady()) {
                           robot.shooterOn();
                        }
                        currentState = States.MOVING_TO_HIGH_GOAL;
                        break;
                    case MOVING_TO_HIGH_GOAL:
                        if (!robot.mecanum.isBusy()) {
                            robot.quickFire3();
                            currentState = States.SHOOTING;
                        }
                        break;
                    case SHOOTING:
                        if (robot.fireController.isComplete()) {
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
                    case MOVING_TO_HIGH_GOAL:
                        break;
                    case SHOOTING:
                        break;
                }
                break;
        }
    }
}
