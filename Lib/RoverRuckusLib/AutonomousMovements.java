package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.opmodes.RoverRuckus.RoverRuckusRobot;

public class AutonomousMovements {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum ActionStates {
        // actions that are common to all movements
        START,
        NO_ACTION,
        DEHANG,
        SETUP_CLEAR_LANDER,
        RUN_CLEAR_LANDER,
        SETUP_STRAIGHTENING_TURN,
        RUN_STRAIGHTENING_TURN,
        LOWER_LIFT,
        DUMP_MARKER,
        WAIT_FOR_DUMP,
        RETURN_DUMP_ARM,
        RESET_ROBOT,

        // actions for minerals
        SETUP_TURN_TOWARDS_MINERAL,
        RUN_TURN_TOWARDS_MINERAL,
        SETUP_DRIVE_TO_MINERAL,
        RUN_DRIVE_TO_MINERAL,

        // general navigation actions
        SETUP_TURN_TOWARDS_WALL,
        RUN_TURN_TOWARDS_WALL,
        SETUP_DRIVE_TO_WALL,
        RUN_DRIVE_TO_WALL,
        SETUP_TURN_TOWARDS_DEPOT,
        RUN_TURN_TOWARDS_DEPOT,
        SETUP_DRIVE_TO_DEPOT,
        RUN_DRIVE_TO_DEPOT,
        SETUP_DRIVE_TO_CRATER,
        RUN_DRIVE_TO_CRATER,

        // actions specific to one type of run
        SETUP_TURN_FOR_DUMP,
        RUN_TURN_TOWARDS_DUMP,
        SETUP_TURN_FOR_COMPENSATION,
        RUN_TURN_FOR_COMPENSATION,
        SETUP_DRIVE_TOWARDS_CRATER,
        RUN_DRIVE_TOWARDS_CRATER
    }


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    public RoverRuckusRobot robot;
    private DataLogging logFile;
    private Telemetry telemetry;

    private ActionStates actionState;
    private ActionStates previousActionState;

    private double headingAfterDehang = 0;
    private double headingForTurn = 0;
    private double distanceToDrive = 0;

    private double normalTurnPower = 0.7;
    private double normalDrivePower = 0.3;

    private ElapsedTime timer;
    private double timeToWait = 0;

    private boolean loggingOn = true;

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

