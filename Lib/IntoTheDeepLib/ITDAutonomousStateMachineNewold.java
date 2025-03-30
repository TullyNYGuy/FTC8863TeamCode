package org.firstinspires.ftc.teamcode.Lib.IntoTheDeepLib;


import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ProfileAccelConstraint;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogOnChange;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.RRNonBlockingRunner;

public class ITDAutonomousStateMachineNewold {

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
        WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_START,
        WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE1,
        WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE2,
        WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE3,
        WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE4,
        WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE5,
        WAIT_FOR_DELIVERY_JOE,
        WAIT_FOR_NO_WOBBLE,
        WAIT_FOR_DELIVER_SAMPLE,
        WAIT_FOR_MOVE_TO_SAMPLE1,
        WAIT_FOR_MOVE_TO_SAMPLE2,
        WAIT_FOR_MOVE_TO_SAMPLE3,
        WAIT_FOR_MOVE_TO_SAMPLE4,
        WAIT_FOR_MOVE_TO_SAMPLE5,
        WAITING_FOR_SETUP_OF_GLIDING_INTAKE_AT_SUBMERSIBLE,
        WAIT_FOR_SETUP_NEW_GLIDING_INTAKE_FOR_SAMPLE2,
        WAIT_FOR_SETUP_NEW_GLIDING_INTAKE_FOR_SAMPLE3,
        WAIT_FOR_EXTENSTION_ARM_RETRACTION_TO_SAMPLE3,
        WAIT_FOR_MOVE_TO_SAMPLE4_AFTER_SAMPLE3_FAILED,
        WAIT_FOR_SETUP_FOR_INTAKE,
        WAIT_FOR_INTAKE,
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

