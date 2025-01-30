package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;


import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RRNonBlockingRunner;

public class ITDAutonomousStateMachine {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum States {
        IDLE,
        START,
        WAIT_FOR_GET_READY_2_RUN,
        WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_START,
        WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE1,
        WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE2,
        WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE3,
        WAIT_FOR_DELIVERY_JOE,
        WAIT_FOR_DELIVER_SAMPLE,
        WAIT_FOR_MOVE_2_SAMPLE1,
        WAIT_FOR_MOVE_2_SAMPLE2,
        WAIT_FOR_MOVE_2_SAMPLE3,
        WAIT_FOR_SETUP_FOR_INTAKE,
        WAIT_FOR_INTAKE,

        WAIT_TO_MOVE_TO_SUBMERISLBE,

        COMPLETE
    }

    private States currentState;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private ITDRobot robot;
    public ITDPinpointDrive mecanumDrive;
    private ElapsedTime timer;
    private DistanceUnit distanceUnits;
    private AngleUnit angleUnits;
    private boolean isComplete = false;

    private DataLogging logFile;
    private boolean enableLogging = false;
    private DataLogOnChange logStateOnChange;
    private DataLogOnChange logCommandOnchange;
    private int sampleNum = 0;

//     actionBuilder(beginPose)
//    // start to delivery Joe
//            .splineToLinearHeading(new Pose2d(48.5, 51.75,Math.toRadians(-135)), Math.PI / 2)
//            //first pickup
//            .strafeToLinearHeading(new Vector2d(47.75, 39), Math.toRadians(-90))
//            //delivery Joe
//            .strafeToLinearHeading(new Vector2d(48.5, 51.75), Math.toRadians(-135))
//            //second pickup
//            .strafeToLinearHeading(new Vector2d(59, 39.25), Math.toRadians(-90))
//            //delivery Joe
//            .strafeToLinearHeading(new Vector2d(48.5, 51.75), Math.toRadians(-135))
//            //third pickup
//            .strafeToLinearHeading(new Vector2d(60, 34.5), Math.toRadians(-45))
//            //delivery Joe
//            .strafeToLinearHeading(new Vector2d(48.5, 51.75), Math.toRadians(-135))
//            .build());

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getPositionInTermsOfAttachment
    //*********************************************************************************************

    public boolean isComplete() {
        return isComplete;
    }

    public String getCurrentState() {
        return currentState.toString();
    }

    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************

