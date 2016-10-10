package org.firstinspires.ftc.teamcode.Lib.ResQLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class AimingServo {

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

    private Servo8863 aimingServo;

    private double aimingHomePosition = 0.65;

    private double aimingDownPosition = 0.00;

    private double aimingUpPosition = 0.65;

    private double aimingInitPosition = 0.65;

    private double calStartPosition = 0.0;
    private double calEndPosition = 1.0;
    private double calPositionIncrement = 0.05;
    private double calTimeBetweenPositions = 3.0;


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


    public AimingServo(HardwareMap hardwareMap, Telemetry telemetry) {
        aimingServo = new Servo8863(RobotConfigMapping.getTapeMeasureAimingServoName(),hardwareMap, telemetry, aimingHomePosition, aimingDownPosition, aimingUpPosition, aimingInitPosition, Servo.Direction.REVERSE);
        aimingServo.goInitPosition();
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

    public void goDown() {
        aimingServo.goDown(); }

    public void goHome() {
        aimingServo.goHome();}

    public void goInit() {
        aimingServo.goInitPosition();
    }

    public void setupCalibration(){
        aimingServo.setUpServoCalibration(calStartPosition, calEndPosition, calPositionIncrement, calTimeBetweenPositions);
    }

    public void updateCalibration() {
        aimingServo.updateServoCalibration();
    }

}
