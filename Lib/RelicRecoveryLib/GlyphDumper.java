package org.firstinspires.ftc.teamcode.Lib.RelicRecoveryLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Lib.FTCLib.Servo8863;

public class GlyphDumper {

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
    private Servo8863 glyphDumpServo;
    private double homePosition = .8;
    private double initPosition = 0;
    private double dumpPosition = .25;

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
    public GlyphDumper(HardwareMap hardwareMap, Telemetry telemetry) {
        glyphDumpServo = new Servo8863("glyphDumpServo", hardwareMap, telemetry);
        glyphDumpServo.setDirection(Servo.Direction.FORWARD);
        glyphDumpServo.setHomePosition(homePosition);
        glyphDumpServo.setInitPosition(initPosition);
        glyphDumpServo.setPositionOne(dumpPosition);
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
        glyphDumpServo.goInitPosition();
    }

    public void goHome() {
        glyphDumpServo.goHome();
    }

    public void dump() {
        glyphDumpServo.goPositionOne();
    }

    public void update() {
        // put any update commands here
    }

    public void shutdown() {
        goHome();
    }
}
