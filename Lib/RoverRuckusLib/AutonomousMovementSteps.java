package org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitIMU8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DcMotor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveCurve;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DriveTrain;
import org.firstinspires.ftc.teamcode.opmodes.RoverRuckus.RoverRuckusRobot;

public class AutonomousMovementSteps {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************

    private enum Steps {
        // steps that are common to all movements
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
        WAIT_FOR_TIMER,
        WAIT_FOR_LIFT,
        WAIT_FOR_STOP,

        // steps for minerals
        WAIT_FOR_GOLD_MINERAL_LOCATION,
        SETUP_TURN_TOWARDS_MINERAL,
        RUN_TURN_TOWARDS_MINERAL,
        SETUP_DRIVE_TO_MINERAL,
        RUN_DRIVE_TO_MINERAL,
        RUN_CURVE_TO_MINERAL,

        // general navigation steps
        SETUP_CURVE_ONTO_LANDER_LANE,
        RUN_CURVE_ONTO_LANDER_LANE,
        SETUP_CURVE_ONTO_DEPOT_LANE,
        RUN_CURVE_ONTO_DEPOT_LANE,
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
        CORRECT_HEADING,

        // steps specific to one type of run
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
    private AutonomousDirector autonomousDirector;

    private AutonomousDirector.AutonomousTasks task;
    private AutonomousDirector.AutonomousTasks previousTask;

    private GoldMineralDetection goldMineralDetection;
    private MineralVoting.LikelyPosition goldPosition;

    private Steps step;
    private Steps previousStep;

    private double adjustment = 0;

    private double headingAfterDehang = 0;
    private double headingForTurn = 0;
    private double distanceToDrive = 0;

    double curveAngle;
    double speed = 0.3;

    private double normalTurnPower = 0.7;
    private double normalDrivePower = 0.3;

    private ElapsedTime timer;
    private double timeToWait = 0;

    private boolean loggingOn = true;

    private double delayTimeToWait = 5000;

    private boolean taskComplete = false;

    private double headingOnGround = 0;

    private DriveCurve driveCurve;

    private MineralVoting.LikelyPosition goldMineralPosition;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public void enableLogging() {
        loggingOn = true;
    }

    public void disableLogging() {
        loggingOn = false;
    }

    public MineralVoting.LikelyPosition getMostLikelyGoldPosition() {
        return goldMineralDetection.getMostLikelyGoldPosition();
    }

