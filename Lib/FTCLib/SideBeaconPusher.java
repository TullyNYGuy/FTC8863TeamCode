package org.firstinspires.ftc.teamcode.Lib.FTCLib;


import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class SideBeaconPusher {

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
    Servo8863 beaconServo;
    double homePosition = 0;
    double openPosition = 1;
    double extraPosition1 = 0;
    double extraPosition2 = 0;


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
   public SideBeaconPusher (HardwareMap hardwareMap, Telemetry telemetry){
       beaconServo = new Servo8863("beaconServo",hardwareMap,telemetry,homePosition,openPosition,extraPosition1,extraPosition2, Servo.Direction.FORWARD);
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
    public void open(){
        beaconServo.goHome();

    }

    public void close(){
        beaconServo.goUp();

    }
    public void init(){
        close();
    }
}
