package org.firstinspires.ftc.teamcode.Lib.ResQLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class BarGrabberServoExtends extends Servo8863 {


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

    private double grabberHomePosition = .7;

    private double grabberDownPosition = 0.0;

    private double grabberUpPosition = 0.0;

    private double initPosition = 0.0;


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


    public BarGrabberServoExtends(HardwareMap hardwareMap, Telemetry telemetry) {
        super(RobotConfigMapping.getBarGrabberServoName(),hardwareMap, telemetry);
        this.setHomePosition(grabberHomePosition);
        this.setDownPosition(grabberDownPosition);
        this.setUpPosition(grabberUpPosition);
        this.setInitPosition(initPosition);
        this.setDirection(Servo.Direction.FORWARD);
        this.goInitPosition();
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

    public void goGrabBar() {
        this.goDown();
    }

    public void goHome() {barGrabberServo.goHome();}

/*    public void goInit() {
        barGrabberServo.goInitPosition();
    }*/

/*    public double getPosition() {
        return barGrabberServo.getPosition();
    }

    public void setPosition(double position) {
        barGrabberServo.setPosition(position);
    }*/

}
