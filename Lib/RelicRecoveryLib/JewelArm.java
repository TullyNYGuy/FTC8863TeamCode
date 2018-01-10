package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;
import org.firstinspires.ftc.teamcode.opmodes.RelicRecovery.TestJewelArm;


public class JewelArm {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************
    public enum RobotSide {
        LEFT, RIGHT;
    }

    /**
     * This defines the states for a state machine that controls the servo movements to get the color
     * sensor sitting just above the ball so a color can be read
     */
    private enum GoAboveBallStates {
        START, // no movements have been started yet
        COMPLETE, // the movement to get the color sensor above the ball has completed
        ROTATE_TO_BALL, // rotate the front back servo to put the sensor in line with the ball
        ARM_PARTIALLY_OUT, // move the elbow so the forearm is partially extended = clear the robot
        ARM_PARTIALLY_OUT_AND_PARTIALLY_DOWN, // the first movement to lower the sensor down towards the ball
        // This movement to lower the sensor on top of the ball is done
        // in steps to avoid extending too far and hitting the wall.
        ARM_COMPLETELY_OUT_AND_PARTIALLY_DOWN, // the elbow is fully extended now but the arm still
        // has to go down more
        ARM_OUT_AND_COMPLETELY_DOWN // the movement to get from partially down to sitting the sensor
        // just over the ball.
    }

    private enum GoBetweenBallStates {
        START, //no movements
        COMPLETE, //finished
        UP_AND_EXTENDING_OUT, //move up down servo up from ball, then extend the elbow servo outwards
        ROTATE_BETWEEN_BALLS, //moves front back servo between the balls, still above
        DOWN_AND_EXTENDING_OUT, //moves up down servo down between balls and extends elbow servo out more also between balls.
    }

    private enum GetBallColorAndKnockBallOffStates {
        START,
        COMPLETE,
        GO_ABOVE_BALL,
        GET_BALL_COLOR,
        MOVE_BETWEEN_BALLS,
        KNOCK_OFF_BALL,
        GO_INIT
    }

    AllianceColor.TeamColor teamColor;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    /**
     * This variable holds the current state of the movement from init to locating the color
     * sensor just above the ball. It initially equals START since nothing has moved yet.
     * When the movement is complete it will equal COMPLETE.
     */

    private GetBallColorAndKnockBallOffStates currentGetBallColorAndKnockBallOffStates = GetBallColorAndKnockBallOffStates.START;

    private GoAboveBallStates currentGoAboveBallState = GoAboveBallStates.START;

    private GoBetweenBallStates currentGoBetweenBallState = GoBetweenBallStates.START;

