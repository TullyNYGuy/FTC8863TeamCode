package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
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

    public enum BallColor {
        RED, BLUE;
    }

    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************
    private double servoArmUpPosition;
    private double servoArmDownPosition;
    private double servoArmCenterPosition;
    private double servoArmFrontPosition;
    private double servoArmBackPosition;
    private RobotSide robotSide;
    private Servo8863 upDownServo;
    private Servo8863 frontBackServo;
    private AdafruitColorSensor8863 colorSensor;
    private TestJewelArm.AllianceColor allianceColor;

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
    public JewelArm(RobotSide robotSide, HardwareMap hardwareMap, Telemetry telemetry, TestJewelArm.AllianceColor allianceColor) {
        this.allianceColor = allianceColor;
        if (robotSide == RobotSide.LEFT) {
            servoArmUpPosition = 0;
            servoArmDownPosition = 0.75;
            servoArmFrontPosition = 0.35;
            servoArmCenterPosition = 0.5;
            servoArmBackPosition = 0.6;

            upDownServo = new Servo8863("leftUpDownServo", hardwareMap, telemetry);
            upDownServo.setDirection(Servo.Direction.FORWARD);
            upDownServo.setInitPosition(servoArmUpPosition);
            upDownServo.setHomePosition(servoArmUpPosition);
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
            servoArmUpPosition = 0;
            servoArmDownPosition = 0.75;
            servoArmFrontPosition = 0.35;
            servoArmCenterPosition = 0.5;
            servoArmBackPosition = 0.6;

            upDownServo = new Servo8863("rightUpDownServo", hardwareMap, telemetry);
            upDownServo.setDirection(Servo.Direction.FORWARD);
            upDownServo.setInitPosition(servoArmUpPosition);
            upDownServo.setHomePosition(servoArmUpPosition);
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

    //change the initposition commands with the ones above so its easier to read
    public void initialize() {
        upDownServo.goInitPosition();
        frontBackServo.goInitPosition();
        colorSensor.turnLEDOn();
    }

    public void shutdown() {
        upDownServo.goInitPosition();
        frontBackServo.goInitPosition();
    }

    public BallColor getBallColor() {
        AdafruitColorSensor8863.ColorFromSensor colorFromSensor;
        colorFromSensor = colorSensor.getSimpleColor();
        if (colorFromSensor == AdafruitColorSensor8863.ColorFromSensor.BLUE) {
            return BallColor.BLUE;
        } else {
            return BallColor.RED;
        }
    }

    public void resetPosition() {
        armUp();
        armCenter();

    }

    public void knockFrontBall() {
        armDown();
        delay(1000);
        armFront();

    }

    public void knockBackBall() {


        armDown();
        delay(1000);
        armBack();

    }

    public BallColor knockOffBall() {
        BallColor ballColor;
        armDown();
        delay(1000);
        ballColor = getBallColor();
        if (allianceColor == TestJewelArm.AllianceColor.BLUE) {
            if (ballColor == BallColor.BLUE) {
                knockFrontBall();
            } else {
                knockBackBall();

            }
        }
        if (allianceColor == TestJewelArm.AllianceColor.RED) {
            if (ballColor == BallColor.RED) {
                knockFrontBall();
            } else {
                knockBackBall();
            }
        }
        return ballColor;
    }
}




