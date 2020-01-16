package org.firstinspires.ftc.teamcode.Lib.SkystoneLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class ClawRotator {

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
    private Servo8863 servoRotator;
    private double initPos = 0.87;
    private double upPos = 0.00;
    private double downPos = 0.87;
    private double homePos = 0.00;
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
    public ClawRotator(String servoName, HardwareMap hardwareMap, Telemetry telemetry) {
        servoRotator = new Servo8863(servoName, hardwareMap, telemetry, homePos, upPos, downPos, initPos, Servo.Direction.FORWARD);

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
    public void rotateFront() {
        servoRotator.goUp();

    }

    public void rotateBack() {
        servoRotator.goDown();

    }

    public void init() {
        rotateBack();
    }

    public void shutdown() {
        rotateBack();
    }

}
