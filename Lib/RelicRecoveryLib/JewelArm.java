package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

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

    AllianceColor.TeamColor teamColor;

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
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
        //nothing to update yet
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
        frontBackServo.goInitPosition();
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

    public void moveBetweenBalls() {
        upDownServo.setPosition(.30);
        elbowServo.setPosition(.20);
        frontBackServo.setPosition(.50);
        delay(500);
        upDownServo.setPosition(50);
        delay(100);
        elbowServo.setPosition(.10);
        delay(500);
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

    public double calculateDistanceToBall(double distanceFromSensorToWallInCm) {
        double distanceToBall = 0;
        double distanceFromTopOfBallToWall = 9.84; //cm
        double distanceFromSensorToServo = 5; //cm
        distanceToBall = distanceFromSensorToWallInCm - distanceFromTopOfBallToWall - distanceFromSensorToServo;
        return distanceToBall;
    }

    public double calculateServoToBallDistance (double distanceToBall){
        double servoToBallDistance=0;
        double servoToFloorDistance=44.0; //cm
        double heightToTopOfBall=9.84; //cm
        servoToBallDistance=Math.sqrt(distanceToBall*distanceToBall+(servoToFloorDistance-heightToTopOfBall));
        return servoToBallDistance;
    }

    public double calculateElbowAngle (double servoToBallDistance){
        double elbowAngle=0;
        double elbowLength=23.75; //cm
        double armLength=18.89; //cm
        return 0;
    }

}




