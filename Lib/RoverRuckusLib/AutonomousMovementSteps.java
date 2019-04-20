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
import org.firstinspires.ftc.teamcode.Lib.RoverRuckusLib.AutonomousDirector;

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
        STOP,
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
        MOVE_BACKWARDS,
        BACKUP_FROM_DUMP_POSITION,

        // general navigation steps
        SETUP_CURVE_ONTO_LANDER_LANE,
        RUN_CURVE_ONTO_LANDER_LANE,
        SETUP_CURVE_ONTO_DEPOT_LANE,
        RUN_CURVE_ONTO_DEPOT_LANE,
        SETUP_TURN_TOWARDS_WALL,
        RUN_TURN_TOWARDS_WALL,
        SETUP_DRIVE_TO_WALL,
        RUN_DRIVE_TO_WALL,
        DRIVE_INTO_DEPOT,
        SETUP_TURN_TOWARDS_DEPOT,
        RUN_TURN_TOWARDS_DEPOT,
        SETUP_DRIVE_TO_DEPOT,
        RUN_DRIVE_TO_DEPOT,
        SETUP_DRIVE_TO_CRATER,
        RUN_DRIVE_TO_CRATER,
        RUN_CURVE_ONTO_CRATER_LANE,
        CORRECT_HEADING,
        BACKUP_FROM_INSIDE_DEPOT,
        PUSH_MINERAL_INTO_DEPOT,
        SETUP_PUSH_MINERAL_INTO_DEPOT,

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
            logFile.logData("START_LOCATION " + autonomousDirector.getHangLocation().toString());
        } else {
            disableLogging();
        }
        this.logFile = logFile;
        this.telemetry = telemetry;

        timer = new ElapsedTime();

        double speed = 0.1;
        driveCurve = new DriveCurve(robot.driveTrain.imu, logFile, robot.driveTrain);

        step = Steps.START;
        task = autonomousDirector.getNextTask();

        goldMineralDetection = new GoldMineralDetection(hardwareMap, telemetry, logFile);

    }


    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************


    /**
     * Log the task to the log file - but only if it has changed from the last time it was logged.
     *
     * @param task
     */
    private void logTask(AutonomousDirector.AutonomousTasks task) {
        if (logFile != null && loggingOn) {
            if (task != previousTask) {
                logFile.logData("Task = ", task.toString());
                previousTask = task;
            }
        }
    }

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

    /**
     * Convert inches to cm
     *
     * @param inches
     * @return cm
     */
    private double inchesToCM(double inches) {
        return inches * 2.54;
    }

    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void update() {
//        boolean complete = false;
        logTask(task);
        switch (task) {
            case LOCATE_GOLD_MINERAL:
                logStep(step);
                switch (step) {
                    case START:
//                        taskComplete = false;
                        robot.deliveryLiftSystem.deliveryBoxToOutOfWay();
                        goldMineralDetection.activate(2500);
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
                logStep(step);
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
                logStep(step);
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
                switch (autonomousDirector.getHangLocation()) {
                    case CRATER_SIDE:
                    case DONT_HANG_CRATER:
                        switch (goldMineralPosition) {
                            case LEFT:
                                logStep(step);
                                switch (step) {
                                    case START:
                                        driveCurve.setupDriveCurve(-90, .1, inchesToCM(14.468), DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.FORWARD);
                                        driveCurve.startDriveCurve();
                                        step = Steps.RUN_CURVE_ONTO_LANDER_LANE;
                                        break;
                                    case RUN_CURVE_ONTO_LANDER_LANE:
                                        driveCurve.update();
                                        if (driveCurve.isCurveComplete()) {
                                            robot.deliveryLiftSystem.goToHome();
                                            robot.driveTrain.setupDriveUsingIMU(-90, inchesToCM(42.124), 0.25, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                            robot.driveTrain.startDriveUsingIMU();
                                            step = Steps.RUN_DRIVE_TO_MINERAL;
                                        }
                                        break;
                                    case RUN_DRIVE_TO_MINERAL:
                                        if (robot.driveTrain.updateDriveUsingIMU()) {
                                            // done with the drive straight
                                            driveCurve.setupDriveCurve(0, 0.1, inchesToCM(8.5), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.FORWARD);
                                            driveCurve.startDriveCurve();
                                            step = Steps.RUN_CURVE_TO_MINERAL;
                                        }
                                        break;
                                    case RUN_CURVE_TO_MINERAL:
                                        driveCurve.update();
                                        if (driveCurve.isCurveComplete()) {
                                            driveCurve.setupDriveCurve(-90, 0.1, inchesToCM(8.5), DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.BACKWARD);
                                            driveCurve.startDriveCurve();
                                            step = Steps.RUN_CURVE_ONTO_DEPOT_LANE;
                                        }
                                        break;
                                    case RUN_CURVE_ONTO_DEPOT_LANE:
                                        driveCurve.update();
                                        if (driveCurve.isCurveComplete()) {
                                            task = autonomousDirector.getNextTask();
                                            step = Steps.START;
                                        }
                                        break;

                                }
                                break;
                            case CENTER:
                            case LEFT_CENTER:
                            case CENTER_RIGHT:
                            case TIE:
                                logStep(step);
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
                                logStep(step);
                                switch (step) {
                                    case START:
                                        //hit right mineral
                                        adjustment = 0;
                                        driveCurve.setupDriveCurve(-55, .1, inchesToCM(24.69), DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.FORWARD);
                                        driveCurve.startDriveCurve();
                                        step = Steps.RUN_CURVE_TO_MINERAL;
                                        break;
                                    case RUN_CURVE_TO_MINERAL:
                                        driveCurve.update();
                                        if (driveCurve.isCurveComplete()) {
                                            //driveCurve.stopCurve(DcMotor8863.FinishBehavior.HOLD);
                                            robot.driveTrain.setupDriveUsingIMU(-55, inchesToCM(2), .1, DriveTrain.DriveDirection.FORWARD, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                            robot.driveTrain.startDriveUsingIMU();
                                            //timer.reset();
                                            step = Steps.RUN_DRIVE_TO_MINERAL;
                                        }
                                        break;
                                    case RUN_DRIVE_TO_MINERAL:
                                        if (robot.driveTrain.updateDriveUsingIMU()) {
                                            robot.driveTrain.setupDriveUsingIMU(-55, inchesToCM(2), .1, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                            robot.driveTrain.startDriveUsingIMU();
                                            step = Steps.MOVE_BACKWARDS;
                                        }
                                        break;
                                    case MOVE_BACKWARDS:
                                        if (robot.driveTrain.updateDriveUsingIMU()) {
                                            step = Steps.START;
                                            task = autonomousDirector.getNextTask();
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
                        break;
                    case DEPOT_SIDE:
                    case DONT_HANG_DEPOT:
                        switch (goldMineralPosition) {
                            case CENTER:
                            case LEFT_CENTER:
                            case CENTER_RIGHT:
                            case TIE:
                                logStep(step);
                                switch (step) {
                                    case START:
                                        adjustment = +2;
                                        driveCurve.setupDriveCurve(90 - adjustment, .1, inchesToCM(14.468), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.FORWARD);
                                        driveCurve.startDriveCurve();
                                        step = Steps.RUN_CURVE_ONTO_LANDER_LANE;
                                        break;
                                    case RUN_CURVE_ONTO_LANDER_LANE:
                                        driveCurve.update();
                                        if (driveCurve.isCurveComplete()) {
                                            robot.deliveryLiftSystem.goToHome();
                                            adjustment = +4;
                                            driveCurve.setupDriveCurve(180, .1, inchesToCM(12.465 + adjustment), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD);
                                            driveCurve.startDriveCurve();
                                            step = Steps.RUN_DRIVE_TO_MINERAL;
                                        }
                                        break;
                                    case RUN_DRIVE_TO_MINERAL:
                                        driveCurve.update();
                                        if (driveCurve.isCurveComplete()) {
                                            task = autonomousDirector.getNextTask();
                                            step = Steps.START;
                                        }
                                        break;
                                }
                                break;
                            case RIGHT:
                            case LEFT_RIGHT:
                                logStep(step);
                                switch (step) {
                                    case START:
                                        //hit right mineral
                                        adjustment = -1.5;
                                        driveCurve.setupDriveCurve(-45, .2, inchesToCM(32.4 + adjustment), DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.FORWARD);
                                        driveCurve.startDriveCurve();
                                        step = Steps.RUN_CURVE_TO_MINERAL;
                                        break;
                                    case RUN_CURVE_TO_MINERAL:
                                        driveCurve.update();
                                        if (driveCurve.isCurveComplete()) {
                                            robot.deliveryLiftSystem.goToHome();
                                            step = Steps.START;
                                            task = autonomousDirector.getNextTask();
                                        }
                                        break;
                                }
                                break;
                            case LEFT:
                                logStep(step);
                                switch (step) {
                                    case START:
                                        //hit left mineral
                                        driveCurve.setupDriveCurve(90, .1, inchesToCM(14.468), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.FORWARD);
                                        driveCurve.startDriveCurve();
                                        step = Steps.RUN_CURVE_TO_MINERAL;
                                        break;
                                    case RUN_CURVE_TO_MINERAL:
                                        driveCurve.update();
                                        if (driveCurve.isCurveComplete()) {
                                            robot.deliveryLiftSystem.goToHome();
                                            robot.driveTrain.setupDriveUsingIMU(+90, inchesToCM(17.44), .2, DriveTrain.DriveDirection.FORWARD, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                            robot.driveTrain.startDriveUsingIMU();
                                            step = Steps.RUN_DRIVE_TO_MINERAL;
                                        }
                                        break;
                                    case RUN_DRIVE_TO_MINERAL:
                                        if (robot.driveTrain.updateDriveUsingIMU()) {
                                            driveCurve.setupDriveCurve(+180, .1, inchesToCM(15), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD);
                                            driveCurve.startDriveCurve();
                                            step = Steps.RUN_DRIVE_TO_CRATER;
                                        }
                                        break;
                                    case RUN_DRIVE_TO_CRATER:
                                        driveCurve.update();
                                        if (driveCurve.isCurveComplete()) {
                                            task = autonomousDirector.getNextTask();
                                            step = Steps.START;
                                        }
                                        break;
                                }
                                break;
                        }
                        break;
                }
                break;

            case CLAIM_DEPOT_FROM_CRATER_SIDE_MINERALS:
                // the route to the depot depends on which spot the gold mineral was in
                switch (goldMineralPosition) {
                    case LEFT:
                        logStep(step);
                        switch (step) {
                            case START:
                                robot.driveTrain.setupDriveUsingIMU(-90, inchesToCM(4), 0.25, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                robot.driveTrain.startDriveUsingIMU();
                                step = Steps.RUN_DRIVE_TO_WALL;
                                break;
                            case RUN_DRIVE_TO_WALL:
                                if (robot.driveTrain.updateDriveUsingIMU()) {
                                    driveCurve.setupDriveCurve(-45, .2, inchesToCM(38.84), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD);
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
                    case CENTER:
                    case LEFT_CENTER:
                    case CENTER_RIGHT:
                    case TIE:
                        logStep(step);
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
                                    robot.driveTrain.setupDriveUsingIMU(-90, inchesToCM(25.233 - adjustment), .2, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
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
                                    driveCurve.setupDriveCurve(-45, .2, inchesToCM(38.84), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD);
                                    driveCurve.startDriveCurve();
                                    step = Steps.RUN_CURVE_ONTO_DEPOT_LANE;
                                }
                                break;
                            case RUN_CURVE_ONTO_DEPOT_LANE:
                                driveCurve.update();
                                if (driveCurve.isCurveComplete()) {
                                    // curve is complete. Setup the next move
                                    robot.driveTrain.setupDriveUsingIMU(-45, inchesToCM(13), .2, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
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
                        logStep(step);
                        switch (step) {
                            case START:
                                driveCurve.setupDriveCurve(-90, .1, inchesToCM(31.15), DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.BACKWARD);
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
                                    driveCurve.setupDriveCurve(-45, .1, inchesToCM(38.84), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD);
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
                                    robot.driveTrain.setupDriveUsingIMU(-45, inchesToCM(13), .2, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
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
                }
                break;

            case CLAIM_DEPOT_FROM_DEPOT_SIDE_MINERALS:
                switch (goldMineralPosition) {
                    case CENTER:
                    case LEFT_CENTER:
                    case CENTER_RIGHT:
                    case TIE:
                        logStep(step);
                        switch (step) {
                            case START:
                                adjustment = +6;
                                robot.driveTrain.setupDriveUsingIMU(180, inchesToCM(12 + adjustment), 0.25, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                robot.driveTrain.startDriveUsingIMU();
                                step = Steps.DRIVE_INTO_DEPOT;
                                break;
                            case DRIVE_INTO_DEPOT:
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
                                robot.driveTrain.setupDriveUsingIMU(180, inchesToCM(10), 0.25, DriveTrain.DriveDirection.FORWARD, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                robot.driveTrain.startDriveUsingIMU();
                                step = Steps.BACKUP_FROM_DUMP_POSITION;
                                break;
                            case BACKUP_FROM_DUMP_POSITION:
                                if (robot.driveTrain.updateDriveUsingIMU()) {
                                    // done with the drive straight stop the robot
                                    robot.driveTrain.stopDriveDistanceUsingIMU();
                                    task = autonomousDirector.getNextTask();
                                    step = Steps.START;
                                }
                                break;
                            case STOP:

                                break;
                        }
                        break;

                    case RIGHT:
                    case LEFT_RIGHT:
                        logStep(step);
                        switch (step) {
                            case START:
                                driveCurve.setupDriveCurve(44, .15, inchesToCM(19.8), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.FORWARD);
                                driveCurve.startDriveCurve();
                                step = Steps.SETUP_PUSH_MINERAL_INTO_DEPOT;
                                break;
                            case SETUP_PUSH_MINERAL_INTO_DEPOT:
                                driveCurve.update();
                                if (driveCurve.isCurveComplete()) {
                                    robot.driveTrain.setupDriveUsingIMU(45, inchesToCM(12), .2, DriveTrain.DriveDirection.FORWARD, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                    robot.driveTrain.startDriveUsingIMU();
                                    step = Steps.PUSH_MINERAL_INTO_DEPOT;
                                }
                                break;
                            case PUSH_MINERAL_INTO_DEPOT:
                                if (robot.driveTrain.updateDriveUsingIMU()) {
                                    // done with the drive straight stop the robot
                                    robot.driveTrain.setupDriveUsingIMU(45, inchesToCM(13.75), .2, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                    robot.driveTrain.startDriveUsingIMU();
                                    step = Steps.BACKUP_FROM_INSIDE_DEPOT;
                                }
                                break;
                            case BACKUP_FROM_INSIDE_DEPOT:
                                    if (robot.driveTrain.updateDriveUsingIMU()) {
                                        // done with the drive straight stop the robot
                                        robot.driveTrain.stopDriveDistanceUsingIMU();
                                        step = Steps.RUN_CURVE_ONTO_DEPOT_LANE;
                                    }
                                    break;
                            case RUN_CURVE_ONTO_DEPOT_LANE:
                                    adjustment = 10;
                                    driveCurve.setupDriveCurve(+135, .1, inchesToCM(16.1 + adjustment), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.FORWARD);
                                    driveCurve.startDriveCurve();
                                    step = Steps.DUMP_MARKER;
                                break;
                            case DUMP_MARKER:
                                driveCurve.update();
                                if (driveCurve.isCurveComplete()) {
                                    driveCurve.stopCurve(DcMotor8863.FinishBehavior.HOLD);
                                    robot.deliveryLiftSystem.deliveryBoxToDump();
                                    logFile.logData("Dumped marker");
                                    // reset the timer to 0 and then wait for it to expire
                                    timer.reset();
                                    step = Steps.WAIT_FOR_DUMP;
                                }
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
                                task = autonomousDirector.getNextTask();
                                step = Steps.START;
                                break;
                            case STOP:
                                break;
                        }
                        break;
                    case LEFT:
                        logStep(step);
                        switch (step) {
                            case START:
                                robot.driveTrain.setupDriveUsingIMU(180, inchesToCM(10.98), .1, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                robot.driveTrain.startDriveUsingIMU();
                                step = Steps.SETUP_CURVE_ONTO_DEPOT_LANE;
                                break;
                            case SETUP_CURVE_ONTO_DEPOT_LANE:
                                if (robot.driveTrain.updateDriveUsingIMU()) {
                                    driveCurve.setupDriveCurve(135, 0.1, inchesToCM(17.17), DriveCurve.CurveDirection.CW, DriveCurve.DriveDirection.BACKWARD);
                                    driveCurve.startDriveCurve();
                                    step = Steps.RUN_CURVE_ONTO_DEPOT_LANE;
                                }
                                break;
                            case RUN_CURVE_ONTO_DEPOT_LANE:
                                driveCurve.update();
                                if (driveCurve.isCurveComplete()) {
                                    driveCurve.stopCurve(DcMotor8863.FinishBehavior.HOLD);
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
                                task = autonomousDirector.getNextTask();
                                step = Steps.START;
                                break;
                            case STOP:
                                break;
                        }
                        break;

                }
                break;

            case PARK_IN_OUR_CRATER_FROM_DEPOT_SIDE_MINERALS:
                switch (goldMineralPosition) {
                    case CENTER:
                        logStep(step);
                        switch (step) {
                            case START:
                                driveCurve.setupDriveCurve(-45, .3, inchesToCM(9.682), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD);
                                driveCurve.startDriveCurve();
                                step = Steps.RUN_CURVE_ONTO_DEPOT_LANE;
                                break;
                            case RUN_CURVE_ONTO_DEPOT_LANE:
                                driveCurve.update();
                                if (driveCurve.isCurveComplete()) {
                                    //driveCurve.stopCurve(DcMotor8863.FinishBehavior.HOLD);
                                    robot.driveTrain.setupDriveUsingIMU(-45, inchesToCM(53.456), .3, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                    robot.driveTrain.startDriveUsingIMU();
                                    //timer.reset();
                                    step = Steps.RUN_DRIVE_TO_CRATER;
                                }
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
                break;

            case CLAIM_DEPOT_FROM_CRATER_SIDE_LANDER:
                logStep(step);
                switch (step) {
                    case START:
                        break;
                }
                break;

            case PARK_IN_OUR_CRATER_FROM_DEPOT:
                logStep(step);
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

            case PARK_IN_OTHER_CRATER_FROM_DEPOT:
                switch (goldMineralPosition) {
                    case CENTER:
                    case LEFT_CENTER:
                    case CENTER_RIGHT:
                    case TIE:
                        logStep(step);
                        switch (step) {
                            case START:
                                adjustment = -4;
                                driveCurve.setupDriveCurve(-45 + adjustment, .1, inchesToCM(9.682), DriveCurve.CurveDirection.CCW, DriveCurve.DriveDirection.BACKWARD);
                                driveCurve.startDriveCurve();
                                step = Steps.RUN_CURVE_ONTO_CRATER_LANE;
                                break;
                            case RUN_CURVE_ONTO_CRATER_LANE:
                                driveCurve.update();
                                if (driveCurve.isCurveComplete()) {
                                    //driveCurve.stopCurve(DcMotor8863.FinishBehavior.HOLD);
                                    robot.driveTrain.setupDriveUsingIMU(-45, inchesToCM(58), .3, DriveTrain.DriveDirection.REVERSE, AdafruitIMU8863.AngleMode.ABSOLUTE);
                                    robot.driveTrain.startDriveUsingIMU();
                                    //timer.reset();
                                    step = Steps.RUN_DRIVE_TO_CRATER;
                                }
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
                    case RIGHT:
                    case LEFT_RIGHT:
                        logStep(step);
                        switch (step) {
                            case START:
                                robot.driveTrain.setupDriveUsingIMU(+135, inchesToCM(60.9), .4, DriveTrain.DriveDirection.FORWARD, AdafruitIMU8863.AngleMode.ABSOLUTE);
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
                    case LEFT:
                        logStep(step);
                        switch (step) {
                            case START:
                                robot.driveTrain.setupDriveUsingIMU(+132, inchesToCM(69), .4, DriveTrain.DriveDirection.FORWARD, AdafruitIMU8863.AngleMode.ABSOLUTE);
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
                break;
        }
    }
}