    public AutonomousMovements(RoverRuckusRobot robot, DataLogging logFile, Telemetry telemetry) {
        this.robot = robot;
        this.logFile = logFile;
        this.telemetry = telemetry;

        timer = new ElapsedTime();

        actionState = ActionStates.START;
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

    public void FacingCraterToLeftMineralToDepotToCrater() {

        // update all of the systems on the robot
        robot.update();
        // log the action state
        logState(actionState);

        switch (actionState) {
            case START:
                robot.dehang();
                actionState = ActionStates.DEHANG;
                break;
            case DEHANG:
                // wait for the robot to finish dehanging, when it does run the next action
                if (robot.deliveryLiftSystem.isLiftMovementComplete()) {
                    actionState = ActionStates.SETUP_CLEAR_LANDER;
                }
                break;
            case SETUP_CLEAR_LANDER:
                // setup a drive straight with power and distance (in cm)  and then run it
                distanceToDrive = 5.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                actionState = ActionStates.RUN_CLEAR_LANDER;
                break;
            case RUN_CLEAR_LANDER:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    // get the heading of the robot after it lands on the ground
                    headingAfterDehang = robot.driveTrain.imu.getHeading();
                    actionState = ActionStates.LOWER_LIFT;
                }
                break;
            case LOWER_LIFT:
                robot.deliveryLiftSystem.goToHome();
                actionState = ActionStates.SETUP_TURN_TOWARDS_MINERAL;
                break;
            case SETUP_TURN_TOWARDS_MINERAL:
                // setup for a turn and then run it
                headingForTurn = 42 - headingAfterDehang;
                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
                actionState = ActionStates.RUN_TURN_TOWARDS_MINERAL;
                break;
            case RUN_TURN_TOWARDS_MINERAL:
                // run the turn, watch for it to complete and when it does move to the next action
                if (robot.driveTrain.updateTurn()) {
                    // the turn has finished, move to the next action
                    actionState = ActionStates.SETUP_DRIVE_TO_MINERAL;
                }
                break;
            case SETUP_DRIVE_TO_MINERAL:
                // setup a drive straight with power and distance (in cm)  and then run it
                distanceToDrive = 95.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                actionState = ActionStates.RUN_DRIVE_TO_MINERAL;
                break;
            case RUN_DRIVE_TO_MINERAL:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    actionState = ActionStates.SETUP_TURN_TOWARDS_DEPOT;
                }
                break;
//            case SETUP_TURN_TOWARDS_WALL:
//                // setup for a turn and then run it
//                headingForTurn = -96;
//                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
//                actionState = ActionStates.RUN_TURN_TOWARDS_WALL;
//                break;
//            case RUN_TURN_TOWARDS_WALL:
//                // run the turn, watch for it to complete and when it does move to the next action
//                if (robot.driveTrain.updateTurn()) {
//                    // the turn has finished, move to the next action
//                    actionState = ActionStates.SETUP_DRIVE_TO_WALL;
//                }
//                break;
//            case SETUP_DRIVE_TO_WALL:
//                // setup a drive straight with power and distance (in cm) and then run it
//                distanceToDrive = -16 0.0;
//                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);;
//                actionState = ActionStates.RUN_DRIVE_TO_WALL;
//                break;
//            case RUN_DRIVE_TO_WALL:
//                // drive straight and watch for the drive to complete. When it does run the next action.
//                if(robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
//                    // Driving straight has finished
//                    actionState = ActionStates.SETUP_TURN_TOWARDS_DEPOT;
//                }
//                break;
            case SETUP_TURN_TOWARDS_DEPOT:
                // setup for a turn and then run it
                headingForTurn = -95;
                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
                actionState = ActionStates.RUN_TURN_TOWARDS_DEPOT;
                break;
            case RUN_TURN_TOWARDS_DEPOT:
                // run the turn, watch for it to complete and when it does move to the next action
                if (robot.driveTrain.updateTurn()) {
                    // the turn has finished, move to the next action
                    actionState = ActionStates.SETUP_DRIVE_TO_DEPOT;
                }
                break;
            case SETUP_DRIVE_TO_DEPOT:
                // setup a drive straight with power and distance (in cm)
                distanceToDrive = -155.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                actionState = ActionStates.RUN_DRIVE_TO_DEPOT;
                break;
            case RUN_DRIVE_TO_DEPOT:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    actionState = ActionStates.SETUP_TURN_FOR_DUMP;
                }
                break;
            case SETUP_TURN_FOR_DUMP:
                // setup for a turn and then run it
                headingForTurn = 10;
                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
                actionState = ActionStates.RUN_TURN_TOWARDS_DUMP;
                break;
            case RUN_TURN_TOWARDS_DUMP:
                // run the turn, watch for it to complete and when it does move to the next action
                if (robot.driveTrain.updateTurn()) {
                    // the turn has finished, move to the next action
                    actionState = ActionStates.DUMP_MARKER;
                }
                break;
            case DUMP_MARKER:
                robot.deliveryLiftSystem.deliveryBoxToDump();
                logFile.logData("Dumped marker");
                // reset the timer to 0 and then wait for it to expire
                timer.reset();
                actionState = ActionStates.WAIT_FOR_DUMP;
                break;
            case WAIT_FOR_DUMP:
                // wait in milliseconds
                timeToWait = 1000;
                if (timer.milliseconds() > timeToWait) {
                    // the wait is over, go to the next action
                    actionState = ActionStates.RETURN_DUMP_ARM;
                }
                break;
            case RETURN_DUMP_ARM:
                // return the delivery box to its normal position and go to the next action
                robot.deliveryLiftSystem.deliveryBoxToHome();
                logFile.logData("Returned delivery box to normal position");
                actionState = ActionStates.SETUP_DRIVE_TO_CRATER;
                break;
            case SETUP_DRIVE_TO_CRATER:
                // setup a drive straight with power and distance (in cm)
                distanceToDrive = 195.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                actionState = ActionStates.RUN_DRIVE_TO_CRATER;
                break;
            case RUN_DRIVE_TO_CRATER:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    actionState = ActionStates.RESET_ROBOT;
                }
                break;
            case RESET_ROBOT:
                logFile.logData("Done with autonomous");
                // can't think of anything to do yet - robot is already reset
                break;
        }
    }

    public void FacingCraterToLeftMineralToDepotToCraterAlternate() {

        // update all of the systems on the robot
        robot.update();
        // log the action state
        logState(actionState);

        switch (actionState) {
            case START:
                robot.dehang();
                actionState = ActionStates.DEHANG;
                break;
            case DEHANG:
                // wait for the robot to finish dehanging, when it does run the next action
                if (robot.deliveryLiftSystem.isLiftMovementComplete()) {
                    actionState = ActionStates.SETUP_CLEAR_LANDER;
                }
                break;
            case SETUP_CLEAR_LANDER:
                // setup a drive straight with power and distance (in cm)  and then run it
                distanceToDrive = 5.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                actionState = ActionStates.RUN_CLEAR_LANDER;
                break;
            case RUN_CLEAR_LANDER:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    // get the heading of the robot after it lands on the ground
                    headingAfterDehang = robot.driveTrain.imu.getHeading();
                    actionState = ActionStates.LOWER_LIFT;
                }
                break;
            case LOWER_LIFT:
                robot.deliveryLiftSystem.goToHome();
                actionState = ActionStates.SETUP_TURN_FOR_COMPENSATION;
                break;
            case SETUP_TURN_FOR_COMPENSATION:
                // setup for a turn and then run it
                headingForTurn = -headingAfterDehang;
                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
                actionState = ActionStates.RUN_TURN_FOR_COMPENSATION;
                break;
            case RUN_TURN_FOR_COMPENSATION:
                // run the turn, watch for it to complete and when it does move to the next action
                if (robot.driveTrain.updateTurn()) {
                    // the turn has finished, move to the next action
                    actionState = ActionStates.SETUP_DRIVE_TOWARDS_CRATER;
                }
                break;
            case SETUP_DRIVE_TOWARDS_CRATER:
                // setup a drive straight with power and distance (in cm)  and then run it
                distanceToDrive = 34.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                actionState = ActionStates.RUN_DRIVE_TOWARDS_CRATER;
                break;
            case RUN_DRIVE_TOWARDS_CRATER:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    actionState = ActionStates.SETUP_TURN_TOWARDS_MINERAL;
                }
                break;
            case SETUP_TURN_TOWARDS_MINERAL:
                // setup for a turn and then run it
                headingForTurn = 62;
                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
                actionState = ActionStates.RUN_TURN_TOWARDS_MINERAL;
                break;
            case RUN_TURN_TOWARDS_MINERAL:
                // run the turn, watch for it to complete and when it does move to the next action
                if (robot.driveTrain.updateTurn()) {
                    // the turn has finished, move to the next action
                    actionState = ActionStates.SETUP_DRIVE_TO_MINERAL;
                }
                break;
            case SETUP_DRIVE_TO_MINERAL:
                // setup a drive straight with power and distance (in cm)  and then run it
                distanceToDrive = 90.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                actionState = ActionStates.RUN_DRIVE_TO_MINERAL;
                break;
            case RUN_DRIVE_TO_MINERAL:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    actionState = ActionStates.SETUP_TURN_TOWARDS_DEPOT;
                }
                break;
