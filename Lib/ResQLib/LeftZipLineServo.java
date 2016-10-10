package org.firstinspires.ftc.teamcode.Lib.ResQLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class LeftZipLineServo {

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

    private Servo8863 leftZipLineServo;

    // home position
    private double leftZipLineHomePosition = 0.8;

    // upper zip line guy
    private double leftZipLineUpperPosition = 0.1;

    // middle zip line guy
    private double leftZipLineMiddlePosition = 0.1;

    // lower zip line guy
    private double leftZipLineLowerPosition = .25;

    private double leftZipLineInitPosition = leftZipLineHomePosition;


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


    public LeftZipLineServo(HardwareMap hardwareMap, Telemetry telemetry) {
        leftZipLineServo = new Servo8863(RobotConfigMapping.getBarGrabberServoName(),hardwareMap, telemetry);
        leftZipLineServo.setHomePosition(leftZipLineHomePosition);
        leftZipLineServo.setUpPosition(leftZipLineUpperPosition);
        leftZipLineServo.setPositionOne(leftZipLineMiddlePosition);
        leftZipLineServo.setDownPosition(leftZipLineLowerPosition);
        leftZipLineServo.setInitPosition(leftZipLineInitPosition);
        leftZipLineServo.setDirection(Servo.Direction.REVERSE);
        leftZipLineServo.goInitPosition();
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
        leftZipLineServo.goHome();
    }

    public void goUpperGuy() {
        leftZipLineServo.goUp();
    }

    public void goMiddleGuy() {
        leftZipLineServo.goPositionOne();
    }

    public void goLowerGuy() {
        leftZipLineServo.goDown();
    }

    public double getPosition() {
        return leftZipLineServo.getPosition();
    }

    public void setPosition(double position) {
        leftZipLineServo.setPosition(position);
    }
}
