package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AllianceColor;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.DataLogging;
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

    private enum GetBallColorStates {
        START,
        COMPLETE,
        BACK_BALL_RIGHT_POSITION,
        BACK_BALL_CENTER_POSITION,
        BACK_BALL_LEFT_POSITION,
        FRONT_BALL_LEFT_POSITION,
        FRONT_BALL_CENTER_POSITION,
        FRONT_BALL_RIGHT_POSITION
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

    private GoAboveBallStates currentGoAboveBallState = GoAboveBallStates.START;

    private GoBetweenBallStates currentGoBetweenBallState = GoBetweenBallStates.START;

    private GetBallColorStates currentGetBallColorStates = GetBallColorStates.START;

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

    private DataLogging dataLog = null;

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

    public JewelArm(RobotSide robotSide, HardwareMap hardwareMap, Telemetry telemetry, DataLogging dataLog) {
        // this says to run the constructor below
        this(robotSide, hardwareMap, telemetry);
        this.dataLog = dataLog;
    }

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
        elbowServo.goInitPosition();
        frontBackServo.goInitPosition();
        colorSensor.turnLEDOn();
        telemetry.addData("Jewel Arm initialized", "!");
        if (dataLog != null) {
            dataLog.logData("Jewel Arm initialized");
        }
    }

    public void testServoMotions() {
        upDownServo.goPositionOne();
        delay(1000);
        upDownServo.goInitPosition();
        delay(1000);
        elbowServo.goPositionOne();
        delay(1000);
        elbowServo.goHome();
        delay(1000);
        frontBackServo.goPositionOne();
        delay(1000);
        frontBackServo.goPositionTwo();
    }

    public void update() {
        boolean isUpdateGoAboveBallComplete = false;
        AdafruitColorSensor8863.ColorFromSensor ballColor;
        // this calls state machines that control the movements of the servos.
        // It could be one big state machine but it is easier to debug if we keep the movements
        // separate.
        isUpdateGoAboveBallComplete = updateGoAboveBall();
        if (isUpdateGoAboveBallComplete) {
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
        if (dataLog != null) {
            dataLog.logData("Jewel color = " + colorFromSensor.toString());
        }
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
     * upDownServo - up = .05 down = .50
     * frontBackServo - front = 0 back = 1
     * elbowServo - in = 1 out = 0
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
                delay(100);
                break;
            case ROTATE_TO_BALL:
                telemetry.addData("state = ", currentGoAboveBallState.toString());
                // delay so the front back servo has time to reach its destination
                // setup the next movements
                elbowServo.setupMoveBySteps(.20, .01, 5);
                // transition to the next state
                currentGoAboveBallState = GoAboveBallStates.ARM_PARTIALLY_OUT;
                //delay(1000);
                break;
            case ARM_PARTIALLY_OUT:
                telemetry.addData("state = ", currentGoAboveBallState.toString());
                elbowServoComplete = elbowServo.updateMoveBySteps();
                if (elbowServoComplete) {
                    //setup the next movements
                    upDownServo.setupMoveBySteps(.30, .01, 5);
                    // transition to the next state
                    //currentGoAboveBallState = GoAboveBallStates.COMPLETE;
                    currentGoAboveBallState = GoAboveBallStates.ARM_PARTIALLY_OUT_AND_PARTIALLY_DOWN;
                    //delay(1000);
                }
                break;
            case ARM_PARTIALLY_OUT_AND_PARTIALLY_DOWN:
                telemetry.addData("state = ", currentGoAboveBallState.toString());
                // find out if the last step command is complete - is the movement complete?
                upDownServoComplete = upDownServo.updateMoveBySteps();
                if (upDownServoComplete) {
                    // movement is complete setup the next movement
                    elbowServo.setupMoveBySteps(.10, .01, 5);
                    // transition to the next state
                    currentGoAboveBallState = GoAboveBallStates.ARM_COMPLETELY_OUT_AND_PARTIALLY_DOWN;
                    //delay(1000);
                }
                break;
            case ARM_COMPLETELY_OUT_AND_PARTIALLY_DOWN:
                telemetry.addData("state = ", currentGoAboveBallState.toString());
                elbowServoComplete = elbowServo.updateMoveBySteps();
                if (elbowServoComplete) {
                    // movement is complete setup the next movement
                    upDownServo.setupMoveBySteps(.47, .01, 10);
                    // transition to the next state
                    currentGoAboveBallState = GoAboveBallStates.ARM_OUT_AND_COMPLETELY_DOWN;
                    //delay(1000);
                }
                break;
            case ARM_OUT_AND_COMPLETELY_DOWN:
                telemetry.addData("state = ", currentGoAboveBallState.toString());
                upDownServoComplete = upDownServo.updateMoveBySteps();
                if (upDownServoComplete) {
                    // movement is complete and this overall movement is also complete
                    // transition to the next state
                    currentGoAboveBallState = GoAboveBallStates.COMPLETE;
                    //delay(1000);
                }
                break;
            case COMPLETE:
                telemetry.addData("state = ", currentGoAboveBallState.toString());
                completed = true;
                if (dataLog != null) {
                    dataLog.logData("Completed moving to above ball");
                }
                break;
        }
        return completed;
    }

    public AdafruitColorSensor8863.ColorFromSensor updateGetBallColorStates() {
        boolean completed = false;
        boolean frontBackServoComplete = false;
        AdafruitColorSensor8863.ColorFromSensor ballColor = AdafruitColorSensor8863.ColorFromSensor.RED;
        double nextPosition;

        switch (currentGetBallColorStates) {
            case BACK_BALL_CENTER_POSITION:
                getBallColor();
                if (ballColor == AdafruitColorSensor8863.ColorFromSensor.BLUE) {
                    currentGetBallColorStates = currentGetBallColorStates.COMPLETE;
                }
                nextPosition = frontBackServo.getPosition() + .05;
                frontBackServo.setPosition(nextPosition);
                delay(100);
                currentGetBallColorStates = currentGetBallColorStates.BACK_BALL_RIGHT_POSITION;
                break;
            case BACK_BALL_RIGHT_POSITION:
                getBallColor();
                if (ballColor == AdafruitColorSensor8863.ColorFromSensor.BLUE) {
                    currentGetBallColorStates = currentGetBallColorStates.COMPLETE;
                }
                nextPosition = frontBackServo.getPosition() - 0.1;
                frontBackServo.setPosition(nextPosition);
                delay(100);
                currentGetBallColorStates = currentGetBallColorStates.BACK_BALL_LEFT_POSITION;
                break;
            case BACK_BALL_LEFT_POSITION:
                getBallColor();
                if (ballColor == AdafruitColorSensor8863.ColorFromSensor.BLUE) {
                    currentGetBallColorStates = currentGetBallColorStates.COMPLETE;
                }
                nextPosition = frontBackServo.getPosition() - 0.05;
                frontBackServo.setPosition(nextPosition);
                delay(100);
                currentGetBallColorStates = currentGetBallColorStates.FRONT_BALL_RIGHT_POSITION;
                break;
            case FRONT_BALL_RIGHT_POSITION:
                getBallColor();
                if (ballColor == AdafruitColorSensor8863.ColorFromSensor.BLUE) {
                    currentGetBallColorStates = currentGetBallColorStates.COMPLETE;
                }
                nextPosition = frontBackServo.getPosition() - 0.05;
                frontBackServo.setPosition(nextPosition);
                delay(100);
                currentGetBallColorStates = currentGetBallColorStates.FRONT_BALL_CENTER_POSITION;
                break;
            case FRONT_BALL_CENTER_POSITION:
                getBallColor();
                if (ballColor == AdafruitColorSensor8863.ColorFromSensor.BLUE) {
                    currentGetBallColorStates = currentGetBallColorStates.COMPLETE;
                }
                nextPosition = frontBackServo.getPosition() - 0.05;
                frontBackServo.setPosition(nextPosition);
                delay(100);
                currentGetBallColorStates = currentGetBallColorStates.FRONT_BALL_LEFT_POSITION;
                break;

            case FRONT_BALL_LEFT_POSITION:
                getBallColor();
                if (ballColor == AdafruitColorSensor8863.ColorFromSensor.BLUE) {
                    currentGetBallColorStates = currentGetBallColorStates.COMPLETE;
                }
                currentGetBallColorStates = currentGetBallColorStates.COMPLETE;
                break;
            case COMPLETE:
                completed = true;
                if (dataLog != null) {
                    dataLog.logData("Completed checking ball color, ball color is " + ballColor.toString());
                }
                break;

        }

        return ballColor;
    }

    public boolean updateGoBetweenBall() {
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
                if (elbowServoComplete && upDownServoComplete) {
                    currentGoBetweenBallState = currentGoBetweenBallState.ROTATE_BETWEEN_BALLS;
                    frontBackServo.setupMoveBySteps(.5, 0.01, 5);
                }
                break;
            case ROTATE_BETWEEN_BALLS:
                frontBackServoComplete = frontBackServo.updateMoveBySteps();
                if (frontBackServoComplete) {
                    currentGoBetweenBallState = currentGoBetweenBallState.DOWN_AND_EXTENDING_OUT;
                    upDownServo.setupMoveBySteps(.5, 0.01, 5);
                    elbowServo.setupMoveBySteps(0.10, 0.01, 5);
                }
                break;
            case DOWN_AND_EXTENDING_OUT:
                upDownServoComplete = upDownServo.updateMoveBySteps();
                elbowServoComplete = elbowServo.updateMoveBySteps();
                if (upDownServoComplete && elbowServoComplete) {
                    currentGoBetweenBallState = currentGoBetweenBallState.COMPLETE;
                }
                break;
            case COMPLETE:
                if (dataLog != null) {
                    dataLog.logData("Completed moving between balls");
                }
                completed = true;
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

    private double distanceFromTopOfBallToWall = 4; //cm
    private double distanceFromSensorToServo = -1.2; //cm
    private double armServoToFloorDistance = 25.4; //cm (10 inches)
    private double heightToTopOfBall = 15.0; //cm
    private double elbowArmLength = 23.75; //cm
    private double armLength = 18.89; //cm
    // due to the difference in location between the arm and elbow pieces - they dont form a perfect triangle
    private double armServoAngleOffset = 7.5; // degrees -
    public double elbowServoAngle;
    public double armServoAngle;

    public double calculateDistanceToBallStraight(double distanceFromSensorToWallInCm) {
        double distanceToBallStraight = 0; // D on math sheet
        distanceToBallStraight = distanceFromSensorToWallInCm - distanceFromTopOfBallToWall - distanceFromSensorToServo;
        return distanceToBallStraight;
    }

    public double calculateServoToBallDistance(double distanceToBallStraight) {
        double armServoToBallDistance = 0; // b on math sheet
        armServoToBallDistance = Math.sqrt(distanceToBallStraight * distanceToBallStraight + (armServoToFloorDistance - heightToTopOfBall));
        return armServoToBallDistance;
    }

    public double calculateElbowAngle(double armServoToBallDistance) {
        double elbowAngle = 0; // B on math sheet
        elbowAngle = Math.acos((armLength * armLength + elbowArmLength * elbowArmLength - armServoToBallDistance * armServoToBallDistance) / (2 * armLength * elbowArmLength));
        return elbowAngle;
    }

    public double calculateArmAngleInTriangle(double armServoToBallDistance) {
        double armAngleInTriangle = 0; // A on math sheet
        armAngleInTriangle = Math.acos((armServoToBallDistance * armServoToBallDistance + armLength * armLength - elbowArmLength * elbowArmLength) / (2 * armServoToBallDistance * armLength));
        return armAngleInTriangle;
    }

    public double calculateZ(double distanceToBallStraight) {
        double angleZ = 0 ; // Z on math sheet
        angleZ = Math.atan(distanceToBallStraight/(armServoToFloorDistance - heightToTopOfBall));
        return angleZ;
    }

    public double calculateArmServoAngle(double angleZ, double armAngleInTriangle) {
        double armServoAngle = 0; // Y on math sheet
        armServoAngle = 180.0-angleZ-armAngleInTriangle;
        return armServoAngle;
    }

    /**
     * This is the angle to set the elbow servo to
     * @return Angle B on the math sheet
     */
    public void getServoAngles(double distanceSensorToWallInCM) {
        double armServoAngle = 0;
        double distanceToBallStraight = calculateDistanceToBallStraight(distanceSensorToWallInCM);
        double armServoToBallDistance = calculateServoToBallDistance(distanceToBallStraight);
        this.elbowServoAngle = calculateElbowAngle(armServoToBallDistance);
        double armAngleInTriangle = calculateArmAngleInTriangle(armServoToBallDistance);
        double angleZ = calculateZ(distanceToBallStraight);
        this.armServoAngle = calculateArmServoAngle(angleZ, armAngleInTriangle) - armServoAngleOffset;
        if (dataLog != null) {
            dataLog.logData("Distance from sensor to wall (in) = " + Double.toString(distanceSensorToWallInCM/2.54));
            dataLog.logData("Elbow servo angle = " + Double.toString(this.elbowServoAngle));
            dataLog.logData("Arm servo angle = " + Double.toString(this.armServoAngle));
        }
        telemetry.addData("Distance from sensor to wall (in) = ", "%5.2f", distanceSensorToWallInCM);
        telemetry.addData("Elbow servo angle = ", "%3.2f", this.elbowServoAngle);
        telemetry.addData("Arm servo angle = ", "%3.2f", this.armServoAngle);
    }
}