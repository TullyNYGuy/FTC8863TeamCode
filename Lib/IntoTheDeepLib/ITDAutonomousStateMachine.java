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
        WAIT_FOR_NO_WOBBLE,
        WAIT_FOR_DELIVER_SAMPLE,
        WAIT_FOR_MOVE_2_SAMPLE1,
        WAIT_FOR_MOVE_2_SAMPLE2,
        WAIT_FOR_MOVE_2_SAMPLE3,
        WAIT_FOR_MOVE_TO_SAMPLE3_AFTER_SAMPLE2_FAILED,
        WAIT_FOR_BUCKET_CLEARANCE_AFTER_SAMPLE2_FAILED,
        WAIT_FOR_SETUP_FOR_INTAKE,
        WAIT_FOR_INTAKE,

        WAIT_TO_MOVE_TO_SUBMERISLBE,
        WAIT_FOR_FINAL_BUCKET_AT_TRANSFER,

        WAIT_FOR_MOVE_TO_INIT_POSE,
        WAIT_FOR_SAMPLE3_INTAKE,

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

    // when a gliding intake fails, the state machine needs to know about it so it can react
    // The intake bucket controller will tell us
    private boolean glidingIntakeFailed = false;

    public void setGlidingIntakeFailed(boolean glidingIntakeFailed) {
        this.glidingIntakeFailed = glidingIntakeFailed;
    }
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
  //  private Pose2d deliveryPose = new Pose2d(48.5, 51.75, Math.toRadians(-135));
    private Pose2d deliveryPose = new Pose2d(51.75, 51, Math.toRadians(-135));
    private Pose2d sample1IntakePose = new Pose2d(47.75, 36.5, Math.toRadians(-90));
   // private Pose2d sample2IntakePose=new Pose2d(59, 39.25, Math.toRadians(-90));
    private Pose2d sample2IntakePose=new Pose2d(57.75, 36.5, Math.toRadians(-90));
    private Pose2d sample3IntakePose=new Pose2d(55.5, 25.25, Math.toRadians(0));
    private Pose2d initAfter3rdSampleFail=new Pose2d(49.5, 41, Math.toRadians(-45));

    // Define the action needed for a movement from point a to point b
    private Action startToDelivery;
    private Action deliveryToSample1;
    private Action sample1ToDelivery;
    private Action deliveryToSample2;
    private Action sample2ToDelivery;
    private Action deliveryToSample3;
    private Action sample3ToDelivery;
    private Action sample1ToSample2;
    private Action sample2ToSample3;
    private Action sample3ToInitPose;

    // Define a non-blocking runner to run the action
    RRNonBlockingRunner startToDeliveryRunner;
    RRNonBlockingRunner deliveryToSample1Runner;
    RRNonBlockingRunner sample1ToDeliveryRunner;
    RRNonBlockingRunner deliveryToSample2Runner;
    RRNonBlockingRunner sample2ToDeliveryRunner;
    RRNonBlockingRunner deliveryToSample3Runner;
    RRNonBlockingRunner sample3ToDeliveryRunner;
    RRNonBlockingRunner sample1ToSample2Runner;
    RRNonBlockingRunner sample2ToSample3Runner;
    RRNonBlockingRunner currentRunner;
    RRNonBlockingRunner sample3ToInitPoseRunner;

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

        sample1ToSample2 = robot.mecanumDrive.actionBuilder(sample1IntakePose)
                .strafeToLinearHeading(sample2IntakePose.position, sample2IntakePose.heading)
                .build();

        sample2ToSample3 = robot.mecanumDrive.actionBuilder(sample2IntakePose)
                .strafeToLinearHeading(sample3IntakePose.position, sample3IntakePose.heading)
                .build();
        sample3ToInitPose = robot.mecanumDrive.actionBuilder(sample3IntakePose)
                .strafeToLinearHeading(initAfter3rdSampleFail.position, initAfter3rdSampleFail.heading)
                .build();


        // Define a non-blocking runner to run the action
        startToDeliveryRunner = new RRNonBlockingRunner(startToDelivery);
        deliveryToSample1Runner = new RRNonBlockingRunner(deliveryToSample1);
        sample1ToDeliveryRunner = new RRNonBlockingRunner(sample1ToDelivery);
        deliveryToSample2Runner = new RRNonBlockingRunner(deliveryToSample2);
        sample2ToDeliveryRunner  = new RRNonBlockingRunner(sample2ToDelivery);
        deliveryToSample3Runner = new RRNonBlockingRunner(deliveryToSample3);
        sample3ToDeliveryRunner = new RRNonBlockingRunner(sample3ToDelivery);
        sample1ToSample2Runner = new RRNonBlockingRunner(sample1ToSample2);
        sample2ToSample3Runner = new RRNonBlockingRunner(sample2ToSample3);
        sample3ToInitPoseRunner=new RRNonBlockingRunner(sample3ToInitPose);
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
                robot.intakeBucketController.setupForDrivingBeforeDeliveryUponStart();
                currentState = States.WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_START;
                break;
