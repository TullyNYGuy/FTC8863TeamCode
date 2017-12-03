package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.AdafruitColorSensor8863;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;


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
                    "coreDIM1",0);

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

            colorSensor = new AdafruitColorSensor8863 (hardwareMap,"rightColorSensor",
                    "coreDIM1",1);

        }


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

    private void armDown() {
        upDownServo.goPositionOne();
    }

    private void armUp() {
        upDownServo.goHome();
    }

    private void armFront() {
        frontBackServo.goPositionOne();
    }

    private void armBack() {
        frontBackServo.goPositionTwo();
    }

    private void armCenter() {
        frontBackServo.goHome();
    }

    public void initialize() {
        upDownServo.goInitPosition();
        frontBackServo.goInitPosition();
        colorSensor.turnLEDOn();
    }

    public void shutdown() {
        upDownServo.goInitPosition();
        frontBackServo.goInitPosition();
    }

    public AdafruitColorSensor8863.ColorFromSensor getBallColor() {
        armCenter();
        armDown();
        return colorSensor.getSimpleColor();

    }

    public void resetPosition() {
        armUp();
        armCenter();

    }
}