    public boolean isTaskComplete() {
        if (task == AutonomousDirector.AutonomousTasks.STOP) {
            return true;
        } else {
            return false;
        }
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public AutonomousMovementSteps(RoverRuckusRobot robot, AutonomousDirector autonomousDirector, DataLogging logFile, HardwareMap hardwareMap, Telemetry telemetry) {
        this.robot = robot;
        this.autonomousDirector = autonomousDirector;
        if (logFile != null) {
            enableLogging();
        } else {
            disableLogging();
        }
        this.logFile = logFile;
        this.telemetry = telemetry;

        timer = new ElapsedTime();

        double speed = 0.1;
        driveCurve = new DriveCurve(90, speed, 8.488 * 2.54, DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.BACKWARD, robot.driveTrain.imu, logFile, robot.driveTrain);

        step = Steps.START;
        task = autonomousDirector.getNextTask();

        goldMineralDetection = new GoldMineralDetection(hardwareMap, telemetry, logFile);
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

    public void update() {
//        boolean complete = false;

        switch (task) {
            case LOCATE_GOLD_MINERAL:
                switch (step) {
                    case START:
//                        taskComplete = false;
                        robot.deliveryLiftSystem.deliveryBoxToOutOfWay();
                        goldMineralDetection.activate(1500);
                        step = Steps.WAIT_FOR_GOLD_MINERAL_LOCATION;
                        break;
                    case WAIT_FOR_GOLD_MINERAL_LOCATION:
                        if (goldMineralDetection.isRecognitionComplete()) {
                            goldMineralDetection.shutdown();
                            goldMineralPosition = goldMineralDetection.getMostLikelyGoldPosition();
                            task = autonomousDirector.getNextTask();
                            // setup to start the next task
                            step = Steps.START;
                            //taskComplete = true;
                        } else {
                            goldMineralDetection.getRecognition(2);
                        }
                        break;
                }
                break;

            case DEHANG:
                switch (step) {
                    case START:
                        //taskComplete = false;
                        robot.dehang();
                        step = Steps.WAIT_FOR_LIFT;
                        break;
                    case WAIT_FOR_LIFT:
                        if (robot.deliveryLiftSystem.isLiftMovementComplete()) {
                            headingOnGround = robot.driveTrain.imu.getHeading();
                            robot.driveTrain.setupTurn(0, 0.7, AdafruitIMU8863.AngleMode.ABSOLUTE);
                            step = Steps.CORRECT_HEADING;
                        }
                        break;
                    case CORRECT_HEADING:
                        if (robot.driveTrain.updateTurn()) {
                            robot.driveTrain.stopTurn();
                            task = autonomousDirector.getNextTask();
                            // setup to start the next task
                            step = Steps.START;
                            //taskComplete = true;
                        }
                        break;

                }
                break;

            case DELAY:
                switch (step) {
                    case START:
//                        taskComplete = false;
                        timer.reset();
                        step = Steps.WAIT_FOR_TIMER;
                        break;
                    case WAIT_FOR_TIMER:
                        if (timer.milliseconds() > autonomousDirector.getDelay()) {
                            task = autonomousDirector.getNextTask();
                            // setup to start the next task
                            step = Steps.START;
//                            taskComplete = true;
                        }
                        break;

                }
                break;

            case HIT_GOLD_MINERAL_FROM_LANDER:
                // the movements depend on where the gold mineral is located
                switch (goldMineralPosition) {
                    case LEFT:
                        break;
                    case CENTER:
                    case LEFT_CENTER:
                    case CENTER_RIGHT:
                    case TIE:
                        switch (step) {
                            case START:
                                // hit center mineral
                                robot.driveTrain.setupDriveUsingIMU(0, inchesToCM(22.96), .3, DriveTrain.DriveDirection.FORWARD, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                robot.driveTrain.startDriveUsingIMU();
                                step = Steps.RUN_DRIVE_TO_MINERAL;
                                break;
                            case RUN_DRIVE_TO_MINERAL:
                                if (robot.driveTrain.updateDriveUsingIMU()) {
                                    // done with the drive straight
                                    robot.driveTrain.stopDriveDistanceUsingIMU();
                                    timer.reset();
                                    step = Steps.WAIT_FOR_STOP;
                                }
                                break;
                            case WAIT_FOR_STOP:
                                if (timer.milliseconds() > 500) {
                                    // we have stopped long enough
                                    // this task is done. Get the next task. Reset the step to start.
                                    task = autonomousDirector.getNextTask();
                                    step = Steps.START;
//                                    taskComplete = true;
                                }
                                break;
                        }
                        break;
                    case RIGHT:
                    case LEFT_RIGHT:
                        switch (step) {
                            case START:
                                //hit right mineral
                                driveCurve.setupDriveCurve(-55, .3, 24.69 * 2.54, DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.FORWARD);
                                driveCurve.startDriveCurve();
                                step = Steps.RUN_CURVE_TO_MINERAL;
                                break;
                            case RUN_CURVE_TO_MINERAL:
                                driveCurve.update();
                                if (driveCurve.isCurveComplete()) {
                                    driveCurve.stopCurve(DcMotor8863.FinishBehavior.HOLD);
                                    timer.reset();
                                    step = Steps.WAIT_FOR_STOP;
                                }
                                break;
                            case WAIT_FOR_STOP:
                                if (timer.milliseconds() > 500) {
                                    // we have stopped long enough
                                    // this task is done. Get the next task. Reset the step to start.
                                    task = autonomousDirector.getNextTask();
                                    step = Steps.START;
//                                    taskComplete = true;
                                }
                                break;
                        }
                }

            case CLAIM_DEPOT_FROM_CRATER_SIDE_MINERALS:
                // the route to the depot depends on which spot the gold mineral was in
                switch (goldMineralPosition) {
                    case LEFT:
                        break;
                    case CENTER:
                    case LEFT_CENTER:
                    case CENTER_RIGHT:
                    case TIE:
                        switch (step) {
                            case START:
                                // from center mineral
                                driveCurve.setupDriveCurve(-90, .1, inchesToCM(8.488), DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.BACKWARD);
                                driveCurve.startDriveCurve();
                                step = Steps.RUN_CURVE_ONTO_LANDER_LANE;
                                break;
                            case RUN_CURVE_ONTO_LANDER_LANE:
                                driveCurve.update();
                                adjustment = 2.8;
                                if (driveCurve.isCurveComplete()) {
                                    robot.driveTrain.setupDriveUsingIMU(-90, inchesToCM(25.233 - adjustment), .3, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                    robot.driveTrain.startDriveUsingIMU();
                                    // lower the lift
                                    robot.deliveryLiftSystem.goToHome();
                                    step = Steps.RUN_DRIVE_TO_WALL;
                                }
                                break;
                            case RUN_DRIVE_TO_WALL:
                                if (robot.driveTrain.updateDriveUsingIMU()) {
                                    // done with the drive straight
                                    // setup curve onto depot lane
                                    driveCurve.setupDriveCurve(-45, .3, inchesToCM(38.84), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD);
                                    driveCurve.startDriveCurve();
                                    step = Steps.RUN_CURVE_ONTO_DEPOT_LANE;
                                }
                                break;
                            case RUN_CURVE_ONTO_DEPOT_LANE:
                                driveCurve.update();
                                if (driveCurve.isCurveComplete()) {
                                    // curve is complete. Setup the next move
                                    robot.driveTrain.setupDriveUsingIMU(-45, inchesToCM(13), .3, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                    robot.driveTrain.startDriveUsingIMU();
                                    step = Steps.RUN_DRIVE_TO_DEPOT;
                                }
                                break;
                            case RUN_DRIVE_TO_DEPOT:
                                if (robot.driveTrain.updateDriveUsingIMU()) {
                                    // done with the drive straight stop the robot
                                    robot.driveTrain.stopDriveDistanceUsingIMU();
                                    step = Steps.DUMP_MARKER;
                                }
                                break;
                            case DUMP_MARKER:
                                robot.deliveryLiftSystem.deliveryBoxToDump();
                                logFile.logData("Dumped marker");
                                // reset the timer to 0 and then wait for it to expire
                                timer.reset();
                                step = Steps.WAIT_FOR_DUMP;
                                break;
                            case WAIT_FOR_DUMP:
                                // wait in milliseconds
                                timeToWait = 1000;
                                if (timer.milliseconds() > timeToWait) {
                                    // the wait is over, go to the next action
                                    step = Steps.RETURN_DUMP_ARM;
                                }
                                break;
                            case RETURN_DUMP_ARM:
                                // return the delivery box to its normal position and go to the next action
                                robot.deliveryLiftSystem.deliveryBoxToHome();
                                logFile.logData("Returned delivery box to normal position");
                                // this task is done. Get the next task. Reset the step to start.
                                task = autonomousDirector.getNextTask();
                                step = Steps.START;
                                break;
                        }
                        break;
                    case RIGHT:
                    case LEFT_RIGHT:
                        switch (step) {
                            case START:
                                driveCurve.setupDriveCurve(-90, .3, 31.15 * 2.54, DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.BACKWARD);
                                driveCurve.startDriveCurve();
                                step = Steps.RUN_CURVE_ONTO_LANDER_LANE;
                                break;
                            case RUN_CURVE_ONTO_LANDER_LANE:
                                driveCurve.update();
                                adjustment = 2.8;
                                if (driveCurve.isCurveComplete()) {
                                    robot.driveTrain.setupDriveUsingIMU(-90, inchesToCM(31.13 - adjustment), speed, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                    robot.driveTrain.startDriveUsingIMU();
                                    step = Steps.RUN_DRIVE_TO_WALL;
                                }
                                break;
                            case RUN_DRIVE_TO_WALL:
                                if (robot.driveTrain.updateDriveUsingIMU()) {
                                    // done with the drive straight
                                    // setup curve onto depot lane
                                    driveCurve.setupDriveCurve(-45, .3, inchesToCM(38.84), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD);
                                    driveCurve.startDriveCurve();
                                    // lower the lift
                                    robot.deliveryLiftSystem.goToHome();
                                    step = Steps.RUN_CURVE_ONTO_DEPOT_LANE;
                                }
                                break;
                            case RUN_CURVE_ONTO_DEPOT_LANE:
                                driveCurve.update();
                                if (driveCurve.isCurveComplete()) {
                                    // curve is complete. Setup the next move
                                    robot.driveTrain.setupDriveUsingIMU(-45, inchesToCM(13), .3, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                    robot.driveTrain.startDriveUsingIMU();
                                    step = Steps.RUN_DRIVE_TO_DEPOT;
                                }
                                break;
                            case RUN_DRIVE_TO_DEPOT:
                                if (robot.driveTrain.updateDriveUsingIMU()) {
                                    // done with the drive straight stop the robot
                                    robot.driveTrain.stopDriveDistanceUsingIMU();
                                    step = Steps.DUMP_MARKER;
                                }
                                break;
                            case DUMP_MARKER:
                                robot.deliveryLiftSystem.deliveryBoxToDump();
                                logFile.logData("Dumped marker");
                                // reset the timer to 0 and then wait for it to expire
                                timer.reset();
                                step = Steps.WAIT_FOR_DUMP;
                                break;
                            case WAIT_FOR_DUMP:
                                // wait in milliseconds
                                timeToWait = 1000;
                                if (timer.milliseconds() > timeToWait) {
                                    // the wait is over, go to the next action
                                    step = Steps.RETURN_DUMP_ARM;
                                }
                                break;
                            case RETURN_DUMP_ARM:
                                // return the delivery box to its normal position and go to the next action
                                robot.deliveryLiftSystem.deliveryBoxToHome();
                                logFile.logData("Returned delivery box to normal position");
                                // this task is done. Get the next task. Reset the step to start.
                                task = autonomousDirector.getNextTask();
                                step = Steps.START;
                                break;
                        }
                }

            case CLAIM_DEPOT_FROM_CRATER_SIDE_LANDER:
                switch (step) {
                    case START:
                        break;
                }
                break;

            case PARK_IN_OUR_CRATER_FROM_DEPOT:
                switch (step) {
                    case START:
                        robot.driveTrain.setupDriveUsingIMU(-45, inchesToCM(82), .3, DriveTrain.DriveDirection.FORWARD, AdafruitIMU8863.AngleMode.ABSOLUTE);
                        robot.driveTrain.startDriveUsingIMU();
                        step = Steps.RUN_DRIVE_TO_CRATER;
                        break;
                    case RUN_DRIVE_TO_CRATER:
                        if (robot.driveTrain.updateDriveUsingIMU()) {
                            // done with the drive straight stop the robot
                            robot.driveTrain.stopDriveDistanceUsingIMU();
                            task = autonomousDirector.getNextTask();
                            step = Steps.START;
                        }
                        break;

                }
                break;
        }
    }

    /*
    public void FacingCraterToLeftMineralToDepotToCrater() {

        // update all of the systems on the robot
        robot.update();
        // log the step
        logStep(step);

        switch (step) {
            case START:
                robot.dehang();
                step = Steps.DEHANG;
                break;
            case DEHANG:
                // wait for the robot to finish dehanging, when it does run the next action
                if (robot.deliveryLiftSystem.isLiftMovementComplete()) {
                    step = Steps.SETUP_CLEAR_LANDER;
                }
                break;
            case SETUP_CLEAR_LANDER:
                // setup a drive straight with power and distance (in cm)  and then run it
                distanceToDrive = 5.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                step = Steps.RUN_CLEAR_LANDER;
                break;
            case RUN_CLEAR_LANDER:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    // get the heading of the robot after it lands on the ground
                    headingAfterDehang = robot.driveTrain.imu.getHeading();
                    step = Steps.LOWER_LIFT;
                }
                break;
            case LOWER_LIFT:
                robot.deliveryLiftSystem.goToHome();
                step = Steps.SETUP_TURN_TOWARDS_MINERAL;
                break;
            case SETUP_TURN_TOWARDS_MINERAL:
                // setup for a turn and then run it
                headingForTurn = 42 - headingAfterDehang;
                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
                step = Steps.RUN_TURN_TOWARDS_MINERAL;
                break;
            case RUN_TURN_TOWARDS_MINERAL:
                // run the turn, watch for it to complete and when it does move to the next action
                if (robot.driveTrain.updateTurn()) {
                    // the turn has finished, move to the next action
                    step = Steps.SETUP_DRIVE_TO_MINERAL;
                }
                break;
            case SETUP_DRIVE_TO_MINERAL:
                // setup a drive straight with power and distance (in cm)  and then run it
                distanceToDrive = 95.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                step = Steps.RUN_DRIVE_TO_MINERAL;
                break;
            case RUN_DRIVE_TO_MINERAL:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    step = Steps.SETUP_TURN_TOWARDS_DEPOT;
                }
                break;
//            case SETUP_TURN_TOWARDS_WALL:
//                // setup for a turn and then run it
//                headingForTurn = -96;
//                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
//                step = Steps.RUN_TURN_TOWARDS_WALL;
//                break;
//            case RUN_TURN_TOWARDS_WALL:
//                // run the turn, watch for it to complete and when it does move to the next action
//                if (robot.driveTrain.updateTurn()) {
//                    // the turn has finished, move to the next action
//                    step = Steps.SETUP_DRIVE_TO_WALL;
//                }
//                break;
//            case SETUP_DRIVE_TO_WALL:
//                // setup a drive straight with power and distance (in cm) and then run it
//                distanceToDrive = -16 0.0;
//                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);;
//                step = Steps.RUN_DRIVE_TO_WALL;
//                break;
//            case RUN_DRIVE_TO_WALL:
//                // drive straight and watch for the drive to complete. When it does run the next action.
//                if(robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
//                    // Driving straight has finished
//                    step = Steps.SETUP_TURN_TOWARDS_DEPOT;
//                }
//                break;
            case SETUP_TURN_TOWARDS_DEPOT:
                // setup for a turn and then run it
                headingForTurn = -95;
                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
                step = Steps.RUN_TURN_TOWARDS_DEPOT;
                break;
            case RUN_TURN_TOWARDS_DEPOT:
                // run the turn, watch for it to complete and when it does move to the next action
                if (robot.driveTrain.updateTurn()) {
                    // the turn has finished, move to the next action
                    step = Steps.SETUP_DRIVE_TO_DEPOT;
                }
                break;
            case SETUP_DRIVE_TO_DEPOT:
                // setup a drive straight with power and distance (in cm)
                distanceToDrive = -155.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                step = Steps.RUN_DRIVE_TO_DEPOT;
                break;
            case RUN_DRIVE_TO_DEPOT:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    step = Steps.SETUP_TURN_FOR_DUMP;
                }
                break;
            case SETUP_TURN_FOR_DUMP:
                // setup for a turn and then run it
                headingForTurn = 10;
                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
                step = Steps.RUN_TURN_TOWARDS_DUMP;
                break;
            case RUN_TURN_TOWARDS_DUMP:
                // run the turn, watch for it to complete and when it does move to the next action
                if (robot.driveTrain.updateTurn()) {
                    // the turn has finished, move to the next action
                    step = Steps.DUMP_MARKER;
                }
                break;
            case DUMP_MARKER:
                robot.deliveryLiftSystem.deliveryBoxToDump();
                logFile.logData("Dumped marker");
                // reset the timer to 0 and then wait for it to expire
                timer.reset();
                step = Steps.WAIT_FOR_DUMP;
                break;
            case WAIT_FOR_DUMP:
                // wait in milliseconds
                timeToWait = 1000;
                if (timer.milliseconds() > timeToWait) {
                    // the wait is over, go to the next action
                    step = Steps.RETURN_DUMP_ARM;
                }
                break;
            case RETURN_DUMP_ARM:
                // return the delivery box to its normal position and go to the next action
                robot.deliveryLiftSystem.deliveryBoxToHome();
                logFile.logData("Returned delivery box to normal position");
                step = Steps.SETUP_DRIVE_TO_CRATER;
                break;
            case SETUP_DRIVE_TO_CRATER:
                // setup a drive straight with power and distance (in cm)
                distanceToDrive = 195.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                step = Steps.RUN_DRIVE_TO_CRATER;
                break;
            case RUN_DRIVE_TO_CRATER:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    step = Steps.RESET_ROBOT;
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
        // log the step
        logStep(step);

        switch (step) {
            case START:
                robot.dehang();
                step = Steps.DEHANG;
                break;
            case DEHANG:
                // wait for the robot to finish dehanging, when it does run the next action
                if (robot.deliveryLiftSystem.isLiftMovementComplete()) {
                    step = Steps.SETUP_CLEAR_LANDER;
                }
                break;
            case SETUP_CLEAR_LANDER:
                // setup a drive straight with power and distance (in cm)  and then run it
                distanceToDrive = 5.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                step = Steps.RUN_CLEAR_LANDER;
                break;
            case RUN_CLEAR_LANDER:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    // get the heading of the robot after it lands on the ground
                    headingAfterDehang = robot.driveTrain.imu.getHeading();
                    step = Steps.LOWER_LIFT;
                }
                break;
            case LOWER_LIFT:
                robot.deliveryLiftSystem.goToHome();
                step = Steps.SETUP_TURN_FOR_COMPENSATION;
                break;
            case SETUP_TURN_FOR_COMPENSATION:
                // setup for a turn and then run it
                headingForTurn = -headingAfterDehang;
                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
                step = Steps.RUN_TURN_FOR_COMPENSATION;
                break;
            case RUN_TURN_FOR_COMPENSATION:
                // run the turn, watch for it to complete and when it does move to the next action
                if (robot.driveTrain.updateTurn()) {
                    // the turn has finished, move to the next action
                    step = Steps.SETUP_DRIVE_TOWARDS_CRATER;
                }
                break;
            case SETUP_DRIVE_TOWARDS_CRATER:
                // setup a drive straight with power and distance (in cm)  and then run it
                distanceToDrive = 34.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                step = Steps.RUN_DRIVE_TOWARDS_CRATER;
                break;
            case RUN_DRIVE_TOWARDS_CRATER:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    step = Steps.SETUP_TURN_TOWARDS_MINERAL;
                }
                break;
            case SETUP_TURN_TOWARDS_MINERAL:
                // setup for a turn and then run it
                headingForTurn = 62;
                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
                step = Steps.RUN_TURN_TOWARDS_MINERAL;
                break;
            case RUN_TURN_TOWARDS_MINERAL:
                // run the turn, watch for it to complete and when it does move to the next action
                if (robot.driveTrain.updateTurn()) {
                    // the turn has finished, move to the next action
                    step = Steps.SETUP_DRIVE_TO_MINERAL;
                }
                break;
            case SETUP_DRIVE_TO_MINERAL:
                // setup a drive straight with power and distance (in cm)  and then run it
                distanceToDrive = 90.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                step = Steps.RUN_DRIVE_TO_MINERAL;
                break;
            case RUN_DRIVE_TO_MINERAL:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    step = Steps.SETUP_TURN_TOWARDS_DEPOT;
                }
                break;
//            case SETUP_TURN_TOWARDS_WALL:
//                // setup for a turn and then run it
//                headingForTurn = -96;
//                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
//                step = Steps.RUN_TURN_TOWARDS_WALL;
//                break;
//            case RUN_TURN_TOWARDS_WALL:
//                // run the turn, watch for it to complete and when it does move to the next action
//                if (robot.driveTrain.updateTurn()) {
//                    // the turn has finished, move to the next action
//                    step = Steps.SETUP_DRIVE_TO_WALL;
//                }
//                break;
//            case SETUP_DRIVE_TO_WALL:
//                // setup a drive straight with power and distance (in cm) and then run it
//                distanceToDrive = -16 0.0;
//                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);;
//                step = Steps.RUN_DRIVE_TO_WALL;
//                break;
//            case RUN_DRIVE_TO_WALL:
//                // drive straight and watch for the drive to complete. When it does run the next action.
//                if(robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
//                    // Driving straight has finished
//                    step = Steps.SETUP_TURN_TOWARDS_DEPOT;
//                }
//                break;
            case SETUP_TURN_TOWARDS_DEPOT:
                // setup for a turn and then run it
                headingForTurn = -109;
                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
                step = Steps.RUN_TURN_TOWARDS_DEPOT;
                break;
            case RUN_TURN_TOWARDS_DEPOT:
                // run the turn, watch for it to complete and when it does move to the next action
                if (robot.driveTrain.updateTurn()) {
                    // the turn has finished, move to the next action
                    step = Steps.SETUP_DRIVE_TO_DEPOT;
                }
                break;
            case SETUP_DRIVE_TO_DEPOT:
                // setup a drive straight with power and distance (in cm)
                distanceToDrive = -145.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                step = Steps.RUN_DRIVE_TO_DEPOT;
                break;
            case RUN_DRIVE_TO_DEPOT:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    step = Steps.DUMP_MARKER;
                }
                break;
//            case SETUP_TURN_FOR_DUMP:
//                // setup for a turn and then run it
//                headingForTurn = 3.0;
//                robot.driveTrain.setupTurn(headingForTurn, normalTurnPower, AdafruitIMU8863.AngleMode.RELATIVE);
//                step = Steps.RUN_TURN_TOWARDS_DUMP;
//                break;
//            case RUN_TURN_TOWARDS_DUMP:
//                // run the turn, watch for it to complete and when it does move to the next action
//                if (robot.driveTrain.updateTurn()) {
//                    // the turn has finished, move to the next action
//                    step = Steps.DUMP_MARKER;
//                }
//                break;
            case DUMP_MARKER:
                robot.deliveryLiftSystem.deliveryBoxToDump();
                logFile.logData("Dumped marker");
                // reset the timer to 0 and then wait for it to expire
                timer.reset();
                step = Steps.WAIT_FOR_DUMP;
                break;
            case WAIT_FOR_DUMP:
                // wait in milliseconds
                timeToWait = 1000;
                if (timer.milliseconds() > timeToWait) {
                    // the wait is over, go to the next action
                    step = Steps.RETURN_DUMP_ARM;
                }
                break;
            case RETURN_DUMP_ARM:
                // return the delivery box to its normal position and go to the next action
                robot.deliveryLiftSystem.deliveryBoxToHome();
                logFile.logData("Returned delivery box to normal position");
                step = Steps.SETUP_DRIVE_TO_CRATER;
                break;
            case SETUP_DRIVE_TO_CRATER:
                // setup a drive straight with power and distance (in cm)
                distanceToDrive = 195.0;
                robot.driveTrain.setupDriveDistance(normalDrivePower, distanceToDrive, DcMotor8863.FinishBehavior.FLOAT);
                step = Steps.RUN_DRIVE_TO_CRATER;
                break;
            case RUN_DRIVE_TO_CRATER:
                // drive straight and watch for the drive to complete. When it does run the next action.
                if (robot.driveTrain.updateDriveDistance() == DriveTrain.Status.COMPLETE) {
                    // Driving straight has finished
                    step = Steps.RESET_ROBOT;
                }
                break;
            case RESET_ROBOT:
                logFile.logData("Done with autonomous");
                // can't think of anything to do yet - robot is already reset
                break;
        }
    }
    */

    /**
     * Log the step to the log file - but only if it has changed from the last time it was logged.
     *
     * @param step
     */
    private void logStep(Steps step) {
        if (logFile != null && loggingOn) {
            if (step != previousStep) {
                logFile.logData("Autonomous step = ", step.toString());
                previousStep = step;
            }
        }
    }

    private double inchesToCM(double inches) {
        return inches * 2.54;
    }

}