//            case SETUP_TURN_TOWARDS_WALL:
//                // setup for a turn and then run it
//                headingForTurn = -96;
//                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
//                actionState = ActionStates.RUN_TURN_TOWARDS_WALL;
//                break;
//            case RUN_TURN_TOWARDS_WALL:
//                // run the turn, watch for it to complete and when it does move to the next action
//                if (robot.driveTrain.updateTurn()) {
//                    // the turn has finished, move to the next action
//                    actionState = ActionStates.SETUP_DRIVE_TO_WALL;
//                }
//                break;
//            case SETUP_DRIVE_TO_WALL:
//                // setup a drive straight with power and distance (in cm) and then run it
//                distanceToDrive = -16 0.0;
//                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);;
//                actionState = ActionStates.RUN_DRIVE_TO_WALL;
//                break;
//            case RUN_DRIVE_TO_WALL:
//                // drive straight and watch for the drive to complete. When it does run the next action.
//                if(robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
//                    // Driving straight has finished
//                    actionState = ActionStates.SETUP_TURN_TOWARDS_DEPOT;
//                }
//                break;
            case SETUP_TURN_TOWARDS_DEPOT:
                // setup for a turn and then run it
                headingForTurn = -109;
                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
                actionState = ActionStates.RUN_TURN_TOWARDS_DEPOT;
                break;
            case RUN_TURN_TOWARDS_DEPOT:
                // run the turn, watch for it to complete and when it does move to the next action
                if (robot.driveTrain.updateTurn()) {
                    // the turn has finished, move to the next action
                    actionState = ActionStates.SETUP_DRIVE_TO_DEPOT;
                }
                break;
            case SETUP_DRIVE_TO_DEPOT:
                // setup a drive straight with power and distance (in cm)
                distanceToDrive = -145.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                actionState = ActionStates.RUN_DRIVE_TO_DEPOT;
                break;
            case RUN_DRIVE_TO_DEPOT:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    actionState = ActionStates.DUMP_MARKER;
                }
                break;