    private double servoArmUpPosition;
    private double servoArmDownPosition;
    private double servoArmUpDownHomePosition;
    private double servoArmCenterPosition;
    private double servoArmFrontPosition;
    private double servoArmBackPosition;
    private double servoArmInPosition;
    private double servoArmOutPosition;
    private double servoDownALittleMorePosition;
    private RobotSide robotSide;
    public Servo8863 upDownServo;
    public Servo8863 frontBackServo;
    public Servo8863 elbowServo;
    private AdafruitColorSensor8863 colorSensor;
    private Telemetry telemetry;

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
    public JewelArm(RobotSide robotSide, HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        if (robotSide == RobotSide.LEFT) {
            servoArmUpPosition = 0.05;
            servoArmDownPosition = 0.55;
            servoArmUpDownHomePosition = .2;
            servoArmFrontPosition = 0.35;
            servoArmCenterPosition = 0.5;
            servoArmBackPosition = 0.6;
            servoArmInPosition = 0.92;
            servoArmOutPosition = 0.2;
            servoDownALittleMorePosition = 0.1;

            elbowServo = new Servo8863("leftElbowServo", hardwareMap, telemetry);
            elbowServo.setDirection(Servo.Direction.FORWARD);
            elbowServo.setInitPosition(servoArmInPosition);
            elbowServo.setHomePosition(servoArmInPosition);
            elbowServo.setPositionOne(servoArmOutPosition);
            elbowServo.setPositionTwo(servoDownALittleMorePosition);

            upDownServo = new Servo8863("leftUpDownServo", hardwareMap, telemetry);
            upDownServo.setDirection(Servo.Direction.FORWARD);
            upDownServo.setInitPosition(servoArmUpPosition);
            upDownServo.setHomePosition(servoArmUpDownHomePosition);
            upDownServo.setPositionOne(servoArmDownPosition);

            frontBackServo = new Servo8863("leftFrontBackServo", hardwareMap, telemetry);
            frontBackServo.setDirection(Servo.Direction.FORWARD);
            frontBackServo.setInitPosition(servoArmCenterPosition);
            frontBackServo.setHomePosition(servoArmCenterPosition);
            frontBackServo.setPositionOne(servoArmFrontPosition);
            frontBackServo.setPositionTwo(servoArmBackPosition);

            colorSensor = new AdafruitColorSensor8863(hardwareMap, "leftColorSensor",
                    "coreDIM1", 0);

        } else {
            servoArmUpPosition = 0.05;
            servoArmDownPosition = 0.55;
            servoArmUpDownHomePosition = .1;
            servoArmFrontPosition = 0.35;
            servoArmCenterPosition = 0.5;
            servoArmBackPosition = 0.6;
            servoArmInPosition = 0.92;
            servoArmOutPosition = 0.2;//orginal was 0.5

            elbowServo = new Servo8863("rightElbowServo", hardwareMap, telemetry);
            elbowServo.setDirection(Servo.Direction.FORWARD);
            elbowServo.setInitPosition(servoArmInPosition);
            elbowServo.setHomePosition(servoArmInPosition);
            elbowServo.setPositionOne(servoArmOutPosition);


            upDownServo = new Servo8863("rightUpDownServo", hardwareMap, telemetry);
            upDownServo.setDirection(Servo.Direction.FORWARD);
            upDownServo.setInitPosition(servoArmUpPosition);
            upDownServo.setHomePosition(servoArmUpDownHomePosition);
            upDownServo.setPositionOne(servoArmDownPosition);

            frontBackServo = new Servo8863("rightFrontBackServo", hardwareMap, telemetry);
            frontBackServo.setDirection(Servo.Direction.FORWARD);
            frontBackServo.setInitPosition(servoArmCenterPosition);
            frontBackServo.setHomePosition(servoArmCenterPosition);
            frontBackServo.setPositionOne(servoArmFrontPosition);
            frontBackServo.setPositionTwo(servoArmBackPosition);

            colorSensor = new AdafruitColorSensor8863(hardwareMap, "rightColorSensor",
                    "coreDIM1", 1);

        }


    }

