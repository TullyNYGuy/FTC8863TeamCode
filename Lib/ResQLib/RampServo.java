package org.firstinspires.ftc.teamcode.Lib.ResQLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class RampServo {

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

    private Servo8863 rampServo;

    private double rampHomePosition = 0.00;

    private double rampDumpPosition = 0.60;

    private double rampInitPosition = 0.00;

    private double calStartPosition = 0.0;
    private double calEndPosition = 1.0;
    private double calPositionIncrement = 0.05;
    private double calTimeBetweenPositions = 3.0;

    private double wiggleStartPosition = 0.6;
    private double wiggleDelta = 0.1;
    private double wiggleTime = 5.0;
    private double wiggleDelay = 0.2;


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


    public RampServo(HardwareMap hardwareMap, Telemetry telemetry) {
        rampServo = new Servo8863(RobotConfigMapping.getRampServoName(),hardwareMap, telemetry, rampHomePosition, rampDumpPosition, rampHomePosition, rampInitPosition, Servo.Direction.FORWARD);
        rampServo.goInitPosition();
        rampServo.goDown();
        rampServo.setupWiggle(wiggleStartPosition, wiggleDelay, wiggleDelta, wiggleTime);
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

    public void goDown() {rampServo.goDown(); }

    public void goHome() {rampServo.goHome();}

    public void goInit() {
        rampServo.goInitPosition();
    }

    public void setupCalibration(){
        rampServo.setUpServoCalibration(calStartPosition, calEndPosition, calPositionIncrement, calTimeBetweenPositions);
    }

    public void updateCalibration() {
        rampServo.updateServoCalibration();
    }

    public void startWiggle() {
        rampServo.startWiggle();
    }

    public Servo8863.ServoWiggleState updateWiggle() {
       return rampServo.updateWiggle();
    }

}
