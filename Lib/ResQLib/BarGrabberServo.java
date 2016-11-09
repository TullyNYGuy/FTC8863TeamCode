package org.firstinspires.ftc.teamcode.Lib.ResQLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class BarGrabberServo{

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

    private Servo8863 barGrabberServo;

    private double grabberHomePosition = 0.65;

    private double grabberDownPosition = 0.05;

    private double grabberUpPosition = 0.65;

    private double initPosition = 0.65;

    private double calStartPosition = 0.0;
    private double calEndPosition = 1.0;
    private double calPositionIncrement = 0.05;
    private double calTimeBetweenPositions = 3.0;


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


    public BarGrabberServo(HardwareMap hardwareMap, Telemetry telemetry) {
        barGrabberServo = new Servo8863(RobotConfigMapping.getBarGrabberServoName(),hardwareMap, telemetry, grabberHomePosition, grabberUpPosition, grabberDownPosition, initPosition, Servo.Direction.FORWARD);
        barGrabberServo.goInitPosition();
        barGrabberServo.goDown();
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

    public void goGrabBar() {barGrabberServo.goDown(); }

    public void goHome() {barGrabberServo.goHome();}

    public void goInit() {
        barGrabberServo.goInitPosition();
    }

    public void setupCalibration(){
        barGrabberServo.setUpServoCalibration(calStartPosition, calEndPosition, calPositionIncrement, calTimeBetweenPositions);
    }

    public void updateCalibration() {
        barGrabberServo.updateServoCalibration();
    }

}
