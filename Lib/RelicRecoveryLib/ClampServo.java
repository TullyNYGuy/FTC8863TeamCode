package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class ClampServo {

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
    private Servo8863 clampServo;
    private Telemetry telemetry;
    private double homePosition = 0.55;
    private double initPosition = 0.55;
    private double positionOne = .30;

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
    public ClampServo(HardwareMap hardwareMap, Telemetry telemetry) {
        clampServo = new Servo8863("clampServo", hardwareMap, telemetry);
        this.telemetry = telemetry;
        clampServo.setDirection(Servo.Direction.REVERSE);
        clampServo.setHomePosition(homePosition);
        clampServo.setInitPosition(initPosition);
        clampServo.setPositionOne(positionOne);
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

    public void init() {
        clampServo.goInitPosition();
        telemetry.addData("Sweeper Servo initialized", "!");
    }

    public void open() {
        clampServo.goHome();
    }

    public void clamp() { clampServo.goPositionOne();}

    public void update() {
        // put any update commands here
    }

    public void shutdown() {open();
    }
}
