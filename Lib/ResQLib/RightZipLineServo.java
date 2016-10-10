package org.firstinspires.ftc.teamcode.Lib.ResQLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class RightZipLineServo {

    //*********************************************************************************************
    //          ENUMERATED TYPES
    //
    // user defined types
    //
    //*********************************************************************************************


    //*********************************************************************************************
    //          PRIVATE DATA FIELDS
    //
    // can be accessed only by this class, or by using the public
    // getter and setter methods
    //*********************************************************************************************

    private Servo8863 rightZipLineServo;

    // home position
    private double rightZipLineHomePosition = 0.8;

    // upper zip line guy
    private double rightZipLineUpperPosition = 0.1;

    // middle zip line guy
    private double rightZipLineMiddlePosition = 0.1;

    // lower zip line guy
    private double rightZipLineLowerPosition = .25;

    private double rightZipLineInitPosition =  rightZipLineHomePosition;

    //*********************************************************************************************
    //          GETTER and SETTER Methods
    //
    // allow access to private data fields for example setMotorPower,
    // getMotorPosition
    //*********************************************************************************************


    //*********************************************************************************************
    //          Constructors
    //
    // the function that builds the class when an object is created
    // from it
    //*********************************************************************************************


    public RightZipLineServo(HardwareMap hardwareMap, Telemetry telemetry) {
        rightZipLineServo = new Servo8863(RobotConfigMapping.getBarGrabberServoName(),hardwareMap, telemetry);
        rightZipLineServo.setHomePosition(rightZipLineHomePosition);
        rightZipLineServo.setUpPosition(rightZipLineUpperPosition);
        rightZipLineServo.setPositionOne(rightZipLineMiddlePosition);
        rightZipLineServo.setDownPosition(rightZipLineLowerPosition);
        rightZipLineServo.setInitPosition(rightZipLineInitPosition);
        rightZipLineServo.setDirection(Servo.Direction.FORWARD);
        rightZipLineServo.goInitPosition();
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

    public void goHome() {
        rightZipLineServo.goHome();
    }

    public void goUpperGuy() {
        rightZipLineServo.goUp();
    }

    public void goMiddleGuy() {
        rightZipLineServo.goPositionOne();
    }

    public void goLowerGuy() {
        rightZipLineServo.goDown();
    }

    public double getPosition() {
        return rightZipLineServo.getPosition();
    }

    public void setPosition(double position) {
        rightZipLineServo.setPosition(position);
    }
}