    //*********************************************************************************************
    //          Helper Methods
    //
    // methods that aid or support the major functions in the class
    //*********************************************************************************************
    private void delay(int mSec) {
        try {
            Thread.sleep((int) (mSec));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    //*********************************************************************************************
    //          MAJOR METHODS
    //
    // public methods that give the class its functionality
    //*********************************************************************************************

    public void armDown() {
        upDownServo.goPositionOne();
    }

    public void armUp() {
        upDownServo.goHome();
    }

    public void armFront() {
        frontBackServo.goPositionOne();
    }

    public void armBack() {
        frontBackServo.goPositionTwo();
    }

    public void armCenter() {
        frontBackServo.goHome();
    }

    public void armIn() {
        elbowServo.goHome();
    }

    public void armOut() {
        elbowServo.goPositionOne();
    }

    public void armDownALittleMore() {
        elbowServo.goPositionTwo();
    }

    //change the initposition commands with the ones above so its easier to read
    public void init() {
        upDownServo.goInitPosition();
        frontBackServo.goInitPosition();
        elbowServo.goInitPosition();
        colorSensor.turnLEDOn();
        telemetry.addData("Jewel Arm initialized", "!");
    }

    public void update() {
        boolean isUpdateGoAboveBallComplete = false;
        AdafruitColorSensor8863.ColorFromSensor ballColor;
        // this calls state machines that control the movements of the servos.
        // It could be one big state machine but it is easier to debug if we keep the movements
        // separate.
        isUpdateGoAboveBallComplete = updateGoAboveBall();
        if(isUpdateGoAboveBallComplete) {
            ballColor = getBallColor();
        }
        // one update for each movement will go here
    }

    public void shutdown() {
        upDownServo.goInitPosition();
        frontBackServo.goInitPosition();
        elbowServo.goInitPosition();
    }

    public AdafruitColorSensor8863.ColorFromSensor getBallColor() {
        AdafruitColorSensor8863.ColorFromSensor colorFromSensor;
        colorFromSensor = colorSensor.getSimpleColor();
        return colorFromSensor;
    }

    public void goInit() {
        upDownServo.goInitPosition();
        delay(500);
        frontBackServo.goInitPosition();
        delay(500);
        elbowServo.goInitPosition();
    }

    public void goHome() {
        armUp();
        armCenter();
        armIn();
    }

    public void knockFrontBall() {
        armDown();
        delay(500);
        armFront();
    }

    public void knockBackBall() {
        armDown();
        delay(500);
        armBack();
    }

    public AdafruitColorSensor8863.ColorFromSensor knockOffBall(AllianceColor.TeamColor teamColor) {
        AdafruitColorSensor8863.ColorFromSensor ballColor;
        armOut();
        delay(100);
        armDown();
        delay(50);
        armDownALittleMore();
        delay(2000);
        ballColor = getBallColor();
        if (teamColor == AllianceColor.TeamColor.BLUE) {
            if (ballColor == AdafruitColorSensor8863.ColorFromSensor.BLUE) {
                knockFrontBall();
            } else {
                knockBackBall();

            }
        }
        if (teamColor == AllianceColor.TeamColor.RED) {
            if (ballColor == AdafruitColorSensor8863.ColorFromSensor.RED) {
                knockFrontBall();
            } else {
                knockBackBall();
            }
        }
        return ballColor;
    }

    public void goAboveBall() {
        //upDownServo - up = .05 down = .50
        //frontBackServo - front = 0 back = 1
        //elbowServo - in = 1 out = 0
        frontBackServo.setPosition(.50);
        elbowServo.setPosition(.20);
        delay(1000);
        upDownServo.setPosition(.42);
        delay(1000);
        frontBackServo.setPosition(.54);
        delay(1000);
        elbowServo.setPosition(.10);
        upDownServo.setPosition(.55);
        //delay(500);
    }

    public void goAboveBall2() {
        //upDownServo - up = .05 down = .50
        //frontBackServo - front = 0 back = 1
        //elbowServo - in = 1 out = 0
        frontBackServo.setPosition(.55);
        elbowServo.setPosition(.20);
        delay(500);
        upDownServo.setPosition(.30);
        delay(500);
        elbowServo.setPosition(.10);
        delay(500);
        upDownServo.setPosition(.47);
        delay(500);
    }


    /**
     * This state machine replaces the goAboveBall2 method above. It has to be a state machine since
     * the servo movements are done in little steps and they have to be constantly updated, one update
     * per loop of the robot opmode
     *
     * @return is the overall movement complete
     */
    public boolean updateGoAboveBall() {
        boolean completed = false;
        boolean upDownServoComplete = false;
        boolean elbowServoComplete = false;

        switch (currentGoAboveBallState) {
            case START:
                telemetry.addData("state = ", currentGoAboveBallState.toString());
                // setup the movement
                // in this case we are not stepping the front back servo so just make it move in one big
                // movement
                frontBackServo.setPosition(.55);
                // transition to the next state
                currentGoAboveBallState = GoAboveBallStates.ROTATE_TO_BALL;
                break;
            case ROTATE_TO_BALL:
                telemetry.addData("state = ", currentGoAboveBallState.toString());
                // delay so the front back servo has time to reach its destination
                delay(100);
                // setup the next movements
                elbowServo.setupMoveBySteps(.20, .01, 5);
                // start the servos moving
                elbowServoComplete = elbowServo.updateMoveBySteps();
                // transition to the next state
                currentGoAboveBallState = GoAboveBallStates.ARM_PARTIALLY_OUT;
                break;
            case ARM_PARTIALLY_OUT:
                telemetry.addData("state = ", currentGoAboveBallState.toString());
                elbowServoComplete = elbowServo.updateMoveBySteps();
                if(elbowServoComplete) {
                    //setup the next movements
                    upDownServo.setupMoveBySteps(.30, .01, 5);
                    // start the servos moving
                    upDownServoComplete = upDownServo.updateMoveBySteps();
                    // transition to the next state
                    currentGoAboveBallState = GoAboveBallStates.ARM_PARTIALLY_OUT_AND_PARTIALLY_DOWN;
                }
                break;
            case ARM_PARTIALLY_OUT_AND_PARTIALLY_DOWN:
                telemetry.addData("state = ", currentGoAboveBallState.toString());
                // find out if the last step command is complete - is the movement complete?
                upDownServoComplete = upDownServo.updateMoveBySteps();
                if (upDownServoComplete) {
                    // movement is complete setup the next movement
                    elbowServo.setupMoveBySteps(.10, .01, 5);
                    // start the servo moving
                    elbowServoComplete = elbowServo.updateMoveBySteps();
                    // transition to the next state
                    currentGoAboveBallState = GoAboveBallStates.ARM_COMPLETELY_OUT_AND_PARTIALLY_DOWN;
                }
                break;
            case ARM_COMPLETELY_OUT_AND_PARTIALLY_DOWN:
                telemetry.addData("state = ", currentGoAboveBallState.toString());
                elbowServoComplete = elbowServo.updateMoveBySteps();
                if (elbowServoComplete) {
                    // movement is complete setup the next movement
                    upDownServo.setupMoveBySteps(.47, .01, 5);
                    // start the servo moving
                    upDownServoComplete = upDownServo.updateMoveBySteps();
                    // transition to the next state
                    currentGoAboveBallState = GoAboveBallStates.ARM_OUT_AND_COMPLETELY_DOWN;
                }
                break;
            case ARM_OUT_AND_COMPLETELY_DOWN:
                telemetry.addData("state = ", currentGoAboveBallState.toString());
                upDownServoComplete = upDownServo.updateMoveBySteps();
                if (upDownServoComplete) {
                    // movement is complete and this overall movement is also complete
                    // transition to the next state
                    currentGoAboveBallState = GoAboveBallStates.COMPLETE;
                }
                break;
            case COMPLETE:
                telemetry.addData("state = ", currentGoAboveBallState.toString());
                completed = true;
                break;
        }
        return completed;
    }

    private boolean updateGoBetweenBall() {
        boolean completed = false;
        boolean elbowServoComplete = false;
        boolean frontBackServoComplete = false;
        boolean upDownServoComplete = false;

        switch (currentGoBetweenBallState) {
            case START:
                upDownServo.setupMoveBySteps(.30, 0.01, 5);
                elbowServo.setupMoveBySteps(0.2, 0.01, 5);
                elbowServoComplete = elbowServo.updateMoveBySteps();
                upDownServoComplete = upDownServo.updateMoveBySteps();
                currentGoBetweenBallState = currentGoBetweenBallState.UP_AND_EXTENDING_OUT;
                break;
            case UP_AND_EXTENDING_OUT:
                elbowServoComplete = elbowServo.updateMoveBySteps();
                upDownServoComplete = upDownServo.updateMoveBySteps();
                if (elbowServoComplete && upDownServoComplete){
                    currentGoBetweenBallState = currentGoBetweenBallState.ROTATE_BETWEEN_BALLS;
                    frontBackServo.setupMoveBySteps(.5, 0.01, 5);
                }
                break;
            case ROTATE_BETWEEN_BALLS:
                frontBackServoComplete = frontBackServo.updateMoveBySteps();
                if (frontBackServoComplete){
                    currentGoBetweenBallState = currentGoBetweenBallState.DOWN_AND_EXTENDING_OUT;
                    upDownServo.setupMoveBySteps(.5, 0.01, 5);
                    elbowServo.setupMoveBySteps(0.10, 0.01, 5);
                }
                break;
            case DOWN_AND_EXTENDING_OUT:
                upDownServoComplete = upDownServo.updateMoveBySteps();
                elbowServoComplete = elbowServo.updateMoveBySteps();
                if (upDownServoComplete && elbowServoComplete){
                    currentGoBetweenBallState = currentGoBetweenBallState.COMPLETE;
                }
                break;
            case COMPLETE:
                break;
        }
        return completed;
    }

    public void moveBetweenBalls() {
        upDownServo.setPosition(.30); //up and extending
        elbowServo.setPosition(.20);
        frontBackServo.setPosition(.50); //between balls
        delay(500);
        upDownServo.setPosition(.50); //going down
        delay(100);
        elbowServo.setPosition(.10); //going farther out
        delay(500);
    }

    /**
     * This state machine replaces the moveBetweenBalls method above. It has to be a state machine since
     * the servo movements are done in little steps and they have to be constantly updated, one update
     * per loop of the robot opmode
     *
     * @return
     */
    private boolean updateMoveBetweenBalls() {
        boolean completed = false;
        boolean upDownServoComplete = false;
        boolean elbowServoComplete = false;

        return completed;
    }

    public void knockOffBall2(AllianceColor.TeamColor teamColor, AdafruitColorSensor8863.ColorFromSensor ballColor) {
        if (teamColor == AllianceColor.TeamColor.BLUE) {
            if (ballColor == AdafruitColorSensor8863.ColorFromSensor.BLUE) {
                knockFrontBall();
            } else {
                knockBackBall();
            }
        }
        if (teamColor == AllianceColor.TeamColor.RED) {
            if (ballColor == AdafruitColorSensor8863.ColorFromSensor.RED) {
                knockFrontBall();
            } else {
                knockBackBall();
                ;
            }
        }
        delay(1000);
    }

    public AdafruitColorSensor8863.ColorFromSensor getBallColorAndKnockOffBall(AllianceColor.TeamColor teamColor) {
        AdafruitColorSensor8863.ColorFromSensor ballColor;

        goAboveBall2();
        ballColor = getBallColor();
        moveBetweenBalls();
        knockOffBall2(teamColor, ballColor);
        goInit();
        delay(250);
        return ballColor;
    }

}