//            case WAIT_FOR_GET_READY_2_RUN:
//                if (robot.intakeBucketController.isGetReadyToRunComplete()) {
//                    logPosition("start at", startAutoPose);
//                    startToDeliveryRunner.runNonBlocking();
//                    logCommand("move to delivery");
//                    currentState = States.WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_START;
//                }
//                break;
            case WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_START:
                if(startToDeliveryRunner.isComplete() && robot.intakeBucketController.isSetupForDeliveryComplete()) {
                    logPosition("Delivery pose", deliveryPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                   // robot.intakeBucketController.setupForDrivingBeforeDelivery();
                    currentState = States.WAIT_FOR_DELIVERY_JOE;
                } else {
                    startToDeliveryRunner.runNonBlocking();
                }

                break;
            case WAIT_FOR_DELIVERY_JOE:
                if (robot.intakeBucketController.isSetupForDeliveryComplete()) {
                    timer.reset();
                    currentState = States.WAIT_FOR_NO_WOBBLE;
                }
                break;
            case WAIT_FOR_NO_WOBBLE:
                // 0 second wait effectively skips this state
                if (timer.milliseconds()>0){
                    // deliver the sample. The lift/bucket controller will also return the bucket to
                    // the transfer position automatically.
                    robot.intakeBucketController.deliverSample();
                    currentState = States.WAIT_FOR_DELIVER_SAMPLE;
                }
                break;
            case WAIT_FOR_DELIVER_SAMPLE:
                if (robot.intakeBucketController.isLiftBucketSampleIsDelivered() || glidingIntakeFailed) {
                    sampleNum = sampleNum + 1;
                    logFile.logData("Moving on to sample " + sampleNum);
                    switch (sampleNum) {
                        case 1:
                            // the lift/bucket will continue to move into the transfer position
                            // start the movement from delivery position to position to intake sample
                            deliveryToSample1Runner.runNonBlocking();
                            // setup the intake for a gliding intake
                            robot.intakeBucketController.setupForGlidingIntake(2);
                            currentState = States.WAIT_FOR_MOVE_2_SAMPLE1;
                            break;
                        case 2:
                            // the lift/bucket will continue to move into the transfer position
                            // start the movement from delivery position to position to intake sample
                            currentRunner=deliveryToSample2Runner;
                            currentRunner.runNonBlocking();
                            // setup the intake for a gliding intake
                            robot.intakeBucketController.setupForGlidingIntake(2);
                            currentState = States.WAIT_FOR_MOVE_2_SAMPLE2;
                            break;
                        case 3:
                            // the lift/bucket will continue to move into the transfer position*
                            // start the movement from delivery position to position to intake sample
                            currentRunner=deliveryToSample3Runner;
                            currentRunner.runNonBlocking();
                            // setup the intake for a gliding intake
                            robot.intakeBucketController.setupForIntake(8);
                            currentState = States.WAIT_FOR_MOVE_2_SAMPLE3;
                            break;
                        case 4:
                            currentState = States.WAIT_FOR_FINAL_BUCKET_AT_TRANSFER;
                            break;
                    }
                }
                break;
            case WAIT_FOR_MOVE_2_SAMPLE1:
                // if the movement is complete and the bucket is ready for a transfer and the intake
                // is setup for a gliding intake
                if (deliveryToSample1Runner.isComplete() &&
                        //todo maybe we don't need the bucket at transfer to start the intake. Maybe we need it there before transferring?
                       // robot.intakeBucketController.isLiftBucketAtTransferPosition() &&
                        robot.intakeBucketController.isSetupForGlidingIntakeComplete()) {
                    logPosition("Sample1 intake pose", sample1IntakePose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    // run a gliding intake
                    robot.extensionArmIntakeController.setIntakeHeight(ITDExtensionArmIntakeController.IntakeHeight.LOW);
                    robot.intakeBucketController.runGlidingIntake(8,.2, 250);
                    glidingIntakeFailed = false;
                    currentState = States.WAIT_FOR_INTAKE;
                } else {
                    // otherwise continue to run the movement, or wait for the bucket to get into
                    // transfer position, or wait for the setup for gliding intake to complete
                    deliveryToSample1Runner.runNonBlocking();
                }
                break;
            case WAIT_FOR_MOVE_2_SAMPLE2:
                if(currentRunner.isComplete() &&
                       // robot.intakeBucketController.isLiftBucketAtTransferPosition() &&
                        robot.intakeBucketController.isSetupForGlidingIntakeComplete()) {
                    logPosition("Sample2 intake pose", sample2IntakePose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    robot.extensionArmIntakeController.setIntakeHeight(ITDExtensionArmIntakeController.IntakeHeight.LOW);
                    robot.intakeBucketController.runGlidingIntake(8,.2, 250);
                    glidingIntakeFailed = false;
                    currentState = States.WAIT_FOR_INTAKE;
                } else {
                    currentRunner.runNonBlocking();
                }
                break;
            case WAIT_FOR_MOVE_2_SAMPLE3:
                if(currentRunner.isComplete() &&
                       // robot.intakeBucketController.isLiftBucketAtTransferPosition() &&
                        robot.intakeBucketController.isSetupForIntakeComplete()) {
                    logPosition("Sample3 intake pose", sample3IntakePose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    robot.intakeBucketController.intakeHighAltitude();
                    glidingIntakeFailed = false;
                    currentState = States.WAIT_FOR_INTAKE;
                } else {
                    currentRunner.runNonBlocking();
                }
                break;
            case WAIT_FOR_SETUP_FOR_INTAKE:
                if (robot.intakeBucketController.isSetupForIntakeComplete()) {
                    robot.intakeBucketController.intakeLowAltitude();
                    currentState = States.WAIT_FOR_INTAKE;
                }
                break;
            case WAIT_FOR_INTAKE:
                if(robot.intakeBucketController.intakeHasValidSample()) {
                    // the gliding intake was successful
                    switch (sampleNum) {
                        case 1:
                            // the intake will transfer the sample while the robot moves
                            sample1ToDeliveryRunner.runNonBlocking();
                            currentState = States.WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE1;
                            break;
                        case 2:
                            // the intake will transfer the sample while the robot moves
                            sample2ToDeliveryRunner.runNonBlocking();
                            currentState = States.WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE2;
                            break;
                        case 3:
                            // the intake will transfer the sample while the robot moves
                            sample3ToDeliveryRunner.runNonBlocking();
                            currentState = States.WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE3;
                            break;
                    }
                }
                if (glidingIntakeFailed){
                    // the gliding intake failed
                    switch(sampleNum){
                        case 1:
                            currentRunner=sample1ToSample2Runner;
                            currentRunner.runNonBlocking();
                            sampleNum=sampleNum+1;
                            currentState=States.WAIT_FOR_MOVE_2_SAMPLE2;
                            break;
                        case 2:
                            sampleNum=sampleNum+1;
                            // if we move to the sample3 intake position without picking up the intake, the
                            // intake knocks sample 3 out of the way
                            robot.intakeBucketController.setupForBucketClearance();
                            currentState=States.WAIT_FOR_BUCKET_CLEARANCE_AFTER_SAMPLE2_FAILED;
                            break;
                        case 3:
                            sample3ToInitPoseRunner.runNonBlocking();
                            currentState=States.WAIT_FOR_MOVE_TO_INIT_POSE;
                            break;
                    }
                }
                break;
            case WAIT_FOR_BUCKET_CLEARANCE_AFTER_SAMPLE2_FAILED:
                if (robot.intakeBucketController.isIntakePositionedForBucketClearance()) {
                    currentRunner=sample2ToSample3Runner;
                    currentRunner.runNonBlocking();
                    currentState = States.WAIT_FOR_MOVE_TO_SAMPLE3_AFTER_SAMPLE2_FAILED;
                }
                break;
            case WAIT_FOR_MOVE_TO_SAMPLE3_AFTER_SAMPLE2_FAILED:
                if (currentRunner.isComplete()) {
                    robot.intakeBucketController.setupForGlidingIntake(2);
                    // even though the movement is already complete, this next state check that the movement is
                    // complete (not needed but it is ok) and then check for completion of gliding intake
                    // before running the gliding intake
                    currentState = States.WAIT_FOR_MOVE_2_SAMPLE3;
                } else {
                    currentRunner.runNonBlocking();
                }
                break;

            case WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE1:
                // The movement has to be complete and the transfer complete before setting up to deliver.
                // todo could we start the setup for delivery after the transfer is complete but while
                // the robot is still moving?
                if(sample1ToDeliveryRunner.isComplete() && robot.intakeBucketController.isTransferComplete()) {
                    logPosition("Delivery pose", deliveryPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    if (glidingIntakeFailed) {
                        currentState = States.WAIT_FOR_DELIVER_SAMPLE;
                    }
                    else {
                        robot.intakeBucketController.setupForDrivingBeforeDelivery();
                        currentState = States.WAIT_FOR_DELIVERY_JOE;
                    }

                } else {
                    sample1ToDeliveryRunner.runNonBlocking();
                }
                break;
            case WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE2:
                if(sample2ToDeliveryRunner.isComplete() && robot.intakeBucketController.isTransferComplete()) {
                    logPosition("Delivery pose", deliveryPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    if (glidingIntakeFailed) {
                        currentState = States.WAIT_FOR_DELIVER_SAMPLE;
                    }
                    else {
                        robot.intakeBucketController.setupForDrivingBeforeDelivery();
                        currentState = States.WAIT_FOR_DELIVERY_JOE;
                    }

                } else {
                    sample2ToDeliveryRunner.runNonBlocking();
                }
                break;
            case WAIT_FOR_M0VE_2_DELIVERY_POS_FROM_SAMPLE3:
                if(sample3ToDeliveryRunner.isComplete()&& robot.intakeBucketController.isTransferComplete()) {
                    logPosition("Delivery pose", deliveryPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    if (glidingIntakeFailed) {
                        currentState = States.WAIT_FOR_DELIVER_SAMPLE;
                    }
                    else {
                        robot.intakeBucketController.setupForDrivingBeforeDelivery();
                        currentState = States.WAIT_FOR_DELIVERY_JOE;
                    }

                } else {
                    sample3ToDeliveryRunner.runNonBlocking();
                }
                break;

            case WAIT_FOR_FINAL_BUCKET_AT_TRANSFER:
               if (robot.intakeBucketController.isLiftBucketAtTransferPosition()){
                   robot.intakeBucketController.init(null);
                   currentState=States.COMPLETE;
               }
                break;

               // for when sample 3 intake fails
            case WAIT_FOR_MOVE_TO_INIT_POSE:
                if(sample3ToInitPoseRunner.isComplete()){
                    robot.intakeBucketController.init(null);
                    currentState=States.COMPLETE;
                }
                    else {
                        sample3ToInitPoseRunner.runNonBlocking();
                    }

                break;

//            case WAIT_TO_MOVE_TO_SUBMERISLBE:
//                break;
            case COMPLETE:
                // the last actions will complete while this state runs
                break;

        }
    }
}