//            case SETUP_TURN_FOR_DUMP:
//                // setup for a turn and then run it
//                headingForTurn = 3.0;
//                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
//                actionState = ActionStates.RUN_TURN_TOWARDS_DUMP;
//                break;
//            case RUN_TURN_TOWARDS_DUMP:
//                // run the turn, watch for it to complete and when it does move to the next action
//                if (robot.driveTrain.updateTurn()) {
//                    // the turn has finished, move to the next action
//                    actionState = ActionStates.DUMP_MARKER;
//                }
//                break;
            case DUMP_MARKER:
                robot.deliveryLiftSystem.deliveryBoxToDump();
                logFile.logData("Dumped marker");
                // reset the timer to 0 and then wait for it to expire
                timer.reset();
                actionState = ActionStates.WAIT_FOR_DUMP;
                break;
            case WAIT_FOR_DUMP:
                // wait in milliseconds
                timeToWait = 1000;
                if (timer.milliseconds() > timeToWait) {
                    // the wait is over, go to the next action
                    actionState = ActionStates.RETURN_DUMP_ARM;
                }
                break;
            case RETURN_DUMP_ARM:
                // return the delivery box to its normal position and go to the next action
                robot.deliveryLiftSystem.deliveryBoxToHome();
                logFile.logData("Returned delivery box to normal position");
                actionState = ActionStates.SETUP_DRIVE_TO_CRATER;
                break;
            case SETUP_DRIVE_TO_CRATER:
                // setup a drive straight with power and distance (in cm)
                distanceToDrive = 195.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                actionState = ActionStates.RUN_DRIVE_TO_CRATER;
                break;
            case RUN_DRIVE_TO_CRATER:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    actionState = ActionStates.RESET_ROBOT;
                }
                break;
            case RESET_ROBOT:
                logFile.logData("Done with autonomous");
                // can't think of anything to do yet - robot is already reset
                break;
        }
    }

    /**
     * Log the state to the log file - but only if it has changed from the last time it was logged.
     *
     * @param actionState
     */
    private void logState(ActionStates actionState) {
        if (logFile != null && loggingOn) {
            if (actionState != previousActionState) {
                logFile.logData("Autonomous state = ", actionState.toString());
                previousActionState = actionState;
            }
        }
    }

}