    public ITDAutonomousStateMachineNewold(ITDRobot robot, Telemetry telemetry) {
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
    private Pose2d sample1IntakePose = new Pose2d(54.5, 44.5, Math.toRadians(-106.7821));
   // private Pose2d sample2IntakePose=new Pose2d(59, 39.25, Math.toRadians(-90));
    private Pose2d sample2IntakePose=new Pose2d(57.75, 45, Math.toRadians(-90));
    //private Pose2d sample3IntakePose=new Pose2d(55.5, 25.25, Math.toRadians(0));
    private Pose2d sample3IntakePose=new Pose2d(60.75, 43.5, Math.toRadians(-70));
    private Pose2d initAfter3rdSampleFail=new Pose2d(49.5, 41, Math.toRadians(-45));
    private Pose2d sample4WaypointPose=new Pose2d(43, 23.5, Math.toRadians(-135));
    private Pose2d subPose=new Pose2d(16.5, 10, Math.toRadians(-180));
    private Pose2d sample5IntakePose=new Pose2d(16.5, 10.5, Math.toRadians(-200));

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
    private Action sample3ToSample4;
    private Action sample3ToInitPose;
    private Action deliveryToSubPose;
    private Action subToDelivery;
    private Action sample4ToSample5;
    private Action sample5ToDelivery;

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
    RRNonBlockingRunner sample3ToSample4Runner;
    RRNonBlockingRunner currentRunner;
    RRNonBlockingRunner sample3ToInitPoseRunner;
    RRNonBlockingRunner deliveryToSubPoseRunner;
    RRNonBlockingRunner subToDeliveryRunnner;
    RRNonBlockingRunner sample4ToSample5Runner;
    RRNonBlockingRunner sample5ToDeliveryRunner;
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

        sample3ToSample4 = robot.mecanumDrive.actionBuilder(sample3IntakePose)
                .strafeToLinearHeading(subPose.position, subPose.heading)
                .build();

        sample4ToSample5 = robot.mecanumDrive.actionBuilder(subPose)
                .strafeToLinearHeading(sample5IntakePose.position, sample5IntakePose.heading)
                .build();

        sample3ToInitPose = robot.mecanumDrive.actionBuilder(sample3IntakePose)
                .strafeToLinearHeading(initAfter3rdSampleFail.position, initAfter3rdSampleFail.heading)
                .build();

        deliveryToSubPose = robot.mecanumDrive.actionBuilder(deliveryPose)
                .strafeToLinearHeading(sample4WaypointPose.position, sample4WaypointPose.heading)
                .splineToSplineHeading(subPose, -Math.PI)
                .build();

        subToDelivery = robot.mecanumDrive.actionBuilder(subPose)
                .setReversed(true)
                .splineToLinearHeading(deliveryPose, Math.PI/3
                       // ,new TranslationalVelConstraint(75.0)
                     //   ,new ProfileAccelConstraint(-75.0, 75.0)
                )
                .build();
        sample5ToDelivery = robot.mecanumDrive.actionBuilder(sample5IntakePose)
                .setReversed(true)
                .splineToLinearHeading(deliveryPose, Math.PI/3
//                        ,new TranslationalVelConstraint(75.0)
//                        ,new ProfileAccelConstraint(-75.0, 75.0)
                )
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
        sample3ToSample4Runner = new RRNonBlockingRunner(sample3ToSample4);
        sample3ToInitPoseRunner=new RRNonBlockingRunner(sample3ToInitPose);
        deliveryToSubPoseRunner=new RRNonBlockingRunner(deliveryToSubPose);
        subToDeliveryRunnner=new RRNonBlockingRunner(subToDelivery);
        sample4ToSample5Runner=new RRNonBlockingRunner(sample4ToSample5);
        sample5ToDeliveryRunner=new RRNonBlockingRunner(sample5ToDelivery);
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
                robot.intakeBucketController.setupForDeliveryUponStart();
                currentState = States.WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_START;
                break;
//            case WAIT_FOR_GET_READY_2_RUN:
//                if (robot.intakeBucketController.isGetReadyToRunComplete()) {
//                    logPosition("start at", startAutoPose);
//                    startToDeliveryRunner.runNonBlocking();
//                    logCommand("move to delivery");
//                    currentState = States.WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_START;
//                }
//                break;
            case WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_START:
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
                            robot.intakeBucketController.setupForGlidingIntake(11, ITDIntakeArmServo.IntakeHeight.HIGH_ALTITUDE_PREP);
                            currentState = States.WAIT_FOR_MOVE_TO_SAMPLE1;
                            break;
                        case 2:
                            // the lift/bucket will continue to move into the transfer position
                            // start the movement from delivery position to position to intake sample
                            currentRunner=deliveryToSample2Runner;
                            currentRunner.runNonBlocking();
                            // setup the intake for a gliding intake
                            robot.intakeBucketController.setupForGlidingIntake(10, ITDIntakeArmServo.IntakeHeight.HIGH_ALTITUDE_PREP);
                            currentState = States.WAIT_FOR_MOVE_TO_SAMPLE2;
                            break;
                        case 3:
                            // the lift/bucket will continue to move into the transfer position*
                            // start the movement from delivery position to position to intake sample
                            currentRunner=deliveryToSample3Runner;
                            currentRunner.runNonBlocking();
                            // setup the intake for a gliding intake
                            robot.intakeBucketController.setupForGlidingIntake(11, ITDIntakeArmServo.IntakeHeight.HIGH_ALTITUDE_PREP);
                            currentState = States.WAIT_FOR_MOVE_TO_SAMPLE3;
                            break;
                        case 4:
                            // the lift/bucket will continue to move into the transfer position*
                            // start the movement from delivery position to the submersible
                            currentRunner=deliveryToSubPoseRunner;
                            currentRunner.runNonBlocking();
                            // Due to the movement of the robot we cannot setup the gliding intake
                            // until we get to the submersible. The extension arm will collide with
                            // the submersiblesetup
                            //robot.intakeBucketController.setupForGlidingIntake(11);
                            currentState = States.WAIT_FOR_MOVE_TO_SAMPLE4;
                            break;
                        case 5:
                        case 6:
                            currentState =States.WAIT_FOR_FINAL_BUCKET_AT_TRANSFER;
                            break;
                    }
                }
                break;
            case WAIT_FOR_MOVE_TO_SAMPLE1:
                // if the movement is complete and the bucket is ready for a transfer and the intake
                // is setup for a gliding intake
                if (deliveryToSample1Runner.isComplete() &&
                        robot.intakeBucketController.isSetupForGlidingIntakeComplete()) {
                    logPosition("Sample1 intake pose", sample1IntakePose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    // run a gliding intake
                    robot.intakeBucketController.runGlidingIntake(15.75, ITDIntakeArmServo.IntakeHeight.HIGH,.2, 500);
                    glidingIntakeFailed = false;
                    currentState = States.WAIT_FOR_INTAKE;
                } else {
                    // otherwise continue to run the movement or wait for the setup for gliding intake to complete
                    deliveryToSample1Runner.runNonBlocking();
                }
                break;
            case WAIT_FOR_MOVE_TO_SAMPLE2:
                if(currentRunner.isComplete() &&
                        robot.intakeBucketController.isSetupForGlidingIntakeComplete()) {
                    logPosition("Sample2 intake pose", sample2IntakePose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    robot.intakeBucketController.runGlidingIntake(15.75, ITDIntakeArmServo.IntakeHeight.HIGH,0.2, 500);
                    glidingIntakeFailed = false;
                    currentState = States.WAIT_FOR_INTAKE;
                } else {
                    // otherwise continue to run the movement or wait for the setup for gliding intake to complete
                    currentRunner.runNonBlocking();
                }
                break;
            case WAIT_FOR_MOVE_TO_SAMPLE3:
                if(currentRunner.isComplete() &&
                        robot.intakeBucketController.isSetupForGlidingIntakeComplete()) {
                    logPosition("Sample3 intake pose", sample3IntakePose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    robot.intakeBucketController.runGlidingIntake(15.75, ITDIntakeArmServo.IntakeHeight.HIGH,0.2,500);
                    glidingIntakeFailed = false;
                    currentState = States.WAIT_FOR_INTAKE;
                } else {
                    // otherwise continue to run the movement or wait for the setup for gliding intake to complete
                    currentRunner.runNonBlocking();
                }
                break;
            case WAIT_FOR_MOVE_TO_SAMPLE4:
                if (currentRunner.isComplete()) {
                    logPosition("sub Pose ", subPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    robot.intakeBucketController.setupForGlidingIntake(3.5, ITDIntakeArmServo.IntakeHeight.BUCKET_CLEARANCE);
                    currentState = States.WAITING_FOR_SETUP_OF_GLIDING_INTAKE_AT_SUBMERSIBLE;
                } else {
                    // continue to run the movement
                    currentRunner.runNonBlocking();
                }
                break;
            case WAIT_FOR_MOVE_TO_SAMPLE5:
                if (currentRunner.isComplete()) {
                    logPosition("sub Pose ", subPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    robot.intakeBucketController.setupForGlidingIntake(3.5, ITDIntakeArmServo.IntakeHeight.BUCKET_CLEARANCE);
                    currentState = States.WAITING_FOR_SETUP_OF_GLIDING_INTAKE_AT_SUBMERSIBLE;
                } else {
                    // continue to run the movement
                    currentRunner.runNonBlocking();
                }
                break;
            case WAITING_FOR_SETUP_OF_GLIDING_INTAKE_AT_SUBMERSIBLE:
                if (robot.intakeBucketController.isSetupForGlidingIntakeComplete()) {
                    robot.intakeBucketController.runGlidingIntake(15.75, ITDIntakeArmServo.IntakeHeight.HIGH, 0.1, 500);
                    glidingIntakeFailed = false;
                    currentState = States.WAIT_FOR_INTAKE;
                }
                break;
            case WAIT_FOR_SETUP_FOR_INTAKE:
                if (robot.intakeBucketController.isSetupForIntakeComplete()) {
                    robot.intakeBucketController.intakeLowAltitude();
                    currentState = States.WAIT_FOR_INTAKE;
                }
                break;
            case WAIT_FOR_INTAKE:
                //todo maybe start the movement once the intake has seen a sample? But will the intake
                //always intake the sample if it has only seen the sample?
                if(robot.intakeBucketController.intakeHasValidSample()) {
                    // the gliding intake was successful
                    switch (sampleNum) {
                        case 1:
                            // the intake will transfer the sample while the robot moves
                            sample1ToDeliveryRunner.runNonBlocking();
                            currentState = States.WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE1;
                            break;
                        case 2:
                            // the intake will transfer the sample while the robot moves
                            sample2ToDeliveryRunner.runNonBlocking();
                            currentState = States.WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE2;
                            break;
                        case 3:
                            // the intake will transfer the sample while the robot moves
                            sample3ToDeliveryRunner.runNonBlocking();
                            currentState = States.WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE3;
                            break;
                        case 4:
                            subToDeliveryRunnner.runNonBlocking();
                            currentState = States.WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE4;
                            break;
                        case 5:
                            sample5ToDeliveryRunner.runNonBlocking();
                            currentState = States.WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE5;
                            break;

                    }
                }
                if (glidingIntakeFailed){
                    // the gliding intake failed
                    switch(sampleNum){
                        case 1:
                            sampleNum=sampleNum+1;
                            robot.intakeBucketController.setupForGlidingIntake(11, ITDIntakeArmServo.IntakeHeight.HIGH_ALTITUDE_PREP);
                            currentState=States.WAIT_FOR_SETUP_NEW_GLIDING_INTAKE_FOR_SAMPLE2;
                            break;
                        case 2:
                            sampleNum=sampleNum+1;
                            robot.intakeBucketController.setupForGlidingIntake(11, ITDIntakeArmServo.IntakeHeight.HIGH_ALTITUDE_PREP);
                            currentState=States.WAIT_FOR_SETUP_NEW_GLIDING_INTAKE_FOR_SAMPLE3;
                            break;
                        case 3:
                            sampleNum=sampleNum+1;
                            robot.intakeBucketController.setupForGlidingIntake(3.5, ITDIntakeArmServo.IntakeHeight.BUCKET_CLEARANCE);
                            currentRunner=sample3ToSample4Runner;
                            currentRunner.runNonBlocking();
                            currentState=States.WAIT_FOR_MOVE_TO_SAMPLE4;
                            break;
                        case 4:
                            sampleNum=sampleNum+1;
                            robot.intakeBucketController.setupForGlidingIntake(3.5, ITDIntakeArmServo.IntakeHeight.BUCKET_CLEARANCE);
                            currentRunner=sample4ToSample5Runner;
                            currentRunner.runNonBlocking();
                            currentState=States.WAIT_FOR_MOVE_TO_SAMPLE5;
                            break;
                        case 5:
                            sampleNum=sampleNum+1;
                           robot.liftBucketArmBucketGateController.parkPosition();
                            break;
                    }
                }
                break;

            case  WAIT_FOR_SETUP_NEW_GLIDING_INTAKE_FOR_SAMPLE2:
                if(robot.intakeBucketController.isSetupForGlidingIntakeComplete()){
                    currentRunner=sample1ToSample2Runner;
                    currentRunner.runNonBlocking();
                    currentState= States.WAIT_FOR_MOVE_TO_SAMPLE2;
                }
                break;

            case  WAIT_FOR_SETUP_NEW_GLIDING_INTAKE_FOR_SAMPLE3:
                if(robot.intakeBucketController.isSetupForGlidingIntakeComplete()){
                    currentRunner=sample2ToSample3Runner;
                    currentRunner.runNonBlocking();
                    currentState= States.WAIT_FOR_MOVE_TO_SAMPLE3;
                }
                break;

            case WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE1:
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
                        robot.intakeBucketController.setupForDelivery();
                        currentState = States.WAIT_FOR_DELIVERY_JOE;
                    }

                } else {
                    sample1ToDeliveryRunner.runNonBlocking();
                }
                break;
            case WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE2:
                if(sample2ToDeliveryRunner.isComplete() && robot.intakeBucketController.isTransferComplete()) {
                    logPosition("Delivery pose", deliveryPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    if (glidingIntakeFailed) {
                        currentState = States.WAIT_FOR_DELIVER_SAMPLE;
                    }
                    else {
                        robot.intakeBucketController.setupForDelivery();
                        currentState = States.WAIT_FOR_DELIVERY_JOE;
                    }

                } else {
                    sample2ToDeliveryRunner.runNonBlocking();
                }
                break;
            case WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE3:
                if(sample3ToDeliveryRunner.isComplete()&& robot.intakeBucketController.isTransferComplete()) {
                    logPosition("Delivery pose", deliveryPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    if (glidingIntakeFailed) {
                        currentState = States.WAIT_FOR_DELIVER_SAMPLE;
                    }
                    else {
                        robot.intakeBucketController.setupForDelivery();
                        currentState = States.WAIT_FOR_DELIVERY_JOE;
                    }

                } else {
                    sample3ToDeliveryRunner.runNonBlocking();
                }
                break;
            case WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE4:
                if(subToDeliveryRunnner.isComplete()&& robot.intakeBucketController.isTransferComplete()) {
                    logPosition("Delivery pose", deliveryPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    if (glidingIntakeFailed) {
                        currentState = States.WAIT_FOR_DELIVER_SAMPLE;
                    }
                    else {
                    //    robot.intakeBucketController.setupForDelivery();
                        currentState = States.WAIT_FOR_DELIVERY_JOE;
                    }

                } else {
                    subToDeliveryRunnner.runNonBlocking();
                }
                break;
            case WAIT_FOR_M0VE_TO_DELIVERY_POS_FROM_SAMPLE5:
                if(sample5ToDeliveryRunner.isComplete()&& robot.intakeBucketController.isTransferComplete()) {
                    logPosition("Delivery pose", deliveryPose);
                    logPosition("Actual pose", robot.mecanumDrive.pose);
                    if (glidingIntakeFailed) {
                        currentState = States.WAIT_FOR_DELIVER_SAMPLE;
                    }
                    else {
                      //  robot.intakeBucketController.setupForDelivery();
                        currentState = States.WAIT_FOR_DELIVERY_JOE;
                    }

                } else {
                    sample5ToDeliveryRunner.runNonBlocking();
                }
                break;

            case WAIT_FOR_FINAL_BUCKET_AT_TRANSFER:
               if (robot.intakeBucketController.isLiftBucketAtTransferPosition()){
                   robot.intakeBucketController.init(null);
                   currentState= States.COMPLETE;
               }
                break;

               // for when sample 3 intake fails
            case WAIT_FOR_MOVE_TO_INIT_POSE:
                if(sample3ToInitPoseRunner.isComplete()){
                    robot.intakeBucketController.init(null);
                    currentState= States.COMPLETE;
                }
                    else {
                    sample3ToInitPoseRunner.runNonBlocking();
                }
                break;

            case COMPLETE:
                // the last actions will complete while this state runs
                break;

        }
    }
}