    public ITDAutonomousStateMachine(ITDRobot robot, Telemetry telemetry) {
        this.robot = robot;
        this.mecanumDrive = robot.mecanumDrive;

        currentState = States.IDLE;
        distanceUnits = DistanceUnit.INCH;
        angleUnits = AngleUnit.DEGREES;
        timer = new ElapsedTime();

        createMovements();
    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    public String getName() {
        return "Auto";
    }

    public void setDataLog(DataLogging logFile) {
        this.logFile = logFile;
        logCommandOnchange = new DataLogOnChange(logFile);
        logStateOnChange = new DataLogOnChange(logFile);
    }

    public void enableDataLogging() {
        enableLogging = true;
    }

    public void disableDataLogging() {
        enableLogging = false;
    }

    private void logState() {
        if (enableLogging && logFile != null) {
            logStateOnChange.log(getName() + " state = " + currentState.toString());
        }
    }

    private void logCommand(String command) {
        if (enableLogging && logFile != null) {
            logCommandOnchange.log(getName() + " command = " + command);
        }
    }

    private void logPosition(String comment, Pose2d pose2d) {
        if (enableLogging && logFile != null) {
            logFile.logData(comment + " " + pose2d.toString());
        }
    }

    //*********************************************************************************************
    //          Robot Positions
    //*********************************************************************************************

    // Define the robot positions
    private Pose2d startAutoPose = new Pose2d(32.5, 54.375, Math.toRadians(-90));
    private Pose2d deliveryPose = new Pose2d(48.5, 51.75, Math.toRadians(-135));
    private Pose2d sample1IntakePose = new Pose2d(47.75, 39, Math.toRadians(-90));
    private Pose2d sample2IntakePose=new Pose2d(59, 39.25, Math.toRadians(-90));
    private Pose2d sample3IntakePose=new Pose2d(60, 34.5, Math.toRadians(-45));

    // Define the action needed for a movement from point a to point b
    private Action startToDelivery;
    private Action deliveryToSample1;
    private Action sample1ToDelivery;
    private Action deliveryToSample2;
    private Action sample2ToDelivery;
    private Action deliveryToSample3;
    private Action sample3ToDelivery;

    // Define a non-blocking runner to run the action
    RRNonBlockingRunner startToDeliveryRunner;
    RRNonBlockingRunner deliveryToSample1Runner;
    RRNonBlockingRunner sample1ToDeliveryRunner;
    RRNonBlockingRunner deliveryToSample2Runner;
    RRNonBlockingRunner sample2ToDeliveryRunner;
    RRNonBlockingRunner deliveryToSample3Runner;
    RRNonBlockingRunner sample3ToDeliveryRunner;

    /**
     * Place all of the trajectories for the autonomous opmode in this method. This method gets
     * called from the constructor so that the trajectories are created when the autonomous object
     * is created.
     */
    public void createMovements() {
        startToDelivery = robot.mecanumDrive.actionBuilder(startAutoPose)
                .splineToLinearHeading(deliveryPose, Math.PI / 2)
                .build();

        deliveryToSample1 = robot.mecanumDrive.actionBuilder(deliveryPose)
                .strafeToLinearHeading(sample1IntakePose.position, sample1IntakePose.heading)
                .build();

        sample1ToDelivery = robot.mecanumDrive.actionBuilder(sample1IntakePose)
                .strafeToLinearHeading(deliveryPose.position, deliveryPose.heading)
                .build();

        deliveryToSample2 = robot.mecanumDrive.actionBuilder(deliveryPose)
                .strafeToLinearHeading(sample2IntakePose.position, sample2IntakePose.heading)
                .build();

        sample2ToDelivery = robot.mecanumDrive.actionBuilder(sample2IntakePose)
                .strafeToLinearHeading(deliveryPose.position, deliveryPose.heading)
                .build();

        deliveryToSample3 = robot.mecanumDrive.actionBuilder(deliveryPose)
                .strafeToLinearHeading(sample3IntakePose.position, sample3IntakePose.heading)
                .build();

        sample3ToDelivery = robot.mecanumDrive.actionBuilder(sample3IntakePose)
                .strafeToLinearHeading(deliveryPose.position, deliveryPose.heading)
                .build();

        // Define a non-blocking runner to run the action
        startToDeliveryRunner = new RRNonBlockingRunner(startToDelivery);
        deliveryToSample1Runner = new RRNonBlockingRunner(deliveryToSample1);
        sample1ToDeliveryRunner = new RRNonBlockingRunner(sample1ToDelivery);
        deliveryToSample2Runner = new RRNonBlockingRunner(deliveryToSample2);
        sample2ToDeliveryRunner  = new RRNonBlockingRunner(sample2ToDelivery);
        deliveryToSample3Runner = new RRNonBlockingRunner(deliveryToSample3);
        sample3ToDeliveryRunner = new RRNonBlockingRunner(sample3ToDelivery);
    }

    //*********************************************************************************************
    //          State machine
    //*********************************************************************************************

    public void start() {
        // initialize the location of the robot
        robot.mecanumDrive.pose = startAutoPose;
        currentState = States.START;
        isComplete = false;
        logCommand("start");
    }

    public void update() {
        logState();

        switch (currentState) {

            case START:
                robot.intakeBucketController.getReadyToRun();
                currentState = States.WAIT_FOR_GET_READY_2_RUN;
                break;
            case WAIT_FOR_GET_READY_2_RUN:
                if (robot.intakeBucketController.isGetReadyToRunComplete()) {
                    logPosition("start at", startAutoPose);
                    startToDeliveryRunner.runNonBlocking();
                    logCommand("move to delivery");
                    currentState = States.WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_START;
                }
                break;
            case WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_START:
                if(startToDeliveryRunner.isComplete()) {
                    logPosition("Delivery pose", deliveryPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    robot.intakeBucketController.setupForDrivingBeforeDelivery();
                    currentState = States.WAIT_FOR_DELIVERY_JOE;
                } else {
                    startToDeliveryRunner.runNonBlocking();
                }

                break;
            case WAIT_FOR_DELIVERY_JOE:
                if (robot.intakeBucketController.isSetupForDeliveryComplete()) {
                    robot.intakeBucketController.deliverSample();
                    currentState = States.WAIT_FOR_DELIVER_SAMPLE;
                }
                break;
            case WAIT_FOR_DELIVER_SAMPLE:
                if (robot.intakeBucketController.isDeliveryComplete()) {
                    sampleNum = sampleNum + 1;
                    logFile.logData("Moving on to sample " + sampleNum);
                    switch (sampleNum) {
                        case 1:
                           deliveryToSample1Runner.runNonBlocking();
                            currentState = States.WAIT_FOR_MOVE_2_SAMPLE1;
                            break;
                        case 2:
                            deliveryToSample2Runner.runNonBlocking();
                            break;
                        case 3:
                           deliveryToSample3Runner.runNonBlocking();
                            break;
                        case 4:
                            isComplete = true;
                            currentState=States.COMPLETE;
                            break;
                    }
                }
                break;
            case WAIT_FOR_MOVE_2_SAMPLE1:
                if(deliveryToSample1Runner.isComplete()) {
                    logPosition("Sample1 intake pose", sample1IntakePose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    robot.intakeBucketController.setupForIntake();
                    currentState = States.WAIT_FOR_SETUP_FOR_INTAKE;
                } else {
                    deliveryToSample1Runner.runNonBlocking();
                }
                break;
            case WAIT_FOR_MOVE_2_SAMPLE2:
                if(deliveryToSample2Runner.isComplete()) {
                    logPosition("Sample2 intake pose", sample2IntakePose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    robot.intakeBucketController.setupForIntake();
                    currentState = States.WAIT_FOR_SETUP_FOR_INTAKE;
                } else {
                    deliveryToSample2Runner.runNonBlocking();
                }
                break;
            case WAIT_FOR_MOVE_2_SAMPLE3:
                if(deliveryToSample3Runner.isComplete()) {
                    logPosition("Sample3 intake pose", sample3IntakePose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    robot.intakeBucketController.setupForIntake();
                    currentState = States.WAIT_FOR_SETUP_FOR_INTAKE;
                } else {
                    deliveryToSample3Runner.runNonBlocking();
                }
                break;
            case WAIT_FOR_SETUP_FOR_INTAKE:
                if (robot.intakeBucketController.isSetupForIntakeComplete()) {
                    robot.intakeBucketController.intake();
                    currentState = States.WAIT_FOR_INTAKE;
                }
                break;
            case WAIT_FOR_INTAKE:
                if(robot.intakeBucketController.isTransferComplete()) {
                    switch (sampleNum) {
                        case 1:
                            sample1ToDeliveryRunner.runNonBlocking();
                            currentState = States.WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE1;
                            break;
                        case 2:
                            deliveryToSample2Runner.runNonBlocking();
                            currentState = States.WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE2;
                            break;
                        case 3:
                            deliveryToSample3Runner.runNonBlocking();
                            currentState = States.WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE3;
                            break;
                    }
                }
                break;
            case WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE1:
                if(sample1ToDeliveryRunner.isComplete()) {
                    logPosition("Delivery pose", deliveryPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    robot.intakeBucketController.setupForDrivingBeforeDelivery();
                    currentState = States.WAIT_FOR_DELIVERY_JOE;
                } else {
                    sample1ToDeliveryRunner.runNonBlocking();
                }
                break;
            case WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE2:
                if(sample2ToDeliveryRunner.isComplete()) {
                    logPosition("Delivery pose", deliveryPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    robot.intakeBucketController.setupForDrivingBeforeDelivery();
                    currentState = States.WAIT_FOR_DELIVERY_JOE;
                } else {
                    sample2ToDeliveryRunner.runNonBlocking();
                }
                break;
            case WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE3:
                if(sample3ToDeliveryRunner.isComplete()) {
                    logPosition("Delivery pose", deliveryPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    robot.intakeBucketController.setupForDrivingBeforeDelivery();
                    currentState = States.WAIT_FOR_DELIVERY_JOE;
                } else {
                    sample3ToDeliveryRunner.runNonBlocking();
                }
                break;

            case WAIT_TO_MOVE_TO_SUBMERISLBE:
                break;
            case COMPLETE:
                break;

        }
    }
}